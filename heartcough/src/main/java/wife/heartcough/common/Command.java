package wife.heartcough.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;

import wife.heartcough.common.Synchronizer;
import wife.heartcough.common.Progress.LogRowData;

public class Command implements Runnable {

	private File[] sources;
	private File target;
	
	private Progress progress = new Progress();
	
	private void displayProgress(long sourceSize, long copiedSize, LogRowData logRowData, long sum) {
		progress.process(sourceSize, copiedSize, logRowData);
		progress.refreshSizeProgress(sum);
	}
	
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
//						progress.process(FileUtils.sizeOf(src), size, logRowData);
//						progress.refreshSizeProgress(count);
						displayProgress(FileUtils.sizeOf(src), size, logRowData, count);
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

	private void process(File src, File tgt) {
		if(src.isDirectory()) {
			File newDir = new File(tgt, src.getName());
			if(!newDir.exists()) {
				newDir.mkdir();
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
//			File newFile = new File(target, source.getName());
//			if(source.isDirectory() && !newFile.exists()) {
//				newFile.mkdir();
//			}
//			process(source, newFile);
			process(source, target);
		}
	}

	@Override
	public void run() {
		paste();
	}
	
}