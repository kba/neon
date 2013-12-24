/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2012 - Maarten Driesen
 * 
 *	This program is free software; you can redistribute it and/or modify
 *	it under the terms of the GNU General Public License as published by
 *	the Free Software Foundation; either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	This program is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU General Public License for more details.
 *
 *	You should have received a copy of the GNU General Public License
 *	along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package neon.ui.console;

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.script.*;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.text.*;

/**
 * This code was adapted from the following forum post: 
 * http://www.javaprogrammingforums.com/java-swing-tutorials/4907-java-tip-jul-29-2010-swing-console-component.html
 * 
 * @author Andrew
 * @author mdriesen
 */
@SuppressWarnings("serial")
public class JConsole extends JTextArea implements KeyListener {
	private final ConsoleInputStream in;
	private CommandHistory history;
	private int	editStart;
	private boolean	running;
	private ScriptEngine engine;
	private ConsoleFilter filter;
	private JDialog frame;

	/**
	 * Initializes a console with the given <code>ScriptEngine</code> and the given parent window.
	 */
	public JConsole(ScriptEngine engine, JFrame parent) {
		frame = new JDialog(parent);
		frame.setTitle("Neon console");
		frame.setPreferredSize(new Dimension(400, 300));
		frame.setContentPane(new JScrollPane(this));

		// create streams that will link with this
		in = new ConsoleInputStream(this);
		// setup the command history
		history = new CommandHistory();
		// setup the script engine
		this.engine = engine;
		ScriptContext context = engine.getContext();
		context.setReader(in);
		context.setWriter(new ConsoleOutputStream(this));
		context.setErrorWriter(new ConsoleOutputStream(this));
		setTabSize(4);
		// setup the event handlers and input processing
		addKeyListener(this);
		// setup the document filter so output and old text can't be modified
		filter = new ConsoleFilter(this);
		((AbstractDocument)getDocument()).setDocumentFilter(filter);
		// start text and edit location
		setText(">>> ");
		getCaret().setDot(editStart);
	}

	/**
	 * Show this console.
	 */
	@Override
	public void show() {
		frame.pack();
		frame.setVisible(true);
	}
	
	@Override
	public void setText(String text) {
		setText(text, true);
	}

	/**
	 * @param text
	 * @param updateEditStart
	 */
	protected void setText(String text, boolean updateEditStart) {
		filter.useFilters = false;
		super.setText(text);
		filter.useFilters = true;
		if(updateEditStart) {
			editStart = text.length();
		}
		getCaret().setDot(text.length());
	}

	private static class ConsoleFilter extends DocumentFilter {
		private JConsole console;
		public boolean useFilters;

		public ConsoleFilter(JConsole console) {
			this.console = console;
			useFilters = true;
		}

		@Override
		public void insertString(DocumentFilter.FilterBypass fb, int offset, String string, AttributeSet attr)
				throws BadLocationException {
			if(useFilters) {
				// determine if we can insert
				if (console.getSelectionStart() >= console.editStart) {
					// can insert
					fb.insertString(offset, string, attr);
				} else {
					// insert at the end of the document
					fb.insertString(console.getText().length(), string, attr);
					// move cursor to the end
					console.getCaret().setDot(console.getText().length());
					// console.setSelectionEnd(console.getText().length());
					// console.setSelectionStart(console.getText().length());
				}
			} else {
				fb.insertString(offset, string, attr);
			}
		}

		@Override
		public void replace(DocumentFilter.FilterBypass fb, int offset, int length, String text, AttributeSet attrs)
				throws BadLocationException {
			if(useFilters) {
				// determine if we can replace
				if(console.getSelectionStart() >= console.editStart) {
					// can replace
					fb.replace(offset, length, text, attrs);
				} else {
					// insert at end
					fb.insertString(console.getText().length(), text, attrs);
					// move cursor to the end
					console.getCaret().setDot(console.getText().length());
					// console.setSelectionEnd(console.getText().length());
					// console.setSelectionStart(console.getText().length());
				}
			} else {
				fb.replace(offset, length, text, attrs);
			}
		}

		@Override
		public void remove(DocumentFilter.FilterBypass fb, int offset, int length) throws BadLocationException {
			if(useFilters) {
				if(offset > console.editStart) {
					// can remove
					fb.remove(offset, length);
				} else {
					// only remove the portion that's editable
					fb.remove(console.editStart, length - (console.editStart - offset));
					// move selection to the start of the editable section
					console.getCaret().setDot(console.editStart);
					// console.setSelectionStart(console.editStart);
					// console.setSelectionEnd(console.editStart);
				}
			} else {
				fb.remove(offset, length);
			}
		}
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e){}
	public void keyPressed(KeyEvent e) {
		if(e.isControlDown()) {
			if(e.getKeyCode() == KeyEvent.VK_A && !e.isShiftDown() && !e.isAltDown()) {
				// handle select all
				// if selection start is in the editable region, try to select
				// only editable text
				if(getSelectionStart() >= editStart) {
					// however, if we already have the editable region selected,
					// default select all
					if(getSelectionStart() != editStart || getSelectionEnd() != this.getText().length()) {
						setSelectionStart(editStart);
						setSelectionEnd(this.getText().length());
						// already handled, don't use default handler
						e.consume();
					}
				}
			}
		} else if(e.getKeyCode() == KeyEvent.VK_DOWN && !e.isShiftDown() && !e.isAltDown()) {
			// next in history
			StringBuilder temp = new StringBuilder(getText());
			// remove the current command
			temp.delete(editStart, temp.length());
			temp.append(history.getNextCommand());
			setText(temp.toString(), false);
			e.consume();
		} else if(e.getKeyCode() == KeyEvent.VK_UP && !e.isShiftDown() && !e.isAltDown()) {
			// prev in history
			StringBuilder temp = new StringBuilder(getText());
			// remove the current command
			temp.delete(editStart, temp.length());
			temp.append(history.getPrevCommand());
			setText(temp.toString(), false);
			e.consume();
		} else if(e.getKeyCode() == KeyEvent.VK_ENTER) {
			// handle script execution
			if(!e.isShiftDown() && !e.isAltDown()) {
				if(running) {
					// we need to put text into the input stream
					StringBuilder text = new StringBuilder(this.getText());
					text.append(System.getProperty("line.separator"));
					String command = text.substring(editStart);
					setText(text.toString());
					in.addText(command);
				} else {
					// run the engine
					StringBuilder text = new StringBuilder(this.getText());
					String command = text.substring(editStart);
					text.append(System.getProperty("line.separator"));
					setText(text.toString());
					// add to the history
					history.add(command);
					// run on a separate thread
					Thread scriptThread = new Thread(new JavaScriptRunner(command));
					// so this thread can't hang JVM shutdown
					scriptThread.setDaemon(true);
					scriptThread.start();
				}
				e.consume();
			} else if (!e.isAltDown()) {
				// shift+enter
				StringBuilder text = new StringBuilder(this.getText());
				if(getSelectedText() != null) {
					// replace text
					text.delete(getSelectionStart(), getSelectionEnd());
				}
				text.insert(getSelectionStart(), System.getProperty("line.separator"));
				setText(text.toString(), false);
			}
		} else if (e.getKeyCode() == KeyEvent.VK_HOME) {
			int selectStart = getSelectionStart();
			if(selectStart > editStart) {
				// we're after edit start, see if we're on the same line as edit
				// start
				for(int i = editStart; i < selectStart; i++) {
					if(this.getText().charAt(i) == '\n') {
						// not on the same line
						// use default handle
						return;
					}
				}
				if(e.isShiftDown()) {
					// move to edit start
					getCaret().moveDot(editStart);
				} else {
					// move select end, too
					getCaret().setDot(editStart);
				}
				e.consume();
			}
		}
	}

	private class JavaScriptRunner implements Runnable {
		private String commands;

		public JavaScriptRunner(String commands) {
			this.commands = commands;
		}

		@Override
		public void run() {
			running = true;
			try {
				engine.eval(commands);
			} catch(ScriptException e) {
				e.printStackTrace();
			}
			StringBuilder text = new StringBuilder(getText());
			text.append(">>> ");
			setText(text.toString());
			running = false;
		}
	}
}
