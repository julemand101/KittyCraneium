package ast.nodes.leafs;

import syntactic.Token;
import syntactic.exceptions.CompileException;
import ast.IASTLeaf;
import ast.IASTVisitor;

public class CoordinatePosition implements IASTLeaf {
	private Token token;
	
	public CoordinatePosition(Token token) {
		this.token = token;
	}	

	@Override
	public Token getToken() {
		return this.token;
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}
	public String toString() {
		return this.token.getSpelling();
	}	
}
