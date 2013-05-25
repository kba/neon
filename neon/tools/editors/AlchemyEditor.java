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

package neon.tools.editors;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import neon.objects.resources.RItem;
import neon.objects.resources.RRecipe;
import neon.tools.Editor;
import neon.tools.NeonFormat;
import neon.tools.help.HelpLabels;

@SuppressWarnings("serial")
public class AlchemyEditor extends ObjectEditor implements MouseListener {
	private JDialog parent;
	private JList<RItem> contentList;
	private DefaultListModel<RItem> contentModel;
	private RRecipe recipe;
	private JFormattedTextField costField;
	
	public AlchemyEditor(JFrame parent, RRecipe recipe) {
		super(parent, "Recipe: " + recipe.id);
		this.recipe = recipe;
		
		contentModel = new DefaultListModel<RItem>();
		contentList = new JList<RItem>(contentModel);
		contentList.addMouseListener(this);
		JScrollPane textScroller = new JScrollPane(contentList);
		textScroller.setBorder(new TitledBorder("Ingredients"));	
		frame.add(textScroller, BorderLayout.CENTER);
		
		JLabel costLabel = new JLabel("Cost: ");
		costField = new JFormattedTextField(NeonFormat.getIntegerInstance());
		costField.setColumns(10);
		JLabel costHelpLabel = HelpLabels.getAlchemyCostHelpLabel();

		JPanel costPanel = new JPanel();
		costPanel.setBorder(new TitledBorder("Properties"));	
		costPanel.add(costLabel);
		costPanel.add(costField);
		costPanel.add(costHelpLabel);
		frame.add(costPanel, BorderLayout.PAGE_START);
	}
	
	protected void load() {
		contentModel.clear();
		costField.setValue(recipe.cost);
		for(String id : recipe.ingredients) {
			RItem ri = (RItem)Editor.resources.getResource(id);
			contentModel.addElement(ri);
		}
	}

	protected void save() {
		recipe.ingredients.clear();
		recipe.cost = (int)costField.getValue();
		for(Enumeration<RItem> items = contentModel.elements(); items.hasMoreElements();) {
			recipe.ingredients.add(items.nextElement().id);
		}
	}
	
	public void mousePressed(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseExited(MouseEvent e) {	    }
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			menu.add(new ClickAction("Add ingredient"));
			menu.add(new ClickAction("Remove ingredient"));
			menu.show(e.getComponent(), e.getX(), e.getY());
			contentList.setSelectedIndex(contentList.locationToIndex(new Point(e.getX(), e.getY())));
		}
	}

	public class ClickAction extends AbstractAction {
		public ClickAction(String name) {
			super(name);
		}
		
		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("Add ingredient")) {
				Object[] items = Editor.resources.getResources(RItem.class).toArray();
				RItem ingredient = (RItem)JOptionPane.showInputDialog(parent, "Add ingredient:",
						"Add ingredient", JOptionPane.PLAIN_MESSAGE, null, items, 0);
				if (ingredient != null) {
					contentModel.addElement(ingredient);
				}
			} else if(e.getActionCommand().equals("Remove ingredient")) {
				try {
					if(contentList.getSelectedIndex() >= 0) {
						contentModel.remove(contentList.getSelectedIndex());
					}
				} catch(ArrayIndexOutOfBoundsException a) {}
			}
		}
	}
}
