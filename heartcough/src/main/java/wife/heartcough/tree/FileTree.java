package wife.heartcough.tree;

import java.io.File;
import java.util.Enumeration;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import wife.heartcough.Synchronizer;




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
		tree.setFocusable(true);
		tree.addFocusListener(FileTreeListener.getFocusListener());
		
		return tree;
	}
	
	public void load() {
		if(Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf()) {
			fileTreeNode.setChildNode();
		}
	}
	
	public void reload() {
//		currentNode.removeAllChildren();
		
		File currentPath = Synchronizer.getCurrentNodeDirectory();
//		fileTable.setCurrentPath(currentPath);
//		fileTable.load(currentNode);
	}
	
	public void change() {
		for(TreeNode node : Synchronizer.getCurrentNode().getPath()) {
			DefaultMutableTreeNode elementNode = (DefaultMutableTreeNode)node;
			File userObject = (File)elementNode.getUserObject();
			
			if(Synchronizer.getCurrentDirectory().equals(userObject)) {
				DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)elementNode.getParent();
				
				// 윈도우에서 부모노드가 없는 경우는 desktop폴더를 현재 노드로 지정한다.
				if(parentNode == null) {
					parentNode = (DefaultMutableTreeNode)Synchronizer.getCurrentNode().getRoot();
					TreePath desktopTreePath = new TreePath((Object[])parentNode.getPath());
					tree.setSelectionPath(desktopTreePath);
				} else {
					Synchronizer.synchronize(parentNode);
				}
			} else {
				Synchronizer.synchronize(Synchronizer.getCurrentDirectory());
			}
		};
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