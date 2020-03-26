package wife.heartcough;

import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import wife.heartcough.table.FileTable;
import wife.heartcough.tree.FileTree;




public class Explorer {

	private FileTable fileTable = new FileTable();
	private FileTree fileTree = new FileTree();
	
	private JSplitPane getSplitter() {
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		splitter.setLeftComponent(new JScrollPane(fileTree.getDesktopFolderTree()));

		fileTable.load();
		splitter.setRightComponent(new JScrollPane(fileTable.getFileTable()));

		return splitter;
	}
	
	public FileTree getFileTree() {
		return fileTree;
	}
	
	public FileTable getFileTable() {
		return fileTable;
	}
	
	private void show() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		JFrame window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setContentPane(getSplitter());
		window.setSize(1024, 768);
		window.setVisible(true);
	}
	
	public static void main(String[] args) throws IOException {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Explorer explorer = new Explorer();
				explorer.show();
				
				explorer.fileTree.setExplorer(explorer);
				explorer.fileTable.setExplorer(explorer);
			}
		});
	}
	
}