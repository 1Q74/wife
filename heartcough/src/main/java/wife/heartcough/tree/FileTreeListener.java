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
					Synchronizer.setSelectedFrom(e.getSource());
					Synchronizer.pathChanged();
				}
	
				@Override
				public void mousePressed(MouseEvent e) {}
	
				@Override
				public void mouseReleased(MouseEvent e) {}
	
				@Override
				public void mouseEntered(MouseEvent e) {}
	
				@Override
				public void mouseExited(MouseEvent e) {}
			};
	}
	
}