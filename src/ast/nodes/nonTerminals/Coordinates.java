package ast.nodes.nonTerminals;

import syntactic.exceptions.CompileException;
import ast.ASTList;
import ast.IASTNode;
import ast.IASTVisitor;
import ast.nodes.leafs.CoordinatePosition;

public class Coordinates implements IASTNode {
	public ASTList<CoordinatePosition> coordinatePositions;
	
	public Coordinates(ASTList<CoordinatePosition> coordinatePositions) {
		this.coordinatePositions = coordinatePositions;
	}

	@Override
	public void visitChildren(IASTVisitor visitor) throws CompileException {
		for (CoordinatePosition coordinatePosition : coordinatePositions) {
			coordinatePosition.acceptVisitor(visitor);
		}
	}

	@Override
	public void acceptVisitor(IASTVisitor visitor) throws CompileException {
		visitor.visit(this);
	}
	
	
}
