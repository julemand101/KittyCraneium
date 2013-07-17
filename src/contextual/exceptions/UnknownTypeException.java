package contextual.exceptions;

public class UnknownTypeException extends Exception {
	private static final long serialVersionUID = 8990778344422028620L;

	public UnknownTypeException(String typeName) {
		super("The type is unknown: " + typeName);
	}
}
