package wife.heartcough.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.TreePath;

import wife.heartcough.system.FileSystem;
import wife.heartcough.tree.FileTree;




public class FileTable {

	private FileTree fileTree;
	private File currentPath;
	private JTable table = new JTable();
	private File[] listFiles;
	
	private int fileCount = 0;
	private int directoryCount = 0;
	
	public void setFileTree(FileTree fileTree) {
		this.fileTree = fileTree;
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
		
		// 윈도우즈의 [내 PC]폴더
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
	
	private MouseListener getMouseListener() {
		return 
			new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				JTable table = (JTable)e.getSource();
				
				int rowIndex = table.getSelectedRow();
				if(rowIndex == -1) return;
				
				File file = listFiles[rowIndex];
				if(file.isDirectory()) {
					fileTree.synchronizeToFileTable(file);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void mouseExited(MouseEvent e) {
				// TODO Auto-generated method stub
				
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
		listFiles = getTableFileList();
		FileListModel model = new FileListModel(listFiles);

		table.setModel(model);
		setFileTableColumn();
		
		table.addMouseListener(getMouseListener());
		table.repaint();
	}
	
	// 디렉토리 개수가 많은지 파일 개수가 많은지 판단한다.
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
