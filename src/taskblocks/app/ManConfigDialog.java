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

import javax.swing.JFrame;
import javax.swing.JPanel;

import taskblocks.graph.TaskGraphComponent;
import taskblocks.modelimpl.ManImpl;
import taskblocks.modelimpl.TaskModelImpl;

public class ManConfigDialog extends ConfigDialogStub  {
	
	ManImpl _man;
	TaskModelImpl _model;
	ManConfigPanel _cfgPanel;
	TaskGraphComponent _graph;
	
	public ManConfigDialog(JFrame owner, ManImpl man, TaskModelImpl model, TaskGraphComponent graph, boolean isCreating) {
		super(owner, isCreating);
		_man = man;
		_model = model;
		_graph = graph;
		init();
	}

	public static boolean openDialog(JFrame owner, ManImpl man, TaskModelImpl model, TaskGraphComponent graph, boolean isCreating) {
		ManConfigDialog d = new ManConfigDialog(owner, man, model, graph, isCreating);
		d.pack();
		d.setLocationRelativeTo(owner);
		d.setVisible(true);
		return d._applied;
	}

	@Override
	JPanel createMainPanel() {
		
		if(_isCreating) {
			setTitle("New Worker");
			_man = new ManImpl();
		} else {
			setTitle("Worker " + _man.getName());
		}

		_cfgPanel = new ManConfigPanel(_man, _model);
		return _cfgPanel;
	}

	@Override
	void doApply() {
		if(isCreating()) {
			addMan();
		} else {
			fillMan(_man);
		}
	}

	private void fillMan(ManImpl man) {
		man.setName(_cfgPanel.nameTF.getText());
		man.setWorkload(((Number)_cfgPanel._workloadSpin.getValue()).doubleValue()/100.0);
	}

	private void addMan() {
		_graph.getGraphRepresentation().updateModel();
		ManImpl man = new ManImpl();
		_model.addMan(man);
		fillMan(man);
		
		_graph.setModel(_model);
		_graph.getGraphRepresentation().setDirty(); // the model->GUI resetted the dirty flag
		_graph.repaint();
		
		_cfgPanel.nameTF.setSelectionStart(0);
		_cfgPanel.nameTF.setSelectionEnd(_cfgPanel.nameTF.getText().length());
		_cfgPanel.nameTF.requestFocus();
	}
}
