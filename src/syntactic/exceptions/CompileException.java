package syntactic.exceptions;

import syntactic.TokenPosition;

public class CompileException extends Exception {
	private static final long serialVersionUID = 7528567859425341633L;
	
	private ErrorType errorType;
	private TokenPosition position;
	
	public CompileException(ErrorType errorType, TokenPosition position, String shortErrorDescription) {
		super(shortErrorDescription);
		
		this.errorType  = errorType;
		this.position = position;
		
		//printStackTrace();
	}
	
	public ErrorType getErrorType() {
		return errorType;
	}
	
	public int getErrorLine() {
		if (position == null)
			return -1;			
		else
			return position.getLine();
	}
	
	public int getErrorColumn() {
		if (position == null)
			return -1;			
		else
			return position.getColumn();
	}
	
//IMPORTANT!!! To be used in exception handler together til main
//**************************************************************	
//	private String format(int line, int column, String source, String message){
//		StringBuilder errorMessage = new StringBuilder();
//		
//		errorMessage.append("Line: " + line + "\t Column: " + column + "\t" + message + "\n");
//		errorMessage.append(getErrorLine(line, source));
//		errorMessage.append("\n");
//		for(int i = 0; i < column; i++){
//			errorMessage.append(" ");
//		}
//		errorMessage.append("^");
//		
//		return errorMessage.toString();
//	}
//	
//	public String getErrorLine(int line, String source){
//		
//		int lineCounter = 0;
//		int charPos = 0;
//		StringBuilder errorLine = new StringBuilder();
//		
//		/*
//		 * We find the position of the line causing the error. 
//		 */
//		while(lineCounter <= line) {
//			while(source.charAt(charPos) != '\n')
//				charPos++;
//			lineCounter++;
//		}
//		
//		/*
//		 * We build a string with the line causing the error.
//		 */
//		while(source.charAt(charPos) != '\n' && source.charAt(charPos) != '\000'){
//			errorLine.append(source.charAt(charPos));
//			charPos++;
//		}
//		
//		return errorLine.toString();
//	}
	
}
