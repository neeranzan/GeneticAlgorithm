package trianglegenome;

import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Contains useful constants for the triangle genome project
 *
 */
public class Constants
{
  /**
   * Amount of Triangles to use for a genome
   */
  public static final int TRIANGLE_COUNT = 200;

  /**
   * Total genes
   */
  public static final int GENE_COUNT = TRIANGLE_COUNT * 10;

  /**
   * Default Initial population size for a tribe
   */
  public static final int DEFAULT_INITIAL_POPULATION = 2;

  /**
   * Default Maximum population of a tribe
   */
  public static final int DEFAULT_MAXIMUM_POPULATION = 4;

  /**
   * Random generator
   */
  public static final Random RANDOM = new Random();

  /**
   * All color values are less than this limit
   */
  public static final int COLOR_LIMIT = 256;

  /**
   * Changing this has consequences as the fitness expects a byte image
   */
  public static final int BUF_IMG_TYPE = BufferedImage.TYPE_3BYTE_BGR;

  /**
   * Serial version ID (not currently needed other than for warnings)
   */
  public static final long SERIAL_VERSION_UID = 6398103666993659100L;

  /**
   * Metric for when a genome is not diverse enough
   */
  public static final int DIVERSITY_METRIC = GENE_COUNT / 3;

  /**
   * Debug Constants
   */
  public static final boolean DEBUG_HILLCLIMB = false;
  public static final boolean DEBUG_MUTATOR = false;
  public static final boolean DEBUG_BREEDING = false;
  public static final boolean DEBUG_NAT_SEL = false;
  public static final boolean DEBUG_GEN = false;
  public static final boolean DEBUG_VALID = false;

}
