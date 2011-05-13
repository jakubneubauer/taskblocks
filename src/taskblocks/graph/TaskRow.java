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

package taskblocks.graph;

import java.awt.Rectangle;

/**
 * Representation of tasks row (for one man).
 */
class TaskRow extends GraphObject {
	
	Object _userManObject;
	public Task[] _tasks;
	
	/** index in the rows array. Used when one needs to know the order of rows */
	int _index;
	
	/** Position from top in pixels (when scale==1). */
	int _topPosition;
	
	/** not pixels, will be multiplied by some factor */
	int _topPadding;
	/** not pixels, will be multiplied by some factor */
	int _bottomPadding;
	
	String _name;
	
	/** Workload of the associated worker. will be used to enlarge his tasks duration */
	double _workload;
	
	Rectangle _bounds = new Rectangle();
	
	public TaskRow(Object manObj) {
		_userManObject = manObj;
	}
	
	public String toString() {
		return _name;
	}
	
	public String getLabel() {
		if(_workload == 1.0) {
			return _name;
		} else {
			return _name + " (" + ((int)(_workload * 100.0)) + "%)";
		}
	}
}
