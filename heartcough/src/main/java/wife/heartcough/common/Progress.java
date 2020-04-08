package wife.heartcough.common;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.lang.reflect.InvocationTargetException;

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
import org.apache.commons.io.FilenameUtils;

public class Progress {
	
	private static int WIDTH;
	private static int HEIGHT;
	
	static {
		WIDTH = 700;
		HEIGHT = 200;
	}
	
	private JProgressBar bar;
	private JTable logTable;
	private DefaultTableModel logModel;
	private long sumSize = 0;
	private long copiedSize = 0;
	private JScrollPane logTableScrollPane;
	
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
		logModel.addColumn("Path");
		
		JTableHeader header = logTable.getTableHeader();
		header.setDefaultRenderer(
				getLogTableDefaultCellRenderer(JLabel.CENTER, BorderFactory.createRaisedBevelBorder())
		);
		
		
		TableColumnModel columnModel = (TableColumnModel)logTable.getColumnModel();
		int[] columnSize = new int[] { 80, 80, 250 };
		for(int i = 0; i < columnSize.length; i++) {
			TableColumn column = columnModel.getColumn(i); 
			column.setMaxWidth(columnSize[i]);
			column.setPreferredWidth(columnSize[i]);
			column.setResizable(true);
			column.sizeWidthToFit();

			switch(i) {
				case 0:
					column.setCellRenderer(getLogTableProgressBarCellRenderer());
					break;
				case 1:
					column.setCellRenderer(
						getLogTableDefaultCellRenderer(JLabel.RIGHT, new EmptyBorder(0, 0, 0, 5))
					);
					break;
				case 2:
					column.setCellRenderer(
						getLogTableDefaultCellRenderer(JLabel.LEFT, new EmptyBorder(0, 5, 0, 0))
					);
					break;
			}
		}
		
		columnModel.getColumn(3).setCellRenderer(
			getLogTableDefaultCellRenderer(JLabel.LEFT, new EmptyBorder(0, 5, 0, 0))
		);
	}

	public void show() {
		initProgressBar();
		initLogTable();
		
		logTableScrollPane = new JScrollPane(logTable);
		
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.setDividerSize(0);
		splitter.setTopComponent(bar);
		splitter.setBottomComponent(logTableScrollPane);
		
		JPanel bodyPanel = new JPanel(new BorderLayout());
		bodyPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		bodyPanel.add(splitter);
		
		JFrame popup = new JFrame();
		popup.setSize(WIDTH, HEIGHT);
		popup.setContentPane(bodyPanel);
		popup.setVisible(true);
		popup.addWindowListener(new WindowListener() {
			@Override
			public void windowClosing(WindowEvent e) {
				ProgressHandler.isStopped(true);
				ProgressHandler.getHandler().shutdownNow();
				
				CommandHandler.isStopped(true);
				CommandHandler.getHandler().shutdownNow();
			}
			
			@Override
			public void windowOpened(WindowEvent e) {}
			
			@Override
			public void windowIconified(WindowEvent e) {}
			
			@Override
			public void windowDeiconified(WindowEvent e) {}
			
			@Override
			public void windowDeactivated(WindowEvent e) {}
			
			@Override
			public void windowClosed(WindowEvent e) {}
			
			@Override
			public void windowActivated(WindowEvent e) {}
		});
	}
	
	private int getSizePercent(double source, double total) {
		return (int)Math.round((source / total) * 100);
	}
	
	public void setSumSize(File[] files) {
		for(File file : files) {
			this.sumSize += file.isDirectory() ? FileUtils.sizeOfDirectory(file) : FileUtils.sizeOf(file);
		}
	}
	
	public void refreshSizeProgress(long sizeGap) {
		copiedSize += sizeGap;
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
	
	public LogRowData init(File sourceFile, String newFilePath) {
		int rowIndex = -1;

		if(sourceFile.isFile()) {
			Object[] rowData = new Object[] {
				0
				, FileUtils.byteCountToDisplaySize(FileUtils.sizeOf(sourceFile))
				, FilenameUtils.getName(newFilePath)
				, newFilePath
			};
			logModel.addRow(rowData);
			rowIndex = logModel.getRowCount() - 1;
			
			
			try {
				SwingUtilities.invokeAndWait(new Runnable() {
					@Override
					public void run() {
						logTableScrollPane.getVerticalScrollBar().setValue(logTableScrollPane.getVerticalScrollBar().getMaximum());
						
					}
				});
			} catch (InvocationTargetException | InterruptedException e) {
			}
		}
		
		return new LogRowData(rowIndex, newFilePath);
	}
	
	public void process(long sourceSize, long targetSize, LogRowData logRowData) {
		int percent = getSizePercent(targetSize, sourceSize);
		System.out.println("sourceSize = " + sourceSize + ", percent = " + percent);
		logTable.setValueAt(percent, logRowData.getRowIndex(), 0);
	}
	
}
