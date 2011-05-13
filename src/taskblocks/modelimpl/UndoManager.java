package taskblocks.modelimpl;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class UndoManager {
	
	int _index;
	List<UndoAction> _actions;
	UndoActionGroup _group;
	ChangeListener _changeListener;
	
	public UndoManager() {
		_actions = new ArrayList<UndoAction>();
	}
	
	public void beginGroup(String name) {
		if(_group != null && !_group.isEmpty()) {
			addActionImpl(_group);
		}
		_group = new UndoActionGroup(name);
	}
	
	public void endGroup() {
		if(_group != null && !_group.isEmpty()) {
			addActionImpl(_group);
		}
		_group = null;
	}
	
	private void addActionImpl(UndoAction a) {
		_actions.add(_index, a);
		_index++;
		if(_index < _actions.size()) {
			_actions.subList(_index, _actions.size()).clear();
		}
		fireChange();
	}
	
	public void addAction(UndoAction a) {
		if(_group != null) {
			_group.addAction(a);
		} else {
			addActionImpl(a);
		}
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
		fireChange();
	}
	
	public void redo() {
		endGroup();
		if(_index >= _actions.size()) {
			throw new IndexOutOfBoundsException();
		}
		_index++;
		_actions.get(_index-1).redo();
		fireChange();
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

	public void setChangeListener(ChangeListener l) {
		_changeListener = l;
	}
	
	private void fireChange() {
		if(_changeListener != null) {
			_changeListener.stateChanged(new ChangeEvent(this));
		}
	}
}
