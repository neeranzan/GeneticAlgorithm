package trianglegenome;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import trianglegenome.util.CrossOver;
import trianglegenome.util.Fitness;
import trianglegenome.util.HillClimbingMutator;
import trianglegenome.util.Mutator;
import trianglegenome.util.Pair;
import trianglegenome.util.RandomMutator;

/**
 * Tribe object for the triangle genome, thread functionality doesn't work right
 * now
 */
public class Tribe implements Runnable
{
  private BufferedImage targetImage;
  private List<Genome> population;
  private List<Genome> genomesToClimb;
  private int improvements = 0;
  private Integer breeds = 0;
  private Fitness fitCalc;
  private Mutator mutator;
  private volatile boolean threadSuspended = false;
  private volatile boolean timeToBreed = false;
  private final int NUMBER_OF_BREEDERS;
  private List<Genome> breeders;
  private int generations = 0;
  private TribeData tribeData;
  private final int id;
  private volatile boolean nextRunCalled;
  private final int initialPopulation;
  private final int maximumPopulation;
  private int crossovers;

  /**
   * Create a new tribe
   * 
   * @param target target image
   */
  public Tribe(BufferedImage target, Fitness fitCalc, int id, TribeData tribeData, int mutatorChoice,
      int initialPopulation, int maximumPopulation)
  {
    targetImage = target;
    this.fitCalc = fitCalc;
    this.id = id;
    this.tribeData = tribeData;
    this.initialPopulation = initialPopulation;
    this.maximumPopulation = maximumPopulation;
    population = createInitialPopulation();
    switch (mutatorChoice)
    {
      case 0:
        mutator = new HillClimbingMutator(fitCalc);
        break;
      case 1:
        mutator = new RandomMutator(fitCalc, targetImage.getWidth(), targetImage.getHeight());
        break;
      default:
        System.out.println("Unexpected Mutator");
        mutator = null;
    }
    genomesToClimb = new ArrayList<Genome>(population);

    NUMBER_OF_BREEDERS = initialPopulation;
    updateTribeData();
  }

  /**
   * Create an initial population for the tribe
   * 
   * @return initial population of genomes
   */
  private List<Genome> createInitialPopulation()
  {
    population = new ArrayList<Genome>(maximumPopulation);
    for (int i = 0; i < initialPopulation; i++)
    {
      Genome g = Genome.randomGenomeFixedAlpha(targetImage, 80);
      if (Constants.DEBUG_VALID)
      {
        if (!Genome.validGenome(g)) System.err.println("Invalid genome created");
      }
      g.setFitness(fitCalc.findFitness(g));
      population.add(g);
    }
    return population;
  }

  /**
   * Get the current population size
   * 
   * @return the population size
   */
  public int getPopulationSize()
  {
    return population.size();
  }

  /**
   * Unpause the thread
   */
  public void unPause()
  {
    synchronized (this)
    {
      threadSuspended = false;
      nextRunCalled = false;
      notify();
    }
  }

  /**
   * Pause the thread
   */
  public void pause()
  {
    synchronized (this)
    {
      threadSuspended = true;
    }
  }

  /**
   * Check if the thread is paused
   * 
   * @return true if paused, false if not
   */
  public boolean isPaused()
  {
    return threadSuspended;
  }

  /**
   * Run the next run loop
   */
  public void next()
  {
    if (!genomesToClimb.isEmpty())
    {
      climbGenes();
    }
    else
    {
      genomesToClimb.addAll(population);
      climbGenes();
      updateTribeData();
    }
  }

  /**
   * Get a specific genome from the population
   * 
   * @param n index for which genome to get
   * @return genome
   */
  public Genome getGenome(int n)
  {
    Collections.sort(population);

    return population.get(n);
  }

  /**
   * Get how many Hill Climbing generations have occurred
   * 
   * @return hill climbing generations
   */
  public int getGenerations()
  {
    return generations;
  }

  /**
   * Get how many crossover breedings have occurred
   * 
   * @return number of cross over breeds
   */
  public int getBreeds()
  {
    return breeds;
  }

  /**
   * Update the tribeData for this tribe
   */
  public void updateTribeData()
  {
    Collections.sort(population);
    Genome g = population.get(0);
    synchronized (tribeData)
    {
      g.drawImage(tribeData.bestImage);
      for (int i = 0; i < Math.min(maximumPopulation, population.size()); i++)
      {
        tribeData.fitness[i] = population.get(i).fitness;
      }
      tribeData.generations = generations;
      tribeData.improvements = improvements;
      tribeData.populationSize = population.size();
      tribeData.averageHammDist = findAverageHammingDistance();
      tribeData.crossovers = crossovers;
    }
  }

  /**
   * Find double value of the average hamming distance
   * 
   * @return avg hamm dist
   */
  private double findAverageHammingDistance()
  {
    double distance = 0;
    int size = population.size();
    for (int i = 0; i < size; i++)
    {
      for (int j = 0; j < size; j++)
      {
        if (i == j) continue;
        distance += population.get(i).hammingDistance(population.get(j));
      }
    }
    return distance / (size * size);
  }

  /**
   * Run the main tribe loop
   */
  @Override
  public void run()
  {
    boolean running = true;
    while (running)
    {
      try
      {
        if (threadSuspended)
        {
          synchronized (this)
          {
            while (threadSuspended)
            {
              wait();
            }
          }
        }

        if (timeToBreed)
        {
          breedGenomes();
          naturalSelection();
        }
        else if (!genomesToClimb.isEmpty())
        {
          climbGenes();
        }
        else
        {
          updateTribeData();
          genomesToClimb.addAll(population);
        }

        if (Thread.interrupted())
        {
          throw new InterruptedException();
        }
      }
      catch (InterruptedException e)
      {
        running = false;
      }
    }
  }

  /**
   * Kill off population if population is too large
   */
  private void naturalSelection()
  {
    while (population.size() > maximumPopulation)
    {
      Collections.sort(population);
      if (Constants.DEBUG_NAT_SEL)
      {
        System.out.println(id + " Best in pop " + population.get(0).getFitness());
        System.out.println(id + " Worst in pop " + population.get(population.size() - 1).getFitness());
        System.out.println(id + " Pop size " + population.size());
      }
      population.remove(population.size() - 1);
      if (Constants.DEBUG_NAT_SEL) System.out.println(id + " Pop size " + population.size());
    }
  }

  /**
   * Climb a few genes
   * 
   * @throws InterruptedException
   */
  private void climbGenes()
  {
    int climbIndex = Constants.RANDOM.nextInt(genomesToClimb.size());
    // System.out.println(population.size());
    Genome g = genomesToClimb.get(climbIndex);
    // System.out.println("climbing " + g);
    for (int i = 0; i < 20; i++)
    {
      generations++;

      // if (randomMutator.mutateGenome(g))
      if (mutator.mutateGenome(g))
      {
        if (Constants.DEBUG_VALID)
        {
          if (!Genome.validGenome(g)) System.err.println(id + " Invalid genome created (mutation)");
        }
        if (Constants.DEBUG_MUTATOR)
        {
          System.out.println(id + " " + generations + " " + improvements
              + " Improved!! Fitness:" + g.getFitness());
        }
        genomesToClimb.remove(climbIndex);
        improvements++;
        return;
      }

    }
  }

  /**
   * Get breeders to use
   * 
   * @return
   */
  public List<Genome> getBreeders()
  {
    List<Genome> myBreeders = new ArrayList<>(NUMBER_OF_BREEDERS);
    Set<Integer> indexes = new TreeSet<Integer>();
    while (myBreeders.size() < NUMBER_OF_BREEDERS)
    {
      int popindex = Constants.RANDOM.nextInt(population.size());
      if (indexes.contains(popindex)) continue;
      myBreeders.add(population.get(popindex).deepCopy());
    }
    if (Constants.DEBUG_BREEDING) System.out.println("My breeders size: " + myBreeders.size());
    return myBreeders;
  }

  /**
   * Set the breeders, should not be called while running
   * 
   * @param otherBreeders
   */
  public void setBreeders(List<Genome> otherBreeders)
  {
    this.breeders = new ArrayList<>(otherBreeders.size());
    for (Genome g : otherBreeders)
    {
      breeders.add(g.deepCopy());
    }
    timeToBreed = true;
  }

  /**
   * Add a genome to the pop
   * 
   * Shouldn't be called while running
   * 
   * @param g genome to add
   * @param geneIndex where to add in population
   */
  public void addGenome(Genome g, int geneIndex)
  {
    g.setFitness(fitCalc.findFitness(g));
    population.add(geneIndex, g);
  }

  /**
   * Breed the genomes that were set up
   */
  private void breedGenomes()
  {
    if (Constants.DEBUG_BREEDING) System.out.println("Breeding genomes " + Thread.currentThread());
    Set<Integer> indexes = new TreeSet<>();
    List<Pair<Genome>> children = new ArrayList<>(NUMBER_OF_BREEDERS);
    for (int count = 0; count < NUMBER_OF_BREEDERS; count++)
    {
      if (Constants.DEBUG_BREEDING) System.out.println("Breeders size: " + breeders.size());
      int b1index = Constants.RANDOM.nextInt(breeders.size());
      int b2index = Constants.RANDOM.nextInt(breeders.size());
      if (indexes.contains(b1index) || indexes.contains(b2index)) continue;
      indexes.add(b1index);
      indexes.add(b2index);

      int type = Constants.RANDOM.nextInt(2);

      Genome b1 = breeders.get(b1index);
      Genome b2 = breeders.get(b2index);

      switch (type)
      {
        case 0:
          if (Constants.DEBUG_BREEDING) System.out.println(id + " Uniform cross over");
          children.add(CrossOver.uniform(b1, b2));
          break;
        case 1:
          if (Constants.DEBUG_BREEDING) System.out.println(id + " Single point cross over");
          int crossOverGene = CrossOver.findCrossOverGene(b1, b2);

          if (crossOverGene == -1)
          {
            if (Constants.DEBUG_BREEDING) System.out.println("Bad cross over gene");
            continue;
          }

          children.add(CrossOver.singlePoint(b1, b2, crossOverGene));
          break;
        default:
          break;

      }
      crossovers++;
    }

    for (Pair<Genome> childs : children)
    {
      childs.first.setFitness(fitCalc.findFitness(childs.first));
      if (Constants.DEBUG_BREEDING) System.out.println("Fitness child: " + childs.first.getFitness());
      childs.second.setFitness(fitCalc.findFitness(childs.second));
      if (Constants.DEBUG_BREEDING) System.out.println("Fitness child: " + childs.second.getFitness());
      addIfDiverse(childs.first);
      addIfDiverse(childs.second);
    }

    if (Constants.DEBUG_BREEDING) System.out.println("Pop size: " + population.size());

    breeders = null;
    timeToBreed = false;
  }

  /**
   * Only add if child is diverse enough, if not replace similar genome with
   * child if it is better
   * 
   * @param child child to add
   */
  private void addIfDiverse(Genome child)
  {
    if (Constants.DEBUG_VALID)
    {
      if (!Genome.validGenome(child)) System.err.println(id + " Invalid genome created (crosover child)");
    }
    for (int i = 0; i < population.size(); i++)
    {
      Genome g = population.get(i);
      if (child.hammingDistance(g) < Constants.DIVERSITY_METRIC)
      {
        if (child.fitness < g.fitness)
        {
          population.set(i, child);
        }
        return;
      }
    }
    population.add(child);
  }

  /**
   * Get genomes that aren't the best
   * 
   * @return non best genome
   */
  public Genome getRandomLowerGenome()
  {
    synchronized (this)
    {
      int rand = Constants.RANDOM.nextInt(population.size() - 1) + 1;
      return population.get(rand);
    }
  }
}
