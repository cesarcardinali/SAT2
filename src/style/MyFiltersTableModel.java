package style;


import javax.swing.table.AbstractTableModel;

import core.SharedObjs;
import customobjects.CustomFilterItem;
import customobjects.CustomFiltersList;


@SuppressWarnings({"serial"})
public class MyFiltersTableModel extends AbstractTableModel
{
	public static final int	ID_INDEX	  = 0;
	public static final int	NAME_INDEX	  = 1;
	public static final int	HEADER_INDEX  = 2;
	public static final int	REGEX_INDEX	  = 3;
	public static final int	MAIN_INDEX	  = 4;
	public static final int	SYSTEM_INDEX  = 5;
	public static final int	KERNEL_INDEX  = 6;
	public static final int	RADIO_INDEX	  = 7;
	public static final int	BREPORT_INDEX = 8;
	public static final int	ROUTPUT_INDEX = 9;
	public static final int	SHARED_INDEX  = 10;
	public static final int	PUBLIC_INDEX  = 11;
	public static final int	ACTIVE_INDEX  = 12;
	public static final int	UPDATE_INDEX  = 13;
	public static final int	HIDDEN_INDEX  = 14;
	
	protected String[]			columnNames;
	protected CustomFiltersList	dataVector;
	
	/**
	 * @param columnNames
	 */
	public MyFiltersTableModel(String[] columnNames)
	{
		dataVector = new CustomFiltersList();
		this.columnNames = columnNames;
	}
	
	/**
	 * 
	 */
	public MyFiltersTableModel()
	{
		dataVector = new CustomFiltersList();
		this.columnNames = new String[] {"#",
										 "Name",
										 "Header",
										 "Regex",
										 "Main",
										 "System",
										 "Kernel",
										 "Radio",
										 "BugRep",
										 "Routput",
										 "Shared",
										 "Public",
										 "Active",
										 "Updated",
										 ""};
	}
	
	@Override
	public String getColumnName(int column)
	{
		return columnNames[column];
	}
	
	@Override
	public boolean isCellEditable(int row, int column)
	{
		if (column == ID_INDEX || column == HIDDEN_INDEX || column == UPDATE_INDEX)
			return false;
		else if (dataVector.get(row).getOwner().equals("Public"))
			return false;
		else if (column == PUBLIC_INDEX)
			if (dataVector.get(row).isShared())
				return true;
			else
				return false;
		return true;
	}
	
	@Override
	public Class<?> getColumnClass(int column)
	{
		switch (column)
		{
			case ID_INDEX:
				return Integer.class;
			case NAME_INDEX:
			case REGEX_INDEX:
			case HEADER_INDEX:
			case UPDATE_INDEX:
			case HIDDEN_INDEX:
				return String.class;
			default:
				return Boolean.class;
		}
	}
	
	@Override
	public Object getValueAt(int row, int column)
	{
		CustomFilterItem record = (CustomFilterItem) dataVector.get(row);
		switch (column)
		{
			case ID_INDEX:
				return dataVector.indexOf(record);
			case NAME_INDEX:
				return record.getName();
			case REGEX_INDEX:
				return record.getRegex();
			case HEADER_INDEX:
				return record.getHeader();
			case MAIN_INDEX:
				return record.isMain();
			case SYSTEM_INDEX:
				return record.isSystem();
			case KERNEL_INDEX:
				return record.isKernel();
			case RADIO_INDEX:
				return record.isRadio();
			case BREPORT_INDEX:
				return record.isBugreport();
			case ROUTPUT_INDEX:
				return record.isRoutput();
			case SHARED_INDEX:
				return record.isShared();
			case PUBLIC_INDEX:
				return record.isPublic();
			case ACTIVE_INDEX:
				return record.isActive();
			case UPDATE_INDEX:
				return record.getLastUpdate();
			default:
				return new Object();
		}
	}
	
	@Override
	public void setValueAt(Object value, int row, int column)
	{
		CustomFilterItem record = (CustomFilterItem) dataVector.get(row);
		switch (column)
		{
			case ID_INDEX:
				dataVector.indexOf(record);
			case NAME_INDEX:
				record.setName((String) value);
				break;
			case REGEX_INDEX:
				record.setRegex((String) value);
				break;
			case HEADER_INDEX:
				record.setHeader((String) value);
				break;
			case MAIN_INDEX:
				record.setMain((Boolean) value);
				break;
			case SYSTEM_INDEX:
				record.setSystem((Boolean) value);
				break;
			case KERNEL_INDEX:
				record.setKernel((Boolean) value);
				break;
			case RADIO_INDEX:
				record.setRadio((Boolean) value);
				break;
			case BREPORT_INDEX:
				record.setBugreport((Boolean) value);
				break;
			case ROUTPUT_INDEX:
				record.setRoutput((Boolean) value);
				break;
			case SHARED_INDEX:
				record.setShared((Boolean) value);
				break;
			case PUBLIC_INDEX:
				record.setEditable((Boolean) value);
				break;
			case ACTIVE_INDEX:
				record.setActive((Boolean) value);
				break;
			case UPDATE_INDEX:
				record.setLastUpdate((String) value);
				break;
			default:
				System.out.println("invalid index");
		}
		fireTableCellUpdated(row, column);
	}
	
	@Override
	public int getRowCount()
	{
		return dataVector.size();
	}
	
	@Override
	public int getColumnCount()
	{
		return columnNames.length;
	}
	
	public boolean hasEmptyRow()
	{
		if (dataVector.size() == 0)
			return false;
			
		CustomFilterItem filterItem = (CustomFilterItem) dataVector.get(dataVector.size() - 1);
		
		if (filterItem.getName().trim().equals(""))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean hasFilterName(CustomFilterItem lookForFilter)
	{
		if (dataVector.size() == 0)
			return false;
		CustomFilterItem filter;
		
		for (int i=0; i < dataVector.size()-1; i++)
		{
			filter = dataVector.get(i);
			if (filter.getName().trim().equals(lookForFilter.getName()) && !filter.getLastUpdate().equals(lookForFilter.getLastUpdate()))
			{
				return true;
			}
		}
		
		return false;
	}
	
	public void addEmptyRow()
	{
		CustomFilterItem newItem = new CustomFilterItem();
		newItem.setOwner(SharedObjs.getUser());
		dataVector.add(newItem);
		fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
	}
	
	public void addRow(CustomFilterItem filter)
	{
		dataVector.add(filter);
		fireTableRowsInserted(dataVector.size() - 1, dataVector.size() - 1);
	}
	
	public void removeRow(int id)
	{
		dataVector.remove(id);
		fireTableRowsDeleted(0, dataVector.size());
	}
	
	public CustomFilterItem getElementAt(int row)
	{
		return dataVector.get(row);
	}
	
	public CustomFiltersList getFilterElements()
	{
		return dataVector;
	}
}
