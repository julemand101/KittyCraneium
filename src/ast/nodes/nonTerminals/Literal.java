package ast.nodes.nonTerminals;

import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.BooleanLiteral;
import ast.nodes.leafs.NumberLiteral;
import ast.nodes.leafs.StringLiteral;

public class Literal implements IASTNode {

	public enum Construction { NUMBER, STRING, BOOLEAN };	
	public Construction construction;
	
	public NumberLiteral number;
	public StringLiteral string;
	public BooleanLiteral bool;
	
	public Literal(NumberLiteral number) {
		this.number = number;
		this.construction = Construction.NUMBER;
	}
	
	public Literal(StringLiteral string) {
		this.string = string;
		this.construction = Construction.STRING;
	}
	
	public Literal(BooleanLiteral bool) {
		this.bool = bool;
		this.construction = Construction.BOOLEAN;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) {
		if (construction == Construction.NUMBER) {
			number.acceptVisitor(visitor);
		} 
		else if (construction == Construction.STRING) {
			string.acceptVisitor(visitor);
		}
		else if (construction == Construction.BOOLEAN) {
			bool.acceptVisitor(visitor);			
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) {
		visitor.visit(this);
	}

}
