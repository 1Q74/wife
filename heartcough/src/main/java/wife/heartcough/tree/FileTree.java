package wife.heartcough.tree;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;

import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;




public class FileTree {
	
	private FileTable fileTable;
	
	public FileTree(FileTable fileTable) {
		this.fileTable = fileTable;
	}
	
	private List<File> getChildFolders(File parent, boolean hiddenAttr) {
		List<File> childFolders = new ArrayList<File>();

		for(File child : FileSystem.VIEW.getFiles(parent, hiddenAttr)) {
			if(child.isFile()) continue;
			childFolders.add(child);
		}
		
		return childFolders;
	}
	
	private Map<File, List<File>> getParentFolders() {
		Map<File, List<File>> parentFolders = new HashMap<File, List<File>>();
	
		for(File parent : FileSystem.VIEW.getRoots()) {
			parentFolders.put(parent, getChildFolders(parent, true));
		}
		
		return parentFolders;
	}
	
	private DefaultMutableTreeNode getFolderTreeItem(File entry) {
		DefaultMutableTreeNode item = new DefaultMutableTreeNode(entry.getName());
		item.setUserObject(entry);
		return item;
	}
	
	public DefaultMutableTreeNode getFolderNodes() {
		Set<Entry<File, List<File>>> entrySet = getParentFolders().entrySet();
		Iterator<Entry<File, List<File>>> iter = entrySet.iterator();
		
		DefaultMutableTreeNode nodes = null;
		while(iter.hasNext()) {
			Map.Entry<File, List<File>> entries = (Entry<File, List<File>>)iter.next();
			nodes = getFolderTreeItem(entries.getKey());
			
			for(File entry : entries.getValue()) {
				nodes.add(getFolderTreeItem(entry));
			}
		}
		
		return nodes;
	}
	
	public JTree getDesktopFolderTree() {
		JTree desktopFolderTree = new JTree(getFolderNodes());
		
		desktopFolderTree.setCellRenderer(new CellRenender());
		desktopFolderTree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				DefaultMutableTreeNode node = (DefaultMutableTreeNode)e.getPath().getLastPathComponent();
				fileTable.setCurrentPath((File)node.getUserObject());
				fileTable.load();
			}
		});
		
		return desktopFolderTree;
	}

}