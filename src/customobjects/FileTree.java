package customobjects;


import java.awt.Color;
import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;

import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeWillExpandListener;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import style.FileTreeNodeRenderer;
import style.Icons;
import supportive.CrChecker;
import supportive.UnZip;
import views.secondarypanes.ProgressDialog;
import core.Logger;
import core.SharedObjs;


@SuppressWarnings("serial")
public class FileTree extends JPanel
{
	private JTree                  fileTree;
	private JScrollPane            scrollPaneTree;
	private DefaultMutableTreeNode root;
	private FileSystemView         fileSystemView;
	private String                 lastDirectory;
	
	// File Tree constructor. It will initialize the file tree
	public FileTree()
	{
		setBorder(null);
		setPreferredSize(new Dimension(1000, 800));
		setLocation(350, 200);
		root = new DefaultMutableTreeNode();
		fileSystemView = FileSystemView.getFileSystemView();
		
		// initializing rootFolderPath variable
		initRootFolder();
		buildTree();
		lastDirectory = "";
		fileTree = new JTree(root);
		fileTree.setBorder(null);
		
		// Select the first child of the root node
		fileTree.setSelectionPath(new TreePath(root.getFirstChild()));
		
		// Node selection listener.
		fileTree.addTreeSelectionListener(new TreeSelectionListener()
		{
			// When user clicks on the folder, the files from that folder will be loaded
			public void valueChanged(TreeSelectionEvent e)
			{
				// BaseWindow.getParser().rootNode.removeAllChildren(); // reseting the result tree
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
				
				if (node != null)
				{
					File file = (File) node.getUserObject();
					if (file.isDirectory())
					{
						if (file.listFiles().length > 0)
							initializeNode(node);
						if (isCrNode(node))
						{
							if (!lastDirectory.equals(node.toString()))
							{
								lastDirectory = node.toString();
								SharedObjs.setCrPath(file.getAbsolutePath() + "\\");
								SharedObjs.parserPane.clearPane();
							}
						}
					}
				}
				
				// Calling updateUI causes the tree's UI to be
				// uninstalled and then a new one installed. The
				// developer
				// is calling this method in the middle of events
				// being dispatched, some of which go to the UI.
				// Because
				// the events are already being dispatched, they will
				// still go to the uninstalled UI which no longer has
				// reference to the tree. If the developer really
				// wants this behavior, they can wrap the call to
				// updateUI
				// in a SwingUtilities.invokeLater(). This method
				// allows us to post a "job" to Swing, which it will
				// then
				// run on the event dispatch thread at its next
				// convenience.
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						// Here, we can safely update the GUI
						// because we'll be called from the
						// event dispatch thread
						fileTree.updateUI();
					}
				});
			}
		});
		
		fileTree.addTreeWillExpandListener(new TreeWillExpandListener()
		{
			@Override
			public void treeWillCollapse(TreeExpansionEvent arg0) throws ExpandVetoException
			{
			}
			
			@Override
			public void treeWillExpand(TreeExpansionEvent arg0) throws ExpandVetoException
			{
				DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
				initializeNode(node);
				
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						// Here, we can safely update the GUI
						// because we'll be called from the
						// event dispatch thread
						fileTree.updateUI();
					}
				});
			}
		});
		
		// Mouse listener
		fileTree.addMouseListener(new MouseAdapter()
		{
			public void mouseClicked(MouseEvent event)
			{
				// if left click, double click opens the file with the default system program
				if (event.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(event))
				{
					DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
					openFile((File) nodeSelected.getUserObject());
				}
				
				// if right click was detected, get the node where the right click happened
				if (SwingUtilities.isRightMouseButton(event) && fileTree.getPathForLocation(event.getX(), event.getY()) != null)
				{
					if (fileTree.getSelectionPaths() == null || fileTree.getSelectionPaths().length == 1)
					{
						fileTree.setSelectionPath(fileTree.getPathForLocation(event.getX(), event.getY()));
						// DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
						// File file = (File) node.getUserObject();
					}
					getPopup(event.getX(), event.getY());
				}
			}
		});
		
		// Key listener
		fileTree.addKeyListener(new KeyAdapter()
		
		{
			public void keyPressed(KeyEvent event)
			{
				if (event.getKeyCode() == KeyEvent.VK_DELETE)
				{
					deleteFilesSelected();
				}
			}
		});
		
		setLayout(new GridLayout(0, 1, 0, 0));
		fileTree.setCellRenderer(new FileTreeNodeRenderer());
		fileTree.expandRow(1);
		fileTree.setRootVisible(false);
		fileTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		scrollPaneTree = new JScrollPane(fileTree);
		scrollPaneTree.setBorder(new LineBorder(Color.LIGHT_GRAY));
		scrollPaneTree.setPreferredSize(new Dimension(1080, 780));
		add(scrollPaneTree);
	}
	
	// Initialize the node with with files from the folder
	public void initializeNode(DefaultMutableTreeNode node)
	{
		if (node != null)
		{
			File file = (File) node.getUserObject();
			node.removeAllChildren();
			
			for (File f : file.listFiles())
			{
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(f);
				node.add(newNode);
			}
		}
	}
	
	// Open file with the default system program
	public void openFile(File file)
	{
		try
		{
			if (file.isFile())
				Desktop.getDesktop().open(file);
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	// Creates the popup menu
	public JPopupMenu getPopup(int posX, int posY)
	{
		JPopupMenu popup = new JPopupMenu();
		JMenuItem open = new JMenuItem("Open");
		JMenuItem delete = new JMenuItem("Delete");
		JMenuItem rename = new JMenuItem("Rename");
		JMenuItem setDownloadPath = new JMenuItem("Set as download path");
		
		open.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				TreePath paths[] = fileTree.getSelectionPaths();
				
				for (TreePath p : paths)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
					try
					{
						Desktop.getDesktop().open((File) node.getUserObject());
					}
					catch (IOException e)
					{
						e.printStackTrace();
					}
				}
			}
		});
		
		delete.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				deleteFilesSelected();
			}
		});
		
		rename.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent event)
			{
				TreePath paths[] = fileTree.getSelectionPaths();
				if (paths.length > 1)
				{
					JOptionPane.showMessageDialog(null, "Can't rename more than one file", "Can't rename", JOptionPane.ERROR_MESSAGE);
				}
				else
				{
					File oldName = new File(fileTree.getLastSelectedPathComponent().toString());
					// C:\dir1\dir2\dir3\oldName --> C:\dir1\dir2\dir3
					String newNameParentDirs = oldName.getParent();
					String extension = "";
					if (oldName.getName().contains("."))
						extension = oldName.getName().split("\\.")[1]; // aaaa.zip --> zip else Selected item doesn't have an extension
						
					// Asking user for a new name
					String newNameString = JOptionPane.showInputDialog(null, "Insert a new name for " + "the file:", "New name", JOptionPane.PLAIN_MESSAGE);
					if (newNameString != null)
					{ // newNameString == null --> user cancelled dialog
						File newName = new File(newNameParentDirs + "\\" + newNameString + "." + extension);
						
						try
						{
							if (!oldName.renameTo(newName)) // renameTo: returns true if successful
								JOptionPane.showMessageDialog(null, "Could not rename.\nThis action may not be" + " allowed for this file/folder.",
								                              "Couldn't rename", JOptionPane.WARNING_MESSAGE);
							DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
							DefaultMutableTreeNode selectedNodeParent = (DefaultMutableTreeNode) selectedNode.getParent();
							fileTree.setSelectionPath(new TreePath(selectedNodeParent));
						}
						catch (Exception e)
						{
							JOptionPane.showMessageDialog(null, "An error occurred while renaming file", "Error", JOptionPane.ERROR_MESSAGE);
						}
					}
				}
			}
		});
		
		setDownloadPath.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				File selectedPath = new File(getFileTree().getLastSelectedPathComponent().toString());
				
				if (selectedPath.isDirectory())
				{
					// The setText method below will also "call" textPath's insertUpdate (Document listener)
					SharedObjs.crsManagerPane.setTextPath(selectedPath.toString());
					
					Logger.log(Logger.TAG_FILETREE, "Changing download path to " + selectedPath);
				}
				else
				{
					JOptionPane.showMessageDialog(null, "Only folders can be set as download paths", "Not a folder", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		delete.setIcon(Icons.recycleBin);
		rename.setIcon(Icons.rename);
		popup.add(open);
		
		if (fileTree.getSelectionPaths().length == 1 && isSelectedNodesFolder(fileTree.getSelectionPaths()))
		{
			JMenu rootFolder = new JMenu("Root");
			JMenuItem root = new JMenuItem("Set as root");
			JMenuItem rootParent = new JMenuItem("Set parent as root");
			JMenuItem reset = new JMenuItem("Reset root folder");
			
			JMenu openBtd = new JMenu("Open BTD");
			JMenuItem openRecent = new JMenuItem("Open the last BTD file");
			JMenuItem openAll = new JMenuItem("Open all BTD files");
			
			openRecent.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
					File file = (File) node.getUserObject();
					File btdFile = null;
					for (File f : file.listFiles())
					{
						if (f.getName().substring(f.getName().lastIndexOf(".") + 1).equals("btd"))
						{
							btdFile = f;
						}
					}
					
					if (btdFile != null)
					{
						try
						{
							Desktop.getDesktop().open(btdFile);
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			});
			
			openAll.addActionListener(new ActionListener()
			{
				
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
					File file = (File) node.getUserObject();
					for (File f : file.listFiles())
					{
						if (f.getName().substring(f.getName().lastIndexOf(".") + 1).equals("btd"))
						{
							try
							{
								Desktop.getDesktop().open(f);
							}
							catch (IOException e)
							{
								e.printStackTrace();
							}
						}
					}
				}
				
			});
			
			openBtd.add(openRecent);
			openBtd.add(openAll);
			popup.add(openBtd);
			
			root.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
					File file = (File) node.getUserObject();
					SharedObjs.setRootFolderPath(file.getAbsolutePath());
					buildTree();
				}
			});
			
			rootParent.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					DefaultMutableTreeNode node = (DefaultMutableTreeNode) fileTree.getLastSelectedPathComponent();
					File file = (File) node.getUserObject();
					file = file.getParentFile();
					
					if (file != null)
						SharedObjs.setRootFolderPath(file.getAbsolutePath());
					else
						SharedObjs.setRootFolderPath("");
					buildTree();
				}
			});
			
			reset.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent arg0)
				{
					SharedObjs.setRootFolderPath("");
					Logger.log(Logger.TAG_FILETREE, SharedObjs.getRootFolderPath());
					buildTree();
				}
			});
			
			rootFolder.add(root);
			rootFolder.add(rootParent);
			rootFolder.add(reset);
			popup.add(rootFolder);
		}
		
		popup.add(setDownloadPath);
		
		if (isSelectedNodesFolder(fileTree.getSelectionPaths()))
		{
			JMenuItem runScript = new JMenuItem("Build report");
			JMenuItem preAnalyze = new JMenuItem("Pre-Analyze CR");
			
			runScript.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							int count = 0;
							ProgressDialog dialog = new ProgressDialog(SharedObjs.satFrame, fileTree.getSelectionPaths().length);
							
							for (TreePath p : fileTree.getSelectionPaths())
							{
								DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
								File file = (File) node.getUserObject();
								
								try
								{
									SharedObjs.crsManagerPane.runScript(file.getAbsolutePath());
									dialog.updateDialogView(++count);
								}
								catch (IOException e)
								{
									e.printStackTrace();
								}
							}
						}
					});
					
					thread.start();
				}
			});
			
			// TODO SAT does not analyze CRs not downloaded using actual SAT instance
			preAnalyze.addActionListener(new ActionListener()
			{
				@Override
				public void actionPerformed(ActionEvent event)
				{
					Thread thread = new Thread(new Runnable()
					{
						@Override
						public void run()
						{
							
							int count = 0;
							ProgressDialog dialog = new ProgressDialog(SharedObjs.satFrame, fileTree.getSelectionPaths().length);
							
							for (TreePath p : fileTree.getSelectionPaths())
							{
								DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
								File file = (File) node.getUserObject();
								
								SharedObjs.crsManagerPane.addLogLine("Pre analyzing CR ...");
								
								CrChecker crChecker = new CrChecker(file.getAbsolutePath());
								boolean crClosed = crChecker.checkCR();
								
								if (crClosed == false)
								{
									SharedObjs.crsManagerPane.addLogLine("Nothing detected");
									try
									{
										SharedObjs.crsManagerPane.runScript(file.getAbsolutePath());
									}
									catch (IOException e)
									{
										e.printStackTrace();
									}
								}
								
								dialog.updateDialogView(++count);
								
								SharedObjs.crsManagerPane.addLogLine("All done for " + file.getName() + "\n");
							}
						}
					});
					
					thread.start();
				}
			});
			
			runScript.setIcon(fileSystemView.getSystemIcon(new File("Data\\scripts\\_Base.pl")));
			popup.add(runScript);
			preAnalyze.setIcon(Icons.analyze);
			// popup.add(preAnalyze); TODO
		}
		
		// if the it's a txt file, show the popup to select the text editor
		if (checkFileExtension(fileTree.getSelectionPaths()).equals("txt"))
		{
			JMenu openWith = new JMenu("Open with");
			JMenuItem textTool = new JMenuItem("Text Analysis Tool");
			JMenuItem notepad = new JMenuItem("Notepad++");
			
			// Get the file selected and open with TextAnalysisTool
			textTool.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					File file;
					DefaultMutableTreeNode node;
					TreePath[] paths = fileTree.getSelectionPaths();
					
					for (int i = 0; i < paths.length; i++)
					{
						node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
						file = (File) node.getUserObject();
						try
						{
							Runtime.getRuntime().exec(new String[] {"Data\\complements\\TextAnalysisTool.exe ", file.getAbsolutePath()});
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			});
			
			// Get the file selected and open with Notepad++
			notepad.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					File file;
					DefaultMutableTreeNode node;
					TreePath[] paths = fileTree.getSelectionPaths();
					
					for (int i = 0; i < paths.length; i++)
					{
						node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
						file = (File) node.getUserObject();
						try
						{
							Runtime.getRuntime().exec(new String[] {"C:\\Program Files (x86)\\Notepad++\\notepad++.exe ", file.getAbsolutePath()});
						}
						catch (IOException e)
						{
							e.printStackTrace();
						}
					}
				}
			});
			
			// Get the icons for the executable programs
			textTool.setIcon(fileSystemView.getSystemIcon(new File("Data\\complements\\TextAnalysisTool.exe")));
			notepad.setIcon(fileSystemView.getSystemIcon(new File("C:\\Program Files (x86)\\Notepad++\\notepad++.exe")));
			popup.add(openWith);
			openWith.add(textTool);
			openWith.add(notepad);
		}
		else if (checkFileExtension(fileTree.getSelectionPaths()).equals("7z") || checkFileExtension(fileTree.getSelectionPaths()).equals("zip"))
		{
			JMenuItem unzip = new JMenuItem("Unzip");
			JMenuItem unzipRun = new JMenuItem("Unzip and run build-report");
			JMenuItem unzipAnalyze = new JMenuItem("Unzip and pre-analyze");
			
			// Get the files selected and unzip them
			unzip.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					unZipFiles(false, false);
				}
			});
			
			// Get the files selected, unzip them and run the build-report
			unzipRun.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					unZipFiles(true, false);
				}
			});
			
			// Get the files selected, unzip them, pre analyze and run the build-report
			unzipAnalyze.addActionListener(new ActionListener()
			{
				public void actionPerformed(ActionEvent arg0)
				{
					unZipFiles(true, true);
				}
			});
			
			unzip.setIcon(Icons.zip);
			unzipRun.setIcon(fileSystemView.getSystemIcon(new File("Data\\scripts\\_Base.pl")));
			unzipAnalyze.setIcon(Icons.analyze);
			popup.add(unzip);
			popup.add(unzipRun);
			// popup.add(unzipAnalyze); TODO
		}
		
		popup.add(rename);
		popup.add(delete);
		popup.show(fileTree, posX, posY);
		
		return popup;
	}
	
	// Delete the file
	public void deleteFile(File file)
	{
		if (file.isDirectory())
			FileUtils.deleteQuietly(file);
		else
			file.delete();
	}
	
	// Delete the files selected
	public void deleteFilesSelected()
	{
		new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					TreePath paths[] = fileTree.getSelectionPaths();
					
					for (TreePath p : paths)
					{
						DefaultMutableTreeNode node = (DefaultMutableTreeNode) p.getLastPathComponent();
						deleteFile((File) node.getUserObject());
						
						// if file is removed, node needs to be removed
						node.removeFromParent();
						SwingUtilities.invokeLater(new Runnable()
						{
							public void run()
							{
								// Here, we can safely update the GUI
								// because we'll be called from the
								// event dispatch thread
								fileTree.updateUI();
							}
						});
					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
					Logger.log(Logger.TAG_FILETREE, "Error while deleting file(s)");
				}
			}
		}).start();
	}
	
	// check the extension of the files selected
	public String checkFileExtension(TreePath paths[])
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) paths[0].getLastPathComponent();
		File file = (File) node.getUserObject();
		
		if (file.isDirectory())
			return "Selection contains at least one folder";
		String fileExtension = FilenameUtils.getExtension(file.getAbsolutePath());
		
		for (int i = 1; i < paths.length; i++)
		{
			node = (DefaultMutableTreeNode) paths[i].getLastPathComponent();
			file = (File) node.getUserObject();
			if (!FilenameUtils.getExtension(file.getAbsolutePath()).equals(fileExtension))
				return "Different file extensions";
		}
		
		return fileExtension;
	}
	
	// Check if the nodes currently selected are folders
	public boolean isSelectedNodesFolder(TreePath paths[])
	{
		DefaultMutableTreeNode node;
		File file;
		
		for (TreePath p : fileTree.getSelectionPaths())
		{
			node = (DefaultMutableTreeNode) p.getLastPathComponent();
			file = (File) node.getUserObject();
			
			if (!file.isDirectory())
				return false;
		}
		
		return true;
	}
	
	// Unzip the files and if necessary runs the build report script
	public void unZipFiles(boolean runScript, boolean analyze)
	{
		final boolean run = runScript;
		final boolean preanalyze = analyze;
		
		Thread thread = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				File file;
				File newFile;
				DefaultMutableTreeNode node;
				TreePath[] paths = fileTree.getSelectionPaths();
				ProgressDialog dialog = new ProgressDialog(SharedObjs.satFrame, paths.length);
				int length = 0;
				
				for (TreePath p : paths)
				{
					node = (DefaultMutableTreeNode) p.getLastPathComponent();
					file = (File) node.getUserObject();
					node = (DefaultMutableTreeNode) node.getParent();
					
					UnZip.unZipIt(file.getAbsolutePath(), file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 28));
					
					newFile = new File(file.getAbsolutePath().substring(0, file.getAbsolutePath().length() - 28));
					
					Logger.log(Logger.TAG_FILETREE, newFile.getAbsolutePath());
					
					if (newFile != null)
					{
						boolean crClosed = false;
						
						if (preanalyze)
						{
							SharedObjs.crsManagerPane.addLogLine("Pre analyzing CR ...");
							
							CrChecker crChecker = new CrChecker(newFile.getAbsolutePath());
							crClosed = crChecker.checkCR();
							
							if (crClosed == false)
							{
								SharedObjs.crsManagerPane.addLogLine("Nothing detected");
								
								if (run)
								{
									try
									{
										SharedObjs.crsManagerPane.runScript(newFile.getAbsolutePath());
									}
									catch (IOException e)
									{
										e.printStackTrace();
									}
								}
							}
						}
						else
						{
							if (run)
							{
								try
								{
									SharedObjs.crsManagerPane.runScript(newFile.getAbsolutePath());
								}
								catch (IOException e)
								{
									e.printStackTrace();
								}
							}
						}
						
						node.add(new DefaultMutableTreeNode(newFile));
						
						SharedObjs.crsManagerPane.addLogLine("All done for " + newFile.getName() + "\n");
					}
					
					fileTree.updateUI();
					dialog.updateDialogView(++length);
				}
			}
		});
		
		thread.start();
	}
	
	public JTree getFileTree()
	{
		return fileTree;
	}
	
	public boolean isCrNode(DefaultMutableTreeNode node)
	{
		DefaultMutableTreeNode Node = node;
		File aux = (File) Node.getUserObject();
		int Length;
		
		if (aux.isDirectory())
		{
			Length = Node.getChildCount();
			
			for (int i = 0; i < Length; i++)
			{
				if (Node.getChildAt(i).toString().toLowerCase().contains("report_info") || Node.getChildAt(i).toString().toLowerCase().contains(".btd")
				    || Node.getChildAt(i).toString().toLowerCase().contains("entry.txt"))
				{
					return true;
				}
			}
			
			return false;
		}
		else
		{
			Node = (DefaultMutableTreeNode) Node.getParent();
			Length = Node.getChildCount();
			
			for (int i = 0; i < Length; i++)
			{
				if (Node.getChildAt(i).toString().toLowerCase().contains("report_info") || Node.getChildAt(i).toString().toLowerCase().contains(".btd")
				    || Node.getChildAt(i).toString().toLowerCase().contains("entry.txt"))
				{
					return true;
				}
			}
			
			return false;
		}
	}
	
	// Initialize the variable rootFolderPath
	public void initRootFolder()
	{
	}
	
	public void buildTree()
	{
		File rootFolder = new File(SharedObjs.getRootFolderPath().replace("\\", "\\\\"));
		
		// Initialize the file tree based on the folder root predefined if it exists
		if (rootFolder.exists())
		{
			DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(rootFolder);
			
			for (File file : rootFolder.listFiles())
			{
				DefaultMutableTreeNode node = new DefaultMutableTreeNode(file);
				newNode.add(node);
			}
			
			root.removeAllChildren();
			root.add(newNode);
		}
		else
		{
			// initialize the file tree based on the system roots
			for (File file : fileSystemView.getRoots())
			{
				DefaultMutableTreeNode newNode = new DefaultMutableTreeNode(file);
				
				if (file.isDirectory())
				{
					for (File f : file.listFiles())
					{
						DefaultMutableTreeNode node = new DefaultMutableTreeNode(f);
						newNode.add(node);
					}
				}
				
				root.removeAllChildren();
				root.add(newNode);
			}
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				// Here, we can safely update the GUI because we'll be called from the event dispatch thread
				if (fileTree != null)
				{
					fileTree.updateUI();
					fileTree.expandRow(0);
				}
			}
		});
	}
}
