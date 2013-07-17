package kitty.lib;
import java.util.ArrayList;

public class Area {
	ArrayList<Coordinate> coordinates;
	
	@SuppressWarnings("deprecation")
	public Area(Coordinate ... coordinates) {
		this.coordinates = new ArrayList<Coordinate>(coordinates);
	}
	
	public void moveContainersTo(Area newArea) {
		
		/* 
		 * This check should not be necessary but is there to catch the 
		 * error if an area should nonetheless manage to overlap. The 
		 * code can be removed when it is safe to the compiler is done 
		 * correctly.
		*/
		for (Coordinate coor : this.coordinates) {
			for (Coordinate newCoor : newArea.coordinates) {
				if (coor == newCoor) {
					SystemMethods.error("Area Overlap");
				}
			}
		}
		
		//Check for free space to move the containers in old area to the new
		if (this.countContainers() <= newArea.freeSpace()) {
			for (Coordinate oldCoor : this.coordinates) {
				for (Coordinate newCoor : newArea.coordinates) {
					while (newCoor.freeSpace() > 0 && oldCoor.countContainers() > 0) {
						oldCoor.getTopContainer().moveTo(newCoor);
					}

					if (oldCoor.countContainers() == 0) {
						break;
					}
				}
				
				if (this.countContainers() == 0) {
					break;
				}
			}
		} else {
			SystemMethods.error("Area has not enough space");
		}
		
		/*
		 * The original purpose of this method was that it should be possible 
		 * to move an area to another area where they could then overlap. 
		 * This is so later in the language changed so that it is not 
		 * possible to have the area's overlapping and so are most of this 
		 * method unusable. The code has therefore been commented out so 
		 * that it can be activated again if need be.
		 */
		
//		ArrayList<Coordinate> uncommonCoordinates = new ArrayList<Coordinate>();
//		
//		//Calculate coordinates there are NOT common (not both in old and new area)
//		//Reason: We don't need to move containers there are already in the new area
//		for (Coordinate coor : this.coordinates) {
//			boolean common = false;
//			
//			for (Coordinate newCoor : newArea.coordinates) {
//				if (coor == newCoor) {
//					common = true; //The coordinate is common
//					break;
//				}
//			}
//			
//			if (!common) {
//				uncommonCoordinates.add(coor);				
//			}
//		}
//		
//		//Making a new area there only contains the uncommon coordinates
//		Area oldAreaWithoutCommonCoordinates = new Area(uncommonCoordinates.toArray(new Coordinate[0]));
//		
//		//Check for free space to move the containers in old area to the new
//		if (oldAreaWithoutCommonCoordinates.countContainers() <= newArea.freeSpace()) {
//			for (Coordinate oldCoor : oldAreaWithoutCommonCoordinates.coordinates) {
//				while (oldCoor.countContainers() > 0) {
//					for (Coordinate newCoor : newArea.coordinates) {
//						while (newCoor.freeSpace() > 0 && oldCoor.countContainers() > 0) {
//							oldCoor.getTopContainer().moveTo(newCoor);
//						}
//						
//						if (oldCoor.countContainers() == 0) {
//							break;
//						}
//					}
//				}
//			}
//		} else {
//			System.err.println("Not enough free space...");
//		}
		
	}
	
	//Returns sum of all containers in the area
	public double countContainers() {
		double sum = 0;
		
		for (Coordinate coor : coordinates) {
			sum += coor.countContainers();
		}
		
		return sum;
	}
	
	//Returns sum of all free space in the area
	public double freeSpace() {
		double sum = 0;
		
		for (Coordinate coor : coordinates) {
			sum += coor.freeSpace();
		}
		
		return sum;
	}
	
	public boolean isFull() {
		if (freeSpace() == 0) {
			return true;
		} else {
			return false;
		}
	}
	
	public boolean isEmpty() {
		for (Coordinate coor : coordinates) {
			if (coor.isEmpty() == false) {
				return false;
			}
		}
		return true;
	}
	
	public ArrayList<Container> getContainers() {
		ArrayList<Container> copy = new ArrayList<Container>();
		
		for (Coordinate coor : coordinates) {
			copy.addAll(coor.getContainers());
		}
		
		return copy;
	}
}
