package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.ASTList;
import ast.IASTNode;
import ast.IASTVisitor;

public class Parameters implements IASTNode {

	public ASTList<ExpressionBlock> expressionBlocks;
	
	public Parameters(ASTList<ExpressionBlock> expressionBlocks) {
		this.expressionBlocks = expressionBlocks;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		for (ExpressionBlock expressionBlock : expressionBlocks) {
			expressionBlock.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
