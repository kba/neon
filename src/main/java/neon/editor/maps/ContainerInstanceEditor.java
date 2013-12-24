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

package neon.editor.maps;

import java.awt.BorderLayout;
import javax.swing.*;
import javax.swing.border.TitledBorder;

import neon.editor.Editor;
import neon.editor.help.HelpLabels;
import neon.editor.resources.IContainer;
import neon.editor.resources.IObject;
import neon.resources.RItem;
import neon.resources.RSpell;
import neon.resources.RItem.Type;

import org.jdom2.Element;
import java.awt.event.*;
import java.util.*;

public class ContainerInstanceEditor implements ActionListener, MouseListener {
	private IContainer container;
	private JDialog frame;
	private JList<IObject> items;
	private DefaultListModel<IObject> model;
	private ZoneTreeNode node;
	private JCheckBox lockBox, trapBox;
	private JSpinner lockSpinner, trapSpinner;
	private JComboBox<RItem> keyBox;
	private JComboBox<RSpell.Enchantment> spellBox;

	public ContainerInstanceEditor(IContainer ic, JFrame parent, ZoneTreeNode node) {
		container = ic;
		this.node = node;
		frame = new JDialog(parent, "Container instance editor: " + container.resource.id);
		JPanel content = new JPanel(new BorderLayout());
		frame.setContentPane(content);
		
		JPanel buttons = new JPanel();
		content.add(buttons, BorderLayout.PAGE_END);
		JButton ok = new JButton("Ok");
		ok.addActionListener(this);
		JButton cancel = new JButton("Cancel");
		cancel.addActionListener(this);
		JButton apply = new JButton("Apply");
		apply.addActionListener(this);
		buttons.add(ok);
		buttons.add(cancel);
		buttons.add(apply);
		
		model = new DefaultListModel<IObject>();
		items = new JList<IObject>(model);
		items.addMouseListener(this);
		JScrollPane scroller = new JScrollPane(items);
		scroller.setBorder(new TitledBorder("Contents"));		
		content.add(scroller, BorderLayout.PAGE_START);
		
		JPanel lockPanel = new JPanel();
		lockPanel.setBorder(new TitledBorder("Lock"));
		GroupLayout lockLayout = new GroupLayout(lockPanel);
		lockPanel.setLayout(lockLayout);
		lockLayout.setAutoCreateGaps(true);
		JLabel choiceLabel = new JLabel("Lockable: ");
		JLabel lockLabel = new JLabel("Lock difficulty: ");
		JLabel keyLabel = new JLabel("Key: ");
		lockBox = new JCheckBox();
		lockSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		keyBox = new JComboBox<RItem>();
		keyBox.addItem(null);
		JLabel lockableHelpLabel = HelpLabels.getLockableHelpLabel();
		JLabel lockHelpLabel = HelpLabels.getLockDCHelpLabel();
		JLabel keyHelpLabel = HelpLabels.getKeyHelpLabel();
		lockLayout.setVerticalGroup(
				lockLayout.createSequentialGroup()
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(choiceLabel).addComponent(lockBox).addComponent(lockableHelpLabel))
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(lockLabel).addComponent(lockSpinner).addComponent(lockHelpLabel))
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(keyLabel).addComponent(keyBox).addComponent(keyHelpLabel)));
		lockLayout.setHorizontalGroup(
				lockLayout.createSequentialGroup()
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(choiceLabel).addComponent(lockLabel).addComponent(keyLabel))
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(lockBox).addComponent(lockSpinner).addComponent(keyBox))
				.addGap(10)
				.addGroup(lockLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(lockableHelpLabel).addComponent(lockHelpLabel).addComponent(keyHelpLabel)));
		content.add(lockPanel, BorderLayout.CENTER);
		
		JPanel trapPanel = new JPanel();
		trapPanel.setBorder(new TitledBorder("Trap"));
		GroupLayout trapLayout = new GroupLayout(trapPanel);
		trapPanel.setLayout(trapLayout);
		trapLayout.setAutoCreateGaps(true);
		JLabel trapLabel = new JLabel("Trapped: ");
		JLabel trapDCLabel = new JLabel("Trap difficulty: ");
		JLabel spellLabel = new JLabel("Spell: ");
		trapBox = new JCheckBox();
		trapSpinner = new JSpinner(new SpinnerNumberModel(0, 0, 100, 1));
		spellBox = new JComboBox<RSpell.Enchantment>();
		trapLayout.setVerticalGroup(
				trapLayout.createSequentialGroup()
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(trapLabel).addComponent(trapBox))
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(trapDCLabel).addComponent(trapSpinner))
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(spellLabel).addComponent(spellBox)));
		trapLayout.setHorizontalGroup(
				trapLayout.createSequentialGroup()
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(trapLabel).addComponent(trapDCLabel).addComponent(spellLabel))
				.addGroup(trapLayout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(trapBox).addComponent(trapSpinner).addComponent(spellBox)));
		content.add(trapPanel, BorderLayout.PAGE_END);

		initContainer();
	}

	public void show() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void initContainer() {
		for(IObject io : container.contents) {
			model.addElement(io);
		}
		
		// keys laden
		for(RItem ri : Editor.resources.getResources(RItem.class)) {
			if(ri.type == Type.item) {
				keyBox.addItem(ri);
			}
		}
		
		// lockable
		lockBox.setSelected(container.lock > 0);
		lockSpinner.setValue(container.lock);
		keyBox.setSelectedItem(container.key);

		// spells laden
		for(RSpell.Enchantment rs: Editor.resources.getResources(RSpell.Enchantment.class)) {
			if(rs.item.equals("trap")) {
				spellBox.addItem(rs);
			}
		}
		
		// trap
		trapBox.setSelected(container.trap > 0);
		trapSpinner.setValue(container.trap);
		spellBox.setSelectedItem(container.spell);
	}

	public void actionPerformed(ActionEvent e) {
		if ("Ok".equals(e.getActionCommand())) {
			save();
			frame.dispose();
		} else if ("Cancel".equals(e.getActionCommand())){
			frame.dispose();
		} else if ("Apply".equals(e.getActionCommand())){
			save();
		}
	}
	
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {}
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			menu.add(new ItemListAction("Add item"));
			menu.add(new ItemListAction("Delete item"));
			menu.show(e.getComponent(), e.getX(), e.getY());
			items.setSelectedIndex(items.locationToIndex(e.getPoint()));
		}
	}

	public void save() {
		// eerste oude objecten verwijderen
		for(Element e : container.toElement().getChildren("item")) {
			node.getZone().map.removeObjectUID(Integer.parseInt(e.getAttributeValue("uid")));
		}
		container.contents.clear();
		// dan nieuwe objecten insteken
		for(Enumeration<IObject> e = model.elements(); e.hasMoreElements();) {
			IObject io = e.nextElement();
			container.contents.add(io);
		}
		// lock
		if(lockBox.isSelected()) {
			container.lock = (Integer)lockSpinner.getValue();
			container.key = (RItem)keyBox.getSelectedItem();
		} else {
			container.lock = 0;
			container.key = null;
		}
		// lock
		if(trapBox.isSelected()) {
			container.trap = (Integer)trapSpinner.getValue();
			container.spell = (RSpell.Enchantment)spellBox.getSelectedItem();
		} else {
			container.trap = 0;
			container.spell = null;
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
				RItem ri = (RItem)JOptionPane.showInputDialog(frame, "Add item:",
						"Add item", JOptionPane.PLAIN_MESSAGE, null, items, 0);
				if (ri != null) {
					IObject io = new IObject(ri, 0, 0, 0, 0);
					model.addElement(io);
				}
			} else if(e.getActionCommand().equals("Delete item")) {
				model.remove(items.getSelectedIndex());
			}						
		}
	}
}
