package wife.heartcough.common;

import java.io.File;

import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.lang3.StringUtils;

import wife.heartcough.path.DirectoryPath;
import wife.heartcough.table.FileTable;
import wife.heartcough.tree.FileTree;

/**
 * DirectoryPath, FileTree, FileTable중의 한 곳에서라도
 * 폴더의 변경이 발생하면, 나머지 객체들도 변경된 폴더로 변경하여
 * 동일한 폴더의 정보를 출력하도록 동기화를 실행하는 객체입니다.
 * 
 * @author jdk
 */
public class Synchronizer {
	
	public Synchronizer() {
		super();
	}

	/**
	 * FileTree의 현재 선택된 노드의 인스턴스를 저장합니다.
	 */
	private static DefaultMutableTreeNode CURRENT_NODE;
	
	/**
	 * CURRENT_NODE의 UserObject를 저장한다.
	 */
	private static File CURRENT_DIRECTORY;
	
	/**
	 * FileTable의 현재 선택된 행의 파일정보를 저장한다.
	 */
	private static File CURRENT_FILE;
	
	/**
	 * FileTable의 현재 선택된 여러 행의 파일정보를 저장한다.
	 */
	private static File[] CURRENT_FILES;
	
	
	/**
	 * FileTable의 현재 데이터를 저장합니다.
	 */
	private static File[] FILE_LIST;
	
	/**
	 * FileTable의 데이터를 구성할 때에 디렉토리정보만 추출하여 이 변수에 저장합니다.
	 */
	private static File[] DIRECTORIES;
	
	/**
	 * FileTable의 데이터를 구성할 때에 파일정보만 추출하여 이 변수에 저장합니다.
	 */
	private static File[] FILES;
	

	/**
	 * FileTree의 인스턴스를 저장합니다.
	 */
	private static FileTree FILE_TREE;
	
	/**
	 * FileTable의 인스턴스를 저장합니다.
	 */
	private static FileTable FILE_TABLE;
	
	/**
	 * DirectoryPath의 인스턴스를 저장합니다.
	 */
	private static DirectoryPath DIRECTORY_PATH;
	
	private static boolean SELECTED_FROM_FILE_TABLE = false;
	private static boolean SELECTED_FROM_FILE_TREE = false;
	private static boolean SELECTED_FROM_FILE_PATH = false;
	
	private static boolean DIRECTORY_PATH_CHANGED = false;
	private static File[] CHANED_DIRECTORY_PATHS;
	private static boolean IS_BEFORE_LAST_CHANGED_DIRECTORY_PATH = false;
	private static File MATCH_CHANGED_DIRECTORY_PATH;
	
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
	
	public static void setCurrentFiles(int[] rowIndexes) {
		CURRENT_FILES = new File[rowIndexes.length];
		
		for(int i = 0; i < rowIndexes.length; i++) {
			CURRENT_FILES[i] = FILE_LIST[rowIndexes[i]];
		}
		
		CURRENT_FILE = CURRENT_FILES[rowIndexes.length - 1];
	}
	
	public static File[] getCurrentFiles() {
		return CURRENT_FILES;
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
		
		if(Synchronizer.isDirectoryPathChanged()) {
			for(File dir : DIRECTORIES) {
				isInChangedDirectoryPath(dir);
			}
		}
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
	
	/**
	 * 디렉토리가 많은 경우 읽어들이는 속도가 빠른 메소드가 있는 반면
	 * 파일이 많은 경우 읽어들이는 속도가 빠른 메소드가 있기 때문에,
	 * FileTable에 출력되는 디렉토리, 파일의 개수를 비교하여
	 * 다른 메소드를 호출한다.
	 * 
	 * @return 디렉토리의 수가 파일보다 많으면 true,
	 *         디렉토리보다 파일의 수가 더 많으면 false
	 */
	public static boolean haveMoreDirecories() {
		return DIRECTORIES.length > FILES.length;
	}
	
	/**
	 * 파일복사 등이 실행되었을 경우 FileTree, FileTable을 다시 읽어들인다.
	 */
	public static void reload() {
		FILE_TREE.removeCurrentNodeChildren();
		load(getCurrentNode());
	}
	
	/**
	 * FileTree에서 노드를 선택했을 경우 FileTable, DirectoryPath를 동기화한다.
	 * 
	 * @param selectedNode FileTree의 현재 선택된 노드
	 */
	public static void load(DefaultMutableTreeNode selectedNode) {
		setCurrentNode(selectedNode);
		setCurrentDirectory((File)getCurrentNode().getUserObject());
		
		FILE_TABLE.addFileList();
		FILE_TABLE.load();
		FILE_TREE.load();
		DIRECTORY_PATH.setPath(Synchronizer.getCurrentNodeDirectory());
		
	}
	
	/**
	 * FileTree에서 노드를 선택했을 경우 FileTable, DirectoryPath를 동기화한다.
	 * 
	 * @param selectedNode FileTree의 현재 선택된 노드
	 */
	public static DefaultMutableTreeNode synchronizedLoad(DefaultMutableTreeNode selectedNode) {
		setCurrentNode(selectedNode);
		setCurrentDirectory((File)getCurrentNode().getUserObject());
		
		FILE_TABLE.addFileList();
		
		if(Synchronizer.isDirectoryPathChanged() && Synchronizer.isBeforeLastChangedDirectoryPath()) {
			FILE_TABLE.load();
			DefaultMutableTreeNode matchedTreeNode = FILE_TREE.load(getMatchChangedDirectoryPath());
			DIRECTORY_PATH.setPath(Synchronizer.getCurrentNodeDirectory());
		
			return matchedTreeNode;
		}
		
		return null;
	}
	
	/**
	 * FileTable에서 디렉토리를 마우스 더블클릭해서 선택했을 경우,
	 * 선택한 디렉토리로 FileTree의 노드를 변경한다.
	 * 
	 * @param currentDirectory FileTable에서 현재 선택한 디렉토리 정보
	 */
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

	/**
	 * FileTree, FileTable에서 디렉토리를 변경했을 경우,
	 * 해당 디렉토리 정보로 DirectoryPath정보를 변경한다.
	 */
	public static void pathChanged() {
		DIRECTORY_PATH.pathChanged();
	}
	
	public static void setSelectedFrom(Object source) {
		if(source instanceof JTable) {
			SELECTED_FROM_FILE_TABLE = true;
		} else if(source instanceof JTree) {
			SELECTED_FROM_FILE_TREE = true;
		} else if(source instanceof JTextField) {
			SELECTED_FROM_FILE_PATH = true;
		}
	}
	
	public static boolean isSelectedFromFileTable() {
		return SELECTED_FROM_FILE_TABLE;
	}
	
	public static boolean isSelectedFromFileTree() {
		return SELECTED_FROM_FILE_TREE;
	}
	
	public static boolean isSelectedFromFilePath() {
		return SELECTED_FROM_FILE_PATH;
	}
	
	public static void isDirectoryPathChanged(boolean changed) {
		DIRECTORY_PATH_CHANGED = changed;
	}
	
	public static boolean isDirectoryPathChanged() {
		return DIRECTORY_PATH_CHANGED;
	}
	
	// 변경된 경로를 디렉토리 구분자로 나눈다.
	public static void setChangedDirectoryPaths() {
		String[] pathTokens = StringUtils.split(
			Synchronizer.getCurrentDirectory().getAbsolutePath(), File.separatorChar
		);
		
		CHANED_DIRECTORY_PATHS = new File[pathTokens.length];
		String path = "";
		
		for(int i = 0; i < pathTokens.length; i++) {
			path += pathTokens[i];
			if(pathTokens.length == 1 || i < pathTokens.length - 1) {
				path += File.separatorChar;
			}
			
			File file = new File(path);
			if(file.isDirectory()) {
				CHANED_DIRECTORY_PATHS[i] = file;
			} else {
				break;
			}
		}
	}
	
	public static File[] getChangedDirectoryPaths() {
		return CHANED_DIRECTORY_PATHS;
	}
	
	public static boolean isInChangedDirectoryPath(File search) {
		for(int i = 0; i < CHANED_DIRECTORY_PATHS.length; i++) {
			File path = CHANED_DIRECTORY_PATHS[i];
			
			if(path.equals(search)) {
				MATCH_CHANGED_DIRECTORY_PATH = path;
				
				if(CHANED_DIRECTORY_PATHS.length == 1 || i <= CHANED_DIRECTORY_PATHS.length - 2) {
					isBeforeLastChangedDirectoryPath(true);
				}
				
				return true;
			}
		}
		
		return false;
	}
	
	public static File getMatchChangedDirectoryPath() {
		return MATCH_CHANGED_DIRECTORY_PATH;
	}
	
	public static void isBeforeLastChangedDirectoryPath(boolean isBefore) {
		IS_BEFORE_LAST_CHANGED_DIRECTORY_PATH = isBefore;
	}
	
	public static boolean isBeforeLastChangedDirectoryPath() {
		return IS_BEFORE_LAST_CHANGED_DIRECTORY_PATH;
	}
	
	public static File getLastChangedDirectoryPath() {
		return CHANED_DIRECTORY_PATHS[CHANED_DIRECTORY_PATHS.length - 1];
	}
	
}
