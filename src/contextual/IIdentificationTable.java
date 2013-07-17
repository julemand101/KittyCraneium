package contextual;

import contextual.exceptions.IdentException;
import syntactic.exceptions.CompileException;
import ast.nodes.leafs.CoordinatePosition;
import ast.nodes.leafs.Identifier;
import ast.nodes.leafs.Type;
import ast.nodes.nonTerminals.VarDeclaration;

public interface IIdentificationTable {
	public void enter(VarDeclaration decl) throws CompileException;
	public void enter(CoordinatePosition pos) throws CompileException;
	public VarDeclaration retrieveVariable(Identifier id) throws CompileException;
	public boolean hasType(String id);
	public MethodDeclaration retrieveMethod(Type type, String name) throws IdentException;
	public void openScope() throws IdentException;
	public void closeScope();
}
