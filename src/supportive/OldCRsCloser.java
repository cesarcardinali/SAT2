package supportive;


import java.util.ArrayList;

import javax.swing.JOptionPane;

import views.secondarypanes.ParallelTextPopup;
import core.Logger;
import core.SharedObjs;


@SuppressWarnings("unused")
public class OldCRsCloser implements Runnable
{
	private String[]           crs;
	private ArrayList<String>  noLabels;
	private JiraSatApi jira;
	
	public OldCRsCloser(String[] crs)
	{
		this.crs = crs;
	}
	
	/*
	 * closeDiagCrs
	 */
	public void closeDiagCrs()
	{
		noLabels = new ArrayList<String>();
		
		Object[] options = {"It's OK. Go!", "Cancel, I need to check"};
		int n = JOptionPane.showOptionDialog(SharedObjs.crsManagerPane,
		                                     "Please, make sure that your username and password are set correctly at \"CRs and Jira\" tab. Otherwise, cancel this window and "
		                                                     + "check your login data.", "Warning", JOptionPane.YES_NO_CANCEL_OPTION,
		                                     JOptionPane.QUESTION_MESSAGE, null, options, options[1]);
		Logger.log(Logger.TAG_CRSCLOSER, "Resposta: " + n);
		
		if (n == 0)
		{
			SharedObjs.addLogLine("Connecting to Jira");
			jira = new JiraSatApi(JiraSatApi.DEFAULT_JIRA_URL, SharedObjs.getUser(), SharedObjs.getPass());
			
			for (String cr : crs)
			{
				SharedObjs.addLogLine("Assigning " + cr);
				jira.assignIssue(cr);
				try
				{
					Thread.sleep(500);
				}
				catch (InterruptedException e)
				{
					e.printStackTrace();
				}
				
				if (SharedObjs.crsManagerModel.getLabels().length > 0)
				{
					SharedObjs.addLogLine("Adding labels");
					String output = jira.addLabel(cr, SharedObjs.crsManagerModel.getLabels());
					if (output.contains("error"))
					{
						SharedObjs.addLogLine("ERROR Adding labels! ");
						noLabels.add(cr);
					}
				}
				SharedObjs.addLogLine("Closing ...");
				jira.closeIssue(cr,
				                JiraSatApi.CANCELLED,
				                "Closing this issue since it's was reported in an old release. Analysis is being focused on latest ones. If the issue happens again, please, raise a new Bug2go.");
				SharedObjs.addLogLine(cr + " closed");
			}
			
			ParallelTextPopup list = new ParallelTextPopup();
			list.clear();
			
			for (String cr : noLabels)
			{
				list.addItemList1(cr);
			}
			
			list.setVisible(true);
		}
		else
		{
			Logger.log(Logger.TAG_CRSCLOSER, "Action cancelled");
		}
	}
	
	@Override
	public void run()
	{
		closeDiagCrs();
	}
}
