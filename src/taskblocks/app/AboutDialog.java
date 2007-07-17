package taskblocks.app;

import java.awt.Component;

import javax.swing.JDialog;
import javax.swing.JOptionPane;

public class AboutDialog {
	public static void showAbout(Component parent) {
		JOptionPane op = new JOptionPane("<html><center><h2>TaskBlocks</h2> Version " + Version.VERSION + "<br><br>Copyright 2007 Jakub Neubauer</center>");
		JDialog d = op.createDialog(parent, "About");
		d.setVisible(true);
	}
}
