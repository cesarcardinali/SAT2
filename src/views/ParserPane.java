package views;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.UndoManager;

import style.NonWrappingTextPane;
import core.SharedObjs;
import customobjects.FileTree;
import customobjects.FiltersTree;


@SuppressWarnings("serial")
public class ParserPane extends JPanel
{
	private UndoManager         undoManager;
	private JSplitPane          splitPane;
	private FileTree            fileTree;
	private FiltersTree         filtersTree;
	private NonWrappingTextPane resultTxtPane;
	
	/**
	 * Create the panel.
	 */
	public ParserPane()
	{
		setMinimumSize(new Dimension(800, 600));
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {250, 600};
		layout.rowHeights = new int[] {30, 300};
		layout.rowWeights = new double[] {0.0, 1.0};
		layout.columnWeights = new double[] {1.0, 1.0};
		setLayout(layout);
		
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setToolTipText("Result of the selected parser item on the left");
		scrollPane.setFont(new Font("Consolas", Font.PLAIN, 12));
		scrollPane.setBorder(new LineBorder(new Color(102, 153, 204)));
		
		scrollPane.setAutoscrolls(true);
		scrollPane.setRequestFocusEnabled(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(500, 500));
		scrollPane.setMinimumSize(new Dimension(400, 400));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		splitPane = new JSplitPane();
		splitPane.setDividerSize(8);
		splitPane.setBorder(null);
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		
		fileTree = new FileTree();
		fileTree.setBorder(new LineBorder(new Color(102, 153, 204), 1, true));
		splitPane.setRightComponent(fileTree);
		JScrollPane scrollFiltersResults = new JScrollPane();
		scrollFiltersResults.setBorder(new LineBorder(new Color(102, 153, 204), 1, true));
		filtersTree = new FiltersTree();
		filtersTree.setBorder(new LineBorder(Color.LIGHT_GRAY));
		scrollFiltersResults.setViewportView(filtersTree);
		scrollFiltersResults.setMinimumSize(new Dimension(150, 150));
		splitPane.setLeftComponent(scrollFiltersResults);
		GridBagConstraints gbc_splitPane = new GridBagConstraints();
		gbc_splitPane.insets = new Insets(0, 5, 10, 5);
		gbc_splitPane.fill = GridBagConstraints.BOTH;
		gbc_splitPane.gridx = 0;
		gbc_splitPane.gridy = 1;
		add(splitPane, gbc_splitPane);
		splitPane.setDividerLocation(300);
		
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.insets = new Insets(0, 10, 10, 10);
		gbc_scrollPane.weighty = 22.0;
		gbc_scrollPane.weightx = 15.0;
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 1;
		add(scrollPane, gbc_scrollPane);
		
		resultTxtPane = new NonWrappingTextPane();
		resultTxtPane.setBorder(null);
		resultTxtPane.setToolTipText("Result of the selected parser item on the left");
		resultTxtPane.setContentType("text/plain");
		resultTxtPane.setMargin(new Insets(7, 2, 7, 2));
		resultTxtPane.setForeground(new Color(0, 0, 0));
		resultTxtPane.setFont(new Font("Consolas", Font.PLAIN, 11));
		resultTxtPane.setText("");
		
		scrollPane.setViewportView(resultTxtPane);
		
		initPane();
	}
	
	
	
	
	
	private void initPane()
	{
		undoManager = new UndoManager();
		resultTxtPane.getDocument().addUndoableEditListener(new UndoableEditListener()
		{
			@Override
			public void undoableEditHappened(UndoableEditEvent e)
			{
				undoManager.addEdit(e.getEdit());
			}
		});
		
		resultTxtPane.addMouseListener(new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
				}
			}
		});
		
		SharedObjs.setResult("");
		setResultsTxtPaneText("");
	}
	
	
	public void setResultTextPaneKeyListener(KeyListener kl)
	{
		resultTxtPane.addKeyListener(kl);
	}
	
	/**
	 * Reset pane UI to initial state
	 */
	public void clearPane()
	{
		filtersTree.clearTree();
		resultTxtPane.setText(""); // reset the text pane
		SharedObjs.setResult(""); // reset the result for the filters
	}
	
	
	/**
	 * Show all log results on the results pane
	 */
	public void showAllLogResults()
	{
		while (SharedObjs.getResult().charAt(0) == '\n')
		{
			SharedObjs.setResult(SharedObjs.getResult().substring(1));
		}
		resultTxtPane.setText(SharedObjs.getResult());
		resultTxtPane.setCaretPosition(0);
	}
	
	
	
	
	
	
	
	
	
	public void undo()
	{
		undoManager.undo();
	}
	
	// Getters and Setters
	public FiltersTree getFiltersResultsTree()
	{
		return filtersTree;
	}
	
	public NonWrappingTextPane getResultsTxtPane()
	{
		return resultTxtPane;
	}
	
	public void setResultsTxtPaneText(String text)
	{
		resultTxtPane.setText(text);
		resultTxtPane.setCaretPosition(0);
	}
	
	public Object getLastSelectedFilterObject()
	{
		return filtersTree.getLastSelectedPathComponent();
	}
	
	public String getResultText()
	{
		return resultTxtPane.getText();
	}
}
