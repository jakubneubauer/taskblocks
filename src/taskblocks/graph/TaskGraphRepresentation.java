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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.swing.event.ChangeListener;

import taskblocks.ArrayUtils;

/**
 * helper class used to build the data structure from task graph model.
 */
public class TaskGraphRepresentation {
	
	TaskRow[] _rows;
	
	Task[] _tasks;
	
	Connection[] _connections;
	
	TaskGraphModel _model;
	
	/** Used internally when shifting tasks */
	boolean _somethingMoved;
	
	private ChangeListener _graphChangeListener;
	
	/** Used to recognize when to recount bounds in {@link TaskGraphComponent#paint(java.awt.Graphics)}. */
	private boolean _paintDirty;
	
	/** Used to recognize if the project has been changed since loaded from file */
	private boolean _saveDirty;
	
	TaskGraphRepresentation(TaskGraphModel model) {
		_model = model;
	}

	public void clearSaveDirtyFlag() {
		_saveDirty = false;
		if(_graphChangeListener != null) {
			_graphChangeListener.stateChanged(null);
		}
	}
	
	public boolean isSaveDirty() {
		return _saveDirty;
	}
	
	/**
	 * Builds all the data structures according to the model
	 * and recounts the starting times, so the tasks don't cross each other
	 */
	void buildFromModel() {
		
		ChangeListener oldChangeList = _graphChangeListener;
		try {
			_graphChangeListener = null;

		
			Object[] taskObjs = _model.getTasks();
			Task[] tasks = new Task[taskObjs.length];
			List<TaskRow> rows = new ArrayList<TaskRow>();
			
			// build list of tasks and rows.
			int i = 0;
			for(Object taskObj: taskObjs) {
				Task t = new Task(taskObj, this);
				tasks[i] = t;
				
				Object manObj = _model.getTaskMan(taskObj);
				TaskRow row = findRowForManObj(manObj, rows);
				if(row == null) {
					row = new TaskRow(manObj);
					row._name = _model.getManName(manObj);
					rows.add(row);
					row._index = rows.size()-1;
				}
				// task -> row mapping
				t._row = row;
				t.setDuration(_model.getTaskDuration(t._userObject));
				t.setStartTime(_model.getTaskStartTime(t._userObject));
				
				// we must initialize these arrays, so we don't care about nulls later
				t._incommingConnections = new Connection[0];
				t._outgoingConnections = new Connection[0];
				
				i++;
			}
			
			// some rows doesn't have tasks, so create them
			for(Object manObj: _model.getMans()) {
				TaskRow row = findRowForManObj(manObj, rows);
				if(row == null) {
					row = new TaskRow(manObj);
					row._name = _model.getManName(manObj);
					rows.add(row);
					row._index = rows.size()-1;
				}
			}
			
			// row -> tasks mapping
			for(TaskRow row: rows) {
				List<Task> rowTasks = new ArrayList<Task>();
				for(Task t: tasks) {
					if(t.getRow() == row) {
						rowTasks.add(t);
					}
				}
				row._tasks = rowTasks.toArray(new Task[rowTasks.size()]);
			}
			
			// build connections array
			List<Connection> connections = new ArrayList<Connection>();
			for(Task t: tasks) {
				Object[] predecessorsUserObjects = _model.getTaskPredecessors(t._userObject);
				if(predecessorsUserObjects != null && predecessorsUserObjects.length > 0) {
					for(Object predecessorUserObject: predecessorsUserObjects) {
						Task predTask = findTaskForUserObject(predecessorUserObject, tasks);
						Connection con = new Connection(predTask, t);
						predTask.addOutgoingConnection(con);
						t.addIncommingConnection(con);
						connections.add(con);
					}
				}
			}
			
			synchronized(this) {
				_tasks = tasks;
				_rows = rows.toArray(new TaskRow[rows.size()]);
				Arrays.sort(_rows, new TaskRowNameComparator());
				// repair row indexes
				for(i = 0; i < _rows.length; i++) {
					_rows[i]._index = i;
				}
				_connections = connections.toArray(new Connection[connections.size()]);
				recountStartingTimes();
				_paintDirty = true;
				_saveDirty = false;
			}
		} finally {
			_graphChangeListener = oldChangeList;
			if(_graphChangeListener != null) {
				_graphChangeListener.stateChanged(null);
			}
		}
		
	}
	
	void changeTaskRow(Task t, TaskRow newRow) {
		TaskRow oldRow = t._row;
		if(oldRow == newRow) {
			return;
		}
		
		t._row = newRow;
		oldRow._tasks = (Task[])ArrayUtils.removeFromArray(oldRow._tasks, t);
		newRow._tasks = (Task[])ArrayUtils.addToArray(newRow._tasks, t);
		
		setDirty();
	}
	
	/**
	 * Must be called synchronized on this object
	 */
	void clearPaintDirtyFlag() {
		_paintDirty = false;
	}
	
	ChangeListener getGraphChangeListener() {
		return _graphChangeListener;
	}

	boolean isPaintDirty() {
		return _paintDirty;
	}
	
	private List<Task> getRootTasks() {
		// build a list of tasks that don't have predecessor.
		List<Task> rootTasks = new ArrayList<Task>();
		for(Task t: _tasks) {
			if(t._incommingConnections.length == 0) {
				rootTasks.add(t);
			}
		}
		return rootTasks;
	}
	
	/**
	 * Shifts tasks as it is needed, so they don't cross each other and the dependencies are OK.
	 */
	synchronized void recountStartingTimes() {
		
		TaskStartTimeComarator taskStartTimeComparator = new TaskStartTimeComarator();
		// sort tasks according to their starting time
		Arrays.sort(_tasks, taskStartTimeComparator);
		
		
		for(TaskRow row: _rows) {
			// first, we must sort them according to their starting time, so they will appear
			// in the same order as before.
			Arrays.sort(row._tasks, taskStartTimeComparator);
		}

		do {
			_somethingMoved = false;
			// build a list of tasks that don't have predecessor.
			List<Task> rootTasks = getRootTasks();
			
			// first, shift tasks according their predecessors.
			for(Task t: rootTasks) {
				shiftSubsequentTasksAfterMe(t);
			}
			
			// secondly, shift tasks, so they will not cross each other in row.
			for(TaskRow row: _rows) {
				// we must sort them according to their starting time again, the previous
				// shifting could change their order.
				Arrays.sort(row._tasks, taskStartTimeComparator);
				
				for(int i = 0; i < row._tasks.length-1; i++) {
					Task t1 = row._tasks[i];
					Task t2 = row._tasks[i+1];
					long t1End = t1.getFinishTime();
					if(t2.getStartTime() < t1End) {
						_somethingMoved = true;
						// we must shift the t2 task. After shifting, check it's subsequent tasks.
						t2.setStartTime(t1End);
						shiftSubsequentTasksAfterMe(t2);
					}
				}
			}
		} while(_somethingMoved);
	}
	
	/**
	 * Sets both, the "paint" and the "save" dirty flags
	 */
	public synchronized void setDirty() {
		_paintDirty = true;
		_saveDirty = true;
		if(_graphChangeListener != null) {
			_graphChangeListener.stateChanged(null);
		}
	}
	
	void setGraphChangeListener(ChangeListener cl) {
		_graphChangeListener = cl;
	}
	
	/**
	 * Sets just the "paint" dirty flag.
	 * Used only when the tasks bounds should be recounted but the model itself didn't change
	 * (for example just scale changed)
	 */
	synchronized void setPaintDirty() {
		_paintDirty = true;
	}
	
	public synchronized void shrinkTasks() {
		TaskStartTimeComarator taskStartTimeComparator = new TaskStartTimeComarator();
		// find the lowest time
		long firstTime = Long.MAX_VALUE;
		for(Task t: _tasks) {
			firstTime = Math.min(t.getStartTime(), firstTime);
		}
		for(TaskRow row: _rows) {
			if(row._tasks.length > 0) {
				// first, we must sort them according to their starting time, so they will appear
				// in the same order as before.
				Arrays.sort(row._tasks, taskStartTimeComparator);
				row._tasks[0].setStartTime(firstTime);
				for(int i = 1; i < row._tasks.length; i++) {
					row._tasks[i].setStartTime(row._tasks[i-1].getFinishTime());
				}
			}
		}
		recountStartingTimes();
	}
	
	public void updateModel() {
		for(Task t: _tasks) {
			// build array of preceeding tasks
			Object[] preceedingTasksUserObjs = new Object[t._incommingConnections.length];
			for(int i = 0; i < t._incommingConnections.length; i++) {
				preceedingTasksUserObjs[i] = t._incommingConnections[i]._fromTask._userObject;
			}
			_model.updateTask(t._userObject, t._row._userManObject, t.getStartTime(), t.getDuration(), preceedingTasksUserObjs);
		}
	}
	
	/**
	 * Used when building data structures. Finds row for given user man object
	 * 
	 * @param manObj
	 * @param rows
	 * @return
	 */
	private TaskRow findRowForManObj(Object manObj, Collection<TaskRow> rows) {
		for(TaskRow row:rows) {
			if(row._userManObject == manObj) {
				return row;
			}
		}
		return null;
	}

	private Task findTaskForUserObject(Object taskUserobj, Task[] tasks) {
		for(Task t: tasks) {
			if(t._userObject == taskUserobj) {
				return t;
			}
		}
		return null;
	}
	
	/** Recursively checks all sub-tasks of given task and if they start before this task finishes,
	 * shifts them.
	 * 
	 * @param t Task to be checked
	 */
	private void shiftSubsequentTasksAfterMe(Task t) {
		for(Connection c: t._outgoingConnections) {
			Task targetTask = c._toTask;
			if(targetTask.getStartTime() < t.getFinishTime()) {
				targetTask.setStartTime(t.getFinishTime());
				_somethingMoved = true;
			}
		}
		// recursion breath-first
		for(Connection c: t._outgoingConnections) {
			shiftSubsequentTasksAfterMe(c._toTask);
		}
	}

	/**
	 * 
	 * @param t1
	 * @param t2
	 * 
	 * @throws Exception if connection cannot be created because of cycle.
	 */
	void createConnection(Task t1, Task t2) throws Exception {
		if(t1 == t2 || t1 == null || t2 == null) {
			throw new IllegalArgumentException("Wrong connection");
		}
		
		Connection c = new Connection(t1, t2);
		t1._outgoingConnections = (Connection[])ArrayUtils.addToArray(t1._outgoingConnections, c);
		t2._incommingConnections = (Connection[])ArrayUtils.addToArray(t2._incommingConnections, c);
		_connections = (Connection[])ArrayUtils.addToArray(_connections, c);
		
		if(checkForCycles()) {
			// UNDO and throw exception
			t1._outgoingConnections = (Connection[])ArrayUtils.removeFromArray(t1._outgoingConnections, c);
			t2._incommingConnections = (Connection[])ArrayUtils.removeFromArray(t2._incommingConnections, c);
			_connections = (Connection[])ArrayUtils.removeFromArray(_connections, c);
			throw new Exception("Loops are not allowed in task dependencies");
		}
		
		setDirty();
	}
	
	/**
	 * Returns true if there are some cycles in the task dependency graph
	 * 
	 * @return
	 */
	private boolean checkForCycles() {
		if(_tasks.length == 0) {
			return false;
		}
		// initialize flags
		for(Task t: _tasks) {
			t._flag = false;
		}
		for(Task t: _tasks) {
			if(checkForCyclesRec(t)) {
				return true;
			}
		}
		return false;
	}

	private boolean checkForCyclesRec(Task t) {
		if(t._flag) {
			return true;
		}
		t._flag = true;
		
		for(Connection outC: t._outgoingConnections) {
			if(checkForCyclesRec(outC._toTask)) {
				return true;
			}
		}
		
		t._flag = false;
		return false;
	}

	public int getManCount() {
		return _rows.length;
	}

	public void removeConnection(Connection c) {
		c._fromTask._outgoingConnections = (Connection[])ArrayUtils.removeFromArray(c._fromTask._outgoingConnections, c);
		c._toTask._incommingConnections = (Connection[])ArrayUtils.removeFromArray(c._toTask._incommingConnections, c);
		_connections = (Connection[])ArrayUtils.removeFromArray(_connections, c);
		setDirty();
	}

	public void removeTask(Task t) {
		for(Connection c: t._incommingConnections) {
			removeConnection(c);
		}
		for(Connection c: t._outgoingConnections) {
			removeConnection(c);
		}
		_tasks = (Task[])ArrayUtils.removeFromArray(_tasks, t);
		t._row._tasks = (Task[])ArrayUtils.removeFromArray(t._row._tasks, t);
		_model.removeTask(t._userObject);
		setDirty();
	}

	public void removeRow(TaskRow row) {
		for(Task t: row._tasks) {
			removeTask(t);
		}
		_rows = (TaskRow[])ArrayUtils.removeFromArray(_rows, row);
		_model.removeMan(row._userManObject);
		setDirty();
	}
}
