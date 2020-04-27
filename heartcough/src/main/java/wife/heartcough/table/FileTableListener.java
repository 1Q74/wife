package wife.heartcough.table;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import wife.heartcough.common.FileEventListener;
import wife.heartcough.common.OperationPopupMenu;
import wife.heartcough.system.Synchronizer;

public class FileTableListener extends FileEventListener {
	
	private void setSelection(MouseEvent e) {
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
	public MouseListener getMouseListener() {
		return 
			new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					setSelection(e);
				}
	
				@Override
				public void mousePressed(MouseEvent e) {
					Synchronizer.setSourceComponent(e.getSource());
				}
	
				@Override
				public void mouseReleased(MouseEvent e) {
					JTable table = (JTable)e.getSource();
					int rowIndex = table.rowAtPoint(e.getPoint());
					if(rowIndex == -1) return;
					
					if(e.getButton() == MouseEvent.BUTTON3) {
						table.setRowSelectionInterval(rowIndex, rowIndex);
						setSelection(e);
						
						OperationPopupMenu popup = new OperationPopupMenu();
						popup.show(e);
					}
				}
	
				@Override
				public void mouseEntered(MouseEvent e) {}
	
				@Override
				public void mouseExited(MouseEvent e) {}
			};
	}
	
}