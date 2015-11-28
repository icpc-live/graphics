/*
 * Copyright © 2015 Spotify AB
 */
package se.kth.livetech.control.ui;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.swing.JFrame;

public class BaseFrame extends JFrame {
  public BaseFrame() {
    positionOnSecondaryScreen();
  }
  public BaseFrame(String title) {
    super(title);
    positionOnSecondaryScreen();
  }

  private void positionOnSecondaryScreen() {
    System.err.println("############## Frame");

    // Position on non-default screen if possible
    // TODO (this is all since rendering on the primary screen is fast)
    // TODO (probably possible to set up a JFrame with an optimized GraphicsConfiguration instead)
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice defaultGd = ge.getDefaultScreenDevice();
    GraphicsDevice wantedGd = defaultGd;
    for (GraphicsDevice gd : ge.getScreenDevices()) {
      if (gd != defaultGd) {
        wantedGd = gd;
      }
    }
    Rectangle bounds = wantedGd.getDefaultConfiguration().getBounds();
    System.err.println("############## GD default " + defaultGd + " wanted " + wantedGd);
    System.err.println("############## my bounds " + getBounds());
    System.err.println("############## wanted bounds " + bounds);
    setBounds((int) bounds.getX(), (int) bounds.getY(), getWidth(), getHeight());
  }
}
