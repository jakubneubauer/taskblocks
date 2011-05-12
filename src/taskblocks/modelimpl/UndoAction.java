package taskblocks.modelimpl;

public interface UndoAction {
	public String getLabel();
	public void undo();
	public void redo();
}
