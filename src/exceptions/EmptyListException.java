package exceptions;

public class EmptyListException extends Exception {
	private static final long serialVersionUID = 625923303494808317L;

	public EmptyListException(String argument) {
		super("List " + argument + " must not be empty!");
	}
}
