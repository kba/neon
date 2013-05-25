/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2013 - Maarten Driesen
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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.border.*;
import neon.core.Engine;
import neon.objects.entities.Creature;
import neon.objects.entities.Player;
import neon.objects.resources.RTattoo;

public class TattooDialog implements KeyListener {
	private JDialog frame;
	private JFrame parent;
	private Player player;
	private JList<RTattoo> tattoos;
	private JPanel panel;
	private String coin;
	
	public TattooDialog(JFrame parent, String coin) {
		this.coin = coin;
		this.parent = parent;
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
        
		// lijst met tattoos
		tattoos = new JList<RTattoo>();
		tattoos.setFocusable(false);
		tattoos.setCellRenderer(new TattooCellRenderer());
        JScrollPane scroller = new JScrollPane(tattoos);
        tattoos.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	scroller.setBorder(new TitledBorder("Tattoos"));
        panel.add(scroller, BorderLayout.CENTER);
        
        // instructies geven
		JLabel instructions = new JLabel("Use arrow keys to select a tattoo, press enter to draw, esc to exit.");
    	instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
        panel.add(instructions, BorderLayout.PAGE_END);

 		frame.setContentPane(panel);
        frame.addKeyListener(this);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show(Player player, Creature tattooist) {
		this.player = player;
		initTattoos();
		
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
			if(tattoos.getSelectedIndex() > 0) {
				tattoos.setSelectedIndex(tattoos.getSelectedIndex()-1);
			}
			break;
		case KeyEvent.VK_DOWN:
			tattoos.setSelectedIndex(tattoos.getSelectedIndex()+1); 
			break;
		case KeyEvent.VK_ENTER:
			try {
				RTattoo tattoo = tattoos.getSelectedValue();
				if(!player.getTattoos().contains(tattoo)) {
					if(player.getMoney() >= tattoo.cost) {
						player.addTattoo(tattoo);
						Engine.getUI().showMessage("You got the tattoo '" + tattoo.name + "'.", 2);
						player.addMoney(-tattoo.cost);
						initTattoos();
					} else {
						Engine.getUI().showMessage("You don't have enough money.", 2);						
					}
				} else {
					Engine.getUI().showMessage("You already have that tattoo.", 2);
				}
			} catch (Exception f) {
				Engine.getUI().showMessage("There is nothing left to buy.", 2);
			} 
			break;
		}
	}
	
	private void initTattoos() {
		tattoos.setListData(Engine.getResources().getResources(RTattoo.class));
    	tattoos.setSelectedIndex(0);
	}
	
	private class TattooCellRenderer implements ListCellRenderer<RTattoo> {
		private UIDefaults defaults = UIManager.getLookAndFeelDefaults();

	    public Component getListCellRendererComponent(JList<? extends RTattoo> list, 
	    		RTattoo value, int index, boolean isSelected, boolean cellHasFocus) {
	    	JLabel label = new JLabel(value.name + " (" + value.cost + " " + coin + ")");
	        
			if(isSelected) {
				label.setBackground(defaults.getColor("List.selectionBackground"));
				label.setForeground(defaults.getColor("List.selectionForeground"));
			} else {
				label.setForeground(defaults.getColor("List.foreground"));
			}
			label.setOpaque(isSelected);
			return label;
	    }			
	}
}
