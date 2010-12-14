package se.kth.livetech.control.ui;

import java.awt.Dimension;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.JLabel;
import javax.swing.JPanel;

import se.kth.livetech.properties.IProperty;

/*
 * 
 * Key strokes that this panel handles are:
 * 
 * Numpad:
 *   [0-9]+ Return: Enter team number
 *   Ctrl+(1-5): Change view of current panel
 *   
 * Note: All numpad keys are consumed
 *       The numpad return key seems to be sent to the other panels even if consumed here
 *
 */

public class QuickControlPanel extends JPanel implements KeyEventDispatcher {
	private static final long serialVersionUID = -7809542144937479696L;
	private JLabel label;
	private IProperty controlBase;
	private IProperty clientBase;
	private int numberBuffer;
	private int numDigits;
	private static final int MAX_TEAMS = 103;

	private Set<Character> numpadPressed = new HashSet<Character>();
	
	public QuickControlPanel(IProperty controlBase, IProperty clientBase) {
		this.controlBase = controlBase;
		this.clientBase = clientBase;
		label = new JLabel("testing testing testing");
		label.setFont(label.getFont().deriveFont(24f));
		label.setVisible(true);
		add(label);
		this.setPreferredSize(new Dimension(200,10));
		nothing();
		KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(this);
	}
	
	private IProperty getSelected() {
		String panel = controlBase.get("preview").getValue();
		String name = controlBase.get(panel).get("name").getValue();
		return clientBase.get(name);
	}
	
	public boolean dispatchKeyEvent(KeyEvent e) {
		boolean down = e.getID() == KeyEvent.KEY_PRESSED;
		boolean released = e.getID() == KeyEvent.KEY_RELEASED;
		boolean typed = e.getID() == KeyEvent.KEY_TYPED;
		boolean ctrl = (e.getModifiersEx() & InputEvent.CTRL_DOWN_MASK) != 0;
		boolean numpad = e.getKeyLocation() == KeyEvent.KEY_LOCATION_NUMPAD;
		boolean standard = e.getKeyLocation() == KeyEvent.KEY_LOCATION_STANDARD;
		
		// hack to get rid of the unwanted KEY_TYPED events on our keys
		if ( typed && numpadPressed.contains(e.getKeyChar()) ) {
			e.consume();
			return true;
		}
		
		if ( standard && ctrl && e.getKeyCode() == KeyEvent.VK_SPACE ) {
			if ( down ) getSelected().get("clear").toggleBooleanValue();
			e.consume();
			return true;
		}
		else if ( standard && ctrl && e.getKeyCode() == KeyEvent.VK_ENTER ) {
			// TODO:
			e.consume();
			return true;
		}
		else if ( standard ) {
			if ( ctrl ) {
				if ( e.getKeyChar() >= '0' && e.getKeyChar() <= '9' ) {
					switch(e.getKeyChar()) {
					case '1': controlBase.set("preview", "panel1"); break;
					case '2': controlBase.set("preview", "panel2"); break;
					case '3': controlBase.set("preview", "panel3"); break;
					case '4': controlBase.set("preview", "panel4"); break;
					}
					e.consume();
					return true;
				}
			}
		}
		else if ( numpad ) {
			// hack to get rid of the unwanted KEY_TYPED events on our keys
			if ( down ) numpadPressed.add(e.getKeyChar());
			else if ( released ) numpadPressed.remove(e.getKeyChar());

			if ( ctrl && down ) {
				switch(e.getKeyChar()) {
				case '1': getSelected().get("mode").setValue("score"); break;
				case '2': getSelected().get("mode").setValue("interview"); break;
				case '3': getSelected().get("mode").setValue("team"); break;
				case '4': getSelected().get("team.show_members").toggleBooleanValue(); break;
				case '5': getSelected().get("team.show_extra").toggleBooleanValue(); break;
				case '6': getSelected().get("team.show_queue").toggleBooleanValue(); break;
				case '7': getSelected().get("mode").setValue("vnc"); break;
				case '8': getSelected().get("mode").setValue("cam"); break;
				case '9': getSelected().get("mode").setValue("fireworks"); break;
				}
			}
			else if ( e.getKeyChar() >= '0' && e.getKeyChar() <= '9' ) {
				if ( down ) {
					int digit = e.getKeyChar() - '0';
					int newNumberBuffer = numberBuffer*10 + digit;
					if ( newNumberBuffer > MAX_TEAMS ) {
						if (digit == 0 ) {
							nothing();
						}
					}
					else {
						if ( digit != 0 || numDigits != 0 ) {
							numberBuffer = newNumberBuffer;
							label.setText(Integer.toString(numberBuffer));
							numDigits++;
						}
					}
				}
			}
			else if ( e.getKeyCode() == KeyEvent.VK_ENTER ) {
				if ( down && numDigits != 0 ) {
					if ( numberBuffer != 0 ) {
						IProperty teamProp = getSelected().get("team.team");
						teamProp.setIntValue(numberBuffer);
						nothing();
					}
				}
			}
			else if ( e.getKeyCode() == 12 ) {
				if ( down ) {
					nothing();
				}
			}
			e.consume();
			return true;
		}
		return false;
	}

	private void nothing() {
		numberBuffer = 0;
		numDigits = 0;
		label.setText("--");
	}
}
