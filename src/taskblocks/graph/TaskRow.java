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
	
	Rectangle _bounds = new Rectangle();
	
	public TaskRow(Object manObj) {
		_userManObject = manObj;
	}
}
