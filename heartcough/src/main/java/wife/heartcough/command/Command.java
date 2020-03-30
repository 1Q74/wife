package wife.heartcough.command;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JTable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import wife.heartcough.Explorer;

public class Command implements KeyListener {

	private Explorer explorer;
	
	private static final int CTRL = 2;
	private static final int C = 67;
	private static final int V = 86;
	
	private File source;
	
	public Command(Explorer explorer) {
		this.explorer = explorer;
	}
	
	private File getFile(Object source) {
		File file = null;
		
		if(source instanceof JTable) {
			file = explorer.getFileTable().getFile((JTable)source);
		} 
		
		return file;
	}
	
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
				source = getFile(e.getSource());
				System.out.println(source);
				break;
			case (CTRL + V):
				copy(new File(explorer.getDirectoryPath().getPath().getText()));
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	private void copy(File target) {
		if(target.isFile()) return;
		
		if(target.isDirectory()) {
			System.out.println(source + " / " + target);
			if(source.isDirectory()) {
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
