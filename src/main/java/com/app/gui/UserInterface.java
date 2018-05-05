package com.app.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTextField;
import com.app.classifiers.wrappers.AnnClassifierWrapper;
import com.app.classifiers.wrappers.IClassifierWrapper;
import com.app.classifiers.wrappers.KnnClassifierWrapper;
import com.app.classifiers.wrappers.RandomTreesClassifierWrapper;
import com.app.data.visualisation.DataStatsPane;
import com.app.generator.SetGenerator;
import com.app.generator.SetGenerator.TAG;

public class UserInterface extends JFrame  {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1983326924128087116L;
	public final static int DEFAULT_WIDTH = 400;
	public final static int DEFAULT_HEIGHT = 700;
	private static final Dimension PREFFERED_FRAME_SIZE = new Dimension(400,800);
	/**
	 * Not implemented
	 */
	private JTextField radialDistance;
	
	/**
	 * Amount of rows to produce
	 */
	private JTextField size;
	/**
	 * Input destination
	 */
	private JTextField inputDestinationPercentages;
	private JTextField inputDevDestination;
	private JTextField inputAngleDestination;
	private JTextField tarDestRTrees;
	private JTextField tarDestKNN;
	private JTextField tarDestANN;
	private AnnClassifierWrapper annWrapper = new AnnClassifierWrapper();
	private RandomTreesClassifierWrapper rTreesWrapper = new RandomTreesClassifierWrapper();
	private KnnClassifierWrapper knnWrapper = new KnnClassifierWrapper();

	/**
	 * Panel containing statistics, on occurrence of values and classes
	 */
	private DataStatsPane stats = new DataStatsPane();
	private JPanel trainingProgressBar;
	private JLabel trainingInformation;
	private JPanel inputFields;

	public UserInterface(){
		super("Window");
		JPanel topPanelContainer = new JPanel();
		topPanelContainer.setLayout(new BoxLayout(topPanelContainer, BoxLayout.PAGE_AXIS));
		JPanel inputPanel3 = new JPanel();
		JPanel inputPanel2 = new JPanel();
		JPanel inputPanel = new JPanel();
		inputPanel2.setLayout(new BoxLayout(inputPanel2, BoxLayout.LINE_AXIS));
		inputPanel3.setLayout(new BoxLayout(inputPanel3, BoxLayout.LINE_AXIS));
		inputPanel.add(new JLabel("Sparcity(distance):"));
		radialDistance = new JTextField(8);
		inputPanel.add(radialDistance);
		inputPanel.add(new JLabel("Size(Rows):"));
		size = new JTextField(8);
		inputPanel.add(size);
		inputPanel.add(new JLabel("Input file dest"));
		inputDestinationPercentages = new JTextField(8);
		inputPanel.add(inputDestinationPercentages);
		inputPanel3.add(new JLabel("Input angle dest:"));
		inputAngleDestination = new JTextField(8);
		inputPanel3.add(inputAngleDestination);
		inputPanel3.add(new JLabel("Input dev dest:"));
		inputDevDestination = new JTextField(8);
		inputPanel3.add(inputDevDestination);
		topPanelContainer.add(inputPanel);
		topPanelContainer.add(inputPanel2);
		topPanelContainer.add(inputPanel3);
		inputPanel2.add(new JLabel("Tar File Random Trees:"));
		tarDestRTrees = new JTextField(8);
		inputPanel2.add(tarDestRTrees);
		inputPanel2.add(new JLabel("Tar File KNN:"));
		tarDestKNN = new JTextField(8);
		inputPanel2.add(tarDestKNN);
		inputPanel2.add(new JLabel("Tar File ANN:"));
		tarDestANN = new JTextField(8);
		inputPanel2.add(tarDestANN);
		inputDestinationPercentages.addKeyListener(new KeyListener() {
			String fileName = "";
			int carPos = 0;
			@Override
			public void keyTyped(KeyEvent e) {
				if(Character.isAlphabetic(e.getKeyChar()) || Character.isDigit(e.getKeyChar()) || e.getKeyChar() == KeyEvent.VK_PERIOD){
					fileName = fileName +e.getKeyChar();
					fileName = fileName.replaceAll(size.getText()+"P.dat", "");
					carPos = inputDestinationPercentages.getCaretPosition()+1;
					inputDestinationPercentages.setText(fileName+size.getText()+"P.dat");
					inputAngleDestination.setText(fileName+size.getText()+"A.dat");
					inputDevDestination.setText(fileName+size.getText()+"D.dat");
					tarDestANN.setText(fileName+"TarAnn"+size.getText()+".dat");
					tarDestKNN.setText(fileName+"TarKnn"+size.getText()+".dat");
					tarDestRTrees.setText(fileName+"TarRTrees"+size.getText()+".dat");
					e.consume();
				}else if(e.getKeyChar() == KeyEvent.VK_BACK_SPACE || e.getKeyChar() == KeyEvent.VK_DELETE){
					carPos = inputDestinationPercentages.getCaretPosition();
					fileName = inputDestinationPercentages.getText();
					inputAngleDestination.setText(fileName+size.getText()+"A.dat");
					inputDevDestination.setText(fileName+size.getText()+"D.dat");
					tarDestANN.setText(fileName+"TarAnn"+size.getText()+".dat");
					tarDestKNN.setText(fileName+"TarKnn"+size.getText()+".dat");
					tarDestRTrees.setText(fileName+"TarRTrees"+size.getText()+".dat");
					e.consume();
				}else{
					carPos = inputDestinationPercentages.getCaretPosition();
					e.consume();
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				inputDestinationPercentages.setCaretPosition(carPos);

			}

			@Override
			public void keyPressed(KeyEvent e) {				

			}
		});
		
		add(topPanelContainer,BorderLayout.NORTH);

		JPanel bottomContainer = new JPanel();
		bottomContainer.setLayout(new BoxLayout(bottomContainer, BoxLayout.PAGE_AXIS));
		JProgressBar progressbar = new JProgressBar();
		progressbar.setAlignmentX(CENTER_ALIGNMENT);
		JButton btnPrintSet = new JButton("Print training set");
		//JButton btnStopPrinting = new JButton("Stop Printing");
		JButton btnRunStatistics = new JButton("Graph statistics");
		JFrame parent = this;
		btnRunStatistics.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				stats.loadClassData(tarDestRTrees.getText());
				stats.loadPercentageData(inputDestinationPercentages.getText(),tarDestRTrees.getText());
				parent.repaint();		
			}
		});
		
		btnPrintSet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(testInputData())	{
					Map<String,Object> params = new TreeMap<>();
					SetGenerator setGenerator = new SetGenerator();
					setGenerator.setInputDevDest(inputDevDestination.getText());
					setGenerator.setInputAngleDest(inputAngleDestination.getText());
					setGenerator.setInputDestFile(inputDestinationPercentages.getText());
					setGenerator.setTargAnnFileOut(tarDestANN.getText());
					setGenerator.setTargKnnFileOut(tarDestKNN.getText());
					setGenerator.setTargRTrees(tarDestRTrees.getText());
					params.put(TAG.ROWS.name(),size.getText() );
					params.put(TAG.SPARCITY.name(),radialDistance.getText() );
					try{
						setGenerator.setParams(params);
					}catch (NumberFormatException ex) {
						JOptionPane pane = new JOptionPane();
						pane.createDialog("Input error!");
						pane.setMessage("Rows and sparcity should be a number.");
						JOptionPane.showMessageDialog(parent, "Message", "Input error", JOptionPane.INFORMATION_MESSAGE);
						return;
					}
					setGenerator.printTrainingSetsMethod1();

				}else{
					JOptionPane.showMessageDialog(parent, "Check input fields, Size is an integer. "
							+ "Input destination can't empty.", "Input error", JOptionPane.INFORMATION_MESSAGE);
				}
				stats.loadClassData(tarDestRTrees.getText());
				stats.loadPercentageData(inputDestinationPercentages.getText(),tarDestRTrees.getText());
				parent.repaint();
			}
		});

	//	bottomContainer.add(progressbar);
		
		trainingProgressBar = new JPanel();
		trainingInformation = new JLabel("Currently Training");
		trainingProgressBar.setLayout(new BorderLayout());
		trainingProgressBar.add(trainingInformation,BorderLayout.CENTER);
		ImageIcon waitingIncon = new ImageIcon("images/ajax-loader.gif");
		trainingProgressBar.add(new JLabel(waitingIncon),BorderLayout.EAST);
		trainingProgressBar.setVisible(false);
		trainingProgressBar.setAlignmentX(CENTER_ALIGNMENT);
		JPanel executionControlBtnGroup = new JPanel();
		executionControlBtnGroup.setLayout(new BoxLayout(executionControlBtnGroup, BoxLayout.LINE_AXIS));
		executionControlBtnGroup.add(btnPrintSet);
		executionControlBtnGroup.add(btnRunStatistics);
		executionControlBtnGroup.setAlignmentX(CENTER_ALIGNMENT);

		bottomContainer.add(executionControlBtnGroup);
		bottomContainer.add(trainingProgressBar);
		JButton showMoreInputs = new JButton("Show more");
		inputFields = new JPanel();
		inputFields.setLayout(new BoxLayout(inputFields, BoxLayout.PAGE_AXIS));
		inputFields.setSize(new Dimension(getWidth(), 200));
		inputFields.setVisible(false);
		showMoreInputs.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				if(inputFields.isVisible()) {
					inputFields.setVisible(false);	
					showMoreInputs.setText("Show more");
				}else {
					inputFields.setVisible(true);
					showMoreInputs.setText("Hide");
				}
			}
		});
		showMoreInputs.setAlignmentX(CENTER_ALIGNMENT);
		showMoreInputs.setMaximumSize(new Dimension(Integer.MAX_VALUE, 26));
		inputFields.setAlignmentX(CENTER_ALIGNMENT);
		addExtraInputfields(inputFields);
		bottomContainer.add(showMoreInputs);
		bottomContainer.add(inputFields);
		JButton trainBtn = new JButton("Train");
		trainBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				new Thread(){
					public void run() {
						trainClassifiers();
					}
				}.start();

			}
		});
		executionControlBtnGroup.add(trainBtn);
		btnPrintSet.setAlignmentX(CENTER_ALIGNMENT);
		add(bottomContainer, BorderLayout.SOUTH);
		add(stats,BorderLayout.CENTER);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();
		setVisible(true);
	}
	private void addExtraInputfields(JPanel inputFields) {
		String[] names = {"annImageOutDestAngles","annImageOutDestDevs","annImageOutDestPercents",
				"knnImageOutDestAngles","knnImageOutDestDevs","knnImageOutDestPercents",
				"RTreesImageOutDestAngles","RTreesImageOutDestDevs","RTreesImageOutDestPercents"};
		JTextField tField;
		for(String name:names) {
			tField = new JTextField(name+(Calendar.getInstance().get(Calendar.MONTH)+1)+"."+Calendar.getInstance().get(Calendar.DATE)+".ml");
			tField.setName(name);
			inputFields.add(tField);
		}
	}
	private String annImageOutDest1 = "annTrained1.ml";
	private String knnImageOutDest1 = "knnTrained1.ml";
	private String rTreesImageOutDest1 = "rTreesTrained1.ml";
	private String annImageOutDest2 = "annTrained2.ml";
	private String knnImageOutDest2 = "knnTrained2.ml";
	private String rTreesImageOutDest2 = "rTreesTrained2.ml";
	private String annImageOutDest3 = "annTrained3.ml";
	private String knnImageOutDest3 = "knnTrained3.ml";
	private String rTreesImageOutDest3 = "rTreesTrained3.ml";
	private void trainClassifiers() {
		getImageDestinations(inputFields);
		trainingInformation.setText("Currently training:Ann, Angles,38 features");
		trainingProgressBar.setVisible(true);
		traineClassifier(annWrapper,inputAngleDestination.getText(),tarDestANN.getText(),
				annImageOutDest1,38);	
		trainingInformation.setText("Currently training:Trees, Angles,38 features");
		traineClassifier(rTreesWrapper,inputAngleDestination.getText(),tarDestRTrees.getText(),
				rTreesImageOutDest1,38);
		trainingInformation.setText("Currently training:Knn, Angles,38 features");
		traineClassifier(knnWrapper,inputAngleDestination.getText(),tarDestKNN.getText(),
				knnImageOutDest1,38);
		trainingInformation.setText("Currently training:Ann, Deviations,19 features");
		traineClassifier(annWrapper,inputDevDestination.getText(),tarDestANN.getText(),
				annImageOutDest2,19);	
		trainingInformation.setText("Currently training:RTrees, Deviations,19 features");
		traineClassifier(rTreesWrapper,inputDevDestination.getText(),tarDestRTrees.getText(),
				rTreesImageOutDest2,19);
		trainingInformation.setText("Currently training:Knn, Deviations,19 features");
		traineClassifier(knnWrapper,inputDevDestination.getText(),tarDestKNN.getText(),
				knnImageOutDest2,19);
		trainingInformation.setText("Currently training:Ann, Percentages,19 features");
		traineClassifier(annWrapper,inputDestinationPercentages.getText(),tarDestANN.getText(),
				annImageOutDest3,19);	
		trainingInformation.setText("Currently training:RTrees, Percentages,19 features");
		traineClassifier(rTreesWrapper,inputDestinationPercentages.getText(),tarDestRTrees.getText(),
				rTreesImageOutDest3,19);
		trainingInformation.setText("Currently training:Knn, Percentages,19 features");
		traineClassifier(knnWrapper,inputDestinationPercentages.getText(),tarDestKNN.getText(),
				knnImageOutDest3,19);
		trainingProgressBar.removeAll();
		trainingProgressBar.add(trainingInformation,BorderLayout.CENTER);
		trainingInformation.setText("Training Complete");
		revalidate();
	}

	private void getImageDestinations(JPanel inputFields) {
		String[] names = {"annImageOutDestAngles","annImageOutDestDevs","annImageOutDestPercents",
				"knnImageOutDestAngles","knnImageOutDestDevs","knnImageOutDestPercents",
				"RTreesImageOutDestAngles","RTreesImageOutDestDevs","RTreesImageOutDestPercents"};
		Component[] components = inputFields.getComponents();
		Map<String,Component> map = new HashMap<>();
		for(Component comp:components) {
			map.put(comp.getName(),comp);
		}
		for(String name:names) {
			setValue(name,((JTextField)map.get(name)).getText());
		}
	}
	
	private void setValue(String name, String text) {
		if(name.equalsIgnoreCase("annImageOutDestAngles")) {
			annImageOutDest1 = text;
		}
		if(name.equalsIgnoreCase("annImageOutDestDevs")) {
			annImageOutDest2 = text;
		}
		if(name.equalsIgnoreCase("annImageOutDestPercents")){
			annImageOutDest3 = text;
		}
		if(name.equalsIgnoreCase("knnImageOutDestAngles")) {
			knnImageOutDest1 = text;
		}
		if(name.equalsIgnoreCase("knnImageOutDestDevs")) {
			knnImageOutDest2 = text;
		}
		if(name.equalsIgnoreCase("knnImageOutDestPercents")) {
			knnImageOutDest3 = text;
		}
		if(name.equalsIgnoreCase("RTreesImageOutDestAngles")) {
			rTreesImageOutDest1 = text;
		}
		if(name.equalsIgnoreCase("RTreesImageOutDestDevs")) {
			rTreesImageOutDest2 = text;
		}
		if(name.equalsIgnoreCase("RTreesImageOutDestPercents")) {
			rTreesImageOutDest3 = text;
		}		
	}
	
	private void traineClassifier(IClassifierWrapper wrapper, String inputData, String targetData,
			String imgOutputDest,int inSize) {
		wrapper.setInputVectorLength(inSize);
		wrapper.setTrainingDataInputFile(inputData);
		wrapper.setTrainingDataTargetFile(targetData);
		wrapper.setImageOutDest(imgOutputDest);
		wrapper.initiate();
		wrapper.train();			
	}

	protected boolean testInputData() {
		if(inputDestinationPercentages.getText().length() > 0){
			return true;
		}
		return false;
	}

	public UserInterface(int width, int height){
		super("Window");
		setPreferredSize(new Dimension(width,height));
		setLayout(new FlowLayout());
		pack();
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}

	public Dimension getPrefferedSize(){
		return PREFFERED_FRAME_SIZE;
	}

	public static void main(String[] args){
		new UserInterface();
	}	
}
