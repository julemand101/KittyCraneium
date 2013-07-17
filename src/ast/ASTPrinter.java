package ast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import contextual.exceptions.IdentException;

import syntactic.exceptions.CompileException;

import ast.nodes.leafs.BooleanLiteral;
import ast.nodes.leafs.CoordinatePosition;
import ast.nodes.leafs.Identifier;
import ast.nodes.leafs.RelationalOperator;
import ast.nodes.leafs.MultiplyDivideOperator;
import ast.nodes.leafs.NumberLiteral;
import ast.nodes.leafs.PlusMinusOperator;
import ast.nodes.leafs.StringLiteral;
import ast.nodes.leafs.Type;
import ast.nodes.nonTerminals.ArithmeticExpression;
import ast.nodes.nonTerminals.BooleanExpression;
import ast.nodes.nonTerminals.Command;
import ast.nodes.nonTerminals.CommandGoto;
import ast.nodes.nonTerminals.CommandMove;
import ast.nodes.nonTerminals.Coordinate;
import ast.nodes.nonTerminals.Coordinates;
import ast.nodes.nonTerminals.EventCommand;
import ast.nodes.nonTerminals.Expression;
import ast.nodes.nonTerminals.ExpressionBlock;
import ast.nodes.nonTerminals.Factor;
import ast.nodes.nonTerminals.IfStatement;
import ast.nodes.nonTerminals.Literal;
import ast.nodes.nonTerminals.Parameters;
import ast.nodes.nonTerminals.Program;
import ast.nodes.nonTerminals.Term;
import ast.nodes.nonTerminals.VarAssignment;
import ast.nodes.nonTerminals.VarAssignmentCommand;
import ast.nodes.nonTerminals.VarDeclaration;
import ast.nodes.nonTerminals.WhereExpression;
import ast.nodes.nonTerminals.BooleanExpression.Construction;
import ast.nodes.subNodes.FactorBlock;
import ast.nodes.subNodes.ElseIfBlock;
import ast.nodes.subNodes.TermBlock;

public final class ASTPrinter implements IASTVisitor {

	private FileWriter writer;
	private BufferedWriter output;
	private ArrayList<Object> leafs = new ArrayList<Object>();	
	
	public final void print(IAST ast, File outputFile) {
		try {
			writer = new FileWriter(outputFile);
			output = new BufferedWriter(writer);
			
			output.write("digraph AST {\n");			
			output.write("size=\"50,50\";\n");
			output.write("node [shape=box, style=filled, color=\"#000000\", fillcolor=\"#f5f5f5\", fontname=Arial, fontsize=18;\n");
			
			try {
				ast.acceptVisitor(this);
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			output.write("subgraph{\n");
			output.write("rank = same;\n");
			
			for (Object leaf : leafs) {
				output.write(leaf.hashCode() + ";\n");
			}
			
			output.write("}\n");
			
			output.write("}\n");
			output.close();
		} catch (IOException e) {
			System.err.println("Error printing AST: " + e.getMessage());
		}
	}
	
	@Override
	public void visit(ArithmeticExpression expr) {
		try {
			output.write(expr.hashCode() + " [label=\"ArithmeticExpression\"];\n");
			output.write(expr.hashCode() + " -> " + expr.term.hashCode() + ";\n");
			if (expr.construction == ArithmeticExpression.Construction.WITHOPERATOR) {
				for (TermBlock termBlock : expr.termBlocks) {
					output.write(expr.hashCode() + " -> " + termBlock.hashCode() + ";\n");
				}
			}		
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			expr.visitChildren(this);
		} catch (Exception e) {
			e.printStackTrace();
		}		
	}

	@Override
	public void visit(BooleanExpression expr) throws CompileException {
		try {
			output.write(expr.hashCode() + " [label=\"BooleanExpression\"];\n");
			output.write(expr.hashCode() + " -> " + expr.expressionBlock1.hashCode() + ";\n");
			
			if (expr.construction == Construction.EXPRESSION) {
				output.write(expr.hashCode() + " -> " + expr.relationalOperator.hashCode() + ";\n");
				output.write(expr.hashCode() + " -> " + expr.expressionBlock2.hashCode() + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		expr.visitChildren(this);
	}

	@Override
	public void visit(Command command) throws CompileException {
		try {
			output.write(command.hashCode() + " [label=\"Command\"];\n");
			
			if (command.construction == Command.Construction.COMMANDMOVE) {
				output.write(command.hashCode() + " -> " + command.commandMove.hashCode() + ";\n");
			} else if (command.construction == Command.Construction.COMMANDGOTO) {
				output.write(command.hashCode() + " -> " + command.commandGoto.hashCode() + ";\n");
			} else if (command.construction == Command.Construction.VARASSIGNMENTCOMMAND) {
				output.write(command.hashCode() + " -> " + command.varAssignmentCommand.hashCode() + ";\n");
			} else if (command.construction == Command.Construction.METHODCALL) {
				output.write(command.hashCode() + " -> " + command.identifierArea.hashCode() + ";\n");
				output.write(command.hashCode() + " -> " + command.identifierMethod.hashCode() + ";\n");
				output.write(command.hashCode() + " -> " + command.parameters.hashCode() + ";\n");								
			} else if (command.construction == Command.Construction.IFSTATEMENT) {
				output.write(command.hashCode() + " -> " + command.ifStatement.hashCode() + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		command.visitChildren(this);
	}

	@Override
	public void visit(CommandGoto commandGoto) throws CompileException {
		try {
			output.write(commandGoto.hashCode() + " [label=\"GotoCommand\"];\n");
			if (commandGoto.construction == CommandGoto.Construction.GOTOAREA)
				output.write(commandGoto.hashCode() + " -> " + commandGoto.identifierArea.hashCode() + ";\n");
			else if (commandGoto.construction == CommandGoto.Construction.GOTOCOORDINATE)
				output.write(commandGoto.hashCode() + " -> " + commandGoto.coordinate.hashCode() + ";\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		commandGoto.visitChildren(this);
	}

	@Override
	public void visit(CommandMove commandMove) throws CompileException {
		try {
			output.write(commandMove.hashCode() + " [label=\"MoveCommand\"];\n");
			output.write(commandMove.hashCode() + " -> " + commandMove.identifierContainer.hashCode() + ";\n");
			if (commandMove.construction == CommandMove.Construction.MOVETOAREA)
				output.write(commandMove.hashCode() + " -> " + commandMove.identifierArea.hashCode() + ";\n");
			else if (commandMove.construction == CommandMove.Construction.MOVETOCOORDINATE)
				output.write(commandMove.hashCode() + " -> " + commandMove.coordinate.hashCode() + ";\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		commandMove.visitChildren(this);
	}

	@Override
	public void visit(Coordinates coordinates) throws CompileException {
		try {
			output.write(coordinates.hashCode() + " [label=\"Coordinates\"];\n");
			for (CoordinatePosition coordinatePosition : coordinates.coordinatePositions) {
				output.write(coordinates.hashCode() + " -> " + coordinatePosition.hashCode() + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		coordinates.visitChildren(this);
	}

	@Override
	public void visit(EventCommand eventCommand) throws CompileException {
		try {
			output.write(eventCommand.hashCode() + " [label=\"EventCommand\"];\n");
			
			output.write(eventCommand.hashCode() + " -> " + eventCommand.type.hashCode() + ";\n");
			output.write(eventCommand.hashCode() + " -> " + eventCommand.identifierEvent.hashCode() + ";\n");
			
			if (eventCommand.construction == EventCommand.Construction.INCOORDINATE) {
				output.write(eventCommand.hashCode() + " -> " + eventCommand.coordinates.hashCode() + ";\n");
			}
			else if (eventCommand.construction == EventCommand.Construction.INCOORDINATEWHERE) {
				output.write(eventCommand.hashCode() + " -> " + eventCommand.coordinates.hashCode() + ";\n");
				output.write(eventCommand.hashCode() + " -> " + eventCommand.whereExpression.hashCode() + ";\n");
			}
			else if (eventCommand.construction == EventCommand.Construction.INAREA) {
				output.write(eventCommand.hashCode() + " -> " + eventCommand.identifierArea.hashCode() + ";\n");
			}
			else if (eventCommand.construction == EventCommand.Construction.INAREAWHERE) {
				output.write(eventCommand.hashCode() + " -> " + eventCommand.identifierArea.hashCode() + ";\n");
				output.write(eventCommand.hashCode() + " -> " + eventCommand.whereExpression.hashCode() + ";\n");
			}
			else if (eventCommand.construction == EventCommand.Construction.INAREAMETHODCALL) {
				output.write(eventCommand.hashCode() + " -> " + eventCommand.identifierArea.hashCode() + ";\n");
				output.write(eventCommand.hashCode() + " -> " + eventCommand.identifierMethod.hashCode() + ";\n");
				output.write(eventCommand.hashCode() + " -> " + eventCommand.parameters.hashCode() + ";\n");
			}
			else if (eventCommand.construction == EventCommand.Construction.INAREAMETHODCALLWHERE) {
				output.write(eventCommand.hashCode() + " -> " + eventCommand.identifierArea.hashCode() + ";\n");
				output.write(eventCommand.hashCode() + " -> " + eventCommand.identifierMethod.hashCode() + ";\n");
				output.write(eventCommand.hashCode() + " -> " + eventCommand.parameters.hashCode() + ";\n");				
				output.write(eventCommand.hashCode() + " -> " + eventCommand.whereExpression.hashCode() + ";\n");
			}
			
			if (eventCommand.varDeclarations.hasItem())
				for (VarDeclaration varDeclaration : eventCommand.varDeclarations) {
					output.write(eventCommand.hashCode() + " -> " + varDeclaration.hashCode() + ";\n");
				}
			if (eventCommand.commands.hasItem())
				for (Command command : eventCommand.commands) {
					output.write(eventCommand.hashCode() + " -> " + command.hashCode() + ";\n");
				}
		} catch (IOException e) {
			e.printStackTrace();
		}
	
		eventCommand.visitChildren(this);
	}

	@Override
	public void visit(Expression expression) {
		try {
			output.write(expression.hashCode() + " [label=\"Expression\"];\n");
			if (expression.construction == Expression.Construction.ARITHMETICEXPRESSION) {
				output.write(expression.hashCode() + " -> " + expression.arithmeticExpression.hashCode() + ";\n");
			}
			else if (expression.construction == Expression.Construction.BOOLEANEXPRESSION) {
				output.write(expression.hashCode() + " -> " + expression.booleanExpression.hashCode() + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		try {
			expression.visitChildren(this);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(ExpressionBlock expressionBlock) throws CompileException {
		try {
			output.write(expressionBlock.hashCode() + " [label=\"ExpressionBlock\"];\n");			
			if (expressionBlock.construction == ExpressionBlock.Construction.IDENTIFIER) {
				output.write(expressionBlock.hashCode() + " -> " + expressionBlock.identifierArea.hashCode() + ";\n");
			} 
			else if (expressionBlock.construction == ExpressionBlock.Construction.IDENTIFIERMETHOD) {
				output.write(expressionBlock.hashCode() + " -> " + expressionBlock.identifierArea.hashCode() + ";\n");
				output.write(expressionBlock.hashCode() + " -> " + expressionBlock.identifierMethod.hashCode() + ";\n");
				output.write(expressionBlock.hashCode() + " -> " + expressionBlock.parameters.hashCode() + ";\n");
			}
			else if (expressionBlock.construction == ExpressionBlock.Construction.LITERAL) {
				output.write(expressionBlock.hashCode() + " -> " + expressionBlock.literal.hashCode() + ";\n");
			}
			else if (expressionBlock.construction == ExpressionBlock.Construction.EXPRESSION) {
				output.write(expressionBlock.hashCode() + " -> " + expressionBlock.expression.hashCode() + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		expressionBlock.visitChildren(this);
	}

	@Override
	public void visit(Factor factor) throws CompileException {
		try {
			output.write(factor.hashCode() + " [label=\"Factor\"];\n");			
			if (factor.construction == Factor.Construction.ARITHMETICEXPRESSION) {
				output.write(factor.hashCode() + " -> " + factor.arithmeticExpression.hashCode() + ";\n");				
			} 
			else if (factor.construction == Factor.Construction.NUMBER) {
				output.write(factor.hashCode() + " -> " + factor.number.hashCode() + ";\n");				
			}
			else if (factor.construction == Factor.Construction.IDENTIFIER) {
				output.write(factor.hashCode() + " -> " + factor.identifierArea.hashCode() + ";\n");				
			}
			else if (factor.construction == Factor.Construction.IDENTIFIERMETHOD) {
				output.write(factor.hashCode() + " -> " + factor.identifierArea.hashCode() + ";\n");
				output.write(factor.hashCode() + " -> " + factor.identifierMethod.hashCode() + ";\n");
				output.write(factor.hashCode() + " -> " + factor.parameters.hashCode() + ";\n");
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		factor.visitChildren(this);
	}

	@Override
	public void visit(IfStatement ifStatement) throws CompileException {
		try {
			output.write(ifStatement.hashCode() + " [label=\"IfStatement\"];\n");			
			if (ifStatement.construction == IfStatement.Construction.IF) {
				output.write(ifStatement.hashCode() + " -> " + ifStatement.booleanExpressionIf.hashCode() + ";\n");
				for (Command commandIf : ifStatement.commandIfs) {
					output.write(ifStatement.hashCode() + " -> " + commandIf.hashCode() + ";\n");
				}
			} 
			else if (ifStatement.construction == IfStatement.Construction.IFELSEIF) {
				output.write(ifStatement.hashCode() + " -> " + ifStatement.booleanExpressionIf.hashCode() + ";\n");
				for (Command commandIf : ifStatement.commandIfs) {
					output.write(ifStatement.hashCode() + " -> " + commandIf.hashCode() + ";\n");
				}
				for (ElseIfBlock elseIfBlock : ifStatement.elseIfBlocks) {
					output.write(ifStatement.hashCode() + " -> " + elseIfBlock.hashCode() + ";\n");
				}				
			}
			else if (ifStatement.construction == IfStatement.Construction.IFELSEIFELSE) {
				output.write(ifStatement.hashCode() + " -> " + ifStatement.booleanExpressionIf.hashCode() + ";\n");
				for (Command commandIf : ifStatement.commandIfs) {
					output.write(ifStatement.hashCode() + " -> " + commandIf.hashCode() + ";\n");
				}
				for (ElseIfBlock elseIfBlock : ifStatement.elseIfBlocks) {
					output.write(ifStatement.hashCode() + " -> " + elseIfBlock.hashCode() + ";\n");
				}
				for (Command commandElse : ifStatement.commandElses) {
					output.write(ifStatement.hashCode() + " -> " + commandElse.hashCode() + ";\n");
				}
			} else if (ifStatement.construction == IfStatement.Construction.IFELSE) {
				output.write(ifStatement.hashCode() + " -> " + ifStatement.booleanExpressionIf.hashCode() + ";\n");
				for (Command commandIf : ifStatement.commandIfs) {
					output.write(ifStatement.hashCode() + " -> " + commandIf.hashCode() + ";\n");
				}
				for (Command commandElse : ifStatement.commandElses) {
					output.write(ifStatement.hashCode() + " -> " + commandElse.hashCode() + ";\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		ifStatement.visitChildren(this);
	}

	@Override
	public void visit(Literal literal) {
		try {
			output.write(literal.hashCode() + " [label=\"Literal\"];\n");		
		
			if (literal.construction == Literal.Construction.NUMBER) {
				output.write(literal.hashCode() + " -> " + literal.number.hashCode() + ";\n");
			} 
			else if (literal.construction == Literal.Construction.STRING) {
				output.write(literal.hashCode() + " -> " + literal.string.hashCode() + ";\n");
			}
			else if (literal.construction == Literal.Construction.BOOLEAN) {
				output.write(literal.hashCode() + " -> " + literal.bool.hashCode() + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}		
		literal.visitChildren(this);
			
	}

	@Override
	public void visit(Parameters parameters) throws CompileException {
		try {
			output.write(parameters.hashCode() + " [label=\"Parameters\"];\n");
			for (ExpressionBlock expBlock : parameters.expressionBlocks) {
				output.write(parameters.hashCode() + " -> " + expBlock.hashCode() + ";\n");
			}			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		parameters.visitChildren(this);		
	}

	@Override
	public void visit(Term term) throws CompileException {
		try {
			output.write(term.hashCode() + " [label=\"Term\"];\n");			
			output.write(term.hashCode() + " -> " + term.factor.hashCode() + ";\n");						
			if (term.construction == Term.Construction.WITHOPERATOR) {
				for (FactorBlock factorBlock : term.factorBlocks) {
					output.write(term.hashCode() + " -> " + factorBlock.hashCode() + ";\n");
				}					
			}				
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		term.visitChildren(this);
	}
	
	@Override
	public void visit(VarAssignment varAssignment) throws CompileException {
		try {
			output.write(varAssignment.hashCode() + " [label=\"VarAssignment\"];\n");
			
			if (varAssignment.construction == VarAssignment.Construction.EXPRESSION) {
				output.write(varAssignment.hashCode() + " -> " + varAssignment.expression.hashCode() + ";\n");
			} if (varAssignment.construction == VarAssignment.Construction.STRING) {
				output.write(varAssignment.hashCode() + " -> " + varAssignment.string.hashCode() + ";\n");
			} else if (varAssignment.construction == VarAssignment.Construction.COORDINATES) {
				output.write(varAssignment.hashCode() + " -> " + varAssignment.coordinates.hashCode() + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		varAssignment.visitChildren(this);
	}

	@Override
	public void visit(VarAssignmentCommand varAssignmentCommand) throws CompileException {
		try {
			output.write(varAssignmentCommand.hashCode() + " [label=\"VarAssignmentCommand\"];\n");			
			output.write(varAssignmentCommand.hashCode() + " -> " + varAssignmentCommand.identifier.hashCode() + ";\n");
			output.write(varAssignmentCommand.hashCode() + " -> " + varAssignmentCommand.varAssignment.hashCode() + ";\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		varAssignmentCommand.visitChildren(this);
	}

	@Override
	public void visit(VarDeclaration varDeclaration) throws CompileException {
		try {
			output.write(varDeclaration.hashCode() + " [label=\"VarDeclaration\"];\n");
			
			output.write(varDeclaration.hashCode() + " -> " + varDeclaration.type.hashCode() + ";\n");
			output.write(varDeclaration.hashCode() + " -> " + varDeclaration.identifier.hashCode() + ";\n");
			
			if (varDeclaration.construction == VarDeclaration.Construction.WITHASSIGNMENT) {
				output.write(varDeclaration.hashCode() + " -> " + varDeclaration.varAssignment.hashCode() + ";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		varDeclaration.visitChildren(this);
	}

	@Override
	public void visit(Program program) throws CompileException, IdentException {
		try {
			output.write(program.hashCode() + " [label=\"Program\"];\n");
			
			if (program.construction == Program.Construction.VARDECLARATIONS || program.construction == Program.Construction.BOTH) {
				for (VarDeclaration varDeclaration : program.varDeclarations) {
					output.write(program.hashCode() + " -> " + varDeclaration.hashCode() + ";\n");
				}	
			}
			if (program.construction == Program.Construction.EVENTCOMMANDS || program.construction == Program.Construction.BOTH) {
				for (EventCommand eventCommand : program.eventCommands) {
					output.write(program.hashCode() + " -> " + eventCommand.hashCode() + ";\n");
				}	
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		program.visitChildren(this);
	}

	@Override
	public void visit(WhereExpression whereExpression) throws CompileException {
		try {
			output.write(whereExpression.hashCode() + " [label=\"WhereExpression\"];\n");			
			output.write(whereExpression.hashCode() + " -> " + whereExpression.booleanExpression.hashCode() + ";\n");
		} catch (IOException e) {
			e.printStackTrace();
		}	
		whereExpression.visitChildren(this);
	}

	@Override
	public void visit(BooleanLiteral literal) {
		try {
			output.write(literal.hashCode() + " [label=\"BooleanLiteral\"];\n");
			output.write(literal.hashCode() + " -> " + literal.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(literal.getToken().hashCode() + " [label=\"" + literal.toString() + "\", shape=plaintext];\n");
			
			leafs.add(literal.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(CoordinatePosition coordinatePosition) {
		try {
			output.write(coordinatePosition.hashCode() + " [label=\"CoordinatePosition\"];\n");
			output.write(coordinatePosition.hashCode() + " -> " + coordinatePosition.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(coordinatePosition.getToken().hashCode() + " [label=\"" + coordinatePosition.toString() + "\", shape=plaintext];\n");
			
			leafs.add(coordinatePosition.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Identifier identifier) {
		try {
			output.write(identifier.hashCode() + " [label=\"Identifier\"];\n");
			output.write(identifier.hashCode() + " -> " + identifier.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(identifier.getToken().hashCode() + " [label=\"" + identifier.toString() + "\", shape=plaintext];\n");
			
			leafs.add(identifier.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(RelationalOperator relationalOperator) {
		try {
			output.write(relationalOperator.hashCode() + " [label=\"RelationalOperator\"];\n");
			output.write(relationalOperator.hashCode() + " -> " + relationalOperator.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(relationalOperator.getToken().hashCode() + " [label=\"" + relationalOperator.toString() + "\", shape=plaintext];\n");
			
			leafs.add(relationalOperator.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(MultiplyDivideOperator operator) {
		try {
			output.write(operator.hashCode() + " [label=\"MultiplyDivideOperator\"];\n");
			output.write(operator.hashCode() + " -> " + operator.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(operator.getToken().hashCode() + " [label=\"" + operator.toString() + "\", shape=plaintext];\n");
			
			leafs.add(operator.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(NumberLiteral literal) {
		try {
			output.write(literal.hashCode() + " [label=\"NumberLiteral\"];\n");
			output.write(literal.hashCode() + " -> " + literal.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(literal.getToken().hashCode() + " [label=\"" + literal.toString() + "\", shape=plaintext];\n");
			
			leafs.add(literal.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(PlusMinusOperator operator) {
		try {
			output.write(operator.hashCode() + " [label=\"MultiplyDivideOperator\"];\n");
			output.write(operator.hashCode() + " -> " + operator.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(operator.getToken().hashCode() + " [label=\"" + operator.toString() + "\", shape=plaintext];\n");
			
			leafs.add(operator.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(StringLiteral literal) {
		try {
			output.write(literal.hashCode() + " [label=\"StringLiteral\"];\n");
			output.write(literal.hashCode() + " -> " + literal.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(literal.getToken().hashCode() + " [label=\"" + literal.toString() + "\", shape=plaintext];\n");
			
			leafs.add(literal.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Type type) {
		try {
			output.write(type.hashCode() + " [label=\"Type\"];\n");
			output.write(type.hashCode() + " -> " + type.getToken().hashCode() + " [style=dashed, len=4];\n");
			output.write(type.getToken().hashCode() + " [label=\"" + type.toString() + "\", shape=plaintext];\n");
			
			leafs.add(type.getToken());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(FactorBlock factorBlock) throws CompileException {
		try {
			output.write(factorBlock.hashCode() + " [label=\"FactorBlock\"];\n");
			
			output.write(factorBlock.hashCode() + " -> " + factorBlock.operator.hashCode() + ";\n");
			output.write(factorBlock.hashCode() + " -> " + factorBlock.factor.hashCode() + ";\n");
						
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		factorBlock.visitChildren(this);
	}

	@Override
	public void visit(ElseIfBlock elseIfBlock) throws CompileException {
		try {
			output.write(elseIfBlock.hashCode() + " [label=\"ElseIfBlock\"];\n");
			
			output.write(elseIfBlock.hashCode() + " -> " + elseIfBlock.booleanExpression.hashCode() + ";\n");
			for (Command command : elseIfBlock.commands) {
				output.write(elseIfBlock.hashCode() + " -> " + command.hashCode() + ";\n");
			}
			
						
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		elseIfBlock.visitChildren(this);
		
	}

	@Override
	public void visit(TermBlock termBlock) throws CompileException {
		try {
			output.write(termBlock.hashCode() + " [label=\"TermBlock\"];\n");
			
			output.write(termBlock.hashCode() + " -> " + termBlock.operator.hashCode() + ";\n");
			output.write(termBlock.hashCode() + " -> " + termBlock.term.hashCode() + ";\n");
						
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		termBlock.visitChildren(this);
	}

	@Override
	public void visit(Coordinate coordinate) throws CompileException {
		try {
			output.write(coordinate.hashCode() + " [label=\"Coordinate\"];\n");
			
			output.write(coordinate.hashCode() + " -> " + coordinate.coordinatePosition.hashCode() + ";\n");
						
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		coordinate.visitChildren(this);
		
	}
}