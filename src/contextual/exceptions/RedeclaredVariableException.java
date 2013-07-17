package contextual.exceptions;

public class RedeclaredVariableException extends Exception {
	private static final long serialVersionUID = 2423595400890942831L;

	public RedeclaredVariableException(String varName) {
		super("Variable already exists in scope: " + varName);
	}
}
