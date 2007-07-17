package taskblocks.app;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.SpinnerNumberModel;

import taskblocks.modelimpl.ColorLabel;
import taskblocks.modelimpl.TaskImpl;
import taskblocks.modelimpl.TaskModelImpl;

public class TaskConfigPanel extends JPanel {

	private TaskImpl _task;
	private TaskModelImpl _model;
	
	JComboBox manCB;
	JTextField nameTF;
	JSpinner durationSP;
	JComboBox _colorLabelCB;
	
	public TaskConfigPanel(TaskImpl task, TaskModelImpl model) {
		_task = task;
		_model = model;
		buildGui();
	}
	
	private void buildGui() {
		// create components
		JPanel contentP = this;
		JLabel nameL = new JLabel("Task name:");
		nameTF = new JTextField(15);
		JLabel manL = new JLabel("Worker:");
		manCB = new JComboBox(new DefaultComboBoxModel(_model._mans));
		JLabel durationL = new JLabel("Duration:");
		durationSP = new JSpinner(new SpinnerNumberModel((int)_task.getDuration(), 1, 365, 1));
		JLabel colorL = new JLabel("Color Label:");
		_colorLabelCB = new JComboBox(new DefaultComboBoxModel(ColorLabel.COLOR_LABELS));
		_colorLabelCB.setRenderer(new ColorLabelRenderer(_colorLabelCB.getRenderer()));
		
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
		gc.gridy++; contentP.add(durationL, gc);
		gc.gridy++; contentP.add(manL, gc);
		gc.gridy++; contentP.add(colorL, gc);
		
		// add edit fields
		gc.gridx++; gc.gridy=0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;
		gc.insets.left = 8;
		//
		contentP.add(nameTF, gc);
		gc.gridy++; contentP.add(durationSP, gc);		
		gc.gridy++; contentP.add(manCB, gc);
		gc.gridy++; contentP.add(_colorLabelCB, gc);
		
		// set component properties
		nameTF.setText(_task.getName());
		manCB.setSelectedItem(_task.getMan());
		if(_task.getColorLabel() != null) {
			_colorLabelCB.setSelectedItem(_task.getColorLabel());
		}
	}
	
	public static class ColorLabelRenderer implements ListCellRenderer {
		ListCellRenderer _orig;
		public ColorLabelRenderer(ListCellRenderer orig) {
			_orig = orig;
		}
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component c = _orig.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			
			if(c instanceof JLabel) {
				((JLabel)c).setIcon(((ColorLabel)value)._icon);
			}
			return c;
		}
	}

}
