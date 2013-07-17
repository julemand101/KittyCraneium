package syntactic;

public enum TokenKind {
    IDENTIFIER,
    INTLITERAL,
    STRINGLITERAL,
    BOOLLITERAL,       // "TRUE" eller "FALSE"
    TYPE,              // "CONTAINER"
    COORDINATE, 	   // "A1"
    TERMOPERATOR,      // "+", "-"
    FACTOROPERATOR,	   // "/", "*"
    RELATIONALOPERATOR,   // "EQUAL TO","LESS THAN","GREATER THAN","GREATER THAN OR EQUAL TO","LESS THAN OR EQUAL TO"
    SEMICOLON,         // ";"
    LPAREN,            // "("
    RPAREN,            // ")"
    ISBOOLEXP,         // "IS"
    BECOMES,           // "="
    LCRLPARAN,         // "{"
    RCRLPARAN,         // "}"
    LSQBRACKET,		   // "["
    RSQBRACKET,		   // "]"
    COMMA,			   // ","
    IF,                // "IF"
    ELSEIF,            // "ELSE IF"
    ELSE,              // "ELSE"
    MOVE,              // "MOVE"
    TO,                // "TO"
    GOTO,              // "GOTO"
    EVENT,             // "EVENT"
    IN,                // "IN"
    WHERE,             // "WHERE"
    DOT,               // "."
    SET,               // "SET"
    EOT;               // End of text
}
