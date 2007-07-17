package taskblocks.app;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;

import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JToolBar;

public class MyToolBar extends JToolBar {

	public JButton add(Action a) {
		JButton b = createActionComponent(a);
    b.putClientProperty("hideActionText", Boolean.FALSE);
		b.setAction(a);
		
		// MacOS user feeling
		b.putClientProperty("JButton.buttonType", "icon");
		
		b.setMargin(new Insets(10,10,10,10));
		b.setBorderPainted(false);
		b.setFocusable(false);
		
		String longD = (String) a.getValue(Action.LONG_DESCRIPTION);
		if (longD != null) {
			b.setToolTipText(longD);
		}
		Icon icon = b.getIcon();
		if(icon != null) {
			BufferedImage bufIm = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2 = (Graphics2D)bufIm.getGraphics();
			icon.paintIcon(this, g2, 0, 0);
			g2.setColor(Color.BLACK);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
			g2.fillRect(0,0,icon.getIconWidth(), icon.getIconHeight());
			ImageIcon pressedIcon = new ImageIcon(bufIm);
			b.setPressedIcon(pressedIcon);
			
			bufIm = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
			g2 = (Graphics2D)bufIm.getGraphics();
			icon.paintIcon(this, g2, 0, 0);
			g2.setColor(Color.WHITE);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.4f));
			g2.fillRect(0,0,icon.getIconWidth(), icon.getIconHeight());
			ImageIcon hoverIcon = new ImageIcon(bufIm);
			b.setRolloverIcon(hoverIcon);
		}
		
		b.setOpaque(false);
		b.setRolloverEnabled(false);
		b.setFont(b.getFont().deriveFont(Font.PLAIN));
		b.setContentAreaFilled(false);

		add(b);
		return b;
	}

}
