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

public class TaskModelImpl implements TaskModel{
	
	public TaskImpl[] _tasks;
	public ManImpl[] _mans;
	
	public TaskModelImpl(TaskImpl[] tasks, ManImpl[] mans) {
		_tasks = tasks;
		_mans = mans;
	}

	public long getTaskDuration(Object task) {
		return ((TaskImpl)task).getDuration();
	}
	
	public long getTaskActualDuration(Object task) {
		return ((TaskImpl)task).getActualDuration();
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

	public void updateTask(Object task, Object taskMan, long startTime, long duration, long actualDuration, Object[] precedingTasks) {
		TaskImpl t = (TaskImpl)task;
		t.setMan((ManImpl)taskMan);
		t.setStartTime(startTime);
		t.setDuration(duration);
		t.setActualDuration( actualDuration );
		TaskImpl[] preds = new TaskImpl[precedingTasks.length];
		for(int i = 0; i < precedingTasks.length; i++) {
			preds[i] = (TaskImpl)precedingTasks[i];
		}
		t.setPredecessors((TaskImpl[])preds);
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
		_mans = (ManImpl[])ArrayUtils.removeFromArray(_mans, man);
	}

	public void removeTask(Object task) {
		_tasks = (TaskImpl[])ArrayUtils.removeFromArray(_tasks, task);
	}

	@Override
	public double getManWorkload(Object man) {
		return ((ManImpl)man).getWorkload();
	}
}
