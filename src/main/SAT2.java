package main;


import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JSeparator;

import org.apache.commons.io.FileUtils;

import style.Icons;
import supportive.AppsChecker;
import views.secondarypanes.ChangelogPane;
import views.secondarypanes.HelpPane;
import core.Logger;
import core.SharedObjs;
import core.Strings;
import core.XmlMngr;


/**
 * Main class. Generates the UI Frame
 */
@SuppressWarnings("serial")
public class SAT2 extends JFrame
{
	/**
	 * Variables
	 */
	private boolean    updating = false; // Checking for update
	private SAT2       satPane;
	private Thread     onShutdown;
	private JMenuBar   menuBar;
	private JMenu      mnSAT;
	private JMenuItem  mntmExit;
	private JSeparator separator;
	private JMenu      mnHelp;
	private JMenuItem  mntmHelpContent;
	private JMenuItem  mntmAbout;
	private JMenuItem  mntmChangelog;
	private JSeparator separator_1;
	
	/**
	 * Runnable
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{	
				// Initialize all static classes
				XmlMngr.initClass();
				Logger.initClass();
				SharedObjs.initClass();
				AppsChecker.initClass();

				// Start UI
				SharedObjs.satFrame.setVisible(true);
			}
		});
	}
	
	/**
	 * Configure application.
	 */
	public SAT2()
	{
		// Initializing window
		setIconImage(Icons.iconSat);
		setTitle(Strings.getToolName() + " " + Strings.getToolVersion());
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
		double width = screenSize.getWidth();
		double height = screenSize.getHeight();
		setBounds((int) (width / 3), 0, (int) (width / 1.5), (int) height - 40);
		setVisible(true);
		setMinimumSize(new Dimension(800, 700));
		
		// Inserting the TabPane
		getContentPane().add(SharedObjs.tabbedPane);
		
		satPane = this;
		
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		mnSAT = new JMenu("SAT");
		mnSAT.setMnemonic('s');
		menuBar.add(mnSAT);
		
		separator = new JSeparator();
		mnSAT.add(separator);
		
		mntmExit = new JMenuItem("Exit");
		mntmExit.setMnemonic('e');
		mntmExit.setIcon(Icons.remove);
		mntmExit.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				System.exit(1);
			}
		});
		mnSAT.add(mntmExit);
		
		mnHelp = new JMenu("Help");
		mnHelp.setMnemonic('h');
		menuBar.add(mnHelp);
		
		mntmHelpContent = new JMenuItem("Help Content");
		mntmHelpContent.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				HelpPane hp = new HelpPane();
				hp.setLocationRelativeTo(satPane);
				hp.setVisible(true);
			}
		});
		mntmHelpContent.setMnemonic('h');
		mntmHelpContent.setIcon(Icons.help);
		mnHelp.add(mntmHelpContent);
		
		mntmChangelog = new JMenuItem("Changelog");
		mntmChangelog.setMnemonic('c');
		mntmChangelog.setIcon(Icons.changelog);
		mntmChangelog.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				ChangelogPane clPane = new ChangelogPane();
				clPane.setLocationRelativeTo(satPane);
				clPane.setVisible(true);
			}
		});
		mnHelp.add(mntmChangelog);
		
		separator_1 = new JSeparator();
		mnHelp.add(separator_1);
		
		mntmAbout = new JMenuItem("About SAT");
		mntmAbout.setMnemonic('a');
		mntmAbout.setIcon(Icons.aboutus);
		mnHelp.add(mntmAbout);
		
		// Save configurations on close
		onShutdown = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				int run = 0;
				
				while (run == 0)
				{
					Logger.log(Logger.TAG_SAT, "Saving all user data ...");
					SharedObjs.crsManagerController.saveUIData();
					SharedObjs.parserController.saveUIData();
					SharedObjs.optionsController.saveUIData();
					SharedObjs.updateUidsXML();
					run = 1;
					Logger.log(Logger.TAG_SAT, "Done");
					Logger.log(Logger.TAG_SAT, "Trying to update SAT");
					checkForUpdate();
				}
				
				XmlMngr.closeXmls();
				Logger.close();
			}
		});
		Runtime.getRuntime().addShutdownHook(onShutdown);
		
		// Start updater thread
		updateThread();
	}
	
	/**
	 * Keep looking for new updates
	 */
	private void updateThread()
	{
		new Thread(new Runnable()
		{
			int stop = 0;
			
			@Override
			public void run()
			{
				stop = checkForUpdate();
				while (stop == 0)
				{
					try
					{
						Thread.sleep(600000); // Check for update each 15 minutes
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
					stop = checkForUpdate();
				}
				if (stop == 1)
				{
					Logger.log(Logger.TAG_SAT, "\n\nExiting");
					System.exit(0);
				}
			}
		}).start();
	}
	
	/**
	 * Check for newer version
	 */
	@SuppressWarnings("resource")
	public int checkForUpdate()
	{
		Logger.log(Logger.TAG_SAT, "Checking for update");
		
		updating = true;
		long dateRemote = 0, dateLocal = 0;
		
		File f1;
		File f2;
		
		f1 = new File(SharedObjs.updateFolder1 + "/" + Strings.getToolFileName());
		Logger.log(Logger.TAG_SAT, "Remote file: " + f1.getAbsolutePath() + " - Modified: " + new Date(f1.lastModified()));
		
		f2 = new File(Strings.getToolFileName());
		Logger.log(Logger.TAG_SAT, "Local file: " + f2.getAbsolutePath() + " - Modified: " + new Date(f2.lastModified()));
		
		dateRemote = f1.lastModified();
		dateLocal = f2.lastModified();
		
		if (dateLocal < dateRemote && dateLocal != 0)
		{
			Boolean mandatory = false;
			String message = null;
			String currentLine;
			
			try
			{
				System.out.println("Updater file: " + SharedObjs.updateFolder1 + "/Data/update/update.cfg");
				BufferedReader br = new BufferedReader(new FileReader(SharedObjs.updateFolder1 + "/Data/update/update.cfg"));
				
				while ((currentLine = br.readLine()) != null)
				{
					if (currentLine.contains("message="))
					{
						message = currentLine.replace("message=", "").replace("\\n", "\n") + "\n\nDo you want to update now?";
					}
					else if (currentLine.contains("mandatory="))
					{
						mandatory = Boolean.parseBoolean(currentLine.replace("mandatory=", ""));
					}
				}
			}
			catch (FileNotFoundException e)
			{
				e.printStackTrace();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			
			Object[] options = new Object[] {"Yes", "No"};
			int n = JOptionPane.showOptionDialog(null, message, XmlMngr.getMessageValueOf(new String[] {"tittles", "new_version"}),
			                                     JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
			
			if (n == 0)
			{
				doUpdate();
				
				return 1;
			}
			else
			{
				if (mandatory)
				{
					n = JOptionPane.showOptionDialog(null,
					                                 "Please, advise.\nThis is a really important update and you should consider to accept it as soon as possible."
					                                                 + "\n\nUpdate now?", XmlMngr.getMessageValueOf(new String[] {"tittles", "new_version"}),
					                                 JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
					
					if (n == 0)
					{
						doUpdate();
						
						return 1;
					}
				}
				
				return 2;
			}
		}
		else
		{
			Logger.log(Logger.TAG_SAT, "SAT is up to date");
		}
		
		return 0;
		
	}
	
	// Do Update
	private void doUpdate()
	{
		try
		{
			Logger.log(Logger.TAG_SAT, "Updating the Updater first, from: " + SharedObjs.updateFolder1);
			FileUtils.copyFile(new File(SharedObjs.updateFolder1 + "/" + Strings.getUpdaterFileName()), new File(Strings.getUpdaterFileName()));
		}
		catch (IOException e)
		{
			Logger.log(Logger.TAG_SAT, "Updating the Updater failed");
			e.printStackTrace();
		}
		
		Logger.log(Logger.TAG_SAT, "Updating");
		
		try
		{
			Logger.log(Logger.TAG_SAT, "path: " + new File("").getAbsolutePath());
			ProcessBuilder builder = new ProcessBuilder("cmd.exe", "/c", "cd " + new File("").getAbsolutePath() + " && java -jar "
			                                                             + Strings.getUpdaterFileName());
			builder.start();
		}
		catch (IOException e2)
		{
			e2.printStackTrace();
		}
	}
	
	// Getters and Setters:
	public boolean isUpdating()
	{
		return updating;
	}
	
	public void setUpdating(boolean is)
	{
		updating = is;
	}
}