package trianglegenome;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import javax.swing.SwingUtilities;

import trianglegenome.gui.TriangleGenomeViewer;
import trianglegenome.util.Fitness;
import trianglegenome.util.Fitness.FitnessType;

/**
 * Main triangle genome class, runs all tribes
 *
 */
public class TriangleGenome implements Runnable
{
  private BufferedImage targetImage;
  private List<Tribe> tribes;
  private List<Thread> tribeThreads;
  private final int numberOfTribes;
  private volatile boolean threadSuspended = false;
  private int bestGenomeTribe;
  private long bestGenomeFitness;
  private int generations = 0;
  private int genStep = 0;
  private static final int GEN_STEP_SIZE = 100;
  private static final int BREED_STEP_MULT = 200;
  private final int BREED_STEP_SIZE;
  private int breedStep;
  private List<TribeData> tribeDataList;
  private int improvements = 0;
  private TriangleGenomeData data;
  private int initialPopulation;
  private int maximumPopulation;
  private int crossovers = 0;

  /**
   * Create a new Triangle genome with specified number of tribes
   * 
   * @param target the target image for the genome
   * @param numberOfTribes the number of tribes to create
   */
  public TriangleGenome(BufferedImage target, int numberOfTribes, TriangleGenomeData data,
      int initialPopulation, int maximumPopulation)
  {
    this.initialPopulation = initialPopulation;
    this.maximumPopulation = maximumPopulation;
    targetImage = target;
    tribeDataList = new ArrayList<>(numberOfTribes);
    for (int i = 0; i < numberOfTribes; i++)
    {
      tribeDataList.add(new TribeData(target.getWidth(), target.getHeight(), maximumPopulation));
    }
    this.numberOfTribes = numberOfTribes;
    tribeThreads = new ArrayList<>(numberOfTribes);
    this.data = data;
    BREED_STEP_SIZE = BREED_STEP_MULT * numberOfTribes;
    breedStep = BREED_STEP_SIZE;

  }

  /**
   * Create the initial population
   * 
   * @param numberOfTribes number of tribes to create
   * @return initial tribe population
   */
  private List<Tribe> createInitialPopulation(int numberOfTribes)
  {
    List<Tribe> tribes = new ArrayList<Tribe>(numberOfTribes);
    int mutatorChoice = 0; // Start with hill climbing only to meet requirements
    for (int id = 0; id < numberOfTribes; id++)
    {
      BufferedImage imageBuf = new BufferedImage(targetImage.getWidth(), targetImage.getHeight(),
          Constants.BUF_IMG_TYPE);
      Fitness fitCalc = new Fitness(targetImage, imageBuf, FitnessType.DISTANCE_SQUARED);
      tribes.add(new Tribe(targetImage, fitCalc, id, tribeDataList.get(id), mutatorChoice, initialPopulation,
          maximumPopulation));
      mutatorChoice = 1 - mutatorChoice;
    }
    return tribes;
  }

  /**
   * Get a tribe from the list of tribes
   * 
   * @param n index for which tribe to get
   * @return tribe
   */
  public Tribe getTribe(int n)
  {
    if (tribes != null)
    {
      return tribes.get(n);
    }
    return null;
  }

  /**
   * Get the number of tribes for this triangle genome
   * 
   * @return number of tribes
   */
  public int getNumberOfTribes()
  {
    return numberOfTribes;
  }

  /**
   * Unpause the thread
   */
  public void unPause()
  {
    threadSuspended = false;
    synchronized (this)
    {
      notify();
    }
    for (Tribe t : tribes)
    {
      if (t.isPaused())
      {
        t.unPause();
      }
    }
  }

  /**
   * Pause the thread
   */
  public void pause()
  {
    threadSuspended = true;
  }

  /**
   * Tell all tribes to perform one next loop
   */
  public void next()
  {
    for (Tribe t : tribes)
    {
      t.next();
    }
    updateData();
  }

  /**
   * Check if pause occured and if so pause all tribes
   * 
   * @throws InterruptedException
   */
  public void checkPauseState() throws InterruptedException
  {
    if (threadSuspended)
    {
      synchronized (this)
      {
        for (Tribe t : tribes)
        {
          t.pause();
        }
        while (threadSuspended)
        {
          wait();
        }
      }
    }
  }

  /**
   * Main run loop
   */
  @Override
  public void run()
  {
    tribes = createInitialPopulation(numberOfTribes);
    boolean running = true;
    for (Tribe t : tribes)
    {
      Thread tt = new Thread(t);
      tribeThreads.add(tt);
      tt.start();
    }
    while (running)
    {
      try
      {
        checkPauseState();
        gatherGenerations();
        updateData();
        setUpBreeding();
        Thread.sleep(500);
      }
      catch (InterruptedException e)
      {
        running = false;
        for (Thread tt : tribeThreads)
        {
          tt.interrupt();
        }
      }
    }
  }

  /**
   * Update data from all tribes for global data
   */
  private void updateData()
  {
    if (generations >= genStep)
    {
      genStep += GEN_STEP_SIZE;
      synchronized (data)
      {
        data.averageHammDist = 0;
        long fitness = Long.MAX_VALUE;
        for (int i = 0; i < numberOfTribes; i++)
        {
          TribeData tribeData = tribeDataList.get(i);
          synchronized (tribeData)
          {
            if (tribeData.fitness[0] <= fitness)
            {
              fitness = tribeData.fitness[0];
              bestGenomeFitness = tribeData.fitness[0];
              bestGenomeTribe = i;
            }
          }

          data.averageHammDist += tribeData.averageHammDist;
        }

        data.averageHammDist /= numberOfTribes;

        TribeData tribeData = tribeDataList.get(bestGenomeTribe);
        synchronized (tribeData)
        {
          Graphics g = data.bestImage.createGraphics();
          g.drawImage(tribeData.bestImage, 0, 0, null);
          data.generations = generations;
          data.improvements = improvements;
          data.crossovers = crossovers;
          data.fitness = fitness;
        }
      }
    }

    if (Constants.DEBUG_GEN) System.out.println("generations " + generations);
    if (Constants.DEBUG_GEN) System.out.println("improvements " + improvements);
  }

  /**
   * Set up breeding for all tribes
   */
  private void setUpBreeding()
  {
    if (numberOfTribes == 1) return;

    if (generations > breedStep)
    {
      breedStep += BREED_STEP_SIZE;

      if (Constants.DEBUG_BREEDING) System.out.println("Setting up breeding!");

      for (Tribe t : tribes)
      {
        t.pause();
      }

      List<Genome> breeders = new ArrayList<>(tribes.size() * initialPopulation);

      // Grab best genome from all
      for (Tribe t : tribes)
      {
        int i = 1;
        breeders.add(t.getGenome(0));
        if (t.getPopulationSize() > 1)
        {
          while (i < Math.min(t.getPopulationSize() - 1, initialPopulation - 1))
          {
            breeders.add(t.getRandomLowerGenome());
            i++;
          }
        }
      }

      if (Constants.DEBUG_BREEDING) System.out.println("Got all breeders!");

      for (Tribe t : tribes)
      {
        t.setBreeders(breeders);
        if (!threadSuspended)
        {
          t.unPause();
        }
      }

      if (Constants.DEBUG_BREEDING) System.out.println("Finished breed set up!");
    }
  }

  /**
   * Gathers information about generations from tribes
   */
  private void gatherGenerations()
  {
    generations = 0;
    improvements = 0;
    crossovers = 0;
    for (TribeData tribeData : tribeDataList)
    {
      synchronized (tribeData)
      {
        for (int i = 0; i < numberOfTribes; i++)
        {
          generations += tribeData.generations;
          crossovers += tribeData.crossovers;
          improvements += tribeData.improvements;
        }
      }
    }
  }

  /**
   * Draws best known genome image
   * 
   * @param image image to draw on
   */
  public void drawBestGenomeImage(BufferedImage image)
  {
    synchronized (data)
    {
      Graphics g = image.createGraphics();
      g.drawImage(data.bestImage, 0, 0, null);
    }
  }

  /**
   * The best known genome tribe
   * 
   * @return
   */
  public int getBestGenomeTribe()
  {
    synchronized (data)
    {
      return bestGenomeTribe;
    }
  }

  /**
   * The best known genome fitness
   * 
   * @return
   */
  public long getBestGenomeFitness()
  {
    synchronized (data)
    {
      return bestGenomeFitness;
    }
  }

  /**
   * Run the trianglegenome program, with gui
   * 
   * @param args
   */
  public static void main(String[] args)
  {
    SwingUtilities.invokeLater(new Runnable()
    {
      public void run()
      {
        TriangleGenomeViewer viewer = new TriangleGenomeViewer();
        viewer.showGUI();
      }

    });
  }
}
