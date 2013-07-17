package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.Identifier;
import ast.nodes.leafs.NumberLiteral;

public class Factor implements IASTNode {

	public enum Construction { ARITHMETICEXPRESSION, NUMBER, IDENTIFIER, IDENTIFIERMETHOD };	
	public Construction construction;
	
	public ArithmeticExpression arithmeticExpression;
	public NumberLiteral number;
	public Identifier identifierArea;
	public Identifier identifierMethod;
	public Parameters parameters;
	public boolean isNegative = false;;
	
	public Factor(ArithmeticExpression arithmeticExpression, boolean isNegative) {
		this.arithmeticExpression = arithmeticExpression;
		this.construction = Construction.ARITHMETICEXPRESSION;
		this.isNegative = isNegative;
	}
	
	public Factor(NumberLiteral number, boolean isNegative) {
		this.number = number;
		this.construction = Construction.NUMBER;
		this.isNegative = isNegative;
	}
	
	public Factor(Identifier identifierArea, boolean isNegative) {
		this.identifierArea = identifierArea;
		this.construction = Construction.IDENTIFIER;
		this.isNegative = isNegative;
	}
	
	public Factor(Identifier identifierArea, Identifier identifierMethod, Parameters parameters, boolean isNegative) {
		this.identifierArea = identifierArea;
		this.identifierMethod = identifierMethod;
		this.parameters = parameters;
		this.construction = Construction.IDENTIFIERMETHOD;
		this.isNegative = isNegative;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		if (construction == Construction.ARITHMETICEXPRESSION) {
			this.arithmeticExpression.acceptVisitor(visitor);
		}
		else if (construction == Construction.NUMBER) {
			this.number.acceptVisitor(visitor);
		}
		else if (construction == Construction.IDENTIFIER) {
			this.identifierArea.acceptVisitor(visitor);			
		}
		else if (construction == Construction.IDENTIFIERMETHOD) {
			this.identifierArea.acceptVisitor(visitor);
			this.identifierMethod.acceptVisitor(visitor);
			this.parameters.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
