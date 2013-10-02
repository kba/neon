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

package neon.editor.editors;

import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.border.*;

import neon.editor.DialogEditor;
import neon.editor.NeonFormat;
import neon.editor.help.HelpLabels;
import neon.narrative.Topic;
import neon.resources.RQuest;

import org.jdom2.Element;

public class QuestEditor extends ObjectEditor implements ActionListener, MouseListener {
	private RQuest quest;
	private JTextField nameField;
	private JFormattedTextField freqField;
	private DefaultTableModel conditionModel, varModel, dialogModel;
	private JTable conditionTable, varTable, dialogTable;
	private JCheckBox randomBox, initialBox;
	private ClickAction cAdd = new ClickAction("Add condition");
	private ClickAction cRemove = new ClickAction("Remove condition");
	private ClickAction vAdd = new ClickAction("Add variable");
	private ClickAction vRemove = new ClickAction("Remove variable");
	private ClickAction tAdd = new ClickAction("Add topic");
	private ClickAction tRemove = new ClickAction("Remove topic");
	
	public QuestEditor(JFrame parent, RQuest quest) {
		super(parent, "Quest Editor: " + quest.id);
		this.quest = quest;
		
		JPanel props = new JPanel(new GridLayout(0, 1));
		props.setBorder(new TitledBorder("Properties"));
		JPanel topPanel = new JPanel();
		JPanel bottomPanel = new JPanel();
		nameField = new JTextField(15);
		freqField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		freqField.setColumns(5);
		randomBox = new JCheckBox("Repeats");
		initialBox = new JCheckBox("Initial");
		JLabel initialHelpLabel = HelpLabels.getInitialHelpLabel();
		topPanel.add(new JLabel("Name: "));
		topPanel.add(nameField);
		topPanel.add(initialBox);
		topPanel.add(initialHelpLabel);
		bottomPanel.add(new JLabel("Chance: "));
		bottomPanel.add(freqField);
		bottomPanel.add(randomBox);
		props.add(topPanel);
		props.add(bottomPanel);
		randomBox.addActionListener(this);

		JTabbedPane stuff = new JTabbedPane();
		
		JPanel conditionPanel = new JPanel(new BorderLayout());
		JPanel cButtonPanel = new JPanel();
		cButtonPanel.add(new JButton(cAdd));
		cButtonPanel.add(new JButton(cRemove));
		conditionModel = new DefaultTableModel(0, 1);
		conditionTable = new JTable(conditionModel);
		conditionTable.setTableHeader(null);
		conditionTable.setFillsViewportHeight(true);
		conditionTable.addMouseListener(this);
		JScrollPane preScroller = new JScrollPane(conditionTable);
		conditionPanel.add(cButtonPanel, BorderLayout.PAGE_END);
		conditionPanel.add(preScroller, BorderLayout.CENTER);
		
		JPanel varPanel = new JPanel(new BorderLayout());
		JPanel vButtonPanel = new JPanel();
		vButtonPanel.add(new JButton(vAdd));
		vButtonPanel.add(new JButton(vRemove));
		String[] varColumns = {"name", "type", "id", "class"};
		varModel = new QuestsTableModel(varColumns, String.class, String.class, String.class, String.class);
		varTable = new JTable(varModel);
		varTable.setFillsViewportHeight(true);
		varTable.addMouseListener(this);
		TableColumn typeColumn = varTable.getColumnModel().getColumn(1);
		JComboBox<String> comboBox = new JComboBox<String>();
		comboBox.addItem("npc");
		comboBox.addItem("item");
		comboBox.addItem("creature");
		typeColumn.setCellEditor(new DefaultCellEditor(comboBox));
		JScrollPane varScroller = new JScrollPane(varTable);
		varPanel.add(vButtonPanel, BorderLayout.PAGE_END);
		varPanel.add(varScroller, BorderLayout.CENTER);
		
		JPanel dialogPanel = new JPanel(new BorderLayout());
		JPanel dButtonPanel = new JPanel();
		dButtonPanel.add(new JButton(tAdd));
		dButtonPanel.add(new JButton(tRemove));
		String[] dialogColumns = {"topic", "preconditions", "answer", "action"};
		dialogModel = new QuestsTableModel(dialogColumns, String.class, String.class, String.class, String.class);
		dialogTable = new JTable(dialogModel);
		dialogTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dialogTable.setFillsViewportHeight(true);
		dialogTable.addMouseListener(this);
		dialogTable.setDefaultEditor(String.class, new DialogEditor(parent));
		JScrollPane dialogScroller = new JScrollPane(dialogTable);
		dialogPanel.add(dButtonPanel, BorderLayout.PAGE_END);
		dialogPanel.add(dialogScroller, BorderLayout.CENTER);
		
		stuff.add("Prerequisites", conditionPanel);
		stuff.add("Variables", varPanel);
		stuff.add("Dialog", dialogPanel);
		stuff.setBorder(new TitledBorder("Contents"));
		
		JPanel center = new JPanel(new BorderLayout());
		center.add(props, BorderLayout.PAGE_START);
		center.add(stuff);

		frame.add(center, BorderLayout.CENTER);
	}

	protected void save() {
		// algemeen
		quest.initial = initialBox.isSelected();
		quest.repeat = randomBox.isSelected();
		if(randomBox.isSelected()) {
			quest.frequency = Integer.parseInt(freqField.getText());
		}
		quest.name = nameField.getText();
		
		// condities
		quest.conditions.clear();
		for(Vector<String> data : (Vector<Vector>)conditionModel.getDataVector()) {
			quest.conditions.add(data.get(0));
		}
		
		Element vars = new Element("objects");
		for(Vector<?> data : (Vector<Vector>)varModel.getDataVector()) {
			Element var = new Element(data.get(1).toString());
			var.setText(data.get(0).toString());
			if(data.get(2) != null) {
				var.setAttribute("id", data.get(2).toString());
			}
			if(data.get(3) != null) {
				var.setAttribute("type", data.get(3).toString());
			}
			vars.addContent(var);
		}
		quest.variables = vars;
		
		quest.getTopics().clear();
		for(Vector<?> data : (Vector<Vector>)dialogModel.getDataVector()) {
			String id = data.get(0).toString();
			String condition = (data.get(1) != null ? data.get(1).toString() : null);
			String answer = (data.get(2) != null ? data.get(2).toString() : null);
			String action = (data.get(3) != null ? data.get(3).toString() : null);
			quest.getTopics().add(new Topic(id, condition, answer, action));
		}
	}

	protected void load() {
		nameField.setText(quest.name);
		initialBox.setSelected(quest.initial);
		randomBox.setSelected(quest.repeat);
		freqField.setEnabled(quest.repeat);
		if(quest.repeat) {
			freqField.setValue(quest.frequency);
		} else {
			freqField.setValue(null);
		}
		
		if(quest.variables != null) {
			for(Element item : quest.variables.getChildren()) {
				String[] data = {item.getText(), item.getName(), 
						item.getAttributeValue("id"), item.getAttributeValue("type")};
				varModel.insertRow(0, data);
			}
		}
		
		for(String condition : quest.conditions) {
			String[] row = {condition};
			conditionModel.addRow(row);
		}
		
		for(Topic topic : quest.getTopics()) {
			String[] data = {topic.getID(), topic.getCondition(), 
					topic.getAnswer(), topic.getAction()};
			dialogModel.insertRow(0, data);
		}
	}
	
	public void actionPerformed(ActionEvent e) {
		if(randomBox.isSelected()) {
			freqField.setEnabled(true);
		} else {
			freqField.setEnabled(false);
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {	    }
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			if(e.getSource().equals(conditionTable)) {
				menu.add(cAdd);
				menu.add(cRemove);
				int row = conditionTable.rowAtPoint(e.getPoint());
				conditionTable.getSelectionModel().setSelectionInterval(row, row);
			} else if(e.getSource().equals(varTable)) {
				menu.add(vAdd);
				menu.add(vRemove);
				int row = varTable.rowAtPoint(e.getPoint());
				varTable.getSelectionModel().setSelectionInterval(row, row);
			} else if(e.getSource().equals(dialogTable)) {
				menu.add(tAdd);
				menu.add(tRemove);
				int row = dialogTable.rowAtPoint(e.getPoint());
				dialogTable.getSelectionModel().setSelectionInterval(row, row);
			}
			menu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@SuppressWarnings("serial")
	private class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add condition")) {
				String s = (String)JOptionPane.showInputDialog(frame, "Give new quest precondition:", 
						"New condition:", JOptionPane.PLAIN_MESSAGE, null, null, null);
				if(s!= null) {
					String[] row = {s};
					conditionModel.addRow(row);
				}
			} else if(e.getActionCommand().equals("Remove condition")) {
				conditionModel.removeRow(conditionTable.getSelectedRow());
			} else if(e.getActionCommand().equals("Add variable")) {
				String s = (String)JOptionPane.showInputDialog(frame, "Variable name:", 
						"Add variable", JOptionPane.PLAIN_MESSAGE, null, null, "");
				if(s!= null) {
					String[] row = {s, "item", "", ""};
					varModel.addRow(row);
				}
			} else if(e.getActionCommand().equals("Remove variable")) {
				varModel.removeRow(varTable.getSelectedRow());
			} else if(e.getActionCommand().equals("Add topic")) {
				String s = (String)JOptionPane.showInputDialog(frame, "Topic name:", 
						"Add topic", JOptionPane.PLAIN_MESSAGE, null, null, "");
				if(s!= null) {
					String[] row = {s, "", "", ""};
					dialogModel.addRow(row);					
				}
			} else if(e.getActionCommand().equals("Remove topic")) {
				dialogModel.removeRow(dialogTable.getSelectedRow());				
			}
		}
	}
	
	@SuppressWarnings("serial")
	private static class QuestsTableModel extends DefaultTableModel {
		private Class<?>[] classes;
		
		public QuestsTableModel(String[] columns, Class<?>... classes) {
			super(columns, 0);
			this.classes = classes;
		}
		
		public Class<?> getColumnClass(int i) {
			return classes[i];
		}

		public boolean isCellEditable(int row, int column) {
			return column != 0;
		}
	}
}
