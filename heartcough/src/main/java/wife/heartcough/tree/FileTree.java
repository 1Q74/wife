package wife.heartcough.tree;

import java.io.File;
import java.util.Enumeration;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import wife.heartcough.DirectoryPath;
import wife.heartcough.Explorer;
import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;




public class FileTree {
	
	private Explorer explorer;
	private DirectoryPath directoryPath;
	private FileTable fileTable;
	private JTree fileTree;
	public DefaultMutableTreeNode currentNode;
	
	public void setExplorer(Explorer explorer) {
		this.explorer = explorer;
		this.directoryPath = this.explorer.getDirectoryPath();
		this.fileTable = this.explorer.getFileTable();
	}
	
	private void setSystemChildNode(DefaultMutableTreeNode nodes, File parent) {
		for(File child : FileSystem.VIEW.getFiles(parent, false)) {
			if(child.isDirectory()) {
				nodes.add(new DefaultMutableTreeNode(child));
			}
		}
	}
	
	public File[] getChildFiles(File parent) {
		String name = parent.getName();
		
		File[] files = null;
		if(FileSystem.isWindowsSpecialFolder(name)|| FileSystem.isDesktopPath(name) || fileTable.haveMoreDirecories()) {
			files = FileSystem.VIEW.getFiles(parent, false);
		} else {
			files = explorer.getFileTable().getListFiles();
		}
		
		return files;
	}
	
	private void setCurrentNode(DefaultMutableTreeNode selectedNode) {
		this.currentNode = selectedNode;
	}
	
	public void setChildNode(final DefaultMutableTreeNode parentNode, File[] childFiles) {
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
                	parentNode.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
            	fileTree.setEnabled(true);
             }
        };
        worker.execute();
	}
	
	public DefaultMutableTreeNode getDesktopFolderNodes() {
		DefaultMutableTreeNode desktopNode = null;
		
		for(File parent : FileSystem.VIEW.getRoots()) {
			desktopNode = new DefaultMutableTreeNode(parent);
			setSystemChildNode(desktopNode, parent);
			setCurrentNode(desktopNode);
		}
		
		return desktopNode;
	}
	
	public JTree getDesktopFolderTree() {
		fileTree = new JTree(getDesktopFolderNodes());
		fileTree.setCellRenderer(new CellRenender());
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						currentNode = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
						File file = (File)currentNode.getUserObject();
						fileTable.setCurrentPath(file);
						fileTable.load(currentNode);
						
						directoryPath.setPath(file);
					}
				});
		    }
		});
		
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
	
	public File getCurrentPath() {
		return (File)currentNode.getUserObject();
	}

}