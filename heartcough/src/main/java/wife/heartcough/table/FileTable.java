package wife.heartcough.table;

import java.io.File;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JTable;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import wife.heartcough.system.FileSystem;




public class FileTable {
	
	private Object[][] fileTableColumnHeaderSet;
	private File currentPath;
	private JTable table = new JTable();
	
	public FileTable() {
		setFileTableColumnHeaderSet();
	}
	
	private void setFileTableColumnHeaderSet() {
		fileTableColumnHeaderSet = new Object[][] {
			{ 25,		""		}
			, { 400,	"¿Ã∏ß"	}
		};
	}
	
	private String[] getFileTableColumnHeader() {
		String[] header = new String[fileTableColumnHeaderSet.length];
		int index = 0;
		
		for(Object[] columnHeaderSet : fileTableColumnHeaderSet) {
			header[index] = (String)columnHeaderSet[1];
			++index;
		}
		
		return header;
	}
	
	private Integer[] getFileTableColumnWidth() {
		Integer[] width = new Integer[fileTableColumnHeaderSet.length];
		int index = 0;
		
		for(Object[] columnHeaderSet : fileTableColumnHeaderSet) {
			width[index] = new Integer((int)columnHeaderSet[0]);
			++index;
		}
		
		return width;
	}
	
	public void setCurrentPath(File path) {
		this.currentPath = path;
	}
	
	private File getCurrentPath() {
		if(this.currentPath == null) {
			this.currentPath = FileSystem.DEFAULT; 
		}
		return this.currentPath;
	}

	private File[] getTableFileList() {
		String[] fileList = getCurrentPath().list();
		File[] listFiles = null;
		
		if(fileList == null) {
			listFiles = getCurrentPath().listFiles();
		} else {
			int end = fileList.length;
			listFiles = new File[end];
			
			for(int i = 0; i < end; i++) {
				String filePath = 	getCurrentPath().getAbsolutePath()
									+ File.separatorChar
									+ fileList[i];
				listFiles[i] = new File(filePath);
			}
		}
		
		return listFiles;
	}
	
	private Object[] getTableFileRow(File file) {
		Object[] row = new Object[fileTableColumnHeaderSet.length];
		row[0] = FileSystem.VIEW.getSystemIcon(file);
		row[1] = FileSystem.VIEW.getSystemDisplayName(file);
		return row;
	}
	
	private void setFileTableColumn() {
		Integer[] width = getFileTableColumnWidth();
		int index = 0;
		for(Integer w : width) {
			TableColumn column = table.getColumnModel().getColumn(index++);
			column.setMaxWidth(w);
			column.setPreferredWidth(w);
		}
	}
	
	public void load() {
		File[] listFiles = getTableFileList();
		Object[] columnHeader = getFileTableColumnHeader();
		
		DefaultTableModel model = new DefaultTableModel() {
			private static final long serialVersionUID = 1L;

			@Override
		    public Class<?> getColumnClass(int column) {
				switch(column) {
					case 0:
						return Icon.class;
					case 1:
						return String.class;
				}
		        
		        return String.class;
		    }
		};
		
		for(Object header : columnHeader) {
			model.addColumn(header);
		}
		
		SwingWorker<Void, File> worker = new SwingWorker<Void, File>() {
            @Override
            public Void doInBackground() {
				for(File file : listFiles) {
					publish(file);
				}
				
				return null;
            }
            
            @Override
            protected void process(List<File> chunks) {
                for (File file : chunks) {
                	Object[] row = getTableFileRow(file);
                	model.addRow(row);
                }
                table.setModel(model);
                setFileTableColumn();
            }

            @Override
            protected void done() {
				table.repaint();
            }
            
		};
		worker.execute();
	}
	
	public JTable getFileTable() {
		return table;
	}
	
}
