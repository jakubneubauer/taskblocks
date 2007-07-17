package taskblocks.app;

import javax.swing.JFrame;
import javax.swing.JPanel;

import taskblocks.Utils;
import taskblocks.graph.TaskGraphComponent;
import taskblocks.modelimpl.ColorLabel;
import taskblocks.modelimpl.ManImpl;
import taskblocks.modelimpl.TaskImpl;
import taskblocks.modelimpl.TaskModelImpl;

public class TaskConfigDialog extends ConfigDialogStub  {
	
	TaskImpl _task;
	TaskModelImpl _model;
	TaskGraphComponent _graph;
	boolean _applied;
	
	TaskConfigPanel _cfgPanel;
	
	public TaskConfigDialog(JFrame owner, TaskImpl task, TaskModelImpl model, TaskGraphComponent graph, boolean isCreating) {
		super(owner, isCreating);
		_task = task;
		_model = model;
		_graph = graph;
		init();
	}

	public void updateTask(TaskImpl task) {
		task.setDuration(((Integer)_cfgPanel.durationSP.getValue()).intValue());
		task.setName(_cfgPanel.nameTF.getText());
		task.setMan(((ManImpl)_cfgPanel.manCB.getSelectedItem()));
		task.setColorLabel((ColorLabel)_cfgPanel._colorLabelCB.getSelectedItem());
		_applied = true;
	}
	
	public void addTask() {
		
		_graph.getGraphRepresentation().updateModel();
		
		TaskImpl t = new TaskImpl();
		
		updateTask(t);
//		t.setDuration(((Integer)_cfgPanel.durationSP.getValue()).intValue());
//		t.setName(_cfgPanel.nameTF.getText());
//		t.setMan(((ManImpl)_cfgPanel.manCB.getSelectedItem()));

		// count the last finish time of all the man's tasks
		long lastFinishTime = 0;
		for(TaskImpl tmpTask: _model._tasks) {
			if(t.getMan() == tmpTask.getMan()) {
				long finish = Utils.countFinishTime(Utils.repairStartTime(tmpTask.geSstartTime()), tmpTask.getDuration());
				if(lastFinishTime < finish) {
					lastFinishTime = finish;
				}
			}
		}
		if(lastFinishTime == 0) {
			lastFinishTime = System.currentTimeMillis()/Utils.MILLISECONDS_PER_DAY;
		}
		
		t.setStartTime(lastFinishTime);
		_model.addTask(t);
		
		_graph.setModel(_model);
		_graph.getGraphRepresentation().setDirty(); // the model->GUI resetted the dirty flag
		_graph.repaint();
		_graph.scrollToTaskVisible(t);
		
		// now focus to name and set text field selection
		_cfgPanel.nameTF.setSelectionStart(0);
		_cfgPanel.nameTF.setSelectionEnd(_cfgPanel.nameTF.getText().length());
		_cfgPanel.nameTF.requestFocus();
	}

	@Override
	JPanel createMainPanel() {
		if(_isCreating) {
			setTitle("New Task");
			_task = new TaskImpl();
			_task.setPredecessors(new TaskImpl[0]);
			_task.setMan(_model._mans[0]);
			_task.setDuration(5);
		} else {
			setTitle("Task " + _task.getName());
		}
		return _cfgPanel = new TaskConfigPanel(_task, _model);
	}

	@Override
	void doApply() {
		if(isCreating()) {
			addTask();
		} else {
			updateTask(_task);
		}
	}
	
	public static boolean openDialog(JFrame owner, TaskImpl task, TaskModelImpl model, TaskGraphComponent graph, boolean isCreating) {
		TaskConfigDialog d = new TaskConfigDialog(owner, task, model, graph, isCreating);
		d.pack();
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
		return d._applied;
	}

}
