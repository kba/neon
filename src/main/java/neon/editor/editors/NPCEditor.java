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

package neon.editor.editors;

import java.awt.event.*;
import javax.swing.*;
import org.jdom2.Element;
import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import javax.swing.border.*;
import javax.swing.event.*;

import neon.editor.*;
import neon.editor.help.HelpLabels;
import neon.editor.resources.RFaction;
import neon.entities.property.Skill;
import neon.resources.*;
import neon.resources.RSpell.SpellType;

public class NPCEditor extends ObjectEditor implements MouseListener {
	private RPerson data;
	private JList<String> spellList, itemList, destList;
	private JTextField nameField;
	private JComboBox<RFaction> factionBox;
	private JComboBox<RCreature> raceBox;
	private JComboBox<RCreature.AIType> aiTypeBox;
	private JSpinner aggressionSpinner, confidenceSpinner, factionSpinner;
	private JFormattedTextField rangeField, destX, destY, destCost, skillField;
	private HashMap<Skill, Integer> skills;
	private Set<Skill> trainedSkills;
	private HashMap<String, Integer> joinedFactions;
	private HashMap<String, Element> destMap;
	private JCheckBox spellBox, skillBox, tradeBox, travelBox, trainBox, spellMakerBox, 
		factionCheckBox, potionBox, healerBox, tattooBox;
	private JComboBox<Skill> skillComboBox;
	private DefaultListModel<String> destListModel, spellListModel, itemListModel;
	private Skill currentSkill;
	private Element currentDest;
	private ArrayList<String> spells;
	
	public NPCEditor(JFrame parent, RPerson data) {
		super(parent, "NPC Editor: " + data.id);
		this.data = data;

		spells = new ArrayList<String>();
		for(RSpell spell : Editor.resources.getResources(RSpell.class)) {
			if(spell.type == SpellType.SPELL) {
				spells.add(spell.id);
			}
		}

		JPanel npcProps = new JPanel();
		npcProps.setBorder(new TitledBorder("Properties"));
		BoxLayout propLayout = new BoxLayout(npcProps, BoxLayout.PAGE_AXIS);
		npcProps.setLayout(propLayout);
		
		JPanel generalPanel = new JPanel();
		generalPanel.setBorder(new TitledBorder("General"));
		nameField = new JTextField(10);
		raceBox = new JComboBox<RCreature>(Editor.resources.getResources(RCreature.class));
		generalPanel.add(new JLabel("Name: "));
		generalPanel.add(nameField);
		generalPanel.add(new JLabel(" "));
		generalPanel.add(HelpLabels.getNameHelpLabel());
		generalPanel.add(new JLabel("  "));
		generalPanel.add(new JLabel("Species: "));
		generalPanel.add(raceBox);
		generalPanel.add(new JLabel(" "));
		generalPanel.add(HelpLabels.getRaceHelpLabel());
		generalPanel.setMaximumSize(new Dimension(generalPanel.getMaximumSize().width, generalPanel.getPreferredSize().height));
		
		JPanel aiPanel = new JPanel();
		GroupLayout layout = new GroupLayout(aiPanel);
		aiPanel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		JLabel aiTypeLabel = new JLabel("Type: ");
		JLabel aggressionLabel = new JLabel("Aggression: ");
		JLabel confidenceLabel = new JLabel("Confidence: ");
		JLabel rangeLabel = new JLabel("Territory: ");
		aiTypeBox = new JComboBox<RCreature.AIType>(RCreature.AIType.values());
		aggressionSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		confidenceSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		rangeField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		rangeField.setValue(0);
		JLabel aiHelpLabel = HelpLabels.getAITypeHelpLabel();
		JLabel confidenceHelpLabel = HelpLabels.getConfidenceHelpLabel();
		JLabel aggressionHelpLabel = HelpLabels.getAggressionHelpLabel();
		JLabel rangeHelpLabel = HelpLabels.getRangeHelpLabel();
		aiPanel.setMaximumSize(new Dimension(generalPanel.getMaximumSize().width, generalPanel.getPreferredSize().height));
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(aiTypeLabel).addComponent(aiTypeBox).addComponent(aiHelpLabel)
						.addComponent(aggressionLabel).addComponent(aggressionSpinner).addComponent(aggressionHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(confidenceLabel).addComponent(confidenceSpinner).addComponent(confidenceHelpLabel)
						.addComponent(rangeLabel).addComponent(rangeField).addComponent(rangeHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(aiTypeLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(confidenceLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(aiTypeBox, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(confidenceSpinner))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(aiHelpLabel).addComponent(confidenceHelpLabel))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(aggressionLabel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(rangeLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(aggressionSpinner, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE).addComponent(rangeField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(aggressionHelpLabel).addComponent(rangeHelpLabel)));
		aiPanel.setBorder(new TitledBorder("AI"));
		
		JTabbedPane servicePane = new JTabbedPane();
		servicePane.setBorder(new TitledBorder("Services"));
		
		JPanel spellPanel = new JPanel(new BorderLayout());
		spellBox = new JCheckBox("Spell trader");
		spellMakerBox = new JCheckBox("Spell maker");
		healerBox = new JCheckBox("Healer");
		JPanel spellBoxPanel = new JPanel();
		spellBoxPanel.add(spellBox, BorderLayout.PAGE_START);
		spellBoxPanel.add(spellMakerBox, BorderLayout.CENTER);
		spellBoxPanel.add(healerBox, BorderLayout.PAGE_END);
		spellPanel.add(spellBoxPanel, BorderLayout.PAGE_START);
		spellListModel = new DefaultListModel<String>();
		spellList = new JList<String>(spellListModel);
		spellList.addMouseListener(this);
		JScrollPane spellScroller = new JScrollPane(spellList);
		spellPanel.add(spellScroller, BorderLayout.CENTER);
		servicePane.add(spellPanel, "Magic");
		
		JPanel tradePanel = new JPanel(new BorderLayout());
		tradeBox = new JCheckBox("Trader");
		tradeBox.setHorizontalAlignment(SwingConstants.CENTER);
		tradePanel.add(tradeBox, BorderLayout.PAGE_START);
		itemListModel = new DefaultListModel<String>();
		itemList = new JList<String>(itemListModel);
		itemList.addMouseListener(this);
		JScrollPane itemScroller = new JScrollPane(itemList);
		tradePanel.add(itemScroller, BorderLayout.CENTER);
		servicePane.add(tradePanel, "Trade");
		
		JPanel skillPanel = new JPanel(new BorderLayout());
		skills = new HashMap<Skill, Integer>();
		trainedSkills = new HashSet<Skill>();
		trainBox = new JCheckBox("Skill trainer");
		trainBox.setHorizontalAlignment(SwingConstants.CENTER);
		skillPanel.add(trainBox, BorderLayout.PAGE_START);
		JPanel skillSubPanel = new JPanel();
		skillSubPanel.add(new JLabel("Skills: "));
		skillComboBox = new JComboBox<Skill>(Skill.values());
		skillComboBox.addActionListener(new SkillListListener());
		skillSubPanel.add(skillComboBox);
		skillField = new JFormattedTextField(NumberFormat.getIntegerInstance());
		skillField.setColumns(3);
		skillSubPanel.add(skillField);
		skillBox = new JCheckBox("Trainable?");
		skillSubPanel.add(skillBox);
		skillPanel.add(skillSubPanel);
		servicePane.add(skillPanel, "Training");

		JPanel travelPanel = new JPanel(new BorderLayout());
		destMap = new HashMap<String, Element>();
		travelBox = new JCheckBox("Travel agent");
		travelBox.setHorizontalAlignment(SwingConstants.CENTER);
		travelPanel.add(travelBox, BorderLayout.PAGE_START);
		destListModel = new DefaultListModel<String>();
		destList = new JList<String>(destListModel);
		destList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		destList.addMouseListener(this);
		destList.addListSelectionListener(new DestListAction());
		JScrollPane destScroller = new JScrollPane(destList);
		travelPanel.add(destScroller, BorderLayout.CENTER);
		JPanel destPanel = new JPanel();
		destPanel.add(new JLabel("x: "));
		destX = new JFormattedTextField(NumberFormat.getIntegerInstance());
		destX.setColumns(5);
		destPanel.add(destX);
		destPanel.add(new JLabel("y: "));
		destY = new JFormattedTextField(NumberFormat.getIntegerInstance());
		destY.setColumns(5);
		destPanel.add(destY);
		destPanel.add(new JLabel("price: "));
		destCost = new JFormattedTextField(NumberFormat.getIntegerInstance());
		destCost.setColumns(5);
		destPanel.add(destCost);
		travelPanel.add(destPanel, BorderLayout.PAGE_END);
		servicePane.add(travelPanel, "Travel");
		
		JPanel otherPanel = new JPanel();
		potionBox = new JCheckBox("Potion maker");
		otherPanel.add(potionBox);
		tattooBox = new JCheckBox("Tattoo artist");
		otherPanel.add(tattooBox);
		servicePane.add(otherPanel, "Other");
		
		// factions
		JPanel factionPanel = new JPanel();
		
		joinedFactions = new HashMap<String, Integer>();
		FactionListListener fl = new FactionListListener();
		
		factionBox = new JComboBox<RFaction>(Editor.resources.getResources(RFaction.class));
		factionBox.addActionListener(fl);
		factionPanel.add(factionBox);
		factionCheckBox = new JCheckBox();
		factionCheckBox.addItemListener(fl);
		factionPanel.add(factionCheckBox);
		factionSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		factionSpinner.addChangeListener(fl);
		factionPanel.add(factionSpinner);
		factionPanel.add(new JLabel(" "));
		factionPanel.add(HelpLabels.getFactionHelpLabel());
		factionPanel.setBorder(new TitledBorder("Factions"));

		npcProps.add(generalPanel);
		npcProps.add(aiPanel);
		npcProps.add(factionPanel);
		npcProps.add(servicePane);
		
		frame.add(new JScrollPane(npcProps), BorderLayout.CENTER);
	}
	
	protected void load() {
		nameField.setText(data.name);
		RCreature species = (RCreature)Editor.resources.getResource(data.species);
		raceBox.setSelectedItem(species);

		for(String s : data.factions.keySet()) {
			joinedFactions.put(s, data.factions.get(s));
		}
		factionCheckBox.setSelected(joinedFactions.containsKey(factionBox.getSelectedItem()));
		if(joinedFactions.containsKey(factionBox.getSelectedItem())) {
			factionSpinner.setValue(data.factions.get(factionBox.getSelectedItem()));
			factionSpinner.setEnabled(true);
		} else {
			factionSpinner.setEnabled(false);
			factionSpinner.setValue(0);
		}

		if(data.aiType != null) {
			aiTypeBox.setSelectedItem(data.aiType);
		} else if(species != null) {
			aiTypeBox.setSelectedItem(species.aiType);
		}
		if(data.aiRange > -1) {
			rangeField.setValue(data.aiRange);
		} else if(species != null) {
			rangeField.setValue(species.aiRange);			
		}
		if(data.aiAggr > -1) {
			aggressionSpinner.setValue(data.aiAggr);
		} else if(species != null) {
			rangeField.setValue(species.aiAggr);			
		}
		if(data.aiConf > -1) {
			confidenceSpinner.setValue(data.aiConf);
		} else if(species != null) {
			rangeField.setValue(species.aiConf);			
		}

		skills = data.skills;
		if(skills.containsKey(skillComboBox.getSelectedItem())) {
			skillField.setValue(skills.get(skillComboBox.getSelectedItem()));
		} else {
			skillField.setValue(0);
		}

		for(String rs : data.spells) {
			spellListModel.addElement(rs);
		}

		for(String i : data.items) {
			itemListModel.addElement(i);
		}

		for(Element service : data.services) {
			if(service.getAttributeValue("id").equals("trade")) {
				tradeBox.setSelected(true);
			} else if(service.getAttributeValue("id").equals("travel")) {
				travelBox.setSelected(true);
				for(Element d : service.getChildren()) {
					destListModel.addElement(d.getAttributeValue("name"));
					destMap.put(d.getAttributeValue("name"), d);
				}
			} else if(service.getAttributeValue("id").equals("training")) {
				trainBox.setSelected(true);
				for(Element s : service.getChildren()) {
					trainedSkills.add(Skill.valueOf(s.getText().toUpperCase()));
				}
				skillBox.setSelected(trainedSkills.contains(skillComboBox.getSelectedItem()));
			} else if(service.getAttributeValue("id").equals("spells")) {
				spellBox.setSelected(true);
			} else if(service.getAttributeValue("id").equals("spellmaker")) {
				spellMakerBox.setSelected(true);
			} else if(service.getAttributeValue("id").equals("healer")) {
				healerBox.setSelected(true);
			} else if(service.getAttributeValue("id").equals("alchemy")) {
				potionBox.setSelected(true);
			} else if(service.getAttributeValue("id").equals("tattoo")) {
				tattooBox.setSelected(true);
			} 
		}	
	}
	
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			if(e.getComponent() == itemList) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(new ItemListAction("Add item"));
				menu.add(new ItemListAction("Delete item"));
				menu.show(e.getComponent(), e.getX(), e.getY());
				itemList.setSelectedIndex(itemList.locationToIndex(e.getPoint()));
			} else if(e.getComponent() == spellList) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(new SpellListAction("Add spell"));
				menu.add(new SpellListAction("Delete spell"));
				menu.show(e.getComponent(), e.getX(), e.getY());
				spellList.setSelectedIndex(spellList.locationToIndex(e.getPoint()));
			} else if(e.getComponent() == destList) {
				JPopupMenu menu = new JPopupMenu();
				menu.add(new DestListAction("Add destination"));
				menu.add(new DestListAction("Delete destination"));
				menu.show(e.getComponent(), e.getX(), e.getY());
				destList.setSelectedIndex(destList.locationToIndex(e.getPoint()));
			}
		}
	}
	
	protected void save() {
		data.name = nameField.getText();
		RCreature species = raceBox.getItemAt(raceBox.getSelectedIndex());
		data.species = species.id;
		
		if(species.aiType.equals(aiTypeBox.getItemAt(aiTypeBox.getSelectedIndex()))) {
			data.aiType = null;
		} else {
			data.aiType = aiTypeBox.getItemAt(aiTypeBox.getSelectedIndex());			
		}
		if(species.aiRange == (Integer)rangeField.getValue()) {
			data.aiRange = -1;
		} else {
			data.aiRange = (Integer)rangeField.getValue();
		}
		if(species.aiAggr == (Integer)aggressionSpinner.getValue()) {
			data.aiAggr = -1;
		} else {
			data.aiAggr = (Integer)aggressionSpinner.getValue();
		}
		if(species.aiConf == (Integer)confidenceSpinner.getValue()) {
			data.aiConf = -1;
		} else {
			data.aiConf = (Integer)confidenceSpinner.getValue();
		}

		data.factions.clear();
		for(String f : joinedFactions.keySet()) {
			data.factions.put(f, joinedFactions.get(f));
		}

		data.services.clear();
		if(tradeBox.isSelected()) {
			data.services.add(new Element("service").setAttribute("id", "trade"));
		}
		data.items.clear();
		for (Enumeration<String> e = itemListModel.elements(); e.hasMoreElements();) {
			data.items.add(e.nextElement());
		}

		if(spellMakerBox.isSelected()) {
			data.services.add(new Element("service").setAttribute("id", "spellmaker"));				
		}
		if(healerBox.isSelected()) {
			data.services.add(new Element("service").setAttribute("id", "healer"));				
		}
		if(spellBox.isSelected()) {
			data.services.add(new Element("service").setAttribute("id", "spells"));				
		}
		for(Enumeration<String> e = spellListModel.elements(); e.hasMoreElements();) {
			data.spells.add(e.nextElement());
		}

		if(trainBox.isSelected()) {
			Element training = new Element("service");
			training.setAttribute("id", "training");
			data.services.add(training);
			for(Skill s : trainedSkills) {
				training.addContent(new Element("skill").setText(s.toString()));
			}
		}
		data.skills.clear();
		for(Skill s : skills.keySet()) {
			if(skills.get(s) != null && !skills.get(s).equals(0)) {
				skills.put(s, skills.get(s));
			}
		}

		if(travelBox.isSelected()) {
			Element travel = new Element("service");
			travel.setAttribute("id", "travel");
			// effen wat magic om laatst aangepaste waarde toch nog in destMap te frommelen
			if(currentDest != null) {
				currentDest.setAttribute("x", destX.getValue().toString());
				currentDest.setAttribute("y", destY.getValue().toString());
				currentDest.setAttribute("cost", destCost.getValue().toString());					
			}				
			// magic gedaan
			for(Element d : destMap.values()) {
				d.detach();
				travel.addContent(d);
			}
			data.services.add(travel);
		}
		
		if(potionBox.isSelected()) {
			data.services.add(new Element("service").setAttribute("id", "alchemy"));				
		}
		
		if(tattooBox.isSelected()) {
			data.services.add(new Element("service").setAttribute("id", "tattoo"));				
		}
		
		data.setPath(Editor.getStore().getActive().get("id"));
	}
	
	private class SkillListListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			try {
				skills.put(currentSkill, Integer.parseInt(skillField.getText()));
			} catch (NumberFormatException f) { }
			if(skillBox.isSelected()) {
				trainedSkills.add(currentSkill);
			} else {
				trainedSkills.remove(currentSkill);
			}
			
			Skill skill = (Skill)skillComboBox.getSelectedItem();
			if(skills.containsKey(skill)) {
				skillField.setText(skills.get(skill).toString());
			} else {
				skillField.setText("0");
			}
			skillBox.setSelected(trainedSkills.contains(skill));
			currentSkill = skill;
		}
	}
	
	private class FactionListListener implements ActionListener, ItemListener, ChangeListener {
		public void actionPerformed(ActionEvent e) {
			String faction = factionBox.getSelectedItem().toString();
			factionCheckBox.setSelected(joinedFactions.containsKey(faction));
			if(joinedFactions.containsKey(faction)) {
				factionSpinner.setEnabled(true);
				factionSpinner.setValue(joinedFactions.get(faction));
			} else {
				factionSpinner.setEnabled(false);
				factionSpinner.setValue(0);				
			}
		}
		
		public void itemStateChanged(ItemEvent e) {
			String faction = factionBox.getSelectedItem().toString();
			if(e.getSource() == factionCheckBox) {
				if(factionCheckBox.isSelected()) {
					if(!joinedFactions.containsKey(faction)) {
						joinedFactions.put(faction, (Integer)factionSpinner.getValue());
					}
					factionSpinner.setEnabled(true);
				} else {
					joinedFactions.remove(faction);
					factionSpinner.setEnabled(false);
				}		        
			} 			
		}

		public void stateChanged(ChangeEvent ce) {
			String faction = factionBox.getSelectedItem().toString();
			if(joinedFactions.containsKey(faction)) {
				joinedFactions.put(faction, (Integer)factionSpinner.getValue());
				System.out.println("state.factions.put: " + (Integer)factionSpinner.getValue());
			}
		}
	}
	
	@SuppressWarnings("serial")
	private class SpellListAction extends AbstractAction {
		public SpellListAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add spell")) {
				String s = (String)JOptionPane.showInputDialog(frame, "New spell:",
						"New spell", JOptionPane.PLAIN_MESSAGE, null, spells.toArray(), 0);
				if (s != null) {
					spellListModel.addElement(s);
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
				String s = (String)JOptionPane.showInputDialog(frame, "Add item:",
						"Add item", JOptionPane.PLAIN_MESSAGE, null, items, 0);
				if (s != null) {
					itemListModel.addElement(s);
				}
			} else if(e.getActionCommand().equals("Delete item")) {
				itemListModel.remove(itemList.getSelectedIndex());
			}						
		}
	}
	
	@SuppressWarnings("serial")
	private class DestListAction extends AbstractAction implements ListSelectionListener {
		public DestListAction() {
			super();
		}
		
		public DestListAction(String name) {
			super(name);
		}
		
		public void valueChanged(ListSelectionEvent e) {
			try {	// in geval npc geen travel agent is
				if(currentDest != null) {
					currentDest.setAttribute("x", destX.getValue().toString());
					currentDest.setAttribute("y", destY.getValue().toString());
					currentDest.setAttribute("cost", destCost.getValue().toString());					
				}
				currentDest = destMap.get(destList.getSelectedValue());
				destX.setValue(Integer.parseInt(currentDest.getAttributeValue("x")));
				destY.setValue(Integer.parseInt(currentDest.getAttributeValue("y")));
				destCost.setValue(Integer.parseInt(currentDest.getAttributeValue("cost")));
			} catch (NullPointerException f) {}
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add destination")) {
				String s = (String)JOptionPane.showInputDialog(frame, "New destination:",
						"New destination", JOptionPane.QUESTION_MESSAGE);
				if ((s != null) && (s.length() > 0)) {
					destListModel.addElement(s);
					Element dest = new Element("dest");
					dest.setAttribute("name", s);
					dest.setAttribute("x", "0");
					dest.setAttribute("y", "0");
					dest.setAttribute("cost", "0");
					destMap.put(s, dest);
				}
			} else if(e.getActionCommand().equals("Delete destination")) {
				destMap.remove(destList.getSelectedValue());
				destListModel.remove(destList.getSelectedIndex());
			}			
		}
	}
}
