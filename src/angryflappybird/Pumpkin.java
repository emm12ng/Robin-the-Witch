package angryflappybird;

import javafx.scene.image.Image;

public class Pumpkin extends Sprite {  
	
	/**
	 * Pumpkin class extends Sprite class. Contains the static variable type, which stores whether pumpkin is 
	 * normal or gold, and methods to change and get the type.
	 */
	
	private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private String IMAGE_DIR = "../resources/images/";
    private String type;
    
    public Pumpkin(double pX, double pY, double vX, Image image, String t) {
    	super(pX, pY, image);
    	this.velocityX = vX;
    	this.type = t;
    }
    
    public Pumpkin(double pX, double pY, Image image) {
    	super(pX, pY, image);
    }
    
    /**
     * make the pumpkin type gold
     */
    public void makeGold() {
    	this.type = "gold";
    }
    
    /**
     * make pumpkin type normal
     */
    public void makeNormal() {
    	this.type = "normal";
    }
    
    /**
     * return the type of the pumpkin
     * @return type (String)
     */
    public String getType() {
    	return type;
    }
	
}
