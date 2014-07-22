package lukfor.radio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Hello world!
 * 
 */
public class App {
	public static void main(String[] args) throws InterruptedException,
			IOException {

		System.out.println("Hello.");

		final Radio radio = new Radio();
		radio.addListener(new IRadioListener() {

			@Override
			public void onUpdate(RadioStation station) {
				System.out.println("Radio: "
						+ radio.getRadioStation().getTitle());
				System.out.println("Title: "
						+ radio.getRadioStation().getTrackTitle());
			}
		});

		radio.on();

		System.out.println("Radio on.");

		BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
		String s;
		while ((s = in.readLine()) != null) {
			if (s.equals("next")) {
				radio.playNext();
			}
			if (s.equals("prev")) {
				radio.playPrevious();
			}
			if (s.equals("quit")) {
				break;
			}
		}

		radio.off();
		System.out.println("Radio off.");

		System.out.println("Bye Bye.");

	}
}
