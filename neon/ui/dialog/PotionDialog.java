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
import javax.swing.*;
import javax.swing.border.*;
import neon.core.Engine;
import neon.core.handlers.InventoryHandler;
import neon.entities.Creature;
import neon.entities.EntityFactory;
import neon.entities.Item;
import neon.entities.Player;
import neon.resources.RRecipe;
import neon.ui.UserInterface;

public class PotionDialog implements KeyListener {
	private JDialog frame;
	private Player player;
	private JList<RRecipe> potions;
	private String coin;
	private UserInterface ui;
	
	public PotionDialog(UserInterface ui, String coin) {
		this.ui = ui;
		this.coin = coin;
		JFrame parent = ui.getWindow();
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
        
		// lijst met recepten
		potions = new JList<RRecipe>();
		potions.setFocusable(false);
		potions.setCellRenderer(new PotionCellRenderer());
        JScrollPane scroller = new JScrollPane(potions);
        potions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	scroller.setBorder(new TitledBorder("Recipes"));
        panel.add(scroller, BorderLayout.CENTER);
        
        // instructies geven
		JLabel instructions = new JLabel("Use arrow keys to select potion, press enter to brew, esc to exit.");
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
	
	public void show(Player player, Creature mixer) {
		this.player = player;
		initPotions();
		
		frame.pack();
		frame.setLocationRelativeTo(null);
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
			if(potions.getSelectedIndex() > 0) {
				potions.setSelectedIndex(potions.getSelectedIndex()-1);
			}
			break;
		case KeyEvent.VK_DOWN:
			potions.setSelectedIndex(potions.getSelectedIndex()+1); 
			break;
		case KeyEvent.VK_ENTER:
			try {
				RRecipe potion = potions.getSelectedValue();
				if(player.getMoney() >= potion.cost) {
					for(String item : potion.ingredients) {
						long uid = removeItem(player, item);
						Engine.getStore().removeEntity(uid);
					}
					Item item = EntityFactory.getItem(potion.toString(), Engine.getStore().createNewEntityUID());
					Engine.getStore().addEntity(item);
					player.inventory.addItem(item.getUID());
					player.addMoney(-potion.cost);
					initPotions();
					ui.showMessage("Potion created.", 2);
				} else {
					ui.showMessage("You don't have enough money.", 2);
				}
			} catch (ArrayIndexOutOfBoundsException f) {
				ui.showMessage("No potion selected.", 2);
			}
			break;
		}
	}
	
	private void initPotions() {
		DefaultListModel<RRecipe> model = new DefaultListModel<RRecipe>();
		for(RRecipe recipe : Engine.getResources().getResources(RRecipe.class)) {
			boolean ok = true;
			for(String item : recipe.ingredients) {
				if(!hasItem(player, item)) {
					ok = false;
				}
			}
			if(ok) {
				model.addElement(recipe);					
			}
		}
		potions.setModel(model);
		potions.setSelectedIndex(0);
	}
	
	private class PotionCellRenderer implements ListCellRenderer<RRecipe> {
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
	    public Component getListCellRendererComponent(JList<? extends RRecipe> list, 
	    		RRecipe value, int index, boolean isSelected, boolean cellHasFocus) {
	    	StringBuffer text = new StringBuffer();
	    	for(String item : value.ingredients) {
	    		text.append(item + ", ");
	    	}
	    	JLabel label = new JLabel(value + " (" + text + value.cost + " " + coin + ")");
	        
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
	
	private boolean hasItem(Creature creature, String item) {
		for(long uid : creature.inventory) {
			if(Engine.getStore().getEntity(uid).getID().equals(item)) {
				return true;
			}
		}
		return false;
	}

	private long removeItem(Creature creature, String id) {
		for(long uid : creature.inventory) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if(item.getID().equals(id)) {
				InventoryHandler.removeItem(creature, uid);
				return uid;
			}
		}
		return 0;
	}
}
