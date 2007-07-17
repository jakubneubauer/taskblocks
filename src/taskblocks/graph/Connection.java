package taskblocks.graph;

import java.awt.Polygon;

/**
 * Representation of dependency connection in graph.
 */
class Connection extends GraphObject {

	/** Source task */
	Task _fromTask;
	
	/** Target task */
	Task _toTask;
	
	Polygon _path;
	
	/**
	 * Says how long is the connection line padded from the tasks  boundary.
	 * Thanks to padding connections don't cross each other.
	 * Padding says just the count of skips, not absolute distance. When rendering,
	 * this number is multiplied by some factor.
	 * Negative value means padding is in the top direction, positive value means direction
	 * to bottom.
	 */
	int _padding;
	
	public Connection(Task from, Task to) {
		_fromTask = from;
		_toTask = to;
		_path = new Polygon(new int[4], new int[4], 4);
	}
}
