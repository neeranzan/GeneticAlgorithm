package trianglegenome.util;

import trianglegenome.Genome;

/**
 * Interface to describe a generic mutator for tribes to use for mutations
 *
 */
public interface Mutator
{
  /**
   * Mutates a genome, returns true if mutation was good, false if mutation was
   * bad (if bad genome is reset)
   * 
   * @return true if mutation occured and was good, otherwise false
   */
  public boolean mutateGenome(Genome genome);
}
