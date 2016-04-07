package controllers;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.undo.CannotRedoException;

import views.ParserPane;
import core.Logger;
import core.SharedObjs;
import core.XmlMngr;
import filters.Alarm;
import filters.B2G;
import filters.Consume;
import filters.Diag;
import filters.Issue;
import filters.Normal;
import filters.Suspicious;
import filters.Tether;


public class ParserController
{
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables ----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private ParserPane  view;
	private KeyListener resultTxtPaneKeyListener;
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Controller initializer ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void startController(ParserPane view)
	{
		// Setup the view
		this.view = view;
		
		// Setup controller
		initVariables();
		setupViewEvents();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Controller setup methods -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void initVariables()
	{
		resultTxtPaneKeyListener = new KeyListener()
		{
			@Override
			public void keyReleased(KeyEvent arg0)
			{
				saveTextChanges(view.getLastSelectedFilterObject(), view.getResultText());
			}
			
			@Override
			public void keyTyped(KeyEvent arg0)
			{
			}
			
			@Override
			public void keyPressed(KeyEvent e)
			{
				if (e.isControlDown() && e.isShiftDown() && e.getKeyCode() == 67)
				{
					copyAll();
				}
				
				if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0))
				{
					try
					{
						view.undo();
					}
					catch (CannotRedoException cre)
					{
						cre.printStackTrace();
					}
				}
			}
		};
		
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View actions setup -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void setupViewEvents()
	{
		view.setResultTextPaneKeyListener(resultTxtPaneKeyListener);
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Methods ------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void copyAll()
	{
		Clipboard clpbrd = Toolkit.getDefaultToolkit().getSystemClipboard();
		StringSelection stringSelection = new StringSelection(view.getResultText());
		clpbrd.setContents(stringSelection, null);
	}
	
	private void saveTextChanges(Object node, String text)
	{
		String selectedNode = node.toString().toLowerCase();
		String selectedNodeParent = ((DefaultMutableTreeNode) node).getParent().toString().toLowerCase();
		
		if ((selectedNode.contains("colors") && selectedNodeParent.contains("alarms ")) || selectedNode.contains("alarms "))
			Alarm.updateResult(text);
		if ((selectedNode.contains("colors") && selectedNodeParent.contains(" consum")) || selectedNode.contains(" consum"))
			Consume.updateResult(text);
		if (selectedNode.contains("diag ") || selectedNodeParent.contains("diag "))
			Diag.updateResult(text);
		if (selectedNode.contains("suspicious"))
			Suspicious.updateResult(text);
		if (selectedNode.contains("tether") || selectedNodeParent.contains("tether"))
			Tether.updateResult(text);
		if (selectedNode.contains("summary") || selectedNodeParent.contains("summary"))
			Normal.updateResult(text);
		if (selectedNode.contains(" issues") || selectedNodeParent.contains(" issues"))
			Issue.updateResult(text);
		if (selectedNode.contains("bug2go") || selectedNodeParent.contains("bug2go"))
		{
			B2G.updateResult(text);
			B2G.setEdited(true);
		}
	}
	
	public void savePaneData()
	{
		XmlMngr.setUserValueOf(new String[] {"parser_pane", "rootPath"}, SharedObjs.getRootFolderPath());
		
		Logger.log(Logger.TAG_PARSER, "Parser data saved");
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Getters and Setters ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
}
