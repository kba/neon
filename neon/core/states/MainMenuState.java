/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2010 - Maarten Driesen
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

package neon.core.states;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.GridLayout;
import java.awt.event.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import neon.core.*;
import neon.ui.LoadGameDialog;
import neon.ui.NewGameDialog;
import neon.ui.OptionDialog;
import neon.util.fsm.State;
import neon.util.fsm.TransitionEvent;

public class MainMenuState extends State {
	private JPanel main;
	private Engine engine;
	
	public MainMenuState(Engine engine, Configuration config) {
		super(engine, "main menu");
		this.engine = engine;
		
		// het main menu JPanel zelf
		main = new JPanel(new BorderLayout());
		
		JPanel buttons = new JPanel(new GridLayout(0,1));
		buttons.setBorder(new EmptyBorder(70,120,70,120));

		JLabel title = new JLabel("<html><font size=\"18\">" + 
				config.getProperty("title") + "</font></html>", JLabel.CENTER);
		
		// de knoppen op het menu
		String newString = config.getString("$newGame", "New Game");
		Action n = new ButtonAction(newString, "n");
		JButton newGame = new JButton(n);
		newGame.setMnemonic(newString.charAt(0));
		String loadString = config.getString("$loadGame", "Load game");
		Action l = new ButtonAction(loadString, "l");    
		JButton load = new JButton(l);
		load.setMnemonic(loadString.charAt(0));
		String optionString = config.getString("$options", "Options");
		Action o = new ButtonAction(optionString, "o");
		JButton options = new JButton(o);
		options.setMnemonic(optionString.charAt(0));
		String quitString = config.getString("$quit", "Quit");
		Action q = new ButtonAction(quitString, "q");
		JButton quit = new JButton(q);
		quit.setMnemonic(quitString.charAt(0));
		JLabel contact = new JLabel("<html><u>http://sourceforge.net/projects/neon/</u></html>", JLabel.CENTER);
		contact.addMouseListener(new LabelListener());

		// knoppen aan menu toevoegen
		buttons.add(title);
		buttons.add(newGame);
		buttons.add(load);
		buttons.add(options);
		buttons.add(quit);
		buttons.add(contact);

		// keybindings
		InputMap map = main.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
		map.put(KeyStroke.getKeyStroke(newString.substring(0, 1)), "new");
		main.getActionMap().put("new", n);
		map.put(KeyStroke.getKeyStroke(loadString.substring(0, 1)), "load");
		main.getActionMap().put("load", l);
		map.put(KeyStroke.getKeyStroke(optionString.substring(0, 1)), "options");
		main.getActionMap().put("options", o);
		map.put(KeyStroke.getKeyStroke(quitString.substring(0, 1)), "quit");
		map.put(KeyStroke.getKeyStroke("ESCAPE"), "esc");
		main.getActionMap().put("quit", q);
		main.getActionMap().put("esc", q);
		
		// versienummer
		main.add(buttons, BorderLayout.CENTER);
		main.add(new JLabel("release " + Configuration.version), BorderLayout.PAGE_END);
	}
	
	@Override
	public void enter(TransitionEvent t) {
		Engine.getUI().showPanel(main);
	}
	
	// knop action
	@SuppressWarnings("serial")
	private class ButtonAction extends AbstractAction {
		public ButtonAction(String text, String command) {
			super(text);
			putValue(ACTION_COMMAND_KEY, command);
		}
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("n")) {
				new NewGameDialog(Engine.getUI().getWindow(), engine).show();
			} else if(e.getActionCommand().equals("l")) {
				new LoadGameDialog(Engine.getUI().getWindow(), engine.getConfig()).show();
			} else if(e.getActionCommand().equals("o")) {
				new OptionDialog(Engine.getUI().getWindow()).show();
			} else if(e.getActionCommand().equals("q")) {
				System.exit(0);
			}
		}
	}
	
	private static class LabelListener implements MouseListener {
		public void mouseClicked(MouseEvent me) { }
		public void mouseEntered(MouseEvent me) { }
		public void mouseExited(MouseEvent me) { }
		public void mousePressed(MouseEvent me) {	}
		public void mouseReleased(MouseEvent me) {
			try {
				Desktop.getDesktop().browse(new URI("http://sourceforge.net/projects/neon"));
			} catch (IOException e) {
			} catch (URISyntaxException e) {
			}
		}
		
	}
}
