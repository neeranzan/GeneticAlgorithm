package trianglegenome.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.Timer;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import trianglegenome.Constants;
import trianglegenome.Genome;
import trianglegenome.Triangle;
import trianglegenome.TriangleGenome;
import trianglegenome.TriangleGenomeData;
import trianglegenome.util.StopWatch;

/**
 * The main GUI for the Triangle Genome Project
 * 
 * Displays necessary information
 *
 */
public class TriangleGenomeViewer
{
  /*
   * GUI elements
   */
  private JFrame frame;
  private ImagePanel genomePanel;
  private ImagePanel targetImagePanel;
  private BufferedImage image;
  private JToggleButton startPauseButton;
  private JButton resetButton;
  private JButton nextButton;
  private JComboBox<String> imageComboBox;
  private JLabel fitnessLabel;
  private Container content;
  private JLabel triangleValue;
  private JTextField triangleCountField;
  private JTextField tribeCountField;
  private JTextField genomeCountField;
  private JSlider triangleSlider;
  private JLabel tribeValue;
  private JSlider tribeSlider;
  private JLabel genomeLabel;
  private JSlider genomeSlider;
  private TriangleGenome triangleGenome;
  private JButton showGenomeTableButton;
  private JButton readGenomeFileButton;
  private JButton writeGenomeFileButton;

  /*
   * Statistics
   */
  private JButton statisticsButton;
  private JFrame statisticsFrame;
  private JLabel elapsedTimeLabel;
  private JLabel generationsLabel;
  private JLabel hillClimbGenLabel;
  private JLabel crossGenLabel;
  private JLabel genPerSecLabel;
  private JLabel deltaFitLabel;
  private JLabel diversityLabel;
  private JLabel improvementsLabel;

  private long lastBestFitness;
  private int nextMinute;
  private float deltaFitness;

  /*
   * Utility
   */
  private Thread triangleGenomeThread;
  private Timer genomeUpdateTimer;
  private List<JComponent> pauseOnlyComponents;
  private List<JComponent> startedOnlyComponents;
  private double totalPixelCount;
  private StopWatch stopWatch;
  private TriangleGenomeData triangleGenomeData;

  /*
   * Settings
   */
  private JFrame settingsFrame;
  private int initialPopulation = Constants.DEFAULT_INITIAL_POPULATION;
  private int maximumPopulation = Constants.DEFAULT_MAXIMUM_POPULATION;
  private boolean paused = true;
  private int tribeIndex = 0;
  private int genomeIndex = 0;
  private int triangleCount = Constants.TRIANGLE_COUNT;
  private int tribeCount = Runtime.getRuntime().availableProcessors();

  private JTextField initialPopulationText;
  private JTextField maximumPopulationText;
  private JTextField tribeCountText;

  /**
   * Main logic for creating the GUI is in this constructor
   */
  public TriangleGenomeViewer()
  {
    pauseOnlyComponents = new ArrayList<>();
    startedOnlyComponents = new ArrayList<>();
    frame = new JFrame("Triangle Genome Viewer");
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    content = frame.getContentPane();
    content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

    // Set initial image to mona lisa
    try
    {
      targetImagePanel = new ImagePanel(getClass().getClassLoader().getResource(
          "images/mona-lisa-cropped-512x413.png"));
    }
    catch (MalformedURLException e)
    {
      System.err.println("Error with default image");
      e.printStackTrace();
    }
    catch (IOException e)
    {
      System.err.println("Error with default image");
      e.printStackTrace();
    }

    JPanel imagePanelContainer = new JPanel();
    imagePanelContainer.setLayout(new BorderLayout());
    imagePanelContainer.setPreferredSize(new Dimension(520, 520));
    imagePanelContainer.add(targetImagePanel, BorderLayout.CENTER);

    // Initial genome
    JPanel genomePanelContainer = new JPanel();
    genomePanelContainer.setLayout(new BorderLayout());
    genomePanelContainer.setPreferredSize(new Dimension(520, 520));
    genomePanel = new ImagePanel(targetImagePanel.getWidth(), targetImagePanel.getHeight());
    genomePanelContainer.add(genomePanel, BorderLayout.CENTER);

    // Initialize triangle slider to max
    triangleSlider = new JSlider(JSlider.HORIZONTAL, 0, Constants.TRIANGLE_COUNT,
        Constants.TRIANGLE_COUNT);
    triangleSlider.setSnapToTicks(true);
    triangleSlider.addChangeListener(event -> triangleSliderChange(event));
    pauseOnlyComponents.add(triangleSlider);
    startedOnlyComponents.add(triangleSlider);

    // Images
    String[] images =
    { "Mona Lisa", "Poppyfields", "The great wave", "Mona Lisa face", "Seated Woman" };

    // Set up image combo box
    imageComboBox = new JComboBox<String>(images);
    imageComboBox.setSize(20, 20);
    imageComboBox.addItemListener(event -> imageComboBoxChange(event));
    pauseOnlyComponents.add(imageComboBox);

    // Add image and genome to display
    JPanel imageContainer = new JPanel();
    GroupLayout imageLayout = new GroupLayout(imageContainer);
    imageContainer.setLayout(imageLayout);
    imageLayout.setAutoCreateContainerGaps(true);
    imageLayout.setAutoCreateGaps(true);
    imageLayout.setVerticalGroup(
        imageLayout.createParallelGroup()
            .addComponent(imagePanelContainer)
            .addComponent(genomePanelContainer));
    imageLayout.setHorizontalGroup(
        imageLayout.createSequentialGroup()
            .addComponent(imagePanelContainer)
            .addComponent(genomePanelContainer));

    content.add(imageContainer);

    // Fitness label and Start
    startPauseButton = new JToggleButton("Start");
    startPauseButton.addActionListener(e -> startButtonPressed(e));
    fitnessLabel = new JLabel();
    fitnessLabel.setBorder(new EmptyBorder(5, 20, 5, 20));
    updateFitnessLabel();
    JPanel startFitnessContainer = new JPanel();
    startFitnessContainer.setLayout(new GridLayout(1, 2));
    startFitnessContainer.add(startPauseButton);
    startFitnessContainer.add(fitnessLabel);
    startFitnessContainer.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
    content.add(startFitnessContainer);

    // Triangle slider and display
    JPanel triangleSliderContainer = new JPanel();
    triangleValue = new JLabel("Triangle #");
    triangleCountField = new JTextField(Integer.toString(Constants.TRIANGLE_COUNT).length());
    triangleCountField.setText(Constants.TRIANGLE_COUNT + "");
    triangleCountField.addActionListener(e -> triangleFieldAction(e));
    pauseOnlyComponents.add(triangleCountField);
    startedOnlyComponents.add(triangleCountField);
    triangleSliderContainer.add(triangleValue);
    triangleSliderContainer.add(triangleCountField);
    triangleSliderContainer.add(triangleSlider);

    // Tribe slider
    JPanel tribeLine = new JPanel(new GridLayout(1, 3));
    JPanel tribeSliderContainer = new JPanel();
    tribeValue = new JLabel("Tribe #");
    tribeCountField = new JTextField(4);
    tribeCountField.setText("0");
    tribeSlider = new JSlider(JSlider.HORIZONTAL, 0, tribeCount - 1, 0);
    tribeSlider.addChangeListener(e -> tribeSliderChange(e));
    tribeSlider.setSnapToTicks(true);
    pauseOnlyComponents.add(tribeCountField);
    pauseOnlyComponents.add(tribeSlider);
    startedOnlyComponents.add(tribeCountField);
    startedOnlyComponents.add(tribeSlider);
    tribeCountField.addActionListener(e -> tribeFieldAction(e));
    tribeSliderContainer.add(tribeValue);
    tribeSliderContainer.add(tribeCountField);
    tribeSliderContainer.add(tribeSlider);

    // Genome slider
    JPanel genomeSliderContainer = new JPanel();
    genomeLabel = new JLabel("Genome #");
    genomeCountField = new JTextField(Integer.toString(Constants.DEFAULT_MAXIMUM_POPULATION).length());
    genomeCountField.setText(0 + "");
    genomeSlider = new JSlider(0, 1);
    genomeSlider.addChangeListener(e -> genomeSliderChange(e));
    genomeSlider.setSnapToTicks(true);
    pauseOnlyComponents.add(genomeCountField);
    pauseOnlyComponents.add(genomeSlider);
    startedOnlyComponents.add(genomeCountField);
    startedOnlyComponents.add(genomeSlider);
    genomeCountField.addActionListener(e -> genomeFieldAction(e));
    genomeSliderContainer.add(genomeLabel);
    genomeSliderContainer.add(genomeCountField);
    genomeSliderContainer.add(genomeSlider);

    // fitness / genome / triangle
    JPanel genomeTriangleSliders = new JPanel(new GridLayout(1, 3));
    genomeTriangleSliders.add(tribeSliderContainer);
    genomeTriangleSliders.add(genomeSliderContainer);
    genomeTriangleSliders.add(triangleSliderContainer);
    content.add(genomeTriangleSliders);

    // Top Buttons
    JButton settingsButton = new JButton("Settings");
    settingsButton.addActionListener(e -> showSettingsDialog(e));

    resetButton = new JButton("Reset");
    resetButton.addActionListener(e -> resetButtonPressed(e));
    pauseOnlyComponents.add(resetButton);

    nextButton = new JButton("Next");
    nextButton.addActionListener(e -> nextButtonPressed(e));
    pauseOnlyComponents.add(nextButton);
    startedOnlyComponents.add(nextButton);

    JPanel topButtonPanel = new JPanel(new GridLayout(1, 4));
    content.add(topButtonPanel);
    topButtonPanel.add(imageComboBox);
    topButtonPanel.add(settingsButton);
    topButtonPanel.add(nextButton);
    topButtonPanel.add(resetButton);

    // Bottom Buttons
    showGenomeTableButton = new JButton("Show Genome Table");
    showGenomeTableButton.addActionListener(e -> showGenomeTable(e));
    pauseOnlyComponents.add(showGenomeTableButton);
    startedOnlyComponents.add(showGenomeTableButton);

    readGenomeFileButton = new JButton("Read Genome File");
    readGenomeFileButton.addActionListener(e -> readGenomeFile(e));
    pauseOnlyComponents.add(readGenomeFileButton);
    startedOnlyComponents.add(readGenomeFileButton);

    writeGenomeFileButton = new JButton("Write Genome File");
    writeGenomeFileButton.addActionListener(e -> writeGenomeFile(e));
    pauseOnlyComponents.add(writeGenomeFileButton);
    startedOnlyComponents.add(writeGenomeFileButton);

    statisticsButton = new JButton("Statistics");
    statisticsButton.addActionListener(e -> toggleStatisticsWindow(e));
    startedOnlyComponents.add(statisticsButton);

    JPanel bottomButtonPanel = new JPanel(new GridLayout(1, 4));
    content.add(bottomButtonPanel);
    bottomButtonPanel.add(showGenomeTableButton);
    bottomButtonPanel.add(readGenomeFileButton);
    bottomButtonPanel.add(writeGenomeFileButton);
    bottomButtonPanel.add(statisticsButton);
    stopWatch = new StopWatch();
    resetTriangleGenome();
    genomeUpdateTimer = new Timer(1000, e -> genomeUpdate(e));
    genomeUpdateTimer.setInitialDelay(750);
    frame.setResizable(false);
  }

  private void tribeCountTextAction(ActionEvent e)
  {
    try
    {
      Integer integerValue = Integer.valueOf(tribeCountText.getText());
      if (integerValue > 1000 || integerValue <= 0)
      {
        tribeCountText.setText(Integer.toString(tribeCount));
        JOptionPane.showMessageDialog(settingsFrame, " The value should be in the range of 1 to 1000");
      }
    }
    catch (NumberFormatException e1)
    {
      tribeCountText.setText(Integer.toString(tribeCount));
      JOptionPane.showMessageDialog(settingsFrame, " The value should be a number 1 to 1000");

    }
  }

  private void maximumPopulationTextAction(ActionEvent e)
  {

    try
    {
      Integer integerValue = Integer.valueOf(maximumPopulationText.getText());
      if (integerValue > 200 || integerValue <= 0)
      {
        maximumPopulationText.setText(Integer.toString(maximumPopulation));
        JOptionPane.showMessageDialog(settingsFrame, " the value should be in the range of 1 to 200");
      }
    }
    catch (NumberFormatException e1)
    {
      maximumPopulationText.setText(Integer.toString(maximumPopulation));
      JOptionPane.showMessageDialog(settingsFrame, " The value should be a number 1 to 200");
    }

  }

  private void initialPopulationTextAction(ActionEvent e)
  {
    try
    {
      Integer integerValue = Integer.valueOf(initialPopulationText.getText());
      if (integerValue > 200 || integerValue <= 0)
      {
        initialPopulationText.setText(Integer.toString(initialPopulation));
        JOptionPane.showMessageDialog(settingsFrame, " The value should be in the range of 1 to 200");
      }
    }
    catch (NumberFormatException e1)
    {

      initialPopulationText.setText(Integer.toString(initialPopulation));
      JOptionPane.showMessageDialog(settingsFrame, " The value should be a number 1 to 200");
    }

  }

  /**
   * Update statistics for the statistics window (only update if shown)
   */
  private void updateStatistics()
  {
    if (statisticsFrame != null)
    {
      synchronized (triangleGenomeData)
      {
        int totalGenerations = triangleGenomeData.generations
            + triangleGenomeData.crossovers;
        generationsLabel.setText(Integer.toString(totalGenerations));
        hillClimbGenLabel.setText(Integer.toString(triangleGenomeData.generations));
        improvementsLabel.setText(Integer.toString(triangleGenomeData.improvements));
        crossGenLabel.setText(Integer.toString(triangleGenomeData.crossovers));
        elapsedTimeLabel.setText(stopWatch.getMinSecString());
        genPerSecLabel.setText(String.format("%.1f",
            ((float) totalGenerations) / ((float) stopWatch.getSeconds())));
        diversityLabel.setText(String.format("%.2f", 100 * triangleGenomeData.averageHammDist
            / ((float) Constants.GENE_COUNT)) + " %");
        deltaFitness = (float) ((triangleGenomeData.fitness - lastBestFitness)
            / (totalPixelCount));
      }
      deltaFitLabel.setText(String.format("%.6f", deltaFitness));
    }
    if (stopWatch.getMinutes() >= nextMinute)
    {
      nextMinute++;
      lastBestFitness = triangleGenomeData.fitness;
    }
  }

  /**
   * Toggle whether the statistics window is shown, and if it is not made make
   * the statistics window
   * 
   * @param e
   */
  private void toggleStatisticsWindow(ActionEvent e)
  {
    if (statisticsFrame == null)
    {
      statisticsFrame = new JFrame("Statistics");
      statisticsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
      JPanel statContent = new JPanel();
      statContent.setBorder(new EmptyBorder(20, 20, 20, 20));
      statContent.setLayout(new GridLayout(0, 2));
      CompoundBorder labelPadding = BorderFactory.createCompoundBorder(new LineBorder(Color.GRAY),
          new EmptyBorder(10, 20, 10, 20));

      JLabel time = new JLabel("Elapsed Time:");
      time.setBorder(labelPadding);
      elapsedTimeLabel = new JLabel("0:00");
      elapsedTimeLabel.setBorder(labelPadding);
      statContent.add(time);
      statContent.add(elapsedTimeLabel);

      JLabel generations = new JLabel("Generations:");
      generationsLabel = new JLabel("0");
      generations.setBorder(labelPadding);
      generationsLabel.setBorder(labelPadding);
      statContent.add(generations);
      statContent.add(generationsLabel);

      JLabel improvements = new JLabel("Improvements:");
      improvementsLabel = new JLabel("0");
      improvements.setBorder(labelPadding);
      improvementsLabel.setBorder(labelPadding);
      statContent.add(improvements);
      statContent.add(improvementsLabel);

      JLabel hill = new JLabel("Hill climb generations:");
      hillClimbGenLabel = new JLabel("0");
      hill.setBorder(labelPadding);
      hillClimbGenLabel.setBorder(labelPadding);
      statContent.add(hill);
      statContent.add(hillClimbGenLabel);

      JLabel cross = new JLabel("Crossover generations:");
      crossGenLabel = new JLabel("0");
      cross.setBorder(labelPadding);
      crossGenLabel.setBorder(labelPadding);
      statContent.add(cross);
      statContent.add(crossGenLabel);

      JLabel gensec = new JLabel("Generations / second:");
      genPerSecLabel = new JLabel("0.0");
      gensec.setBorder(labelPadding);
      genPerSecLabel.setBorder(labelPadding);
      statContent.add(gensec);
      statContent.add(genPerSecLabel);

      JLabel deltfit = new JLabel("Delta Fitness / minute:");
      deltaFitLabel = new JLabel("0");
      deltfit.setBorder(labelPadding);
      deltaFitLabel.setBorder(labelPadding);
      statContent.add(deltfit);
      statContent.add(deltaFitLabel);

      JLabel div = new JLabel("Diversity:");
      diversityLabel = new JLabel("?");
      div.setBorder(labelPadding);
      diversityLabel.setBorder(labelPadding);
      statContent.add(div);
      statContent.add(diversityLabel);

      statisticsFrame.getContentPane().add(statContent);
      statisticsFrame.pack();
    }

    if (statisticsFrame.isVisible())
    {
      statisticsFrame.setVisible(false);
    }
    else
    {
      statisticsFrame.setVisible(true);
    }
  }

  /**
   * Update the genome panel while threads are running
   * 
   * @param e
   */
  private void genomeUpdate(ActionEvent e)
  {
    if (lastBestFitness == -1)
    {
      lastBestFitness = triangleGenome.getBestGenomeFitness();
    }
    triangleGenome.drawBestGenomeImage(genomePanel.getImage());
    genomePanel.repaint();
    tribeSlider.setValue(triangleGenome.getBestGenomeTribe());
    updateFitnessLabel();
    genomeSlider.setValue(0);
    updateStatistics();
  }

  /**
   * Next action listener, should perform 1 generation
   * 
   * @param e
   */
  private void nextButtonPressed(ActionEvent e)
  {
    triangleGenome.next();
    updateStatistics();
  }

  /**
   * Reset action listener
   * 
   * @param e
   */
  private void resetButtonPressed(ActionEvent e)
  {
    resetTriangleGenome();
  }

  /**
   * Reset the triangle genome, stop all threads and ready to start new
   */
  private void resetTriangleGenome()
  {
    startPauseButton.setText("Start");
    for (JComponent comp : startedOnlyComponents)
    {
      comp.setEnabled(false);
    }
    if (triangleGenomeThread != null && !triangleGenomeThread.isInterrupted())
    {
      triangleGenomeThread.interrupt();
    }
    triangleGenome = null;
    if (statisticsFrame != null)
    {
      statisticsFrame.setVisible(false);
      statisticsFrame.dispose();
    }
    statisticsFrame = null;
    genomePanel.resetImage(targetImagePanel.getWidth(), targetImagePanel.getHeight());
    totalPixelCount = (double) targetImagePanel.getWidth() * targetImagePanel.getHeight();
    updateFitnessLabel();
    genomePanel.repaint();
    stopWatch.reset();
    deltaFitness = 0;
    nextMinute = 1;
    lastBestFitness = -1;
    tribeSlider.setMaximum(tribeCount - 1);
  }

  /**
   * Show the GUI
   */
  public void showGUI()
  {
    frame.pack();
    frame.setVisible(true);
  }

  /**
   * Main controller button, start and pauses the GUI/Threads
   * 
   * @param e
   */
  private void startButtonPressed(ActionEvent e)
  {
    paused = !paused;
    if (paused)
    {
      startPauseButton.setText("Resume");
      triangleGenome.pause();
      genomeUpdateTimer.stop();
      for (JComponent comp : pauseOnlyComponents)
      {
        comp.setEnabled(true);
      }
      triangleCount = Constants.TRIANGLE_COUNT;
      updateGenomeSlider();
      updateGenomePanel();
      stopWatch.pause();
    }
    else
    {
      stopWatch.start();
      startPauseButton.setText("Pause");
      if (triangleGenome == null)
      {
        triangleGenomeData = new TriangleGenomeData(tribeCount, targetImagePanel.getWidth(),
            targetImagePanel.getHeight());
        triangleGenome = new TriangleGenome(targetImagePanel.getImage(), tribeCount, triangleGenomeData,
            initialPopulation, maximumPopulation);
        triangleGenomeThread = new Thread(triangleGenome);
        triangleGenomeThread.start();
      }
      else
      {
        triangleGenome.unPause();
      }
      genomeUpdateTimer.start();
      for (JComponent comp : pauseOnlyComponents)
      {
        comp.setEnabled(false);
      }
      statisticsButton.setEnabled(true);
      triangleSlider.setValue(Constants.TRIANGLE_COUNT);
    }
  }

  /**
   * For replacement of the ItemListener for the image combo box, uses the event
   * to change the image to one of 3 predefined images
   * 
   * @param event the item event change triggered
   */
  private void imageComboBoxChange(ItemEvent event)
  {
    String fileName = "";

    if (event.getStateChange() == ItemEvent.SELECTED)
    {
      int index = imageComboBox.getSelectedIndex();
      if (index == 0)
      {
        fileName = "mona-lisa-cropped-512x413.png";
      }
      if (index == 1)
      {
        fileName = "poppyfields-512x384.png";
      }
      if (index == 2)
      {
        fileName = "the_great_wave_off_kanagawa-512x352.png";
      }
      if (index == 3)
      {
        fileName = "mona-lisa-face-200x200.png";
      }
      if (index == 4)
      {
        fileName = "seated-woman-cropped-500x500.jpg";
      }

      try
      {

        image = ImageIO.read(getClass().getClassLoader().getResource("images/" + fileName));
        BufferedImage byteImage = new BufferedImage(image.getWidth(), image.getHeight(),
            Constants.BUF_IMG_TYPE);
        byteImage.getGraphics().drawImage(image, 0, 0, null);
        targetImagePanel.setImage(byteImage);
        triangleSlider.setValue(Constants.TRIANGLE_COUNT);
        resetTriangleGenome();
        content.repaint();
      }
      catch (MalformedURLException e)
      {
        System.err.println("Error loading image: " + fileName);
        e.printStackTrace();
      }
      catch (IOException e)
      {
        System.err.println("Error loading image: " + fileName);
        e.printStackTrace();
      }
    }
  }

  /**
   * Read ASCII or XML genome file
   * 
   * @param e
   */
  private void readGenomeFile(ActionEvent e)
  {
    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));
    chooser.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (e.getActionCommand() == "CancelSelection")
        {
          System.out.print("correct closed read");
          return;
        }
        File file = chooser.getSelectedFile();

        List<Triangle> triangles = new ArrayList<>();
        List<String> listOfInput = new ArrayList<String>();
        List<Integer> listOfElements = new ArrayList<>();

        String line = null;
        try (BufferedReader reader = new BufferedReader(new FileReader(file.getPath())))
        {

          if (file.getPath().contains(".txt"))
          {
            while ((line = reader.readLine()) != null)
            {
              if (line.contains("#"))
              {
                continue;
              }
              listOfInput.add(line);
            }

            for (String temp : listOfInput)
            {
              listOfElements.removeAll(listOfElements);
              String[] t = temp.split("[d\t]");
              for (int i = 1; i < t.length; i++)
              {

                Integer integerTemp = new Integer(Integer.valueOf(t[i]));
                listOfElements.add(integerTemp);

              }
              Triangle triangleTemp = new Triangle(new Point(0, 0), new Point(0, 0), new Point(0, 0),
                  new Color(0, 0, 0, 0), targetImagePanel.getWidth(), targetImagePanel.getHeight());
              triangleTemp.setElementOfTriangle(listOfElements);
              triangles.add(triangleTemp);
            }
            Genome g = new Genome(triangles, targetImagePanel.getWidth(), targetImagePanel.getHeight());
            if (Genome.validGenome(g))
            {
              triangleGenome.getTribe(tribeIndex).addGenome(g, genomeIndex);
            }
            else
            {
              System.err.println("Invalid genome could not add to tribe.");
            }
          }
          else if (file.getPath().contains(".xml"))
          {

            /**
             * ================================================================
             * == to read XML file
             */

            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            org.w3c.dom.Document doc = dBuilder.parse(file);

            doc.getDocumentElement().normalize();

            NodeList nList = doc.getElementsByTagName("triangleNomber");

            for (int i = 0; i < nList.getLength(); i++)
            {

              Node nNode = nList.item(i);

              if (nNode.getNodeType() == Node.ELEMENT_NODE)
              {

                Element eElement = (Element) nNode;

                listOfElements.removeAll(listOfElements);

                listOfElements.add(Integer
                    .valueOf(eElement.getElementsByTagName("X1").item(0).getTextContent()));
                listOfElements.add(Integer
                    .valueOf(eElement.getElementsByTagName("Y1").item(0).getTextContent()));
                listOfElements.add(Integer
                    .valueOf(eElement.getElementsByTagName("X2").item(0).getTextContent()));
                listOfElements.add(Integer
                    .valueOf(eElement.getElementsByTagName("Y2").item(0).getTextContent()));
                listOfElements.add(Integer
                    .valueOf(eElement.getElementsByTagName("X3").item(0).getTextContent()));
                listOfElements.add(Integer
                    .valueOf(eElement.getElementsByTagName("Y3").item(0).getTextContent()));
                listOfElements.add(Integer.valueOf(eElement.getElementsByTagName("Red").item(0)
                    .getTextContent()));
                listOfElements.add(Integer.valueOf(eElement.getElementsByTagName("Blue").item(0)
                    .getTextContent()));
                listOfElements.add(Integer.valueOf(eElement.getElementsByTagName("Green").item(0)
                    .getTextContent()));
                listOfElements.add(Integer.valueOf(eElement.getElementsByTagName("Alpha").item(0)
                    .getTextContent()));

                Triangle triangleTemp = new Triangle(new Point(0, 0), new Point(0, 0), new Point(0, 0),
                    new Color(0, 0, 0, 0), targetImagePanel.getWidth(), targetImagePanel.getHeight());
                triangleTemp.setElementOfTriangle(listOfElements);
                triangles.add(triangleTemp);
              }

            }
            Genome g = new Genome(triangles, targetImagePanel.getWidth(), targetImagePanel.getHeight());
            if (Genome.validGenome(g))
            {
              triangleGenome.getTribe(tribeIndex).addGenome(g, genomeIndex);
            }
            else
            {
              System.err.println("Invalid genome could not add to tribe.");
            }
            /**
             * finish read XML file code
             */
          }
          else
          {
            JOptionPane.showMessageDialog(frame, "Format not allowed for reading");
          }

          if (reader != null)
          {
            reader.close();
          }
        }
        catch (FileNotFoundException e1)
        {
          System.err.println("File not found: " + e1.getMessage());
          return;
        }
        catch (IOException e1)
        {
          System.err.println("Error in file read: " + e1.getMessage());
        }
        catch (ParserConfigurationException e1)
        {
          System.err.println("Error in file read (parser): " + e1.getMessage());
        }
        catch (SAXException e1)
        {
          System.err.println("Error in file read (SAX): " + e1.getMessage());
        }
      }
    });
    chooser.showOpenDialog(new JFrame());
  }

  /**
   * Write ASCII or XML genome file
   * 
   * @param e
   */
  private void writeGenomeFile(ActionEvent e)
  {

    JFileChooser chooser = new JFileChooser();
    chooser.setCurrentDirectory(new File("."));

    // Adding extension filters
    FileNameExtensionFilter filterXML = new FileNameExtensionFilter(
        "XML File (.xml)", "xml");
    chooser.addChoosableFileFilter(filterXML);
    FileNameExtensionFilter filterTxt = new FileNameExtensionFilter(
        "Text File (.txt)", "txt");
    chooser.addChoosableFileFilter(filterTxt);

    chooser.addActionListener(new ActionListener()
    {

      @Override
      public void actionPerformed(ActionEvent e)
      {
        if (e.getActionCommand() == "CancelSelection")
        {
          System.out.print("correct closed write");
          return;
        }
        File file = chooser.getSelectedFile();
        BufferedWriter write = null;

        try
        {
          Genome genomeToWrite = triangleGenome.getTribe(tribeIndex).getGenome(genomeIndex);
          if (!(chooser.getFileFilter().equals(filterTxt) | chooser.getFileFilter().equals(filterTxt)))
          {
            chooser.setFileFilter(filterXML);
          }
          if (chooser.getFileFilter().equals(filterTxt))
          {
            write = new BufferedWriter(new FileWriter(file.getPath() + ".txt"));
            PrintWriter printLine = new PrintWriter(write);

            printLine.printf("%s", "#" + "\t" + "X1" + "\t" + "Y1" + "\t" + "X2" + "\t" + "Y2" + "\t"
                + "X3" + "\t" + "Y3" + "\t" + "C1" + "\t" + "C2" + "\t" + "C3" + "\t" + "C4" + "\n");
            for (Triangle triangleTemp : genomeToWrite.getTriangles())
            {
              String temp = ("\t" + triangleTemp.getElementOfTriangle().get(0)
                  + "\t" + triangleTemp.getElementOfTriangle().get(1) +
                  "\t" + triangleTemp.getElementOfTriangle().get(2)
                  + "\t" + triangleTemp.getElementOfTriangle().get(3) +
                  "\t" + triangleTemp.getElementOfTriangle().get(4)
                  + "\t" + triangleTemp.getElementOfTriangle().get(5) +
                  "\t" + triangleTemp.getElementOfTriangle().get(6)
                  + "\t" + triangleTemp.getElementOfTriangle().get(7) +
                  "\t" + triangleTemp.getElementOfTriangle().get(8)
                  + "\t" + triangleTemp.getElementOfTriangle().get(9) + "\n");
              printLine.printf("%s", temp);

            }
            printLine.close();
          }
          else
          {
            /**
             * ====================================================== to XML
             */

            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            // root elements

            org.w3c.dom.Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("Genome");
            doc.appendChild(rootElement);

            Integer i = 0;

            for (Triangle triangleTemp : genomeToWrite.getTriangles())
            {
              // triangeNomber elements
              Element triangeNomber = doc.createElement("triangleNomber");
              rootElement.appendChild(triangeNomber);

              i++; // triangle number
              // set attribute to triangeNomber element
              Attr attr = doc.createAttribute("No");
              attr.setValue(i.toString());
              triangeNomber.setAttributeNode(attr);

              // X1 elements
              Element X1 = doc.createElement("X1");
              X1.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(0).toString()));
              triangeNomber.appendChild(X1);

              // Y1 elements
              Element Y1 = doc.createElement("Y1");
              Y1.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(1).toString()));
              triangeNomber.appendChild(Y1);

              // X2 elements
              Element X2 = doc.createElement("X2");
              X2.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(2).toString()));
              triangeNomber.appendChild(X2);

              // Y2 elements
              Element Y2 = doc.createElement("Y2");
              Y2.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(3).toString()));
              triangeNomber.appendChild(Y2);

              // X3 elements
              Element X3 = doc.createElement("X3");
              X3.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(4).toString()));
              triangeNomber.appendChild(X3);

              // Y3 elements
              Element Y3 = doc.createElement("Y3");
              Y3.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(5).toString()));
              triangeNomber.appendChild(Y3);

              // Color 1 elements
              Element C1 = doc.createElement("Red");
              C1.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(6).toString()));
              triangeNomber.appendChild(C1);

              // Color 2 elements
              Element C2 = doc.createElement("Blue");
              C2.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(7).toString()));
              triangeNomber.appendChild(C2);

              // Color 3 elements
              Element C3 = doc.createElement("Green");
              C3.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(8).toString()));
              triangeNomber.appendChild(C3);

              // Color 4 elements
              Element C4 = doc.createElement("Alpha");
              C4.appendChild(doc.createTextNode(triangleTemp.getElementOfTriangle().get(9).toString()));
              triangeNomber.appendChild(C4);
            }

            // write the content into XML file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);

            StreamResult result = new StreamResult(new File(file.getPath() + ".xml"));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(source, result);
          }

        }
        catch (IOException e1)
        {
          System.err.println("Error with writing genome IO: " + e1.getMessage());
        }
        catch (ParserConfigurationException e1)
        {
          System.err.println("Error with writing genome (parser): " + e1.getMessage());

        }
        catch (TransformerConfigurationException e1)
        {
          System.err.println("Error with writing genome (xml): " + e1.getMessage());

        }
        catch (TransformerException e1)
        {
          System.err.println("Error with writing genome (xml): " + e1.getMessage());
        }

        /**
         * ------------------------------------------------
         */

      }
    });
    chooser.showSaveDialog(new JFrame());
  }

  /**
   * Show the genome table for the current genome
   * 
   * @param e
   */
  private void showGenomeTable(ActionEvent e)
  {

    String[] columnNames =
    { "Triangle", "X1", "Y1", "X2", "Y2", "X3", "Y3", "Red", "Blue", "Green", "Alpha" };

    Object[][] dataMy = new Object[200][11];
    List<Triangle> listOftrianglesTemp = triangleGenome.getTribe(tribeIndex).getGenome(genomeIndex)
        .getTriangles();
    List<Integer> listOfTriangleValues = new ArrayList<>();
    for (int i = 0; i < 200; i++)
    {
      if (i != 0)
      {
        listOfTriangleValues.removeAll(listOfTriangleValues);
      }
      listOfTriangleValues.addAll(listOftrianglesTemp.get(i).getElementOfTriangle());
      listOfTriangleValues.add(0, i + 1);
      for (int j = 0; j < 11; j++)
      {
        dataMy[i][j] = listOfTriangleValues.get(j);
      }

    }

    JTable table = new JTable(dataMy, columnNames);
    table.setPreferredScrollableViewportSize(new Dimension(700, 500));
    table.setFillsViewportHeight(true);
    table.setVisible(true);

    JFrame frameTable = new JFrame("Genome Table");

    JScrollPane scrollTable = new JScrollPane(table);
    table.setFillsViewportHeight(true);
    frameTable.add(scrollTable);

    frameTable.pack();
    frameTable.setVisible(true);
  }

  /**
   * Change the genome panel count based on the value given by the new event
   * 
   * @param event ChangeEvent for the triangle slider
   */
  private void triangleSliderChange(ChangeEvent event)
  {
    JSlider source = (JSlider) event.getSource();
    int count = (int) source.getValue();
    triangleCount = count;
    triangleCountField.setText(count + "");
    updateGenomePanel();
  }

  /**
   * Change the maximum value of the genome slider
   */
  private void updateGenomeSlider()
  {
    if (paused && triangleGenome != null && triangleGenome.getTribe(tribeIndex) != null)
    {
      genomeSlider.setMaximum(triangleGenome.getTribe(tribeIndex).getPopulationSize() - 1);
    }
  }

  /**
   * Tribe selector event
   * 
   * @param event
   */
  private void tribeSliderChange(ChangeEvent event)
  {
    JSlider source = (JSlider) event.getSource();
    tribeIndex = (int) source.getValue();
    tribeCountField.setText(tribeIndex + "");
    updateGenomeSlider();
    updateGenomePanel();
  }

  /**
   * Movement of the genome slider
   * 
   * @param event
   */
  private void genomeSliderChange(ChangeEvent event)
  {
    JSlider source = (JSlider) event.getSource();
    genomeIndex = (int) source.getValue();
    genomeCountField.setText(genomeIndex + "");
    updateGenomePanel();
  }

  /**
   * Grab data from tribe and draw onto genome panel, should only be used when
   * paused
   */
  private void updateGenomePanel()
  {
    if (triangleGenome != null && triangleGenome.getTribe(tribeIndex) != null)
    {
      Genome g = triangleGenome.getTribe(tribeIndex).getGenome(genomeIndex);
      g.drawImage(genomePanel.getImage(), triangleCount);
      genomePanel.repaint();
      updateFitnessLabel(g.fitness);
    }
  }

  /**
   * Update fitnessLabel to display the fitness of the current image on
   * genomePanel
   */
  private void updateFitnessLabel()
  {
    if (triangleGenome == null)
    {
      fitnessLabel.setText("Fitness: " + "unknown");
    }
    else
    {
      // Normalize fitness
      double fitness = triangleGenome.getBestGenomeFitness() / totalPixelCount;
      fitnessLabel.setText("Fitness: " + String.format("%6f", fitness));
    }
  }

  /**
   * Update fitnessLabel to display the specified fitness on the genomePanel
   * 
   * @param fitness specified fitness
   */
  private void updateFitnessLabel(double fitness)
  {
    fitnessLabel.setText("Fitness: " + String.format("%8f", fitness / totalPixelCount));
  }

  /**
   * Action event for tribe field select through text box
   * 
   * @param e
   */
  private void tribeFieldAction(ActionEvent e)
  {
    try
    {
      int tribeNo = Integer.parseInt(tribeCountField.getText());
      if (tribeNo >= 0 && tribeNo < tribeCount)
      {
        tribeSlider.setValue(tribeNo);
      }
      else if (tribeNo < 0)
      {
        tribeSlider.setValue(0);
      }
      else
      {
        tribeSlider.setValue(tribeCount - 1);
      }
    }
    catch (NumberFormatException e1)
    {
      tribeCountField.setText(0 + "");
      tribeSlider.setValue(0);
    }
  }

  /**
   * Action event for genome field, select through text box
   * 
   * @param e
   */
  private void genomeFieldAction(ActionEvent e)
  {
    try
    {
      int genomeNo = Integer.parseInt(genomeCountField.getText());
      if (genomeNo >= 0 && genomeNo < triangleGenome.getTribe(tribeIndex).getPopulationSize())
      {
        genomeSlider.setValue(genomeNo);
      }
      else if (genomeNo < 0)
      {
        genomeSlider.setValue(0);
      }
      else
      {
        genomeSlider.setValue(triangleGenome.getTribe(tribeIndex).getPopulationSize() - 1);
      }
    }
    catch (NumberFormatException e1)
    {
      genomeCountField.setText(0 + "");
      genomeSlider.setValue(0);
    }
  }

  /**
   * Action event for genome field, select through text box
   * 
   * @param e
   */
  private void triangleFieldAction(ActionEvent e)
  {
    try
    {
      int triangleNo = Integer.parseInt(triangleCountField.getText());
      if (triangleNo >= 0 && triangleNo < Constants.TRIANGLE_COUNT)
      {
        triangleSlider.setValue(triangleNo);
      }
      else if (triangleNo < 0)
      {
        triangleSlider.setValue(0);
      }
      else
      {
        triangleSlider.setValue(Constants.TRIANGLE_COUNT - 1);
      }
    }
    catch (NumberFormatException e1)
    {
      triangleCountField.setText(0 + "");
      triangleSlider.setValue(0);
    }
  }

  /**
   * Show the settings menu action
   * 
   * @param e1
   */
  private void showSettingsDialog(ActionEvent e1)
  {
    if (settingsFrame == null || (!settingsFrame.isVisible()))
    {
      settingsFrame = new JFrame("Genetic Algorithm Settings");
      settingsFrame.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);

      EmptyBorder smallBorder = new EmptyBorder(10, 10, 10, 10);

      JLabel labelForTribeCount = new JLabel("Tribe Count (1-1000)");
      labelForTribeCount.setBorder(smallBorder);
      tribeCountText = new JTextField(4);
      tribeCountText.setText(Integer.toString(tribeCount));
      tribeCountText.addActionListener(e -> tribeCountTextAction(e));

      JLabel labelForInitialPopulation = new JLabel("Initial Population (1-200)");
      labelForInitialPopulation.setBorder(smallBorder);
      initialPopulationText = new JTextField(4);
      initialPopulationText.setText(Integer.toString(initialPopulation));
      initialPopulationText.addActionListener(e -> initialPopulationTextAction(e));

      JLabel labelForMaximumPopulation = new JLabel("Maximum Population (1-200)");
      labelForMaximumPopulation.setBorder(smallBorder);
      maximumPopulationText = new JTextField(4);
      maximumPopulationText.setText(Integer.toString(maximumPopulation));
      maximumPopulationText.addActionListener(e -> maximumPopulationTextAction(e));

      Container content = settingsFrame.getContentPane();
      content.setLayout(new BoxLayout(content, BoxLayout.PAGE_AXIS));

      JPanel settingsValues = new JPanel();
      settingsValues.setLayout(new GridLayout(0, 2));

      settingsValues.add(labelForTribeCount);
      settingsValues.add(tribeCountText);

      settingsValues.add(labelForInitialPopulation);
      settingsValues.add(initialPopulationText);

      settingsValues.add(labelForMaximumPopulation);
      settingsValues.add(maximumPopulationText);

      content.add(settingsValues);

      JPanel buttonSettings = new JPanel();
      JButton apply = new JButton("Apply");
      JButton cancel = new JButton("Cancel");

      apply.addActionListener(e -> applySettings(e));
      cancel.addActionListener(new ActionListener()
      {

        @Override
        public void actionPerformed(ActionEvent e)
        {
          settingsFrame.setVisible(false);
          settingsFrame.dispose();
          settingsFrame = null;
        }
      });

      buttonSettings.add(apply);
      buttonSettings.add(cancel);
      content.add(buttonSettings);

      settingsFrame.pack();
      settingsFrame.setVisible(true);
    }
    else
    {
      settingsFrame.setVisible(false);
      settingsFrame.dispose();
      settingsFrame = null;
    }
  }

  /**
   * Apply settings if possible
   * 
   * @param e
   */
  private void applySettings(ActionEvent e)
  {
    if (triangleGenome != null)
    {
      Object[] options =
      { "Reset", "Cancel" };
      int reset = JOptionPane.showOptionDialog(settingsFrame,
          "Applying the settings will reset the triangle genome",
          "Confirm settings apply",
          JOptionPane.YES_NO_OPTION,
          JOptionPane.QUESTION_MESSAGE,
          null,
          options,
          options[1]);

      if (reset != JOptionPane.YES_OPTION)
      {
        return;
      }
    }

    if (!validateSettingsAndApply())
    {
      JOptionPane.showMessageDialog(settingsFrame, "Invalid settings.");
    }

    settingsFrame.setVisible(false);
    settingsFrame.dispose();
    settingsFrame = null;
  }

  /**
   * Validate settings in fields and apply theme
   * 
   * @return
   */
  private boolean validateSettingsAndApply()
  {
    try
    {
      int tribeCountSet = Integer.parseInt(tribeCountText.getText());
      int initPopSet = Integer.parseInt(initialPopulationText.getText());
      int maxPopSet = Integer.parseInt(maximumPopulationText.getText());
      if (tribeCountSet <= 0 || tribeCountSet > 1000)
      {
        return false;
      }
      else if (initPopSet <= 0 || initPopSet > 200)
      {
        return false;
      }
      else if (maxPopSet <= 0 || maxPopSet > 200)
      {
        return false;
      }
      else
      {
        tribeCount = tribeCountSet;
        initialPopulation = initPopSet;
        maximumPopulation = maxPopSet;
        resetTriangleGenome();
        return true;
      }
    }
    catch (NumberFormatException e)
    {
      return false;
    }
  }
}
