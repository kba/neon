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

package neon.tools.help;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JLabel;
import javax.swing.ToolTipManager;
import javax.swing.border.EtchedBorder;
import neon.tools.Editor;

public class HelpLabels {
	private static ToolTipListener listener = new ToolTipListener();
	
	public static JLabel getAggressionHelpLabel() {
		return getLabel("A higher aggression makes a creature more likely to fight when it meets an enemy.");
	}
	
	public static JLabel getAidHelpLabel() {
		return getLabel("Whether or not this item can be used for first aid.");
	}
	
	public static JLabel getAITypeHelpLabel() {
		return getLabel("The base behaviour of this creature. Default is guard.");
	}
	
	public static JLabel getAlchemyCostHelpLabel() {
		return getLabel("Price charged to brew this potion. Default is 10.");
	}
	
	public static JLabel getAmountHelpLabel() {
		return getLabel("The amount of raw material needed to craft this item.");
	}
	
	public static JLabel getArmorRatingHelpLabel() {
		return getLabel("Higher rated armor absorbs more damage before the wearer loses health.");
	}
	
	public static JLabel getArmorSlotHelpLabel() {
		return getLabel("<html>The body part this piece of armor protects:<br>" +
				"<b>Boots:</b> protects the feet<br>" +
				"<b>Bracers:</b> protects the forearms<br>" +
				"<b>Chausses:</b> protects the legs<br>" +
				"<b>Cuirass:</b> protects the torso<br>" +
				"<b>Gauntlets:</b> protects the hands and forearms<br>" +
				"<b>Helmet:</b> protects the head<br>" +
				"<b>Pauldrons:</b> protects the shoulders<br>" +
				"<b>Shield:</b> worn on the arm, protects the entire body<br>" +
				"Bracers and gauntlets can't be worn at the same time." +
				"</html>");
	}
	
	public static JLabel getAttackHelpLabel() {
		return getLabel("<html>The base unarmed damage this creature can do. The <br>" +
				"format is: &lt;number of dice&gt;d&lt;type of die&gt;+&lt;bonus&gt;.</html>");
	}
	
	public static JLabel getBigHelpLabel() {
		return getLabel("<html>The name of large denomination coins (such as <i>euro</i> or <i>dollar</i> in real life).</html>");
	}
	
	public static JLabel getBookTextHelpLabel() {
		return getLabel("<html>The name of the .html file containing the text of this book, <br>" +
				"without the .html (if the filename is 'content.html', this <br>" +
				"field would read 'content'). The texts should be manually <br>" +
				"saved in the 'books' folder of your mod.</html>");
	}
	
	public static JLabel getBranchHelpLabel() {
		return getLabel("A high branching factor gives dungeons with more connections between the zones.");
	}
	
	public static JLabel getChaHelpLabel() {
		return getLabel("Charisma determines a creature's reputation.");
	}
	
	public static JLabel getCharHelpLabel() {
		return getLabel("The character this object will be represented with in the game. Default is 'X'.");
	}
	
	public static JLabel getClassHelpLabel() {
		return getLabel("<html>The weight rating of an armor piece determines which skill is used for <br>" +
				"a combat defense check. The actual weight is not taken into account.");
	}
	
	public static JLabel getClosedHelpLabel() {
		return getLabel("<html>The character this door will be represented with when closed. If no character <br>" +
				"is given, the open door character will be used.</html>");
	}
	
	public static JLabel getClothingEnchantmentHelpLabel() {
		return getLabel("The optional enchantment on this clothing piece. It must be a constant effect.");
	}
	
	public static JLabel getClothingSlotHelpLabel() {
		return getLabel("<html>The type of clothing. Gloves can't be worn together with bracers or <br>" +
				"gauntlets (armor). Shoes can't be worn together with boots (armor)</html>");
	}
	
	public static JLabel getColorHelpLabel() {
		return getLabel("The color of the character this object will be represented with in the game. " +
				"Default is white.");
	}
	
	public static JLabel getConfidenceHelpLabel() {
		return getLabel("A higher confidence makes a creature less likely to flee when wounded.");
	}
	
	public static JLabel getConHelpLabel() {
		return getLabel("Constitution determines healing rate, starting health and speed increases on level up.");
	}
	
	public static JLabel getCostHelpLabel() {
		return getLabel("The cost of this item in " + Editor.getStore().getActive().get("small") + ".");
	}
	
	public static JLabel getCraftingCostHelpLabel() {
		return getLabel("The cost to craft this item (in " + Editor.getStore().getActive().get("small") + ").");
	}
	
	public static JLabel getCreatureTypeHelpLabel() {
		return getLabel("The type determines whether a creature has dialog or can be used as a mount.");
	}
	
	public static JLabel getDamageHelpLabel() {
		return getLabel("<html>Determines the base damage this weapon can inflict. The format <br>" +
				"is: &lt;number of dice&gt;d&lt;type of die&gt;+&lt;bonus&gt;</html>");
	}
	
	public static JLabel getDefenseHelpLabel() {
		return getLabel("Natural armor. Higher numbers mean more damage is absorbed before losing health.");
	}
	
	public static JLabel getDexHelpLabel() {
		return getLabel("Dexterity determines starting health and speed increases on level up.");
	}
	
	public static JLabel getDoorHelpLabel() {
		return getLabel("<html>A list of doors that can appear in this zone. They <br>" +
				"should be separated by a comma (no spaces).</html>");
	}
	
	public static JLabel getDurationHelpLabel() {
		return getLabel("How many turns this spell stays active. 0 means an immediate effect.");
	}
	
	public static JLabel getEffectHelpLabel() {
		return getLabel("What this spell actually does.");
	}
	
	public static JLabel getFactionHelpLabel() {
		return getLabel("<html>The checkbox determines whether this NPC belongs to the selected <br>" +
				" faction. The spinner determines its rank within this faction.</html>");
	}
	
	public static JLabel getFloorHelpLabel() {
		return getLabel("<html>A list of floor types that can appear in this zone. They <br>" +
				"should be separated by a comma (no spaces).</html>");
	}
	
	public static JLabel getFoodEffectHelpLabel() {
		return getLabel("The (spell) effect eating this food has.");
	}
	
	public static JLabel getHabitatHelpLabel() {
		return getLabel("<html>The kind of habitat this creature is generally found in. This is mainly used in<br>" +
				"random genereration: land creatures will never be generated in water, water <br>" +
				"creatures will never be generated on land. Water creatures can never move on <br>" +
				"land, land creatures can enter water with a high enough swimming skill.</html>.");
	}
	
	public static JLabel getHitHelpLabel() {
		return getLabel("<html>Hit dice determine the starting health and health increases at level up. <br>" +
				"The format is: &lt;number of dice&gt;d&lt;type of die&gt;+&lt;bonus&gt;</html>.");
	}
	
	public static JLabel getInitialHelpLabel() {
		return getLabel("Initial quests are active from the start of a new game.");
	}
	
	public static JLabel getIntervalHelpLabel() {
		return getLabel("How many turns it takes before this power can used again.");
	}
	
	public static JLabel getIntHelpLabel() {
		return getLabel("Intelligence determines a creature's mana and skill increase rate.");
	}
	
	public static JLabel getItemHelpLabel() {
		return getLabel("<html>The type of item this enchantment is used on. Clothing enchantments can be <br>" +
				"used on armor and vice versa. Same goes for door/container enchantments <br>" +
				"and food/potion enchantments. Books and scrolls can be enchanted with any <br>" +
				"normal spell, and are not included here.</html>");
	}
	
	public static JLabel getKeyHelpLabel() {
		return getLabel("The key that can be used to open this lock.");
	}
	
	public static JLabel getLockableHelpLabel() {
		return getLabel("Whether this container has a lock or not.");
	}
	
	public static JLabel getLockDCHelpLabel() {
		return getLabel("How difficult it is to pick this lock.");
	}
	
	public static JLabel getLockedHelpLabel() {
		return getLabel("<html>The character this door will be represented with when locked. If no character <br>" +
				"is given, the closed door character will be used. If this is not given either, the <br>" +
				"open door character will be used.</html>");
	}
	
	public static JLabel getLockStateHelpLabel() {
		return getLabel("The state of this door: open, closed or locked.");
	}
	
	public static JLabel getManaHelpLabel() {
		return getLabel("This number multiplied by Int determines the mana available to this creature.");
	}
	
	public static JLabel getMaxSizeHelpLabel() {
		return getLabel("The maximum width and height of the generated zone.");
	}
	
	public static JLabel getMinSizeHelpLabel() {
		return getLabel("The minimum width and height of the generated zone.");
	}
	
	public static JLabel getMaxZoneHelpLabel() {
		return getLabel("The maximum number of zones generated in this dungeon.");
	}
	
	public static JLabel getMinZoneHelpLabel() {
		return getLabel("The minimum number of zones generated in this dungeon.");
	}
	
	public static JLabel getNameHelpLabel() {
		return getLabel("The name of this resource. If no name is given, " +
				"the object id will be used as name.");
	}
	
	public static JLabel getRaceHelpLabel() {
		return getLabel("The species of this NPC. Services given to animals are ignored in-game");
	}
	
	public static JLabel getRangeHelpLabel() {
		return getLabel("Maximum distance this creature will move from its starting position (guard AI only).");
	}
	
	public static JLabel getRawHelpLabel() {
		return getLabel("The raw material needed to craft this item.");
	}
	
	public static JLabel getScrollTextHelpLabel() {
		return getLabel("The text written on this scroll.");
	}
	
	public static JLabel getSizeHelpLabel() {
		return getLabel("The size of this creature. Tiny creatures are shown at 2/3 scale in-game, " +
				"huge creatures at 3/2 scale.");
	}
	
	public static JLabel getSmallHelpLabel() {
		return getLabel("<html>The name of small denomination coins, 1/100th <br>" +
				"the value of big coins (such as <i>cent</i> in real life).</html>");
	}
	
	public static JLabel getSpeedHelpLabel() {
		return getLabel("Speed determines how long a creature has to wait for its next turn.");
	}
	
	public static JLabel getSpellHelpLabel() {
		return getLabel("The effect of this object when used. Any normal spell is valid.");
	}
	
	public static JLabel getSpellRadiusHelpLabel() {
		return getLabel("The radius of the area affected by this spell.");
	}
	
	public static JLabel getSpellRangeHelpLabel() {
		return getLabel("<html>The range of this spell. The following values are valid:<br>" +
				"<b>0:</b> spell is cast on the caster himself<br>" +
				"<b>1:</b> spell can only be cast on someone/something right next to the caster<br>" +
				"<b>&gt;1:</b> spell can be cast anywhere up to this distance</html>");
	}
	
	public static JLabel getSpellSizeHelpLabel() {
		return getLabel("<html>The magnitude of the spell effect (a heal spell with <br>" +
				"magnitude 5 means it will heal 5 points per turn).</html>");
	}
	
	public static JLabel getStartMapHelpLabel() {
		return getLabel("The map where a new character will start. Only non-random maps are allowed");
	}
	
	public static JLabel getStartXLabel() {
		return getLabel("The x-position where a new character will start. Make sure this position exists.");
	}
	
	public static JLabel getStartYLabel() {
		return getLabel("The y-position where a new character will start. Make sure this position exists.");
	}
	
	public static JLabel getStartZoneLabel() {
		return getLabel("<html>The zone where a new character will start. Only non-random zones <br>" +
				"are allowed. If none are available, choose another map.</html>");
	}
	
	public static JLabel getStrHelpLabel() {
		return getLabel("Strength determines carrying capacity and starting health.");
	}
	
	public static JLabel getSVGHelpLabel() {
		return getLabel("<html>Whether or not to render this object with custom SVG in <br>" +
				"the game. Setting this will override the color and character <br>" +
				"attributes.</html>");
	}
	
	public static JLabel getTattooAbilityHelpLabel() {
		return getLabel("This tattoo will increase the chosen ability by the chosen amount.");
	}
	
	public static JLabel getTerrainHelpLabel() {
		return getLabel("The modifier determines how a creature moves across this terrain type.");
	}
	
	public static JLabel getTitleHelpLabel() {
		return getLabel("The title of this mod. It will be displayed on the start screen and in the title bar.");
	}
	
	public static JLabel getTopHelpLabel() {
		return getLabel("<html>Whether or not this object is shown on top of all other objects <br>" +
				"in the game. By default, creatures are shown on top of items, <br>" +
				"which are shown on top of terrain.</html>");
	}
	
	public static JLabel getWallHelpLabel() {
		return getLabel("The type of walls in this dungeon. Only a single type is allowed");
	}
	
	public static JLabel getWeaponEnchantmentHelpLabel() {
		return getLabel("The optional enchantment on this weapon. No constant effects are allowed.");
	}
	
	public static JLabel getWeaponTypeHelpLabel() {
		return getLabel("Determines which skill is used and how it is used (melee or ranged).");
	}
	
	public static JLabel getWeightHelpLabel() {
		return getLabel("The weight of this item in kg.");
	}
	
	public static JLabel getWisHelpLabel() {
		return getLabel("Wisdom determines mana restoration rate and spell resistance.");
	}
	
	public static JLabel getZoneHelpLabel() {
		return getLabel("<html>The different zones that can be generated in this dungeon. The <br>" +
				"zone names should be separated by a semicolon without spaces.</html>");	
	}
	
	private static JLabel getLabel(String text) {
		JLabel label = new JLabel(" ? ");
		label.setToolTipText(text);
		label.setBorder(new EtchedBorder());
		label.addMouseListener(listener);
		return label;
	}
	
	private static class ToolTipListener extends MouseAdapter {
		private int defaultInitialDelay;

		public void mouseEntered(MouseEvent me) {
			defaultInitialDelay = ToolTipManager.sharedInstance().getInitialDelay();
			ToolTipManager.sharedInstance().setInitialDelay(0);
		}

		public void mouseExited(MouseEvent me) {
			ToolTipManager.sharedInstance().setInitialDelay(defaultInitialDelay);
		}		
	}
}
