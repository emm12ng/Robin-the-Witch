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

/**
 * @author Adle Akbulut 
 * @author Emma Nguyen
 * @author Amy Geng
 *
 */



//The Application layer
public class AngryFlappyBird extends Application {
	
	/**
	 * Initializes the angry flappy bird (halloween version) game elements and runs the game.
	 * 
	 * The game has four levels: Easy, Intermediate, Hard, and Survival Mode. Easy, Intermediate, and Hard have 
	 * the same game set up but with different speeds, and Survival Mode has a different game set up with more 
	 * candles and pumpkins. User selects the level in the GUI panel before pressing "Go!" to start the game.
	 * 
	 * Game Objectives:
	 * Avoid candles and ghosts.
	 * Collect pumpkins.
	 * Do not let ghosts steal pumpkins.
	 * 
	 * Game Rules:
	 * Start with 3 lives and 0 score.
	 * Collection of a normal pumpkin increases score by 5.
	 * Collection of a gold pumpkin enables autopilot.
	 * Collection of a pumpkin by a ghost decreases score by 3.
	 * Passing each candle increases score by 1.
	 * Collision with a candle decreases lives by 1.
	 * Game ends when user dies at 0 lives left.
	 * Game ends when witch collides with the floor or a ghost.
	 * 
	 * Autopilot is enabled when a gold pumpkin is collected. When on autopilot, the witch will automatically 
	 * fly without colliding into candles or ghosts. Autopilot lasts six seconds at a time.
	 */

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
    private ArrayList<Sprite> ghosts2;
    
    //Variable to check if the survivalModeCandles method is run once
    int survivalModeCandlesEntry = -1;
    
    //variables for moveCandle
	double nextX;
	double nextY;
    
    private int score;
    private int lives;
    private int random;
    
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
    private boolean CLICKED, GAME_START, GAME_OVER, RESTART, CANDLESCOLL,GHOSTSCOLL,LIVELOSS;
    
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
    		for(int i=0; i<DEF.CANDLE_COUNT_SURVIVIAL; i++) {
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
     * Initialize the candles for normal mode
     */
    
    private void normalInit() {
    	
    	
    	//Initialize Ghost for normal mode
    	for (int i = 0;i<(DEF.CANDLE_COUNT); i++) {
    	 	double nextX =  -14+ (24 + DEF.CANDLES_SPLIT)*(DEF.CANDLE_COUNT-1);
    	 	
    		Sprite ghost = new Sprite(nextX-76, -100, DEF.IMAGE.get("ghost"));
    	

    		ghosts2.add(ghost);

    	}
    	
    	// initialize Pumpkin for normal mode
    	// make the pumpkins bigger for normal mode
    	 DEF.resizeImage("normalpumpkin", 100, 100);
    	 DEF.resizeImage("goldpumpkin", 100, 100);
    	for (int i = 0;i<(DEF.CANDLE_COUNT); i++) {
    	 	double nextX =  -14+ (24 + DEF.CANDLES_SPLIT)*(DEF.CANDLE_COUNT-1);
    	 	int posBotH =  DEF.SCENE_HEIGHT - (60);
    		 Sprite goldPumpkin = new Sprite(nextX-76, posBotH-100, DEF.IMAGE.get("goldpumpkin"));
		
				Sprite normalPumpkin = new Sprite(nextX-76, posBotH-100, DEF.IMAGE.get("normalpumpkin"));
				
				pumpkins2.add(goldPumpkin);
				pumpkins2.add(normalPumpkin);
	    	
    	}
    	// make the pumpkin size back to normal
    	 DEF.resizeImage("normalpumpkin", DEF.PUMPKIN_WIDTH, DEF.PUMPKIN_HEIGHT);
		DEF.resizeImage("goldpumpkin", DEF.PUMPKIN_WIDTH, DEF.PUMPKIN_HEIGHT);
    	
    	
    	
		// initialize candle for normal mode
	    	int posA;
	    	int posB;
	    	int posC =  DEF.SCENE_HEIGHT - (60);
	    	
	    	
	    	// initialize candle
	    	for(int i=0; i<DEF.CANDLE_COUNT; i++) {
	    		random = (int)(Math.random() * 200) + 100; // 100~380
	    		
	    		//autoHeight.add(random);
		    		
		    	posA =i* DEF.CANDLES_SPLIT+DEF.CANDLES_START;
				posB = -(random+DEF.CANDLE_PAIR_DISTANCE); //70 is the difficulty
				Sprite candleDown = new Sprite(posA, posB,DEF.IMAGE.get("CandlesLong"));
				
				Sprite candleUp = new Sprite(posA, posB, DEF.IMAGE.get("CandlesLongUp"));
				
				Sprite candleBot = new Sprite(posA-16, posC, DEF.IMAGE.get("CandleBottom"));
				
				Sprite normalPumpkin = new Sprite(posA-36, posC-100, DEF.IMAGE.get("normalpumpkin"));
				
				Sprite goldPumpkin = new Sprite(posA-36, posC-100, DEF.IMAGE.get("goldpumpkin"));
				
				Sprite ghost = new Sprite (posA-36, -100, DEF.IMAGE.get("ghost"));
				
				
				candleUp.setPositionXY(posA, posB);
				candleUp.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
				
				posB = DEF.SCENE_HEIGHT - (random); 
				candleDown.setPositionXY(posA, posB);
				candleDown.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
				
				candleBot.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
			
				candles.add(candleUp);
				
				
				if (random %3 == 0 && random %6 !=0) {
					normalPumpkin.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
					normalPumpkin.render(gc);
					
					pumpkins2.add(normalPumpkin);
					candles.add(candleDown);
				}
				
				else if  (random %6 ==0)
				{	goldPumpkin.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
					goldPumpkin.render(gc);
					pumpkins2.add(goldPumpkin);
					candles.add(candleDown);
				}
				
				else {
					candleDown.render(gc);
					candles.add(candleDown);
				}
				
				if  (random %5 ==0) {
					ghost.setVelocity(DEF.SCENE_SHIFT_INCR, -0.2);
					ghost.render(gc);
					ghosts2.add(ghost);
				}
				
				candleUp.render(gc);
				candleBot.render(gc);
				
				
				
				candles.add(candleBot);
				
				
				
				
	    	
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
           	 GHOSTSCOLL = false;
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
            	
            	lives = 3;
            	score = 0;
           	    //update CANDLESCOLL
           	    CANDLESCOLL = false;
           	    GHOSTSCOLL = false;
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
            // for live loss in normal mode
            else if (LIVELOSS) {
            	
            	CANDLESCOLL = false;
            	GHOSTSCOLL = false;
            	timer.stop();
            	lives -= 1;
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
        LIVELOSS = false;
        //Reset lists
        floors = new ArrayList<>();
        ghosts = new ArrayList<>();
        pumpkins = new ArrayList<>();
        candles = new ArrayList<>();
        candlesSurvival = new ArrayList<>();
        pumpkins2 = new ArrayList<>();
        ghosts2 = new ArrayList<>();
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
    	
    	
    	
    	
    	// initialize the normal mode sprite
    	normalInit();
    	
    	// initialize survival mode sprite
    	// initialize candle
    	for(int i=0; i<DEF.CANDLE_COUNT_SURVIVIAL; i++) {
    		Sprite candle = new Sprite(0, 0, DEF.IMAGE.get("ShortCandleUp"));
    		candlesSurvival.add(candle);
    	}
    
    	
		
		
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
        
        // initialize timer
        startTime = System.nanoTime();
        timer = new MyTimer();
        timer.start();
        

    	
    	
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
	    	    	 moveFloor("easy");
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
    	    		
    	    		 
    	    	    	 
    	    	    	
    	 		    	//check scores
       	    		 candleScore(pickedDifficulty);
    	    	    	 
       	    	

    	    	    	 // check live loss because of crushing a candle
    	    		 	checkLives();
    	    		 	
    	    		 	// check collision
    	    	    	checkCollision("other");
    	    	    	
    	    	    	// check pumpkin Collect
    	    	    	checkPumpkinCollect();
    	    	    	 
    	    	    	// display lives and scores 
    	    	    	displayScoreLives();
    	 		    	
    	 			 
    	    		 
    	    		
	    	    	 //for easy mode
    	    		 if(easyMode) {
    	    			//update floor
    	    			 moveFloor("easy");
    	    	    	 //update candle
    	    	    	 moveCandle("easy");
    	    	    	 //update blob
    	    	    	 moveBlob();
    	    	    	//control ghost
    	    	    	 //controlGhost();
    	    		 } //for intermediate mode
    	    		 else if(intermediateMode) {
    	    			//update floor
    	    	    	 moveFloor("intermediate");
    	    	    	 //update candle
    	    	    	 moveCandle("intermediate");
    	    	    	 //update blob
    	    	    	 moveBlob();
    	    	    	//control ghost
    	    	    	 controlGhost();
    	    		 } //for hard mode
    	    		 else if(hardMode) {
    	    			//update floor
    	    	    	 moveFloor("hard");
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
    	  * Changes the background for every 20 points earned
    	  */
    	 private void changeBackground() {
    		 // check if the background should be changed
    		 if (score > 0 && score%5 == 0 && backgroundChangeAvailable) {
    			 changeBackground = true;
    		 }
    		 // if the background should be changed, change the background
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
    		 // enable background changing when current 20th score is passed
    		 if (score%20 == 1) {
    			 backgroundChangeAvailable = true;
    		 }
    	 }
    	 
    	 /**
    	  * check live loss for crashing candles
    	  */
    	 public void  checkLives() {
    		 for (Sprite candle: candles) {
 				if  (lives != 0 && blob.intersectsSprite(candle,"others")) {
 					LIVELOSS = false;
 					}
 			}
    	 }

    	 /**
    	  * uodate live
    	  */
    	 public void displayScoreLives() {
    		 scoreText.setText(String.valueOf(score));
	    	 livesText.setText(lives + " lives left");
    	 }
    	 
    	 
    	 /**
    	  * Updates the floor and gives the illusion of moving
    	  */
    	 private void moveFloor(String difficultyLevel) {
    		 int upDateTime;
    		//set the velocity
    			if (difficultyLevel == "easy") {
    				upDateTime = DEF.SCENE_SHIFT_TIME;
    			}
    			else if(difficultyLevel == "intermediate") {
    				upDateTime = DEF.SCENE_SHIFT_TIME + 3;
    			}
    			else {
    				upDateTime = DEF.SCENE_SHIFT_TIME + 7;
    			}
    		 
    		 
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
    			floors.get(i).update(upDateTime);
    		}
    	 }

    	 
    	
    	 /**
    	  * Update candles 
    	  * Specifically made for Survival Mode
    	  */
    	 private void moveCandle() {
    		//moves each candle to the left 
     		for(int i=0; i<DEF.CANDLE_COUNT_SURVIVIAL; i++) {
     			//If all the candles in the array are placed
     			if (candlesSurvival.get(i).getPositionX() <= -DEF.CANDLE_COUNT_SURVIVIAL) {
     				//For the candles on top
     				if (candlesSurvival.get(i).getWidth() == 60) {
     					//50 depends on preference, it can be changed according to the difficulty
     					nextX = candlesSurvival.get((i+ (DEF.CANDLE_COUNT_SURVIVIAL - 1))%DEF.CANDLE_COUNT_SURVIVIAL).getPositionX() + 50;
     					nextY = 0;
     	    		}
     				//For the candles on bottom
     	    		else if (candlesSurvival.get(i).getWidth() == 50) {
     	    			//40 depends on preference, it can be changed according to the difficulty
     	    			nextX = candlesSurvival.get((i+ (DEF.CANDLE_COUNT_SURVIVIAL - 1))%DEF.CANDLE_COUNT_SURVIVIAL).getPositionX() + 40;
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
    		 int upDateTime;

   			//set the velocity
   			if (difficultyLevel == "easy") {
   				upDateTime = DEF.SCENE_SHIFT_TIME;
   			}
   			else if(difficultyLevel == "intermediate") {
   				upDateTime = DEF.SCENE_SHIFT_TIME + 3;
   			}
   			else {
   				upDateTime = DEF.SCENE_SHIFT_TIME + 7;
   			}
    		 
    		 
    		 
    		 for(int i=0; i<DEF.CANDLE_COUNT*3; i = i +3) {
     			
                 random = (int)(Math.random() * 200) + 100;
        			// 0 = up, 1 = down, 2 = bottom
        			
                	int posVertical= i* DEF.CANDLES_SPLIT+DEF.CANDLES_START;

                	int posUpH = -(random+DEF.CANDLE_PAIR_DISTANCE); //0 is the difficulty
                	int posDownH = DEF.SCENE_HEIGHT - (random); 
                	int posBotH =  DEF.SCENE_HEIGHT - (60);
                	
                
                	double nextX = candles.get((i+3)%(candles.size())).getPositionX() + (24 + DEF.CANDLES_SPLIT)*(DEF.CANDLE_COUNT-1);
               	
                	
                
                	
                	
              // set pumpkins and candles, and move candles
        			if (candles.get(i).getPositionX() <= - ( DEF.CANDLE_HOLDER_SQUARE+DEF.CANDLES_SPLIT)) {
       				
       	        	candles.get(i).setPositionXY(nextX, posUpH);
       	        	candles.get(i+2).setPositionXY(nextX-16, posBotH);
       	        	
       	        	
       	        	if (random %3 == 0 && random %6 !=0) {
       	        	
       	        		System.out.println(candles.get((i+3)%(candles.size())).getPositionX() );

       	        		candles.get(i+1).setPositionXY(nextX, DEF.SCENE_HEIGHT);
       	        			
       	        		ghosts2.get(i/3).setPositionXY(nextX-36, 100);
       	        		
       	        		pumpkins2.get(i/3).setPositionXY(nextX-36, posBotH-100);

       	 			}
       	        	else if  (random %6 ==0)	{	
       	        		
       	        		System.out.println(candles.get((i+3)%(candles.size())).getPositionX() );
       	 				
       	 				candles.get(i+1).setPositionXY(nextX, DEF.SCENE_HEIGHT);
       	 				
       	 				ghosts2.get(i/3).setPositionXY(nextX-36, 100);
       	 				pumpkins2.get(i/3).setPositionXY(nextX-36, posBotH-100);
       	 				
       	 				}
       	        	else {
       	        		candles.get(i+1).setPositionXY(nextX, posDownH);
       	        		
       	 			
       	        	}

       	        	
        			}


        			candles.get(i).render(gc);
        			candles.get(i).update(upDateTime);
        			candles.get(i+1).render(gc);
 	       		candles.get(i+1).update(upDateTime);
        			
        			candles.get(i+2).render(gc);		
        			candles.get(i+2).update(upDateTime);
        			
        			// move candles
        			
        		 if (candles.get(i+1).getPositionY() == DEF.SCENE_HEIGHT) {
        			 
 				 pumpkins2.get(i/3).setVelocity(DEF.SCENE_SHIFT_INCR, 0);
 				 pumpkins2.get(i/3).render(gc);
 				 pumpkins2.get(i/3).update(upDateTime);
 				 
 				 // move ghost
 				 if (ghosts2.get(i/3).getPositionX()<=0) {
 					 
 					 ghosts2.get(i/3).setVelocity(DEF.SCENE_SHIFT_INCR, 2);
 					 ghosts2.get(i/3).render(gc);
 					 ghosts2.get(i/3).update(upDateTime);
 				 }
 				 else {
 					 ghosts2.get(i/3).setVelocity(DEF.SCENE_SHIFT_INCR, 0.07);
 					 ghosts2.get(i/3).render(gc);
 					 ghosts2.get(i/3).update(upDateTime);
 				 }
 				
 				 
 				 
 				 
 			}
        		 

        		
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
    			 int randomCandle = rand.nextInt(DEF.CANDLE_COUNT_SURVIVIAL);
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

    	 /**
    	  * randomly and separately bring the two ghosts into the screen
    	  * make ghosts travel across the screen
    	  */
    	 public void controlGhost() {
    		 
    		 Random rand = new Random();
    		 
    		 // determine whether to make the ghosts (randomly)
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
    		 // if making ghost, play ghost sound
    		 if (makeGhost || makeGhost2) {
    			 DEF.ghostSoundMP.setVolume(5000);
    			 DEF.ghostSoundMP.stop();
    			 DEF.ghostSoundMP.play();
    		 }
    		 // if making ghosts, bring ghosts into screen
    		 if (makeGhost) {
    			 if (ghosts.get(0).getPositionX()>DEF.SCENE_WIDTH && makeGhost) {
    				 ghosts.get(0).setPositionXY(DEF.SCENE_WIDTH,  0-ghosts.get(0).getHeight());
    				 ghosts.get(0).setVelocity(-30, DEF.GHOST_VEL1);
    				 makeGhost = false;
    			 }
    		 }
    		 if (makeGhost2) {
    			 if (ghosts.get(1).getPositionX()>DEF.SCENE_WIDTH && makeGhost2) {
    				 ghosts.get(1).setPositionXY(DEF.SCENE_WIDTH,  0-ghosts.get(1).getHeight());
    				 ghosts.get(1).setVelocity(-30, DEF.GHOST_VEL1);
    				 makeGhost2 = false;
    			 }
    		 }
    		 // if ghosts have moved out of the screen, reset them to initial position outside of screen and set 
    		 // velocity to 0
    		 // render the ghosts on scene
    		 for(Sprite ghost :ghosts) {
    			 ghost.update(elapsedTime * DEF.NANOSEC_TO_SEC);
        		 ghost.render(gc);
    		 }
    	 }
    	 
    	 /**
    	  * randomly and separately bring the two pumpkins into the screen
    	  * make pumpkins travel across the screen
    	  */
    	 public void controlPumpkin(double posX, double posY) {
    		 
    		 Random rand = new Random();
    		 
    		// determine whether to make the pumpkins (randomly)
    		 int makePumpkinInt = rand.nextInt(300);
    		 boolean makePumpkin = false;
    		 if (makePumpkinInt == 1) {
    			 makePumpkin = true;
    		 }
    		 int makePumpkinInt2 = rand.nextInt(300);
    		 boolean makePumpkin2 = false;
    		 if (makePumpkinInt2 == 1) {
    			 makePumpkin2 = true;
    		 }
    		// if making pumpkins, bring pumpkins into screen
    		 if (makePumpkin) {
    			 int makeGoldPumpkinInt = rand.nextInt(4);
    			 boolean makeGoldPumpkin = false;
        		 if (makeGoldPumpkinInt == 3) {
        			 makeGoldPumpkin = true;
        		 }
	    			 if (pumpkins.get(0).getPositionX()>DEF.SCENE_WIDTH && makePumpkin) {
	        			 pumpkins.get(0).setPositionXY(posX, posY);
	        			 pumpkins.get(0).setVelocity(DEF.PUMPKIN_VEL, 0);
	        			 if(makeGoldPumpkin) {
	        				 pumpkins.get(0).makeGold();
	        				 pumpkins.get(0).setImage(DEF.IMAGE.get("goldpumpkin"));
	        			 }
	        			 makePumpkin = false;
	        			 makeGoldPumpkin = false;
	        		 }
    		 }
    		 if (makePumpkin2) {
    			 int makeGoldPumpkinInt2 = rand.nextInt(4);
    			 boolean makeGoldPumpkin2 = false;
        		 if (makeGoldPumpkinInt2 == 3) {
        			 makeGoldPumpkin2 = true;
        		 }
        		 if (pumpkins.get(1).getPositionX()>DEF.SCENE_WIDTH && makePumpkin) {
        			 pumpkins.get(1).setPositionXY(posX, posY);
        			 pumpkins.get(1).setVelocity(DEF.PUMPKIN_VEL, 0);
        			 if(makeGoldPumpkin2) {
        				 pumpkins.get(1).makeGold();
        				 pumpkins.get(1).setImage(DEF.IMAGE.get("goldpumpkin"));
        			 }
        			 makePumpkin2 = false;
        			 makeGoldPumpkin2 = false;
        		 }
    		 }
    		 // if pumpkins have moved out of the screen, reset them to initial position outside of screen, set 
    		 // velocity to 0, and make pumpkin normal
    		 // render the pumpkins on scene
    		 for(Pumpkin pumpkin :pumpkins) {
    			 if (pumpkin.getPositionX()<0-pumpkin.getWidth()) {
        			 pumpkin.setPositionXY(posX, posY);
        			 pumpkin.setVelocity(DEF.PUMPKIN_VEL, 0);
        			 pumpkin.makeNormal();
        			 pumpkin.setImage(DEF.IMAGE.get("normalpumpkin"));
    			 }
    			 pumpkin.update(elapsedTime * DEF.NANOSEC_TO_SEC);
        		 pumpkin.render(gc);
    		 }
    	 }

    	 /**
    	  * controls the blob's regular fly
    	  */
    	 public void regularFly() {
    		 //the time difference between the current moment and the clciktime
    		 long diffTime = System.nanoTime() - clickTime;
     		// blob flies upward with animation
     		if (CLICKED && diffTime <= DEF.BLOB_DROP_TIME) {
     			int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
     			imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
     			//update images
     			blob.setImage(DEF.IMAGE.get("1-"+String.valueOf(imageIndex)));
     			//set velocity
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

    	 /**
    	  * Controls the autoPilot mode which is activated by the golden pumpkin
    	  */
    	 public void autoPilot() {
    		 double ongoingTime = System.nanoTime() * DEF.NANOSEC_TO_SEC;
    		 //makes the autopilot mode to continue for six seconds
    		 if (ongoingTime < (goldPumpkinCollectTime * DEF.NANOSEC_TO_SEC + 6)) {
    			 //update the ongoing time
    			 ongoingTime = System.nanoTime() * DEF.NANOSEC_TO_SEC;
    			 //fix the blob image

        		 blob.setImage(DEF.IMAGE.get("1-2"));
        		 //set velocity
        		 blob.setVelocity(0, 0);
        		 //for survival mode
        		 if (survivalMode) {
        			 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y + 105);
        		 } //for other modes
        		 else {
        			 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y + 200);
        		 }
    		 } //when autopilot mode ends
    		 else {
    			 //for survival mode
    			 if (survivalMode) {
    			 //update blob's position
    			 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y + 15);
    			 } //for other modes
    			 else {
        			 //update blob's position
    				 blob.setPositionXY(DEF.BLOB_POS_X, DEF.BLOB_POS_Y);
    			 }
    			 //set autopilot mode to false
          		 auto = false;
    		 }
    		 //display the blob
    		 blob.render(gc);
    		 //update the blob
    		 blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
    	 }

    	 /**
    	  * Check witch collision with floors, candles and ghosts. Collision with candles decrease a life. 
    	  * Collision with floors and ghosts ends game.
    	  * @param difficultyLevel
    	  */
    	 public void checkCollision(String difficultyLevel) {
    		
    		

			// if witch collided with a ghost, reset score to 0
			if (ghostCollision) {
				score = 0;
				ghostCollision = false;
			}
			
			// check collision with candles depending on mode of game. If yes, reset screen
			// if in survival mode, use candlesSurvival arraylist
			if (difficultyLevel == "survival") {

				// check if witch collides with ghosts. If yes, end game
				
					
					
				
				for (Ghost ghost: ghosts) {
					GHOSTSCOLL  = GAME_OVER || blob.intersectsSprite(ghost,difficultyLevel);
					GAME_OVER = GAME_OVER || blob.intersectsSprite(ghost,difficultyLevel);
					if (blob.intersectsSprite(ghost,difficultyLevel)) {
						ghostCollision = true;
					}
					
				}
				for (Sprite candle: candlesSurvival) {
					
					if (blob.intersectsSprite(candle,difficultyLevel)) {
						candleCollision = true;
					}
					GAME_OVER = GAME_OVER || blob.intersectsSprite(candle,difficultyLevel);
					
				}
				 // check if witch collided with floors. If yes, end game
				for (Sprite floor: floors) {
					GAME_OVER = GAME_OVER || blob.intersectsSprite(floor,difficultyLevel);
					if (blob.intersectsSprite(floor,difficultyLevel)) {
						floorCollision = true;
						
					}
					
			 		
			 		
				}
				// if witch collided with floor, reset score to 0
				
				if (floorCollision) {
					score = 0;
					floorCollision = false;
					
				}
			}
			// if in another mode, use candles arraylist
			else {
				for (Sprite ghost: ghosts2) {
					if(!auto) {
						
						GAME_OVER = GAME_OVER || blob.intersectsSprite(ghost,difficultyLevel);
						GHOSTSCOLL = GAME_OVER;
						if (blob.intersectsSprite(ghost,difficultyLevel)) {
							lives = 0;
							
						}
					}
					
					
				}
				
				for (Sprite candle: candles) {
					if (!auto) {
					if (lives != 0) {
					LIVELOSS = LIVELOSS || blob.intersectsSprite(candle,"others");
					CANDLESCOLL = LIVELOSS;
					
					
					}
					else {
						
						GAME_OVER = GAME_OVER || blob.intersectsSprite(candle,"others");
						CANDLESCOLL = GAME_OVER ;
						
					}}
				}
				for (Sprite floor: floors) {
					if (!LIVELOSS ) {
						
					GAME_OVER = GAME_OVER || blob.intersectsSprite(floor,"others");
					
					if (blob.intersectsSprite(floor,difficultyLevel)) {
						lives = 0;
						
					}
					}
					
				}
					
				}
			
			
			// if witch collided with a candle, decrease lives by 1
			if (difficultyLevel == "survival") {
			if (candleCollision) {
				lives = lives - 1;
				candleCollision = false;
			}
			// stop the screen when GAME_OVER is true
						if (GAME_OVER) {
							survivalModeCandlesEntry = -1;
							//showCollEffect();
							showHitEffect();
							timer.stop();
							DEF.backgroundMusicMP.pause();
							blob.setVelocity(0,0);
							for (Sprite floor: floors) {
								floor.setVelocity(0, 0);
							}
						}
						}
			// for other mode
			else {
				if (LIVELOSS) {
					
					//lives -= 1;
					
					//timer.stop();
					//showCollEffect();
					for (Sprite floor: floors) {
						floor.setVelocity(0, 0);
					}
					for (Sprite candle: candles) {
						candle.setVelocity(0, 0);
					}
					for (Sprite ghost: ghosts2) {
						ghost.setVelocity(0, 0);
					}
					for (Sprite pumpkin:pumpkins2) {
						pumpkin.setVelocity(0, 0);
					}
					blob.setVelocity(0,0);
					
					showCollEffect();
					
				}
				if (GAME_OVER && !LIVELOSS ) {
					
					showCollEffect();

					lives = 0;
					
					for (Sprite floor: floors) {
						floor.setVelocity(0, 0);
					}
					
				}
			
			}
			
    	 }

    	 /**
    	  * check witch collection of pumpkins for survival mode
    	  * plays laughing sounds and increases score accordingly
    	  */
    	 public void checkPumpkinCollectSurvival() {
    		 
    		 // check if pumpkins were collected
    		 for (Pumpkin pumpkin:pumpkins) {
    			 if (blob.intersectsSprite(pumpkin,"survival")) {
    				 PUMPKIN_COLLECTED = true;
    				 // if normal pumpkin was collected, increase score by 5
    				 if (pumpkin.getType().equals("normal")) {
        				 score = score + 5;
    				 }
    				 // if gold pumpkin was collected, record time
    				 else {
    					 auto = true;
    					 goldPumpkinCollectTime = System.nanoTime();
    				 }
    				 // reset pumpkin to initial position and velocity
    				 pumpkin.setPositionXY(DEF.SCENE_WIDTH+2 + DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1);
    				 pumpkin.setVelocity(0, 0);
    			 }
    		 }
    		 
    		 // if pumpkin was collected, play witch laugh
    		 DEF.witchLaughMP.setCycleCount(1);
    		 if (PUMPKIN_COLLECTED) {
    			 DEF.witchLaughMP.stop();
    			 DEF.witchLaughMP.play();
    			 PUMPKIN_COLLECTED = false;
    		 }
    	 }
    	 
    	 /**
    	  * check witch collection of pumpkins for modes that aren't survival
    	  * plays laughing sounds and increases score accordingly
    	  */
    	 public void checkPumpkinCollect() {
    		
    		// check if pumpkins were collected
    		 for (Sprite pumpkin:pumpkins2) {
    			 if (blob.intersectsSprite(pumpkin,"other")) {
    				 PUMPKIN_COLLECTED = true;
    				// if gold pumpkin was collected, record time
    				 if (pumpkin.isGold()) {
    					 goldPumpkinCollectTime = System.nanoTime();
    					 auto = true;
					 
    				 }
    				 // if normal pumpkin was collected, increase score by 5
    				 else {
    					 System.out.println("increase points");
    					 score  = score +5;
    				 }
    				// reset pumpkin to initial position and velocity
    				 pumpkin.setPositionXY(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1);
    				 pumpkin.setVelocity(0, 0);
    			 }
    			 for (Sprite ghost:ghosts2) {
    				 if (ghost.intersectsSprite(pumpkin,"other")) {
    					 pumpkin.setPositionXY(2000, 2000);
    					 score --;
    				 }
    			 }
    		 }
    			 
    		// if pumpkin was collected, play witch laugh
    		 DEF.witchLaughMP.setCycleCount(1);
    		 if (PUMPKIN_COLLECTED) {
    			 DEF.witchLaughMP.stop();
    			 DEF.witchLaughMP.play();
    			 PUMPKIN_COLLECTED = false;
    		 }
    	 }
    	 
    	 /**
    	  * increase score by 1 every time witch passes a candle, depending on game mode
    	  * @param difficultyLevel
    	  */
    	 public void candleScore(String difficultyLevel) {
    		 // if on survival mode, use candlesSurvival arraylist
    		 if (difficultyLevel == "survival") {
    			 for (Sprite candle:candlesSurvival) {
    				 // if witch passes top candle, increase score by 1
        			 if (candle.getWidth() == 60) {
        				 if (candle.getPositionX() == (blob.getPositionX())) {
        					 score = score + 1;
        				 }
        			 }
        		 }
    		 }
    		// if on another mode, use candles arraylist
    		 else {
    			 for (Sprite candle:candles) {
    				// if witch passes a candle, increase score by 1
    				 if (candle.getPositionX() == (blob.getPositionX()) && candle.getWidth()==DEF.CANDLE_HOLDER_SQUARE) {
    					 score = score + 1;
    				 }
    			 }
    		 }
    	 }
    	 
    	 /**
    	  * check if lives is decreasing below 0. If yes, end and reset game.
    	  */
    	 public void checkNoLives() {
    		 
    		 if (lives < 0) {
    			 
    			gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
    			
           	    // display game over and restart slogans
           	  	gameOverSlogen.setText("GAME OVER");
           	  	startSlogen.setText("Press SPACE or the 'Go!'"+"\n"+"     Button to Restart.");
           	  	
           	  	// reset pumpkins and ghosts
           	  	for (Pumpkin pumpkin:pumpkins) {
           	  		pumpkin.setPositionXY(DEF.SCENE_WIDTH+2 + DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1);
           	  	}
           	  	for (Ghost ghost:ghosts) {
           	  		ghost.setPositionXY(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1);
           	  	}
           	  	
           	  	lives = 3;
           	  	score = 0;
 			
           	  	GAME_OVER = true;
    			RESTART = false; 
    			
    		 }
    	 }
    	 
    	 /**
    	  * check if ghosts collected pumpkins and decrease score accordingly
    	  */
    	 private void checkGhostCollectPumpkin(){
    		 for (Pumpkin pumpkin:pumpkins) {
    			 for (Ghost ghost:ghosts) {
    				 if (ghost.intersectsSprite(pumpkin,"survival")) {
    					 // reset the pumpkin
    					 pumpkin.setPositionXY(DEF.SCENE_WIDTH+2+DEF.GHOST_WIDTH, DEF.SCENE_HEIGHT+1);
    					 pumpkin.setVelocity(0, 0);
        	    		 pumpkin.makeNormal();
        	    		 pumpkin.setImage(DEF.IMAGE.get("normalpumpkin"));
        	    		 
    					 decreaseScore = true;
    				 }
    			 }
    		 }
    		 // if ghost collected pumpkin, decrease score by 3
    		 if (decreaseScore) {
				 score = score - 3;
				 decreaseScore = false;
			 } 
    	 }

    	 /**
    	  * when the witch collides with a candle, show bounce back effect
    	  */
    	 private void showCollEffect() {
    		 // get the position of the blob
    		 double posBlobX = blob.getPositionX();
    		 double posBlobY = blob.getPositionY();
    		 // stop the bounce back when the witch hits the ground
    		 	if((CANDLESCOLL || GHOSTSCOLL)&&blob.getPositionY() <= ( DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT-40)) {   
    		 		// set witch image to appropriate image
					 blob.setImage(DEF.IMAGE.get("1-f"));
					 
					 // change witch position and velocity for bounce back effect
					 blob.setPositionXY(posBlobX,posBlobY);
					 blob.setVelocity(-2, 2);
		    		 blob.update(DEF.SCENE_SHIFT_TIME);
		    		 blob.render(gc);
        		 
    		 	}
    		 	//show hit effect
    		 	else { 
    		 		GHOSTSCOLL= false;
    		 		CANDLESCOLL = false;
	       			 showHitEffect();
	       			 timer.stop();
	       			 if (lives ==0) {
	       			gc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT); 
	       			gameOverSlogen.setText("GAME OVER");
	           	  	startSlogen.setText("Press SPACE or the 'Go!'"+"\n"+"     Button to Restart.");}
       			 }
    	 }
    	
    	 /**
    	  * show flashing hit effect
    	  */
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