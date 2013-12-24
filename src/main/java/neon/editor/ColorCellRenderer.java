/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2007 - Maarten Driesen
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

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("serial")
public class ColorCellRenderer extends JLabel implements ListCellRenderer<String> {
	public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
		setOpaque(true);		
		setBackground(Color.black);
		setForeground(neon.util.ColorFactory.getColor(value));
		setText(" " + value);
		return this;
	}
}
