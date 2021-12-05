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
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Random;



//The Application layer
public class AngryFlappyBird extends Application {
	
	private Defines DEF = new Defines();
    
    // time related attributes
    private long clickTime, startTime, elapsedTime; 
    private long candleHitTime;
    private AnimationTimer timer;
    
    // game components
    private Sprite blob;
    
    private Sprite blobAnimate;
    private ArrayList<Sprite> floors;

    private ArrayList<Ghost> ghosts;
    private ArrayList<Sprite> pumpkins2;

    private ArrayList<Sprite> candles;
    
   
    
    //score and life
    
    private int score;
    private int lives = 3;
    
    private Text scoreText;
    private Text livesText;
    private Text gameOverSlogen;
    private Text startSlogen;

    
    // game flags
    private boolean CLICKED, GAME_START, GAME_OVER,LIVELOSS,RESTART,CANDLESCOLL;
    
    
    // scene graphs
    private Group gameScene;	 // the left half of the scene
    private VBox gameControl;	 // the right half of the GUI (control)

    
    // canvas for background
    private GraphicsContext gc;	
    private GraphicsContext bgc;
    private GraphicsContext cgc;
    private GraphicsContext tgc;
    
    Sprite candle;
    
	
    private ArrayList<Integer> autoHeight;
    
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
       	  gameOverSlogen.setText("GAMEOVER");
          startSlogen.setText("Press SPACE or the 'Go!'"+"\n"+"     Button to Restart.");
          
          
       	  GAME_OVER = false;
       	  RESTART = true;
                
            }
            else if (RESTART) {
            	
            	lives = 3;
            	score = 0;
            	 gameOverSlogen.setText("");
            	resetGameScene(false);
            }
            else if (LIVELOSS) {
            	
            	lives -= 1;
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
        gameControl.getChildren().addAll(DEF.startButton);
      
    }
    
   
    private void resetGameScene(boolean firstEntry) {
    	
    	
    	
    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
        RESTART = false;
        LIVELOSS = false;
        CANDLESCOLL = false;
        floors = new ArrayList<>();

        ghosts = new ArrayList<>();
        pumpkins2 = new ArrayList<>();

        candles = new ArrayList<>();
        
        
        
        
    	if(firstEntry) {
    		// create four canvases
            Canvas canvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            gc = canvas.getGraphicsContext2D();

            Canvas bcanvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            bgc = bcanvas.getGraphicsContext2D();
            
            Canvas ccanvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            cgc = canvas.getGraphicsContext2D();
            
            Canvas tcanvas = new Canvas(DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);
            tgc = canvas.getGraphicsContext2D();
            
            // create a background
            ImageView background = DEF.IMVIEW.get("background");
            
            
            //create score and live display
            scoreText = new Text();
            livesText = new Text();
            gameOverSlogen = new Text();
            startSlogen = new Text();
            
            // create the game scene
            gameScene = new Group();
            gameScene.getChildren().addAll(background, canvas, bcanvas, ccanvas,tcanvas,  scoreText, livesText,gameOverSlogen,startSlogen);
            
           
    	}
    	
    	//initialize live and score
    	
        startSlogen.setX(75);
        startSlogen.setY(260);
        startSlogen.setFont(Font.font("Verdana", 20));
        startSlogen.setFill(Color.WHITE);
    	 
        
    	scoreText.setX(20);
    	scoreText.setY(60);
    	livesText.setX(DEF.SCENE_WIDTH - 120);
    	livesText.setY(DEF.SCENE_HEIGHT - 20);
    	
    	gameOverSlogen.setX(60);
    	gameOverSlogen.setY(200);
    	gameOverSlogen.setFont(Font.font("Verdana", 50));
    	gameOverSlogen.setFill(Color.WHITE);
    	
    	
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
    	
    	
    	int posA;
    	int posB;
    	int posC =  DEF.SCENE_HEIGHT - (60);
    	int random;
    	
    	// initialize candle
    	for(int i=0; i<DEF.CANDLE_COUNT; i++) {
    		random = (int)(Math.random() * 200) + 100; // 100~380
    		
    		//autoHeight.add(random);
	    		
	    	posA =i* DEF.CANDLES_SPLIT+DEF.CANDLES_START;
			posB = -(random+80); //70 is the difficulty
			Sprite candleDown = new Sprite(posA, posB,DEF.IMAGE.get("CandlesLong"));
			
			Sprite candleUp = new Sprite(posA, posB, DEF.IMAGE.get("CandlesLongUp"));
			
			Sprite candleBot = new Sprite(posA-16, posC, DEF.IMAGE.get("CandleBottom"));
			
			Sprite normalPumpkin = new Sprite(posA-36, posC-100, DEF.IMAGE.get("normalpumpkin"));
			
			Sprite goldPumpkin = new Sprite(posA-36, posC-100, DEF.IMAGE.get("goldpumpkin"));
			
			
			
			
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
		
		
		
    	/*
    	
    	for(int i=0; i<DEF.CANDLE_COUNT; i++) {
    		
    		int posA = i * DEF.UP_CANDLE_WIDTH;
    		int posB = 0;
			Sprite candle = new Sprite(posA, posB, DEF.IMAGE.get(DEF.randomCandlePic()));
    		
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
    	}
    	*/
    	       
		//initialize blob
		blob = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("1-0"));
		blob.render(bgc);
		blobAnimate = new Sprite(DEF.BLOB_POS_X, DEF.BLOB_POS_Y,DEF.IMAGE.get("1-f"));
		
        // initialize ghosts
        for(int i=0; i<DEF.GHOST_COUNT; i++) {
        	Ghost ghost = new Ghost(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1, 0, 0, DEF.IMAGE.get("ghost"));
        	ghost.render(gc);
        	ghosts.add(ghost);
        }
        
     // initialize pumpkins2
       // for(int i=0; i<DEF.PUMPKIN_COUNT; i++) {
        //	Pumpkin pumpkin = new Pumpkin(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1, 0, DEF.IMAGE.get("normalpumpkin"), "normal");
        //	pumpkin.render(gc);
        //	pumpkins2.add(pumpkin);
        //}

    	
    	
        
        

        
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
    	     tgc.clearRect(0, 0, DEF.SCENE_WIDTH, DEF.SCENE_HEIGHT);

    	     if (GAME_START) {
    	    	 // step1: update floor
    	    	 
    	    	 startSlogen.setText("");
    	    	 moveFloor();
    	    	 
    	    	 moveCandle();
    	    	 
    	    	 movePumpkin();
    	    	 
    	    	 checkScore();

    	    	 moveBlob();
 
    	    	 checkCollision();
	 
    	    	 checkPumpkinCollect();
    	    	 
    	    	 displayLives();
    	    	 
    	    	 
    	    	 
    	    	 
    	    	 }
    	     else {
    	    	
    	                 startSlogen.setText("Press SPACE or the 'Go!'"+"\n"+"       Button to Start.");
    	             
    	        	 
    	        	 
    	        	 
    	   
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
    	 // update candles
    	 private void moveCandle() {
     		
     		for(int i=0; i<DEF.CANDLE_COUNT; i++) {
     			if (candles.get(i).getPositionX() <= -DEF.CANDLE_COUNT) {
    				double nextX = candles.get((i+1)%DEF.CANDLE_COUNT).getPositionX() + DEF.UP_CANDLE_WIDTH;
    	        	double nextY = DEF.SHORT_CANDLE_HEIGHT;
    	        	candles.get(i).setPositionXY(nextX, nextY);
     			}
     			candles.get(i).render(cgc);
     			candles.get(i).update(DEF.SCENE_SHIFT_TIME);
     		}
     	 }
    	 
    	
    	 
    	 //update pumpkin
    	
    	 private void movePumpkin() {
      		
      		for(int i=0; i<DEF.PUMPKIN_COUNT; i++) {
      			
      			pumpkins2.get(i).render(cgc);
      			pumpkins2.get(i).update(DEF.SCENE_SHIFT_TIME);
      		}
      	 }
    	 
    	 //check score
    	 public  void checkScore() {
    		 
    		 for (Sprite candle: candles) {
    			 if (candle.getWidth()==DEF.CANDLE_HOLDER_SQUARE  && candle.getPositionX() ==DEF.BLOB_POS_X ) {
    				 score ++;
    				 
    			 }
    			 
    		 }
    		 
    	
    		 
    		 
    		 
    	 }
    	 // update live
    	 public void displayLives() {
    		 scoreText.setText(String.valueOf(score));
	    	 livesText.setText(lives + " lives left");
    	 }
    	 //check lives
    	 public void  checkLives() {
    		 for (Sprite candle: candles) {
 				if  (lives != 0 && blob.intersectsSprite(candle)) {
 					LIVELOSS = false;
 					}
 			}
    	 }
    	 
    	 
    	 
    	 // step2: update blob
    	 private void moveBlob() {
    		 if (auto == false) {
    		 regularFly();}
    		 else {autoFly();}
 
    	 }
    	 
    	
    	 
    	
    	 public void autoFly() {
    		 
    		 long diffTime = System.nanoTime() - clickTime;
    		 long ongoingTime = System.nanoTime();
    		 if (ongoingTime < 6) {
			if (auto && diffTime <= DEF.BLOB_DROP_TIME) {
				int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
      			imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
      			blob.setImage(DEF.IMAGE.get("1-"+String.valueOf(imageIndex)));
      			blob.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
			}
			}else {auto = false;}
    		blob.render(bgc);
    		blob.update(elapsedTime*DEF.NANOSEC_TO_SEC);

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
     		blob.render(bgc);

    	 }
    	 
    	 public void checkCollision() {
    		 

    		// check collision  
    		// check if either floors were hit
    		// check collision to floor
			for (Sprite floor: floors) {
				if (!LIVELOSS) {
					
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);}
			}
			

			for (Sprite ghost: ghosts) {
				
				GAME_OVER = GAME_OVER || blob.intersectsSprite(ghost);
			}
			
			// check collision to candles
			for (Sprite candle: candles) {
				if (lives != 0) {
				LIVELOSS = LIVELOSS || blob.intersectsSprite(candle);
				CANDLESCOLL = LIVELOSS|| blob.intersectsSprite(candle);
				
				}
				else {
					
					GAME_OVER = GAME_OVER || blob.intersectsSprite(candle);
					CANDLESCOLL = GAME_OVER || blob.intersectsSprite(candle);
				}
			}

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
				for (Ghost ghost: ghosts) {
					ghost.setVelocity(0, 0);
				}
				for (Sprite pumpkin:pumpkins2) {
					pumpkin.setVelocity(0, 0);
				}
				blob.setVelocity(0,0);
				
				showCollEffect();
				
			}
			
			
				
			
			// end the game when blob hit stuff
			if (GAME_OVER && !LIVELOSS ) {
				
					showCollEffect();
				
				
				lives = 0;
				
				for (Sprite floor: floors) {
					floor.setVelocity(0, 0);
				}
				
				
				
				
			}
			
    	 }
    	 
    	 
    	 
    	 public void checkPumpkinCollect() {
    		 
    		 for (Sprite pumpkin:pumpkins2) {
    			 if (blob.intersectsSprite(pumpkin)) {
    				 System.out.println("pumpkin collected");
    				 System.out.println(pumpkin.isGold());
    				 if (pumpkin.isGold()) {
    					 System.out.println("autopilot");
    					 auto = true;
    					 
    				 }
    				 else {
    					 System.out.println("increase points");
    					 score  = score+1;
    				 }
    				 pumpkin.setPositionXY(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1);
    				 
    				 pumpkin.setVelocity(0, 0);
    			 }
    			 for (Ghost ghost:ghosts) {
    				 if (ghost.intersectsSprite(pumpkin)) {
    					 ghost.stealPumpkin();
    					 
    				 }
    			 }
    		 }
    		 
    	 }
    	 
    	 private void showCollEffect() {
    		 double posBlobX = blob.getPositionX();
    		 double posBlobY = blob.getPositionY();
    		 System.out.println(String.valueOf(posBlobX));
    		 System.out.println(String.valueOf(posBlobY));
    		 if (CANDLESCOLL) {
    		 	if(blob.getPositionY() <= ( DEF.SCENE_HEIGHT - DEF.FLOOR_HEIGHT+20)) {    	
    		 		
    		 	
    			 blob.setImage(DEF.IMAGE.get("1-f"));
    			 
    			 blob.setPositionXY(posBlobX,posBlobY);
    			 blob.setVelocity(-2, 2);
        		 blob.update(DEF.SCENE_SHIFT_TIME);
        		 blob.render(bgc);
        		 
    		 	}else {
       			 showHitEffect();
       			 timer.stop();
       			 }
    		 	
    		 }
        		 else {
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
   
    
    


} // End of AngryFlappyBird Class

