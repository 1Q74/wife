package wife.heartcough.tree;

import java.io.File;
import java.util.List;

import javax.swing.JTree;
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
			private JTree tree = Synchronizer.getFileTree().getTree();
			
            @Override
            public Void doInBackground() {
            	tree.setEnabled(false);
            	Synchronizer.checkHasMoreChanedDirectoryPaths();
            	
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

                	// DirectoryPath가 사용자에 의해 변경되었다면
                	// 자동선택되어야 할 다음 디렉토리 경로를 찾는다.
                	if(Synchronizer.isDirectoryPathChanged()) {
                		Synchronizer.setNextChangedDirectoryTreePath(childNode);
                	}
                }
            }
            
            private void expandPath() {
        		tree.expandPath(new TreePath(Synchronizer.getCurrentNode().getPath()));            	
            }
            
            @Override
            protected void done() {
            	tree.setEnabled(true);

    			if(Synchronizer.isExpandingPath()) {
            		expandPath();
            	}            			

           		// DirectoryPath변경에 의해 자동선택되어야 할 디렉토리가 있다면
           		// 해당 디렉토리를 선택한다.
        		if(Synchronizer.isDirectoryPathChanged() && Synchronizer.hasMoreChanedDirectoryPaths()) {
            		tree.setSelectionPath(Synchronizer.getNextChangedDirectoryTreePath());
            	}
             }
        };
        worker.execute();
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