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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import neon.core.Engine;
import neon.core.handlers.InventoryHandler;
import neon.entities.Creature;
import neon.entities.Entity;
import neon.entities.Item;
import neon.entities.Player;
import neon.ui.DescriptionPanel;
import neon.ui.UserInterface;

public class TradeDialog implements KeyListener, ListSelectionListener {
	private final static UIDefaults defaults = UIManager.getLookAndFeelDefaults();
	private final static Color line = defaults.getColor("List.foreground");

	private JDialog frame;
	private Player player;
	private Creature trader;
	private JList<Item> sellList, buyList;
	private JScrollPane sScroll, bScroll;
	private JLabel info;
	private JPanel panel;
	private DescriptionPanel description;
	private String big, small;
	private UserInterface ui;
	
	/**
	 * 
	 * @param big	name of major denominations (euro, dollar)
	 * @param small	name of minor denominations (cents)
	 */
	public TradeDialog(UserInterface ui, String big, String small) {
		this.big = big;
		this.small = small;
		this.ui = ui;
		
		JFrame parent = ui.getWindow();
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
		JPanel center = new JPanel(new java.awt.GridLayout(0,3));
        
        // inventory panel klaarmaken
        sellList = new JList<Item>();
		sellList.addKeyListener(this);
		sellList.addListSelectionListener(this);
        sScroll = new JScrollPane(sellList);
    	sellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	sScroll.setBorder(new TitledBorder(new LineBorder(line.brighter(), 2), "Inventory"));
    	center.add(sScroll);
    	
    	// description label klaarmaken
    	description = new DescriptionPanel(small);
    	center.add(description);

        // trader panel klaarmaken
        buyList = new JList<Item>();
		buyList.addKeyListener(this);
		buyList.addListSelectionListener(this);
        bScroll = new JScrollPane(buyList);
    	buyList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	bScroll.setBorder(new TitledBorder("For sale"));
        center.add(bScroll);    	
        panel.add(center, BorderLayout.CENTER);
        
        // instructies geven
		JLabel instructions = new JLabel("Use arrows keys to select item, press space to buy/sell item, esc to exit.");
    	instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
        panel.add(instructions, BorderLayout.PAGE_END);

        // info geven
        info = new JLabel("You have no money.");
    	info.setBorder(new CompoundBorder(new TitledBorder("Money"), new EmptyBorder(0,5,10,5)));
        panel.add(info, BorderLayout.PAGE_START);
        
		frame.setContentPane(panel);
        frame.addKeyListener(this);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show(Player player, Creature trader) {
		this.trader = trader;
		this.player = player;
		initGoods();

		sellList.requestFocus();
		sellList.setSelectedIndex(0);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);	
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: 
			frame.dispose();
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			if(sellList.hasFocus()) {
				sellList.clearSelection();
				buyList.requestFocus();
				buyList.setSelectedIndex(0);
				sScroll.setBorder(new TitledBorder("Inventory"));
				bScroll.setBorder(new TitledBorder(new LineBorder(line.brighter(), 2), "For sale"));
			} else {
				buyList.clearSelection();
				sellList.requestFocus();
				sellList.setSelectedIndex(0);
				sScroll.setBorder(new TitledBorder(new LineBorder(line.brighter(), 2), "Inventory"));
				bScroll.setBorder(new TitledBorder("For sale"));
			}
			break;
		case KeyEvent.VK_SPACE:
			try {
				if(sellList.hasFocus()) {
					sell(sellList.getSelectedValue());
					initGoods();
				} else {
					buy(buyList.getSelectedValue());
					initGoods();
				}
			} catch (Exception e) {
				if(sellList.hasFocus()) {
					ui.showMessage("There is nothing left to sell.", 2);
				} else {
					ui.showMessage("There is nothing left to buy.", 2);
				}
			} 
			break;
		}
	}
	
	private void initGoods() {
		Vector<Item> sellData = new Vector<Item>();
		for(long uid : Engine.getPlayer().getInventoryComponent()) {
			sellData.add((Item)Engine.getStore().getEntity(uid));				
		}
		sellList.setListData(sellData);

		Vector<Item> buyData = new Vector<Item>();
		for(long uid : trader.getInventoryComponent()) {
			buyData.add((Item)Engine.getStore().getEntity(uid));				
		}
		buyList.setListData(buyData);
		
    	info.setText("Money: " + moneyString(player.getInventoryComponent().getMoney()) + ".");
    	panel.repaint();		
	}

	private String moneyString(int money) {
		if(money == 0) {
			return "no money";
		} else {
			int copper = money%100;
			int gold = (money - copper)/100;
			return gold + " " + big + " and " + copper + " " + small;
		}
	}
	
	private void buy(Item item) {
		int price = item.resource.cost;
		if(price > player.getInventoryComponent().getMoney()) {
			ui.showMessage("Not enough money to buy this item.", 2);
		} else {
			InventoryHandler.removeItem(trader, item.getUID());
			player.getInventoryComponent().addItem(item.getUID());				
			player.getInventoryComponent().addMoney(-price);
		}
	}
	
	private void sell(Item item) {
		InventoryHandler.removeItem(player, item.getUID());
		trader.getInventoryComponent().addItem(item.getUID());
		player.getInventoryComponent().addMoney(item.resource.cost);
	}

	public void valueChanged(ListSelectionEvent lse) {
		Entity item = buyList.getSelectedValue() != null ? buyList.getSelectedValue() : sellList.getSelectedValue();
		description.update(item);
	}
}
