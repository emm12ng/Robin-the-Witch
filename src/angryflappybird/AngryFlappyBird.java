package angryflappybird;

import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

import com.sun.media.jfxmedia.AudioClip;

import java.awt.geom.Rectangle2D;
import java.io.File;



//The Application layer
public class AngryFlappyBird extends Application {
	
	private Defines DEF = new Defines();
    
    // time related attributes
    private long clickTime, startTime, elapsedTime;   
    private AnimationTimer timer;
    
    // game components
    private Sprite blob;
    private ArrayList<Sprite> floors;

    private ArrayList<Ghost> ghosts;
    private ArrayList<Pumpkin> pumpkins;

    private ArrayList<Sprite> candles;

    
    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)

    private GraphicsContext gc;	
    private GraphicsContext bgc;
    private GraphicsContext cgc;	
    
    
	//Sound for Witch Laugh
    
    //AudioClip witchLaugh = new AudioClip(getClass().getResource("../resources/sounds/witchLaugh").toExternalForm());
    //Media witchLaugh = new Media(getClass().getResource("../resources/sounds/witchLaugh.mp3").toExternalForm());
    //MediaPlayer mediaPlayer = new MediaPlayer(witchLaugh);
    
    
    
    // canvas for background
    
    
    //the status of the auto-pliot mode
    private boolean auto = false;

    
	// the mandatory main method 
    public static void main(String[] args) {
        launch(args);
    }
    
    
       
    // the start method sets the Stage layer
    @Override
    public void start(Stage primaryStage) throws Exception {
    	
    	// initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
    	resetGameScene(true);  // resets the gameScene
    	
        HBox root = new HBox();
		HBox.setMargin(gameScene, new Insets(0,0,0,15));
		root.getChildren().add(gameScene);
		root.getChildren().add(gameControl);
		
		// add scene graphs to scene
        Scene scene = new Scene(root, DEF.APP_WIDTH, DEF.APP_HEIGHT);
        
        // finalize and show the stage
        primaryStage.setScene(scene);
        primaryStage.setTitle(DEF.STAGE_TITLE);
        primaryStage.setResizable(false);
        primaryStage.show();
    }
    
    // the getContent method sets the Scene layer
    private void resetGameControl() {
        
    	//RadioButton selectedDifficulty = (RadioButton) DEF.difficultyLevels.getSelectedToggle();
		//selectedDifficulty.setOnAction(event -> {
    		if (DEF.easy.isSelected()) {
	     	   System.out.print("Difficulty Level: Easy");
	        }
	        else if (DEF.medium.isSelected()) {
	     	   System.out.print("Difficulty Level: Medium");
	        }
	        else if (DEF.hard.isSelected()) {
	     	   System.out.print("Difficulty Level: Hard");
	        }
	        else if (DEF.survival.isSelected()){
	     	   System.out.print("Difficulty Level: Survival");
	        }
 	
	    	
    	
        //modifying feature
    	//use keyboard input to fire the button
    	
        DEF.startButton.setOnKeyPressed(event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
            	 DEF.startButton.fire();
            }
        }
        );
        
        DEF.startButton.setOnAction(event -> {
            if (GAME_OVER) {
                resetGameScene(false);
                
            }
        	else if (GAME_START){
                clickTime = System.nanoTime();   
                
            }
        	GAME_START = true;
            CLICKED = true; 
            
        	}
        );
        
       
        
        //DEF.startButton.setOnMouseClicked(this::mouseClickHandler);
        
        
        gameControl = new VBox();
        gameControl.getChildren().addAll(DEF.startButton, DEF.easy, DEF.medium, DEF.hard, DEF.survival);
      
    }
    
    /*
    private void mouseClickHandler() {
    	if (GAME_OVER) {
            resetGameScene(false);
        }
    	else if (GAME_START){
            clickTime = System.nanoTime();   
        }
    	GAME_START = true;
        CLICKED = true;
    }
    */
    
    
    //Change the background with time
    /*
    private ImageView changeBackground() {
		 ImageView background;
		 if (startTime > 5) {
			 System.out.print("Scene Change");
			 background = DEF.IMVIEW.get("background1");
			 return background;
		 }
		 background = DEF.IMVIEW.get("background");
		 return background;
	 }
	 */
    
    
    //Control background music
    public void controlBackgroundMusic() {
    	MediaPlayer bgMusic = DEF.backgroundMusicMP;
    	bgMusic.setVolume(1);
   	 	bgMusic.play();
   	 	while (GAME_START || GAME_OVER == false) {
   	 	bgMusic.play();
   	 	}
   	 	bgMusic.stop();
    }
    
    //Put the candles to the appropriate positions
    public void initializeCandle(Sprite candle, Integer posA, Integer posB) {
    	//For the candles on top
		if (candle.getWidth() == 60) {
			candle.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
		}
		//For the candles on bottom
		else if (candle.getWidth() == 50) {
			//For the short candles
			if (candle.getHeight() == DEF.SHORT_CANDLE_HEIGHT) {
				posB = DEF.SCENE_HEIGHT - (185);
			}
			//For the middle candles
			else if (candle.getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
				posB = DEF.SCENE_HEIGHT - (215);
			}
			//For the long candles
			else {
				posB = DEF.SCENE_HEIGHT - (235);
			}
			candle.setPositionXY(posA, posB);
			candle.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
		}
		candle.render(gc);
		candles.add(candle);
	 }
    
    
    private void resetGameScene(boolean firstEntry) {
    	
    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        //Reset lists 
        floors = new ArrayList<>();
        ghosts = new ArrayList<>();
        pumpkins = new ArrayList<>();
        candles = new ArrayList<>();

    	if(firstEntry) {
    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            Canvas bcanvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            bgc = bcanvas.getGraphicsContext2D();
            
            Canvas ccanvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            cgc = canvas.getGraphicsContext2D();
            
            // create a background
            ImageView background = DEF.IMVIEW.get("backgroundLight");
            //ImageView background = changeBackground();
            
            //MediaPlayer backgroundMusic = DEF.backgroundMusicMP;
            
            
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, canvas, bcanvas, ccanvas);
    	}
    	
    	// initialize floor
    	for(int i=0; i<DEF.FLOOR_COUNT; i++) {
    		
    		int posX = i * DEF.FLOOR_WIDTH;
    		int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    		
    		Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor1"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);
    		
    		floors.add(floor);
    	}
    	

    	/*
    	// initialize candle
    	int posY;
    	Sprite candle;
    	for(int i=0; i<DEF.CANDLE_COUNT; i++) {
    		int posX = i * DEF.CANDLE_WIDTH;
    		if ((i % 2 == 0) || (i % 3 == 0)) {
    			posY = DEF.CANDLE_HEIGHT;
    		} else {
    			posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT - DEF.CANDLE_HEIGHT;
    		}
    		if (i % 7 == 0) {
    			candle = new Sprite(posX, posY, DEF.IMAGE.get("LongCandleBottom"));
    		}
    		candle.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		candle.render(gc);
    		candles.add(candle);
    	}
    	*/
    	
    	
    	
			
			/*
			candleDuos.add(candleA);
			//candleDuos.add(candleB);
			candleButtom.add(candleB);
    		for (int j=0;j<candleButtom.size();j++) {
    			if (candleButtom.get(j).getWidth() == 60) {
        			//candleDuos.get(j).setVelocity(DEF.SCENE_SHIFT_INCR, 0);
        			candleButtom.get(j).setVelocity(DEF.SCENE_SHIFT_INCR, 0);
        			
        		}
        		else if (candleButtom.get(j).getWidth() == 50) {
        			if (candleButtom.get(j).getHeight() == DEF.SHORT_CANDLE_HEIGHT) {
        				posB = DEF.SCENE_HEIGHT - (195);
        			}
        			else if (candleButtom.get(j).getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
        				posB = DEF.SCENE_HEIGHT - (225);
        			}
        			else {
        				posB = DEF.SCENE_HEIGHT - (245);
        			}
        			candleButtom.get(j).setPositionXY(posA, posB);
        			candleButtom.get(j).setVelocity(DEF.SCENE_SHIFT_INCR, 0);
        		}
        		candleButtom.get(j).render(cgc);
        		candles.add(candleButtom.get(j));
    		}
    		*/
    		/*
    		if (candle.getWidth() == 60) {
    			candle.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		}
    		else if (candle.getWidth() == 50) {
    			if (candle.getHeight() == DEF.SHORT_CANDLE_HEIGHT) {
    				posB = DEF.SCENE_HEIGHT - (195);
    			}
    			else if (candle.getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
    				posB = DEF.SCENE_HEIGHT - (225);
    			}
    			else {
    				posB = DEF.SCENE_HEIGHT - (245);
    			}
    			candle.setPositionXY(posA, posB);
    			candle.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		}
    		candle.render(cgc);	
    		candles.add(candle);
    		*/
    	
    	
    	// initialize candle
		for(int i=0; i<DEF.CANDLE_COUNT; i++) {
			//18 depends on preference, for difficulty it can be changed. It determines the distance of the candles
			int posX = i * (DEF.UP_CANDLE_WIDTH + 18);
			int posY = 0;
			//Create a random candle for top
			Sprite candleA = new Sprite(posX, posY, DEF.IMAGE.get(DEF.randomUpCandlePic()));
			//Create a random candle for bottom
			Sprite candleB = new Sprite(posX, posY, DEF.IMAGE.get(DEF.randomBottomCandlePic()));
			//Initialize the candles
			initializeCandle(candleA, posX, posY);
			initializeCandle(candleB, posX, posY);
		}
		//initialize blob
		blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("1-0"));
		blob.render(gc);
        // initialize ghosts
        for(int i=0; i<DEF.GHOST_COUNT; i++) {
        	Ghost ghost = new Ghost(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1, 0, 0, DEF.IMAGE.get("ghost"));
        	ghost.render(gc);
        	ghosts.add(ghost);
        }
        // initialize pumpkins
        for(int i=0; i<DEF.PUMPKIN_COUNT; i++) {
        	Pumpkin pumpkin = new Pumpkin(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1, 0, DEF.IMAGE.get("normalpumpkin"), "normal");
        	pumpkin.render(gc);
        	pumpkins.add(pumpkin);
        }  
        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();
    }

    //timer stuff
    class MyTimer extends AnimationTimer {
    	
    	int counter = 0;
    	
    	 @Override
    	 public void handle(long now) {   		 
    		 // time keeping
    	     elapsedTime = now - startTime;
    	     startTime = now;
    	     
    	     // clear current scene
    	     gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
    	     bgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
    	     cgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

    	     if (GAME_START) {
    	    	 // step1: update floor
    	    	 moveFloor();
    	    	 //update candle
    	    	 moveCandle();
    	    	 
    	    	 // step2: update blob
    	    	 moveBlob();
    	    	 //check collisions with floor and candles
    	    	 checkCollision();
    	    	 
    	    	 
    	    	 //control ghost
    	    	 controlGhost();
    	    	 
    	    	 //controlPumpkin();
    	    	 pumpkinOverCandle();
    	    	 //check pumpkin collection
    	    	 checkPumpkinCollect();
    	    	 
    	    	 //change background 
    	    	 //changeBackground();
    	    	 
    	    	 //Play background music
    	    	 //controlBackgroundMusic();
    	    	 
    	    	 
				DEF.backgroundMusicMP.setVolume(1);
				DEF.backgroundMusicMP.setAutoPlay(true);
    	    	 //while (GAME_OVER == false) {
					//DEF.backgroundMusicMP.setVolume(1);
					//DEF.backgroundMusicMP.setAutoPlay(true);
    	    	 //}
    	    	 //DEF.backgroundMusicMP.stop()
    	     }
    	 }
    	 
    	 
    	 
    	 // step1: update floor
    	 private void moveFloor() {
    		
    		for(int i=0; i<DEF.FLOOR_COUNT; i++) {
    			if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
    				double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
    	        	double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    	        	floors.get(i).setPositionXY(nextX, nextY);
    			}
    			floors.get(i).render(gc);
    			floors.get(i).update(DEF.SCENE_SHIFT_TIME);
    		}
    	 }
    	 
    	 
    	 double nextX;
  		 double nextY;
    	 //update candles
    	 private void moveCandle() {
     		for(int i=0; i<DEF.CANDLE_COUNT; i++) {
     			//If all the candles in the array are placed 
     			if (candles.get(i).getPositionX() <= -DEF.CANDLE_COUNT) {
     				//For the candles on top
     				if (candles.get(i).getWidth() == 60) {
     					//50 depends on preference, it can be changed according to the difficulty 
     					nextX = candles.get((i+ (DEF.CANDLE_COUNT - 1))%DEF.CANDLE_COUNT).getPositionX() + 50;
     					nextY = 0;
     	    		}
     				//For the candles on bottom
     	    		else if (candles.get(i).getWidth() == 50) {
     	    			//40 depends on preference, it can be changed according to the difficulty 
     	    			nextX = candles.get((i+ (DEF.CANDLE_COUNT - 1))%DEF.CANDLE_COUNT).getPositionX() + 40;
     	    			//For the short candles
     	    			if (candles.get(i).getHeight() == DEF.SHORT_CANDLE_HEIGHT) {
     	    				nextY = DEF.SCENE_HEIGHT - (185);
     	    			}
     	    			//For the middle candles
     	    			else if (candles.get(i).getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
     	    				nextY = DEF.SCENE_HEIGHT - (215);
     	    			}
     	    			//For the long candles
     	    			else {
     	    				nextY = DEF.SCENE_HEIGHT - (235);
     	    			}
     	    		}
     				//Update the position of the candle
    	        	candles.get(i).setPositionXY(nextX, nextY);
     			}
     			candles.get(i).render(gc);
     			candles.get(i).update(DEF.SCENE_SHIFT_TIME + 3);
     		}
     	 }
    	 
    	 // step2: update blob
    	 private void moveBlob() {
    		 if (auto == false) {
    			 regularFly();}
    		 
    	 }
    	 
    	 //Place the pumpkins over the candles
    	 public void pumpkinOverCandle() {
    		 Random rand = new Random();
    		 //int randomCandle = rand.nextInt(10);
    		 int totalPumpkin = 20;
    		 while (totalPumpkin > -1) {
    			 //Pick a random candle
    			 int randomCandle = rand.nextInt(DEF.CANDLE_COUNT);
    			 Sprite pickedCandle = candles.get(randomCandle);
    			 //System.out.print(pickedCandle.getWidth());
    			 //Check if the candle is on bottom
    			 if (pickedCandle.getWidth() == 50) {
    				//For the short candles
    				 if (pickedCandle.getHeight() == DEF.SHORT_CANDLE_HEIGHT) {
    					 controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (225));
    	    				//posB = DEF.SCENE_HEIGHT - (195);
	    			 }
    				//For the middle candles
	    			 else if (pickedCandle.getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
    	    			controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (255));
    	    				//posB = DEF.SCENE_HEIGHT - (225);
	    			 }
    				//For the long candles
	    			 else {
	    				 controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (275));
    	    				//posB = DEF.SCENE_HEIGHT - (245);
	    			 }
    				 //controlPumpkin(pickedCandle.getPositionX(), ((200 )));
    				 totalPumpkin = totalPumpkin - 1;
    			 }
    			 
    			 /*
    			 if (pickedCandle.getWidth() == DEF.UP_CANDLE_WIDTH) {
    				 controlPumpkin(pickedCandle.getPositionX(), pickedCandle.getPositionY() + 100);
    			 } else {
    			 */
    			 /*
    				 if (pickedCandle.getHeight() == DEF.SHORT_CANDLE_HEIGHT) {
    					 controlPumpkin(pickedCandle.getPositionX(), pickedCandle.getPositionY() + 100 + pickedCandle.getHeight());
  	    			}
  	    			else if (pickedCandle.getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
  	    				controlPumpkin(pickedCandle.getPositionX(), pickedCandle.getPositionY() + 100 + pickedCandle.getHeight());
  	    			}
  	    			else {
  	    				controlPumpkin(pickedCandle.getPositionX(), pickedCandle.getPositionY() + 100 + pickedCandle.getHeight());
  	    			}
    			 */
    			 
    			 //totalPumpkin = totalPumpkin - 1;
    		 }
    	 }
    	
    	 
    	 public void controlGhost() {
    		 Random rand = new Random();
    		 int makeGhostInt = rand.nextInt(400);
    		 boolean makeGhost = false;
    		 if (makeGhostInt == 1) {
    			 makeGhost = true;
    		 }
    		 int makeGhostInt2 = rand.nextInt(500);
    		 boolean makeGhost2 = false;
    		 if (makeGhostInt2 == 1) {
    			 makeGhost2 = true;
    		 }
    		 if (makeGhost) {
	    		 /**for (Sprite ghost:ghosts) {*/
	    			 if (ghosts.get(0).getPositionX()>DEF.SCENE_WIDTH && makeGhost) {
	        			 ghosts.get(0).setPositionXY(DEF.SCENE_WIDTH,  0-ghosts.get(0).getHeight());
	        			 ghosts.get(0).setVelocity(-30, DEF.GHOST_VEL1);
	        			 DEF.ghostSoundMP.setVolume(300);
	        			 DEF.ghostSoundMP.play();
	        			 makeGhost = false;
	        			 //break;
	        		 }
	    		 //}
    		 }
    		 if (makeGhost2) {
	    		 /**for (Sprite ghost:ghosts) {*/
	    			 if (ghosts.get(1).getPositionX()>DEF.SCENE_WIDTH && makeGhost2) {
	        			 ghosts.get(1).setPositionXY(DEF.SCENE_WIDTH,  0-ghosts.get(1).getHeight());
	        			 ghosts.get(1).setVelocity(-30, DEF.GHOST_VEL1);
	        			 DEF.ghostSoundMP.setVolume(200);
	        			 DEF.ghostSoundMP.play();
	        			 makeGhost2 = false;
	        			 //break;
	        		 }
	    		 //}
    		 }
    		 for(Sprite ghost :ghosts) {
    			 if (ghost.getPositionX()<0-ghost.getWidth() || ghost.getPositionY()<0-ghost.getHeight()) {
        			 ghost.setPositionXY(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1);
        			 ghost.setVelocity(0, 0);
        			 
    			 }
    			 ghost.update(elapsedTime * DEF.NANOSEC_TO_SEC);
        		 ghost.render(gc);
    		 }
    	 }
    	 
    	 public void controlPumpkin(double posX, double posY) {
    		 Random rand = new Random();
    		 int makePumpkinInt = rand.nextInt(300);
    		 boolean makePumpkin = false;
    		 if (makePumpkinInt == 1) {
    			 makePumpkin = true;
    		 }
    		 if (makePumpkin) {
    			 int makeGoldPumpkinInt = rand.nextInt(4);
    			 boolean makeGoldPumpkin = false;
        		 if (makeGoldPumpkinInt == 3) {
        			 makeGoldPumpkin = true;
        		 }
	    		 //for (Pumpkin pumpkin:pumpkins) {
	    			 if (pumpkins.get(0).getPositionX()>DEF.SCENE_WIDTH && makePumpkin) {
	        			 pumpkins.get(0).setPositionXY(posX, posY);
	        			 pumpkins.get(0).setVelocity(DEF.PUMPKIN_VEL, 0);
	        			 if(makeGoldPumpkin) {
	        				 pumpkins.get(0).makeGold();
	        				 pumpkins.get(0).setImage(DEF.IMAGE.get("goldpumpkin"));
	        			 }
	        			 makePumpkin = false;
	        			 //break;
	        		 }
	    		 //}
    		 }
    		 int makePumpkinInt2 = rand.nextInt(300);
    		 boolean makePumpkin2 = false;
    		 if (makePumpkinInt2 == 1) {
    			 makePumpkin2 = true;
    		 }
    		 if (makePumpkin2) {
    			 int makeGoldPumpkinInt2 = rand.nextInt(4);
    			 boolean makeGoldPumpkin2 = false;
        		 if (makeGoldPumpkinInt2 == 3) {
        			 makeGoldPumpkin2 = true;
        		 }
	    		 //for (Pumpkin pumpkin:pumpkins) {
	    			 if (pumpkins.get(1).getPositionX()>DEF.SCENE_WIDTH && makePumpkin) {
	        			 pumpkins.get(1).setPositionXY(posX, posY);
	        			 pumpkins.get(1).setVelocity(DEF.PUMPKIN_VEL, 0);
	        			 if(makeGoldPumpkin2) {
	        				 pumpkins.get(1).makeGold();
	        				 pumpkins.get(1).setImage(DEF.IMAGE.get("goldpumpkin"));
	        			 }
	        			 makePumpkin2 = false;
	        			 //break;
	        		 }
	    		 //}
    		 }
    		 for(Pumpkin pumpkin :pumpkins) {
    			 if (pumpkin.getPositionX()<0-pumpkin.getWidth() || pumpkin.getPositionY()<0-pumpkin.getHeight()) {
        			 pumpkin.setPositionXY(posX, posY);
        			 pumpkin.setVelocity(DEF.PUMPKIN_VEL, 0);
        			 pumpkin.makeNormal();
    			 }
    			 pumpkin.update(elapsedTime * DEF.NANOSEC_TO_SEC);
        		 pumpkin.render(gc);
    		 }

    	 }
    	 
    	 public void regularFly() {
    		 long diffTime = System.nanoTime() - clickTime;
    		 
     		
     		// blob flies upward with animation
     		if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {
     			
     			int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
     			imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
     			blob.setImage(DEF.IMAGE.get("1-"+String.valueOf(imageIndex)));
     			
     			//blob.setImage(DEF.IMAGE.get("blob"+String.valueOf(ranNum)));
     			
     			blob.setVelocity(0, DEF.BLOB_FLY_VEL);
     		}
     		
     		// blob drops after a period of time without button click
     		else {
     		    blob.setVelocity(0, DEF.BLOB_DROP_VEL); 
     		    if (diffTime> 562000000) {
     		    blob.setImage(DEF.IMAGE.get("1-f"));}
     		    CLICKED = false;
     		}

     		// render blob on GUI
     		blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
     		blob.render(gc);

    	 }
    	 
    	 
    	 public void checkCollision() {
    		// check collision  
    		// check if either floors were hit
    		// check collision to floor
			for (Sprite floor: floors) {
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
			}
			

			for (Sprite ghost: ghosts) {
				GAME_OVER = GAME_OVER || blob.intersectsSprite(ghost);
			}
			

			// check collision to candles
			for (Sprite candle: candles) {
				/*
				javafx.geometry.Rectangle2D a;
				a = candle.getCandleBoundary();
				System.out.print(a.getMaxX());
				System.out.print(a.getMaxY());
				System.out.print(a.getMinX());
				System.out.print(a.getMinY());
				*/
				GAME_OVER = GAME_OVER || blob.intersectsSprite(candle);
			}
			
			// end the game when blob hit stuff
			if (GAME_OVER) {
				showHitEffect(); 
				DEF.backgroundMusicMP.stop();
				for (Sprite floor: floors) {
					floor.setVelocity(0, 0);
				}
				timer.stop();
			}
			
    	 }
    	 
    	 public void checkPumpkinCollect() {
    		 
    		 for (Pumpkin pumpkin:pumpkins) {
    			 if (blob.intersectsSprite(pumpkin)) {
    				 DEF.witchLaughMP.setAutoPlay(true);
    				 System.out.println("pumpkin collected");
    				 if (pumpkin.getType().equals("normal")) {
    					 System.out.println("increase points");
    				 }
    				 else {
    					 System.out.println("autopilot");
    				 }
    				 pumpkin.setPositionXY(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1);
    				 pumpkin.makeNormal();
    				 pumpkin.setVelocity(0, 0);
    			 }
    			 for (Ghost ghost:ghosts) {
    				 if (ghost.intersectsSprite(pumpkin)) {
    					 ghost.stealPumpkin();
    					 pumpkin.isStolen(DEF.GHOST_VEL1);
    				 }
    			 }
    		 }
    		 
    	 }
    	 
	     private void showHitEffect() {
	        ParallelTransition parallelTransition = new ParallelTransition();
	        FadeTransition fadeTransition = new FadeTransition(Duration.seconds(DEF.TRANSITION_TIME), gameScene);
	        fadeTransition.setToValue(0);
	        fadeTransition.setCycleCount(DEF.TRANSITION_CYCLE);
	        fadeTransition.setAutoReverse(true);
	        parallelTransition.getChildren().add(fadeTransition);
	        parallelTransition.play();
	     }
    	 
    } // End of MyTimer class
    
    
    


} // End of AngryFlappyBird Class

