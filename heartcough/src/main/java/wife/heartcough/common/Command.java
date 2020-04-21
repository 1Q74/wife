package wife.heartcough.common;

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

import wife.heartcough.common.Synchronizer;
import wife.heartcough.common.Progress.LogRowData;

public class Command implements Runnable {

	private File[] sources;
	private File target;
	private List<File> newDirectories;
	
	private Progress progress = new Progress();
	
	public Command() {
		Synchronizer.setProgress(this.progress);
	}
	
	/**
	 * 복사를 진행한다.
	 * 
	 * @param src 원본 파일
	 * @param newFile 대상 파일
	 */
	private void copyFile(File src, File newFile) {
 		LogRowData logRowData = progress.init(src, newFile.getAbsolutePath());
		
		new Thread(new Runnable() {
			public void run() {
				InputStream in = null;
				OutputStream out = null;
				
				try {
					in = new FileInputStream(src);
					out = new FileOutputStream(newFile);
					
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
	
	private File getUniqueDirectory(File newDir) {
		return getUniqueFile(newDir);
	}
	
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
	
	public void copy() {
		Object source = Synchronizer.getSourceComponent();
		
		if(Synchronizer.isCopiedFromFileTable()) {
			int[] rowIndexes = ((JTable)source).getSelectedRows();
			Synchronizer.setCurrentFilesForTable(rowIndexes);
		} else if(Synchronizer.isCopiedFromFileTree()) {
			JTree tree = (JTree)source;
			DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
			Synchronizer.setCurrentFileForTree((File)selectedNode.getUserObject());
		}
		
		sources = Synchronizer.getCurrentFiles();
	}
	
	private void initNewDirectories() {
		this.newDirectories = new ArrayList<File>();
	}
	
	private void addNewDirectory(File newDir, int depth) {
		if(depth == 0 && Synchronizer.isCopiedFromFileTree()) {
			this.newDirectories.add(newDir);	
		}
	}
	
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
		
	private File getTarget() {
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
		
		return target; 
	}
	
	private void reload() {
		if(Synchronizer.isDirectoryFileCountChanged()
			&& Synchronizer.isCopiedFromFileTree()
			&& Synchronizer.getCurrentNode().equals(Synchronizer.getFileTree().getRootNode())) {
			Synchronizer.getFileTree().reload(this.newDirectories);
		} else {
			Synchronizer.reload();
		}
	}

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

	@Override
	public void run() {
		// KeyListener에서 호출될 때 NullPointerException이 발생할 경우가 있기 때문에
		// run메소드에서 호출되도록 변경
		initNewDirectories();
		
		paste();
	}
	
}