package ast.nodes.leafs;

import syntactic.Token;
import syntactic.exceptions.CompileException;
import ast.IASTLeaf;
import ast.IASTVisitor;
import ast.nodes.nonTerminals.VarDeclaration;

public class Identifier implements IASTLeaf {
	private Token token;
	private boolean isLocalIdentifier = false;
	private VarDeclaration declaration;
	private String returnType = "";
	
	public Identifier(Token token) {
		this.token = token;
	}
	
	public boolean getIsLocalIdentifier() {
		return isLocalIdentifier;
	}
	
	public void setIsLocalIdentifier(boolean isLocalIdentifier) {
		this.isLocalIdentifier = isLocalIdentifier;
	}
	
	public VarDeclaration getDeclaration() {
		return declaration;
	}
	
	public void setDeclaration(VarDeclaration declaration) {
		this.declaration = declaration;
	}
	
	public String getReturnType() {
		return returnType;
	}
	
	public void setReturnType(String returnType) {
		this.returnType = returnType;
	}
	
	@Override
	public Token getToken()  {
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
