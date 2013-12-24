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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Vector;
import javax.swing.*;
import javax.swing.border.*;
import neon.entities.Creature;
import neon.entities.Player;
import neon.resources.RSpell;
import neon.ui.UserInterface;

public class SpellTradeDialog implements KeyListener {
	private JDialog frame;
	private Player player;
	private JList<RSpell> buy;
	private Creature trader;
	private JLabel info;
	private JPanel panel;
	private String big, small;
	private UserInterface ui;
	
	/**
	 * Initializes a new spell trading dialog box.
	 * 
	 * @param big	name of major denominations (euro, dollar)
	 * @param small	name of minor denominations (cents)
	 */
	public SpellTradeDialog(UserInterface ui, String big, String small) {
		this.big = big;
		this.small = small;
		this.ui = ui;
		
		JFrame parent = ui.getWindow();
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));
        
		// trader panel klaarmaken
        buy = new JList<RSpell>();
		JScrollPane scroller = new JScrollPane(buy);
     	scroller.setBorder(new TitledBorder("Spells"));
        buy.setFocusable(false);
    	buy.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	panel.add(scroller, BorderLayout.CENTER);
		buy.setCellRenderer(new SpellCellRenderer());

        // info geven
        info = new JLabel("You have no money.");
        info.setBorder(new CompoundBorder(new TitledBorder("Messages"), new EmptyBorder(0,5,10,5)));
        panel.add(info, BorderLayout.PAGE_START);
        
        // instructies geven
		JLabel instructions = new JLabel("Use arrows keys to select spell, press enter to buy spell, esc to exit.");
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
	
	public void show(Player player, Creature trader) {
		this.trader = trader;
		this.player = player;
		initSpells();
		
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
			if(buy.getSelectedIndex() > 0) {
				buy.setSelectedIndex(buy.getSelectedIndex()-1);
			}
			break;
		case KeyEvent.VK_DOWN: 
			buy.setSelectedIndex(buy.getSelectedIndex()+1); 
			break;
		case KeyEvent.VK_ENTER:
			try {
				RSpell spell = buy.getSelectedValue();
				if(player.getInventoryComponent().getMoney() >= spell.cost) {
					if(!player.getMagicComponent().getSpells().contains(spell)) {
						player.getMagicComponent().addSpell(spell);
						ui.showMessage("You bought the spell " + spell + ".", 2);
						initSpells();
					} else {
						ui.showMessage("You already have that spell.", 2);
					}
				} else {
					ui.showMessage("You don't have enough money.", 2);						
				}
			} catch (Exception f) {
				ui.showMessage("There is nothing left to buy.", 2);
			} 
			break;
		}
	}

	private String moneyString(int money) {
		if(money == 0) {
			return "no money";
		} else {
			int gold = (money - money%100)/100;
			int copper = money%100;
			return gold + " " + big + " and " + copper + " " + small;
		}
	}
	
	private void initSpells() {
		Vector<RSpell> spells = new Vector<RSpell>();
		for(RSpell s : trader.getMagicComponent().getSpells()) {
			spells.add(s);			
		}
		
		buy.setListData(spells);
    	buy.setSelectedIndex(0);
    	info.setText("Money: " + moneyString(player.getInventoryComponent().getMoney()) + ".");
    	panel.repaint();		
	}
	
	private class SpellCellRenderer implements ListCellRenderer<RSpell> {
		private UIDefaults defaults = UIManager.getLookAndFeelDefaults();

	    public Component getListCellRendererComponent(JList<? extends RSpell> list, 
	    		RSpell value, int index, boolean isSelected, boolean cellHasFocus) {
	    	JLabel label = new JLabel(value.id + " (" + value.cost + " " + small + ")");
	        
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
