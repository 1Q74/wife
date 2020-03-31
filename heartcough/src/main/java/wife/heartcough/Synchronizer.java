package wife.heartcough;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import wife.heartcough.path.DirectoryPath;
import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;
import wife.heartcough.tree.FileTree;

public class Synchronizer {
	
	private static DefaultMutableTreeNode CURRENT_NODE;
	private static File[] DIRECTORIES;
	private static File[] FILES;
	
	private static FileTree FILE_TREE;
	private static FileTable FILE_TABLE;
	private static DirectoryPath DIRECTORY_PATH;
	
	public static void setFileTree(FileTree fileTree) {
		FILE_TREE = fileTree;
	}
	
	public static FileTree getFileTree() {
		return FILE_TREE;
	}
	
	public static void setFileTable(FileTable fileTable) {
		FILE_TABLE = fileTable;
	}
	
	public static void setDirectoryPath(DirectoryPath directoryPath) {
		DIRECTORY_PATH = directoryPath;
	}

	public static void setCurrentNode(DefaultMutableTreeNode currentNode) {
		CURRENT_NODE = currentNode;
	}
	
	public static DefaultMutableTreeNode getCurrentNode() {
		return CURRENT_NODE;
	}

	public static File getCurrentDirectory() {
		return (File)getCurrentNode().getUserObject();
	}
	
	public static String getCurrentDirectoryPath() {
		return getCurrentDirectory().getAbsolutePath();
	}
	
	public static String getCurrentDirectoryName() {
		return getCurrentDirectory().getName();
	}
	
	public static void setDirectories(File[] directories) {
		DIRECTORIES = directories;
	}
	
	public static File[] getDirectories() {
		return DIRECTORIES;
	}
	
	public static void setFiles(File[] files) {
		FILES = files;
	}
	
	public static File[] getFiles() {
		return FILES;
	}
	
	public static boolean haveMoreDirecories() {
		return DIRECTORIES.length > FILES.length;
	}
	
	public static void load(DefaultMutableTreeNode selectedNode) {
		setCurrentNode(selectedNode);
		
		FILE_TABLE.load();
		FILE_TREE.load();
		DIRECTORY_PATH.setPath(Synchronizer.getCurrentDirectory());
	}
	
}
