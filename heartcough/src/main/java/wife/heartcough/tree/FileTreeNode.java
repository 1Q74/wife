package wife.heartcough.tree;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.SynchronousQueue;

import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import wife.heartcough.Explorer;
import wife.heartcough.Synchronizer;
import wife.heartcough.path.DirectoryPath;
import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;




public class FileTreeNode {
	
	private DefaultMutableTreeNode currentNode;
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
		System.out.println("currentdirectoryPath = " + Synchronizer.getCurrentDirectoryPath());
		return
			FileSystem.isDesktopPath(Synchronizer.getCurrentDirectoryPath())
			|| Synchronizer.haveMoreDirecories()
			? FileSystem.VIEW.getFiles(Synchronizer.getCurrentDirectory(), false)
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
                	System.out.println(child);
                	Synchronizer.getCurrentNode().add(new DefaultMutableTreeNode(child));
                }
            }

            @Override
            protected void done() {
//            	fileTree.setEnabled(true);
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