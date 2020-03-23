package wife.heartcough;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import wife.heartcough.system.FileSystem;
import wife.heartcough.table.FileTable;
import wife.heartcough.tree.FileTree;




public class Explorer {

	private FileTable fileTable = new FileTable();
	private FileTree fileTree = new FileTree(fileTable);
	
	private JSplitPane getSplitter() {
		JSplitPane splitter = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		
		splitter.setLeftComponent(new JScrollPane(fileTree.getDesktopFolderTree()));

		fileTable.load();
		splitter.setRightComponent(new JScrollPane(fileTable.getFileTable()));

		return splitter;
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
			}
		});
	}
	
}