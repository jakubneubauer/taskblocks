package taskblocks.app;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import taskblocks.modelimpl.ManImpl;
import taskblocks.modelimpl.TaskModelImpl;


public class ManConfigPanel extends JPanel {

	JTextField nameTF;
	ManImpl _man;
	TaskModelImpl _model;

	public ManConfigPanel(ManImpl man, TaskModelImpl model) {
		_man = man;
		_model = model;
		buildGui();
	}
	
	private void buildGui() {
		// create components
		JPanel contentP = this;
		JLabel nameL = new JLabel("Worker name:");
		nameTF = new JTextField(15);
		
		//layout components
		contentP.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		// add labels
		gc.gridx = 0; gc.gridy = 0;
		gc.fill = GridBagConstraints.NONE;
		gc.insets.bottom = 5;
		gc.anchor = GridBagConstraints.EAST;
		//
		contentP.add(nameL, gc);
		
		// add edit fields
		gc.gridx++; gc.gridy=0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;
		gc.insets.left = 8;
		//
		contentP.add(nameTF, gc);
		
		// set component properties
		nameTF.setText(_man.getName());
	}


}
