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

package neon.ui;

import javax.script.ScriptEngine;
import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import neon.ui.console.JConsole;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;

/**
 * Implements the main user interface of the neon engine. Any engine state can 
 * register a <code>JComponent</code> to show. 
 * 
 * @author	mdriesen
 */
public class UserInterface {	
	private JFrame window;
	private Popup popup;
	private HelpWindow help;
	
    /**
     * Initialize the user interface, using data provided by the engine.
     * 
     * @param title	the title of the window
     */
	public UserInterface(String title) {
		window = new JFrame("Neon: " + title);
    	window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	window.setMinimumSize(new Dimension(800, 480));
    	window.setPreferredSize(new Dimension(1024, 600));
	}
	
    /**
     * Starts running this user interface by making the main window visible.
     */
	public void show() {
    	window.pack();
		window.setVisible(true);
		window.requestFocus();

    	// centreren op scherm
    	window.setLocationRelativeTo(null);
	}
	
    /**
     * Shows a <code>JComponent</code> by making it the ContentPane of the main JFrame.
     * 
     * @param panel	the component to show
     */
	public void showPanel(JComponent panel) {
		panel.setPreferredSize(window.getContentPane().getSize());
		window.setContentPane(panel);
		window.setPreferredSize(window.getSize());	// anders resize naar minimumSize
		window.pack();	// anders is er niet veel te zien
		panel.requestFocus();
	}
	
	/**
	 * @return	the main JFrame
	 */
	public JFrame getWindow() {
		return window;
	}
	
	/**
	 * Shows a message on screen for the time given.
	 * 
	 * @param message	the message to show, html code is allowed
	 * @param time		the time in seconds
	 */
	public void showMessage(String message, int time, int pos) {
		JLabel label = new JLabel("<html><center>" + message + "</center></html>");
		label.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(5,10,10,10)));
		JDialog dialog = new JDialog(window, false);
		dialog.setAlwaysOnTop(true);
		dialog.add(label);
		dialog.setUndecorated(true);

		Timer timer = new Timer(1000 * time, new DialogListener(dialog));
		timer.setRepeats(false);
		timer.start();
		
		dialog.pack();
		int x = window.getX() + (window.getWidth() - dialog.getWidth())/2;
		int y = window.getY() + (window.getHeight()*9/10 - dialog.getHeight())/2;
		
		switch(pos) {
		case SwingConstants.CENTER: 
			break;
		default:
			x = window.getX() + (window.getWidth() - label.getPreferredSize().width)/2;
			y = window.getY() + window.getContentPane().getHeight() - label.getPreferredSize().height;
			break;
		}
		
		dialog.setLocation(x, y);
		dialog.setVisible(true);
		window.getContentPane().requestFocus();
	}

	/**
	 * Shows a message centered on screen for the time given.
	 * 
	 * @param message	the message to show, html code is allowed
	 * @param time		the time in seconds
	 */
	public void showMessage(String message, int time) {
		showMessage(message, time, SwingConstants.CENTER);
	}
	
	/**
	 * Shows a yes/no question centered on screen.
	 * 
	 * @param question	the message to show, html code is allowed
	 * @return	the answer to the question
	 */
	public boolean showQuestion(String question) {
		JPanel content = new JPanel(new BorderLayout());
		content.add(new JLabel("<html><center>" + question + "</center></html>"));
		JPanel buttons = new JPanel();
		JButton yes = new JButton("Yes");
		yes.setMnemonic('Y');
		JButton no = new JButton("No");
		no.setMnemonic('N');
		buttons.add(yes);
		buttons.add(no);
		content.add(buttons, BorderLayout.PAGE_END);
		content.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));
		JDialog dialog = new JDialog(window, true);
		dialog.setContentPane(content);
		dialog.setUndecorated(true);

		DialogListener listener = new DialogListener(dialog);
		yes.addActionListener(listener);
		no.addActionListener(listener);
        content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('y'), "Yes");
        content.getActionMap().put("Yes", listener);
        content.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke('n'), "No");
        content.getActionMap().put("No", listener);

		dialog.pack();
        int x = window.getX() + (window.getWidth() - dialog.getWidth())/2;
        int y = window.getY() + (window.getHeight() - dialog.getHeight())/2;
        dialog.setLocation(x, y);
		dialog.setVisible(true);
		
		return listener.answer;
	}
	
	/**
	 * Shows a popup message at the bottom of the screen.
	 * 
	 * @param message	the message to show, html code is allowed
	 * @return	the {@code Popup}
	 */
	public Popup showPopup(String message) {
		JLabel label = new JLabel("<html><center>" + message + "</center></html>");
		label.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(4,10,8,10)));
		int x = window.getX() + (window.getWidth() - label.getPreferredSize().width)/2;
		int y = window.getY() + window.getContentPane().getHeight() - label.getPreferredSize().height;

		if(popup != null) {
			popup.hide();
		}

		popup = PopupFactory.getSharedInstance().getPopup(window, label, x, y);
		popup.show();
		
		return popup;
	}
	
	/**
	 * Shows the console
	 */
	public void showConsole(ScriptEngine engine) {
		JConsole console = new JConsole(engine, window);
		console.show();
	}
	
	/**
	 * Shows the given string in a help window.
	 */
	public void showHelp(String text) {
		if(help == null) {
			help = new HelpWindow(window);
		}
		help.show("Neon help", text);
	}
	
	public void update() {
		window.getContentPane().repaint();
	}
	
	/**
	 * @return	the inner dimensions of the main window
	 */
	public Dimension getScreenSize() {
		return window.getContentPane().getSize();
	}
	
	@SuppressWarnings("serial")
	private class DialogListener extends AbstractAction {
		private JDialog dialog;
		private boolean answer;
		
		public DialogListener(JDialog dialog) {
			this.dialog = dialog;
		}
		
		public void actionPerformed(ActionEvent e) {
			answer = "Yes".equals(e.getActionCommand()) || "y".equals(e.getActionCommand());
			dialog.dispose();
			window.repaint();
		}
	}
}
