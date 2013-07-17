package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.Identifier;

public class CommandGoto implements IASTNode {

	public enum Construction { GOTOCOORDINATE, GOTOAREA };
	
	public Construction construction;
	
	public Identifier identifierArea;
	public Coordinate coordinate;
	
	public CommandGoto(Coordinate coordinate) {
		this.coordinate = coordinate;
		
		this.construction = Construction.GOTOCOORDINATE;
	}
	
	public CommandGoto(Identifier identifierArea) {
		this.identifierArea = identifierArea;
				
		this.construction = Construction.GOTOAREA;
	}
	
	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {	
		if (construction == Construction.GOTOCOORDINATE) {
			coordinate.acceptVisitor(visitor);
		} else if (construction == Construction.GOTOAREA) {
			identifierArea.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

}
