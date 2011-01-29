package se.kth.livetech.presentation.layout;

public interface Content {
	public boolean isText();
	public String getText();
	public boolean isImage();
	public String getImageName();
	public Object getStyle();
	// TODO: public Object getLayer();

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
	}

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
	}
}
