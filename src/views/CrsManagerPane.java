package views;


import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URI;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.json.simple.parser.ParseException;

import supportive.Bug2goDownloader;
import supportive.OldCRsCloser;
import supportive.JiraSatApi;
import core.Logger;
import core.SharedObjs;
import core.XmlMngr;
import customobjects.CrItem;
import customobjects.CrItemsList;


@SuppressWarnings("serial")
public class CrsManagerPane extends JPanel
{
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private JTextArea   textDownload;
	private JTextField  textPath;
	private JTextField  textLabels;
	private JTextPane   textLog;
	private JTextPane   textPane;
	private JCheckBox   chckbxAssign;
	private JCheckBox   chckbxLabels;
	private JCheckBox   chckbxUnassign;
	private JCheckBox   chckbxRemLabels;
	private JCheckBox   chckbxDownload;
	private JCheckBox   chckbxUnzip;
	private JCheckBox   chckbxAnalyze;
	private JCheckBox   chckbxCloseAsOld;
	private JCheckBox   chckbxIgnoreAnalyzed;
	private String      CRs[];
	private String      labels[];
	private int         errors;
	private JButton     btnDownload;
	private CrItemsList ignoredList;
	private JButton btnOpenOnBrowser;
	private JButton btnLists;
	private JButton btnClear;
	private JCheckBox btnPaste;
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Constructor -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public CrsManagerPane()
	{
		setPreferredSize(new Dimension(632, 695));
		setMinimumSize(new Dimension(600, 950));
		setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel contentPane = new JPanel();
		contentPane.setMinimumSize(new Dimension(10, 700));
		add(contentPane);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0, 270, 0, 0};
		gridBagLayout.rowHeights = new int[] {140, 210, 0, 0};
		gridBagLayout.columnWeights = new double[] {1.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {0.0, 10.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gridBagLayout);
		
		JPanel panel_3 = new JPanel();
		panel_3.setMinimumSize(new Dimension(160, 10));
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.gridheight = 2;
		gbc_panel_3.insets = new Insets(10, 5, 5, 5);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 0;
		contentPane.add(panel_3, gbc_panel_3);
		
		GridBagLayout gbl_panel_3 = new GridBagLayout();
		gbl_panel_3.columnWidths = new int[] {0, 0};
		gbl_panel_3.rowHeights = new int[] {0, 0, 0, 0, 0, 0};
		gbl_panel_3.columnWeights = new double[] {1.0, Double.MIN_VALUE};
		gbl_panel_3.rowWeights = new double[] {0.0, 0.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_3.setLayout(gbl_panel_3);
		
		JLabel lblDownloader = new JLabel("CRs List");
		lblDownloader.setHorizontalAlignment(SwingConstants.CENTER);
		lblDownloader.setFont(new Font("Tahoma", Font.BOLD, 18));
		GridBagConstraints gbc_lblDownloader = new GridBagConstraints();
		gbc_lblDownloader.fill = GridBagConstraints.BOTH;
		gbc_lblDownloader.insets = new Insets(0, 0, 5, 0);
		gbc_lblDownloader.gridx = 0;
		gbc_lblDownloader.gridy = 0;
		panel_3.add(lblDownloader, gbc_lblDownloader);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		scrollPane_2.setPreferredSize(new Dimension(150, 350));
		scrollPane_2.setBorder(new LineBorder(SystemColor.activeCaption));
		scrollPane_2.setMinimumSize(new Dimension(150, 300));
		GridBagConstraints gbc_scrollPane_2 = new GridBagConstraints();
		gbc_scrollPane_2.gridheight = 2;
		gbc_scrollPane_2.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_2.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_2.gridx = 0;
		gbc_scrollPane_2.gridy = 1;
		panel_3.add(scrollPane_2, gbc_scrollPane_2);
		
		textDownload = new JTextArea();
		textDownload.setToolTipText("List of CRs to be downloaded. Used to create the link between downloded CRs and its Jira ID too.");
		textDownload.setFont(new Font("Tahoma", Font.PLAIN, 11));
		scrollPane_2.setViewportView(textDownload);
		textDownload.setTabSize(4);
		textDownload.setBorder(null);
		
		btnClear = new JButton("Clear");
		btnClear.setToolTipText("Clear the text area above");
		btnClear.setPreferredSize(new Dimension(113, 23));
		btnClear.setMaximumSize(new Dimension(113, 23));
		btnClear.setMinimumSize(new Dimension(113, 23));
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.insets = new Insets(0, 0, 5, 0);
		gbc_btnClear.gridx = 0;
		gbc_btnClear.gridy = 3;
		panel_3.add(btnClear, gbc_btnClear);
		
		btnPaste = new JCheckBox("New check box");
		GridBagConstraints gbc_btnPaste = new GridBagConstraints();
		gbc_btnPaste.gridx = 0;
		gbc_btnPaste.gridy = 4;
		panel_3.add(btnPaste, gbc_btnPaste);
		
		JPanel panel_1 = new JPanel();
		panel_1.setMinimumSize(new Dimension(250, 350));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.NORTH;
		gbc_panel_1.gridheight = 2;
		gbc_panel_1.insets = new Insets(10, 5, 5, 5);
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 2;
		gbc_panel_1.gridy = 0;
		contentPane.add(panel_1, gbc_panel_1);
		
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {0};
		gbl_panel_1.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[] {0.0, 1.0};
		gbl_panel_1.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel label = new JLabel("Action");
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.BOLD, 18));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.BOTH;
		gbc_label.gridwidth = 2;
		gbc_label.insets = new Insets(0, 0, 5, 0);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		panel_1.add(label, gbc_label);
		
		chckbxAssign = new JCheckBox("Assign CRs");
		chckbxAssign.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_chckbxAssign = new GridBagConstraints();
		gbc_chckbxAssign.anchor = GridBagConstraints.WEST;
		gbc_chckbxAssign.fill = GridBagConstraints.BOTH;
		gbc_chckbxAssign.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxAssign.gridx = 0;
		gbc_chckbxAssign.gridy = 1;
		panel_1.add(chckbxAssign, gbc_chckbxAssign);
		chckbxAssign.setMargin(new Insets(0, 2, 0, 2));
		chckbxAssign.setMinimumSize(new Dimension(15, 20));
		
		chckbxUnassign = new JCheckBox("Unassign CRs");
		GridBagConstraints gbc_chckbxUnassign = new GridBagConstraints();
		gbc_chckbxUnassign.fill = GridBagConstraints.BOTH;
		gbc_chckbxUnassign.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxUnassign.gridx = 0;
		gbc_chckbxUnassign.gridy = 2;
		panel_1.add(chckbxUnassign, gbc_chckbxUnassign);
		
		JSeparator separator = new JSeparator();
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.insets = new Insets(0, 5, 5, 0);
		gbc_separator.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator.gridwidth = 2;
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 3;
		panel_1.add(separator, gbc_separator);
		
		chckbxLabels = new JCheckBox("Add labels");
		GridBagConstraints gbc_chckbxLabels = new GridBagConstraints();
		gbc_chckbxLabels.fill = GridBagConstraints.BOTH;
		gbc_chckbxLabels.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxLabels.gridx = 0;
		gbc_chckbxLabels.gridy = 4;
		panel_1.add(chckbxLabels, gbc_chckbxLabels);
		chckbxLabels.setMargin(new Insets(0, 2, 0, 2));
		chckbxLabels.setMinimumSize(new Dimension(61, 15));
		
		textLabels = new JTextField();
		textLabels.setPreferredSize(new Dimension(150, 20));
		GridBagConstraints gbc_textLabel = new GridBagConstraints();
		gbc_textLabel.gridheight = 2;
		gbc_textLabel.fill = GridBagConstraints.HORIZONTAL;
		gbc_textLabel.insets = new Insets(0, 5, 5, 0);
		gbc_textLabel.gridx = 1;
		gbc_textLabel.gridy = 4;
		panel_1.add(textLabels, gbc_textLabel);
		textLabels.setText("ll_prodteam_analyzed");
		textLabels.setColumns(10);
		textLabels.setBorder(new LineBorder(SystemColor.activeCaption));
		
		chckbxRemLabels = new JCheckBox("Remove labels");
		GridBagConstraints gbc_chckbxRemLabels = new GridBagConstraints();
		gbc_chckbxRemLabels.fill = GridBagConstraints.BOTH;
		gbc_chckbxRemLabels.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxRemLabels.gridx = 0;
		gbc_chckbxRemLabels.gridy = 5;
		panel_1.add(chckbxRemLabels, gbc_chckbxRemLabels);
		
		JSeparator separator_1 = new JSeparator();
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 2;
		gbc_separator_1.insets = new Insets(0, 5, 5, 0);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 6;
		panel_1.add(separator_1, gbc_separator_1);
		
		chckbxDownload = new JCheckBox("Download");
		chckbxDownload.setSelected(true);
		GridBagConstraints gbc_chckbxDownload = new GridBagConstraints();
		gbc_chckbxDownload.fill = GridBagConstraints.BOTH;
		gbc_chckbxDownload.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxDownload.gridx = 0;
		gbc_chckbxDownload.gridy = 7;
		panel_1.add(chckbxDownload, gbc_chckbxDownload);
		
		textPath = new JTextField();
		GridBagConstraints gbc_textPath = new GridBagConstraints();
		gbc_textPath.insets = new Insets(0, 5, 5, 0);
		gbc_textPath.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPath.gridx = 1;
		gbc_textPath.gridy = 7;
		panel_1.add(textPath, gbc_textPath);
		textPath.setToolTipText("Path to stock and read your CRs");
		textPath.setHorizontalAlignment(SwingConstants.LEFT);
		textPath.setBorder(new LineBorder(SystemColor.activeCaption));
		textPath.setMinimumSize(new Dimension(130, 20));
		textPath.setPreferredSize(new Dimension(150, 20));
		
		chckbxUnzip = new JCheckBox("Unzip downloaded CRs");
		chckbxUnzip.setSelected(true);
		GridBagConstraints gbc_chckbxUnzip = new GridBagConstraints();
		gbc_chckbxUnzip.fill = GridBagConstraints.BOTH;
		gbc_chckbxUnzip.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxUnzip.gridx = 0;
		gbc_chckbxUnzip.gridy = 8;
		panel_1.add(chckbxUnzip, gbc_chckbxUnzip);
		
		chckbxAnalyze = new JCheckBox("Analyze downloaded CRs");
		chckbxAnalyze.setSelected(true);
		GridBagConstraints gbc_chckbxAnalyze = new GridBagConstraints();
		gbc_chckbxAnalyze.fill = GridBagConstraints.BOTH;
		gbc_chckbxAnalyze.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxAnalyze.gridx = 0;
		gbc_chckbxAnalyze.gridy = 9;
		panel_1.add(chckbxAnalyze, gbc_chckbxAnalyze);
		
		chckbxCloseAsOld = new JCheckBox("Close CRs as Old");
		GridBagConstraints gbc_chckbxCloseAsOld = new GridBagConstraints();
		gbc_chckbxCloseAsOld.fill = GridBagConstraints.BOTH;
		gbc_chckbxCloseAsOld.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxCloseAsOld.gridx = 0;
		gbc_chckbxCloseAsOld.gridy = 10;
		panel_1.add(chckbxCloseAsOld, gbc_chckbxCloseAsOld);
		
		JSeparator separator_2 = new JSeparator();
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_2.gridwidth = 2;
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 11;
		panel_1.add(separator_2, gbc_separator_2);
		
		chckbxIgnoreAnalyzed = new JCheckBox("Ignore Analyzed CRs");
		chckbxIgnoreAnalyzed.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_chckbxIgnoreAnalyzed = new GridBagConstraints();
		gbc_chckbxIgnoreAnalyzed.anchor = GridBagConstraints.WEST;
		gbc_chckbxIgnoreAnalyzed.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxIgnoreAnalyzed.gridx = 0;
		gbc_chckbxIgnoreAnalyzed.gridy = 12;
		panel_1.add(chckbxIgnoreAnalyzed, gbc_chckbxIgnoreAnalyzed);
		
		btnDownload = new JButton("Exec!");
		GridBagConstraints gbc_btnDownload = new GridBagConstraints();
		gbc_btnDownload.insets = new Insets(25, 0, 5, 0);
		gbc_btnDownload.gridwidth = 2;
		gbc_btnDownload.gridx = 0;
		gbc_btnDownload.gridy = 13;
		panel_1.add(btnDownload, gbc_btnDownload);
		btnDownload.setToolTipText("Start to download the CRs on the list above");
		btnDownload.setPreferredSize(new Dimension(113, 23));
		btnDownload.setMaximumSize(new Dimension(113, 23));
		btnDownload.setMinimumSize(new Dimension(113, 23));
		
		btnLists = new JButton("Show Result");
		btnLists.setPreferredSize(new Dimension(113, 23));
		btnLists.setToolTipText("Open the CRs on the list above on Chrome");
		GridBagConstraints gbc_btnLists = new GridBagConstraints();
		gbc_btnLists.insets = new Insets(0, 0, 5, 0);
		gbc_btnLists.gridwidth = 2;
		gbc_btnLists.gridx = 0;
		gbc_btnLists.gridy = 14;
		panel_1.add(btnLists, gbc_btnLists);
		
		btnOpenOnBrowser = new JButton("Open on Browser");
		GridBagConstraints gbc_btnOpenOnBrowser = new GridBagConstraints();
		gbc_btnOpenOnBrowser.gridwidth = 2;
		gbc_btnOpenOnBrowser.gridx = 0;
		gbc_btnOpenOnBrowser.gridy = 15;
		panel_1.add(btnOpenOnBrowser, gbc_btnOpenOnBrowser);
		btnOpenOnBrowser.setToolTipText("Open the CRs on the list above on Chrome");
		
		JPanel panel_5 = new JPanel();
		panel_5.setBorder(new LineBorder(new Color(153, 204, 255), 1, true));
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.insets = new Insets(50, 0, 10, 0);
		gbc_panel_5.gridwidth = 4;
		gbc_panel_5.gridx = 0;
		gbc_panel_5.gridy = 2;
		contentPane.add(panel_5, gbc_panel_5);
		
		GridBagLayout gbl_panel_5 = new GridBagLayout();
		gbl_panel_5.columnWidths = new int[] {0, 0, 0};
		gbl_panel_5.rowHeights = new int[] {0, 0, 0};
		gbl_panel_5.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_5.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
		panel_5.setLayout(gbl_panel_5);
		
		JLabel lblStatusLog = new JLabel("Status Log:");
		GridBagConstraints gbc_lblStatusLog = new GridBagConstraints();
		gbc_lblStatusLog.anchor = GridBagConstraints.WEST;
		gbc_lblStatusLog.insets = new Insets(0, 10, 5, 5);
		gbc_lblStatusLog.gridx = 0;
		gbc_lblStatusLog.gridy = 0;
		panel_5.add(lblStatusLog, gbc_lblStatusLog);
		
		JLabel lblLastAction = new JLabel("Last Action:");
		GridBagConstraints gbc_lblLastAction = new GridBagConstraints();
		gbc_lblLastAction.anchor = GridBagConstraints.WEST;
		gbc_lblLastAction.insets = new Insets(0, 10, 5, 0);
		gbc_lblLastAction.gridx = 1;
		gbc_lblLastAction.gridy = 0;
		panel_5.add(lblLastAction, gbc_lblLastAction);
		
		JScrollPane scrollPane_3 = new JScrollPane();
		scrollPane_3.setPreferredSize(new Dimension(400, 82));
		scrollPane_3.setMinimumSize(new Dimension(400, 82));
		scrollPane_3.setBorder(new LineBorder(new Color(128, 128, 128)));
		GridBagConstraints gbc_scrollPane_3 = new GridBagConstraints();
		gbc_scrollPane_3.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPane_3.gridx = 0;
		gbc_scrollPane_3.gridy = 1;
		panel_5.add(scrollPane_3, gbc_scrollPane_3);
		
		textLog = new JTextPane();
		scrollPane_3.setViewportView(textLog);
		textLog.setEditable(false);
		textLog.setBorder(null);
		textLog.setPreferredSize(new Dimension(400, 82));
		textLog.setMinimumSize(new Dimension(400, 42));
		
		JScrollPane scrollPane_4 = new JScrollPane();
		scrollPane_4.setPreferredSize(new Dimension(100, 82));
		scrollPane_4.setMinimumSize(new Dimension(100, 82));
		scrollPane_4.setBorder(new LineBorder(new Color(128, 128, 128)));
		
		GridBagConstraints gbc_scrollPane_4 = new GridBagConstraints();
		gbc_scrollPane_4.gridx = 1;
		gbc_scrollPane_4.gridy = 1;
		panel_5.add(scrollPane_4, gbc_scrollPane_4);
		
		textPane = new JTextPane();
		scrollPane_4.setViewportView(textPane);
		textPane.setEditable(false);
		textPane.setBorder(null);
		textPane.setPreferredSize(new Dimension(100, 82));
		textPane.setMinimumSize(new Dimension(50, 42));
		
		loadUserData();
		errors = 0;
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Add Listeners ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void btnListsAddActionListener(ActionListener al)
	{
		btnLists.addActionListener(al);
	}
	
	public void btnPasteAddActionListener(ActionListener al)
	{
		btnPaste.addActionListener(al);
	}
	
	public void btnClearAddActionListener(ActionListener al)
	{
		btnClear.addActionListener(al);
	}
	
	public void btnDownloadAddActionListener(ActionListener al)
	{
		btnDownload.addActionListener(al);
	}
	
	public void btnOpenOnBrowserAddActionListener(ActionListener al)
	{
		btnOpenOnBrowser.addActionListener(al);
	}
	
	public void textPathAddDocumentListener(DocumentListener dl)
	{
		textPath.getDocument().addDocumentListener(dl);
	}

	public void chckbxDownloadAddChangeListener(ChangeListener cl)
	{
		chckbxDownload.addChangeListener(cl);
	}
	
	public void chckbxUnzipAddChangeListener(ChangeListener cl)
	{
		chckbxUnzip.addChangeListener(cl);
	}
	
	public void chckbxAssignAddChangeListener(ChangeListener cl)
	{
		chckbxAssign.addChangeListener(cl);
	}
	
	public void chckbxUnassignAddChangeListener(ChangeListener cl)
	{
		chckbxUnassign.addChangeListener(cl);
	}
	
	public void chckbxLabelsAddChangeListener(ChangeListener cl)
	{
		chckbxLabels.addChangeListener(cl);
	}
	
	public void chckbxRemLabelsAddChangeListener(ChangeListener cl)
	{
		chckbxRemLabels.addChangeListener(cl);
	}
	
	public void chckbxCloseAsOldAddChangeListener(ChangeListener cl)
	{
		chckbxCloseAsOld.addChangeListener(cl);
	}
	
	
	
	
	public void setupActions()
	{
		btnLists.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				if (SharedObjs.getClosedList() == null || SharedObjs.getOpenedList() == null)
				{
					JOptionPane.showMessageDialog(SharedObjs.crsManagerPane, "Error: The lists does not exist");
				}
				
				SharedObjs.getClosedList().setVisible(true);
				SharedObjs.getOpenedList().setVisible(true);
			}
		});
		btnPaste.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				btnPasteAction();
			}
		});
		btnClear.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				btnClearAction();
			}
		});
		btnDownload.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				btnDownloadAction();
			}
		});
		btnOpenOnBrowser.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				btnOpenAction();
			}
		});
		
		textPath.getDocument().addDocumentListener(new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent arg0)
			{
				SharedObjs.setDownloadPath(textPath.getText());
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0)
			{
				SharedObjs.setDownloadPath(textPath.getText());
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0)
			{
			}
		});
		
		chckbxDownload.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				
				if (chckbxDownload.isSelected())
				{
					chckbxUnzip.setEnabled(true);
					chckbxCloseAsOld.setSelected(false);
				}
				else
				{
					chckbxUnzip.setEnabled(false);
				}
			}
		});
		chckbxUnzip.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent arg0)
			{
				if (chckbxUnzip.isSelected() && chckbxUnzip.isEnabled())
				{
					chckbxAnalyze.setEnabled(true);
				}
				else
				{
					chckbxAnalyze.setEnabled(false);
				}
			}
		});
		chckbxAssign.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (chckbxAssign.isSelected())
				{
					chckbxUnassign.setSelected(false);
				}
			}
		});
		chckbxUnassign.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (chckbxUnassign.isSelected())
				{
					chckbxAssign.setSelected(false);
				}
			}
		});
		chckbxLabels.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (chckbxLabels.isSelected())
				{
					chckbxRemLabels.setSelected(false);
				}
			}
		});
		chckbxRemLabels.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (chckbxRemLabels.isSelected())
				{
					chckbxLabels.setSelected(false);
				}
			}
		});
		chckbxCloseAsOld.addChangeListener(new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if (chckbxCloseAsOld.isSelected())
				{
					chckbxDownload.setSelected(false);
				}
			}
		});
	}
	
	/**
	 * Download CRs
	 * 
	 * main download function
	 * @throws ParseException
	 */
	private void downloadCRs() throws ParseException
	{
		errors = 0;
		
		// Setup jira connection
		SharedObjs.crsManagerPane.addLogLine("Connecting to Jira ...");
		JiraSatApi jira = new JiraSatApi(JiraSatApi.DEFAULT_JIRA_URL, SharedObjs.getUser(), SharedObjs.getPass());
		
		// Get the CRs list
		CRs = textDownload.getText().replaceAll(" ", "").split("\n");
		
		Logger.log(Logger.TAG_CRSMANAGER, "CRs List:" + CRs.length);
		if (CRs.length == 0 || (CRs.length == 1 && !CRs[0].contains("-")))
		{
			SharedObjs.crsManagerPane.addLogLine("CRs list empty");
			enableOptionsAndBtns();
			return;
		}
		
		ArrayList<String> b2gList = new ArrayList<String>();
		
		// Get label list
		labels = textLabels.getText().split(" ");
		for (String s : labels)
		{
			Logger.log(Logger.TAG_CRSMANAGER, "Label entered: " + s);
		}
		
		SharedObjs.crsManagerPane.addLogLine("Acquiring " + CRs.length + " CRs data ...");
		SharedObjs.getCrsList().clear();
		
		// Manage CR
		int crsCount = 0;
		ignoredList = new CrItemsList();
		for (String crKey : CRs)
		{
			crKey = trimCR(crKey);
			if (crKey.equals(""))
			{
				SharedObjs.crsManagerPane.addLogLine("CR list is empty");
				return;
			}
			
			CrItem crItem = jira.getCrData(crKey);
			++crsCount;
			
			if (crItem != null)
			{
				if (chckbxIgnoreAnalyzed.isSelected())
				{
					if (crItem.getLabels().contains("sat_pre_analyzed"))
					{
						addLogLine(crsCount + " - " + crKey + " - Will not be analyzed");
						ignoredList.add(crItem);
					}
				}
				
				addLogLine(crsCount + " - " + crKey + " - got it");
				
				if (chckbxLabels.isSelected())
				{
					jira.assignIssue(crKey);
					jira.addLabel(crKey, labels);
					
					if (chckbxAssign.isSelected())
					{
						crItem.setAssignee(SharedObjs.getUser());
					}
					else
					{
						crItem.setAssignee("");
						jira.unassignIssue(crKey);
					}
				}
				else if (chckbxAssign.isSelected())
				{
					jira.assignIssue(crKey);
					crItem.setAssignee(SharedObjs.getUser());
				}
				
				SharedObjs.addCrToList(crItem);
				b2gList.add(crItem.getB2gID());
			}
			else
			{
				Logger.log(Logger.TAG_CRSMANAGER, "CR KEY: " + crKey + " seems not to exist. Or your user/password is wrong");
				SharedObjs.crsManagerPane.addLogLine("CR KEY: " + crKey + " seems not to exist. Or your user/password is wrong");
				errors++;
			}
		}
		
		if (chckbxDownload.isSelected())
			if (b2gList.size() > 0)
			{
				// Configure the B2gDownloader
				Bug2goDownloader b2gDownloader = Bug2goDownloader.getInstance();
				
				if (b2gDownloader.getExecutor() == null || b2gDownloader.getExecutor().isTerminated())
				{
					SharedObjs.crsManagerPane.addLogLine("Generating download list ...");
				}
				else
				{
					SharedObjs.crsManagerPane.addLogLine("New b2g files added to download list ...");
				}
				
				try
				{
					b2gDownloader.addBugIdList(b2gList);
					b2gDownloader.setError(errors);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				// Start download thread
				b2gDownloader.execute();
			}
			else
			{
				if (ignoredList.size() == CRs.length)
				{
					JOptionPane.showMessageDialog(SharedObjs.crsManagerPane, "All the CRs in the list were ignored.");
				}
				else
				{
					JOptionPane.showMessageDialog(SharedObjs.crsManagerPane, "There were errors during the b2g collection."
					                                                         + "\nWe could not get CRs data from Jira."
					                                                         + "\nYour pass or username may be wrong or " + "the CRs sent does not exist.");
				}
			}
		
		enableOptionsAndBtns();
	}
	
	/**
	 * Interface functions -------------------------------
	 */
	private void btnClearAction()
	{
		textDownload.setText("");
	}
	
	private void btnPasteAction()
	{
		textDownload.setText("");
		Scanner scanner;
		Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
		
		try
		{
			String string = (String) clipboard.getData(DataFlavor.stringFlavor);
			scanner = new Scanner(string);
			String str;
			
			while (scanner.hasNext())
			{
				str = scanner.nextLine();
				textDownload.setText(textDownload.getText() + str + "\n");
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(this, "An error occurred. Please check logs.");
			Logger.log(Logger.TAG_CRSMANAGER, ex.getMessage());
			ex.printStackTrace();
		}
	}
	
	private void btnDownloadAction()
	{
		disableOptionsAndBtns();
		
		new Thread(new Runnable()
		{
			
			@Override
			public void run()
			{
				try
				{
					// Download proccess
					if (chckbxDownload.isSelected())
					{
						File downloadPath = new File(textPath.getText().replace("\\", "/"));
						if (!downloadPath.exists())
							downloadPath.mkdir();
						
						downloadCRs();
					}
					
					// Close as old proccess
					else if (chckbxCloseAsOld.isSelected())
					{
						OldCRsCloser closer = new OldCRsCloser(textDownload.getText().split("\n"));
						new Thread(closer).start();
					}
					
					// Singular options solo definition
					else
					{
						// Get the CRs list
						SharedObjs.crsManagerPane.addLogLine("Ganerating CRs list...");
						CRs = textDownload.getText().replaceAll(" ", "").split("\n");
						
						// Check if not empty list
						Logger.log(Logger.TAG_CRSMANAGER, "CRs List:" + CRs.length);
						if (CRs.length == 0 || (CRs.length == 1 && !CRs[0].contains("-")))
						{
							SharedObjs.crsManagerPane.addLogLine("CRs list empty");
							enableOptionsAndBtns();
							return;
						}
						
						// Setup jira connection
						SharedObjs.crsManagerPane.addLogLine("Connecting to Jira ...");
						JiraSatApi jira = new JiraSatApi(JiraSatApi.DEFAULT_JIRA_URL, SharedObjs.getUser(), SharedObjs.getPass());
						
						// Assign CRs
						if (chckbxAssign.isSelected())
						{
							// If need to add labels too, do it at same time
							if (chckbxLabels.isSelected())
							{
								SharedObjs.crsManagerPane.addLogLine("Assigning and adding labels ...");
								
								// Get label list
								labels = textLabels.getText().split(" ");
								for (String s : labels)
								{
									Logger.log(Logger.TAG_CRSMANAGER, "Label entered: " + s);
								}
								
								for (String crKey : CRs)
								{
									crKey = trimCR(crKey);
									jira.assignIssue(crKey);
									jira.addLabel(crKey, labels);
								}
							}
							
							// Otherwise ...
							else
							{
								SharedObjs.crsManagerPane.addLogLine("Assigning CRs...");
								for (String crKey : CRs)
								{
									crKey = trimCR(crKey);
									jira.assignIssue(crKey);
								}
							}
							
							SharedObjs.crsManagerPane.addLogLine("Done");
						}
						
						// Add Labels
						else if (chckbxLabels.isSelected())
						{
							SharedObjs.crsManagerPane.addLogLine("Adding labels ...");
							
							// Get label list
							labels = textLabels.getText().split(" ");
							for (String s : labels)
							{
								Logger.log(Logger.TAG_CRSMANAGER, "Label entered: " + s);
							}
							
							// Manage CR
							for (String crKey : CRs)
							{
								crKey = trimCR(crKey);
								if (jira.addLabel(crKey, labels)
								        .contains("\"labels\":\"Field 'labels' cannot be set. It is not on the appropriate screen, or unknown"))
								{
									jira.assignIssue(crKey);
									jira.addLabel(crKey, labels);
									jira.unassignIssue(crKey);
								}
							}
							
							SharedObjs.crsManagerPane.addLogLine("Done");
						}
						
						// Unassign CRs
						if (chckbxUnassign.isSelected())
						{
							// If need to unassign issues too, do it at same time
							if (chckbxRemLabels.isSelected())
							{
								SharedObjs.crsManagerPane.addLogLine("Unassignin and removing labels ...");
								
								// Get label list
								labels = textLabels.getText().split(" ");
								for (String crKey : CRs)
								{
									crKey = trimCR(crKey);
									jira.removeLabel(crKey, labels);
									jira.unassignIssue(crKey);
								}
							}
							
							// Otherwise ...
							else
							{
								SharedObjs.crsManagerPane.addLogLine("Unassigning CRs ...");
								for (String crKey : CRs)
								{
									crKey = trimCR(crKey);
									jira.unassignIssue(crKey);
								}
							}
							
							SharedObjs.crsManagerPane.addLogLine("Done");
						}
						
						// Remove labels
						if (chckbxRemLabels.isSelected())
						{
							SharedObjs.crsManagerPane.addLogLine("Removing labels ...");
							
							// Get label list
							labels = textLabels.getText().split(" ");
							for (String s : labels)
							{
								Logger.log(Logger.TAG_CRSMANAGER, "Label entered: " + s);
							}
							
							for (String crKey : CRs)
							{
								crKey = trimCR(crKey);
								if (jira.removeLabel(crKey, labels)
								        .contains("\"labels\":\"Field 'labels' cannot be set. It is not on the appropriate screen, or unknown"))
								{
									jira.assignIssue(crKey);
									jira.removeLabel(crKey, labels);
									jira.unassignIssue(crKey);
								}
							}
							
							SharedObjs.crsManagerPane.addLogLine("Done");
						}
					}
				}
				catch (ParseException e)
				{
					e.printStackTrace();
				}
				finally
				{
					enableOptionsAndBtns();
				}
			}
		}).start();
	}
	
	public void enableOptionsAndBtns()
	{
		btnDownload.setEnabled(true);
		
		if (btnDownload.isSelected())
			chckbxAnalyze.setEnabled(true);
		
		if (chckbxAnalyze.isEnabled())
			chckbxUnzip.setEnabled(true);
		
		chckbxAssign.setEnabled(true);
		chckbxCloseAsOld.setEnabled(true);
		chckbxDownload.setEnabled(true);
		chckbxLabels.setEnabled(true);
		chckbxRemLabels.setEnabled(true);
		chckbxUnassign.setEnabled(true);
	}
	
	public void disableOptionsAndBtns()
	{
		btnDownload.setEnabled(false);
		chckbxAnalyze.setEnabled(false);
		chckbxAssign.setEnabled(false);
		chckbxAssign.setEnabled(false);
		chckbxCloseAsOld.setEnabled(false);
		chckbxDownload.setEnabled(false);
		chckbxLabels.setEnabled(false);
		chckbxRemLabels.setEnabled(false);
		chckbxUnassign.setEnabled(false);
		chckbxUnzip.setEnabled(false);
	}
	
	private void btnOpenAction()
	{
		for (String s : textDownload.getText().split("\n"))
		{
			try
			{
				s = trimCR(s);
				Desktop.getDesktop().browse(new URI("http://idart.mot.com/browse/" + s));
				Thread.sleep(500);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(this, "Exception: " + ex.getMessage());
			}
		}
	}
	
	public String trimCR(String s)
	{
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\t", "");
		s = s.trim();
		return s;
	}
	
	public void updateDiagList()
	{
		updateUI();
		repaint();
		revalidate();
	}
	
	public void addLogLine(String line)
	{
		if (textLog.getText().split("\n").length > 150)
		{
			try
			{
				File f = new File("Data\\logs\\log_" + new Timestamp(System.currentTimeMillis()).toString().replace(":", "_") + ".txt");
				BufferedWriter bw = new BufferedWriter(new FileWriter(f));
				bw.write(textLog.getText());
				bw.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			textLog.setText("");
		}
		
		Date date = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		// format.setTimeZone(TimeZone.getTimeZone("Brazil/East"));
		
		textLog.setText(textLog.getText() + format.format(date) + "\t" + line + "\n");
		textLog.setCaretPosition(textLog.getText().length());
	}
	
	/**
	 * Aux functions --------------------------------------------------------
	 */
	public void saveUserData()
	{
		String xmlPath[] = new String[] {"crs_jira_pane", ""};
		
		xmlPath[1] = "path";
		XmlMngr.setUserValueOf(xmlPath, textPath.getText());
		xmlPath[1] = "assign";
		XmlMngr.setUserValueOf(xmlPath, chckbxAssign.isSelected() + "");
		xmlPath[1] = "unassign";
		XmlMngr.setUserValueOf(xmlPath, chckbxUnassign.isSelected() + "");
		xmlPath[1] = "label";
		XmlMngr.setUserValueOf(xmlPath, chckbxLabels.isSelected() + "");
		xmlPath[1] = "rem_label";
		XmlMngr.setUserValueOf(xmlPath, chckbxRemLabels.isSelected() + "");
		xmlPath[1] = "labels";
		XmlMngr.setUserValueOf(xmlPath, textLabels.getText());
		xmlPath[1] = "download";
		XmlMngr.setUserValueOf(xmlPath, chckbxDownload.isSelected() + "");
		xmlPath[1] = "unzip";
		XmlMngr.setUserValueOf(xmlPath, chckbxUnzip.isSelected() + "");
		xmlPath[1] = "analyze";
		XmlMngr.setUserValueOf(xmlPath, chckbxAnalyze.isSelected() + "");
		xmlPath[1] = "close";
		XmlMngr.setUserValueOf(xmlPath, chckbxCloseAsOld.isSelected() + "");
		xmlPath[1] = "ignore";
		XmlMngr.setUserValueOf(xmlPath, chckbxIgnoreAnalyzed.isSelected() + "");
		
		Logger.log(Logger.TAG_CRSMANAGER, "CrsManagerPane data saved");
	}
	
	private void loadUserData()
	{
		String xmlPath[] = new String[] {"crs_jira_pane", ""};
		
		xmlPath[1] = "path";
		textPath.setText(XmlMngr.getUserValueOf(xmlPath));
		SharedObjs.setDownloadPath(textPath.getText());
		
		xmlPath[1] = "assign";
		chckbxAssign.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		xmlPath[1] = "unassign";
		chckbxUnassign.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "label";
		chckbxLabels.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		xmlPath[1] = "rem_label";
		chckbxRemLabels.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "labels";
		textLabels.setText(XmlMngr.getUserValueOf(xmlPath));
		
		xmlPath[1] = "download";
		chckbxDownload.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		System.out.println("" + Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "unzip";
		chckbxUnzip.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "analyze";
		chckbxAnalyze.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "close";
		chckbxCloseAsOld.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "ignore";
		chckbxIgnoreAnalyzed.setSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		Logger.log(Logger.TAG_CRSMANAGER, "CrsManagerPane variables Loaded");
	}
	
	public void runScript(String folder) throws IOException
	{
		Logger.log(Logger.TAG_CRSMANAGER, "Generating report output for " + folder);
		
		// File seek and load configuration
		File f = new File(folder);
		File[] filesList = f.listFiles();
		String reportFile = null, sCurrentLine;
		String bugreport = null;
		
		addLogLine("Generating bugreport for " + f.getName() + " ...");
		
		// Look for the file
		for (int j = 0; j < filesList.length; j++)
		{
			if (filesList[j].isFile())
			{
				String files = filesList[j].getName();
				if (files.toLowerCase().endsWith(".txt") && files.toLowerCase().contains("report_info"))
				{
					reportFile = folder + "\\" + files;
					break;
				}
			}
		}
		
		// Try to open file
		BufferedReader br = null;
		
		if (reportFile == null)
		{
			Logger.log(Logger.TAG_CRSMANAGER, "Log not found: " + reportFile);
			Logger.log(Logger.TAG_CRSMANAGER, "Not possible to find product ");
			JOptionPane.showMessageDialog(null, "Could not find product ! Report output not being generated for this CR");
			return;
		}
		else
		{
			br = new BufferedReader(new FileReader(reportFile));
		}
		
		// Parse file
		boolean parsed = false;
		String bpVersion = "";
		
		while ((sCurrentLine = br.readLine()) != null)
		{
			sCurrentLine = sCurrentLine.toLowerCase();
			if (sCurrentLine.contains("bpversion"))
			{
				Logger.log(Logger.TAG_CRSMANAGER, "--- Initial line: " + sCurrentLine);
				Matcher m = Pattern.compile(".*bpversion\": \".+ (.+)\".*").matcher(sCurrentLine);
				if (m.matches())
				{
					bpVersion = m.group(1);
					Logger.log(Logger.TAG_CRSMANAGER, "bpVersion: " + bpVersion);
					bpVersion = bpVersion.substring(0, bpVersion.indexOf("_"));
				}
			}
			else if (sCurrentLine.contains("product"))
			{
				Logger.log(Logger.TAG_CRSMANAGER, "--- Initial line: " + sCurrentLine);
				sCurrentLine = sCurrentLine.replace("\"product\": \"", "").replace(" ", "");
				
				// BATTRIAGE-212
				if (sCurrentLine.indexOf("_") >= 0)
				{
					sCurrentLine = sCurrentLine.substring(0, sCurrentLine.indexOf("_"));
				}
				else if (sCurrentLine.indexOf("\"") >= 0)
				{
					sCurrentLine = sCurrentLine.substring(0, sCurrentLine.indexOf("\""));
				}
				Logger.log(Logger.TAG_CRSMANAGER, sCurrentLine);
				
				if (sCurrentLine.equals("griffin") || sCurrentLine.equals("unknown"))
				{
					sCurrentLine = bpVersion;
				}
				
				Logger.log(Logger.TAG_CRSMANAGER, "Product name detected: " + sCurrentLine);
				
				SharedObjs.copyScript(new File("Data\\scripts\\_Base.pl"), new File(folder + "\\build_report.pl"));
				
				// Configure build report battery capacity
				PrintWriter out = null;
				try
				{
					@SuppressWarnings("resource")
					Scanner scanner = new Scanner(new File(folder + "\\build_report.pl"));
					String content = scanner.useDelimiter("\\Z").next();
					scanner.close();
					
					// Get/Set battery capacity
					if (SharedObjs.advOptions.getBatCapValue(sCurrentLine) != null)
					{
						content = content.replace("#bat_cap#", SharedObjs.advOptions.getBatCapValue(sCurrentLine));
					}
					else
					{
						String pName = JOptionPane.showInputDialog("Type the product name", sCurrentLine);
						String bCap = JOptionPane.showInputDialog("Type the battery capacity");
						SharedObjs.advOptions.addNewBatCapValue(pName, bCap);
						content = content.replace("#bat_cap#", bCap);
					}
					
					out = new PrintWriter(folder + "\\build_report.pl");
					out.println(content);
					parsed = true;
				}
				catch (FileNotFoundException e)
				{
					e.printStackTrace();
				}
				finally
				{
					out.close();
				}
				
				break;
			}
		}
		
		if (!parsed)
		{
			PrintWriter out = null;
			try
			{
				Logger.log(Logger.TAG_CRSMANAGER, "Could not find product  or product battery capacity. Using 3000 as bat cap");
				JOptionPane.showMessageDialog(null, "Could not find product  or product battery capacity.\nUsing 3000 as battery capacity");
				@SuppressWarnings("resource")
				Scanner scanner = new Scanner(new File(folder + "\\build_report.pl"));
				String content = scanner.useDelimiter("\\Z").next();
				content = content.replace("#bat_cap#", "3000");
				out = new PrintWriter(folder + "\\build_report.pl");
				out.println(content);
				out.close();
				parsed = true;
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			finally
			{
				if (out != null)
					out.close();
			}
		}
		
		if (br != null)
			br.close();
		
		for (File file : filesList)
		{
			if (file.getName().contains("bugreport"))
			{
				bugreport = file.getName();
			}
		}
		
		ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd \"" + folder + "\" && build_report.pl " + bugreport + " > report-output.txt");
		Logger.log(Logger.TAG_CRSMANAGER, "Report Output file: " + bugreport);
		
		for (String c : builder.command())
		{
			Logger.log(Logger.TAG_CRSMANAGER, "Commands: " + c);
		}
		
		builder.redirectErrorStream(true);
		Process p = builder.start();
		BufferedReader r = new BufferedReader(new InputStreamReader(p.getInputStream()));
		String line = "";
		String output = ""; // workaround for report outout 0kb
		
		while (true)
		{
			line = r.readLine();
			
			if (line == null)
			{
				break;
			}
			
			output += line + "\n";
			
			Logger.log(Logger.TAG_CRSMANAGER, line);
		}
		
		r.close();
		
		if (new File(folder + "/report-output.txt").length() < 10)
		{
			BufferedWriter bw = new BufferedWriter(new FileWriter(new File(folder + "/report-output.txt")));
			bw.write(output);
			bw.close();
		}
		
		addLogLine("Report output generated for " + f.getName());
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Supportive methods ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public String getDownloadPath()
	{
		return textPath.getText().replace("\\", "\\\\").concat("\\\\");
	}
	
	public String[] getCrsToDownload()
	{
		return textDownload.getText().replace(" ", "").replace("\r", "").split("\n");
	}
	
	public String[] getLabelsArray()
	{
		return textLabels.getText().split(" ");
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Getters ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public String getTextDownload()
	{
		return textDownload.getText();
	}
	
	public String getTextPath()
	{
		return textPath.getText();
	}
	
	public String getTextLabels()
	{
		return textLabels.getText();
	}
	
	public String getTextLog()
	{
		return textLog.getText();
	}
	
	public String getTextPane()
	{
		return textPane.getText();
	}
	
	public Boolean isChckbxAssignSelected()
	{
		return chckbxAssign.isSelected();
	}
	
	public Boolean isChckbxLabelsSelected()
	{
		return chckbxLabels.isSelected();
	}
	
	public Boolean isChckbxUnassignSelected()
	{
		return chckbxUnassign.isSelected();
	}
	
	public Boolean isChckbxRemLabelsSelected()
	{
		return chckbxRemLabels.isSelected();
	}
	
	public Boolean isChckbxDownloadSelected()
	{
		return chckbxDownload.isSelected();
	}
	
	public Boolean isChckbxUnzipSelected()
	{
		return chckbxUnzip.isSelected();
	}
	
	public Boolean isChckbxAnalyzeSelected()
	{
		return chckbxAnalyze.isSelected();
	}
	
	public Boolean isChckbxCloseAsOldSelected()
	{
		return chckbxCloseAsOld.isSelected();
	}
	
	public Boolean isChckbxIgnoreAnalyzedSelected()
	{
		return chckbxIgnoreAnalyzed.isSelected();
	}
	
	public String[] getCRs()
	{
		return CRs;
	}
	
	public String[] getLabels()
	{
		return labels;
	}
	
	public int getErrors()
	{
		return errors;
	}
	
	public CrItemsList getIgnoredList()
	{
		return ignoredList;
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Setters ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void setTextDownload(String text)
	{
		textDownload.setText(text);
	}
	
	public void setTextPath(String text)
	{
		textPath.setText(text);
	}
	
	public void setTextLabels(String text)
	{
		textLabels.setText(text);
	}
	
	public void setTextLog(String text)
	{
		textLog.setText(text);
	}
	
	public void setTextPane(String text)
	{
		textPane.setText(text);
	}
	
	public void setChckbxAssign(Boolean value)
	{
		chckbxAssign.setSelected(value);
	}
	
	public void setChckbxLabels(Boolean value)
	{
		chckbxLabels.setSelected(value);
	}
	
	public void setChckbxUnassign(Boolean value)
	{
		chckbxUnassign.setSelected(value);
	}
	
	public void setChckbxRemLabels(Boolean value)
	{
		chckbxRemLabels.setSelected(value);
	}
	
	public void setChckbxDownload(Boolean value)
	{
		chckbxDownload.setSelected(value);
	}
	
	public void setChckbxUnzip(Boolean value)
	{
		chckbxUnzip.setSelected(value);
	}
	
	public void setChckbxAnalyze(Boolean value)
	{
		chckbxAnalyze.setSelected(value);
	}
	
	public void setChckbxCloseAsOld(Boolean value)
	{
		chckbxCloseAsOld.setSelected(value);
	}
	
	public void setChckbxIgnoreAnalyzed(Boolean value)
	{
		chckbxIgnoreAnalyzed.setSelected(value);
	}
	
	public void setCRs(String[] cRs)
	{
		CRs = cRs;
	}
	
	public void setLabels(String[] labels)
	{
		this.labels = labels;
	}
	
	public void setErrors(int errors)
	{
		this.errors = errors;
	}
	
	public void setIgnoredList(CrItemsList ignoredList)
	{
		this.ignoredList = ignoredList;
	}
}
