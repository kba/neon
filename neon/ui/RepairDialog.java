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
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import javax.swing.*;
import javax.swing.border.*;
import neon.core.Engine;
import neon.objects.entities.Armor;
import neon.objects.entities.Creature;
import neon.objects.entities.Item;
import neon.objects.entities.Player;
import neon.objects.entities.Weapon;

public class RepairDialog implements KeyListener {
	private JDialog frame;
	private JFrame parent;
	private Player player;
	private JList<Item> items;
	private ArrayList<Item> listData;	
	
	public RepairDialog(JFrame parent) {
		this.parent = parent;
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		JPanel contents = new JPanel(new BorderLayout());
		contents.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
		frame.setContentPane(contents);
		
		items = new JList<Item>();
		items.setFocusable(false);
        JScrollPane scroller = new JScrollPane(items);
        items.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	scroller.setBorder(new TitledBorder("Items in need of repair"));
    	contents.add(scroller, BorderLayout.CENTER);
		
		JLabel instructions = new JLabel("Use arrow keys to select item, press enter to repair, esc to exit.");
		instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
		contents.add(instructions, BorderLayout.PAGE_END);
		
        frame.addKeyListener(this);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show(Player player, Creature repairer) {
		this.player = player;
		initItems();
		
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
			if(items.getSelectedIndex() > 0) {
				items.setSelectedIndex(items.getSelectedIndex()-1);
			}
			break;
		case KeyEvent.VK_DOWN:
			items.setSelectedIndex(items.getSelectedIndex()+1); 
			break;
		case KeyEvent.VK_ENTER:
			try {
				Object item = items.getSelectedValue();
				if(item instanceof Armor) {
					((Armor)item).setState(100);
				} else if(item instanceof Weapon) {
					((Weapon)item).setState(100);					
				}
				initItems();
				Engine.getUI().showMessage("Item repaired.", 2);
			} catch (ArrayIndexOutOfBoundsException f) {
				Engine.getUI().showMessage("No item selected.", 2);
			}
			break;
		}
	}

	private void initItems() {
		listData = new ArrayList<Item>();
		for(long uid : player.inventory) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item instanceof Weapon && ((Weapon)item).getState() < 100) {
				listData.add(item);
			} else if(item instanceof Armor && ((Armor)item).getState() < 100) {
				listData.add(item);				
			}
		}
		items.setListData(listData.toArray(new Item[0]));
		items.setSelectedIndex(0);
	}
}
