package syntactic;

import exceptions.EmptyListException;
import syntactic.exceptions.CompileException;
import ast.*;

public interface IParser {
	public IAST parse(TokenList tokens) throws CompileException, EmptyListException;
}
