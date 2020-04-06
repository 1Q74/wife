package wife.heartcough.command;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Calendar;

import org.apache.commons.io.FileUtils;

import wife.heartcough.common.Progress;
import wife.heartcough.common.Progress.LogRowData;

public class Copy {
	
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
				
				new Thread(new Runnable() {
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
				}).start();
			}
		}
	}
	
	public void execute(File source, File target) {
		progress.show();
		progress.setSumSize(FileUtils.sizeOf(source));
		
		File newFile = new File(getCopiedFileName(source, target));
		newFile.mkdir();
		
		process(source, newFile);
	}
	
	private void execute() {
		File source = new File("G:\\backup\\t2");
		File target = new File("G:\\backup\\출석고사");
		
		File newFile = new File(getCopiedFileName(source, target));
		newFile.mkdir();
		
//		String targetPath = target.getAbsolutePath() + File.separatorChar + source.getName();
		process(source, newFile);
	}
	
	public static void main(String[] args) {
		Calendar start = Calendar.getInstance();
		
		System.out.println(
			"[START] "
			+ start.get(Calendar.HOUR_OF_DAY)
			+ ":" + start.get(Calendar.MINUTE)
			+ ":" + start.get(Calendar.SECOND)
		);

		Copy copy = new Copy();
		copy.execute();
		
		Calendar end = Calendar.getInstance();
		System.out.println(
			"[END] "
			+ end.get(Calendar.HOUR_OF_DAY)
			+ ":" + end.get(Calendar.MINUTE)
			+ ":" + end.get(Calendar.SECOND)
		);
	}
	
}
