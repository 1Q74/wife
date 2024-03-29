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

import wife.heartcough.path.DirectoryPath;
import wife.heartcough.system.Synchronizer;
import wife.heartcough.table.FileTable;
import wife.heartcough.tree.FileTree;




public class Explorer {

	private FileTree fileTree = new FileTree();
	private FileTable fileTable = new FileTable();
	private DirectoryPath directoryPath = new DirectoryPath();
	private JFrame window = new JFrame();
	
	public Explorer() {
		Synchronizer.setFileTree(fileTree);
		Synchronizer.setFileTable(fileTable);
		Synchronizer.setDirectoryPath(directoryPath);
	}
	
	private JSplitPane getSplitter() {
		JSplitPane body = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
		body.setLeftComponent(new JScrollPane(fileTree.getRoot()));
		
		fileTable.addFileList();
		body.setRightComponent(new JScrollPane(fileTable.load()));
		
		JPanel bodyPanel = new JPanel(new BorderLayout());
		bodyPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		bodyPanel.add(body);
		
		directoryPath.setPath(Synchronizer.getCurrentNodeDirectory());
		JPanel pathPanel = new JPanel(new BorderLayout());
		pathPanel.setBorder(new EmptyBorder(5, 5, 0, 5));
		pathPanel.add(directoryPath.getPath());
		
		
		JSplitPane splitter = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		splitter.setDividerSize(0);
		splitter.setTopComponent(pathPanel);
		splitter.setBottomComponent(bodyPanel);
		
		
		return splitter;
	}
	
	private void show() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException | InstantiationException
				| IllegalAccessException | UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
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