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

package neon.core;

import java.awt.Rectangle;
import java.io.File;
import neon.core.event.SaveEvent;
import neon.core.event.TaskQueue;
import neon.core.event.MagicTask;
import neon.core.event.ScriptAction;
import neon.entities.Player;
import neon.entities.property.Feat;
import neon.entities.property.Skill;
import neon.magic.Spell;
import neon.maps.Atlas;
import neon.resources.RSpell;
import neon.systems.files.XMLTranslator;
import neon.util.fsm.Action;
import net.engio.mbassy.listener.Handler;
import net.engio.mbassy.listener.Listener;
import net.engio.mbassy.listener.References;

import com.google.common.collect.Multimap;

import org.jdom2.Document;
import org.jdom2.Element;

@Listener(references = References.Strong)
public class GameSaver {
	private TaskQueue queue;
	
	public GameSaver(TaskQueue queue) {
		this.queue = queue;
	}
	
	/**
	 * Saves the current game.
	 */
	@Handler public void saveGame(SaveEvent se) {
		Document doc = new Document();
		Element root = new Element("save");
		doc.setRootElement(root);
		
		Player player = Engine.getPlayer();
		root.addContent(savePlayer(player));	// player data saven
		root.addContent(saveJournal(player));	// journal saven
		root.addContent(saveEvents());			// events saven
		root.addContent(saveQuests());			// quests saven
		Element timer = new Element("timer");
		timer.setAttribute("ticks", String.valueOf(Engine.getTimer().getTime()));
		root.addContent(timer);
		
		File saves = new File("saves");
		if(!saves.exists()) {
			saves.mkdir();
		}
		
		File dir = new File("saves/" + player.getName());
		if(!dir.exists()) {
			dir.mkdir();
		}
		
		// eerst alles vanuit temp naar save kopiëren, zodat savedoc zeker niet overschreven wordt
		Engine.getAtlas().getCache().commit();
		Engine.getStore().getCache().commit();
		Engine.getFileSystem().storeTemp(dir);
		Engine.getFileSystem().saveFile(doc, new XMLTranslator(), "saves", player.getName(), "save.xml");
	}
	
	private Element saveEvents() {
		Element events = new Element("events");
		
		// alle gewone tasks (voorlopig enkel script tasks)
		Multimap<String, Action> tasks = queue.getTasks();
		for(String key : tasks.keySet()) {
			for(Action action : tasks.get(key)) {
				Element event = new Element("task");
				event.setAttribute("desc", key);
				if(action instanceof ScriptAction) {
					ScriptAction task = (ScriptAction)action;
					event.setAttribute("script", task.getScript());				
				}
				events.addContent(event);
			}
		}

		// alle timer tasks
		Multimap<Integer, TaskQueue.RepeatEntry> repeats = queue.getTimerTasks();
		for(Integer key : repeats.keySet()) {
			for(TaskQueue.RepeatEntry entry : repeats.get(key)) {
				Element event = new Element("timer");
				event.setAttribute("tick", key + ":" + entry.getPeriod() + ":" + entry.getStop());
				if(entry.getScript() != null) {
					event.setAttribute("task", "script");
					event.setAttribute("script", entry.getScript());				
				} else if(entry.getTask() instanceof MagicTask) {
					event.setAttribute("task", "magic");
					Spell spell = ((MagicTask)entry.getTask()).getSpell();
					event.setAttribute("effect", spell.getEffect().name());
					if(spell.getTarget() != null) {
						event.setAttribute("target", Long.toString(spell.getTarget().getUID()));
					}
					if(spell.getCaster() != null) {
						event.setAttribute("caster", Long.toString(spell.getCaster().getUID()));
					}
					if(spell.getScript() != null) {
						event.setAttribute("script", spell.getScript());
					}
					event.setAttribute("stype", spell.getType().name());
					event.setAttribute("mag", Float.toString(spell.getMagnitude()));
				}
				events.addContent(event);
			}
		}

		return events;
	}
	
	private Element saveQuests() {
		Element quests = new Element("quests");
		// TODO: random quests saven
		return quests;
	}
	
	private Element savePlayer(Player player) {
		Element PC = new Element("player");

		PC.setAttribute("name", player.getName());
		PC.setAttribute("race", player.species.id);

		PC.setAttribute("gender", player.getGender().toString().toLowerCase());
		
		PC.setAttribute("spec", player.getSpecialisation().toString());
		
		Atlas atlas = Engine.getAtlas();
		PC.setAttribute("map", Integer.toString(atlas.getCurrentMap().getUID()));
		int l = atlas.getCurrentZoneIndex();
		PC.setAttribute("l", Integer.toString(l));
		Rectangle bounds = player.getShapeComponent();
		PC.setAttribute("x", String.valueOf(bounds.x));
		PC.setAttribute("y", String.valueOf(bounds.y));
		PC.setAttribute("sign", player.getSign());
		
		Element skills = new Element("skills");
		for(Skill s : Skill.values()) {
			skills.setAttribute(s.toString(), String.valueOf(player.getSkill(s)));
		}
		PC.addContent(skills);
		
		Element stats = new Element("stats");
		stats.setAttribute("str", String.valueOf(player.getStatsComponent().getStr()));
		stats.setAttribute("con", String.valueOf(player.getStatsComponent().getCon()));
		stats.setAttribute("dex", String.valueOf(player.getStatsComponent().getDex()));
		stats.setAttribute("int", String.valueOf(player.getStatsComponent().getInt()));
		stats.setAttribute("wis", String.valueOf(player.getStatsComponent().getWis()));
		stats.setAttribute("cha", String.valueOf(player.getStatsComponent().getCha()));
		PC.addContent(stats);
		
		Element money = new Element("money");
		money.setText(String.valueOf(player.getInventoryComponent().getMoney()));
		PC.addContent(money);
		
		for(long uid : player.getInventoryComponent()) {
			Element item = new Element("item");
			item.setAttribute("uid", Long.toString(uid));
			PC.addContent(item);
		}
		
		for(RSpell s : player.getMagicComponent().getSpells()) {
			Element spell = new Element("spell");
			spell.setText(s.id);
			PC.addContent(spell);
		}
		
		for(RSpell p : player.getMagicComponent().getPowers()) {
			Element spell = new Element("spell");
			spell.setText(p.id);
			PC.addContent(spell);
		}
		
		for(Feat f : player.getCharacteristicsComponent().getFeats()) {
			Element feat = new Element("feat");
			feat.setText(f.toString());
			PC.addContent(feat);
		}
		
		return PC;
	}
	
	private Element saveJournal(Player player) {
		Element journal = new Element("journal");
		
		for(String q : player.getJournal().getQuests().keySet()) {
			Element quest = new Element("quest");
			quest.setAttribute("id", q);
			quest.setAttribute("stage", String.valueOf(player.getJournal().getQuests().get(q)));
			quest.setText(player.getJournal().getSubjects().get(q));
			journal.addContent(quest);
		}
		return journal;
	}
}
