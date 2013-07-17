package compiler;

import java.io.*;
import java.util.ArrayList;

import ast.*;
import run.Main;
import syntactic.*;
import syntactic.exceptions.CompileException;
import contextual.*;
import contextual.exceptions.IdentException;
import codegen.*;

public class Compiler {
	private static IAST ast;
	private static IAST decoratedAst;
	public static boolean compile(String sourceCode, File outputDir, boolean useGraphviz) throws IOException {
		TokenList tokens;

		//SYNTACTIC ANALYSIS
		//Scan
		try {
			tokens = new Scanner(sourceCode).getTokens();
		} catch (CompileException e) {
			System.out.println("Syntax error at line: " + e.getErrorLine() + ", column " + e.getErrorColumn());
			System.out.println(e.getMessage());
			System.out.println(getErrorLine(e.getErrorLine(), sourceCode));
			System.out.println(buildDebugSnake(e.getErrorColumn()));
			
			return false;
		}
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		//Parse
		try {
			ast = new Parser().parse(tokens);
		} catch (CompileException e) {
			System.out.println("Syntax error at line: " + e.getErrorLine() + ", column " + e.getErrorColumn());
			System.out.println(e.getMessage());
			System.out.println(getErrorLine(e.getErrorLine(), sourceCode));
			System.out.println(buildDebugSnake(e.getErrorColumn()));
			
			return false;
		} 
		catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		if(useGraphviz) {

			//Define AST output directory
			File astDir = new File(outputDir, "AST");
			
			//If AST output directory not exists, create it
			if (!astDir.exists()) {
				astDir.mkdirs();
			}
			
			//Define graphviz dot code file for AST
			File astOutputFile = new File(outputDir.toString()+"/AST/ASTprinter.dot");
			
			//Print AST and make dot file
			ASTPrinter astprinter = new ASTPrinter();
			astprinter.print(ast, astOutputFile);
			
			if(Main.getOperatingSystem() == Main.OS.Linux) {
				//Using dot compiler from graphviz package
				//Tested on Arch Linux 32-bit
				String dotCompiler = "dot";
				compileDotFile(dotCompiler, astOutputFile);
			}
			else if(Main.getOperatingSystem() == Main.OS.Win32) {
				String filePathToGraphviz = System.getenv("PROGRAMFILES");
				filePathToGraphviz += "\\Graphviz2.26.3\\bin\\dot.exe";
				compileDotFile(filePathToGraphviz, astOutputFile);
			}
			else if(Main.getOperatingSystem() == Main.OS.Win64) {
				String filePathToGraphviz = System.getenv("PROGRAMFILES(X86)");
				filePathToGraphviz += "\\Graphviz2.26.3\\bin\\dot.exe";
				compileDotFile(filePathToGraphviz, astOutputFile);
			}
		}
		
		//CONTEXTUAL ANALYSIS
		try {
			ContextualAnalyzer contextual = new ContextualAnalyzer();
			decoratedAst = contextual.check(ast);
		} catch (CompileException e) {			
			//Error position set
			System.out.println("Context error at line: " + e.getErrorLine() + ", column " + e.getErrorColumn());
			System.out.println(e.getMessage());
			System.out.println(getErrorLine(e.getErrorLine(), sourceCode));
			System.out.println(buildDebugSnake(e.getErrorColumn()));				
			return false;
		} catch (IdentException e) {			
			System.out.println("Context error!");
			System.out.println(e.getMessage());
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		//CODE GENERATION
		try {
			CodeGenerator codegen = new CodeGenerator();
			codegen.generateCode(decoratedAst, outputDir);
		} catch (CompileException e) {
			System.out.println("Error at line: " + e.getErrorLine() + ", column " + e.getErrorColumn());
			System.out.println(e.getMessage());
			System.out.println(getErrorLine(e.getErrorLine(), sourceCode));
			System.out.println(buildDebugSnake(e.getErrorColumn()));			
			return false;
		} catch (IOException e) {
			System.out.println("Compiler cannot write to disk.");
			System.out.println("File and cause: " + e.getMessage());
			return false;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	public static IAST getAst() {
		return ast;
	}

	public static String getErrorLine(int line, String source){
		int lineCounter = 0;
		int charPos = 0;
		StringBuilder errorLine = new StringBuilder();

		/*
		 * We find the position of the line causing the error. 
		 */
		while(lineCounter < line-1) {
			while(source.charAt(charPos) != '\n')
				charPos++;
			lineCounter++;
			charPos++;
		}		
		/*
		 * We build a string with the line causing the error.
		 */
		while(charPos < source.length() && source.charAt(charPos) != '\n' && source.charAt(charPos) != '\000'){
			errorLine.append(source.charAt(charPos));
			charPos++;
		}

		return errorLine.toString();
	}

	public static String buildDebugSnake (int length) {
		StringBuilder sb = new StringBuilder();

		for (int i = 1; i < length; i++) {
			sb.append(' ');
		}

		sb.append('^');

		return sb.toString();
	}
	
	private static void compileDotFile(String dotCompiler, File astOutputFile) throws IOException {
		String[] printPng = new String[] {dotCompiler, astOutputFile.getAbsolutePath(), "-Gdpi=400", "-Tpng", "-s300", "-O" };
		String[] printEps = new String[] {dotCompiler, astOutputFile.getAbsolutePath(), "-Teps", "-s300", "-O" };
		String[] printSvg = new String[] {dotCompiler, astOutputFile.getAbsolutePath(), "-Tsvg", "-s300", "-O" };
		
		ArrayList<String[]> commands = new ArrayList<String[]>();
		commands.add(printPng);	commands.add(printEps);	commands.add(printSvg);
		
		//Run Grpahviz on the AST dot file
		for (String[] command : commands) {
			Runtime.getRuntime().exec(command);
		}
	}
}
