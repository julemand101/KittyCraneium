package contextual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import contextual.exceptions.IdentException;
import syntactic.Token;
import syntactic.TokenKind;
import syntactic.TokenPosition;
import syntactic.exceptions.ErrorType;
import syntactic.exceptions.CompileException;
import ast.nodes.nonTerminals.*;
import ast.nodes.leafs.CoordinatePosition;
import ast.nodes.leafs.Identifier;
import ast.nodes.leafs.Type;

public class IdentificationTable implements IIdentificationTable {
	private HashMap<String, VarDeclaration> globalVariables = new HashMap<String, VarDeclaration>();
	private HashMap<String, VarDeclaration> localVariables;
	private ArrayList<String> globalCoordinates = new ArrayList<String>();
	private ArrayList<String> localCoordinates;
	private HashMap<String, Type> types = new HashMap<String, Type>();
	private HashMap<String, MethodDeclaration> methodDeclarations = new HashMap<String, MethodDeclaration>();
	private boolean localScopeOpen = false;
	
	public IdentificationTable() {
		establishStandardEnvironment();
	}
	
	public void enter(VarDeclaration decl) throws CompileException {
		String varName = decl.identifier.toString();
		
		if (localScopeOpen) {
			if (localVariables.containsKey(varName))
				throw new CompileException(ErrorType.REDECLARED_VARIABLE, decl.identifier.getToken().getPosition(), "Attempted to redeclare variable " + varName);
			
			localVariables.put(varName, decl);
			decl.identifier.setIsLocalIdentifier(true);
		} else {
			if (globalVariables.containsKey(varName))
				throw new CompileException(ErrorType.REDECLARED_VARIABLE, decl.identifier.getToken().getPosition(),  "Attempted to redeclare variable " + varName);
			
			globalVariables.put(varName, decl);
			decl.identifier.setIsLocalIdentifier(false);
		}
		
		decl.identifier.setDeclaration(decl);
	}
	
	public void enter(CoordinatePosition pos) throws CompileException {
		String key = pos.getToken().getSpelling().toString();
		
		if (globalCoordinates.contains(key))
			throw new CompileException(ErrorType.INVALID_COORDINATE, pos.getToken().getPosition(), "The coordinate " + key + " has already been assigned to an area.");
		
		if (localScopeOpen) {
			if (localCoordinates.contains(key))
				throw new CompileException(ErrorType.INVALID_COORDINATE, pos.getToken().getPosition(), "The coordinate " + key + " has already been assigned to an area.");
			
			localCoordinates.add(key);
		} else {
			globalCoordinates.add(key);
		}		
	}
	
	public VarDeclaration retrieveVariable(Identifier id) throws CompileException {
		VarDeclaration decl;
		
		String key = id.toString();
		
		if (localScopeOpen && localVariables.containsKey(key)) {
			decl = localVariables.get(key);
			id.setIsLocalIdentifier(true);
		} else if (globalVariables.containsKey(key)) {
			decl = globalVariables.get(key);
			id.setIsLocalIdentifier(false);
		} else
			throw new CompileException(ErrorType.UNDECLARED_VARIABLE, id.getToken().getPosition(), "Attempted to read undeclared variable " + key);
		
		id.setDeclaration(decl);
		
		return decl;
	}
	
	public boolean hasType(String id) {
		if (!types.containsKey(id))
			return false;
		else
			return true;
	}
	
	// NOTE: Maybe this should take the type as a string instead?
	public MethodDeclaration retrieveMethod(Type type, String name) throws IdentException {
		String key = type.toString() + "." + name;		
		if (!methodDeclarations.containsKey(key))
			throw new IdentException(ErrorType.UNDECLARED_METHOD, "Attempted to use unknown method: " + key);
		return methodDeclarations.get(key);
	}
	
	public void openScope() throws IdentException {
		if (localScopeOpen) // NOTE: This (should) not be able to occur. Safety first :-)
			// TODO: Find the correct TokenPosition
			// TODO: Add new ErrorType
			throw new IdentException(ErrorType.UNEXPECTED_TOKEN, "Cannot open local scope while previous local scope has not been closed.");
		
		localVariables = new HashMap<String, VarDeclaration>();
		localCoordinates = new ArrayList<String>();
		localScopeOpen = true;
	}
	
	public void closeScope() {
		localCoordinates = null;
		localVariables = null;
		localScopeOpen = false;
	}
	
	private void establishStandardEnvironment() {
		try {
			// Add types
			types.put("AREA", new Type(new Token(TokenKind.TYPE, "AREA", new TokenPosition(-1, -1))));
			types.put("BOOLEAN", new Type(new Token(TokenKind.TYPE, "BOOLEAN", new TokenPosition(-1, -1))));
			types.put("CONTAINER", new Type(new Token(TokenKind.TYPE, "CONTAINER", new TokenPosition(-1, -1))));
			types.put("NUMBER", new Type(new Token(TokenKind.TYPE, "NUMBER", new TokenPosition(-1, -1))));
			types.put("STRING", new Type(new Token(TokenKind.TYPE, "STRING", new TokenPosition(-1, -1))));
			types.put("SYSTEM", new Type(new Token(TokenKind.TYPE, "SYSTEM", new TokenPosition(-1, -1))));
			
			// Add system variable
			enter(new VarDeclaration(types.get("SYSTEM"), new Identifier(new Token(TokenKind.IDENTIFIER, "system", new TokenPosition(-1, -1)))));
			
			// Add system methods
			methodDeclarations.put("SYSTEM.print", new MethodDeclaration(null, new ArrayList<Type>(Arrays.asList(types.get("STRING")))));
			methodDeclarations.put("SYSTEM.quit", new MethodDeclaration(null, null));
			methodDeclarations.put("SYSTEM.error", new MethodDeclaration(null, new ArrayList<Type>(Arrays.asList(types.get("STRING")))));
			methodDeclarations.put("SYSTEM.resetCrane", new MethodDeclaration(null, null));
			methodDeclarations.put("SYSTEM.playBennyHill", new MethodDeclaration(null, null));
			
			// Add container methods
			methodDeclarations.put("CONTAINER.getCID", new MethodDeclaration(types.get("NUMBER"), null));
			methodDeclarations.put("CONTAINER.getGID", new MethodDeclaration(types.get("NUMBER"), null));
			methodDeclarations.put("CONTAINER.setGID", new MethodDeclaration(null, new ArrayList<Type>(Arrays.asList(types.get("NUMBER")))));
			
			// Add area methods
			methodDeclarations.put("AREA.moveContainersTo", new MethodDeclaration(null, new ArrayList<Type>(Arrays.asList(types.get("AREA")))));
			methodDeclarations.put("AREA.countContainers", new MethodDeclaration(types.get("NUMBER"), null));
			methodDeclarations.put("AREA.freeSpace", new MethodDeclaration(types.get("NUMBER"), null));
			methodDeclarations.put("AREA.isFull", new MethodDeclaration(types.get("BOOLEAN"), null));
			methodDeclarations.put("AREA.isEmpty", new MethodDeclaration(types.get("BOOLEAN"), null));
			
		} catch (CompileException e) {
			// No errors can occur at this time
		}
	}
}