package kitty.lib;

public class Container {
	private double CID; //Container ID
	private double GID; //Group ID
	private Coordinate position;
	private static int uniq_CID = 0;
	
	public Container(Coordinate position, int GID) {
		this.position = position;
		this.CID = uniq_CID;
		this.GID = GID;
		position.addContainer(this);
		uniq_CID++;
	}
	
	public Coordinate getCoordinate() {
		return position;
	}
	
	public double getCID() {
		return CID;
	}
	
	public double getGID() {
		return GID;
	}
	
	public void setGID(double newGID) {
		this.GID = newGID;
	}
	
	public void moveTo(Coordinate newPosition) {
		if (newPosition.freeSpace() > 0) {
			position.moveContainer(this, newPosition);
			position = newPosition;
		} else {
			SystemMethods.error("Coordinate has not enough space.");
		}

	}
	
	public void moveTo(Area newPosition) {
		if (newPosition.freeSpace() > 0) {
			for (Coordinate coor : newPosition.coordinates) {
				if (coor.freeSpace() > 0) {
					moveTo(coor);
					break;
				}
			}
		} else {
			SystemMethods.error("Area has not enough space.");
		}
	}
	
	public String toString() {
		char coorX = (char) ((char) position.getX() + 'A');
		
		return "Container ID: " + CID + ". Group ID: " + GID + 
		". Coordinate: (" + coorX + position.getY() + ").";
	}
}
