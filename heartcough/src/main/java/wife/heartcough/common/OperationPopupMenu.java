package wife.heartcough.common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import org.apache.commons.lang3.StringUtils;

import wife.heartcough.command.Command;
import wife.heartcough.command.CommandHandler;
import wife.heartcough.command.ProgressHandler;
import wife.heartcough.system.Synchronizer;

public class OperationPopupMenu implements ActionListener {
	
	private JPopupMenu menu = new JPopupMenu();
	
	public OperationPopupMenu() {
		JMenuItem copy = new JMenuItem("Copy");
		copy.addActionListener(this);
		menu.add(copy);
		
		JMenuItem paste = new JMenuItem("Paste");
		paste.addActionListener(this);
		menu.add(paste);
		
		if(!Synchronizer.isFileCopied()) {
			paste.setEnabled(false);
		}
	}

	public void show(MouseEvent e) {
		menu.show(e.getComponent(), e.getX(), e.getY());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String actionCommand = e.getActionCommand();
		Command command = new Command();
		if(StringUtils.equals(actionCommand, "Copy")) {
			if(!command.copy()) return;
		} else if(StringUtils.equals(actionCommand, "Paste")) {
			// 붙여넣기를 했을 경우 FileTree의 노드를 펼쳐서 보여준다.
			Synchronizer.isExpandingPath(true);
			// 한번 복사한 파일을 여러 번 붙여넣기 할 경우가 있을 수 있기 때문에
			// 붙여넣기 할 때에 isStopped(false)를 실행한다.
			// 그렇지 않으면 최초에 붙여넣는 파일 리스트만 진행상태가 제대로 출력되고
			// 두 번째 부터는 진행바에 0에 멈추어 있게 된다.
			ProgressHandler.isStopped(false);
			// command객체를 강제종료할 수 있도록 하기 위해 submit메소드로 넘겨서 실행한다.
			CommandHandler.getHandler().submit(command);
		}
	}
	
}
