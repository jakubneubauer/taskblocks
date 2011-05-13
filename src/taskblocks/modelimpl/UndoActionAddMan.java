package taskblocks.modelimpl;

public class UndoActionAddMan implements UndoAction {
	
	ManImpl _man;
	TaskModelImpl _model;
	
	public UndoActionAddMan(TaskModelImpl model, ManImpl man) {
		_man = man;
		_model = model;
	}

	@Override
	public String getLabel() {
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

}
