package wife.heartcough.table;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

import javax.swing.JTable;
import javax.swing.SwingWorker;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import wife.heartcough.CommandProgressBar;
import wife.heartcough.Synchronizer;




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
		
		private File getCreatedFile(File target) {
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
					File target = Synchronizer.getCurrentDirectory();
					copy(target);
					Synchronizer.reload();
					break;
			}
		}

		@Override
		public void keyReleased(KeyEvent e) {
		}
		
		private void copy(File target) {
			if(target.isFile()) return;
			
			if(target.isDirectory()) {
				if(source.isDirectory()) {
					CommandProgressBar.show();
					
					SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
						private int percent = 0;
						
			            @Override
			            public Void doInBackground() {
			            	File createdFile = getCreatedFile(target);
			            	
			            	try {
								FileUtils.copyDirectory(source, createdFile, new FileFilter() {
									private long sizeSum = FileUtils.sizeOfDirectory(source);
									
									@Override
									public boolean accept(File file) {
//										if(file.isFile()) {
//											percent += Math.round(((double)FileUtils.sizeOf(file) / (double)sizeSum) * 100);
//										}
										CommandProgressBar.progress(file, sizeSum);
										return true;
									}
								});
							} catch (IOException e) {
								e.printStackTrace();
							}
			                return null;
			            }

			            @Override
			            protected void done() {
			            	System.out.println("== done ==");
			            }
			        };
			        worker.execute();
				}
			}
		}

	}

	
}