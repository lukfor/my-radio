package lukfor.radio.mplayer;

public interface IMPlayerListener {

	public static final int STREAM_TITLE_CHANGED = 2;
		
	public void onChange(int type);
	
}
