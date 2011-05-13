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

import taskblocks.graph.TaskModel;
import taskblocks.utils.ArrayUtils;

public class TaskModelImpl implements TaskModel {
	
	public TaskImpl[] _tasks;
	public ManImpl[] _mans;
	UndoManager _undoManager;
	
	public TaskModelImpl(TaskImpl[] tasks, ManImpl[] mans) {
		_tasks = tasks;
		_mans = mans;
		_undoManager = new UndoManager();
	}
	
	public UndoManager getUndoManager() {
		return _undoManager;
	}

	public long getTaskEffort(Object task) {
		return ((TaskImpl)task).getEffort();
	}
	
	public long getTaskWorkedTime(Object task) {
		return ((TaskImpl)task).getWorkedTime();
	}

	public Object getTaskMan(Object task) {
		return ((TaskImpl)task).getMan();
	}

	public Object[] getTaskPredecessors(Object task) {
		return ((TaskImpl)task).getPredecessors();
	}

	public long getTaskStartTime(Object task) {
		return ((TaskImpl)task).geStartTime();
	}

	public Object[] getTasks() {
		return _tasks;
	}

	public String getTaskName(Object task) {
		return ((TaskImpl)task).getName();
	}
	
	public String getTaskComment(Object task) {
		return ((TaskImpl)task).getComment();
	}
	
	public static TaskModelImpl createEmptyModel() {
		return new TaskModelImpl(new TaskImpl[0], new ManImpl[0]);
	}

	public void updateTask(Object task, Object taskMan, long startTime, long effort, long workedTime, Object[] precedingTasks) {
		TaskImpl prev = null;
		TaskImpl t = (TaskImpl)task;
		if(t.getMan() != taskMan) {
			if(prev == null) {
				prev = t.clone();
			}
			t.setMan((ManImpl)taskMan);
		}
		if(t.getStartTime() != startTime) {
			if(prev == null) {
				prev = t.clone();
			}
			t.setStartTime(startTime);
		}
		if(t.getEffort() != effort) {
			if(prev == null) {
				prev = t.clone();
			}
			t.setEffort(effort);
		}
		if(t.getWorkedTime() != workedTime) {
			if(prev == null) {
				prev = t.clone();
			}
			t.setWorkedTime( workedTime );
		}
		if(!ArrayUtils.arrayEqualsExceptNull(precedingTasks, t.getPredecessors())) {
			if(prev == null) {
				prev = t.clone();
			}
			TaskImpl[] preds = new TaskImpl[precedingTasks.length];
			for(int i = 0; i < precedingTasks.length; i++) {
				preds[i] = (TaskImpl)precedingTasks[i];
			}
			t.setPredecessors((TaskImpl[])preds);
		}
		if(prev != null) {
			_undoManager.addAction(new UndoActionTaskModify(this, prev, t));
		}
	}

	public String getManName(Object man) {
		return ((ManImpl)man).getName();
	}

	public void addTask(TaskImpl t) {
		_tasks = (TaskImpl[])ArrayUtils.addToArray(_tasks, t);
	}
	
	public void addMan(ManImpl m) {
		_mans = (ManImpl[])ArrayUtils.addToArray(_mans, m);
	}

	public Object[] getMans() {
		return _mans;
	}

	public void removeMan(Object man) {
		_undoManager.addAction(new UndoActionRemoveMan(this, (ManImpl)man));
		removeManImpl(man);
	}

	public void removeManImpl(Object man) {
		_mans = (ManImpl[])ArrayUtils.removeFromArray(_mans, man);
	}

	public void removeTask(Object task) {
		_undoManager.addAction(new UndoActionRemoveTask(this, (TaskImpl)task));
		for(TaskImpl t: _tasks) {
			if(t == task) { continue; }
			if(ArrayUtils.arrayContains(t.getPredecessors(), task)) {
				TaskImpl prev = t.clone();
				t.setPredecessors(ArrayUtils.removeFromArray(t.getPredecessors(), (TaskImpl)task));
				_undoManager.addAction(new UndoActionTaskModify(this, prev, t));
			}
		}
		removeTaskImpl(task);
	}

	public void removeTaskImpl(Object task) {
		_tasks = (TaskImpl[])ArrayUtils.removeFromArray(_tasks, task);
		for(TaskImpl t: _tasks) {
			TaskImpl[] newPred = ArrayUtils.removeFromArray(t.getPredecessors(), t);
			if(!ArrayUtils.arrayEqualsExceptNull(newPred, t.getPredecessors())) {
				TaskImpl prev = t.clone();
				t.setPredecessors(newPred);
			}
		}
	}

	public double getManWorkload(Object man) {
		return ((ManImpl)man).getWorkload();
	}

	public void beginUpdateGroup(String groupName) {
		_undoManager.beginGroup(groupName);
	}

	public void endUpdateGroup() {
		_undoManager.endGroup();
	}
}
