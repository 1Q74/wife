package wife.heartcough.table;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JTable;
import javax.swing.table.TableColumn;

import wife.heartcough.system.FileSystem;
import wife.heartcough.system.Synchronizer;


/**
 * 파일목록으로 테이블을 구성한다.
 *  
 * @author jdk
 */
public class FileTable {

	private JTable table = new JTable();
	
	/**
	 * 이벤트 리스러를 등록한다.
	 */
	public FileTable() {
		FileTableListener listener = new FileTableListener();
		
		table.addMouseListener(listener.getMouseListener());
		table.addKeyListener(listener.getKeyListener());
		table.addFocusListener(listener.getFocusListener());
	}
	
	/**
	 * 디렉토리, 파일, 디렉토리+파일을 구별해서 저장하여
	 * FileTree에서는 디렉토리만 사용하도록 한다.
	 * 
	 * FileTree, DirectoryPath등의 클래스에서 디렉토리, 파일정보를
	 * 사용할 수 있도록 Synchronizer클래스에 값을 저장한다.
	 * 
	 * @param directoryList
	 * @param fileList
	 * @param files
	 */
	private void copyToFileArray(List<File> directoryList, List<File> fileList, File[] files) {
		int directoryCount = directoryList.size();
		int fileCount = fileList.size();
		
		File[] tmpDirectories = new File[directoryCount];
		File[] tmpFiles = new File[fileCount];
		
		directoryList.toArray(tmpDirectories);
		fileList.toArray(tmpFiles);
		
		Synchronizer.setDirectories(tmpDirectories);
		Synchronizer.setFiles(tmpFiles);
		
		System.arraycopy(tmpDirectories, 0, files, 0, directoryCount);
		System.arraycopy(Synchronizer.getFiles(), 0, files, directoryCount, fileCount);
		
		Synchronizer.setFileList(files);
	}

	/**
	 * 파일목록을 읽어들인다.
	 * 윈도우 스페셜 폴더는 별도로 취급한다.
	 */
	public void addFileList() {
		// String[] filenames = Synchronizer.getCurrentNodeDirectory().list();
		File[] files = null;
		
		if(FileSystem.isWindowsSpecialFolder(Synchronizer.getCurrentNodeDirectoryName())) {
			files = Synchronizer.getCurrentNodeDirectory().listFiles();
			Synchronizer.setDirectories(files);
			Synchronizer.setFileList(files);
		} else {
			List<String> filenames = new ArrayList<String>();
			try {
				DirectoryStream<Path> dirStream = Files.newDirectoryStream(Synchronizer.getCurrentNodeDirectory().toPath());
				for(Path path : dirStream) {
					filenames.add(path.getFileName().toString());
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			int end = filenames.size();
			files = new File[end];
			
			List<File> directoryList = new ArrayList<File>();
			List<File> fileList = new ArrayList<File>();
			
			for(int i = 0; i < end; i++) {
				String filePath = 	Synchronizer.getCurrentNodeDirectoryPath()
									+ File.separatorChar
									+ filenames.get(i);

				File file = new File(filePath);
				if(file.isDirectory()) {
					directoryList.add(file);
				} else {
					fileList.add(file);
				}
			}

			copyToFileArray(directoryList, fileList, files);
		}
	}
	
	/**
	 * 테이블의 컬럼 사이즈를 설정한다.
	 */
	private void setFileIconColumn() {
		int width = 25;
		TableColumn column = table.getColumnModel().getColumn(0);
		column.setMaxWidth(width);
		column.setPreferredWidth(width);
	}
	
	/**
	 * 파일목록으로 테이블을 구성하고, 리스너를 등록한 후
	 * JTable객체를 리턴한다.
	 * 
	 * @return JTable객체
	 */
	public JTable load() {
//		addFileList();
		
		table.setModel(new FileListModel(Synchronizer.getFileList()));
		setFileIconColumn();
		
		table.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);
		table.setFillsViewportHeight(true);
		table.repaint();
		
		return table;
	}
	
}
