package run;

import javax.swing.*;
import javax.swing.JTextPane;

import java.awt.Font;
import java.io.PrintStream;
 
class WindowFrame extends JFrame {
    /**
	 * Window Frame used for redirecting std. out. from terminal to GUI area.
	 * Is used when user do not provide sufficient arguments to the compiler.
	 * That is, typical when the compiler is invoked by double clicking it.
	 */
	private static final long serialVersionUID = 1L;
	//declare PrintStream and JTextArea
    private static PrintStream ps = null;
    private JTextPane textPane = new JTextPane();
    private JScrollPane slider = new JScrollPane(textPane);
    
    //constructor
    public WindowFrame(int width, int height) {
    	textPane.setFont(Font.decode("monospaced-12"));
    	textPane.setEditable(false);
    	setDefaultCloseOperation(EXIT_ON_CLOSE);
    	setSize(width, height);
		setTitle("Yet Another KittyCraneium Compiler - Output Window");
		getContentPane().add(slider);
	 
		//this is the trick: overload the println(String)
		//method of the PrintStream
		//and redirect anything sent to this to the text box
		ps = new PrintStream(System.out) {
			public void println(String x) {
			    textPane.setText(textPane.getText() + x + "\n");
			}
			public void println(int x) {
				textPane.setText(textPane.getText() + String.valueOf(x) + "\n");
			}
		};
    }
 
    public PrintStream getPs() {
    	return ps;
    }
}
