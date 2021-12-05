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
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
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
    private long clickTime, startTime, elapsedTime, goldPumpkinCollectTime;
    private AnimationTimer timer;

    // game components
    private Sprite blob;
    private ArrayList<Sprite> floors;

    private ArrayList<Ghost> ghosts;
    private ArrayList<Pumpkin> pumpkins;
    private ArrayList<Sprite> pumpkins2;
    private ArrayList<Sprite> candles;
    private ArrayList<Sprite> candlesSurvival;
    
    private int score;
    private int lives;
    
    private Text scoreText;
    private Text livesText;
    
    private Text gameOverSlogen;
    private Text startSlogen;
    
    private boolean candleCollision = false;
    private boolean floorCollision = false;
    private boolean ghostCollision = false;
    private boolean decreaseScore = false;
    
    private boolean changeBackground = false;
    private boolean backgroundChangeAvailable;
    private ImageView background;
    private String currBackground;

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER, RESTART, CANDLESCOLL;


    private boolean PUMPKIN_COLLECTED = false;

    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)

    private GraphicsContext gc;
    private GraphicsContext bgc;
    private GraphicsContext cgc;
    private GraphicsContext tgc;
	
    
    private String pickedDifficulty = "";
    private boolean survivalMode = false;
    private boolean easyMode = false;
    private boolean intermediateMode = false;
    private boolean hardMode = false;

    // canvas for background


    //the status of the auto-pliot mode
    private boolean auto = false;


	// the mandatory main method
    public static void main(String[] args) {
        launch(args);
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
		candlesSurvival.add(candle);
	 }
    
    int survivalModeCandlesEntry = -1;
    private void survivalModeCandles() {
    	survivalModeCandlesEntry += 1;
    	// initialize candle
    	if (survivalModeCandlesEntry == 0) {
    		candlesSurvival.clear();
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
    	}
    	//
		
    }
    


    // the start method sets the Stage layer
    @Override
    public void start(Stage primaryStage) throws Exception {

    	// initialize scene graphs and UIs
        resetGameControl();    // resets the gameControl
    	resetGameScene(true);  // resets the gameScene
    	

        HBox root = new HBox();
		HBox.setMargin(root, new Insets(0,0,0,15));
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
    		pickedDifficulty = "easy";
    		easyMode = true;
    		intermediateMode = false;
    		hardMode = false;
    		survivalMode = false;
    		
    	});
    	DEF.intermediate.setOnMouseClicked(event -> {
    		System.out.print("Difficulty Level: Intermediate");
    		pickedDifficulty = "intermediate";
    		intermediateMode = true;
    		easyMode = false;
    		hardMode = false;
    		survivalMode = false;
    	});
    	DEF.hard.setOnMouseClicked(event -> {
    		System.out.print("Difficulty Level: Hard");
    		pickedDifficulty = "hard";
    		hardMode = true;
    		easyMode = false;
    		intermediateMode = false;
    		survivalMode = false;
    	});
    	DEF.survival.setOnMouseClicked(event -> {
    		System.out.print("Difficulty Level: Survival");
    		pickedDifficulty = "survival";
    		survivalMode = true;
    		easyMode = false;
    		intermediateMode = false;
    		hardMode = false;
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
            	gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    bgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    cgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    survivalModeCandlesEntry = -1;
           	    //difficultiesCandlesEntry = -1;
            	//sceneStatus = "gameOver";
           	    CANDLESCOLL = false;
           	    timer.stop();
            	DEF.backgroundMusicMP.pause();
                resetGameScene(false);
                //timer.stop();
                GAME_OVER = false;
                RESTART = true;
            }
            else if (RESTART) {
            	gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    bgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    cgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    
           	    CANDLESCOLL = false;
        	    
            	timer.stop();
            	survivalModeCandlesEntry = -1;
            	//lives = 3;
            	score = 0;
            	 gameOverSlogen.setText("");
            	resetGameScene(false);
            }
        	else if (GAME_START){
        		//sceneStatus = "play";
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
        gameControl.getChildren().addAll( DEF.easy, DEF.intermediate, DEF.hard, DEF.survival, DEF.startButton, DEF.normalPumpkinInstruct, DEF.goldPumpkinInstruct, DEF.ghostInstruct);


    }





    private void resetGameScene(boolean firstEntry) {

    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        RESTART = false;
        //Reset lists
        floors = new ArrayList<>();
        ghosts = new ArrayList<>();
        pumpkins = new ArrayList<>();
        candles = new ArrayList<>();
        candlesSurvival = new ArrayList<>();
        pumpkins2 = new ArrayList<>();
        //CANDLESCOLL = false;

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
            background = DEF.IMVIEW.get("backgroundLight");
            currBackground = "backgroundA";
            //ImageView background = changeBackground();
            
            gameOverSlogen = new Text();
            startSlogen = new Text();
            
   
            
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, gameOverSlogen, startSlogen, canvas, ccanvas, tcanvas,  scoreText, livesText);
            
     
    	}
    	
    	startSlogen.setX(75);
        startSlogen.setY(260);
        startSlogen.setFont(Font.font("Verdana", 20));
        startSlogen.setFill(Color.WHITE);
        
        gameOverSlogen.setX(60);
    	gameOverSlogen.setY(200);
    	gameOverSlogen.setFont(Font.font("Verdana", 50));
    	gameOverSlogen.setFill(Color.WHITE);
    	
    	scoreText.setX(20);
    	scoreText.setY(60);
    	livesText.setX(DEF.SCENE_WIDTH - 120);
    	livesText.setY(DEF.SCENE_HEIGHT - 20);
    	
    	scoreText.setFont(Font.font("Verdana", 50));
    	livesText.setFont(Font.font("Verdana", 20));
    	scoreText.setFill(Color.WHITE);
    	livesText.setFill(Color.RED);
    	
    	
    	// initialize floor
    	for(int i=0; i<DEF.FLOOR_COUNT; i++) {

    		int posX = i * DEF.FLOOR_WIDTH;
    		int posY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;

    		Sprite floor = new Sprite(posX, posY, DEF.IMAGE.get("floor1"));
    		floor.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    		floor.render(gc);

    		floors.add(floor);
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
        	Pumpkin pumpkin = new Pumpkin(DEF.SCENE_WIDTH+2 + DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1, 0, DEF.IMAGE.get("normalpumpkin"), "normal");
        	pumpkin.render(gc);
        	pumpkins.add(pumpkin);
        }
        
        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();
        

    	//initialize candle
    	for(int i=0; i<DEF.CANDLE_COUNT; i++) {
    		Sprite candle = new Sprite(0, 0, DEF.IMAGE.get("ShortCandleUp"));
    		candlesSurvival.add(candle);
    	}
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
    	    	 startSlogen.setText("");
    	    	 gameOverSlogen.setText("");
    	    	 if (survivalMode) {
    	    		 survivalModeCandles();
	    	    	 // step2: update blob
	    	    	 moveBlob();
	    	    	 candleScore(pickedDifficulty);
    	    		// step1: update floor
	    	    	 moveFloor();
	    	    	 //update candle
	    	    	 moveCandle();
	    	    	 //control ghost
	    	    	 controlGhost();
	    	    	 //controlPumpkin();
	    	    	 pumpkinOverCandle();
	    	    	 //check pumpkin collection
	    	    	 checkPumpkinCollectSurvival();
	    	    	 checkNoLives();
	    	    	 scoreText.setText(String.valueOf(score));
	    	    	 livesText.setText(lives + " lives left");
	
	    	    	 //change background
	    	    	 changeBackground();
    	    	 } else {
    	    		 int posA;
    	    	     int posB;
    	    	     int posC =  DEF.SCENE_HEIGHT - (60);
    	    	     int random;
    	    		 DEF.resizeImage("normalpumpkin", 90, 90);
    	 			 DEF.resizeImage("goldpumpkin", 90, 90);
    	 			for(int i=0; i<DEF.CANDLE_COUNT; i++) {
    	 	    		random = (int)(Math.random() * 200) + 100; // 100~380
    	 	    		
    	 		    		
    	 		    	posA =i* DEF.CANDLES_SPLIT+DEF.CANDLES_START;
    	 				posB = -(random+80); //70 is the difficulty
    	 				Sprite candleDown = new Sprite(posA, posB,DEF.IMAGE.get("CandlesLong"));
    	 				
    	 				Sprite candleUp = new Sprite(posA, posB, DEF.IMAGE.get("CandlesLongUp"));
    	 				
    	 				Sprite candleBot = new Sprite(posA-16, posC, DEF.IMAGE.get("CandleBottom"));
    	 				Sprite normalPumpkin = new Sprite(posA-36, posC-100, DEF.IMAGE.get("normalpumpkin"));
    	 				Sprite goldPumpkin = new Sprite(posA-36, posC-100, DEF.IMAGE.get("goldpumpkin"));
    	 				pumpkins.clear();
    	 				
    	 				
    	 				
    	 				candleUp.setPositionXY(posA, posB);
    	 				candleUp.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 				
    	 				posB = DEF.SCENE_HEIGHT - (random); 
    	 				candleDown.setPositionXY(posA, posB);
    	 				candleDown.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 				
    	 				candleBot.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 			
    	 				
    	 				
    	 				if (random %3 == 0 && random %6 !=0) {
    	 					normalPumpkin.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 					normalPumpkin.render(cgc);
    	 					
    	 					pumpkins2.add(normalPumpkin);
    	 					
    	 				}
    	 				else if  (random %6 ==0)
    	 				{	goldPumpkin.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 					goldPumpkin.render(cgc);
    	 					pumpkins2.add(goldPumpkin);}
    	 				else {
    	 					candleDown.render(cgc);
    	 					candles.add(candleDown);
    	 				}
    	 				
    	 				candleUp.render(cgc);
    	 				candleBot.render(cgc);
    	 				
    	 				candles.add(candleUp);
    	 				
    	 				
    	 				candles.add(candleBot);
    	 	    	}
    	 			
    	    		 candleScore(pickedDifficulty);
    	    		 checkNoLives();
	
	    	    	 movePumpkin();
	    	    	 
	
	    	    	 //controlPumpkin();
	    	    	 //pumpkinOverCandle();
	    	    	 //check pumpkin collection
	    	    	 checkPumpkinCollect();
	    	    	 
	    	    	 scoreText.setText(String.valueOf(score));
	    	    	 livesText.setText(lives + " lives left");
	
    	    		 if(easyMode) {
    	    			 moveFloor();
    	    	    	 //update candle
    	    	    	 moveCandle("easy");
    	
    	    	    	 // step2: update blob
    	    	    	 moveBlob();
    	    	    	 
    	    	    	//control ghost
    	    	    	 controlGhost();
    	    		 }
    	    		 else if(intermediateMode) {
    	    			// step1: update floor
    	    	    	 moveFloor();
    	    	    	 //update candle
    	    	    	 moveCandle("intermediate");
    	
    	    	    	 // step2: update blob
    	    	    	 moveBlob();
    	    	    	 
    	    	    	//control ghost
    	    	    	 controlGhost();
    	    		 }
    	    		 else if(hardMode) {
    	    			// step1: update floor
    	    	    	 moveFloor();
    	    	    	 //update candle
    	    	    	 moveCandle("hard");
    	
    	    	    	 // step2: update blob
    	    	    	 moveBlob();
    	    	    	 
    	    	    	//control ghost
    	    	    	 controlGhost();
    	    		 }

	    	    	 
	    	    	 
	    	    	 //check collisions with floor and candles
	    	    	 //checkCollision();
	
	    	    	 
	    	    	
	    	    	 //change background
	    	    	 //changeBackground();
    	    	 }

    	     }
    	     else {
    	     startSlogen.setText("Press SPACE or the 'Go!'"+"\n"+"       Button to Start.");
    	     }
    	 }
    	 
    	 private void changeBackground() {
    		 if (score > 0 && score%20 == 0 && backgroundChangeAvailable) {
    			 changeBackground = true;
    		 }
    		 if (changeBackground) {
    			 if (currBackground.equals("backgroundA")) {
    				 background = DEF.IMVIEW.get("background1");
    				 currBackground = "backgroundB";
    			 }
    			 else {
    				 background = DEF.IMVIEW.get("backgroundLight");
    				 currBackground = "backgroundA";
    			 }
    			 gameScene.getChildren().remove(background);
    			 gameScene.getChildren().add(background);
    			 gameScene.getChildren().get(gameScene.getChildren().size()-1).toBack();
    			 changeBackground = false;
    			 backgroundChangeAvailable = false;
    		 }
    		 if (score%20 == 1) {
    			 backgroundChangeAvailable = true;
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
     			if (candlesSurvival.get(i).getPositionX() <= -DEF.CANDLE_COUNT) {
     				//For the candles on top
     				if (candlesSurvival.get(i).getWidth() == 60) {
     					//50 depends on preference, it can be changed according to the difficulty
     					nextX = candlesSurvival.get((i+ (DEF.CANDLE_COUNT - 1))%DEF.CANDLE_COUNT).getPositionX() + 50;
     					nextY = 0;
     	    		}
     				//For the candles on bottom
     	    		else if (candlesSurvival.get(i).getWidth() == 50) {
     	    			//40 depends on preference, it can be changed according to the difficulty
     	    			nextX = candlesSurvival.get((i+ (DEF.CANDLE_COUNT - 1))%DEF.CANDLE_COUNT).getPositionX() + 40;
     	    			//For the short candles
     	    			if (candlesSurvival.get(i).getHeight() == DEF.SHORT_CANDLE_HEIGHT) {
     	    				nextY = DEF.SCENE_HEIGHT - (185);
     	    			}
     	    			//For the middle candles
     	    			else if (candlesSurvival.get(i).getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
     	    				nextY = DEF.SCENE_HEIGHT - (215);
     	    			}
     	    			//For the long candles
     	    			else {
     	    				nextY = DEF.SCENE_HEIGHT - (235);
     	    			}
     	    		}
     				//Update the position of the candle
    	        	candlesSurvival.get(i).setPositionXY(nextX, nextY);
     			}
     			candlesSurvival.get(i).render(gc);
     			candlesSurvival.get(i).update(DEF.SCENE_SHIFT_TIME);
     		}
     	 }
    	 
    	 private void moveCandle(String difficultyLevel) {
      		
      		for(int i=0; i<DEF.CANDLE_COUNT; i++) {
      			if (candles.get(i).getPositionX() <= -500) {
     				double nextX = candles.get((i+1)%DEF.CANDLE_COUNT).getPositionX() + DEF.UP_CANDLE_WIDTH;
     	        	double nextY = DEF.SHORT_CANDLE_HEIGHT;
     	        	candles.get(i).setPositionXY(nextX, nextY);
      			}
      			candles.get(i).render(cgc);
      			if (difficultyLevel == "easy") {
      				candles.get(i).update(DEF.SCENE_SHIFT_TIME + 3);
      			}
      			else if(difficultyLevel == "intermediate") {
      				candles.get(i).update(DEF.SCENE_SHIFT_TIME + 7);
      			}
      			else {
      				candles.get(i).update(DEF.SCENE_SHIFT_TIME + 11);
      			}
      		}
      	 }
    	 
    	 
    	 
    	 private void movePumpkin() {
       		
       		for(int i=0; i<DEF.PUMPKIN_COUNT; i++) {
       			
       			pumpkins2.get(i).render(cgc);
       			pumpkins2.get(i).update(DEF.SCENE_SHIFT_TIME);
       		}
       	 }

    	 // step2: update blob
    	 private void moveBlob() {
    		 if (auto == false) {
    			 regularFly();
    			 //checkCollision();
    			 }
    		 
    		 else {
    			 //while(auto == true) {
    				 autoPilot();
    			 //}
    		 }
    		 

    	 }


    	 //Place the pumpkins over the candles
    	 public void pumpkinOverCandle() {
    		DEF.resizeImage("normalpumpkin", DEF.PUMPKIN_WIDTH, DEF.PUMPKIN_HEIGHT);
 			DEF.resizeImage("goldpumpkin", DEF.PUMPKIN_WIDTH, DEF.PUMPKIN_HEIGHT);
    		 Random rand = new Random();
    		 //int randomCandle = rand.nextInt(10);
    		 int totalPumpkin = 1;
    		 while (totalPumpkin > -1) {
    			 //Pick a random candle
    			 int randomCandle = rand.nextInt(DEF.CANDLE_COUNT);
    			 Sprite pickedCandle = candlesSurvival.get(randomCandle);
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
	    			 else if (pickedCandle.getHeight() == DEF.LONG_CANDLE_HEIGHT){
	    				 controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (278));
    	    				//posB = DEF.SCENE_HEIGHT - (245);
	    			 }
    				 //controlPumpkin(pickedCandle.getPositionX(), ((200 )));
    				 totalPumpkin = totalPumpkin - 1;
    			 }
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
	        			 makeGoldPumpkin = false;
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
	        			 makeGoldPumpkin2 = false;
	        			 //break;
	        		 }
	    		 //}
    		 }
    		 for(Pumpkin pumpkin :pumpkins) {
    			 if (pumpkin.getPositionX()<0-pumpkin.getWidth() || pumpkin.getPositionY()<0-pumpkin.getHeight()) {
        			 pumpkin.setPositionXY(posX, posY);
        			 pumpkin.setVelocity(DEF.PUMPKIN_VEL, 0);
        			 pumpkin.makeNormal();
        			 pumpkin.setImage(DEF.IMAGE.get("normalpumpkin"));
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
     		checkCollision(pickedDifficulty);
     		checkGhostCollectPumpkin();

    	 }
    	 
    	 public void autoPilot() {
    		 //long diffTime = System.nanoTime() - clickTime;
    		 
    		 //CLICKED = true;
    		 double ongoingTime = System.nanoTime() * DEF.NANOSEC_TO_SEC;
    		 if (ongoingTime < (goldPumpkinCollectTime * DEF.NANOSEC_TO_SEC + 6)) {
    			 ongoingTime = System.nanoTime() * DEF.NANOSEC_TO_SEC;
    			 System.out.println("This is autopilot loop");
    			 //clickTime = System.nanoTime();
    			 //CLICKED = true;
    			 //DEF.startButton.fire();
    			 
        		 blob.setImage(DEF.IMAGE.get("1-2"));
        		 blob.setVelocity(0, 0);
        		 if (survivalMode) {
        			 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y + 105);
        		 }
        		 else {
        			 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y + 200);
        		 }
        		 
        		 //moveFloor();
        		 //moveCandle();
        		 //blob.render(gc);
        		 /*
        		 long passedTime = System.nanoTime() - goldPumpkinCollectTime;
        		 if (passedTime *  DEF.NANOSEC_TO_SEC > 6) {
        			 CLICKED = false;
        		 }
        		 */
    		 }
    		 else {
    			 System.out.println("This is outside of autopilot loop");
    			 if (survivalMode) {
    			 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y + 15);
    			 }
    			 else {
    				 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y);
    			 }
          		 auto = false;
          		 //regularFly();
    		 }
    		 //CLICKED = false;
    		 //blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
    		 //System.out.println("This is outside of autopilot loop");
    		 blob.render(gc);
    		 blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
      		 //System.out.println(auto);
    	 }


    	 public void checkCollision(String difficultyLevel) {
    		// check collision
    		// check if either floors were hit
    		// check collision to floor
			for (Sprite floor: floors) {
				if (blob.intersectsSprite(floor)) {
					//lives = 3;
					//score = 0;
					//blob.setPositionXY(blob.getPositionX(), blob.getPositionY());
//					blob.setVelocity(-5, 5);
//					blob.update(DEF.TRANSITION_TIME);
					floorCollision = true;
				}
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
			}
			if (floorCollision) {
				//lives = 3;
				score = 0;
				
				floorCollision = false;
				
			}


			for (Sprite ghost: ghosts) {
				if (blob.intersectsSprite(ghost)) {
					//lives = 3;
					//score = 0;
					//blob.setPositionXY(blob.getPositionX() - 10, blob.getPositionY());
					//showHitEffect();
					//blob.setPositionXY(blob.getPositionX() + 1, blob.getPositionY());
					//showHitEffect();
					//blob.setPositionXY(blob.getPositionX() - 1, blob.getPositionY());
//					blob.setVelocity(-5, 5);
//					blob.update(DEF.TRANSITION_TIME);
					ghostCollision = true;
					//showCollEffect();
				}
				GAME_OVER = GAME_OVER || blob.intersectsSprite(ghost);
			}
			if (ghostCollision) {
				//lives = 3;
				score = 0;
				
				//blob.setVelocity(-5, 0);
				
				ghostCollision = false;
			}

			if (difficultyLevel == "survival") {
				for (Sprite candle: candlesSurvival) {
					if (blob.intersectsSprite(candle)) {
						//lives = lives - 1;
//						blob.setVelocity(-5, 5);
//						blob.setPositionXY(blob.getPositionX() - 10, blob.getPositionY());
//						blob.update(DEF.TRANSITION_TIME);
						candleCollision = true;
						//showCollEffect();
					}
					GAME_OVER = GAME_OVER || blob.intersectsSprite(candle);
					CANDLESCOLL = GAME_OVER || blob.intersectsSprite(candle);
					
				}
			}else {

			// check collision to candles
			for (Sprite candle: candles) {
				if (blob.intersectsSprite(candle)) {
					//lives = lives - 1;
//					blob.setVelocity(-5, 5);
//					blob.setPositionXY(blob.getPositionX() - 10, blob.getPositionY());
//					blob.update(DEF.TRANSITION_TIME);
					candleCollision = true;
					//showCollEffect();
				}
				GAME_OVER = GAME_OVER || blob.intersectsSprite(candle);
				CANDLESCOLL = GAME_OVER || blob.intersectsSprite(candle);
				
				
			}
			}
			
			if (candleCollision) {
				lives = lives - 1;
				
				
				candleCollision = false;
			}

			// end the game when blob hit stuff
			if (GAME_OVER) {
				survivalModeCandlesEntry = -1;
				showCollEffect();
				//showHitEffect();
				DEF.backgroundMusicMP.pause();
				blob.setVelocity(0,0);
				for (Sprite floor: floors) {
					floor.setVelocity(0, 0);
				}
				
				
			}

    	 }
    	 
    	 

    	 public void checkPumpkinCollectSurvival() {
    		 DEF.witchLaughMP.setCycleCount(1);
    		 for (Pumpkin pumpkin:pumpkins) {
    			 if (blob.intersectsSprite(pumpkin)) {
    				 PUMPKIN_COLLECTED = true;
    				 //System.out.println("pumpkin collected");
    				 if (pumpkin.getType().equals("normal")) {
        				 score = score + 5;
    				 }
    				 else {
    					 //System.out.println("autopilot");
    					 auto = true;
    					 goldPumpkinCollectTime = System.nanoTime();
    					// System.out.print("goldPumpkinCollectTime");
    					 //System.out.println(auto);
    					 //DEF.witchLaughMP.play();
    				 }
    				 //DEF.witchLaughMP.setCycleCount(DEF.witchLaughMP.getCycleCount() + 1);
    				 
    				 pumpkin.setPositionXY(DEF.SCENE_WIDTH+2 + DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1);
    				 //pumpkin.makeNormal();
    				 //pumpkin.setImage(DEF.IMAGE.get("normalpumpkin"));
    				 pumpkin.setVelocity(0, 0);
    				
    			 }
    			 
    		 if (PUMPKIN_COLLECTED) {
    			 DEF.witchLaughMP.stop();
    			 DEF.witchLaughMP.play();
    			 PUMPKIN_COLLECTED = false;
    			 /*
    			 if (NORMAL_PUMPKIN_COLLECTED) {
        			 score = score + 5;
        			 NORMAL_PUMPKIN_COLLECTED = false;
    			 }
    			 */
    		 }
		 }
    		 //!!! If game stops for any reason stop the laugh!!!

    	 }
    	 
    	 public void checkPumpkinCollect() {
    		 DEF.witchLaughMP.setCycleCount(1);
    		 for (Sprite pumpkin:pumpkins2) {
			 if (blob.intersectsSprite(pumpkin)) {
				 PUMPKIN_COLLECTED = true;
				 System.out.println("pumpkin collected");
				 System.out.println(pumpkin.isGold());
				 if (pumpkin.isGold()) {
					 System.out.println("autopilot");
					 goldPumpkinCollectTime = System.nanoTime();
					 auto = true;
					 
				 }
				 else {
					 System.out.println("increase points");
				 }
				 pumpkin.setPositionXY(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1);
				 
				 pumpkin.setVelocity(0, 0);
			 }
			 for (Ghost ghost:ghosts) {
				 if (ghost.intersectsSprite(pumpkin)) {
					 ghost.stealPumpkin();
					 
				 }
			 }
    			 
    		 if (PUMPKIN_COLLECTED) {
    			 DEF.witchLaughMP.stop();
    			 DEF.witchLaughMP.play();
    			 PUMPKIN_COLLECTED = false;
    			 /*
    			 if (NORMAL_PUMPKIN_COLLECTED) {
        			 score = score + 5;
        			 NORMAL_PUMPKIN_COLLECTED = false;
    			 }
    			 */
    		 }
		 }
    		 //!!! If game stops for any reason stop the laugh!!!

    	 }
    	 
    	 public void candleScore(String difficultyLevel) {
    		 if (difficultyLevel == "survival") {
    			 for (Sprite candle:candlesSurvival) {
        			 //System.out.print("for each candle in candlescore");
        			 if (candle.getWidth() == 60) {
        				 if (candle.getPositionX() == (blob.getPositionX())) {
        					 //System.out.print("candle and witch in the same position increase score");
        					 score = score + 1;
        				 }
        				 //else if(candle.getPositionX() == 0 || (candle.getPositionX() > 0 &&  ) {
        					 
        				 //}
        			 }
        		 }
    			 //moveCandle();
    		 }
    		 else {
    		 for (Sprite candle:candles) {
    			 //System.out.print("for each candle in candlescore");
    			 //if (candle.getWidth() == 40) {
    				 if (candle.getPositionX() == (blob.getPositionX())) {
    					 score = score + 1;
    				 }
    			 //}
    		 }
    		 }
    	 }
    	 
    	 public void checkNoLives() {
    		 if (lives < 0) {
    			gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    bgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    cgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    
           	    gameOverSlogen.setText("GAME OVER");
           	  	startSlogen.setText("Press SPACE or the 'Go!'"+"\n"+"     Button to Restart.");
           	  	
           	  	for (Pumpkin pumpkin:pumpkins) {
           	  		pumpkin.setPositionXY(DEF.SCENE_WIDTH+2 + DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1);
           	  	}
           	  	for (Ghost ghost:ghosts) {
           	  		ghost.setPositionXY(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1);
           	  	}
           	  	GAME_OVER = true;
           	  	//RESTART = true;
    			 lives = 3;
    			 score = 0;
    			 RESTART = false; 
    		 }
    	

    	 }
    	 
    	 private void checkGhostCollectPumpkin(){
    		 for (Pumpkin pumpkin:pumpkins) {
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
    	 }

    	 private void showCollEffect() {
    		 double posBlobX = blob.getPositionX();
    		 double posBlobY = blob.getPositionY();
    		 
    		 
    		 	if(CANDLESCOLL &&blob.getPositionY() <= ( DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT-40)) {    	
    		 		
				 	System.out.println("bounce back");
					 blob.setImage(DEF.IMAGE.get("1-f"));
					 
					 blob.setPositionXY(posBlobX,posBlobY);
					 blob.setVelocity(-2, 2);
		    		 blob.update(DEF.SCENE_SHIFT_TIME);
		    		 blob.render(gc);
        		 
    		 	}else { 
    		 		CANDLESCOLL = false;
	       			 showHitEffect();
	       			 timer.stop();
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





}	// End of AngryFlappyBird Class