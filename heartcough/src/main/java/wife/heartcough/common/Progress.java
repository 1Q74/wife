package wife.heartcough.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

public class Progress {
	
	private static int WIDTH;
	private static int HEIGHT;
	
	static {
		WIDTH = 450;
		HEIGHT = 200;
	}
	
	private static JProgressBar bar;
	private static JTable logTable;
	private static DefaultTableModel logModel;
	
	private static void initProgressBar() {
		bar = new JProgressBar();
		bar.setValue(0);
		bar.setStringPainted(true);
	}
	
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
					JLabel header = new JLabel((String)value);
					header.setHorizontalAlignment(alignment);
					header.setBorder(border);
					
					return header;
				}
			};
	}
	
	private static void initLogTable() {
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
	}

	private static void ready() {
		initProgressBar();
		initLogTable();
		
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
	}
	
	public static void show() {
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					ready();
				}
			}
		).start();
	}
	
	public static class LogRowData {
		private int rowIndex;
		private String filePath;

		public LogRowData(int rowIndex, String filePath) {
			this.rowIndex = rowIndex;
			this.filePath = filePath;
		}
		
		public int getRowIndex() {
			return rowIndex;
		}
		
		public String getFilePath() {
			return filePath;
		}
	}
	
	public static LogRowData init(File copiedDirectory, String sourceAbsolutePath, File newFile, long sum) {
		int rowIndex = -1;
		String newFilePath =	copiedDirectory.getAbsolutePath() 
								+ File.separatorChar
								+ StringUtils.substring(newFile.getAbsolutePath(), sourceAbsolutePath.length() + 1);
		
		if(newFile.isFile()) {
			String[] rowData = new String[] {
				"0"
				, FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(newFile))
				, newFilePath
			};
			logModel.addRow(rowData);
			rowIndex = logModel.getRowCount() - 1;
		}
		
		return new LogRowData(rowIndex, newFilePath);
	}
	
	public static void process(long sourceSize, LogRowData logRowData) {
		if(sourceSize == 0) return;

		File newFile = new File(logRowData.getFilePath());
		long targetSize = 0;
		
		while(true) {
			if(newFile.exists()) {
				targetSize = FileUtils.sizeOf(newFile);
			} else {
				try {
					Thread.sleep(200);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			int percent = (int)Math.round(((double)targetSize / (double)sourceSize) * 100);
			logTable.setValueAt(Integer.toString(percent), logRowData.getRowIndex(), 0);
			
			if(targetSize > 0 && targetSize == sourceSize) break;
		}
	}
	
}
