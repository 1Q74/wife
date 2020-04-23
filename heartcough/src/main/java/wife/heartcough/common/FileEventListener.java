package wife.heartcough.common;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;

import wife.heartcough.command.Command;
import wife.heartcough.command.CommandHandler;
import wife.heartcough.command.ProgressHandler;
import wife.heartcough.system.Synchronizer;

/**
 * FileTree, FileTable에서 발생하는 이벤트 Listener 클래스
 * 
 * @author jdk
 *
 */
public abstract class FileEventListener {
	
	/**
	 * 마우스 클릭 이벤트 발생 시는 로직이 달라져야 하므로 추상 메소드로 정의한다.
	 * 
	 * @return MouseListener
	 */
	public abstract MouseListener getMouseListener();

	/**
	 * 키보드 이벤트 리스너를 정의한다.
	 * 
	 * @return KeyListener
	 */
	public KeyListener getKeyListener() {
		return
			new KeyListener() {
				// Control키
				private static final int CTRL = 2;
				// 'C'키
				private static final int C = 67;
				// 'V'키
				private static final int V = 86;
				// 복사 등의 명령을 처리하는 Command 객체
				private Command command = new Command();
			
				@Override
				public void keyPressed(KeyEvent e) {
					int keyCodeSum = e.getModifiers() + e.getKeyCode();
					
					switch(keyCodeSum) {
						case (CTRL + C):
							command.copy();
							// 복사할 원본 파일이 선택되지 않은 상태
							if(Synchronizer.getCurrentFiles() == null) return;
							break;
						case (CTRL + V):
							// 붙여넣기를 했을 경우 FileTree의 노드를 펼쳐서 보여준다.
							Synchronizer.isExpandingPath(true);
							// 한번 복사한 파일을 여러 번 붙여넣기 할 경우가 있을 수 있기 때문에
							// 붙여넣기 할 때에 isStopped(false)를 실행한다.
							// 그렇지 않으면 최초에 붙여넣는 파일 리스트만 진행상태가 제대로 출력되고
							// 두 번째 부터는 진행바에 0에 멈추어 있게 된다.
							ProgressHandler.isStopped(false);
							// command객체를 강제종료할 수 있도록 하기 위해 submit메소드로 넘겨서 실행한다.
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
	
	/**
	 * FileTree, FileTable이 마우스 포커스를 받았을 경우 실행되는 Listener 정의한다.
	 * 
	 * @return FocusListener
	 */
	public FocusListener getFocusListener() {
		return 
			new FocusListener() {
				
				@Override
				public void focusLost(FocusEvent e) {}
				
				@Override
				public void focusGained(FocusEvent e) {
					// 현재 표시되고 있는 디렉토리의 파일이나 디렉토리 개수가 변경되었는지 확인하여
					// 변경사항이 있다면 데이터를 다시 읽어들인다.
					if(Synchronizer.isDirectoryFileCountChanged()) {
						Synchronizer.isExpandingPath(true);
						Synchronizer.reload();
					}
				}
			};
	}
	
}