package trianglegenome.util;

import trianglegenome.Constants;
import trianglegenome.Genome;

/**
 * A more random hill climbing mutator, makes random change and uses that delta
 * to climb a hill if it was good
 *
 */
public class RandomMutator implements Mutator
{
  private Fitness fitCalc;
  private int width;
  private int height;

  /**
   * Create a new random mutator
   * 
   * @param fitCalc calculator to use
   * @param width width of target image
   * @param height height of target image
   */
  public RandomMutator(Fitness fitCalc, int width, int height)
  {
    this.fitCalc = fitCalc;
    this.width = width;
    this.height = height;
  }

  /**
   * Mutate a genome using random + hill climbing
   */
  @Override
  public boolean mutateGenome(Genome genome)
  {
    // Check for history
    if (genome.previousGene != -1)
    {
      if (!continueClimb(genome)) return false;
      if (Constants.DEBUG_VALID)
      {
        if (!Genome.validGenome(genome)) System.err.println("Invalid genome continue climb");
      }
    }
    else
    {
      randomClimb(genome);
      if (Constants.DEBUG_VALID)
      {
        if (!Genome.validGenome(genome)) System.err.println("Invalid genome random climb");
      }
    }

    return checkForImprovement(genome);
  }

  /**
   * Check if genome has improved otherwise reset
   * 
   * @param genome genome to check
   * @return true if improved, false if reset
   */
  private boolean checkForImprovement(Genome genome)
  {
    if (genome.previousGene == -1) return false;

    long newFitness = fitCalc.findFitness(genome);
    if (newFitness < genome.getFitness())
    {
      genome.setFitness(newFitness);
      return true;
    }
    else
    {
      resetClimb(genome);
      return false;
    }
  }

  /**
   * Reset the mutation
   * 
   * @param genome genome to reset
   */
  private static void resetClimb(Genome genome)
  {
    genome.setGene(genome.previousGene, genome.oldGeneValue);
    if (genome.previousDelta != 0)
    {
      genome.previousDelta = genome.previousDelta / 2;
    }
    else
    {
      genome.previousGene = -1;
      genome.previousDelta = 0;
    }
  }

  /**
   * Try full random gene changes
   * 
   * @param genome genome to change
   */
  private void randomClimb(Genome genome)
  {
    int randGene = Constants.RANDOM.nextInt(Constants.GENE_COUNT);
    int newVal = randVal(randGene);
    genome.oldGeneValue = genome.getGene(randGene);
    genome.setGene(randGene, newVal);
    int delta = newVal - genome.oldGeneValue;
    genome.previousDelta = delta;
    genome.previousGene = randGene;
  }

  /**
   * Continue a climb
   * 
   * @param genome genome to continue
   * @return true if continued false if couldn't
   */
  public boolean continueClimb(Genome genome)
  {
    if (genome.canChange(genome.previousGene, genome.previousDelta))
    {
      genome.oldGeneValue = genome.getGene(genome.previousGene);
      genome.changeGene(genome.previousGene, genome.previousDelta);
      return true;
    }
    else if (Math.abs(genome.previousDelta) > 1)
    {
      genome.previousDelta = genome.previousDelta / 2;
    }
    else
    {
      genome.previousGene = -1;
      genome.previousDelta = 0;
    }
    return false;

  }

  /**
   * Get a valid random value from a gene to mutate
   * 
   * @param geneToMutate gene to generate value for
   * @return
   */
  public int randVal(int geneToMutate)
  {
    int geneIndex = geneToMutate % 10;
    int limit = 0;
    if (geneIndex < 6)
    {
      if (geneIndex % 2 == 0)
      {
        limit = width;
      }
      else
      {
        limit = height;
      }
    }
    else
    {
      limit = Constants.COLOR_LIMIT;
    }
    return Constants.RANDOM.nextInt(limit);
  }
}
