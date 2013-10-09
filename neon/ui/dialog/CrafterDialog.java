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

package neon.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import javax.swing.*;
import javax.swing.border.*;
import neon.core.Engine;
import neon.core.handlers.InventoryHandler;
import neon.entities.Creature;
import neon.entities.EntityFactory;
import neon.entities.Item;
import neon.entities.Player;
import neon.resources.RCraft;
import neon.ui.Client;

public class CrafterDialog implements KeyListener {
	private JDialog frame;
	private JFrame parent;
	private Player player;
	private JList<RCraft> items;
	private JPanel panel;
	private String coin;
	
	public CrafterDialog(JFrame parent, String coin) {
		this.parent = parent;
		this.coin = coin;
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
        
		// lijst met recepten
		items = new JList<RCraft>();
		items.setFocusable(false);
		items.setCellRenderer(new CraftCellRenderer());
        JScrollPane scroller = new JScrollPane(items);
        items.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	scroller.setBorder(new TitledBorder("Items"));
        panel.add(scroller, BorderLayout.CENTER);
        
        // instructies geven
		JLabel instructions = new JLabel("Use arrow keys to select item, press enter to craft, esc to exit.");
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
	
	public void show(Player player, Creature crafter) {
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
				RCraft craft = items.getSelectedValue();
				if(player.getMoney() >= craft.cost) {
					Collection<Long> removed = InventoryHandler.removeItems(player, craft.raw, craft.amount);
					for(long uid : removed) {	// gebruikte items verwijderen
						Engine.getStore().removeEntity(uid);
					}
					Item item = EntityFactory.getItem(craft.name, Engine.getStore().createNewEntityUID());
					Engine.getStore().addEntity(item);
					player.inventory.addItem(item.getUID());
					player.addMoney(-craft.cost);
					Client.getUI().showMessage("Item crafted.", 2);
					initItems();
				} else {
					Client.getUI().showMessage("You don't have enough money.", 2);
				}
			} catch (ArrayIndexOutOfBoundsException f) {
				Client.getUI().showMessage("No item selected.", 2);
			}
			break;
		}
	}
	
	private void initItems() {
		DefaultListModel<RCraft> model = new DefaultListModel<RCraft>();
		for(RCraft thing : Engine.getResources().getResources(RCraft.class)) {
			if(InventoryHandler.getAmount(player, thing.raw) >= thing.amount) {
				model.addElement(thing);
			}
		}
		items.setModel(model);
		items.setSelectedIndex(0);
	}
	
	private class CraftCellRenderer implements ListCellRenderer<RCraft> {
		private UIDefaults defaults = UIManager.getLookAndFeelDefaults();

		/**
		 * Returns this renderer with the right properties (color, font, background color).
		 * 
		 * @param list			the list that contains the cell
		 * @param value			the object that is contained in the cell
		 * @param index			the index of the cell in the list
		 * @param isSelected	whether the cell is selected
		 * @param cellHasFocus	whether the cell has keyboard focus
		 * @return				this <code>InventoryCellRenderer</code>
		 */
	    public Component getListCellRendererComponent(JList<? extends RCraft> list, 
	    		RCraft value, int index, boolean isSelected, boolean cellHasFocus) {
	    	JLabel label = new JLabel(value + " (" + value.amount + " " + value.raw  + 
	    			", " + value.cost + " " + coin + ")");
	        
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
