package taskblocks.modelimpl;

public class UndoActionTaskModify implements UndoAction {
	
	TaskImpl _before;
	TaskImpl _after;
	TaskModelImpl _model;
	
	public UndoActionTaskModify(TaskModelImpl model, TaskImpl before, TaskImpl after) {
		_before = before;
		_after = after.clone();
		_model = model;
	}

	public String getLabel() {
		return "Modify task " + _after.getName();
	}

	public void redo() {
		// TODO: tasky se stejnym jmenem
		for(Object t: _model.getTasks()) {
			if((t instanceof TaskImpl) && ((TaskImpl)t).getName().equals(_before.getName())) {
				((TaskImpl)t).updateFrom(_after);
			}
		}
	}

	public void undo() {
		// TODO: tasky se stejnym jmenem
		for(Object t: _model.getTasks()) {
			if((t instanceof TaskImpl) && ((TaskImpl)t).getName().equals(_after.getName())) {
				((TaskImpl)t).updateFrom(_before);
			}
		}
	}
}
