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