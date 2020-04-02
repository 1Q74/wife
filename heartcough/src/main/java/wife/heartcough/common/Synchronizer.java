package wife.heartcough.common;

import java.io.File;

import javax.swing.tree.DefaultMutableTreeNode;

import wife.heartcough.path.DirectoryPath;
import wife.heartcough.table.FileTable;
import wife.heartcough.tree.FileTree;




public class Synchronizer {
	
	private static DefaultMutableTreeNode CURRENT_NODE;
	private static File CURRENT_DIRECTORY;
	private static File CURRENT_FILE;
	private static File[] FILE_LIST;
	private static File[] DIRECTORIES;
	private static File[] FILES;
	
	private static FileTree FILE_TREE;
	private static FileTable FILE_TABLE;
	private static DirectoryPath DIRECTORY_PATH;
	
	private static File[] PREVIOUS_DIRECTORIES;
	private static File[] PREVIOUS_FILES;
	private static boolean IS_DIRECTORY_CHANGED = false;
	private static boolean IS_FILE_CHANGED = false;
	
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
	
	public static void setCurrentDirectory(File currentDirectory) {
		CURRENT_DIRECTORY = currentDirectory;
	}
	
	public static File getCurrentDirectory() {
		return CURRENT_DIRECTORY;
	}
	
	public static void setCurrentFile(int rowIndex) {
		CURRENT_FILE = FILE_LIST[rowIndex];
	}
	
	public static File getCurrentFile() {
		return CURRENT_FILE;
	}

	public static File getCurrentNodeDirectory() {
		return (File)getCurrentNode().getUserObject();
	}
	
	public static String getCurrentNodeDirectoryPath() {
		return getCurrentNodeDirectory().getAbsolutePath();
	}
	
	public static String getCurrentNodeDirectoryName() {
		return getCurrentNodeDirectory().getName();
	}
	
	public static void setFileList(File[] fileList) {
		FILE_LIST = fileList;
	}
	
	public static File[] getFileList() {
		return FILE_LIST;
	}
	
	public static void setDirectories(File[] directories) {
		DIRECTORIES = directories;
		IS_DIRECTORY_CHANGED = PREVIOUS_DIRECTORIES != null && !DIRECTORIES.equals(PREVIOUS_DIRECTORIES); 
		PREVIOUS_DIRECTORIES = DIRECTORIES;
	}
	
	public static File[] getDirectories() {
		return DIRECTORIES;
	}
	
	public static void setFiles(File[] files) {
		FILES = files;
		IS_FILE_CHANGED = PREVIOUS_FILES != null && !FILES.equals(PREVIOUS_FILES);
		PREVIOUS_FILES = FILES;
	}
	
	public static File[] getFiles() {
		return FILES;
	}
	
	public static boolean haveMoreDirecories() {
		return DIRECTORIES.length > FILES.length;
	}
	
	public static boolean isDirectoryChanged() {
		return IS_DIRECTORY_CHANGED;
	}
	
	public static boolean isFileChanged() {
		return IS_FILE_CHANGED;
	}
	
	public static void initDirectoryChanged() {
		IS_DIRECTORY_CHANGED = false;
	}
	
	public static void initFileChanged() {
		IS_FILE_CHANGED = false;
	}
	
	public static void reload() {
		FILE_TREE.removeCurrentNodeChildren();
		load(getCurrentNode());
	}
	
	public static void load(DefaultMutableTreeNode selectedNode) {
		setCurrentNode(selectedNode);
		
		FILE_TABLE.load();
		FILE_TREE.load();
		DIRECTORY_PATH.setPath(Synchronizer.getCurrentNodeDirectory());
	}
	
	public static void synchronize(File currentDirectory) {
		setCurrentDirectory(currentDirectory);
		FILE_TREE.synchronize();
	}
	
	public static void synchronize(DefaultMutableTreeNode currnetNode) {
		setCurrentNode(currnetNode);
		FILE_TREE.synchronize();
	}
	
	public static void change(File changedDirectory) {
		setCurrentDirectory(changedDirectory);
		FILE_TREE.change();
	}
	
	public static void restorePath() {
		DIRECTORY_PATH.restorePath();
	}
	
}
