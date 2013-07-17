package syntactic;

                 
import syntactic.exceptions.*;

public class Scanner implements IScanner {
    private char currentChar;
    private TokenKind currentKind;
    private StringBuffer currentSpelling;
    private String sourceCode;
	private int sourceCodeLineNumber = 1;
    private int sourceCodeColumnNumber = 0;
    private int charPosition = 0;
    private TokenList tokens;
    
    public Scanner(String sourceCode) throws CompileException {
    	this.sourceCode = sourceCode.replace("\t", "        ") + "\n\000";
        this.tokens = scan();
    }
    
    public TokenList getTokens() {
        return this.tokens;
    }
    
    public String getSourceCode() {
		return this.sourceCode;
	}
    
    private void readNextCharacter() {
        charPosition++;
        sourceCodeColumnNumber++;
        currentChar = sourceCode.charAt(charPosition);
        
        if (currentChar == '\n') {
            sourceCodeLineNumber++;
            sourceCodeColumnNumber = 0;
        }
    }
    
    private void takeIt() {
        currentSpelling.append(currentChar);
        readNextCharacter();
    }
    
    private boolean isDigit(char ch) {
        return Character.isDigit(ch);
    }
    
    private boolean isLetter(char ch) {
        return Character.isLetter(ch);
    }
    
    private boolean isCapitalized(char ch){
        return Character.isUpperCase(ch);
    }
    
    private boolean isGraphic(char ch) {
        if (ch == '\n')
            return false;
        else
            return true;
    }
    
    //Used to verify graphic does not contain '
    private boolean isChar(char c) {
        if(c == '\'')
            return false;
        else
            return isGraphic(c);
    }
    
    private TokenKind scanToken() throws CompileException {        
        switch(currentChar){
	        case 'a': case 'b': case 'c': case 'd':
	        case 'e': case 'f': case 'g': case 'h':
	        case 'i': case 'j': case 'k': case 'l':
	        case 'm': case 'n': case 'o': case 'p':
	        case 'q': case 'r': case 's': case 't':
	        case 'u': case 'v': case 'w': case 'x':
	        case 'y': case 'z':
	            do {
	                takeIt();
	            } while (isLetter(currentChar) || isDigit(currentChar));
	            
	            return TokenKind.IDENTIFIER;
	            
	        case 'A': case 'B': case 'C': case 'D':
	        case 'E': case 'F': case 'G': case 'H':
	        case 'I': case 'J': case 'K': case 'L':
	        case 'M': case 'N': case 'O': case 'P':
	        case 'Q': case 'R': case 'S': case 'T':
	        case 'U': case 'V': case 'W': case 'X':
	        case 'Y': case 'Z':
	        	takeIt();
	        	if (isDigit(currentChar)) {
	        		takeIt();
	            	return TokenKind.COORDINATE;
	            }
	        	while (isCapitalized(currentChar)) {
	                takeIt();
	        	}
	        	if (Character.isLowerCase(currentChar)) {
	        		TokenPosition position = new TokenPosition(sourceCodeLineNumber, sourceCodeColumnNumber);
	        		throw new CompileException(ErrorType.MISSING_SPACE, position, "Missing space between TYPE and identifer.");
	        	}
	        	
	            return TokenKind.TYPE;
	         
	        case '1': case '2': case '3':
	        case '4': case '5': case '6': 
	        case '7': case '8': case '9':
	            
	            do {
	                takeIt();
	            } while (isDigit(currentChar));
	            return TokenKind.INTLITERAL;
	
	        case '0':
	            takeIt();
	            if (isDigit(currentChar)){
	                throw new CompileException(ErrorType.UNEXPECTED_CHARACTER, 
	                          new TokenPosition(sourceCodeLineNumber, sourceCodeColumnNumber),
	                          "Numbers cannot start with a '0'. Zero can only consist of one digit.");
	            }
	            
	            return TokenKind.INTLITERAL;
	        
	        case '=':
	            takeIt();
	            return  TokenKind.BECOMES;
	            
	        case '\'':
	            
	        	readNextCharacter();
	        	
	            do {
	                takeIt();
	            } while (isChar(currentChar));
	            
	            if (currentChar == '\'')
	            	readNextCharacter();
	            else
	            	throw new CompileException(ErrorType.MISSING_APOSTROPHE, 
	                                          new TokenPosition(sourceCodeLineNumber, sourceCodeColumnNumber),
	                                          "Missing matching apostrophe in string.");
	            
	            return TokenKind.STRINGLITERAL;
	        
	        case '[':
	        	takeIt();
	            return TokenKind.LSQBRACKET;
	                
	        case ']':
	        	takeIt();
	        	return TokenKind.RSQBRACKET;
	        
	        /**
	         * We take only the "obvious" ones, as the other (GREATHER THAN etc) 
	         * needs to be analyzed by the post processor
	         */
	        case '+': case '-':
	            takeIt();
	            return TokenKind.TERMOPERATOR;
	        case '*': case '/':
	        	 takeIt();
	             return TokenKind.FACTOROPERATOR;
	        case ';':
	            takeIt();
	            return TokenKind.SEMICOLON;
	        case ',':
	        	takeIt();
	        	return TokenKind.COMMA;
	        case '(':
	            takeIt();
	            return TokenKind.LPAREN;
	            
	        case ')':
	            takeIt();
	            return TokenKind.RPAREN;
	        
	        case '{':
	            takeIt();
	            return TokenKind.LCRLPARAN;
	            
	        case '}':
	            takeIt();
	            return TokenKind.RCRLPARAN;
	        
	        case '.':
	            takeIt();
	            return TokenKind.DOT;
	        
	        case '\000':
	            return TokenKind.EOT;
	            
	        default:
	            throw new CompileException(ErrorType.INVALID_CHARACTER, 
	                                      new TokenPosition(sourceCodeLineNumber, sourceCodeColumnNumber),
	                                      "Invalid character ("+currentChar+").");
        }
    }
    
    private void scanSeparator() throws CompileException {
        switch(currentChar){
        //Scanning comments
        case '!' : 
            do {
                readNextCharacter();
            } while (isGraphic(currentChar));
            
            if (currentChar == '\n')
                readNextCharacter();
            else
                throw new CompileException(ErrorType.MISSING_NEWLINE, 
                                            new TokenPosition(sourceCodeLineNumber, sourceCodeColumnNumber),
                                            "Missing newline after comment.");
            
            break;
        
        case ' ': case '\n': case '\r':
            readNextCharacter();
            break;
        }
    }
    
    private TokenList scan() throws CompileException {
        if (sourceCode.isEmpty())
        	return new TokenList();
        
    	TokenList tokenList = new TokenList();
        currentChar = this.sourceCode.charAt(0);
        do {
            while(currentChar == '!'
               || currentChar == ' ' 
               || currentChar == '\n'
               || currentChar == '\r') {
            	scanSeparator();
            }
            
            currentSpelling = new StringBuffer("");
            currentKind = scanToken();
            TokenPosition currentPos = new TokenPosition(sourceCodeLineNumber, 
                    sourceCodeColumnNumber - currentSpelling.toString().length());
            
            if (currentKind == TokenKind.IDENTIFIER && currentSpelling.equals("system"))
            	throw new CompileException(ErrorType.RESERVED_KEYWORD, currentPos, "Identifier named 'system', but it is a reserved word.");
            
            if (currentKind == TokenKind.TYPE && currentSpelling.equals("SYSTEM"))
            	throw new CompileException(ErrorType.RESERVED_KEYWORD, currentPos, "Type SYSTEM was declared, but it is a reserved type.");
            
            tokenList.add(new Token(currentKind, 
                                    currentSpelling.toString(),
                                    currentPos));
        } while (currentKind != TokenKind.EOT);
        
        return postProcessor(tokenList);
    }
    
    /**
     *    Post processing of tokens:
     *        > Finds and combines identifiers that are separated by space (eg. ELSE IF) 
     *        > Finds the specific token type of identifiers that could not be recognized
     *        > correctly by the pre-processor.
     */    
    static public TokenList postProcessor(TokenList tokens) throws CompileException {
        //Loop through all tokens, do something when we find a identifier that may have        
        //to be combined with the next identifier(s) (eg. identifiers ELSE and IF)
        for (int i = 0; i < tokens.size(); i++) {
            Token currentToken = tokens.get(i);
            
            if (currentToken.getTokenKind().equals(TokenKind.TYPE)) {
                //Token is a Type and thereby it could be an RELATIONALOPERATOR, hence we have to check it
	            if (currentToken.getSpelling().equals("GREATER") || currentToken.getSpelling().equals("LESS")) {
	                //Current token is GREATER or LESS - check if next are "THAN" or "THAN OR EQUAL TO":
	                if (tokens.get(i+1).getSpelling().equals("THAN")) {
	                    //Next token is an THAN... so far so good. Is this an ...OR EQUAL TO token?
	                    if (tokens.get(i+2).getSpelling().equals("OR") &&
	                        tokens.get(i+3).getSpelling().equals("EQUAL") &&
	                        tokens.get(i+4).getSpelling().equals("TO")){
	                        //this is a (GREATER | LESS) THAN OR EQUALS TO token
	                        tokens.replace(i, 5, TokenKind.RELATIONALOPERATOR);
	                    } else {
	                        //This is not a (GREATER | LESS) THAN OR EQUAL TO - must be a GREATER THAN token then.
	                        tokens.replace(i, 2, TokenKind.RELATIONALOPERATOR);
	                    }
	                }
	            } else if (currentToken.getSpelling().equals("EQUAL")){
	            	//CurrentToken is EQUAL - check if next are "TO".
	            	if (tokens.get(i+1).getTokenKind() == TokenKind.TO){
	            		//Next token is an TO, repace with EQUAL TO relational operator.
	            		tokens.replace(i, 2, TokenKind.RELATIONALOPERATOR);
	            	}
	            } else if(currentToken.getSpelling().equals("NOT")){
	            	//CurrentToken is NOT - check if next are "EQUAL".
	            	if(tokens.get(i+1).getSpelling().equals("EQUAL")){
	            		//CurrentToken is EQUAL - check if next are "TO".
	            		if (tokens.get(i+2).getTokenKind() == TokenKind.TO){
		            		//Next token is an TO, repace with NOT EQUAL TO relational operator.
		            		tokens.replace(i, 3, TokenKind.RELATIONALOPERATOR);
		            	}
	            	}
	            }
            }
        } 
        
        return tokens;
    }
}
