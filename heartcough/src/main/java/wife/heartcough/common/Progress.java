package wife.heartcough.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Rectangle;
import java.io.File;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
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
	
	private JProgressBar bar;
	private JTable logTable;
	private DefaultTableModel logModel;
	private long sumSize = 0;
	private long copiedSize = 0;
	
	private void initProgressBar() {
		bar = new JProgressBar();
		bar.setString("Calcurating...");
		bar.setStringPainted(true);
	}
	
	private TableCellRenderer getLogTableDefaultCellRenderer(int alignment, Border border) {
		return
			new TableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(	JTable table
																, Object value
																, boolean isSelected
																, boolean hasFocus
																, int row
																, int column) {
					JLabel label = new JLabel((String)value);
					label.setHorizontalAlignment(alignment);
					label.setBorder(border);
					return label;
				}
			};
	}
	
	private TableCellRenderer getLogTableProgressBarCellRenderer() {
		return
			new TableCellRenderer() {
				@Override
				public Component getTableCellRendererComponent(	JTable table
																, Object value
																, boolean isSelected
																, boolean hasFocus
																, int row
																, int column) {
					JProgressBar progressBar = new JProgressBar();
					progressBar.setValue((Integer)value);
					progressBar.setStringPainted(true);
					return progressBar;
				}
			};
	}
	
	private void initLogTable() {
		logTable = new JTable();
		logTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		logTable.setFillsViewportHeight(true);
		
		logModel = (DefaultTableModel)logTable.getModel();
		logModel.addColumn("Percent");
		logModel.addColumn("Size");
		logModel.addColumn("Name");
		
		JTableHeader header = logTable.getTableHeader();
		header.setDefaultRenderer(
				getLogTableDefaultCellRenderer(JLabel.CENTER, BorderFactory.createRaisedBevelBorder())
		);
		
		
		TableColumnModel columnModel = (TableColumnModel)logTable.getColumnModel();
		int[] columnSize = new int[] { 100, 120 };
		for(int i = 0; i < columnSize.length; i++) {
			TableColumn column = columnModel.getColumn(i); 
			column.setMaxWidth(columnSize[i]);
			column.setWidth(columnSize[i]);
			column.setResizable(true);
			column.sizeWidthToFit();

			switch(i) {
				case 0:
					column.setCellRenderer(getLogTableProgressBarCellRenderer());
					break;
				case 1:
					Border border = new EmptyBorder(0, 0, 0, 0);
					column.setCellRenderer(getLogTableDefaultCellRenderer(JLabel.RIGHT, border));
					break;
			}
		}
	}

	private void ready() {
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
	
	public void show() {
		new Thread(
			new Runnable() {
				@Override
				public void run() {
					ready();
				}
			}
		).start();
	}
	
	private int getSizePercent(double source, double total) {
		return (int)Math.round((source / total) * 100);
	}
	
	public void setSumSize(long sumSize) {
		this.sumSize = sumSize;
	}
	
	public void refreshSizeProgress(long sizeGap) {
		copiedSize += sizeGap;
		System.out.println("[copiedSize:" + copiedSize + ", targetSize:" + sizeGap + "]");
		int percent = getSizePercent(copiedSize, sumSize);
		
		bar.setValue(percent);
		bar.setString(
			percent + "%"
			+ "[ "
			+ FileUtils.byteCountToDisplaySize(copiedSize)
			+ " / "
			+ FileUtils.byteCountToDisplaySize(sumSize)
			+ " ]"
		);
	}
	
	public class LogRowData {
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
	
	public LogRowData init(File copiedDirectory, String sourceAbsolutePath, File sourceFile) {
		int rowIndex = -1;
		String newFilePath =	copiedDirectory.getAbsolutePath() 
								+ File.separatorChar
								+ StringUtils.substring(sourceFile.getAbsolutePath(), sourceAbsolutePath.length() + 1);
		
		if(sourceFile.isFile()) {
			Object[] rowData = new Object[] {
				0
				, FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(sourceFile))
				, newFilePath
			};
			logModel.addRow(rowData);
			rowIndex = logModel.getRowCount() - 1;
			
			logTable.scrollRectToVisible(new Rectangle(logTable.getCellRect(rowIndex, 0, false)));
		}
		
		return new LogRowData(rowIndex, newFilePath);
	}
	
	public void process(long sourceSize, LogRowData logRowData) {
		if(sourceSize == 0) return;

		File newFile = new File(logRowData.getFilePath());
		long targetSize = 0;
		long previousTargetSize = 0;
		long sizeGap = 0;
		
		while(true) {
			if(newFile.exists()) {
				targetSize = FileUtils.sizeOf(newFile);
				if(targetSize > previousTargetSize) {
					sizeGap = targetSize - previousTargetSize;
					refreshSizeProgress(sizeGap);
					previousTargetSize = targetSize;
				}
				
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			
			int percent = getSizePercent(sourceSize, targetSize);
			logTable.setValueAt(percent, logRowData.getRowIndex(), 0);
			
			if(targetSize > 0 && targetSize == sourceSize) {
				refreshSizeProgress(sizeGap);
				break;
			}
		}
	}
	
}
