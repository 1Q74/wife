package wife.heartcough.tree;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import wife.heartcough.Synchronizer;




public class FileTreeListener {
	
	public static MouseListener getMouseListener() {
		return 
			new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {
				Synchronizer.restorePath();
			}

			@Override
			public void mousePressed(MouseEvent e) {
			}

			@Override
			public void mouseReleased(MouseEvent e) {
			}

			@Override
			public void mouseEntered(MouseEvent e) {
			}

			@Override
			public void mouseExited(MouseEvent e) {
			}
		};
	}
	
	public static FocusListener getFocusListener() {
		return
			new FocusListener() {

				@Override
				public void focusGained(FocusEvent e) {
					if(Synchronizer.isDirectoryChanged()) {
						Synchronizer.reload();
						Synchronizer.initDirectoryChanged();
					}
				}

				@Override
				public void focusLost(FocusEvent e) {
				}
		};
	}
	
}