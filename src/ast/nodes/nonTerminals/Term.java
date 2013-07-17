package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.ASTList;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.subNodes.FactorBlock;

public class Term implements IASTNode {

	public enum Construction { WITHOPERATOR, WITHOUTOPERATOR };	
	public Construction construction;
	
	public Factor factor;
	public ASTList<FactorBlock> factorBlocks;
	
	public Term(Factor factor, ASTList<FactorBlock> factorBlocks) {		
		this.factor = factor;
		this.factorBlocks = factorBlocks;
		
		this.construction = Construction.WITHOPERATOR;
	}
	
	public Term(Factor factor) {		
		this.factor = factor;
		this.construction = Construction.WITHOUTOPERATOR;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.factor.acceptVisitor(visitor);
		if (construction == Construction.WITHOPERATOR) {		
			for (FactorBlock factorBlock : factorBlocks) {
				factorBlock.acceptVisitor(visitor);
			}
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
