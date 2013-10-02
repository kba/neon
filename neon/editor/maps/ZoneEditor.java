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

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.border.TitledBorder;
import javax.swing.table.*;

import neon.editor.Editor;
import neon.editor.resources.IContainer;
import neon.editor.resources.IDoor;
import neon.editor.resources.IObject;
import neon.editor.resources.Instance;
import neon.editor.resources.RZone;
import neon.resources.RZoneTheme;
import neon.ui.graphics.Renderable;

public class ZoneEditor implements ActionListener {
	private JDialog frame;
	private JTable table;
	private ZoneTreeNode node;
	private RZone zone;
	private JTextField nameField;
	private JComboBox<RZoneTheme> themeBox;
	
	public ZoneEditor(ZoneTreeNode node, JFrame parent) {
		JPanel content = new JPanel(new BorderLayout());
		frame = new JDialog(parent, "Zone editor: " + node);
		frame.setContentPane(content);
		this.node = node;
		zone = node.getZone();
		
		JPanel props = new JPanel();
		GroupLayout layout = new GroupLayout(props);
		props.setLayout(layout);
		layout.setAutoCreateGaps(true);

		JLabel nameLabel = new JLabel("Name: ");
		JLabel themeLabel = new JLabel("Theme: ");
		nameField = new JTextField(15);
		themeBox = new JComboBox<RZoneTheme>(Editor.resources.getResources(RZoneTheme.class));
		themeBox.addItem(null);
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
		props.setBorder(new TitledBorder("Properties"));
		content.add(props, BorderLayout.PAGE_START);
		
		String[] columnNames = {"ID", "X", "Y"};
		Object[][] data = loadTable();
		table = new JTable(data, columnNames);
		table.getColumn("ID").setCellRenderer(new ElementRenderer());
		table.getColumn("ID").setCellEditor(new InstanceEditor());
		JScrollPane scroller = new JScrollPane(table);
		scroller.setBorder(new TitledBorder("Contents"));
		content.add(scroller, BorderLayout.CENTER);

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
		content.add(buttons, BorderLayout.PAGE_END);
	}
	
	public void show() {
		nameField.setText(zone.name);
		themeBox.setSelectedItem(zone.theme);
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
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
	
	private void save() {
		zone.name = nameField.getText();
		if(themeBox.getSelectedItem() != null) {
			String question = "You have set a dungeon theme. Saving will delete all current zone contents. Do you wish to continue?";
			if(zone.getScene().getElements().isEmpty() || JOptionPane.showConfirmDialog(frame, 
					question, "Theme warning", JOptionPane.YES_NO_OPTION) == JOptionPane.OK_OPTION) {
				zone.getScene().clear();
				zone.theme = (RZoneTheme)themeBox.getSelectedItem();
			} 
		} else {
			for(int i = 0; i < table.getRowCount(); i++) {
				Instance e = (Instance)table.getModel().getValueAt(i, 0);
				if(e.x != toInt(table.getValueAt(i, 1)) || e.y != toInt(table.getValueAt(i, 2))) {
					e.x = toInt(table.getValueAt(i, 1));
					e.y = toInt(table.getValueAt(i, 2));
					zone.getScene().removeElement(e);
					zone.getScene().addElement(e, new Rectangle(e.x, e.y, e.width, e.height), e.z);
				}
			}
		}
	}

	private int toInt(Object number) {
		Integer value = 0;
		if(number instanceof String) {
			value = Integer.parseInt((String)number);
		} else if(number instanceof Integer) {
			value = (Integer)number;
		}
		return value;
	}
	
	private Object[][] loadTable() {
		Collection<Renderable> stuff = new ArrayList<Renderable>();
		if(zone.getScene() == null) {
			zone.map.load();
		}
		for(Renderable i : zone.getScene().getElements()) {
			if(i instanceof IObject) {
				stuff.add(i);
			}
		}
		Object[][] data = new Object[stuff.size()][3];
		
		int i = 0;
		for(Renderable e : stuff) {
			Object[] row = {e, e.getBounds().x, e.getBounds().y};
			data[i] = row;
			i++;
		}

		return data;
	}
	
	@SuppressWarnings("serial")
	private class InstanceEditor extends AbstractCellEditor implements TableCellEditor, MouseListener {
		private IObject instance;
		
		public Object getCellEditorValue() {
			return instance;
		}
		
		public void mouseExited(MouseEvent e) {}
		public void mouseEntered(MouseEvent e) {}
		public void mouseReleased(MouseEvent e) {}
		public void mouseClicked(MouseEvent e) {}
		public void mousePressed(MouseEvent e) {		
			if(e.getButton() == MouseEvent.BUTTON1) {
				if(e.getClickCount() == 2) {
					if(instance instanceof IDoor) {
						new DoorInstanceEditor((IDoor)instance, Editor.getFrame()).show();
					} else if(instance instanceof IContainer) {
						new ContainerInstanceEditor((IContainer)instance, Editor.getFrame(), node).show();
					}
				}
			}
		}

		public JComponent getTableCellEditorComponent(JTable table, Object object, boolean isSelected, int row, int column) {
			this.instance = (IObject)table.getValueAt(row, column);
			JLabel label = new JLabel(instance.toElement().getAttributeValue("id"));
			label.setFont(new Font(label.getFont().getName(), Font.BOLD, label.getFont().getSize()));
			label.addMouseListener(this);
			return label;
		}
	}
	
	@SuppressWarnings("serial")
	private static class ElementRenderer extends DefaultTableCellRenderer {
		@Override
	    public void setValue(Object value) {
	        setText((value == null) ? "" : ((Instance)value).toElement().getAttributeValue("id"));
	    }
	}
}
