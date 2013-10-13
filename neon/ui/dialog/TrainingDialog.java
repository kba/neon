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
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.EventObject;
import javax.swing.*;
import javax.swing.border.*;
import org.jdom2.Element;
import neon.core.Engine;
import neon.entities.Creature;
import neon.entities.Player;
import neon.entities.property.Skill;
import neon.ui.UserInterface;
import neon.util.fsm.TransitionEvent;
import neon.resources.RPerson;
import net.engio.mbassy.bus.MBassador;

public class TrainingDialog implements KeyListener {
	private JDialog frame;
	private Player player;
	private JList<Skill> skills;
	private Creature trainer;	// uw trainer
	private JScrollPane scroller;
	private MBassador<EventObject> bus;
	private UserInterface ui;
	
	public TrainingDialog(UserInterface ui, MBassador<EventObject> bus) {
		this.bus = bus;
		this.ui = ui;
		JFrame parent = ui.getWindow();
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		JPanel contents = new JPanel(new BorderLayout());
		contents.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
		frame.setContentPane(contents);
		
		// lijst met recepten
		skills = new JList<Skill>();
		skills.setFocusable(false);
        skills.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        scroller = new JScrollPane(skills);
    	scroller.setBorder(new TitledBorder("Skills"));
    	contents.add(scroller);
		
		JLabel instructions = new JLabel("Use arrow keys to select a skill, press enter to train, esc to exit.");
		instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
		contents.add(instructions, BorderLayout.PAGE_END);
		
        frame.addKeyListener(this);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show(Player player, Creature trainer) {
		this.trainer = trainer;
		this.player = player;
		initTraining();
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: 
			frame.dispose();
			break;
		case KeyEvent.VK_UP:
			if(skills.getSelectedIndex() > 0) {
				skills.setSelectedIndex(skills.getSelectedIndex()-1);
			}
			break;
		case KeyEvent.VK_DOWN: 
			skills.setSelectedIndex(skills.getSelectedIndex()+1); 
			break;
		case KeyEvent.VK_ENTER:
			try {
				train(skills.getSelectedValue());
				ui.showMessage("Training finished.", 2);
				// terug naar gameModule
				frame.dispose();
				bus.publishAsync(new TransitionEvent("return"));
			} catch (ArrayIndexOutOfBoundsException f) {
				ui.showMessage("No skill selected.", 2);
			}
			break;
		}
	}
	
	private void initTraining() {
		DefaultListModel<Skill> model = new DefaultListModel<Skill>();
		for(Element e : ((RPerson)Engine.getResources().getResource(trainer.getName())).services) {
			if(e.getAttributeValue("id").equals("training")) {
				for(Element skill : e.getChildren()) {
					model.addElement(Skill.valueOf(skill.getText().toUpperCase()));
				}
			}
		}
		skills.setModel(model);
    	skills.setSelectedIndex(0);
	}
	
	private void train(Skill skill) {
		player.trainSkill(skill, 1);
	}
}
