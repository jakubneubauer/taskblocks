package taskblocks.modelimpl;

public class UndoActionRemoveMan implements UndoAction {
	
	ManImpl _man;
	TaskModelImpl _model;
	
	public UndoActionRemoveMan(TaskModelImpl model, ManImpl man) {
		_man = man;
		_model = model;
	}

	@Override
	public String getUndoLabel() {
		return "remove man " + _man.getName();
	}

	@Override
	public String getRedoLabel() {
		return "remove man " + _man.getName();
	}

	@Override
	public void undo() {
		_model.addMan(_man);
	}

	@Override
	public void redo() {
		_model.removeManImpl(_man);
	}

}
