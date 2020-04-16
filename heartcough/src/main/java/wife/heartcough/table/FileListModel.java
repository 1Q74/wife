package wife.heartcough.table;

import java.io.File;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import wife.heartcough.common.FileSystem;

public class FileListModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAME = new String[] { "", "Name" };
	private File[] files;
	
	public FileListModel(File[] files) {
		this.files = files;
	}
	
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
	
	@Override
	public String getColumnName(int column) {
		return COLUMN_NAME[column];
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		File file = files[rowIndex];
		
		switch(columnIndex) {
			case 0:
				return FileSystem.VIEW.getSystemIcon(file);
			case 1:
				return FileSystem.VIEW.getSystemDisplayName(file);
			default:
				break;
		}
		return "";
	}
	
	@Override
	public int getRowCount() {
		return files.length;
	}
	
	@Override
	public int getColumnCount() {
		return COLUMN_NAME.length;
	}
	
};