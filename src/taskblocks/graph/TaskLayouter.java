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

import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.ArrayList;
import java.util.List;

/**
 * This class is used to count real positions of tasks on the component
 * 
 * @author jakub
 * 
 */
class TaskLayouter {

	/**
	 * Recounts tasks and rows bounds
	 */
	static void recountBounds(int _graphTop, int _rowHeight,
			TaskGraphRepresentation _builder, TaskGraphComponent _graph, Graphics2D g2) {

		int rowIndex = 0;
		FontMetrics fm = g2.getFontMetrics();
		int maxRowWidth = fm.stringWidth("Worker");
		for (TaskRow row : _builder._rows) {
			maxRowWidth = Math.max(maxRowWidth, fm.stringWidth(row.getLabel()));
		}
		_graph._headerWidth = maxRowWidth + 20;
		_graph.recountBounds();

		// count rows and tasks boundaries
		int cummulatedRowAdd = _graph._scrollTop;
		for (TaskRow row : _builder._rows) {
			int rowTop = (int) (_graphTop + rowIndex * _rowHeight + cummulatedRowAdd);

			// count connections paddings and modify cummulatedRowAdd and rowTop
			// according to them.
			int maxUpPadding = 0;
			int maxDownPadding = 0;
			List<Connection> processedConnections = new ArrayList<Connection>();
			for (Task t : row._tasks) {
				for (int i = 0; i < t._outgoingConnections.length; i++) {
					Connection c = t._outgoingConnections[i];
					// recognize, if connection is going up or down
					boolean goingDown = row._index <= c._toTask.getRow()._index;
					int connPadding = 1;

					// simple hack - if connection is in the same row, padding
					// should be
					// greater that 1, so the arrow will look nicer.
					if (c._fromTask.getRow() == c._toTask.getRow()) {
						connPadding = 2;
					}
					// now check if the connection crosses something in the same
					// direction.
					// if yes, increase connPadding until nothings crosses.
					// Note, that we must check only already processed
					// connections.
					LOOP:
					while (true) {
						for (Connection c2 : processedConnections) {
							boolean c2GoingDown = row._index <= c2._toTask
									.getRow()._index;
							if (goingDown == c2GoingDown
									&& connPadding == c2._padding
									&& crosses(c, c2)) {
								connPadding++;
								// padding increased, lets check again.
								continue LOOP;
							}
						}
						// all checks OK => stop
						break LOOP;
					}
					c._padding = connPadding;
					processedConnections.add(c);
					if (goingDown) {
						maxDownPadding = Math.max(maxDownPadding, connPadding);
					} else {
						maxUpPadding = Math.max(maxUpPadding, connPadding);
					}
				}
			}
			rowTop += maxUpPadding * TaskGraphComponent.CONN_PADDING_FACTOR;
			cummulatedRowAdd += (maxDownPadding + 1) * TaskGraphComponent.CONN_PADDING_FACTOR
					+ maxUpPadding * TaskGraphComponent.CONN_PADDING_FACTOR;
			row._topPosition = rowTop;
			row._topPadding = maxUpPadding;
			row._bottomPadding = maxDownPadding+1;

			double top = rowTop + TaskGraphComponent.CONN_PADDING_FACTOR;
			double height = _rowHeight
			- TaskGraphComponent.CONN_PADDING_FACTOR;
			for (Task t : row._tasks) {
				double left = _graph.timeToX(t.getStartTime());
				double widthPlaned = t.getRealDuration() * _graph._dayWidth;
				t._bounds.setBounds((int) left+2, (int) top, (int) widthPlaned-4,
						(int) height);
			}
			
			// if this is row of cursor, recount also cursor.
			if(_graph._cursorTempTask != null && _graph._cursorTempTask._row == row) {
				double left = _graph.timeToX(_graph._cursorTempTask.getStartTime());
				double widthPlaned = _graph._cursorTempTask.getRealDuration() * _graph._dayWidth;
				_graph._cursorTempTask._bounds.setBounds((int) left+2, (int) top, (int) widthPlaned-4,
						(int) height);
			}
			
			
			rowIndex++;
			
		} // for all rows
		
		for(Connection c: _builder._connections) {
			recountConnectionBounds(c, _graph);
		}

		_builder.clearPaintDirtyFlag();
		
	}

	/**
	 * Checks if the given connections crosses each other (without padding)
	 * 
	 * @param c1
	 * @param c2
	 * @return
	 */
	static boolean crosses(Connection c1, Connection c2) {
		long c1Left = c1._fromTask.getFinishTime();
		long c1Right = c1._toTask.getStartTime();
		long c2Left = c2._fromTask.getFinishTime();
		long c2Right = c2._toTask.getStartTime();

		// c1.left inside c2
		if (c1Left >= c2Left && c1Left <= c2Right) {
			return true;
		}
		// c1.right inside c2
		if (c1Right >= c2Left && c1Right <= c2Right) {
			return true;
		}

		// c2.left inside c1
		if (c2Left >= c1Left && c2Left <= c1Right) {
			return true;
		}
		// c2.right inside c1
		if (c2Right >= c1Left && c2Right <= c1Right) {
			return true;
		}

		return false;
	}
	
	static void recountConnectionBounds(Connection c, TaskGraphComponent _graph) {
		long fromTime = c._fromTask.getFinishTime();
		long toTime = c._toTask.getStartTime();
		int x1 = _graph.timeToX(fromTime);
		int x2 = _graph.timeToX(toTime);
		int y1, y2;
		boolean goingDown = c._fromTask.getRow()._index <= c._toTask.getRow()._index;
		if(goingDown) {
			y1 = c._fromTask.getRow()._topPosition + TaskGraphComponent.ROW_HEIGHT;
		} else {
			y1 = c._fromTask.getRow()._topPosition + TaskGraphComponent.CONN_PADDING_FACTOR;
		}
		boolean commingFromUp = c._fromTask.getRow()._index < c._toTask.getRow()._index;
		if(commingFromUp) {
			y2 = c._toTask.getRow()._topPosition + TaskGraphComponent.CONN_PADDING_FACTOR;
		} else {
			y2 = c._toTask.getRow()._topPosition + TaskGraphComponent.ROW_HEIGHT;
		}

		
		int mediumY;
		if(y2 >= y1) {
			mediumY = y1 + c._padding*TaskGraphComponent.CONN_PADDING_FACTOR;
		} else {
			mediumY = y1 - c._padding*TaskGraphComponent.CONN_PADDING_FACTOR;
		}
		c._path.xpoints[0] = x1;
		c._path.ypoints[0] = y1;
		c._path.xpoints[1] = x1;
		c._path.ypoints[1] = mediumY;
		c._path.xpoints[2] = x2;
		c._path.ypoints[2] = mediumY;
		c._path.xpoints[3] = x2;
		c._path.ypoints[3] = y2;
		
	}

}
