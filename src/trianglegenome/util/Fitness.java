package trianglegenome.util;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import trianglegenome.Constants;
import trianglegenome.Genome;

/**
 * Fitness utility with different calculators for a set image
 *
 */
public class Fitness
{
  private int[] targetPixels;
  private WritableRaster targetRaster;
  private FitnessType type;
  private BufferedImage imageBuf;
  private BufferedImage targetImage;
  int width;
  int height;

  /**
   * Types of available fitness calculators
   */
  public enum FitnessType
  {
    MANHATTAN,
    DISTANCE_SQUARED
  }

  /**
   * Create a new fitness calculator
   * 
   * @param target target image
   * @param imageBuf an image buffer to use
   * @param type type of fitness calculation
   */
  public Fitness(BufferedImage target, BufferedImage imageBuf, FitnessType type)
  {
    this.type = type;
    this.imageBuf = imageBuf;
    this.width = target.getWidth();
    this.height = target.getHeight();
    targetPixels = target.getRaster().getPixels(0, 0, width, height, targetPixels);
    targetRaster = target.getRaster();
    this.targetImage = new BufferedImage(width, height, Constants.BUF_IMG_TYPE);
    Graphics g = this.targetImage.createGraphics();
    g.drawImage(target, 0, 0, null);

  }

  /**
   * BROKEN:
   * 
   * @TODO FIX
   * 
   *       Find fitness for bounding rectangle of a gene
   * @param genome genome
   * @param gene gene for bounding rect
   * @return fitness of bounding rectangle on image
   */
  public long findRectFitness(Genome genome, int gene)
  {
    long fitness = 0;
    genome.drawImage(imageBuf);
    int[] rect = genome.getGeneBoundingRect(gene);
    switch (type)
    {
      case MANHATTAN:
        fitness = rectManhattanDistance(imageBuf, rect);
        break;
      case DISTANCE_SQUARED:
        fitness = rectDistanceSquared(imageBuf, rect);
        break;
      default:
        break;
    }
    return fitness;
  }

  /**
   * Find manhattan fitness on bounding rectangle
   * 
   * @param imageBuf image buffer to get fitness from
   * @param rect rectangle to get fitness from
   * @return fitness
   */
  private long rectManhattanDistance(BufferedImage imageBuf, int[] rect)
  {
    WritableRaster imageRaster = imageBuf.getRaster();
    long distance = 0;
    int[] pixels = new int[3];
    int[] targetPixels = new int[3];
    for (int i = rect[0]; i <= rect[2]; i++)
    {
      for (int j = rect[1]; j <= rect[3]; j++)
      {
        pixels = imageRaster.getPixel(i, j, pixels);
        targetPixels = targetRaster.getPixel(i, j, targetPixels);
        for (int b = 0; b < 3; b++)
        {
          distance += Math.abs(pixels[b] - targetPixels[b]);
        }
      }
    }
    return distance;
  }

  /**
   * Find distance squared fitness on bounding rectangle
   * 
   * @param imageBuf image buffer to get fitness from
   * @param rect rectangle to get fitness from
   * @return fitness
   */
  private long rectDistanceSquared(BufferedImage imageBuf, int[] rect)
  {
    WritableRaster imageRaster = imageBuf.getRaster();
    long distance = 0;
    int subVal = 0;
    int[] pixels = new int[3];
    int[] targetPixels = new int[3];
    for (int i = rect[0]; i <= rect[2]; i++)
    {
      for (int j = rect[1]; j <= rect[3]; j++)
      {
        pixels = imageRaster.getPixel(i, j, pixels);
        targetPixels = targetRaster.getPixel(i, j, targetPixels);
        for (int b = 0; b < 3; b++)
        {
          subVal = pixels[b] - targetPixels[b];
          distance += subVal * subVal;
        }
      }
    }
    return distance;
  }

  /**
   * Find full fitness of a genome
   * 
   * @param genome genome
   * @return fitness
   */
  public long findFitness(Genome genome)
  {
    // long start = System.nanoTime();
    long fitness = 0;
    genome.drawImage(imageBuf);
    switch (type)
    {
      case MANHATTAN:
        fitness = manhattanDistance(imageBuf);
        break;
      case DISTANCE_SQUARED:
        fitness = distanceSquared(imageBuf);
        break;
      default:
        break;
    }
    // long end = System.nanoTime();
    // System.out.println("Fitness time: " + (end - start));
    return fitness;
  }

  /**
   * Find the manhattan distance from target
   * 
   * @param image image to calculate fitness of
   * @return fitness
   */
  public int manhattanDistance(BufferedImage image)
  {
    WritableRaster imageRaster = imageBuf.getRaster();
    int distance = 0;
    int[] pixels = new int[3];
    int[] targetPixels = new int[3];
    for (int i = 0; i < width; i++)
    {
      for (int j = 0; j < height; j++)
      {
        pixels = imageRaster.getPixel(i, j, pixels);
        targetPixels = targetRaster.getPixel(i, j, targetPixels);
        for (int b = 0; b < 3; b++)
        {
          distance += Math.abs(pixels[b] - targetPixels[b]);
        }
      }
    }
    return distance;
  }

  /**
   * Find the distance squared distance from target
   * 
   * @param image image to calculate fitness of
   * @return fitness
   */
  public long distanceSquared(BufferedImage image)
  {
    WritableRaster imageRaster = imageBuf.getRaster();
    long distance = 0;
    int subVal = 0;
    int[] pixels = new int[3];
    int[] targetPixels = new int[3];
    for (int i = 0; i < width; i++)
    {
      for (int j = 0; j < height; j++)
      {
        pixels = imageRaster.getPixel(i, j, pixels);
        targetPixels = targetRaster.getPixel(i, j, targetPixels);
        for (int b = 0; b < 3; b++)
        {
          subVal = pixels[b] - targetPixels[b];
          distance += (subVal * subVal);
        }
      }
    }
    return distance;
  }
}
