package wife.heartcough.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import wife.heartcough.Synchronizer;
import wife.heartcough.command.Command;
import wife.heartcough.system.FileSystem;




public class FileTable {

	private JTable table = new JTable();
	private File[] rowElement;
	
	public FileTable() {
		table.addKeyListener(new Command());
	}
	
	private void copyToFileArray(List<File> directoryList, List<File> fileList, File[] files) {
		int directoryCount = directoryList.size();
		int fileCount = fileList.size();
		
		File[] tmpDirectories = new File[directoryCount];
		File[] tmpFiles = new File[fileCount];
		
		directoryList.toArray(tmpDirectories);
		fileList.toArray(tmpFiles);
		
		Synchronizer.setDirectories(tmpDirectories);
		Synchronizer.setFiles(tmpFiles);
		
		System.arraycopy(Synchronizer.getDirectories(), 0, files, 0, directoryCount);
		System.arraycopy(Synchronizer.getFiles(), 0, files, directoryCount, fileCount);
	}

	public File[] getTableFileList() {
		String[] filenames = Synchronizer.getCurrentNodeDirectory().list();
		File[] files = null;
		
		if(FileSystem.isWindowsSpecialFolder(Synchronizer.getCurrentNodeDirectoryName())) {
			files = Synchronizer.getCurrentNodeDirectory().listFiles();
			Synchronizer.setDirectories(files);
		} else {
			int end = filenames.length;
			files = new File[end];
			
			List<File> directoryList = new ArrayList<File>();
			List<File> fileList = new ArrayList<File>();
			
			for(int i = 0; i < end; i++) {
				String filePath = 	Synchronizer.getCurrentNodeDirectoryPath()
									+ File.separatorChar
									+ filenames[i];
				
				File file = new File(filePath);
				if(file.isDirectory()) {
					directoryList.add(file);
				} else {
					fileList.add(file);
				}
			}

			copyToFileArray(directoryList, fileList, files);
		}
		
		return files;
	}
	
	private MouseListener getMouseListener() {
		return 
			new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					int rowIndex = ((JTable)e.getSource()).getSelectedRow();
					if(rowIndex == -1) return;
					
					Synchronizer.setCurrentFile(rowElement[rowIndex]);
					if(Synchronizer.getCurrentFile().isDirectory()) {
						Synchronizer.synchronize(Synchronizer.getCurrentFile());
					}
				}
				
				Synchronizer.restorePath();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
	}
	
	private void setFileIconColumn() {
		int width = 25;
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setMaxWidth(width);
		column.setPreferredWidth(width);
	}
	
	public JTable load() {
		rowElement = getTableFileList();
		table.setModel(new FileListModel(rowElement));
		setFileIconColumn();
		
		table.addMouseListener(getMouseListener());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setFillsViewportHeight(true);
		table.repaint();
		
		return table;
	}
	
}
