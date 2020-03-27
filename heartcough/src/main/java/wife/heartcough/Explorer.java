package wife.heartcough;

import java.awt.BorderLayout;
import java.io.IOException;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.border.EmptyBorder;

import wife.heartcough.command.Command;
import wife.heartcough.path.DirectoryPath;
import wife.heartcough.table.FileTable;
import wife.heartcough.tree.FileTree;




public class Explorer {

	private FileTable fileTable = new FileTable();
	private FileTree fileTree = new FileTree();
	private DirectoryPath directoryPath = new DirectoryPath();
	
	private JSplitPane getSplitter() {
		fileTable.load();
		
		
		JSplitPane body = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		body.setLeftComponent(new JScrollPane(fileTree.getDesktopFolderTree()));
		body.setRightComponent(new JScrollPane(fileTable.getFileTable()));
		
		JPanel bodyPanel = new JPanel(new BorderLayout());
		bodyPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		bodyPanel.add(body);
		
		directoryPath.setPath(fileTree.getCurrentPath());
		JPanel pathPanel = new JPanel(new BorderLayout());
		pathPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		pathPanel.add(directoryPath.getPath());
		
		
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.setDividerSize(0);
		splitter.setTopComponent(pathPanel);
		splitter.setBottomComponent(bodyPanel);
		
		
		return splitter;
	}
	
	public DirectoryPath getDirectoryPath() {
		return directoryPath;
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
				
				explorer.directoryPath.setExplorer(explorer);
				explorer.fileTree.setExplorer(explorer);
				explorer.fileTable.setExplorer(explorer);
			}
		});
	}
	
}