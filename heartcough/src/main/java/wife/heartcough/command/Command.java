package wife.heartcough.command;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import wife.heartcough.Explorer;
import wife.heartcough.Synchronizer;
import wife.heartcough.tree.FileTree;

public class Command implements KeyListener {

	private static final int CTRL = 2;
	private static final int C = 67;
	private static final int V = 86;
	
	private File source;
	
//	private File getFile(Object source) {
//		File file = null;
//		
//		if(source instanceof JTable) {
//			file = explorer.getFileTable().getFile((JTable)source);
//		} 
//		
//		return file;
//	}
	
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
//				File target = new File(explorer.getDirectoryPath().getPath().getText());
//				copy(target);
//				fileTree.reload();
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
				System.out.println(source + " / " + target);
				try {
					File createdFile = getCreatedFile(target);
					FileUtils.copyDirectory(source, createdFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
