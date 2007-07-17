package taskblocks.app;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Icon;

public abstract class MyAction extends AbstractAction {
	
	public MyAction(String name) {
		super(name);
		this.putValue(Action.LONG_DESCRIPTION, name);
		this.putValue(Action.SHORT_DESCRIPTION, name);
	}
	
	public MyAction(String name, Icon icon) {
		super(name, icon);
		this.putValue(Action.SHORT_DESCRIPTION, name);
		this.putValue(Action.LONG_DESCRIPTION, name);
	}
	
	public MyAction(String name, Icon icon, String longDescription) {
		super(name, icon);
		this.putValue(Action.SHORT_DESCRIPTION, name);
		this.putValue(Action.LONG_DESCRIPTION, longDescription);
	}

}
