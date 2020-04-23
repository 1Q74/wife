package wife.heartcough.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import wife.heartcough.common.FileEventListener;
import wife.heartcough.system.Synchronizer;

public class FileTableListener extends FileEventListener {
	
	@Override
	public MouseListener getMouseListener() {
		return 
			new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					JTable table = (JTable)e.getSource();
					
					int rowIndex = table.getSelectedRow();
					if(rowIndex == -1) return;
					
					Synchronizer.setCurrentFile(rowIndex);
					
					if(e.getClickCount() == 2) {
						if(Synchronizer.getCurrentFile().isDirectory()) {
							Synchronizer.isExpandingPath(true);
							Synchronizer.synchronize(Synchronizer.getCurrentFile());
						}
					}
					
					Synchronizer.pathChanged();
				}
	
				@Override
				public void mousePressed(MouseEvent e) {
					Synchronizer.setSourceComponent(e.getSource());
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