package wife.heartcough;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;;

public class CommandProgressBar {
	
	private static int WIDTH;
	private static int HEIGHT;
	
	static {
		WIDTH = 450;
		HEIGHT = 200;
	}
	
	private static JProgressBar progressBar;
	private static JLabel fileName;
	private static JTextArea logger;
	private static int percent = 0;
	
	public static void show() {
		progressBar = new JProgressBar();
		progressBar.setValue(0);
//		progressBar.setPreferredSize(new Dimension(WIDTH - 100, HEIGHT - 180));
		progressBar.setStringPainted(true);
		
		logger = new JTextArea(10, 10);
//		logger.setPreferredSize(new Dimension(WIDTH - 100, 80));
		JScrollPane scrollPane = new JScrollPane(logger);
		
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.setDividerSize(0);
		splitter.setTopComponent(progressBar);
		splitter.setBottomComponent(scrollPane);
				
		JFrame popup = new JFrame();
		popup.setSize(WIDTH, HEIGHT);
		popup.getContentPane().add(splitter);
		popup.setVisible(true);
	}
	
	public static void _show() {
		fileName = new JLabel();
		fileName.setPreferredSize(new Dimension(WIDTH - 100, 30));
		fileName.setOpaque(true);
//		fileName.setBackground(Color.ORANGE);
		
		progressBar = new JProgressBar();
		progressBar.setValue(0);
		progressBar.setPreferredSize(new Dimension(WIDTH - 100, HEIGHT - 180));
		progressBar.setStringPainted(true);
		
		logger = new JTextArea();
		logger.setPreferredSize(new Dimension(WIDTH - 100, 80));
		logger.setAutoscrolls(true);
		

		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridwidth = GridBagConstraints.REMAINDER;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		
		JPanel panel = new JPanel();
		panel.setLayout(new GridBagLayout());
//		panel.setBackground(new Color(255, 0, 0));
		panel.add(fileName, gbc);
		panel.add(progressBar, gbc);
		panel.add(logger, gbc);
				
		JFrame popup = new JFrame();
//		popup.setResizable(false);
		popup.setSize(WIDTH, HEIGHT);
		popup.add(panel);
		popup.setVisible(true);
	}
	
	public static void progress(File file, long sum) {
//		fileName.setText(file.getAbsolutePath());
		if(file.isFile()) {
			long size = FileUtils.sizeOf(file);
			percent += Math.round(((double)size / (double)sum) * 100);
			progressBar.setValue(percent);
			
			String sizePart = "[" + size + "]";
			sizePart += StringUtils.repeat(" ", 20 - sizePart.length());
			logger.append(sizePart + file.getAbsolutePath() + "\n");
		}
	}
	
}
