/*
 *	Neon, a roguelike engine.
 *	Copyright (C) 2010 - Maarten Driesen
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
import javax.swing.border.BevelBorder;
import java.awt.BorderLayout;

@SuppressWarnings("serial")
public class StatusBar extends JComponent {
    private JLabel label;
	
    public StatusBar() {
        setLayout(new BorderLayout());
		setBorder(new BevelBorder(BevelBorder.LOWERED));
        label = new JLabel(" Ready");
        add(label);
    }
    
    public void setMessage(String message) {
        label.setText(" " + message);        
    }        
}
