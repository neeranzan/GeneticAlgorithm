package trianglegenome.util;

import java.util.concurrent.TimeUnit;

/**
 * Stop watch utility
 *
 * Helps keep track of time
 *
 */
public class StopWatch
{
  private long timeElapsed;
  private long startTime;
  private boolean counting;

  /**
   * Create a new stop watch utility
   */
  public StopWatch()
  {
    timeElapsed = 0;
    counting = false;
  }

  /**
   * Start the stop watch
   */
  public void start()
  {
    if (!counting)
    {
      startTime = System.nanoTime();
      counting = true;
    }
  }

  /**
   * Get the time elapsed
   * 
   * @return time stop watch has been "running"
   */
  public long getTimeElapsed()
  {
    if (counting)
    {
      return timeElapsed + (System.nanoTime() - startTime);
    }
    return timeElapsed;
  }

  /**
   * Pause the stop watch
   */
  public void pause()
  {
    if (counting)
    {
      timeElapsed += System.nanoTime() - startTime;
      counting = false;
    }
  }

  /**
   * Reset the stop watch
   */
  public void reset()
  {
    counting = false;
    timeElapsed = 0;
  }

  /**
   * Get the minute second string for the GUI format m:ss
   * 
   * @return formatted string
   */
  public String getMinSecString()
  {
    long time = getTimeElapsed();
    long minutes = TimeUnit.NANOSECONDS.toMinutes(time);
    long seconds = TimeUnit.NANOSECONDS.toSeconds(time) - TimeUnit.MINUTES.toSeconds(minutes);
    return String.format("%d:%02d", minutes, seconds);
  }

  /**
   * Get how many seconds have passed
   * 
   * @return seconds
   */
  public long getSeconds()
  {
    return TimeUnit.NANOSECONDS.toSeconds(getTimeElapsed());
  }

  /**
   * Get how many minutes have passed
   * 
   * @return minutes
   */
  public long getMinutes()
  {
    return TimeUnit.NANOSECONDS.toMinutes(getTimeElapsed());
  }
}
