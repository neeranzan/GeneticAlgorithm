package trianglegenome.tests;

import java.awt.Point;

import trianglegenome.Constants;
import trianglegenome.Genome;
import trianglegenome.Triangle;

public class GenomeTests
{
  /**
   * Checks whether a single point cross over was done correctly
   * 
   * TODO: Implement
   * 
   * @param p1 first parent
   * @param p2 second parent
   * @param c1 first child
   * @param c2 second child
   * @param crossOverGene the gene given to the single point crossover
   * @return True if single point cross over was done correctly, false otherwise
   */
  public static void checkCorrectCrossOver(Genome parent1, Genome parent2, Genome child1, Genome child2,
      int crossOverGene)
  {
    for (int i = 0; i < Constants.GENE_COUNT; i++)
    {
      if (i < crossOverGene)
      {
        assert child1.getGene(i) != parent2.getGene(i);
        assert child2.getGene(i) != parent1.getGene(i);
      }
      else
      {
        assert child1.getGene(i) != parent1.getGene(i);
        assert child2.getGene(i) != parent2.getGene(i);
      }

    }
  }

  /**
   * Checks that two parents are valid input and the distance is valid
   * 
   * @param p1 first parent
   * @param p2 second parent
   * @param distance the Hamming Distance used for the cross over
   * @return true if parents are valid, false if: parents are identical, parents
   *         genes share addresses, the crossover point is invalid
   */
  public static void checkValidCrossOverInput(Genome parent1, Genome parent2, int distance)
  {
    // check if parents are identical or the same object
    assert !(parent1 == parent2 || parent1.equals(parent2));

    // Check for shared object references for genes (Expensive)
    for (Triangle p1triangle : parent1.getTriangles())
    {
      for (Triangle p2triangle : parent2.getTriangles())
      {
        assert !(p1triangle.shareObjectReferences(p2triangle));
      }
    }

    // Check that the hamming distance point is correct for this crossover
    int computedDistance = parent1.hammingDistance(parent2);

    // Need at least two differences to be valid
    assert (computedDistance > 1);

    // Check valid distances
    assert (distance > 0 || distance < computedDistance);

  }

  /**
   * Checks for a valid cross over output
   * 
   * @param p1
   * @param p2
   * @param inputChild1
   * @param inputChild2
   * @param outputChild1
   * @param outputChild2
   * @return true if and only if the addresses of the input children are the
   *         same as the output children, and the genes of the input children
   *         differ from both each other and their parents
   */
  public static void checkValidCrossOverOutput(Genome parent1, Genome parent2,
      Genome child1, Genome child2)
  {
    // Check that children do not equal each other or their parents
    assert !child1.equals(child2);

    assert !(child1.equals(parent1) || child1.equals(parent2));

    assert !(child2.equals(parent1) || child2.equals(parent2));

    // Check for shared object references for genes in the children (Expensive)
    for (Triangle c1Tri : child1.getTriangles())
    {
      for (Triangle c2Tri : child2.getTriangles())
      {
        assert !(c1Tri.shareObjectReferences(c2Tri));
      }
    }
  }

  /**
   * Checks a genome with assert statements that all values are in the correct
   * ranges
   * 
   * @param g Genome to check
   * @param width width of the target image
   * @param height height of the target image
   */
  public static void checkValidGenome(Genome g, int width, int height)
  {
    assert g.getTriangles().size() == Constants.TRIANGLE_COUNT;

    assert g.getWidth() == width;

    assert g.getHeight() == height;

    for (Triangle triangle : g.getTriangles())
    {
      // Check vertices
      for (Point p : triangle.getVertices())
      {
        assert p.x >= 0 && p.x < width;
        assert p.y >= 0 && p.y < height;
      }

      for (Integer colorVal : triangle.getColorsList())
      {
        assert colorVal >= 0 && colorVal < Constants.COLOR_LIMIT;
      }
    }
  }

  public static void main(String[] args)
  {

  }
}
