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

import org.apache.commons.lang3.StringUtils;

import wife.heartcough.common.FileSystem;
import wife.heartcough.common.Synchronizer;
import wife.heartcough.path.DirectoryPath;




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
						System.out.println("[valueChanged] " + (DefaultMutableTreeNode)e.getPath().getLastPathComponent());
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

	/**
	 * FileTree를 로드한다.
	 * 
	 * Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf() : 마우스 클릭(사용자 선택)
	 * Synchronizer.isDirectoryPathChanged() : 파일 경로의 변경
	 */
	/*
	public DefaultMutableTreeNode load(boolean sychronized, File matchedDirectory) {
		DefaultMutableTreeNode matchedTreeNode = null;
		
		if((Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf())
			|| Synchronizer.isDirectoryPathChanged()) {
			matchedTreeNode = fileTreeNode.setSynchronizedChildNode(matchedDirectory);
		}
		
		return matchedTreeNode;
	}
	*/
	
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
       			} else {
//       				tree.expandPath(new TreePath(childNode.getPath()));
       			}
       			searchChildNode(childNode);
       		}
		}
	}
	
	/*
	public void searchChildNode(DefaultMutableTreeNode parentNode) {
		DefaultMutableTreeNode matchedTreeNode = Synchronizer.load(parentNode, true);
		
		if(matchedTreeNode != null) {
//			SwingUtilities.invokeLater(new Runnable() {
//				@Override
//				public void run() {
					if(Synchronizer.isBeforeLastChangedDirectoryPath()) {
						System.out.println("[expand] " + matchedTreeNode + ", " + Synchronizer.isBeforeLastChangedDirectoryPath());
						tree.expandPath(new TreePath(matchedTreeNode.getPath()));
						
						if(matchedTreeNode.getUserObject().equals(Synchronizer.getLastChangedDirectoryPath())) {
							Synchronizer.isBeforeLastChangedDirectoryPath(false);
							tree.setSelectionPath(new TreePath(matchedTreeNode.getPath()));	
						}
					} else {
						System.out.println("[selection] " + matchedTreeNode + ", " + Synchronizer.isBeforeLastChangedDirectoryPath());
						Synchronizer.isBeforeLastChangedDirectoryPath(false);
						tree.setSelectionPath(new TreePath(matchedTreeNode.getPath()));						
					}
//				}
//			});
			searchChildNode(matchedTreeNode);
		}
	}
	*/
	
	public void change() {
		System.out.println("///////////////////////// [FileTree.change] //////////////////////////////");
//		DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
//		search(root);
		
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
//					searchChildNode(childNode);
				} else {
					searchChildNode(childNode);
				}
			}
		}
		System.out.println("///////////////////////// [//FileTree.change] //////////////////////////////");
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