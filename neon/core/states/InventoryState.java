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

package neon.core.states;

import neon.core.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.util.*;
import java.awt.event.*;
import neon.core.handlers.InventoryHandler;
import neon.core.handlers.MagicHandler;
import neon.core.handlers.SkillHandler;
import neon.maps.Atlas;
import neon.objects.entities.*;
import neon.objects.property.Skill;
import neon.objects.resources.RItem;
import neon.objects.resources.RText;
import neon.ui.BookDialog;
import neon.ui.DescriptionPanel;
import neon.util.fsm.TransitionEvent;
import neon.util.fsm.State;

public class InventoryState extends State implements KeyListener, MouseListener {
	private Player player;
	private JList<Item> inventory;
	private JLabel info;
	private HashMap<String, Integer> listData;
	private JPanel panel;
	private DescriptionPanel description;
	private Configuration config;
	private Atlas atlas;
	
	public InventoryState(Engine engine, Configuration config, Atlas atlas) {
		super(engine, "inventory module");
		this.config = config;
		this.atlas = atlas;
		panel = new JPanel(new BorderLayout());
		
		// info
    	info = new JLabel();
    	info.setBorder(new CompoundBorder(new TitledBorder("Information"), new EmptyBorder(0,5,10,5)));
		panel.add(info, BorderLayout.PAGE_START);

		JPanel contents = new JPanel(new GridLayout(0, 2));
    	panel.add(contents, BorderLayout.CENTER);
		
		// lijst met items
		inventory = new JList<Item>();
		inventory.setFocusable(false);
		inventory.addMouseListener(this);
        JScrollPane scroller = new JScrollPane(inventory);
		listData = new HashMap<String, Integer>();
    	inventory.setCellRenderer(new neon.ui.InventoryCellRenderer(listData));
    	inventory.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	scroller.setBorder(new TitledBorder("Inventory"));
    	contents.add(scroller);
    	
    	description = new DescriptionPanel(config.getProperty("smallCoin"));    	
    	contents.add(description);
    	
		// instructies
		JLabel instructions = new JLabel("<html>Use arrows to select item, press enter to use/eat/(un)equip item, " +
				"space to drop item, ctrl or esc to exit inventory. Equiped items are in bold.</html>");
		instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
		panel.add(instructions, BorderLayout.PAGE_END);
		
		panel.addKeyListener(this);
	}
	
	@Override
	public void enter(TransitionEvent t) {
		player = Engine.getPlayer();
		initList();
    	inventory.setSelectedIndex(0);
    	inventory.repaint();
		description.update(inventory.getSelectedValue());
		Engine.getUI().showPanel(panel);
	}
	
	private void use(Item item) {
//		System.out.println(item.getClass());
		if(item instanceof Item.Potion) {
			MagicHandler.drink(player, (Item.Potion)item);
			initList();
		} else if(item instanceof Item.Book && !(item instanceof Item.Scroll)) {
			RText text = (RText)Engine.getResources().getResource(((RItem.Text)item.resource).content + ".html");
			new BookDialog(Engine.getUI().getWindow()).show(item.toString(), text.getText());
		} else if(item instanceof Item.Food) {
			InventoryHandler.removeItem(player, item.getUID());
			initList();
			MagicHandler.eat(player, (Item.Food)item);
		} else if(item instanceof Item.Aid) {
			InventoryHandler.removeItem(player, item.getUID());
			initList();
			player.heal(SkillHandler.check(player, Skill.MEDICAL)/5f);
		} else if(!player.inventory.hasEquiped(item.getUID())) {
			InventoryHandler.equip(item, player);			
		} else {
			InventoryHandler.unequip(item.getUID(), player);	
		}
		inventory.repaint();
	}
	
	private void initList() {
		Vector<Item> buffer = new Vector<Item>();
		listData.clear();
		
		for(long uid : Engine.getPlayer().inventory) {
			Item i = (Item)Engine.getStore().getEntity(uid);
			if(!listData.containsKey(i.getID())) {
				listData.put(i.getID(), 1);
				buffer.add(i);
			} else {
				listData.put(i.getID(), listData.get(i.getID()) + 1);
			}
		}
		
		info.setText("Weight: " + player.getWeight() + " kg. Money: " + moneyString(player.getMoney()) + ".");
    	inventory.setListData(buffer);
	}
	
	private String moneyString(int money) {
		if(money == 0) {
			return "no money";
		} else {
			int gold = money/100;
			int copper = money%100;
			return gold + " " + config.getProperty("bigCoin") + " and " + copper + " " + config.getProperty("smallCoin");
		}
	}
	
	public void keyTyped(KeyEvent e) {}
	public void keyReleased(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_CONTROL: 
		case KeyEvent.VK_ESCAPE: 
			Engine.post(new TransitionEvent("cancel"));
			break;
		case KeyEvent.VK_UP:
			if(inventory.getSelectedIndex() > 0) {
				inventory.setSelectedIndex(inventory.getSelectedIndex() - 1);
			}
			break;
		case KeyEvent.VK_DOWN:
			inventory.setSelectedIndex(inventory.getSelectedIndex() + 1); 
			break;
		case KeyEvent.VK_ENTER:
			if(inventory.getSelectedValue() != null) {
				use(inventory.getSelectedValue()); 					
			} else {
				Engine.getUI().showMessage("There is nothing left to use/eat/(un)equip.", 2);
			}
			break;
		case KeyEvent.VK_SPACE:
			if(inventory.getSelectedValue() != null) {
				Item item = inventory.getSelectedValue();
				InventoryHandler.removeItem(player, item.getUID());
				item.getBounds().setLocation(player.getBounds().x, player.getBounds().y);
				atlas.getCurrentZone().addItem(item);
				initList();
				inventory.setSelectedIndex(0);
				inventory.repaint();
			} else {
				Engine.getUI().showMessage("There is nothing left to drop.", 2);
			}
			break;
		}
		description.update(inventory.getSelectedValue());
	}

	public void mouseClicked(MouseEvent me) {}
	public void mouseEntered(MouseEvent me) {}
	public void mouseExited(MouseEvent me) {}
	public void mouseReleased(MouseEvent me) {}
	public void mousePressed(MouseEvent me) {
		if(me.getClickCount() == 2) {
			use(inventory.getSelectedValue());
		}
	}
}
