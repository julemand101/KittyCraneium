package kitty.lib;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;

public class Crane {
	/*Motors
		X = Skinnevejen
		Y = Tandhjulvejen
		Z = Op og ned
	*/
	Motor motorX, motorY, motorZ;
	LightSensor xPositionLightSensor;
	TouchSensor yzPositionTouchSensor;
	private static Crane instance;
	
	enum Axis {x, y, z}
	
	//CONSTANT - The sleep time we spend before we check the x and y sensors again
	static final int WAIT_TIME_FOR_X_OR_Y = 100;
	
	//CONSTANTS - Color values
	static final int COLOR_BLACK = 23;
	static final int COLOR_YELLOW = 43;
	
	//CONSTANTS - 0 baseret
	static final int MAX_POSITION_X = 4;
	static final int MAX_POSITION_Y = 1;
	static final int MAX_POSITION_Z = 2;
	
	//STATIC POSITIONS - Y MOTOR - DEGREES
	static final int ROW_0 = 0;
	static final int ROW_1 = 1030;
	static final int TAKE_CONTAINER = 430;
	
	//STATIC POSITIONS - Z MOTOR - DEGREES
	static final int MOVE_POSITION = 0;
	static final int CONTAINER_0 = 2120;
	static final int CONTAINER_1 = 1600;
	static final int CONTAINER_2 = 1050;
	
	//CURRENT POSITIONS - 0 baseret
	int currentPositionX = 0;
	int currentPositionY = 0;
	int currentPositionZ = 0;
	
	
	private Crane() {
		xPositionLightSensor = new LightSensor(SensorPort.S1);
		yzPositionTouchSensor = new TouchSensor(SensorPort.S2);
		
		motorX = new Motor(MotorPort.C);
		motorX.setPower(100);
		motorX.setSpeed(600);
		
		motorY = new Motor(MotorPort.B);
		motorY.setPower(100);
		motorY.setSpeed(900);
		
		motorZ = new Motor(MotorPort.A);
		motorZ.setPower(100);
		motorZ.setSpeed(900);
		
		resetPosition();
	}
	
	public static Crane getInstance() {
		if (instance == null) {
			synchronized (Crane.class) {
				if (instance == null) {
					instance = new Crane();
				}
			}
		}
		return instance;
	}
	
	public static boolean isInitialized() {
		return (instance != null);
	}
	
	@SuppressWarnings("static-access")
	public void resetPosition() {
		class resetXthread extends Thread {
			public void run() {
				resetX();
			}
		}
		
		class resetYthread extends Thread {
			public void run() {
				resetY();
			}
		}
		
		resetXthread thread1 = new resetXthread();
		resetYthread thread2 = new resetYthread();
		
		resetZ();
		
		thread1.start();
		thread2.start();
		
		while (thread1.isAlive() == true || thread2.isAlive() == true) {
			try {
				Thread.currentThread().sleep(WAIT_TIME_FOR_X_OR_Y);
			} catch (InterruptedException e) {
				System.out.println("Thread exception");
			}
		}
	}
	
	private void resetX() {
		//RESET X MOTOR
		if (xPositionLightSensor.getLightValue() <= COLOR_BLACK) {
			motorX.resetTachoCount();
			currentPositionX = 0;
		} else {
			motorX.forward();
			while (true) {
				if (xPositionLightSensor.getLightValue() <= COLOR_BLACK) {
					motorX.stop();
					motorX.resetTachoCount();
					currentPositionX = 0;
					break;
				}
			}
		}
	}
	
	private void resetY() {
		//RESET Y MOTOR
		int oldSpeed = motorY.getSpeed();
		
		motorY.setSpeed(500);
		if (yzPositionTouchSensor.isPressed()) {
			motorY.rotate(45);
			motorY.resetTachoCount();
		} else {
			motorY.backward();
			
			while (true) {
				if (yzPositionTouchSensor.isPressed()) {
					motorY.stop();
					motorY.rotate(45);
					motorY.resetTachoCount();
					currentPositionZ = 0;
					break;
				}
			}
		}
		motorY.setSpeed(oldSpeed);
	}
	
	private void resetZ() {
		//RESET Z MOTOR
		if (yzPositionTouchSensor.isPressed()) {
			motorZ.rotate(360);
			motorZ.resetTachoCount();
		} else {
			motorZ.backward();
			
			while (true) {
				if (yzPositionTouchSensor.isPressed()) {
					motorZ.stop();
					motorZ.rotate(360);
					motorZ.resetTachoCount();
					currentPositionZ = 0;
					break;
				}
			}
		}
		
	}
	
	// We move the crane to the first coordinate in an area
	public void goTo(Area input_area) {
		if (input_area.coordinates.size() > 0) {
			goTo(input_area.coordinates.get(0));
		} else {
			System.err.println("Strange bug here - An area without coordinates.");
		}
	}
	
	//Move the crane to a coordinate
	public void goTo(Coordinate input_coordinate) {
		move(input_coordinate.getX(), input_coordinate.getY(), -1);
	}
	
	public void moveX(int goToPositionX) {
		boolean lightChanged = false;
		
		if (goToPositionX > MAX_POSITION_X) {
			lejos.nxt.Sound.buzz();
			System.out.println("TRYING TO GO OVER MAX_POSITION_X");
		} else {
			if (goToPositionX == 0) {
				resetX();	//The reset method is setting the X motor to 0 position
			} else {
				if (goToPositionX < currentPositionX) {
					motorX.forward();
				} else if (goToPositionX > currentPositionX) {
					motorX.backward();
				} else {
					motorX.stop();
				}
				
				while (true) {
					if (xPositionLightSensor.getLightValue() >= COLOR_YELLOW) {
						if (lightChanged) {
							if (motorX.isBackward()) {
								currentPositionX++;
							} else {
								currentPositionX--;
							}
							
							lightChanged = false;
						}
					} else {
						lightChanged = true;
					}
					if (currentPositionX == goToPositionX) {
						motorX.stop();
						break;
					}
				}
			}
		}
	}
	
	public void moveY(int goToPositionY) {
		switch (goToPositionY) {
			case 0:
				motorY.rotateTo(ROW_0);
				currentPositionY = 0;
				break;
				
			case 1:
				motorY.rotateTo(ROW_1);
				currentPositionY = 1;
				break;
	
			default:
				lejos.nxt.Sound.buzz();
				System.out.println("TRYING TO GO OVER MAX_POSITION_Y");
				break;
		}
	}
	
	public void moveY(int goToPositionY, boolean takePosition) {
		switch (goToPositionY) {
			case 0:
				if (takePosition)
					motorY.rotateTo(ROW_0 + TAKE_CONTAINER);
				else
					motorY.rotateTo(ROW_0);
				currentPositionY = 0;
				break;
				
			case 1:
				if (takePosition)
					motorY.rotateTo(ROW_1 + TAKE_CONTAINER);
				else
					motorY.rotateTo(ROW_1);
				currentPositionY = 1;
				break;
	
			default:
				lejos.nxt.Sound.buzz();
				System.out.println("TRYING TO GO OVER MAX_POSITION_Y");
				break;
		}
	}

	public void moveZ(int goToPositionZ) {
		switch (goToPositionZ) {
			case -1:
				motorZ.rotateTo(MOVE_POSITION);
				break;
			
			case 0:
				motorZ.rotateTo(CONTAINER_0); // Nederste container
				currentPositionZ = 0;
				break;
				
			case 1:
				motorZ.rotateTo(CONTAINER_1); // Mellemeste container
				currentPositionZ = 1;
				break;
				
			case 2:
				motorZ.rotateTo(CONTAINER_2); // Øverste container
				currentPositionZ = 2;
				break;
	
			default:
				lejos.nxt.Sound.buzz();
				System.out.println("TRYING TO GO OVER MAX_POSITION_Z");
				break;
		}
	}
	
	public void takeContainer(int grepX, int grepY, int grepZ) {
		System.out.println("Taking container [" + grepX + "," + grepY + "," + grepZ + "]");
		
		move(grepX, grepY, grepZ);
		
		motorY.rotate(TAKE_CONTAINER);
		motorZ.rotateTo(MOVE_POSITION);
	}
	
	@SuppressWarnings("static-access")
	public void deliverContainer(int deliverX, int deliverY, int deliverZ) {
		int positionY = currentPositionY;
		
		moveThread thread1 = new moveThread(Axis.x, deliverX);
		moveThread thread2 = new moveThread(Axis.y, deliverY, true);
		thread1.start();
		
		if (deliverY != positionY)
			thread2.start();
		
		while (thread1.isAlive() == true || thread2.isAlive() == true) {
			try {
				Thread.currentThread().sleep(WAIT_TIME_FOR_X_OR_Y);
			} catch (InterruptedException e) {
				System.out.println("Thread exception");
			}
		}
		
		moveZ(deliverZ);
		
		moveY(currentPositionY);
		motorZ.rotateTo(MOVE_POSITION);
	}
	
	@SuppressWarnings("static-access")
	public void move(int x, int y, int z) {
		moveThread thread1 = new moveThread(Axis.x, x);
		moveThread thread2 = new moveThread(Axis.y, y);
		
		thread1.start();
		thread2.start();
		
		while (thread1.isAlive() == true || thread2.isAlive() == true) {
			try {
				Thread.currentThread().sleep(WAIT_TIME_FOR_X_OR_Y);
			} catch (InterruptedException e) {
				System.out.println("Thread exception");
			}
		}
		
		moveZ(z);
	}
	
	class moveThread extends Thread {
		Axis selectedAxis;
		int position;
		boolean takePosition;
		
		moveThread(Axis akse, int position) {
			this(akse, position, false);
		}
		
		moveThread(Axis akse, int position, boolean takePosition) {
			selectedAxis = akse;
			this.position = position;
			this.takePosition = takePosition;
		}
		
		public void run() {
			switch (selectedAxis) {
			case x:
				moveX(position);
				break;
				
			case y:
				moveY(position,takePosition);
				break;

			case z:
				moveZ(position);
				break;
				
			default:
				System.err.println("WRONG AXIS");
				break;
			}
		}
	}
}
