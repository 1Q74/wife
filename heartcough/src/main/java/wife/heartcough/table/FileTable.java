package wife.heartcough.table;

import java.io.File;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import wife.heartcough.system.FileSystem;




public class FileTable {

	private File currentPath;
	private JTable table = new JTable();
	private File[] listFiles;
	
	private int fileCount = 0;
	private int directoryCount = 0;
	
	public void setCurrentPath(File path) {
		this.currentPath = path;
	}
	
	private File getCurrentPath() {
		if(this.currentPath == null) {
			this.currentPath = FileSystem.DEFAULT; 
		}
		return this.currentPath;
	}

	private File[] getTableFileList() {
		String[] fileList = getCurrentPath().list();
		File[] listFiles = null;
		
		// ���������� [�� PC]����
		if(fileList == null) {
			listFiles = getCurrentPath().listFiles();
		} else {
			int end = fileList.length;
			listFiles = new File[end];
			
			for(int i = 0; i < end; i++) {
				String filePath = 	getCurrentPath().getAbsolutePath()
									+ File.separatorChar
									+ fileList[i];
				
				File file = new File(filePath);
				if(file.isDirectory()) {
					++directoryCount;
				} else {
					++fileCount;
				}
				listFiles[i] = file;
			}
		}
		
		return listFiles;
	}
	
	private void setFileTableColumn() {
		Integer[] width = FileListModel.COLUMN_WIDTH;
		int index = 0;
		for(Integer w : width) {
			TableColumn column = table.getColumnModel().getColumn(index++);
			column.setMaxWidth(w);
			column.setPreferredWidth(w);
		}
	}
	
	public void load() {
		listFiles = getTableFileList();
		FileListModel model = new FileListModel(listFiles);

		table.setModel(model);
		setFileTableColumn();
		
		table.repaint();
	}
	
	// ���丮 ������ ������ ���� ������ ������ �Ǵ��Ѵ�.
	public boolean haveMoreDirecories() {
		return directoryCount > fileCount;
	}
	
	public File[] getListFiles() {
		return listFiles;
	}
	
	public JTable getFileTable() {
		return table;
	}
	
}
