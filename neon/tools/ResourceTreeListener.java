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

package neon.tools;

import java.awt.event.*;
import javax.swing.*;
import neon.objects.resources.*;
import neon.tools.editors.AfflictionEditor;
import neon.tools.editors.AlchemyEditor;
import neon.tools.editors.CraftingEditor;
import neon.tools.editors.DungeonThemeEditor;
import neon.tools.editors.EnchantmentEditor;
import neon.tools.editors.FactionEditor;
import neon.tools.editors.LevelSpellEditor;
import neon.tools.editors.PoisonEditor;
import neon.tools.editors.PowerEditor;
import neon.tools.editors.QuestEditor;
import neon.tools.editors.RegionThemeEditor;
import neon.tools.editors.SignEditor;
import neon.tools.editors.SpellEditor;
import neon.tools.editors.TattooEditor;
import neon.tools.editors.ZoneThemeEditor;
import neon.tools.resources.RFaction;
import javax.swing.tree.*;

public class ResourceTreeListener implements MouseListener {
	private JTree tree;
	private JFrame frame;
	
	public ResourceTreeListener(JTree resourceTree, JFrame parent) {
		tree = resourceTree;
	}
	
	protected JTree getTree() {
		return tree;
	}
	
	protected void launchEditor(ResourceNode node) {
		switch(node.getType()) {
		case CRAFT: 
			new CraftingEditor(frame, (RCraft)node.getResource()).show(); 
			break;
		case FACTION: 
			new FactionEditor(frame, (RFaction)node.getResource()).show(); 
			break;
		case DUNGEON: 
			new DungeonThemeEditor(frame, (RDungeonTheme)node.getResource()).show(); 
			break;
		case ZONE: 
			new ZoneThemeEditor(frame, (RZoneTheme)node.getResource()).show(); 
			break;
		case REGION: 
			new RegionThemeEditor(frame, (RRegionTheme)node.getResource()).show(); 
			break;
		case QUEST: 
			new QuestEditor(frame, (RQuest)node.getResource()).show(); 
			break;
		case CURSE: 
			new AfflictionEditor(frame, (RSpell)node.getResource()).show(); 
			break;
		case DISEASE: 
			new AfflictionEditor(frame, (RSpell)node.getResource()).show(); 
			break;
		case ENCHANTMENT: 
			new EnchantmentEditor(frame, (RSpell.Enchantment)node.getResource()).show(); 
			break;
		case LEVEL_SPELL: 
			new LevelSpellEditor(frame, (LSpell)node.getResource()).show(); 
			break;
		case POISON: 
			new PoisonEditor(frame, (RSpell)node.getResource()).show(); 
			break;
		case POWER: 
			new PowerEditor(frame, (RSpell.Power)node.getResource()).show(); 
			break;
		case SIGN: 
			new SignEditor(frame, (RSign)node.getResource()).show(); 
			break;
		case RECIPE: 
			new AlchemyEditor(frame, (RRecipe)node.getResource()).show(); 
			break;
		case TATTOO: 
			new TattooEditor(frame, (RTattoo)node.getResource()).show(); 
			break;
		default: 
			new SpellEditor(frame, (RSpell)node.getResource()).show(); 
			break;
		}
	}
	
	public void mouseExited(MouseEvent e) {}
	public void mouseEntered(MouseEvent e) {}
	public void mouseReleased(MouseEvent e) {}
	public void mousePressed(MouseEvent e) {		
		if(e.getButton() == MouseEvent.BUTTON1) {
			if(e.getClickCount() == 2) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
				if(tree.getSelectionPath() != null && node.getLevel() == 2) {
					launchEditor((ResourceNode)node);						
				} 
			}
		}
	}
	
	public void mouseClicked(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON3) {
			JPopupMenu menu = new JPopupMenu();
			tree.setSelectionRow(tree.getRowForLocation(e.getX(), e.getY()));
			ResourceNode node = (ResourceNode)tree.getLastSelectedPathComponent();
			if(tree.getSelectionPath() != null && node.getLevel() == 2) {
				menu.add(new ResourceAction("Delete resource", node.getType(), this));
				menu.add(new ResourceAction("Edit resource", node.getType(), this));
				menu.add(new ResourceAction("New " + node.getType().toString().toLowerCase(), node.getType(), this));	
				menu.show(e.getComponent(), e.getX(), e.getY());
			} else if(tree.getSelectionPath() != null && node.getLevel() == 1) {
				menu.add(new ResourceAction("New " + node.getType().toString().toLowerCase(), node.getType(), this));				
				menu.show(e.getComponent(), e.getX(), e.getY());
			}
		}
	}
}
