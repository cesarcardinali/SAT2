package views.secondarypanes;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

import style.FiltersTableCheckboxCellRenderer;
import style.FiltersTableIntCellRenderer;
import style.FiltersTableStringCellRenderer;
import style.Icons;
import style.MyFiltersTableModel;
import style.SharedFiltersTableModel;
import core.Logger;
import core.SharedObjs;
import core.XmlMngr;
import customobjects.CustomFilterItem;
import customobjects.CustomFiltersList;


@SuppressWarnings("serial")
public class CustomFiltersPane extends JDialog
{
	private int                     lastTab;
	private JTabbedPane             tabbedPane;
	private JScrollPane             scrollPaneTable1;
	private JScrollPane             scrollPaneTable2;
	private JPanel                  myFiltersPane;
	private JPanel                  sharedPane;
	private JButton                 btnDone;
	private JButton                 btnAdd;
	private JButton                 btnDel;
	private JLabel                  label;
	private JTable                  myFiltersTable;
	private JTable                  sharedFiltersTable;
	private MyFiltersTableModel     myFiltersTableModel;
	private SharedFiltersTableModel sharedTableModel;
	private CustomFiltersList       changesStack;
	private CustomFiltersList       sharedTabChangesStack;
	
	// Create the dialog.
	public CustomFiltersPane()
	{
		setModalityType(ModalityType.APPLICATION_MODAL);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setVisible(false);
		setTitle("Filters Manager");
		setBounds(100, 100, 1172, 441);
		
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] {0, 0};
		gridBagLayout.rowHeights = new int[] {0, 0};
		gridBagLayout.columnWeights = new double[] {1.0, Double.MIN_VALUE};
		gridBagLayout.rowWeights = new double[] {1.0, 0.0};
		getContentPane().setLayout(gridBagLayout);
		
		tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		GridBagConstraints gbc_tabbedPane = new GridBagConstraints();
		gbc_tabbedPane.insets = new Insets(5, 5, 5, 5);
		gbc_tabbedPane.fill = GridBagConstraints.BOTH;
		gbc_tabbedPane.gridx = 0;
		gbc_tabbedPane.gridy = 0;
		getContentPane().add(tabbedPane, gbc_tabbedPane);
		
		myFiltersPane = new JPanel();
		tabbedPane.addTab("My filters", null, myFiltersPane, null);
		GridBagLayout gbl_myFiltersPane = new GridBagLayout();
		gbl_myFiltersPane.columnWidths = new int[] {0, 0};
		gbl_myFiltersPane.rowHeights = new int[] {0, 0, 0, 0, 0, 0, 0};
		gbl_myFiltersPane.columnWeights = new double[] {1.0, 0.0};
		gbl_myFiltersPane.rowWeights = new double[] {0.0, 0.0, 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		myFiltersPane.setLayout(gbl_myFiltersPane);
		
		JLabel lblFiltersList = new JLabel("My Filters List:");
		GridBagConstraints gbc_lblFiltersList = new GridBagConstraints();
		gbc_lblFiltersList.fill = GridBagConstraints.HORIZONTAL;
		gbc_lblFiltersList.insets = new Insets(2, 0, 5, 0);
		gbc_lblFiltersList.gridx = 0;
		gbc_lblFiltersList.gridy = 0;
		myFiltersPane.add(lblFiltersList, gbc_lblFiltersList);
		lblFiltersList.setHorizontalAlignment(SwingConstants.CENTER);
		lblFiltersList.setFont(new Font("Tahoma", Font.BOLD, 11));
		lblFiltersList.setPreferredSize(new Dimension(70, 23));
		
		sharedPane = new JPanel();
		tabbedPane.addTab("Shared filters", null, sharedPane, null);
		GridBagLayout gbl_sharedPane = new GridBagLayout();
		gbl_sharedPane.columnWidths = new int[] {0, 0, 0};
		gbl_sharedPane.rowHeights = new int[] {0, 0, 0, 0, 0};
		gbl_sharedPane.columnWeights = new double[] {1.0, 0.0, Double.MIN_VALUE};
		gbl_sharedPane.rowWeights = new double[] {0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE};
		sharedPane.setLayout(gbl_sharedPane);
		
		label = new JLabel("My Filters List:");
		label.setPreferredSize(new Dimension(70, 23));
		label.setHorizontalAlignment(SwingConstants.CENTER);
		label.setFont(new Font("Tahoma", Font.BOLD, 11));
		GridBagConstraints gbc_label = new GridBagConstraints();
		gbc_label.fill = GridBagConstraints.HORIZONTAL;
		gbc_label.insets = new Insets(2, 0, 0, 5);
		gbc_label.gridx = 0;
		gbc_label.gridy = 0;
		sharedPane.add(label, gbc_label);
		
		scrollPaneTable2 = new JScrollPane();
		scrollPaneTable2.getViewport().setBackground(Color.white);
		GridBagConstraints gbc_scrollPaneTable2 = new GridBagConstraints();
		gbc_scrollPaneTable2.gridheight = 3;
		gbc_scrollPaneTable2.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPaneTable2.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTable2.gridx = 0;
		gbc_scrollPaneTable2.gridy = 1;
		sharedPane.add(scrollPaneTable2, gbc_scrollPaneTable2);
		
		// Shared Filters Table Model
		sharedTableModel = new SharedFiltersTableModel();
		
		// Shared Filters Table Definition
		sharedFiltersTable = new JTable(sharedTableModel);
		sharedFiltersTable.setDefaultRenderer(String.class, new FiltersTableStringCellRenderer());
		sharedFiltersTable.setDefaultRenderer(Boolean.class, new FiltersTableCheckboxCellRenderer());
		sharedFiltersTable.setDefaultRenderer(Integer.class, new FiltersTableIntCellRenderer());
		sharedFiltersTable.getColumnModel().setColumnMargin(1);
		sharedFiltersTable.getColumnModel().getColumn(0).setMinWidth(20);
		sharedFiltersTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		sharedFiltersTable.getColumnModel().getColumn(1).setMinWidth(60);
		sharedFiltersTable.getColumnModel().getColumn(1).setPreferredWidth(60);
		sharedFiltersTable.getColumnModel().getColumn(2).setMinWidth(100);
		sharedFiltersTable.getColumnModel().getColumn(2).setPreferredWidth(150);
		sharedFiltersTable.getColumnModel().getColumn(3).setMinWidth(120);
		sharedFiltersTable.getColumnModel().getColumn(3).setPreferredWidth(200);
		sharedFiltersTable.getColumnModel().getColumn(4).setMinWidth(200);
		sharedFiltersTable.getColumnModel().getColumn(4).setPreferredWidth(300);
		sharedFiltersTable.getColumnModel().getColumn(5).setMinWidth(25);
		sharedFiltersTable.getColumnModel().getColumn(5).setPreferredWidth(40);
		sharedFiltersTable.getColumnModel().getColumn(6).setMinWidth(25);
		sharedFiltersTable.getColumnModel().getColumn(6).setPreferredWidth(40);
		sharedFiltersTable.getColumnModel().getColumn(7).setMinWidth(25);
		sharedFiltersTable.getColumnModel().getColumn(7).setPreferredWidth(40);
		sharedFiltersTable.getColumnModel().getColumn(8).setMinWidth(25);
		sharedFiltersTable.getColumnModel().getColumn(8).setPreferredWidth(40);
		sharedFiltersTable.getColumnModel().getColumn(9).setMinWidth(25);
		sharedFiltersTable.getColumnModel().getColumn(9).setPreferredWidth(40);
		sharedFiltersTable.getColumnModel().getColumn(10).setMinWidth(25);
		sharedFiltersTable.getColumnModel().getColumn(10).setPreferredWidth(40);
		sharedFiltersTable.getColumnModel().getColumn(11).setMinWidth(25);
		sharedFiltersTable.getColumnModel().getColumn(11).setPreferredWidth(40);
		sharedFiltersTable.getColumnModel().getColumn(12).setMinWidth(60);
		sharedFiltersTable.getColumnModel().getColumn(12).setPreferredWidth(65);
		sharedFiltersTable.getColumnModel().getColumn(13).setMinWidth(2);
		sharedFiltersTable.getColumnModel().getColumn(13).setMaxWidth(2);
		sharedFiltersTable.getColumnModel().getColumn(13).setPreferredWidth(2);
		sharedFiltersTable.setFillsViewportHeight(false);
		sharedFiltersTable.setSurrendersFocusOnKeystroke(true);
		sharedFiltersTable.setColumnSelectionAllowed(true);
		sharedFiltersTable.setCellSelectionEnabled(true);
		sharedFiltersTable.getTableHeader().setReorderingAllowed(true);
		sharedFiltersTable.setAutoCreateRowSorter(true);
		sharedFiltersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPaneTable2.setViewportView(sharedFiltersTable);
		
		scrollPaneTable1 = new JScrollPane();
		scrollPaneTable1.getViewport().setBackground(Color.white);
		GridBagConstraints gbc_scrollPaneTable1 = new GridBagConstraints();
		gbc_scrollPaneTable1.gridheight = 5;
		gbc_scrollPaneTable1.insets = new Insets(0, 0, 0, 5);
		gbc_scrollPaneTable1.fill = GridBagConstraints.BOTH;
		gbc_scrollPaneTable1.gridx = 0;
		gbc_scrollPaneTable1.gridy = 1;
		myFiltersPane.add(scrollPaneTable1, gbc_scrollPaneTable1);
		
		// My Filters Table Model
		myFiltersTableModel = new MyFiltersTableModel();
		
		// My Filters Table Definition
		myFiltersTable = new JTable(myFiltersTableModel);
		myFiltersTable.setDefaultRenderer(String.class, new FiltersTableStringCellRenderer());
		myFiltersTable.setDefaultRenderer(Boolean.class, new FiltersTableCheckboxCellRenderer());
		myFiltersTable.setDefaultRenderer(Integer.class, new FiltersTableIntCellRenderer());
		myFiltersTable.getColumnModel().setColumnMargin(1);
		myFiltersTable.getColumnModel().getColumn(0).setMinWidth(20);
		myFiltersTable.getColumnModel().getColumn(0).setPreferredWidth(20);
		myFiltersTable.getColumnModel().getColumn(1).setMinWidth(100);
		myFiltersTable.getColumnModel().getColumn(1).setPreferredWidth(120);
		myFiltersTable.getColumnModel().getColumn(2).setMinWidth(120);
		myFiltersTable.getColumnModel().getColumn(2).setMinWidth(120);
		myFiltersTable.getColumnModel().getColumn(2).setPreferredWidth(250);
		myFiltersTable.getColumnModel().getColumn(3).setMinWidth(120);
		myFiltersTable.getColumnModel().getColumn(3).setPreferredWidth(290);
		myFiltersTable.getColumnModel().getColumn(4).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(4).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(5).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(5).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(6).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(6).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(7).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(7).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(8).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(8).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(9).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(9).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(10).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(10).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(11).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(11).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(12).setMinWidth(25);
		myFiltersTable.getColumnModel().getColumn(12).setPreferredWidth(40);
		myFiltersTable.getColumnModel().getColumn(13).setMinWidth(60);
		myFiltersTable.getColumnModel().getColumn(13).setPreferredWidth(70);
		myFiltersTable.getColumnModel().getColumn(14).setMinWidth(2);
		myFiltersTable.getColumnModel().getColumn(14).setMaxWidth(2);
		myFiltersTable.getColumnModel().getColumn(14).setPreferredWidth(2);
		myFiltersTable.setFillsViewportHeight(false);
		myFiltersTable.setSurrendersFocusOnKeystroke(true);
		myFiltersTable.setColumnSelectionAllowed(true);
		myFiltersTable.setCellSelectionEnabled(true);
		myFiltersTable.getTableHeader().setReorderingAllowed(true);
		myFiltersTable.setAutoCreateRowSorter(true);
		myFiltersTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
		scrollPaneTable1.setViewportView(myFiltersTable);
		
		btnAdd = new JButton("");
		btnAdd.setIcon(Icons.add);
		btnAdd.setPreferredSize(new Dimension(25, 25));
		btnAdd.setMinimumSize(new Dimension(25, 25));
		btnAdd.setMargin(new Insets(2, 2, 2, 2));
		GridBagConstraints gbc_btnAdd = new GridBagConstraints();
		gbc_btnAdd.insets = new Insets(0, 0, 5, 0);
		gbc_btnAdd.gridx = 1;
		gbc_btnAdd.gridy = 2;
		myFiltersPane.add(btnAdd, gbc_btnAdd);
		
		btnDel = new JButton("");
		btnDel.setIcon(Icons.delete);
		btnDel.setPreferredSize(new Dimension(25, 25));
		btnDel.setMinimumSize(new Dimension(25, 25));
		btnDel.setMargin(new Insets(2, 2, 2, 2));
		GridBagConstraints gbc_btnDel = new GridBagConstraints();
		gbc_btnDel.insets = new Insets(0, 0, 5, 0);
		gbc_btnDel.gridx = 1;
		gbc_btnDel.gridy = 3;
		myFiltersPane.add(btnDel, gbc_btnDel);
		
		btnDone = new JButton("Save and Exit");
		btnDone.setMaximumSize(new Dimension(200, 23));
		btnDone.setPreferredSize(new Dimension(140, 23));
		btnDone.setMinimumSize(new Dimension(120, 23));
		GridBagConstraints gbc_btnDone = new GridBagConstraints();
		gbc_btnDone.insets = new Insets(5, 5, 5, 5);
		gbc_btnDone.gridx = 0;
		gbc_btnDone.gridy = 1;
		getContentPane().add(btnDone, gbc_btnDone);
		
		changesStack = new CustomFiltersList();
		sharedTabChangesStack = new CustomFiltersList();
		
		lastTab = -1;
		tabbedPane.addChangeListener(new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent arg0)
			{
				if (arg0.getSource() != null && arg0.getSource().getClass() == JTabbedPane.class)
				{
					JTabbedPane tabPane = (JTabbedPane) arg0.getSource();
					if (tabPane.getSelectedIndex() != lastTab)
					{
						System.out.println("Pane2 Mudou tab de " + lastTab + " para " + tabPane.getSelectedIndex());
						
						switch (lastTab)
						{
							case 0:
								if (myFiltersTable.isEditing())
									myFiltersTable.getCellEditor().stopCellEditing();
								SharedObjs.getUserFiltersList().clear();
								SharedObjs.getUserFiltersList().addAll(myFiltersTableModel.getFilterElements());
								
								/*
								 * XmlMngr.removeAllMyFilters(); XmlMngr.addMyFilters(SharedObjs.getUserFiltersList());
								 * 
								 * for (CustomFilterItem filter : SharedObjs.getUserFiltersList()) { if
								 * (SharedObjs.satDB.updateFilter(filter) <= 0) { SharedObjs.satDB.insertFilter(filter); } }
								 */
								break;
							case 1:
								if (sharedFiltersTable.isEditing())
									sharedFiltersTable.getCellEditor().stopCellEditing();
								SharedObjs.getSharedFiltersList().clear();
								SharedObjs.getSharedFiltersList().addAll(sharedTableModel.getFilterElements());
								
								/*
								 * XmlMngr.removeAllSharedFilters(); XmlMngr.addSharedFilters(SharedObjs.getSharedFiltersList());
								 * 
								 * for (CustomFilterItem filter : sharedTableModel.getFilterElements()) { System.out.println("-- " +
								 * filter.getOwner()); if (filter.getOwner().equals("")) {
								 * System.out.println(SharedObjs.satDB.updateFilter(filter)); } }
								 */
								break;
						}
						
						switch (tabPane.getSelectedIndex())
						{
							case 0:
								if (myFiltersTable != null)
								{
								}
								break;
							case 1:
								if (sharedFiltersTable != null)
								{
								}
								break;
						}
						
						lastTab = tabPane.getSelectedIndex();
					}
				}
			}
		});
		
		btnDone.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				saveFilters();
				setVisible(false);
				tabbedPane.setSelectedIndex(0);
			}
		});
		
		btnAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (myFiltersTable.isEditing())
					myFiltersTable.getCellEditor().stopCellEditing();
				
				if (myFiltersTableModel.hasEmptyRow())
				{
					myFiltersTable.setColumnSelectionInterval(1, 1);
					int nRows = myFiltersTableModel.getRowCount() - 1;
					myFiltersTable.setRowSelectionInterval(myFiltersTable.convertRowIndexToView(nRows), myFiltersTable.convertRowIndexToView(nRows));
					
					JOptionPane.showMessageDialog(SharedObjs.getCustomFiltersPane(), "The filter tagName can not be empty");
					
					myFiltersTable.editCellAt(myFiltersTable.convertRowIndexToView(nRows), 1);
					myFiltersTable.transferFocus();
				}
				else
				{
					myFiltersTableModel.addEmptyRow();
					myFiltersTable.setColumnSelectionInterval(1, 1);
					int nRows = myFiltersTableModel.getRowCount() - 1;
					myFiltersTable.setRowSelectionInterval(myFiltersTable.convertRowIndexToView(nRows), myFiltersTable.convertRowIndexToView(nRows));
					myFiltersTable.editCellAt(myFiltersTable.convertRowIndexToView(nRows), 1);
					myFiltersTable.transferFocus();
					
					myFiltersTableModel.getElementAt(nRows).setModified("Insert");
					myFiltersTableModel.getElementAt(nRows).setLastUpdate("" + new Timestamp(new java.util.Date().getTime()));
					
					changesStack.add(myFiltersTableModel.getElementAt(nRows));
				}
			}
		});
		
		btnDel.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				if (myFiltersTable.getSelectedRow() >= 0)
				{
					int ans = JOptionPane.showOptionDialog(SharedObjs.getCustomFiltersPane(), "Do you really want to remove this filter?", "Deleting filter",
					                                       JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, new String[] {"Yes", "No"}, "Yes");
					if (ans == 0)
					{
						CustomFilterItem filter = myFiltersTableModel.getElementAt(myFiltersTable.getSelectedRow());
						if (filter.getModified().equals("Insert"))
						{
							changesStack.remove(changesStack.indexOf(filter.getLastUpdate()));
						}
						else
						{
							filter.setModified("Delete");
							changesStack.add(filter);
						}
						myFiltersTableModel.removeRow(myFiltersTable.getSelectedRow());
					}
				}
			}
		});
		
		myFiltersTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 1)
				{
					JTable target = (JTable) e.getSource();
					int column = target.getSelectedColumn();
					
					if (column == 0)
					{
						myFiltersTable.setColumnSelectionInterval(myFiltersTable.getColumnCount() - 2, column);
					}
					/*
					 * table.changeSelection(row, column, false, false); table.requestFocus();
					 */
				}
			}
		});
		
		sharedFiltersTable.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent e)
			{
				if (e.getClickCount() == 1)
				{
					JTable target = (JTable) e.getSource();
					int column = target.getSelectedColumn();
					
					if (column == 0)
					{
						sharedFiltersTable.setColumnSelectionInterval(sharedFiltersTable.getColumnCount() - 1, column);
					}
				}
			}
		});
		
		myFiltersTableModel.addTableModelListener(new TableModelListener()
		{
			@Override
			public void tableChanged(TableModelEvent evt)
			{
				myFiltersTableTracer(evt);
			}
		});
		
		sharedTableModel.addTableModelListener(new TableModelListener()
		{
			@Override
			public void tableChanged(TableModelEvent evt)
			{
				sharedFiltersTableTracer(evt);
			}
		});
		
	}
	
	public void open()
	{
		reloadFiltersTable();
		tabbedPane.setSelectedIndex(0);
		lastTab = -1;
		changesStack.clear();
		sharedTabChangesStack.clear();
		setLocationRelativeTo(SharedObjs.satFrame);
		setVisible(true);
	}
	
	public void reloadFiltersTable()
	{
		// If connected to DB, update the shared filters list
		if (SharedObjs.satDB != null)
		{
			SharedObjs.getSharedFiltersList().clear();
			SharedObjs.getSharedFiltersList().addAll(SharedObjs.satDB.sharedFilters());
			XmlMngr.removeAllSharedFilters();
			XmlMngr.addSharedFilters(SharedObjs.getSharedFiltersList());
			
			SharedObjs.getUserFiltersList().clear();
			SharedObjs.getUserFiltersList().addAll(SharedObjs.satDB.myFilters());
			XmlMngr.removeAllMyFilters();
			XmlMngr.addMyFilters(SharedObjs.getUserFiltersList());
			
			SharedObjs.getActiveFiltersList().clear();
			SharedObjs.getActiveFiltersList().addAll(SharedObjs.satDB.activeFilters());
			XmlMngr.removeAllActiveFilters();
			XmlMngr.addActiveFilters(SharedObjs.getActiveFiltersList());
			
			for (CustomFilterItem filter : SharedObjs.getActiveFiltersList())
			{
				System.out.println("Active filter: " + filter.getName());
				
				if (SharedObjs.getUserFiltersList().indexOf(filter.getId()) >= 0)
				{
					SharedObjs.getUserFiltersList().get(SharedObjs.getUserFiltersList().indexOf(filter.getId())).setActive(true);
				}
				
				if (SharedObjs.getSharedFiltersList().indexOf(filter.getId()) >= 0)
				{
					SharedObjs.getSharedFiltersList().get(SharedObjs.getSharedFiltersList().indexOf(filter.getId())).setActive(true);
				}
			}
		}
		
		setSharedTableFields();
		setMyFiltersTableFields();
	}
	
	public void saveFilters()
	{
		Logger.log("FiltersManager", "Saving filters");
		
		// Finish cell editing if editing
		if (myFiltersTable.isEditing())
			myFiltersTable.getCellEditor().stopCellEditing();
		
		if (sharedFiltersTable.isEditing())
			sharedFiltersTable.getCellEditor().stopCellEditing();
		
		// Execute stacked changes
		if (SharedObjs.satDB != null)
		{
			Logger.log(Logger.TAG_CUSTOM_FILTERS, "Updating DB and XML");
			
			// Execute changes made in MyFilters tab
			for (CustomFilterItem filter : changesStack)
			{
				if (filter.isPublic())
					filter.setOwner("Public");
				
				if (filter.getModified().equals("Update"))
				{
					System.out.println("Update element: " + filter.getName());
					SharedObjs.satDB.updateFilter(filter);
				}
				else if (filter.getModified().equals("Insert"))
				{
					System.out.println("Insert: " + filter.getName());
					SharedObjs.satDB.insertFilter(filter);
				}
				else if (filter.getModified().equals("Delete"))
				{
					System.out.println("Delete: " + filter.getName());
					SharedObjs.satDB.deleteFilter(filter);
				}
			}
			
			// Execute changes made in SharedFilters tab
			for (CustomFilterItem filter : sharedTabChangesStack)
			{
				if (filter.isPublic())
					filter.setOwner("Public");
				
				if (filter.getModified().equals("Update"))
				{
					System.out.println("Update element: " + filter.getName());
					SharedObjs.satDB.updateFilter(filter);
				}
				else if (filter.getModified().equals("Insert"))
				{
					System.out.println("Insert: " + filter.getName());
					SharedObjs.satDB.insertFilter(filter);
				}
				else if (filter.getModified().equals("Delete"))
				{
					System.out.println("Delete: " + filter.getName());
					SharedObjs.satDB.deleteFilter(filter);
				}
			}
			
			// Show changes stack trace
			String stackTrace = "";
			for (CustomFilterItem filter : changesStack)
			{
				stackTrace = stackTrace + " - Name: " + filter.getName() + " Mod: " + filter.getModified();
			}
			Logger.log(Logger.TAG_CUSTOM_FILTERS, "MyChangeStack: " + stackTrace);
			
			stackTrace = "";
			for (CustomFilterItem filter : sharedTabChangesStack)
			{
				stackTrace = stackTrace + " - Name: " + filter.getName() + " Mod: " + filter.getModified();
			}
			Logger.log(Logger.TAG_CUSTOM_FILTERS, "SharedChangeStack: " + stackTrace);
			
			// Update XML file and in memory lists
			SharedObjs.getUserFiltersList().clear();
			SharedObjs.getUserFiltersList().addAll(myFiltersTableModel.getFilterElements());
			XmlMngr.removeAllMyFilters();
			XmlMngr.addMyFilters(SharedObjs.getUserFiltersList());
			
			SharedObjs.getSharedFiltersList().clear();
			SharedObjs.getSharedFiltersList().addAll(sharedTableModel.getFilterElements());
			XmlMngr.removeAllSharedFilters();
			XmlMngr.addSharedFilters(SharedObjs.getSharedFiltersList());
			
			SharedObjs.getActiveFiltersList().clear();
			SharedObjs.getActiveFiltersList().addAll(SharedObjs.satDB.activeFilters());
			XmlMngr.removeAllActiveFilters();
			XmlMngr.addActiveFilters(SharedObjs.getActiveFiltersList());
		}
		else
		{
			Logger.log("FiltersManager", "Updating XML");
			XmlMngr.removeAllMyFilters();
			XmlMngr.removeAllSharedFilters();
			XmlMngr.removeAllActiveFilters();
			SimpleDateFormat formater = new SimpleDateFormat("yyy-MM-dd HH:mm:ss");
			
			// Execute changes made in MyFilters tab
			for (CustomFilterItem filter : changesStack)
			{
				if (filter.getModified().equals("Update"))
				{
					System.out.println("Update element: " + filter.getName());
					filter.setLastUpdate(formater.format(new Date()));
				}
				else if (filter.getModified().equals("Insert"))
				{
					System.out.println("Insert: " + filter.getName());
					filter.setLastUpdate(formater.format(new Date()));
				}
				else if (filter.getModified().equals("Delete"))
				{
					System.out.println("Delete: " + filter.getName());
				}
			}
			
			for (CustomFilterItem filter : myFiltersTableModel.getFilterElements())
			{
				if (filter.isPublic())
					filter.setOwner("Public");
				
				XmlMngr.setMyFiltersValueOf(filter);
				// System.out.println(filter.getName() + " - " + filter.isActive());
				if (filter.isActive())
				{
					XmlMngr.setActiveFiltersValueOf(filter);
				}
			}
			
			for (CustomFilterItem filter : sharedTableModel.getFilterElements())
			{
				if (filter.isPublic())
					filter.setOwner("Public");
				
				filter.setLastUpdate(formater.format(new Date()));
				XmlMngr.setSharedFiltersValueOf(filter);
				// System.out.println(filter.getName() + " - " + filter.isActive());
				if (filter.isActive())
				{
					XmlMngr.setActiveFiltersValueOf(filter);
				}
			}
			
			// Update in memory lists
			SharedObjs.getUserFiltersList().clear();
			SharedObjs.getUserFiltersList().addAll(XmlMngr.getAllMyFilters());
			
			SharedObjs.getSharedFiltersList().clear();
			SharedObjs.getSharedFiltersList().addAll(XmlMngr.getAllSharedFilters());
			
			SharedObjs.getActiveFiltersList().clear();
			SharedObjs.getActiveFiltersList().addAll(XmlMngr.getAllActiveFilters());
		}
		
		SharedObjs.parserPane.getFiltersResultsTree().updateFiltersTree();
		
		Logger.log("FiltersManager", "Filters Saved");
	}
	
	public void sharedFiltersTableTracer(TableModelEvent evt)
	{
		if (evt.getType() == TableModelEvent.UPDATE)
		{
			// int column = evt.getColumn();
			int row = evt.getFirstRow();
			
			CustomFilterItem filter = sharedTableModel.getElementAt(row);
			
			if (sharedTableModel.hasFilterName(filter))
			{
				sharedFiltersTable.setColumnSelectionInterval(1, 1);
				sharedFiltersTable.setRowSelectionInterval(sharedFiltersTable.convertRowIndexToView(row), sharedFiltersTable.convertRowIndexToView(row));
				int i = 1;
				filter.setName(filter.getName() + "(" + i + ")");
				
				while (sharedTableModel.hasFilterName(filter))
				{
					i++;
					filter.setName(filter.getName().subSequence(0, filter.getName().length() - 3) + "(" + i + ")");
				}
				
				JOptionPane.showMessageDialog(SharedObjs.getCustomFiltersPane(), "This is already in use");
				
				sharedFiltersTable.transferFocus();
			}
			else
			{
				if (!filter.getModified().equals("Insert"))
					filter.setModified("Update");
				
				if (filter.getId() >= 0)
				{
					if (sharedTabChangesStack.indexOf(filter.getId()) >= 0)
					{
						sharedTabChangesStack.set(sharedTabChangesStack.indexOf(filter.getId()), filter);
					}
					else
					{
						sharedTabChangesStack.add(filter);
					}
				}
				else if (sharedTabChangesStack.indexOf(filter.getLastUpdate()) >= 0)
				{
					sharedTabChangesStack.set(sharedTabChangesStack.indexOf(filter.getLastUpdate()), filter);
				}
				else
				{
					sharedTabChangesStack.add(filter);
				}
			}
		}
	}
	
	public void myFiltersTableTracer(TableModelEvent evt)
	{
		if (evt.getType() == TableModelEvent.UPDATE)
		{
			int row = evt.getFirstRow();
			
			CustomFilterItem filter = myFiltersTableModel.getElementAt(row);
			
			if (myFiltersTableModel.hasFilterName(filter))
			{
				myFiltersTable.setColumnSelectionInterval(1, 1);
				myFiltersTable.setRowSelectionInterval(myFiltersTable.convertRowIndexToView(row), myFiltersTable.convertRowIndexToView(row));
				int i = 1;
				filter.setName(filter.getName() + "(" + i + ")");
				
				while (myFiltersTableModel.hasFilterName(filter))
				{
					i++;
					filter.setName(filter.getName().subSequence(0, filter.getName().length() - 3) + "(" + i + ")");
				}
				
				JOptionPane.showMessageDialog(SharedObjs.getCustomFiltersPane(), "This is already in use");
				
				myFiltersTable.transferFocus();
			}
			else
			{
				System.out.println("Nome OK");
				if (!filter.getModified().equals("Insert"))
					filter.setModified("Update");
				
				if (filter.getId() >= 0)
				{
					System.out.println("ID Encontrado");
					if (changesStack.indexOf(filter.getId()) >= 0)
					{
						changesStack.set(changesStack.indexOf(filter.getId()), filter);
					}
					else
					{
						changesStack.add(filter);
					}
				}
				else if (changesStack.indexOf(filter.getLastUpdate()) >= 0)
				{
					changesStack.set(changesStack.indexOf(filter.getLastUpdate()), filter);
				}
				else
				{
					changesStack.add(filter);
				}
			}
		}
	}
	
	public void setMyFiltersTableFields()
	{
		// Clean table
		while (myFiltersTableModel.getRowCount() > 0)
			myFiltersTableModel.removeRow(myFiltersTableModel.getRowCount() - 1);
		
		// Load MyFilters
		for (CustomFilterItem filter : SharedObjs.getUserFiltersList())
		{
			System.out.println(filter.getName() + " - " + filter.isActive());
			myFiltersTableModel.addRow(filter);
		}
	}
	
	public void setSharedTableFields()
	{
		// Clean table
		while (sharedTableModel.getRowCount() > 0)
			sharedTableModel.removeRow(sharedTableModel.getRowCount() - 1);
		
		// Load SharedFilters
		for (CustomFilterItem filter : SharedObjs.getSharedFiltersList())
		{
			sharedTableModel.addRow(filter);
		}
	}
}