package wife.heartcough.tree;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import wife.heartcough.Explorer;
import wife.heartcough.Synchronizer;
import wife.heartcough.path.DirectoryPath;
import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;




public class FileTree {
	
	private static final long serialVersionUID = 1L;
	private FileTreeNode fileTreeNode = new FileTreeNode();
	
	
	
	
	private Explorer explorer;
	private DirectoryPath directoryPath;
	private FileTable fileTable;
	private JTree fileTree;
	private DefaultMutableTreeNode currentNode;
	
	
	public void setExplorer(Explorer explorer) {
		this.explorer = explorer;
		this.directoryPath = this.explorer.getDirectoryPath();
		this.fileTable = this.explorer.getFileTable();
	}
	
	private MouseListener getMouseListener() {
		return 
			new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
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
	
	private FocusListener getFocusListener() {
		return
			new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
//					explorer.refresh();
				}

				@Override
				public void focusLost(FocusEvent e) {
					// TODO Auto-generated method stub
					
				}
			
		};
	}
	
	public JTree getDesktopFolderTree() {
		fileTree = new JTree(fileTreeNode.getDesktopFolderNodes());
		fileTree.setCellRenderer(new FileTreeNodeCellRenender());
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Synchronizer.load((DefaultMutableTreeNode)e.getPath().getLastPathComponent());
//						File file = (File)currentNode.getUserObject();
//						File file = Synchronizer.getCurrentDirectory();
//						fileTable.setCurrentPath(Synchronizer.getCurrentDirectory());
//						fileTable.load(Synchronizer.getCurrentNode());
						
//						directoryPath.setPath(Synchronizer.getCurrentDirectory());
					}
				});
		    }
		});
		fileTree.addMouseListener(getMouseListener());
		fileTree.setFocusable(true);
		fileTree.addFocusListener(getFocusListener());
		
		return fileTree;
	}
	
	public void load() {
		if(Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf()) {
			fileTreeNode.setChildNode();
		}
	}
	
	public void reload() {
		currentNode.removeAllChildren();
		
		File currentPath = Synchronizer.getCurrentDirectory();
//		fileTable.setCurrentPath(currentPath);
//		fileTable.load(currentNode);
	}
	
	public void searchAndChangePath(File selectedPath) {
		for(TreeNode node : currentNode.getPath()) {
			DefaultMutableTreeNode elementNode = (DefaultMutableTreeNode)node;
			File userObject = (File)elementNode.getUserObject();
			
			if(selectedPath.equals(userObject)) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)elementNode.getParent();
				
				// 윈도우에서 부모노드가 없는 경우는 desktop폴더를 현재 노드로 지정한다.
				if(parentNode == null) {
					parentNode = (DefaultMutableTreeNode)currentNode.getRoot();
					TreePath desktopTreePath = new TreePath((Object[])parentNode.getPath());
					fileTree.setSelectionPath(desktopTreePath);
				} else {
					Synchronizer.setCurrentNode(parentNode);
					synchronizeToFileTable(selectedPath);
				}
				return;
			}
		};
	}
	
	public void synchronizeToFileTable(File selectedPath) {
		Enumeration<?> children = currentNode.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File file = (File)childNode.getUserObject();

			if(selectedPath.equals(file)) {
				TreePath childNodePath = new TreePath((Object[])childNode.getPath());
				fileTree.setSelectionPath(childNodePath);
				return;
			}
		}
	}
	
	public JTree getTree() {
		return fileTree;
	}

}