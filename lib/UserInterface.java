package kitty.lib;

import java.util.ArrayList;

import lejos.nxt.LCD;
import lejos.util.TextMenu;

public class UserInterface implements IEvent {
	private int EVENT_ID = 0;

	public int getEventID() {
		return EVENT_ID;
	}

	public boolean run() {
		//Notify the user that the menu is ready
		System.out.println("Menu opened");
		lejos.nxt.Sound.twoBeeps();	
		mainMenu();		
		return true;	
	}
	
	public void mainMenu() {
		LCD.clear();
		String title = "Kitty Craneium";
		String[] items = 	{"Add container",
							 "View containers",
							 "Cheerful tune",
							 "Reset crane",
							 "Leave menu",
							 "Stop program"};
		int choice = menu(title, items);
		switch (choice) {
			case 0 : addContainer(); break;
			case 1 : viewContainers(); break;
			case 2 : playBennyHill(); break;
			case 3 : resetCrane(); break;
			case 4 : hideMenu(); break;
			case 5 : stopProgram(); break;
			default: hideMenu(); break;
		}		
	}
	
	private void viewContainers() {
		LCD.clear();		
		//Find out where to place the container		
		String title = "Where?";		
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		ArrayList<String> coordinateNames = new ArrayList<String>();		
		
		//Find all the containers with room for another container
		for (Coordinate c : Coordinate.allCordinates) {
			if (!c.isEmpty()) {
				//There's room in this coordinate
				coordinates.add(c);
				
				char x = (char) ((char) c.getX() + 'A');
				int y  = c.getY() + 1;
				String coord = String.valueOf(x) + String.valueOf(y);
								
				coordinateNames.add(coord);
			}
		}
		
		//Now check if we found any containers with room for more containers		
		if (coordinateNames.size() > 0) {
			LCD.clear();
			
			String[] items2 = new String[coordinateNames.size()];
			
			for (int i = 0; i < coordinateNames.size(); i++) {
				items2[i] = coordinateNames.get(i);
			}
			
			int choice = menu(title, items2);
			
			Coordinate chosenCoordinate = coordinates.get(choice);
			int amountOfContainers = chosenCoordinate.countContainers();
			String containerName = String.valueOf((char) ((char) chosenCoordinate.getX() + 'A')) + String.valueOf(chosenCoordinate.getY() + 1);
			
			String[] goBack = new String[] {"OK"};
			menu(amountOfContainers + " in " + containerName, goBack);
			mainMenu();
									
		} else {
			//No more room in any coordinates
			String[] goBack = new String[] {"Go back"};
			menu("No containers", goBack);
			mainMenu();
		}
	}

	private void resetCrane() {
		SystemMethods.resetCrane();
	}

	private void stopProgram() {
		SystemMethods.quit();
	}
	
	private void hideMenu() {
		LCD.clear();
	}
	
	private void playBennyHill() {
		LCD.clear();
		System.out.println("One moment - gathering the band...");
		kitty.lib.SystemMethods.playBennyHill();
		String[] goBack = new String[] {"OK", "Fine", "Whatever"};
		menu("Playing...", goBack);
		mainMenu();
	}
	
	private void addContainer() {		
		LCD.clear();		
		//Find out where to place the container		
		String title = "Add Where?";		
		ArrayList<Coordinate> coordinates = new ArrayList<Coordinate>();
		ArrayList<String> coordinateNames = new ArrayList<String>();		
		
		//Find all the containers with room for another container		
		for (Coordinate c : Coordinate.allCordinates) {			
			if (c.freeSpace() > 0) {				
				//There's room in this coordinate
				coordinates.add(c);
				
				char x = (char) ((char) c.getX() + 'A');				
				int y  = c.getY() + 1;				
				String coord = String.valueOf(x) + String.valueOf(y);
								
				coordinateNames.add(coord);
			}
		}
		
		//Now check if we found any containers with room for more containers		
		if (coordinateNames.size() > 0) {			
			LCD.clear();
			
			String[] items2 = new String[coordinateNames.size()];
			
			for (int i = 0; i < coordinateNames.size(); i++) {
				items2[i] = coordinateNames.get(i);
			}
			
			int choice = menu(title, items2);
			
			Coordinate chosenCoordinate = coordinates.get(choice);			
			
			//Ask how many containers to add to this coordinate
			String title2 = "Add how many?";
			ArrayList<String> amount = new ArrayList<String>();
			
			//Can we add 1, 2 or 3 containers?
			for(int i = 1; i <= chosenCoordinate.freeSpace(); i++) {
				amount.add(String.valueOf(i));
			}
			
			String[] items3 = new String[amount.size()];			
			for (int i = 0; i < amount.size(); i++) {
				items3[i] = amount.get(i);
			}
			
			LCD.clear();
			int amountOfContainers = menu(title2, items3) + 1; //yuk			
			
			//Ask for Group ID
			String title3 = "Group ID?";
			String[] items = 	{"0",
								 "1",							 							 
								 "2",
								 "3",
								 "4",
								 "5",
								 "6",
								 "7",
								 "8",
								 "9"};
			LCD.clear();
						
			int gid = menu(title3, items);			
						
			for (int i = 0; i < amountOfContainers; i++) {
				new Container(chosenCoordinate, gid); //Add containers!!
				System.out.println("Added container!");
			}
			
			LCD.clear();
			String[] goBack = new String[] {"OK"};			
			menu("Cont.(s) added", goBack);
			mainMenu();
									
		} else {
			//No more room in any coordinates
			String[] goBack = new String[] {"Go back"};
			menu("No room", goBack);
			mainMenu();
		}
	}
	
	private int menu(String title, String[] items ) {
		return new TextMenu(items, 0, title).select();
	}
}
