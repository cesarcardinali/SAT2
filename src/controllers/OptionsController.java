package controllers;


import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

import models.OptionsModel;
import supportive.Encryptation;
import views.OptionsPane;
import core.Logger;
import core.SharedObjs;
import core.XmlMngr;


public class OptionsController
{
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Variables -------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	OptionsPane    view;
	OptionsModel   model;
	FocusListener  userDataFocusListener;
	MouseListener  collapseExtendMouseListener;
	ActionListener helpBtnsActionListener;
	ItemListener   chkbxItemsListener;
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// Initialize controller -------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void startController(OptionsPane view, OptionsModel model)
	{
		// Set view/model
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
		userDataFocusListener = new FocusListener()
		{
			@Override
			public void focusLost(FocusEvent e)
			{
				view.updateUserdata();
			}
			
			@Override
			public void focusGained(FocusEvent e)
			{
				// Do nothing
			}
		};
		
		collapseExtendMouseListener = new MouseListener()
		{
			@Override
			public void mouseReleased(MouseEvent e)
			{
			}
			
			@Override
			public void mousePressed(MouseEvent e)
			{
			}
			
			@Override
			public void mouseExited(MouseEvent e)
			{
			}
			
			@Override
			public void mouseEntered(MouseEvent e)
			{
			}
			
			@Override
			public void mouseClicked(MouseEvent e)
			{
				if (e.getSource().getClass().getSimpleName().equals("JLabel"))
				{
					JLabel clickedLabel = (JLabel) e.getSource();
					System.out.println(clickedLabel.getText());
					
					Boolean visibility = view.getCommentsPanelVisibility();
					view.setCommentsPanelVisibility(!view.getCommentsPanelVisibility());
					
					if (!visibility == true)
					{
						view.setCommentsIcon(new ImageIcon("Data\\pics\\collapse.png"));
					}
					else
					{
						view.setCommentsIcon(new ImageIcon("Data\\pics\\expand.png"));
					}
				}
			}
		};
		
		helpBtnsActionListener = new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				String actionSourceClass = e.getSource().getClass().getSimpleName();
				System.out.println(actionSourceClass);
				
				if (actionSourceClass.equals("JButton"))
				{
					JButton btn = (JButton) e.getSource();
					String tooltip = btn.getToolTipText();
					System.out.println(tooltip);
					
					if (tooltip.contains("consumption"))
					{
						view.showPopUp("#pname#: Process name\n#avgconsume#: Average process CPU consumption\n"
						               + "#scoffconsume#: Average consumption while screen off\n" + "#sconconsume#: Average consumption while screen on\n"
						               + "#logfull#: Show complete log results\n#logoff#: Log lines while screen off\n"
						               + "#logon#: Log lines while screen on\n");
					}
					
					else if (tooltip.contains("wakelocks"))
					{
						view.showPopUp("Edit headers from \"SystemPM\" filter result");
					}
					
					else if (tooltip.contains("suspicious"))
					{
						view.showPopUp("#pname#*: Process name\n#tag#: Process tag held\n#duration#: Wakelock duration\n#log#: Android logs");
					}
					
					else if (tooltip.contains("alarms"))
					{
						view.showPopUp("#pname#*: Process name\n#log#: Android logs");
					}
					
					else if (tooltip.contains("bug2go"))
					{
						view.showPopUp("#log#: Android logs");
					}
					
					else if (tooltip.contains("tethering"))
					{
						view.showPopUp("#log#: Android logs");
					}
					
					else if (tooltip.contains("Diag"))
					{
						view.showPopUp("#log#: Android logs\n#dupcr#: Duplicate CR ID");
					}
					
					else if (tooltip.contains("manage parser tab filters"))
					{
						SharedObjs.getCustomFiltersPane().open();
					}
					
					else if (tooltip.contains("see advanced options"))
					{
						SharedObjs.advOptions.setLocationRelativeTo(SharedObjs.satFrame);
						SharedObjs.advOptions.setVisible(true);
					}
				}
				
				else if (actionSourceClass.equals("JRadioButton"))
				{
					JRadioButton rdBtn = (JRadioButton) e.getSource();
					String tooltip = rdBtn.getToolTipText();
					System.out.println(tooltip);
					
					if (tooltip.contains("Double click"))
					{
						SharedObjs.parserPane.getFiltersTree().setToggleClickCount(2);
					}
					
					else if (tooltip.contains("Single click"))
					{
						SharedObjs.parserPane.getFiltersTree().setToggleClickCount(1);
					}
				}
			}
		};
		
		chkbxItemsListener = new ItemListener()
		{
			@Override
			public void itemStateChanged(ItemEvent e)
			{
				String actionSourceClass = e.getSource().getClass().getSimpleName();
				System.out.println(actionSourceClass);
				
				if (actionSourceClass.equals("JCheckBox"))
				{
					JCheckBox btn = (JCheckBox) e.getSource();
					String tooltip = btn.getToolTipText();
					System.out.println(tooltip);
					
					if (tooltip.contains("Wrap text"))
					{
						if (e.getStateChange() == ItemEvent.SELECTED)
							SharedObjs.parserPane.setResultsTxtPaneTextWrap(true);
						else
							SharedObjs.parserPane.setResultsTxtPaneTextWrap(false);
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
		view.textUsernameAddFocusListener(userDataFocusListener);
		view.textPasswordAddFocusListener(userDataFocusListener);
		view.chcbxRememberAddFocusListener(userDataFocusListener);
		
		view.commentsIconLabelsAddMouseListener(collapseExtendMouseListener);
		view.commentsTextLabelsAddMouseListener(collapseExtendMouseListener);
		
		view.chkTextWrapAddItemListener(chkbxItemsListener);
		
		view.btnConsumeHelpAddActionListener(helpBtnsActionListener);
		view.btnSystemPMHelpAddActionListener(helpBtnsActionListener);
		view.btnSuspiciousHelpAddActionListener(helpBtnsActionListener);
		view.btnAlarmsHelpAddActionListener(helpBtnsActionListener);
		view.btnB2gHelpAddActionListener(helpBtnsActionListener);
		view.btnTetherHelpAddActionListener(helpBtnsActionListener);
		view.btnDiagHelpAddActionListener(helpBtnsActionListener);
		view.btnManageFiltersAddActionListener(helpBtnsActionListener);
		view.btnMoreOptionsAddActionListener(helpBtnsActionListener);
		view.rdbtnSingleclickAddActionListener(helpBtnsActionListener);
		view.rdbtnDoubleAddActionListener(helpBtnsActionListener);
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View options initialization -------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	private void initializeViewItens()
	{
		Logger.log(Logger.TAG_OPTIONS, "Loading option pane values data ...");
		loadViewUserDataTexts();
		loadViewCommentsTexts();
		loadViewParserOptions();
		Logger.log(Logger.TAG_OPTIONS, "Option pane values loaded");
	}
	
	private void loadViewUserDataTexts()
	{
		String uName = XmlMngr.getUserValueOf(new String[] {"option_pane", "uname"});
		view.setTextUsername(uName);
		if (uName.equals("user") || uName.equals("null") || uName.equals(""))
		{
			view.setTextUsername(SharedObjs.getUser());
		}
		else
		{
			SharedObjs.setUser("" + uName);
		}
		
		try
		{
			BufferedInputStream bin;
			bin = new BufferedInputStream(new FileInputStream(SharedObjs.pwdFile));
			String encrypt_len = XmlMngr.getUserValueOf(new String[] {"option_pane", "encrypt_len"});
			if (encrypt_len.equals("null"))
				encrypt_len = "0";
			
			byte[] toDecrypt = new byte[Integer.parseInt(encrypt_len)];
			bin.read(toDecrypt);
			bin.close();
			String decryptedPass = Encryptation.decrypt(toDecrypt);
			if (!decryptedPass.equals(""))
			{
				SharedObjs.setPass(decryptedPass);
			}
			view.setTextPassword(decryptedPass);
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
		}
	}
	
	private void loadViewCommentsTexts()
	{
		view.setTextConsumeFull(XmlMngr.getUserValueOf(new String[] {"option_pane", "full_log"}));
		view.setTextConsumeOff(XmlMngr.getUserValueOf(new String[] {"option_pane", "screen_off"}));
		view.setTextConsumeOn(XmlMngr.getUserValueOf(new String[] {"option_pane", "screen_on"}));
		view.setTextHighCurrent(XmlMngr.getUserValueOf(new String[] {"option_pane", "high_current"}));
		view.setTextKernel(XmlMngr.getUserValueOf(new String[] {"option_pane", "krnl_wkl"}));
		view.setTextJava(XmlMngr.getUserValueOf(new String[] {"option_pane", "java_wkl"}));
		view.setTextSuspiciousHeader(XmlMngr.getUserValueOf(new String[] {"option_pane", "suspicious_header"}));
		view.setTextSuspicious(XmlMngr.getUserValueOf(new String[] {"option_pane", "suspicious"}));
		view.setTextAlarms(XmlMngr.getUserValueOf(new String[] {"option_pane", "alarms"}));
		view.setTextB2g(XmlMngr.getUserValueOf(new String[] {"option_pane", "b2g"}));
		view.setTextTether(XmlMngr.getUserValueOf(new String[] {"option_pane", "tether"}));
		view.setTextDiag(XmlMngr.getUserValueOf(new String[] {"option_pane", "diag"}));
	}
	
	private void loadViewParserOptions()
	{
		System.out.println("-" + XmlMngr.getUserValueOf(new String[] {"option_pane", "tree_breakdown"}) + "-");
		if (XmlMngr.getUserValueOf(new String[] {"option_pane", "tree_breakdown"}).equals("1"))
		{
			SharedObjs.parserPane.getFiltersTree().setToggleClickCount(1);
			view.setRdBtnSingleClickSelected(true);
			view.setRdBtnSingleClickSelected(false);
		}
		else
		{
			SharedObjs.parserPane.getFiltersTree().setToggleClickCount(2);
			view.setRdBtnDoubleClickSelected(true);
			view.setRdBtnSingleClickSelected(false);
		}
		
		if (XmlMngr.getUserValueOf(new String[] {"option_pane", "wwrap"}).equals("0"))
		{
			view.setChkTextWrapSelected(false);
		}
		else
		{
			view.setChkTextWrapSelected(true);
		}
		
		if (XmlMngr.getUserValueOf(new String[] {"option_pane", "editor"}).equals("0"))
		{
			view.setRdBtnTAnalisysSelected(true);
			view.setRdBtnNotepadSelected(false);
		}
		else
		{
			view.setRdBtnTAnalisysSelected(false);
			view.setRdBtnNotepadSelected(true);
		}
	}
	
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// View options saving ---------------------------------------------------------------------------------------------------------------------------------------------------------------------
	// -----------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------
	public void saveUIData()
	{
		Logger.log(Logger.TAG_OPTIONS, "Saving options pane values ...");
		
		saveUserDataOptions();
		saveCommentsTexts();
		saveParserOptions();
		
		Logger.log(Logger.TAG_OPTIONS, "Options pane values saved!");
	}
	
	private void saveParserOptions()
	{
		if (view.isRdBtnNotepadSelected())
			XmlMngr.setUserValueOf(new String[] {"option_pane", "editor"}, "1");
		else
			XmlMngr.setUserValueOf(new String[] {"option_pane", "editor"}, "0");
		
		if (view.isChkTextWrapSelected())
			XmlMngr.setUserValueOf(new String[] {"option_pane", "wwrap"}, "1");
		else
			XmlMngr.setUserValueOf(new String[] {"option_pane", "wwrap"}, "0");
		
		if (view.isRdBtnSingleClickSelected())
			XmlMngr.setUserValueOf(new String[] {"option_pane", "tree_breakdown"}, "1");
		else
			XmlMngr.setUserValueOf(new String[] {"option_pane", "tree_breakdown"}, "2");
	}
	
	private void saveUserDataOptions()
	{
		XmlMngr.setUserValueOf(new String[] {"option_pane", "uname"}, view.getTextUsername());
		
		try
		{
			BufferedOutputStream bout;
			bout = new BufferedOutputStream(new FileOutputStream(SharedObjs.pwdFile));
			byte[] encPass = Encryptation.encrypt(view.getTextPassword());
			bout.write(encPass);
			bout.close();
			
			XmlMngr.setUserValueOf(new String[] {"option_pane", "encrypt_len"}, "" + encPass.length);
		}
		catch (Exception e2)
		{
			e2.printStackTrace();
		}
	}
	
	private void saveCommentsTexts()
	{
		XmlMngr.setUserValueOf(new String[] {"option_pane", "full_log"}, view.getTextConsumeFull());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "screen_off"}, view.getTextConsumeOff());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "screen_on"}, view.getTextConsumeOn());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "high_current"}, view.getTextHighCurrent());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "krnl_wkl"}, view.getTextKernel());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "java_wkl"}, view.getTextJava());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "suspicious_header"}, view.getTextSuspiciousHeader());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "suspicious"}, view.getTextSuspicious());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "alarms"}, view.getTextAlarms());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "b2g"}, view.getTextB2g());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "tether"}, view.getTextTether());
		XmlMngr.setUserValueOf(new String[] {"option_pane", "diag"}, view.getTextDiag());
	}
	
}
