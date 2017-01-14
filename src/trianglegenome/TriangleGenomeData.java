package trianglegenome;

import java.awt.image.BufferedImage;

/**
 * Global data share class
 * 
 * @author humagain
 *
 */
public class TriangleGenomeData
{
  public BufferedImage bestImage;
  public int generations;
  public int improvements;
  public int crossovers;
  public long fitness;
  public double averageHammDist;
  public Genome bestGenomeCopy;

  /**
   * Create a new triangle genome for specific number of tribes
   * 
   * @param numberOfTribes
   * @param width
   * @param height
   */
  public TriangleGenomeData(int numberOfTribes, int width, int height)
  {
    bestImage = new BufferedImage(width, height, Constants.BUF_IMG_TYPE);
  }
}
