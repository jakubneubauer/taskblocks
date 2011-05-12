package taskblocks.modelimpl;

public class UndoActionAddTask implements UndoAction {

	TaskImpl _task;
	TaskModelImpl _model;
	
	public UndoActionAddTask(TaskModelImpl model, TaskImpl t) {
		_model = model;
		_task = t;
	}
	
	public String getLabel() {
		return "add task " + _task.getName();
	}

	public void undo() {
		_model.removeTaskImpl(_task);
	}

	public void redo() {
		_model.addTask(_task);
	}
	
	public String toString() {
		return getLabel();
	}

}
