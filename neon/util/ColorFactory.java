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

package neon.util;

import java.awt.Color;
import java.util.*;

/**
 * ColorFactory implements all "X11 colors" from the CSS3 specification as defined 
 * in the W3C standard. Also included are some extra Netscape colors.
 * 
 * @author	mdriesen
 */
public class ColorFactory {
	private static LinkedHashMap<String, Color> colors;
	
	static {
		colors = new LinkedHashMap<String, Color>();
		// al deze kleuren zijn de zogenaamde 'webcolors' (zie bv. wikipedia)
		// red colors
		colors.put("indianRed", new Color(205, 92, 92));
		colors.put("lightCoral", new Color(240, 128, 128));
		colors.put("salmon", new Color(250, 128, 114));
		colors.put("darkSalmon", new Color(233, 150, 122));
		colors.put("lightSalmon", new Color(255, 160, 120));
		colors.put("crimson", new Color(220, 20, 60));
		colors.put("red", new Color(255, 0, 0));
		colors.put("fireBrick", new Color(178, 34, 34));
		colors.put("darkRed", new Color(139, 0, 0));
		// pink colors
		colors.put("pink", new Color(255, 192, 203));
		colors.put("lightPink", new Color(255, 182, 193));
		colors.put("hotPink", new Color(255, 105, 180));
		colors.put("deepPink", new Color(255, 20, 147));
		colors.put("mediumVioletRed", new Color(199, 21, 133));
		colors.put("paleVioletRed", new Color(219, 112, 147));
		// orange colors
		colors.put("coral", new Color(255, 127, 80));
		colors.put("tomato", new Color(255, 99, 71));
		colors.put("orangeRed", new Color(255, 69, 0));
		colors.put("darkOrange", new Color(255, 140, 0));
		colors.put("orange", new Color(255, 165, 0));
		// yellow colors
		colors.put("gold", new Color(255, 215, 0));
		colors.put("yellow", new Color(255, 255, 0));
		colors.put("lightYellow", new Color(255, 255, 224));
		colors.put("lemmonChiffon", new Color(255, 250, 205));
		colors.put("lightGoldenrodYellow", new Color(250, 250, 210));
		colors.put("papayaWhip", new Color(255, 239, 213));
		colors.put("moccassin", new Color(255, 228, 181));
		colors.put("peachPuff", new Color(255, 218, 185));
		colors.put("paleGoldenrod", new Color(238, 232, 170));
		colors.put("khaki", new Color(240, 230, 140));
		colors.put("darkKhaki", new Color(189, 183, 107));
		// purple colors
		colors.put("lavender", new Color(230, 230, 250));
		colors.put("thistle", new Color(216, 191, 216));
		colors.put("plum", new Color(221, 160, 221));
		colors.put("violet", new Color(238, 130, 238));
		colors.put("orchid", new Color(218, 112, 214));
		colors.put("fuchsia", new Color(255, 0, 255));
		colors.put("magenta", new Color(255, 0, 255));
		colors.put("mediumOrchid", new Color(186, 85, 211));
		colors.put("mediumPurple", new Color(147, 112, 219));
		colors.put("blueViolet", new Color(138, 43, 226));
		colors.put("darkViolet", new Color(148, 0, 211));
		colors.put("darkOrchid", new Color(153, 50, 204));
		colors.put("darkMagenta", new Color(139, 0, 139));
		colors.put("purple", new Color(128, 0, 128));
		colors.put("indigo", new Color(75, 0, 130));
		colors.put("slateBlue", new Color(106, 90, 205));
		colors.put("darkSlateBlue", new Color(72, 61, 139));
		// green colors
		colors.put("greenYellow", new Color(173, 255, 47));
		colors.put("chartreuse", new Color(127, 255, 0));
		colors.put("lawnGreen", new Color(124, 252, 0));
		colors.put("lime", new Color(0, 255, 0));
		colors.put("limeGreen", new Color(50, 205, 50));
		colors.put("paleGreen", new Color(152, 251, 152));
		colors.put("lightGreen", new Color(144, 238, 144));
		colors.put("mediumSpringGreen", new Color(0, 250, 154));
		colors.put("springGreen", new Color(0, 255, 127));
		colors.put("mediumSeaGreen", new Color(60, 179, 113));
		colors.put("seaGreen", new Color(46, 139, 87));
		colors.put("forestGreen", new Color(34, 139, 34));
		colors.put("green", new Color(0, 128, 0));
		colors.put("darkGreen", new Color(0, 100, 0));
		colors.put("yellowGreen", new Color(154, 205, 50));
		colors.put("oliveDrab", new Color(107, 142, 35));
		colors.put("olive", new Color(128, 128, 0));
		colors.put("darkOliveGreen", new Color(85, 107, 47));
		colors.put("mediumAquamarine", new Color(102, 205, 170));
		colors.put("darkSeaGreen", new Color(143, 188, 143));
		colors.put("lightSeaGreen", new Color(32, 178, 170));
		colors.put("darkGreenCopper", new Color(74, 118, 110));
		colors.put("darkCyan", new Color(0, 139, 139));
		colors.put("teal", new Color(0, 128, 128));
		// blue colors
		colors.put("aqua", new Color(0, 255, 255));
		colors.put("cyan", new Color(0, 255, 255));
		colors.put("lightCyan", new Color(224, 255, 255));
		colors.put("paleTurquoise", new Color(175, 238, 238));
		colors.put("aquamarine", new Color(127, 255, 212));
		colors.put("turquoise", new Color(64, 224, 208));
		colors.put("mediumTurquoise", new Color(72, 209, 204));
		colors.put("darkTurquoise", new Color(0, 206, 209));
		colors.put("cadetBlue", new Color(95, 158, 160));
		colors.put("richBlue", new Color(89, 89, 171));
		colors.put("steelBlue", new Color(70, 130, 180));
		colors.put("lightSteelBlue", new Color(176, 196, 222));
		colors.put("powderBlue", new Color(176, 224, 230));
		colors.put("lightBlue", new Color(173, 216, 230));
		colors.put("skyBlue", new Color(135, 206, 235));
		colors.put("lightSkyBlue", new Color(135, 206, 250));
		colors.put("deepSkyBlue", new Color(0, 191, 255));
		colors.put("dodgerBlue", new Color(30, 144, 255));
		colors.put("summerSky", new Color(56, 176, 222));
		colors.put("cornflowerBlue", new Color(100, 149, 237));
		colors.put("royalBlue", new Color(65, 105, 225));
		colors.put("mediumSlateBlue", new Color(123, 104, 238));
		colors.put("neonBlue", new Color(77, 77, 255));
		colors.put("blue", new Color(0, 0, 255));
		colors.put("mediumBlue", new Color(0, 0, 205));
		colors.put("newMidnightBlue", new Color(0, 0, 156));
		colors.put("navyBlue", new Color(35, 35, 142));
		colors.put("darkBlue", new Color(0, 0, 139));
		colors.put("navy", new Color(0, 0, 128));
		colors.put("midnightBlue", new Color(25, 25, 112));
		// brown colors
		colors.put("cornsilk", new Color(255, 248, 220));
		colors.put("blanchedAlmond", new Color(255, 235, 205));
		colors.put("bisque", new Color(255, 228, 196));
		colors.put("navajoWhite", new Color(255, 222, 173));
		colors.put("wheat", new Color(245, 222, 179));
		colors.put("newTan", new Color(235, 199, 158));
		colors.put("burlyWood", new Color(222, 184, 135));
		colors.put("mediumWood", new Color(166, 128, 100));
		colors.put("tan", new Color(210, 180, 140));
		colors.put("rosyBrown", new Color(188, 143, 143));
		colors.put("sandyBrown", new Color(244, 164, 96));
		colors.put("goldenrod", new Color(218, 165, 32));
		colors.put("darkGoldenrod", new Color(184, 134, 11));
		colors.put("peru", new Color(205, 133, 63));
		colors.put("chocolate", new Color(210, 105, 30));
		colors.put("darkTan", new Color(151, 105, 79));
		colors.put("saddleBrown", new Color(139, 69, 19));
		colors.put("lightWood", new Color(133, 99, 99));
		colors.put("darkWood", new Color(133, 94, 66));
		colors.put("sienna", new Color(160, 82, 45));
		colors.put("brown", new Color(165, 42, 42));
		colors.put("maroon", new Color(128, 0, 0));
		colors.put("darkBrown", new Color(92, 64, 51));
		// white colors
		colors.put("white", new Color(255, 255, 255));
		colors.put("snow", new Color(255, 250, 250));
		colors.put("honeyDew", new Color(240, 255, 240));
		colors.put("mintCream", new Color(245, 255, 250));
		colors.put("azure", new Color(240, 255, 255));
		colors.put("aliceBlue", new Color(240, 248, 255));
		colors.put("ghostWhite", new Color(248, 248, 255));
		colors.put("whiteSmoke", new Color(245, 245, 245));
		colors.put("seaShell", new Color(255, 245, 238));
		colors.put("beige", new Color(245, 245, 220));
		colors.put("oldLace", new Color(253, 245, 230));
		colors.put("floralWhite", new Color(255, 250, 240));
		colors.put("ivory", new Color(255, 255, 240));
		colors.put("antiqueWhite", new Color(250, 235, 215));
		colors.put("linen", new Color(250, 240, 230));
		colors.put("lavenderBlush", new Color(255, 240, 245));
		colors.put("mistyRose", new Color(255, 228, 225));
		// grey colors
		colors.put("gainsboro", new Color(220, 220, 220));
		colors.put("lightGrey", new Color(211, 211, 211));
		colors.put("lightGray", new Color(211, 211, 211));
		colors.put("silver", new Color(192, 192, 192));
		colors.put("darkGrey", new Color(169, 169, 169));
		colors.put("darkGray", new Color(169, 169, 169));
		colors.put("grey", new Color(128, 128, 128));
		colors.put("gray", new Color(128, 128, 128));
		colors.put("dimGrey", new Color(105, 105, 105));
		colors.put("dimGray", new Color(105, 105, 105));
		colors.put("lightSlateGrey", new Color(119, 136, 153));
		colors.put("lightSlateGray", new Color(119, 136, 153));
		colors.put("slateGrey", new Color(112, 128, 144));
		colors.put("slateGray", new Color(112, 128, 144));
		colors.put("darkSlateGrey", new Color(47, 79, 79));
		colors.put("darkSlateGray", new Color(47, 79, 79));
		colors.put("black", new Color(0, 0, 0));
	}
	
	/**
	 * Returns the <code>Color</code> with the given name. Grey/gray variants
	 * are both valid. The name of the color is given in Java style, e.g.
	 * darkSlateGray, beginning with a lowercase letter.
	 * 
	 * @param name	the name of one of the web colors
	 * @return		the <code>Color</code> object corresponding with the given name
	 */
	public static Color getColor(String name) {
		return colors.get(name);
	}
	
	/**
	 * @return	a list of all possible colors
	 */
	public static Vector<String> getColorNames() {
		return new Vector<String>(colors.keySet());
	}
}
