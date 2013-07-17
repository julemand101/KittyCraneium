package ast.nodes.nonTerminals;

import contextual.exceptions.IdentException;
import syntactic.exceptions.CompileException;
import ast.ASTList;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.Identifier;
import ast.nodes.leafs.Type;

public class EventCommand implements IASTNode {
	
	public enum Construction { INCOORDINATE, INCOORDINATEWHERE, INAREA, INAREAWHERE, INAREAMETHODCALL, INAREAMETHODCALLWHERE, WHERE };
	
	public Construction construction;
	
	public Type type;	
	public Identifier identifierEvent;	
	public Coordinates coordinates;
	public Identifier identifierArea;
	public Identifier identifierMethod;
	public Parameters parameters;
	public WhereExpression whereExpression;
	public ASTList<VarDeclaration> varDeclarations = new ASTList<VarDeclaration>();
	public ASTList<Command> commands = new ASTList<Command>();
	
	public EventCommand(Type type, Identifier identifierEvent, WhereExpression whereExpression, ASTList<VarDeclaration> varDeclarations, ASTList<Command> commands) {
		this.type = type;
		this.identifierEvent = identifierEvent;
		this.whereExpression = whereExpression;
		this.varDeclarations = varDeclarations;
		this.commands = commands;
		
		this.construction = Construction.WHERE;
	}
	
	public EventCommand(Type type, Identifier identifierEvent, Coordinates coordinates, ASTList<VarDeclaration> varDeclarations, ASTList<Command> commands) {
		this.type = type;
		this.identifierEvent = identifierEvent;
		this.coordinates = coordinates;
		this.varDeclarations = varDeclarations;
		this.commands = commands;		
		this.construction = Construction.INCOORDINATE;		
	}
	
	public EventCommand(Type type, Identifier identifierEvent, Coordinates coordinates, WhereExpression whereExpression, ASTList<VarDeclaration> varDeclarations, ASTList<Command> commands) {
		this.type = type;
		this.identifierEvent = identifierEvent;
		this.coordinates = coordinates;
		this.whereExpression = whereExpression;
		this.varDeclarations = varDeclarations;
		this.commands = commands;		
		this.construction = Construction.INCOORDINATEWHERE;		
	}
	
	public EventCommand(Type type, Identifier identifierEvent, Identifier identifierArea, ASTList<VarDeclaration> varDeclarations, ASTList<Command> commands) {
		this.type = type;
		this.identifierEvent = identifierEvent;
		this.identifierArea = identifierArea;
		this.varDeclarations = varDeclarations;
		this.commands = commands;		
		this.construction = Construction.INAREA;
		
	}
	
	public EventCommand(Type type, Identifier identifierEvent, Identifier identifierArea, WhereExpression whereExpression, ASTList<VarDeclaration> varDeclarations, ASTList<Command> commands) {
		this.type = type;
		this.identifierEvent = identifierEvent;
		this.identifierArea = identifierArea;
		this.whereExpression = whereExpression;
		this.varDeclarations = varDeclarations;
		this.commands = commands;		
		this.construction = Construction.INAREAWHERE;
		
	}
	
	public EventCommand(Type type, Identifier identifierEvent, Identifier identifierArea, Identifier identifierMethod, Parameters parameters, ASTList<VarDeclaration> varDeclarations, ASTList<Command> commands) {
		this.type = type;
		this.identifierEvent = identifierEvent;
		this.identifierArea = identifierArea;
		this.identifierMethod = identifierMethod;
		this.parameters = parameters;
		this.varDeclarations = varDeclarations;
		this.commands = commands;		
		this.construction = Construction.INAREAMETHODCALL;
		
	}
	
	public EventCommand(Type type, Identifier identifierEvent, Identifier identifierArea, Identifier identifierMethod, Parameters parameters, WhereExpression whereExpression, ASTList<VarDeclaration> varDeclarations, ASTList<Command> commands) {
		this.type = type;
		this.identifierEvent = identifierEvent;
		this.identifierArea = identifierArea;
		this.identifierMethod = identifierMethod;
		this.parameters = parameters;
		this.whereExpression = whereExpression;
		this.varDeclarations = varDeclarations;
		this.commands = commands;		
		this.construction = Construction.INAREAMETHODCALLWHERE;		
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		this.type.acceptVisitor(visitor);
		this.identifierEvent.acceptVisitor(visitor);
		if (construction == Construction.WHERE) {
			this.whereExpression.acceptVisitor(visitor);
		} if (construction == Construction.INCOORDINATE) {			
			this.coordinates.acceptVisitor(visitor);			
		} else if (construction == Construction.INCOORDINATEWHERE) {
			this.coordinates.acceptVisitor(visitor);
			this.whereExpression.acceptVisitor(visitor);
		} else if (construction == Construction.INAREA) {
			this.identifierArea.acceptVisitor(visitor);
		} else if (construction == Construction.INAREAWHERE) {
			this.identifierArea.acceptVisitor(visitor);
			this.whereExpression.acceptVisitor(visitor);
		} else if (construction == Construction.INAREAMETHODCALL) {
			this.identifierArea.acceptVisitor(visitor);
			this.identifierMethod.acceptVisitor(visitor); // NOTE: Cannot visit it as an identifier; does not exist as such in the ident table
			this.parameters.acceptVisitor(visitor);
		} else if (construction == Construction.INAREAMETHODCALLWHERE) {
			this.identifierArea.acceptVisitor(visitor);
			this.identifierMethod.acceptVisitor(visitor); // NOTE: Cannot visit it as an identifier; does not exist as such in the ident table
			this.parameters.acceptVisitor(visitor);
			this.whereExpression.acceptVisitor(visitor);
		}
		
		for (VarDeclaration varDeclaration : this.varDeclarations) {
			varDeclaration.acceptVisitor(visitor);
		}
		
		for (Command command : this.commands) {
			command.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException, IdentException {
		visitor.visit(this);
	}
}