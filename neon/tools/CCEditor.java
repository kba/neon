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

package neon.tools;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;
import neon.objects.resources.RCreature;
import neon.objects.resources.RItem;
import neon.objects.resources.RSpell;
import java.util.*;
import java.util.List;
import neon.objects.resources.RSpell.SpellType;
import neon.objects.resources.RCreature.Type;
import neon.tools.help.HelpLabels;
import neon.tools.resources.RMap;
import neon.tools.resources.RZone;

public class CCEditor implements ActionListener, ItemListener, ListSelectionListener, MouseListener {
	private JDialog frame;
	private JCheckBox raceBox;
	private JFormattedTextField xField, yField;
	private JComboBox<RMap> mapBox;
	private JComboBox<RZone> zoneBox;
	private HashMap<RCreature, Boolean> races;
	private JList<RCreature> raceList;
	private JList<RItem> itemList;
	private JList<RSpell> spellList;
	private RCreature currentRace;
	private DefaultListModel<RSpell> spellListModel;
	private DefaultListModel<RItem> itemListModel;
	private String[] spells;
	private JPanel raceEditPanel;
	
	public CCEditor(JFrame parent) {
		frame = new JDialog(parent, "Character Creation Editor", true);	// modal dialog
		JPanel content = new JPanel(new BorderLayout());
		frame.setPreferredSize(new Dimension(400, 300));
		frame.setContentPane(content);
		JTabbedPane tabs = new JTabbedPane();
		content.add(tabs, BorderLayout.CENTER);
		
		// races
		JPanel racePanel = new JPanel(new GridLayout(0,2));
		raceList = new JList<RCreature>();
		raceList.addListSelectionListener(this);
		JScrollPane raceScroller = new JScrollPane(raceList);
		raceScroller.setBorder(new TitledBorder("Races"));
		racePanel.add(raceScroller);
		raceEditPanel = new JPanel();
		raceEditPanel.setBorder(new TitledBorder("Edit"));
		raceBox = new JCheckBox("Playable");
		raceBox.addActionListener(this);
		raceEditPanel.add(raceBox);
		racePanel.add(raceEditPanel);
		
		// position
		JPanel mapPanel = new JPanel();
		mapPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		GroupLayout layout = new GroupLayout(mapPanel);
		mapPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		
		JLabel mapLabel = new JLabel("Map: ");
		JLabel zoneLabel = new JLabel("Zone: ");
		JLabel xLabel = new JLabel("x-coordinate: ");
		JLabel yLabel = new JLabel("y-coordinate: ");
		mapBox = new JComboBox<RMap>();
		mapBox.addItemListener(this);
		xField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		yField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		zoneBox = new JComboBox<RZone>();
		JLabel mapHelpLabel = HelpLabels.getStartMapHelpLabel();
		JLabel zoneHelpLabel = HelpLabels.getStartZoneLabel();
		JLabel xHelpLabel = HelpLabels.getStartXLabel();
		JLabel yHelpLabel = HelpLabels.getStartYLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(mapLabel).addComponent(mapBox).addComponent(mapHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(zoneLabel).addComponent(zoneBox).addComponent(zoneHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(xLabel).addComponent(xField).addComponent(xHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(yLabel).addComponent(yField).addComponent(yHelpLabel))
				.addGap(10));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(mapLabel).addComponent(zoneLabel).addComponent(xLabel).addComponent(yLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(mapBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(zoneBox).addComponent(xField).addComponent(yField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(mapHelpLabel).addComponent(zoneHelpLabel).addComponent(xHelpLabel)
						.addComponent(yHelpLabel)));

		// starting items
		JPanel itemPanel = new JPanel(new BorderLayout());
		itemListModel = new DefaultListModel<RItem>();
		itemList = new JList<RItem>(itemListModel);
		itemList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itemPanel.add(new JScrollPane(itemList));
		
		// starting spells
		JPanel spellPanel = new JPanel(new BorderLayout());
		spellListModel = new DefaultListModel<RSpell>();
		spellList = new JList<RSpell>(spellListModel);
		spellList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		spellPanel.add(new JScrollPane(spellList));
		
		tabs.add(racePanel, "Playable races");
		tabs.add(mapPanel, "Starting position");
		tabs.add(itemPanel, "Starting items");
		tabs.add(spellPanel, "Starting spells");
		
		JPanel buttons = new JPanel();
		content.add(buttons, BorderLayout.PAGE_END);
		JButton ok = new JButton("Ok");
		ok.addActionListener(this);
		JButton apply = new JButton("Apply");
		apply.addActionListener(this);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		buttons.add(ok);

		itemList.addMouseListener(this);
		spellList.addMouseListener(this);
		buttons.add(cancel);
		buttons.add(apply);
	}

	public void show() {
		DataStore store = Editor.getStore();
		Mod mod = store.getActive();
		List<RCreature> playableRaces = new ArrayList<RCreature>();
		// als species niet bestaat, automatisch verwijderen
		for(String race : mod.getList("races")) {
			if(Editor.resources.getResource(race) instanceof RCreature) {
				playableRaces.add((RCreature)Editor.resources.getResource(race));
			} else {
				mod.ccRaces.remove(race);
			}
		}
		itemListModel.clear();
		// als item niet bestaat, automatisch verwijderen
		for(String item : mod.getList("items")) {
			if(Editor.resources.getResource(item) instanceof RItem) {
				itemListModel.addElement((RItem)Editor.resources.getResource(item));
			} else {
				mod.ccRaces.remove(item);
			}
		}
		spellListModel.clear();		
		// als spell niet bestaat, automatisch verwijderen
		for(String spell : mod.getList("spells")) {
			if(Editor.resources.getResource(spell, "magic") != null) {
				spellListModel.addElement((RSpell)Editor.resources.getResource(spell, "magic"));
			} else {
				mod.ccRaces.remove(spell);
			}
		}

		races = new HashMap<RCreature, Boolean>();
		for(RCreature rc : Editor.resources.getResources(RCreature.class)) {
			// alleen humanoids of goblins
			if(rc.type == Type.humanoid || rc.type == Type.goblin) {
				races.put(rc, playableRaces.contains(rc));
			}
		}		
		raceList.setListData(races.keySet().toArray(new RCreature[0]));

		for(RMap map : Editor.resources.getResources(RMap.class)) {
			if(map.theme == null) {	// geen random maps toelaten
				mapBox.addItem(map);
			}
		}
		RMap map = (RMap)Editor.resources.getResource(mod.get("map"), "maps");
		mapBox.setSelectedItem(map);
		for(RZone zone : map.zones.values()) {
			if(zone.theme == null) {	// geen random zones toelaten
				zoneBox.addItem(zone);
			}
		}
		zoneBox.setSelectedItem(map.getZone(Integer.parseInt(mod.get("z"))));
		xField.setValue(Integer.parseInt(mod.get("x")));
		yField.setValue(Integer.parseInt(mod.get("y")));

		ArrayList<String> temp = new ArrayList<String>();
		for(RSpell spell : Editor.resources.getResources(RSpell.class)) {
			if(spell.type == SpellType.SPELL) {
				temp.add(spell.id);
			}
		}
		spells = temp.toArray(new String[temp.size()]);

		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	private void saveAll() {
		Mod mod = Editor.getStore().getActive();
		if(mapBox.getSelectedItem() != null) {
			RMap map = (RMap)mapBox.getSelectedItem();
			mod.set("map", map.id);
			RZone zone = (RZone)zoneBox.getSelectedItem();
			mod.set("z", Integer.toString(map.getZone(zone)));
			mod.set("x", xField.getText());
			mod.set("y", yField.getText());
		} else {
			JOptionPane.showMessageDialog(frame, "No start map selected!");
		}

		mod.ccRaces.clear();
		for(RCreature rc : races.keySet()) {
			if(races.get(rc)) {
				mod.ccRaces.add(rc.id);
			}
		}
		mod.ccItems.clear();
		for(Enumeration<RItem> e = itemListModel.elements(); e.hasMoreElements();) {
			RItem ri = e.nextElement();
			mod.ccItems.add(ri.id);
		}
		mod.ccSpells.clear();
		for(Enumeration<RSpell> e = spellListModel.elements(); e.hasMoreElements();) {
			RSpell rs = e.nextElement();
			mod.ccSpells.add(rs.id);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if ("Playable".equals(e.getActionCommand())){
			races.put(currentRace, raceBox.isSelected());
		} else if ("Ok".equals(e.getActionCommand())) {
			saveAll();
			frame.dispose();
		} else if ("Cancel".equals(e.getActionCommand())){
			frame.dispose();
		} else if ("Apply".equals(e.getActionCommand())){
			saveAll();
		}
	}
	
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			if(e.getComponent() == spellList) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(new SpellListAction("Add spell"));
				menu.add(new SpellListAction("Delete spell"));
				menu.show(e.getComponent(), e.getX(), e.getY());
				spellList.setSelectedIndex(spellList.locationToIndex(e.getPoint()));
			} else if(e.getComponent() == itemList) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(new ItemListAction("Add item"));
				menu.add(new ItemListAction("Delete item"));
				menu.show(e.getComponent(), e.getX(), e.getY());
				itemList.setSelectedIndex(itemList.locationToIndex(e.getPoint()));
			} 
		}
	}

	public void valueChanged(ListSelectionEvent e) {
		// blijkbaar worden er twee events gefired bij selectie
		if(e.getValueIsAdjusting()) {
			currentRace = raceList.getSelectedValue();
			raceBox.setSelected(races.get(currentRace));
			raceEditPanel.setBorder(new TitledBorder(currentRace.id));
		}
	}
	
	public void itemStateChanged(ItemEvent e) {
		 if(mapBox.equals(e.getSource())) {
			 // zones laden
			 zoneBox.setModel(new DefaultComboBoxModel<RZone>());
			 RMap map = (RMap)mapBox.getSelectedItem();
			 if(map != null) {
				 for(RZone zone : map.zones.values()) {
					 if(zone.theme == null) {	// geen random zones toelaten
						 zoneBox.addItem(zone);
					 }
				 }
			 }
			 frame.pack();
		 } 
	}

	@SuppressWarnings("serial")
	private class SpellListAction extends AbstractAction {
		public SpellListAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add spell")) {
				RSpell rs = (RSpell)JOptionPane.showInputDialog(neon.tools.Editor.getFrame(), "Add spell:",
						"Add spell", JOptionPane.PLAIN_MESSAGE, null, spells, 0);
				if(rs != null) {
					spellListModel.addElement(rs);
				}
			} else if(e.getActionCommand().equals("Delete spell")) {
				spellListModel.remove(spellList.getSelectedIndex());
			}						
		}
	}
	
	@SuppressWarnings("serial")
	private class ItemListAction extends AbstractAction {
		public ItemListAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add item")) {
				Object[] items = Editor.resources.getResources(RItem.class).toArray();
				RItem ri = (RItem)JOptionPane.showInputDialog(neon.tools.Editor.getFrame(), "Add item:",
						"Add item", JOptionPane.PLAIN_MESSAGE, null, items, 0);
				if(ri != null) {
					itemListModel.addElement(ri);
				}
			} else if(e.getActionCommand().equals("Delete item")) {
				itemListModel.remove(itemList.getSelectedIndex());
			}						
		}
	}
}
