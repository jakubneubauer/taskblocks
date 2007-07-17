package taskblocks.app;

import java.io.File;

import javax.swing.SwingUtilities;

public class TaskBlocks  {

	static final boolean RUNNING_ON_MAC = System.getProperty("os.name").toLowerCase().startsWith("mac os x");
	static final boolean RUNNING_ON_WINDOWS = System.getProperty("os.name").toLowerCase().startsWith("windows");
	
	public TaskBlocks(final String[] args) {

		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("com.apple.mrj.application.growbox.intrudes", "false");
		System.setProperty("com.apple.mrj.application.live-resize", "true");
		
		SwingUtilities.invokeLater(new Runnable(){
			public void run() {
				if(args.length > 0) {
					for(int i = 0; i < args.length; i++) {
						new ProjectFrame().openFile(new File(args[i]));
					}
				} else {
					new ProjectFrame();
				}
			}});
	}

	public static void main(String args[]) {
		new TaskBlocks(args);
	}

}