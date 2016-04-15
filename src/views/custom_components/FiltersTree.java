package views.custom_components;


import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

import style.ColorPrinter;
import style.LabelTreeNodeRenderer;
import core.Logger;
import core.SharedObjs;
import customobjects.CustomFilterItem;
import filters.Alarm;
import filters.B2G;
import filters.Consume;
import filters.Diag;
import filters.Issue;
import filters.Normal;
import filters.Suspicious;
import filters.Tether;


public class FiltersTree extends JTree
{
	// Global Variables
	private DefaultTreeModel       treeModel;
	private DefaultMutableTreeNode rootNode;
	
	// Tree definition
	public FiltersTree()
	{
		// Initializing variables
		treeModel = (DefaultTreeModel) getModel();
		rootNode = (DefaultMutableTreeNode) treeModel.getRoot();
		setToggleClickCount(1);
		
		// Setting up initial tree (needs to change for when custom filters enabled)
		initializeTree();
		
		// Configuring rows UI
		setCellRenderer(new LabelTreeNodeRenderer());
		setRowHeight(20);
		
		// Configuring tree selection listener
		addTreeSelectionListener(new TreeSelectionListener()
		{
			@Override
			public void valueChanged(TreeSelectionEvent e)
			{
				treeSelectionAction();
			}
		});
		
		// Reloading tree architecture on the UI
		updateResultTreeUI();
	}
	
	// Node control methods
	public void addConsumeNode(String node)
	{
		DefaultMutableTreeNode HCNode = findNode("High Consumption");
		DefaultMutableTreeNode HCitem = new DefaultMutableTreeNode(node);
		
		if (HCNode == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node");
		}
		else if (!HCNode.toString().contains("Error"))
		{
			if (HCNode.getChildCount() == 0)
			{
				HCNode.add(new DefaultMutableTreeNode("On Colors"));
			}
			HCitem.add(new DefaultMutableTreeNode("Screen ON"));
			HCitem.add(new DefaultMutableTreeNode("Screen OFF"));
			HCitem.add(new DefaultMutableTreeNode("Full Log"));
			HCitem.add(new DefaultMutableTreeNode("On Colors"));
		}
		
		HCNode.insert(HCitem, HCNode.getChildCount());
	}
	
	public void addWakeLocksNode(String node)
	{
		DefaultMutableTreeNode SPNode = findNode("Suspicious");
		DefaultMutableTreeNode WLitem = new DefaultMutableTreeNode(node);
		
		if (SPNode == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node");
		}
		
		SPNode.insert(WLitem, SPNode.getChildCount());
	}
	
	public void addIssues(String node)
	{
		DefaultMutableTreeNode INode = findNode("WakeLocks");
		DefaultMutableTreeNode Iitem = new DefaultMutableTreeNode(node);
		
		if (INode == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node");
			INode = new DefaultMutableTreeNode("WakeLocks");
		}
		
		INode.insert(Iitem, 0);
	}
	
	public void addBug2go(String node)
	{
		DefaultMutableTreeNode BGNode = findNode("Bug2Go");
		
		if (BGNode == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node");
			BGNode = new DefaultMutableTreeNode("Bug2Go");
		}
		
		BGNode.insert(new DefaultMutableTreeNode(node), 0);
	}
	
	public void addTether(String node)
	{
		DefaultMutableTreeNode TNode = findNode("Tethering");
		
		if (TNode == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node");
		}
		
		if (Tether.getResult().contains("No tethering evidences were found in text logs"))
			TNode.insert(new DefaultMutableTreeNode("No tethering activity found"), TNode.getChildCount());
		else
			TNode.insert(new DefaultMutableTreeNode(node), 0);
	}
	
	public void addDiag(String node)
	{
		DefaultMutableTreeNode DNode = findNode("Diag");
		
		if (DNode == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node");
		}
		
		DNode.insert(new DefaultMutableTreeNode(node), 0);
	}
	
	public void addAlarms(String node)
	{
		DefaultMutableTreeNode ANode = findNode("Alarms");
		DefaultMutableTreeNode Aitem = new DefaultMutableTreeNode(node);
		
		if (ANode == null)
		{
			Logger.log(Logger.TAG_OPTIONS, "Could not find node");
		}
		else if (!node.contains("Nothing") && !(ANode.getUserObject().toString()).contains("Error"))
		{
			if (ANode.getChildCount() == 0)
			{
				ANode.add(new DefaultMutableTreeNode("On Colors"));
			}
			Aitem.add(new DefaultMutableTreeNode("On Colors"));
		}
		
		ANode.insert(Aitem, ANode.getChildCount());
	}
	
	public void addSummary(String node)
	{
		DefaultMutableTreeNode SNode = findNode("Summary");
		
		if (SNode == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node");
		}
		
		SNode.insert(new DefaultMutableTreeNode(node), 0);
	}
	
	public void addCustomResult(String node, String res)
	{
		DefaultMutableTreeNode Node = findNode(node);
		
		if (Node == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node. Adding new one.");
			Node = new DefaultMutableTreeNode(node);
			Node.add(new DefaultMutableTreeNode(res));
		}
		
		Node.add(new DefaultMutableTreeNode(res));
	}
	
	public void addCustomNode(String node)
	{
		DefaultMutableTreeNode Node = findNode(node);
		
		if (Node == null)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Could not find node. Adding new one.");
			Node = new DefaultMutableTreeNode(node);
			rootNode.add(Node);
		}
		
		updateResultTreeUI();
	}
	
	// Filter threads
	public void alarmThread(DefaultMutableTreeNode selectedNode)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Alarms thread running");
		String alarmResult;
		String x = (String) selectedNode.getUserObject();
		x = ("Alarms - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		String crPatch = new String(SharedObjs.getCrPath());
		alarmResult = Alarm.makelog(SharedObjs.getCrPath());
		
		if (crPatch.equals(SharedObjs.getCrPath()))
		{
			if (alarmResult.contains("FileNotFoundException"))
			{
				x = ("Alarms - Error");
				selectedNode.setUserObject(x);
				addAlarms("System log not found");
				updateResultTreeUI();
			}
			else if (alarmResult.contains("IOException"))
			{
				x = ("Alarms - Error");
				selectedNode.setUserObject(x);
				addAlarms("IOException");
				updateResultTreeUI();
			}
			else if (alarmResult.contains("Error"))
			{
				x = ("Alarms - Error");
				selectedNode.setUserObject(x);
				addAlarms("IOException");
				updateResultTreeUI();
			}
			else if (alarmResult.contains("Not a directory"))
			{
				x = ("Alarms - Error");
				selectedNode.setUserObject(x);
				addAlarms("No directory selected");
				updateResultTreeUI();
			}
			else
			{
				if (Alarm.getListSize() == 0)
					addAlarms("Nothing found in the logs");
				else
					for (int i = 0; i < Alarm.getListSize(); i++)
					{
						addAlarms(Alarm.getList().get(i).getProcess());
						Logger.log(Logger.TAG_FILTERSRESULTSTREE, Alarm.getList().get(i).getProcess());
					}
				
				SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n======================= Alarms Resume =======================\n" + alarmResult);
				x = ("Alarms - Done");
				selectedNode.setUserObject(x);
			}
			
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Alarms thread finished");
			updateResultTreeUI();
			// expandPath(new TreePath(selectedNode.getPath()));
			
			showResultOnTextPane(selectedNode);
		}
	}
	
	public void bug2goThread(DefaultMutableTreeNode selectedNode)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Bug2go thread running");
		String b2gResult;
		String x = (String) selectedNode.getUserObject();
		x = ("Bug2Go - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		b2gResult = B2G.makelog(SharedObjs.getCrPath());
		
		if (b2gResult.contains("FileNotFoundException"))
		{
			x = ("Bug2Go - Error");
			selectedNode.setUserObject(x);
			addBug2go("Logs not found");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Bug2go thread error");
		}
		else if (b2gResult.contains("IOException"))
		{
			x = ("Bug2Go - Error");
			selectedNode.setUserObject(x);
			addBug2go("IOException");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Bug2go thread error");
		}
		else if (b2gResult.contains("Not a directory"))
		{
			x = ("Bug2Go - Error");
			selectedNode.setUserObject(x);
			addBug2go("No directory selected");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Bug2go thread error");
		}
		else
		{
			addBug2go("Result");
			SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n========================= Bug2Go =========================\n" + b2gResult);
			x = ("Bug2Go - Done");
			selectedNode.setUserObject(x);
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Bug2go thread finished");
		}
		// expandPath(new TreePath(selectedNode.getPath()));
		
		showResultOnTextPane(selectedNode);
	}
	
	public void diagThread(DefaultMutableTreeNode selectedNode)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Diag thread running");
		String diagResult;
		String x = (String) selectedNode.getUserObject();
		x = ("Diag - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		diagResult = Diag.makelog(SharedObjs.getCrPath());
		
		if (diagResult.contains("FileNotFoundException"))
		{
			x = ("Diag - Error");
			selectedNode.setUserObject(x);
			addDiag("Logs not found");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Diag thread error");
		}
		else if (diagResult.contains("IOException"))
		{
			x = ("Diag - Error");
			selectedNode.setUserObject(x);
			addDiag("IOException");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Diag thread error");
		}
		else if (diagResult.contains("JDOMException"))
		{
			x = ("Diag - Error");
			selectedNode.setUserObject(x);
			addDiag("JDOM Error");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Diag thread error");
		}
		else if (diagResult.contains("Not a directory"))
		{
			x = ("Diag - Error");
			selectedNode.setUserObject(x);
			addDiag("No directory selected");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Diag thread error");
		}
		else
		{
			addDiag("Result");
			SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n======================= Diag Wake locks =======================\n" + diagResult);
			x = ("Diag - Done");
			selectedNode.setUserObject(x);
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Diag thread finished");
		}
		// expandPath(new TreePath(selectedNode.getPath()));
		
		showResultOnTextPane(selectedNode);
	}
	
	public void wakelocksThread(DefaultMutableTreeNode selectedNode)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, "SystemPM thread running");
		String wakelocksResult;
		String x = (String) selectedNode.getUserObject();
		x = ("WakeLocks - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		wakelocksResult = Issue.makelog(SharedObjs.getCrPath());
		
		if (wakelocksResult.contains("FileNotFoundException"))
		{
			x = ("WakeLocks - Error");
			selectedNode.setUserObject(x);
			addIssues("Logs not found");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "SystemPM thread error");
		}
		else if (wakelocksResult.contains("IOException"))
		{
			x = ("WakeLocks - Error");
			selectedNode.setUserObject(x);
			addIssues("IOException");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "SystemPM thread error");
		}
		else if (wakelocksResult.contains("Not a directory"))
		{
			x = ("WakeLocks - Error");
			selectedNode.setUserObject(x);
			addIssues("No directory selected");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "SystemPM thread error");
		}
		else
		{
			addIssues("Result");
			SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n========================= SystemPM =========================\n" + wakelocksResult);
			x = ("WakeLocks - Done");
			selectedNode.setUserObject(x);
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "SystemPM thread finished");
		}
		// expandPath(new TreePath(selectedNode.getPath()));
		
		showResultOnTextPane(selectedNode);
	}
	
	public void comsumptionThread(DefaultMutableTreeNode selectedNode)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Consumption thread running");
		String consumptionResult;
		String x = (String) selectedNode.getUserObject();
		x = ("High Consumption - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		consumptionResult = Consume.makelog(SharedObjs.getCrPath());
		
		if (consumptionResult.contains("FileNotFoundException"))
		{
			x = ("High Consumption - Error");
			selectedNode.setUserObject(x);
			addConsumeNode("Logs not found");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Consumption thread error");
		}
		else if (consumptionResult.contains("IOException"))
		{
			x = ("High Consumption - Error");
			selectedNode.setUserObject(x);
			addConsumeNode("IOException");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Consumption thread error");
		}
		else if (consumptionResult.contains("Not a directory"))
		{
			x = ("High Consumption - Error");
			selectedNode.setUserObject(x);
			addConsumeNode("No directory selected");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Consumption thread error");
		}
		else
		{
			for (int i = 0; i < Consume.getHCList().size(); i++)
			{
				addConsumeNode(Consume.getHCList().get(i).getProcess());
			}
			if (Consume.getHCList().size() == 0)
				addConsumeNode("Nothing found in logs");
			SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n========================= High Consumption Apps =========================\n"
			                     + consumptionResult);
			x = ("High Consumption - Done");
			selectedNode.setUserObject(x);
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Consumption thread finished");
		}
		// expandPath(new TreePath(selectedNode.getPath()));
		
		showResultOnTextPane(selectedNode);
	}
	
	public void summaryThread(DefaultMutableTreeNode selectedNode)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Summary thread running");
		String summaryResult;
		String x = (String) selectedNode.getUserObject();
		x = ("Summary - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		summaryResult = Normal.makeLog(SharedObjs.getCrPath());
		
		if (summaryResult.contains("FileNotFoundException"))
		{
			x = ("Summary - Error");
			selectedNode.setUserObject(x);
			addSummary("Logs not found");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Summary thread error");
		}
		else if (summaryResult.contains("IOException"))
		{
			x = ("Summary - Error");
			selectedNode.setUserObject(x);
			addSummary("IOException");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Summary thread error");
		}
		else if (summaryResult.contains("Not a directory"))
		{
			x = ("Summary - Error");
			selectedNode.setUserObject(x);
			addSummary("No directory selected");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Summary thread error");
		}
		else
		{
			addSummary("Result");
			SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n========================= Summary =========================\n" + summaryResult);
			x = ("Summary - Done");
			selectedNode.setUserObject(x);
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Summary thread finished");
		}
		// expandPath(new TreePath(selectedNode.getPath()));
		
		showResultOnTextPane(selectedNode);
	}
	
	public void suspiciousThread(DefaultMutableTreeNode selectedNode)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Suspicious thread running");
		String suspiciousResult;
		String x = (String) selectedNode.getUserObject();
		x = ("Suspicious - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		suspiciousResult = Suspicious.makelog(SharedObjs.getCrPath());
		
		// TODO Test to be removed
		// SystemPM test = new SystemPM(SharedObjs.getCrPath());
		// suspiciousResult = test.makelog();
		
		if (suspiciousResult.contains("FileNotFoundException"))
		{
			x = ("Suspicious - Error");
			selectedNode.setUserObject(x);
			addWakeLocksNode("Logs not found");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Suspicious thread error");
		}
		else if (suspiciousResult.contains("IOException"))
		{
			x = ("Suspicious - Error");
			selectedNode.setUserObject(x);
			addWakeLocksNode("IOException");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Suspicious thread error");
		}
		else if (suspiciousResult.contains("Not a directory"))
		{
			x = ("Suspicious - Error");
			selectedNode.setUserObject(x);
			addWakeLocksNode("No directory selected");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Suspicious thread error");
		}
		else
		{
			if (Suspicious.getWakeLocks().size() != 0)
				for (int i = 0; i < Suspicious.getWakeLocks().size(); i++)
				{
					addWakeLocksNode(Suspicious.getWakeLocks().get(i).getProcess() + " - " + Suspicious.getWakeLocks().get(i).getTag() + " - "
					                 + Suspicious.getWakeLocks().get(i).getDuration());
				}
			else
			{
				addWakeLocksNode("No suspicious found");
			}
			SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n========================= Suspicious =========================\n" + suspiciousResult);
			x = ("Suspicious - Done");
			selectedNode.setUserObject(x);
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Suspicious thread finished");
		}
		// expandPath(new TreePath(selectedNode.getPath()));
		
		showResultOnTextPane(selectedNode);
	}
	
	public void tetherThread(DefaultMutableTreeNode selectedNode)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Summary thread running");
		String tetherResult;
		String x = (String) selectedNode.getUserObject();
		x = ("Tethering - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		tetherResult = Tether.makeLog(SharedObjs.getCrPath());
		
		if (tetherResult.contains("FileNotFoundException"))
		{
			x = ("Tethering - Error");
			selectedNode.setUserObject(x);
			addTether("Logs not found");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Tethering thread error");
		}
		else if (tetherResult.contains("IOException"))
		{
			x = ("Tethering - Error");
			selectedNode.setUserObject(x);
			addTether("IOException");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Tethering thread error");
		}
		else if (tetherResult.contains("Not a directory"))
		{
			x = ("Tethering - Error");
			selectedNode.setUserObject(x);
			addTether("No directory selected");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Tethering thread error");
		}
		else
		{
			addTether("Result");
			SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n========================= Tethering =========================\n" + tetherResult);
			x = ("Tethering - Done");
			selectedNode.setUserObject(x);
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Tethering thread finished");
		}
		
		// expandPath(new TreePath(selectedNode.getPath()));
		
		showResultOnTextPane(selectedNode);
	}
	
	public void customThread(DefaultMutableTreeNode selectedNode)
	{
		String result;
		String nodeName = selectedNode.toString();
		String x = (String) selectedNode.getUserObject();
		x = (nodeName + " - Running");
		selectedNode.setUserObject(x);
		updateResultTreeUI();
		int index = SharedObjs.getActiveFiltersList().indexOfName(nodeName);
		
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, nodeName + " thread running");
		
		if (index >= 0)
		{
			CustomFilterItem filter = SharedObjs.getActiveFiltersList().get(index);
			result = filter.runFilter(SharedObjs.getCrPath());
			if (result.contains(" log missing"))
			{
				x = (nodeName + " - Error");
				selectedNode.setUserObject(x);
				addCustomResult(nodeName, "Logs not found");
				updateResultTreeUI();
				Logger.log(Logger.TAG_FILTERSRESULTSTREE, nodeName + " thread error");
			}
			else if (result.contains("SAT IOException"))
			{
				x = (nodeName + " - Error");
				selectedNode.setUserObject(x);
				addCustomResult(nodeName, "IOException");
				updateResultTreeUI();
				Logger.log(Logger.TAG_FILTERSRESULTSTREE, nodeName + " thread error");
			}
			else if (result.contains("Not a directory"))
			{
				x = (nodeName + " - Error");
				selectedNode.setUserObject(x);
				addCustomResult(nodeName, "No directory selected");
				updateResultTreeUI();
				Logger.log(Logger.TAG_FILTERSRESULTSTREE, nodeName + " thread error");
			}
			else
			{
				if (filter.isMain())
					addCustomResult(nodeName, "Main");
				if (filter.isSystem())
					addCustomResult(nodeName, "System");
				if (filter.isKernel())
					addCustomResult(nodeName, "Kernel");
				if (filter.isRadio())
					addCustomResult(nodeName, "Radio");
				if (filter.isBugreport())
					addCustomResult(nodeName, "BugReport");
				if (filter.isRoutput())
					addCustomResult(nodeName, "RepOutput");
				
				result = filter.getHeader() + result + "\n";
				SharedObjs.setResult(SharedObjs.getResult() + "\n\n\n========================= " + filter.getName() + " =========================\n" + result);
				x = (nodeName + " - Done");
				selectedNode.setUserObject(x);
				updateResultTreeUI();
				Logger.log(Logger.TAG_FILTERSRESULTSTREE, nodeName + "thread finished");
			}
			
			showResultOnTextPane(selectedNode);
		}
		else
		{
			x = (nodeName + " - Error");
			selectedNode.setUserObject(x);
			addCustomResult(nodeName, "Filter does not exists");
			updateResultTreeUI();
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, nodeName + " thread error");
		}
	}
	
	// Tree supportive methods
	public DefaultMutableTreeNode findNode(String Node)
	{
		int Length = rootNode.getChildCount();
		
		for (int i = 0; i < Length; i++)
		{
			if (rootNode.getChildAt(i).toString().contains(Node))
			{
				return (DefaultMutableTreeNode) rootNode.getChildAt(i);
			}
		}
		
		return null;
	}
	
	public int findNodeIndex(DefaultMutableTreeNode Node)
	{
		Logger.log(Logger.TAG_FILTERSRESULTSTREE, String.valueOf(rootNode.getIndex(Node)));
		
		return rootNode.getIndex(Node);
	}
	
	public void clearTree()
	{
		rootNode.removeAllChildren();
		initializeTree();
	}
	
	public void updateResultTreeUI()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				updateUI();
			}
		});
	}
	
	public void initializeTree()
	{
		rootNode.setUserObject("Filters and Results");
		rootNode.removeAllChildren();
		
		if (Alarm.isEnabled())
		{
			rootNode.add(new DefaultMutableTreeNode("Alarms"));
		}
		if (B2G.isEnabled())
		{
			rootNode.add(new DefaultMutableTreeNode("Bug2Go"));
		}
		if (Diag.isEnabled())
		{
			rootNode.add(new DefaultMutableTreeNode("Diag"));
		}
		if (Issue.isEnabled())
		{
			rootNode.add(new DefaultMutableTreeNode("WakeLocks"));
		}
		if (Consume.isEnabled())
		{
			rootNode.add(new DefaultMutableTreeNode("High Consumption"));
		}
		if (Normal.isEnabled())
		{
			rootNode.add(new DefaultMutableTreeNode("Summary"));
		}
		if (Suspicious.isEnabled())
		{
			rootNode.add(new DefaultMutableTreeNode("Suspicious"));
		}
		if (Tether.isEnabled())
		{
			rootNode.add(new DefaultMutableTreeNode("Tethering"));
		}
		for (CustomFilterItem item : SharedObjs.getActiveFiltersList())
		{
			rootNode.add(new DefaultMutableTreeNode(item.getName()));
		}
		
		updateResultTreeUI();
	}
	
	public void updateFiltersTree()
	{
		for (int i = 8; i < rootNode.getChildCount(); i++)
		{
			System.out.println(rootNode.getChildAt(i).toString());
			rootNode.remove(i);
		}
		
		revalidate();
		updateUI();
		
		for (CustomFilterItem filter : SharedObjs.getActiveFiltersList())
		{
			addCustomNode(filter.getName());
		}
		
		revalidate();
		updateUI();
		updateResultTreeUI();
	}
	
	public void removeCustomNode(String name)
	{
		for (int i = 0; i < rootNode.getChildCount(); i++)
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "No: " + rootNode.getChildAt(i).toString());
			
			if (rootNode.getChildAt(i).toString().replace(" - Done", "").replace(" - Error", "").replace(" - Running", "").equals(name))
			{
				Logger.log(Logger.TAG_FILTERSRESULTSTREE, "achou");
				rootNode.remove(i);
				updateResultTreeUI();
			}
		}
	}
	
	private boolean showResultOnTextPane(DefaultMutableTreeNode node)
	{
		if (!SharedObjs.parserPane.isResultsTxtPaneFocusOwner() || SharedObjs.parserPane.getResultText().isEmpty())
		{
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "ResultsTxtPane on focus? " + SharedObjs.parserPane.isResultsTxtPaneFocusOwner());
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "ResultsTxtPane is empty? " + SharedObjs.parserPane.getResultText().isEmpty());
			
			DefaultMutableTreeNode nodeSelected = (DefaultMutableTreeNode) SharedObjs.parserPane.getFiltersTree().getLastSelectedPathComponent();
			
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Node received by the method: " + node.toString());
			Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Node selected on the result tree: " + nodeSelected.toString());
			
			if (node.toString().equals(nodeSelected.toString()))
			{
				Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Node names match!");
				
				if (((String) node.getUserObject()).toLowerCase().contains("alarm"))
				{
					SharedObjs.parserPane.setResultsTxtPaneText(Alarm.getResult());
					return true;
				}
				
				else if (((String) node.getUserObject()).toLowerCase().contains("bug2go"))
				{
					SharedObjs.parserPane.setResultsTxtPaneText(B2G.getResult());
					return true;
				}
				
				else if (((String) node.getUserObject()).toLowerCase().contains("diag"))
				{
					SharedObjs.parserPane.setResultsTxtPaneText(Diag.getResult());
					return true;
				}
				
				else if (((String) node.getUserObject()).toLowerCase().contains("wakelock"))
				{
					SharedObjs.parserPane.setResultsTxtPaneText(Issue.getResult());
					return true;
				}
				
				else if (((String) node.getUserObject()).toLowerCase().contains("consumption"))
				{
					SharedObjs.parserPane.setResultsTxtPaneText(Consume.getResult());
					return true;
				}
				
				else if (((String) node.getUserObject()).toLowerCase().contains("summary"))
				{
					SharedObjs.parserPane.setResultsTxtPaneText(Normal.getResult());
					return true;
				}
				
				else if (((String) node.getUserObject()).toLowerCase().contains("suspicious"))
				{
					SharedObjs.parserPane.setResultsTxtPaneText(Suspicious.getResult());
					return true;
				}
				
				else if (((String) node.getUserObject()).toLowerCase().contains("tether"))
				{
					SharedObjs.parserPane.setResultsTxtPaneText(Tether.getResult());
					return true;
				}
				
				else
				{
					int index = SharedObjs.getActiveFiltersList().indexOfName(node.toString().replace(" - Done", "").replace(" - Error", ""));
					if (index > -1)
					{
						SharedObjs.parserPane.setResultsTxtPaneText(SharedObjs.getActiveFiltersList().get(index).getResult());
						return true;
					}
				}
				
			}
		}
		return false;
	}
	
	private void treeSelectionAction()
	{
		final DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) getLastSelectedPathComponent();
		
		if (selectedNode != null)
		{
			if (!selectedNode.toString().contains("On Colors"))
				ColorPrinter.resetPanelStyle(SharedObjs.parserPane.getResultTxtPane());
			
			switch (selectedNode.getLevel())
			{
				case 0: // Root selected
					SharedObjs.parserController.showAllLogResults();
					Logger.log(Logger.TAG_FILTERSRESULTSTREE, "Showing all results.");
					break;
				case 1: // Leaf filter selected
					if (selectedNode.toString().contains("Alarms") && !selectedNode.toString().contains(" - Running"))
					{
						if (selectedNode.getChildCount() == 0)
						{
							new Thread(new Runnable()
							{
								@Override
								public void run()
								{
									alarmThread(selectedNode);
								}
							}).start();
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Alarm.getResult());
						}
					}
					else if (selectedNode.toString().contains("Bug2Go") && !selectedNode.toString().contains(" - Running"))
					{
						if (selectedNode.getChildCount() == 0)
						{
							new Thread(new Runnable()
							{
								@Override
								public void run()
								{
									bug2goThread(selectedNode);
								}
							}).start();
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(B2G.getResult());
						}
					}
					else if (selectedNode.toString().contains("Diag") && !selectedNode.toString().contains(" - Running"))
					{
						if (selectedNode.getChildCount() == 0)
						{
							new Thread(new Runnable()
							{
								@Override
								public void run()
								{
									diagThread(selectedNode);
								}
							}).start();
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Diag.getResult());
						}
					}
					else if (selectedNode.toString().contains("WakeLocks") && !selectedNode.toString().contains(" - Running"))
					{
						if (selectedNode.getChildCount() == 0)
						{
							new Thread(new Runnable()
							{
								@Override
								public void run()
								{
									wakelocksThread(selectedNode);
								}
							}).start();
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Issue.getResult());
						}
					}
					else if (selectedNode.toString().contains("High Consumption"))
					{
						if (selectedNode.getChildCount() == 0 && !selectedNode.toString().contains(" - Running"))
						{
							new Thread(new Runnable()
							{
								@Override
								public void run()
								{
									comsumptionThread(selectedNode);
								}
							}).start();
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Consume.getResult());
						}
					}
					else if (selectedNode.toString().contains("Summary") && !selectedNode.toString().contains(" - Running"))
					{
						if (selectedNode.getChildCount() == 0)
						{
							new Thread(new Runnable()
							{
								@Override
								public void run()
								{
									summaryThread(selectedNode);
								}
							}).start();
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Normal.getResult());
						}
					}
					else if (selectedNode.toString().contains("Suspicious") && !selectedNode.toString().contains(" - Running"))
					{
						if (selectedNode.getChildCount() == 0)
						{
							new Thread(new Runnable()
							{
								@Override
								public void run()
								{
									suspiciousThread(selectedNode);
								}
							}).start();
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Suspicious.getResult());
						}
					}
					else if (selectedNode.toString().contains("Tethering") && !selectedNode.toString().contains(" - Running"))
					{
						if (selectedNode.getChildCount() == 0)
						{
							new Thread(new Runnable()
							{
								@Override
								public void run()
								{
									tetherThread(selectedNode);
								}
							}).start();
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Tether.getResult());
						}
					}
					else
					{
						int index = SharedObjs.getActiveFiltersList().indexOfName(selectedNode.toString().replace(" - Done", "").replace(" - Error", ""));
						if (index >= 0)
						{
							if (selectedNode.getChildCount() == 0 && !selectedNode.toString().contains(" - Running"))
							{
								new Thread(new Runnable()
								{
									@Override
									public void run()
									{
										customThread(selectedNode);
									}
								}).start();
							}
							else
							{
								SharedObjs.parserPane.setResultsTxtPaneText(SharedObjs.getActiveFiltersList().get(index).getResult());
							}
						}
					}
					break;
				case 2: // A filter child selected
					DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode) selectedNode.getParent();
					
					if (parentNode.toString().startsWith("Alarms"))
					{
						if (parentNode.toString().contains("Error"))
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Alarm.getResult());
						}
						else
						{
							int nodeIndex = parentNode.getIndex(selectedNode);
							
							if (selectedNode.toString().contains("On Colors"))
							{
								ColorPrinter.colorsAlarm(SharedObjs.parserPane.getResultTxtPane(), Alarm.getResult());
								break;
							}
							else
							{
								SharedObjs.parserPane.setResultsTxtPaneText(SharedObjs.optionsPane.getTextAlarms()
								                                                                  .replace("#pname#",
								                                                                           Alarm.getList().get(nodeIndex - 1).getProcess())
								                                                                  .replace("#log#",
								                                                                           Alarm.getList().get(nodeIndex - 1).toString())
								                                                                  .replace("\\n", "\n"));
								break;
							}
						}
					}
					else if (parentNode.toString().contains("Bug2Go"))
					{
						if (parentNode.toString().contains("Error"))
						{
							SharedObjs.parserPane.setResultsTxtPaneText(B2G.getResult());
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(B2G.getResult());
						}
					}
					else if (parentNode.toString().contains("Diag"))
					{
						if (parentNode.toString().contains("Error"))
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Diag.getResult());
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Diag.getResult());
						}
					}
					else if (parentNode.toString().contains("WakeLocks"))
					{
						if (parentNode.toString().contains("Error"))
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Issue.getResult());
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Issue.getResult());
						}
					}
					else if (parentNode.toString().contains("High Consumption"))
					{
						if (parentNode.toString().contains("Error"))
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Consume.getResult());
						}
						else
						{
							int nodeIndex = parentNode.getIndex(selectedNode);
							
							if (selectedNode.toString().contains("On Colors"))
							{
								ColorPrinter.colorsApps(SharedObjs.parserPane.getResultTxtPane(), Consume.getResult());
							}
							else
							{
								SharedObjs.parserPane.setResultsTxtPaneText("{panel}\n" + Consume.getHCList().get(nodeIndex - 1).toString() + "{panel}");
							}
						}
					}
					else if (parentNode.toString().contains("Summary"))
					{
						if (parentNode.toString().contains("Error"))
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Normal.getResult());
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Normal.getResult());
						}
					}
					else if (parentNode.toString().contains("Suspicious"))
					{
						if (parentNode.toString().contains("Error"))
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Suspicious.getResult());
						}
						else
						{
							int nodeIndex = parentNode.getIndex(selectedNode);
							SharedObjs.parserPane.setResultsTxtPaneText(Suspicious.getWakeLocks().get(nodeIndex).toString());
						}
					}
					else if (parentNode.toString().contains("Tethering"))
					{
						if (parentNode.toString().contains("Error"))
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Tether.getResult());
						}
						else
						{
							SharedObjs.parserPane.setResultsTxtPaneText(Tether.getResult());
						}
					}
					else
					{
						int index = SharedObjs.getActiveFiltersList().indexOfName(parentNode.toString().replace(" - Done", "").replace(" - Error", ""));
						
						if (index >= 0)
						{
							CustomFilterItem filter = SharedObjs.getActiveFiltersList().get(index);
							if (selectedNode.toString().contains("Main"))
								SharedObjs.parserPane.setResultsTxtPaneText(filter.getMares());
							if (selectedNode.toString().contains("System"))
								SharedObjs.parserPane.setResultsTxtPaneText(filter.getSyres());
							if (selectedNode.toString().contains("Kernel"))
								SharedObjs.parserPane.setResultsTxtPaneText(filter.getKeres());
							if (selectedNode.toString().contains("Radio"))
								SharedObjs.parserPane.setResultsTxtPaneText(filter.getRares());
							if (selectedNode.toString().contains("BugReport"))
								SharedObjs.parserPane.setResultsTxtPaneText(filter.getBures());
							if (selectedNode.toString().contains("RepOutput"))
								SharedObjs.parserPane.setResultsTxtPaneText(filter.getRores());
						}
					}
					break;
				
				case 3: // A filter child derivation selected
					parentNode = (DefaultMutableTreeNode) selectedNode.getParent().getParent();
					int nodeIndex = parentNode.getIndex(selectedNode.getParent());
					
					if (parentNode.toString().contains("Alarms"))
					{
						ColorPrinter.colorsAlarm(SharedObjs.parserPane.getResultTxtPane(),
						                         SharedObjs.optionsPane.getTextAlarms().replace("#pname#", Alarm.getList().get(nodeIndex - 1).getProcess())
						                                               .replace("#log#", Alarm.getList().get(nodeIndex - 1).toString()).replace("\\n", "\n"));
					}
					else if (parentNode.toString().contains("High Consumption"))
					{
						if (selectedNode.toString().contains("ON"))
							SharedObjs.parserPane.setResultsTxtPaneText(SharedObjs.optionsPane.getTextConsumeOn()
							                                                                  .replaceAll("#pname#",
							                                                                              Consume.getHCList().get(nodeIndex - 1).getProcess())
							                                                                  .replaceAll("#sconconsume#",
							                                                                              String.valueOf(Consume.getHCList().get(nodeIndex - 1)
							                                                                                                    .getScOnConsume()))
							                                                                  .replaceAll("#logon#",
							                                                                              Consume.getHCList().get(nodeIndex - 1).getLogOn())
							                                                                  .replace("\\n", "\n"));
						else if (selectedNode.toString().contains("OFF"))
							SharedObjs.parserPane.setResultsTxtPaneText(SharedObjs.optionsPane.getTextConsumeOff()
							                                                                  .replaceAll("#pname#",
							                                                                              Consume.getHCList().get(nodeIndex - 1).getProcess())
							                                                                  .replaceAll("#scoffconsume#",
							                                                                              String.valueOf(Consume.getHCList().get(nodeIndex - 1)
							                                                                                                    .getScOffConsume()))
							                                                                  .replaceAll("#logoff#",
							                                                                              Consume.getHCList().get(nodeIndex - 1).getLogOff())
							                                                                  .replace("\\n", "\n"));
						else if (selectedNode.toString().contains("Full"))
							SharedObjs.parserPane.setResultsTxtPaneText(SharedObjs.optionsPane.getTextConsumeFull()
							                                                                  .replaceAll("#pname#",
							                                                                              Consume.getHCList().get(nodeIndex - 1).getProcess())
							                                                                  .replaceAll("#avgconsume#",
							                                                                              String.valueOf(Consume.getHCList().get(nodeIndex - 1)
							                                                                                                    .getConsumeAvg()))
							                                                                  .replaceAll("#logfull#",
							                                                                              Consume.getHCList().get(nodeIndex - 1).getLog())
							                                                                  .replace("\\n", "\n"));
						else
							ColorPrinter.colorsApps(SharedObjs.parserPane.getResultTxtPane(),
							                        SharedObjs.optionsPane.getTextConsumeFull()
							                                              .replaceAll("#pname#", Consume.getHCList().get(nodeIndex - 1).getProcess())
							                                              .replaceAll("#avgconsume#",
							                                                          String.valueOf(Consume.getHCList().get(nodeIndex - 1).getConsumeAvg()))
							                                              .replaceAll("#logfull#", Consume.getHCList().get(nodeIndex - 1).getLog())
							                                              .replace("\\n", "\n"));
					}
					break;
				
				default: // Something different happened
					break;
			}
		}
	}
}
