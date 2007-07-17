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
			updateMan();
		}
	}

	private void updateMan() {
		throw new IllegalArgumentException("Not yet implemented");
	}

	private void addMan() {
		_graph.getGraphRepresentation().updateModel();
		ManImpl man = new ManImpl();
		man.setName(_cfgPanel.nameTF.getText());
		_model.addMan(man);
		
		_graph.setModel(_model);
		_graph.getGraphRepresentation().setDirty();
		_graph.repaint();
		
		_cfgPanel.nameTF.setSelectionStart(0);
		_cfgPanel.nameTF.setSelectionEnd(_cfgPanel.nameTF.getText().length());
		_cfgPanel.nameTF.requestFocus();
	}
}
