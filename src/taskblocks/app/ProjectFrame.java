package taskblocks.app;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Action;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
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
import taskblocks.modelimpl.TaskImpl;
import taskblocks.modelimpl.TaskModelImpl;
import taskblocks.modelimpl.TaskPainterImpl;

public class ProjectFrame extends JFrame implements WindowListener, GraphActionListener {
	
	static int _numWindows;
	static List<JMenuItem> _windowMenuItems = new ArrayList<JMenuItem>();

	TaskModelImpl _taskModel;
	TaskGraphComponent _graph;
	
	File _file;
	boolean _newCleanProject;
	JCheckBoxMenuItem _myWindowMenuItem;
	
	Action _shrinkAction = new MyAction("Shrink", getImage("shrink.png"), "Shrink tasks as near as possible") {
		public void actionPerformed(ActionEvent e) {
			_graph.getGraphRepresentation().shrinkTasks();
			_graph.repaint();
		}
	};
	
	Action _scaleDownAction = new MyAction("Zoom Out", getImage("zoomOut.png")) {
		public void actionPerformed(ActionEvent arg0) {
			_graph.scaleDown();
		}
	};
	
	Action _scaleUpAction = new MyAction("Zoom In", getImage("zoomIn.png")) {
		public void actionPerformed(ActionEvent arg0) {
			_graph.scaleUp();
		}
	};
	
	Action _closeFileAction = new MyAction("Close") {
		public void actionPerformed(ActionEvent arg0) {
			tryClose();
		}
	};
	
	Action _loadFileAction = new MyAction("Open...", getImage("folder.gif"), "Lets you open an existing project") {
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
	
	Action _newFileAction = new MyAction("New Project") {
		public void actionPerformed(ActionEvent e) {
			new ProjectFrame();
		}
	};

	Action _saveAction = new MyAction("Save", getImage("save.png")) {
		public void actionPerformed(ActionEvent e) {
			save();
		}
	};
	
	Action _leftAction = new MyAction("Left", getImage("left.gif"), "Scrolls left") {
		public void actionPerformed(ActionEvent e) {
			_graph.moveLeft();
		}
	};

	Action _focusTodayAction = new MyAction("Focus on today", getImage("down.gif"), "Scrolls to current day") {
		public void actionPerformed(ActionEvent e) {
			_graph.focusOnToday();
		}
	};

	Action _rightAction = new MyAction("Right", getImage("right.gif"), "Scrolls right") {
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

	Action _newTaskAction = new MyAction("New Task...", getImage("newtask.png"), "Opens the New Task Wizard"){
		public void actionPerformed(ActionEvent e) {
			TaskConfigDialog.openDialog(ProjectFrame.this, null, _taskModel, _graph, true);
		}
	};

	Action _newManAction = new MyAction("New Worker...", getImage("newman.png"), "Opens the New Worker Wizard"){
		public void actionPerformed(ActionEvent e) {
			ManConfigDialog.openDialog(ProjectFrame.this, null, _taskModel, _graph, true);
		}
	};
	Action _aboutAction = new MyAction("About..."){
		public void actionPerformed(ActionEvent e) {
			AboutDialog.showAbout(ProjectFrame.this);
		}
	};

	Action _bugzillaSubmit = new MyAction("Export to Bugzilla...", getImage("bugzilla.png"), "Opens the Bugzilla Export dialog"){
		public void actionPerformed(ActionEvent e) {
			BugzillaExportDialog.openDialog(ProjectFrame.this, _taskModel._tasks);
			
		}}; 

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
		}
	}

	public void setTitle(String title) {
		super.setTitle("TaskMan - " + title);
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
		toolB.add(Box.createHorizontalStrut(8));
		toolB.add(_newTaskAction);
		toolB.add(_newManAction);
		toolB.add(_shrinkAction);
		toolB.add(Box.createHorizontalStrut(8));
		toolB.add(_scaleUpAction);
		toolB.add(_scaleDownAction);
		toolB.add(Box.createHorizontalStrut(8));
		toolB.add(_leftAction);
		toolB.add(_rightAction);
		toolB.add(_focusTodayAction);
		
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
		menuFile.add(new JSeparator());
		menuFile.add(_closeFileAction).setAccelerator(getAcceleratorStroke('W'));
		menu.add(menuFile);
		
		JMenu menuProject = new JMenu("Project");
		menuProject.add(_newTaskAction).setAccelerator(getAcceleratorStroke('T'));
		menuProject.add(_newManAction).setAccelerator(getAcceleratorStroke('U'));
		menuProject.add(new JSeparator());
		menuProject.add(_shrinkAction).setAccelerator(getAcceleratorStroke('R'));
		menuProject.add(new JSeparator());
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
	
	private KeyStroke getAcceleratorStroke(char key) {
		return KeyStroke.getKeyStroke(key, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask());
	}
		
	private void setFile(File f) {
		_file = f;
		setTitle(_file.getName());
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
			JOptionPane op = new JOptionPane("<html><b>Do you want to save changes to this document<br>before closing?</b><br><br><font size=\"-2\">If you don't save, your changes will be lost.<br></font>", JOptionPane.QUESTION_MESSAGE, 0, null, options);
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
				FileDialog fd = new FileDialog(ProjectFrame.this, "blabla", FileDialog.SAVE);
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
		if(TaskConfigDialog.openDialog(this, t, _taskModel, _graph, false)) {
			_graph.setModel(_taskModel); // model -> GUI udate
			_graph.getGraphRepresentation().setDirty(); // the model->GUI resetted the dirty flag
			_graph.repaint();
		}
	}

	public void mouseClicked(Object task, MouseEvent e) {
		if(task != null && e.getClickCount() >= 2) {
			configureTask((TaskImpl)task);
		}
	}

	private static ImageIcon getImage(String name) {
		URL url = ProjectFrame.class.getResource("/taskblocks/img/" + name);
		if (url == null) {
			return null;
		}
		return new ImageIcon(url);
	}	
}
