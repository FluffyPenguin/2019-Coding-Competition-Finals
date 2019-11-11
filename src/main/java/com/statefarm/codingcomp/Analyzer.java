package com.statefarm.codingcomp;

import java.awt.EventQueue;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import com.statefarm.codingcomp.enums.PolicyStatus;
import com.statefarm.codingcomp.model.Policy;
import com.statefarm.codingcomp.reader.Reader;
import java.awt.GridLayout;
import java.awt.Image;

import javax.swing.JLabel;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.ImageIcon;

import java.awt.Toolkit;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URL;

import javax.swing.JComboBox;
import javax.swing.LayoutStyle.ComponentPlacement;
import javax.imageio.ImageIO;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JLayeredPane;
import java.awt.CardLayout;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import org.eclipse.wb.swing.FocusTraversalOnArray;
import java.awt.Component;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.awt.event.ActionEvent;
import java.awt.FlowLayout;
import java.awt.Dimension;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JButton;


public class Analyzer {

	private JFrame frm;
	private JComboBox<String> chooseGraphCB;
	private JLabel lblGraph;
	private JComboBox<String> policyTypeCB;
	private JComboBox<String> policyStatusCB;
	private JComboBox<String> stateCB;
	private JTextField ageTxt;
	private JTextField accidentsTxt;
	private JTextPane outputTxt;
	private JButton btnGo;
	
	private List<Policy> policies;
	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Analyzer window = new Analyzer();
					window.frm.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Analyzer() throws Exception{
		initialize();
		somethingInteresting();
		addEventListeners();
		
	}
	private void addEventListeners() {
		chooseGraphCB.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if (chooseGraphCB.getSelectedIndex() == 0 ) {
					return;
				}
				BufferedImage img = null;
				try {
				    img = ImageIO.read(new File((String) chooseGraphCB.getSelectedItem()));
				} catch (IOException exception) {
				    exception.printStackTrace();
				}
				Image dimg = img.getScaledInstance(lblGraph.getWidth(), lblGraph.getHeight(),
				        Image.SCALE_SMOOTH);
				ImageIcon imgIco = new ImageIcon(dimg);

				lblGraph.setIcon(imgIco);
				lblGraph.setBounds(10, 10, 200, 200);
				lblGraph.setSize(100,100);
				Analyzer.this.frm.pack();
			}
		});
		
		btnGo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String policyType = (String) policyTypeCB.getSelectedItem();
				if (policyType.equals("Any")) {
					policyType = null;
				}
				String policyStatusStr = (String) policyStatusCB.getSelectedItem();
				PolicyStatus policyStatus;
				if (policyStatusStr.equals("Any")) {
					policyStatus = null;
				} else if (policyStatusStr.equals("Active")) {
					policyStatus = PolicyStatus.ACTIVE;
				} else if (policyStatusStr.equals("Cancelled by Policy Holder")) {
					policyStatus = PolicyStatus.CANCELLED_BY_POLICYHOLDER;
				} else if (policyStatusStr.equals("Cancelled by Company")) {
					policyStatus = PolicyStatus.CANCELLED_BY_COMPANY;
				} else {
					policyStatus = null;
				}
				String state = (String) stateCB.getSelectedItem();
				if(state.equals("Any")) {
					state = null;
				}
				String ageRangeStr = ageTxt.getText();
				int[] ageRange;
				if(ageRangeStr.equals("")) {
					ageRange = new int[0];
				} else {
					String[] ageRangeSplit = ageRangeStr.split("-");
					for(int i = 0; i < ageRangeSplit.length; i++) {
						ageRangeSplit[i] = ageRangeSplit[i].trim();
					}
					if (ageRangeSplit.length == 2) {
						int min = Integer.parseInt(ageRangeSplit[0]);
						int max = Integer.parseInt(ageRangeSplit[1]);
						ageRange = new int[min-max+1];
						for (int i = min; i <= max; i++) {
							ageRange[i-min] = i;
						}
					} else {//just one elem
						ageRange = new int[1];
						ageRange[0] = Integer.parseInt(ageRangeSplit[0]);
					}
				}
				//------
				String accidentsStr = accidentsTxt.getText();
				int[] accidents;
				if(accidentsStr.equals("")) {
					accidents = new int[0];
				}
				else {
					String[] accidentsSplit = accidentsStr.split("-");
					for(int i = 0; i < accidentsSplit.length; i++) {
						accidentsSplit[i] = accidentsSplit[i].trim();
					}
					
					if (accidentsSplit.length == 2) {
						int min = Integer.parseInt(accidentsSplit[0]);
						int max = Integer.parseInt(accidentsSplit[1]);
						accidents = new int[min-max+1];
						for (int i = min; i <= max; i++) {
							accidents[i-min] = i;
						}
					} else {//just one elem
						accidents = new int[1];
						accidents[0] = Integer.parseInt(accidentsSplit[0]);
					}
				}
				String output = "";
				output += String.format("N: %s \n", PolicyStat.findN(policies, policyType, policyStatus, state, ageRange, accidents));
				output += String.format("Average Premium: %s \n", PolicyStat.findMeanPremium(policies, policyType, policyStatus, state, ageRange, accidents));
				output += String.format("Premium Standard Deviation: %s \n", PolicyStat.findStandardDeviation(policies, policyType, policyStatus, state, ageRange, accidents));
				output += String.format("Median Premium: %s \n", PolicyStat.findMedianPremium(policies, policyType, policyStatus, state, ageRange, accidents));
				outputTxt.setText(output);
			}
		});
	}
	private void somethingInteresting() throws Exception{
		this.policies = new Reader().read();
		for(Policy p : policies) {
			System.out.println(p);
		}
		System.out.println(policies);
		URL url = this.getClass().getResource("/graphs");
		URI uri = new URI(url.toString());
		File graphFolder = new File(uri.getPath());
		chooseGraphCB.addItem("");
		for (String file : graphFolder.list()) {
			chooseGraphCB.addItem(file);
		}
		//add states to the stateCB for the policies that we have
		ArrayList<String> states = new ArrayList<>();
		stateCB.addItem("Any");
		for(Policy p : policies) {
			String state = p.getState();
			if (!states.contains(state)) {
				states.add(state);
			}
		}
		Collections.sort(states);
		for (String state : states) {
			stateCB.addItem(state);
		}
		
	}	
	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frm = new JFrame();
		frm.setTitle("State Farm Data Analytics");
		frm.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\alexk\\git\\2019-Coding-Competition-Finals\\statefarm logo.jpg"));
		frm.setBounds(100, 100, 450, 399);
		frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frm.getContentPane().setLayout(new CardLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		tabbedPane.setName("tabbedPane");
		frm.getContentPane().add(tabbedPane, "name_335440691485800");
		
		JPanel dataAnalyticsPanel = new JPanel();
		tabbedPane.addTab("Data Analytics", null, dataAnalyticsPanel, null);
		
		JLabel lblPolicyType = new JLabel("Policy Type:");
		
		JLabel lblPolicyStatus = new JLabel("Policy Status:");
		
		JLabel lblState = new JLabel("State:");
		lblState.setMaximumSize(new Dimension(77, 13));
		
		JLabel lblAge = new JLabel("Age:");
		
		JLabel lblNumberOfAccidents = new JLabel("# of Accidents:");
		
		policyTypeCB = new JComboBox();
		policyTypeCB.setModel(new DefaultComboBoxModel(new String[] {"Any", "Renters", "Private Passenger"}));
		
		policyStatusCB = new JComboBox();
		policyStatusCB.setModel(new DefaultComboBoxModel(new String[] {"Any", "Active", "Cancelled by Policy Holder", "Cancelled by Company"}));
		
		stateCB = new JComboBox();
		
		ageTxt = new JTextField();
		ageTxt.setColumns(10);
		
		accidentsTxt = new JTextField();
		accidentsTxt.setColumns(10);
		
		outputTxt = new JTextPane();
		outputTxt.setEditable(false);
		
		btnGo = new JButton("Go!");
		GroupLayout gl_dataAnalyticsPanel = new GroupLayout(dataAnalyticsPanel);
		gl_dataAnalyticsPanel.setHorizontalGroup(
			gl_dataAnalyticsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dataAnalyticsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.LEADING)
						.addComponent(outputTxt, Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE)
						.addGroup(gl_dataAnalyticsPanel.createSequentialGroup()
							.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.LEADING)
								.addComponent(lblNumberOfAccidents, GroupLayout.PREFERRED_SIZE, 94, GroupLayout.PREFERRED_SIZE)
								.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.LEADING, false)
									.addComponent(lblState, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblPolicyStatus, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
									.addComponent(lblPolicyType, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
								.addComponent(lblAge, GroupLayout.PREFERRED_SIZE, 79, GroupLayout.PREFERRED_SIZE))
							.addPreferredGap(ComponentPlacement.RELATED)
							.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.LEADING, false)
								.addComponent(stateCB, 0, 195, Short.MAX_VALUE)
								.addComponent(policyTypeCB, 0, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
								.addComponent(policyStatusCB, 0, 195, Short.MAX_VALUE)
								.addComponent(ageTxt)
								.addComponent(accidentsTxt)))
						.addComponent(btnGo, Alignment.TRAILING, GroupLayout.PREFERRED_SIZE, 85, GroupLayout.PREFERRED_SIZE))
					.addContainerGap())
		);
		gl_dataAnalyticsPanel.setVerticalGroup(
			gl_dataAnalyticsPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_dataAnalyticsPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPolicyType)
						.addComponent(policyTypeCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblPolicyStatus)
						.addComponent(policyStatusCB, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblState, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
						.addComponent(stateCB, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblAge)
						.addComponent(ageTxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addPreferredGap(ComponentPlacement.RELATED)
					.addGroup(gl_dataAnalyticsPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblNumberOfAccidents)
						.addComponent(accidentsTxt, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(28)
					.addComponent(outputTxt, GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
					.addGap(18)
					.addComponent(btnGo)
					.addContainerGap())
		);
		dataAnalyticsPanel.setLayout(gl_dataAnalyticsPanel);
		
		JPanel graphPanel = new JPanel();
		graphPanel.setName("Graphs\r\n");
		tabbedPane.addTab("Graphs", null, graphPanel, null);
		
		chooseGraphCB = new JComboBox<String>();
		
		
		JLabel lblHello = new JLabel("Graph:");
		
		lblGraph = new JLabel("");
		GroupLayout gl_graphPanel = new GroupLayout(graphPanel);
		gl_graphPanel.setHorizontalGroup(
			gl_graphPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_graphPanel.createSequentialGroup()
					.addContainerGap()
					.addGroup(gl_graphPanel.createParallelGroup(Alignment.LEADING)
						.addGroup(gl_graphPanel.createSequentialGroup()
							.addComponent(lblHello)
							.addPreferredGap(ComponentPlacement.UNRELATED)
							.addComponent(chooseGraphCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
						.addComponent(lblGraph, GroupLayout.DEFAULT_SIZE, 411, Short.MAX_VALUE))
					.addContainerGap())
		);
		gl_graphPanel.setVerticalGroup(
			gl_graphPanel.createParallelGroup(Alignment.LEADING)
				.addGroup(gl_graphPanel.createSequentialGroup()
					.addGap(5)
					.addGroup(gl_graphPanel.createParallelGroup(Alignment.BASELINE)
						.addComponent(lblHello)
						.addComponent(chooseGraphCB, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
					.addGap(11)
					.addComponent(lblGraph, GroupLayout.DEFAULT_SIZE, 189, Short.MAX_VALUE)
					.addContainerGap())
		);
		graphPanel.setLayout(gl_graphPanel);
		tabbedPane.setFocusTraversalPolicy(new FocusTraversalOnArray(new Component[]{graphPanel, lblHello, chooseGraphCB, dataAnalyticsPanel}));
	}
}
