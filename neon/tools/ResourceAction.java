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

import java.awt.event.ActionEvent;
import java.util.Collection;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import neon.objects.resources.LSpell;
import neon.objects.resources.RCraft;
import neon.objects.resources.RData;
import neon.objects.resources.RDungeonTheme;
import neon.objects.resources.RItem;
import neon.objects.resources.RQuest;
import neon.objects.resources.RRegionTheme;
import neon.objects.resources.RSign;
import neon.objects.resources.RSpell;
import neon.objects.resources.RTattoo;
import neon.objects.resources.RRecipe;
import neon.objects.resources.RZoneTheme;
import neon.objects.resources.RSpell.SpellType;
import neon.tools.resources.RFaction;
import neon.tools.ResourceNode.ResourceType;

@SuppressWarnings("serial")
public class ResourceAction extends AbstractAction {
	private ResourceType type;
	private JTree tree;
	private DefaultTreeModel model;
	private ResourceTreeListener listener;

	public ResourceAction(String name, ResourceType type, ResourceTreeListener listener) {
		super(name);
		this.type = type;
		this.listener = listener;
		tree = listener.getTree();;
		model = (DefaultTreeModel)tree.getModel();
	}

	public void actionPerformed(ActionEvent e) {
		String mod = Editor.getStore().getActive().get("id");
		RData object = null;
		if(e.getActionCommand().startsWith("New")) {
			NewObjectDialog.Properties props = new NewObjectDialog().showInputDialog(Editor.getFrame(), 
					"Create new " + type.toString().toLowerCase(), type.getNamespace());
			if(!props.cancelled()) {
				switch(type) {
				case RECIPE:
					Collection<RItem.Potion> potions = Editor.resources.getResources(RItem.Potion.class);
					if(!potions.isEmpty()) {	// kijken of er al potions zijn aangemaakt
						RItem potion = (RItem)JOptionPane.showInputDialog(Editor.getFrame(), "Choose potion:",
								"Create new recipe", JOptionPane.PLAIN_MESSAGE, null, potions.toArray(), 0);
						if(potion != null) {
							object = new RRecipe(props.getID(), potion, mod);
							Editor.resources.addResource((RRecipe)object, "magic");
						}
					} else {
						JOptionPane.showMessageDialog(Editor.getFrame(), "No potions defined!");
					}
					break;
				case CRAFT:
					Collection<RItem> items = Editor.resources.getResources(RItem.class);
					if(!items.isEmpty()) {	// kijken of er al items zijn aangemaakt
						RItem craft = (RItem)JOptionPane.showInputDialog(Editor.getFrame(), "Choose item:",
								"Create new item", JOptionPane.PLAIN_MESSAGE, null, items.toArray(), 0);
						if (craft != null) {
							object = new RCraft(props.getID(), craft, mod);
							Editor.resources.addResource((RCraft)object);
						}
					} else {
						JOptionPane.showMessageDialog(Editor.getFrame(), "No items defined!");					
					}
					break;
				case FACTION: 
					object = new RFaction(props.getID(), mod);
					Editor.resources.addResource(object, "faction");
					break;
				case REGION:
					object = new RRegionTheme(props.getID(), mod); 
					Editor.resources.addResource(object, "theme");
					break;
				case ZONE:
					object = new RZoneTheme(props.getID(), mod); 
					Editor.resources.addResource(object, "theme");
					break;
				case DUNGEON:
					object = new RDungeonTheme(props.getID(), mod); 
					Editor.resources.addResource(object, "theme");
					break;
				case QUEST:
					object = new RQuest(props.getID(), mod);
					Editor.resources.addResource(object, "quest");
					break;
				case CURSE: 
					object = new RSpell(props.getID(), SpellType.CURSE, mod);
					Editor.resources.addResource(object, "magic");
					break;
				case DISEASE:
					object = new RSpell(props.getID(), SpellType.DISEASE, mod);
					Editor.resources.addResource(object, "magic");
					break;
				case ENCHANTMENT: 
					object = new RSpell.Enchantment(props.getID(), mod);
					Editor.resources.addResource(object, "magic");
					break;
				case LEVEL_SPELL:
					object = new LSpell(props.getID(), mod);
					Editor.resources.addResource(object, "magic");
					break;
				case POISON:
					object = new RSpell(props.getID(), SpellType.POISON, mod);
					Editor.resources.addResource(object, "magic");
					break;
				case POWER:
					object = new RSpell.Power(props.getID(), mod);
					Editor.resources.addResource(object, "magic");
					break;
				case SIGN:
					object = new RSign(props.getID(), mod);
					Editor.resources.addResource(object, "magic");
					break;
				case SPELL:
					object = new RSpell(props.getID(), SpellType.SPELL, mod);
					Editor.resources.addResource(object, "magic");
					break;
				case TATTOO:
					object = new RTattoo(props.getID(), mod);
					Editor.resources.addResource(object, "magic");
					break;
				default: 
					object = new RSpell(props.getID(), SpellType.SPELL, mod);
					Editor.resources.addResource(object, "magic");
					break;
				}

				// node op juiste plaats in tree duwen
				ResourceNode node = new ResourceNode(object, type);
				DefaultMutableTreeNode root = (DefaultMutableTreeNode)model.getRoot();
				listener.launchEditor(node);				
				for(int i = 0; i < root.getChildCount(); i++) {
					ResourceNode parent = (ResourceNode)root.getChildAt(i);
					if(parent.getType() == type) {
						parent.add(node);
						tree.updateUI();	// anders update de tree niet na toevoegen
						Object[] nodes = {root, parent, node};
						TreePath path = new TreePath(nodes);
						tree.scrollPathToVisible(path);
						tree.setSelectionPath(path);
					}
				}
			}
		} else if(e.getActionCommand().equals("Edit resource")) {
			listener.launchEditor((ResourceNode)tree.getLastSelectedPathComponent());
		} else if(e.getActionCommand().equals("Delete resource")) {
			// uit tree en datastore halen
			ResourceNode node = (ResourceNode)tree.getLastSelectedPathComponent();
			model.removeNodeFromParent(node);
			Editor.resources.removeResource(node.getResource());
		} 
	}
}