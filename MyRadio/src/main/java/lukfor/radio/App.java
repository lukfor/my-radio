package lukfor.radio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.Enumeration;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.pi4j.io.gpio.RaspiPin;

/**
 * Hello world!
 * 
 */
public class App {

	public final static int LCD_ROWS = 2;
	public final static int LCD_ROW_1 = 0;
	public final static int LCD_ROW_2 = 1;
	public final static int LCD_COLUMNS = 16;
	public final static int LCD_BITS = 4;

	private static final Log log = LogFactory.getLog(App.class);

	
	public static void main(String[] args) throws InterruptedException,
			IOException {

		final RadioLcd lcd = new RadioLcd(RaspiPin.GPIO_06, // LCD RS pin
				RaspiPin.GPIO_05, // LCD strobe pin
				RaspiPin.GPIO_04, // LCD data bit 1
				RaspiPin.GPIO_00, // LCD data bit 2
				RaspiPin.GPIO_02, // LCD data bit 3
				RaspiPin.GPIO_03); // LCD data bit 4

		Thread lcdThread = new Thread(lcd);
		lcdThread.start();

		
		
		log.info("Hello.");

		lcd.clear();
		lcd.writeLineA("<< Radio on >>", false);

		Enumeration e = NetworkInterface.getNetworkInterfaces();
		while (e.hasMoreElements()) {
			NetworkInterface n = (NetworkInterface) e.nextElement();
			Enumeration ee = n.getInetAddresses();
			while (ee.hasMoreElements()) {
				InetAddress i = (InetAddress) ee.nextElement();

				if (i.getHostAddress().startsWith("192")) {
					lcd.writeLineB(i.getHostAddress().toString(), false);

				}
			}
		}
		final Radio radio = new Radio();
		radio.addListener(new IRadioListener() {

			@Override
			public void onUpdate(RadioStation station) {
				log.info("Radio: "
						+ radio.getRadioStation().getTitle());
				log.info("Title: "
						+ radio.getRadioStation().getTrackTitle());

				// lcd.clear();
				lcd.writeLineA(radio.getRadioStation().getTitle(), false);
				lcd.writeLineB(radio.getRadioStation().getTrackTitle()
						+ " *** ", true);

			}
		});

		if (!radio.on()) {
			log.error("Radio on failed.");
			return;
		}

		log.info("Radio on.");

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
		log.info("Radio off.");
		lcd.clear();
		lcd.writeLineA("<< Radio odd >>", false);
		lcd.writeLineB("", false);

		lcdThread.interrupt();

		log.info("Bye Bye.");

	}
}
