package views.secondarypanes;


import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextPane;
import javax.swing.JTree;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import style.Icons;


public class HelpPane extends JFrame
{
	
	private JPanel                 contentPane;
	private JSplitPane             splitPane;
	private JTree                  tree;
	private DefaultTreeModel       treeModel;
	private DefaultMutableTreeNode treeRoot;
	private JPanel                 panel;
	private JTextPane textPane;
	
	/**
	 * Launch the application.
	 */
//	public static void main(String[] args)
//	{
//		EventQueue.invokeLater(new Runnable()
//		{
//			public void run()
//			{
//				try
//				{
//					HelpPane frame = new HelpPane();
//					frame.setVisible(true);
//				}
//				catch (Exception e)
//				{
//					e.printStackTrace();
//				}
//			}
//		});
//	}
	
	/**
	 * Create the frame.
	 */
	public HelpPane()
	{
		setTitle("Help");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1100, 850);
		setIconImage(Icons.help.getImage());
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);
		
		tree = new JTree();
		tree.setModel(new DefaultTreeModel(new DefaultMutableTreeNode("SAT")
		{
			{
				DefaultMutableTreeNode node_1;
				node_1 = new DefaultMutableTreeNode("General");
				node_1.add(new DefaultMutableTreeNode("Basic Setup"));
				node_1.add(new DefaultMutableTreeNode("How to use"));
				this.add(node_1);
				node_1 = new DefaultMutableTreeNode("Parser");
				node_1.add(new DefaultMutableTreeNode("Basics"));
				node_1.add(new DefaultMutableTreeNode("Filters Tree"));
				node_1.add(new DefaultMutableTreeNode("File Tree"));
				this.add(node_1);
				node_1 = new DefaultMutableTreeNode("Downloader");
				node_1.add(new DefaultMutableTreeNode("Basics"));
				node_1.add(new DefaultMutableTreeNode("Items Description"));
				this.add(node_1);
				node_1 = new DefaultMutableTreeNode("Options");
				node_1.add(new DefaultMutableTreeNode("User Data Setup"));
				node_1.add(new DefaultMutableTreeNode("Customizing Comments"));
				node_1.add(new DefaultMutableTreeNode("Parser Options"));
				node_1.add(new DefaultMutableTreeNode("Managing Filters"));
				node_1.add(new DefaultMutableTreeNode("Advanced"));
				this.add(node_1);
			}
		}));
		treeModel = (DefaultTreeModel) tree.getModel();
		treeRoot = (DefaultMutableTreeNode) treeModel.getRoot();
		tree.expandRow(1);
		tree.addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				DefaultMutableTreeNode element = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
				DefaultMutableTreeNode elementParent = (DefaultMutableTreeNode) element.getParent();
				
				textPane.setText("");
				String tab = "        ";
				StyledDocument doc = (StyledDocument) textPane.getDocument();
				
				SimpleAttributeSet normal = new SimpleAttributeSet();
				StyleConstants.setFontFamily(normal, "Tahoma");
				StyleConstants.setBold(normal, false);
		        StyleConstants.setFontSize(normal, 12);
		        
		        SimpleAttributeSet normalBold = new SimpleAttributeSet(normal);
		        StyleConstants.setBold(normalBold, true);
		        StyleConstants.setItalic(normalBold, true);

		        SimpleAttributeSet h1 = new SimpleAttributeSet(normal);
		        StyleConstants.setBold(h1, true);
		        StyleConstants.setFontSize(h1, 20);

		        SimpleAttributeSet h2 = new SimpleAttributeSet(h1);
		        StyleConstants.setFontSize(h2, 16);
		        StyleConstants.setItalic(h2, true);
				
		        if (element == null || elementParent == null || element.isRoot())
		        {
		        	try
                    {
	                    doc.insertString(doc.getLength(),"SAT Helping System\n\n", h1);
	                    doc.insertString(doc.getLength(),"Select an option on the left to start\n"
	                    				+ "General folder contains a brief how to configure and use SAT application.\n"
	                    				+ "Each other folder contains specific information about each SAT features packages", normal);
                    }
                    catch (BadLocationException e2)
                    {
	                    e2.printStackTrace();
                    }
		        }
		        
		        else if (element.getDepth() == 1)
		        {
		        	switch (treeRoot.getIndex(element))
    				{
    					case 0:
    						try
                            {
	                            doc.insertString(doc.getLength(),"General:\n\n", h1);
	                            doc.insertString(doc.getLength(),"In this folder you find a brief 'how to' configure and use SAT application.\n"
	                            				+ "Select a subitem to get more details.", normal);
                            }
                            catch (BadLocationException e1)
                            {
	                            e1.printStackTrace();
                            }
    						break;
    					
    					case 1:
    						try
                            {
	                            doc.insertString(doc.getLength(),"Parser:\n\n", h1);
	                            doc.insertString(doc.getLength(),"This folder contains detailed information about all functionalities found on 'Parser' tab.\n"
	                            				+ "Select a subitem to get more details.", normal);
                            }
                            catch (BadLocationException e1)
                            {
	                            e1.printStackTrace();
                            }
    						break;
    					
    					case 2:
    						try
                            {
	                            doc.insertString(doc.getLength(),"Downloader:\n\n", h1);
	                            doc.insertString(doc.getLength(),"This folder contains detailed information about all functionalities found on 'Downloder' tab.\n"
	                            				+ "Select a subitem to get more details.", normal);
                            }
                            catch (BadLocationException e1)
                            {
	                            e1.printStackTrace();
                            }
    						break;
    					
    					case 3:
    						try
                            {
	                            doc.insertString(doc.getLength(),"Options:\n\n", h1);
	                            doc.insertString(doc.getLength(),"This folder contains detailed information about all functionalities found on 'Options' tab.\n"
	                            				+ "Select a subitem to get more details.", normal);
                            }
                            catch (BadLocationException e1)
                            {
	                            e1.printStackTrace();
                            }
    						break;
    				}
		        }
		        
		        else
		        {
		        	switch (treeRoot.getIndex(elementParent))
    				{
    					case 0: // General
    						switch (elementParent.getIndex(element))
    						{
    							case 0:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Basic Setup\n\n", h1);
    									
    									doc.insertString(doc.getLength(),"User account:\n", h2);
    									doc.insertString(doc.getLength(), tab + "Go to 'Options' tab and setup your motorola coreid and password\n", normal);
    									doc.insertString(doc.getLength(), tab + "Now you are ready to download CRs from Jira and SAT is ready to analyze them all\n", normal);
    									
    									doc.insertString(doc.getLength(),"\nComments:\n", h2);
    									doc.insertString(doc.getLength(), tab + "You can customize the comments the way you want.\n"
    													+ "Use the '?' button to get help creating the comment\n", normal);
    									
    									doc.insertString(doc.getLength(),"\nFiltering information:\n", h2);
    									doc.insertString(doc.getLength(), tab + "You can click 'Manage Filters' button to create, delete, edit and share filters.\n", normal);
    									doc.insertString(doc.getLength(), tab + "These filters are shown on 'Parser' tab, get more information about how to use them on the left menu\n", normal);
    									
    									doc.insertString(doc.getLength(),"\nConfiguring new products:\n", h2);
    									doc.insertString(doc.getLength(), tab + "Click 'Advanced' button, in 'Options' panel, to add, remove or update products and its battery capacity.\n", normal);
    									
    									doc.insertString(doc.getLength(), "\n\nThis is the basic of SAT, and now you are ready to use it.\n"
    													+ "If you need more specific information, try the left options menu.\n\n"
    													+ "For extra support, you can mail cesarc@motorla.com or raise a CR on SAT project.", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    							case 1:
    								try
                                    {
    									doc.insertString(doc.getLength(),"How to use\n\n", h1);
    									
    									doc.insertString(doc.getLength(),"Acquiring CRs bug2go packages:\n", h2);
    									doc.insertString(doc.getLength(), tab + "Go to 'Downloader' tab. Configure the checkboxes and labels field\n", normal);
    									doc.insertString(doc.getLength(), tab + "Get a list of Jira IDs containing dogfood automatic raised CRs\n", normal);
    									doc.insertString(doc.getLength(), tab + "Paste this list into the white rectangle text box and click 'Get CRs' button\n", normal);
    									doc.insertString(doc.getLength(), tab + "When the process is finished, SAT will ask you about what to do next\n", normal);
    									doc.insertString(doc.getLength(), tab + "You have three options:\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "1- Unzip/PreAnalyse: SAT unzip bug2go packages into folders named according to bug2go ID, analyze each package and closes CRs selected by the pre-analyzer algorithm.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "2- Just unzip: SAT unzip bug2go packages into folders named according to bug2go ID\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "3- Nothing: As it says, SAT does nothing more.\n\n", normal);
    									
    									doc.insertString(doc.getLength(),"Analyzing a CR package:\n", h2);
    									doc.insertString(doc.getLength(), tab + "Go to 'Parser' tab and select a CR folder using 'File Tree' on the bottom-left menu.\n", normal);
    									doc.insertString(doc.getLength(), tab + "After doing that, you now should select the filters on top-left menu.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Results are shown on the text pane, on the right side.\n\n", normal);
    									doc.insertString(doc.getLength(), tab + "- Tip: You can type Ctrl + Shift + C to copy all text content of the result pane without selecting anything.\n\n\n", normal);
    									
    									doc.insertString(doc.getLength(), "For more detailed information about how to use each feature, fields and options, please, check the others help itens on the left menu of the HELP content", normal);
    									
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    						}
    						break;
    					
    					case 1: // Parser
    						switch (elementParent.getIndex(element))
    						{
    							case 0:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Basics\n\n", h1);
    									
    									doc.insertString(doc.getLength(),"Filters Tree:\n", h2);
    									doc.insertString(doc.getLength(), tab + "On your top left you see the Filters Tree\n", normal);
    									doc.insertString(doc.getLength(), tab + "Active filters are listed there, and cliking on an item triggers the filter action.\n", normal);
    									doc.insertString(doc.getLength(), tab + "There are default filters and custom filters. Custom filter icon is always a loupe\n", normal);
    									
    									
    									doc.insertString(doc.getLength(),"\nFile Tree:\n", h2);
    									doc.insertString(doc.getLength(), tab + "On your bottom left you see the File Tree.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Use this tree to select the CR directory to be analyzed.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Use right click to get more options.\n", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    							case 1:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Filters Tree\n\n", h1);

    									doc.insertString(doc.getLength(), tab + "This tree contains all active filters. Filters can be activated/deactived on Manage Filters, located on Options Tab.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Standard filters have specific icons and the custom filters is always a loupe icon.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Clicking on a filter will trigger its parser action. While running, filters tagName receive the tag 'Running'.\n", normal);
    									doc.insertString(doc.getLength(), tab + "After parser proccess is finished the filter is tagged as 'Done' or 'Error'\n\n", normal);
    									doc.insertString(doc.getLength(), tab + "So, what all those tags means?\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "- Running:\tFilter is being applied, parsing logs and trying to get the info.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "- Done: \tFilter was applied successfully. When click is received, just show the data it got.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "- Error: \tFiltering proccess went wrong. Mainly because a CR directory is not selected or the it does not contains the logs needed.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "\t\tThe error summary is shown on text area at the right side.\n\n", normal);
    									doc.insertString(doc.getLength(), tab + "If you would like to know how to add, remove, edit, active or deactive filters, please, go to Options help item.\n", normal);
    									doc.insertString(doc.getLength(), tab + "There you will get information about how to Manage filters and much more.\n", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    							case 2:
    								try
                                    {
    									doc.insertString(doc.getLength(),"File Tree\n\n", h1);

    									doc.insertString(doc.getLength(), tab + "This tree allows you to navigate through your PC file system, select files and directories.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Use this navigation tree to select a CR directory that will be used by the filters/parsers to get specific data for you.\n", normal);
    									doc.insertString(doc.getLength(), tab + "In order to filters works properly, you shall select a single CR directory at a time.\n\n", normal);
    									doc.insertString(doc.getLength(), tab + "To make the navigation easier and faster, this tree offers you some possibilities than can be accessed by right click menu.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Note that clicking with the right button some options are always there and some others are type dependend (directory or file).\n", normal);
    									doc.insertString(doc.getLength(), tab + "The general options are:\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "- Open: ", normalBold);
    									doc.insertString(doc.getLength(), "Triggers the default OS double click action.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "- Rename: ", normalBold);
    									doc.insertString(doc.getLength(), "Allows you to rename the directory/file.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "- Delete: ", normalBold);
    									doc.insertString(doc.getLength(), "Delete the directory/file.\n\n", normal);
    									
    									doc.insertString(doc.getLength(), tab + "If you click on an directory folder, you get some more options:\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "+ Open BTD:\n", normalBold);
    									doc.insertString(doc.getLength(), tab + tab + tab + "- Open latest BTD file: Open the latest BTD file located inside selected folder.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + tab + "- Open all BTD files: Open the all BTD files located inside selected folder.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "+ Root:\n", normalBold);
    									doc.insertString(doc.getLength(), tab + tab + tab + "- Select as root: Selects the actual folder as root of the file tree.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + tab + "- Select parent as root: Selects actual folder's parent as root of the tree.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + tab + "- Reset root: Select system root folder as root of the tree.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "- Set as download path: ", normalBold);
    									doc.insertString(doc.getLength(), "Put the actual folder path as download path (at Downloader tab).\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "- Build report: ", normalBold);
    									doc.insertString(doc.getLength(), "Build report output inside the folder.\n", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    						}
    						break;
    					
    					case 2: // Downloader
    						switch (elementParent.getIndex(element))
    						{
    							case 0:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Basics\n\n", h1);
    									
    									doc.insertString(doc.getLength(),"The basic proccess:\n", h2);
    									doc.insertString(doc.getLength(), tab + "Here you will se how to use the basic features of this tab. For more specific information, select 'Items Description' on Help menu.\n\n\n", normal);
    									doc.insertString(doc.getLength(), tab + "At first, make sure your username and password are correct. You can setup them on Options tab.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Setup your download path. Remember that you can do this using File tree on Parser tab as well.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Configure the checkboxes as your prefference. The default should be all marked/checked.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Add the labels on the textbox below label checkbox. Separete them using blankspace(' ').\n", normal);
    									doc.insertString(doc.getLength(), tab + "Put a list of Jira IDs and on the blank space.", normal);
    									doc.insertString(doc.getLength(), " One CR ID per line.\n", normalBold);
    									doc.insertString(doc.getLength(), tab + "Now, you can click 'Get CRs' button, the first button on the left.\n", normal);
    									doc.insertString(doc.getLength(), tab + "After downloading all the CRs, SAT will ask you about what to do next. You have 3 options:\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "1- Unzip/PreAnalyse: SAT unzip bug2go packages into folders named according to bug2go ID, analyze each package and closes CRs selected by the pre-analyzer algorithm.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "2- Just unzip: SAT unzip bug2go packages into folders named according to bug2go ID\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "3- Nothing: As it says, SAT does nothing more.\n\n", normal);
    									
    									doc.insertString(doc.getLength(), tab + "Now you are all set to begin the analysis using the parser tab.\n", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    							case 1:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Items Description\n\n", h1);

    									doc.insertString(doc.getLength(), "CRs List:\n", h2);
    									doc.insertString(doc.getLength(), tab + "CRs List: ", normalBold);
    									doc.insertString(doc.getLength(), "Put there the CR IDs to be downloaded. Put one CR ID per line (use 'Enter').\n", normal);
    									doc.insertString(doc.getLength(), tab + "Clear Button: ", normalBold);
    									doc.insertString(doc.getLength(), "Clear the CRs list textbox.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Paste Button: ", normalBold);
    									doc.insertString(doc.getLength(), "Paste your clipboard on the CRs list textbox\n\n", normal);
    									
    									doc.insertString(doc.getLength(), "Action Options:\n", h2);
    									doc.insertString(doc.getLength(), tab + "Assign checkbox: ", normalBold);
    									doc.insertString(doc.getLength(), "If marked, SAT will assign to you the listed CRs.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Unassign checkbox: ", normalBold);
    									doc.insertString(doc.getLength(), "If marked, SAT will unassign the CRs on the list.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Add Labels checkbox: ", normalBold);
    									doc.insertString(doc.getLength(), "If marked, SAT will add the specified labels on the CRs on the list.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Remove Labels checkbox: ", normalBold);
    									doc.insertString(doc.getLength(), "If marked, SAT will remove the specified labels from the CRs on the list. ", normal);
    									doc.insertString(doc.getLength(), "Labels must be separated by a blankspace!\n", normalBold);
    									doc.insertString(doc.getLength(), tab + "Download: ", normalBold);
    									doc.insertString(doc.getLength(), "Mark this option to SAT download bug2go packages from the CRs on the list.\n"
    													+ tab + tab + "Set your download folder using the textfield and make sure the folder already exists.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "Clicking with right button on a folder inside the File tree (at Parser tab) you can set that folder as your download path.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Unzip checkbox: ", normalBold);
    									doc.insertString(doc.getLength(), "If marked, SAT will unzip downloaded CRs.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Analyze checkbox: ", normalBold);
    									doc.insertString(doc.getLength(), "If marked, SAT will analyze downloaded CRs.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Close as old checkbox: ", normalBold);
    									doc.insertString(doc.getLength(), "Close all CR on the list as old. Use it to close CRs out of date.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Exec button: ", normalBold);
    									doc.insertString(doc.getLength(), "Build and start a proccess based on options selected.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Open on Browser button: ", normalBold);
    									doc.insertString(doc.getLength(), "Opens the list of CRs on your default browser.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Show Results button: ", normalBold);
    									doc.insertString(doc.getLength(), "Open the lists containing closed and unclosed CRs. The data is generated after an analysis proccess.\n\n", normal);
    									
    									doc.insertString(doc.getLength(), "Logging area:\n", h2);
    									doc.insertString(doc.getLength(), tab + "The area red bounded is the logging area. There you can see what is happening during the download proccess.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Some other informations are shown there too, but it is focused on 'Get CRs' task button proccess.\n", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    						}
    						break;
    					
    					case 3: // Options
    						switch (elementParent.getIndex(element))
    						{
    							case 0:
    								try
                                    {
    									doc.insertString(doc.getLength(),"User data\n\n", h1);
    									
    									doc.insertString(doc.getLength(), tab + "Use to setup your user credencials.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Put your 'coreid' into username textfield.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Put your 'password' into password textfield.\n\n", normal);
    									
    									doc.insertString(doc.getLength(), tab + "All done.\n", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    							case 1:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Customizing Comments\n\n", h1);

    									doc.insertString(doc.getLength(), tab + "Use that group to customize the comments that SAT generates.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Note that the names of the subgroups are the of the standard filters on the Filters tree (Parser tab).\n", normal);
    									doc.insertString(doc.getLength(), tab + "It means that each subgroup is dedicated to configure a specific standard filter.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Click on the '?' button in front of each subgroup to get specific help about how to configure each one.\n", normal);
    									
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    							case 2:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Parser Options\n\n", h1);

    									doc.insertString(doc.getLength(), tab + "Use that group to set your prefferences on the Parser tab.\n\n", normal);
    									doc.insertString(doc.getLength(), tab + "You can select your default text editor.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Active or deactive Word Wrap option.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Change click count for tree breakdown.\n", normal);
    									
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    							case 3:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Manage Filters\n\n", h1);

    									doc.insertString(doc.getLength(), tab + "On the Options panel you can locate the 'Manage Filters' button. Click to open the Filters Manager.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Once you click the button, the filter manager is opened and you see two tabs:\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "My Filters: ", normalBold);
    									doc.insertString(doc.getLength(), "This tab allows you to create, delete, edit, (de)active and share filters.\n", normal);
    									doc.insertString(doc.getLength(), tab + tab + "Shared Filters: ", normalBold);
    									doc.insertString(doc.getLength(), "This tab allows you to active and deactive fitlers shared by other users. If a filter is public (green text) you can edit them as well.\n", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    							case 4:
    								try
                                    {
    									doc.insertString(doc.getLength(),"Advanced Options\n\n", h1);

    									doc.insertString(doc.getLength(), tab + "Here you can Add products and its battery capacity.\n", normal);
    									doc.insertString(doc.getLength(), tab + "The battery capacity is used to generate de report-output.\n", normal);
    									doc.insertString(doc.getLength(), tab + "You cannot remove the products using the interface by now, but it will be possible.\n", normal);
    									doc.insertString(doc.getLength(), tab + "Anyway, if you really want to do it, you can edit the user_cfg.xml file manually, it is located inside Data/cfgs folder\n", normal);
                                    }
                                    catch (BadLocationException e1)
                                    {
    	                                e1.printStackTrace();
                                    }
    								break;
    						}
    						break;
    				}
		        }
			}
		});
		
		splitPane.setLeftComponent(tree);
		
		panel = new JPanel();
		splitPane.setRightComponent(panel);
		GridBagLayout gbl_panel = new GridBagLayout();
		gbl_panel.columnWidths = new int[]{401, 0};
		gbl_panel.rowHeights = new int[]{20, 0};
		gbl_panel.columnWeights = new double[]{1.0, Double.MIN_VALUE};
		gbl_panel.rowWeights = new double[]{1.0, Double.MIN_VALUE};
		panel.setLayout(gbl_panel);
		
		textPane = new JTextPane();
		textPane.setEditable(false);
		GridBagConstraints gbc_textPane = new GridBagConstraints();
		gbc_textPane.fill = GridBagConstraints.BOTH;
		gbc_textPane.gridx = 0;
		gbc_textPane.gridy = 0;
		panel.add(textPane, gbc_textPane);
		splitPane.setDividerLocation(160);
	}
	
}
