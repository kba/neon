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
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.HashMap;
import javax.swing.*;
import javax.swing.border.*;
import org.jdom2.Element;
import neon.core.Engine;
import neon.entities.Creature;
import neon.entities.Player;
import neon.ui.Client;
import neon.util.fsm.State;
import neon.util.fsm.TransitionEvent;
import neon.resources.RPerson;

public class TravelDialog implements KeyListener {
	private JDialog frame;
	private JFrame parent;
	private Player player;
	private JList<String> destinations;
	private Creature agent;	// uw reisagent
	private HashMap<String, Point> listData;
	private HashMap<String, Integer> costData;
	private JScrollPane scroller;
	private State dialog;
	
	public TravelDialog(JFrame parent, State dialog) {
		this.dialog = dialog;
		this.parent = parent;
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		JPanel contents = new JPanel(new BorderLayout());
		contents.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
		frame.setContentPane(contents);
		
		// lijst met bestemmingen
		destinations = new JList<String>();
		destinations.setFocusable(false);
        scroller = new JScrollPane(destinations);
        destinations.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	scroller.setBorder(new TitledBorder("Destinations"));
    	contents.add(scroller);
		
		JLabel instructions = new JLabel("Use arrow keys to select destination, press enter to travel, esc to exit.");
		instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
		contents.add(instructions, BorderLayout.PAGE_END);
		
        frame.addKeyListener(this);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show(Player player, Creature agent) {
		this.agent = agent;
		this.player = player;
		initDestinations();
		
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);	
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: 
			frame.dispose();
			break;
		case KeyEvent.VK_UP:
			if(destinations.getSelectedIndex() > 0) {
				destinations.setSelectedIndex(destinations.getSelectedIndex()-1);
			}
			break;
		case KeyEvent.VK_DOWN:
			destinations.setSelectedIndex(destinations.getSelectedIndex()+1); 
			break;
		case KeyEvent.VK_ENTER:
			try {
				if(player.getMoney() >= costData.get(destinations.getSelectedValue())) {
					travel(destinations.getSelectedValue());
					player.addMoney(-costData.get(destinations.getSelectedValue()));
					frame.dispose();
					dialog.transition(new TransitionEvent("return"));
				} else {
					Client.getUI().showMessage("You don't have enough money to go there.", 2);
				}
			} catch (ArrayIndexOutOfBoundsException f) {
				Client.getUI().showMessage("No destination selected.", 2);
			}
			break;
		}
	}
	
	private void initDestinations() {
		listData = new HashMap<String, Point>();
		costData = new HashMap<String, Integer>();
		for(Element e : ((RPerson)Engine.getResources().getResource(agent.getID())).services) {
			if(e.getAttributeValue("id").equals("travel")) {
				for(Element dest : e.getChildren()) {
					int x = Integer.parseInt(dest.getAttributeValue("x"));
					int y = Integer.parseInt(dest.getAttributeValue("y"));
					listData.put(dest.getAttributeValue("name") + ": " + dest.getAttributeValue("cost") + " cp", new Point(x, y));
					costData.put(dest.getAttributeValue("name") + ": " + dest.getAttributeValue("cost") + " cp", 
							Integer.parseInt(dest.getAttributeValue("cost")));
				}
			}
		}
		destinations.setListData(listData.keySet().toArray(new String[0]));
		destinations.setSelectedIndex(0);
	}
	
	private void travel(String point) {
		player.getBounds().setLocation(listData.get(point).x, listData.get(point).y);
	}
}
