package lukfor.radio;

public class RadioStation {

	private String title;

	private String url;

	private String trackTitle = "*** unknown ***";

	public RadioStation() {

	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getTitle() {
		return title;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getUrl() {
		return url;
	}

	public void setTrackTitle(String trackTitle) {
		this.trackTitle = trackTitle;
	}

	public String getTrackTitle() {
		return trackTitle;
	}

}
