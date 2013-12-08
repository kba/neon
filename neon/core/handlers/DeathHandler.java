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

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptException;
import neon.core.Engine;
import neon.core.event.DeathEvent;
import neon.entities.Creature;
import neon.entities.components.ScriptComponent;
import neon.resources.RScript;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

/**
 * 
 * 
 * @author mdriesen
 */
@Listener(references = References.Strong)	// strong, om gc te vermijden
public class DeathHandler {
	public DeathHandler() {}
	
	@Handler public void handle(DeathEvent de) {
		Creature creature = de.getCreature();
		
		// creature laten doodgaan
		creature.die(de.getTime());
		
		// scripts draaien op creature
		ScriptComponent sc = creature.getScriptComponent();
		
		for(String s : sc.getScripts()) {
			RScript rs = (RScript) Engine.getResources().getResource(s, "script");
			ScriptEngine se = Engine.getScriptEngine();
			try {
				se.eval(rs.script);
				((Invocable) se).invokeFunction("onDeath", "0");
			} catch (ScriptException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}
	}
}
