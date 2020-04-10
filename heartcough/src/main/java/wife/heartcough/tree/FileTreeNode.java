package wife.heartcough.tree;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import wife.heartcough.common.FileSystem;
import wife.heartcough.common.Synchronizer;




public class FileTreeNode {
	
	private void setSystemChildNode(DefaultMutableTreeNode nodes, File parent) {
		for(File child : FileSystem.VIEW.getFiles(parent, false)) {
			if(child.isDirectory()) {
				nodes.add(new DefaultMutableTreeNode(child));
			}
		}
	}
	
	public File[] selectNodeElementSource() {
		return
			FileSystem.isDesktopPath(Synchronizer.getCurrentNodeDirectoryPath())
			|| Synchronizer.haveMoreDirecories()
			? FileSystem.VIEW.getFiles(Synchronizer.getCurrentNodeDirectory(), false)
			: Synchronizer.getDirectories();
	}
	
	public void setChildNode() {
		File[] nodeElement = selectNodeElementSource();
		
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
            	Synchronizer.getFileTree().getTree().setEnabled(false);
            	
            	for(File file : nodeElement) {
        			if(file.isDirectory()) {
        				publish(file);
        			}
                }
                return null;
            }

            @Override
            protected void process(List<File> chunks) {
                for(File child : chunks) {
                	DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
                	Synchronizer.getCurrentNode().add(childNode);

                	if(Synchronizer.isDirectoryPathChanged()) {
                		Synchronizer.setNextChangedDirectoryTreePath(childNode);
                	}
                }
            }

            @Override
            protected void done() {
            	System.out.println("== done ==");
            	Synchronizer.getFileTree().getTree().setEnabled(true);
            	
            	JTree tree = Synchronizer.getFileTree().getTree();
            	TreePath currentNodeTreePath = new TreePath(Synchronizer.getCurrentNode().getPath());
            	
            	if(!Synchronizer.isBeforeLastChangedDirectoryPath()) {
            		System.out.println("expandPath");
            		tree.expandPath(currentNodeTreePath);
            	}
            	
//            	if(Synchronizer.isSelectedFromFileTable() || Synchronizer.isDirectoryPathChanged()) {
//	            	Synchronizer.getFileTree().getTree().expandPath(
//	            		new TreePath(Synchronizer.getCurrentNode().getPath())
//	            	);
//	            	
//	            	if(Synchronizer.isDirectoryPathChanged() && Synchronizer.hasMoreChanedDirectoryPaths()) {
//						Synchronizer.getFileTree().getTree().setSelectionPath(Synchronizer.getNextChangedDirectoryTreePath());
//						
//						if(Synchronizer.noMoreChanedDirectoryPaths()) {
//							
//						}
//	            	} 
//            	}
             }
        };
        worker.execute();
        
        
        if(worker.isDone()) {
        	System.out.println("[isDone] " + Synchronizer.getCurrentNode());
//        	if(Synchronizer.isDirectoryPathChanged() && Synchronizer.noMoreChanedDirectoryPaths()) {
//    			Synchronizer.isDirectoryPathChanged(false);
//        	}        	
        }
	}
	
	public DefaultMutableTreeNode setSynchronizedChildNode(File matchedDirectory) {
		System.out.println("== setSynchronizedChildNode ==");
		Synchronizer.getFileTree().getTree().setEnabled(false);
		
		File[] nodeElement = selectNodeElementSource();
		DefaultMutableTreeNode matchedTreeNode = null;
		
		for(File child : nodeElement) {
           	DefaultMutableTreeNode childNode = new DefaultMutableTreeNode(child);
           	Synchronizer.getCurrentNode().add(childNode);
           	
           	if(childNode.getUserObject().equals(matchedDirectory)) {
           		matchedTreeNode = childNode;
           	}
           	
//           	if(Synchronizer.isInChangedDirectoryPath((File)childNode.getUserObject())) {
//           		matchedTreeNode = childNode;
//           	}
        }

		Synchronizer.getFileTree().getTree().setEnabled(true);
		
		JTree tree = Synchronizer.getFileTree().getTree();
    	TreePath currentNodeTreePath = new TreePath(Synchronizer.getCurrentNode().getPath());
    	
    	if(!Synchronizer.isBeforeLastChangedDirectoryPath()) {
    		System.out.println("expandPath");
    		tree.expandPath(currentNodeTreePath);
    	}
		
		return matchedTreeNode;
	}
	
	public DefaultMutableTreeNode getDesktopFolderNodes() {
		DefaultMutableTreeNode desktopNode = null;
		
		for(File parent : FileSystem.VIEW.getRoots()) {
			desktopNode = new DefaultMutableTreeNode(parent);
			setSystemChildNode(desktopNode, parent);
			Synchronizer.setCurrentNode(desktopNode);
		}
		
		return desktopNode;
	}
	
}