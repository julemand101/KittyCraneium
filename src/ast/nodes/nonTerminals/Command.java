package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.Identifier;

public class Command implements IASTNode {
	
	public enum Construction { COMMANDMOVE, COMMANDGOTO, VARASSIGNMENTCOMMAND, METHODCALL, IFSTATEMENT };	
	public Construction construction;
	
	public CommandMove commandMove;
	public CommandGoto commandGoto;
	public VarAssignmentCommand varAssignmentCommand;
	public Identifier identifierArea;
	public Identifier identifierMethod;
	public Parameters parameters;
	public IfStatement ifStatement;

	public Command(CommandMove commandMove) {
		this.commandMove = commandMove;
		
		this.construction = Construction.COMMANDMOVE;
	}
	
	public Command(CommandGoto commandGoto) {
		this.commandGoto = commandGoto;
		
		this.construction = Construction.COMMANDGOTO;
	}
	
	public Command(VarAssignmentCommand varAssignmentCommand) {
		this.varAssignmentCommand = varAssignmentCommand;
		
		this.construction = Construction.VARASSIGNMENTCOMMAND;
	}
	
	public Command(Identifier identifierArea, Identifier identifierMethod, Parameters parameters) {
		this.identifierArea = identifierArea;
		this.identifierMethod = identifierMethod;
		this.parameters = parameters;

		this.construction = Construction.METHODCALL;
	}
	
	public Command(IfStatement ifStatement) {
		this.ifStatement = ifStatement;
		
		this.construction = Construction.IFSTATEMENT;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		if (construction == Construction.COMMANDMOVE)
			commandMove.acceptVisitor(visitor);
		else if (construction == Construction.COMMANDGOTO)
			commandGoto.acceptVisitor(visitor);
		else if (construction == Construction.VARASSIGNMENTCOMMAND)
			varAssignmentCommand.acceptVisitor(visitor);
		else if (construction == Construction.METHODCALL) {
			this.identifierArea.acceptVisitor(visitor);
			this.identifierMethod.acceptVisitor(visitor);
			this.parameters.acceptVisitor(visitor);
		}
		else if (construction == Construction.IFSTATEMENT)
			ifStatement.acceptVisitor(visitor);		
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}
	
}
