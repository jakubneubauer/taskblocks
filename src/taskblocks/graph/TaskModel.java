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

public interface TaskModel {
	/** Returns all tasks */
	Object[] getTasks();
	/** Returns all mans */ 
	Object[] getMans();
	/** Returns tasks preceding the given task */
	Object[] getTaskPredecessors(Object task);
	/** Returns duration of the task (in days) */
	long getTaskDuration(Object task);
	/** Returns actual duration (days worked) */
	long getTaskActualDuration(Object task);
	/** Returns starting time of the task (in days since Epoch (1.1.1970) */
	long getTaskStartTime(Object task);
	/** Returns man working on the task */
	Object getTaskMan(Object task);
	/** Returns name of the task */
	String getTaskName(Object task);
	/** Returns name of the man */
	String getManName(Object man);
	/** returns comment for the task */
	String getTaskComment(Object task);
	/** Should update specified task with specified info */
	public void updateTask(Object task, Object taskMan, long startTime, long duration, long actualDuration, Object[] precedingTasks);
	/** Deletes task from the model */
	public void removeTask(Object task);
	/** removes man from the model */
	public void removeMan(Object man);
}
