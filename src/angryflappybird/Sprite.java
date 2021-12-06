package angryflappybird;

import javafx.geometry.Rectangle2D;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class Sprite {  
	
    private Image image;
    private double positionX;
    private double positionY;
    private double velocityX;
    private double velocityY;
    private double width;
    private double height;
    private String IMAGE_DIR = "../resources/images/";

    public Sprite() {
        this.positionX = 0;
        this.positionY = 0;
        this.velocityX = 0;
        this.velocityY = 0;
    }
    
    public Sprite(double pX, double pY, Image image) {
    	setPositionXY(pX, pY);
        setImage(image);
        this.velocityX = 0;
        this.velocityY = 0;
    }

    public void setImage(Image image) {
        this.image = image;
        this.width = image.getWidth();
        this.height = image.getHeight();
    }
    
    public Image getImage() {
        return image;
    }
    
    
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public void setHeight(Integer height) {
    	this.height = height;
    }

    public void setPositionXY(double positionX, double positionY) {
        this.positionX = positionX;
        this.positionY = positionY;
    }

    public double getPositionX() {
        return positionX;
    }

    public double getPositionY() {
        return positionY;
    }

    public void setVelocity(double velocityX, double velocityY) {
        this.velocityX = velocityX;
        this.velocityY = velocityY;
    }

    public void addVelocity(double x, double y) {
        this.velocityX += x;
        this.velocityY += y;
    }

    public double getVelocityX() {
        return velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }

    public void render(GraphicsContext gc) {
        gc.drawImage(image, positionX, positionY);
    }

    public Rectangle2D getBoundary(String difficultyLevel) {
    	if (difficultyLevel == "survival") {
        return new Rectangle2D(positionX -13, positionY, width-25, height);
    	}else {
    		return new Rectangle2D(positionX-3 , positionY, width-5, height);
    	}
    	
    }

    public boolean intersectsSprite(Sprite s, String difficultyLevel) {
        return s.getBoundary(difficultyLevel).intersects(this.getBoundary(difficultyLevel));
    }


    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }
    
    public boolean isGold() {
    	return image.getUrl().contains("goldpumpkin.png");
 
    }
}