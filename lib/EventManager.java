package kitty.lib;
import java.util.ArrayList;

import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.Sound;

public class EventManager implements ButtonListener {
	ArrayList<IEvent> events = new ArrayList<IEvent>();
	boolean eventIsRunning = false;
	boolean menuWantsToLoad = false;
	boolean menuLoaded = false;
	
	public EventManager() {
		Sound.setVolume(100);
		//Add the button listener (ENTER button) that is the trigger for the 
		//GUI event
		Button.ESCAPE.addButtonListener(this);		
	}
	
	public void add(IEvent event) {
		events.add(event);
	}
	
	public void start() {
		while(true) {
			for (IEvent selectedEvent : events) {
				
				boolean eventIsStillTrue = true;
				
				while (eventIsStillTrue) {				
          //Make sure the menu gets a chance to open          
          try { Thread.sleep(200); } catch (InterruptedException e) { }
          
          
          //Do a sleep loop while the event is running
          while(menuLoaded) {
            try { Thread.sleep(500); } catch (InterruptedException e) { }
					}
					
					eventIsRunning = true;
          while (	!menuWantsToLoad && (eventIsStillTrue = selectedEvent.run()));
          eventIsRunning = false;
					
				}
			}				
		}
	}

	@Override
	public void buttonPressed(Button b) {		
		
	}

	@Override
	public void buttonReleased(Button b) {
		if (!menuLoaded && !menuWantsToLoad) {
			menuWantsToLoad = true;
			System.out.println("Waiting for event to finish");
			//Wait until the event has finished		
			while (eventIsRunning) {				
				Sound.playTone(500, 80);				
				try { Thread.sleep(800); } 
				catch (InterruptedException e) { System.err.println("EventManager cannot sleep"); }
			}
			//We are now ready to show the menu
			menuWantsToLoad = false;
			menuLoaded = true;
			System.out.println("Loading menu");
			new UserInterface().run();
			LCD.clear();
			System.out.println("Leaving menu");
			menuLoaded = false;
		} else {
			System.out.println("Menu already loaded or about to load");
		}	
	}	
}
