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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.KeyStroke;
import javax.swing.SwingUtilities;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.html.HTMLEditorKit;

import taskblocks.bugzilla.BugzillaSubmitter;
import taskblocks.graph.TaskGraphComponent;
import taskblocks.modelimpl.ColorLabel;
import taskblocks.modelimpl.TaskImpl;
import taskblocks.utils.Utils;

public class BugzillaExportDialog extends JDialog {
	
	String[] COL_NAMES = new String[] { "", "Bug#", "Color", "Summary", "Assignee",
			"Estimated Time", "Remaining", "Status Whiteboard" };

	Class<?>[] COL_CLASSES = new Class[] { Boolean.class, String.class, Icon.class, String.class,
			String.class, String.class, String.class, String.class };

	int INDEX_ENABLED = 0;
	int INDEX_BUG_ID = 1;
	int INDEX_COLOR = 2;
	int INDEX_NAME = 3;
	int INDEX_MAN = 4;
	int INDEX_HOURS = 5;
	int INDEX_REMAINS = 6;
	int INDEX_STATUS_WHITEBOARD = 7;

	TaskImpl[] _tasks;
	TaskGraphComponent _graph;

	JTable _tasksTable;

	Object[][] _tasksData;
	TasksModel _tasksModel;

	JButton okB;

	JButton cancelB;

	private AbstractAction _okAction;

	private AbstractAction _cancelAction;

	JTextField _baseUrlTF;

	JTextField _userTF;

	JTextField _passwdTF;

	JTextField _productTF;

	JTextField _versionTF;

	JTextField _componentTF;

	JTextField _blocksTF;

	JTextField _hardwareTF;

	JTextField _osTF;

	JTextField _priorTF;

	JTextField _severTF;

	JTextPane _logArea;
	
	JTextField _keywordsTF;
	
	Preferences _prefs = Preferences.userNodeForPackage(this.getClass());

	private BugzillaExportDialog(JFrame owner, TaskImpl[] tasks, TaskGraphComponent graph) {
		super(owner, "Bugzilla export", true);

		_tasks = new TaskImpl[tasks.length];
		System.arraycopy(tasks, 0, _tasks, 0, tasks.length);
		Arrays.sort(_tasks, new Comparator<TaskImpl>(){
			@Override
			public int compare(TaskImpl o1, TaskImpl o2) {
				int res = o1.getMan().getName().compareTo(o2.getMan().getName());
				if(res != 0) {
					return res;
				}
				return o1.getName().compareTo(o2.getName());
			}});
		_graph = graph;
		constructData();
		createActions();

		JPanel mainP = new JPanel(new BorderLayout(0, 12));
		okB = new JButton(_okAction);
		cancelB = new JButton(_cancelAction);
		Box butP = Box.createHorizontalBox();
		butP.add(Box.createHorizontalGlue());
		butP.add(okB);
		butP.add(Box.createHorizontalStrut(10));
		butP.add(cancelB);
		mainP.add(butP, BorderLayout.SOUTH);
		getContentPane().add(mainP);
		JPanel mainPanel = createMainPanel();
		mainP.add(mainPanel, BorderLayout.CENTER);

		mainP.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
		
		setDefaultActions(getRootPane());
		this.addWindowListener(new WindowAdapter(){
		    public void windowClosed(WindowEvent e) {
		    	saveTextFields();
		    }
		});
	}
	
	private void saveTextFields() {
		_prefs.put("baseUrl", _baseUrlTF.getText());
		_prefs.put("user", _userTF.getText());
		_prefs.put("product", _productTF.getText());
		_prefs.put("version", _versionTF.getText());
		_prefs.put("component", _componentTF.getText());
		_prefs.put("blocks", _blocksTF.getText());
		_prefs.put("hardware", _hardwareTF.getText());
		_prefs.put("os", _osTF.getText());
		_prefs.put("priority", _priorTF.getText());
		_prefs.put("severity", _severTF.getText());
		_prefs.put("keywords", _keywordsTF.getText());
	}
	
	private void setDefaultActions(JRootPane rootPane) {
		KeyStroke strokeEsc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		//KeyStroke strokeEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(strokeEsc, "ESCAPE");
		//inputMap.put(strokeEnter, "ENTER");
		rootPane.getActionMap().put("ESCAPE", _cancelAction);
		//rootPane.getActionMap().put("ENTER", _okAction);
	}

	private JPanel createMainPanel() {
		JPanel p = new JPanel(new BorderLayout(0, 12));
		_tasksTable = new JTable(_tasksModel = new TasksModel()) {
			public Component prepareRenderer(TableCellRenderer renderer, int row,
					int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				return c;
			}
		};
		_tasksTable.getColumnModel().getColumn(INDEX_ENABLED).setMaxWidth(25);
		_tasksTable.getColumnModel().getColumn(INDEX_ENABLED).setMinWidth(25);
		_tasksTable.getColumnModel().getColumn(INDEX_COLOR).setMaxWidth(50);
		MouseListener popupListener = new MouseAdapter(){
			void showPopup(MouseEvent e) {
				JPopupMenu pm = new JPopupMenu();
				pm.add(new AbstractAction("Select All") {
					public void actionPerformed(ActionEvent e) {
						selectAll(true);
					}});
				pm.add(new AbstractAction("Unselect All") {
					public void actionPerformed(ActionEvent e) {
						selectAll(false);
					}});
				pm.show((Component)e.getSource(), e.getX(), e.getY());
			}
		    public void mouseClicked(MouseEvent e) {
		    	if(e.isPopupTrigger()) {
		    		showPopup(e);
		    	}
		    }
		    public void mousePressed(MouseEvent e) {
		    	if(e.isPopupTrigger()) {
		    		showPopup(e);
		    	}
		    }
		    public void mouseReleased(MouseEvent e) {
		    	if(e.isPopupTrigger()) {
		    		showPopup(e);
		    	}
		    }
		};
		_tasksTable.addMouseListener(popupListener);
		
		JScrollPane sp = new JScrollPane(_tasksTable);
		sp.addMouseListener(popupListener);
		sp.setPreferredSize(new Dimension(700,200));
		JPanel contentP = new JPanel();
		p.add(sp, BorderLayout.CENTER);

		_logArea = new JTextPane();
		((JTextPane)_logArea).setContentType("text/html");
		_logArea.setEditable(false);
		_logArea.setText("<html>Log:");
		
		String domain="";
		try {
			domain = InetAddress.getLocalHost().getCanonicalHostName();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
		_baseUrlTF = new JTextField(_prefs.get("baseUrl", "http://"));
		_userTF = new JTextField(_prefs.get("user", System.getProperty("user.name") + "@" + domain));
		_passwdTF = new JPasswordField("");
		_productTF = new JTextField(_prefs.get("product", ""));
		_versionTF = new JTextField(_prefs.get("version", ""));
		_componentTF = new JTextField(_prefs.get("component", ""));
		_blocksTF = new JTextField(_prefs.get("blocks", ""));
		_hardwareTF = new JTextField(_prefs.get("hardware", "All"));
		_osTF = new JTextField(_prefs.get("os", "All"));
		_priorTF = new JTextField(_prefs.get("priority", "P2"));
		_severTF = new JTextField(_prefs.get("severity", "enhancement"));
		_keywordsTF = new JTextField(_prefs.get("keywords", ""));

		// layout components
		contentP.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();

		// add labels
		gc.gridx = 0;
		gc.gridy = -1;
		gc.fill = GridBagConstraints.NONE;
		gc.insets.bottom = 5;
		gc.anchor = GridBagConstraints.EAST;
		//
		gc.gridy++;
		contentP.add(new JLabel("Bugzilla URL:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("User:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("Password:"), gc);

		gc.gridy++;
		gc.gridwidth = 4;
		gc.insets.top = 8;
		gc.insets.bottom = 8;
		gc.fill = GridBagConstraints.HORIZONTAL;
		contentP.add(new JSeparator(JSeparator.HORIZONTAL), gc);
		gc.insets.top = 0;
		gc.insets.bottom = 4;
		gc.gridwidth = 1;
		gc.fill = GridBagConstraints.NONE;

		gc.gridy++;
		contentP.add(new JLabel("Product:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("Version:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("Component:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("Blocks:"), gc);
		gc.gridx += 2;
		gc.gridy = 3;
		gc.insets.left = 10;
		gc.gridy++;
		contentP.add(new JLabel("Hardware:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("OS:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("Priority:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("Severity:"), gc);
		gc.gridy++;
		contentP.add(new JLabel("Keywords:"), gc);

		// add edit fields
		gc.gridx = 1;
		gc.gridy = -1;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;
		gc.insets.left = 8;
		//
		gc.gridwidth = 3;
		gc.gridy++;
		contentP.add(_baseUrlTF, gc);
		gc.gridy++;
		contentP.add(_userTF, gc);
		gc.gridy++;
		contentP.add(_passwdTF, gc);
		gc.gridwidth = 1;
		gc.gridy++;
		gc.gridy++;
		contentP.add(_productTF, gc);
		gc.gridy++;
		contentP.add(_versionTF, gc);
		gc.gridy++;
		contentP.add(_componentTF, gc);
		gc.gridy++;
		contentP.add(_blocksTF, gc);
		gc.gridx += 2;
		gc.gridy = 3;
		gc.gridy++;
		contentP.add(_hardwareTF, gc);
		gc.gridy++;
		contentP.add(_osTF, gc);
		gc.gridy++;
		contentP.add(_priorTF, gc);
		gc.gridy++;
		contentP.add(_severTF, gc);
		gc.gridy++;
		contentP.add(_keywordsTF, gc);

		p.add(contentP, BorderLayout.NORTH);
		JScrollPane logSP = new JScrollPane(_logArea);
		logSP.setPreferredSize(new Dimension(100,100));
		logSP.setMaximumSize(new Dimension(100,100));
		p.add(logSP, BorderLayout.SOUTH);
		return p;
	}

	private void createActions() {
		_cancelAction = new AbstractAction("Cancel") {
			public void actionPerformed(ActionEvent e) {
				dispose();
			}
		};

		_okAction = new AbstractAction("Submit") {
			public void actionPerformed(ActionEvent e) {
				submit();
			}
		};
	}

	private void constructData() {

		SimpleDateFormat statusWhiteboardFormat = new SimpleDateFormat("ddMMyy");

		_tasksData = new Object[_tasks.length][];
		for (int i = 0; i < _tasks.length; i++) {
			TaskImpl task = _tasks[i];
			long startTime = task.getStartTime() * Utils.MILLISECONDS_PER_DAY;
			
			// in bugzilla we want the end date to be the
			// last day the worker will work on the task,
			// not the day after (mathematically)
			
			long endTime = (Utils.countFinishTime(task.getStartTime(), task
					.getDuration()) -1) * Utils.MILLISECONDS_PER_DAY;
			_tasksData[i] = new Object[COL_NAMES.length];
			_tasksData[i][INDEX_ENABLED] = Boolean.valueOf(true);
			if(_tasks[i].getBugId() != null) {
				_tasksData[i][INDEX_BUG_ID] = _tasks[i].getBugId();
			}
			ColorLabel taskColLabel = _tasks[i].getColorLabel();
			if(taskColLabel == null) {
				_tasksData[i][INDEX_COLOR] = ColorLabel.COLOR_LABELS[0]._icon;
			} else {
				_tasksData[i][INDEX_COLOR] = taskColLabel._icon;
			}
			_tasksData[i][INDEX_NAME] = task.getName();
			_tasksData[i][INDEX_MAN] = task.getMan().getName();
			_tasksData[i][INDEX_HOURS] = Integer.valueOf(
					(int) ((double) task.getDuration() * 8d * 0.8));
			_tasksData[i][INDEX_REMAINS] = Integer.valueOf(
					(int) ((double) (task.getDuration()-task.getActualDuration()) * 8d * 0.8));
			if(((Integer)_tasksData[i][INDEX_REMAINS]) < 0) {
				_tasksData[i][INDEX_REMAINS] = 0;
			}
			_tasksData[i][INDEX_STATUS_WHITEBOARD] = statusWhiteboardFormat.format(new Date(startTime))
					+ "-" + statusWhiteboardFormat.format(new Date(endTime));
		}
	}
	
	private void selectAll(boolean value) {
		for(int i = 0; i < _tasksData.length; i++) {
			_tasksData[i][INDEX_ENABLED] = value;
		}
		((TasksModel)_tasksTable.getModel()).fireTableDataChanged();
	}

	class TasksModel extends AbstractTableModel {

		public String getColumnName(int column) {
			return COL_NAMES[column];
		}

		public Class<?> getColumnClass(int col) {
			return COL_CLASSES[col];
		}

		public int getColumnCount() {
			return COL_NAMES.length;
		}

		public int getRowCount() {
			return _tasksData.length;
		}

		public boolean isCellEditable(int rowIndex, int columnIndex) {
			return true;
		}

		public Object getValueAt(int rowIndex, int columnIndex) {
			return _tasksData[rowIndex][columnIndex];
		}

		public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
			_tasksData[rowIndex][columnIndex] = aValue;
		}

	}

	private void submit() {
		_okAction.setEnabled(false);
		new Thread() {
			public void run() {
				try {
					int success = 0;
					int fails = 0;
					for (int i = 0; BugzillaExportDialog.this.isShowing() && i < _tasks.length; i++) {
						if (((Boolean) _tasksData[i][INDEX_ENABLED]).booleanValue()) {

							final int row = i;
							SwingUtilities.invokeAndWait(new Runnable(){
								public void run() {
									_tasksTable.getSelectionModel().setSelectionInterval(row, row);
									_tasksTable.scrollRectToVisible(_tasksTable.getCellRect(row, 0, true));
								}
							});
							
							if(submitTask(i, _tasksData[i], _tasks[i])) {
								success++;
								SwingUtilities.invokeAndWait(new Runnable(){
									public void run() {
										_tasksData[row][INDEX_ENABLED] = Boolean.FALSE;
										((TasksModel)_tasksTable.getModel()).fireTableDataChanged();
									}
								});
							} else {
								fails++;
							}
						}
					}
					if(success > 0) {
						logMsg("Submitted " + success + " tasks");
						
						// bug IDs should be saved, setting project as dirty.
						_graph.getGraphRepresentation().setDirty();
					}
					if(fails > 0) {
						logMsg("Failed to submit " + fails + " tasks");
					}
				} catch(InterruptedException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} finally {
					_okAction.setEnabled(true);
				}
			}
		}.start();
	}

	private boolean submitTask(int index, Object[] taskData, TaskImpl task) {
		
		BugzillaSubmitter bs = new BugzillaSubmitter();
		Map<String, String> taskProps = new HashMap<String, String>();
		taskProps.put(BugzillaSubmitter.SUMMARY, (String) taskData[INDEX_NAME]);
		taskProps.put(BugzillaSubmitter.ASSIGNED_TO, (String) taskData[INDEX_MAN]);
		taskProps.put(BugzillaSubmitter.ESTIMATED_TIME, String.valueOf((Integer) taskData[INDEX_HOURS]));
		taskProps.put(BugzillaSubmitter.REMAINING_TIME, String.valueOf((Integer) taskData[INDEX_REMAINS]));
		taskProps.put(BugzillaSubmitter.STATUS_WHITEBOARD, (String) taskData[INDEX_STATUS_WHITEBOARD]);
		
		taskProps.put(BugzillaSubmitter.PRODUCT, _productTF.getText());
		taskProps.put(BugzillaSubmitter.VERSION, _versionTF.getText());
		taskProps.put(BugzillaSubmitter.COMPONENT, _componentTF.getText());
		taskProps.put(BugzillaSubmitter.BLOCKS, _blocksTF.getText());
		taskProps.put(BugzillaSubmitter.HARDWARE, _hardwareTF.getText());
		taskProps.put(BugzillaSubmitter.OS, _osTF.getText());
		taskProps.put(BugzillaSubmitter.PRIORITY, _priorTF.getText());
		taskProps.put(BugzillaSubmitter.SEVERITY, _severTF.getText());
		taskProps.put(BugzillaSubmitter.DESCRIPTION, ""); // required by bugzilla v. 2.2, (3.0 doesn't)
		taskProps.put(BugzillaSubmitter.KEYWORDS, _keywordsTF.getText());
		
		// this is needed by GMC Bugzilla. Hope it will not brake other Bugzillas.
		taskProps.put("target_milestone", "---");
		taskProps.put("bug_file_loc", "");
		taskProps.put("longdesclength", "1");

		try {
			
			String bugId = task.getBugId();
			if(bugId != null && bugId.trim().length() > 0) {
				// don't change remaining time and 'BLOCKS'
				taskProps.remove(BugzillaSubmitter.REMAINING_TIME);
				taskProps.remove(BugzillaSubmitter.BLOCKS);
				bs.change(_baseUrlTF.getText(), _userTF.getText(),
						_passwdTF.getText(), bugId, taskProps);
				logMsg("- Changed bug #" + bugId + " for task '" + task.getName() + "'\n");
			} else {
				bugId = bs.submit(_baseUrlTF.getText(), _userTF.getText(),
						_passwdTF.getText(), taskProps);
				logMsg("- Submitted bug #" + bugId + " for task '" + task.getName() + "'\n");
				task.setBugId(bugId);
				_tasksData[index][INDEX_BUG_ID] = bugId;
				_tasksModel.fireTableCellUpdated(index, INDEX_BUG_ID);
			}
			return true;
		} catch (Exception e) {
			String msg;
			if(e instanceof IOException || e.getMessage() == null) {
				msg = e.toString();
			} else {
				msg = e.getMessage();
			}
			logMsg("<font color=\"#ff0000\"><b>- Error submitting task '" + task.getName()
					+ "'</b><br>\n&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;" + msg
					+ "</font>\n");
			return false;
		}
	}

	private void logMsg(String msg) {
		HTMLEditorKit kit =
	    (HTMLEditorKit) _logArea.getEditorKit();
	  Document doc = _logArea.getDocument();
	  StringReader reader = new StringReader(msg);
	  try {
			kit.read(reader, doc, doc.getLength());
		} catch (IOException e) {
			// NOTHING TO DO
		} catch (BadLocationException e) {
			// NOTHING TO DO
		}
		int len = doc.getLength();
		_logArea.setSelectionStart(len);
		_logArea.setSelectionEnd(len);
		_logArea.setPreferredSize(new Dimension(100,100));
		_logArea.setMaximumSize(new Dimension(100,100));
	}

	public static void openDialog(JFrame owner, TaskImpl[] tasks, TaskGraphComponent graph) {
		BugzillaExportDialog d = new BugzillaExportDialog(owner, tasks, graph);
		d.pack();
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
	}

}
