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

import java.awt.event.*;
import javax.swing.*;
import java.awt.*;
import java.util.Enumeration;
import javax.swing.border.*;

import neon.editor.ColorCellRenderer;
import neon.editor.Editor;
import neon.editor.help.HelpLabels;
import neon.resources.RItem;
import neon.util.ColorFactory;

public class ContainerEditor extends ObjectEditor implements MouseListener {
	private JTextField nameField;
	private JComboBox<String> colorBox;
	private JFormattedTextField charField;
	private RItem.Container data;
	private JList<RItem> items;
	private DefaultListModel<RItem> model;
	
	public ContainerEditor(JFrame parent, RItem.Container data) {
		super(parent, "Container Editor: " + data.id);
		frame.setPreferredSize(new Dimension(400, 200));
		this.data = data;
				
		JPanel itemProps = new JPanel();
		GroupLayout layout = new GroupLayout(itemProps);
		itemProps.setLayout(layout);
		layout.setAutoCreateGaps(true);

		JLabel nameLabel = new JLabel("Name: ");
		JLabel colorLabel = new JLabel("Color: ");
		JLabel charLabel = new JLabel("Character: ");
		nameField = new JTextField(15);
		colorBox = new JComboBox<String>(ColorFactory.getColorNames());
		colorBox.setBackground(Color.black);
		colorBox.setRenderer(new ColorCellRenderer());
		colorBox.addActionListener(new ColorListener(colorBox));
		charField = new JFormattedTextField(getMaskFormatter("*", 'X'));
		JLabel nameHelpLabel = HelpLabels.getNameHelpLabel();
		JLabel colorHelpLabel = HelpLabels.getColorHelpLabel();
		JLabel charHelpLabel = HelpLabels.getCharHelpLabel();
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameLabel).addComponent(nameField).addComponent(nameHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(colorLabel).addComponent(colorBox).addComponent(colorHelpLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(charLabel).addComponent(charField).addComponent(charHelpLabel)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(colorLabel).addComponent(charLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(colorBox).addComponent(charField))
				.addGap(10)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameHelpLabel).addComponent(colorHelpLabel).addComponent(charHelpLabel)));

		JScrollPane propScroller = new JScrollPane(itemProps);
		propScroller.setBorder(new TitledBorder("Properties"));
		frame.add(propScroller, BorderLayout.LINE_START);
		
		model = new DefaultListModel<RItem>();
		items = new JList<RItem>(model);
		items.addMouseListener(this);
		JScrollPane itemScroller = new JScrollPane(items);
		itemScroller.setBorder(new TitledBorder("Contents"));
		frame.add(itemScroller, BorderLayout.CENTER);
	}
	
	protected void save() {
		data.name = nameField.getText();
		data.color = colorBox.getSelectedItem().toString();
		data.text = charField.getText();
		
		data.contents.clear();
		for(Enumeration<RItem> e = model.elements(); e.hasMoreElements();) {
			RItem ri = e.nextElement();
			data.contents.add(ri.id);
		}
		
		data.setPath(Editor.getStore().getActive().get("id"));
	}
	
	protected void load() {
		nameField.setText(data.name);
		colorBox.setSelectedItem(data.color);
		charField.setValue(data.text);
		for(String id : data.contents) {
			model.addElement((RItem)Editor.resources.getResource(id));
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
					model.addElement(ri);
			} else if(e.getActionCommand().equals("Delete item")) {
				model.remove(items.getSelectedIndex());
			}						
		}
	}
}
