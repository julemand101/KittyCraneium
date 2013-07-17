package syntactic;

import java.util.ArrayList;

import syntactic.exceptions.CompileException;

public class TokenList extends ArrayList<Token> {
	private static final long serialVersionUID = -3867882993544256092L;
	private int currentPos = 0;
	
	/**
	 * Replaces tokens.
	 * 
	 * arg fromIndex: The position of the initial element in the tokenlist to replace	
	 * arg amountOfTokens: The amount of tokens to replace
	 * arg replacedTokenKind: The new tokenkind of the "new" combined token.
	 * @throws CompileException 
	 */
	public void replace(int fromIndex, int amountOfTokens, TokenKind replacedTokenKind) throws CompileException {
		//Get the spelling of the old tokens and combine them to one string
		StringBuilder replacedTokenSpelling = new StringBuilder();
		int i;
		
		for (i = fromIndex; i < fromIndex + amountOfTokens - 1; i++) {
			replacedTokenSpelling.append(this.get(i).getSpelling() + " ");
		}
		
		replacedTokenSpelling.append(this.get(i).getSpelling());
		
		Token firstTokenToReplace = this.get(fromIndex);
		Token tokenToAdd =  new Token(replacedTokenKind, 
									  replacedTokenSpelling.toString(), 
								      firstTokenToReplace.getPosition());

		//Remove the old tokens
		this.removeRange(fromIndex, fromIndex + amountOfTokens);

		//Insert the new, combined token		
		this.add(fromIndex,tokenToAdd);
	}
	
	public Token nextToken() {		
		Token nextToken = this.get(currentPos);
		currentPos++;
		
		return nextToken; 
	}
	
	@Override
	public String toString() {
		StringBuilder returnStr = new StringBuilder();
		
		for (Token token : this) {
			returnStr.append("[");
			
			if (token.getSpelling().length() > 20)
				returnStr.append(token.getSpelling().substring(0, 17) + "...]");
			else {
				returnStr.append(token.getSpelling() + "]");
				
				for (int i = token.getSpelling().length(); i < 20; i++) {
					returnStr.append(" ");
				}
			}
			
			returnStr.append("[" + token.getTokenKind().toString());
			returnStr.append("]\n");
		}
		
		return returnStr.toString();
	}
}