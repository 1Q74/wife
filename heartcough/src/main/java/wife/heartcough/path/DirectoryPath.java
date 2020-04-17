package wife.heartcough.path;

import java.io.File;

import wife.heartcough.common.FileSystem;
import wife.heartcough.common.Synchronizer;

/**
 * 파일 경로를 구성합니다.
 * 
 * @author jdk
 */
public class DirectoryPath {
	
	private IconTextField path = new IconTextField();
	private File currentPath;

	public DirectoryPath() {
		this.path.addKeyListener(DirectoryPathListener.getKeyListener());
	}
	
	public void setPath(File directory) {
		currentPath = directory;
		path.setIcon(FileSystem.VIEW.getSystemIcon(directory));
		
		if(FileSystem.isWindowsSpecialFolder(directory.getName())) {
			path.setText(FileSystem.VIEW.getSystemDisplayName(directory));
		} else {
			path.setText(directory.getAbsolutePath());
		}
	}
	
	public File getCurrentPath() {
		return currentPath;
	}
	
	public IconTextField getPath() {
		return this.path;
	}
	
	public void pathChanged() {
		setPath(Synchronizer.getCurrentNodeDirectory());
	}
	
}