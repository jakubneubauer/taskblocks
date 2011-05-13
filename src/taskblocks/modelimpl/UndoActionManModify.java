package taskblocks.modelimpl;

public class UndoActionManModify implements UndoAction {

	ManImpl _before;
	ManImpl _after;
	ManImpl _origAfter;
	TaskModelImpl _model;
	
	public UndoActionManModify(TaskModelImpl model, ManImpl before, ManImpl after) {
		_before = before;
		_after = after.clone();
		_origAfter = after;
		_model = model;
	}

	public String getUndoLabel() {
		return "change '" + _after.getName() + "'";
	}
	
	public String getRedoLabel() {
		return "change '" + _before.getName() + "'";
	}

	public void undo() {
		for(Object t: _model.getMans()) {
			if(_origAfter == t) {
				((ManImpl)t).updateFrom(_before);
			}
		}
	}
	
	public void redo() {
		for(Object t: _model.getMans()) {
			if(_origAfter == t) {
				((ManImpl)t).updateFrom(_after);
			}
		}
	}

	public String toString() {
		return getUndoLabel();
	}
}
