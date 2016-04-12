package views;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentListener;


@SuppressWarnings("serial")
public class CrsManagerPane extends JPanel
{
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private JTextField textPath;
	private JTextField textLabels;
	private JTextArea  textDownload;
	private JTextPane  textLog;
	private JTextPane  textPane;
	private JCheckBox  chckbxAssign;
	private JCheckBox  chckbxLabels;
	private JCheckBox  chckbxUnassign;
	private JCheckBox  chckbxRemLabels;
	private JCheckBox  chckbxDownload;
	private JCheckBox  chckbxUnzip;
	private JCheckBox  chckbxAnalyze;
	private JCheckBox  chckbxCloseAsOld;
	private JCheckBox  chckbxIgnoreAnalyzed;
	private JButton    btnPaste;
	private JButton    btnOpenOnBrowser;
	private JButton    btnShowResultLists;
	private JButton    btnClear;
	private JButton    btnExecute;
	
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
		btnClear.setToolTipText("Clear text area above");
		btnClear.setPreferredSize(new Dimension(113, 23));
		btnClear.setMaximumSize(new Dimension(113, 23));
		btnClear.setMinimumSize(new Dimension(113, 23));
		GridBagConstraints gbc_btnClear = new GridBagConstraints();
		gbc_btnClear.insets = new Insets(0, 0, 5, 0);
		gbc_btnClear.gridx = 0;
		gbc_btnClear.gridy = 3;
		panel_3.add(btnClear, gbc_btnClear);
		
		btnPaste = new JButton("Paste");
		btnPaste.setToolTipText("Paste clipboard");
		btnPaste.setPreferredSize(new Dimension(113, 23));
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
		chckbxAssign.setToolTipText("Mark this option to assign the CR for you");
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
		chckbxUnassign.setToolTipText("Mark it to let CRs unassigned");
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
		chckbxLabels.setToolTipText("Mark it to add the specified labels to CRs");
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
		chckbxRemLabels.setToolTipText("Mark it to remove the specified labels to CRs");
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
		chckbxDownload.setToolTipText("Mark it to download CRs");
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
		chckbxUnzip.setToolTipText("Mark it to unzip downloaded CRs");
		chckbxUnzip.setSelected(true);
		GridBagConstraints gbc_chckbxUnzip = new GridBagConstraints();
		gbc_chckbxUnzip.fill = GridBagConstraints.BOTH;
		gbc_chckbxUnzip.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxUnzip.gridx = 0;
		gbc_chckbxUnzip.gridy = 8;
		panel_1.add(chckbxUnzip, gbc_chckbxUnzip);
		
		chckbxAnalyze = new JCheckBox("Analyze downloaded CRs");
		chckbxAnalyze.setToolTipText("Mark it to SAT analyze downloaded CRs");
		chckbxAnalyze.setSelected(true);
		GridBagConstraints gbc_chckbxAnalyze = new GridBagConstraints();
		gbc_chckbxAnalyze.fill = GridBagConstraints.BOTH;
		gbc_chckbxAnalyze.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxAnalyze.gridx = 0;
		gbc_chckbxAnalyze.gridy = 9;
		panel_1.add(chckbxAnalyze, gbc_chckbxAnalyze);
		
		chckbxCloseAsOld = new JCheckBox("Close CRs as old issues");
		chckbxCloseAsOld.setToolTipText("Mark it close the CRs as Old");
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
		chckbxIgnoreAnalyzed.setToolTipText("Mark it to ignore already analyzed CRs");
		chckbxIgnoreAnalyzed.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_chckbxIgnoreAnalyzed = new GridBagConstraints();
		gbc_chckbxIgnoreAnalyzed.anchor = GridBagConstraints.WEST;
		gbc_chckbxIgnoreAnalyzed.insets = new Insets(0, 0, 5, 5);
		gbc_chckbxIgnoreAnalyzed.gridx = 0;
		gbc_chckbxIgnoreAnalyzed.gridy = 12;
		panel_1.add(chckbxIgnoreAnalyzed, gbc_chckbxIgnoreAnalyzed);
		
		btnExecute = new JButton("Exec!");
		GridBagConstraints gbc_btnDownload = new GridBagConstraints();
		gbc_btnDownload.insets = new Insets(25, 0, 5, 0);
		gbc_btnDownload.gridwidth = 2;
		gbc_btnDownload.gridx = 0;
		gbc_btnDownload.gridy = 13;
		panel_1.add(btnExecute, gbc_btnDownload);
		btnExecute.setToolTipText("Start downloading listed CRs");
		btnExecute.setPreferredSize(new Dimension(113, 23));
		btnExecute.setMaximumSize(new Dimension(113, 23));
		btnExecute.setMinimumSize(new Dimension(113, 23));
		
		btnShowResultLists = new JButton("Show Results");
		btnShowResultLists.setPreferredSize(new Dimension(113, 23));
		btnShowResultLists.setToolTipText("Show analysis result");
		GridBagConstraints gbc_btnLists = new GridBagConstraints();
		gbc_btnLists.insets = new Insets(0, 0, 5, 0);
		gbc_btnLists.gridwidth = 2;
		gbc_btnLists.gridx = 0;
		gbc_btnLists.gridy = 14;
		panel_1.add(btnShowResultLists, gbc_btnLists);
		
		btnOpenOnBrowser = new JButton("Open on Browser");
		GridBagConstraints gbc_btnOpenOnBrowser = new GridBagConstraints();
		gbc_btnOpenOnBrowser.gridwidth = 2;
		gbc_btnOpenOnBrowser.gridx = 0;
		gbc_btnOpenOnBrowser.gridy = 15;
		panel_1.add(btnOpenOnBrowser, gbc_btnOpenOnBrowser);
		btnOpenOnBrowser.setToolTipText("Open listed CRs using your default browser");
		
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
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Add Listeners ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void btnShowResultListsAddActionListener(ActionListener al)
	{
		btnShowResultLists.addActionListener(al);
	}
	
	public void btnPasteAddActionListener(ActionListener al)
	{
		btnPaste.addActionListener(al);
	}
	
	public void btnClearAddActionListener(ActionListener al)
	{
		btnClear.addActionListener(al);
	}
	
	public void btnExecuteAddActionListener(ActionListener al)
	{
		btnExecute.addActionListener(al);
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
	
	public void setBtnDownloadEnabled(Boolean value)
	{
		btnExecute.setEnabled(value);
	}
	
	public void setChckbxAnalyzeEnabled(Boolean value)
	{
		chckbxAnalyze.setEnabled(value);
	}
	
	public void setChckbxAssignEnabled(Boolean value)
	{
		chckbxAssign.setEnabled(value);
	}
	
	public void setChckbxCloseAsOldEnabled(Boolean value)
	{
		chckbxCloseAsOld.setEnabled(value);
	}
	
	public void setChckbxDownloadEnabled(Boolean value)
	{
		chckbxDownload.setEnabled(value);
	}
	
	public void setChckbxLabelsEnabled(Boolean value)
	{
		chckbxLabels.setEnabled(value);
	}
	
	public void setChckbxRemLabelsEnabled(Boolean value)
	{
		chckbxRemLabels.setEnabled(value);
	}
	
	public void setChckbxUnassignEnabled(Boolean value)
	{
		chckbxUnassign.setEnabled(value);
	}
	
	public void setChckbxUnzipEnabled(Boolean value)
	{
		chckbxUnzip.setEnabled(value);
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
	
	public Boolean isChckbxUnzipEnabled()
	{
		return chckbxUnzip.isEnabled();
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

	public void setTextLogCarretPosition(int pos)
	{
		textLog.setCaretPosition(pos);
	}
	
	public void setTextPane(String text)
	{
		textPane.setText(text);
	}
	
	public void setChckbxAssignSelected(Boolean value)
	{
		chckbxAssign.setSelected(value);
	}
	
	public void setChckbxLabelsSelected(Boolean value)
	{
		chckbxLabels.setSelected(value);
	}
	
	public void setChckbxUnassignSelected(Boolean value)
	{
		chckbxUnassign.setSelected(value);
	}
	
	public void setChckbxRemLabelsSelected(Boolean value)
	{
		chckbxRemLabels.setSelected(value);
	}
	
	public void setChckbxDownloadSelected(Boolean value)
	{
		chckbxDownload.setSelected(value);
	}
	
	public void setChckbxUnzipSelected(Boolean value)
	{
		chckbxUnzip.setSelected(value);
	}
	
	public void setChckbxAnalyzeSelected(Boolean value)
	{
		chckbxAnalyze.setSelected(value);
	}
	
	public void setChckbxCloseAsOldSelected(Boolean value)
	{
		chckbxCloseAsOld.setSelected(value);
	}
	
	public void setChckbxIgnoreAnalyzedSelected(Boolean value)
	{
		chckbxIgnoreAnalyzed.setSelected(value);
	}
}