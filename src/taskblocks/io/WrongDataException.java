package taskblocks.io;

/**
 * Exception thrown when loading data and the data file is wrong
 * 
 * @author jakub
 *
 */
public class WrongDataException extends Exception {
	
	public WrongDataException(String msg) {
		super(msg);
	}

	public WrongDataException(String msg, Throwable t) {
		super(msg, t);
	}
}
