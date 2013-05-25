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

import java.awt.Dimension;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.BoundedRangeModel;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.UIDefaults;
import javax.swing.UIManager;

public class HelpWindow implements KeyListener {
	private static UIDefaults defaults = UIManager.getLookAndFeelDefaults();
	private JDialog frame;
	private JTextPane area;
	private JScrollPane scroller;
	
	public HelpWindow(JFrame parent) {
		frame = new JDialog(parent, "Help");
		
		area = new JTextPane();
		area.setContentType("text/html");
		area.setEditable(false);
		area.addKeyListener(this);
		scroller = new JScrollPane(area);
		
		frame.setPreferredSize(new Dimension(600, 600));
		frame.setContentPane(scroller);
	}
	
	/**
	 * Shows this HelpWindow with the given html text.
	 * 
	 * @param title	window title
	 * @param text	html text
	 */
	public void show(String title, String text) {
		frame.setTitle(title);
		area.setText(text);
		area.getCaret().setDot(0);
		area.setBackground(defaults.getColor("control"));
		
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);		
	}

	public void keyReleased(KeyEvent ke) {}
	public void keyTyped(KeyEvent ke) {}
	public void keyPressed(KeyEvent ke) {
		int inc = area.getScrollableUnitIncrement(scroller.getVisibleRect(), SwingConstants.VERTICAL, 1);
		BoundedRangeModel model = scroller.getVerticalScrollBar().getModel();
		switch(ke.getKeyCode()) {
		case KeyEvent.VK_ESCAPE: frame.dispose();
		case KeyEvent.VK_UP: model.setValue(model.getValue() - inc); break;
		case KeyEvent.VK_DOWN: model.setValue(model.getValue() + inc); break;		
		}
	}
}
