package ast.nodes.nonTerminals;

import contextual.exceptions.IdentException;
import syntactic.exceptions.CompileException;
import ast.ASTList;
import ast.IASTNode;
import ast.IASTVisitor;

public class Program implements IASTNode {
	// {  <VarDeclaration>  }*   {  <EventCommand>  }*
	
	public enum Construction { VARDECLARATIONS, EVENTCOMMANDS, BOTH };	
	public Construction construction;
	
	public ASTList<EventCommand> eventCommands = new ASTList<EventCommand>();
	public ASTList<VarDeclaration> varDeclarations = new ASTList<VarDeclaration>();
	
	public Program(ASTList<EventCommand> eventCommands, boolean java_uses_type_erasure_so_set_this_boolean_if_you_want_to_declare_an_eventcommandList) {
		this.eventCommands = eventCommands;
		this.construction = Construction.EVENTCOMMANDS;
	}

	public Program(ASTList<VarDeclaration> varDeclarations) {
		this.varDeclarations = varDeclarations;
		this.construction = Construction.VARDECLARATIONS;
	}
	
	public Program(ASTList<VarDeclaration> varDeclarations, ASTList<EventCommand> eventCommands) {
		this.eventCommands = eventCommands;
		this.varDeclarations = varDeclarations;
		this.construction = Construction.BOTH;		
	}
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException, IdentException {		
		for (VarDeclaration varDeclaration : varDeclarations) {
			varDeclaration.acceptVisitor(visitor);
		}
		for (EventCommand eventCommand : eventCommands) {
			eventCommand.acceptVisitor(visitor);
		}
	}
	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException, IdentException {
		visitor.visit(this);
	}
}
