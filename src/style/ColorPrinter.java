package style;


import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

import core.Logger;


/**
 * Defines the color printer class. It is responsible to put colors on text
 */
public class ColorPrinter
{
	static public void colorsAlarm(JTextPane txtPaneAlarms, String log)
	{
		long difTempo = 0L;
		String linhaAnterior = null, linhaAtual;
		StyledDocument docAlarms = (StyledDocument) txtPaneAlarms.getDocument();
		Style OK = docAlarms.addStyle("OK", null);
		StyleConstants.setForeground(OK, Colors.verdeEscuro);
		Style medio = docAlarms.addStyle("medio", null);
		StyleConstants.setForeground(medio, Colors.amareloEscuro);
		Style NOK = docAlarms.addStyle("NOK", null);
		StyleConstants.setForeground(NOK, Colors.vermelhoEscuro);
		String[] lines = log.split("\n");
		txtPaneAlarms.setText("");
		
		try
		{
			for (int i = 0; i < lines.length; i++)
			{
				linhaAtual = lines[i];
				
				if (linhaAtual.contains("AlarmManager"))
				{
					// parse the line color
					if (linhaAnterior != null)
					{
						difTempo = getTime(linhaAtual) - getTime(linhaAnterior);
					}
					// Alarms
					if (difTempo > 60000L)
					{
						docAlarms.insertString(docAlarms.getLength(), linhaAtual + "\n", OK);
					}
					else if (difTempo < 20000L)
					{
						docAlarms.insertString(docAlarms.getLength(), linhaAtual + "\n", NOK);
					}
					else
					{
						docAlarms.insertString(docAlarms.getLength(), linhaAtual + "\n", medio);
					}
				}
				else if (linhaAtual.toLowerCase().contains("process"))
				{ // notice that is a new application in question
					docAlarms.insertString(docAlarms.getLength(), linhaAtual + "\n", null);
					linhaAnterior = null;
					difTempo = 0L;
				}
				else
				{ // just skip and write it black
					docAlarms.insertString(docAlarms.getLength(), linhaAtual + "\n", null);
				}
				
				if (linhaAnterior != null)
				{
					difTempo = getTime(linhaAtual) - getTime(linhaAnterior);
				}
				
				linhaAnterior = linhaAtual;
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, ex.getClass() + ":\n" + ex.getStackTrace());
			ex.printStackTrace();
		}
		
		txtPaneAlarms.setCaretPosition(0);
		
		Logger.log(Logger.TAG_COLORPRINTER, "Done");
	}
	
	/** Color Apps */
	static public void colorsApps(JTextPane txtPaneAlarms, String log)
	{
		long difTempo = 0L;
		StyledDocument docAlarms = (StyledDocument) txtPaneAlarms.getDocument();
		Style High_Freq = docAlarms.addStyle("High_Freq", null);
		StyleConstants.setForeground(High_Freq, Colors.vermelho);
		StyleConstants.setBackground(High_Freq, Colors.escuro);
		Style High = docAlarms.addStyle("High", null);
		StyleConstants.setForeground(High, Colors.vermelho);
		StyleConstants.setBackground(High, Colors.branco);
		Style Ok_Freq = docAlarms.addStyle("Ok_Freq", null);
		StyleConstants.setForeground(Ok_Freq, Colors.verde);
		StyleConstants.setBackground(Ok_Freq, Colors.escuro);
		Style Ok = docAlarms.addStyle("Ok", null);
		StyleConstants.setForeground(Ok, Colors.verde);
		StyleConstants.setBackground(Ok, Colors.branco);
		String[] lines = log.split("\n");
		txtPaneAlarms.setText("");
		float cons = 0;
		int scrn = -1;
		String linhaAnterior = null, linhaAtual = null;
		
		try
		{
			for (int i = 0; i < lines.length; i++)
			{
				if (lines[i].toLowerCase().contains("%  pid:"))
				{ // notice that is a new application in question
					linhaAtual = lines[i];
					cons = getCons(lines[i]);
					scrn = getScreen(lines[i]);
					if (linhaAnterior != null)
					{
						difTempo = getTime(linhaAtual) - getTime(linhaAnterior);
					}
					
					if (scrn == 2)
					{
						if (cons > 20F)
						{
							if (difTempo > 120000L)
								docAlarms.insertString(docAlarms.getLength(), lines[i] + " \t\t\n", High);
							else
								docAlarms.insertString(docAlarms.getLength(), lines[i] + " \t\t\n", High_Freq);
						}
						else
						{
							if (difTempo > 120000L)
								docAlarms.insertString(docAlarms.getLength(), lines[i] + " \t\t\n", Ok);
							else
								docAlarms.insertString(docAlarms.getLength(), lines[i] + " \t\t\n", Ok_Freq);
						}
					}
					else
					{
						if (cons > 4F)
						{
							if (difTempo > 120000L)
								docAlarms.insertString(docAlarms.getLength(), lines[i] + " \t\t\n", High);
							else
								docAlarms.insertString(docAlarms.getLength(), lines[i] + " \t\t\n", High_Freq);
						}
						else
						{
							if (difTempo > 120000L)
								docAlarms.insertString(docAlarms.getLength(), lines[i] + " \t\t\n", Ok);
							else
								docAlarms.insertString(docAlarms.getLength(), lines[i] + " \t\t\n", Ok_Freq);
						}
					}
					
					linhaAnterior = linhaAtual;
				}
				else
				{ // just skip and write it black
					docAlarms.insertString(docAlarms.getLength(), lines[i] + "\n", null);
					linhaAnterior = null;
					difTempo = 99000L;
				}
			}
		}
		catch (Exception ex)
		{
			JOptionPane.showMessageDialog(null, ex.getClass() + ":\n" + ex.getStackTrace());
			ex.printStackTrace();
		}
		
		txtPaneAlarms.setCaretPosition(0);
		
		Logger.log(Logger.TAG_COLORPRINTER, "Done");
	}
	
	public static void resetPanelStyle(JTextPane pane)
	{
		Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
		pane.setCharacterAttributes(defaultStyle, true);
	}
	
	/** Get Consumption */
	static private float getCons(String line)
	{
		return Float.parseFloat(line.replace(",", ".").substring(57, line.indexOf("%")));
	}
	
	/** Get Screen */
	static private int getScreen(String line)
	{
		if (line.contains("Screen OFF"))
			return 1;
		else if (line.contains("Screen ON"))
			return 2;
		else
			return 0;
	}
	
	/** Get Time */
	static private long getTime(String line)
	{
		long time = 0L;
		SimpleDateFormat dateFormat = new SimpleDateFormat("MM-dd HH:mm:ss");
		
		try
		{
			if (line.contains("AlarmManager"))
			{
				time = dateFormat.parse((String) line.subSequence(0, 13)).getTime();
			}
			else if ((!line.contains("AlarmManager"))
			         && (line.contains("Screen") || line.matches("Unknow.+pid")))
			{
				time = dateFormat.parse((String) line.substring(12, 26)).getTime();
			}
		}
		catch (ParseException e)
		{
			e.printStackTrace();
		}
		
		return time;
	}
}
