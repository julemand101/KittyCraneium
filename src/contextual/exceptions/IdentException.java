package contextual.exceptions;

import syntactic.exceptions.ErrorType;

public class IdentException extends Exception {
	/*
	 * This error is thrown by IdentificationTable.java when it cannot determine the
	 * position of the error in the source. Where possible, IdentificationTableExceptions 
	 * should be catched and thrown as a SyntaxException with an error position. This
	 * is done in ContextualAnalyzer.java 
	 */
	private static final long serialVersionUID = 7528567859425341633L;
	
	private ErrorType errorType;
	
	public IdentException(ErrorType errorType, String shortErrorDescription) {
		super(shortErrorDescription);
		this.errorType  = errorType;
		
		//printStackTrace();
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}
	
}	
