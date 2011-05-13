package taskblocks.modelimpl;

public class UndoActionRemoveMan implements UndoAction {
	
	ManImpl _man;
	TaskModelImpl _model;
	
	public UndoActionRemoveMan(TaskModelImpl model, ManImpl man) {
		_man = man;
		_model = model;
	}

	public String getUndoLabel() {
		return "remove man " + _man.getName();
	}

	public String getRedoLabel() {
		return "remove man " + _man.getName();
	}

	public void undo() {
		_model.addMan(_man);
	}

	public void redo() {
		_model.removeManImpl(_man);
	}

}
