package ast;

import contextual.exceptions.IdentException;
import syntactic.exceptions.CompileException;

public interface IAST {	
	public void acceptVisitor(IASTVisitor visitor) throws CompileException, IdentException;
}
