package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.Identifier;

public class VarAssignmentCommand implements IASTNode {
	
	public Identifier identifier;	
	public VarAssignment varAssignment;
	
	public VarAssignmentCommand(Identifier identifier, VarAssignment varAssignment) {
		this.identifier = identifier;
		this.varAssignment = varAssignment;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.identifier.acceptVisitor(visitor);
		this.varAssignment.acceptVisitor(visitor);
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
