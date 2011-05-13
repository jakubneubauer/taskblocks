package taskblocks.modelimpl;

public class UndoActionAddMan implements UndoAction {
	
	ManImpl _man;
	TaskModelImpl _model;
	
	public UndoActionAddMan(TaskModelImpl model, ManImpl man) {
		_man = man;
		_model = model;
	}

	@Override
	public String getUndoLabel() {
		return "add man " + _man.getName();
	}

	@Override
	public String getRedoLabel() {
		return "add man " + _man.getName();
	}

	@Override
	public void undo() {
		_model.removeManImpl(_man);
	}

	@Override
	public void redo() {
		_model.addMan(_man);
	}

	public String toString() {
		return getUndoLabel();
	}
}
