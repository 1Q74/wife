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

	/**
	 * 진행상태 창의 가로 사이즈
	 */
	private static int WIDTH = 700;
	
	/**
	 * 진행상태 창의 세로 사이즈
	 */
	private static int HEIGHT = 200;
	
	
	/**
	 * 진행상태 바의 객체
	 */
	private JProgressBar bar;
	
	/**
	 * 파일의 작업진행상태를 표시하기 위한 테이블
	 */
	private JTable logTable;
	
	/**
	 * 파일 작업진행상태 테이블의 모델
	 */
	private DefaultTableModel logModel;
	
	/**
	 * 작업하는 파일 크기의 합계
	 */
	private long sumSize = 0;
	
	/**
	 * 현재 작업(복사 등)이 진행중인 파일의 작업완료 크기
	 */
	private long copiedSize = 0;
	
	/**
	 * 작업상태 테이블을 스크롤 하기위한 스크롤 객체
	 */
	private JScrollPane logTableScrollPane;
	
	/**
	 * 진행상태 바를 초기화한다.
	 */
	private void initProgressBar() {
		sumSize = 0;
		copiedSize = 0;
		
		bar = new JProgressBar();
		bar.setString("Calcurating...");
		bar.setStringPainted(true);
	}
	
	/**
	 * 작업상태 테이블의 CellRenender을 설정한다.
	 * 
	 * @param alignment 셀에 표시되는 텍스트의 정렬 방향
	 * @param border 셀의 라인
	 * @return TableCellRenderer
	 */
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
	
	/**
	 * 진행상태 바의 CellRenender을 설정한다.
	 * 
	 * @return TableCellRenderer
	 */
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
	
	/**
	 * 진행상태 테이블을 초기화한다.
	 */
	private void initLogTable() {
		logTable = new JTable();
		logTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		logTable.setFillsViewportHeight(true);
		logTable.setEnabled(false);
		
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
				case 0:		// 진행상태 바
					column.setCellRenderer(getLogTableProgressBarCellRenderer());
					break;
				case 1:		// 파일명
					column.setCellRenderer(
						getLogTableDefaultCellRenderer(JLabel.RIGHT, new EmptyBorder(0, 0, 0, 5))
					);
					break;
				case 2:		// 파일의 절대경로
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

	/**
	 * 진행상태 창을 표시한다.
	 */
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
			// 창을 닫을 경우 작업 중인 쓰레드와 진행상태 쓰레드를 모두 종료한다.
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
	
	/**
	 * 진행상태 바의 퍼센트 값을 얻는다.
	 * @param source 현재 작업완료 된 파일 크기
	 * @param total 파일 원본의 크기
	 * @return 진행상태 바의 퍼센트 값
	 */
	private int getSizePercent(double source, double total) {
		if(source == 0) return 100;
		return (int)Math.round((source / total) * 100);
	}
	
	/**
	 * 파일 원본의 전체 사이즈를 계산한다.
	 * 
	 * @param files 작업대상 파일들
	 */
	public void setSumSize(File[] files) {
		for(File file : files) {
			sumSize += file.isDirectory() ? FileUtils.sizeOfDirectory(file) : FileUtils.sizeOf(file);
		}
	}
	
	/**
	 * 작업되는 파일의 전체 크기 중 몇 퍼세트가 작업되었는지 표시한다.
	 * 
	 * @param sizeGap 현재 작업완료된 파일의 크기
	 */
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
	
	/**
	 * 작업진행 테이블 행의 정보를 구성하는 클래스 
	 */
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
	
	/**
	 * 테이블 행의 데이터를 초기화한다.
	 * 
	 * @param sourceFile 파일크기 및 파일명을 출력하기 위한 원본 파일
	 * @param newFilePath 새롭게 생성되는 파일의 절대경로
	 * @return 초기화된 LogRowData의 객체
	 */
	public LogRowData init(File sourceFile, String newFilePath) {
		int rowIndex = -1;

		// 크기가 0인 디렉토리도 LogTable에 출력한다.
		Object[] rowData = new Object[] {
			sourceFile.length() == 0 ? 100 : 0
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
		
		return new LogRowData(rowIndex, newFilePath);
	}
	
	/**
	 * 파일 작업진행상태의 퍼센트 값을 업데이트한다.
	 * 
	 * @param sourceSize
	 * @param targetSize
	 * @param logRowData
	 */
	public void process(long sourceSize, long targetSize, LogRowData logRowData) {
		int percent = getSizePercent(targetSize, sourceSize);
		logTable.setValueAt(percent, logRowData.getRowIndex(), 0);
	}
	
	/**
	 * 디렉토리의 크리가 0인 것도 LogTable에 출력되도록 한다.
	 * 
	 * @param src 원본 디렉토리
	 * @param newDir 원본(src)의 이름으로 새롭게 생성된 디렉토리
	 */
	public void displayZeroByteDirectory(File src, File newDir) {
		new Thread(new Runnable() {
			public void run() {
				LogRowData logRowData = init(src, newDir.getAbsolutePath());
				progress(0, 0, logRowData, 0);
			}
		}).start();
	}
	
	/**
	 * LogTable에 작업(복사, 이동, 삭제 등)상태를 표시한다.
	 * 
	 * @param sourceSize 원본 파일의 크기
	 * @param targetSize 작업대상 파일의 크기
	 * @param logRowData LogTable의 구성하기 위한 행(row) 객체
	 * @param sum 작업대상 파일의 총합
	 */
	public void progress(long sourceSize, long targetSize, LogRowData logRowData, long sum) {
		process(sourceSize, targetSize, logRowData);
		refreshSizeProgress(sum);
	}
	
	/**
	 * 파일 복사 등을 실행할 경우 사용자에게 보여지는 메시지 팝업의 부모 객체로 사용한다.
	 * 
	 * @return 파일리스트를 출력하는 LogTable 객체
	 */
	public JTable getLogTable() {
		return logTable;
	}
	
}
