package ast.nodes.leafs;

import syntactic.Token;
import ast.IASTLeaf;
import ast.IASTVisitor;

public class NumberLiteral implements IASTLeaf {
	private Token token;
	
	public NumberLiteral(Token token) {
		this.token = token;
	}

	@Override
	public Token getToken() {
		return this.token;
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) {
		visitor.visit(this);
	}
	public String toString() {
		return this.token.getSpelling();
	}
}
