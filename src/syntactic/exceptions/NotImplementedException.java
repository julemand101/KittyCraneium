package syntactic.exceptions;

public class NotImplementedException extends UnsupportedOperationException {
	private static final long serialVersionUID = -3987843717370419712L;
	
	public NotImplementedException() {
		super("This method has not been implemented yet!");
	}
}
