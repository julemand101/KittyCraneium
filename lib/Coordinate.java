package kitty.lib;
import java.util.ArrayList;

public class Coordinate {
	private final int MAX_CONTAINERS = 3;
	
	private ArrayList<Container> containerList;
	private int x;
	private int y;
	
	public static final Coordinate A1 = new Coordinate(0,0);
	public static final Coordinate A2 = new Coordinate(0,1);
	public static final Coordinate B1 = new Coordinate(1,0);
	public static final Coordinate B2 = new Coordinate(1,1);
	public static final Coordinate C1 = new Coordinate(2,0);
	public static final Coordinate C2 = new Coordinate(2,1);
	public static final Coordinate D1 = new Coordinate(3,0);
	public static final Coordinate D2 = new Coordinate(3,1);
	public static final Coordinate E1 = new Coordinate(4,0);
	public static final Coordinate E2 = new Coordinate(4,1);
	
	//Is used if we need loop all cordinates
	public static final Coordinate[] allCordinates = { A1, A2,
													   B1, B2, 
													   C1, C2,
													   D1, D2,
													   E1, E2 };
	
	//The constructor is private so only the class itself can create coordinate classes
	private Coordinate(int x, int y) {
		containerList = new ArrayList<Container>(MAX_CONTAINERS);
		this.x = x;
		this.y = y;
	}
	
	/**
	 * Add container to the top of a coordinate if any free space.
	 * If no free space the method cast a exception.
	 * 
	 * THIS METHOD DON'T MAKE ANY MOVEMENTS ON THE CRANE AND SHOULD THEREFORE
	 * ONLY BE USED INTERNEL.
	 */
	public void addContainer(Container element) {
		if (containerList.size() < MAX_CONTAINERS) {
			containerList.add(element);
		} else {
			throw new ArrayStoreException();
		}
	}
	
	/**
	 * Removes the top container. In this exsample the method removes
	 * "container 3". If no containers in the coordinate the method does
	 * nothing.
	 * 
	 * -------------
	 * |container 3| <-- Removed container
	 * -------------
	 * |container 2|
	 * -------------
	 * |container 1|
	 * -------------
	 */
	public void removeContainer() {
		if (containerList.size() > 0) {
			containerList.remove(containerList.size() - 1);	
		}
	}
	
	/**
	 * Removes all containers in the coordinate.
	 */
	public void removeAllContainers() {
		containerList.clear();
	}
	
	
	public void moveContainer(Container selectedContainer, Coordinate newCoordinate) {
		Coordinate oldCoordinate = selectedContainer.getCoordinate();
		
		System.out.println("#################");
		System.out.println("ID: " + selectedContainer.getCID());

		if (oldCoordinate != newCoordinate) {
			if (newCoordinate.freeSpace() > 0) {
				int zIndexOfContainer = oldCoordinate.containerList.indexOf(selectedContainer);
				int containersInCoordinate = oldCoordinate.containerList.size();
				Crane crane = Crane.getInstance();
				ArrayList<Container> tempMovedContainers = new ArrayList<Container>();
				
				//Checks if the container is NOT on the top
				if (zIndexOfContainer != (containersInCoordinate - 1)) {
					System.out.println("We need to temp move containers!");
					//We need to move all the containers above the container we move
					
					for (int i = containersInCoordinate - 1; i > zIndexOfContainer; i--) {
						
						boolean freeCoordinate = false;
					
						//We trying to find a coordinate with freeSpace
						for (Coordinate tempSpaceCoordinate : allCordinates) {
							if (tempSpaceCoordinate == newCoordinate || tempSpaceCoordinate == oldCoordinate)
								continue;
							
							System.out.println("Checking for free space in: " + tempSpaceCoordinate.x + ":" + tempSpaceCoordinate.y);
							if (tempSpaceCoordinate.freeSpace() > 0) {
								System.out.println("Wuhu free space... time to move: " + oldCoordinate.containerList.get(i).getCID());
								freeCoordinate = true;
								
								Container tempContainer = oldCoordinate.containerList.get(i);
								
								System.out.println("Step 1");
								tempContainer.moveTo(tempSpaceCoordinate);
								System.out.println("Step 2");
								tempMovedContainers.add(tempContainer);
								System.out.println("Step 3");
								break;
								
								//Some code there move the containers to temp space and place 
								//the movement into "temoMovedContainers" and "tempMovedContainersCoordinates".
							}
						}
						
						if (freeCoordinate == false) {
							System.err.println("No free space to move temp containers");
						}
					}
					
				}
				System.out.println("Move from:");
				System.out.println(oldCoordinate.getX() + " " + oldCoordinate.getY() + " " + zIndexOfContainer);
				crane.takeContainer(oldCoordinate.getX(), oldCoordinate.getY(), zIndexOfContainer);
				
				System.out.println("Move to:");
				System.out.println(newCoordinate.getX() + " " + newCoordinate.getY() + " " + newCoordinate.containerList.size());
				crane.deliverContainer(newCoordinate.getX(), newCoordinate.getY(), newCoordinate.containerList.size());
				
				//We removes the container from old coordinate and insert it again in the new coordinate (on the top)
				oldCoordinate.containerList.remove(zIndexOfContainer);
				newCoordinate.containerList.add(selectedContainer);
				
				//Maybe we have moved some containers there should be replaced again
				if (tempMovedContainers.size() > 0) {
					System.out.println("Moves temp containers back to orginal position.");

					for (int i = tempMovedContainers.size(); i > 0; i--) {
						System.out.println("Moves temp: " + i);
						tempMovedContainers.get(i - 1).moveTo(oldCoordinate);
					}
				}
				
			} else {
				System.err.println("No free space on: [" + newCoordinate.getX() + ":" + newCoordinate.getY() + "]");
			}
		} else {
			System.err.println("Trying to move the container to the same coordinate");
		}
		
		System.out.println("#################");
	}
	
//	private boolean freeSpace(Coordinate selectedCoordinate) {
//		System.out.println("Free space checker on : " + selectedCoordinate.getX() + ":" + selectedCoordinate.getY());
//		System.out.println("\t" + selectedCoordinate.containerList.size());
//		if (selectedCoordinate.containerList.size() < MAX_CONTAINERS) {
//			System.out.println("\tTRUE");
//			return true;
//		}
//		System.out.println("\\tFALSE");
//		return false;
//	}
	
	//Get the X coordinate
	public int getX() {
		return x;
	}
	
	//Get the Y coordinate
	public int getY() {
		return y;
	}
	
	/*
	 * This method returns the top container.
	 * If no containers this method throw an exception.
	 * 
	 * -------------
	 * |container 3| <-- Output this container
	 * -------------
	 * |container 2|
	 * -------------
	 * |container 1|
	 * -------------
	 */
	public Container getTopContainer() {
		if (containerList.size() != 0) {
			return containerList.get(containerList.size() - 1);
		} else {
			throw new ArrayStoreException();
		}
	}
	
	public int countContainers() {
		return containerList.size();
	}
	
	public int freeSpace() {
		return MAX_CONTAINERS - countContainers();
	}
	
	/*
	 * Returns an Arraylist with all containers in the coordinate.
	 * The first container in the list is the top container on the
	 * coordinate.
	 * If this is how the containers is placed on the coordinate:
	 * 
	 * -------------
	 * |container 3|
	 * -------------
	 * |container 2|
	 * -------------
	 * |container 1|
	 * -------------
	 * 
	 * The returned list is:
	 * |ID | Content:    |
	 * -------------------
	 * |(0)| container 3 |
	 * |(1)| container 2 |
	 * |(2)| container 1 |
	 */
	public ArrayList<Container> getContainers() {
		ArrayList<Container> copy = new ArrayList<Container>();
		
		for (int i = containerList.size(); i > 0; i--) {
			copy.add(containerList.get(i - 1));
		}
		
		return copy;
	}
	
	public boolean isFull() {
		if (containerList.size() == MAX_CONTAINERS) {
			return true;
		}
		return false;
	}
	
	public boolean isEmpty() {
		if (containerList.size() == 0) {
			return true;
		}
		return false;
	}
	
	public static boolean emptyCoordinate() {
		for (Coordinate coor : allCordinates) {
			if (coor.countContainers() == 0) {
				return true;
			}
		}
		
		return false;
	}
	
	public static Coordinate getEmptyCoordinate() {
		for (Coordinate coor : allCordinates) {
			if (coor.countContainers() == 0) {
				return coor;
			}
		}
		
		SystemMethods.error("No coordinate is empty.");
		return null;
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		char x = (char) ((char) getX() + 'A');
		
		sb.append("+----+\n");
		sb.append("+ "+x+(getY()+1)+" +\n");
		sb.append("+----+\n");
		
		for (int i = containerList.size(); i > 0; i--) {
			sb.append("| " + containerList.get(i - 1).getCID() + " |\n");
		}
		
		sb.append("+----+\n");
		
		return sb.toString(); 
	}
}
