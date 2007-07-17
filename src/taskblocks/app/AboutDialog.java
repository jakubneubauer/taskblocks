package taskblocks.app;

import java.awt.Component;
import java.awt.Font;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;

public class AboutDialog {
	public static void showAbout(Component parent) {
		JLabel l = new JLabel(
				"<html><center><h2>Task Blocks</h2>"
				+ "Version " + Version.VERSION + "<br><br>"
				+ "Copyright \u00A9 2007 Jakub Neubauer<br>"
				+"&lt;http://taskblocks.googlecode.com&gt;"
				+"</center><br>"
				);
		l.setHorizontalAlignment(SwingConstants.CENTER);
		l.setFont(l.getFont().deriveFont(Font.PLAIN));
		JOptionPane op = new JOptionPane(l);
		JDialog d = op.createDialog(parent, "About");
		d.setVisible(true);
	}
}
