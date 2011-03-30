package se.kth.livetech.presentation.layout;

public interface Content {
	public boolean isText();
	public String getText();
	public boolean isImage();
	public String getImageName();
	public Object getStyle();
	public int getLayer();

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
		public int getLayer() {
			return -1;
		}
	}
}
