package wife.heartcough.path;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;

import wife.heartcough.common.Synchronizer;

/**
 * 파일 탐색기 상단에 있는 IconTextField의 리스너입니다. 
 * 
 * @author jdk
 */
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
						Synchronizer.setSelectedFrom(e.getSource());
						Synchronizer.change(file);
					}
				}
			}
		}; 
	}
	
}