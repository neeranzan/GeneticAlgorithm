package trianglegenome.util;

import trianglegenome.Constants;
import trianglegenome.Genome;

/**
 * Basic hill climbing mutator
 * 
 * Uses a delta of magnitude 1 to change a genome
 *
 */
public class HillClimbingMutator implements Mutator
{
  private Fitness fitCalc;

  /**
   * Create a new hill climbing mutator
   * 
   * @param fitCalc fitness calculator to use
   */
  public HillClimbingMutator(Fitness fitCalc)
  {
    this.fitCalc = fitCalc;
  }

  /**
   * Mutate a genome using hill climbing
   * 
   * @param genome to mutate
   */
  @Override
  public boolean mutateGenome(Genome genome)
  {
    // Check for history
    if (genome.previousGene != -1)
    {
      if (!continueClimb(genome)) return false;
    }
    else
    {
      randomClimb(genome);
    }

    return checkForImprovement(genome);
  }

  /**
   * Check if a genome has improved
   * 
   * @param genome genome to check
   * @return true if improved false if reset
   */
  private boolean checkForImprovement(Genome genome)
  {
    if (genome.previousGene == -1) return false;

    long newFitness = fitCalc.findFitness(genome);
    if (newFitness < genome.getFitness())
    {
      genome.setFitness(newFitness);
      genome.successfulClimbs++;
      return true;
    }
    else
    {
      resetClimb(genome);
      return false;
    }
  }

  /**
   * Reset a climb from previous move
   * 
   * @param genome genome to reset
   */
  private static void resetClimb(Genome genome)
  {
    if (genome.previousGene != -1)
    {
      genome.changeGene(genome.previousGene, (-genome.previousDelta));
      if (Math.abs(genome.previousDelta) > 1)
      {
        genome.previousDelta = genome.previousDelta / 2;
        genome.successfulClimbs = 0;
      }
      else
      {
        genome.previousGene = -1;
        genome.previousDelta = 0;
        genome.successfulClimbs = 0;
      }
    }
  }

  /**
   * Randomly climb a genome with delta magnitude 1
   * 
   * @param genome
   */
  private static void randomClimb(Genome genome)
  {
    int randGene = Constants.RANDOM.nextInt(Constants.GENE_COUNT);

    int delta = Constants.RANDOM.nextInt(2);
    if (delta == 0) delta = -1;
    if (genome.canChange(randGene, delta))
    {
      genome.changeGene(randGene, delta);
      genome.previousDelta = delta;
      genome.previousGene = randGene;
    }
  }

  /**
   * Continue a climb using the previous delta
   * 
   * @param genome genome to continue
   * @return true if continued, false if could not continue
   */
  private static boolean continueClimb(Genome genome)
  {
    if (genome.successfulClimbs > 1)
    {
      if (Constants.DEBUG_HILLCLIMB) System.out.println("d " + genome.previousDelta);
      genome.previousDelta += genome.previousDelta;
      if (Constants.DEBUG_HILLCLIMB) System.out.println("Increasing delta " + genome.previousDelta);
      genome.successfulClimbs = 1;
    }

    if (genome.canChange(genome.previousGene, genome.previousDelta))
    {
      genome.oldGeneValue = genome.getGene(genome.previousGene);
      genome.changeGene(genome.previousGene, genome.previousDelta);
      return true;
    }
    else if (Math.abs(genome.previousDelta) > 1)
    {
      genome.previousDelta = genome.previousDelta / 2;
      genome.successfulClimbs = 1;
    }
    else
    {
      genome.previousGene = -1;
      genome.previousDelta = 0;
      genome.successfulClimbs = 0;
    }
    return false;
  }
}
