/*
 * Copyright (C) Jakub Neubauer, 2007
 *
 * This file is part of TaskBlocks
 *
 * TaskBlocks is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
