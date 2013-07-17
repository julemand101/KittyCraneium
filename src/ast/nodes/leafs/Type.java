package ast.nodes.leafs;

import syntactic.Token;
import ast.IASTLeaf;
import ast.IASTVisitor;

public class Type implements IASTLeaf {
	private Token token;
	
	public Type(Token token) {
		this.token = token;
	}	

	// TODO: Evt. en getMethods() metode som returnerer metode-navn og retur-type baseret på token'ens spelling. 
	
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
