package taskblocks.graph;

public interface TaskGraphModel {
	
	Object[] getTasks();
	Object[] getMans();
	
	Object[] getTaskPredecessors(Object task);
	long getTaskDuration(Object task);
	long getTaskStartTime(Object task);
	Object getTaskMan(Object task);
	String getTaskName(Object task);
	String getManName(Object man);

	public void updateTask(Object task, Object taskMan, long startTime, long duration, Object[] precedingTasks);
	public void removeTask(Object task);
	public void removeMan(Object man);
}
