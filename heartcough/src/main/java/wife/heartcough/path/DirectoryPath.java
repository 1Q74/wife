package wife.heartcough.path;

import java.io.File;

import wife.heartcough.Synchronizer;
import wife.heartcough.system.FileSystem;

public class DirectoryPath {
	
	private IconTextField path = new IconTextField();

	public DirectoryPath() {
		this.path.addKeyListener(DirectoryPathListener.getKeyListener());
	}
	
	public void setPath(File directory) {
		path.setIcon(FileSystem.VIEW.getSystemIcon(directory));
		
		if(FileSystem.isWindowsSpecialFolder(directory.getName())) {
			path.setText(FileSystem.VIEW.getSystemDisplayName(directory));
		} else {
			path.setText(directory.getAbsolutePath());
		}
	}
	
	public IconTextField getPath() {
		return this.path;
	}
	
	public void restorePath() {
		setPath(Synchronizer.getCurrentNodeDirectory());
	}
	
}