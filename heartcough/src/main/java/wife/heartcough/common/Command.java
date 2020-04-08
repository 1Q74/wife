package wife.heartcough.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import wife.heartcough.common.Synchronizer;
import wife.heartcough.common.Progress.LogRowData;




public class Command implements Runnable {

	private File[] sources;
//	private File source;
	private File target;
	
	private Progress progress = new Progress();
	
	/*
	private String getCopiedRootPath(File source, File target) {
		return target.getAbsolutePath() + File.separatorChar + source.getName();
	}
	
	private String getNewFilePath(File file) {
		int start = this.source.getAbsolutePath().length() + 1;
		return	target.getAbsolutePath() 
				+ File.separatorChar
				+ StringUtils.substring(file.getAbsolutePath(), start);
	}
	*/
	
	private void copyFile(File src, File newFile) {
		System.out.println(src + " | " + newFile);
		
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
						progress.process(FileUtils.sizeOf(src), size, logRowData);
						progress.refreshSizeProgress(count);
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
		File[] files = src.listFiles();
		
		if(files == null) {
			copyFile(src, tgt);
		} else {
			for(File file : files) {
				File newFile = new File(tgt, file.getName());
				
				if(file.isDirectory()) {
					newFile.mkdir();
					process(file, newFile);
				} else {
					copyFile(file, newFile);
				}
			}
		}
	}
		
	public void copy() {
		System.out.println("<Command.copy>");
		sources = Synchronizer.getCurrentFiles();
	}

	private void paste() {
		System.out.println("<Command.paste>");
		target = Synchronizer.getCurrentDirectory();
		System.out.println("[target] " + target);
		if(target.isFile()) return;
		
		progress.show();
		progress.setSumSize(sources);
		
		System.out.println("sources.length = " + sources.length);
		for(File source : sources) {
//			this.source = source;
//			progress.setSumSize(FileUtils.sizeOf(source));
			
			File newFile = new File(target, source.getName());
			System.out.println("[newFile] " + newFile);
			if(source.isDirectory() && !newFile.exists()) {
				newFile.mkdir();
			}
			
			process(source, newFile);
		}
	}

	@Override
	public void run() {
		paste();
	}
	
}