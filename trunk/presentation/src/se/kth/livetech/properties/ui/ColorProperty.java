package se.kth.livetech.properties.ui;

import java.awt.Color;

import se.kth.livetech.properties.IProperty;

public class ColorProperty {
	public static Color getColor(IProperty property) {
		return new Color(
				property.get("r").getIntValue(),
				property.get("g").getIntValue(),
				property.get("b").getIntValue());
	}
}
