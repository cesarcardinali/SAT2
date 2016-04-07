package views.secondarypanes;


import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URI;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;


public class ParallelTextPopup extends JFrame
{
	
	private JPanel      contentPane;
	private JScrollPane scrollPane;
	private JTextPane   textCrKeyList;
	private JScrollPane scrollPane_1;
	private JTextPane   textCrResolution;
	private JButton     btnOpen;
	ParallelTextPopup view;
	
	/**
	 * Create the frame.
	 */
	public ParallelTextPopup()
	{
		view = this;
		setTitle("Automatically closed CRs");
		setResizable(false);
		setAlwaysOnTop(false);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 274, 434);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		GridBagLayout gbl_contentPane = new GridBagLayout();
		gbl_contentPane.columnWidths = new int[] {129, 129, 0};
		gbl_contentPane.rowHeights = new int[] {373, 0, 0};
		gbl_contentPane.columnWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
		gbl_contentPane.rowWeights = new double[] {0.0, 0.0, Double.MIN_VALUE};
		contentPane.setLayout(gbl_contentPane);
		
		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.gridx = 0;
		gbc_scrollPane.gridy = 0;
		contentPane.add(scrollPane, gbc_scrollPane);
		
		textCrKeyList = new JTextPane();
		textCrKeyList.setEditable(false);
		scrollPane.setViewportView(textCrKeyList);
		
		textCrKeyList.setText("");
		
		scrollPane_1 = new JScrollPane();
		GridBagConstraints gbc_scrollPane_1 = new GridBagConstraints();
		gbc_scrollPane_1.insets = new Insets(0, 0, 5, 0);
		gbc_scrollPane_1.fill = GridBagConstraints.BOTH;
		gbc_scrollPane_1.gridx = 1;
		gbc_scrollPane_1.gridy = 0;
		contentPane.add(scrollPane_1, gbc_scrollPane_1);
		
		textCrResolution = new JTextPane();
		textCrResolution.setEditable(false);
		scrollPane_1.setViewportView(textCrResolution);
		textCrResolution.setText("");
		
		btnOpen = new JButton("Open on browser");
		btnOpen.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				btnOpenAction();
			}
		});
		GridBagConstraints gbc_btnOpen = new GridBagConstraints();
		gbc_btnOpen.gridwidth = 2;
		gbc_btnOpen.insets = new Insets(0, 0, 0, 5);
		gbc_btnOpen.gridx = 0;
		gbc_btnOpen.gridy = 1;
		contentPane.add(btnOpen, gbc_btnOpen);
	}
	
	
	public void addItemList1(String item)
	{
		textCrKeyList.setText(textCrKeyList.getText() + item + "\n");
	}
	
	public void addItemList2(String item)
	{
		textCrResolution.setText(textCrResolution.getText() + item + "\n");
	}
	
	public void clear()
	{
		textCrKeyList.setText("");
		textCrResolution.setText("");
	}
	
	private void btnOpenAction()
	{
		for (String s : textCrKeyList.getText().split("\n"))
		{
			try
			{
				s = s.replaceAll("\n", "");
				s = s.replaceAll("\r", "");
				s = s.replaceAll(" ", "");
				Desktop.getDesktop().browse(new URI("http://idart.mot.com/browse/" + s));
				Thread.sleep(500);
			}
			catch (Exception ex)
			{
				JOptionPane.showMessageDialog(this, "Exception: " + ex.getMessage());
			}
		}
	}
	
	public void showWindow()
	{
		setLocationRelativeTo(null);
		setVisible(true);
	}
	
	public void setListTitle(String title)
	{
		view.setTitle(title);
	}
}
