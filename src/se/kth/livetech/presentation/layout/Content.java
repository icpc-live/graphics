package se.kth.livetech.presentation.layout;

public interface Content<Style> {
	public boolean isText();
	public String getText();
	public boolean isImage();
	public String getImageName();
	public Style getStyle();
	
	public static abstract class Text<Style> implements Content<Style> {
		public boolean isText() {
			return true;
		}
		
		public boolean isImage() {
			return false;
		}
		
		public String getImageName() {
			return null;
		}
	}

	public static abstract class Image<Style> implements Content<Style> {
		public boolean isText() {
			return false;
		}
		
		public String getText() {
			return null;
		}
		
		public boolean isImage() {
			return true;
		}
	}
}
