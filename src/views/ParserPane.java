package views;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.LineBorder;
import javax.swing.event.UndoableEditListener;

import style.NonWrappingTextPane;
import views.custom_components.FileTree;
import views.custom_components.FiltersTree;


@SuppressWarnings("serial")
public class ParserPane extends JPanel
{
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private JSplitPane          splitPane;
	private JSplitPane          splitPane_1;
	private FileTree            fileTree;
	private FiltersTree         filtersTree;
	private NonWrappingTextPane resultTxtPane;
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Constructor -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public ParserPane()
	{
		setMinimumSize(new Dimension(800, 600));
		GridBagLayout layout = new GridBagLayout();
		layout.columnWidths = new int[] {0};
		layout.rowHeights = new int[] {30, 300};
		layout.rowWeights = new double[] {0.0, 1.0};
		layout.columnWeights = new double[] {1.0};
		setLayout(layout);
		
		splitPane_1 = new JSplitPane();
		splitPane_1.setLastDividerLocation(200);
		GridBagConstraints gbc_splitPane_1 = new GridBagConstraints();
		gbc_splitPane_1.fill = GridBagConstraints.BOTH;
		gbc_splitPane_1.gridx = 0;
		gbc_splitPane_1.gridy = 1;
		add(splitPane_1, gbc_splitPane_1);
		
		JScrollPane scrollPane = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane);
		scrollPane.setToolTipText("Result of the selected parser item on the left");
		scrollPane.setFont(new Font("Consolas", Font.PLAIN, 12));
		scrollPane.setBorder(new LineBorder(new Color(102, 153, 204)));
		
		scrollPane.setAutoscrolls(true);
		scrollPane.setRequestFocusEnabled(false);
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setPreferredSize(new Dimension(500, 500));
		scrollPane.setMinimumSize(new Dimension(400, 400));
		scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		
		resultTxtPane = new NonWrappingTextPane();
		resultTxtPane.setBorder(null);
		resultTxtPane.setToolTipText("Result of the selected parser item on the left");
		resultTxtPane.setContentType("text/plain");
		resultTxtPane.setMargin(new Insets(7, 2, 7, 2));
		resultTxtPane.setForeground(new Color(0, 0, 0));
		resultTxtPane.setFont(new Font("Consolas", Font.PLAIN, 11));
		resultTxtPane.setText("");
		
		scrollPane.setViewportView(resultTxtPane);
		
		splitPane = new JSplitPane();
		splitPane_1.setLeftComponent(splitPane);
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
		splitPane.setDividerLocation(300);
		splitPane_1.setDividerLocation(200);
		
		// initPane();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Add Listeners ---------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void resultTxtPaneAddUndoableEditListener(UndoableEditListener ul)
	{
		resultTxtPane.getDocument().addUndoableEditListener(ul);
	}
	
	public void resultTxtPaneAddMouseListener(MouseAdapter ml)
	{
		resultTxtPane.addMouseListener(ml);
	}
	
	public void resultTextPaneAddKeyListener(KeyListener kl)
	{
		resultTxtPane.addKeyListener(kl);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Supportive methods ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void setResultsTxtPaneCaretPosition(int pos)
	{
		resultTxtPane.setCaretPosition(pos);
	}
	
	public Object getLastSelectedFilterObject()
	{
		return filtersTree.getLastSelectedPathComponent();
	}
	
	public Boolean isResultsTxtPaneFocusOwner()
	{
		return resultTxtPane.isFocusOwner();
	}
	
	public void setResultsTxtPaneTextWrap(Boolean bool)
	{
		resultTxtPane.setWrapText(bool);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Getters/Setters -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public FiltersTree getFiltersTree()
	{
		return filtersTree;
	}
	
	public void setResultsTxtPaneText(String text)
	{
		resultTxtPane.setText(text);
		resultTxtPane.setCaretPosition(0);
	}
	
	public String getResultText()
	{
		return resultTxtPane.getText();
	}

	public NonWrappingTextPane getResultTxtPane()
	{
		return resultTxtPane;
	}
	
	
}
