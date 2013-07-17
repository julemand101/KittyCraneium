package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.Identifier;

public class ExpressionBlock implements IASTNode {

	public enum Construction { IDENTIFIER, IDENTIFIERMETHOD, LITERAL, EXPRESSION };	
	public Construction construction;
	
	public Identifier identifierArea;
	public Identifier identifierMethod;
	public Parameters parameters;
	public Literal literal;
	public Expression expression;
	public boolean isNegative;
	
	public ExpressionBlock(Identifier identifierArea, boolean isNegative) {
		this.identifierArea = identifierArea;
		this.construction = Construction.IDENTIFIER;
		this.isNegative = isNegative;
	}
	
	public ExpressionBlock(Identifier identifierArea, Identifier identifierMethod, Parameters parameters, boolean isNegative) {
		this.identifierArea = identifierArea;
		this.identifierMethod = identifierMethod;
		this.parameters = parameters;
		this.construction = Construction.IDENTIFIERMETHOD;
		this.isNegative = isNegative;
	}
	
	public ExpressionBlock(Literal literal, boolean isNegative) {
		this.literal = literal;
		this.construction = Construction.LITERAL;
		this.isNegative = isNegative;
	}
	
	public ExpressionBlock(Expression expression, boolean isNegative) {
		this.expression = expression;
		this.construction = Construction.EXPRESSION;
		this.isNegative = isNegative;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		if (construction == Construction.IDENTIFIER || construction == Construction.IDENTIFIERMETHOD) {
			this.identifierArea.acceptVisitor(visitor);
			if (construction == Construction.IDENTIFIERMETHOD) {
				this.identifierMethod.acceptVisitor(visitor);
				this.parameters.acceptVisitor(visitor);
			}
		}
		else if (construction == Construction.LITERAL) {
			this.literal.acceptVisitor(visitor);
		} 
		else if (construction == Construction.EXPRESSION) {
			this.expression.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
