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

import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.*;
import neon.core.Engine;
import neon.core.GameLoader;
import neon.objects.entities.Player;
import neon.objects.property.Gender;
import neon.objects.resources.RCreature;
import neon.objects.resources.RSign;
import neon.util.fsm.TransitionEvent;

public class NewGameDialog {
	private JDialog frame;
	private JFrame parent;
	private JComboBox<String> race;
	private JComboBox<RSign> signBox;
	private JComboBox<Gender> gender;
	private JComboBox<Player.Specialisation> spec;
	private JPanel main;
	private JTextField name, prof;
	private HashMap<String, String> raceList;
	private Engine engine;
		
	public NewGameDialog(JFrame parent, Engine engine) {
		this.engine = engine;
		this.parent = parent;
		frame = new JDialog(parent, false);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		frame.setTitle("Options");
		
		main = new JPanel(new BorderLayout());

		// uitleg in bovenste deel van scherm
		JLabel instructions = new JLabel("Use tab to move between options. Press enter to accept, esc to cancel");
		instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));

		// naam, beroep, ras, geslacht, specialisatie
        JPanel middle = new JPanel(new GridLayout(0,1));
		// naam
        JPanel namePanel = new JPanel();
        namePanel.setBorder(new TitledBorder("Name"));
		name = new JTextField(20);
		namePanel.add(name);
		middle.add(namePanel);
		// beroep
        JPanel profPanel = new JPanel();
        profPanel.setBorder(new TitledBorder("Profession"));
		prof = new JTextField("adventurer", 20);
		profPanel.add(prof);
		middle.add(profPanel);
		// ras
        JPanel racePanel = new JPanel();
        raceList = new HashMap<String, String>();
        for(String s : engine.getConfig().getPlayableRaces()) {
        	raceList.put(((RCreature)Engine.getResources().getResource(s)).getName(), s);
        }
        race = new JComboBox<String>(raceList.keySet().toArray(new String[raceList.size()]));
        racePanel.add(race);
        racePanel.setBorder(new TitledBorder("Race"));
        middle.add(racePanel);
		// geslacht
        JPanel genderPanel = new JPanel();
        gender = new JComboBox<Gender>(Gender.values());
        genderPanel.add(gender);
        genderPanel.setBorder(new TitledBorder("Gender"));
        middle.add(genderPanel);
		// specialisatie
        JPanel specPanel = new JPanel();
        spec = new JComboBox<Player.Specialisation>(Player.Specialisation.values());
        specPanel.add(spec);
		specPanel.setBorder(new TitledBorder("Specialisation"));
		middle.add(specPanel);
		
		// birthsign
        JPanel signPanel = new JPanel();
        signBox = new JComboBox<RSign>(Engine.getResources().getResources(RSign.class));
        signPanel.add(signBox);
		signPanel.setBorder(new TitledBorder("Birthsign"));
		middle.add(signPanel);
		
		// OK en cancel knop
		JPanel bottom = new JPanel();
		Action ok = new Ok("ok");
		JButton okButton = new JButton(ok);
		bottom.add(okButton);
		Action cancel = new Cancel("Cancel");
		JButton cancelButton = new JButton(cancel);
		bottom.add(cancelButton);		
		
		// alles toevoegen aan main
		main.add(instructions, BorderLayout.PAGE_START);
		main.add(middle, BorderLayout.CENTER);
		main.add(bottom, BorderLayout.PAGE_END);		

        main.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ENTER"), "enter");
        main.getActionMap().put("enter", ok);
        main.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke("ESCAPE"), "esc");
        main.getActionMap().put("esc", cancel);
		
		main.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
		frame.setContentPane(main);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show() {
		name.requestFocus();
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);	
	}

	@SuppressWarnings("serial")
	private class Ok extends AbstractAction {
		public Ok(String text) {
			super(text);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(name.getText().equals("")) {
				Engine.getUI().showMessage("Please give a name.", 2);
				name.requestFocus();
			} else if(checkSaves(name.getText())) {
				Engine.getUI().showMessage("There is already a character with the given name. <br>" +
						"Choose another name or remove the existing character.", 3);
				name.requestFocus();
			} else {
				new GameLoader(engine.getConfig()).initGame(raceList.get(race.getSelectedItem()), name.getText(), 
						(Gender)gender.getSelectedItem(), (Player.Specialisation)spec.getSelectedItem(), 
						prof.getText(), (RSign)signBox.getSelectedItem());
				Engine.post(new TransitionEvent("start"));
				frame.dispose();
			}
		}
		
		private boolean checkSaves(String name) {
			File save = new File("saves/" + name);

			if(save.exists()) {
				return true;
			} else {
				return false;
			}
		}
	}

	@SuppressWarnings("serial")
	private class Cancel extends AbstractAction {
		public Cancel(String text) {
			super(text);
		}
		
		public void actionPerformed(ActionEvent e) {
			frame.dispose();
		}
	}
}
