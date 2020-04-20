package wife.heartcough.common;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import javax.swing.JTable;

import wife.heartcough.common.Command;
import wife.heartcough.common.CommandHandler;
import wife.heartcough.common.ProgressHandler;
import wife.heartcough.common.Synchronizer;

public abstract class FileEventListener {
	
	public abstract MouseListener getMouseListener();
	
	public KeyListener getKeyListener() {
		return
			new KeyListener() {
				private static final int CTRL = 2;
				private static final int C = 67;
				private static final int V = 86;
				private Command command = new Command();
			
				@Override
				public void keyPressed(KeyEvent e) {
					JTable table = (JTable)e.getSource();
					int keyCodeSum = e.getModifiers() + e.getKeyCode();
					
					switch(keyCodeSum) {
						case (CTRL + C):
							int[] rowIndexes = table.getSelectedRows();
							if(rowIndexes == null || rowIndexes.length == 0) return;
							
							Synchronizer.setCurrentFiles(rowIndexes);
							command.copy();
							break;
						case (CTRL + V):
							Synchronizer.isExpandingPath(true);
							// 한번 복사한 파일을 여러 번 붙여넣기 할 경우가 있을 수 있기 때문에
							// 붙여넣기 할 때에 isStopped(false)를 실행한다.
							// 그렇지 않으면 최초에 붙여넣는 파일 리스트만 진행상태가 제대로 출력되고
							// 두 번째 부터는 진행바에 0에 멈추어 있게 된다.
							ProgressHandler.isStopped(false);
							CommandHandler.getHandler().submit(command);
							break;
					}
				}
				
				@Override
				public void keyTyped(KeyEvent e) {}

				@Override
				public void keyReleased(KeyEvent e) {}
			};
	}
	
	public FocusListener getFocusListener() {
		return 
			new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {}
				
				@Override
				public void focusGained(FocusEvent e) {
					System.out.println("== focusGained ==");
					if(Synchronizer.isDirectoryFileCountChanged()) {
						Synchronizer.isExpandingPath(true);
						Synchronizer.reload();
					}
				}
			};
	}
	
}