package wife.heartcough.command;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.swing.JTable;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;

import wife.heartcough.table.FileTable;

public class Command implements KeyListener {

	private FileTable fileTable;
	
	private static final int CTRL = 2;
	private static final int C = 67;
	private static final int V = 86;
	
	private File source;
	
	public Command(FileTable fileTable) {
		this.fileTable = fileTable;
	}
	
	private File getFile(Object source) {
		File file = null;
		
		if(source instanceof JTable) {
			file = fileTable.getFile((JTable)source);
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
				break;
			case (CTRL + V):
				copy(getFile(e.getSource()));
				break;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
	
	private void copy(File target) {
		if(target.isFile()) return;
		
		File createdFile = getCreatedFile(target);
		
		if(target.isDirectory()) {
			if(source.isDirectory()) {
				try {
					System.out.println(source + " / " + createdFile);
					
					FileUtils.copyDirectory(source, createdFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

}
