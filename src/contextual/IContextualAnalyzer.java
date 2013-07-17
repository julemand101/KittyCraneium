package contextual;

import contextual.exceptions.IdentException;
import syntactic.exceptions.CompileException;
import ast.IAST;

public interface IContextualAnalyzer {
	public IAST check(IAST program) throws CompileException, IdentException;
	public IIdentificationTable getIdentificationTable();
}
