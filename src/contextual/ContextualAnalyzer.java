package contextual;

import contextual.exceptions.IdentException;
import syntactic.TokenPosition;
import syntactic.exceptions.ErrorType;
import syntactic.exceptions.CompileException;

import ast.nodes.nonTerminals.*;
import ast.nodes.nonTerminals.ExpressionBlock.Construction;
import ast.nodes.leafs.*;
import ast.nodes.subNodes.*;
import ast.IAST;
import ast.IASTVisitor;

public class ContextualAnalyzer implements IContextualAnalyzer, IASTVisitor {
	private IAST decoratedProgram;
	private IIdentificationTable identTable;
	
	public IAST check(IAST program) throws CompileException, IdentException {
		decoratedProgram = program;
		identTable = new IdentificationTable();
		
		decoratedProgram.acceptVisitor(this);		
		
		return decoratedProgram;
	}
	
	public IIdentificationTable getIdentificationTable() {
		return identTable;
	}

	@Override
	public void visit(ArithmeticExpression arithmeticExpression) throws CompileException {
		arithmeticExpression.visitChildren(this);
	}

	@Override
	public void visit(BooleanExpression booleanExpression) throws CompileException {
		String type1 = getExpressionBlockType(booleanExpression.expressionBlock1);
		
		if (booleanExpression.construction == ast.nodes.nonTerminals.BooleanExpression.Construction.EXPRESSION)
		{
			String type2 = getExpressionBlockType(booleanExpression.expressionBlock2);
			
			if (booleanExpression.relationalOperator.getToken().getSpelling().equals("EQUAL TO") ||
					booleanExpression.relationalOperator.getToken().getSpelling().equals("NOT EQUAL TO")) {
				if (!type1.equals(type2))
					throw new CompileException(ErrorType.INVALID_OPERATOR, booleanExpression.relationalOperator.getToken().getPosition(), "Invalid logical operator " + booleanExpression.relationalOperator.toString() + " used on non-matching operand types: " + type1 + " and " + type2);
			} else {
				if (!type1.equals("NUMBER") || !type2.equals("NUMBER"))
					throw new CompileException(ErrorType.INVALID_OPERATOR, booleanExpression.relationalOperator.getToken().getPosition(), "Invalid logical operator " + booleanExpression.relationalOperator.toString() + " used with types: " + type1 + " and " + type2);
			}
		}
		
		booleanExpression.visitChildren(this);
	}
	
	@Override
	public void visit(BooleanLiteral booleanAST) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void visit(Command command) throws CompileException {		
		if (command.construction == ast.nodes.nonTerminals.Command.Construction.METHODCALL) {						
			VarDeclaration varDecl;
			try {				
				varDecl = identTable.retrieveVariable(command.identifierArea);				
			} catch (CompileException e) {
				throw new CompileException(e.getErrorType(), command.identifierArea.getToken().getPosition(), e.getMessage());
			}
			
			MethodDeclaration methodDecl;
			try {
				methodDecl = identTable.retrieveMethod(varDecl.type, command.identifierMethod.toString());
			} catch (IdentException e) {
				throw new CompileException(e.getErrorType(), command.identifierArea.getToken().getPosition(), e.getMessage());
			}
			
			if (methodDecl.getType() != null)
				command.identifierMethod.setReturnType(methodDecl.getType().toString());			
			verifyParameters(methodDecl, command.parameters, command.identifierMethod.getToken().getPosition());
		} else
			command.visitChildren(this);
	}

	@Override
	public void visit(CommandGoto commandGoto) throws CompileException {
		if (commandGoto.construction == ast.nodes.nonTerminals.CommandGoto.Construction.GOTOAREA) {
			VarDeclaration decl = identTable.retrieveVariable(commandGoto.identifierArea);
			if (!decl.type.toString().equals("AREA") && !decl.type.toString().equals("COORDINATE"))
				throw new CompileException(ErrorType.INVALID_TYPE, decl.type.getToken().getPosition(), "Can only go to an area or coordinate.");
		}
		
		commandGoto.visitChildren(this);
	}

	@Override
	public void visit(CommandMove commandMove) throws CompileException {
		VarDeclaration decl;
		
		decl = identTable.retrieveVariable(commandMove.identifierContainer);
		if (!decl.type.toString().equals("CONTAINER"))
			throw new CompileException(ErrorType.INVALID_TYPE, decl.type.getToken().getPosition(), "Can only move containers.");
		
		if (commandMove.construction == ast.nodes.nonTerminals.CommandMove.Construction.MOVETOAREA) {
			decl = identTable.retrieveVariable(commandMove.identifierArea);
			if (!decl.type.toString().equals("AREA") && !decl.type.toString().equals("COORDINATE"))
				throw new CompileException(ErrorType.INVALID_TYPE, decl.type.getToken().getPosition(), "Can only move to an area or coordinate.");
		}
		
		commandMove.visitChildren(this);
	}

	@Override
	public void visit(Coordinate coordinate) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void visit(CoordinatePosition coordinatePosition) throws CompileException {
		
	}
	
	@Override
	public void visit(Coordinates coordinates) throws CompileException {
		coordinates.visitChildren(this);
	}

	@Override
	public void visit(EventCommand eventCommand) throws CompileException, IdentException {		
		if (!eventCommand.type.toString().equals("CONTAINER"))
			throw new CompileException(ErrorType.INVALID_TYPE, eventCommand.type.getToken().getPosition(), "Events can only operate on CONTAINER types.");
		
		identTable.openScope();
		
		// Creates a new dummy variable declaration. Is used to put the event alias in the ident. table.
		VarDeclaration varDecl = new VarDeclaration(eventCommand.type, eventCommand.identifierEvent);
		identTable.enter(varDecl);		
		if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INAREAMETHODCALL ||
				eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INAREAMETHODCALLWHERE) {
			varDecl = identTable.retrieveVariable(eventCommand.identifierArea);
			MethodDeclaration methodDecl = identTable.retrieveMethod(varDecl.type, eventCommand.identifierMethod.toString());
			
			eventCommand.identifierMethod.setReturnType(methodDecl.getType().toString());
			
			if (!methodDecl.getType().toString().equals("AREA"))
				throw new CompileException(ErrorType.INVALID_TYPE, methodDecl.getType().getToken().getPosition(), "Invalid return type of method in eventcommand's in-clause.");
		} else if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INAREA || eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INAREAWHERE) {
			varDecl = identTable.retrieveVariable(eventCommand.identifierArea);
			
			if (!varDecl.type.toString().equals("AREA") && !varDecl.type.toString().equals("COORDINATES") && !varDecl.type.toString().equals("COORDINATE"))
				throw new CompileException(ErrorType.INVALID_TYPE, varDecl.type.getToken().getPosition(), "Invalid type used in eventcommand's in-clause. Only AREA types are allowed.");
		}
		
		eventCommand.type.acceptVisitor(this);
		eventCommand.identifierEvent.acceptVisitor(this);
		if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.WHERE) {
			eventCommand.whereExpression.acceptVisitor(this);
		} else if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INCOORDINATE) {
			eventCommand.coordinates.acceptVisitor(this);			
		} else if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INCOORDINATEWHERE) {
			eventCommand.coordinates.acceptVisitor(this);
			eventCommand.whereExpression.acceptVisitor(this);
		} else if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INAREA) {			
			eventCommand.identifierArea.acceptVisitor(this);
		} else if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INAREAWHERE) {
			eventCommand.identifierArea.acceptVisitor(this);
			eventCommand.whereExpression.acceptVisitor(this);
		} else if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INAREAMETHODCALL) {
			eventCommand.identifierArea.acceptVisitor(this);
			eventCommand.parameters.acceptVisitor(this);
		} else if (eventCommand.construction == ast.nodes.nonTerminals.EventCommand.Construction.INAREAMETHODCALLWHERE) {
			eventCommand.identifierArea.acceptVisitor(this);
			eventCommand.parameters.acceptVisitor(this);
			eventCommand.whereExpression.acceptVisitor(this);
		}
		
		for (VarDeclaration varDeclaration : eventCommand.varDeclarations) {
			varDeclaration.acceptVisitor(this);
		}
		
		for (Command command : eventCommand.commands) {
			command.acceptVisitor(this);
		}
		
		identTable.closeScope();
	}

	@Override
	public void visit(Expression expression) throws CompileException {		
		expression.visitChildren(this);
	}

	@Override
	public void visit(ExpressionBlock expressionBlock) throws CompileException {
		if (expressionBlock.construction == Construction.IDENTIFIERMETHOD) {			
			VarDeclaration decl = identTable.retrieveVariable(expressionBlock.identifierArea);
			MethodDeclaration methodDecl;
			try {
				methodDecl = identTable.retrieveMethod(decl.type, expressionBlock.identifierMethod.toString());
			} catch (IdentException e) {
				throw new CompileException(e.getErrorType(), expressionBlock.identifierMethod.getToken().getPosition(), e.getMessage());
			}
			
			expressionBlock.identifierMethod.setReturnType(methodDecl.getType().toString());
			
			verifyParameters(methodDecl, expressionBlock.parameters, expressionBlock.identifierMethod.getToken().getPosition());
		} else
			expressionBlock.visitChildren(this);
	}

	@Override
	public void visit(Factor factor) throws CompileException {
		if (factor.construction == ast.nodes.nonTerminals.Factor.Construction.IDENTIFIERMETHOD) {
			VarDeclaration decl = identTable.retrieveVariable(factor.identifierArea);
			MethodDeclaration methodDecl;
			try {
				methodDecl = identTable.retrieveMethod(decl.type, factor.identifierMethod.toString());
			} catch (IdentException e) {
				throw new CompileException(e.getErrorType(), factor.identifierMethod.getToken().getPosition(), e.getMessage());
			}
			
			factor.identifierMethod.setReturnType(methodDecl.getType().toString());
			
			verifyParameters(methodDecl, factor.parameters, factor.identifierMethod.getToken().getPosition());
		} else
			factor.visitChildren(this);
	}
	
	@Override
	public void visit(FactorBlock factorBlock) throws CompileException {
		if (!getFactorType(factorBlock.factor).equals("NUMBER"))
			throw new CompileException(ErrorType.INVALID_OPERATOR, factorBlock.operator.getToken().getPosition(), "Invalid operand used in factor block");
		
		factorBlock.visitChildren(this);
	}

	@Override
	public void visit(Identifier identifier) throws CompileException {
		identTable.retrieveVariable(identifier);
	}
	
	@Override
	public void visit(IfStatement ifStatement) throws CompileException {
		ifStatement.visitChildren(this);
	}

	@Override
	public void visit(Literal literal) {
		// TODO Auto-generated method stub		
	}
	
	@Override
	public void visit(RelationalOperator logicalOperator) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void visit(MultiplyDivideOperator multiplyDivideOperator) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(NumberLiteral numberAST) {
		// TODO Auto-generated method stub
	}

	@Override
	public void visit(ElseIfBlock elseIfBlock) throws CompileException {
		elseIfBlock.visitChildren(this);
	}
	
	@Override
	public void visit(Parameters parameters) throws CompileException {
		parameters.visitChildren(this);
	}
	
	@Override
	public void visit(PlusMinusOperator plusMinusOperator) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void visit(Program program) throws CompileException, IdentException {
		program.visitChildren(this);
	}

	@Override
	public void visit(StringLiteral stringAST) {
		// TODO Auto-generated method stub
	}
	
	@Override
	public void visit(Term term) throws CompileException {
		term.visitChildren(this);
	}
	
	@Override
	public void visit(TermBlock termBlock) throws CompileException {
		if (!getTermType(termBlock.term).equals("NUMBER"))
			throw new CompileException(ErrorType.INVALID_OPERATOR, termBlock.operator.getToken().getPosition(), "Invalid operand used in term block");

		termBlock.visitChildren(this);
	}
	
	@Override
	public void visit(Type type) {
		identTable.hasType(type.toString());
	}
	
	@Override
	public void visit(VarAssignment varAssignment) throws CompileException {
		varAssignment.visitChildren(this);
		
		if (varAssignment.construction == ast.nodes.nonTerminals.VarAssignment.Construction.COORDINATES) {
			for (CoordinatePosition pos : varAssignment.coordinates.coordinatePositions) {				
				identTable.enter(pos);
			}
		}
	}

	@Override
	public void visit(VarAssignmentCommand varAssignmentCommand) throws CompileException {
		VarDeclaration varDecl = identTable.retrieveVariable(varAssignmentCommand.identifier);
		
		switch (varAssignmentCommand.varAssignment.construction) {
		case COORDINATES:
			if (!varDecl.type.toString().equals("AREA"))
				throw new CompileException(ErrorType.INVALID_TYPE, varAssignmentCommand.identifier.getToken().getPosition(), "Can only assign coordinates to areas.");
			break;
		case EXPRESSION:
			switch (varAssignmentCommand.varAssignment.expression.construction) {
				case ARITHMETICEXPRESSION:
					String type = getArithmeticExpressionType(varAssignmentCommand.varAssignment.expression.arithmeticExpression);
					if (!varDecl.type.toString().equals(type))
						throw new CompileException(ErrorType.INVALID_TYPE, varAssignmentCommand.identifier.getToken().getPosition(), "Type mismatch when assigning " + type + " to " + varDecl.type.toString());
					break;
				case BOOLEANEXPRESSION:
					if (!varDecl.type.toString().equals("BOOLEAN"))
						throw new CompileException(ErrorType.INVALID_TYPE, varAssignmentCommand.identifier.getToken().getPosition(), "Type mismatch when assigning BOOLEAN to " + varDecl.type.toString());
					break;
			}
			break;
		case STRING:
			if (!varDecl.type.toString().equals("STRING"))
				throw new CompileException(ErrorType.INVALID_TYPE, varAssignmentCommand.identifier.getToken().getPosition(), "Type mismatch when assigning STRING to " + varDecl.type.toString());
			break;
		}
		
		varAssignmentCommand.visitChildren(this);
	}

	@Override
	public void visit(VarDeclaration varDeclaration) throws CompileException {
		if (!identTable.hasType(varDeclaration.type.toString()))
			throw new CompileException(ErrorType.INVALID_TYPE, varDeclaration.type.getToken().getPosition(), "Cannot declare variable of invalid type: " + varDeclaration.type.toString());
		
		if (varDeclaration.construction == ast.nodes.nonTerminals.VarDeclaration.Construction.WITHASSIGNMENT) {
			switch (varDeclaration.varAssignment.construction) {
			case COORDINATES:
				if (!varDeclaration.type.toString().equals("AREA"))
					throw new CompileException(ErrorType.INVALID_TYPE, varDeclaration.identifier.getToken().getPosition(), "Can only assign coordinates to areas.");
				break;
			case EXPRESSION:
				switch (varDeclaration.varAssignment.expression.construction) {
					case ARITHMETICEXPRESSION:
						String type = getArithmeticExpressionType(varDeclaration.varAssignment.expression.arithmeticExpression);
						if (!varDeclaration.type.toString().equals(type))
							throw new CompileException(ErrorType.INVALID_TYPE, varDeclaration.identifier.getToken().getPosition(), "Type mismatch when assigning " + type + " to " + varDeclaration.type.toString());
						break;
					case BOOLEANEXPRESSION:
						if (!varDeclaration.type.toString().equals("BOOLEAN"))
							throw new CompileException(ErrorType.INVALID_TYPE, varDeclaration.identifier.getToken().getPosition(), "Type mismatch when assigning BOOLEAN to " + varDeclaration.type.toString());
						break;
				}
				break;
			case STRING:
				if (!varDeclaration.type.toString().equals("STRING"))
					throw new CompileException(ErrorType.INVALID_TYPE, varDeclaration.identifier.getToken().getPosition(), "Type mismatch when assigning STRING to " + varDeclaration.type.toString());
				break;
			}
		}
		
		varDeclaration.type.acceptVisitor(this);
		if (varDeclaration.construction == ast.nodes.nonTerminals.VarDeclaration.Construction.WITHASSIGNMENT) {
			varDeclaration.varAssignment.acceptVisitor(this);
		}
		//varDeclaration.identifier.acceptVisitor(this);
		
		identTable.enter(varDeclaration);
	}

	@Override
	public void visit(WhereExpression whereExpression) throws CompileException {
		whereExpression.visitChildren(this);
	}
	
	private String getArithmeticExpressionType(ArithmeticExpression expr) throws CompileException {
		return getTermType(expr.term);
	}

	private String getExpressionBlockType(ExpressionBlock block) throws CompileException {
		VarDeclaration varDecl;
		
		switch (block.construction) {
			case EXPRESSION:
				switch (block.expression.construction) {
					case ARITHMETICEXPRESSION:
						return getArithmeticExpressionType(block.expression.arithmeticExpression);
					case BOOLEANEXPRESSION:
						return "BOOLEAN";
				}
					
				break;
				
			case IDENTIFIER:
				varDecl = identTable.retrieveVariable(block.identifierArea);
				return varDecl.type.toString();
				
			case IDENTIFIERMETHOD:
				varDecl = identTable.retrieveVariable(block.identifierArea);
			MethodDeclaration methodDecl;
			try {
				methodDecl = identTable.retrieveMethod(varDecl.type, block.identifierMethod.toString());
			} catch (IdentException e) {
				throw new CompileException(e.getErrorType(), block.identifierMethod.getToken().getPosition(), e.getMessage());
			}
				block.identifierMethod.setReturnType(methodDecl.getType().toString());
				return methodDecl.getType().toString();
				
			case LITERAL:
				switch (block.literal.construction) {
					case BOOLEAN:
						return "BOOLEAN";
					case NUMBER:
						return "NUMBER";
					case STRING:
						return "STRING";
				}
				
				break;
		}
		
		return "UNKNOWN TYPE";
	}
	
	private String getFactorType(Factor factor) throws CompileException {
		VarDeclaration varDecl;
		
		switch (factor.construction) {
			case ARITHMETICEXPRESSION:
				return getArithmeticExpressionType(factor.arithmeticExpression);
	
			case IDENTIFIER:
				varDecl = identTable.retrieveVariable(factor.identifierArea);
				return varDecl.type.toString();
	
			case IDENTIFIERMETHOD:
				varDecl = identTable.retrieveVariable(factor.identifierArea);
			MethodDeclaration methodDecl;
			try {
				methodDecl = identTable.retrieveMethod(varDecl.type, factor.identifierMethod.toString());
			} catch (IdentException e) {
				throw new CompileException(e.getErrorType(), factor.identifierMethod.getToken().getPosition(), e.getMessage());
			}
				factor.identifierMethod.setReturnType(methodDecl.getType().toString());
				return methodDecl.getType().toString();
				
			case NUMBER:
				return "NUMBER";
		}
		
		return "UNKNOWN TYPE";
	}
	
	private String getTermType(Term term) throws CompileException {
		return getFactorType(term.factor);
	}
	
	private void verifyParameters(MethodDeclaration methodDecl, Parameters parameters, TokenPosition pos) throws CompileException {
		if (methodDecl.getParameterTypes().size() != parameters.expressionBlocks.size())
			throw new CompileException(ErrorType.PARAMETER_COUNT_MISMATCH, pos, "Parameter count mismatch. Got: " + parameters.expressionBlocks.size() + " - Expected: " + methodDecl.getParameterTypes().size());
		
		for (int i = 0; i < methodDecl.getParameterTypes().size(); i++) {
			Type type1 = methodDecl.getParameterTypes().get(i);
			String type2 = getExpressionBlockType(parameters.expressionBlocks.get(i));
			
			if (!type1.toString().equals(type2))
				throw new CompileException(ErrorType.INVALID_TYPE, pos, "Invalid parameter type given to method. Got: " + type2 + " - Expected: " + type1.toString());
		}
	}
}