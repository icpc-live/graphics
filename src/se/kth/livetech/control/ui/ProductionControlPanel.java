package se.kth.livetech.control.ui;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;

import se.kth.livetech.properties.IProperty;
import se.kth.livetech.properties.PropertyListener;
import se.kth.livetech.properties.ui.Text;
import se.kth.livetech.properties.ui.ToggleButton;

@SuppressWarnings("serial")
public class ProductionControlPanel  extends JPanel implements PropertyListener{
	Box box=new Box(BoxLayout.Y_AXIS);
	int index=-1;
	IProperty base=null;
	IProperty clients=null;
	public ProductionControlPanel (IProperty b, IProperty c, int i) {
		index=i;
		base=b;
		clients=c;
		Box textPreviewBox = Box.createHorizontalBox();
		textPreviewBox.add(new Text(base.get("panel"+index+".name")));
		textPreviewBox.add(new ToggleButton(base.get("preview"), "panel"+index, "Active"));
		box.add(textPreviewBox);
		base.get("panel"+index+".name").addPropertyListener(this);
		//clients.addPropertyListener(this);
		//base.addPropertyListener(this);
		this.add(box);
		updateFrame();
	}
	
	ProductionPanel currentPanel=null;
	
	private synchronized void updateFrame() {
//		DebugTrace.trace("Control panel updated: "+this.toString());
		if(currentPanel!=null) {
			box.remove(currentPanel);
		}
		String client=base.get("panel"+index+".name").getValue();
		if(client.equals("")){
			client="default";
		}
		currentPanel = new ProductionPanel(clients.get(client));
		box.add(currentPanel);
		//DebugTrace.trace(clients.get(base.get("panel"+index+".name").getValue()).getName());
		this.validate();
	}

	public synchronized void propertyChanged(IProperty changed) {
		updateFrame();
	}
}
