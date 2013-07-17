package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;

public class Expression implements IASTNode{

	public enum Construction { BOOLEANEXPRESSION, ARITHMETICEXPRESSION };	
	public Construction construction;
	
	public BooleanExpression booleanExpression;
	public ArithmeticExpression arithmeticExpression;
	
	public Expression(BooleanExpression booleanExpression) {
		this.booleanExpression = booleanExpression;
		
		this.construction = Construction.BOOLEANEXPRESSION;
	}
	
	public Expression(ArithmeticExpression arithmeticExpression) {
		this.arithmeticExpression = arithmeticExpression;
		
		this.construction = Construction.ARITHMETICEXPRESSION;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		if (construction == Construction.BOOLEANEXPRESSION) {
			this.booleanExpression.acceptVisitor(visitor);
		} 
		else if (construction == Construction.ARITHMETICEXPRESSION) {
			this.arithmeticExpression.acceptVisitor(visitor);
		}
		
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
