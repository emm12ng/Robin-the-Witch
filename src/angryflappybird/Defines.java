package angryflappybird;

import java.awt.TextArea;
import java.awt.TextField;
import java.util.HashMap;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;


public class Defines {
    
	// dimension of the GUI application
    final int APP_HEIGHT = 600;
    final int APP_WIDTH = 600;
    final int SCENE_HEIGHT = 570;
    final int SCENE_WIDTH = 400;

    // coefficients related to the blob
    final int BLOB_WIDTH = 70;
    final int BLOB_HEIGHT = 70;
    final int BLOB_POS_X = 70;
    final int BLOB_POS_Y = 200;
    final int BLOB_DROP_TIME = 300000000;  	// the elapsed time threshold before the blob starts dropping
    final int BLOB_DROP_VEL = 300;    		// the blob drop velocity
    final int BLOB_FLY_VEL = -40;			// the blob flying velocity
    final int BLOB_IMG_LEN = 10;
    final int BLOB_IMG_PERIOD = 5;
    
    // coefficients related to the ghost
    final int GHOST_POS_Y = 0;
    final int GHOST_POS_X = SCENE_WIDTH;
    final int GHOST_VEL1 = 45;
    final int GHOST_VEL2 = 90;
    final int GHOST_VEL3 = 120;
    final int GHOST_WIDTH = 70;
    final int GHOST_HEIGHT = 90;
    final int GHOST_COUNT = 2;
    
    // coefficients related to the pumpkin
    final int PUMPKIN_POS_X = SCENE_WIDTH;
    final int PUMPKIN_POS_Y = 150; // depend on pipe
    final double PUMPKIN_VEL = -58.2; // depend on pipe
    final int PUMPKIN_WIDTH = 45;
    final int PUMPKIN_HEIGHT = 45;
    final int PUMPKIN_COUNT = 2;
    
    // coefficients related to the floors
    final int FLOOR_WIDTH = 400;
    final int FLOOR_HEIGHT = 100;
    final int FLOOR_COUNT = 2;
    
 // coefficients related to the candles
    final int UP_CANDLE_WIDTH = 60;
    final int BOTTOM_CANDLE_WIDTH = 50;
    final int LONG_CANDLE_HEIGHT = 160;
    final int MIDDLE_CANDLE_HEIGHT = 140;
    final int SHORT_CANDLE_HEIGHT = 110;
    final int CANDLE_COUNT = 30;
    
    final int CANDLES_SPLIT = 300;
    final int CANDLES_START = 500;
    final int CANDLE_HOLDER_SQUARE = 130;
    
    // coefficients related to time
    final int SCENE_SHIFT_TIME = 5;
    final double SCENE_SHIFT_INCR = -0.4;
    final double NANOSEC_TO_SEC = 1.0 / 1000000000.0;
    final double TRANSITION_TIME = 0.1;
    final int TRANSITION_CYCLE = 2;
    
    
    //Sound for Witch Laugh
    //final AudioClip witchLaugh = new AudioClip(getClass().getResource("../resources/sounds/witchLaugh").toExternalForm());
    
    Media witchLaugh = new Media(getClass().getResource("../resources/sounds/witchLaugh1.mp3").toExternalForm());
    MediaPlayer witchLaughMP = new MediaPlayer(witchLaugh);
    
    Media ghostSound = new Media(getClass().getResource("../resources/sounds/ghostSound2.mp3").toExternalForm());
    MediaPlayer ghostSoundMP = new MediaPlayer(ghostSound);
    
    Media backgroundMusic = new Media(getClass().getResource("../resources/sounds/backgroundMusic.mp3").toExternalForm());
    MediaPlayer backgroundMusicMP = new MediaPlayer(backgroundMusic);
    
    
    // coefficients related to media display
    final String STAGE_TITLE = "Angry Flappy Bird";
	private final String IMAGE_DIR = "../resources/images/";

    final String[] IMAGE_FILES = {"backgroundLight","blob0", "blob1", "blob2", "blob3", "floor1", "ShortCandleUp", "MiddleCandleUp", "LongCandleUp", "ShortCandleBottom", "MiddleCandleBottom", "LongCandleBottom", "ghost", "goldpumpkin", "normalpumpkin","1-0", "1-1", "1-2", "1-3","1-4","1-5","1-6","1-7","1-8","1-9","1-f", "CandlesLong","CandlesLongUp","CandleBottom", "background1"};


    final HashMap<String, ImageView> IMVIEW = new HashMap<String, ImageView>();
    final HashMap<String, Image> IMAGE = new HashMap<String, Image>();
    
    //nodes on the scene graph
    Button startButton;
  
    RadioButton easy;
    RadioButton intermediate;
    RadioButton hard;
    RadioButton survival;
    
    ToggleGroup difficultyLevels;
    
    Label normalPumpkinInstruct;
    Label goldPumpkinInstruct;
    Label ghostInstruct;
    
    // constructor
	Defines() {
		
		// initialize images
		for(int i=0; i<IMAGE_FILES.length; i++) {
			Image img;
			
			
		
			if (i == 5) {
				img = new Image(pathImage(IMAGE_FILES[i]), FLOOR_WIDTH, FLOOR_HEIGHT, false, false);
			}
			else if (i == 1 || i == 2 || i == 3 || i == 4){
				img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
			}
			
			
			
			else if (i == 6){
				img = new Image(pathImage(IMAGE_FILES[i]), UP_CANDLE_WIDTH, SHORT_CANDLE_HEIGHT, false, false);
			}
			else if (i == 7){
				img = new Image(pathImage(IMAGE_FILES[i]), UP_CANDLE_WIDTH, MIDDLE_CANDLE_HEIGHT, false, false);
			}
			else if (i == 8){
				img = new Image(pathImage(IMAGE_FILES[i]), UP_CANDLE_WIDTH, LONG_CANDLE_HEIGHT, false, false);
			}
			else if (i == 9){
				img = new Image(pathImage(IMAGE_FILES[i]), BOTTOM_CANDLE_WIDTH, SHORT_CANDLE_HEIGHT, false, false);
			}
			else if (i == 10){
				img = new Image(pathImage(IMAGE_FILES[i]), BOTTOM_CANDLE_WIDTH, MIDDLE_CANDLE_HEIGHT, false, false);
			}
			else if (i == 11){
				img = new Image(pathImage(IMAGE_FILES[i]), BOTTOM_CANDLE_WIDTH, LONG_CANDLE_HEIGHT, false, false);
			} else if (i == 12) {
				img = new Image(pathImage(IMAGE_FILES[i]), GHOST_WIDTH, GHOST_HEIGHT, false, false);
			}
			else if (i == 13) {
				img = new Image(pathImage(IMAGE_FILES[i]), PUMPKIN_WIDTH, PUMPKIN_HEIGHT, false, false);
			}
			else if (i == 14) {
				img = new Image(pathImage(IMAGE_FILES[i]), PUMPKIN_WIDTH, PUMPKIN_HEIGHT, false, false);
			}   
			else if (i>=15 && i <=25){
				System.out.println(IMAGE_FILES[i]);
				img = new Image(pathImage(IMAGE_FILES[i]), BLOB_WIDTH, BLOB_HEIGHT, false, false);
				
			}
			else if (i == 26 || i == 27) {
				img = new Image(pathImage(IMAGE_FILES[i]), 40, 500, false, false);
				
			}
			else if (i == 28) {
				img = new Image(pathImage(IMAGE_FILES[i]), CANDLE_HOLDER_SQUARE -50 ,CANDLE_HOLDER_SQUARE, false, false);
			}
			else {
				img = new Image(pathImage(IMAGE_FILES[i]), SCENE_WIDTH, SCENE_HEIGHT, false, false);
			}
    		IMAGE.put(IMAGE_FILES[i],img);
    	}
		
		// initialize image views
		for(int i=0; i<IMAGE_FILES.length; i++) {
    		ImageView imgView = new ImageView(IMAGE.get(IMAGE_FILES[i]));
    		IMVIEW.put(IMAGE_FILES[i],imgView);
    	}
		
		// initialize scene nodes
		startButton = new Button("Go!");
		
		
		easy = new RadioButton("Easy");
		intermediate = new RadioButton("Intermediate");
		hard = new RadioButton("Hard");
		survival = new RadioButton("Survival");
		
		difficultyLevels  = new ToggleGroup();
		easy.setToggleGroup(difficultyLevels);
		intermediate.setToggleGroup(difficultyLevels);
		hard.setToggleGroup(difficultyLevels);
		survival.setToggleGroup(difficultyLevels);
		
		normalPumpkinInstruct = new Label("5 Bonus Points");
		goldPumpkinInstruct = new Label("Go on autopilot");
		ghostInstruct = new Label("Avoid ghosts, \nand don't let \nthem steal \npumpkins");
		
		ImageView normalPumpkinView = new ImageView(IMAGE.get("normalpumpkin"));
		ImageView goldPumpkinView = new ImageView(IMAGE.get("goldpumpkin"));
		ImageView ghostView = new ImageView(IMAGE.get("ghost"));
		
		normalPumpkinView.setFitHeight(70);
		normalPumpkinView.setFitWidth(70);
		goldPumpkinView.setFitHeight(70);
		goldPumpkinView.setFitWidth(70);
		
		normalPumpkinInstruct.setGraphic(normalPumpkinView);
		goldPumpkinInstruct.setGraphic(goldPumpkinView);
		ghostInstruct.setGraphic(ghostView);
		
		
	}
	
	public String pathImage(String filepath) {
    	String fullpath = getClass().getResource(IMAGE_DIR+filepath+".png").toExternalForm();
    	System.out.println(filepath);
    	return fullpath;
    }
	
	public Image resizeImage(String filepath, int width, int height) {
    	IMAGE.put(filepath, new Image(pathImage(filepath), width, height, false, false));
    	return IMAGE.get(filepath);
    }
	

	public AudioClip getSound(String filepath) {
    	AudioClip soundEffect = new AudioClip(getClass().getResource(filepath).toExternalForm());
    	return soundEffect;
    }
	
	public String randomUpCandlePic() {
		int max = 3;
        int min = 1;
        int range = max - min + 1;
        int random = 4;
        while (random > 3) {
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
        return "MiddleCandleUp";
	}
	
	public String randomBottomCandlePic() {
		int max = 3;
        int min = 1;
        int range = max - min + 1;
        int random = 4;
        while (random > 3) {
        	random = (int)(Math.random() * range) + min;
        	if (random == 1) {
            	return ("ShortCandleBottom");
            }
            else if (random == 2) {
            	return ("MiddleCandleBottom");
            }
            else if (random == 3) {
            	return ("LongCandleBottom");
            }
        }
        return "MiddleCandleBottom";    
	}
	



}
