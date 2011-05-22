package se.kth.livetech.presentation.layout;

import java.util.Collection;

public interface Content {
	public boolean isText();
	public String getText();
	public boolean isImage();
	public String getImageName();
	public boolean isGraph();
	public Graph getGraph();
	public Object getStyle();
	public int getLayer();
	
	public interface Graph {
		public interface Node {
			Object getKey();
			double getX();
			double getY();
			Object getNodeStyle();
		}
		public Object getLineStyle();
		public double getLineWidth();
		public Collection<? extends Node> getNodes();
	}

	@Deprecated
	public static abstract class Text implements Content {
		@Override
		public boolean isText() {
			return true;
		}

		@Override
		public boolean isImage() {
			return false;
		}

		@Override
		public String getImageName() {
			return null;
		}

		@Override
		public boolean isGraph() {
			return false;
		}

		@Override
		public Graph getGraph() {
			return null;
		}
		
		@Override
		public int getLayer() {
			return 0;
		}
	}

	@Deprecated
	public static abstract class Image implements Content {
		@Override
		public boolean isText() {
			return false;
		}

		@Override
		public String getText() {
			return null;
		}

		@Override
		public boolean isImage() {
			return true;
		}

		@Override
		public boolean isGraph() {
			return false;
		}

		@Override
		public Graph getGraph() {
			return null;
		}
		
		@Override
		public int getLayer() {
			return 0;
		}
	}
	
	@Deprecated
	public static abstract class Background implements Content {
		@Override
		public boolean isText() {
			return false;
		}

		@Override
		public String getText() {
			return null;
		}

		@Override
		public boolean isImage() {
			return false;
		}
		
		@Override
		public String getImageName() {
			return null;
		}

		@Override
		public boolean isGraph() {
			return false;
		}

		@Override
		public Graph getGraph() {
			return null;
		}
		
		@Override
		public int getLayer() {
			return -1;
		}
	}
}
