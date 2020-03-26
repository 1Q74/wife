package wife.heartcough;

import java.io.File;

import javax.swing.JTextField;
import wife.heartcough.system.FileSystem;

public class DirectoryPath {
	private JTextField path = new JTextField();
	
	public void setPath(File directory) {
		String path = "";
		
		if(FileSystem.isWindowsSpecialFolder(directory.getName())) {
			path = FileSystem.VIEW.getSystemDisplayName(directory);
		} else {
			path = directory.getAbsolutePath();
		}
		
		this.path.setText(path);
	}
	
	public JTextField getPath() {
		return path;
	}
}
