package taskblocks.modelimpl;

public interface UndoAction {
	public String getLabel();
	public void redo();
	public void undo();
}
