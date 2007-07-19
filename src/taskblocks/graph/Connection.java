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
