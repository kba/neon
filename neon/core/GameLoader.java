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

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import neon.core.event.MagicTask;
import neon.core.event.ScriptAction;
import neon.core.handlers.InventoryHandler;
import neon.core.handlers.SkillHandler;
import neon.entities.Entity;
import neon.entities.EntityFactory;
import neon.entities.Item;
import neon.entities.Player;
import neon.entities.UIDStore;
import neon.entities.property.Ability;
import neon.entities.property.Feat;
import neon.entities.property.Gender;
import neon.entities.property.Skill;
import neon.magic.Effect;
import neon.magic.Spell;
import neon.magic.SpellFactory;
import neon.maps.Map;
import neon.resources.CGame;
import neon.resources.RCreature;
import neon.resources.RMod;
import neon.resources.RSign;
import neon.resources.RSpell.SpellType;
import neon.systems.files.FileUtils;
import neon.systems.files.XMLTranslator;
import org.apache.jdbm.DBMaker;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;

public class GameLoader {
	private Configuration config;
	
	public GameLoader(Configuration config) {
		this.config = config;
	}
	
	/**
	 * Creates a new game using the supplied data.
	 * 
	 * @param race
	 * @param name
	 * @param gender
	 * @param spec
	 * @param profession
	 * @param sign
	 */
	public void initGame(String race, String name, Gender gender, 
			Player.Specialisation spec, String profession, RSign sign) {
		// engine initialiseren
		initCache();
		
		// speler initialiseren
		RCreature species = new RCreature(((RCreature)Engine.getResources().getResource(race)).toElement());
		Player player = new Player(species, name, gender, spec, profession);
		player.species.text = "@";
		Engine.setPlayer(player);		
		setSign(player, sign);
		for(Skill skill : Skill.values()) {
			SkillHandler.checkFeat(skill, player);
		}
		
		CGame game = (CGame)Engine.getResources().getResource("game", "config");
		
		// starting items
		for(String i : game.getStartingItems()) {
			Item item = EntityFactory.getItem(i, Engine.getStore().createNewEntityUID());
			Engine.getStore().addEntity(item);
			InventoryHandler.addItem(player, item.getUID());
		}
		// starting spells
		for(String i : game.getStartingSpells()) {
			player.animus.addSpell(SpellFactory.getSpell(i));
		}
		
		// player in positie brengen
		player.getBounds().setLocation(game.getStartPosition().x, game.getStartPosition().y);
		Map map = Engine.getAtlas().getMap(Engine.getStore().getMapUID(game.getStartMap()));
		Engine.getScriptEngine().put("map", map);
		Engine.getAtlas().setMap(map);
		Engine.getAtlas().setCurrentZone(game.getStartZone());
	}
	
	private void setSign(Player player, RSign sign) {
		player.setSign(sign.id);
		for(String power : sign.powers) {
			player.animus.addSpell(SpellFactory.getSpell(power));
		}
		for(Ability ability : sign.abilities.keySet()) {
			player.addAbility(ability, sign.abilities.get(ability));
		}		
	}
	
	/**
	 * Loads a saved game.
	 * 
	 * @param save	the name of the saved game
	 */
	public void loadGame(String save) {
		config.setProperty("save", save);

		Document doc = new Document();
		try {
			FileInputStream in = new FileInputStream("saves/" + save + "/save.xml");
			doc = new SAXBuilder().build(in);
			in.close();
		} catch (IOException e) {
			System.out.println("IOException in loadGame");
		} catch(JDOMException e) {
			System.out.println("JDOMException in loadGame");
		}
		Element root = doc.getRootElement();
		
		// save map naar temp kopiëren
		Path savePath = Paths.get("saves", save);
		Path tempPath = Paths.get("temp");
		FileUtils.copy(savePath, tempPath);
		
		// engine initialiseren
		initCache();
		
		// tijd juist zetten (met setTime(), anders worden listeners aangeroepen)
		Engine.getTimer().setTime(Integer.parseInt(root.getChild("timer").getAttributeValue("ticks")));
		
		// player aanmaken
		loadPlayer(root.getChild("player"));

		// events
		loadEvents(root.getChild("events"));
		
		// quests
		Element journal = root.getChild("journal");
		for(Element e: journal.getChildren()) {
			Engine.getPlayer().getJournal().addQuest(e.getAttributeValue("id"), e.getText());
			Engine.getPlayer().getJournal().updateQuest(e.getAttributeValue("id"), 
					Integer.parseInt(e.getAttributeValue("stage")));
		}		
	}
	
	private void loadEvents(Element events) {
		// gewone tasks
		for(Element event : events.getChildren("task")) {
			String description = event.getAttributeValue("desc");
			if(event.getAttribute("script") != null) {
				String script = event.getAttributeValue("script");
				Engine.getQueue().add(description, new ScriptAction(script));
			}
		}
		
		// getimede tasks
		for(Element event : events.getChildren("timer")) {
			String[] ticks = event.getAttributeValue("tick").split(":");
			int start = Integer.parseInt(ticks[0]);
			int period = Integer.parseInt(ticks[1]);
			int stop = Integer.parseInt(ticks[2]);
					
			switch(event.getAttributeValue("task")) {
			case "script": 
				Engine.getQueue().add(event.getAttributeValue("script"), start, period, stop);
				break;
			case "magic": 
				Effect effect = Effect.valueOf(event.getAttributeValue("effect").toUpperCase());
				float magnitude = Float.parseFloat(event.getAttributeValue("magnitude"));
				String script = event.getAttributeValue("script");
				SpellType type = SpellType.valueOf(event.getAttributeValue("type").toUpperCase());
				Entity caster = null;
				if(event.getAttribute("caster") != null) {
					caster = Engine.getStore().getEntity(Long.parseLong(event.getAttributeValue("caster")));
				}
				Entity target = null;
				if(event.getAttribute("target") != null) {
					target = Engine.getStore().getEntity(Long.parseLong(event.getAttributeValue("target")));
				}
				Spell spell = new Spell(target, caster, effect, magnitude, script, type);
				Engine.getQueue().add(new MagicTask(spell, stop), start, stop, period);
				break;
			}
		}
	}

	private void loadPlayer(Element playerData) {
		// player aanmaken
		RCreature species = (RCreature)Engine.getResources().getResource(playerData.getAttributeValue("race"));
		Engine.setPlayer(new Player(new RCreature(species.toElement()), playerData.getAttributeValue("name"), 
				Gender.valueOf(playerData.getAttributeValue("gender").toUpperCase()), Player.Specialisation.valueOf(playerData.getAttributeValue("spec")),
				playerData.getAttributeValue("prof")));
		Player player = Engine.getPlayer();
		player.getBounds().setLocation(Integer.parseInt(playerData.getAttributeValue("x")), Integer.parseInt(playerData.getAttributeValue("y")));
		player.setSign(playerData.getAttributeValue("sign"));
		player.species.text = "@";
		
		// start map
		int mapUID = Integer.parseInt(playerData.getAttributeValue("map"));
		Engine.getAtlas().setMap(Engine.getAtlas().getMap(mapUID));
		int level = Integer.parseInt(playerData.getAttributeValue("l"));
		Engine.getAtlas().setCurrentZone(level);
		
		// stats
		player.addStr(Integer.parseInt(playerData.getChild("stats").getAttributeValue("str")) - player.getStr());
		player.addCon(Integer.parseInt(playerData.getChild("stats").getAttributeValue("con")) - player.getCon());
		player.addDex(Integer.parseInt(playerData.getChild("stats").getAttributeValue("dex")) - player.getDex());
		player.addInt(Integer.parseInt(playerData.getChild("stats").getAttributeValue("int")) - player.getInt());
		player.addWis(Integer.parseInt(playerData.getChild("stats").getAttributeValue("wis")) - player.getWis());
		player.addCha(Integer.parseInt(playerData.getChild("stats").getAttributeValue("cha")) - player.getCha());
		
		// skills
		for(Attribute skill : (List<Attribute>)playerData.getChild("skills").getAttributes()) {
			player.setSkill(Skill.valueOf(skill.getName()), Integer.parseInt(skill.getValue()));
		}
		
		// items
		for(Element e : playerData.getChildren("item")) {
			long uid = Long.parseLong(e.getAttributeValue("uid"));
			player.inventory.addItem(uid);
		}
		
		// spells
		for(Element e : playerData.getChildren("spell")) {
			player.animus.addSpell(SpellFactory.getSpell(e.getText()));
		}
		
		// feats
		for(Element e: playerData.getChildren("feat")) {
			player.addFeat(Feat.valueOf(e.getText()));
		}

		// geld
		player.addMoney(Integer.parseInt(playerData.getChildText("money")));		
	}

	private void initCache() {
		Engine.getAtlas().setCache(DBMaker.openFile("temp/atlas").disableLocking().make());
		Engine.getStore().setCache(DBMaker.openFile("temp/store").disableLocking().make());
		
		// mods en maps in uidstore steken
		for(RMod mod : Engine.getResources().getResources(RMod.class)) {
			if(Engine.getStore().getModUID(mod.id) == 0) {
				Engine.getStore().addMod(mod.id);
			}
			for(String[] path : mod.getMaps())
			try {	// maps zitten in twowaymap, en worden dus niet in cache opgeslagen
				Element map = Engine.getFileSystem().getFile(new XMLTranslator(), path).getRootElement();
				short mapUID = Short.parseShort(map.getChild("header").getAttributeValue("uid"));
				int uid = UIDStore.getMapUID(Engine.getStore().getModUID(path[0]), mapUID);
				Engine.getStore().addMap(uid, path);
			} catch(Exception e) {
				Engine.getLogger().fine("Map error in mod " + path[0]);
			}			
		}
	}
}
