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

    
    // canvas for background
    private GraphicsContext gc;	
    private GraphicsContext bgc;
    private GraphicsContext cgc;	
    
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
    private void resetGameScene(boolean firstEntry) {
    	
    	// reset variables
        CLICKED = false;
        GAME_OVER = false;
        GAME_START = false;
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
            ImageView background = DEF.IMVIEW.get("background");
            
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
    	int posA;
    	int posB;
    	int posC =  DEF.SCENE_HEIGHT - (60);
    	int random;
    	
    	// initialize candle
    	for(int i=0; i<DEF.CANDLE_COUNT; i++) {
    		random = (int)(Math.random() * 280) + 100; // 100~380
    		
    		//autoHeight.add(random);
	    		
	    	posA =i* 300+400;
			posB = -(random+80); //70 is the difficulty
			Sprite candleDown = new Sprite(posA, posB,DEF.IMAGE.get("CandlesLong"));
			
			Sprite candleUp = new Sprite(posA, posB, DEF.IMAGE.get("CandlesLongUp"));
			
			Sprite candleBot = new Sprite(posA-15, posC, DEF.IMAGE.get("CandleBottom"));
			
			candleUp.setPositionXY(posA, posB);
			candleUp.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
			
			posB = DEF.SCENE_HEIGHT - (random); 
			candleDown.setPositionXY(posA, posB);
			candleDown.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
			
			candleBot.setVelocity(DEF.SCENE_SHIFT_INCR, 0);
			
			if (random %4 == 0) {
				Pumpkin pumpkin = new Pumpkin(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1, 0, DEF.IMAGE.get("normalpumpkin"), "normal");
	        	pumpkin.render(gc);
	        	pumpkins.add(pumpkin);
	        	
				
			}
			else {
				candleDown.render(cgc);
			}
			
			candleDown.render(cgc);
			candleUp.render(cgc);
			candleBot.render(cgc);
			
			candles.add(candleUp);
			candles.add(candleDown);
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
        // initialize ghosts
        for(int i=0; i<DEF.GHOST_COUNT; i++) {
        	Ghost ghost = new Ghost(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1, 0, 0, DEF.IMAGE.get("ghost"));
        	ghost.render(gc);
        	ghosts.add(ghost);
        }
        
     // initialize pumpkins
       // for(int i=0; i<DEF.PUMPKIN_COUNT; i++) {
        //	Pumpkin pumpkin = new Pumpkin(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1, 0, DEF.IMAGE.get("normalpumpkin"), "normal");
        //	pumpkin.render(gc);
        //	pumpkins.add(pumpkin);
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

    	     if (GAME_START) {
    	    	 // step1: update floor
    	    	 moveFloor();
    	    	 
    	    	 moveCandle();
    	    	 
    	    	 // step2: update blob
    	    	 
    	    	 if (auto == false) {
    	    	 moveBlob();}
    	    	 else if (auto ==true) {autoFly();}
    	    	 
    	    	 checkCollision();
    	    	 
    	    	 controlGhost();
    	    	 
    	    	 controlPumpkin();
    	    	 
    	    	 checkPumpkinCollect();
    	    	 
    	    	 
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
    	 
    	 // step2: update blob
    	 private void moveBlob() {
    		 regularFly();
    		
    		 
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
	        			 pumpkins.get(0).setPositionXY(DEF.SCENE_WIDTH, DEF.PUMPKIN_POS_Y);
	        			 pumpkins.get(0).setVelocity(DEF.PUMPKIN_VEL, 0);
	        			 if(makeGoldPumpkin || true) {
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
	        			 pumpkins.get(1).setPositionXY(DEF.SCENE_WIDTH, DEF.PUMPKIN_POS_Y);
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
        			 pumpkin.setPositionXY(DEF.SCENE_WIDTH+1, DEF.SCENE_HEIGHT+1);
        			 pumpkin.setVelocity(0, 0);
        			 pumpkin.makeNormal();
    			 }
    			 pumpkin.update(elapsedTime * DEF.NANOSEC_TO_SEC);
        		 pumpkin.render(gc);
    		 }

    	 }
    	 
    	 public void autoFly() {
    		 long difftime  = System.nanoTime();
    		 
    		 if (System.nanoTime() - difftime >= 6000000 && auto ==true ) {
    			int imageIndex = Math.floorDiv(counter++, DEF.BLOB_IMG_PERIOD);
      			imageIndex = Math.floorMod(imageIndex, DEF.BLOB_IMG_LEN);
      			blob.setImage(DEF.IMAGE.get("1-"+String.valueOf(imageIndex)));
      			blob.setVelocity(0, DEF.BLOB_FLY_VEL);
    		 }
    		 else {
    			 auto = false;}
    		 
    		 blob.update(elapsedTime * DEF.NANOSEC_TO_SEC);
      		 blob.render(bgc);
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
				GAME_OVER = GAME_OVER || blob.intersectsSprite(floor);
			}
			

			for (Sprite ghost: ghosts) {
				GAME_OVER = GAME_OVER || blob.intersectsSprite(ghost);
			}
			
			
			

			// check collision to candles
			for (Sprite candle: candles) {
				GAME_OVER = GAME_OVER || blob.intersectsSprite(candle);
			}
			
			// end the game when blob hit stuff
			if (GAME_OVER) {
				showHitEffect(); 
				for (Sprite floor: floors) {
					floor.setVelocity(0, 0);
				}
				timer.stop();
			}
			
    	 }
    	 
    	 public void checkPumpkinCollect() {
    		 
    		 for (Pumpkin pumpkin:pumpkins) {
    			 if (blob.intersectsSprite(pumpkin)) {
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

