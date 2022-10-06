import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;

public class PlayerSprite {
    public double x,y,size;
    private Color color;
    private JPanel jPanel;
    Image imgFrame;
    public PlayerSprite(double a, double b, double s, Color c){
        x = a;
        y = b;
        size = s;
        color = c;
    }

    public PlayerSprite(double a, double b, double s, JPanel panel) {
        x = a;
        y = b;
        size = s;
        jPanel = panel;
    }

    public void drawSperite(Graphics2D g2d){
        Rectangle2D.Double sqare = new Rectangle2D.Double(x,y,size,size);
        g2d.setColor(color);
        g2d.fill(sqare);
    }

    public void drawSperite(Graphics g2d, Image img) {
        g2d.drawImage(img, 0, 0, 100, 100, jPanel);
    }

    public void moveH(double n){
        x +=n;
    }
    public  void moveV(double n){
        y+=n;
    }
    public  void setX(double n){
        x = n;
    }
    public void  setY(double n){
        y=n;
    }

    public double getX(){
        return x;
    }
    public double getY(){
        return y;
    }
}
