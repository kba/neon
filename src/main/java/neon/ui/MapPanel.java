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

package neon.ui;

import javax.swing.JComponent;
import java.awt.*;
import neon.core.Engine;
import neon.maps.Zone;
import neon.maps.Region;
import neon.ui.graphics.ZComparator;
import java.util.*;

/**
 * A custom JComponent used to view the worldmap. 
 * 
 * @author	mdriesen
 */
@SuppressWarnings("serial")
public class MapPanel extends JComponent {
	private Zone zone;
	private float zoom;
	private boolean fill;
	private ZComparator comparator;
	
	/**
	 * Initializes this <code>MapPanel</code>.
	 */
	public MapPanel(Zone zone) {
		setBackground(Color.black);
		this.zone = zone;
		fill = true;
		comparator = new ZComparator();
	}
	
	/**
	 * Paints the contents of the map on this panel.
	 * 
	 * @param g	the <code>Graphics</code> object on which to paint
	 */
	public void paintComponent(Graphics g) {
		float width = getWidth();
		float height = getHeight();
		zoom = Math.min(width/zone.getWidth(), height/zone.getHeight());
		
		super.paintComponent(g);
		drawTerrain(new ArrayList<Region>(zone.getRegions()), (Graphics2D)g);
		g.setColor(Color.white);
		try {
			Rectangle bounds = Engine.getPlayer().getShapeComponent();
			g.drawString("x", (int)(zoom*(bounds.x + 0.5)), 
					(int)(zoom*(bounds.y + 0.9)));	
		} catch(NullPointerException e) {}
	}
	
	/**
	 * Toggles whether the regions are drawn filled or outlined.
	 */
	public void toggleFill() {
		fill = !fill;
		repaint();
	}
	
	/**
	 * Sets the zoom level.
	 * 
	 * @param zoom	the new zoom level
	 */
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
	
	private void drawTerrain(ArrayList<Region> regions, Graphics2D graphics) {
		Collections.sort(regions, comparator);
		ArrayList<Region> labels = new ArrayList<Region>();
		for(Region r : regions) {
			if(zoom*r.getWidth() > 1 && zoom*r.getHeight() > 1) {
				graphics.setPaint(r.getColor());				
				graphics.drawRect((int)(zoom*r.getX()), (int)(zoom*r.getY()), 
						(int)(zoom*r.getWidth()), (int)(zoom*r.getHeight()));
				if(fill) {
					graphics.fillRect((int)(zoom*r.getX()), (int)(zoom*r.getY()), 
							(int)(zoom*r.getWidth()), (int)(zoom*r.getHeight()));
				}							
			}
			
			if(r.getLabel() != null) {
				labels.add(r);
			}
		}
		
		// labels op kaart
		graphics.setPaint(Color.WHITE);
		for(Region r : labels) {
			graphics.drawOval((int)(zoom*r.getBounds().getCenterX() - 4), 
					(int)(zoom*r.getBounds().getCenterY()) - 4, 8, 8);
			graphics.drawString(r.getLabel(), zoom*r.getX() + 6, zoom*r.getY());			
		}
	}	
}
