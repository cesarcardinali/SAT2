package style;


import java.awt.Color;
import java.awt.Component;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableCellRenderer;


@SuppressWarnings("serial")
public class FiltersTableIntCellRenderer extends DefaultTableCellRenderer
{
	public FiltersTableIntCellRenderer()
	{
		setHorizontalAlignment(JLabel.RIGHT);
	}
	
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
												   boolean hasFocus, int row, int column)
	{
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		JLabel val = (JLabel) c;
		
		if (isSelected)
		{
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
			val.setFont(new Font("Tahoma", Font.BOLD, 9));
			val.setForeground(Color.black);
		}
		else
		{
			setForeground(table.getTableHeader().getForeground());
			setBackground(table.getTableHeader().getBackground());
			
		}
		
		if (isSelected && hasFocus)
		{
			System.out.println("Int: " + MyFiltersTableModel.HIDDEN_INDEX + " - " + column);
			if (column == 0)
			{
				table.setColumnSelectionInterval(table.getColumnCount() - 1, 0);
			}
		}
		
		return val;
	}
}
