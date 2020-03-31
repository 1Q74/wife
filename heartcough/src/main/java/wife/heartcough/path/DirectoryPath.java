package wife.heartcough.path;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import wife.heartcough.Explorer;
import wife.heartcough.Synchronizer;
import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;
import wife.heartcough.tree.FileTree;

public class DirectoryPath {
	
	private Explorer explorer;
	private FileTree fileTree;
	
	private IconTextField path = new IconTextField();
	
	public void setExplorer(Explorer explorer) {
		this.explorer = explorer;
		this.fileTree = this.explorer.getFileTree();
	}
	
	private KeyListener getKeyListener() {
		return new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}
			
			@Override
			public void keyReleased(KeyEvent e) {
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					String currentPath = path.getText();
					File file = new File(currentPath);

					if(file.isDirectory()) {
						explorer.getFileTree().searchAndChangePath(file);
					}
				}
			}
		}; 
	}
	
	public void setPath(File directory) {
		path.setIcon(FileSystem.VIEW.getSystemIcon(directory));
		
		if(FileSystem.isWindowsSpecialFolder(directory.getName())) {
			path.setText(FileSystem.VIEW.getSystemDisplayName(directory));
		} else {
			path.setText(directory.getAbsolutePath());
		}
		
		path.addKeyListener(getKeyListener());
	}
	
	public IconTextField getPath() {
		return this.path;
	}
	
	public void restorePath() {
		path.setText(Synchronizer.getCurrentDirectoryPath());
	}
	
}