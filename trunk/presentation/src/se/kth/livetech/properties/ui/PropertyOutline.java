package se.kth.livetech.properties.ui;

import java.awt.Color;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import javax.swing.Icon;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RenderDataProvider;
import org.netbeans.swing.outline.RowModel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;

@SuppressWarnings("serial")
public class PropertyOutline extends Outline implements PropertyListener {
	private IProperty property;
	private Model model;
	private PropertySelectionChangedListener selectListener;

	/**
	 * A listener for property selection changes in the PropertyOutline widget
	 * @author hammond
	 *
	 */
	public interface PropertySelectionChangedListener {
		public void selectionChanged(IProperty property);
	}

	public PropertyOutline(IProperty property) {
		this.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		this.property = property;
		this.model = new Model();
		OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(model, model, true, "Tree");
		this.setModel(outlineModel);
		property.addPropertyListener(this);
		setRenderDataProvider(new RenderProvider());
	}

	private class RenderProvider implements RenderDataProvider {
		public String getDisplayName(Object a) {
			IProperty ip = (IProperty) a;
			String name = ip.getName();
			int index = name.lastIndexOf('.');
			String extra = ""; //" (" + ip.getSubProperties().size() + ")";
			return (index < 0 ? name : name.substring(index+1)) + extra;
		}

		public String getTooltipText(Object a) {
			IProperty ip = (IProperty) a;
			String name = ip.getName();
			String extra = " (" + ip.getSubProperties().size() + ")";
			return name + extra;
		}

		public Color getBackground(Object a) { return Color.white; }
		public Color getForeground(Object a) { return Color.black; }
		public Icon getIcon(Object a) { return null; }
		public boolean isHtmlDisplayName(Object a) { return false; }
	}
	
	public void setPropertySelectionChangedListener(PropertySelectionChangedListener sl) { selectListener = sl; }
	
	public void valueChanged(ListSelectionEvent le) {
		super.valueChanged(le);
		if ( selectListener == null ) return;
		if ( le.getValueIsAdjusting() ) return;
		int row = this.getSelectedRow();
		if ( row >= 0 ) {
			IProperty p = (IProperty)this.dataModel.getValueAt(row, 0);
			selectListener.selectionChanged(p);
		}
		else
			selectListener.selectionChanged(null);
	}
	
	class Model implements TreeModel, RowModel {
		Set<TreeModelListener> listeners = new CopyOnWriteArraySet<TreeModelListener>();
		
		public void addTreeModelListener(TreeModelListener l) {
			listeners.add(l);
		}

		public Object getChild(Object parent, int index) {
			return ((IProperty) parent).getSubProperties().get(index);
		}

		public int getChildCount(Object parent) {
			//System.out.println("getChildCount " + ((IProperty) parent).getSubProperties().size());
			return ((IProperty) parent).getSubProperties().size();
		}

		public int getIndexOfChild(Object parent, Object child) {
			return ((IProperty) parent).getSubProperties().indexOf(child);
		}

		public Object getRoot() {
			//System.out.println("getRoot " + PropertyOutline.this.property);
			return PropertyOutline.this.property;
		}

		public boolean isLeaf(Object node) {
			return getChildCount(node) == 0;
		}

		public void removeTreeModelListener(TreeModelListener l) {
			listeners.remove(l);
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
			TreeModelEvent e = new TreeModelEvent(this, path);
			for (TreeModelListener l : this.listeners) {
				l.treeStructureChanged(e);
			}
		}
		
		// Column model

		private final String[] columnNames = { "Value", "Link" }; 
		
		public int getColumnCount() { return columnNames.length; }
		public String getColumnName(int i) { return columnNames[i]; }
		public Class<?> getColumnClass(int i) { return String.class; }
		public boolean isCellEditable(Object node, int i) { return true; }

		public Object getValueFor(Object node, int i) {
			IProperty p = (IProperty) node;
			switch (i) {
			case 0: return p.getValue();
			case 1: return p.getLink();
			}
			return null;
		}

		public void setValueFor(Object node, int i, Object v) {
			IProperty p = (IProperty) node;
			String s = v.toString();
			switch (i) {
			case 0: p.setValue(s); break;
			case 1: p.setLink(s); break;
			}
		}
		
	}
	public void propertyChanged(IProperty changed) {
		String[] parts = changed.getName().split("\\.");
		TreePath path = new TreePath(parts); //this.model.getRoot());
		this.model.valueForPathChanged(path, this.model.getRoot());
		this.tableChanged(new TableModelEvent(this.dataModel));
//		this.setModel(dataModel);
		//System.err.println("Property changed: " + changed);
	}
}
