package wife.heartcough.table;

import java.io.File;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import wife.heartcough.system.FileSystem;

public class FileListModel extends AbstractTableModel {

	private static final long serialVersionUID = 1L;

	private static final Object[][] COLUMN_SET = new Object[][] {
		{ 25,		""		}
		, { 400,	"¿Ã∏ß"	}
	};
	public static final Integer[] COLUMN_WIDTH = new Integer[COLUMN_SET.length];
	public static final String[] COLUMN_NAME = new String[COLUMN_SET.length];
	private File[] files;
	
	public FileListModel(File[] files) {
		this.files = files;
		
		setColumn();
	}
	
	private void setColumn() {
		int index = 0;
		for(Object[] column : COLUMN_SET) {
			COLUMN_WIDTH[index] = new Integer((int)column[0]);
			COLUMN_NAME[index] = new String((String)column[1]);
			++index;
		}
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
		return (String)COLUMN_NAME[column];
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
		return COLUMN_SET.length;
	}
	
};