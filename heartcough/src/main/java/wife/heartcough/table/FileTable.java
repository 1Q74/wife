package wife.heartcough.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;

import wife.heartcough.Explorer;
import wife.heartcough.Synchronizer;
import wife.heartcough.command.Command;
import wife.heartcough.path.DirectoryPath;
import wife.heartcough.system.FileSystem;
import wife.heartcough.tree.FileTree;




public class FileTable {

	private Explorer explorer;
	private FileTree fileTree;
	private DirectoryPath directoryPath;
	
	private JTable table = new JTable();
	private File[] rowElement;
	private FileListModel model;
	
	private int fileCount = 0;
	private int directoryCount = 0;
	
	public void setExplorer(Explorer explorer) {
		this.explorer = explorer;
		this.fileTree = this.explorer.getFileTree();
		this.directoryPath = this.explorer.getDirectoryPath();
		
		table.addKeyListener(new Command(explorer));
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
//		String[] filenames = getCurrentPath().list();
		String[] filenames = Synchronizer.getCurrentDirectory().list();
		File[] files = null;
		
		if(FileSystem.isWindowsMyPC(Synchronizer.getCurrentDirectoryName())) {
			files = Synchronizer.getCurrentDirectory().listFiles();
			Synchronizer.setDirectories(files);
		} else {
			int end = filenames.length;
			files = new File[end];
			
			List<File> directoryList = new ArrayList<File>();
			List<File> fileList = new ArrayList<File>();
			
			for(int i = 0; i < end; i++) {
				String filePath = 	Synchronizer.getCurrentDirectoryPath()
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
	
	public File getFile(JTable source) {
		int rowIndex = table.getSelectedRow();
		if(rowIndex == -1) return null;
		
		return rowElement[rowIndex];
	}
	
	private MouseListener getMouseListener() {
		return 
			new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					File file = getFile((JTable)e.getSource());
					if(file == null) return;
					
					if(file.isDirectory()) {
						fileTree.synchronizeToFileTable(file);
					}
				}
				
				directoryPath.restorePath();
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
	
//	public void load() {
//		load(null);
//	}
	
	public void load() {
		rowElement = getTableFileList();
		
//		if(selectedNode != null && selectedNode.isLeaf()) {
//			fileTree.setChildNode(selectedNode, fileTree.getChildFiles((File)selectedNode.getUserObject()));
//		}
		model = new FileListModel(rowElement);

		table.setModel(model);
		setFileIconColumn();
		
		table.addMouseListener(getMouseListener());
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setFillsViewportHeight(true);
		table.repaint();
	}
	
	// 폴더와 파일 중에서 어느 것이 더 많은지에 따라 로직을 달리한다.
	public boolean haveMoreDirecories() {
		return directoryCount > fileCount;
	}
	
	public File[] getListFiles() {
		return rowElement;
	}
	
	public JTable getFileTable() {
		return table;
	}
	
	public FileListModel getFileTableModel() {
		return model;
	}
	
}
