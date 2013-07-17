package syntactic;
import syntactic.exceptions.CompileException;

public class Token {
    private TokenKind kind;
    private String spelling;
    private TokenPosition position;
    
    public Token (TokenKind kind, String spelling, TokenPosition position) throws CompileException{
        this.kind = kind;
        this.spelling = spelling;
        this.position = position;
        
//        if (kind == TokenKind.IDENTIFIER) {
//        	if (this.spelling.equals("system"))
//        		throw new SyntaxException(ErrorType.RESERVED_KEYWORD, this.position, "Identifier named 'system', but it is a reserved word.");
//        }
        
        if (kind == TokenKind.TYPE) {
        	//SYSTEM
        	//if (this.spelling.equals("SYSTEM"))
        	//	throw new SyntaxException(ErrorType.RESERVED_KEYWORD, this.position, "Type SYSTEM was declared, but it is a reserved type."); 
        	
        	//BOOLEAN
            if (this.spelling.equals("TRUE") || this.spelling.equals("FALSE"))
                this.kind = TokenKind.BOOLLITERAL;
            
            //BOOLEXPRESSION
            else if (this.spelling.equals("IS"))
            	this.kind = TokenKind.ISBOOLEXP;
            
            //IF
            else if (this.spelling.equals("IF"))
                this.kind = TokenKind.IF;
                     
            //ELSE
            else if (this.spelling.equals("ELSE"))
                this.kind = TokenKind.ELSE;

            //ELSEIF
            else if (this.spelling.equals("ELSEIF"))
                this.kind = TokenKind.ELSEIF;            
            
            //MOVE
            else if (this.spelling.equals("MOVE"))
            	this.kind = TokenKind.MOVE;
            
            //SET
            else if (this.spelling.equals("SET"))
            	this.kind = TokenKind.SET;
            
            //TO
            else if (this.spelling.equals("TO"))
            	this.kind = TokenKind.TO;
            
            //GOTO
            else if (this.spelling.equals("GOTO"))
            	this.kind = TokenKind.GOTO;
            
            //EVENT
            else if (this.spelling.equals("EVENT"))
            	this.kind = TokenKind.EVENT;
            
            //IN
            else if (this.spelling.equals("IN"))
            	this.kind = TokenKind.IN;
            
            //WHERE
            else if (this.spelling.equals("WHERE"))
            	this.kind = TokenKind.WHERE;
        }
    }
    
    public TokenPosition getPosition(){
        return this.position;
    }
    
    public TokenKind getTokenKind() {
        return kind;
    }
    
    public String getSpelling() {
        return spelling;
    }
    
    @Override
    public String toString() {
    	return "TOKEN\nKIND    :" + this.kind+"\nSPELLING:" + this.spelling + "\nPOSITION:" + position;
    }
}