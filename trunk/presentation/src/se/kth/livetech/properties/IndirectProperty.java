package se.kth.livetech.properties;

/** Use the value of one property to set the link of another. */
public class IndirectProperty implements PropertyListener {
	IProperty property;
	IProperty target;
	public IndirectProperty(IProperty property, IProperty target) {
		this.property = property;
		this.target = target;
		this.property.addPropertyListener(this);
	}
	public void propertyChanged(IProperty changed) {
		this.target.setLink(this.property.getValue());
	}
}
