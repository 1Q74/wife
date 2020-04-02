package wife.heartcough.table;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.List;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import wife.heartcough.common.Progress;
import wife.heartcough.common.Progress.LogRowData;
import wife.heartcough.common.Synchronizer;




public class FileTableListener {

	public static MouseListener getMouseListener() {
		return 
			new MouseListener() {
				@Override
				public void mouseClicked(MouseEvent e) {
					int rowIndex = ((JTable)e.getSource()).getSelectedRow();
					if(rowIndex == -1) return;
					
					Synchronizer.setCurrentFile(rowIndex);
					
					if(e.getClickCount() == 2) {
						if(Synchronizer.getCurrentFile().isDirectory()) {
							Synchronizer.synchronize(Synchronizer.getCurrentFile());
						}
					}
					
					Synchronizer.restorePath();
				}
	
				@Override
				public void mousePressed(MouseEvent e) {
				}
	
				@Override
				public void mouseReleased(MouseEvent e) {
				}
	
				@Override
				public void mouseEntered(MouseEvent e) {
				}
	
				@Override
				public void mouseExited(MouseEvent e) {
				}
			};
	}
	
	public static class Command implements KeyListener {

		private static final int CTRL = 2;
		private static final int C = 67;
		private static final int V = 86;
		
		private File source;
		
		private File getCopiedDirectory(File target) {
			String sourceName = FilenameUtils.getName(source.getAbsolutePath());
			return new File(target.getAbsolutePath() + File.separatorChar + sourceName);
		}
		
		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int keyCodeSum = e.getModifiers() + e.getKeyCode();
			
			switch(keyCodeSum) {
				case (CTRL + C):
					source = Synchronizer.getCurrentFile();
					break;
				case (CTRL + V):
					File targetDirectory = Synchronizer.getCurrentDirectory();
					copy(targetDirectory);
					Synchronizer.reload();
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
		
		private void copy(File targetDirectory) {
			if(targetDirectory.isFile()) return;
			
			if(targetDirectory.isDirectory()) {
				if(source.isDirectory()) {
					new Thread(new Runnable() {
						public void run() {
							Progress.show();
							
							File copiedDirectory = getCopiedDirectory(targetDirectory);
			            	try {
								FileUtils.copyDirectory(source, copiedDirectory, new FileFilter() {
									private long sizeSum = FileUtils.sizeOfDirectory(source);
									
									@Override
									public boolean accept(File file) {
										LogRowData logRowData = Progress.init(copiedDirectory, source.getAbsolutePath(), file, sizeSum);
										if(logRowData.getRowIndex() >= 0) {
											new Thread(new Runnable() {
												public void run() {
													Progress.process(FileUtils.sizeOf(file), logRowData);
												}
											}).start();
										}
											
										return true;
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}).start();
					
					/*
					SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
			            @Override
			            public Void doInBackground() {
			            	File copiedDirectory = getCopiedDirectory(targetDirectory);
			            	
			            	try {
								FileUtils.copyDirectory(source, copiedDirectory, new FileFilter() {
									private long sizeSum = FileUtils.sizeOfDirectory(source);
									
									@Override
									public boolean accept(File file) {
										LogRowData logRowData = Progress.init(copiedDirectory, source.getAbsolutePath(), file, sizeSum);
										if(logRowData.getRowIndex() >= 0) {
											new Thread(new Runnable() {
												public void run() {
													Progress.process(FileUtils.sizeOf(file), logRowData);
												}
											}).start();
										}
										return true;
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
			                return null;
			            }
			            
			            @Override
			            protected void process(List<File> chunks) {
			            	System.out.println(chunks);
			            }

			            @Override
			            protected void done() {
			            	System.out.println("== done ==");
			            	Synchronizer.reload();
							Synchronizer.initDirectoryChanged();
			            }
			        };
			        worker.execute();
			        */
				}
			}
		}

	}

	
}