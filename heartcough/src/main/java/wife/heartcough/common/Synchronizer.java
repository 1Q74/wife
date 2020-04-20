package wife.heartcough.common;

import java.io.File;

import javax.swing.JFrame;
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
	
	/**
	 * 메인 Window의 인스턴스를 저장합니다.
	 */
	private static JFrame WINDOW;
	
	/**
	 * 파일 복사 등의 진행화면(Progress)의 인스턴스를 저장합니다.
	 */
	private static Progress PROGRESS;
	
	/**
	 * DirectoryPath가 변경되었는지의 여부
	 */
	private static boolean DIRECTORY_PATH_CHANGED = false;
	
	/**
	 * DirectoryPath에서 변경된 디렉토리의 계층 경로
	 */
	private static File[] CHANED_DIRECTORY_PATHS;
	
	/**
	 * DirectoryPath에서 변경된 디렉토리의 계층의 다음 경로
	 */
	private static TreePath NEXT_CHANGED_DIRECTORY_TREE_PATH;
	
	/**
	 * DirectoryPath에서 변경된 디렉토리의 계층의 다음 경로의 존재 여부
	 */
	private static boolean HAS_MORE_CHANGED_DIRECTORY_PATH = true;
	
	private static boolean IS_EXPANDING_PATH = false;
	
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
	
	public static DirectoryPath getDirectoryPath() {
		return DIRECTORY_PATH;
	}
	
	public static void setWindow(JFrame window) {
		WINDOW = window;
	}
	
	public static JFrame getWindow() {
		return WINDOW;
	}
	
	public static void setProgress(Progress progress) {
		PROGRESS = progress;
	}
	
	public static Progress getProgress() {
		return PROGRESS;
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
		
		// 자식 노드를 찾아가는 동안의 디렉토리 경로가 변경되는 것이 보이므로
		// 디렉토리를 수동으로 변경한 경우는 DirectoryPath를 동기화하지 않는다.
		if(!isDirectoryPathChanged()) {
			DIRECTORY_PATH.setPath(Synchronizer.getCurrentNodeDirectory());
		}
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
		// DirectoryPath 변경에 의해서 false로 되어 있다면 true로 초기화한다.
		hasMoreChanedDirectoryPaths(true);
		FILE_TREE.change();
	}

	/**
	 * FileTree, FileTable에서 디렉토리를 변경했을 경우,
	 * 해당 디렉토리 정보로 DirectoryPath정보를 변경한다.
	 */
	public static void pathChanged() {
		DIRECTORY_PATH.pathChanged();
	}
	
	public static void isDirectoryPathChanged(boolean changed) {
		DIRECTORY_PATH_CHANGED = changed;
	}
	
	public static boolean isDirectoryPathChanged() {
		return DIRECTORY_PATH_CHANGED;
	}
	
	/**
	 * 변경된 경로를 디렉토리 구분자로 나눈다.
	 * 
	 * ex) C:\Windows\System32
	 *     => [0] : C:\ 
	 *        [1] : C:\Windows
	 *        [2] : C:\Windows\System32
	 */
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
	
	public static void setNextChangedDirectoryTreePath(DefaultMutableTreeNode childNode) {
		isNextChangedDirectoryTreeNode(childNode, true);
	}
	
	public static TreePath getNextChangedDirectoryTreePath() {
		return NEXT_CHANGED_DIRECTORY_TREE_PATH;
	}
	
	public static void hasMoreChanedDirectoryPaths(boolean isExists) {
		HAS_MORE_CHANGED_DIRECTORY_PATH = isExists;
	}
	
	public static boolean hasMoreChanedDirectoryPaths() {
		return HAS_MORE_CHANGED_DIRECTORY_PATH;
	}
	
	/**
	 * 탐색해야 할 변경된 디렉토리 경로가 더 존재하는지 확인하기 위해,
	 * 현재 선택된 트리 노드의 UserObject가 변경된 경로정보를 가지고 있는 배열의
	 * 가장 마지막 요소와 일치하는지 확인한다.
	 */
	public static void checkHasMoreChanedDirectoryPaths() {
		checkHasMoreChanedDirectoryPaths((File)getCurrentNode().getUserObject());
	}
	
	/**
	 * 탐색해야 할 변경된 디렉토리 경로가 더 존재하는지 확인하기 위해,
	 * 현재 선택된 트리 노드의 UserObject가 변경된 경로정보를 가지고 있는 배열의
	 * 가장 마지막 요소와 일치하는지 확인한다.
	 */
	public static void checkHasMoreChanedDirectoryPaths(File userObject) {
		if(getChangedDirectoryPaths() == null) return;
		
		File lastPath = getChangedDirectoryPaths()[getChangedDirectoryPaths().length - 1];
		if(userObject.equals(lastPath)) {
			hasMoreChanedDirectoryPaths(false);
		}
	}

	/**
	 * 변경된 디렉토리 계층에 FileTree의 자식노드가 존재하는지 비교한다.
	 * setNextNode가 true이면 CHANED_DIRECTORY_PATHS에서 자동선택되어야 할
	 * 다음 디렉토리 경로를 넘겨준다.
	 * 
	 * @param childNode 변경된 디렉토리 경로의 계층과 비교하기 위한 FileTree의 자식노드
	 * @param setNextNode selectionPath를 이용하여 다음 경로를 자동으로 선택하기 위한
	 *        변경된 디렉토리의 다음 경로 
	 * @return childNode가 변경된 디렉토리 계층의 값과 동일하다면 true, 그렇지 않으면 false
	 */
	public static boolean isNextChangedDirectoryTreeNode(DefaultMutableTreeNode childNode, boolean setNextNode) {
		for(int i = 0; i < CHANED_DIRECTORY_PATHS.length; i++) {
			File path = CHANED_DIRECTORY_PATHS[i];
			
			if(path.equals((File)childNode.getUserObject())) {
				if(setNextNode) {
					NEXT_CHANGED_DIRECTORY_TREE_PATH = new TreePath(childNode.getPath());
					hasMoreChanedDirectoryPaths(true);
				}
				return true;
			}
		}
		
		return false;
	}

	/**
	 * FileTable에서 디렉토리를 선택하거나 DirectoryPath에서 디렉토리의 경로를
	 * 변경했을 경우, FileTree의 자식노드를 확장된(expanded) 상태로 보여줄 지 결정한다.
	 * 
	 * @param isExpanding FileTree의 자식노드를 확장시킨 상태로 보여주려면 true,
	 *        그렇지 않으면 flase
	 */
	public static void isExpandingPath(boolean isExpanding) {
		IS_EXPANDING_PATH = isExpanding;
	}
	
	/**
	 * FileTree의 자식노드를 확장해서 보여줄 지를 선택하는 옵션
	 * 
	 * @return FileTree의 자식노드를 확장해서 보여준다면 treu, 그렇지 않으면 false
	 */
	public static boolean isExpandingPath() {
		return IS_EXPANDING_PATH;
	}

	/**
	 * Component가 마우스 포커스를 얻었을 때에 Component의 갱신여부를 결정한다.
	 * 
	 * @return 현재 디렉토리의 파일 개수가 변했다면 true, 그렇지 않으면 false
	 */
	public static boolean isDirectoryFileCountChanged() {
		return
			!FileSystem.isWindowsSpecialFolder(DIRECTORY_PATH.getCurrentPath().getName())
			&& getFileList().length != DIRECTORY_PATH.getCurrentPath().list().length;
	}
	
}
