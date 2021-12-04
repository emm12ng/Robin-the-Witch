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

    public Rectangle2D getBoundary() {
        return new Rectangle2D(positionX -13, positionY, width-25, height);
    }

    public boolean intersectsSprite(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }
    
    /*
    public Rectangle2D getBlobBoundary() {
        return new Rectangle2D(positionX, positionY, width, height);
    }

    public boolean intersectsBlobSprite(Sprite s) {
        return s.getBoundary().intersects(this.getBoundary());
    }
    */
    
    /*
    public Rectangle2D getCandleBoundary() {
        return new Rectangle2D(20, positionY, 10, height);
    }
    
    public boolean intersectsCandleSprite(Sprite s) {
        return s.getBoundary().intersects(this.getCandleBoundary());
    }
    */

    public void update(double time) {
        positionX += velocityX * time;
        positionY += velocityY * time;
    }
    public String getType() {
		return "normal";
    	
    }
}
