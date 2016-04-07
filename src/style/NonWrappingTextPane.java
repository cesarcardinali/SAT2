package style;


import java.awt.Component;

import javax.swing.JTextPane;
import javax.swing.plaf.ComponentUI;
import javax.swing.text.StyledDocument;


@SuppressWarnings("serial")
public class NonWrappingTextPane extends JTextPane
{
	private boolean wrapText = false;
	
	public boolean isWrapText()
	{
		return wrapText;
	}
	
	public void setWrapText(boolean wrapText)
	{
		this.wrapText = wrapText;
		updateUI();
	}
	
	public NonWrappingTextPane()
	{
		super();
	}
	
	public NonWrappingTextPane(StyledDocument doc)
	{
		super(doc);
	}
	
	/*
	 * Override getScrollableTracksViewportWidth to preserve the full width of the text (non-Javadoc)
	 * 
	 * @see javax.swing.JEditorPane#getScrollableTracksViewportWidth()
	 */
	public boolean getScrollableTracksViewportWidth()
	{
		Component parent = getParent();
		ComponentUI ui = getUI();
		if (!wrapText)
			return parent != null ? (ui.getPreferredSize(this).width <= parent.getSize().width) : true;
		else
			return super.getScrollableTracksViewportWidth();
	}
}