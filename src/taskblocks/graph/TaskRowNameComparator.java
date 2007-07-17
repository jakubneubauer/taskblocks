package taskblocks.graph;

import java.util.Comparator;

public class TaskRowNameComparator implements Comparator<TaskRow> {

	public int compare(TaskRow r1, TaskRow r2) {
		if(r1._name == null) {
			if(r2._name == null) {
				return 0;
			} else {
				return -1;
			}
		}
		return r1._name.compareTo(r2._name);
	}

}
