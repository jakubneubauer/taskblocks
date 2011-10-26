/*
 * Copyright (C) Jakub Neubauer, 2007
 *
 * This file is part of TaskBlocks
 *
 * TaskBlocks is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package taskblocks.io;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import taskblocks.modelimpl.ColorLabel;
import taskblocks.modelimpl.ManImpl;
import taskblocks.modelimpl.TaskImpl;
import taskblocks.modelimpl.TaskModelImpl;
import taskblocks.utils.Pair;
import taskblocks.utils.Utils;

/**
 * Used to load/save the task project model
 * 
 * @author jakub
 *
 */
public class ProjectSaveLoad {

	public static final String TASKMAN_E = "taskman";
	public static final String TASKS_E = "tasks";
	public static final String MANS_E = "mans";
	public static final String TASK_E = "task";
	public static final String MAN_E = "man";
	public static final String PREDECESSORS_E = "predecessors";
	public static final String PREDECESSOR_E = "predecessor";
	
	public static final String VERSION_A = "version";
	public static final String NAME_A = "name";
	public static final String WORKLOAD_A = "workload";
	public static final String ID_A = "id";
	public static final String START_A = "start";
	public static final String END_A = "end";
	public static final String DURATION_A = "duration";
	public static final String ACTUAL_A = "actualDuration";
	public static final String MAN_A = "man";
	public static final String PRED_A = "pred";
	public static final String COLOR_A = "color";
	public static final String COMM_A = "comment";
	// Id in Bugzilla, used when exporting to it.
	public static final String BUGID_A = "bugid";
	
	TaskModelImpl _model;
	Map<String, TaskImpl> _taskIds;
	Map<String, ManImpl> _manIds;
	
	public static final int CURRENT_VERSION = 1;

	/**
	 * Loads project data model from given file.
	 * TODO: checks for missing data in elements
	 *  
	 * @param f
	 * @return
	 * @throws WrongDataException
	 */
	public TaskModelImpl loadProject(URL f) throws WrongDataException {
		InputStream input = null;
		try {
			input = f.openStream();
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc;
			doc = dbf.newDocumentBuilder().parse(input);
			Element rootE = doc.getDocumentElement();
			if(!TASKMAN_E.equals(rootE.getNodeName())) {
				throw new WrongDataException("Document is not TaskBlocks project");
			}
			
			// mapping ID -> ManImpl
			Map<String, ManImpl> mans = new HashMap<String, ManImpl>();
			// mapping ID -> TaskImpl 
			Map<String, TaskImpl>tasks = new HashMap<String, TaskImpl>();
			// mapping tasks -> list of their predecessors IDs
			List<Pair<TaskImpl, String[]>> taskPredecessorsIds = new ArrayList<Pair<TaskImpl,String[]>>();

			// check the data version. The first Taskblocks files had no version attribute.
			String versionAttr = rootE.getAttribute(VERSION_A);
			if(versionAttr != null && versionAttr.trim().length() > 0) {
				try {
					versionAttr = versionAttr.trim();
					int version = Integer.parseInt(versionAttr);
					if(version > CURRENT_VERSION) {
						throw new WrongDataException("Cannot load higher version '" + version + "', current is '" + CURRENT_VERSION + "'.");
					}
				} catch(NumberFormatException e) {
					throw new WrongDataException("Wrong data file version: '" + versionAttr + "'");
				}
			}
			
			// 1. load all tasks and mans alone, 2. bind them between each other
			Element mansE = getFirstChild(rootE, MANS_E);
			if(mansE != null) {
				Element[] manEs = Utils.getChilds(mansE, MAN_E);
				for(Element manE: manEs) {
					String manName = manE.getAttribute(NAME_A);
					String manWorkload = manE.getAttribute(WORKLOAD_A);
					String manId = manE.getAttribute(ID_A);
					ManImpl man = new ManImpl();
					man.setName(manName);
					if(manWorkload != null && manWorkload.trim().length() > 0) {
						try {
							man.setWorkload(Double.parseDouble(manWorkload.trim())/100.0);
						} catch(NumberFormatException e) {
							// TODO Jakub: handle exception
							System.err.println("Cannot parse man's workload '" + manWorkload + "'");
						}
					}
					mans.put(manId, man);
				}
			}
			Element tasksE = getFirstChild(rootE, TASKS_E);
			if(tasksE != null) {
				for(Element taskE: Utils.getChilds(tasksE, TASK_E)) {
					String taskId = taskE.getAttribute(ID_A);
					String taskName = taskE.getAttribute(NAME_A);
					String taskManId = taskE.getAttribute(MAN_A);
					ManImpl man = mans.get(taskManId); // mans are already loaded
					if(man == null) {
						throw new WrongDataException("Task with id " + taskId + " is not assigned to any man");
					}
					
					long taskStart = xmlTimeToTaskTime(taskE.getAttribute(START_A));
					String durAttr = taskE.getAttribute(DURATION_A);
					long taskEffort;
					if(durAttr != null && durAttr.trim().length() > 0) {
						taskEffort = xmlDurationToTaskDuration(durAttr);
					} else {
						String endAttr = taskE.getAttribute(END_A);
						long taskEnd = xmlTimeToTaskTime(endAttr);

						// start with effort=1. Increase it until the real duration is over
						for(long newEffort = 2; true; newEffort++) {
							long tmpEndTime = Utils.countFinishTime(taskStart, newEffort, man.getWorkload());
							// Note: we compare to (taskEnd+1), since the tasks start/end are counted mathematically. For example task with
							// duration 1 day starting on 2011-01-01 ends on 2011-01-02 (the second day)
							if(tmpEndTime > (taskEnd+1)) {
								// we are over, step back
								taskEffort = newEffort-1;
								break;
							}
						}
					}
					
					String usedStr = taskE.getAttribute(ACTUAL_A);
					long taskWorkedTime = 0;
					if(usedStr != null && usedStr.trim().length() > 0) {
						taskWorkedTime = xmlDurationToTaskDuration(taskE.getAttribute(ACTUAL_A));
					}
					String bugId = taskE.getAttribute(BUGID_A);
					String colorTxt = taskE.getAttribute(COLOR_A);
					String comment = taskE.getAttribute(COMM_A);
					
					if(bugId != null && bugId.length() == 0) {
						bugId = null;
					}
					
					TaskImpl task = new TaskImpl();
					task.setName(taskName);
					task.setStartTime(taskStart);
					task.setEffort(taskEffort);
					task.setWorkedTime(taskWorkedTime);
					task.setMan(man);
					task.setComment( comment );
					task.setBugId(bugId);
					if(colorTxt != null && colorTxt.length() > 0) {
						int colorIndex = Integer.parseInt(colorTxt);
						if(colorIndex >= 0 && colorIndex < ColorLabel.COLOR_LABELS.length) {
							task.setColorLabel(ColorLabel.COLOR_LABELS[colorIndex]);
						}
					}

					// read predecessors ids
					Element predsE = getFirstChild(taskE, PREDECESSORS_E);
					if(predsE != null) {
						List<String> preds = new ArrayList<String>();
						for(Element predE: Utils.getChilds(predsE, PREDECESSOR_E)) {
							preds.add(predE.getAttribute(PRED_A));
						}
						taskPredecessorsIds.add(new Pair<TaskImpl, String[]>(task, preds.toArray(new String[preds.size()])));
					}
					
					tasks.put(taskId, task);
				}
			}
			
			// now count the predecessors of tasks
			for(Pair<TaskImpl, String[]> taskAndPredIds: taskPredecessorsIds) {
				List<TaskImpl> preds = new ArrayList<TaskImpl>();
				for(String predId: taskAndPredIds.snd) {
					TaskImpl pred = tasks.get(predId);
					if(pred == null) {
						System.out.println("Warning: Task predecessor with id " + predId + " doesn't exist"); // NOPMD by jakub on 6.8.09 15:50
					} else if(pred == taskAndPredIds.fst) {
						System.out.println("Warning: Task with id " + predId + " is it's own predecessor"); // NOPMD by jakub on 6.8.09 15:50
					} else {
						preds.add(pred);
					}
				}
				taskAndPredIds.fst.setPredecessors(preds.toArray(new TaskImpl[preds.size()]));
			}
			
			TaskModelImpl taskModel = new TaskModelImpl(tasks.values().toArray(new TaskImpl[tasks.size()]), mans.values().toArray(new ManImpl[mans.size()]));
			
			return taskModel;
			
		} catch (SAXException e) {
			throw new WrongDataException("Document is not valid data file", e);
		} catch (IOException e) {
			throw new WrongDataException("Can't read file: " + e.getMessage(), e);
		} catch (ParserConfigurationException e) {
			throw new WrongDataException("Document is not TaskBlocks project", e);
		} finally {
			if(input != null) {
				try {
					input.close();
				} catch (IOException e) {
					throw new WrongDataException("Cannot close input stream: " + e.toString());
				}
			}
		}
	}
	

	public void saveProject(URL url, TaskModelImpl model) throws TransformerException, ParserConfigurationException, IOException {
		if("file".equals(url.getProtocol())) {
			File f;
			try {
			  f = new File(url.toURI());
			} catch(URISyntaxException e) {
			  f = new File(url.getPath());
			}
			saveProject(f, model);
		} else if("http".equals(url.getProtocol())) {
			
			ByteArrayOutputStream tmp = new ByteArrayOutputStream();
			saveProject(tmp, model);
			
			OutputStream out = null;
			HttpURLConnection con = null;
			try {
				
				con = (HttpURLConnection) url.openConnection();
				con.setDoOutput(true);
				//con.setDoInput(false);
				con.setRequestMethod("PUT");
				con.setRequestProperty("Content-Length", String.valueOf(tmp.size()));
				con.setRequestProperty("Content-Type", "application/xml");
				con.connect();
				out = con.getOutputStream();
				out.write(tmp.toByteArray());
				int responseCode = con.getResponseCode();
				if(responseCode != 200) {
					throw new IOException("HTTP Error " + responseCode + ": " + con.getResponseMessage());
				}
			} finally {
				if(out != null) {
					out.close();
				}
				if(con != null) {
					con.disconnect();
				}
			}
		} else {
			throw new IOException("Unsupported url protocol: " + url.getProtocol());
		}
	}

	/**
	 * Saves project to specified file
	 * 
	 * @param f
	 * @param model
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 * @throws IOException 
	 */
	public void saveProject(File f, TaskModelImpl model) throws TransformerException, ParserConfigurationException, IOException {
		FileOutputStream fos = new FileOutputStream(f);
		try {
			saveProject(fos, model);
		} finally {
			fos.close();
		}
	}
	
	public void saveProject(OutputStream out, TaskModelImpl model) throws TransformerException, ParserConfigurationException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		Document doc;
		doc = dbf.newDocumentBuilder().newDocument();
		
		_model = model;
		_taskIds = new HashMap<String, TaskImpl>();
		_manIds = new HashMap<String, ManImpl>();

		// build the xml tree
		saveProject(doc);
		prettyLayout((Element)doc.getFirstChild());

		TransformerFactory tf = TransformerFactory.newInstance();
		Transformer t = tf.newTransformer();
		t.transform(new DOMSource(doc), new StreamResult(out));
	}
	
	private void saveProject(Document doc) {
		Element rootE = doc.createElement(TASKMAN_E);
		doc.appendChild(rootE);
		Set<ManImpl> mans = new HashSet<ManImpl>();

		// generate list of mans
		for(ManImpl m: _model._mans) {
			mans.add(m);
		}
		
		// generate task and man ids
		int lastTaskId = 1;
		int lastManId = 1;
		for(TaskImpl t: _model._tasks) {
			t._id = String.valueOf(lastTaskId++);
		}
		for(ManImpl man: mans) {
			man._id = String.valueOf(lastManId++);
		}
		
		// save mans
		Element mansE = doc.createElement(MANS_E);
		for(ManImpl man : mans) {
			saveMan(mansE, man);
		}
		rootE.appendChild(mansE);
		
		// save tasks
		Element tasksE = doc.createElement(TASKS_E);
		for(TaskImpl t: _model._tasks) {
			saveTask(tasksE, t);
		}
		rootE.setAttribute(VERSION_A, String.valueOf(CURRENT_VERSION));
		rootE.appendChild(tasksE);
	}

	private void saveMan(Element mansE, ManImpl man) {
		Element manE = mansE.getOwnerDocument().createElement(MAN_E);
		manE.setAttribute(ID_A, man._id);
		manE.setAttribute(NAME_A, man.getName());
		manE.setAttribute(WORKLOAD_A, String.valueOf((int)(man.getWorkload()*100)));
		mansE.appendChild(manE);
	}
	
	static SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	
	private static String taskTimeToXmlTime(long day) {
		return df.format(new Date(day * Utils.MILLISECONDS_PER_DAY + 8*60*60*1000));
	}
	
	private static String taskDurationToXmlDuration(long dur) {
		return "P" + dur + "D";
	}
	
	private static long xmlTimeToTaskTime(String time) throws WrongDataException {
		try {
			return Long.parseLong(time);
		} catch(NumberFormatException e) {
			// do nothing
		}
		try {
			return df.parse(time).getTime()/Utils.MILLISECONDS_PER_DAY;
		} catch (ParseException e) {
			// DO NOTHING
		}
		throw new WrongDataException("Wrong time value: " + time);
	}
	
	private static long xmlDurationToTaskDuration(String dur) throws WrongDataException{
		if(dur == null || dur.trim().length() == 0) {
			return 0;
		}
		try {
			return Long.parseLong(dur);
		} catch(NumberFormatException e) {
			// do nothing
		}
		if(dur.startsWith("P") && dur.endsWith("D")) {
			try {
				return Long.parseLong(dur.substring(1,dur.length()-1));
			} catch(NumberFormatException e) {
				// do nothing
			}
		}
		throw new WrongDataException("Wrong duration value: " + dur);
	}

	private void saveTask(Element tasksE, TaskImpl t) {
		Element taskE = tasksE.getOwnerDocument().createElement(TASK_E);
		taskE.setAttribute(NAME_A, t.getName());
		taskE.setAttribute(ID_A, t._id);
		taskE.setAttribute(START_A, taskTimeToXmlTime(t.getStartTime()));
		taskE.setAttribute(DURATION_A, taskDurationToXmlDuration(t.getEffort()));
		if(t.getWorkedTime() != 0) {
			taskE.setAttribute(ACTUAL_A, taskDurationToXmlDuration(t.getWorkedTime()));
		}
		taskE.setAttribute(MAN_A, t.getMan()._id);
		taskE.setAttribute(COMM_A, t.getComment());
		if(t.getColorLabel() != null) {
			taskE.setAttribute(COLOR_A, String.valueOf(t.getColorLabel()._index));
		}
		if(t.getBugId() != null && t.getBugId().trim().length() > 0) {
			taskE.setAttribute(BUGID_A, t.getBugId().trim());
		}
		
		// save predecessors
		if(t.getPredecessors().length > 0) {
			Element predsE = taskE.getOwnerDocument().createElement(PREDECESSORS_E);
			for(TaskImpl pred: t.getPredecessors()) {
				Element predE = predsE.getOwnerDocument().createElement(PREDECESSOR_E);
				predE.setAttribute(PRED_A, pred._id);
				predsE.appendChild(predE);
			}
			taskE.appendChild(predsE);
		}
		tasksE.appendChild(taskE);
	}
	
	private Element getFirstChild(Element e, String name) {
		NodeList nl = e.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE && name.equals(n.getNodeName())) {
				return (Element)n;
			}
		}
		return null;
	}
	
	private void prettyLayout(Element e) {
		prettyLayoutRec(e, "");
	}
	
	private void prettyLayoutRec(Element e, String currentIndent) {
		
		// insert spaces before 'e'
		// but only if indent > 0. This also resolves problem that we cannot insert
		// anything in Document node (before root element).
		if(currentIndent.length() > 0) {
			e.getParentNode().insertBefore(e.getOwnerDocument().createTextNode(currentIndent), e);
		}
		
		// first check if element has some sub-element. if true, prettyLayout them
		// recursively with increase indent
		NodeList nl = e.getChildNodes();
		boolean hasChildrenElems = false;
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE) {
				hasChildrenElems = true;
				break;
			}
		}
		
		if(hasChildrenElems) {
			// \n after start-tag. it means before first child
			e.insertBefore(e.getOwnerDocument().createTextNode("\n"), e.getFirstChild());
			
			// indent before end-tag. It means just as last child
			e.appendChild(e.getOwnerDocument().createTextNode(currentIndent));

			// we must get the nodelist again, because previous adding of childs broked
			// the old nodelist.
			Node n = e.getFirstChild();
			while(n != null) {
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					prettyLayoutRec((Element)n, currentIndent + "  ");
				}
				n = n.getNextSibling();
			}
		}

		// \n after end-tag
		Node text = e.getOwnerDocument().createTextNode("\n");
		if(e.getNextSibling() == null) {
			if(e.getParentNode().getNodeType() != Node.DOCUMENT_NODE) {
				e.getParentNode().appendChild(text);
			}
		} else {
			e.getParentNode().insertBefore(text, e.getNextSibling());
		}
	}
}
