package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.Identifier;
import ast.nodes.leafs.Type;

public class VarDeclaration implements IASTNode {

	public enum Construction { WITHOUTASSIGNMENT, WITHASSIGNMENT };
	
	public Construction construction; 
	
	public Type type;
	public Identifier identifier;
	public VarAssignment varAssignment;
	
	public VarDeclaration(Type type, Identifier identifier) {
		this.type = type;
		this.identifier = identifier;
		
		this.construction = Construction.WITHOUTASSIGNMENT;
	}
	public VarDeclaration(Type type, Identifier identifier, VarAssignment varAssignment) {
		this.type = type;
		this.identifier = identifier;
		this.varAssignment = varAssignment;
		
		this.construction = Construction.WITHASSIGNMENT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.type.acceptVisitor(visitor);
		this.identifier.acceptVisitor(visitor);
		if (this.construction == Construction.WITHASSIGNMENT) {
			this.varAssignment.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
