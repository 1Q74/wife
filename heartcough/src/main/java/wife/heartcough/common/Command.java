package wife.heartcough.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import wife.heartcough.common.Synchronizer;
import wife.heartcough.common.Progress.LogRowData;

public class Command implements Runnable {

	private File[] sources;
	private File target;
	
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
	
	private void process(File src, File tgt, int depth) {
		if(src.isDirectory()) {
			File newDir = new File(tgt, src.getName());
			
			if(newDir.exists() && depth == 0) {
				newDir = getUniqueDirectory(newDir);
			} 
			newDir.mkdir();

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
		
	public void copy() {
		sources = Synchronizer.getCurrentFiles();
	}

	private void paste() {
		target = Synchronizer.getDirectoryPath().getCurrentPath();
		if(target.isFile()) return;
		
		progress.show();
		progress.setSumSize(sources);

		for(File source : sources) {
			int depth = 0;
			process(source, target, depth);
			Synchronizer.reload();
		}
	}

	@Override
	public void run() {
		paste();
	}
	
}