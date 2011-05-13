package taskblocks.modelimpl;

import junit.framework.TestCase;

public class UndoManagerTest extends TestCase {
	
	StringBuilder log = new StringBuilder();
	UndoManager um = new UndoManager();
	
	class UA implements UndoAction {
		String name;
		UA(String name) {this.name = name;}
		@Override public String getUndoLabel() {return name;}
		@Override public String getRedoLabel() {return name;}
		@Override public void undo() {log.append("u" + name);}
		@Override public void redo() {log.append("r" + name);}
		@Override public String toString() { return name; }
		
	}
	
	public void test1() {
		um.addAction(new UA("1"));
		um.addAction(new UA("2"));
		um.undo();
		um.addAction(new UA("3"));
		assertEquals("u2", log.toString());
		assertFalse(um.canRedo());
		assertTrue(um.canUndo());
		um.undo();
		assertEquals("u2u3", log.toString());
		assertTrue(um.canUndo());
		um.undo();
		assertEquals("u2u3u1", log.toString());
		assertFalse(um.canUndo());
		assertTrue(um.canRedo());
		um.redo();
		assertEquals("u2u3u1r1", log.toString());
		assertTrue(um.canUndo());
		assertTrue(um.canRedo());
		um.redo();
		assertEquals("u2u3u1r1r3", log.toString());
		assertTrue(um.canUndo());
		assertFalse(um.canRedo());
		
	}

}
