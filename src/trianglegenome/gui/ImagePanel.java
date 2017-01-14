package trianglegenome.gui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import trianglegenome.Constants;

/**
 * Simple image panel, draws a buffered image to the screen, loads the image
 * from a url
 *
 */
public class ImagePanel extends JPanel
{
  private static final long serialVersionUID = Constants.SERIAL_VERSION_UID;
  private BufferedImage image;
  private int width;
  private int height;

  /**
   * Create a new image panel using a image file
   * 
   * @param imageURL path to image file
   * @throws IOException exception thrown by ImageIO
   */
  public ImagePanel(URL imageURL) throws IOException
  {
    image = ImageIO.read(imageURL);
    width = image.getWidth();
    height = image.getHeight();
    setPreferredSize(new Dimension(width, height));
  }

  /**
   * Blank image image panel
   * 
   * @param width width of image
   * @param height height of image
   */
  public ImagePanel(int width, int height)
  {
    this.width = width;
    this.height = height;
    resetImage(width, height);
  }

  /**
   * Draws image to screen
   */
  @Override
  protected void paintComponent(Graphics g)
  {
    super.paintComponent(g);
    g.drawImage(image, 0, 0, null);
  }

  /**
   * Set the BufferedImage manually updates width and height
   * 
   * @param image new image for the image panel
   */
  public void setImage(BufferedImage image)
  {
    this.image = image;
    this.width = image.getWidth();
    this.height = image.getHeight();
    repaint();
  }

  /**
   * Get the current image for the image panel
   * 
   * @return current image
   */
  public BufferedImage getImage()
  {
    return image;
  }

  /**
   * Get the current image's width
   * 
   * @return current width
   */
  public int getWidth()
  {
    return width;
  }

  /**
   * Get the current image's height
   * 
   * @return current height
   */
  public int getHeight()
  {
    return height;
  }

  /**
   * Reset to a blank image of width and height
   * 
   * @param width width
   * @param height height
   */
  public void resetImage(int width, int height)
  {
    this.width = width;
    this.height = height;
    image = new BufferedImage(width, height, Constants.BUF_IMG_TYPE);
    Graphics g = image.getGraphics();
    g.setColor(Color.white);
    g.fillRect(0, 0, width, height);
    setPreferredSize(new Dimension(width, height));
  }
}
