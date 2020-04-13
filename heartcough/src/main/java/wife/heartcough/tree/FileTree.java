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
						DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
						System.out.println("[valueChanged] " + node);
						Synchronizer.load(node);
					}
				});
		    }
		});
		tree.addMouseListener(FileTreeListener.getMouseListener());
		
		return tree;
	}
	
	/**
	 * 노드를 선택할 수 있는 상태인지 확인한다.
	 * 
	 * @return 현재 선택되어 있는 노드가 아니고 leaf상태이면 true
	 */
	private boolean isEnableNodeSelection() {
		return Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf();
	}
	
	/**
	 * 상단의 디렉토리 경로가 수동으로 변경되어, 디렉토리를 검색하는 중인지를 확인한다. 
	 * @return 현재 선택되어 있는 노드가 아니고,
	 *         상단의 디렉토리 경로가 수동으로 변경되었다면 true
	 */
	private boolean isEnableNodeSelectionWithDirectoryPathChanged() {
		return Synchronizer.getCurrentNode() != null && Synchronizer.isDirectoryPathChanged();
	}
	
	/**
	 * FileTree노드의 사용자 선택에 따라 FileTree를 로드한다.
	 * 
	 * Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf() : 마우스 클릭(사용자 선택)
	 * Synchronizer.isDirectoryPathChanged() : 파일 경로의 변경
	 */
	public void load() {
		if(!isEnableNodeSelection()) return;
		fileTreeNode.setChildNode();
	}

	/**
	 * 상단의 디렉토리 경로가 수동으로 변경된 경우, 변경된 경로까지 FileTree를 로드한다.
	 * 
	 * Synchronizer.isDirectoryPathChanged() : 파일 경로의 사용자 변경여부
	 */
	public DefaultMutableTreeNode load(File matchedDirectory) {
		DefaultMutableTreeNode matchedTreeNode = null;
		
		if(isEnableNodeSelectionWithDirectoryPathChanged()) {
			matchedTreeNode = fileTreeNode.setSynchronizedChildNode(matchedDirectory);
		}
		
		return matchedTreeNode;
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
       			} else {
//       				tree.expandPath(new TreePath(childNode.getPath()));
       			}
       			searchChildNode(childNode);
       		}
		}
	}
	
	public void change() {
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
		search(root);
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