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

package neon.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.*;
import javax.swing.border.*;
import neon.core.Engine;
import neon.core.GameLoader;
import neon.util.fsm.State;
import neon.util.fsm.TransitionEvent;

public class LoadGameDialog {
	private JDialog frame;
	private JFrame parent;
	private JList<String> games;
	private JPanel menu;
	private State state;
	
	public LoadGameDialog(JFrame parent, State state) {
		this.state = state;
		this.parent = parent;
		frame = new JDialog(parent, false);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		frame.setTitle("Options");
		
		menu = new JPanel();
		menu.setLayout(new BorderLayout());
		
		// instructies
    	JLabel instructions = new JLabel("Use arrow keys to select a saved game, press enter to start, esc to cancel.");
    	instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
		menu.add(instructions, BorderLayout.PAGE_START);
		
		// lijst met items
		games = new JList<String>();
		games.setFocusable(false);
        JScrollPane scroller = new JScrollPane(games);
        games.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	scroller.setBorder(new TitledBorder("Saved games"));
    	menu.add(scroller, BorderLayout.CENTER);

		// OK en cancel knop
		JPanel bottom = new JPanel();
		Action ok = new ButtonAction("Ok", "ok");
		JButton okButton = new JButton(ok);
		bottom.add(okButton);
		Action cancel = new ButtonAction("Cancel", "esc");
		JButton cancelButton = new JButton(cancel);
		bottom.add(cancelButton);
		menu.add(bottom, BorderLayout.PAGE_END);
		
		// keybindings
        menu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(0x26, 0), "up");
        menu.getActionMap().put("up", new Up());
        menu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(0x28, 0), "down");
        menu.getActionMap().put("down", new Down());
        menu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
        menu.getActionMap().put("enter", ok);
        menu.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "esc");
        menu.getActionMap().put("esc", cancel);
		
		menu.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
		frame.setContentPane(menu);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show() {
		initSaves();
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);	
	}

	private void initSaves() {
		File savedir = new File("saves");
		if(savedir.isDirectory()) {
			File[] files = savedir.listFiles();
			String[] saves = new String[files.length];
			for(int i = 0; i < files.length; i++) {
				saves[i] = files[i].getName();
			}
			games.setListData(saves);
		}
		games.setSelectedIndex(0);
	}
	
	// key actions
	@SuppressWarnings("serial")
	private class Up extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			if(games.getSelectedIndex() > 0) {
				games.setSelectedIndex(games.getSelectedIndex() - 1);
			}
		}
	}	
	
	@SuppressWarnings("serial")
	private class Down extends AbstractAction {
		public void actionPerformed(ActionEvent e) {
			games.setSelectedIndex(games.getSelectedIndex() + 1); 
		}
	}
	
	@SuppressWarnings("serial")
	private class ButtonAction extends AbstractAction {
		public ButtonAction(String text, String command) {
			super(text);
			putValue(ACTION_COMMAND_KEY, command);
		}
		
		public void actionPerformed(ActionEvent a) {
			if(a.getActionCommand().equals("ok")) {
				new GameLoader(Engine.getConfig()).loadGame((String)games.getSelectedValue());
				state.transition(new TransitionEvent("start"));
				frame.dispose();
			} else if(a.getActionCommand().equals("esc")) {
				frame.dispose();
			}
		}
	}
}
