package controllers;


import java.awt.Desktop;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import models.CrsManagerModel;

import org.json.simple.parser.ParseException;

import supportive.Bug2goDownloader;
import supportive.JiraSatApi;
import supportive.OldCRsCloser;
import views.CrsManagerPane;
import core.Logger;
import core.SharedObjs;
import core.XmlMngr;
import customobjects.CrItem;
import customobjects.CrItemsList;


public class CrsManagerController
{
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	final String     TAG = "CRS_MANAGER_CONTROLLER";
	CrsManagerPane   view;
	CrsManagerModel  model;
	ActionListener   btnsActionListener;
	ChangeListener   chckbxChangeListener;
	DocumentListener txtsDocumentListener;
	
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
					String btnText = btn.getText();
					
					if (btnText.equals("Clear")) // Clear action
					{
						clearBtnAction();
					}
					
					else if (btnText.equals("Paste")) // Paste action
					{
						pasteBtnAction();
					}
					
					else if (btnText.equals("Exec!")) // Download action
					{
						execBtnAtion();
					}
					
					else if (btnText.equals("Show Results")) // Show results
					{
						showResultsBtnAtion();
					}
					
					else if (btnText.equals("Open on Browser")) // Open on browser
					{
						openOnBrowserBtnAction();
					}
					
					else
					{
						Logger.log(TAG, "Button could not be identified!");
					}
				}
			}
		};
		
		chckbxChangeListener = new ChangeListener()
		{
			@Override
			public void stateChanged(ChangeEvent e)
			{
				if (e.getSource().getClass().getSimpleName().equals("JCheckBox"))
				{
					JCheckBox checkBox = (JCheckBox) e.getSource();
					String cbText = checkBox.getText();
					// System.out.println(cbText);
					
					if (cbText.contains("Assign"))
					{
						if (view.isChckbxAssignSelected())
						{
							view.setChckbxUnassignSelected(false);
						}
					}
					
					else if (cbText.contains("Unassign"))
					{
						if (view.isChckbxUnassignSelected())
						{
							view.setChckbxAssignSelected(false);
						}
					}
					
					else if (cbText.contains("Add labels"))
					{
						if (view.isChckbxLabelsSelected())
						{
							view.setChckbxRemLabelsSelected(false);
						}
					}
					
					else if (cbText.contains("Remove labels"))
					{
						if (view.isChckbxRemLabelsSelected())
						{
							view.setChckbxLabelsSelected(false);
						}
					}
					
					else if (cbText.contains("Download"))
					{
						if (view.isChckbxDownloadSelected())
						{
							view.setChckbxCloseAsOldSelected(false);
							
							view.setChckbxUnzipEnabled(true);
						}
						else
						{
							view.setChckbxUnzipEnabled(false);
						}
					}
					
					else if (cbText.contains("Unzip downloaded"))
					{
						if (view.isChckbxUnzipSelected() && view.isChckbxUnzipEnabled())
						{
							view.setChckbxAnalyzeEnabled(true);
						}
						else
						{
							view.setChckbxAnalyzeEnabled(false);
						}
					}
					
					else if (cbText.contains("Analyze downloaded"))
					{
						
					}
					
					else if (cbText.contains("Close CRs as old"))
					{
						if (view.isChckbxCloseAsOldSelected())
						{
							view.setChckbxDownloadSelected(false);
						}
					}
				}
			}
		};
		
		txtsDocumentListener = new DocumentListener()
		{
			@Override
			public void removeUpdate(DocumentEvent arg0)
			{
				SharedObjs.setDownloadPath(view.getTextPath());
				Logger.log(TAG, "Path updated");
			}
			
			@Override
			public void insertUpdate(DocumentEvent arg0)
			{
				SharedObjs.setDownloadPath(view.getTextPath());
				Logger.log(TAG, "Path updated");
			}
			
			@Override
			public void changedUpdate(DocumentEvent arg0)
			{
			}
		};
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View actions setup ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void setupViewActionListeners()
	{
		view.btnClearAddActionListener(btnsActionListener);
		view.btnPasteAddActionListener(btnsActionListener);
		view.btnExecuteAddActionListener(btnsActionListener);
		view.btnShowResultListsAddActionListener(btnsActionListener);
		view.btnOpenOnBrowserAddActionListener(btnsActionListener);
		
		view.chckbxAssignAddChangeListener(chckbxChangeListener);
		view.chckbxUnassignAddChangeListener(chckbxChangeListener);
		view.chckbxLabelsAddChangeListener(chckbxChangeListener);
		view.chckbxRemLabelsAddChangeListener(chckbxChangeListener);
		view.chckbxDownloadAddChangeListener(chckbxChangeListener);
		view.chckbxUnzipAddChangeListener(chckbxChangeListener);
		view.chckbxAssignAddChangeListener(chckbxChangeListener);
		view.chckbxCloseAsOldAddChangeListener(chckbxChangeListener);
		
		view.textPathAddDocumentListener(txtsDocumentListener);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View itens initialization ---------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void initializeViewItens()
	{
		loadUIData();
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Actions definition ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void clearBtnAction()
	{
		view.setTextDownload("");
	}
	
	public void pasteBtnAction()
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
	
	public void showResultsBtnAtion()
	{
		if (SharedObjs.getClosedList() == null || SharedObjs.getOpenedList() == null)
		{
			JOptionPane.showMessageDialog(SharedObjs.crsManagerPane, "Error: The lists does not exist");
		}
		
		SharedObjs.getClosedList().setVisible(true);
		SharedObjs.getOpenedList().setVisible(true);
	}
	
	private void execBtnAtion()
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
						SharedObjs.addLogLine("Ganerating CRs list...");
						model.setCRs(view.getTextDownload().replaceAll(" ", "").split("\n"));
						
						// Check if not empty list
						Logger.log(Logger.TAG_CRSMANAGER, "CRs List:" + model.getCRsCount());
						if (model.getCRsCount() == 0 || (model.getCRsCount() == 1 && !model.getCrAt(0).contains("-")))
						{
							SharedObjs.addLogLine("CRs list empty");
							enableViewOptionsAndBtns();
							return;
						}
						
						// Setup jira connection
						SharedObjs.addLogLine("Connecting to Jira ...");
						JiraSatApi jira = new JiraSatApi(JiraSatApi.DEFAULT_JIRA_URL, SharedObjs.getUser(), SharedObjs.getPass());
						
						// Assign CRs
						if (view.isChckbxAssignSelected())
						{
							// If need to add labels too, do it at same time
							if (view.isChckbxLabelsSelected())
							{
								SharedObjs.addLogLine("Assigning and adding labels ...");
								
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
								SharedObjs.addLogLine("Assigning CRs...");
								for (String crKey : model.getCRs())
								{
									crKey = trimCR(crKey);
									jira.assignIssue(crKey);
								}
							}
							
							SharedObjs.addLogLine("Done");
						}
						
						// Add Labels
						else if (view.isChckbxLabelsSelected())
						{
							SharedObjs.addLogLine("Adding labels ...");
							
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
							
							SharedObjs.addLogLine("Done");
						}
						
						// Unassign CRs
						if (view.isChckbxUnassignSelected())
						{
							// If need to unassign issues too, do it at same time
							if (view.isChckbxRemLabelsSelected())
							{
								SharedObjs.addLogLine("Unassignin and removing labels ...");
								
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
								SharedObjs.addLogLine("Unassigning CRs ...");
								for (String crKey : model.getCRs())
								{
									crKey = trimCR(crKey);
									jira.unassignIssue(crKey);
								}
							}
							
							SharedObjs.addLogLine("Done");
						}
						
						// Remove labels
						if (view.isChckbxRemLabelsSelected())
						{
							SharedObjs.addLogLine("Removing labels ...");
							
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
							
							SharedObjs.addLogLine("Done");
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
	
	public void openOnBrowserBtnAction()
	{
		for (String s : view.getTextDownload().split("\n"))
		{
			try
			{
				s = trimCR(s);
				Desktop.getDesktop().browse(new URI("http://idart.mot.com/browse/" + s));
				Thread.sleep(500);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(view, "Exception: " + ex.getMessage());
			}
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View manipulation methods ---------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void disableOptionsAndBtns()
	{
		view.setBtnDownloadEnabled(false);
		
		view.setChckbxAssignEnabled(false);
		view.setChckbxUnassignEnabled(false);
		view.setChckbxLabelsEnabled(false);
		view.setChckbxRemLabelsEnabled(false);
		view.setChckbxDownloadEnabled(false);
		view.setChckbxAnalyzeEnabled(false);
		view.setChckbxUnzipEnabled(false);
		view.setChckbxCloseAsOldEnabled(false);
	}
	
	public void enableViewOptionsAndBtns()
	{
		view.setBtnDownloadEnabled(true);
		
		if (view.isChckbxDownloadSelected())
			view.setChckbxUnzipEnabled(true);
		
		if (view.isChckbxUnzipSelected())
			view.setChckbxAnalyzeEnabled(true);
		
		view.setChckbxAnalyzeEnabled(true);
		view.setChckbxCloseAsOldEnabled(true);
		view.setChckbxDownloadEnabled(true);
		view.setChckbxLabelsEnabled(true);
		view.setChckbxRemLabelsEnabled(true);
		view.setChckbxUnassignEnabled(true);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// UI data load/save -----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void saveUIData()
	{
		String xmlPath[] = new String[] {"crs_jira_pane", ""};
		
		xmlPath[1] = "path";
		XmlMngr.setUserValueOf(xmlPath, view.getTextPath());
		xmlPath[1] = "assign";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxAssignSelected() + "");
		xmlPath[1] = "unassign";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxUnassignSelected() + "");
		xmlPath[1] = "label";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxLabelsSelected() + "");
		xmlPath[1] = "rem_label";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxRemLabelsSelected() + "");
		xmlPath[1] = "labels";
		XmlMngr.setUserValueOf(xmlPath, view.getTextLabels());
		xmlPath[1] = "download";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxDownloadSelected() + "");
		xmlPath[1] = "unzip";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxUnzipSelected() + "");
		xmlPath[1] = "analyze";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxAnalyzeSelected() + "");
		xmlPath[1] = "close";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxCloseAsOldSelected() + "");
		xmlPath[1] = "ignore";
		XmlMngr.setUserValueOf(xmlPath, view.isChckbxIgnoreAnalyzedSelected() + "");
		
		Logger.log(Logger.TAG_CRSMANAGER, "CrsManagerPane data saved");
	}
	
	private void loadUIData()
	{
		String xmlPath[] = new String[] {"crs_jira_pane", ""};
		
		xmlPath[1] = "path";
		view.setTextPath(XmlMngr.getUserValueOf(xmlPath));
		SharedObjs.setDownloadPath(view.getTextPath());
		
		xmlPath[1] = "assign";
		view.setChckbxAssignSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		xmlPath[1] = "unassign";
		view.setChckbxUnassignSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "label";
		view.setChckbxLabelsSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		xmlPath[1] = "rem_label";
		view.setChckbxRemLabelsSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "labels";
		view.setTextLabels(XmlMngr.getUserValueOf(xmlPath));
		
		xmlPath[1] = "download";
		view.setChckbxDownloadSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		System.out.println("" + Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "unzip";
		view.setChckbxUnzipSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "analyze";
		view.setChckbxAnalyzeSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "close";
		view.setChckbxCloseAsOldSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		xmlPath[1] = "ignore";
		view.setChckbxIgnoreAnalyzedSelected(Boolean.parseBoolean(XmlMngr.getUserValueOf(xmlPath)));
		
		Logger.log(Logger.TAG_CRSMANAGER, "CrsManagerPane variables Loaded");
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Supportive methods ----------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void downloadCRs() throws ParseException
	{
		model.resetErrorsCount();
		
		// Setup jira connection
		SharedObjs.addLogLine("Connecting to Jira ...");
		JiraSatApi jira = new JiraSatApi(JiraSatApi.DEFAULT_JIRA_URL, SharedObjs.getUser(), SharedObjs.getPass());
		
		// Get the CRs list
		model.setCRs(view.getTextDownload().replaceAll(" ", "").split("\n"));
		
		Logger.log(Logger.TAG_CRSMANAGER, "CRs List:" + model.getCRsCount());
		if (model.getCRsCount() == 0 || (model.getCRsCount() == 1 && !model.getCrAt(0).contains("-")))
		{
			SharedObjs.addLogLine("CRs list empty");
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
		
		SharedObjs.addLogLine("Acquiring " + model.getCRsCount() + " CRs data ...");
		SharedObjs.getCrsList().clear();
		
		// Manage CR
		int crsCount = 0;
		model.setIgnoredList(new CrItemsList());
		for (String crKey : model.getCRs())
		{
			crKey = trimCR(crKey);
			if (crKey.equals(""))
			{
				SharedObjs.addLogLine("CR list is empty");
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
						SharedObjs.addLogLine(crsCount + " - " + crKey + " - Will not be analyzed");
						model.addToIgnoredList(crItem);
					}
				}
				
				SharedObjs.addLogLine(crsCount + " - " + crKey + " - got it");
				
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
				SharedObjs.addLogLine("CR KEY: " + crKey + " seems not to exist. Or your user/password is wrong");
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
					SharedObjs.addLogLine("Generating download list ...");
				}
				else
				{
					SharedObjs.addLogLine("New b2g files added to download list ...");
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
				if (model.getIgnoredListSize() == model.getCRsCount())
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
	
	private String trimCR(String s)
	{
		s = s.replaceAll("\n", "");
		s = s.replaceAll("\r", "");
		s = s.replaceAll("\t", "");
		s = s.trim();
		return s;
	}
	
}
