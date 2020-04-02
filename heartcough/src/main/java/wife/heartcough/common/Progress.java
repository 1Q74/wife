package wife.heartcough.common;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.EtchedBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;;

public class Progress {
	
	private static int WIDTH;
	private static int HEIGHT;
	
	static {
		WIDTH = 450;
		HEIGHT = 200;
	}
	
	private static JProgressBar bar;
//	private static JLabel fileName;
	private static JTextArea logger;
	private static int percent = 0;
	private static JTable logTable;
	private static DefaultTableModel logModel;
	
	private static TableCellRenderer getLogTableCellRenderer(int alignment, Border border) {
		return
			new TableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(	JTable table
																, Object value
																, boolean isSelected
																, boolean hasFocus
																, int row
																, int column) {
					System.out.println("value = " + value);
//					Border border = BorderFactory.createEtchedBorder(EtchedBorder.LOWERED);
					
					JLabel header = new JLabel((String)value);
					header.setHorizontalAlignment(alignment);
					header.setBorder(border);
					
					return header;
				}
			};
	}

	public static void show() {
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		
		bar = new JProgressBar();
		bar.setValue(0);
//		bar.setPreferredSize(new Dimension(WIDTH - 100, HEIGHT - 180));
		bar.setStringPainted(true);

		
		logTable = new JTable();
		logTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		logTable.setFillsViewportHeight(true);
		
		logModel = (DefaultTableModel)logTable.getModel();
		logModel.addColumn("Percent");
		logModel.addColumn("Size");
		logModel.addColumn("Name");
		
		JTableHeader header = logTable.getTableHeader();
		header.setDefaultRenderer(
			getLogTableCellRenderer(JLabel.CENTER, BorderFactory.createRaisedBevelBorder())
		);
//		DefaultTableCellRenderer columnHeaderRenderer = new DefaultTableCellRenderer();
//		columnHeaderRenderer.setHorizontalAlignment(JLabel.CENTER);
//		header.setDefaultRenderer(columnHeaderRenderer);
		
		
		TableColumnModel columnModel = (TableColumnModel)logTable.getColumnModel();
		int[] columnSize = new int[] { 100, 120 };
		for(int i = 0; i < columnSize.length; i++) {
			TableColumn column = columnModel.getColumn(i); 
			column.setMaxWidth(columnSize[i]);
			column.setWidth(columnSize[i]);
			column.setResizable(true);
			column.sizeWidthToFit();
			
			Border border = new EmptyBorder(0, 0, 0, 0);
			column.setCellRenderer(getLogTableCellRenderer(JLabel.RIGHT, border));
		}

		
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.setDividerSize(0);
		splitter.setTopComponent(bar);
		splitter.setBottomComponent(new JScrollPane(logTable));
		
		
		JPanel bodyPanel = new JPanel(new BorderLayout());
		bodyPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		bodyPanel.add(splitter);
		
		JFrame popup = new JFrame();
		popup.setSize(WIDTH, HEIGHT);
		popup.setContentPane(bodyPanel);
		popup.setVisible(true);
		popup.pack();
	}
	
	public static void progress(File file, long sum) {
		System.out.println(file);
//		if(file.isDirectory()) return;
		
		String[] rowData = new String[] {
			"0"
			, FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(file))
			, file.getAbsolutePath()
		};
		System.out.println(rowData[1]);
		System.out.println(rowData[2]);
		logModel.addRow(rowData);
	}
	
	
	
	/*
	public static void __show() {
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
	*/
	
	/*
	public static void progress(File file, long sum) {
//		fileName.setText(file.getAbsolutePath());
		if(file.isFile()) {
			long size = FileUtils.sizeOf(file);
			percent += Math.round(((double)size / (double)sum) * 100);
			bar.setValue(percent);
			
			String sizePart = "[" + size + "]";
			sizePart += StringUtils.repeat(" ", 20 - sizePart.length());
			logger.append(sizePart + file.getAbsolutePath() + "\n");
		}
	}
	*/
	
}
