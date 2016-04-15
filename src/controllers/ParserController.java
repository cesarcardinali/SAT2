package controllers;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.SwingUtilities;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.UndoManager;

import models.ParserModel;
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
	private ParserPane           view;
	private ParserModel          model;
	private KeyListener          resultTxtPaneKeyListener;
	private UndoManager          undoManager;
	private UndoableEditListener undoableEditListener;
	private MouseAdapter         mouseListener;
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Controller initializer ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void startController(ParserPane view, ParserModel model)
	{
		// Set the view/model
		this.view = view;
		this.setModel(model);
		
		// Setup controller
		configureVariables();
		setupViewActionListeners();
		initializeViewItens();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Initialize controller variables ---------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void configureVariables()
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
						undo();
					}
					catch (CannotRedoException cre)
					{
						cre.printStackTrace();
					}
				}
			}
		};
		
		undoManager = new UndoManager();
		
		undoableEditListener = new UndoableEditListener()
		{
			@Override
			public void undoableEditHappened(UndoableEditEvent e)
			{
				undoManager.addEdit(e.getEdit());
			}
		};
		
		mouseListener = new MouseAdapter()
		{
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (SwingUtilities.isRightMouseButton(e))
				{
					// TODO Popup menu with options select all, copy and paste
				}
			}
		};
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View actions setup -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void setupViewActionListeners()
	{
		view.resultTextPaneAddKeyListener(resultTxtPaneKeyListener);
		
		view.resultTxtPaneAddUndoableEditListener(undoableEditListener);
		
		view.resultTxtPaneAddMouseListener(mouseListener);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View itens initialization ---------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void initializeViewItens()
	{
		SharedObjs.setResult("");
		view.setResultsTxtPaneText("");
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Actions definition ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View manipulation methods ---------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void clearPane()
	{
		view.getFiltersTree().clearTree();
		view.setResultsTxtPaneText(""); // reset the text pane
		SharedObjs.setResult(""); // reset the result for the filters
	}
	
	public void showAllLogResults()
	{
		while (SharedObjs.getResult().charAt(0) == '\n')
		{
			SharedObjs.setResult(SharedObjs.getResult().substring(1));
		}
		view.setResultsTxtPaneText(SharedObjs.getResult());
	}
	
	public void undo()
	{
		undoManager.undo();
	}
	
	// --------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Supportive Methods -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
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
		{
			Alarm.updateResult(text);
		}
		if ((selectedNode.contains("colors") && selectedNodeParent.contains(" consum")) || selectedNode.contains(" consum"))
		{
			Consume.updateResult(text);
		}
		if (selectedNode.contains("diag ") || selectedNodeParent.contains("diag "))
		{
			Diag.updateResult(text);
		}
		if (selectedNode.contains("suspicious"))
		{
			Suspicious.updateResult(text);
		}
		if (selectedNode.contains("tether") || selectedNodeParent.contains("tether"))
		{
			Tether.updateResult(text);
		}
		if (selectedNode.contains("summary") || selectedNodeParent.contains("summary"))
		{
			Normal.updateResult(text);
		}
		if (selectedNode.contains(" issues") || selectedNodeParent.contains(" issues"))
		{
			Issue.updateResult(text);
		}
		if (selectedNode.contains("bug2go") || selectedNodeParent.contains("bug2go"))
		{
			B2G.updateResult(text);
			B2G.setEdited(true);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// UI data load/save -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void saveUIData()
	{
		XmlMngr.setUserValueOf(new String[] {"parser_pane", "rootPath"}, SharedObjs.getRootFolderPath());
		
		Logger.log(Logger.TAG_PARSER, "Parser data saved");
	}
	
	public void loadUIData()
	{
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Getters/Setters -------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public ParserModel getModel()
	{
		return model;
	}
	
	public void setModel(ParserModel model)
	{
		this.model = model;
	}
	
}
