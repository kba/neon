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

package neon.ui.dialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.border.*;
import neon.maps.Zone;
import neon.ui.MapPanel;

public class MapDialog implements KeyListener {
	private JDialog frame;
	private JFrame parent;
	private MapPanel map;
	
	public MapDialog(JFrame parent, Zone zone) {
		this.parent = parent;
		frame = new JDialog(parent, false);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 50));
		frame.setUndecorated(true);
		
		JPanel panel = new JPanel(new BorderLayout());
		panel.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
        
		// mappanel
		map = new MapPanel(zone);
        panel.add(map, BorderLayout.CENTER);
        
        // instructies geven
		JLabel instructions = new JLabel("An 'X' marks your location. Press esc or M to return.");
    	instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
        panel.add(instructions, BorderLayout.PAGE_END);

 		frame.setContentPane(panel);
        frame.addKeyListener(this);
        try {
        	frame.setOpacity(0.95f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show() {
		frame.pack();
		frame.setLocationRelativeTo(parent.getContentPane());
		frame.setVisible(true);	
	}
	
	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
		case KeyEvent.VK_M: 
		case KeyEvent.VK_ESCAPE: 
			frame.dispose();
			break;
		case KeyEvent.VK_F: 
			map.toggleFill();
			break;
		}					
	}
}
