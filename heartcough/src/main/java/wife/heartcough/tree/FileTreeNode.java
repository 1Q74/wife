package wife.heartcough.tree;

import java.io.File;
import java.util.List;

import javax.swing.SwingWorker;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import wife.heartcough.Synchronizer;
import wife.heartcough.system.FileSystem;




public class FileTreeNode {
	
	private File[] nodeElement;
	
	public void nodeElement(File[] files) {
		this.nodeElement = files;
	}
	
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
		nodeElement = selectNodeElementSource();
		
//		fileTree.setEnabled(false);
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
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
                	Synchronizer.getCurrentNode().add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
//            	fileTree.setEnabled(true);
            	Synchronizer.getFileTree().getTree().expandPath(new TreePath(Synchronizer.getCurrentNode().getPath()));
            	Synchronizer.getFileTree().getTree().repaint();
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