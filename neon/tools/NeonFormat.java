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

package neon.tools;

import java.text.NumberFormat;
import java.util.Locale;

public class NeonFormat {
	private static NumberFormat intFormat = NumberFormat.getIntegerInstance();
	private static NumberFormat floatFormat = NumberFormat.getInstance(Locale.ENGLISH);

	public static NumberFormat getIntegerInstance() {
		intFormat.setGroupingUsed(false);
		return intFormat;
	}

	public static NumberFormat getFloatInstance() {
		floatFormat.setGroupingUsed(false);
		return floatFormat;
	}
}
