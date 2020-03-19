package wife.heartcough.tree;

import java.io.File;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;




public class FileTree {
	
	private JTree fileTree;
	private FileTable fileTable;
	
	public FileTree(FileTable fileTable) {
		this.fileTable = fileTable;
	}
	
	private DefaultMutableTreeNode getFolderTreeItem(File entry) {
		DefaultMutableTreeNode item = new DefaultMutableTreeNode();
		item.setUserObject(entry);
		return item;
	}
	
	private void setChildNode(DefaultMutableTreeNode nodes, File parent) {
		for(File child : FileSystem.VIEW.getFiles(parent, false)) {
			if(child.isDirectory()) {
				nodes.add(getFolderTreeItem(child));
			}
		}
	}
	
	public DefaultMutableTreeNode getDesktopFolderNodes() {
		DefaultMutableTreeNode nodes = null;
		
		for(File parent : FileSystem.VIEW.getRoots()) {
			nodes = getFolderTreeItem(parent);
			setChildNode(nodes, parent);
		}
		
		return nodes;
	}
	
	private void getChildFolderNodes(DefaultMutableTreeNode parentNode) {
		File parent = (File)parentNode.getUserObject();
		setChildNode(parentNode, parent);
	}
	
	public JTree getDesktopFolderTree() {
		fileTree = new JTree(getDesktopFolderNodes());
		
		fileTree.setCellRenderer(new CellRenender());
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
						if(node.isLeaf()) {
							getChildFolderNodes(node);							
						}
						
						fileTable.setCurrentPath((File)node.getUserObject());
						fileTable.load();
					}
				});
			}
		});
		
		return fileTree;
	}

}