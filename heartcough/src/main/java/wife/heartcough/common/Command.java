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

import wife.heartcough.common.Synchronizer;
import wife.heartcough.common.Progress.LogRowData;

public class Command implements Runnable {

	private File[] sources;
	private File target;
	
	private Progress progress = new Progress();
	
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
//						out.write(buffer, 0, count);
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
	
	private File getUniqueDirectory(File newDir) {
		File uniqueDirectory = null;
		
		for(int i = 1; ; i++) {
			uniqueDirectory = new File(newDir.getAbsolutePath() + " (" + i + ")");
			if(uniqueDirectory.exists()) {
				continue;
			} else {
				break;
			}
		}
		
		return uniqueDirectory;
	}
	
//	JOptionPane.showConfirmDialog(
//	Synchronizer.getWindow()
//	, "Same directory name exists"
//	, "Confirm"
//	, JOptionPane.YES_NO_OPTION
//);
	
	private void process(File src, File tgt) {
		if(src.isDirectory()) {
			File newDir = new File(tgt, src.getName());
			
			if(newDir.exists()) {
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
					process(file, newDir);
				} else {
					copyFile(file, new File(newDir, file.getName()));
				}
			}
		} else {
			copyFile(src, new File(tgt, src.getName()));
		}
	}
		
	public void copy() {
		sources = Synchronizer.getCurrentFiles();
	}

	private void paste() {
		target = Synchronizer.getCurrentDirectory();
		if(target.isFile()) return;
		
		progress.show();
		progress.setSumSize(sources);
		
		for(File source : sources) {
			process(source, target);
		}
	}

	@Override
	public void run() {
		paste();
	}
	
}