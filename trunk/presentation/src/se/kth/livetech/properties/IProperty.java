package se.kth.livetech.properties;

import java.util.List;

/**
 * Property interface for properties with listeners, hierarchy and links.
 * 
 * @author zalenski
 *
 */
public interface IProperty {
	/** Property's own name */
	public String getName();

	/* Linking */
	public boolean isLinked();
	public String getLink();
	public void clearLink();
	public void setLink(String link);

	/* Returns the value or linked value. */
	public boolean isSet();
	public String getValue();
	public String getOwnValue();
	public void clearValue();
	public void setValue(String value);
	
	/** Looks up the property that the value is obtained from. */
	public IProperty getDefaultProperty();
	
	/** Looks up the property descriptor. */
	public PropertyDescriptor getDescriptor();

	/* Type conversion */
	public int getIntValue();
	public void setIntValue(int value);

	public double getDoubleValue();
	public void setDoubleValue(double value);
	
	public boolean getBooleanValue();
	public void setBooleanValue(boolean value);
	public void toggleBooleanValue();

	/** Add a property listener to this property, only a weak link is maintained! */
	public void addPropertyListener(PropertyListener listener);
	/** Remove a property listener to this property, rare use. */
	public void removePropertyListener(PropertyListener listener);

	/** Get a sub property */
	public IProperty get(String subName);
	/** Convenience method to set a sub property directly */
	public void set(String subName, String value);
	/** Get all sub properties */
	public List<IProperty> getSubProperties();

	/** Get high */
	public IProperty getAbsolute(String canonicalName);
 }
