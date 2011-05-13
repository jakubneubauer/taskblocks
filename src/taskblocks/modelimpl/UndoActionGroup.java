package taskblocks.modelimpl;

import java.util.ArrayList;
import java.util.List;

public class UndoActionGroup implements UndoAction {
	
	String _name;
	List<UndoAction> _actions;
	boolean _isUndone;
	
	public UndoActionGroup(String name) {
		_name = name;
		_actions = new ArrayList<UndoAction>();
	}
	
	// if only one task in group, returns it's name. Otherwise return the group name
	public String getUndoLabel() {
		if(_actions.size() == 1) {
			return _actions.get(0).getUndoLabel();
		}
		return _name;
	}

	public String getRedoLabel() {
		if(_actions.size() == 1) {
			return _actions.get(0).getRedoLabel();
		}
		return _name;
	}

	public void undo() {
		for(int i = _actions.size()-1; i >= 0; i--) {
			_actions.get(i).undo();
		}
		_isUndone = true;
	}

	public void redo() {
		for(int i = 0; i < _actions.size(); i++) {
			_actions.get(i).redo();
		}
		_isUndone = false;
	}
	
	public void addAction(UndoAction a) {
		if(_isUndone) {
			throw new RuntimeException("Internal error: Cannot add 'undo' action to group after it was undone");
		}
		_actions.add(a);
	}

	public boolean isEmpty() {
		return _actions.isEmpty();
	}
	
	public String toString() {
		return getUndoLabel();
	}
}
