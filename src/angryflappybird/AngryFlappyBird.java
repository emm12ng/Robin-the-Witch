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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;

//@authors = Emma (Trang) Nguyen, Amy Geng, Adle Akbulut 



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
    
    //Variable to check if the survivalModeCandles method is run once
    int survivalModeCandlesEntry = -1;
    
    //variables for moveCandle
	double nextX;
	double nextY;
    
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
    private boolean PUMPKIN_COLLECTED = false;
    
    private boolean changeBackground = false;
    private boolean backgroundChangeAvailable;
    private ImageView background;
    private String currBackground;

    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER, RESTART, CANDLESCOLL;
    
// scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)

    private GraphicsContext gc;
	
    private String pickedDifficulty = "";
    private boolean survivalMode = false;
    private boolean easyMode = false;
    private boolean intermediateMode = false;
    private boolean hardMode = false;

    //the status of the auto-pilot mode
    private boolean auto = false;

    
	// the mandatory main method
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Puts the candles to the appropriate positions for the Survival Mode
     * @param candle(Sprite)
     * @param posA(Integer)
     * @param posB(Integer)
     */
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
    
    /**
     * Initializes the candles for survival mode
     */
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
    }
    

    /**
     * the start method sets the Stage layer
     */
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
 
    /**
     * the getContent method sets the Scene layer
     */
    private void resetGameControl() {

    	//Picking difficulty level by mouse clicking
    	DEF.easy.setOnMouseClicked(event -> {
    		//Update the mode variables and pickedDifficulty for easy
    		pickedDifficulty = "easy";
    		easyMode = true;
    		intermediateMode = false;
    		hardMode = false;
    		survivalMode = false;
    		
    	});
    	DEF.intermediate.setOnMouseClicked(event -> {
    		//Update the mode variables and pickedDifficulty for intermediate
    		pickedDifficulty = "intermediate";
    		intermediateMode = true;
    		easyMode = false;
    		hardMode = false;
    		survivalMode = false;
    	});
    	DEF.hard.setOnMouseClicked(event -> {
    		//Update the mode variables and pickedDifficulty for hard
    		pickedDifficulty = "hard";
    		hardMode = true;
    		easyMode = false;
    		intermediateMode = false;
    		survivalMode = false;
    	});
    	DEF.survival.setOnMouseClicked(event -> {
    		//Update the mode variables and pickedDifficulty for survival
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

        //Action for startButton
        DEF.startButton.setOnAction(event -> {
            if (GAME_OVER) {
            	//clear canvases
            	gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    //update the candle entry for survival mode
           	    survivalModeCandlesEntry = -1;
           	    //update CANDLESCOLL
           	    CANDLESCOLL = false;
           	    //stop timer
           	    timer.stop();
           	    //pause background music
            	DEF.backgroundMusicMP.pause();
            	//update resetGameScene to false 
                resetGameScene(false);
                //update GAME_OVER to false 
                GAME_OVER = false;
                //update RESTART to true 
                RESTART = true;
            }
            else if (RESTART) {
            	//clear canvases
            	gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
           	    //update CANDLESCOLL
           	    CANDLESCOLL = false;
           	    //stop timer
            	timer.stop();
            	//update the candle entry for survival mode
            	survivalModeCandlesEntry = -1;
            	//Update score
            	score = 0;
            	//Update gameOverSlogen
            	gameOverSlogen.setText("");
            	//update resetGameScene to false 
            	resetGameScene(false);
            }
        	else if (GAME_START){
        		//assign the time value at the moment to the clickTime
                clickTime = System.nanoTime();
                //set background music cycle to 50
                DEF.backgroundMusicMP.setCycleCount(50);
                //play the background music
                DEF.backgroundMusicMP.play();
            }
            //update GAME_START to true 
        	GAME_START = true;
        	//update CLICKED to true 
            CLICKED = true;
        	}
        );
        //Assign a VBox to gameControl
        gameControl = new VBox();
        //Add all the variables for the VBox to gameControl
        gameControl.getChildren().addAll( DEF.easy, DEF.intermediate, DEF.hard, DEF.survival, DEF.startButton, DEF.normalPumpkinInstruct, DEF.goldPumpkinInstruct, DEF.ghostInstruct);
    }

    /**
     * Resets the game scene
     * @param firstEntry(boolean)
     */
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
        //For first try out of four
    	if(firstEntry) {
    		//Set lives to 3
    		lives = 3;
    		//Set score to 0
            score = 0;
            //Create Text for text variables
            scoreText = new Text();
            livesText = new Text();
    		// create canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();
            // create a background
            background = DEF.IMVIEW.get("backgroundLight");
            currBackground = "backgroundA";
            //Create Text for text variables
            gameOverSlogen = new Text();
            startSlogen = new Text();
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, gameOverSlogen, startSlogen, canvas,  scoreText, livesText);
    	}
    	
    	//Style texts
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
		
        //initialize ghosts
        for(int i=0; i<DEF.GHOST_COUNT; i++) {
        	Ghost ghost = new Ghost(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1, 0, 0, DEF.IMAGE.get("ghost"));
        	ghost.render(gc);
        	ghosts.add(ghost);
        }
        
        //initialize pumpkins
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
    /**
     * Timer is keeps track of time shifts and game's functioning
     */
    class MyTimer extends AnimationTimer {

    	int counter = 0;

    	 public void handle(long now) {
    		 // time keeping
    	     elapsedTime = now - startTime;
    	     startTime = now;

    	     // clear current scene
    	     gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
    	    
    	     if (GAME_START) {
    	    	 //Update texts
    	    	 startSlogen.setText("");
    	    	 gameOverSlogen.setText("");
    	    	 //For survival mode
    	    	 if (survivalMode) {
    	    		 //put candles to the scene
    	    		 survivalModeCandles();
	    	    	 //update blob
	    	    	 moveBlob();
	    	    	 //keep track of the score
	    	    	 candleScore(pickedDifficulty);
    	    		//update floor
	    	    	 moveFloor();
	    	    	 //update candle
	    	    	 moveCandle();
	    	    	 //control ghost
	    	    	 controlGhost();
	    	    	 //controlPumpkin();
	    	    	 pumpkinOverCandle();
	    	    	 //check pumpkin collection
	    	    	 checkPumpkinCollectSurvival();
	    	    	 //check if no lives left
	    	    	 checkNoLives();
	    	    	 //show the lives
	    	    	 scoreText.setText(String.valueOf(score));
	    	    	 livesText.setText(lives + " lives left");
	    	    	 //change background
	    	    	 changeBackground();
    	    	 } //For other modes
    	    	 else {
    	    		 int posA;
    	    	     int posB;
    	    	     int posC =  DEF.SCENE_HEIGHT - (60);
    	    	     int random;
    	    	     //resize images for the game environment
    	    		 DEF.resizeImage("normalpumpkin", 90, 90);
    	 			 DEF.resizeImage("goldpumpkin", 90, 90);
    	 			 for(int i=0; i<DEF.CANDLE_COUNT; i++) {
    	 				//pick a random number
    	 	    		random = (int)(Math.random() * 200) + 100; // 100~380
    	 	    		//Assign positions
    	 		    	posA =i* DEF.CANDLES_SPLIT+DEF.CANDLES_START;
    	 				posB = -(random+80); //70 is the difficulty
    	 				//initialize candles
    	 				Sprite candleDown = new Sprite(posA, posB,DEF.IMAGE.get("CandlesLong"));
    	 				Sprite candleUp = new Sprite(posA, posB, DEF.IMAGE.get("CandlesLongUp"));
    	 				Sprite candleBot = new Sprite(posA-16, posC, DEF.IMAGE.get("CandleBottom"));
    	 				//Initialize pumpkins
    	 				Sprite normalPumpkin = new Sprite(posA-36, posC-100, DEF.IMAGE.get("normalpumpkin"));
    	 				Sprite goldPumpkin = new Sprite(posA-36, posC-100, DEF.IMAGE.get("goldpumpkin"));
    	 				//clear the other pumpkins list
    	 				pumpkins.clear();
    	 				//update candle's properties
    	 				candleUp.setPositionXY(posA, posB);
    	 				candleUp.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 				//update candle's properties
    	 				posB = DEF.SCENE_HEIGHT - (random); 
    	 				candleDown.setPositionXY(posA, posB);
    	 				candleDown.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 				//update candle's properties
    	 				candleBot.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 			
    	 				//random pumpkin display
    	 				if (random %3 == 0 && random %6 !=0) {
    	 					normalPumpkin.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 					normalPumpkin.render(gc);
    	 					pumpkins2.add(normalPumpkin);
    	 				}
    	 				else if  (random %6 ==0)
    	 				{	goldPumpkin.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
    	 					goldPumpkin.render(gc);
    	 					pumpkins2.add(goldPumpkin);}
    	 				else {
    	 					candleDown.render(gc);
    	 					candles.add(candleDown);
    	 				}
    	 				
    	 				//candle display
    	 				candleUp.render(gc);
    	 				candleBot.render(gc);
    	 				//add candles to the arrayList
    	 				candles.add(candleUp);
    	 				candles.add(candleBot);
    	 	    	}
    	 			 //check scores
    	    		 candleScore(pickedDifficulty);
    	    		 //check if no lives left
    	    		 checkNoLives();
    	    		//control Pumpkin
	    	    	 movePumpkin();
	    	    	 //check pumpkin collection
	    	    	 checkPumpkinCollect();
	    	    	 //update texts
	    	    	 scoreText.setText(String.valueOf(score));
	    	    	 livesText.setText(lives + " lives left");
	    	    	 //for easy mode
    	    		 if(easyMode) {
    	    			//update floor
    	    			 moveFloor();
    	    	    	 //update candle
    	    	    	 moveCandle("easy");
    	    	    	 //update blob
    	    	    	 moveBlob();
    	    	    	//control ghost
    	    	    	 controlGhost();
    	    		 } //for intermediate mode
    	    		 else if(intermediateMode) {
    	    			//update floor
    	    	    	 moveFloor();
    	    	    	 //update candle
    	    	    	 moveCandle("intermediate");
    	    	    	 //update blob
    	    	    	 moveBlob();
    	    	    	//control ghost
    	    	    	 controlGhost();
    	    		 } //for hard mode
    	    		 else if(hardMode) {
    	    			//update floor
    	    	    	 moveFloor();
    	    	    	 //update candle
    	    	    	 moveCandle("hard");
    	    	    	 //update blob
    	    	    	 moveBlob();
    	    	    	//control ghost
    	    	    	 controlGhost();
    	    		 }
	    	    	 changeBackground();
    	    	 }

    	     } //if game start is false
    	     else {
    	     //set text
    	     startSlogen.setText("Press SPACE or the 'Go!'"+"\n"+"       Button to Start.");
    	     }
    	 }
    	 
    	 /**
    	  * Changes the background 
    	  */
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



    	 /**
    	  * Updates the floor and gives the illusion of moving
    	  */
    	 private void moveFloor() {
    		//moves each floor to the left 
    		for(int i=0; i<DEF.FLOOR_COUNT; i++) {
    			if (floors.get(i).getPositionX() <= -DEF.FLOOR_WIDTH) {
    				double nextX = floors.get((i+1)%DEF.FLOOR_COUNT).getPositionX() + DEF.FLOOR_WIDTH;
    	        	double nextY = DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT;
    	        	floors.get(i).setPositionXY(nextX, nextY);
    			}
    			//display floor
    			floors.get(i).render(gc);
    			//update floor
    			floors.get(i).update(DEF.SCENE_SHIFT_TIME);
    		}
    	 }

    	 
    	
    	 /**
    	  * Update candles 
    	  * Specifically made for Survival Mode
    	  */
    	 private void moveCandle() {
    		//moves each candle to the left 
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
     			//display candles
     			candlesSurvival.get(i).render(gc);
     			//update candles
     			candlesSurvival.get(i).update(DEF.SCENE_SHIFT_TIME);
     		}
     	 }
    	 
    	 /**
    	  * Moves candles to the left 
    	  * Made for easy, intermediate, and hard modes
    	  * @param difficultyLevel(String): depending on the difficulty the movement speed is different
    	  */
    	 private void moveCandle(String difficultyLevel) {
    		//set new positions
      		for(int i=0; i<DEF.CANDLE_COUNT; i++) {
      			if (candles.get(i).getPositionX() <= -500) {
     				double nextX = candles.get((i+1)%DEF.CANDLE_COUNT).getPositionX() + DEF.UP_CANDLE_WIDTH;
     	        	double nextY = DEF.SHORT_CANDLE_HEIGHT;
     	        	candles.get(i).setPositionXY(nextX, nextY);
      			}
      			//display candles
      			candles.get(i).render(gc);
      			//set the velocity
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
    	 
    	 
    	 /**
    	  * Moves the pumpkins to left
    	  */
    	 private void movePumpkin() {
       		
       		for(int i=0; i<DEF.PUMPKIN_COUNT; i++) {
       			pumpkins2.get(i).render(gc);
       			pumpkins2.get(i).update(DEF.SCENE_SHIFT_TIME);
       		}
       	  }

    	 
    	 /**
    	  * Updates blob
    	  */
    	 private void moveBlob() {
    		 //for regular fly
    		 if (auto == false) {
    			 regularFly();
    		 } //for autoPilot
    		 else {
			 	autoPilot();
    		 }
    	 }


    	 /**
    	  * Place the pumpkins over the candles randomly
    	  * Specifically made for Survival Mode
    	  */
    	 public void pumpkinOverCandle() {
    		 //resize the images for the environment
    		 DEF.resizeImage("normalpumpkin", DEF.PUMPKIN_WIDTH, DEF.PUMPKIN_HEIGHT);
 			 DEF.resizeImage("goldpumpkin", DEF.PUMPKIN_WIDTH, DEF.PUMPKIN_HEIGHT);
 			 //pick a random number
    		 Random rand = new Random();
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
	    			 }
    				//For the middle candles
	    			 else if (pickedCandle.getHeight() == DEF.MIDDLE_CANDLE_HEIGHT) {
    	    			controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (258));
	    			 }
    				//For the long candles
	    			 else if (pickedCandle.getHeight() == DEF.LONG_CANDLE_HEIGHT){
	    				 controlPumpkin(pickedCandle.getPositionX(), DEF.SCENE_HEIGHT - (278));
	    			 }
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
	        			 makeGhost = false;
	        		 }
    		 }
    		 if (makeGhost2) {
	    		 /**for (Sprite ghost:ghosts) {*/
	    			 if (ghosts.get(1).getPositionX()>DEF.SCENE_WIDTH && makeGhost2) {
	        			 ghosts.get(1).setPositionXY(DEF.SCENE_WIDTH,  0-ghosts.get(1).getHeight());
	        			 ghosts.get(1).setVelocity(-30, DEF.GHOST_VEL1);
	        			 makeGhost2 = false;
	        		 }
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