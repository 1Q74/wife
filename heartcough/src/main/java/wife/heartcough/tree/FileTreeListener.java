package wife.heartcough.tree;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import wife.heartcough.common.Synchronizer;

public class FileTreeListener {
	
	public static MouseListener getMouseListener() {
		return 
			new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					Synchronizer.pathChanged();
					
					// 마우스 더블 클릭 시에는 노드를 확장한다.
					if(e.getClickCount() == 2) {
						Synchronizer.isExpandingPath(true);
					}
				}
	
				@Override
				public void mousePressed(MouseEvent e) {
					// FileTable, DirectoryPath에서 true로 설정되었다면
					// 마우스 클릭 시에는 false로 설정한다.
					Synchronizer.isExpandingPath(false);
				}
	
				@Override
				public void mouseReleased(MouseEvent e) {}
	
				@Override
				public void mouseEntered(MouseEvent e) {}
	
				@Override
				public void mouseExited(MouseEvent e) {}
			};
	}
	
}