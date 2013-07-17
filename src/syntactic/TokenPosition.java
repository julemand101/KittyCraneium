package syntactic;

public class TokenPosition {
	private int line;
	private int column;
	
	public TokenPosition(int line, int column) {
		this.line = line;
		this.column = column;
	}
	
	public int getLine() {
		return this.line;
	}
	
	public int getColumn() {
		return this.column;
	}
	
	@Override
	public String toString() {
		return "line: "+line+" col: "+column;
	}
}
