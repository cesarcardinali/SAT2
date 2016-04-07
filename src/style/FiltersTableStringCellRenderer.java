package style;


import java.awt.Component;

import javax.swing.JLabel;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;


@SuppressWarnings("serial")
public class FiltersTableStringCellRenderer extends DefaultTableCellRenderer
{
	
	public FiltersTableStringCellRenderer()
	{
		super();
	}
	
	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
												   boolean hasFocus, int row, int column)
	{
		Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
		JLabel val = (JLabel) c;
		
		if (isSelected)
		{
			setForeground(table.getSelectionForeground());
			setBackground(table.getSelectionBackground());
		}
		else
		{
			if (row % 2 == 1)
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
		
		// Exclusive mods for tables using CRsTableModel
		if (table.getModel().getClass().equals(MyFiltersTableModel.class))
		{
			if (isSelected && hasFocus && column == MyFiltersTableModel.HIDDEN_INDEX)
			{
				if ((table.getModel().getRowCount() - 1) == row
					&& !((MyFiltersTableModel) table.getModel()).hasEmptyRow())
				{
					((MyFiltersTableModel) table.getModel()).addEmptyRow();
					table.setRowSelectionInterval(table.getRowCount() - 1, table.getRowCount() - 1);
					table.setColumnSelectionInterval(table.getColumnCount() - 1, 0);
				}
			}
		}
		
		// Exclusive mods for tables using SharedFiltersTableModel
		if (table.getModel().getClass().equals(SharedFiltersTableModel.class))
		{
			if (table.getModel().getValueAt(row, 1).equals("Public")
				|| table.getModel().getValueAt(row, 1).equals(""))
			{
				val.setForeground(Colors.verdeEscuro);
			}
		}
		
		return c;
	}
	
	public void tableChanged(TableModelEvent evt)
	{
		if (evt.getType() == TableModelEvent.UPDATE)
		{
			int column = evt.getColumn();
			int row = evt.getFirstRow();
			
			System.out.println("row: " + row + " column: " + column);
			
			/*
			 * Focus next cell table.setColumnSelectionInterval(column + 1, column + 1); table.setRowSelectionInterval(row, row);
			 */
			
		}
	}
	
}
