package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.ASTList;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.subNodes.ElseIfBlock;

public class IfStatement implements IASTNode {
	
	public enum Construction { IF, IFELSEIF, IFELSE, IFELSEIFELSE };
	
	public Construction construction;
	
	public BooleanExpression booleanExpressionIf;
	public ASTList<Command> commandIfs;
	public ASTList<ElseIfBlock> elseIfBlocks;
	public ASTList<Command> commandElses;

	public IfStatement(BooleanExpression booleanExpressionIf, ASTList<Command> commandIfs) {
		this.booleanExpressionIf = booleanExpressionIf;
		this.commandIfs = commandIfs;
		
		this.construction = Construction.IF;
	}
	
	public IfStatement(	BooleanExpression booleanExpressionIf, ASTList<Command> commandIfs, ASTList<ElseIfBlock> elseIfBlocks, boolean java_uses_type_erasure_so_set_this_boolean_if_you_want_to_declare_an_eventcommandList) {
		this.booleanExpressionIf = booleanExpressionIf;
		this.commandIfs = commandIfs;
		this.elseIfBlocks = elseIfBlocks;
		
		this.construction = Construction.IFELSEIF;
	}
	
	public IfStatement(BooleanExpression booleanExpressionIf, ASTList<Command> commandIfs, ASTList<Command> commandElses) {
		this.booleanExpressionIf = booleanExpressionIf;
		this.commandIfs = commandIfs;	
		this.commandElses = commandElses;
		
		this.construction = Construction.IFELSE;
	}
	
	public IfStatement(BooleanExpression booleanExpressionIf, ASTList<Command> commandIfs, ASTList<ElseIfBlock> elseIfBlocks, ASTList<Command> commandElses) {
		this.booleanExpressionIf = booleanExpressionIf;
		this.commandIfs = commandIfs;
		this.elseIfBlocks = elseIfBlocks;
		this.commandElses = commandElses;
		
		this.construction = Construction.IFELSEIFELSE;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.booleanExpressionIf.acceptVisitor(visitor);
		for (Command commandIf : commandIfs) {
			commandIf.acceptVisitor(visitor);
		}
		if (construction == Construction.IFELSEIF || construction == Construction.IFELSEIFELSE) {
			for (ElseIfBlock elseIfBlock : elseIfBlocks) {
				elseIfBlock.acceptVisitor(visitor);
			}
			if (construction == Construction.IFELSEIFELSE) {
				for (Command commandElse : commandElses) {
					commandElse.acceptVisitor(visitor);
				}	
			}
		} else if (construction == Construction.IFELSE) {
			for (Command commandElse : commandElses) {
				commandElse.acceptVisitor(visitor);
			}
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}
	
}
