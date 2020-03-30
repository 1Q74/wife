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
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import wife.heartcough.Explorer;
import wife.heartcough.path.DirectoryPath;
import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;




public class FileTree {
	
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
		if(FileSystem.isDesktopPath(name) || fileTable.haveMoreDirecories()) {
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
					setCurrentNode(parentNode);
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
	
	public File getCurrentPath() {
		return (File)currentNode.getUserObject();
	}

}