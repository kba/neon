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

package neon.editor.maps;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

@SuppressWarnings("serial")
public class TabLabel extends JPanel {
	private JButton button;
	
    public TabLabel(String title) {
        // randen rond label wat kleiner maken
        super(new FlowLayout(FlowLayout.LEFT, 0, 0));
        setOpaque(false);
        
        add(new JLabel(title));
        // tab button
        button = new TabButton();
        add(button);
        // add more space to the top of the component
        setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
    }
    
    public void addActionListener(ActionListener listener) {
    	button.addActionListener(listener);
    }
    
    private static class TabButton extends JButton {
        public TabButton() {
            int size = 17;
            setPreferredSize(new Dimension(size, size));
            // make it transparent
            setContentAreaFilled(false);
        }

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D)g;
            // shift the image for pressed buttons
            if(getModel().isPressed()) {
                g2.translate(1, 1);
            }
            g2.setColor(Color.BLACK);
            if(getModel().isRollover()) {
                g2.setColor(Color.MAGENTA);
            }
            int delta = 6;
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(delta, delta, getWidth() - delta - 1, getHeight() - delta - 1);
            g2.drawLine(getWidth() - delta - 1, delta, delta, getHeight() - delta - 1);

            g2.dispose();
        }
    }
}


