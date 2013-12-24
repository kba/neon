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

package neon.editor;

import java.awt.event.*;
import javax.swing.*;

import neon.editor.editors.ArmorEditor;
import neon.editor.editors.BookEditor;
import neon.editor.editors.ClothingEditor;
import neon.editor.editors.ContainerEditor;
import neon.editor.editors.CreatureEditor;
import neon.editor.editors.DoorEditor;
import neon.editor.editors.FoodEditor;
import neon.editor.editors.ItemEditor;
import neon.editor.editors.LevelCreatureEditor;
import neon.editor.editors.LevelItemEditor;
import neon.editor.editors.LightEditor;
import neon.editor.editors.MoneyEditor;
import neon.editor.editors.NPCEditor;
import neon.editor.editors.ObjectEditor;
import neon.editor.editors.PotionEditor;
import neon.editor.editors.ScrollEditor;
import neon.editor.editors.WeaponEditor;

import javax.swing.tree.*;

import neon.resources.*;
import neon.resources.RItem.Type;

public class ObjectTreeListener implements MouseListener {
	private JTree tree;
	private JFrame frame;
	private DefaultTreeModel model;
	
	public ObjectTreeListener(JTree objectTree, JFrame frame) {
		this.frame = frame;
		tree = objectTree;
		model = (DefaultTreeModel)tree.getModel();
	}
	
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {		
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getClickCount() == 2) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				if(tree.getSelectionPath() != null && node.getLevel() == 2) {
					launchEditor((ObjectNode)node);						
				} 
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			tree.setSelectionRow(tree.getRowForLocation(e.getX(), e.getY()));
			ObjectNode node = (ObjectNode)tree.getLastSelectedPathComponent();
			if(tree.getSelectionPath() != null && node.getLevel() == 2) {
				menu.add(new ClickAction("Delete object", node.getType()));
				menu.add(new ClickAction("Edit object", node.getType()));
				menu.add(new ClickAction("New " + node.getType().toString().toLowerCase(), node.getType()));	
				menu.show(e.getComponent(), e.getX(), e.getY());
			} else if(tree.getSelectionPath() != null && node.getLevel() == 1) {
				menu.add(new ClickAction("New " + node.getType().toString().toLowerCase(), node.getType()));				
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
	
	private void launchEditor(ObjectNode node) {
		ObjectEditor editor;
		switch(node.getType()) {
		case LEVEL_CREATURE: editor = new LevelCreatureEditor(frame, (LCreature)node.getResource()); break;
		case BOOK: editor = new BookEditor(frame, (RItem.Text)node.getResource()); break;
		case ARMOR: editor = new ArmorEditor(frame, (RClothing)node.getResource()); break;
		case CLOTHING: editor = new ClothingEditor(frame, (RClothing)node.getResource()); break;
		case CONTAINER: editor = new ContainerEditor(frame, (RItem.Container)node.getResource()); break;
		case DOOR: editor = new DoorEditor(frame, (RItem.Door)node.getResource()); break;
		case ITEM: editor = new ItemEditor(frame, (RItem)node.getResource()); break;
		case LIGHT: editor = new LightEditor(frame, (RItem)node.getResource()); break;
		case MONEY: editor = new MoneyEditor(frame, (RItem)node.getResource()); break;
		case NPC: editor = new NPCEditor(frame, (RPerson)node.getResource()); break;
		case POTION: editor = new PotionEditor(frame, (RItem)node.getResource()); break;
		case SCROLL: editor = new ScrollEditor(frame, (RItem.Text)node.getResource()); break;
		case LEVEL_ITEM: editor = new LevelItemEditor(frame, (LItem)node.getResource()); break;
		case WEAPON: editor = new WeaponEditor(frame, (RWeapon)node.getResource()); break;
		case FOOD: editor = new FoodEditor(frame, (RItem)node.getResource()); break;
		default: editor = new CreatureEditor(frame, (RCreature)node.getResource()); break;
		}
		editor.show();		
	}
	
	@SuppressWarnings("serial")
	public class ClickAction extends AbstractAction {
		private ObjectNode.ObjectType type;
		
		public ClickAction(String name, ObjectNode.ObjectType type) {
			super(name);
			this.type = type;
		}

		public void actionPerformed(ActionEvent e) {
			if(e.getActionCommand().equals("New " + type.toString().toLowerCase())) {
				NewObjectDialog.Properties props = new NewObjectDialog().showInputDialog(frame, 
						"Create new " + type.toString().toLowerCase());
				if(!props.cancelled()) {
					RData object;
					// item aanmaken en in dataStore toevoegen
					switch(type) {
					case CREATURE: 
						object = new RCreature(props.getID(), Editor.getStore().getActive().get("id")); break;
					case LEVEL_CREATURE:
						object = new LCreature(props.getID(), Editor.getStore().getActive().get("id")); break;
					case NPC: 
						object = new RPerson(props.getID(), Editor.getStore().getActive().get("id")); break;
					case BOOK:
					case SCROLL:
						object = new RItem.Text(props.getID(), Type.valueOf(type.toString().toLowerCase()), 
								Editor.getStore().getActive().get("id")); break;						
					case DOOR:
						object = new RItem.Door(props.getID(), Type.valueOf(type.toString().toLowerCase()), 
								Editor.getStore().getActive().get("id")); break;						
					case WEAPON:
						object = new RWeapon(props.getID(), Type.valueOf(type.toString().toLowerCase()), 
								Editor.getStore().getActive().get("id")); break;						
					case CLOTHING:
					case ARMOR:
						object = new RClothing(props.getID(), Type.valueOf(type.toString().toLowerCase()), 
								Editor.getStore().getActive().get("id")); break;						
					case LEVEL_ITEM:
						object = new LItem(props.getID(), Editor.getStore().getActive().get("id")); break;
					default: 
						object = new RItem(props.getID(), Type.valueOf(type.toString().toLowerCase()), 
								Editor.getStore().getActive().get("id")); break;
					}
					
					// node op juiste plaats in tree duwen
					Editor.resources.addResource(object);
					ObjectNode node = new ObjectNode(object, type);
					DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
					for(int i = 0; i < root.getChildCount(); i++) {
						ObjectNode parent = (ObjectNode)root.getChildAt(i);
						if(parent.getType() == type) {
							parent.add(node);
							tree.updateUI();	// anders update de tree niet na toevoegen
							Object[] nodes = {root, parent, node};
							TreePath path = new TreePath(nodes);
							tree.scrollPathToVisible(path);
							tree.setSelectionPath(path);
						}
					}					
					launchEditor(node);				
				}
			} else if(e.getActionCommand().equals("Edit object")) {
				launchEditor((ObjectNode)tree.getLastSelectedPathComponent());
			} else if(e.getActionCommand().equals("Delete object")) {
				// uit tree en datastore halen
				ObjectNode node = (ObjectNode)tree.getLastSelectedPathComponent();
				model.removeNodeFromParent(node);
				Editor.resources.removeResource(node.getResource());
			} 
		}
	}
}
