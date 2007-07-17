package taskblocks.app;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRootPane;
import javax.swing.KeyStroke;
import javax.swing.Box.Filler;

public abstract class ConfigDialogStub extends JDialog  {
	
	boolean _applied;
	
	JButton okB;
	JButton cancelB;
	boolean _isCreating;
	JCheckBox _stayOpenCB;

	private JPanel _mainPanel;

	private AbstractAction _okAction;
	private AbstractAction _cancelAction;
	
	public ConfigDialogStub(JFrame owner, boolean isCreating) {
		super(owner, true);
		_isCreating = isCreating;
	}
	
	public void init() {
		createActions();
		buildGui();
	}
	
	abstract JPanel createMainPanel();

	private void buildGui() {
		
		// create components
		JPanel mainP = new JPanel(new BorderLayout(0, 20));
		Box butP = Box.createHorizontalBox();
		okB = new JButton(_okAction);
		cancelB = new JButton(_cancelAction);
		_mainPanel = createMainPanel();
		
		//layout components
		if(_isCreating) {
			_stayOpenCB = new JCheckBox("Don't close this dialog");
			_stayOpenCB.setSelected(false);
			butP.add(_stayOpenCB);
			butP.add(new Filler(new Dimension(0,0), new Dimension(30,0), 
					new Dimension(Short.MAX_VALUE, 0))
			);
		} else {
			butP.add(Box.createHorizontalGlue());
		}
		butP.add(okB);
		butP.add(Box.createHorizontalStrut(10));
		butP.add(cancelB);
		mainP.add(butP, BorderLayout.SOUTH);
		getContentPane().add(mainP);
		mainP.add(_mainPanel, BorderLayout.CENTER);

		mainP.setBorder(BorderFactory.createEmptyBorder(20,20,20,20));

		this.addWindowListener(new WindowAdapter(){
		    public void windowClosing(WindowEvent e) {dispose();}
		});
		setDefaultActions(getRootPane());
	}
	
	abstract void doApply();
	
	private void apply() {
		doApply();
		_applied = true;
	}
	
	/**
	 * Sets <code>CloseWindow</code> action for key Escape and <code>SaveWindow</code> action for
	 * key Enter.
	 *
	 * @param rootPane
	 */
	private void setDefaultActions(JRootPane rootPane) {
		KeyStroke strokeEsc = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0);
		KeyStroke strokeEnter = KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0);

		InputMap inputMap = rootPane.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		inputMap.put(strokeEsc, "ESCAPE");
		inputMap.put(strokeEnter, "ENTER");
		rootPane.getActionMap().put("ESCAPE", _cancelAction);
		rootPane.getActionMap().put("ENTER", _okAction);
	}
	
	private void createActions() {
		_cancelAction = new AbstractAction("Cancel"){
			public void actionPerformed(ActionEvent e) {
				dispose();
			}};
			
		_okAction = new AbstractAction(_isCreating ? "Create" : "OK"){
			public void actionPerformed(ActionEvent e) {
				apply();
				if(_isCreating) {
					if(!_stayOpenCB.isSelected()) {
						dispose();
					}
				} else {
					dispose();
				}
			}
		};
		
	}

	public boolean isCreating() {
		return _isCreating;
	}
}
