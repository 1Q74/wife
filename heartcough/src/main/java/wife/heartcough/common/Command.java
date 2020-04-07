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

	private File source;
	private File target;
	
	private Progress progress = new Progress();
	
	private String getCopiedFileName(File source, File target) {
		return target.getAbsolutePath() + File.separatorChar + source.getName();
	}

	private void process(File source, File target) {
		File[] files = source.listFiles();
		
		for(File file : files) {
			File newFile = new File(getCopiedFileName(file, target));
			
			if(file.isDirectory()) {
				newFile.mkdir();
				process(file, newFile);
			} else {
				LogRowData logRowData = progress.init(target, source.getAbsolutePath(), file);
				
				ProgressHandler.getHandler().submit(new Runnable() {
					public void run() {
						try {
							InputStream in = new FileInputStream(file);
							OutputStream out = new FileOutputStream(newFile);
							
							byte[] buffer = new byte[1024 * 1024];
							int count = 0;
							long size = 0;
							while((count = in.read(buffer, 0, buffer.length)) != -1) {
								out.write(buffer, 0, count);
								size += count;
								progress.process(FileUtils.sizeOf(file), size, logRowData);
								progress.refreshSizeProgress(count);
							}
							System.out.println(file.getAbsolutePath() + " (" + FileUtils.byteCountToDisplaySize( size) + ")");
							out.close();
							in.close();
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				});
			}
		}
	}
		
	public void copy() {
		source = Synchronizer.getCurrentFile();
	}

	private void paste() {
		target = Synchronizer.getCurrentDirectory();
		if(target.isFile()) return;
		
		if(target.isDirectory()) {
			if(source.isDirectory()) {
				progress.show();
				progress.setSumSize(FileUtils.sizeOf(source));
				
				File newFile = new File(getCopiedFileName(source, target));
				newFile.mkdir();
				
				process(source, newFile);
			}
		}
	}

	@Override
	public void run() {
		paste();
	}
	
}