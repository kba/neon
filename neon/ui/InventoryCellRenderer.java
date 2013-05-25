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

package neon.ui;

import javax.swing.*;
import java.awt.*;
import neon.core.Engine;
import neon.objects.entities.Entity;
import java.util.HashMap;

/**
 * Decides how to render the contents of a cell in the player's inventory. 
 * 
 * @author	mdriesen
 */
@SuppressWarnings("serial")
public class InventoryCellRenderer extends JLabel implements ListCellRenderer<Entity> {
	private static UIDefaults defaults = UIManager.getLookAndFeelDefaults();
	private Font font;
    private HashMap<String, Integer> data;
	
	/**
	 * Initializes this renderer.
	 */
	public InventoryCellRenderer(HashMap<String, Integer> data) {
        font = getFont();
        this.data = data;
    }
    
	/**
	 * Returns this renderer with the right properties (color, font, background color).
	 * If the player has equiped the item in the cell, the text is rendered
	 * in bold.
	 * 
	 * @param list			the list that contains the cell
	 * @param value			the object that is contained in the cell
	 * @param index			the index of the cell in the list
	 * @param isSelected	whether the cell is selected
	 * @param cellHasFocus	whether the cell has keyboard focus
	 * @return				this <code>InventoryCellRenderer</code>
	 */
    public Component getListCellRendererComponent(JList<? extends Entity> list, Entity value, int index, boolean isSelected, boolean cellHasFocus) {
    	String text = value.getID();
    	if(data.get(text) > 1) { 
    		text = text + " (" + data.get(text) + ")";
    	}
    	setText(text);
        if(Engine.getPlayer().inventory.hasEquiped(value.getUID())) {
			setFont(new Font(font.getName(), Font.BOLD, font.getSize()));
        } else {
        	setFont(font);
        }
        
		if(isSelected) {
			setBackground(defaults.getColor("List.selectionBackground"));
			setForeground(defaults.getColor("List.selectionForeground"));
		} else {
			setForeground(defaults.getColor("List.foreground"));
		}
		setOpaque(isSelected);
		return this;
    }	
}
