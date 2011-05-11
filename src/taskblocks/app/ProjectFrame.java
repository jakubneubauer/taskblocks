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

package taskblocks.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import taskblocks.graph.GraphActionListener;
import taskblocks.graph.TaskGraphComponent;
import taskblocks.io.BugzillaExportDialog;
import taskblocks.io.ProjectSaveLoad;
import taskblocks.io.WrongDataException;
import taskblocks.modelimpl.ManImpl;
import taskblocks.modelimpl.TaskImpl;
import taskblocks.modelimpl.TaskModelImpl;
import taskblocks.modelimpl.TaskPainterImpl;
import taskblocks.modelimpl.UndoActionTaskModify;
import taskblocks.modelimpl.UndoManager;

public class ProjectFrame extends JFrame implements WindowListener, GraphActionListener {
	
	public class EditMenuListener implements MenuListener {

		public void menuCanceled(MenuEvent arg0) {}

		public void menuDeselected(MenuEvent arg0) {
		}

		public void menuSelected(MenuEvent arg0) {
			JMenu editMenu = (JMenu) arg0.getSource();
			final UndoManager um = _taskModel.getUndoManager();
			editMenu.removeAll();
			if(um.canUndo()) {
				editMenu.add(new AbstractAction("Undo " + um.getFirstUndoActionLabel()){
					public void actionPerformed(ActionEvent arg0) {
						_graph.getGraphRepresentation().updateModel(); // GUI -> model update
						um.undo();
						_graph.setModel(_taskModel); // model -> GUI
						_graph.getGraphRepresentation().setDirty(); // the model->GUI resetted the dirty flag
						_graph.repaint();
					}});
			}
			if(um.canRedo()) {
				editMenu.add(new AbstractAction("Redo " + um.getFirstRedoActionLabel()){
					public void actionPerformed(ActionEvent arg0) {
						_graph.getGraphRepresentation().updateModel(); // GUI -> model update
						um.redo();
						_graph.setModel(_taskModel); // model -> GUI
						_graph.getGraphRepresentation().setDirty(); // the model->GUI resetted the dirty flag
						_graph.repaint();
					}});
			}
		}

	}

	static int _numWindows;
	static List<JMenuItem> _windowMenuItems = new ArrayList<JMenuItem>();

	TaskModelImpl _taskModel;
	TaskGraphComponent _graph;
	
	File _file;
	boolean _newCleanProject;
	JCheckBoxMenuItem _myWindowMenuItem;
	
	Preferences _prefs = Preferences.userNodeForPackage(this.getClass());

	
	Action _shrinkAction = new MyAction("Shrink", TaskBlocks.getImage("shrink.png"), "Shrink tasks as near as possible") {
		public void actionPerformed(ActionEvent e) {
			_graph.getGraphRepresentation().shrinkTasks();
			_graph.repaint();
		}
	};
	
	Action _scaleDownAction = new MyAction("Zoom Out", TaskBlocks.getImage("zoomOut.png")) {
		public void actionPerformed(ActionEvent arg0) {
			_graph.scaleDown();
		}
	};
	
	Action _scaleUpAction = new MyAction("Zoom In", TaskBlocks.getImage("zoomIn.png")) {
		public void actionPerformed(ActionEvent arg0) {
			_graph.scaleUp();
		}
	};
	
	Action _closeFileAction = new MyAction("Close") {
		public void actionPerformed(ActionEvent arg0) {
			tryClose();
		}
	};
	
	Action _loadFileAction = new MyAction("Open...", TaskBlocks.getImage("folder.gif"), "Lets you open an existing project") {
		public void actionPerformed(ActionEvent e) {

			File f = null;
			if(TaskBlocks.RUNNING_ON_MAC || TaskBlocks.RUNNING_ON_WINDOWS) {
				// MacOS & Windows user feeling
				FileDialog fd = new FileDialog(ProjectFrame.this, "blabla", FileDialog.LOAD);
				fd.setVisible(true);
				if(fd.getFile() != null) {
					f = new File(fd.getDirectory(), fd.getFile());
				}
			} else {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.showOpenDialog(ProjectFrame.this);
				f = fc.getSelectedFile();
			}
			
			if(f != null) {
				openFile(f);
			}
		}
	};
	
	class OpenRecentFileAction extends MyAction {
		String _path;
		public OpenRecentFileAction(String path) {
			super(path);
			_path = path;
		}
		public void actionPerformed(ActionEvent e) {
			openFile(new File(_path));
		}
	}
	
	Action _newFileAction = new MyAction("New Project") {
		public void actionPerformed(ActionEvent e) {
			new ProjectFrame();
		}
	};

	Action _saveAction = new MyAction("Save", TaskBlocks.getImage("save.png")) {
		public void actionPerformed(ActionEvent e) {
			save();
		}
	};
	
	Action _saveAsAction = new MyAction("Save As...") {
		public void actionPerformed(ActionEvent e) {
			File oldFile = _file;
			_file = null;
			if(!save()) {
				_file = oldFile;
			}
		}
	};
	
	Action _leftAction = new MyAction("Left", TaskBlocks.getImage("left.gif"), "Scrolls left") {
		public void actionPerformed(ActionEvent e) {
			_graph.moveLeft();
		}
	};

	Action _focusTodayAction = new MyAction("Focus on today", TaskBlocks.getImage("down.gif"), "Scrolls to current day") {
		public void actionPerformed(ActionEvent e) {
			_graph.focusOnToday();
		}
	};

	Action _rightAction = new MyAction("Right", TaskBlocks.getImage("right.gif"), "Scrolls right") {
		public void actionPerformed(ActionEvent e) {
			_graph.moveRight();
		}
	};
	
	ChangeListener _graphChangeListener = new ChangeListener(){
		public void stateChanged(ChangeEvent e) {
			_newCleanProject = false;
			updateActionsEnableState();
		}
	};
	
	Action _minimizeAction = new MyAction("Minimize"){
		public void actionPerformed(ActionEvent e) {
			ProjectFrame.this.setExtendedState(JFrame.ICONIFIED);
		}
	};

	Action _newTaskAction = new MyAction("New Task...", TaskBlocks.getImage("newtask.png"), "Opens the New Task Wizard"){
		public void actionPerformed(ActionEvent e) {
			TaskConfigDialog.openDialog(ProjectFrame.this, null, _taskModel, _graph, true);
		}
	};

	Action _newManAction = new MyAction("New Worker...", TaskBlocks.getImage("newman.png"), "Opens the New Worker Wizard"){
		public void actionPerformed(ActionEvent e) {
			ManConfigDialog.openDialog(ProjectFrame.this, null, _taskModel, _graph, true);
		}
	};
	Action _aboutAction = new MyAction("About..."){
		public void actionPerformed(ActionEvent e) {
			AboutDialog.showAbout(ProjectFrame.this);
		}
	};

	Action _bugzillaSubmit = new MyAction("Export to Bugzilla...", TaskBlocks.getImage("bugzilla.png"), "Opens the Bugzilla Export dialog"){
		public void actionPerformed(ActionEvent e) {
			BugzillaExportDialog.openDialog(ProjectFrame.this, _taskModel._tasks, new ChangeListener() {
				public void stateChanged(ChangeEvent arg0) {
					_graph.getGraphRepresentation().setDirty();
				}
			});
			
		}};
		
	Action _deleteSel = new MyAction("Delete Selection", TaskBlocks.getImage("delete.gif"), "Deletes selected objects") {
		public void actionPerformed(ActionEvent e) {
			_graph.deleteSelection();
		}
	}; 

	/**
	 * creates window with empty project.
	 */
	public ProjectFrame() {
		this(TaskModelImpl.createEmptyModel());
		setTitle("New project");
		_newCleanProject = true;
		updateActionsEnableState();
	}
	
	public ProjectFrame(File f) throws WrongDataException {
		this(new ProjectSaveLoad().loadProject(f));
		setFile(f);
		updateActionsEnableState();
	}
	
	private ProjectFrame(TaskModelImpl model) {
		// there are some issues with transparent icon in frame, so we use icon
		// with filled background.
		this.setIconImage(TaskBlocks.getImage("frameicon32.png").getImage());
		_taskModel = model;
		buildGui();
		pack();
		setSize(800,400);
		fillMenu();
		_graph.setGraphChangeListener(_graphChangeListener);
		this.addWindowListener(this);
		setLocationRelativeTo(null);
		setVisible(true);
		_numWindows++;
	}

	/**
	 * Opens the specified file. If this frame contains empty project, opens the file in this frame.
	 * If project in this frame is modified, opens new window with it.
	 * 
	 * @param f
	 */
	public void openFile(File f) {
		try {
			// check if the project is empty
			_graph.getGraphRepresentation().updateModel();
			if(_taskModel._tasks.length == 0) {
				_taskModel = new ProjectSaveLoad().loadProject(f);
				_graph.setModel(_taskModel);
				setFile(f);
				updateActionsEnableState();
			} else {
				new ProjectFrame(f);
			}
			
		} catch (WrongDataException e) {
			JOptionPane.showMessageDialog(null, "<html><b>Couldn't Open File</b><br><br><font size=\"-2\">" + e.getMessage() + "<br><br>");
		} catch(Exception e) {
			JOptionPane.showMessageDialog(null, "<html><b>Couldn't Open File</b><br><br><font size=\"-2\">" + e.getMessage() + "<br><br>");
		}
	}

	public void setTitle(String title) {
		super.setTitle(title + " - Task Blocks");
		_myWindowMenuItem.setText(title);
	}

	public void windowActivated(WindowEvent arg0) {
	}
	
	public void windowClosed(WindowEvent arg0) {
		_numWindows--;
		_windowMenuItems.remove(_myWindowMenuItem);
		if(_numWindows <= 0) {
			System.exit(0);
		}
	}
	
	public void windowClosing(WindowEvent arg0) {
		tryClose();
	}
	
	public void windowDeactivated(WindowEvent arg0) {}
	
	public void windowDeiconified(WindowEvent arg0) {}
	
	public void windowIconified(WindowEvent arg0) {}
	
	public void windowOpened(WindowEvent arg0) {}

	private void buildGui() {
		
		// create components
		JPanel mainP = new JPanel(new BorderLayout());
		MyToolBar toolB = new MyToolBar();
		_graph = new TaskGraphComponent(_taskModel, new TaskPainterImpl());
		
		// setup toolbar actions
		toolB.add(_loadFileAction);
		toolB.add(_saveAction);
		toolB.add(Box.createHorizontalStrut(4));
		toolB.add(new FixedSeparator(FixedSeparator.VERTICAL));
		toolB.add(Box.createHorizontalStrut(4));
		toolB.add(_newTaskAction);
		toolB.add(_newManAction);
		toolB.add(_shrinkAction);
		toolB.add(Box.createHorizontalStrut(4));
		toolB.add(new FixedSeparator(FixedSeparator.VERTICAL));
		toolB.add(Box.createHorizontalStrut(4));
		toolB.add(_scaleUpAction);
		toolB.add(_scaleDownAction);
		toolB.add(Box.createHorizontalStrut(8));
		toolB.add(_leftAction);
		toolB.add(_rightAction);
		toolB.add(_focusTodayAction);
		toolB.add(Box.createHorizontalStrut(4));
		toolB.add(new FixedSeparator(FixedSeparator.VERTICAL));
		toolB.add(Box.createHorizontalStrut(4));
		toolB.add(_deleteSel);
		// set component's properties
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		mainP.add(toolB, BorderLayout.NORTH);
		mainP.add(_graph, BorderLayout.CENTER);
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(mainP);
		//toolB.setRollover(true);
		toolB.setFloatable(false);
		
		_graph.setGraphActionListener(this);
	}
	
	private void fillMenu() {
		JMenuBar menu = getJMenuBar();
		if(menu == null) {
			menu = new JMenuBar();
			this.setJMenuBar(menu);
		}
		JMenu menuFile = new JMenu("File");
		menuFile.add(_newFileAction).setAccelerator(getAcceleratorStroke('N'));
		menuFile.add(_loadFileAction).setAccelerator(getAcceleratorStroke('O'));
		menuFile.add(new JSeparator());
		menuFile.add(_saveAction).setAccelerator(getAcceleratorStroke('S'));
		menuFile.add(_saveAsAction);
		menuFile.add(new JSeparator());
		fillRecentFilesMenu(menuFile);
		menuFile.add(new JSeparator());
		menuFile.add(_closeFileAction).setAccelerator(getAcceleratorStroke('W'));
		menu.add(menuFile);
		
		JMenu menuEdit = new JMenu("Edit");
		menuEdit.addMenuListener(new EditMenuListener());
		menu.add(menuEdit);
		
		JMenu menuProject = new JMenu("Project");
		menuProject.add(_newTaskAction).setAccelerator(getAcceleratorStroke('T'));
		menuProject.add(_newManAction).setAccelerator(getAcceleratorStroke('U'));
		menuProject.add(new JSeparator());
		JMenuItem mi = menuProject.add(_deleteSel);
		if(TaskBlocks.RUNNING_ON_MAC) {
			mi.setAccelerator(getAcceleratorStroke(KeyEvent.VK_BACK_SPACE));
		} else {
			mi.setAccelerator(getAcceleratorStroke(KeyEvent.VK_DELETE));
		}
		menuProject.add(new JSeparator());
		menuProject.add(_shrinkAction).setAccelerator(getAcceleratorStroke('R'));
		menuProject.add(_bugzillaSubmit);
		menu.add(menuProject);
		
		
		final JMenu menuWindow = new JMenu("Window");
		_myWindowMenuItem = new JCheckBoxMenuItem("???");
		_myWindowMenuItem.setFont(_myWindowMenuItem.getFont().deriveFont(Font.PLAIN));
		_myWindowMenuItem.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				ProjectFrame.this.toFront();
			}});
		_windowMenuItems.add(_myWindowMenuItem);
		menuWindow.addMenuListener(new MenuListener(){
			public void menuCanceled(MenuEvent e) {
			}

			public void menuDeselected(MenuEvent e) {
			}

			public void menuSelected(MenuEvent e) {
				menuWindow.removeAll();
				JMenuItem minItem = menuWindow.add(new JMenuItem(_minimizeAction));
				minItem.setAccelerator(getAcceleratorStroke('M'));
				minItem.setFont(minItem.getFont().deriveFont(Font.PLAIN));
				menuWindow.add(new JSeparator());
				for(JMenuItem winItem: _windowMenuItems) {
					menuWindow.add(winItem);
					winItem.setSelected(winItem == _myWindowMenuItem);
				}
			}});
		menu.add(menuWindow);
		
		if(!TaskBlocks.RUNNING_ON_MAC) {
			JMenu menuHelp = new JMenu("Help");
			menuHelp.add(_aboutAction);
			menu.add(menuHelp);
		}

		// not bold menu items and on mac without icons
		for(int i = 0; i < menu.getMenuCount(); i++) {
			JMenu subMenu = menu.getMenu(i);
			subMenu.setFont(subMenu.getFont().deriveFont(Font.PLAIN));
			for(Component c: subMenu.getMenuComponents()) {
				if(c instanceof JMenuItem) {
					// MacOS user feeling
					if(TaskBlocks.RUNNING_ON_MAC) {
						// clear icons from all menus. It is convention on Mac
						((JMenuItem)c).setIcon(null);
					}
					((JMenuItem)c).setFont(c.getFont().deriveFont(Font.PLAIN));
				}
			}
		}
	}
	
	private void fillRecentFilesMenu(JMenu menuFile) {
		try {
			Preferences p = _prefs.node("recentFiles");
			for(String child: p.childrenNames()) {
				String path = p.node(child).get("path", null);
				if(path != null) {
					menuFile.add(new OpenRecentFileAction(path));
				}
			}
		} catch (BackingStoreException e) {
			// NOTHING TO DO
		}
	}

	private void addToRecentFiles(File f) {
		try {
			Preferences p = _prefs.node("recentFiles");
			String[] childs = p.childrenNames();
			for(String child: childs) {
				String path = p.node(child).get("path", null);
				if(path != null) {
					if(path.equals(f.getAbsolutePath())) {
						// is already in recent list - do nothing
						return;
					}
				}
			}
			if(childs.length >= 5) {
				p.node(childs[0]).removeNode();
			}
			Preferences newNode = p.node(String.valueOf(System.currentTimeMillis()));
			newNode.put("path", f.getAbsolutePath());
			
		} catch (BackingStoreException e) {
			// NOTHING TO DO
		}
	}

	private KeyStroke getAcceleratorStroke(char key) {
		return KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
	}
	private KeyStroke getAcceleratorStroke(int keyCode) {
		return KeyStroke.getKeyStroke(keyCode, 0);
	}
		
	private void setFile(File f) {
		_file = f;
		setTitle(_file.getName());
		addToRecentFiles(f);
	}

	private void tryClose() {
		if(_graph.getGraphRepresentation().isSaveDirty()) {
			// not saved - ask for save
			String SAVE = "Save";
			String DONT_SAVE = "Don't Save";
			String CANCEL = "Cancel";
			Object[] options;
			
			// from unknown reasons the appearing order is reversed on Mac 
			if(TaskBlocks.RUNNING_ON_MAC) {
				options = new Object[] {SAVE, CANCEL, DONT_SAVE};
			} else {
				options = new Object[] {DONT_SAVE, SAVE, CANCEL};
			}
			JLabel l = new JLabel("<html><b>Do you want to save changes to this document<br>before closing?</b><br><br><font size=\"-2\">If you don't save, your changes will be lost.<br></font><br>");
			l.setFont(l.getFont().deriveFont(Font.PLAIN));
			JOptionPane op = new JOptionPane(l, JOptionPane.QUESTION_MESSAGE, 0, null, options);
			op.createDialog(this, _file == null ? "Unsaved project" : _file.getName()).setVisible(true);
			op.setInitialSelectionValue(CANCEL);
			Object choice = op.getValue();
			if(choice == null) {
				return; // cancel;
			}
			if(choice.equals(SAVE)) {
				// save
				if(!save()) {
					// save didn't success, don't close
					return;
				} else {
					this.dispose();
				}
			} else if(choice.equals(DONT_SAVE)) {
				this.dispose();
			}
		} else {
			this.dispose();
		}
	}
	
	private void updateActionsEnableState() {
		boolean unsaved = _newCleanProject || _graph.getGraphRepresentation().isSaveDirty();
		_saveAction.setEnabled(unsaved);
		
		// MacOS user feeling
		getRootPane().putClientProperty("windowModified", unsaved?Boolean.TRUE : Boolean.FALSE);
		_newTaskAction.setEnabled(_graph.getGraphRepresentation().getManCount() > 0);
	}

	private boolean save() {
		_graph.getGraphRepresentation().updateModel();
		File f = _file;
		if(f == null) {
			
			// ask for file
			if(TaskBlocks.RUNNING_ON_MAC || TaskBlocks.RUNNING_ON_WINDOWS) {
				// MacOS user feeling
				FileDialog fd = new FileDialog(ProjectFrame.this, "Save", FileDialog.SAVE);
				fd.setVisible(true);
				if(fd.getFile() != null) {
					f = new File(fd.getDirectory(), fd.getFile());
				}
			} else {
				JFileChooser fc = new JFileChooser();
				fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
				fc.showSaveDialog(ProjectFrame.this);
				f = fc.getSelectedFile();
			}
		}
		if(f != null) {
			try {
				new ProjectSaveLoad().saveProject(f, _taskModel);
				setFile(f);
				_graph.getGraphRepresentation().clearSaveDirtyFlag();
				return true;
			} catch (Exception e1) {
				JOptionPane.showMessageDialog(null, "<html><b>Couldn't Save</b><br><br><font size=\"-2\">" + e1.getMessage() + "<br><br>");
				e1.printStackTrace();
			}
		}
		return false;
	}
	
	private void configureTask(TaskImpl t) {
		_graph.getGraphRepresentation().updateModel(); // GUI -> model update
		TaskImpl before = t.clone();
		if(TaskConfigDialog.openDialog(this, t, _taskModel, _graph, false)) {
			_graph.setModel(_taskModel); // model -> GUI udate
			_graph.getGraphRepresentation().setDirty(); // the model->GUI resetted the dirty flag
			_graph.repaint();
			_taskModel.getUndoManager().addAction(new UndoActionTaskModify(_taskModel, before, t));
		}
	}

	private void configureMan(ManImpl man) {
		_graph.getGraphRepresentation().updateModel(); // GUI -> model update
		if(ManConfigDialog.openDialog(this, man, _taskModel, _graph, false)) {
			_graph.setModel(_taskModel); // model -> GUI udate
			_graph.getGraphRepresentation().setDirty(); // the model->GUI resetted the dirty flag
			_graph.repaint();
		}
	}

	public void graphClicked(MouseEvent e) {
	}

	public void manClicked(Object man, MouseEvent e) {
		if(man != null && e.getClickCount() >= 2) {
			configureMan((ManImpl)man);
		}
	}
	public void taskClicked(Object task, MouseEvent e) {
		if(task != null && e.getClickCount() >= 2) {
			configureTask((TaskImpl)task);
		}
	}	
}
