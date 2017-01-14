package trianglegenome.util;

import java.util.Random;

import trianglegenome.Constants;
import trianglegenome.Genome;

/**
 * Crossover utility methods
 * 
 */
public class CrossOver
{
  /**
   * Find a crossover gene
   * 
   * @param g1 genome parent 1
   * @param g2 parent 2
   * @return -1 if no suitable gene, otherwise a crossover gene for sigle point
   *         crossover
   */
  public static int findCrossOverGene(Genome g1, Genome g2)
  {
    int distance = g1.hammingDistance(g2);

    if (distance <= 1) return -1;

    int differences = 0;

    int point = Constants.RANDOM.nextInt(distance - 1) + 1;

    for (int i = 0; i < Constants.GENE_COUNT; i++)
    {
      if (g1.getGene(i) != g2.getGene(i)) differences++;

      if (differences == point) return i;
    }

    return -1;
  }

  /**
   * Performs a single point crossover of two genomes
   * 
   * @param g1 first parent
   * @param g2 second parent
   * @param crossOverGene the gene at which to perform the crossover
   * @return child genomes
   */
  public static Pair<Genome> singlePoint(Genome g1, Genome g2, int crossOverGene)
  {
    Genome child1 = g1.deepCopy();
    Genome child2 = g1.deepCopy();

    for (int i = 0; i < Constants.GENE_COUNT; i++)
    {
      if (i < crossOverGene)
      {
        child1.setGene(i, g2.getGene(i));
      }
      else
      {
        child2.setGene(i, g2.getGene(i));
      }
    }

    return new Pair<Genome>(child1, child2);
  }

  /**
   * Create a uniform cross over choosing randomly from which parent to pick
   * each gene
   * 
   * Uses Constants.RANDOM
   * 
   * @param other The other parent
   * @return child genomes
   */
  public static Pair<Genome> uniform(Genome g1, Genome g2)
  {
    return uniform(g1, g2, Constants.RANDOM);
  }

  /**
   * Create a uniform cross over choosing randomly from which parent to pick
   * each gene
   * 
   * @param g2 The other parent
   * @param rand the random generator to use
   * @return child genomes (opposites)
   */
  public static Pair<Genome> uniform(Genome g1, Genome g2, Random rand)
  {
    Genome child1 = g1.deepCopy();
    Genome child2 = g1.deepCopy();
    for (int i = 0; i < Constants.GENE_COUNT; i++)
    {
      if (rand.nextInt(2) == 0)
      {
        child1.setGene(i, g2.getGene(i));
      }
      else
      {
        child2.setGene(i, g2.getGene(i));
      }
    }

    return new Pair<Genome>(child1, child2);
  }
}
