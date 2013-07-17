package ast.nodes.subNodes;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.PlusMinusOperator;
import ast.nodes.nonTerminals.Term;

public class TermBlock implements IASTNode {
	
	public PlusMinusOperator operator;
	public Term term;
	
	public TermBlock(PlusMinusOperator operator, Term term) {		
		this.operator = operator;
		this.term = term;
	}

	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.operator.acceptVisitor(visitor);
		this.term.acceptVisitor(visitor);
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}
}

