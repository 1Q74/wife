package wife.heartcough.tree;

import java.io.File;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import wife.heartcough.common.FileSystem;
import wife.heartcough.common.Synchronizer;

public class FileTree {
	
	private FileTreeNode fileTreeNode = new FileTreeNode();
	private JTree tree;

	public JTree getRoot() {
		tree = 	new JTree(fileTreeNode.getDesktopFolderNodes());
		tree.setCellRenderer(new FileTreeNodeCellRenender());
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Synchronizer.load((DefaultMutableTreeNode)e.getPath().getLastPathComponent());
					}
				});
		    }
		});
		tree.addMouseListener(FileTreeListener.getMouseListener());
		
		return tree;
	}
	
	/**
	 * FileTree를 로드한다.
	 * 
	 * Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf() : 마우스 클릭(사용자 선택)
	 * Synchronizer.isDirectoryPathChanged() : 파일 경로의 변경
	 */
	public void load() {
		if(Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf()) {
			fileTreeNode.setChildNode();
		}
	}

	public void refresh() {
		DefaultMutableTreeNode currentTreeNode = Synchronizer.getCurrentNode();
		Enumeration<?> children = currentTreeNode.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File file = (File)childNode.getUserObject();

			if(!file.exists()) {
				currentTreeNode.remove(childNode);
			}
		}
		
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.reload(currentTreeNode);
	}
	
	public void removeCurrentNodeChildren() {
		Synchronizer.getCurrentNode().removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel)getTree().getModel();
		model.reload(Synchronizer.getCurrentNode());
	}
	
	public boolean search(DefaultMutableTreeNode parentNode) {
		boolean isFound = false;
		Enumeration<?> children = parentNode.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File userObject = (File)childNode.getUserObject();
			
			if(Synchronizer.getCurrentDirectory().equals(userObject)) {
				Synchronizer.synchronize((DefaultMutableTreeNode)childNode.getParent());
				return true;
			} else {
				isFound = search(childNode);
				if(isFound) break;
			}
		}
		
		return isFound;
	}
	
	public void searchChildNode(DefaultMutableTreeNode parentNode) {
		Enumeration<?> children = parentNode.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();

			if(Synchronizer.isNextChangedDirectoryTreeNode(childNode)) {
       			if(childNode.isLeaf()) {
       				tree.setSelectionPath(new TreePath(childNode.getPath()));
       			} 
       			searchChildNode(childNode);
       		}
		}
	}
	
	public void change() {
		// 변경된 경로를 디렉토리 구분자로 나눈다.
		Synchronizer.setChangedDirectoryPaths();
		
		// 윈도우 : 모든 경로의 시작은 드라이브로 시작될 것이므로 먼저 [내 PC]노드를 찾는다.
		File windowsMyPC = new File(FileSystem.WINDOWS_MY_PC_NAME);
		
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
		Enumeration<?> children = root.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File userObject = (File)childNode.getUserObject();
			
			if(userObject.equals(windowsMyPC)) {
				Synchronizer.isDirectoryPathChanged(true);
				
				if(childNode.isLeaf()) {
					TreePath childNodePath = new TreePath(childNode.getPath());
					tree.setSelectionPath(childNodePath);
				} else {
					searchChildNode(childNode);
				}
			}
		}
	}
	
	public void synchronize() {
		Enumeration<?> children = Synchronizer.getCurrentNode().children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File file = (File)childNode.getUserObject();

			if(Synchronizer.getCurrentDirectory().equals(file)) {
				TreePath childNodePath = new TreePath((Object[])childNode.getPath());
				tree.setSelectionPath(childNodePath);
				return;
			}
		}
	}
	
	public JTree getTree() {
		return tree;
	}

}