package style;


import java.awt.Component;
import java.io.File;

import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.filechooser.FileSystemView;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;


@SuppressWarnings("serial")
public class FileTreeNodeRenderer extends DefaultTreeCellRenderer
{
	private FileSystemView fileSystemView;
	private JLabel		   nodeLabel;
	
	public FileTreeNodeRenderer()
	{
		fileSystemView = FileSystemView.getFileSystemView();
		nodeLabel = new JLabel();
		nodeLabel.setOpaque(true);
	}
	
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded,
												  boolean leaf, int row, boolean hasFocus)
	{
		DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
		File file = (File) node.getUserObject();
		
		if (file != null)
		{
			if (file.isFile())
			{
				long size;
				String sizeOfFile;
				
				size = file.length();
				sizeOfFile = String.valueOf(size) + " Bytes";
				
				if (size > 1024)
				{
					size = size / 1024;
					sizeOfFile = String.valueOf(size) + " KB";
					
					if (size > 1024)
					{
						size = size / 1024;
						sizeOfFile = String.valueOf(size) + " MB";
					}
				}
				
				String labelText = fileSystemView.getSystemDisplayName(file) + " - " + sizeOfFile;
				nodeLabel.setText(labelText);
			}
			
			else
			{
				nodeLabel.setText(fileSystemView.getSystemDisplayName(file));
			}
			nodeLabel.setIcon(fileSystemView.getSystemIcon(file));
		}
		
		if (sel)
		{
			nodeLabel.setBackground(backgroundSelectionColor);
			nodeLabel.setForeground(textSelectionColor);
		}
		else
		{
			nodeLabel.setBackground(backgroundNonSelectionColor);
			nodeLabel.setForeground(textNonSelectionColor);
		}
		
		return nodeLabel;
	}
}
