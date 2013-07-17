package ast;

import contextual.exceptions.IdentException;
import syntactic.exceptions.CompileException;

public interface IASTNode extends IAST {
	public void visitChildren(IASTVisitor visitor) throws CompileException, IdentException;
}
