package se.kth.livetech.properties;


/** Implements type conversion and setting sub properties */
abstract public class AbstractProperty implements IProperty {
	public String getValue() {
		if (isSet())
			return getOwnValue();
		return this.getDefaultProperty().getOwnValue();
	}
	
	public double getDoubleValue() {
		try {
			return Double.parseDouble(getValue());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public void setDoubleValue(double value) {
		this.setValue(Double.toString(value));
	}

	public int getIntValue() {
		try {
			return Integer.parseInt(this.getValue());
		} catch (NumberFormatException e) {
			return (int) getDoubleValue();
		}
	}

	public void setIntValue(int value) {
		this.setValue(Integer.toString(value));
	}
	
	public boolean getBooleanValue() {
		return Boolean.parseBoolean(getValue());
	}
	
	public void setBooleanValue(boolean value) {
		this.setValue(Boolean.toString(value));
	}

	public void toggleBooleanValue() {
		this.setBooleanValue(!this.getBooleanValue());
	}

	public void set(String subName, String value) {
		this.get(subName).setValue(value);
	}
	
	public IProperty getAbsolute(String canonicalName) {
		String start = this.getName() + '.';
		if (!canonicalName.startsWith(start))
			return null;
		return this.get(canonicalName.substring(start.length()));
	}

	public String toString() { return this.getName();} //return "[" + this.getName() + " = " + this.getValue() + "]"; }
}
