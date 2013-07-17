package syntactic;

import ast.ASTList;
import ast.IAST;
import ast.nodes.leafs.*;
import ast.nodes.nonTerminals.*;
import ast.nodes.subNodes.*;
import syntactic.exceptions.*;

public class Parser implements IParser {
	private Token currentToken;
	private TokenList tokens;
	
	
	/*
	 *	Generel parsing methods:
	 */
	private Token accept(TokenKind expectedKind) throws CompileException {
		if (currentToken.getTokenKind() != expectedKind)
			throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Wrong token, got " + currentToken.getTokenKind()+ " ,expected "+expectedKind);        	
		Token previousToken = currentToken;
		acceptIt();
		return previousToken;
	}

	private void acceptIt() {
		if (currentToken.getTokenKind() != TokenKind.EOT)
			currentToken = tokens.nextToken();

	}

	public IAST parse(TokenList tokens) throws CompileException {		
		this.tokens = tokens;
		currentToken = tokens.nextToken();

		return parseProgram();
	}
	
	/*
	 * KittyCranium related parsing methods.
	 */
	
	private ArithmeticExpression parseArithmeticExpression() throws CompileException {
		Term term = parseTerm();
		
		ASTList<TermBlock> termBlocks = new ASTList<TermBlock>();
		while (currentToken.getTokenKind() == TokenKind.TERMOPERATOR) {
			termBlocks.add(new TermBlock(parsePlusMinusOperator(), parseTerm()));
		}

		if (termBlocks.isEmpty())
			return new ArithmeticExpression(term);
		else
			return new ArithmeticExpression(term, termBlocks);
	}

	private BooleanLiteral parseBoolean() throws CompileException {
		return new BooleanLiteral(accept(TokenKind.BOOLLITERAL));    	 
	}

	private BooleanExpression parseBooleanExpression() throws CompileException {
		if (currentToken.getTokenKind() == TokenKind.ISBOOLEXP) {
			accept(TokenKind.ISBOOLEXP);
			return new BooleanExpression(parseExpressionBlock(), parseLogicalOperator(), parseExpressionBlock()); 
		}
		else {
			if (currentToken.getTokenKind() != TokenKind.BOOLLITERAL)
				throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Wrong token. Got: " + currentToken.getTokenKind().toString() + ", expected: BOOLLITERAL.");
			
			return new BooleanExpression(parseExpressionBlock());
		}
	}

	private Command parseCommand() throws CompileException {
		
		Command returnCommand;
		
		switch (currentToken.getTokenKind()) {
		case MOVE:
			returnCommand = new Command(parseCommandMove());
			break;
		case GOTO:
			returnCommand = new Command(parseCommandGoto());
			break;
		case SET:
			returnCommand = new Command(parseVarAssignmentCommand());
			break;
		case IDENTIFIER:
			Identifier identifierArea = parseIdentifier();
			accept(TokenKind.DOT);
			returnCommand = new Command(identifierArea, parseIdentifier(), parseParameters());
			break;
		case IF:
			return new Command(parseIfStatement()); //If doesn't need ';' so it's returned turn here.
		default:
			throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Wrong token in parseCommand, got "+ currentToken.getTokenKind());
		}
		accept(TokenKind.SEMICOLON);
		return returnCommand;
	}

	private CommandGoto parseCommandGoto() throws CompileException {
		accept(TokenKind.GOTO);
		
		switch (currentToken.getTokenKind()) {
		case IDENTIFIER:
			return new CommandGoto(parseIdentifier());
		case LSQBRACKET:
			return new CommandGoto(parseCoordinate());
		default:
			throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Wrong token, in parseMoveCommand, Got "+currentToken.getTokenKind()+".");
		}
	}

	private CommandMove parseCommandMove() throws CompileException {
		accept(TokenKind.MOVE);
		Identifier identifierContainer = parseIdentifier();
		accept(TokenKind.TO);

		switch (currentToken.getTokenKind()) {
		case IDENTIFIER:
			return new CommandMove(identifierContainer, parseIdentifier());
		case LSQBRACKET:
			return new CommandMove(identifierContainer, parseCoordinate());
		default:
			throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Wrong token, in parseMoveCommand, Got "+currentToken.getTokenKind()+".");
		}
	}

	private Coordinate parseCoordinate() throws CompileException {

		accept(TokenKind.LSQBRACKET);
		CoordinatePosition coordinatePosition = parseCoordinatePosition();
		accept(TokenKind.RSQBRACKET);
		return new Coordinate(coordinatePosition);
	}

	private CoordinatePosition parseCoordinatePosition() throws CompileException {
		return new CoordinatePosition(accept(TokenKind.COORDINATE));   
	}

	private Coordinates parseCoordinates() throws CompileException {
		ASTList<CoordinatePosition> coordinates = new ASTList<CoordinatePosition>();

		accept(TokenKind.LSQBRACKET);		
		coordinates.add(parseCoordinatePosition());
		while (currentToken.getTokenKind() == TokenKind.COMMA) {
			acceptIt(); //COMMA
			coordinates.add(parseCoordinatePosition());    		
		}
		accept(TokenKind.RSQBRACKET);

		return new Coordinates(coordinates);
	}

	private EventCommand parseEventCommand() throws CompileException {		
		ASTList<VarDeclaration> varDeclarations = new ASTList<VarDeclaration>();
		ASTList<Command> commands = new ASTList<Command>();
		Coordinates inCoordinates = null;
		WhereExpression whereExpression = null;
		Identifier identifierArea = null;
		Identifier identifierMethod = null;
		Parameters parameters = null;
		
		accept(TokenKind.EVENT);
		Type type = parseType();
		Identifier identifierEvent = parseIdentifier();
		//Does the EVENT contain an IN clause?
		if (currentToken.getTokenKind() == TokenKind.IN) {
			acceptIt(); //accept the IN token

			//Is it IN COORDINATES or an IDENTIFIER?
			if (currentToken.getTokenKind() == TokenKind.LSQBRACKET){
				//its IN COORDINATES        			
				inCoordinates = parseCoordinates();
			} else if (currentToken.getTokenKind() == TokenKind.IDENTIFIER){
				//It's IN an IDENTIFIER or a METHODCALL
				identifierArea = parseIdentifier();
				if (currentToken.getTokenKind() == TokenKind.DOT) {
					//It's a METHODCALL
					acceptIt();
					identifierMethod = parseIdentifier();        				        				
					parameters = parseParameters();	
				}
			}
		}
		//Does the EVENT contain a WHERE clause?
		if (currentToken.getTokenKind() == TokenKind.WHERE) {
			whereExpression = parseWhereExpression();
		}
		//Start parsing variableDeclarations.
		accept(TokenKind.LCRLPARAN);
		
		while(currentToken.getTokenKind() == TokenKind.TYPE)
			varDeclarations.add(parseVarDeclaration());
		
		//Start parsing commands.
		while(	currentToken.getTokenKind() != TokenKind.RCRLPARAN )
			commands.add(parseCommand());
		
		accept(TokenKind.RCRLPARAN);
		
		//Find the correct constructor.
		if (inCoordinates != null) {
			if (whereExpression != null) {
				//IN COORDINATES WHERE
				return new EventCommand(type, identifierEvent, inCoordinates, whereExpression, varDeclarations, commands);
			}
			//IN COORDINATES
			return new EventCommand(type, identifierEvent, inCoordinates, varDeclarations, commands);
		}
		else if (identifierMethod != null) {
			if (whereExpression != null) {
				//IN METHODCALL WHERE
				return new EventCommand(type, identifierEvent, identifierArea, identifierMethod, parameters, whereExpression, varDeclarations, commands);
			}
			//IN METHODCALL
			return new EventCommand(type, identifierEvent, identifierArea, identifierMethod, parameters, varDeclarations, commands);
		}
		else if(identifierArea != null) {
			if (whereExpression != null) {
				//IN IDENTIFIER WHERE
				return new EventCommand(type, identifierEvent, identifierArea, whereExpression, varDeclarations, commands);
			}
			//IN IDENTIFIER
			return new EventCommand(type, identifierEvent, identifierArea, varDeclarations, commands);
		}
		else if (whereExpression != null) {
			return new EventCommand(type, identifierEvent, whereExpression, varDeclarations, commands);
		}
		else {
			//We need either an IN or WHERE or Both...
			throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "parseEvent syntax error, EVENT's needs either an IN or WHERE clause, or Both, got "+ currentToken.getTokenKind());
		}
	}
	private Expression parseExpression() throws CompileException {
		if (currentToken.getTokenKind() == TokenKind.BOOLLITERAL || currentToken.getTokenKind() == TokenKind.ISBOOLEXP) {
			return new Expression(parseBooleanExpression());
		} else {
			return new Expression(parseArithmeticExpression());
		}
	}

	private ExpressionBlock parseExpressionBlock() throws CompileException {
		boolean isNegative = false;
		
		while (true) {
			switch (currentToken.getTokenKind()) {
			case TERMOPERATOR:
				if (currentToken.getSpelling().equals("-")) {
					acceptIt();
					isNegative = true;
					break;
				} else
					throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Unexpected token: " + currentToken.getSpelling());
				
			case IDENTIFIER:
				Identifier identifier = parseIdentifier();
				if (currentToken.getTokenKind() == TokenKind.DOT) {
					acceptIt();
					return new ExpressionBlock(identifier,  parseIdentifier(), parseParameters(), isNegative);
				} else
					return new ExpressionBlock(identifier, isNegative);
			case STRINGLITERAL: case INTLITERAL: case BOOLLITERAL:
				return new ExpressionBlock(parseLiteral(), isNegative);
			case LPAREN:
				acceptIt();
				Expression expression = parseExpression();
				accept(TokenKind.RPAREN);
				
				return new ExpressionBlock(expression, isNegative);
			default:
				throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Wrong token, in parseExpressionBlock: " + currentToken.getTokenKind());
			}
		}
	}

	private Factor parseFactor() throws CompileException {
		boolean isNegative = false;
		
		while (true) {
			switch (currentToken.getTokenKind()) {
			case TERMOPERATOR :
				if (currentToken.getSpelling().equals("-")) {
					acceptIt();
					isNegative = true;
					break;
				} else
					throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Unexpected token: " + currentToken.getSpelling());
			
			case LPAREN:
				acceptIt();
				ArithmeticExpression arithmeticExpression = parseArithmeticExpression();
				accept(TokenKind.RPAREN);
				return new Factor(arithmeticExpression, isNegative);
			
			case INTLITERAL:
				return new Factor(parseNumber(), isNegative);
				
			case IDENTIFIER:
				Identifier identifierArea = parseIdentifier();
				if (currentToken.getTokenKind() == TokenKind.DOT) {
					acceptIt();
					return new Factor(identifierArea, parseIdentifier(), parseParameters(), isNegative);
				} else {
					return new Factor(identifierArea, isNegative);
				}
	
			default:
				throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Wrong token, in parseFactor: " + currentToken.getTokenKind());
			}
		}
	}

	private Identifier parseIdentifier() throws CompileException {
		return new Identifier(accept(TokenKind.IDENTIFIER));    
	}

	private IfStatement parseIfStatement() throws CompileException {
		ASTList<Command> commandIfs = new ASTList<Command>();
		ASTList<ElseIfBlock> elseIfBlocks = new ASTList<ElseIfBlock>();
		ASTList<Command> commandElses = new ASTList<Command>();

		accept(TokenKind.IF);
		accept(TokenKind.LPAREN);
		BooleanExpression booleanExpressionIf = parseBooleanExpression();
		accept(TokenKind.RPAREN);

		accept(TokenKind.LCRLPARAN);
		while(currentToken.getTokenKind() == TokenKind.MOVE 
			||currentToken.getTokenKind() == TokenKind.GOTO
			||currentToken.getTokenKind() == TokenKind.SET
			||currentToken.getTokenKind() == TokenKind.IDENTIFIER
			||currentToken.getTokenKind() == TokenKind.IF) {
			//Its a command
			commandIfs.add(parseCommand());
		}
		accept(TokenKind.RCRLPARAN);
		//We're at the end of the IF clause
		while (currentToken.getTokenKind() == TokenKind.ELSEIF) {
			acceptIt();
			accept(TokenKind.LPAREN);
			BooleanExpression orExpression = parseBooleanExpression();
			accept(TokenKind.RPAREN);
			accept(TokenKind.LCRLPARAN);
			ASTList<Command> commandOrs = new ASTList<Command>();
			while(currentToken.getTokenKind() == TokenKind.MOVE 
				||currentToken.getTokenKind() == TokenKind.GOTO
				||currentToken.getTokenKind() == TokenKind.SET
				||currentToken.getTokenKind() == TokenKind.IDENTIFIER
				||currentToken.getTokenKind() == TokenKind.IF) {
				//Its a command
				commandOrs.add(parseCommand());
			}    		
			elseIfBlocks.add(new ElseIfBlock(orExpression, commandOrs));
			accept(TokenKind.RCRLPARAN);
		}
		if (currentToken.getTokenKind() == TokenKind.ELSE) {
			acceptIt();
			accept(TokenKind.LCRLPARAN);
			while(currentToken.getTokenKind() == TokenKind.MOVE 
				||currentToken.getTokenKind() == TokenKind.GOTO
				||currentToken.getTokenKind() == TokenKind.SET
				||currentToken.getTokenKind() == TokenKind.IDENTIFIER
				||currentToken.getTokenKind() == TokenKind.IF) {
				//Its a command
				commandElses.add(parseCommand());
			}
			accept(TokenKind.RCRLPARAN);
		}

		//Find out what constructor to call
		if (elseIfBlocks.isEmpty() && commandElses.isEmpty()) {
			return new IfStatement(booleanExpressionIf, commandIfs);
		} else if (!elseIfBlocks.isEmpty() && commandElses.isEmpty()) {
			return new IfStatement(booleanExpressionIf, commandIfs, elseIfBlocks, true); //the boolean is garbage required as java uses type erasure
		} else if (elseIfBlocks.isEmpty() && !commandElses.isEmpty()) {
			return new IfStatement(booleanExpressionIf, commandIfs, commandElses);
		} else {
			return new IfStatement(booleanExpressionIf, commandIfs, elseIfBlocks, commandElses);
		}
	}

	private Literal parseLiteral() throws CompileException {
		switch (currentToken.getTokenKind()) {		
		case TERMOPERATOR :
			if (currentToken.getSpelling().equals("-")) {
				acceptIt();				
				return new Literal(parseNumber());
			}				
		case INTLITERAL :
			return new Literal(parseNumber());
		case STRINGLITERAL :
			return new Literal(parseString());
		case BOOLLITERAL :
			return new Literal(parseBoolean());
		default : 
			throw new CompileException(ErrorType.UNEXPECTED_TOKEN, currentToken.getPosition(), "Wrong token, in parseLiteral, got "+currentToken.getTokenKind());
		}
	}

	private RelationalOperator parseLogicalOperator() throws CompileException {
		return new RelationalOperator(accept(TokenKind.RELATIONALOPERATOR));
	}

	private MultiplyDivideOperator parseMultiplyDivideOperator() throws CompileException {
		return new MultiplyDivideOperator(accept(TokenKind.FACTOROPERATOR));	
	}

	private NumberLiteral parseNumber() throws CompileException {
		return new NumberLiteral(accept(TokenKind.INTLITERAL));
	}

	private Parameters parseParameters() throws CompileException {
		ASTList<ExpressionBlock> expressionBlocks = new ASTList<ExpressionBlock>();

		accept(TokenKind.LPAREN);
		
		if (currentToken.getTokenKind() != TokenKind.RPAREN) {
			//There must be parameters - parse them all
			expressionBlocks.add(parseExpressionBlock());
			while (currentToken.getTokenKind() == TokenKind.COMMA) {
				acceptIt();
				expressionBlocks.add(parseExpressionBlock());
			}
		}
		accept(TokenKind.RPAREN);
		return new Parameters(expressionBlocks);
	}

	private PlusMinusOperator parsePlusMinusOperator() throws CompileException {
		return new PlusMinusOperator(accept(TokenKind.TERMOPERATOR));		
	}

	private Program parseProgram() throws CompileException {                
		ASTList<VarDeclaration> varDeclarations = new ASTList<VarDeclaration>();
		ASTList<EventCommand> eventCommands = new ASTList<EventCommand>();

		while (currentToken.getTokenKind() == TokenKind.TYPE)
			varDeclarations.add(parseVarDeclaration());

		while (currentToken.getTokenKind() == TokenKind.EVENT)
			eventCommands.add(parseEventCommand());

		accept(TokenKind.EOT);

		if (!eventCommands.isEmpty() && varDeclarations.isEmpty())
			//only eventCommands
			return new Program(eventCommands, true); //the boolean is garbage required as java uses type erasure
		else if (eventCommands.isEmpty() && !varDeclarations.isEmpty())
			//only varDeclarations
			return new Program(varDeclarations);
		else
			//Both
			return new Program(varDeclarations, eventCommands);
	}

	private StringLiteral parseString() throws CompileException {
		return new StringLiteral(accept(TokenKind.STRINGLITERAL));    
	}

	private Term parseTerm() throws CompileException {
		ASTList<FactorBlock> factorBlocks = new ASTList<FactorBlock>();

		Factor factor = parseFactor();

		while (currentToken.getTokenKind() == TokenKind.FACTOROPERATOR) {
			factorBlocks.add(new FactorBlock( parseMultiplyDivideOperator(), parseFactor()));
		}

		if (factorBlocks.isEmpty())
			return new Term(factor);
		else
			return new Term(factor, factorBlocks);

	}

	private Type parseType() throws CompileException {
		return new Type(accept(TokenKind.TYPE));    	 
	}

	private VarAssignment parseVarAssignment() throws CompileException {
		accept(TokenKind.BECOMES);
		switch (currentToken.getTokenKind()){
		case STRINGLITERAL :
			return new VarAssignment(parseString());
		case LSQBRACKET :
			return new VarAssignment(parseCoordinates());
		default:
			//must be an expression then
			return new VarAssignment(parseExpression());
		}
	}

	private VarAssignmentCommand parseVarAssignmentCommand() throws CompileException {
		accept(TokenKind.SET);
		return new VarAssignmentCommand(parseIdentifier(), parseVarAssignment());
	}

	private VarDeclaration parseVarDeclaration() throws CompileException {
		Type type = parseType();
		Identifier identifier = parseIdentifier();
		if (currentToken.getTokenKind() == TokenKind.BECOMES){
			VarAssignment varAssignment = parseVarAssignment();
			accept(TokenKind.SEMICOLON);
			return new VarDeclaration(type, identifier, varAssignment);
		}
		else {
			accept(TokenKind.SEMICOLON);
			return new VarDeclaration(type, identifier);
		}
	}

	private WhereExpression parseWhereExpression() throws CompileException {
		accept(TokenKind.WHERE);
		return new WhereExpression(parseBooleanExpression());
	}    
}