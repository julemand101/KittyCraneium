package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.CoordinatePosition;

public class Coordinate implements IASTNode {

	public CoordinatePosition coordinatePosition;
	
	public Coordinate(CoordinatePosition coordinatePosition) {
		this.coordinatePosition = coordinatePosition;
	}	

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}

	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		coordinatePosition.acceptVisitor(visitor);
	}

}
