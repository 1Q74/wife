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
import wife.heartcough.system.FileSystem;
import wife.heartcough.tree.FileTree;




public class FileTable {

	private Explorer explorer;
	private FileTree fileTree;
	private File currentPath;
	private JTable table = new JTable();
	private File[] listFiles;
	
	private int fileCount = 0;
	private int directoryCount = 0;
	
	public void setExplorer(Explorer explorer) {
		this.explorer = explorer;
		this.fileTree = this.explorer.getFileTree();
	}
	
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
		
		// 윈도우의 [내 PC]
		if(fileList == null) {
			listFiles = getCurrentPath().listFiles();
		} else {
			int end = fileList.length;
			listFiles = new File[end];
			
			directoryCount = 0;
			fileCount = 0;
			
			List<File> directories = new ArrayList<File>();
			List<File> files = new ArrayList<File>();
			
			for(int i = 0; i < end; i++) {
				String filePath = 	getCurrentPath().getAbsolutePath()
									+ File.separatorChar
									+ fileList[i];
				
				File file = new File(filePath);
				if(file.isDirectory()) {
					++directoryCount;
					directories.add(file);
				} else {
					++fileCount;
					files.add(file);
				}
			}
			
			System.arraycopy(directories.toArray(), 0, listFiles, 0, directoryCount);
			System.arraycopy(files.toArray(), 0, listFiles, directoryCount, fileCount);
		}
		
		return listFiles;
	}
	
	private MouseListener getMouseListener() {
		return 
			new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getClickCount() == 2) {
					JTable table = (JTable)e.getSource();
					
					int rowIndex = table.getSelectedRow();
					if(rowIndex == -1) return;
					
					File file = listFiles[rowIndex];
					if(file.isDirectory()) {
						fileTree.synchronizeToFileTable(file);
					}
				}
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
		load(null);
	}
	
	public void load(DefaultMutableTreeNode selectedNode) {
		listFiles = getTableFileList();
		
		if(selectedNode != null && selectedNode.isLeaf()) {
			fileTree.setChildNode(selectedNode, fileTree.getChildFiles((File)selectedNode.getUserObject()));
		}
		FileListModel model = new FileListModel(listFiles);

		table.setModel(model);
		setFileTableColumn();
		
		table.addMouseListener(getMouseListener());
		table.repaint();
	}
	
	// 폴더와 파일 중에서 어느 것이 더 많은지에 따라 로직을 달리한다.
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
