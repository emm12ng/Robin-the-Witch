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
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

import com.sun.media.jfxmedia.AudioClip;


import java.awt.TextArea;
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
    
    private ArrayList<Sprite> takenCandles;
    
    private int score;
    private int lives;
    
    private Text scoreText;
    private Text livesText;
    
    private boolean candleCollision = false;
    private boolean floorCollision = false;
    private boolean ghostCollision = false;
    private boolean decreaseScore = false;


    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER;


    private boolean PUMPKIN_COLLECTED = false;
    private boolean NORMAL_PUMPKIN_COLLECTED = false;

    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)
    private VBox difficultyControl;

    private GraphicsContext gc;
    private GraphicsContext bgc;
    private GraphicsContext cgc;
    private GraphicsContext tgc;


	


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

    	//Picking difficulty level by mouse clicking
    	DEF.easy.setOnMouseClicked(event -> {
    		System.out.print("Difficulty Level: Easy");
    	});
    	DEF.intermediate.setOnMouseClicked(event -> {
    		System.out.print("Difficulty Level: Intermediate");
    	});
    	DEF.hard.setOnMouseClicked(event -> {
    		System.out.print("Difficulty Level: Hard");
    	});
    	DEF.survival.setOnMouseClicked(event -> {
    		System.out.print("Difficulty Level: Survival");
    	});

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
            	DEF.backgroundMusicMP.pause();
                resetGameScene(false);

            }
        	else if (GAME_START){
                clickTime = System.nanoTime();
                DEF.backgroundMusicMP.setCycleCount(50);
                DEF.backgroundMusicMP.play();
                //DEF.backgroundMusicMP.setAutoPlay(true);

            }
        	GAME_START = true;
            CLICKED = true;

        	}
        );

        gameControl = new VBox();
        gameControl.getChildren().addAll( DEF.easy, DEF.intermediate, DEF.hard, DEF.survival, DEF.startButton);




    }




    //Control background music
    public void controlBackgroundMusic() {
    	MediaPlayer bgMusic = DEF.backgroundMusicMP;
    	bgMusic.setVolume(1);
   	 	bgMusic.play();
   	 	while (GAME_START || GAME_OVER == false) {
   	 	bgMusic.play();
   	 	}
   	 	bgMusic.pause();
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
    		
    		lives = 3;
            score = 0;
            
            scoreText = new Text();
            livesText = new Text();
            
    		// create two canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            Canvas bcanvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            bgc = bcanvas.getGraphicsContext2D();

            Canvas ccanvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            cgc = canvas.getGraphicsContext2D();
            
            Canvas tcanvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            tgc = canvas.getGraphicsContext2D();

            // create a background
            ImageView background = DEF.IMVIEW.get("backgroundLight");
            //ImageView background = changeBackground();

            //MediaPlayer backgroundMusic = DEF.backgroundMusicMP;


            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, canvas, bcanvas, ccanvas, tcanvas,  scoreText, livesText);
    	}
    	
    	scoreText.setX(20);
    	scoreText.setY(60);
    	livesText.setX(DEF.SCENE_WIDTH - 120);
    	livesText.setY(DEF.SCENE_HEIGHT - 20);
    	
    	scoreText.setFont(Font.font("Verdana", 50));
    	livesText.setFont(Font.font("Verdana", 20));
    	scoreText.setFill(Color.WHITE);
    	livesText.setFill(Color.RED);
    	
    	tgc.setLineWidth(3.0);
        tgc.setFill(Color.WHITE);
    	tgc.fillText(String.valueOf(score), 20, 60);
    	tgc.fillText(lives + " lives left", DEF.SCENE_WIDTH - 120, DEF.SCENE_HEIGHT - 20);

    	// initialize floor
    	for(int i=0; i<DEF.FLOOR_COUNT; i++) {

    		int posX = i * DEF.FLOOR_WIDTH;
    		int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;

    		Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor1"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);

    		floors.add(floor);
    	}


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
        	Pumpkin pumpkin = new Pumpkin(DEF.SCENE_WIDTH+30 + DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1, 0, DEF.IMAGE.get("normalpumpkin"), "normal");
        	pumpkin.render(gc);
        	pumpkins.add(pumpkin);
        }
        
        takenCandles = new ArrayList<>();
        
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

    	    	 controlPumpkin();
    	    	 //pumpkinOverCandle();
    	    	 //check pumpkin collection
    	    	 checkPumpkinCollect();
    	    	 
    	    	 checkNoLives();
    	    	 
    	    	 candleScore();
    	    	 scoreText.setText(String.valueOf(score));
    	    	 livesText.setText(lives + " lives left");

    	    	 //change background
    	    	 //changeBackground();


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
     			candles.get(i).update(DEF.SCENE_SHIFT_TIME);
     		}
     	 }

    	 // step2: update blob
    	 private void moveBlob() {
    		 if (auto == false) {
    			 regularFly();}

    	 }

/*
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
    					 controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (228));
    	    				//posB = DEF.SCENE_HEIGHT - (195);
	    			 }
    				//For the middle candles
	    			 else if (pickedCandle.getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
    	    			controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (258));
    	    				//posB = DEF.SCENE_HEIGHT - (225);
	    			 }
    				//For the long candles
	    			 else {
	    				 controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (278));
    	    				//posB = DEF.SCENE_HEIGHT - (245);
	    			 }
    				 //controlPumpkin(pickedCandle.getPositionX(), ((200 )));
    				 totalPumpkin = totalPumpkin - 1;
    			 }
    		 }
    	}*/



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
    		 if (makeGhost || makeGhost2) {
    			 DEF.ghostSoundMP.setVolume(5000);
    			 DEF.ghostSoundMP.stop();
    			 DEF.ghostSoundMP.play();
    		 }
    		 if (makeGhost) {
	    		 /**for (Sprite ghost:ghosts) {*/
	    			 if (ghosts.get(0).getPositionX()>DEF.SCENE_WIDTH && makeGhost) {
	        			 ghosts.get(0).setPositionXY(DEF.SCENE_WIDTH,  0-ghosts.get(0).getHeight());
	        			 ghosts.get(0).setVelocity(-30, DEF.GHOST_VEL1);
	        			 //DEF.ghostSoundMP.setVolume(300);
	        			 //DEF.ghostSoundMP.play();
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
	        			 //DEF.ghostSoundMP.setVolume(300);
	        			 //DEF.ghostSoundMP.play();
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
    	 
    	 public void controlPumpkin() {
    		 Random rand = new Random();
    	     int makePumpkinInt = rand.nextInt(100);
    	     boolean makePumpkin = false;
    	     if (makePumpkinInt == 1) {
    	        makePumpkin = true;
    	     }
    	     if (makePumpkin) {
    	    	 int makeGoldPumpkinInt = rand.nextInt(3);
    	    	 boolean makeGoldPumpkin = false;
    	    	 if (makeGoldPumpkinInt == 1) {
    	            makeGoldPumpkin = true;
    	            System.out.println("make gold pumpkin");
    	         }
    	         if (pumpkins.get(0).getPositionX()>DEF.SCENE_WIDTH && makePumpkin) {
    	        	 pumpkins.get(0).setVelocity(-50, 0);
    	        	 pumpkins.get(0).setPositionXY(DEF.SCENE_WIDTH, 300);//DEF.SCENE_HEIGHT - (candle.getHeight() + DEF.FLOOR_HEIGHT));
    	        	 if(makeGoldPumpkin) {
    	        		 pumpkins.get(0).makeGold();
    	        		 pumpkins.get(0).setImage(DEF.IMAGE.get("goldpumpkin"));
    	        	 }
    	         }
    	         System.out.println("made pumpkin 1");
    	         makePumpkin = false;
    	         makeGoldPumpkin = false;
    	     }
    	     int makePumpkinInt2 = rand.nextInt(100);
    	     boolean makePumpkin2 = false;
    	     if (makePumpkinInt2 == 1) {
    	    	 makePumpkin2 = true;
    	       	}
    	     if (makePumpkin2) {
    	    	 int makeGoldPumpkinInt2 = rand.nextInt(3);
    	    	 boolean makeGoldPumpkin2 = false;
    	         if (makeGoldPumpkinInt2 == 1) {
    	        	 makeGoldPumpkin2 = true;
    	        	 System.out.println("make gold pumpkin");
    	         }
    	         if (pumpkins.get(1).getPositionX()>DEF.SCENE_WIDTH && makePumpkin) {
    	        	 pumpkins.get(1).setVelocity(-50, 0);
    	        	 pumpkins.get(1).setPositionXY(DEF.SCENE_WIDTH, 300);
    	        	 if(makeGoldPumpkin2) {
    	        		 pumpkins.get(1).makeGold();
    	        		 pumpkins.get(1).setImage(DEF.IMAGE.get("goldpumpkin"));
    	        	 }
    	        }
    	        System.out.println("made pumpkin 2");
    	        makePumpkin2 = false;
	        	makeGoldPumpkin2 = false;
    	     }
    	     for(Pumpkin pumpkin :pumpkins) {
    	    	 if (pumpkin.getPositionX()<0-pumpkin.getWidth()) {
    	    		 pumpkin.setPositionXY(DEF.SCENE_WIDTH+2+DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1);
    	    		 pumpkin.setVelocity(0, 0);
    	    		 pumpkin.makeNormal();
    	    	 }
    	    	 pumpkin.update(elapsedTime * DEF.NANOSEC_TO_SEC);
    	         pumpkin.render(gc);
    	     }

    	 }
    	 
    	 public void candleScore() {
    		 for (Sprite candle:candles) {
    			 if (candle.getWidth() == 50) {
    				 if (candle.getPositionX() == (blob.getPositionX())) {
    					 score = score + 1;
    				 }
    			 }
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
				if (blob.intersectsSprite(floor)) {
					//lives = 3;
					//score = 0;
					floorCollision = true;
				}
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
			}
			if (floorCollision) {
				lives = 3;
				score = 0;
				floorCollision = false;
			}


			for (Sprite ghost: ghosts) {
				if (blob.intersectsSprite(ghost)) {
					//lives = 3;
					//score = 0;
					ghostCollision = true;
				}
				GAME_OVER = GAME_OVER || blob.intersectsSprite(ghost);
			}
			if (ghostCollision) {
				lives = 3;
				score = 0;
				ghostCollision = false;
			}


			// check collision to candles
			for (Sprite candle: candles) {
				if (blob.intersectsSprite(candle)) {
					//lives = lives - 1;
					candleCollision = true;
				}
				GAME_OVER = GAME_OVER || blob.intersectsSprite(candle);
			}
			if (candleCollision) {
				lives = lives - 1;
				candleCollision = false;
			}

			// end the game when blob hit stuff
			if (GAME_OVER) {
				showHitEffect();
				DEF.backgroundMusicMP.pause();
				for (Sprite floor: floors) {
					floor.setVelocity(0, 0);
				}
				timer.stop();
			}

    	 }
    	 
    	 public void checkNoLives() {
    		 if (lives < 0) {
    			 lives = 3;
    			 score = 0;
    		 }
    	 }

    	 public void checkPumpkinCollect() {
    		 DEF.witchLaughMP.setCycleCount(1);
    		 for (Pumpkin pumpkin:pumpkins) {
    			 if (blob.intersectsSprite(pumpkin)) {
    				 PUMPKIN_COLLECTED = true;
    				 System.out.println("pumpkin collected");
    				 if (pumpkin.getType().equals("normal")) {
        				 NORMAL_PUMPKIN_COLLECTED = true;
        				 score = score + 5;
        				 
    				 }
    				 else {
    					 System.out.println("autopilot");
    					 //DEF.witchLaughMP.play();
    				 }
    				 //DEF.witchLaughMP.setCycleCount(DEF.witchLaughMP.getCycleCount() + 1);
    				 pumpkin.setPositionXY(DEF.SCENE_WIDTH+2 + DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1);
    				 pumpkin.setVelocity(0, 0);
    	    		 pumpkin.makeNormal();
    	    		 pumpkin.setImage(DEF.IMAGE.get("normalpumpkin"));
    			 }
    			 for (Ghost ghost:ghosts) {
    				 if (ghost.intersectsSprite(pumpkin)) {
    					 pumpkin.setPositionXY(DEF.SCENE_WIDTH+2+DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1);
    					 pumpkin.setVelocity(0, 0);
        	    		 pumpkin.makeNormal();
        	    		 pumpkin.setImage(DEF.IMAGE.get("normalpumpkin"));
    					 decreaseScore = true;
    					 System.out.println("pumpkin stolen");
    				 }
    			 }
    		 }
    		 if (decreaseScore) {
				 score = score - 3;
				 decreaseScore = false;
			 }
    		 if (PUMPKIN_COLLECTED) {
    			 DEF.witchLaughMP.stop();
    			 DEF.witchLaughMP.play();
    			 PUMPKIN_COLLECTED = false;
    			 /*if (NORMAL_PUMPKIN_COLLECTED) {
        			 score = score + 5;
    			 }*/
    		 }

    		 //!!! If game stops for any reason stop the laugh!!!

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





}	// End of AngryFlappyBird Class