package codegen;

import java.io.*;

import contextual.exceptions.IdentException;
import syntactic.exceptions.CompileException;
import ast.IAST;
import ast.IASTVisitor;
import ast.nodes.leafs.*;
import ast.nodes.nonTerminals.*;
import ast.nodes.nonTerminals.VarDeclaration.Construction;
import ast.nodes.subNodes.*;

public class CodeGenerator implements ICodeGenerator, IASTVisitor {
	private File outputDirectory;
	private FileWriter activeFileWriter;
	int eventCounter;
	int tabLevel = 0; //Defines numbers of tabs we need on every line
	
	public void generateCode(IAST program, File outputDirectory) throws IOException, CompileException, IdentException {
		File realOutputDirectory = new File(outputDirectory, "kitty");
		
		if (!realOutputDirectory.exists()) {
			realOutputDirectory.mkdirs();
		}
		
		if (realOutputDirectory.isFile()) {
			throw new IOException("A file is named as the same as the directory");
		}
		
		FileOperations.emptyDirectory(realOutputDirectory);
		
		this.outputDirectory = realOutputDirectory;
		
		//Unpack libs into the output directory
		FileOperations.unzip(getClass().getResourceAsStream("/codegen/lib.zip"), realOutputDirectory);
		
		// Traverse the AST		
		program.acceptVisitor(this);		
		
		// Write Main.java
		writeMainFile();
	}

	private void writeMainFile() throws IOException {
		FileWriter writer = new FileWriter(new File(outputDirectory, "Main.java"));
		writeFileHeader(writer);
		
		writer.write("public class Main {\n");
		writer.write("\tpublic static void main(String[] args) {\n");
		writer.write("\t\t//Output all output to nxjconsole.\n");
		writer.write("\t\tRConsole.openBluetooth(1000000);\n");
		writer.write("\t\tSystem.setErr(new PrintStream(RConsole.openOutputStream()));\n");
		writer.write("\t\tSystem.setOut(new PrintStream(RConsole.openOutputStream()));\n\n");
		writer.write("\t\tEventManager em = new EventManager();\n");
		writer.write("\t\t\n");
		
		for (int i = 1; i <= eventCounter; i++) {
			writer.write("\t\tem.add(new Event" + i + "());\n");
		}
		
		writer.write("\t\t\n");
		writer.write("\t\tem.start();\n");
		writer.write("\t}\n");
		writer.write("}\n");
		
		writer.close();
	}
	
	@Override
	public void visit(CoordinatePosition coordinatePosition) {
		try {
			activeFileWriter.write("Coordinate." + coordinatePosition.getToken().getSpelling());
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public void visit(ArithmeticExpression arithmeticExpression) throws CompileException {
		try {
			activeFileWriter.write("(");
			arithmeticExpression.visitChildren(this);
			activeFileWriter.write(")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(BooleanExpression booleanExpression) throws CompileException {
		try {
			boolean stringCompare = false;
			
			activeFileWriter.write("(");
			
			if (booleanExpression.construction == BooleanExpression.Construction.EXPRESSION) {
				if (booleanExpression.expressionBlock1.construction == ExpressionBlock.Construction.IDENTIFIER) {
					if (booleanExpression.expressionBlock1.identifierArea.getDeclaration().type.getToken().getSpelling().equals("STRING")) {
						if (booleanExpression.relationalOperator.getToken().getSpelling().equals("EQUAL TO")) {
							booleanExpression.expressionBlock1.identifierArea.acceptVisitor(this);
							activeFileWriter.write(".equals(");
							stringCompare = true;
						} else if (booleanExpression.relationalOperator.getToken().getSpelling().equals("NOT EQUAL TO")) { 
							activeFileWriter.write("!");
							booleanExpression.expressionBlock1.identifierArea.acceptVisitor(this);
							activeFileWriter.write(".equals(");
							stringCompare = true;
						} else {
							System.err.println("[BooleanExpression] Can not recognize: " + booleanExpression.relationalOperator.getToken().getSpelling());
						}
						
					} else {
						booleanExpression.expressionBlock1.identifierArea.acceptVisitor(this);
						booleanExpression.relationalOperator.acceptVisitor(this);
					}
					booleanExpression.expressionBlock2.visitChildren(this);
					
				} else if (booleanExpression.expressionBlock1.construction == ExpressionBlock.Construction.IDENTIFIERMETHOD) {
					if (booleanExpression.expressionBlock1.identifierMethod.getReturnType().equals("STRING")) {
						if (booleanExpression.relationalOperator.getToken().getSpelling().equals("EQUAL TO")) {
							booleanExpression.expressionBlock1.acceptVisitor(this);
							
						} else if (booleanExpression.relationalOperator.getToken().getSpelling().equals("NOT EQUAL TO")) {
							activeFileWriter.write("!");
							booleanExpression.expressionBlock1.acceptVisitor(this);
							
						} else {
							System.err.println("[BooleanExpression] Can not recognize: " + booleanExpression.relationalOperator.getToken().getSpelling());
						}
						
						activeFileWriter.write(".equals(");
						booleanExpression.expressionBlock2.acceptVisitor(this);
						activeFileWriter.write(")");
					} else {
						booleanExpression.visitChildren(this);
					}
					
				} else if (booleanExpression.expressionBlock1.construction == ExpressionBlock.Construction.LITERAL) {
					if (booleanExpression.expressionBlock1.literal.construction == Literal.Construction.STRING) {
						if (booleanExpression.relationalOperator.getToken().getSpelling().equals("EQUAL TO")) {
							booleanExpression.expressionBlock1.literal.acceptVisitor(this);
							activeFileWriter.write(".equals(");
							stringCompare = true;
							booleanExpression.expressionBlock2.acceptVisitor(this);
						} else if (booleanExpression.relationalOperator.getToken().getSpelling().equals("NOT EQUAL TO")) {
							activeFileWriter.write("!");
							booleanExpression.expressionBlock1.literal.acceptVisitor(this);
							activeFileWriter.write(".equals(");
							stringCompare = true;
							booleanExpression.expressionBlock2.acceptVisitor(this);
						} else {
							System.err.println("[BooleanExpression] Can not recognize: " + booleanExpression.relationalOperator.getToken().getSpelling());
						}
					} else {
						booleanExpression.visitChildren(this);
					}
					
				} else if (booleanExpression.expressionBlock1.construction == ExpressionBlock.Construction.EXPRESSION) {
					booleanExpression.visitChildren(this);
				}
				
			} else { //Construction is LITERAL
				booleanExpression.visitChildren(this);
			}
			
			if (stringCompare) {
				activeFileWriter.write(")");
			}
			
			activeFileWriter.write(")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Command command) throws CompileException {
		try {
			if (command.construction == Command.Construction.METHODCALL) {
				
				if (command.identifierArea.getToken().getSpelling().equals("system")) {
					activeFileWriter.write("SystemMethods");
				} else {
					command.identifierArea.acceptVisitor(this);
				}
				
				activeFileWriter.write(".");
				activeFileWriter.write(command.identifierMethod.getToken().getSpelling());
				command.parameters.acceptVisitor(this);

			} else {
				command.visitChildren(this);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(CommandGoto commandGoto) throws CompileException {
		
		try {
			activeFileWriter.write("Crane.getInstance().goTo(");
			commandGoto.visitChildren(this);
			activeFileWriter.write(")");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(CommandMove commandMove) throws CompileException {

		try {
			commandMove.identifierContainer.acceptVisitor(this);
			activeFileWriter.write(".moveTo(");

			if (commandMove.construction == CommandMove.Construction.MOVETOAREA) {
				commandMove.identifierArea.acceptVisitor(this);
			} else if (commandMove.construction == CommandMove.Construction.MOVETOCOORDINATE) {
				commandMove.coordinate.acceptVisitor(this);
			}
			
			activeFileWriter.write(")");

		} catch (IOException e) {
			e.printStackTrace();
		}

		
	}

	@Override
	public void visit(Coordinates coordinates) {
		boolean firstCoordinate = true;
		StringBuilder sb = new StringBuilder();
		
		sb.append("new Area(");
		for (CoordinatePosition cp : coordinates.coordinatePositions) {
			if (firstCoordinate) {
				firstCoordinate = false;
			} else {
				sb.append(", ");
			}
			sb.append("Coordinate." + cp.getToken().getSpelling());
		}
		sb.append(")");
		
		try {
			activeFileWriter.write(sb.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void visit(EventCommand eventCommand) throws CompileException {
		
		try {
			activeFileWriter.write("public class Event" + eventCounter + " implements IEvent {\n");
			activeFileWriter.write("\tprivate int EVENT_ID = " + eventCounter + ";\n\n");
			activeFileWriter.write("\tpublic int getEventID() {\n");
			activeFileWriter.write("\t\treturn EVENT_ID;\n");
			activeFileWriter.write("\t}\n\n");
			
			activeFileWriter.write("\tpublic boolean run() {\n");
			activeFileWriter.write("\t\t//Event\n");
			if (eventCommand.construction == EventCommand.Construction.INCOORDINATE) {
				activeFileWriter.write("\t\tArea areaToCheck = ");
				eventCommand.coordinates.acceptVisitor(this);
				activeFileWriter.write(";\n\n");
				
				activeFileWriter.write("\t\tif (areaToCheck.getContainers().size() < 1) {\n");
				activeFileWriter.write("\t\t\treturn false;\n");
				activeFileWriter.write("\t\t}\n");
				
				activeFileWriter.write("\t\t");
				eventCommand.type.acceptVisitor(this);
				activeFileWriter.write(" ");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = areaToCheck.getContainers().get(0);\n");
				
			} else if (eventCommand.construction == EventCommand.Construction.INCOORDINATEWHERE) {
				activeFileWriter.write("\t\tArea areaToCheck = ");
				eventCommand.coordinates.acceptVisitor(this);
				activeFileWriter.write(";\n");
				
				activeFileWriter.write("\t\t");
				eventCommand.type.acceptVisitor(this);
				activeFileWriter.write(" ");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = null;\n");

				activeFileWriter.write("\t\tboolean runEvent = false;\n\n");
				
				activeFileWriter.write("\t\tfor (Container con : areaToCheck.getContainers()) {\n");
				activeFileWriter.write("\t\t\t");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = con;\n\n");
				
				activeFileWriter.write("\t\t\tif ");
				eventCommand.whereExpression.acceptVisitor(this);
				activeFileWriter.write(" {\n");
				
				activeFileWriter.write("\t\t\t\trunEvent = true;\n");
				
				activeFileWriter.write("\t\t\t\tbreak;\n");
				activeFileWriter.write("\t\t\t}\n");
				activeFileWriter.write("\t\t}\n\n");	

				activeFileWriter.write("\t\tif (!runEvent) {\n");
				activeFileWriter.write("\t\t\treturn false;\n");
				activeFileWriter.write("\t\t}\n");
				
			} else if (eventCommand.construction == EventCommand.Construction.INAREA) {
				activeFileWriter.write("\t\tArea areaToCheck = ");
				eventCommand.identifierArea.acceptVisitor(this);
				activeFileWriter.write(";\n\n");
				
				activeFileWriter.write("\t\tif (areaToCheck.getContainers().size() < 1) {\n");
				activeFileWriter.write("\t\t\treturn false;\n");
				activeFileWriter.write("\t\t}\n");
				
				activeFileWriter.write("\t\t");
				eventCommand.type.acceptVisitor(this);
				activeFileWriter.write(" ");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = areaToCheck.getContainers().get(0);\n");
				
			} else if (eventCommand.construction == EventCommand.Construction.INAREAWHERE) {
				activeFileWriter.write("\t\tArea areaToCheck = ");
				eventCommand.identifierArea.acceptVisitor(this);
				activeFileWriter.write(";\n");
				
				activeFileWriter.write("\t\t");
				eventCommand.type.acceptVisitor(this);
				activeFileWriter.write(" ");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = null;\n");

				activeFileWriter.write("\t\tboolean runEvent = false;\n\n");
				
				activeFileWriter.write("\t\tfor (Container con : areaToCheck.getContainers()) {\n");
				activeFileWriter.write("\t\t\t");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = con;\n\n");
				
				activeFileWriter.write("\t\t\tif ");
				eventCommand.whereExpression.acceptVisitor(this);
				activeFileWriter.write(" {\n");
				
				activeFileWriter.write("\t\t\t\trunEvent = true;\n");
				
				activeFileWriter.write("\t\t\t\tbreak;\n");
				activeFileWriter.write("\t\t\t}\n");
				activeFileWriter.write("\t\t}\n\n");	

				activeFileWriter.write("\t\tif (!runEvent) {\n");
				activeFileWriter.write("\t\t\treturn false;\n");
				activeFileWriter.write("\t\t}\n");
				
			} else if (eventCommand.construction == EventCommand.Construction.INAREAMETHODCALL) {
				activeFileWriter.write("\t\tArea areaToCheck = ");
				
				if (eventCommand.identifierArea.getToken().getSpelling().equals("system")) {
					activeFileWriter.write("SystemMethods");
				} else {
					eventCommand.identifierArea.acceptVisitor(this);
				}
				
				activeFileWriter.write(".");
				activeFileWriter.write(eventCommand.identifierMethod.getToken().getSpelling());
				eventCommand.parameters.acceptVisitor(this);
				activeFileWriter.write(";\n\n");
				
				activeFileWriter.write("\t\tif (areaToCheck.getContainers().size() < 1) {\n");
				activeFileWriter.write("\t\t\treturn false;\n");
				activeFileWriter.write("\t\t}\n");
				
				activeFileWriter.write("\t\t");
				eventCommand.type.acceptVisitor(this);
				activeFileWriter.write(" ");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = areaToCheck.getContainers().get(0);\n");
				
			} else if (eventCommand.construction == EventCommand.Construction.INAREAMETHODCALLWHERE) {
				activeFileWriter.write("\t\tArea areaToCheck = ");
				
				if (eventCommand.identifierArea.getToken().getSpelling().equals("system")) {
					activeFileWriter.write("SystemMethods");
				} else {
					eventCommand.identifierArea.acceptVisitor(this);
				}
				
				activeFileWriter.write(".");
				activeFileWriter.write(eventCommand.identifierMethod.getToken().getSpelling());
				eventCommand.parameters.acceptVisitor(this);
				activeFileWriter.write(";\n");
				
				activeFileWriter.write("\t\t");
				eventCommand.type.acceptVisitor(this);
				activeFileWriter.write(" ");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = null;\n");

				activeFileWriter.write("\t\tboolean runEvent = false;\n\n");
				
				activeFileWriter.write("\t\tfor (Container con : areaToCheck.getContainers()) {\n");
				activeFileWriter.write("\t\t\t");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = con;\n\n");
				
				activeFileWriter.write("\t\t\tif ");
				eventCommand.whereExpression.acceptVisitor(this);
				activeFileWriter.write(" {\n");
				
				activeFileWriter.write("\t\t\t\trunEvent = true;\n");
				
				activeFileWriter.write("\t\t\t\tbreak;\n");
				activeFileWriter.write("\t\t\t}\n");
				activeFileWriter.write("\t\t}\n\n");	

				activeFileWriter.write("\t\tif (!runEvent) {\n");
				activeFileWriter.write("\t\t\treturn false;\n");
				activeFileWriter.write("\t\t}\n");
				
			} else if (eventCommand.construction == EventCommand.Construction.WHERE) {
				activeFileWriter.write("\t\tArea areaToCheck = new Area(Coordinate.allCordinates);\n");
				
				activeFileWriter.write("\t\t");
				eventCommand.type.acceptVisitor(this);
				activeFileWriter.write(" ");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = null;\n");

				activeFileWriter.write("\t\tboolean runEvent = false;\n\n");
				
				activeFileWriter.write("\t\tfor (Container con : areaToCheck.getContainers()) {\n");
				activeFileWriter.write("\t\t\t");
				eventCommand.identifierEvent.acceptVisitor(this);
				activeFileWriter.write(" = con;\n\n");
				
				activeFileWriter.write("\t\t\tif ");
				eventCommand.whereExpression.acceptVisitor(this);
				activeFileWriter.write(" {\n");
				
				activeFileWriter.write("\t\t\t\trunEvent = true;\n");
				
				activeFileWriter.write("\t\t\t\tbreak;\n");
				activeFileWriter.write("\t\t\t}\n");
				activeFileWriter.write("\t\t}\n\n");	

				activeFileWriter.write("\t\tif (!runEvent) {\n");
				activeFileWriter.write("\t\t\treturn false;\n");
				activeFileWriter.write("\t\t}\n");
				
			} else {
				System.err.println("[EventCommand] Can not recognize: " + eventCommand.construction);
			}
			
			activeFileWriter.write("\n\t\t//Variables\n");
			for (VarDeclaration decl : eventCommand.varDeclarations) {
				activeFileWriter.write("\t\t");
				decl.acceptVisitor(this);
				activeFileWriter.write(";\n");
			}
			
			activeFileWriter.write("\n\t\t//Commands\n");
			for (Command com : eventCommand.commands) {
				tabLevel = 2;
				activeFileWriter.write(tabs());
				com.acceptVisitor(this);
				
				if (com.construction != Command.Construction.IFSTATEMENT) {
					activeFileWriter.write(";\n");
				}
			}
			
			activeFileWriter.write("\n\t\treturn true;\n");
			activeFileWriter.write("\t}\n");
			activeFileWriter.write("}");
			
			//eventCommand.visitChildren(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Expression expression) throws CompileException {
		expression.visitChildren(this);
	}

	@Override
	public void visit(ExpressionBlock expressionBlock) throws CompileException {
		
		if (expressionBlock.isNegative) {
			try {
				activeFileWriter.write("-");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (expressionBlock.construction == ExpressionBlock.Construction.IDENTIFIERMETHOD) {
			try {
				if (expressionBlock.identifierArea.getToken().getSpelling().equals("system")) {
					activeFileWriter.write("SystemMethods");
				} else {
					expressionBlock.identifierArea.acceptVisitor(this);
				}
				activeFileWriter.write(".");
				activeFileWriter.write(expressionBlock.identifierMethod.getToken().getSpelling());
				expressionBlock.parameters.acceptVisitor(this);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			expressionBlock.visitChildren(this);	
		}
	}

	@Override
	public void visit(Factor factor) throws CompileException {
		
		if (factor.isNegative) {
			try {
				activeFileWriter.write("-");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		if (factor.construction == Factor.Construction.IDENTIFIERMETHOD) {
			try {
				factor.identifierArea.acceptVisitor(this);
				activeFileWriter.write(".");
				activeFileWriter.write(factor.identifierMethod.getToken().getSpelling());
				factor.parameters.acceptVisitor(this);
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		} else {
			factor.visitChildren(this);
		}
	}

	@Override
	public void visit(IfStatement ifStatement) throws CompileException {
		
		try {
			activeFileWriter.write("if ");
			ifStatement.booleanExpressionIf.acceptVisitor(this);
			activeFileWriter.write(" {\n");
			tabLevel++;

			for (Command com : ifStatement.commandIfs) {
				activeFileWriter.write(tabs());
				com.acceptVisitor(this);
				if (com.construction != Command.Construction.IFSTATEMENT) {
					activeFileWriter.write(";\n");
				}
			}
			
			if (ifStatement.construction == IfStatement.Construction.IFELSEIF || ifStatement.construction == IfStatement.Construction.IFELSEIFELSE) {
				for (ElseIfBlock elseIfBlock : ifStatement.elseIfBlocks) {
					elseIfBlock.acceptVisitor(this);
				}
			}
			
			if (ifStatement.construction == IfStatement.Construction.IFELSE || ifStatement.construction == IfStatement.Construction.IFELSEIFELSE) {
				tabLevel--;
				activeFileWriter.write(tabs() + "} else {\n");
				tabLevel++;
				for (Command com : ifStatement.commandElses) {
					activeFileWriter.write(tabs());
					com.acceptVisitor(this);
					
					if (com.construction != Command.Construction.IFSTATEMENT) {
						activeFileWriter.write(";\n");
					}
				}
			}
			
			tabLevel--;
			activeFileWriter.write(tabs() + "}\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Literal literal) {
		literal.visitChildren(this);
	}

	@Override
	public void visit(Parameters parameters) {
		try {
			activeFileWriter.write("(");

			boolean moreThanOneParameter = false;
			for (ExpressionBlock eb : parameters.expressionBlocks) {
				if (moreThanOneParameter) {
					activeFileWriter.write(", ");
				}

				eb.visitChildren(this);
				moreThanOneParameter = true;
			}

			activeFileWriter.write(")");
		} catch (CompileException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(VarAssignment varAssignment) throws CompileException {
		varAssignment.visitChildren(this);
	}

	@Override
	public void visit(VarAssignmentCommand varAssignmentCommand) throws CompileException {
		try {
			varAssignmentCommand.identifier.acceptVisitor(this);
			activeFileWriter.write(" = ");
			varAssignmentCommand.varAssignment.acceptVisitor(this);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(VarDeclaration varDeclaration) throws CompileException {
		try {
			varDeclaration.type.acceptVisitor(this);
			activeFileWriter.write(" _" + varDeclaration.identifier.toString());
			
			if (varDeclaration.construction == Construction.WITHASSIGNMENT) {
				activeFileWriter.write(" = ");
				
				switch (varDeclaration.varAssignment.construction) {
					case EXPRESSION:
						varDeclaration.varAssignment.expression.visitChildren(this);
						break;
					case STRING:
						varDeclaration.varAssignment.visitChildren(this);
						break;
					case COORDINATES:
						varDeclaration.varAssignment.visitChildren(this);
						break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Program program) throws CompileException, IdentException {
		// Write StaticVariables.java
		File staticVariablesFile = new File(outputDirectory, "StaticVariables.java");
		try {
			activeFileWriter = new FileWriter(staticVariablesFile);
			
			writeFileHeader(activeFileWriter);
			activeFileWriter.write("public class StaticVariables {\n");
			
			for (VarDeclaration decl : program.varDeclarations) {
				activeFileWriter.write("\tpublic static ");
				decl.acceptVisitor(this);
				activeFileWriter.write(";\n");
			}
			activeFileWriter.write("}");
			
			activeFileWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		// Write EventCommands to individual class files
		eventCounter = 0;
		for (EventCommand event : program.eventCommands) {
			eventCounter++;
			File classFile = new File(outputDirectory, "Event" + eventCounter + ".java");
			
			try {
				activeFileWriter = new FileWriter(classFile);
				writeFileHeader(activeFileWriter);
				
				event.acceptVisitor(this);
				
				activeFileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void writeFileHeader(FileWriter writer) throws IOException {
		writer.write("package kitty;\n");
		writer.write("import kitty.lib.*;\n");
		writer.write("import lejos.nxt.comm.*;\n");
		writer.write("import java.io.*;\n");
		writer.write("\n");
	}
	
	@Override
	public void visit(WhereExpression whereExpression) throws CompileException {
		whereExpression.visitChildren(this);
	}

	@Override
	public void visit(BooleanLiteral booleanAST) {
		try {
			if (booleanAST.getToken().getSpelling().equals("TRUE")) {
				activeFileWriter.write("true");
			} else if (booleanAST.getToken().getSpelling().equals("FALSE")) {
				activeFileWriter.write("false");
			} else {
				System.err.println("[BooleanLiteral] Can not recognize: " + booleanAST.getToken().getSpelling());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Coordinate coordinate) throws CompileException {
		coordinate.visitChildren(this);
	}

	@Override
	public void visit(Identifier identifier) throws CompileException {
		try {
			if (identifier.getToken().getSpelling().equals("system")) {
				activeFileWriter.write("SystemMethods");
			} else {
				if (identifier.getIsLocalIdentifier()) {
					activeFileWriter.write("_" + identifier.getToken().getSpelling());
				} else {
					activeFileWriter.write("StaticVariables._" + identifier.getToken().getSpelling());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(RelationalOperator logicalOperator) {
		try {
			String operation = logicalOperator.getToken().getSpelling();
			
			if (operation.equals("EQUAL TO")) {
				activeFileWriter.write("==");
			} else if (operation.equals("NOT EQUAL TO")) {
				activeFileWriter.write("!=");
			} else if (operation.equals("LESS THAN")) {
				activeFileWriter.write("<");
			} else if (operation.equals("LESS THAN OR EQUAL TO")) {
				activeFileWriter.write("<=");
			} else if (operation.equals("GREATER THAN")) {
				activeFileWriter.write(">");
			} else if (operation.equals("GREATER THAN OR EQUAL TO")) {
				activeFileWriter.write(">=");
			} else {
				System.err.println("[RelationalOperator] Can not recognize: " + operation);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(MultiplyDivideOperator multiplyDivideOperator) {
		try {
			activeFileWriter.write(multiplyDivideOperator.getToken().getSpelling());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(NumberLiteral numberAST) {
		try {
			activeFileWriter.write(numberAST.getToken().getSpelling());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void visit(PlusMinusOperator plusMinusOperator) {
		try {
			activeFileWriter.write(plusMinusOperator.getToken().getSpelling());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(StringLiteral stringAST) {
		try {
			activeFileWriter.write("\"" + stringAST.getToken().getSpelling() + "\"");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(Type type) {
		try {
			String spelling = type.getToken().getSpelling();

			//Convert from KittyCranium type to Java type
			if (spelling.equals("NUMBER"))
				activeFileWriter.write("double");
			else if (spelling.equals("BOOLEAN"))
				activeFileWriter.write("boolean");
			else if (spelling.equals("STRING"))
				activeFileWriter.write("String");
			else if (spelling.equals("AREA"))
				activeFileWriter.write("Area");
			else if (spelling.equals("CONTAINER"))
				activeFileWriter.write("Container");
			else
				System.err.println("[type] Can not recognize: " + spelling);
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(FactorBlock factorBlock) throws CompileException {
		factorBlock.visitChildren(this);		
	}

	@Override
	public void visit(ElseIfBlock elseIfBlock) throws CompileException {
		
		try {
			tabLevel--;
			activeFileWriter.write(tabs() + "} else if ");
			elseIfBlock.booleanExpression.acceptVisitor(this);
			activeFileWriter.write(" {\n");
			tabLevel++;
			
			for (Command com : elseIfBlock.commands) {
				activeFileWriter.write(tabs());
				com.acceptVisitor(this);
				activeFileWriter.write(";\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void visit(TermBlock termBlock) throws CompileException {
		termBlock.visitChildren(this);
	}

	@Override
	public void visit(Term term) throws CompileException {
		term.visitChildren(this);
	}
	
	//This metod is used to prints a variable numbers of tabs (only used places this is needed)
	private String tabs() {
		StringBuilder sb = new StringBuilder();

		for (int i = 0; i < tabLevel; i++) {
			sb.append("\t");
		}
		return sb.toString();

	}
}