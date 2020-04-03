package wife.heartcough.table;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import wife.heartcough.common.Command;
import wife.heartcough.common.CommandHandler;
import wife.heartcough.common.Synchronizer;




public class FileTableListener {
	
	public static MouseListener getMouseListener() {
		return 
			new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int rowIndex = ((JTable)e.getSource()).getSelectedRow();
					if(rowIndex == -1) return;
					
					Synchronizer.setCurrentFile(rowIndex);
					
					if(e.getClickCount() == 2) {
						if(Synchronizer.getCurrentFile().isDirectory()) {
							Synchronizer.synchronize(Synchronizer.getCurrentFile());
						}
					}
					
					Synchronizer.restorePath();
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
	
	public static KeyListener getKeyListener() {
		return
			new KeyListener() {
				private static final int CTRL = 2;
				private static final int C = 67;
				private static final int V = 86;
				private Command command = new Command();
			
				@Override
				public void keyPressed(KeyEvent e) {
					int keyCodeSum = e.getModifiers() + e.getKeyCode();
					
					switch(keyCodeSum) {
						case (CTRL + C):
							command.copy();
							break;
						case (CTRL + V):
							CommandHandler.getHandler().submit(command);
							Synchronizer.reload();
							break;
					}
				}
				
				@Override
				public void keyTyped(KeyEvent e) {}

				@Override
				public void keyReleased(KeyEvent e) {}
			};
	}
	
}