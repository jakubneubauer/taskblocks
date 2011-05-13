package taskblocks.modelimpl;

public class UndoActionAddMan implements UndoAction {
	
	ManImpl _man;
	TaskModelImpl _model;
	
	public UndoActionAddMan(TaskModelImpl model, ManImpl man) {
		_man = man;
		_model = model;
	}

	public String getUndoLabel() {
		return "add man " + _man.getName();
	}

	public String getRedoLabel() {
		return "add man " + _man.getName();
	}

	public void undo() {
		_model.removeManImpl(_man);
	}

	public void redo() {
		_model.addMan(_man);
	}

	public String toString() {
		return getUndoLabel();
	}
}
