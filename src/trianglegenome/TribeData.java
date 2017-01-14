package trianglegenome;

import java.awt.image.BufferedImage;

/**
 * Class which keeps track of data for each tribe, message passing from tribe to
 * trianglegenome
 * 
 * @author wortiz
 *
 */
public class TribeData
{
  public BufferedImage bestImage;
  public int bestImageIndex;
  public int improvements;
  public int generations;
  public int crossovers;
  public long[] fitness;
  public int populationSize;
  public double averageHammDist;

  /**
   * Create a new tribe data object
   * 
   * @param width width of target
   * @param height height of target
   */
  public TribeData(int width, int height, int maximumPopulation)
  {
    bestImage = new BufferedImage(width, height, Constants.BUF_IMG_TYPE);
    improvements = 0;
    generations = 0;
    fitness = new long[maximumPopulation];
    populationSize = 0;
    averageHammDist = 0;
  }
}
