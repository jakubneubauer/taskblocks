package taskblocks.modelimpl;

import java.awt.Color;

import taskblocks.Colors;

public class TaskImpl {
	
	private String _name;
	private long _startTime;
	private long _duration;
	private TaskImpl[] _predecessors;
	private ManImpl _man;
	
	private ColorLabel _colorLabel;
	
	/** Used only when saving */
	public String _id;
	
	public long getDuration() {
		return _duration;
	}
	public void setDuration(long _duration) {
		if(_duration < 1) {
			_duration = 1;
		}
		this._duration = _duration;
	}
	public String getName() {
		return _name;
	}
	public void setName(String _name) {
		this._name = _name;
	}
	public long geSstartTime() {
		return _startTime;
	}
	public void setStartTime(long time) {
		_startTime = time;
	}
	public long getStartTime() {
		return _startTime;
	}
	public TaskImpl[] getPredecessors() {
		return _predecessors;
	}
	public void setPredecessors(TaskImpl[] preds) {
		_predecessors = preds;
	}
	public void setMan(ManImpl man) {
		_man = man;
	}
	public ManImpl getMan() {
		return _man;
	}
	
	public Color getColor() {
		if(_colorLabel == null) {
			return Colors.TASK_COLOR;
		} else {
			return _colorLabel._color;
		}
	}
	
	public ColorLabel getColorLabel() {
		return _colorLabel;
	}
	public void setColorLabel(ColorLabel cl) {
		_colorLabel = cl;
	}
}
