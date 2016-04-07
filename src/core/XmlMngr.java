package core;


import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;

import customobjects.CustomFilterItem;
import customobjects.CustomFiltersList;


/**
 * It deals with all XML features
 */
public class XmlMngr
{
	/**
	 * Variables
	 */
	private static Document userDocument;   // User XML file
	private static Document systemDocument; // System XML file
	private static Document messageDocument; // Message XML file
	private static Document filtersDocument; // Filters XML file
	private static Document uidsDocument;   // UIDs XML file
	private static Document reportDocument; // UIDs XML file
	                                         
	/**
	 * Initialize all variables and configure the class
	 */
	public static void initClass()
	{
		openXmlFiles();
	}
	
	/**
	 * Open XML files to be read/written
	 */
	private static void openXmlFiles()
	{
		try
		{
			// Create the XML document builder
			SAXBuilder builder = new SAXBuilder();
			
			// Create the document as to be read as a XML tree
			userDocument = (Document) builder.build(SharedObjs.userCfgFile);
			systemDocument = (Document) builder.build(SharedObjs.sytemCfgFile);
			messageDocument = (Document) builder.build(SharedObjs.messageCfgFile);
			filtersDocument = (Document) builder.build(SharedObjs.filtersFile);
			uidsDocument = (Document) builder.build(SharedObjs.uidsFile);
			reportDocument = (Document) builder.build(SharedObjs.reportFile);
		}
		catch (IOException | JDOMException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Returns value in user XML file.<br/>
	 * Returns null if element not found.
	 * <p>
	 * <strong> Example: </strong> XmlMngr.getUserValueOf(new String[] {"parser_pane","path"});
	 * <p>
	 * 
	 * @param path Array of strings to following value<br/>
	 * @return Return the value as a string <br/>
	 */
	public static String getUserValueOf(String path[])
	{
		Element requestedElement = userDocument.getRootElement();
		
		for (String item : path)
		{
			if (requestedElement != null)
				requestedElement = requestedElement.getChild(item);
		}
		
		if (requestedElement != null)
		{
			return requestedElement.getText();
		}
		
		return "null";
	}
	
	/**
	 * Update value in user XML file
	 * <p>
	 * <strong> Example: </strong> XmlMngr.getUserValueOf(new String[] {"parser_pane","path"}, "Ops, fail!");
	 * <p>
	 * 
	 * @param path Array of strings to following value<br/>
	 * @param value Value to be set as element text<br/>
	 * @return Return true if successful. False otherwise.<br/>
	 */
	public static boolean setUserValueOf(String path[], String value)
	{
		Element requestedElement = userDocument.getRootElement();
		
		for (String item : path)
		{
			if (requestedElement.getChild(item) != null)
			{
				requestedElement = requestedElement.getChild(item);
			}
			else
			{
				requestedElement.addContent(new Element(item));
				requestedElement = requestedElement.getChild(item);
			}
		}
		
		if (requestedElement != userDocument.getRootElement())
		{
			requestedElement.setText(value);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns value in system XML file.<br/>
	 * Returns null if element not found.
	 * <p>
	 * <strong> Example: </strong> XmlMngr.getSystemValueOf(new String[] {"configs","tool_name"});
	 * <p>
	 * 
	 * @param path Array of strings to following value<br/>
	 * @return Return the value as a string <br/>
	 */
	public static String getSystemValueOf(String path[])
	{
		Element requestedElement = systemDocument.getRootElement();
		
		for (String item : path)
		{
			requestedElement = requestedElement.getChild(item);
		}
		
		if (requestedElement != null)
		{
			return requestedElement.getText();
		}
		
		return "null";
	}
	
	/**
	 * Update value in system XML file
	 * <p>
	 * <strong> Example: </strong> XmlMngr.getSystemValueOf(new String[] {"configs","tool_name"}, "Ops, fail!");
	 * <p>
	 * 
	 * @param path Array of strings to following value<br/>
	 * @param value Value to be set as element text<br/>
	 * @return Return true if successful. False otherwise.<br/>
	 */
	public static boolean setSystemValueOf(String path[], String value)
	{
		Element requestedElement = systemDocument.getRootElement();
		
		for (String item : path)
		{
			requestedElement = requestedElement.getChild(item);
		}
		
		if (requestedElement != null)
		{
			requestedElement.setText(value);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns value in message XML file.<br/>
	 * Returns null if element not found.
	 * <p>
	 * <strong> Example: </strong> XmlMngr.getMessageValueOf(new String[] {"messages","error"});
	 * <p>
	 * 
	 * @param path Array of {@link String} to following value<br/>
	 * @return Return the value as a string <br/>
	 */
	public static String getMessageValueOf(String path[])
	{
		Element requestedElement = messageDocument.getRootElement();
		
		for (String item : path)
		{
			requestedElement = requestedElement.getChild(item);
		}
		
		if (requestedElement != null)
		{
			return requestedElement.getText();
		}
		
		return "null";
	}
	
	/**
	 * Update value in message XML file.
	 * <p>
	 * <strong> Example: </strong> XmlMngr.setMessageValueOf(new String[] {"messages","error"}, "Ops, fail!");
	 * <p>
	 * 
	 * @param path Array of strings to following value<br/>
	 * @param value Value to be set as element text<br/>
	 * @return Return true if successful. False otherwise.<br/>
	 */
	public static boolean setMessageValueOf(String path[], String value)
	{
		Element requestedElement = messageDocument.getRootElement();
		
		for (String item : path)
		{
			requestedElement = requestedElement.getChild(item);
		}
		
		if (requestedElement != null)
		{
			requestedElement.setText(value);
			return true;
		}
		
		return false;
	}
	
	/**
	 * Returns value in user XML file.<br/>
	 * Returns null if element not found.
	 * <p>
	 * <strong> Example: </strong> XmlMngr.getUserValueOf(new String[] {"parser_pane","path"});
	 * <p>
	 * 
	 * @param path Array of strings to following value<br/>
	 * @return Return the value as a string <br/>
	 */
	public static String getUidsValueOf(String path[])
	{
		Element requestedElement = uidsDocument.getRootElement();
		path[path.length - 1] = "u_" + path[path.length - 1];
		
		for (String item : path)
		{
			requestedElement = requestedElement.getChild(item);
		}
		
		if (requestedElement != null)
		{
			return requestedElement.getText();
		}
		
		return "null";
	}
	
	/**
	 * Update value in user XML file
	 * <p>
	 * <strong> Example: </strong> XmlMngr.getUserValueOf(new String[] {"parser_pane","path"}, "Ops, fail!");
	 * <p>
	 * 
	 * @param path Array of strings to following value<br/>
	 * @param value Value to be set as element text<br/>
	 * @return Return true if successful. False otherwise.<br/>
	 */
	public static boolean setUidsValueOf(String path[], String value)
	{
		Element requestedElement = uidsDocument.getRootElement();
		path[path.length - 1] = "u_" + path[path.length - 1];
		
		for (String item : path)
		{
			if (requestedElement.getChild(item) != null)
			{
				requestedElement = requestedElement.getChild(item);
			}
			else
			{
				requestedElement.addContent(new Element(item));
				requestedElement = requestedElement.getChild(item);
			}
		}
		
		if (requestedElement != userDocument.getRootElement())
		{
			requestedElement.setText(value);
			return true;
		}
		
		return false;
	}
	
	public static void clearUids()
	{
		Element requestedElement = uidsDocument.getRootElement();
		requestedElement = requestedElement.getChild("Known");
		requestedElement.removeContent();
	}
	
	public static HashMap<String, String> getAllUids()
	{
		Element requestedElement = uidsDocument.getRootElement();
		requestedElement = requestedElement.getChild("Known");
		
		HashMap<String, String> hm = new HashMap<String, String>();
		for (Element e : requestedElement.getChildren())
		{
			hm.put(e.getName(), e.getText().replace("u_", ""));
			System.out.println(e.getName() + " - " + e.getText());
		}
		
		return hm;
	}
	
	/**
	 * Get user filter item
	 * 
	 * @param name Name of the filter
	 * @return Filter item as {@link CustomFilterItem}. Null if not found.
	 */
	public static CustomFilterItem getMyFiltersValueOf(String name)
	{
		for (Element requestedElement : filtersDocument.getRootElement().getChild("myFilters").getChildren())
		{
			if (requestedElement.getChildText("name").equals(name))
			{
				CustomFilterItem filter;
				filter = new CustomFilterItem();
				updateFilter(requestedElement, filter);
				
				return filter;
			}
		}
		
		return null;
	}
	
	/**
	 * Set a user filter
	 * @param filter {@link CustomFilterItem}
	 * @return true if successful. false if not.
	 */
	public static boolean setMyFiltersValueOf(CustomFilterItem filter)
	{
		if (!filter.getName().equals(""))
		{
			Element myFiltersElement = filtersDocument.getRootElement().getChild("myFilters");
			
			for (Element requestedElement : myFiltersElement.getChildren())
			{
				if (requestedElement.getChildText("name").equals(filter.getName()))
				{
					updateElement(requestedElement, filter);
					
					return true;
				}
			}
			
			myFiltersElement.addContent(createElement(filter, myFiltersElement.getChildren().size() + 1));
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get shared filter item
	 * 
	 * @param name Name of the filter
	 * @param owner Filter owner name
	 * @return Filter item as {@link CustomFilterItem}. Null if not found.
	 */
	public static CustomFilterItem getSharedFiltersValueOf(String name, String owner)
	{
		for (Element requestedElement : filtersDocument.getRootElement().getChild("sharedFilters").getChildren())
		{
			if (requestedElement.getName().equals(name))
			{
				CustomFilterItem filter;
				filter = new CustomFilterItem();
				updateFilter(requestedElement, filter);
				
				return filter;
			}
		}
		
		return null;
	}
	
	/**
	 * Set a shared filter
	 * @param filter {@link CustomFilterItem}
	 * @return true if successful. false if not.
	 */
	public static boolean setSharedFiltersValueOf(CustomFilterItem filter)
	{
		Element sharedFiltersElement = filtersDocument.getRootElement().getChild("sharedFilters");
		
		if (!filter.getName().equals(""))
		{
			for (Element requestedElement : sharedFiltersElement.getChildren())
			{
				if (requestedElement.getChildText("name").equals(filter.getName()))
				{
					updateElement(requestedElement, filter);
					return true;
				}
			}
			
			sharedFiltersElement.addContent(createElement(filter, sharedFiltersElement.getChildren().size() + 1));
			return true;
		}
		
		return false;
	}
	
	public static CustomFilterItem getActiveFiltersValueOf(String name, String owner)
	{
		for (Element requestedElement : filtersDocument.getRootElement().getChild("activeFilters").getChildren())
		{
			if (requestedElement.getChildText("name").equals(name))
			{
				CustomFilterItem filter;
				filter = new CustomFilterItem();
				updateFilter(requestedElement, filter);
				
				return filter;
			}
		}
		
		return null;
	}
	
	public static boolean setActiveFiltersValueOf(CustomFilterItem filter)
	{
		if (!filter.getName().equals(""))
		{
			Element activeFiltersElement = filtersDocument.getRootElement().getChild("activeFilters");
			
			for (Element requestedElement : activeFiltersElement.getChildren())
			{
				if (requestedElement.getChildText("name").equals(filter.getName()))
				{
					updateElement(requestedElement, filter);
					return true;
				}
			}
			
			activeFiltersElement.addContent(createElement(filter, activeFiltersElement.getChildren().size() + 1));
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Get all user filters
	 * 
	 * @return Array of {@link CustomFilterItem}
	 */
	public static CustomFiltersList getAllMyFilters()
	{
		Element requestedElement = filtersDocument.getRootElement().getChild("myFilters");
		CustomFiltersList filters = new CustomFiltersList();
		CustomFilterItem aux;
		
		for (Element filterElement : requestedElement.getChildren())
		{
			aux = new CustomFilterItem();
			updateFilter(filterElement, aux);
			filters.add(aux);
		}
		
		return filters;
	}
	
	/**
	 * Get all shared filters
	 * 
	 * @return Array of {@link CustomFilterItem}
	 */
	public static CustomFiltersList getAllSharedFilters()
	{
		Element requestedElement = filtersDocument.getRootElement().getChild("sharedFilters");
		CustomFiltersList filters = new CustomFiltersList();
		CustomFilterItem aux;
		
		for (int i = 0; i < requestedElement.getChildren().size(); i++)
		{
			Element filterElement = requestedElement.getChildren().get(i);
			aux = new CustomFilterItem();
			updateFilter(filterElement, aux);
			filters.add(aux);
		}
		
		return filters;
	}
	
	/**
	 * Get all active shared filters
	 * 
	 * @return Array of {@link CustomFilterItem}
	 */
	public static CustomFiltersList getAllActiveFilters()
	{
		Element requestedElement = filtersDocument.getRootElement().getChild("activeFilters");
		CustomFiltersList filters = new CustomFiltersList();
		CustomFilterItem aux;
		
		for (int i = 0; i < requestedElement.getChildren().size(); i++)
		{
			Element filterElement = requestedElement.getChildren().get(i);
			aux = new CustomFilterItem();
			updateFilter(filterElement, aux);
			filters.add(aux);
		}
		
		return filters;
	}
	
	/**
	 * @param filters
	 * @return
	 */
	public static boolean addMyFilters(CustomFiltersList filters)
	{
		for (CustomFilterItem filter : filters)
		{
			setMyFiltersValueOf(filter);
		}
		
		return true;
	}
	
	/**
	 * @param filters
	 * @return
	 */
	public static boolean addSharedFilters(CustomFiltersList filters)
	{
		for (CustomFilterItem filter : filters)
		{
			setSharedFiltersValueOf(filter);
		}
		
		return true;
	}
	
	/**
	 * @param filters
	 * @return
	 */
	public static boolean addActiveFilters(CustomFiltersList filters)
	{
		for (CustomFilterItem filter : filters)
		{
			setActiveFiltersValueOf(filter);
		}
		
		return true;
	}
	
	/**
	 * @param filter
	 * @return
	 */
	public static boolean removeMyFiltersValueOf(CustomFilterItem filter)
	{
		Element myFiltersElement = filtersDocument.getRootElement().getChild("myFilters");
		
		for (Element requestedElement : myFiltersElement.getChildren())
		{
			if (requestedElement.getChildText("name").equals(filter.getName()))
			{
				myFiltersElement.removeChild(requestedElement.getName());
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return
	 */
	public static boolean removeAllMyFilters()
	{
		Element myFiltersElement = filtersDocument.getRootElement().getChild("myFilters");
		
		if (myFiltersElement != null)
			myFiltersElement.removeContent();
		else
			return false;
		
		return true;
	}
	
	/**
	 * @param filter
	 * @return
	 */
	public static boolean removeSharedFiltersValueOf(CustomFilterItem filter)
	{
		Element sharedFiltersElement = filtersDocument.getRootElement().getChild("sharedFilters");
		
		for (Element requestedElement : sharedFiltersElement.getChildren())
		{
			if (requestedElement.getChildText("name").equals(filter.getName()))
			{
				sharedFiltersElement.removeChild(requestedElement.getName());
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return
	 */
	public static boolean removeAllSharedFilters()
	{
		Element sharedFiltersElement = filtersDocument.getRootElement().getChild("sharedFilters");
		
		if (sharedFiltersElement != null)
			sharedFiltersElement.removeContent();
		else
			return false;
		
		return true;
	}
	
	/**
	 * @param filter
	 * @return
	 */
	public static boolean removeActiveFiltersValueOf(CustomFilterItem filter)
	{
		Element activeFiltersElement = filtersDocument.getRootElement().getChild("activeFilters");
		
		for (Element requestedElement : activeFiltersElement.getChildren())
		{
			if (requestedElement.getChildText("name").equals(filter.getName()))
			{
				activeFiltersElement.removeChild(requestedElement.getName());
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * @return
	 */
	public static boolean removeAllActiveFilters()
	{
		Element activeFiltersElement = filtersDocument.getRootElement().getChild("activeFilters");
		
		if (activeFiltersElement != null)
			activeFiltersElement.removeContent();
		else
			return false;
		
		return true;
	}
	
	/**
	 * @param pass
	 */
	public static void savePass(String pass)
	{
		try
		{
			BufferedWriter writer = new BufferedWriter(new FileWriter(SharedObjs.pwdFile));
			writer.write(pass);
			writer.close();
		}
		catch (IOException e)
		{
			Logger.log(Logger.TAG_XMLMNGR, "Error saving password");
			e.printStackTrace();
		}
	}
	
	public static HashMap<String, String> getBatteryCapacityItems()
	{
		Element requestedElement = userDocument.getRootElement().getChild("bat_cap");
		HashMap<String, String> items = new HashMap<String, String>();
		
		for (Element element : requestedElement.getChildren())
		{
			items.put(element.getName().toString(), element.getText());
		}
		
		return items;
	}
	
	public static HashMap<String, String> getDiagDupItems()
	{
		Element requestedElement = userDocument.getRootElement().getChild("diag_dup");
		HashMap<String, String> items = new HashMap<String, String>();
		
		for (Element element : requestedElement.getChildren())
		{
			items.put(element.getName(), element.getText());
		}
		
		return items;
	}
	
	public static void setBatteryCapacityItems(HashMap<String, String> items)
	{
		Element element = userDocument.getRootElement().getChild("bat_cap");
		
		for (String value : items.keySet())
		{
			Element e = element.getChild(value);
			if (e == null)
			{
				element.addContent(new Element(value));
				e = element.getChild(value).setText(items.get(value));
			}
			else
			{
				e.setText(items.get(value));
			}
		}
	}
	
	public static void setDiagDupItems(HashMap<String, String> items)
	{
		Element element = userDocument.getRootElement().getChild("diag_dup");
		
		for (String value : items.keySet())
		{
			Element e = element.getChild(value);
			if (e == null)
			{
				element.addContent(new Element(value));
				e = element.getChild(value).setText(items.get(value));
			}
			else
			{
				e.setText(items.get(value));
			}
		}
	}
	
	public static void setBatteryCapacityItem(String product, String value)
	{
		Element element = userDocument.getRootElement().getChild("bat_cap").getChild(product);
		element.setText(value);
	}
	
	public static void setDiagDupItem(String product, String value)
	{
		Element element = userDocument.getRootElement().getChild("diag_dup").getChild(product);
		element.setText(value);
	}
	
	private static Element createElement(CustomFilterItem filter, int index)
	{
		Element xmlElement = new Element("item_" + index);
		xmlElement.addContent(new Element("name").setText(filter.getName()));
		xmlElement.addContent(new Element("regex").setText(filter.getRegex()));
		xmlElement.addContent(new Element("header").setText(filter.getHeader()));
		xmlElement.addContent(new Element("owner").setText(filter.getOwner()));
		xmlElement.addContent(new Element("main").setText("" + filter.isMain()));
		xmlElement.addContent(new Element("system").setText("" + filter.isSystem()));
		xmlElement.addContent(new Element("kernel").setText("" + filter.isKernel()));
		xmlElement.addContent(new Element("radio").setText("" + filter.isRadio()));
		xmlElement.addContent(new Element("bugreport").setText("" + filter.isBugreport()));
		xmlElement.addContent(new Element("routput").setText("" + filter.isRoutput()));
		xmlElement.addContent(new Element("shared").setText("" + filter.isShared()));
		xmlElement.addContent(new Element("editable").setText("" + filter.isPublic()));
		xmlElement.addContent(new Element("active").setText("" + filter.isActive()));
		xmlElement.addContent(new Element("last_update").setText("" + filter.getLastUpdate()));
		return xmlElement;
	}
	
	private static void updateElement(Element requestedElement, CustomFilterItem filter)
	{
		requestedElement.getChild("name").setText(filter.getName());
		requestedElement.getChild("regex").setText(filter.getRegex());
		requestedElement.getChild("header").setText(filter.getHeader());
		requestedElement.getChild("owner").setText(filter.getOwner());
		requestedElement.getChild("main").setText("" + filter.isMain());
		requestedElement.getChild("system").setText("" + filter.isSystem());
		requestedElement.getChild("kernel").setText("" + filter.isKernel());
		requestedElement.getChild("radio").setText("" + filter.isRadio());
		requestedElement.getChild("bugreport").setText("" + filter.isBugreport());
		requestedElement.getChild("routput").setText("" + filter.isRoutput());
		requestedElement.getChild("shared").setText("" + filter.isShared());
		requestedElement.getChild("editable").setText("" + filter.isPublic());
		requestedElement.getChild("active").setText("" + filter.isActive());
		requestedElement.getChild("last_update").setText("" + filter.getLastUpdate());
	}
	
	private static void updateFilter(Element requestedElement, CustomFilterItem filter)
	{
		filter.setName(requestedElement.getChildText("name"));
		filter.setRegex(requestedElement.getChildText("regex"));
		filter.setHeader(requestedElement.getChildText("header"));
		filter.setOwner(requestedElement.getChildText("owner"));
		filter.setMain(Boolean.parseBoolean(requestedElement.getChildText("main")));
		filter.setSystem(Boolean.parseBoolean(requestedElement.getChildText("system")));
		filter.setKernel(Boolean.parseBoolean(requestedElement.getChildText("kernel")));
		filter.setRadio(Boolean.parseBoolean(requestedElement.getChildText("radio")));
		filter.setBugreport(Boolean.parseBoolean(requestedElement.getChildText("bugreport")));
		filter.setRoutput(Boolean.parseBoolean(requestedElement.getChildText("routput")));
		filter.setShared(Boolean.parseBoolean(requestedElement.getChildText("shared")));
		filter.setEditable(Boolean.parseBoolean(requestedElement.getChildText("editable")));
		filter.setActive(Boolean.parseBoolean(requestedElement.getChildText("active")));
		filter.setLastUpdate(requestedElement.getChildText("last_update"));
	}
	
	/**
	 * Save and close all XMLs files.
	 */
	public static void closeXmls()
	{
		XMLOutputter xmlOutputter = new XMLOutputter(Format.getPrettyFormat());
		
		// Enable the following line to output XML to console for
		// debugging
		// xmlOutputter.output(doc, System.out);
		
		// Save each XML file.
		try
		{
			xmlOutputter.output(userDocument, new FileOutputStream(SharedObjs.userCfgFile));
			xmlOutputter.output(systemDocument, new FileOutputStream(SharedObjs.sytemCfgFile));
			xmlOutputter.output(messageDocument, new FileOutputStream(SharedObjs.messageCfgFile));
			xmlOutputter.output(filtersDocument, new FileOutputStream(SharedObjs.filtersFile));
			xmlOutputter.output(uidsDocument, new FileOutputStream(SharedObjs.uidsFile));
			xmlOutputter.output(reportDocument, new FileOutputStream(SharedObjs.reportFile));
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
}
