package controllers;


import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JOptionPane;

import models.CrsManagerModel;

import org.json.simple.parser.ParseException;

import supportive.Bug2goDownloader;
import supportive.JiraSatApi;
import supportive.OldCRsCloser;
import views.CrsManagerPane;
import core.Logger;
import core.SharedObjs;
import customobjects.CrItem;
import customobjects.CrItemsList;


public class CrsManagerController
{
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	final String    TAG = "CRS_MANAGER_CONTROLLER";
	CrsManagerPane  view;
	CrsManagerModel model;
	ActionListener  btnsActionListener;
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Initialize controller -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void startController(CrsManagerPane view, CrsManagerModel model)
	{
		this.view = view;
		this.model = model;
		
		configureVariables();
		setupViewActionListeners();
		initializeViewItens();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Initialize controller variables ---------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void configureVariables()
	{
		btnsActionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				if (e.getSource().getClass().getSimpleName().equals("JButton"))
				{
					JButton btn = (JButton) e.getSource();
					
					if (btn.getToolTipText().contains(""))
					{
						if (SharedObjs.getClosedList() == null || SharedObjs.getOpenedList() == null)
						{
							JOptionPane.showMessageDialog(SharedObjs.crsManagerPane, "Error: The lists does not exist");
						}
						
						SharedObjs.getClosedList().setVisible(true);
						SharedObjs.getOpenedList().setVisible(true);
					}
					
					else if (btn.getToolTipText().contains(""))
					{
						view.setTextDownload("");
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
								view.setTextDownload(view.getTextDownload() + str + "\n");
							}
						}
						catch (Exception ex)
						{
							JOptionPane.showMessageDialog(view, "An error occurred. Please check logs.");
							Logger.log(Logger.TAG_CRSMANAGER, ex.getMessage());
							ex.printStackTrace();
						}
					}
					
					else if (btn.getToolTipText().contains(""))
					{
						view.setTextDownload("");
					}
					
					else if (btn.getToolTipText().contains(""))
					{
						downloadBtnAction();
					}
					
					else if (btn.getToolTipText().contains(""))
					{
						
					}
					
					else
					{
						Logger.log(TAG, "Button could not be identified!");
					}
				}
			}
		};
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View actions setup ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void setupViewActionListeners()
	{
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View itens initialization ---------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void initializeViewItens()
	{
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View data saving ------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void saveViewData()
	{
		
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Controller support methods --------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void disableOptionsAndBtns()
	{
		view.setBtnDownloadEnabled(false);
		view.setChckbxAnalyzeEnabled(false);
		view.setChckbxAssignEnabled(false);
		view.setChckbxCloseAsOldEnabled(false);
		view.setChckbxDownloadEnabled(false);
		view.setChckbxLabelsEnabled(false);
		view.setChckbxRemLabelsEnabled(false);
		view.setChckbxUnassignEnabled(false);
		view.setChckbxUnzipEnabled(false);
	}
	
	public void enableViewOptionsAndBtns()
	{
		
		view.setBtnDownloadEnabled(true);
		
		if (view.isChckbxDownloadSelected())
			view.setChckbxAnalyzeEnabled(true);
		
		if (view.isChckbxAnalyzeSelected())
			view.setChckbxUnzipEnabled(true);
		
		view.setChckbxAnalyzeEnabled(true);
		view.setChckbxCloseAsOldEnabled(true);
		view.setChckbxDownloadEnabled(true);
		view.setChckbxLabelsEnabled(true);
		view.setChckbxRemLabelsEnabled(true);
		view.setChckbxUnassignEnabled(true);
	}
	
	private void downloadBtnAction()
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
					if (view.isChckbxDownloadSelected())
					{
						File downloadPath = new File(view.getTextPath().replace("\\", "/"));
						if (!downloadPath.exists())
							downloadPath.mkdir();
						
						downloadCRs();
					}
					
					// Close as old proccess
					else if (view.isChckbxCloseAsOldSelected())
					{
						OldCRsCloser closer = new OldCRsCloser(view.getTextDownload().split("\n"));
						new Thread(closer).start();
					}
					
					// Singular options solo definition
					else
					{
						// Get the CRs list
						SharedObjs.crsManagerPane.addLogLine("Ganerating CRs list...");
						model.setCRs(view.getTextDownload().replaceAll(" ", "").split("\n"));
						
						// Check if not empty list
						Logger.log(Logger.TAG_CRSMANAGER, "CRs List:" + model.getNumOfCRs());
						if (model.getNumOfCRs() == 0 || (model.getNumOfCRs() == 1 && !model.getCrAt(0).contains("-")))
						{
							SharedObjs.crsManagerPane.addLogLine("CRs list empty");
							enableViewOptionsAndBtns();
							return;
						}
						
						// Setup jira connection
						SharedObjs.crsManagerPane.addLogLine("Connecting to Jira ...");
						JiraSatApi jira = new JiraSatApi(JiraSatApi.DEFAULT_JIRA_URL, SharedObjs.getUser(), SharedObjs.getPass());
						
						// Assign CRs
						if (view.isChckbxAssignSelected())
						{
							// If need to add labels too, do it at same time
							if (view.isChckbxLabelsSelected())
							{
								SharedObjs.crsManagerPane.addLogLine("Assigning and adding labels ...");
								
								// Get label list
								model.setLabels(view.getTextLabels().split(" |  |   "));
								for (String s : model.getLabels())
								{
									Logger.log(Logger.TAG_CRSMANAGER, "Label entered: " + s);
								}
								
								for (String crKey : model.getCRs())
								{
									crKey = trimCR(crKey);
									jira.assignIssue(crKey);
									jira.addLabel(crKey, model.getLabels());
								}
							}
							
							// Otherwise ...
							else
							{
								SharedObjs.crsManagerPane.addLogLine("Assigning CRs...");
								for (String crKey : model.getCRs())
								{
									crKey = trimCR(crKey);
									jira.assignIssue(crKey);
								}
							}
							
							SharedObjs.crsManagerPane.addLogLine("Done");
						}
						
						// Add Labels
						else if (view.isChckbxLabelsSelected())
						{
							SharedObjs.crsManagerPane.addLogLine("Adding labels ...");
							
							// Get label list
							model.setLabels(view.getTextLabels().split(" |  |   "));
							for (String s : model.getLabels())
							{
								Logger.log(Logger.TAG_CRSMANAGER, "Label entered: " + s);
							}
							
							// Manage CR
							for (String crKey : model.getCRs())
							{
								crKey = trimCR(crKey);
								if (jira.addLabel(crKey, model.getLabels())
								        .contains("\"labels\":\"Field 'labels' cannot be set. It is not on the appropriate screen, or unknown"))
								{
									jira.assignIssue(crKey);
									jira.addLabel(crKey, model.getLabels());
									jira.unassignIssue(crKey);
								}
							}
							
							SharedObjs.crsManagerPane.addLogLine("Done");
						}
						
						// Unassign CRs
						if (view.isChckbxUnassignSelected())
						{
							// If need to unassign issues too, do it at same time
							if (view.isChckbxRemLabelsSelected())
							{
								SharedObjs.crsManagerPane.addLogLine("Unassignin and removing labels ...");
								
								// Get label list
								model.setLabels(view.getTextLabels().split(" |  |   "));
								for (String crKey : model.getCRs())
								{
									crKey = trimCR(crKey);
									jira.removeLabel(crKey, model.getLabels());
									jira.unassignIssue(crKey);
								}
							}
							
							// Otherwise ...
							else
							{
								SharedObjs.crsManagerPane.addLogLine("Unassigning CRs ...");
								for (String crKey : model.getCRs())
								{
									crKey = trimCR(crKey);
									jira.unassignIssue(crKey);
								}
							}
							
							SharedObjs.crsManagerPane.addLogLine("Done");
						}
						
						// Remove labels
						if (view.isChckbxRemLabelsSelected())
						{
							SharedObjs.crsManagerPane.addLogLine("Removing labels ...");
							
							// Get label list
							model.setLabels(view.getTextLabels().split(" |  |   "));
							for (String s : model.getLabels())
							{
								Logger.log(Logger.TAG_CRSMANAGER, "Label entered: " + s);
							}
							
							for (String crKey : model.getCRs())
							{
								crKey = trimCR(crKey);
								if (jira.removeLabel(crKey, model.getLabels())
								        .contains("\"labels\":\"Field 'labels' cannot be set. It is not on the appropriate screen, or unknown"))
								{
									jira.assignIssue(crKey);
									jira.removeLabel(crKey, model.getLabels());
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
					enableViewOptionsAndBtns();
				}
			}
		}).start();
	}
	
	/**
	 * Download CRs
	 * 
	 * main download function
	 * @throws ParseException
	 */
	private void downloadCRs() throws ParseException
	{
		model.resetErrorsCount();
		
		// Setup jira connection
		SharedObjs.crsManagerPane.addLogLine("Connecting to Jira ...");
		JiraSatApi jira = new JiraSatApi(JiraSatApi.DEFAULT_JIRA_URL, SharedObjs.getUser(), SharedObjs.getPass());
		
		// Get the CRs list
		model.setCRs(view.getTextDownload().replaceAll(" ", "").split("\n"));
		
		Logger.log(Logger.TAG_CRSMANAGER, "CRs List:" + model.getNumOfCRs());
		if (model.getNumOfCRs() == 0 || (model.getNumOfCRs() == 1 && !model.getCrAt(0).contains("-")))
		{
			SharedObjs.crsManagerPane.addLogLine("CRs list empty");
			enableViewOptionsAndBtns();
			return;
		}
		
		ArrayList<String> b2gList = new ArrayList<String>();
		
		// Get label list
		model.setLabels(view.getTextLabels().split(" |  |   "));
		for (String s : model.getLabels())
		{
			Logger.log(Logger.TAG_CRSMANAGER, "Label entered: " + s);
		}
		
		SharedObjs.crsManagerPane.addLogLine("Acquiring " + model.getNumOfCRs() + " CRs data ...");
		SharedObjs.getCrsList().clear();
		
		// Manage CR
		int crsCount = 0;
		model.setIgnoredList(new CrItemsList());
		for (String crKey : model.getCRs())
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
				if (view.isChckbxIgnoreAnalyzedSelected())
				{
					if (crItem.getLabels().contains("sat_pre_analyzed"))
					{
						view.addLogLine(crsCount + " - " + crKey + " - Will not be analyzed");
						model.addToIgnoredList(crItem);
					}
				}
				
				view.addLogLine(crsCount + " - " + crKey + " - got it");
				
				if (view.isChckbxLabelsSelected())
				{
					jira.assignIssue(crKey);
					jira.addLabel(crKey, model.getLabels());
					
					if (view.isChckbxAssignSelected())
					{
						crItem.setAssignee(SharedObjs.getUser());
					}
					else
					{
						crItem.setAssignee("");
						jira.unassignIssue(crKey);
					}
				}
				else if (view.isChckbxAssignSelected())
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
				model.incrementErrorsCount();
			}
		}
		
		if (view.isChckbxDownloadSelected())
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
					b2gDownloader.setError(model.getErrors());
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
				if (model.getIgnoredListSize() == model.getNumOfCRs())
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
		
		enableViewOptionsAndBtns();
	}
	
	public String trimCR(String s)
	{
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\t", "");
		s = s.trim();
		return s;
	}
}
