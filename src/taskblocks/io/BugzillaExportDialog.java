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
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
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

import taskblocks.Utils;
import taskblocks.bugzilla.BugzillaSubmitter;
import taskblocks.modelimpl.ColorLabel;
import taskblocks.modelimpl.TaskImpl;

public class BugzillaExportDialog extends JDialog {

	TaskImpl[] _tasks;

	JTable _tasksTable;

	Object[][] _tasksData;

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

	public BugzillaExportDialog(JFrame owner, TaskImpl[] tasks) {
		super(owner, "Bugzilla export", true);

		_tasks = tasks;
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
		_tasksTable = new JTable(new TasksModel()) {
			public Component prepareRenderer(TableCellRenderer renderer, int row,
					int column) {
				Component c = super.prepareRenderer(renderer, row, column);
				return c;
			}
		};
		//_tasksTable.setIntercellSpacing(new Dimension());
		_tasksTable.getColumnModel().getColumn(0).setMaxWidth(25);
		_tasksTable.getColumnModel().getColumn(0).setMinWidth(25);
		_tasksTable.getColumnModel().getColumn(1).setMaxWidth(50);
		JScrollPane sp = new JScrollPane(_tasksTable);
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
			long endTime = Utils.countFinishTime(task.getStartTime(), task
					.getDuration())
					* Utils.MILLISECONDS_PER_DAY;
			_tasksData[i] = new Object[6];
			_tasksData[i][0] = new Boolean(true);
			ColorLabel taskColLabel = _tasks[i].getColorLabel();
			if(taskColLabel == null) {
				_tasksData[i][1] = ColorLabel.COLOR_LABELS[0]._icon;
			} else {
				_tasksData[i][1] = taskColLabel._icon;
			}
			_tasksData[i][2] = task.getName();
			_tasksData[i][3] = task.getMan().getName();
			_tasksData[i][4] = new Integer(
					(int) ((double) task.getDuration() * 8d * 0.8));
			_tasksData[i][5] = statusWhiteboardFormat.format(new Date(startTime))
					+ "-" + statusWhiteboardFormat.format(new Date(endTime));
		}
	}

	class TasksModel extends AbstractTableModel {

		String[] COL_NAMES = new String[] { "", "Color", "Summary", "Assignee",
				"Estimated Time", "Status Whiteboard" };

		Class[] COL_CLASSES = new Class[] { Boolean.class, Icon.class, String.class,
				String.class, String.class, String.class };

		public String getColumnName(int column) {
			return COL_NAMES[column];
		}

		public Class getColumnClass(int col) {
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
						if (((Boolean) _tasksData[i][0]).booleanValue()) {

							final int row = i;
							SwingUtilities.invokeAndWait(new Runnable(){
								public void run() {
									_tasksTable.getSelectionModel().setSelectionInterval(row, row);
									_tasksTable.scrollRectToVisible(_tasksTable.getCellRect(row, 0, true));
								}
							});
							
							if(submitTask(_tasksData[i], _tasks[i])) {
								success++;
								SwingUtilities.invokeAndWait(new Runnable(){
									public void run() {
										_tasksData[row][0] = Boolean.FALSE;
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

	private boolean submitTask(Object[] taskData, TaskImpl task) {
		BugzillaSubmitter bs = new BugzillaSubmitter();
		Map<String, String> taskProps = new HashMap<String, String>();
		taskProps.put(BugzillaSubmitter.SUMMARY, (String) taskData[2]);
		taskProps.put(BugzillaSubmitter.ASSIGNED_TO, (String) taskData[3]);
		taskProps.put(BugzillaSubmitter.ESTIMATED_TIME, String.valueOf((Integer) taskData[4]));
		taskProps.put(BugzillaSubmitter.STATUS_WHITEBOARD, (String) taskData[5]);
		
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

		try {
			//String bugId = "pokus";
			String bugId = bs.submit(_baseUrlTF.getText(), _userTF.getText(),
					_passwdTF.getText(), taskProps);
			logMsg("- Submitted bug #" + bugId + " for task '" + task.getName() + "'\n");
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
		} catch (BadLocationException e) {
		}
		int len = doc.getLength();
		_logArea.setSelectionStart(len);
		_logArea.setSelectionEnd(len);
		_logArea.setPreferredSize(new Dimension(100,100));
		_logArea.setMaximumSize(new Dimension(100,100));
	}

	public static void openDialog(JFrame owner, TaskImpl[] tasks) {
		BugzillaExportDialog d = new BugzillaExportDialog(owner, tasks);
		d.pack();
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
	}

}
