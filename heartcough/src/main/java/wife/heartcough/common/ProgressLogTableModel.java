package wife.heartcough.common;

import java.io.File;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import wife.heartcough.common.FileSystem;




public class ProgressLogTableModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final String[] COLUMN_NAME = new String[] { "Progress", "Size", "Name" };
	private File[] files;
	
	public ProgressLogTableModel(File[] files) {
		this.files = files;
	}
	
	@Override
    public Class<?> getColumnClass(int column) {
		switch(column) {
			case 0:
				return String.class;
			case 1:
				return String.class;
			case 2:
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