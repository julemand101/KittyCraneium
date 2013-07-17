package ast;

import java.util.ArrayList;

import contextual.exceptions.IdentException;

import syntactic.exceptions.CompileException;

import ast.IAST;
import ast.IASTNode;
import ast.IASTVisitor;

public class ASTList<T extends IAST> extends ArrayList<T> implements IASTNode {
	private static final long serialVersionUID = -5008636289413360459L;

	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException, IdentException {
		for (T item : this) {
			item.acceptVisitor(visitor);
		}	
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) {
	}
	
	public boolean hasItem() {
		if (this.size() > 0) {
			return true;
		} else {
			return false;
		}		
	}
}
