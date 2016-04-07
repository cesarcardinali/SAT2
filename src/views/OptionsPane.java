package views;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionListener;
import java.awt.event.FocusListener;
import java.awt.event.ItemListener;
import java.awt.event.MouseListener;

import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import views.secondarypanes.NotificationDialog;
import core.SharedObjs;


public class OptionsPane extends JPanel
{
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private JTextField         textConsumeFull;
	private JTextField         textConsumeOff;
	private JTextField         textConsumeOn;
	private JTextField         textHighCurrent;
	private JTextField         textKernelWake;
	private JTextField         textJavaWake;
	private JTextField         textSuspiciousHeader;
	private JTextField         textSuspicious;
	private JTextField         textAlarms;
	private JTextField         textB2g;
	private JTextField         textTether;
	private JTextField         textDiag;
	private JTextField         textUsername;
	private JPasswordField     textPassword;
	private JLabel             lblServerStatus;
	private JLabel             lblExpandCollapseIco;
	private JLabel             lblComments;
	private JLabel             lblParserOptions;
	private JLabel             lblMoreOptions;
	private JLabel             lblUserData;
	private JRadioButton       rdbtnSingleclick;
	private JRadioButton       rdbtnDouble;
	private JRadioButton       rdbtnTAnalisys;
	private JRadioButton       rdbtnNotepad;
	private JCheckBox          chkTextWrap;
	private JButton            btnManageFilters;
	private JButton            btnMoreOptions;
	private JButton            btnConsumeHelp;
	private JButton            btnSystemPMHelp;
	private JButton            btnSuspiciousHelp;
	private JButton            btnAlarmsHelp;
	private JButton            btnB2gHelp;
	private JButton            btnDiagHelp;
	private JButton            btnTetherHelp;
	private JPanel             panel_2;
	private NotificationDialog warning;
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Constructor -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public OptionsPane()
	{
		setMinimumSize(new Dimension(800, 600));
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {918};
		gridBagLayout.rowHeights = new int[] {590};
		gridBagLayout.columnWeights = new double[] {1.0};
		gridBagLayout.rowWeights = new double[] {1.0};
		setLayout(gridBagLayout);
		ButtonGroup editorSelector = new ButtonGroup();
		ButtonGroup breakdownSelector = new ButtonGroup();
		
		warning = new NotificationDialog("Help", "");
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		add(scrollPane, gbc_scrollPane);
		JPanel panel = new JPanel();
		panel.setMinimumSize(new Dimension(700, 10));
		panel.setBorder(null);
		scrollPane.setViewportView(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {150, 500, 0};
		gbl_panel.rowHeights = new int[] {0, 0, 0, 0, 20, 100, 0, 0, 0};
		gbl_panel.columnWeights = new double[] {1.0, 1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblUserData = new JLabel("User data:");
		lblUserData.setFont(new Font("Tahoma", Font.BOLD, 16));
		GridBagConstraints gbc_lblUserData = new GridBagConstraints();
		gbc_lblUserData.anchor = GridBagConstraints.WEST;
		gbc_lblUserData.insets = new Insets(0, 5, 5, 5);
		gbc_lblUserData.gridx = 0;
		gbc_lblUserData.gridy = 0;
		panel.add(lblUserData, gbc_lblUserData);
		
		lblServerStatus = new JLabel("Server Status");
		lblServerStatus.setFont(new Font("Cambria Math", Font.BOLD, 11));
		lblServerStatus.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_lblServerStatus = new GridBagConstraints();
		gbc_lblServerStatus.anchor = GridBagConstraints.EAST;
		gbc_lblServerStatus.insets = new Insets(0, 0, 5, 15);
		gbc_lblServerStatus.gridx = 1;
		gbc_lblServerStatus.gridy = 0;
		panel.add(lblServerStatus, gbc_lblServerStatus);
		
		JPanel panel_7 = new JPanel();
		panel_7.setBorder(new LineBorder(new Color(102, 153, 204), 1, true));
		GridBagConstraints gbc_panel_7 = new GridBagConstraints();
		gbc_panel_7.gridwidth = 2;
		gbc_panel_7.insets = new Insets(0, 15, 5, 15);
		gbc_panel_7.fill = GridBagConstraints.BOTH;
		gbc_panel_7.gridx = 0;
		gbc_panel_7.gridy = 1;
		panel.add(panel_7, gbc_panel_7);
		GridBagLayout gbl_panel_7 = new GridBagLayout();
		gbl_panel_7.columnWidths = new int[] {0, 50, 0, 50, 0, 0};
		gbl_panel_7.rowHeights = new int[] {0, 0};
		gbl_panel_7.columnWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_7.rowWeights = new double[] {0.0, Double.MIN_VALUE};
		panel_7.setLayout(gbl_panel_7);
		
		JLabel label_4 = new JLabel("Username: ");
		label_4.setPreferredSize(new Dimension(60, 14));
		label_4.setMinimumSize(new Dimension(60, 14));
		label_4.setMaximumSize(new Dimension(60, 14));
		label_4.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_label_4 = new GridBagConstraints();
		gbc_label_4.insets = new Insets(0, 0, 0, 5);
		gbc_label_4.anchor = GridBagConstraints.EAST;
		gbc_label_4.gridx = 0;
		gbc_label_4.gridy = 0;
		panel_7.add(label_4, gbc_label_4);
		
		textUsername = new JTextField();
		textUsername.setToolTipText("Motorola username");
		textUsername.setText((String) null);
		textUsername.setPreferredSize(new Dimension(90, 20));
		textUsername.setMinimumSize(new Dimension(90, 20));
		GridBagConstraints gbc_textUsername = new GridBagConstraints();
		gbc_textUsername.insets = new Insets(0, 0, 0, 5);
		gbc_textUsername.fill = GridBagConstraints.HORIZONTAL;
		gbc_textUsername.gridx = 1;
		gbc_textUsername.gridy = 0;
		panel_7.add(textUsername, gbc_textUsername);
		
		JLabel label_5 = new JLabel("Password: ");
		label_5.setPreferredSize(new Dimension(60, 14));
		label_5.setMinimumSize(new Dimension(60, 14));
		label_5.setMaximumSize(new Dimension(60, 14));
		label_5.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_label_5 = new GridBagConstraints();
		gbc_label_5.anchor = GridBagConstraints.EAST;
		gbc_label_5.insets = new Insets(0, 0, 0, 5);
		gbc_label_5.gridx = 2;
		gbc_label_5.gridy = 0;
		panel_7.add(label_5, gbc_label_5);
		
		textPassword = new JPasswordField();
		textPassword.setToolTipText("Motorola password");
		textPassword.setPreferredSize(new Dimension(90, 20));
		textPassword.setMinimumSize(new Dimension(90, 20));
		GridBagConstraints gbc_textPassword = new GridBagConstraints();
		gbc_textPassword.insets = new Insets(0, 0, 0, 5);
		gbc_textPassword.fill = GridBagConstraints.HORIZONTAL;
		gbc_textPassword.gridx = 3;
		gbc_textPassword.gridy = 0;
		panel_7.add(textPassword, gbc_textPassword);
		
		JPanel panel_8 = new JPanel();
		GridBagConstraints gbc_panel_8 = new GridBagConstraints();
		gbc_panel_8.fill = GridBagConstraints.BOTH;
		gbc_panel_8.insets = new Insets(10, 5, 5, 5);
		gbc_panel_8.gridx = 0;
		gbc_panel_8.gridy = 2;
		panel.add(panel_8, gbc_panel_8);
		GridBagLayout gbl_panel_8 = new GridBagLayout();
		gbl_panel_8.columnWidths = new int[] {150, 0, 0};
		gbl_panel_8.rowHeights = new int[] {0};
		gbl_panel_8.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
		gbl_panel_8.rowWeights = new double[] {0.0};
		panel_8.setLayout(gbl_panel_8);
		
		lblComments = new JLabel("Comments personalization");
		lblComments.setHorizontalAlignment(SwingConstants.LEFT);
		GridBagConstraints gbc_lblComments = new GridBagConstraints();
		gbc_lblComments.insets = new Insets(0, 0, 0, 5);
		gbc_lblComments.anchor = GridBagConstraints.WEST;
		gbc_lblComments.gridx = 0;
		gbc_lblComments.gridy = 0;
		panel_8.add(lblComments, gbc_lblComments);
		lblComments.setToolTipText("Click to hide/show");
		lblComments.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblComments.setAlignmentX(0.5f);
		
		lblExpandCollapseIco = new JLabel("");
		lblExpandCollapseIco.setVerticalAlignment(SwingConstants.BOTTOM);
		GridBagConstraints gbc_lblExpandCollapseIco = new GridBagConstraints();
		gbc_lblExpandCollapseIco.anchor = GridBagConstraints.SOUTHWEST;
		gbc_lblExpandCollapseIco.insets = new Insets(0, 0, 0, 5);
		gbc_lblExpandCollapseIco.gridx = 1;
		gbc_lblExpandCollapseIco.gridy = 0;
		panel_8.add(lblExpandCollapseIco, gbc_lblExpandCollapseIco);
		lblExpandCollapseIco.setIcon(new ImageIcon("Data\\pics\\collapse.png"));
		lblExpandCollapseIco.setToolTipText("Click to hide/show");
		lblExpandCollapseIco.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblExpandCollapseIco.setAlignmentX(0.5f);
		
		panel_2 = new JPanel();
		panel_2.setBorder(new LineBorder(new Color(102, 153, 204), 1, true));
		GridBagConstraints gbc_panel_2 = new GridBagConstraints();
		gbc_panel_2.fill = GridBagConstraints.BOTH;
		gbc_panel_2.insets = new Insets(0, 15, 5, 15);
		gbc_panel_2.gridwidth = 2;
		gbc_panel_2.gridx = 0;
		gbc_panel_2.gridy = 3;
		panel.add(panel_2, gbc_panel_2);
		GridBagLayout gbl_panel_2 = new GridBagLayout();
		gbl_panel_2.columnWidths = new int[] {175, 450, 30, 0};
		gbl_panel_2.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_panel_2.columnWeights = new double[] {0.0, 2.0, 0.0, Double.MIN_VALUE};
		gbl_panel_2.rowWeights = new double[] {
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        0.0,
		        Double.MIN_VALUE};
		panel_2.setLayout(gbl_panel_2);
		
		JLabel label_1 = new JLabel("High consumption apps:");
		label_1.setVerticalAlignment(SwingConstants.BOTTOM);
		label_1.setForeground(Color.DARK_GRAY);
		label_1.setMinimumSize(new Dimension(96, 18));
		label_1.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		label_1.setAlignmentX(0.5f);
		GridBagConstraints gbc_label_1 = new GridBagConstraints();
		gbc_label_1.anchor = GridBagConstraints.EAST;
		gbc_label_1.insets = new Insets(0, 0, 5, 5);
		gbc_label_1.gridx = 0;
		gbc_label_1.gridy = 0;
		panel_2.add(label_1, gbc_label_1);
		
		JLabel label_19 = new JLabel("Full log:");
		GridBagConstraints gbc_label_19 = new GridBagConstraints();
		gbc_label_19.anchor = GridBagConstraints.EAST;
		gbc_label_19.insets = new Insets(0, 0, 5, 5);
		gbc_label_19.gridx = 0;
		gbc_label_19.gridy = 1;
		panel_2.add(label_19, gbc_label_19);
		label_19.setMaximumSize(new Dimension(100, 14));
		label_19.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textConsumeFull = new JTextField();
		GridBagConstraints gbc_textConsumeFull = new GridBagConstraints();
		gbc_textConsumeFull.fill = GridBagConstraints.HORIZONTAL;
		gbc_textConsumeFull.insets = new Insets(0, 0, 5, 5);
		gbc_textConsumeFull.gridx = 1;
		gbc_textConsumeFull.gridy = 1;
		panel_2.add(textConsumeFull, gbc_textConsumeFull);
		textConsumeFull.setColumns(10);
		
		btnConsumeHelp = new JButton("?");
		btnConsumeHelp.setToolTipText("Click for help creating consumption comments");
		GridBagConstraints gbc_btnConsumeHelp = new GridBagConstraints();
		gbc_btnConsumeHelp.fill = GridBagConstraints.VERTICAL;
		gbc_btnConsumeHelp.gridheight = 3;
		gbc_btnConsumeHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnConsumeHelp.gridx = 2;
		gbc_btnConsumeHelp.gridy = 1;
		panel_2.add(btnConsumeHelp, gbc_btnConsumeHelp);
		btnConsumeHelp.setMargin(new Insets(2, 8, 2, 8));
		
		JLabel label_20 = new JLabel("Screen Off:");
		GridBagConstraints gbc_label_20 = new GridBagConstraints();
		gbc_label_20.anchor = GridBagConstraints.EAST;
		gbc_label_20.insets = new Insets(0, 0, 5, 5);
		gbc_label_20.gridx = 0;
		gbc_label_20.gridy = 2;
		panel_2.add(label_20, gbc_label_20);
		label_20.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textConsumeOff = new JTextField();
		GridBagConstraints gbc_textConsumeOff = new GridBagConstraints();
		gbc_textConsumeOff.fill = GridBagConstraints.HORIZONTAL;
		gbc_textConsumeOff.insets = new Insets(0, 0, 5, 5);
		gbc_textConsumeOff.gridx = 1;
		gbc_textConsumeOff.gridy = 2;
		panel_2.add(textConsumeOff, gbc_textConsumeOff);
		textConsumeOff.setColumns(10);
		
		JLabel label_21 = new JLabel("Screen On:");
		GridBagConstraints gbc_label_21 = new GridBagConstraints();
		gbc_label_21.insets = new Insets(0, 0, 5, 5);
		gbc_label_21.anchor = GridBagConstraints.EAST;
		gbc_label_21.gridx = 0;
		gbc_label_21.gridy = 3;
		panel_2.add(label_21, gbc_label_21);
		label_21.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textConsumeOn = new JTextField();
		GridBagConstraints gbc_textConsumeOn = new GridBagConstraints();
		gbc_textConsumeOn.fill = GridBagConstraints.HORIZONTAL;
		gbc_textConsumeOn.insets = new Insets(0, 0, 5, 5);
		gbc_textConsumeOn.gridx = 1;
		gbc_textConsumeOn.gridy = 3;
		panel_2.add(textConsumeOn, gbc_textConsumeOn);
		textConsumeOn.setColumns(10);
		
		JSeparator separator = new JSeparator();
		separator.setPreferredSize(new Dimension(500, 1));
		separator.setMinimumSize(new Dimension(3, 2));
		separator.setForeground(Color.LIGHT_GRAY);
		separator.setBackground(Color.WHITE);
		GridBagConstraints gbc_separator = new GridBagConstraints();
		gbc_separator.gridwidth = 3;
		gbc_separator.insets = new Insets(0, 0, 5, 0);
		gbc_separator.gridx = 0;
		gbc_separator.gridy = 4;
		panel_2.add(separator, gbc_separator);
		
		JLabel label_22 = new JLabel("SystemPM:");
		label_22.setVerticalAlignment(SwingConstants.BOTTOM);
		label_22.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_label_22 = new GridBagConstraints();
		gbc_label_22.anchor = GridBagConstraints.EAST;
		gbc_label_22.insets = new Insets(0, 0, 5, 5);
		gbc_label_22.gridx = 0;
		gbc_label_22.gridy = 5;
		panel_2.add(label_22, gbc_label_22);
		label_22.setMinimumSize(new Dimension(100, 18));
		label_22.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		
		JLabel label_23 = new JLabel("High Current:");
		GridBagConstraints gbc_label_23 = new GridBagConstraints();
		gbc_label_23.anchor = GridBagConstraints.EAST;
		gbc_label_23.insets = new Insets(0, 0, 5, 5);
		gbc_label_23.gridx = 0;
		gbc_label_23.gridy = 6;
		panel_2.add(label_23, gbc_label_23);
		label_23.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textHighCurrent = new JTextField();
		GridBagConstraints gbc_textHighCurrent = new GridBagConstraints();
		gbc_textHighCurrent.fill = GridBagConstraints.HORIZONTAL;
		gbc_textHighCurrent.insets = new Insets(0, 0, 5, 5);
		gbc_textHighCurrent.gridx = 1;
		gbc_textHighCurrent.gridy = 6;
		panel_2.add(textHighCurrent, gbc_textHighCurrent);
		textHighCurrent.setColumns(10);
		
		btnSystemPMHelp = new JButton("?");
		btnSystemPMHelp.setToolTipText("Click for help creating wakelocks comments");
		GridBagConstraints gbc_btnSystemPMHelp = new GridBagConstraints();
		gbc_btnSystemPMHelp.fill = GridBagConstraints.VERTICAL;
		gbc_btnSystemPMHelp.gridheight = 3;
		gbc_btnSystemPMHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnSystemPMHelp.gridx = 2;
		gbc_btnSystemPMHelp.gridy = 6;
		panel_2.add(btnSystemPMHelp, gbc_btnSystemPMHelp);
		btnSystemPMHelp.setMargin(new Insets(2, 8, 2, 8));
		
		JLabel label_24 = new JLabel("Kernel SystemPM:");
		GridBagConstraints gbc_label_24 = new GridBagConstraints();
		gbc_label_24.anchor = GridBagConstraints.EAST;
		gbc_label_24.insets = new Insets(0, 0, 5, 5);
		gbc_label_24.gridx = 0;
		gbc_label_24.gridy = 7;
		panel_2.add(label_24, gbc_label_24);
		label_24.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textKernelWake = new JTextField();
		GridBagConstraints gbc_textKernelWake = new GridBagConstraints();
		gbc_textKernelWake.fill = GridBagConstraints.HORIZONTAL;
		gbc_textKernelWake.insets = new Insets(0, 0, 5, 5);
		gbc_textKernelWake.gridx = 1;
		gbc_textKernelWake.gridy = 7;
		panel_2.add(textKernelWake, gbc_textKernelWake);
		textKernelWake.setColumns(10);
		
		JLabel label_25 = new JLabel("Java SystemPM:");
		GridBagConstraints gbc_label_25 = new GridBagConstraints();
		gbc_label_25.anchor = GridBagConstraints.EAST;
		gbc_label_25.insets = new Insets(0, 0, 5, 5);
		gbc_label_25.gridx = 0;
		gbc_label_25.gridy = 8;
		panel_2.add(label_25, gbc_label_25);
		label_25.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textJavaWake = new JTextField();
		GridBagConstraints gbc_textJavaWake = new GridBagConstraints();
		gbc_textJavaWake.fill = GridBagConstraints.HORIZONTAL;
		gbc_textJavaWake.insets = new Insets(0, 0, 5, 5);
		gbc_textJavaWake.gridx = 1;
		gbc_textJavaWake.gridy = 8;
		panel_2.add(textJavaWake, gbc_textJavaWake);
		textJavaWake.setColumns(10);
		
		JSeparator separator_1 = new JSeparator();
		separator_1.setPreferredSize(new Dimension(500, 1));
		separator_1.setMinimumSize(new Dimension(3, 2));
		separator_1.setForeground(Color.LIGHT_GRAY);
		separator_1.setBackground(Color.WHITE);
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.gridwidth = 3;
		gbc_separator_1.insets = new Insets(0, 0, 5, 0);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 9;
		panel_2.add(separator_1, gbc_separator_1);
		
		JLabel label_26 = new JLabel("Suspicious:");
		label_26.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_label_26 = new GridBagConstraints();
		gbc_label_26.anchor = GridBagConstraints.EAST;
		gbc_label_26.insets = new Insets(0, 0, 5, 5);
		gbc_label_26.gridx = 0;
		gbc_label_26.gridy = 10;
		panel_2.add(label_26, gbc_label_26);
		label_26.setMinimumSize(new Dimension(100, 18));
		label_26.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		
		JLabel label_27 = new JLabel("Header:");
		GridBagConstraints gbc_label_27 = new GridBagConstraints();
		gbc_label_27.anchor = GridBagConstraints.EAST;
		gbc_label_27.insets = new Insets(0, 0, 5, 5);
		gbc_label_27.gridx = 0;
		gbc_label_27.gridy = 11;
		panel_2.add(label_27, gbc_label_27);
		label_27.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textSuspiciousHeader = new JTextField();
		GridBagConstraints gbc_textSuspiciousHeader = new GridBagConstraints();
		gbc_textSuspiciousHeader.fill = GridBagConstraints.HORIZONTAL;
		gbc_textSuspiciousHeader.insets = new Insets(0, 0, 5, 5);
		gbc_textSuspiciousHeader.gridx = 1;
		gbc_textSuspiciousHeader.gridy = 11;
		panel_2.add(textSuspiciousHeader, gbc_textSuspiciousHeader);
		textSuspiciousHeader.setColumns(10);
		
		btnSuspiciousHelp = new JButton("?");
		btnSuspiciousHelp.setToolTipText("Click for help creating suspicious comments");
		GridBagConstraints gbc_btnSuspiciousHelp = new GridBagConstraints();
		gbc_btnSuspiciousHelp.fill = GridBagConstraints.VERTICAL;
		gbc_btnSuspiciousHelp.gridheight = 2;
		gbc_btnSuspiciousHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnSuspiciousHelp.gridx = 2;
		gbc_btnSuspiciousHelp.gridy = 11;
		panel_2.add(btnSuspiciousHelp, gbc_btnSuspiciousHelp);
		btnSuspiciousHelp.setMargin(new Insets(2, 8, 2, 8));
		
		JLabel label_28 = new JLabel("Comment:");
		GridBagConstraints gbc_label_28 = new GridBagConstraints();
		gbc_label_28.anchor = GridBagConstraints.EAST;
		gbc_label_28.insets = new Insets(0, 0, 5, 5);
		gbc_label_28.gridx = 0;
		gbc_label_28.gridy = 12;
		panel_2.add(label_28, gbc_label_28);
		label_28.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textSuspicious = new JTextField();
		GridBagConstraints gbc_textSuspicious = new GridBagConstraints();
		gbc_textSuspicious.fill = GridBagConstraints.HORIZONTAL;
		gbc_textSuspicious.insets = new Insets(0, 0, 5, 5);
		gbc_textSuspicious.gridx = 1;
		gbc_textSuspicious.gridy = 12;
		panel_2.add(textSuspicious, gbc_textSuspicious);
		textSuspicious.setColumns(10);
		
		JSeparator separator_2 = new JSeparator();
		separator_2.setPreferredSize(new Dimension(500, 1));
		separator_2.setMinimumSize(new Dimension(3, 2));
		separator_2.setForeground(Color.LIGHT_GRAY);
		separator_2.setBackground(Color.WHITE);
		GridBagConstraints gbc_separator_2 = new GridBagConstraints();
		gbc_separator_2.gridwidth = 3;
		gbc_separator_2.insets = new Insets(0, 0, 5, 0);
		gbc_separator_2.gridx = 0;
		gbc_separator_2.gridy = 13;
		panel_2.add(separator_2, gbc_separator_2);
		
		JLabel label_29 = new JLabel("Alarms: ");
		label_29.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_label_29 = new GridBagConstraints();
		gbc_label_29.anchor = GridBagConstraints.EAST;
		gbc_label_29.insets = new Insets(0, 0, 5, 5);
		gbc_label_29.gridx = 0;
		gbc_label_29.gridy = 14;
		panel_2.add(label_29, gbc_label_29);
		label_29.setMinimumSize(new Dimension(125, 18));
		label_29.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		
		JLabel label_30 = new JLabel("Comment:");
		GridBagConstraints gbc_label_30 = new GridBagConstraints();
		gbc_label_30.anchor = GridBagConstraints.EAST;
		gbc_label_30.insets = new Insets(0, 0, 5, 5);
		gbc_label_30.gridx = 0;
		gbc_label_30.gridy = 15;
		panel_2.add(label_30, gbc_label_30);
		label_30.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textAlarms = new JTextField();
		GridBagConstraints gbc_textAlarms = new GridBagConstraints();
		gbc_textAlarms.fill = GridBagConstraints.HORIZONTAL;
		gbc_textAlarms.insets = new Insets(0, 0, 5, 5);
		gbc_textAlarms.gridx = 1;
		gbc_textAlarms.gridy = 15;
		panel_2.add(textAlarms, gbc_textAlarms);
		textAlarms.setColumns(10);
		
		btnAlarmsHelp = new JButton("?");
		btnAlarmsHelp.setToolTipText("Click for help creating alarms comments");
		GridBagConstraints gbc_btnAlarmsHelp = new GridBagConstraints();
		gbc_btnAlarmsHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnAlarmsHelp.gridx = 2;
		gbc_btnAlarmsHelp.gridy = 15;
		panel_2.add(btnAlarmsHelp, gbc_btnAlarmsHelp);
		btnAlarmsHelp.setMargin(new Insets(2, 8, 2, 8));
		
		JSeparator separator_3 = new JSeparator();
		separator_3.setPreferredSize(new Dimension(500, 1));
		separator_3.setMinimumSize(new Dimension(3, 2));
		separator_3.setForeground(Color.LIGHT_GRAY);
		separator_3.setBackground(Color.WHITE);
		GridBagConstraints gbc_separator_3 = new GridBagConstraints();
		gbc_separator_3.insets = new Insets(0, 0, 5, 0);
		gbc_separator_3.gridwidth = 3;
		gbc_separator_3.gridx = 0;
		gbc_separator_3.gridy = 16;
		panel_2.add(separator_3, gbc_separator_3);
		
		JLabel label_31 = new JLabel("Bug2Go: ");
		label_31.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_label_31 = new GridBagConstraints();
		gbc_label_31.anchor = GridBagConstraints.EAST;
		gbc_label_31.insets = new Insets(0, 0, 5, 5);
		gbc_label_31.gridx = 0;
		gbc_label_31.gridy = 17;
		panel_2.add(label_31, gbc_label_31);
		label_31.setMinimumSize(new Dimension(125, 18));
		label_31.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		
		JLabel label_32 = new JLabel("Comment:");
		GridBagConstraints gbc_label_32 = new GridBagConstraints();
		gbc_label_32.anchor = GridBagConstraints.EAST;
		gbc_label_32.insets = new Insets(0, 0, 5, 5);
		gbc_label_32.gridx = 0;
		gbc_label_32.gridy = 18;
		panel_2.add(label_32, gbc_label_32);
		label_32.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textB2g = new JTextField();
		GridBagConstraints gbc_textB2g = new GridBagConstraints();
		gbc_textB2g.fill = GridBagConstraints.HORIZONTAL;
		gbc_textB2g.insets = new Insets(0, 0, 5, 5);
		gbc_textB2g.gridx = 1;
		gbc_textB2g.gridy = 18;
		panel_2.add(textB2g, gbc_textB2g);
		textB2g.setColumns(10);
		
		btnB2gHelp = new JButton("?");
		btnB2gHelp.setToolTipText("Click for help creating bug2go comments");
		GridBagConstraints gbc_btnB2gHelp = new GridBagConstraints();
		gbc_btnB2gHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnB2gHelp.gridx = 2;
		gbc_btnB2gHelp.gridy = 18;
		panel_2.add(btnB2gHelp, gbc_btnB2gHelp);
		btnB2gHelp.setMargin(new Insets(2, 8, 2, 8));
		
		JSeparator separator_4 = new JSeparator();
		separator_4.setPreferredSize(new Dimension(500, 1));
		separator_4.setMinimumSize(new Dimension(3, 2));
		separator_4.setForeground(Color.LIGHT_GRAY);
		separator_4.setBackground(Color.WHITE);
		GridBagConstraints gbc_separator_4 = new GridBagConstraints();
		gbc_separator_4.gridwidth = 3;
		gbc_separator_4.insets = new Insets(0, 0, 5, 0);
		gbc_separator_4.gridx = 0;
		gbc_separator_4.gridy = 19;
		panel_2.add(separator_4, gbc_separator_4);
		
		JLabel label_33 = new JLabel("Tethering: ");
		label_33.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_label_33 = new GridBagConstraints();
		gbc_label_33.anchor = GridBagConstraints.EAST;
		gbc_label_33.insets = new Insets(0, 0, 5, 5);
		gbc_label_33.gridx = 0;
		gbc_label_33.gridy = 20;
		panel_2.add(label_33, gbc_label_33);
		label_33.setMinimumSize(new Dimension(125, 18));
		label_33.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		
		JLabel label_34 = new JLabel("Comment:");
		GridBagConstraints gbc_label_34 = new GridBagConstraints();
		gbc_label_34.anchor = GridBagConstraints.EAST;
		gbc_label_34.insets = new Insets(0, 0, 5, 5);
		gbc_label_34.gridx = 0;
		gbc_label_34.gridy = 21;
		panel_2.add(label_34, gbc_label_34);
		label_34.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textTether = new JTextField();
		GridBagConstraints gbc_textTether = new GridBagConstraints();
		gbc_textTether.fill = GridBagConstraints.HORIZONTAL;
		gbc_textTether.insets = new Insets(0, 0, 5, 5);
		gbc_textTether.gridx = 1;
		gbc_textTether.gridy = 21;
		panel_2.add(textTether, gbc_textTether);
		textTether.setColumns(10);
		
		btnTetherHelp = new JButton("?");
		btnTetherHelp.setToolTipText("Click for help creating tethering comment");
		GridBagConstraints gbc_btnTetherHelp = new GridBagConstraints();
		gbc_btnTetherHelp.insets = new Insets(0, 0, 5, 5);
		gbc_btnTetherHelp.gridx = 2;
		gbc_btnTetherHelp.gridy = 21;
		panel_2.add(btnTetherHelp, gbc_btnTetherHelp);
		btnTetherHelp.setMargin(new Insets(2, 8, 2, 8));
		
		JSeparator separator_5 = new JSeparator();
		separator_5.setPreferredSize(new Dimension(500, 1));
		separator_5.setMinimumSize(new Dimension(3, 2));
		separator_5.setForeground(Color.LIGHT_GRAY);
		separator_5.setBackground(Color.WHITE);
		GridBagConstraints gbc_separator_5 = new GridBagConstraints();
		gbc_separator_5.insets = new Insets(0, 0, 5, 0);
		gbc_separator_5.gridwidth = 3;
		gbc_separator_5.gridx = 0;
		gbc_separator_5.gridy = 22;
		panel_2.add(separator_5, gbc_separator_5);
		
		JLabel label_35 = new JLabel("Diag: ");
		label_35.setForeground(Color.DARK_GRAY);
		GridBagConstraints gbc_label_35 = new GridBagConstraints();
		gbc_label_35.anchor = GridBagConstraints.EAST;
		gbc_label_35.insets = new Insets(0, 0, 5, 5);
		gbc_label_35.gridx = 0;
		gbc_label_35.gridy = 23;
		panel_2.add(label_35, gbc_label_35);
		label_35.setMinimumSize(new Dimension(125, 18));
		label_35.setFont(new Font("Tahoma", Font.BOLD | Font.ITALIC, 13));
		
		JLabel label_36 = new JLabel("Comment:");
		GridBagConstraints gbc_label_36 = new GridBagConstraints();
		gbc_label_36.anchor = GridBagConstraints.EAST;
		gbc_label_36.insets = new Insets(0, 0, 0, 5);
		gbc_label_36.gridx = 0;
		gbc_label_36.gridy = 24;
		panel_2.add(label_36, gbc_label_36);
		label_36.setHorizontalAlignment(SwingConstants.RIGHT);
		
		textDiag = new JTextField();
		GridBagConstraints gbc_textDiag = new GridBagConstraints();
		gbc_textDiag.fill = GridBagConstraints.HORIZONTAL;
		gbc_textDiag.insets = new Insets(0, 0, 0, 5);
		gbc_textDiag.gridx = 1;
		gbc_textDiag.gridy = 24;
		panel_2.add(textDiag, gbc_textDiag);
		textDiag.setColumns(10);
		
		btnDiagHelp = new JButton("?");
		btnDiagHelp.setToolTipText("Click for help creating Diag comments");
		GridBagConstraints gbc_btnDiagHelp = new GridBagConstraints();
		gbc_btnDiagHelp.insets = new Insets(0, 0, 0, 5);
		gbc_btnDiagHelp.gridx = 2;
		gbc_btnDiagHelp.gridy = 24;
		panel_2.add(btnDiagHelp, gbc_btnDiagHelp);
		btnDiagHelp.setMargin(new Insets(2, 8, 2, 8));
		
		lblParserOptions = new JLabel("Parser options:");
		lblParserOptions.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblParserOptions.setAlignmentX(0.5f);
		GridBagConstraints gbc_lblParserOptions = new GridBagConstraints();
		gbc_lblParserOptions.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblParserOptions.insets = new Insets(10, 5, 5, 0);
		gbc_lblParserOptions.gridwidth = 2;
		gbc_lblParserOptions.gridx = 0;
		gbc_lblParserOptions.gridy = 4;
		panel.add(lblParserOptions, gbc_lblParserOptions);
		
		JPanel panel_1 = new JPanel();
		panel_1.setBorder(new LineBorder(new Color(102, 153, 204), 1, true));
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.insets = new Insets(0, 15, 5, 15);
		gbc_panel_1.gridwidth = 2;
		gbc_panel_1.fill = GridBagConstraints.BOTH;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 5;
		panel.add(panel_1, gbc_panel_1);
		GridBagLayout gbl_panel_1 = new GridBagLayout();
		gbl_panel_1.columnWidths = new int[] {175, 450, 0};
		gbl_panel_1.rowHeights = new int[] {0, 0, 0, 0};
		gbl_panel_1.columnWeights = new double[] {0.0, 1.0, Double.MIN_VALUE};
		gbl_panel_1.rowWeights = new double[] {0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel_1.setLayout(gbl_panel_1);
		
		JLabel label_38 = new JLabel("Text editor:");
		label_38.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_label_38 = new GridBagConstraints();
		gbc_label_38.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_38.insets = new Insets(0, 0, 5, 5);
		gbc_label_38.gridx = 0;
		gbc_label_38.gridy = 0;
		panel_1.add(label_38, gbc_label_38);
		label_38.setToolTipText("Select default text editor");
		
		JPanel panel_3 = new JPanel();
		GridBagConstraints gbc_panel_3 = new GridBagConstraints();
		gbc_panel_3.insets = new Insets(0, 0, 5, 0);
		gbc_panel_3.fill = GridBagConstraints.BOTH;
		gbc_panel_3.gridx = 1;
		gbc_panel_3.gridy = 0;
		panel_1.add(panel_3, gbc_panel_3);
		FlowLayout flowLayout = (FlowLayout) panel_3.getLayout();
		flowLayout.setVgap(1);
		flowLayout.setHgap(1);
		flowLayout.setAlignment(FlowLayout.LEFT);
		panel_3.setPreferredSize(new Dimension(10, 25));
		panel_3.setMinimumSize(new Dimension(10, 25));
		panel_3.setMaximumSize(new Dimension(32767, 30));
		panel_3.setBorder(null);
		
		rdbtnTAnalisys = new JRadioButton("TextAnalysis");
		rdbtnTAnalisys.setToolTipText("Use TextAnalysis tool as default text editor");
		rdbtnTAnalisys.setSelected(true);
		panel_3.add(rdbtnTAnalisys);
		
		rdbtnNotepad = new JRadioButton("Notepad++");
		rdbtnNotepad.setToolTipText("Use Notepad++ as default text editor");
		panel_3.add(rdbtnNotepad);
		
		editorSelector.add(rdbtnTAnalisys);
		editorSelector.add(rdbtnNotepad);
		
		JLabel label_39 = new JLabel("Word Wrap:");
		GridBagConstraints gbc_label_39 = new GridBagConstraints();
		gbc_label_39.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_39.insets = new Insets(0, 0, 5, 5);
		gbc_label_39.gridx = 0;
		gbc_label_39.gridy = 1;
		panel_1.add(label_39, gbc_label_39);
		label_39.setToolTipText("Word wrap on/off");
		label_39.setPreferredSize(new Dimension(55, 23));
		label_39.setMinimumSize(new Dimension(55, 23));
		label_39.setHorizontalAlignment(SwingConstants.RIGHT);
		
		chkTextWrap = new JCheckBox("");
		chkTextWrap.setToolTipText("Wrap text to keep it in the view");
		GridBagConstraints gbc_chkTextWrap = new GridBagConstraints();
		gbc_chkTextWrap.insets = new Insets(0, 0, 5, 0);
		gbc_chkTextWrap.anchor = GridBagConstraints.WEST;
		gbc_chkTextWrap.gridx = 1;
		gbc_chkTextWrap.gridy = 1;
		panel_1.add(chkTextWrap, gbc_chkTextWrap);
		
		JLabel label_40 = new JLabel("Tree Breakdown:");
		label_40.setHorizontalAlignment(SwingConstants.RIGHT);
		GridBagConstraints gbc_label_40 = new GridBagConstraints();
		gbc_label_40.fill = GridBagConstraints.HORIZONTAL;
		gbc_label_40.insets = new Insets(0, 0, 0, 5);
		gbc_label_40.gridx = 0;
		gbc_label_40.gridy = 2;
		panel_1.add(label_40, gbc_label_40);
		label_40.setToolTipText("Changes affect just filters/Results tree");
		
		JPanel panel_4 = new JPanel();
		GridBagConstraints gbc_panel_4 = new GridBagConstraints();
		gbc_panel_4.anchor = GridBagConstraints.WEST;
		gbc_panel_4.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_4.gridx = 1;
		gbc_panel_4.gridy = 2;
		panel_1.add(panel_4, gbc_panel_4);
		FlowLayout flowLayout_1 = (FlowLayout) panel_4.getLayout();
		flowLayout_1.setVgap(1);
		flowLayout_1.setHgap(1);
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		panel_4.setPreferredSize(new Dimension(10, 25));
		panel_4.setMinimumSize(new Dimension(10, 25));
		
		rdbtnDouble = new JRadioButton("DoubleClick");
		rdbtnDouble.setToolTipText("Double click to expand parser trees");
		rdbtnDouble.setSelected(true);
		panel_4.add(rdbtnDouble);
		
		rdbtnSingleclick = new JRadioButton("SingleClick");
		rdbtnSingleclick.setToolTipText("Single click to expand parser trees");
		panel_4.add(rdbtnSingleclick);
		breakdownSelector.add(rdbtnDouble);
		breakdownSelector.add(rdbtnSingleclick);
		
		lblMoreOptions = new JLabel("More Options:");
		lblMoreOptions.setFont(new Font("Tahoma", Font.BOLD, 16));
		lblMoreOptions.setAlignmentX(0.5f);
		GridBagConstraints gbc_lblMoreOptions = new GridBagConstraints();
		gbc_lblMoreOptions.anchor = GridBagConstraints.WEST;
		gbc_lblMoreOptions.insets = new Insets(10, 5, 5, 5);
		gbc_lblMoreOptions.gridx = 0;
		gbc_lblMoreOptions.gridy = 6;
		panel.add(lblMoreOptions, gbc_lblMoreOptions);
		
		JPanel panel_6 = new JPanel();
		panel_6.setBorder(new LineBorder(new Color(102, 153, 204), 1, true));
		GridBagConstraints gbc_panel_6 = new GridBagConstraints();
		gbc_panel_6.gridwidth = 2;
		gbc_panel_6.insets = new Insets(0, 15, 0, 15);
		gbc_panel_6.fill = GridBagConstraints.BOTH;
		gbc_panel_6.gridx = 0;
		gbc_panel_6.gridy = 7;
		panel.add(panel_6, gbc_panel_6);
		GridBagLayout gbl_panel_6 = new GridBagLayout();
		gbl_panel_6.columnWidths = new int[] {0, 0};
		gbl_panel_6.rowHeights = new int[] {0, 0};
		gbl_panel_6.columnWeights = new double[] {0.0, Double.MIN_VALUE};
		gbl_panel_6.rowWeights = new double[] {0.0, Double.MIN_VALUE};
		panel_6.setLayout(gbl_panel_6);
		
		JPanel panel_5 = new JPanel();
		GridBagConstraints gbc_panel_5 = new GridBagConstraints();
		gbc_panel_5.gridx = 0;
		gbc_panel_5.gridy = 0;
		panel_6.add(panel_5, gbc_panel_5);
		
		btnManageFilters = new JButton("Manage filters");
		btnManageFilters.setToolTipText("Click to manage parser tab filters");
		panel_5.add(btnManageFilters);
		
		btnMoreOptions = new JButton("More Options");
		btnMoreOptions.setToolTipText("Click to see advanced options");
		btnMoreOptions.setPreferredSize(new Dimension(103, 23));
		panel_5.add(btnMoreOptions);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Add Listeners ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void commentsIconLabelsAddMouseListener(MouseListener ml)
	{
		lblExpandCollapseIco.addMouseListener(ml);
	}
	
	public void textUsernameAddFocusListener(FocusListener fl)
	{
		textUsername.addFocusListener(fl);
	}
	
	public void textPasswordAddFocusListener(FocusListener fl)
	{
		textUsername.addFocusListener(fl);
	}
	
	public void chcbxRememberAddFocusListener(FocusListener fl)
	{
		textUsername.addFocusListener(fl);
	}
	
	public void commentsTextLabelsAddMouseListener(MouseListener ml)
	{
		lblComments.addMouseListener(ml);
	}
	
	public void btnConsumeHelpAddActionListener(ActionListener al)
	{
		btnConsumeHelp.addActionListener(al);
	}
	
	public void btnSystemPMHelpAddActionListener(ActionListener al)
	{
		btnSystemPMHelp.addActionListener(al);
	}
	
	public void btnSuspiciousHelpAddActionListener(ActionListener al)
	{
		btnSuspiciousHelp.addActionListener(al);
	}
	
	public void btnAlarmsHelpAddActionListener(ActionListener al)
	{
		btnAlarmsHelp.addActionListener(al);
	}
	
	public void btnB2gHelpAddActionListener(ActionListener al)
	{
		btnB2gHelp.addActionListener(al);
	}
	
	public void btnTetherHelpAddActionListener(ActionListener al)
	{
		btnTetherHelp.addActionListener(al);
	}
	
	public void btnDiagHelpAddActionListener(ActionListener al)
	{
		btnDiagHelp.addActionListener(al);
	}
	
	public void btnManageFiltersAddActionListener(ActionListener al)
	{
		btnManageFilters.addActionListener(al);
	}
	
	public void btnMoreOptionsAddActionListener(ActionListener al)
	{
		btnMoreOptions.addActionListener(al);
	}
	
	public void rdbtnDoubleAddActionListener(ActionListener al)
	{
		rdbtnDouble.addActionListener(al);
	}
	
	public void rdbtnSingleclickAddActionListener(ActionListener al)
	{
		rdbtnSingleclick.addActionListener(al);
	}
	
	public void chkTextWrapAddItemListener(ItemListener il)
	{
		chkTextWrap.addItemListener(il);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Supportive methods ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void setCommentsPanelVisibility(Boolean b)
	{
		panel_2.setVisible(b);
		lblExpandCollapseIco.setIcon(new ImageIcon("Data\\pics\\expand.png"));
	}
	
	public Boolean getCommentsPanelVisibility()
	{
		return panel_2.isVisible();
	}
	
	public void setCommentsIcon(ImageIcon icon)
	{
		lblExpandCollapseIco.setIcon(icon);
	}
	
	public Icon setCommentsIcon()
	{
		return lblExpandCollapseIco.getIcon();
	}
	
	public void showPopUp(String text)
	{
		warning.setText(text);
		warning.setVisible(true);
	}
	
	public void updateUserdata()
	{
		SharedObjs.setUser(textUsername.getText());
		SharedObjs.setPass(String.copyValueOf(textPassword.getPassword()));
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Getters ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public String getTextUsername()
	{
		return textUsername.getText();
	}
	
	public String getTextPassword()
	{
		return String.copyValueOf(textPassword.getPassword());
	}
	
	public String getTextConsumeOn()
	{
		return textConsumeOn.getText();
	}
	
	public String getTextConsumeOff()
	{
		return textConsumeOff.getText();
	}
	
	public String getTextConsumeFull()
	{
		return textConsumeFull.getText();
	}
	
	public String getTextSuspiciousHeader()
	{
		return textSuspiciousHeader.getText();
	}
	
	public String getTextSuspicious()
	{
		return textSuspicious.getText();
	}
	
	public String getTextKernel()
	{
		return textKernelWake.getText();
	}
	
	public String getTextJava()
	{
		return textJavaWake.getText();
	}
	
	public String getTextAlarms()
	{
		return textAlarms.getText();
	}
	
	public String getTextB2g()
	{
		return textB2g.getText();
	}
	
	public String getTextTether()
	{
		return textTether.getText();
	}
	
	public String getTextDiag()
	{
		return textDiag.getText();
	}
	
	public String getTextHighCurrent()
	{
		return textHighCurrent.getText();
	}
	
	public Boolean isRdBtnNotepadSelected()
	{
		return rdbtnNotepad.isSelected();
	}
	
	public Boolean isRdBtnTAnalisysSelected()
	{
		return rdbtnTAnalisys.isSelected();
	}
	
	public Boolean isRdBtnSingleClickSelected()
	{
		return rdbtnSingleclick.isSelected();
	}
	
	public Boolean isRdBtnDoubleClickSelected()
	{
		return rdbtnDouble.isSelected();
	}
	
	public Boolean isChkTextWrapSelected()
	{
		return chkTextWrap.isSelected();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Setters ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void setTextUsername(String text)
	{
		textUsername.setText(text);
	}
	
	public void setTextPassword(String text)
	{
		textPassword.setText(text);
	}
	
	public void setTextConsumeOn(String text)
	{
		textConsumeOn.setText(text);
	}
	
	public void setTextConsumeOff(String text)
	{
		textConsumeOff.setText(text);
	}
	
	public void setTextConsumeFull(String text)
	{
		textConsumeFull.setText(text);
	}
	
	public void setTextSuspiciousHeader(String text)
	{
		textSuspiciousHeader.setText(text);
	}
	
	public void setTextSuspicious(String text)
	{
		textSuspicious.setText(text);
	}
	
	public void setTextKernel(String text)
	{
		textKernelWake.setText(text);
	}
	
	public void setTextJava(String text)
	{
		textJavaWake.setText(text);
	}
	
	public void setTextAlarms(String text)
	{
		textAlarms.setText(text);
	}
	
	public void setTextB2g(String text)
	{
		textB2g.setText(text);
	}
	
	public void setTextTether(String text)
	{
		textTether.setText(text);
	}
	
	public void setTextDiag(String text)
	{
		textDiag.setText(text);
	}
	
	public void setTextHighCurrent(String text)
	{
		textHighCurrent.setText(text);
	}
	
	public void setRdBtnNotepadSelected(Boolean value)
	{
		rdbtnNotepad.setSelected(value);
	}
	
	public void setRdBtnTAnalisysSelected(Boolean value)
	{
		rdbtnTAnalisys.setSelected(value);
	}
	
	public void setRdBtnSingleClickSelected(Boolean value)
	{
		rdbtnSingleclick.setSelected(value);
	}
	
	public void setRdBtnDoubleClickSelected(Boolean value)
	{
		rdbtnDouble.setSelected(value);
	}
	
	public void setChkTextWrapSelected(Boolean value)
	{
		chkTextWrap.setSelected(value);
	}
	
	public void setServerStatus(boolean status)
	{
		if (status)
		{
			lblServerStatus.setForeground(style.Colors.verdeEscuro);
			lblServerStatus.setText("Connected to SAT DB");
		}
		else
		{
			lblServerStatus.setForeground(style.Colors.vermelhoEscuro);
			lblServerStatus.setText("Not connected to SAT DB");
		}
	}
}
