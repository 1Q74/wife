package wife.heartcough.path;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;

import wife.heartcough.Explorer;
import wife.heartcough.system.FileSystem;

public class DirectoryPath {
	
	private Explorer explorer;
	private IconTextField path = new IconTextField();
	private File previousPath;
	
	public void setExplorer(Explorer explorer) {
		this.explorer = explorer;
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
						previousPath = file;
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
	
}