package views.secondarypanes;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

import org.jdom2.JDOMException;

import style.Icons;
import core.Logger;
import core.SharedObjs;
import core.XmlMngr;


@SuppressWarnings("serial")
public class AdvancedOptionsPane extends JFrame
{
	private JPanel                  contentPane;
	private JLabel                  lblTitle;
	private JPanel                  panel;
	private JLabel                  lblBatCap;
	private JLabel                  lblProduct2;
	private JComboBox<String>       cbxBatCap;
	private JTextField              textBatCap;
	private JSeparator              separator_1;
	private JPanel                  panel_1;
	private JButton                 btnOk;
	private HashMap<String, String> uidsMap;
	private HashMap<String, String> bat_capMap;
	private JButton                 btnCancel;
	private JButton                 addBatCap;
	private JLabel                  label;
	private JComboBox<String>       cbxUids;
	private JTextField              textUids;
	private JButton                 delBatCap;
	private JButton                 addUid;
	private JButton                 delUid;
	private AdvancedOptionsPane     thisPane;
	
	/**
	 * Create the frame.
	 */
	public AdvancedOptionsPane()
	{
		setTitle("Advanced options");
		setMinimumSize(new Dimension(400, 200));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		setBounds(100, 100, 444, 305);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		setLocationRelativeTo(SharedObjs.satFrame);
		
		thisPane = this;
		
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {0, 0};
		gbl_contentPane.rowHeights = new int[] {0, 0, 0, 0};
		gbl_contentPane.columnWeights = new double[] {1.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[] {0.0, 0.0, 1.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		lblTitle = new JLabel("Advanced Options:");
		lblTitle.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitle.setFont(new Font("Tahoma", Font.BOLD, 24));
		GridBagConstraints gbc_lblTitle = new GridBagConstraints();
		gbc_lblTitle.insets = new Insets(0, 0, 10, 0);
		gbc_lblTitle.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblTitle.gridx = 0;
		gbc_lblTitle.gridy = 0;
		contentPane.add(lblTitle, gbc_lblTitle);
		
		panel = new JPanel();
		GridBagConstraints gbc_panel = new GridBagConstraints();
		gbc_panel.insets = new Insets(5, 5, 5, 10);
		gbc_panel.fill = GridBagConstraints.BOTH;
		gbc_panel.gridx = 0;
		gbc_panel.gridy = 1;
		contentPane.add(panel, gbc_panel);
		
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[] {0, 150, 0, 0, 0, 0};
		gbl_panel.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
		gbl_panel.columnWeights = new double[] {0.0, 1.0, 1.0, 0.0, 0.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		lblBatCap = new JLabel("Configure battery capacities");
		lblBatCap.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_lblBatCap = new GridBagConstraints();
		gbc_lblBatCap.insets = new Insets(0, 0, 5, 5);
		gbc_lblBatCap.anchor = GridBagConstraints.WEST;
		gbc_lblBatCap.gridwidth = 3;
		gbc_lblBatCap.gridx = 0;
		gbc_lblBatCap.gridy = 0;
		panel.add(lblBatCap, gbc_lblBatCap);
		
		lblProduct2 = new JLabel("Product:");
		GridBagConstraints gbc_lblProduct2 = new GridBagConstraints();
		gbc_lblProduct2.anchor = GridBagConstraints.EAST;
		gbc_lblProduct2.insets = new Insets(0, 5, 5, 5);
		gbc_lblProduct2.gridx = 0;
		gbc_lblProduct2.gridy = 1;
		panel.add(lblProduct2, gbc_lblProduct2);
		
		cbxBatCap = new JComboBox<String>();
		cbxBatCap.setMinimumSize(new Dimension(150, 20));
		cbxBatCap.setPreferredSize(new Dimension(150, 20));
		GridBagConstraints gbc_cbxBatCap = new GridBagConstraints();
		gbc_cbxBatCap.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbxBatCap.insets = new Insets(0, 0, 5, 5);
		gbc_cbxBatCap.gridx = 1;
		gbc_cbxBatCap.gridy = 1;
		panel.add(cbxBatCap, gbc_cbxBatCap);
		
		textBatCap = new JTextField();
		textBatCap.setPreferredSize(new Dimension(110, 20));
		textBatCap.setMinimumSize(new Dimension(110, 20));
		GridBagConstraints gbc_textBatCap = new GridBagConstraints();
		gbc_textBatCap.fill = GridBagConstraints.HORIZONTAL;
		gbc_textBatCap.insets = new Insets(0, 0, 5, 5);
		gbc_textBatCap.gridx = 2;
		gbc_textBatCap.gridy = 1;
		panel.add(textBatCap, gbc_textBatCap);
		
		addBatCap = new JButton("");
		addBatCap.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				String pName = JOptionPane.showInputDialog(thisPane, "Type the product name");
				String bCap = JOptionPane.showInputDialog(thisPane, "Type the battery capacity");
				
				if (pName != null && bCap != null)
				{
					bat_capMap.put(pName, bCap);
					setData();
					getData();
				}
			}
		});
		addBatCap.setIcon(Icons.add);
		addBatCap.setPreferredSize(new Dimension(30, 25));
		GridBagConstraints gbc_addBatCap = new GridBagConstraints();
		gbc_addBatCap.insets = new Insets(0, 0, 5, 5);
		gbc_addBatCap.gridx = 3;
		gbc_addBatCap.gridy = 1;
		panel.add(addBatCap, gbc_addBatCap);
		
		delBatCap = new JButton("");
		delBatCap.setPreferredSize(new Dimension(30, 25));
		delBatCap.setIcon(Icons.delete);
		GridBagConstraints gbc_delBatCap = new GridBagConstraints();
		gbc_delBatCap.insets = new Insets(0, 0, 5, 0);
		gbc_delBatCap.gridx = 4;
		gbc_delBatCap.gridy = 1;
		panel.add(delBatCap, gbc_delBatCap);
		
		separator_1 = new JSeparator();
		separator_1.setPreferredSize(new Dimension(2, 2));
		separator_1.setForeground(Color.GRAY);
		separator_1.setBackground(Color.LIGHT_GRAY);
		GridBagConstraints gbc_separator_1 = new GridBagConstraints();
		gbc_separator_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_separator_1.gridwidth = 4;
		gbc_separator_1.insets = new Insets(10, 0, 10, 5);
		gbc_separator_1.gridx = 0;
		gbc_separator_1.gridy = 2;
		panel.add(separator_1, gbc_separator_1);
		
		label = new JLabel("Manage UIDs");
		label.setFont(new Font("Tahoma", Font.BOLD, 14));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.anchor = GridBagConstraints.WEST;
		gbc_label.gridwidth = 4;
		gbc_label.insets = new Insets(0, 0, 5, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 3;
		panel.add(label, gbc_label);
		
		cbxUids = new JComboBox<String>();
		cbxUids.setPreferredSize(new Dimension(150, 20));
		cbxUids.setMinimumSize(new Dimension(150, 20));
		GridBagConstraints gbc_cbxUids = new GridBagConstraints();
		gbc_cbxUids.insets = new Insets(0, 0, 5, 5);
		gbc_cbxUids.fill = GridBagConstraints.HORIZONTAL;
		gbc_cbxUids.gridx = 1;
		gbc_cbxUids.gridy = 4;
		panel.add(cbxUids, gbc_cbxUids);
		
		textUids = new JTextField();
		textUids.setText((String) null);
		textUids.setPreferredSize(new Dimension(110, 20));
		textUids.setMinimumSize(new Dimension(110, 20));
		GridBagConstraints gbc_textUids = new GridBagConstraints();
		gbc_textUids.insets = new Insets(0, 0, 5, 5);
		gbc_textUids.fill = GridBagConstraints.HORIZONTAL;
		gbc_textUids.gridx = 2;
		gbc_textUids.gridy = 4;
		panel.add(textUids, gbc_textUids);
		
		addUid = new JButton("");
		addUid.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (SharedObjs.dbStatus)
				{
					String uid = JOptionPane.showInputDialog(thisPane, "Type the UID");
					String process = JOptionPane.showInputDialog(thisPane, "Type the package/service");
					if (uid != null && process != null)
					{
						SharedObjs.satDB.addUidProcess(uid, process);
						updateUidsData();
					}
				}
				else
				{
					JOptionPane.showMessageDialog(thisPane, "There is no connection to SAT DB.\nYou can't edit this data while DB offline");
				}
			}
		});
		addUid.setPreferredSize(new Dimension(30, 25));
		addUid.setIcon(Icons.add);
		GridBagConstraints gbc_addUid = new GridBagConstraints();
		gbc_addUid.insets = new Insets(0, 0, 5, 5);
		gbc_addUid.gridx = 3;
		gbc_addUid.gridy = 4;
		panel.add(addUid, gbc_addUid);
		
		delUid = new JButton("");
		delUid.setPreferredSize(new Dimension(30, 25));
		delUid.setIcon(Icons.delete);
		delUid.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (SharedObjs.dbStatus)
				{
					SharedObjs.satDB.deleteUid(cbxUids.getSelectedItem().toString());
					updateUidsData();
				}
				else
				{
					JOptionPane.showMessageDialog(thisPane, "There is no connection to SAT DB.\nYou can't edit this data while DB offline");
				}
			}
		});
		GridBagConstraints gbc_delUid = new GridBagConstraints();
		gbc_delUid.insets = new Insets(0, 0, 5, 0);
		gbc_delUid.gridx = 4;
		gbc_delUid.gridy = 4;
		panel.add(delUid, gbc_delUid);
		
		panel_1 = new JPanel();
		GridBagConstraints gbc_panel_1 = new GridBagConstraints();
		gbc_panel_1.anchor = GridBagConstraints.SOUTH;
		gbc_panel_1.insets = new Insets(0, 10, 0, 10);
		gbc_panel_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_panel_1.gridx = 0;
		gbc_panel_1.gridy = 2;
		contentPane.add(panel_1, gbc_panel_1);
		
		panel_1.setLayout(new FlowLayout(FlowLayout.CENTER, 1, 1));
		btnOk = new JButton("Save and Exit");
		panel_1.add(btnOk);
		
		btnCancel = new JButton("Cancel");
		btnCancel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				dispose();
			}
		});
		panel_1.add(btnCancel);
		
		// Initialize variables
		uidsMap = new HashMap<String, String>();
		uidsMap.clear();
		bat_capMap = new HashMap<String, String>();
		bat_capMap.clear();
		
		// Load data
		getData();
		
		cbxBatCap.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				textBatCap.setText(bat_capMap.get(e.getItem()));
			}
		});
		
		textBatCap.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent arg0)
			{
				bat_capMap.put((String) cbxBatCap.getSelectedItem(), textBatCap.getText());
			}
			
			@Override
			public void focusGained(FocusEvent arg0)
			{
			}
		});
		
		cbxUids.addItemListener(new ItemListener()
		{
			public void itemStateChanged(ItemEvent e)
			{
				textUids.setText(uidsMap.get(e.getItem()));
			}
		});
		
		textUids.addFocusListener(new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent arg0)
			{
				uidsMap.put((String) cbxUids.getSelectedItem(), textUids.getText());
			}
			
			@Override
			public void focusGained(FocusEvent arg0)
			{
			}
		});
		
		btnOk.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				try
				{
					setData();
					updateUidsData();
					dispose();
				}
				catch (Throwable e1)
				{
					e1.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * @throws JDOMException
	 * @throws IOException
	 */
	public void getData()
	{
		uidsMap.clear();
		cbxUids.removeAllItems();
		
		if (SharedObjs.dbStatus)
		{
			uidsMap.putAll(SharedObjs.satDB.getAllUids());
			
			for (String value : uidsMap.keySet())
			{
				cbxUids.addItem(value);
			}
		}
		else
		{
			uidsMap.putAll(XmlMngr.getAllUids());
			
			for (String value : uidsMap.keySet())
			{
				cbxUids.addItem(value);
			}
		}
		
		bat_capMap.clear();
		cbxBatCap.removeAllItems();
		bat_capMap.putAll(XmlMngr.getBatteryCapacityItems());
		
		for (String value : bat_capMap.keySet())
		{
			cbxBatCap.addItem(value);
		}
		
		textBatCap.setText(bat_capMap.get((String) cbxBatCap.getSelectedItem()));
		textUids.setText(uidsMap.get((String) cbxUids.getSelectedItem()));
		
		Logger.log(Logger.TAG_OPTIONS, "Advanced options loaded");
	}
	
	/**
	 * @throws FileNotFoundException
	 * @throws IOException
	 * @throws JDOMException
	 */
	public void setData()
	{
		XmlMngr.setBatteryCapacityItems(bat_capMap);
		
		Logger.log(Logger.TAG_OPTIONS, "Advanced Options Saved");
	}
	
	public void updateUidsData()
	{
		uidsMap.clear();
		cbxUids.removeAllItems();
		
		uidsMap.putAll(SharedObjs.satDB.getAllUids());
		System.out.println(uidsMap);
		
		for (String value : uidsMap.keySet())
		{
			cbxUids.addItem(value);
		}
		
		textUids.setText(uidsMap.get((String) cbxUids.getSelectedItem()));
		
		SharedObjs.isUidsDBModified = true;
	}
	
	// Getters and Setters
	public String getDupValue(String productName)
	{
		return uidsMap.get(productName);
	}
	
	public String getBatCapValue(String productName)
	{
		return bat_capMap.get(productName);
	}
	
	public void addNewBatCapValue(String productName, String batCap)
	{
		bat_capMap.put(productName, batCap);
		XmlMngr.setBatteryCapacityItems(bat_capMap);
		Logger.log(Logger.TAG_OPTIONS, "Advanced Options Updated");
	}
}
