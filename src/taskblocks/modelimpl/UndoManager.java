package taskblocks.modelimpl;

import java.util.ArrayList;
import java.util.List;

public class UndoManager {
	
	int _index;
	List<UndoAction> _actions;
	UndoActionGroup _group;
	
	public UndoManager() {
		_actions = new ArrayList<UndoAction>();
	}
	
	public void beginGroup(String name) {
		//System.out.println("begin group " + name);
		if(_group != null && !_group.isEmpty()) {
			addActionImpl(_group);
		}
		_group = new UndoActionGroup(name);
	}
	
	public void endGroup() {
		//System.out.println("end group");
		if(_group != null && !_group.isEmpty()) {
			addActionImpl(_group);
		}
		_group = null;
		//System.out.println("Idx: " + _index + ", queue: " + _actions + ", Group: " + (_group == null ? "null" : (_group._actions)));
	}
	
	private void trimAtIndex() {
		if(_index < _actions.size()) {
			_actions.subList(_index, _actions.size()).clear();
		}
	}
	
	private void addActionImpl(UndoAction a) {
		_actions.add(_index, a);
		_index++;
		trimAtIndex();
	}
	
	public void addAction(UndoAction a) {
		//System.out.println("addAction");
		if(_group != null) {
			_group.addAction(a);
		} else {
			addActionImpl(a);
		}
		//System.out.println("Idx: " + _index + ", queue: " + _actions + ", Group: " + (_group == null ? "null" : (_group._actions)));
	}
	
	public boolean canUndo() {
		return _index > 0;
	}
	
	public boolean canRedo() {
		return _index < _actions.size();
	}
	
	public void undo() {
		endGroup();
		if(_index <= 0) {
			throw new IndexOutOfBoundsException();
		}
		_index--;
		_actions.get(_index).undo();
		//System.out.println("Idx: " + _index + ", queue: " + _actions + ", Group: " + (_group == null ? "null" : (_group._actions)));
	}
	
	public void redo() {
		endGroup();
		if(_index >= _actions.size()) {
			throw new IndexOutOfBoundsException();
		}
		_index++;
		_actions.get(_index-1).redo();
		//System.out.println("Idx: " + _index + ", queue: " + _actions + ", Group: " + (_group == null ? "null" : (_group._actions)));
	}
	
	public String getFirstUndoActionLabel() {
		if(_group != null && !_group.isEmpty()) {
			return _group.getLabel();
		}

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
