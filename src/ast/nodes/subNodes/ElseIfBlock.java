package ast.nodes.subNodes;

import syntactic.exceptions.CompileException;
import ast.ASTList;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.nonTerminals.BooleanExpression;
import ast.nodes.nonTerminals.Command;

public class ElseIfBlock implements IASTNode {

	public BooleanExpression booleanExpression;
	public ASTList<Command> commands;
	
	public ElseIfBlock(BooleanExpression booleanExpression, ASTList<Command> commands) {
		this.booleanExpression = booleanExpression;
		this.commands = commands;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.booleanExpression.acceptVisitor(visitor);
		for (Command command : commands) {
			command.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
