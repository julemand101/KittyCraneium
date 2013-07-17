package ast.nodes.subNodes;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.MultiplyDivideOperator;
import ast.nodes.nonTerminals.Factor;

public class FactorBlock implements IASTNode {
	
	public MultiplyDivideOperator operator;
	public Factor factor;
	
	public FactorBlock(MultiplyDivideOperator operator, Factor factor) {		
		this.operator = operator;
		this.factor = factor;
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.operator.acceptVisitor(visitor);
		this.factor.acceptVisitor(visitor);
	}

}

