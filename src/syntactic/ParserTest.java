package syntactic;

import ast.IAST;
import compiler.Compiler;
import java.io.*;

import syntactic.exceptions.CompileException;

public class ParserTest {
	
	public void testParser2() throws IOException{
		TokenList tokenList = null;
		Parser parser = null;
		/*String sourceCode =
			"CONTAINER myContainer;\n" +
			"NUMBER tal = 1+5+3;\n" +
			"!The following is the place to be\n" +
			"AREA nicePlace = [A1,A2];\n" +
			"AREA badPlace = [B4,C7];\n" +
			"EVENT CONTAINER container IN nicePlace WHERE IS container.type() EQUAL TO 'CoolerContainer'{\n" +
			"\tCONTAINER myContainer = container;\n" +
			"\tMOVE myContainer TO [A3];\n" +
			"\tIF(IS tal EQUAL TO (79+2)){\n" +
			"\t\tGOTO nicePlace;\n" +
			"\t}\n" +
			"\tOR(IS tal LESS THAN OR EQUAL TO (13+2)){\n" +
			"\t\tGOTO [A8];\n" +
			"\t}\n" +
			"}\n" +
			"!Flot program!\n" +
			"EVENT CONTAINER container IN badPlace WHERE IS container.type() EQUAL TO 'DangerousContainer'{\n" +
			"\tCONTAINER myContainer = container;\n" +
			"\tMOVE myContainer TO [A2];\n" +
			"\tIF(IS tal LESS THAN OR EQUAL TO ((7+2)*6+5)){\n" +
			"\t\tGOTO nicePlace;\n" +
			"\t}\n" +
			"\tOR(IS tal GREATER THAN OR EQUAL TO (13+2)){\n" +
			"\t\tGOTO [M2];\n" +
			"\t}\n" +
			"\tOR(IS tal GREATER THAN ((((4+49/39*19392)+indbygget.medisterMetoden(949))))){\n" +
			"\t\tGOTO [H8];\n" +
			"\t}\n" +
			"\tELSE {\n" +
			"\t\tminlolL23deL.kkkk3orii();\n" +
			"\t\tIF(IS tal EQUAL TO tal2){\n" +
			"\t\t\tIF(IS nested EQUAL TO working){ !Sure looks as it does!\n" +
			"\t\t\t\tSET minlolz = ((lolzSFA323.aa33()));\n" +
			"\t\t\t}\n" +
			"\t\t}\n" +
			"\t}\n" +
			"}\n";
		*/
		String sourceCode = readFile("C:\\Documents and Settings\\jkgeyti\\Desktop\\source.txt");
		System.out.println(sourceCode);
		try {
			Scanner scanner = new Scanner(sourceCode);
			tokenList = scanner.getTokens();
			System.out.println(tokenList);
		} catch (CompileException e) {
			System.out.println("Scanner Error:");
			System.out.println("Line: " + e.getErrorLine() + " Column: " + e.getErrorColumn() + " | " + e.getMessage());
			System.out.println(compiler.Compiler.getErrorLine(e.getErrorLine(), sourceCode));
			System.out.println(compiler.Compiler.buildDebugSnake(e.getErrorColumn()));
		}
		
		parser = new Parser();

		try {
			parser.parse(tokenList);
		} catch (CompileException e){
			System.out.println("Parser Error:");
			System.out.println("Line: " + e.getErrorLine() + " Column: " + e.getErrorColumn() + " | " + e.getMessage());
			System.out.println(compiler.Compiler.getErrorLine(e.getErrorLine(), sourceCode));
			System.out.println(compiler.Compiler.buildDebugSnake(e.getErrorColumn()));
		}
		
		//It should have been parsed by now.
		File outputGraphvizFile = new File("C:\\Documents and Settings\\jkgeyti\\Desktop\\ASTGraphviz.txt");
		ast.ASTPrinter printer = new ast.ASTPrinter();
		//File outputDir = new File("C:\\Users\\kfc\\Desktop");
		File outputDir = new File("C:\\Documents and Settings\\jkgeyti\\Desktop");
		System.out.println(Compiler.compile(sourceCode, outputDir, false));
		IAST ast = Compiler.getAst();		
		printer.print(ast, outputGraphvizFile);
		try {
			graphviz(outputGraphvizFile);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	public void graphviz(File outputGraphvizFile) throws Exception {
		String filePathToGraphviz = System.getenv("PROGRAMFILES(X86)");

		if (filePathToGraphviz == null) {
			filePathToGraphviz = System.getenv("PROGRAMFILES");
		}

		filePathToGraphviz += "\\Graphviz2.26.3\\bin\\dot.exe";

		//String[] cmds = new String[] {filePathToGraphviz, outputGraphvizFile.getAbsolutePath(), "-Tpng", "-s300", "-O" };
		String[] cmds = new String[] {filePathToGraphviz, outputGraphvizFile.getAbsolutePath(), "-Teps", "-s300", "-O" };
		Runtime.getRuntime().exec(cmds);
	}
	
	 private String readFile( String file ) {
		    try {
				BufferedReader reader = new BufferedReader( new FileReader (file));
				String line  = null;
				StringBuilder stringBuilder = new StringBuilder();
				String ls = System.getProperty("line.separator");
				while( ( line = reader.readLine() ) != null ) {
				    stringBuilder.append( line );
				    stringBuilder.append( ls );
				}
				return stringBuilder.toString();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return "nonsuccessful";
	 }
}
