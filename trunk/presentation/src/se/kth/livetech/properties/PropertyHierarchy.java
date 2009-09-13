package se.kth.livetech.properties;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.WeakHashMap;

/**
 */
public class PropertyHierarchy {
	public Map<String, Property> properties;
	
	public PropertyHierarchy() {
		this.properties = Collections.synchronizedMap(new TreeMap<String, Property>());
	}
	
	public IProperty getProperty(String name) { return get(name); }
	public void setProperty(String name, String value) { get(name).setValue(value); }
	private Property get(String name) {
		if (properties.containsKey(name))
			return properties.get(name);
		int index = name.lastIndexOf('.');
		if (index > 0)
			get(name.substring(0, index));
		Property p = new Property(this, name);
		properties.put(name, p);
		return p;
	}
	private Property getDefaultProperty(String base, String ending) {
		// 1. Property is set or has a descriptor
		Property property = properties.get(ending != null ? base + ending : base);
		if (property != null && property.isSet())
			return property;
		if (property != null && property.descriptor != null)
			return property;
		// 2. Base property is linked and linked property is set
		if (ending != null) property = properties.get(base);
		if (property != null) {
			String link = property.getLink();
			if (link != null) {
				Property defaultProperty = getDefaultProperty(link, ending);
				if (defaultProperty != null)
					return defaultProperty;
			}
		}
		// 3. Next higher base
		int index = base.lastIndexOf('.');
		if (index >= 0) {
			if (ending != null)
				return getDefaultProperty(base.substring(0, index), base.substring(index) + ending);
			return getDefaultProperty(base.substring(0, index), base.substring(index));
		}
		return null;
	}
	private void propertyChanged(Property property) {
		this.notifyLinks(property, property.name, null);
	}
	private void notifyLinks(Property changed, String base, String ending) {
		//System.out.println("Notifying that property " + changed.getName() + " is changed to " + changed.getValue() + " to " + base);
		Property property = properties.get(base);
		if (property != null) {
			// 1. Base property
			property.propertyChanged(changed);
			// 2. Links
			for (Property rlink : property.reverseLinks())
				notifyLinks(changed, ending != null ? rlink.name + ending : rlink.name, null);
		}
		// 3. Next higher base
		int index = base.lastIndexOf('.');
		if (index >= 0)
			if (ending != null)
				notifyLinks(changed, base.substring(0, index), base.substring(index) + ending);
			else
				notifyLinks(changed, base.substring(0, index), base.substring(index));
	}		
	
	public List<IProperty> getSubProperties() {
		ArrayList<IProperty> subs = new ArrayList<IProperty>();
		for (IProperty property : this.properties.values())
			subs.add(property);
		return subs;
	}
	
	

	private class Property extends AbstractProperty implements IProperty, PropertyListener {
		protected PropertyHierarchy hierarchy;
		protected String name;
		protected String link;
		protected String value;
		protected PropertyDescriptor descriptor;
		WeakHashMap<PropertyListener, Void> listeners;
		Set<Property> reverseLinks;
		
		public Property(PropertyHierarchy hierarchy, String name) {
			this.hierarchy = hierarchy;
			this.name = name;
			this.listeners = new WeakHashMap<PropertyListener, Void>();
		}
		
		private synchronized void addReverseLink(Property rlink) {
			if (reverseLinks == null)
				reverseLinks = new HashSet<Property>();
			reverseLinks.add(rlink);
		}
		private synchronized void removeReverseLink(Property rlink) {
			if (reverseLinks != null)
				reverseLinks.remove(rlink);
			if (reverseLinks.isEmpty())
				reverseLinks = null;
		}
		private synchronized Set<Property> reverseLinks() {
			if (reverseLinks != null)
				return reverseLinks;
			return Collections.<Property>emptySet();
		}
		
		public String getName() {
			return name;
		}

		public String getValue() {
			if (value != null)
				return value;
			else if (descriptor != null)
				return descriptor.getDefaultValue();
			else {
				Property defaultProperty = this.getDefaultProperty();
				if (defaultProperty != null)
					return defaultProperty.value;
				return "";
			}
		}
		
		public String getOwnValue() {
			return value;
		}

		public void setValue(String value) {
//			System.out.println("Property.setValue, value = " + value);
			if (value != null ? !value.equals(this.value) : this.value != null) {
				this.value = value;
				this.hierarchy.propertyChanged(this);
			}
		}
		
		public void clearValue() {
			setValue(null);
		}


		public boolean isLinked() {
			return link != null;
		}
		public String getLink() {
			return link;
		}
		public void clearLink() {
			this.setLink(null);
		}
		public void setLink(String link) {
			if (link != null ? !link.equals(this.link) : this.link != null) {
				if (this.link != null) {
					this.hierarchy.get(this.link).removeReverseLink(this);
				}
				this.link = link;
				if (this.link != null) {
					this.hierarchy.get(this.link).addReverseLink(this);
				}
				// TODO: type changes may affect more linked properties than those notified by a value change
				hierarchy.propertyChanged(this);
			}
		}

		public void addPropertyListener(PropertyListener listener) {
			listeners.put(listener, null);
			try {
				listener.propertyChanged(this);
			}
			catch (Exception e) {
				// TODO: report exception, Trace.trace(Trace.WARNING, "addPropertyListener", e);
			}
		}

		public void removePropertyListener(PropertyListener listener) {
			listeners.remove(listener);
		}

		public boolean isSet() {
			return this.value != null;
		}

		public void propertyChanged(IProperty property) {
//			DebugTrace.trace(this.name);
			List<PropertyListener> listeners = new ArrayList<PropertyListener>(this.listeners.keySet());
			for (PropertyListener listener : listeners) {
				try {
					listener.propertyChanged(property);
				}
				catch (Exception e) {
					// TODO: report exception, Trace.trace(Trace.WARNING, "propertyChanged", e);
				}
			}
		}

		public Property getDefaultProperty() {
			return this.hierarchy.getDefaultProperty(this.name, null);
		}

		public IProperty get(String subName) {
			return this.hierarchy.getProperty(this.name + '.' + subName);
		}

		public List<IProperty> getSubProperties() {
			ArrayList<IProperty> subs = new ArrayList<IProperty>();
			synchronized (this.hierarchy.properties) {
				for (IProperty property : this.hierarchy.properties.values())
					if (property.getName().startsWith(this.name))
						if (property.getName().lastIndexOf('.') == this.name.length())
							subs.add(property);
			}
			return subs;
		}

		public PropertyDescriptor getDescriptor() {
			if (this.descriptor != null)
				return this.descriptor;
			Property p = this.getDefaultProperty();
			if (p != null)
				return p.getDescriptor();
			return null;
		}
	}
}
