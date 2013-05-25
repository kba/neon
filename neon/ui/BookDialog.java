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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import javax.swing.border.*;

public class BookDialog implements KeyListener {
	private JDialog frame;
	private JEditorPane area;
	private JFrame parent;
	private JScrollPane scroller;
	
	public BookDialog(JFrame parent) {
		this.parent = parent;
		frame = new JDialog(parent, true);
		frame.setPreferredSize(new Dimension(parent.getWidth() - 100, parent.getHeight() - 100));
		frame.setUndecorated(true);
		
		JPanel contents = new JPanel(new BorderLayout());
		contents.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED), new EmptyBorder(10,10,10,10)));		
		frame.setContentPane(contents);
		
		area = new JEditorPane();
		area.setContentType("text/html");
		area.setEditable(false);
		area.setFocusable(false);
		area.setBackground(Color.darkGray);
		scroller = new JScrollPane(area);
		contents.add(scroller, BorderLayout.CENTER);
		
		JLabel instructions = new JLabel("<html>Use arrows keys to scroll, press space to return to inventory.</html>");
		instructions.setBorder(new CompoundBorder(new TitledBorder("Instructions"), new EmptyBorder(0,5,10,5)));
		contents.add(instructions, BorderLayout.PAGE_END);
        frame.addKeyListener(this);
        try {
        	frame.setOpacity(0.9f);
        } catch(UnsupportedOperationException e) {
        	System.out.println("setOpacity() not supported.");
        }
	}
	
	public void show(String title, String text) {
		frame.setTitle(title);
		area.setText(text);
		area.setCaretPosition(0);
		scroller.setBorder(new TitledBorder(title));
		frame.pack();
		frame.setLocationRelativeTo(parent);
		frame.setVisible(true);
	}

	public void keyReleased(KeyEvent e) {}
	public void keyTyped(KeyEvent e) {}
	public void keyPressed(KeyEvent e) {
		int inc = area.getScrollableUnitIncrement(scroller.getVisibleRect(), SwingConstants.VERTICAL, 1);
		BoundedRangeModel model = scroller.getVerticalScrollBar().getModel();
		
		switch (e.getKeyCode()) {
		case KeyEvent.VK_SPACE: frame.dispose(); break;
		case KeyEvent.VK_UP: model.setValue(model.getValue() - inc); break;
		case KeyEvent.VK_DOWN: model.setValue(model.getValue() + inc); break;		
		}
	}
}
