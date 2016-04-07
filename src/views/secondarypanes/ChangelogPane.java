package views.secondarypanes;


import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import style.Icons;


public class ChangelogPane extends JFrame
{
	
	private JPanel      contentPane;
	private JScrollPane scrollPane;
	private JTextArea   textArea;
	
	/**
	 * Launch the application.
	 */
	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					ChangelogPane frame = new ChangelogPane();
					frame.setVisible(true);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}
	
	/**
	 * Create the frame.
	 */
	@SuppressWarnings("resource")
    public ChangelogPane()
	{
		setType(Type.POPUP);
		setTitle("Changelog");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(100, 100, 1103, 900);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		setIconImage(Icons.changelog.getImage());
		
		scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		
		textArea = new JTextArea();
		textArea.setEditable(false);
		textArea.setTabSize(4);
		textArea.setText("Changelog:\n\n");
		
		try
		{
			BufferedReader br = new BufferedReader(new FileReader(new File("Data/update/changelog.txt")));
			String lineRead = "";
			
			while ((lineRead = br.readLine()) != null)
			{
				textArea.setText(textArea.getText() + lineRead + "\n");
			}
		}
		catch (FileNotFoundException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
		scrollPane.setViewportView(textArea);
	}
	
}
