package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.RelationalOperator;

public class BooleanExpression implements IASTNode {
	public enum Construction { LITERAL, EXPRESSION };	
	public Construction construction;
	
	public ExpressionBlock expressionBlock1;
	public RelationalOperator relationalOperator;
	public ExpressionBlock expressionBlock2;
	
	public BooleanExpression(ExpressionBlock expressionBlock) {
		this.expressionBlock1 = expressionBlock;
		this.construction = Construction.LITERAL;
	}
	
	public BooleanExpression(ExpressionBlock expressionBlock1, RelationalOperator relationalOperator, ExpressionBlock expressionBlock2) {
		this.expressionBlock1 = expressionBlock1;
		this.relationalOperator = relationalOperator;
		this.expressionBlock2 = expressionBlock2;
		this.construction = Construction.EXPRESSION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		expressionBlock1.acceptVisitor(visitor);
		
		if (construction == Construction.EXPRESSION) {
			relationalOperator.acceptVisitor(visitor);
			expressionBlock2.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
