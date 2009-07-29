/*
 * Copyright (C) Jakub Neubauer, 2007
 *
 * This file is part of TaskBlocks
 *
 * TaskBlocks is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package taskblocks.app;

import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
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
	JTextField bugIdTF;
	JSpinner planedSP;
	JSpinner actualSP;
	JComboBox colorLabelCB;
	JTextArea commentTA;
	
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
		JLabel durationL = new JLabel("Planed Duration:");
		planedSP = new JSpinner(new SpinnerNumberModel((int)_task.getDuration(), 1, 365, 1));
		JLabel actualDurL = new JLabel("Actual Duration:");
		actualSP = new JSpinner(new SpinnerNumberModel((int)_task.getActualDuration(), 0, 365, 1));
		JLabel colorL = new JLabel("Color Label:");
		colorLabelCB = new JComboBox(new DefaultComboBoxModel(ColorLabel.COLOR_LABELS));
		colorLabelCB.setRenderer(new ColorLabelRenderer(colorLabelCB.getRenderer()));
		JLabel bugIdL = new JLabel("Bugzilla ID:");
		bugIdL.setForeground(Color.GRAY);
		bugIdTF = new JTextField(15);
		// Comment
		JLabel commentL = new JLabel("Comment:");
		commentL.setForeground(Color.GRAY);
		commentTA = new JTextArea( 5, 20 );
		commentTA.setLineWrap( true );
		JScrollPane scrollPane = new JScrollPane(commentTA); 
		
		
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
		// TODO: Actual Duration. For now disabled
		//gc.gridy++; contentP.add(actualDurL, gc);
		gc.gridy++; contentP.add(manL, gc);
		gc.gridy++; contentP.add(colorL, gc);
		gc.gridy++; contentP.add(bugIdL, gc);
		gc.anchor = GridBagConstraints.NORTHEAST;
		gc.gridy++; contentP.add(commentL, gc);
		gc.anchor = GridBagConstraints.EAST;
		
		// add edit fields
		gc.gridx++; gc.gridy=0;
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.weightx = 1;
		gc.insets.left = 8;
		//
		contentP.add(nameTF, gc);
		gc.gridy++; contentP.add(planedSP, gc);
		// TODO: Actual Duration. For now disabled
		//gc.gridy++; contentP.add(actualSP, gc);
		gc.gridy++; contentP.add(manCB, gc);
		gc.gridy++; contentP.add(colorLabelCB, gc);
		gc.gridy++; contentP.add(bugIdTF, gc);
		gc.fill = GridBagConstraints.BOTH;
		gc.weighty = 1;
		gc.gridy++; contentP.add(scrollPane, gc);
		
		// set component properties
		nameTF.setText(_task.getName());
		manCB.setSelectedItem(_task.getMan());
		bugIdTF.setText(_task.getBugId());
		commentTA.setText( _task.getComment() );
		commentTA.setCaretPosition( 0 );
		if(_task.getColorLabel() != null) {
			colorLabelCB.setSelectedItem(_task.getColorLabel());
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
