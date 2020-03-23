package wife.heartcough.tree;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.time.DateUtils;

import wife.heartcough.system.FileSystem;

public class FileTreeModel implements TreeModel {
	
	private File rootFile = FileSystem.VIEW.getRoots()[0];
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode(rootFile);
	private List<DefaultMutableTreeNode> children;
	private DefaultMutableTreeNode[] childNodes;
	
	@Override
	public Object getRoot() {
		return rootNode;
	}

	@Override
	public Object getChild(Object parent, int index) {
		return childNodes[index];
	}

	@Override
	public int getChildCount(Object parent) {
		DefaultMutableTreeNode parentNode = (DefaultMutableTreeNode)parent;
		File parentFile = (File)((DefaultMutableTreeNode)parentNode).getUserObject();
		
		children = new ArrayList<DefaultMutableTreeNode>();
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		
//		System.out.println("//////////////////////////////////////////////////////////////////");
//		System.out.println("[START] " + formatter.format(calendar.getTime()));
		
		for(File child : FileSystem.VIEW.getFiles(parentFile, false)) {
//			System.out.println("==========> " + child);
			if(child.isDirectory()) {
//				System.out.println(">>>>>>>>>>>>> FOUND!!!" + child);
				children.add(new DefaultMutableTreeNode(child));
			}
		}
		
		calendar.setTimeInMillis(System.currentTimeMillis());
//		System.out.println("[END]" + formatter.format(calendar.getTime()));
//		System.out.println("//////////////////////////////////////////////////////////////////");
		
		int count = children.size();
		childNodes = new DefaultMutableTreeNode[count];
		children.toArray(childNodes);
		
		return count;
	}

	@Override
	public boolean isLeaf(Object node) {
//		boolean isLeaf = false;
//		DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode)node;
//		File nodeFile = (File)((DefaultMutableTreeNode)node).getUserObject();
//		
//		if(!FileSystem.isDesktopPath(nodeFile.getName())) {
//			isLeaf = treeNode.isLeaf();
//		}
		
		return false;
	}

	@Override
	public void valueForPathChanged(TreePath path, Object newValue) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getIndexOfChild(Object parent, Object child) {
		return children.indexOf(child);
	}

	@Override
	public void addTreeModelListener(TreeModelListener l) {
		System.out.println("== addTreeModelListener ==");
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeTreeModelListener(TreeModelListener l) {
		// TODO Auto-generated method stub
		
	}

}
