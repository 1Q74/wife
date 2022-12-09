package wife.heartcough.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;

import wife.heartcough.command.Progress.LogRowData;
import wife.heartcough.system.Synchronizer;

public class Command implements Runnable {

	/**
	 * 작업대상(원본) 파일들
	 */
	private File[] sources;
	
	/**
	 * 작업(복사,이동 등) 되어질 디렉토리 경로
	 */
	private File target;
	
	/**
	 * 원본과 동일한 이름이 존재할 경우 뒤에 '파일명(숫자)'로 자동생성된 디렉토리를 저장한다.
	 * 
	 * ■ 작업되어질 경로가 FileTree의 root노드의 Desktop일 경우 파일 데이터를 리로드 하지 않고
	 *   TreeNode에 추가해서 보여준다.
	 *   리로드 했을 경우 윈도우 스페셜 디렉토리나 기타 row index가 작은 노드들이 초기화 되기 때문에
	 *   디렉토리를 선택한 경우라면 하위 디렉토리가 삭제되고 1Depth에 있는 부모폴더만 남게된다.
	 * 
	 * ■ 리로드의 로직이 자식 노드들을 지우고 새로 읽어 들이기 때문에 발생하는 문제이나
	 *   적당한 해결책을 찾지 못하여 작업이 완료된 디렉토리를 추가하는 방식으로 보여준다.
	 */
	private List<File> newDirectories;
	
	/**
	 * 진행상태를 표시하는 Progress의 객체
	 */
	private Progress progress = new Progress();
	
	/**
	 * Progress객체를 Synchronizer쪽으로 별도 저장한다.
	 */
	public Command() {
		Synchronizer.setProgress(this.progress);
	}
	
	/**
	 * 복사를 진행한다.
	 * 
	 * @param src 원본 파일
	 * @param newFile 대상 파일
	 */
	private void copyFile(final File src, final File newFile) {
 		final LogRowData logRowData = progress.init(src, newFile.getAbsolutePath());
		
		new Thread(new Runnable() {
			public void run() {
				InputStream in = null;
				OutputStream out = null;
				
				try {
					in = new FileInputStream(src);
					out = new FileOutputStream(newFile);
					
					// [To Do] 원본 파일의 크기에 따라 버퍼를 조절할 필요가 있어 보인다.
					byte[] buffer = new byte[1024 * 1024];
					int count = 0;
					long size = 0;
					
					while((count = in.read(buffer, 0, buffer.length)) != -1 && !ProgressHandler.isStopped()) {
						out.write(buffer, 0, count);
						size += count;
						progress.progress(FileUtils.sizeOf(src), size, logRowData, count);
					}
					
					out.close();
					in.close();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					if(in != null) {
						try {
							in.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
					
					if(out != null) {
						try {
							out.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}).start();
	}
	
	/**
	 * 동일한 디렉토리나 파일이 있을 경우 이름 뒤에 '(번호)'를 붙여서 이름을 자동생성한다.
	 * 
	 * @param newFile 새롭게 생성될 이름
	 * @param index 중복방지를 위한 순차 번호
	 * @return 새롭게 생성된 파일명
	 */
	private String getUniqueFileName(File newFile, int index) {
		String fileName = "";
		
		if(newFile.isDirectory()) {
			fileName = newFile.getAbsolutePath() + "(" + index + ")";
		} else {
			fileName =	FilenameUtils.getFullPath(newFile.getAbsolutePath())
						+ FilenameUtils.getBaseName(newFile.getAbsolutePath())
						+ "(" + index + ")"
						+ "." + FilenameUtils.getExtension(newFile.getName());
		}
		
		return fileName;
	}
	
	/**
	 * 동일한 파일이 있을 경우에 번호를 붙여서 자동생성된 파일을 리턴한다.
	 * 
	 * @param newFile 새로 생성될 파일명
	 * @return 번호를 붙여서 자동생성된 파일
	 */
	private File getUniqueFile(File newFile) {
		File uniqueFile = null;
		
		for(int i = 1; ; i++) {
			uniqueFile = new File(getUniqueFileName(newFile, i));
			if(uniqueFile.exists()) {
				continue;
			} else {
				break;
			}
		}
		
		return uniqueFile;
	}
	
	/**
	 * 동일한 디렉토리가 있을 경우에 번호를 붙여서 자동생성된 디렉토리를 리턴한다.
	 * 
	 * @param newDir 새로 생성될 디렉토리명
	 * @return 번호를 붙여서 자동생성된 디렉토리
	 */
	private File getUniqueDirectory(File newDir) {
		return getUniqueFile(newDir);
	}
	
	/**
	 * 동일한 이름이 존재할 경우 작업을 계속 진행할 지 확인하는 팝업창을 출력한다.
	 * 
	 * @param newFile 새로 생성될 파일, 디렉토리
	 * @return 선택버튼의 정수값
	 */
	private int checkFileExistence(File newFile) {
		int result = JOptionPane.YES_OPTION;
		if(newFile.exists()) {
			result = JOptionPane.showOptionDialog(
				Synchronizer.getProgress().getLogTable()
				, newFile.getName() + "\nSame file name exists"
				, "Confirm"
				, JOptionPane.YES_NO_OPTION
				, JOptionPane.WARNING_MESSAGE
				, null
				, new String[] { "Overwrite", "Skip" }
				, null
			);
		}
		return result;
	}
	
	/**
	 * 파일 복사를 진행하면서 동일한 이름이 존재하는 지 확인하여 
	 * '덮이쓰기(Overwrite)'를 할 지 '건너뛰기(Skip)'할 지를 결정한다.
	 * 
	 * @param src 원본 파일
	 * @param newFile 새로 생성된 파일
	 * @param depth 복사되는 디렉토리의 depth.
	 *              1depth의 경우는 이름 뒤에 '(번호)'를 붙은 이름을 자동으로 생성하고,
	 *              2depth부터는 진행여부를 확인하는 팝업을 출력한다.
	 */
	private void doCopyFile(File src, File newFile, int depth) {
		if(newFile.exists()) {
			if(depth == 0) {
				newFile = getUniqueFile(newFile);
				copyFile(src, newFile);
			} else if(checkFileExistence(newFile) == JOptionPane.YES_OPTION) {
				copyFile(src, newFile);
			}
		} else {
			copyFile(src, newFile);
		}
	}
	
	/**
	 * CTRL+C가 어느 콤포넌트에서 발생했는지 확인해서 선택한 파일정보를 저장한다. 
	 */
	public boolean copy() {
		Object source = Synchronizer.getSourceComponent();
		
		if(Synchronizer.isCopiedFromFileTable()) {
			int[] rowIndexes = ((JTable)source).getSelectedRows();
			Synchronizer.setCurrentFilesForTable(rowIndexes);
		} else if(Synchronizer.isCopiedFromFileTree()) {
			JTree tree = (JTree)source;
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			Synchronizer.setCurrentFileForTree((File)selectedNode.getUserObject());
		}
		
		Synchronizer.isFileCopied(Synchronizer.getCurrentFiles() != null ? true : false);
		
		return Synchronizer.isFileCopied();
	}
	
	/**
	 * 중복되서 자동 생성된 디렉토리를 저장할 List객체를 초기화한다.
	 */
	private void initNewDirectories() {
		this.newDirectories = new ArrayList<File>();
	}
	
	/**
	 * 중복되서 자동 생성된 디렉토리를 List객체에 추가한다.
	 * FileTree의 root노드 Desktop에 붙여넣기가 되었는지만 구별하면 되기 때문에
	 * depth가 '0'인지 아닌지, 그리고 파일복사가 FileTree에서 실행되었는지를
	 * 확인한다.
	 *  
	 * @param newDir 새로 만들어진 디렉토리
	 * @param depth 복사되는 디렉토리의 depth.
	 */
	private void addNewDirectory(File newDir, int depth) {
		if(depth == 0 && Synchronizer.isCopiedFromFileTree()) {
			this.newDirectories.add(newDir);	
		}
	}
	
	/**
	 * 복사를 진행한다.
	 * 디렉토리는 재귀함수를 이용한다.
	 * 
	 * @param src 원본 소스
	 * @param tgt 복사되어지는 경로
	 * @param depth 복사되어지는 경로의 디렉토리 depth
	 */
	private void process(File src, File tgt, int depth) {
		if(src.isDirectory()) {
			File newDir = new File(tgt, src.getName());
			
			if(newDir.exists() && depth == 0) {
				newDir = getUniqueDirectory(newDir);
			} 
			newDir.mkdir();
			addNewDirectory(newDir, depth);
			
			// 서브 디렉토리나 파일이 없을 경우 크기가 0인 디렉토리로 LogTable에 출력한다.
			if(src.list().length == 0) {
				progress.displayZeroByteDirectory(src, newDir);
			}
			
			File[] files = src.listFiles();
			for(File file : files) {
				if(file.isDirectory()) {
					process(file, newDir, ++depth);
				} else {
					doCopyFile(file, new File(newDir, file.getName()), depth);
				}
			}
		} else {
			doCopyFile(src, new File(tgt, src.getName()), depth);
		}
	}
	
	/**
	 * FileTree에서 복사할 때 원본과 복사되어지는 경로가 같은 경우
	 * 부모 경로에 복사를 진행한다.
	 * 
	 * @return 복사되어질 경로의 디렉토리 객체
	 */
	private File getTarget() {
		try {
		// FileTree에서는 복사되는 소스와 복사되어질 대상이 같다. 
		boolean isEqualSourceAndTarget = StringUtils.equals(
			sources[0].getAbsolutePath()
			, Synchronizer.getDirectoryPath().getCurrentPath().getAbsolutePath()
		);
		
		// FileTree에서 복사할 경우
		if(isEqualSourceAndTarget) {
			File currentPath = Synchronizer.getDirectoryPath().getCurrentPath();
			String parentPath = FilenameUtils.getFullPath(currentPath.getAbsolutePath());
			target = new File(parentPath);
		// FileTable에서 복사할 경우
		} else {
			target = Synchronizer.getDirectoryPath().getCurrentPath();
		}
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		return target; 
	}
	
	/**
	 * FileTree의 root노드 Desktop에 붙여넣기가 이루어진 경우
	 * 디렉토리를 추가해서 보여주고, 그 외의 경우는 reload메소드 이용해서
	 * 파일(디렉토리) 정보를 다시 읽어들인다.
	 */
	private void reload() {
		if(Synchronizer.isDirectoryFileCountChanged()
			&& Synchronizer.isCopiedFromFileTree()
			&& Synchronizer.getCurrentNode().equals(Synchronizer.getFileTree().getRootNode())) {
			Synchronizer.getFileTree().reload(this.newDirectories);
		} else {
			Synchronizer.reload();
		}
	}

	/**
	 * 붙여넣기를 진행한다.
	 */
	private void paste() {
		target = getTarget();
		if(target.isFile()) return;
		
		progress.show();
		progress.setSumSize(sources);
		
		for(File source : sources) {
			int depth = 0;
			process(source, target, depth);
		}
		
		reload();
	}

	/**
	 * 작업진행상태 창이 강제종료되었을 경우에 작업(복사)을 중지시키키 위해
	 * Thread형태로 실행한다.
	 */
	@Override
	public void run() {
		// KeyListener에서 호출될 때 NullPointerException이 발생할 경우가 있기 때문에
		// run메소드에서 호출되도록 변경
		initNewDirectories();
		
		sources = Synchronizer.getCurrentFiles();
		
		paste();
	}
	
}