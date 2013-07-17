package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.ASTList;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.subNodes.TermBlock;

public class ArithmeticExpression implements IASTNode {

	public enum Construction { WITHOPERATOR, WITHOUTOPERATOR };	
	public Construction construction;
	
	public Term term;
	public ASTList<TermBlock> termBlocks;
	
	public ArithmeticExpression(Term term, ASTList<TermBlock> termBlocks) {		
		this.term = term;
		this.termBlocks = termBlocks;
		
		this.construction = Construction.WITHOPERATOR;
	}
	
	public ArithmeticExpression(Term term) {
		this.term = term;
		this.construction = Construction.WITHOUTOPERATOR;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.term.acceptVisitor(visitor);
		if (construction == Construction.WITHOPERATOR) {		
			for (TermBlock termBlock : termBlocks) {
				termBlock.acceptVisitor(visitor);
			}
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
