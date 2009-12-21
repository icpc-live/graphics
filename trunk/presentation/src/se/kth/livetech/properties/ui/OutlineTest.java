package se.kth.livetech.properties.ui;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.netbeans.swing.outline.DefaultOutlineModel;
import org.netbeans.swing.outline.Outline;
import org.netbeans.swing.outline.OutlineModel;
import org.netbeans.swing.outline.RowModel;

import se.kth.livetech.properties.IProperty;

@SuppressWarnings("serial")
public class OutlineTest extends Outline {
	IProperty property;
	public OutlineTest(IProperty property) {
		Model model = new Model();
		OutlineModel outlineModel = DefaultOutlineModel.createOutlineModel(model, model, true, "Name");
		this.setModel(outlineModel);
		this.property = property;
	}
	class Model implements TreeModel, RowModel {
		public void addTreeModelListener(TreeModelListener l) {
			// TODO Auto-generated method stub
		}

		public Object getChild(Object parent, int index) {
			return parent.toString() + "." + index;
		}

		public int getChildCount(Object parent) {
			return parent.toString().length();
		}

		public int getIndexOfChild(Object parent, Object child) {
			for (int i = 0; i < getChildCount(parent); ++i)
				if (getChild(parent, i).equals(child))
					return i;
			return 0;
		}

		public Object getRoot() {
			return "0";
		}

		public boolean isLeaf(Object node) {
			return getChildCount(node) == 0;
		}

		public void removeTreeModelListener(TreeModelListener l) {
			// TODO Auto-generated method stub
		}

		public void valueForPathChanged(TreePath path, Object newValue) {
			// TODO Auto-generated method stub
		}
		
		// Column model

		public Class<?> getColumnClass(int i) {
			return String.class;
		}

		public int getColumnCount() {
			return 3;
		}

		public String getColumnName(int i) {
			return new String[]{"Name", "Type", "Value"}[i];
		}

		public Object getValueFor(Object node, int i) {
			return node.toString() + '/' + i;
		}

		public boolean isCellEditable(Object node, int i) {
			return false;
		}

		public void setValueFor(Object node, int i, Object v) {
		}
	}
}
