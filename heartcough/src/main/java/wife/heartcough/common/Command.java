package wife.heartcough.common;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import wife.heartcough.common.Progress;
import wife.heartcough.common.Progress.LogRowData;
import wife.heartcough.common.Synchronizer;




public class Command implements Runnable {

	private File source;
	private File target;
	
	private File getCopiedDirectory(File target) {
		String sourceName = FilenameUtils.getName(source.getAbsolutePath());
		return new File(target.getAbsolutePath() + File.separatorChar + sourceName);
	}
	
	private void processCopy(File targetDirectory) {
		Progress progress  = new Progress();
		progress.show();
		progress.setSumSize(FileUtils.sizeOf(source));
		
		File copiedDirectory = getCopiedDirectory(targetDirectory);
		
    	try {
			FileUtils.copyDirectory(source, copiedDirectory, new FileFilter() {
				@Override
				public boolean accept(File file) {
					if(CommandHandler.isStopped()) return false;
					
					LogRowData logRowData = progress.init(copiedDirectory, source.getAbsolutePath(), file);
					if(logRowData.getRowIndex() >= 0) {
//						new Thread(new Runnable() {
//							public void run() {
//								progress.process(FileUtils.sizeOf(file), logRowData);
//							}
//						}).start();
						ProgressHandler.getHandler().submit(
							new Runnable() {
								public void run() {
									progress.process(FileUtils.sizeOf(file), logRowData);
								}
							}
						);
					}
						
					return true;
				}
			});
		} catch(ClosedByInterruptException e) {
			// Progress Window is forcely closed 
			System.out.println("Progress Window is forcely closed");
		} catch(IOException e) {
			e.printStackTrace();
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
//				new Thread(
//					new Runnable() {
//						public void run() {
							processCopy(target);
//						}
//					}
//				).start();
			}
		}
	}

	@Override
	public void run() {
		paste();
	}
	
}