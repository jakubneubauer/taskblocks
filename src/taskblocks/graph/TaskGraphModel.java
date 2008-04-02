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

public interface TaskGraphModel {
	
	Object[] getTasks();
	Object[] getMans();
	
	Object[] getTaskPredecessors(Object task);
	long getTaskDuration(Object task);
	long getTaskActualDuration(Object task);
	long getTaskStartTime(Object task);
	Object getTaskMan(Object task);
	String getTaskName(Object task);
	String getManName(Object man);
	String getTaskComment(Object task);

	public void updateTask(Object task, Object taskMan, long startTime, long duration, long actualDuration, Object[] precedingTasks);
	public void removeTask(Object task);
	public void removeMan(Object man);
}
