package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.lang.Math;

public class Candle extends Sprite {
	
	// coefficients related to the candles
    final int CANDLE_WIDTH = 60;
    final int LONG_CANDLE_HEIGHT = 190;
    final int MIDDLE_CANDLE_HEIGHT = 150;
    final int SHORT_CANDLE_HEIGHT = 100;
    final int CANDLE_COUNT = 500;
    
	public Candle() {
		super();
	}
	
	public Candle(double pX, double pY, Image image) {
		super();
	}
	
	public String randomCandlePic() {
		int max = 6;
        int min = 1;
        int range = max - min + 1;
        int random = 7;
        while (random > 6) {
        	random = (int)(Math.random() * range) + min;
        }
        if (random == 1) {
        	return ("ShortCandleUp");
        }
        else if (random == 2) {
        	return ("MiddleCandleUp");
        }
        else if (random == 3) {
        	return ("LongCandleUp");
        }
        else if (random == 4) {
        	return ("ShortCandleBottom");
        }
        else if (random == 5) {
        	return ("MiddleCandleBottom");
        }
        else if (random == 6) {
        	return ("LongCandleBottom");
        }
        return "MiddleCandleUp";
	}
}