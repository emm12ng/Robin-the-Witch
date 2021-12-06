package angryflappybird;

import javafx.scene.image.Image;

public class Pumpkin extends Sprite {  
	
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
    
    public void isStolen(double ghostVelocity) {
    	this.positionY = this.positionY - 50;
    	this.setVelocity(this.velocityX, -ghostVelocity);
    }
    
    public void makeGold() {
    	this.type = "gold";
    }
    
    public void makeNormal() {
    	this.type = "normal";
    }
    
    public String getType() {
    	return type;
    }
	
}
