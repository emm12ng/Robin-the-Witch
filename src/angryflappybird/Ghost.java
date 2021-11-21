package angryflappybird;

import javafx.scene.image.Image;

public class Ghost extends Sprite {  
	
	private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    
    public Ghost(double pX, double pY, double vX, double vY, Image image) {
    	super(pX, pY, image);
    	this.velocityX = vX;
    	this.velocityY = vY;
    }
    
    public void stealPumpkin() {
    	this.setVelocity(this.velocityX, -this.velocityY); 
    }
	
}
