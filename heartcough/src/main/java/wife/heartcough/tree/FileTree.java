package wife.heartcough.tree;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.ExpandVetoException;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.ArrayUtils;

import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;




public class FileTree {
	
	private JTree fileTree;
	private FileTable fileTable;
	public DefaultMutableTreeNode currentNode;
	
	public FileTree(FileTable fileTable) {
		this.fileTable = fileTable;
		this.fileTable.setFileTree(this);
	}

	private DefaultMutableTreeNode getFolderTreeItem(File entry) {
		DefaultMutableTreeNode item = new DefaultMutableTreeNode();
		item.setUserObject(entry);
		return item;
	}
	
	private void setSystemChildNode(DefaultMutableTreeNode nodes, File parent) {
		for(File child : FileSystem.VIEW.getFiles(parent, false)) {
			if(child.isDirectory()) {
				nodes.add(getFolderTreeItem(child));
			}
		}
	}
	
	private File[] getChildFiles(File parent) {
		String name = parent.getName();
		
		File[] files = null;
		if(FileSystem.isWindowsSpecialFolder(name)|| FileSystem.isDesktopPath(name) || fileTable.haveMoreDirecories()) {
			files = FileSystem.VIEW.getFiles(parent, false);
		} else {
			files = fileTable.getListFiles();
		}
		
		return files;
	}
	
	private void setChildNode(final DefaultMutableTreeNode nodes, File[] childFiles) {
		fileTree.setEnabled(false);
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
            	for(File file : childFiles) {
        			if(file.isDirectory()) {
        				publish(file);
        			}
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for(File child : chunks) {
                	nodes.add(getFolderTreeItem(child));
                }
            }

            @Override
            protected void done() {
            	fileTree.setEnabled(true);
            	currentNode = nodes;
             }
        };
        worker.execute();
	}
	
	public DefaultMutableTreeNode getDesktopFolderNodes() {
		DefaultMutableTreeNode nodes = null;
		
		for(File parent : FileSystem.VIEW.getRoots()) {
			nodes = getFolderTreeItem(parent);
			setSystemChildNode(nodes, parent);
			
			currentNode = nodes;
		}
		
		return nodes;
	}
	
	public JTree getDesktopFolderTree() {
		fileTree = new JTree(getDesktopFolderNodes());
		fileTree.setCellRenderer(new CellRenender());
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				
				for(TreePath path : e.getPaths()) {
					System.out.println(path.toString());
				}
				
		       	fileTable.setCurrentPath((File)node.getUserObject());
				fileTable.load();
		

            	if(node.isLeaf()) {
            		setChildNode(node, getChildFiles((File)node.getUserObject()));
				}

		    }
		});
		
		return fileTree;
	}
	
	public JTree getInstance() {
		return fileTree;
	}
	
	public void synchronizeToFileTable(File selectedPath) {
		Enumeration<?> e = currentNode.children();
		while(e.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)e.nextElement();
			File file = (File)childNode.getUserObject();
			
			if(selectedPath.equals(file)) {
				TreePath childNodePath = new TreePath((Object[])childNode.getPath());
				fileTree.setSelectionPath(childNodePath);
				break;
			}
		}
	}

}