package wife.heartcough.path;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import wife.heartcough.Synchronizer;




public class DirectoryPathListener {
	
	public static KeyListener getKeyListener() {
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
					String currentPath = ((IconTextField)e.getSource()).getText();
					File file = new File(currentPath);

					if(file.isDirectory()) {
						Synchronizer.change(file);
					}
				}
			}
		}; 
	}
	
}