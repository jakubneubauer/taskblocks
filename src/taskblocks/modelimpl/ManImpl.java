package taskblocks.modelimpl;

public class ManImpl {
	
	private String _name;
	
	/** Used only when saving */
	public String _id;
	
	public String getName() {
		return _name;
	}
	
	public void setName(String name) {
		_name = name;
	}
	
	public String toString() {
		return _name;
	}
}
