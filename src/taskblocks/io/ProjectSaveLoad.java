package taskblocks.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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

import taskblocks.Pair;
import taskblocks.modelimpl.ColorLabel;
import taskblocks.modelimpl.ManImpl;
import taskblocks.modelimpl.TaskImpl;
import taskblocks.modelimpl.TaskModelImpl;

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
	
	public static final String NAME_A = "name";
	public static final String ID_A = "id";
	public static final String START_A = "start";
	public static final String DURATION_A = "duration";
	public static final String MAN_A = "man";
	public static final String PRED_A = "pred";
	public static final String COLOR_A = "color";
	
	TaskModelImpl _model;
	Map<String, TaskImpl> _taskIds;
	Map<String, ManImpl> _manIds;

	/**
	 * Loads project data model from given file.
	 * TODO: checks for missing data in elements
	 *  
	 * @param f
	 * @return
	 * @throws WrongDataException
	 */
	public TaskModelImpl loadProject(File f) throws WrongDataException {
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			Document doc;
			doc = dbf.newDocumentBuilder().parse(f);
			Element rootE = doc.getDocumentElement();
			if(!TASKMAN_E.equals(rootE.getNodeName())) {
				throw new WrongDataException("Document is not TaskManager project");
			}
			
			// mapping ID -> ManImpl
			Map<String, ManImpl> mans = new HashMap<String, ManImpl>();
			// mapping ID -> TaskImpl 
			Map<String, TaskImpl>tasks = new HashMap<String, TaskImpl>();
			// mapping tasks -> list of their predecessors IDs
			List<Pair<TaskImpl, String[]>> taskPredecessorsIds = new ArrayList<Pair<TaskImpl,String[]>>();
			
			// 1. load all tasks and mans alone, 2. bind them between each other
			Element mansE = getFirstChild(rootE, MANS_E);
			if(mansE != null) {
				Element[] manEs = getChilds(mansE, MAN_E);
				for(Element manE: manEs) {
					String manName = manE.getAttribute(NAME_A);
					String manId = manE.getAttribute(ID_A);
					ManImpl man = new ManImpl();
					man.setName(manName);
					mans.put(manId, man);
				}
			}
			Element tasksE = getFirstChild(rootE, TASKS_E);
			if(tasksE != null) {
				for(Element taskE: getChilds(tasksE, TASK_E)) {
					String taskId = taskE.getAttribute(ID_A);
					String taskName = taskE.getAttribute(NAME_A);
					long taskStart = Long.valueOf(taskE.getAttribute(START_A));
					long taskDuration = Long.valueOf(taskE.getAttribute(DURATION_A));
					String colorTxt = taskE.getAttribute(COLOR_A);
					String taskManId = taskE.getAttribute(MAN_A);
					ManImpl man = mans.get(taskManId); // mans are already loaded
					
					if(man == null) {
						throw new WrongDataException("Task with id " + taskId + " is not assigned to any man");
					}
					
					TaskImpl task = new TaskImpl();
					task.setName(taskName);
					task.setStartTime(taskStart);
					task.setDuration(taskDuration);
					task.setMan(man);
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
						for(Element predE: getChilds(predsE, PREDECESSOR_E)) {
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
						System.out.println("Warning: Task predecessor with id " + predId + " doesn't exist");
					} else if(pred == taskAndPredIds.fst) {
						System.out.println("Warning: Task with id " + predId + " is it's own predecessor");
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
			throw new WrongDataException("Document is not TaskManager project", e);
		}
	}
	
	/**
	 * Saves project to specified file
	 * 
	 * @param f
	 * @param model
	 * @throws TransformerException
	 * @throws ParserConfigurationException
	 */
	public void saveProject(File f, TaskModelImpl model) throws TransformerException, ParserConfigurationException {
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
		t.transform(new DOMSource(doc), new StreamResult(f));
	}
	
	private void saveProject(Document doc) {
		Element rootE = doc.createElement(TASKMAN_E);
		doc.appendChild(rootE);
		Set<ManImpl> mans = new HashSet<ManImpl>();

		// generate list of mans
		for(TaskImpl t: _model._tasks) {
			mans.add(t.getMan());
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
		rootE.appendChild(tasksE);
	}

	private void saveMan(Element mansE, ManImpl man) {
		Element manE = mansE.getOwnerDocument().createElement(MAN_E);
		manE.setAttribute(ID_A, man._id);
		manE.setAttribute(NAME_A, man.getName());
		mansE.appendChild(manE);
	}

	private void saveTask(Element tasksE, TaskImpl t) {
		Element taskE = tasksE.getOwnerDocument().createElement(TASK_E);
		taskE.setAttribute(NAME_A, t.getName());
		taskE.setAttribute(ID_A, t._id);
		taskE.setAttribute(START_A, String.valueOf(t.getStartTime()));
		taskE.setAttribute(DURATION_A, String.valueOf(t.getDuration()));
		taskE.setAttribute(MAN_A, t.getMan()._id);
		if(t.getColorLabel() != null) {
			taskE.setAttribute(COLOR_A, String.valueOf(t.getColorLabel()._index));
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
	
	private Element[] getChilds(Element e, String name) {
		List<Element>childs = new ArrayList<Element>();
		NodeList nl = e.getChildNodes();
		for(int i = 0; i < nl.getLength(); i++) {
			Node n = nl.item(i);
			if(n.getNodeType() == Node.ELEMENT_NODE && name.equals(n.getNodeName())) {
				childs.add((Element)n);
			}
		}
		return childs.toArray(new Element[childs.size()]);
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

			// recursively indent children
			currentIndent += "  ";
			// we must get the nodelist again, because previous adding of childs broked
			// the old nodelist.
			Node n = e.getFirstChild();
			while(n != null) {
				if(n.getNodeType() == Node.ELEMENT_NODE) {
					prettyLayoutRec((Element)n, currentIndent);
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
