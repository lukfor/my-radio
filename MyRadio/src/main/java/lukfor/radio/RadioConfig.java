package lukfor.radio;

import java.util.List;
import java.util.Vector;

public class RadioConfig {

	private List<RadioStation> stations = new Vector<RadioStation>();
	
	public void setStations(List<RadioStation> stations) {
		this.stations = stations;
	}
	
	public List<RadioStation> getStations() {
		return stations;
	}
	
}
