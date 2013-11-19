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

import java.awt.*;
import java.awt.geom.*;
import java.awt.image.BufferedImage;
import java.awt.image.BufferedImageOp;
import java.awt.image.ColorModel;
import java.util.Collection;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.text.DefaultCaret;

import neon.core.Engine;
import neon.core.handlers.CombatUtils;
import neon.entities.Player;
import neon.entities.components.HealthComponent;
import neon.entities.components.RenderComponent;
import neon.entities.components.ShapeComponent;
import neon.entities.property.Condition;
import neon.ui.graphics.*;
import neon.util.ColorFactory;
import neon.maps.*;

/**
 * This class represents the main game screen. It contains a <code>JVectorPane</code> 
 * overlayed with player information, and a text area to show in-game messages.
 * 
 * @author mdriesen
 */
@SuppressWarnings("serial")
public class GamePanel extends JComponent {
	// onderdelen
	private JTextArea text;
	private JScrollPane scroller;
	private JPanel stats;
	private DefaultRenderable cursor;
	private TitledBorder sBorder, aBorder, cBorder;
	private JVectorPane drawing;
	
	// onderdelen van het statspanel
	private JLabel intLabel, conLabel, dexLabel, strLabel, wisLabel, chaLabel;
	private JLabel healthLabel, magicLabel, AVLabel, DVLabel;
	
	/**
	 * Initializes this GamePanel.
	 */
	public GamePanel() {
		drawing = new JVectorPane();
		drawing.setFilter(new LightFilter());
		
        // stats field (mottige manier om dit semi-transparant te krijgen)
		stats = new JPanel(new GridLayout(0, 1)) {
			@Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }    
        };
        stats.setLayout(new BoxLayout(stats, BoxLayout.PAGE_AXIS));
        stats.setOpaque(false);
        JPanel sPanel = new JPanel(new GridLayout(0, 1));
        sBorder = new TitledBorder("Stats");
        sBorder.setBorder(new LineBorder(Color.LIGHT_GRAY));
        sPanel.setBorder(sBorder);
        sPanel.setOpaque(false);
        sPanel.add(healthLabel = new JLabel());
        sPanel.add(magicLabel = new JLabel());
        stats.add(sPanel);
        JPanel cPanel = new JPanel(new GridLayout(0, 1));
        cBorder = new TitledBorder("Combat");
        cBorder.setBorder(new LineBorder(Color.LIGHT_GRAY));
        cPanel.setBorder(cBorder);
        cPanel.setOpaque(false);
        cPanel.add(AVLabel = new JLabel());
        cPanel.add(DVLabel = new JLabel());
        stats.add(cPanel);
        JPanel aPanel = new JPanel(new GridLayout(0, 1));
        aBorder = new TitledBorder("Attributes");
        aBorder.setBorder(new LineBorder(Color.LIGHT_GRAY));
        aPanel.setBorder(aBorder);
        aPanel.setOpaque(false);
        aPanel.add(intLabel = new JLabel());
        aPanel.add(conLabel = new JLabel());
        aPanel.add(dexLabel = new JLabel());
        aPanel.add(strLabel = new JLabel());
        aPanel.add(wisLabel = new JLabel());
        aPanel.add(chaLabel = new JLabel());
        stats.add(aPanel);
        
        // text field (weer mottig)
		text = new JTextArea();
		text.setOpaque(false);
		text.setFocusable(false);
		text.setLineWrap(true);
		text.setWrapStyleWord(true);
		
		DefaultCaret caret = (DefaultCaret)text.getCaret();
		caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
		
        scroller = new JScrollPane(text) {
        	@Override
            public void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(0, 0, 0, 150));
                g.fillRect(0, 0, getWidth(), getHeight());
            }    
        };
        scroller.setOpaque(false);
        scroller.getViewport().setOpaque(false);
    	scroller.setBorder(new TitledBorder(new LineBorder(Color.LIGHT_GRAY), "Messages"));
    	scroller.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
    	
    	layout(true);
	}
	
    /**
     * Zooms in on the main game screen.
     */
	public void zoomIn() {
		drawing.setZoom(drawing.getZoom() + 1);
		repaint();
    }
    
	/**
	 * Zooms the main game screen out.
	 */
    public void zoomOut() {
		drawing.setZoom(drawing.getZoom() - 1);
		repaint();
    }	

    /**
     * Prints a message in the message box of the main game screen.
     * 
     * @param message	the message to print
     */
	public void print(String message) {
		text.append(message + "\n");
    }
	
	@Override
	public void repaint() {
		if(Engine.getPlayer() != null) {
			drawStats();
			ShapeComponent bounds = Engine.getPlayer().getComponent(ShapeComponent.class);
			drawing.updateCamera(bounds.getLocation());
		} 
		Collection<Renderable> renderables = Engine.getAtlas().getCurrentZone().getRenderables(getVisibleRectangle());
		renderables.add(Engine.getPlayer().getComponent(RenderComponent.class));
		if(cursor != null) {
			renderables.add(cursor);
		}
		drawing.setRenderables(renderables);
		super.repaint();		
	}
	
	private void layout(boolean over) {
		removeAll();
		if(over) {
			add(drawing);
	        add(stats);
	    	add(scroller);

	    	// layouten
			SpringLayout layout = new SpringLayout();
	    	layout.putConstraint(SpringLayout.NORTH, drawing, 0, SpringLayout.NORTH, this);
	    	layout.putConstraint(SpringLayout.EAST, drawing, 0, SpringLayout.EAST, this);
	    	layout.putConstraint(SpringLayout.SOUTH, drawing, 0, SpringLayout.SOUTH, this);
	    	layout.putConstraint(SpringLayout.WEST, drawing, 0, SpringLayout.WEST, this);
	    	layout.putConstraint(SpringLayout.NORTH, stats, 0, SpringLayout.NORTH, this);
	    	layout.putConstraint(SpringLayout.SOUTH, stats, 0, SpringLayout.SOUTH, this);
	    	layout.putConstraint(SpringLayout.WEST, stats, 0, SpringLayout.WEST, this);
	    	layout.putConstraint(SpringLayout.NORTH, scroller, 0, SpringLayout.NORTH, this);
	    	layout.putConstraint(SpringLayout.EAST, scroller, 0, SpringLayout.EAST, this);
	    	layout.putConstraint(SpringLayout.SOUTH, scroller, 0, SpringLayout.SOUTH, this);
			setLayout(layout);
			// om drawing vanonder te steken
			setComponentZOrder(drawing, 2);			
		} else {
			setLayout(new BorderLayout());
	        add(stats, BorderLayout.LINE_START);
			add(drawing, BorderLayout.CENTER);
	    	add(scroller, BorderLayout.LINE_END);			
		}
	}

	private void drawStats() {
		// components 
		Player player = Engine.getPlayer();
		HealthComponent health = player.getComponent(HealthComponent.class);

		if(health.getHealth()*4 < health.getBaseHealth()) {
			healthLabel.setText("<html>health: <font color=red>" + health.getHealth() + "/" + 
					health.getBaseHealth() + "</font></html>");
		} else if(health.getHealth() > health.getBaseHealth()) {
			healthLabel.setText("<html>health: <font color=green>" + health.getHealth() + "/" + 
					health.getBaseHealth() + "</font></html>");
		} else {
			healthLabel.setText("health: " + health.getHealth() + "/" + health.getBaseHealth());
		}
		if(player.animus.getMana() > player.species.mana*player.species.iq) {
			magicLabel.setText("<html>magic: <font color=green>" + player.animus.getMana() + "/" +
					(int)(player.species.mana*player.species.iq) + "</font></html>");
		} else {
			magicLabel.setText("magic: " + player.animus.getMana() + "/" + (int)(player.species.mana*player.species.iq));			
		}
		AVLabel.setText("AV: " + player.getAVString());
		DVLabel.setText("DV: " + CombatUtils.getDV(player));

		if(player.getStr() > (int)player.species.str) {
			strLabel.setText("<html>strength: <font color=green>" + player.getStr() + "</font></html>");			
		} else if(player.getStr() < (int)player.species.str) {
			strLabel.setText("<html>strength: <font color=red>" + player.getStr() + "</font></html>");			
		} else {
			strLabel.setText("strength: " + player.getStr());			
		}
		if(player.getDex() > (int)player.species.dex) {
			dexLabel.setText("<html>dexterity: <font color=green>" + player.getDex() + "</font></html>");			
		} else if(player.getDex() < (int)player.species.dex) {
			dexLabel.setText("<html>dexterity: <font color=red>" + player.getDex() + "</font></html>");			
		} else {
			dexLabel.setText("dexterity: " + player.getDex());
		}
		if(player.getCon() > (int)player.species.con) {
			conLabel.setText("<html>constitution: <font color=green>" + player.getCon() + "</font></html>");			
		} else if(player.getCon() < (int)player.species.con) {
			conLabel.setText("<html>constitution: <font color=red>" + player.getCon() + "</font></html>");			
		} else {
			conLabel.setText("constitution: " + player.getCon());
		}
		if(player.getInt() > (int)player.species.iq) {
			intLabel.setText("<html>intelligence: <font color=green>" + player.getInt() + "</font></html>");			
		} else if(player.getInt() < (int)player.species.iq) {
			intLabel.setText("<html>intelligence: <font color=red>" + player.getInt() + "</font></html>");			
		} else {
			intLabel.setText("intelligence: " + player.getInt());
		}
		if(player.getWis() > (int)player.species.wis) {
			wisLabel.setText("<html>wisdom: <font color=green>" + player.getWis() + "</font></html>");			
		} else if(player.getWis() < (int)player.species.wis) {
			wisLabel.setText("<html>wisdom: <font color=red>" + player.getWis() + "</font></html>");			
		} else {
			wisLabel.setText("wisdom: " + player.getWis());
		}
		if(player.getCha() > (int)player.species.cha) {
			chaLabel.setText("<html>charisma: <font color=green>" + player.getCha() + "</font></html>");			
		} else if(player.getCha() < (int)player.species.cha) {
			chaLabel.setText("<html>charisma: <font color=red>" + player.getCha() + "</font></html>");			
		} else {
			chaLabel.setText("charisma: " + player.getCha());
		}
		
		if(player.getConditions().contains(Condition.DISEASED) || player.getConditions().contains(Condition.CURSED) || 
				player.getConditions().contains(Condition.POISONED)) {
			sBorder.setBorder(new LineBorder(ColorFactory.getColor("darkGreen")));
		} else {
			sBorder.setBorder(new LineBorder(Color.LIGHT_GRAY));			
		}
	}
	
    /**
     * Shows a cursor on the main game screen.
     * 
	 * @return	the cursor
     */
	public DefaultRenderable showCursor() {
    	cursor = new DefaultRenderable(0, 0, Byte.MAX_VALUE, 1, 1, "x", Color.white);
		return cursor;
	}
	
    /**
     * Removes the cursor from the screen.
     */
	public void hideCursor() {
		cursor = null;
	}
	
	/**
	 * Turns the HUD on and off.
	 */
	public void toggleHUD() {
		if(!stats.isVisible()) {
			stats.setVisible(true);
			scroller.setVisible(true);
			layout(false);
			doLayout();
		} else if(getLayout() instanceof BorderLayout){
			layout(true);
			doLayout();
		} else {
			stats.setVisible(!stats.isVisible());
			scroller.setVisible(!scroller.isVisible());			
		}
		repaint();
	}
	
	public Rectangle getVisibleRectangle() {
		return drawing.getVisibleRectangle();
	}
	
	private class LightFilter implements BufferedImageOp {
		private BufferedImage image;	// omdat dest altijd null is
		
		public BufferedImage createCompatibleDestImage(BufferedImage src, ColorModel destCM) {
			return new BufferedImage(src.getWidth(), src.getHeight(), src.getType());
		}

		public BufferedImage filter(BufferedImage src, BufferedImage dest) {
			Rectangle view = drawing.getVisibleRectangle();	
			Player player = Engine.getPlayer();
			float zoom = drawing.getZoom();
			Area area = new Area(new Rectangle(src.getWidth(), src.getHeight()));
			
			// TODO: player staat niet gecentreerd op outdoor map
			// 8 en 17: cirkel met diameter 16, gecentreerd op player
			ShapeComponent bounds = player.getComponent(ShapeComponent.class);
			area.subtract(new Area(new Ellipse2D.Float((bounds.x - 8)*zoom - view.x*zoom, 
					(bounds.y - 8)*zoom - view.y*zoom, (int)(17*zoom), (int)(17*zoom))));
			for(Point p : Engine.getAtlas().getCurrentZone().getLightMap().keySet()) {
				// 4 en 9: cirkel met diameter 8, gecentreerd op licht
				area.subtract(new Area(new Ellipse2D.Float((p.x - 4)*zoom - view.x*zoom, (p.y - 4)*zoom - view.y*zoom, 
						9*zoom, 9*zoom)));
			}

			if(image == null || image.getWidth() != src.getWidth() || image.getHeight() != src.getHeight()) {
				image = getGraphicsConfiguration().createCompatibleImage(src.getWidth(), src.getHeight());
			} 
			
			Graphics2D g = (Graphics2D)image.getGraphics();
			g.clearRect(0, 0, src.getWidth(), src.getHeight());
			g.drawImage(src, src.getMinX(), src.getMinY(), null);
			
			if(Engine.getAtlas().getCurrentMap() instanceof World) {
				int hour = (Engine.getTimer().getTime()/(60*1) + 12)%24;
				g.setColor(new Color(0, 0, 0, (hour-12)*(hour-12)*3/2));
				g.fill(area);
			} else {
				// TODO: verschillend gekleurde lichtjes
				g.setColor(new Color(0, 0, 0, 200));
				g.fill(area);
			}
			return image;
		}
		
		public Rectangle2D getBounds2D(BufferedImage src) {
			return src.getGraphics().getClip().getBounds();
		}

		public Point2D getPoint2D(Point2D srcPt, Point2D destPt) {
			destPt = srcPt;
			return destPt;
		}

		public RenderingHints getRenderingHints() {
			return null;
		}		
	}
}
