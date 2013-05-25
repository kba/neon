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

package neon.tools.maps;

import java.awt.event.*;
import javax.swing.*;
import java.awt.BorderLayout;
import java.util.Enumeration;
import javax.swing.border.*;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import neon.objects.resources.RDungeonTheme;
import neon.tools.Editor;
import neon.tools.resources.RMap;

public class MapInfoEditor implements ActionListener {
	private JDialog frame;
	private JPanel itemProps;
	private JTextField nameField;
	private JComboBox<RDungeonTheme> themeBox;
	private RMap data;
	private MapTreeNode node;
	private JTree tree;
	
	public MapInfoEditor(JFrame parent, MapTreeNode node, JTree tree) {
		data = node.getMap();
		frame = new JDialog(parent, "Map editor: " + data.id);
		JPanel content = new JPanel(new BorderLayout());
		frame.setContentPane(content);
		this.node = node;
		this.tree = tree;
				
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
		
		itemProps = new JPanel();
		GroupLayout layout = new GroupLayout(itemProps);
		itemProps.setLayout(layout);
		layout.setAutoCreateGaps(true);

		JLabel nameLabel = new JLabel("Name: ");
		JLabel themeLabel = new JLabel("Theme: ");
		nameField = new JTextField(15);
		themeBox = new JComboBox<RDungeonTheme>(Editor.resources.getResources(RDungeonTheme.class));
		themeBox.addItem(null);
		themeBox.setEnabled(node.getMap().isDungeon());
		themeBox.addActionListener(this);
		layout.setVerticalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(nameLabel).addComponent(nameField))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(themeLabel).addComponent(themeBox)));
		layout.setHorizontalGroup(
				layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(nameLabel).addComponent(themeLabel))
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING, false)
						.addComponent(nameField, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(themeBox)));
		itemProps.setBorder(new TitledBorder("Properties"));

		content.add(itemProps, BorderLayout.CENTER);
		
		initProps();
	}
	
	public void show() {
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}
	
	private void initProps() {
		nameField.setText(data.name);
		themeBox.setSelectedItem(data.theme);
	}

	private void save() {
		data.name = nameField.getText();
		if(themeBox.getSelectedItem() != null) {
			String question = "You have set a dungeon theme. Saving will delete all current dungeon zones. Do you wish to continue?";
			if(data.zones.isEmpty() || JOptionPane.showConfirmDialog(frame, question, "Theme warning", 
					JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				data.zones.clear();
				DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
				for(Enumeration<MutableTreeNode> mtn = node.children(); mtn.hasMoreElements();) {
					model.removeNodeFromParent(mtn.nextElement());
				}
				data.theme = (RDungeonTheme)themeBox.getSelectedItem();				
			}
		}
		data.setPath(Editor.getStore().getActive().get("id"));
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
}