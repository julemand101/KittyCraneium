package contextual.exceptions;

public class ParameterCountMismatchException extends Exception {
	private static final long serialVersionUID = -4858989887720236147L;

	public ParameterCountMismatchException(String methodName) {
		super("Invalid number of parameters supplied for method: " + methodName);
	}
}
