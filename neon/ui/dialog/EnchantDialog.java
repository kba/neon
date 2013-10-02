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
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import neon.core.Engine;
import neon.entities.Clothing;
import neon.entities.Creature;
import neon.entities.Item;
import neon.entities.Player;
import neon.entities.Weapon;
import neon.entities.components.Enchantment;
import neon.magic.Effect;
import neon.resources.RSpell;

public class EnchantDialog implements KeyListener, ListSelectionListener {
	private JDialog frame;
	private JFrame parent;
	private JPanel panel;
	private JList<Item> itemList;
	private DefaultListModel<Item> itemModel;
	private JList<Effect> spellList;
	private DefaultListModel<Effect> spellModel;
	
	public EnchantDialog(JFrame parent) {
		this.parent = parent;
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
        
		itemModel = new DefaultListModel<Item>();
		itemList = new JList<Item>(itemModel);
		itemList.addKeyListener(this);
		itemList.addListSelectionListener(this);
        itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane itemScroller = new JScrollPane(itemList);
        itemScroller.setBorder(new TitledBorder("Items"));

        spellModel = new DefaultListModel<Effect>();
		spellList = new JList<Effect>(spellModel);
		spellList.addKeyListener(this);
        spellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JScrollPane spellScroller = new JScrollPane(spellList);
        spellScroller.setBorder(new TitledBorder("Enchantments"));
        
        JPanel listPanel = new JPanel(new GridLayout(0, 2));
        listPanel.add(itemScroller);
        listPanel.add(spellScroller);
        panel.add(listPanel, BorderLayout.CENTER);
        
        // instructies geven
		JLabel instructions = new JLabel("Use arrow keys to select item and enchantment, press enter to enchant, esc to exit.");
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
	
	public void show(Player player, Creature enchanter) {
		initLists();
		
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);	
	}
	
	public void initLists() {
		itemModel.clear();
		spellModel.clear();
		
		for(Long uid : Engine.getPlayer().inventory) {
			Item item = (Item)Engine.getStore().getEntity(uid);
			if((item instanceof Weapon || item instanceof Clothing) && item.enchantment == null) {
				itemModel.addElement(item);
			}
		}
		itemList.setSelectedIndex(0);
	}
	
	public void keyReleased(KeyEvent key) {}
	public void keyTyped(KeyEvent key) {}
	public void keyPressed(KeyEvent key) {
		switch(key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: 
			frame.dispose();
			break;
		case KeyEvent.VK_LEFT: 
		case KeyEvent.VK_RIGHT:
			if(itemList.hasFocus()) {
				spellList.requestFocus();
				spellList.setSelectedIndex(0);
			} else {
				itemList.requestFocus();
				itemList.setSelectedIndex(0);
			}
			break;
		case KeyEvent.VK_ENTER: 
			if(spellList.getSelectedValue() != null) {
				Effect effect = spellList.getSelectedValue();
				Item item = itemList.getSelectedValue();
				RSpell spell = new RSpell(effect.toString(), 0, 0, effect.name(), 0, 0, "enchant");
				item.enchantment = new Enchantment(spell, 100, item.getUID());
				Engine.getUI().showMessage("Item enchanted.", 2);
				initLists();
			} else {
				Engine.getUI().showMessage("No enchantment selected!", 2);				
			}
		}
	}
	
	public void valueChanged(ListSelectionEvent event) {
		if(itemList.getSelectedValue() != null) {
			spellModel.clear();
			if(itemList.getSelectedValue() instanceof Weapon) {
				for(Effect effect : Effect.values()) {
					if(effect.getHandler().isWeaponEnchantment()) {
						spellModel.addElement(effect);
					}
				}
			} else {
				for(Effect effect : Effect.values()) {
					if(effect.getHandler().isClothingEnchantment()) {
						spellModel.addElement(effect);
					}
				}
			}
		}
	}
}
