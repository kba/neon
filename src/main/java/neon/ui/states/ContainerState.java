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

package neon.ui.states;

import neon.core.Engine;
import neon.maps.Zone;
import neon.resources.CClient;
import neon.ui.*;
import neon.core.handlers.InventoryHandler;
import neon.core.handlers.MotionHandler;
import neon.entities.Container;
import neon.entities.Creature;
import neon.entities.Door;
import neon.entities.Entity;
import neon.entities.Item;
import neon.entities.Player;
import neon.util.fsm.*;
import net.engio.mbassy.bus.MBassador;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;



public class ContainerState extends State implements KeyListener, ListSelectionListener {
	private final static UIDefaults defaults = UIManager.getLookAndFeelDefaults();
	private final static Color line = defaults.getColor("List.foreground");
	
	private Player player;
	private Object container;
	private MBassador<EventObject> bus;
	private UserInterface ui;

	// onderdelen van het JPanel
	private JPanel panel;
	private JList<Item> iList;
	private JList<Entity> cList;
	private JScrollPane cScroll, iScroll;
	private DescriptionPanel description;
	
	// lijstjes
	private HashMap<String, Integer> cData, iData;
	
	public ContainerState(State parent, MBassador<EventObject> bus, UserInterface ui) {
		super(parent);
		this.bus = bus;
		this.ui = ui;
		
		panel = new JPanel(new BorderLayout());
        JPanel center = new JPanel(new java.awt.GridLayout(0,3));
        panel.addKeyListener(this);
        
        // inventory panel klaarmaken
        iList = new JList<Item>();
		iList.addKeyListener(this);
		iList.addListSelectionListener(this);
        iScroll = new JScrollPane(iList);
        iData = new HashMap<String, Integer>();
    	iList.setCellRenderer(new InventoryCellRenderer(iData));
    	iList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    	iScroll.setBorder(new TitledBorder(new LineBorder(line.brighter(), 2), "Inventory"));
    	center.add(iScroll);

    	// description panel klaarmaken
		CClient ini = (CClient)Engine.getResources().getResource("client", "config");
    	description = new DescriptionPanel(ini.getSmall());
    	center.add(description);
    	
        // container panel klaarmaken
        cList = new JList<Entity>();
		cList.addKeyListener(this);
		cList.addListSelectionListener(this);
        cScroll = new JScrollPane(cList);
        cData = new HashMap<String, Integer>();
    	cList.setCellRenderer(new InventoryCellRenderer(cData));
    	cList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        center.add(cScroll);    	
        panel.add(center, BorderLayout.CENTER);
        
        // instructies geven
		JLabel instructions = new JLabel("Use arrows keys to select item, press space to pick up/drop item, ctrl to exit.");
    	instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
        panel.add(instructions, BorderLayout.PAGE_END);
	}
	
	@Override
	public void enter(TransitionEvent t) {
		container = t.getParameter("holder");
		cScroll.setBorder(new TitledBorder(new LineBorder(line), container.toString()));
		player = Engine.getPlayer();
		ui.showPanel(panel);
		update();
		iList.requestFocus();
		iList.setSelectedIndex(0);
		cList.clearSelection();
	}

	public void keyReleased(KeyEvent key) {}
	public void keyTyped(KeyEvent key) {}
	public void keyPressed(KeyEvent key) {
		switch (key.getKeyCode()) {
		case KeyEvent.VK_CONTROL: 
		case KeyEvent.VK_ESCAPE: 
			bus.publishAsync(new TransitionEvent("return"));
			break;
		case KeyEvent.VK_LEFT:
		case KeyEvent.VK_RIGHT:
			if(iList.hasFocus()) {
				iList.clearSelection();
				cList.requestFocus();
				cList.setSelectedIndex(0);
				cScroll.setBorder(new TitledBorder(new LineBorder(line.brighter(), 2), container.toString()));
				iScroll.setBorder(new TitledBorder(new LineBorder(line), "Inventory"));
			} else {
				cList.clearSelection();
				iList.requestFocus();
				iList.setSelectedIndex(0);
				cScroll.setBorder(new TitledBorder(new LineBorder(line), container.toString()));
				iScroll.setBorder(new TitledBorder(new LineBorder(line.brighter(), 2), "Inventory"));
			}
			break;
		case KeyEvent.VK_SPACE:
			try {
				if(iList.hasFocus()) {	// iets dumpen
					Item item = (Item)iList.getSelectedValue();
					InventoryHandler.removeItem(player, item.getUID());
					if(container instanceof Container) {	// verandering registreren
						((Container)container).addItem(item.getUID());
					} else if(container instanceof Zone) {	// itempositie aanpassen
						Rectangle pBounds = player.getShapeComponent();
						Rectangle iBounds = item.getShapeComponent();
						iBounds.setLocation(pBounds.x, pBounds.y);
						Engine.getAtlas().getCurrentZone().addItem(item);
					} else if(container instanceof Creature) {
						InventoryHandler.addItem(((Creature)container), item.getUID());
					}
					update();
				} else {	// iets oppakken
					Entity item = (Entity)cList.getSelectedValue();
					if(item instanceof Container) {
						bus.publishAsync(new TransitionEvent("return"));
						bus.publishAsync(new TransitionEvent("container", "holder", item));
					} else if(item instanceof Door) {
						MotionHandler.teleport(player, (Door)item);
						bus.publishAsync(new TransitionEvent("return"));
					} else if(item instanceof Creature) {
						bus.publishAsync(new TransitionEvent("return"));
						bus.publishAsync(new TransitionEvent("container", "holder", item));
					} else {
						if(container instanceof Zone) {
							Engine.getAtlas().getCurrentZone().removeItem((Item)item);
						} else if(container instanceof Creature) {
							InventoryHandler.removeItem(((Creature)container), item.getUID());
						} else {
							((Container)container).removeItem(item.getUID());
						}
						InventoryHandler.addItem(player, item.getUID());
						update();
					}
				}
			} catch (ArrayIndexOutOfBoundsException e) {
				if(iList.hasFocus()) {
					ui.showMessage("There is nothing left to drop.", 2);
				} else {
					ui.showMessage("There is nothing left to pick up.", 2);
				}
			} 
		}
	}
	
	private void update() {
		Vector<Item> iBuffer = new Vector<Item>();
		iData.clear();
		Vector<Entity> cBuffer = new Vector<Entity>();
		cData.clear();

		ArrayList<Object> items = new ArrayList<Object>();
		if(container instanceof Zone) {
			Zone zone = (Zone)container;
			Rectangle bounds = player.getShapeComponent();
			items.addAll(zone.getItems(bounds.getLocation()));
		} else if(container instanceof Creature) {
			items.addAll(((Creature)container).getInventoryComponent().getItems());
		} else {
			items.addAll(((Container)container).getItems());
		}
		
		for(Object o : items) {
			Item item = null;
			if(o instanceof Item) {
				item = (Item)o;				
			} else {
				item = (Item)Engine.getStore().getEntity((Long)o);
			}
			if(!cData.containsKey(item.getID())) {
				cData.put(item.getID(), 1);
				cBuffer.add(item);
			} else {
				cData.put(item.getID(), cData.get(item.getID()) + 1);
			}
		}

		for(long uid : player.getInventoryComponent()) {
			Item i = (Item)Engine.getStore().getEntity(uid);
			if(!iData.containsKey(i.getID())) {
				iData.put(i.getID(), 1);
				iBuffer.add(i);
			} else {
				iData.put(i.getID(), iData.get(i.getID()) + 1);
			}
		}

		iList.setListData(iBuffer);
    	cList.setListData(cBuffer);
    	panel.repaint();
	}

	public void valueChanged(ListSelectionEvent lse) {
		Entity item = iList.getSelectedValue() != null ? iList.getSelectedValue() : cList.getSelectedValue();
		description.update(item);
	}
}
