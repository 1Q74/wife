package wife.heartcough.tree;

import java.io.File;
import java.util.Enumeration;
import java.util.List;
import java.util.regex.Pattern;

import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import wife.heartcough.common.FileSystem;
import wife.heartcough.common.Synchronizer;

public class FileTree {
	
	private FileTreeNode fileTreeNode = new FileTreeNode();
	private JTree tree;

	/**
	 * 윈도우의 경우 Desktop경로를 root노드로 하는 트리를 구성한다. 
	 * 
	 * @return FileTree를 구성하는 JTree객체
	 */
	public JTree getRoot() {
		tree = 	new JTree(fileTreeNode.getDesktopFolderNodes());
		tree.setCellRenderer(new FileTreeNodeCellRenender());
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						Synchronizer.load((DefaultMutableTreeNode)e.getPath().getLastPathComponent());
					}
				});
		    }
		});
		
		FileTreeListener listener = new FileTreeListener();
		tree.addMouseListener(listener.getMouseListener());
		tree.addKeyListener(listener.getKeyListener());
		
		return tree;
	}
	
	/**
	 * FileTree를 로드한다.
	 * 
	 * Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf() : 마우스 클릭(사용자 선택)
	 */
	public void load() {
		if(Synchronizer.getCurrentNode() != null && Synchronizer.getCurrentNode().isLeaf()) {
			fileTreeNode.setChildNode();
		}
	}

	public void refresh() {
		DefaultMutableTreeNode currentTreeNode = Synchronizer.getCurrentNode();
		Enumeration<?> children = currentTreeNode.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File file = (File)childNode.getUserObject();

			if(!file.exists()) {
				currentTreeNode.remove(childNode);
			}
		}
		
		DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
		model.reload(currentTreeNode);
	}
	
	public void removeCurrentNodeChildren() {
		Synchronizer.getCurrentNode().removeAllChildren();
		DefaultTreeModel model = (DefaultTreeModel)getTree().getModel();
		model.reload(Synchronizer.getCurrentNode());
	}
	
	public boolean search(DefaultMutableTreeNode parentNode) {
		boolean isFound = false;
		Enumeration<?> children = parentNode.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File userObject = (File)childNode.getUserObject();
			
			if(Synchronizer.getCurrentDirectory().equals(userObject)) {
				Synchronizer.synchronize((DefaultMutableTreeNode)childNode.getParent());
				return true;
			} else {
				isFound = search(childNode);
				if(isFound) break;
			}
		}
		
		return isFound;
	}
	
	/**
	 * 노드가 드라이브인지의 여부를 확인한다.
	 * 
	 * @param path 노드에 저장된 파일의 절대경로
	 * @return 파일의 경대경로가 드라이브 문자이면 true, 그렇지 않으면 false
	 */
	private boolean isDrive(String path) {
		return Pattern.matches("^[A-Z]:\\\\$", path);
	}
	
	public void searchChildNode(DefaultMutableTreeNode parentNode, int depth) {
		Enumeration<?> children = parentNode.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			
			// [내 PC]의 자식노드 중에서 드라이브가 아닌 것은 검색에서 제외한다.
			// * DirectoryPath의 변경시 무조건 드라이브 노드부터 검색하도록 한다.
			if(depth == 2 && !isDrive(((File)childNode.getUserObject()).getAbsolutePath())) {
				continue;
			}
			
			if(Synchronizer.isNextChangedDirectoryTreeNode(childNode, false)) {
				// 한번 읽어들인 자식노드는 isLeaf() == false이기 때문에
				// 변경된 가장 마지막 디렉토리일 경우 setSelectPath가 실행되도록 한다.
				//---------------------------------------------------------------------------
				// * 같은 depth의 자식노드를 읽어들어온 상태라면 마지막 디렉토리가 선택되지 않는 오류가 발생하기 때문에
				//    setSelectionPath가 실행되도록 한다.
				//---------------------------------------------------------------------------
				Synchronizer.checkHasMoreChanedDirectoryPaths((File)childNode.getUserObject());
				
       			if(childNode.isLeaf() || !Synchronizer.hasMoreChanedDirectoryPaths()) {
       				tree.setSelectionPath(new TreePath(childNode.getPath()));
       			} else {
       				searchChildNode(childNode, ++depth);
       			}
       		}
		}
	}
	
	public void change() {
		// 변경된 경로를 디렉토리 구분자로 나눈다.
		Synchronizer.setChangedDirectoryPaths();
		
		// 윈도우 : 모든 경로의 시작은 드라이브로 시작될 것이므로 먼저 [내 PC]노드를 찾는다.
		File windowsMyPC = new File(FileSystem.WINDOWS_MY_PC_NAME);
		
		DefaultMutableTreeNode root = (DefaultMutableTreeNode)tree.getModel().getRoot();
		Enumeration<?> children = root.children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File userObject = (File)childNode.getUserObject();
			
			if(userObject.equals(windowsMyPC)) {
				Synchronizer.isDirectoryPathChanged(true);
				
				if(childNode.isLeaf()) {
					TreePath childNodePath = new TreePath(childNode.getPath());
					tree.setSelectionPath(childNodePath);
				} else {
					// 바탕화면(depth:0) > 내 PC(depth:1) > NODE(depth:2);
					searchChildNode(childNode, 2);
				}
			}
		}
	}
	
	public void synchronize() {
		Enumeration<?> children = Synchronizer.getCurrentNode().children();
		
		while(children.hasMoreElements()) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode)children.nextElement();
			File file = (File)childNode.getUserObject();

			if(Synchronizer.getCurrentDirectory().equals(file)) {
				TreePath childNodePath = new TreePath((Object[])childNode.getPath());
				tree.setSelectionPath(childNodePath);
				return;
			}
		}
	}
	
	/**
	 * FileTree의 JTree객체를 리턴한다.
	 * 
	 * @return FileTree클래스의 JTree객체
	 */
	public JTree getTree() {
		return tree;
	}
	
	/**
	 * JTree의 모델을 리턴한다.
	 * 
	 * @return JTree의 모델
	 */
	public DefaultTreeModel getModel() {
		return (DefaultTreeModel)getTree().getModel();
	}
	
	/**
	 * JTree 모델의 root노드를 리턴한다.
	 * 
	 * @return JTree 모델의 root노드
	 */
	public DefaultMutableTreeNode getRootNode() {
		return (DefaultMutableTreeNode)getModel().getRoot();
	}
	
	/**
	 * FileTree에서 파일을 복사해서 root노드의 바탕화면에 붙여넣기를 실행했을 경우에만
	 * 새롭게 추가된 디렉토리 정보를 root노드에 추가한다. 
	 * 
	 * @param newDirectories 복사 시 새롭게 추가된 디렉토리들
	 */
	public void reload(List<File> newDirectories) {
		for(File dir : newDirectories) {
			getRootNode().add(new DefaultMutableTreeNode(dir));
		}
		getModel().reload();
	}

}