package contextual;

import java.util.ArrayList;

import ast.nodes.leafs.Type;
public class MethodDeclaration {
	private Type type;
	private ArrayList<Type> parameterTypes = new ArrayList<Type>();
	
	public MethodDeclaration(Type type, ArrayList<Type> parameterTypes) {
		this.type  = type;
		
		if (parameterTypes != null)
			this.parameterTypes = parameterTypes;
	}
	
	public Type getType() {
		return type;
	}
	
	public ArrayList<Type> getParameterTypes() {
		return parameterTypes;
	}
}
