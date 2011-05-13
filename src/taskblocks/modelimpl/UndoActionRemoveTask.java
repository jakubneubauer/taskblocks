package taskblocks.modelimpl;

public class UndoActionRemoveTask implements UndoAction {

	TaskImpl _task;
	TaskModelImpl _model;

	public UndoActionRemoveTask(TaskModelImpl model, TaskImpl t) {
		_model = model;
		_task = t;
	}
	public String getUndoLabel() {
		return "remove task " + _task.getName();
	}
	public String getRedoLabel() {
		return "remove task " + _task.getName();
	}

	public void undo() {
		_model.addTask(_task);
	}

	public void redo() {
		_model.removeTaskImpl(_task);
	}
	
	public String toString() {
		return getUndoLabel();
	}
}
