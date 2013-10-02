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

package neon.core.handlers;

import java.awt.Rectangle;
import java.util.Collection;
import neon.core.Configuration;
import neon.core.Engine;
import neon.core.event.TurnEvent;
import neon.entities.Creature;
import neon.entities.Player;
import neon.entities.property.Condition;
import neon.entities.property.Habitat;
import neon.maps.*;
import neon.maps.Region.Modifier;
import neon.resources.RRegionTheme;
import neon.ui.GamePanel;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

@Listener(references = References.Strong)	// strong, om gc te vermijden
public class TurnHandler {
	private GamePanel panel;
	private Generator generator;
	
	public TurnHandler(GamePanel panel) {
		this.panel = panel;
	}
	
	@Handler public void tick(TurnEvent te) {
		// indien beurt gedaan is (timer krijgt extra tick): 
		//	1) random regions controleren
		//	2) monsters controleren
		//	3) speler controleren
		
		// kijken of terrain moet gegenereerd worden
		if(Configuration.gThread) {
			if(generator == null || !generator.isAlive()) {
				generator = new Generator();
				generator.start();
			} 
		} else {
			checkRegions();
		}

		// monsters controleren
		Player player = Engine.getPlayer();
		int range = (panel.getVisibleRectangle().width + panel.getVisibleRectangle().width)/4;
		for(long uid : Engine.getAtlas().getCurrentZone().getCreatures()) {
			Creature creature = (Creature)Engine.getStore().getEntity(uid);
			if(!creature.hasCondition(Condition.DEAD)) {
				creature.heal(creature.getCon()/100f);
				creature.animus.addMana(creature.getWis()/100f);
				if(player.bounds.getLocation().distance(creature.bounds.getLocation()) < range) {
					int spd = creature.getSpeed();
					Region region = Engine.getAtlas().getCurrentZone().getRegion(creature.bounds.getLocation());
					if(creature.species.habitat == Habitat.LAND && region.getMovMod() == Modifier.SWIM) {
						spd = spd/4;	// zwemmende creatures hebben penalty
					}
					if(player.isSneaking()) {
						spd = spd*2;	// player krijgt penalty bij sneaken
					}
					
					while(spd > player.getSpeed()*Math.random()) {
						creature.brain.act();
						spd -= player.getSpeed();
					}
				}
			}
		}

		// player in gereedheid brengen voor volgende beurt
		player.heal(player.getCon()/100f);
		player.animus.addMana(player.getWis()/100f);
		
		// en systems updaten
//		long tijd = System.currentTimeMillis();
		Engine.getPhysicsEngine().update();
//		System.out.println("physics time = " + (System.currentTimeMillis() - tijd));
		Engine.getUI().update();
	}
	
	private class Generator extends Thread {
		public void run() {
			if(checkRegions()) {
				panel.repaint();	// anders wordt er teveel gerepaint
			}
		}
	}
	
	private boolean checkRegions() {	// die boolean is eigenlijk maar louche
		Rectangle window = panel.getVisibleRectangle();
		Zone zone = Engine.getAtlas().getCurrentZone();
		boolean fixed = true;
		boolean generated = false;	// om aan te geven dat er iets gegenereerd werd
		
		do {
			Collection<Region> buffer = zone.getRegions(window);
			fixed = true;
			for(Region r : buffer) {
				if(!r.isFixed()) {
					generated = true;
					fixed = false;
					RRegionTheme theme = r.getTheme();
					r.fix();	// vanaf hier wordt theme null
					if(theme.id.startsWith("town")) {
						new TownGenerator(zone).generate(r.getX(), r.getY(), r.getWidth(), r.getHeight(), theme, r.getZ());
					} else {
						new WildernessGenerator(zone).generate(r, theme);
					}
				}
			}
		} while(fixed == false);
		
		return generated;
	}
}
