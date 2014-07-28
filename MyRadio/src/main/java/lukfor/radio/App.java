package lukfor.radio;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.pi4j.component.lcd.LCDTextAlignment;
import com.pi4j.component.lcd.impl.GpioLcdDisplay;
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
	
	public static void main(String[] args) throws InterruptedException,
			IOException {

		final GpioLcdDisplay lcd = new GpioLcdDisplay(LCD_ROWS, // number of row
				// supported by
				// LCD
LCD_COLUMNS, // number of columns supported by LCD
RaspiPin.GPIO_06, // LCD RS pin
RaspiPin.GPIO_05, // LCD strobe pin
RaspiPin.GPIO_04, // LCD data bit 1
RaspiPin.GPIO_00, // LCD data bit 2
RaspiPin.GPIO_02, // LCD data bit 3
RaspiPin.GPIO_03); // LCD data bit 4
		
		System.out.println("Hello.");

		lcd.clear();
		lcd.writeln(LCD_ROW_1, "<< Radio on >>", LCDTextAlignment.ALIGN_CENTER);
		
		final Radio radio = new Radio();
		radio.addListener(new IRadioListener() {

			@Override
			public void onUpdate(RadioStation station) {
				System.out.println("Radio: "
						+ radio.getRadioStation().getTitle());
				System.out.println("Title: "
						+ radio.getRadioStation().getTrackTitle());
				
				//lcd.clear();
				lcd.writeln(LCD_ROW_1, radio.getRadioStation().getTitle());
				lcd.writeln(LCD_ROW_2, radio.getRadioStation().getTrackTitle());

			}
		});

		if (!radio.on()){
			System.out.println("Radio on failed.");
			return;
		}

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
		lcd.clear();
		lcd.writeln(LCD_ROW_1, "<< Radio odd >>", LCDTextAlignment.ALIGN_CENTER);
	
		System.out.println("Bye Bye.");

	}
}
