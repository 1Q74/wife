package wife.heartcough.table;

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
	
	public FileTable() {
		table.addMouseListener(FileTableListener.getMouseListener());
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
		
		Synchronizer.setFileList(files);
	}

	public void addFileList() {
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
	}
	
	private void setFileIconColumn() {
		int width = 25;
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setMaxWidth(width);
		column.setPreferredWidth(width);
	}
	
	public JTable load() {
		addFileList();
		
		table.setModel(new FileListModel(Synchronizer.getFileList()));
		setFileIconColumn();
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setFillsViewportHeight(true);
		table.repaint();
		
		return table;
	}
	
}
