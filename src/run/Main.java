package run;

import java.awt.HeadlessException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.prefs.BackingStoreException;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import compiler.Compiler;

public class Main {

	/**
	 * @param args
	 * @throws BackingStoreException 
	 * @throws HeadlessException 
	 * @throws IOException 
	 */
	public enum OS {Linux, Win32, Win64};
	private static OS operatingSystem;
	
	public static void main(String[] args) throws HeadlessException, BackingStoreException, IOException {		
		
		//declare window frame
	    WindowFrame outputFrame;
		
		boolean showedHelp = false; 
		if (args.length == 1) { 
			if ( args[0].equals("help") || args[0].equals("-help") || args[0].equals("-?")) {
				//Used asked for help - print help dialog
				
				//Print Hello Kitty Welcome Screen
				printKitty();
				showHelp();
				showedHelp = true;
			} 
		}
		if (!showedHelp) {
			
			String nameOS = "os.name";        
			String OSname = System.getProperty(nameOS);
			
			if (OSname.equals("Linux")) {
				operatingSystem = OS.Linux;
			}
			else { 
				//Assume 64bit Windows
				String filePathToGraphviz = System.getenv("PROGRAMFILES(X86)");
				operatingSystem = OS.Win64;
				
				//If 32bit Windows
				if (filePathToGraphviz == null) {
					filePathToGraphviz = System.getenv("PROGRAMFILES");
					operatingSystem = OS.Win32;
				}
			}
			
			//If no arguments provided, assume execution from GUI
			if(args.length == 0) {
				//Choose Windows or GTK GUI
				setWindowStyle();
				//initialize window frame
				outputFrame = new WindowFrame(600, 480);
				//show it
			    outputFrame.setVisible(true);
				//redirect the std output stream
				System.setOut(outputFrame.getPs());
			}
			
			//Print Hello Kitty Welcome Screen
			printKitty();
			
			//User didn't ask for help - that means we're supposed to run the compiler
			VerifiedArguments arguments = new VerifiedArguments(new ArrayList<String>(Arrays.asList(args)), operatingSystem);
			
			if (arguments.allArgumentsAreValid()) {
				System.out.println(	"Input file:\t\t" + arguments.getSourceFile().getAbsolutePath() + "\n" +
						"Output directory:\t\t" + arguments.getOutputDir().getAbsolutePath() + "\n" +
						"Graphviz AST?:\t\t" + arguments.userWantsGraphvizAST() + "\n" +
						//"File content: ################################\n" +
						//createCompilableString(sourceFile) + "\n\n" +
						//"##############################################\n" +
				"Compiling...\n\n");
				
				//Start compiler
				boolean compiledWithSuccess = Compiler.compile(createCompilableString(arguments.getSourceFile()), arguments.getOutputDir(), arguments.userWantsGraphvizAST());
				if (!compiledWithSuccess)
					System.out.println("An error occured. Read the error description above, and check your source file.");
				else
					System.out.println("Compiled with success!");
			}
			else {
				System.out.println("ERROR: Invalid arguments. Use \"-help\" to see list of valid arguments.");
			}
		}
	}
	
	private static String createCompilableString(File file) {
		BufferedReader input;
		String line;
		StringBuffer tempSource = new StringBuffer();

		try {
			input = new BufferedReader(new FileReader(file));

			while ((line = input.readLine()) != null) {
				tempSource.append(line);
				tempSource.append('\n');
			}

			input.close();
		} catch (IOException e) {
			JOptionPane.showMessageDialog(null, "It seems I cannot read file contents", "Test", JOptionPane.WARNING_MESSAGE);
		}
		
		return tempSource.toString();	
	}	
	
	public static String test_CreateCompiableString(File file) {
		return createCompilableString(file);
	}
	
	public static OS getOperatingSystem() {
		return operatingSystem;
	}
	
	public static void printKitty() {
		System.out.println("   .-. __ _ .-.              ___________________________________");
		System.out.println("   |  `  / \\  |             /      __ ___ __  __               /|");
		System.out.println("   /     '.()--\\           /      / //_(_) /_/ /___ __        / |");
		System.out.println("  |         '._/          /      / ,< / / __/ __/ // /       /  |");
		System.out.println(" _| O   _   O |_         /      /_/|_/_/\\__/\\__/\\_, /       /   |");
		System.out.println(" =\\    '-'    /=        /                      /___/       /    |");
		System.out.println("   '-._____.-'         /__________________________________/     /");
		System.out.println("   /`/\\___/\\`\\         |Yet Another KittyCraneium Compiler|    /");
		System.out.println("  /\\/o     o\\/\\        | <^__)~  <^__)~    ~(__^>  ~(__^> |   /");     
		System.out.println(" (_|         |_)       |s406a - 4th Semester - Spring 2010|  /");     
		System.out.println("   |____,____|         |Department of Computer Science,   | /");
		System.out.println("   (____|____)         |Aalborg University                |/");
		System.out.println("                        ¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯¯\n");
	}
	
	public static void showHelp() {
		System.out.println(
				"Valid arguments:\n" +
				"\tArg 1: Source file path\n" +
				"\tArg 2: Make Graphviz create a graphical AST representation (true/false)\n" +
				"\tArg 3: Output directory path");
	}
	
	private static void setWindowStyle() {
		try {
			if(Main.getOperatingSystem() == Main.OS.Win32 || Main.getOperatingSystem() == Main.OS.Win64)
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			else
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel");
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
		} catch(Exception e) {
			System.out.println("Error setting window style: " + e);
		}
	}
}