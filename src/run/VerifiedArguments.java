package run;

import java.io.File;
import java.util.ArrayList;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

public class VerifiedArguments {

	private File sourceFile;
	private File outputDir;
	private boolean createGraphvizAST;	
	private boolean argumentsAreValid;

	public VerifiedArguments(ArrayList<String> arrayList, Main.OS OSname) {
		
		argumentsAreValid = false;
		
		if (arrayList.size() == 3) {
			if (setSourceFile(arrayList.get(0))			== true && 
				setCreateGraphvizAST(arrayList.get(1)) 	== true &&
				setOutputDir(arrayList.get(2)) 			== true )
			{
				argumentsAreValid = true;
			}
			
		}
		else if (arrayList.size() == 0) {
			if (promptSourceFile() 		== true &&
			promptGraphvizAST(OSname) 	== true &&
			promptOutputDir() 			== true) {
				argumentsAreValid = true;
			}
		}
	}

	private boolean setOutputDir(String string) {
		File outputDir = new File(string).getAbsoluteFile();
		return setOutputDir(outputDir);
	}

	private boolean setCreateGraphvizAST(String string) {
		if (string.equals("true")) {
			createGraphvizAST = true;
			return true;
		}
		else {
			createGraphvizAST = false;
			return true;
		}
	}

	private boolean setSourceFile(String string) {
		File sourceFile = new File(string).getAbsoluteFile();
		return setSourceFile(sourceFile);
	}	
	
	public File getSourceFile() {
		return sourceFile;
	}
	
	public boolean allArgumentsAreValid() {
		return argumentsAreValid;
	}	

	public File getOutputDir() {
		return outputDir;
	}

	public boolean userWantsGraphvizAST() {
		return createGraphvizAST;
	}	
	
	private boolean promptOutputDir() {
		JFileChooser outputDirChooser = new JFileChooser();
		outputDirChooser.setDialogTitle("Choose Output Directory");
		outputDirChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int guiValue = outputDirChooser.showOpenDialog(null);
		if (guiValue == JOptionPane.OK_OPTION)			
			return setOutputDir( outputDirChooser.getSelectedFile() ); //user has defined an output directory
		else
			System.exit(0);
		return false;
	}

	private boolean setOutputDir(File outputDir) {
		outputDir = outputDir.getAbsoluteFile();
		if (outputDir.exists() && outputDir.isDirectory()) {
			this.outputDir = outputDir;
			return true;
		} else {			
			System.out.println("ERROR: Output directory " + outputDir.getAbsolutePath() + " does not exist.");
			return false;
		}
	}

	private boolean promptSourceFile() {
		JFileChooser sourceFileChooser= new JFileChooser();
		sourceFileChooser.setDialogTitle("Choose KittyCraneium Source File");
		int guiValue = sourceFileChooser.showOpenDialog(null);
		if (guiValue == JOptionPane.OK_OPTION)			
			return setSourceFile( sourceFileChooser.getSelectedFile() ); //user has defined an output directory
		else
			System.exit(0);
		return false;
	}

	private boolean setSourceFile(File sourceFile) {
		sourceFile = sourceFile.getAbsoluteFile();
		if (sourceFile.exists() && sourceFile.isFile()) {
			this.sourceFile = sourceFile;
			return true;
		} else {
			System.out.println("ERROR: Input source file " + sourceFile.getAbsolutePath() + " does not exist.");
			return false;
		}
	}

	private boolean promptGraphvizAST(Main.OS OSname) {
		String graphvizLocation;
		
		switch (OSname) {
			case Linux: graphvizLocation = "package to be installed."; break;
			case Win32: graphvizLocation = "to be installed in C:\\Program Files\\"; break;
			case Win64: graphvizLocation = "to be installed in C:\\Program Files(x86)\\"; break;
			default   : graphvizLocation = "to be installed in default location.)"; break;
		}
		
		int guiValue = JOptionPane.showConfirmDialog(	null,
				"Do you want to produce graphical AST? \n(Requires Graphviz 2.26.3 " + graphvizLocation + ")",
				"Create graphical AST?",
				JOptionPane.YES_NO_CANCEL_OPTION,
				JOptionPane.QUESTION_MESSAGE);
			
		//Yes, use Graphviz
			if (guiValue == JOptionPane.YES_OPTION) {
				createGraphvizAST = true;
				return true;
			}
			//No, do not use Graphviz
			else if (guiValue == JOptionPane.NO_OPTION) {
				createGraphvizAST = false;
				return true;
			}
			//Another button hitted, close!
			else
				System.exit(0);
			return false;
	}
}
