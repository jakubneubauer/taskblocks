package taskblocks.modelimpl;

public interface UndoAction {
	public String getUndoLabel();
	public String getRedoLabel();
	public void undo();
	public void redo();
}
