package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;

public class WhereExpression implements IASTNode {

	public BooleanExpression booleanExpression;
	
	public WhereExpression(BooleanExpression booleanExpression) {
		this.booleanExpression = booleanExpression;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.booleanExpression.acceptVisitor(visitor);
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
