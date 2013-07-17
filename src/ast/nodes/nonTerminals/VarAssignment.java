package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.StringLiteral;

public class VarAssignment implements IASTNode {
	
	public enum Construction { EXPRESSION, STRING, COORDINATES };
	
	public Construction construction; 
	
	public Expression expression;
	public StringLiteral string;
	public Coordinates coordinates;
	
	
	public VarAssignment(Expression expression) {
		this.expression = expression;
		this.construction = Construction.EXPRESSION;
	}
	
	public VarAssignment(StringLiteral string) {
		this.string = string;
		this.construction = Construction.STRING;
	}
	
	public VarAssignment(Coordinates coordinates) {
		this.coordinates = coordinates;
		this.construction = Construction.COORDINATES;
	}
	
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		if (construction == Construction.EXPRESSION) {
			this.expression.acceptVisitor(visitor);
		} else if (construction == Construction.STRING) {
			this.string.acceptVisitor(visitor);
		} else if (construction == Construction.COORDINATES) {
			this.coordinates.acceptVisitor(visitor);
		} 
	}
	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}
}