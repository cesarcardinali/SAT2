package style;


import java.awt.Component;

import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;


@SuppressWarnings("serial")
public class FiltersTableCheckboxCellRenderer extends JCheckBox implements TableCellRenderer
{
	public FiltersTableCheckboxCellRenderer()
	{
		setHorizontalAlignment(JLabel.CENTER);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
												   boolean hasFocus, int row, int column)
	{
		
		if (isSelected)
		{
			setForeground(table.getSelectionForeground());
			// super.setBackground(table.getSelectionBackground());
			setBackground(table.getSelectionBackground());
		}
		else
		{
			if(row%2 == 1)
			{
				setForeground(table.getForeground());
				setBackground(Colors.cinzaClaro);
			}
			else
			{
				setForeground(table.getForeground());
				setBackground(table.getBackground());
			}
		}
		
		
		
		setSelected((value != null && ((Boolean) value).booleanValue()));
		
		return this;
	}
}
