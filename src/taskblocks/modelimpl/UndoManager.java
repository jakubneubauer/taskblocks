package taskblocks.modelimpl;

import java.util.ArrayList;
import java.util.List;

public class UndoManager {
	
	int _index;
	List<UndoAction> _actions;
	
	public UndoManager() {
		_actions = new ArrayList<UndoAction>();
	}
	
	public void addAction(UndoAction a) {
		_actions.add(_index, a);
		_index++;
		if(_index < _actions.size()) {
			_actions.subList(_index, _actions.size()).clear();
		}
	}
	
	public boolean canUndo() {
		return _index > 0;
	}
	
	public boolean canRedo() {
		return _index < _actions.size();
	}
	
	public void undo() {
		if(_index <= 0) {
			throw new IndexOutOfBoundsException();
		}
		_index--;
		_actions.get(_index).undo();
	}
	
	public void redo() {
		if(_index >= _actions.size()) {
			throw new IndexOutOfBoundsException();
		}
		_index++;
		_actions.get(_index-1).redo();
	}
	
	public String getFirstUndoActionLabel() { 
		if(_index > 0) {
			return _actions.get(_index - 1).getLabel();
		}
		return null;
	}
	
	public String getFirstRedoActionLabel() {
		if(_index < _actions.size()) {
			return _actions.get(_index).getLabel();
		}
		return null;
	}

}
