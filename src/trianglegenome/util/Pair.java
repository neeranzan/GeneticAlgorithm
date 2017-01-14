package trianglegenome.util;

/**
 * Utility class to create a Pair of objects for returns
 *
 * @param <T> Object type of the pair
 */
public class Pair<T>
{
  /**
   * First object of the pair
   */
  public T first;

  /**
   * Second object of the pair
   */
  public T second;

  /**
   * Create a new pair
   * 
   * @param first first object
   * @param second second object
   */
  public Pair(T first, T second)
  {
    this.first = first;
    this.second = second;
  }
}
