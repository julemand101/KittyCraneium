package contextual.exceptions;

public class UndeclaredVariableException extends Exception {
	private static final long serialVersionUID = -1548432712075079086L;

	public UndeclaredVariableException(String varName) {
		super("Illegal use of variable. Variable must be declared before use: " + varName);
	}
}
