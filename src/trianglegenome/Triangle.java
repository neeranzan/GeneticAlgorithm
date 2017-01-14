/**
 * 
 */
package trianglegenome;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * The current Triangle represantation for Genome
 * 
 * Implements Triangle, all overrided methods match the specified information in
 * the Triangle interface
 */
public class Triangle
{
  private int[] xPoints;
  private int[] yPoints;
  private int[] colors;
  private final int width;
  private final int height;

  /**
   * Create a new Triangle given three vertices and a color
   * 
   * @param p1 first vertex
   * @param p2 second vertex
   * @param p3 third vertex
   * @param c Color of the triangle
   * @param width width of the target image
   * @param height height of the target image
   */
  public Triangle(Point p1, Point p2, Point p3, Color c, int width, int height)
  {
    xPoints = new int[3];
    yPoints = new int[3];
    colors = new int[4];
    xPoints[0] = p1.x;
    xPoints[1] = p2.x;
    xPoints[2] = p3.x;
    yPoints[0] = p1.y;
    yPoints[1] = p2.y;
    yPoints[2] = p3.y;
    colors[0] = c.getRed();
    colors[1] = c.getBlue();
    colors[2] = c.getGreen();
    colors[3] = c.getAlpha();
    this.width = width;
    this.height = height;
  }

  /**
   * Create a new Triangle given a list of vertices and a color
   * 
   * @param vertices list of vertices
   * @param c color to set
   * @param width width of the target image
   * @param height height of the target image
   */
  public Triangle(List<Point> vertices, Color c, int width, int height)
  {
    for (int i = 0; i < 3; i++)
    {
      xPoints[i] = vertices.get(i).x;
      yPoints[i] = vertices.get(i).y;
    }
    colors[0] = c.getRed();
    colors[1] = c.getBlue();
    colors[2] = c.getGreen();
    colors[4] = c.getAlpha();
    this.width = width;
    this.height = height;
  }

  /**
   * Create a new Triangle given arrays of xPoints, yPoints, and colors
   * 
   * @param xPoints x coords of vertices
   * @param yPoints y coords of vertices
   * @param colors color values r g b a
   * @param width width of the target image
   * @param height height of the target image
   */
  public Triangle(int[] xPoints, int[] yPoints, int[] colors, int width, int height)
  {
    this.xPoints = xPoints.clone();
    this.yPoints = yPoints.clone();
    this.colors = colors.clone();
    this.width = width;
    this.height = height;
  }

  /**
   * The current color of the triangle
   * 
   * @return current color
   */
  public Color getColor()
  {
    return new Color(colors[0], colors[1], colors[2], colors[3]);
  }

  /**
   * Current colors of the triangle
   * 
   * @return red green blue alpha array
   */
  public int[] getColors()
  {
    return colors;
  }

  /**
   * Set the color of the triangle
   * 
   * @param c color to be set
   */
  public void setColor(Color c)
  {
    colors[0] = c.getRed();
    colors[1] = c.getBlue();
    colors[2] = c.getGreen();
    colors[4] = c.getAlpha();
  }

  /**
   * Set the colors of the triangle, copies values;
   * 
   * @param c size 4 array of rgba colors
   */
  public void setColors(int[] colors)
  {
    this.colors = colors.clone();
  }

  /**
   * Get the first vertex
   * 
   * @return first vertex of the triangle
   */
  public Point getFirstVertex()
  {
    return new Point(xPoints[0], yPoints[0]);
  }

  /**
   * Set the first vertex
   * 
   * @param p1 point of the first vertex
   */
  public void setFirstVertex(Point p1)
  {
    xPoints[0] = p1.x;
    yPoints[0] = p1.y;
  }

  /**
   * Set the first vertex
   * 
   * @param x x coord
   * @param y y coord
   */
  public void setFirstVertex(int x, int y)
  {
    xPoints[0] = x;
    yPoints[0] = y;
  }

  /**
   * Get the second vertex
   * 
   * @return second vertex of the triangle
   */
  public Point getSecondVertex()
  {
    return new Point(xPoints[1], yPoints[1]);
  }

  /**
   * Set the second vertex
   * 
   * @param p2 point of the second vertex
   */
  public void setSecondVertex(Point p2)
  {
    xPoints[1] = p2.x;
    yPoints[1] = p2.y;
  }

  /**
   * Set the second vertex
   * 
   * @param x x coord
   * @param y y coord
   */
  public void setSecondVertex(int x, int y)
  {
    xPoints[1] = x;
    yPoints[1] = y;
  }

  /**
   * Get the third vertex
   * 
   * @return third vertex of the triangle
   */
  public Point getThirdVertex()
  {
    return new Point(xPoints[2], yPoints[2]);
  }

  /**
   * Set the third vertex
   * 
   * @param p3 point of the third vertex
   */
  public void setThirdVertex(Point p3)
  {
    xPoints[2] = p3.x;
    yPoints[2] = p3.y;
  }

  /**
   * Set the third vertex
   * 
   * @param x x coord
   * @param y y coord
   */
  public void setThirdVertex(int x, int y)
  {
    xPoints[2] = x;
    yPoints[2] = y;
  }

  /**
   * Size 3 list of the triangle vertices
   * 
   * @return vertices in order, size 3 Point list
   */
  public List<Point> getVertices()
  {
    List<Point> vertices = new ArrayList<>(3);
    for (int i = 0; i < 3; i++)
    {
      vertices.add(new Point(xPoints[i], yPoints[i]));
    }
    return vertices;
  }

  /**
   * Set the vertices of the triangle
   * 
   * @param vertices size 3 list in order of vertices
   */
  public void setVertices(List<Point> vertices)
  {
    for (int i = 0; i < 3; i++)
    {
      Point p = vertices.get(i);
      xPoints[i] = p.x;
      yPoints[i] = p.y;
    }
  }

  /**
   * Get a list of color values for the current triangle in order R G B A
   * 
   * @return list of color values
   */
  public List<Integer> getColorsList()
  {
    List<Integer> colorValues = new ArrayList<>(4);
    for (int i = 0; i < 4; i++)
    {
      colorValues.add(colors[i]);
    }
    return colorValues;
  }

  /**
   * Get a gene's value
   * 
   * @param n gene to get
   * @return genes value
   */
  public int getGene(int n)
  {
    if (n < 6)
    {
      if (n % 2 == 0)
      {
        return xPoints[n / 2];
      }
      else
      {
        return yPoints[n / 2];
      }
    }
    else
    {
      return colors[n - 6];
    }
  }

  /**
   * Set a gene to a specific value, does not check if value is in bounds
   * 
   * @param n gene to set
   * @param value new value for the gene
   */
  public void setGene(int n, int value)
  {
    if (n < 6)
    {
      if (n % 2 == 0)
      {
        xPoints[n / 2] = value;
      }
      else
      {
        yPoints[n / 2] = value;
      }
    }
    else
    {
      colors[n - 6] = value;
    }
  }

  /**
   * Check if a gene can be changed in the triangle
   * 
   * @param n gene to change
   * @param delta how much to change
   * @return true if change is valid
   */
  public boolean canChange(int n, int delta)
  {
    if (n < 6)
    {
      if (n % 2 == 0)
      {
        int val = xPoints[n / 2] + delta;
        return val >= 0 && val < width;
      }
      else
      {
        int val = yPoints[n / 2] + delta;
        return val >= 0 && val < height;
      }
    }
    else
    {
      int val = colors[n - 6] + delta;
      return val >= 0 && val < Constants.COLOR_LIMIT;
    }
  }

  /**
   * Change a gene by a specified delta
   * 
   * @param n gene to change
   * @param delta delta to change
   */
  public void changeGene(int n, int delta)
  {
    if (n < 6)
    {
      if (n % 2 == 0)
      {
        xPoints[n / 2] += delta;
      }
      else
      {
        yPoints[n / 2] += delta;
      }
    }
    else
    {
      colors[n - 6] += delta;
    }
  }

  /**
   * 
   * this method return a list of values since does not mater what element is,
   * they are integers. this method is used to make gen's cross over
   * 
   * @param gen
   * @return
   */
  public List<Integer> getElementOfTriangle()
  {
    List<Integer> listOfElemets = new ArrayList<>();
    for (int i = 0; i < 3; i++)
    {
      listOfElemets.add(xPoints[i]);
      listOfElemets.add(yPoints[i]);
    }
    // colors elements
    listOfElemets.addAll(getColorsList());

    return listOfElemets;

  }

  /**
   * Set the traingle's color values from a list of color values in order R G B
   * A
   * 
   */
  public void setColorValues(List<Integer> colorValues)
  {
    for (int i = 0; i < 4; i++)
    {
      colors[i] = colorValues.get(i);
    }
  }

  /**
   * 
   * this method set the elements of the tangle using a a list of integer values
   * this method is used for loading genomes into tribes, it normalizes the
   * genome so all values are valid
   * 
   * @param gen
   * @return
   */
  public void setElementOfTriangle(List<Integer> listOfElements)
  {
    for (int i = 0; i < 3; i++)
    {
      xPoints[i] = listOfElements.get(i * 2);
      yPoints[i] = listOfElements.get(1 + i * 2);
    }
    setColorValues(listOfElements.subList(6, 10));
    makeAllValuesValid();
  }

  /**
   * Set all values to valid values
   */
  private void makeAllValuesValid()
  {
    for (int i = 0; i < 3; i++)
    {
      xPoints[i] = closestInRange(xPoints[i], 0, width - 1);
      yPoints[i] = closestInRange(yPoints[i], 0, height - 1);
      colors[i] = closestInRange(colors[i], 0, Constants.COLOR_LIMIT);
    }
    colors[3] = closestInRange(colors[3], 0, Constants.COLOR_LIMIT);
  }

  /**
   * Set a value to the closest value in a range, if value is greater than max
   * return max if less than min return min otherwise return the value
   * 
   * @param value value to check
   * @param min minimum value
   * @param max maximum value
   * @return
   */
  private static int closestInRange(int value, int min, int max)
  {
    if (value < min)
    {
      return min;
    }
    else if (value > max)
    {
      return max;
    }
    return value;
  }

  /**
   * Draw the triangle
   * 
   * @param g Graphics object to draw on
   */
  public void drawTriangle(Graphics g)
  {
    g.setColor(getColor());
    g.fillPolygon(xPoints, yPoints, 3);
  }

  /**
   * Hamming distance from another triangle
   * 
   * @param other the other triangle to compare
   * @return computed hamming distance
   */
  public int hammingDistance(Triangle other)
  {
    int distance = 0;
    for (int i = 0; i < 3; i++)
    {
      if (xPoints[i] != other.xPoints[i]) distance++;
      if (yPoints[i] != other.yPoints[i]) distance++;
      if (colors[i] != other.colors[i]) distance++;
    }
    if (colors[3] != other.colors[3]) distance++;
    return distance;
  }

  /**
   * Checks if a triangle is valid in the limits of width and height, and all
   * color values are under COLOR_LIMIT
   * 
   * @param width width of target image
   * @param height height of target image
   * @return true if valid, false if not
   */
  public boolean valid()
  {
    // Check color values
    for (int i = 0; i < 4; i++)
    {
      if (colors[i] >= Constants.COLOR_LIMIT || colors[i] < 0)
      {
        if (Constants.DEBUG_VALID)
        {
          System.out.println("Wrong color " + i);
        }
        return false;
      }
    }

    // Check vertices
    for (int i = 0; i < 3; i++)
    {
      if (xPoints[i] >= width || xPoints[i] < 0 || yPoints[i] >= height || yPoints[i] < 0)
      {
        if (Constants.DEBUG_VALID)
        {
          System.out.println("Wrong vertices " + xPoints[i] + " " + yPoints[i]);
          System.out.println("Image size: " + width + " x " + height);
        }
        return false;
      }
    }

    return true;
  }

  /**
   * Check if this triangle shares object references with another triangle
   * 
   * @param other Other triangle to compare with
   * @return true if object are shared, otherwise false
   */
  public boolean shareObjectReferences(Triangle other)
  {
    return xPoints == other.xPoints || yPoints == other.yPoints || colors == other.colors;
  }

  /**
   * Create a deep copy of the triangle and return it
   * 
   * @return new Triangle with all values copied but new objects
   */
  public Triangle deepCopy()
  {
    return new Triangle(xPoints, yPoints, colors, width, height);
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#hashCode()
   */
  @Override
  public int hashCode()
  {
    final int prime = 31;
    int result = 1;
    result = prime * result + Arrays.hashCode(colors);
    result = prime * result + height;
    result = prime * result + width;
    result = prime * result + Arrays.hashCode(xPoints);
    result = prime * result + Arrays.hashCode(yPoints);
    return result;
  }

  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Object#equals(java.lang.Object)
   */
  @Override
  public boolean equals(Object obj)
  {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    Triangle other = (Triangle) obj;
    if (!Arrays.equals(colors, other.colors)) return false;
    if (height != other.height) return false;
    if (width != other.width) return false;
    if (!Arrays.equals(xPoints, other.xPoints)) return false;
    if (!Arrays.equals(yPoints, other.yPoints)) return false;
    return true;
  }

  /**
   * Create a random Triangle that fits within the width and height. Uses the
   * random generator in constants
   * 
   * @param width maximum width
   * @param height maximum height
   * @return randomly generated Triangle
   */
  public static Triangle createRandom(int width, int height)
  {
    return createRandom(width, height, Constants.RANDOM);
  }

  /**
   * Create a random Triangle that fits within the width and height. Uses
   * supplied random generator
   * 
   * @param width maximum width
   * @param height maximum height
   * @param rand random generator
   * @return randomly generated Triangle
   */
  public static Triangle createRandom(int width, int height, Random rand)
  {
    int[] xPoints = new int[3];
    int[] yPoints = new int[3];
    int[] colors = new int[4];
    for (int i = 0; i < 3; i++)
    {
      xPoints[i] = rand.nextInt(width);
      yPoints[i] = rand.nextInt(height);
      colors[i] = rand.nextInt(Constants.COLOR_LIMIT);
    }
    colors[3] = rand.nextInt(Constants.COLOR_LIMIT);
    return new Triangle(xPoints, yPoints, colors, width, height);
  }

  /**
   * Create a random Triangle with a set specific alpha
   * 
   * @param width maximum width
   * @param height maximum height
   * @param rand random generator
   * @param alpha set alpha to use
   * @return randomly generated Triangle
   */
  public static Triangle createRandomFixedAlpha(int width, int height, Random rand, int alpha)
  {
    int[] xPoints = new int[3];
    int[] yPoints = new int[3];
    int[] colors = new int[4];
    for (int i = 0; i < 3; i++)
    {
      xPoints[i] = rand.nextInt(width);
      yPoints[i] = rand.nextInt(height);
      colors[i] = rand.nextInt(Constants.COLOR_LIMIT);
    }
    colors[3] = alpha;
    return new Triangle(xPoints, yPoints, colors, width, height);
  }

  public int[] getBoundingRect()
  {
    int[] rect = new int[4];
    int minx = xPoints[0];
    int miny = yPoints[0];
    int maxx = xPoints[0];
    int maxy = yPoints[0];

    for (int i = 1; i < 3; i++)
    {
      minx = Math.min(minx, xPoints[i]);
      miny = Math.min(miny, yPoints[i]);
      maxx = Math.max(maxx, xPoints[i]);
      maxy = Math.max(maxy, yPoints[i]);
    }

    rect[0] = minx;
    rect[1] = miny;
    rect[2] = maxx - minx;
    rect[3] = maxy - miny;
    return rect;
  }
}
