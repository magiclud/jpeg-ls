package tgaTest;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;


public class ImagePanel extends JPanel{
    Image image;
    ImagePanel(Image img){
        image = img;
        Dimension fixed = new Dimension(img.getWidth(this), img.getHeight(this));
        setSize(fixed);
        setMinimumSize(fixed);
        setMaximumSize(fixed);
        setPreferredSize(fixed);

    }
    @Override
    public void paintComponent(Graphics g){
        g.drawImage(image, 0, 0, this);
    }


}
