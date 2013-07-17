package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.Identifier;

public class CommandMove implements IASTNode {

	public enum Construction { MOVETOCOORDINATE, MOVETOAREA };
	
	public Construction construction;
	
	public Identifier identifierContainer;
	public Identifier identifierArea;
	public Coordinate coordinate;
	
	
	public CommandMove(Identifier identifierContainer, Coordinate coordinate) {
		this.identifierContainer = identifierContainer;
		this.coordinate = coordinate;
		
		this.construction = Construction.MOVETOCOORDINATE;
	}
	
	public CommandMove(Identifier identifierContainer, Identifier identifierArea) {
		this.identifierContainer = identifierContainer;
		this.identifierArea = identifierArea;
				
		this.construction = Construction.MOVETOAREA;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		identifierContainer.acceptVisitor(visitor);
		if (construction == Construction.MOVETOCOORDINATE) {
			coordinate.acceptVisitor(visitor);
		} else if (construction == Construction.MOVETOAREA) {
			identifierArea.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
