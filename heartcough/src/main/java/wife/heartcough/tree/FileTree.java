package wife.heartcough.tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

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
	
	private void setSystemChildNode(DefaultMutableTreeNode nodes, File parent) {
		for(File child : FileSystem.VIEW.getFiles(parent, false)) {
			if(child.isDirectory()) {
				nodes.add(getFolderTreeItem(child));
			}
		}
	}
	
	private void setChildNode(final DefaultMutableTreeNode nodes, File parent) {
		for(File file : fileTable.getListFiles()) {
			if(file.isDirectory()) {
//				nodes.add(getFolderTreeItem(file));
				DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(file.getAbsolutePath());
				nodes.add(childNode);
			}
		}
		
//		String[] fileNameList = parent.list();
//		
//		for(String fileName : fileNameList) {
//			String filePath = parent.getAbsolutePath() + File.separatorChar + fileName;
//			nodes.add(getFolderTreeItem(new File(filePath)));
//		}
		
//		DirectoryStream<Path> stream = null;
//		
//		try {
//			stream = Files.newDirectoryStream(parent.toPath());
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		
//		for(Path path : stream) {
//			if(path.toFile().isDirectory()) {
//				nodes.add(getFolderTreeItem(path.toFile()));
//			}
//		}
		
//		List<File> files = new ArrayList<File>();
//		stream.forEach(path -> nodes.add(getFolderTreeItem(path.toFile())));
//		System.out.println(nodes);
		
		
		
		/*
        SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
            	DirectoryStream<Path> stream = null;
        		
        		try {
        			stream = Files.newDirectoryStream(parent.toPath());
        			System.out.println(stream);
        		} catch (IOException e) {
        			e.printStackTrace();
        		}
        		
        		stream.forEach(path -> publish(path.toFile()));
            	
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for(File child : chunks) {
                	System.out.println(child);
               		nodes.add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
            }
        };
        worker.execute();
        */
	}
	
	public DefaultMutableTreeNode getDesktopFolderNodes() {
		DefaultMutableTreeNode nodes = null;
		
		for(File parent : FileSystem.VIEW.getRoots()) {
			nodes = getFolderTreeItem(parent);
			setSystemChildNode(nodes, parent);
		}
		
		return nodes;
	}
	
	private void getChildFolderNodes(DefaultMutableTreeNode parentNode) {
//		File parent = (File)parentNode.getUserObject();
//		String name = parent.getName();
		
		Object userObject = parentNode.getUserObject();
        File parent = null;
        if(userObject instanceof File) {
        	parent = (File)userObject;
        } else {
        	parent = new File((String)userObject);
        }
        String name = parent.getName();
		
		
		if(FileSystem.isWindowsSpecialFolder(name) || FileSystem.isDesktopPath(name)) {
			setSystemChildNode(parentNode, parent);
		} else {
			setChildNode(parentNode, parent);
		}
	}
	
	public JTree getDesktopFolderTree() {
		fileTree = new JTree(getDesktopFolderNodes());
//		fileTree = new JTree();
//		fileTree.setModel(new FileTreeModel());
		
		fileTree.setCellRenderer(new CellRenender());
		fileTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
//						fileTable.setCurrentPath((File)node.getUserObject());
						 Object userObject = node.getUserObject();
					        File file = null;
					        if(userObject instanceof File) {
					        	file = (File)userObject;
					        } else {
					        	file = new File((String)userObject);
					        }
				        fileTable.setCurrentPath(file);
						fileTable.load();
						
						if(node.isLeaf()) {
							getChildFolderNodes(node);
						}
					}
				});
						
				SwingUtilities.invokeLater(new Runnable() {
					@Override
					public void run() {
//						fileTable.setCurrentPath((File)node.getUserObject());
//						fileTable.load();
					}
				});
			}
		});
		
		return fileTree;
	}

}