package trianglegenome.tests;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;

import javax.imageio.ImageIO;

import trianglegenome.Constants;
import trianglegenome.util.Fitness;
import trianglegenome.util.Fitness.FitnessType;

public class FitnessTests
{

  /**
   * Convert an image to the specified format for the project
   * 
   * @param image
   * @return
   */
  public static BufferedImage convertToGenomeFormat(BufferedImage image)
  {
    BufferedImage newBuf = new BufferedImage(image.getWidth(), image.getHeight(), Constants.BUF_IMG_TYPE);
    Graphics2D g = newBuf.createGraphics();
    g.drawImage(image, 0, 0, null);
    return newBuf;
  }

  /**
   * Run some basic assert tests
   * 
   * @param args
   * @throws IOException
   */
  public static void main(String[] args) throws IOException
  {
    FitnessTests fitTests = new FitnessTests();
    BufferedImage targetImage = convertToGenomeFormat(ImageIO.read(fitTests.getClass().getClassLoader()
        .getResource(
            "testfiles/clean.png")));
    BufferedImage imageBuf = convertToGenomeFormat(ImageIO.read(fitTests.getClass().getClassLoader()
        .getResource(
            "testfiles/redpixel.png")));

    Fitness fitCalc = new Fitness(targetImage, imageBuf, FitnessType.MANHATTAN);

    assert fitCalc.manhattanDistance(imageBuf) == 512;
    assert fitCalc.distanceSquared(imageBuf) == 130052;

    targetImage = convertToGenomeFormat(ImageIO.read(fitTests.getClass().getClassLoader()
        .getResource(
            "testfiles/mona-lisa-face-200x200.png")));
    imageBuf = convertToGenomeFormat(ImageIO.read(fitTests.getClass().getClassLoader()
        .getResource(
            "testfiles/mona-lisa-face-redPixel-200x200.png")));

    fitCalc = new Fitness(targetImage, imageBuf, FitnessType.MANHATTAN);

    assert fitCalc.manhattanDistance(imageBuf) == 218;

    assert fitCalc.distanceSquared(imageBuf) == 22246;
  }
}
