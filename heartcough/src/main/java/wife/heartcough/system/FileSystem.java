package wife.heartcough.system;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang3.StringUtils;




public class FileSystem {
	
	public static FileSystemView VIEW;
	public static File DEFAULT;
	public static File HOME;
	public static final String WINDOWS_MY_PC_NAME;
	
	static {
		initialize();
		WINDOWS_MY_PC_NAME = "::{20D04FE0-3AEA-1069-A2D8-08002B30309D}";
	}
	
	private static void initialize() {
		VIEW = FileSystemView.getFileSystemView();
		DEFAULT = VIEW.getRoots()[0];
		HOME = VIEW.getHomeDirectory();
	}
	
	public static boolean isDesktopPath(String path) {
		return StringUtils.equals(DEFAULT.getAbsolutePath(), path);
	}
	
	public static boolean isWindowsMyPC(String directoryName) {
		return StringUtils.equals(directoryName, WINDOWS_MY_PC_NAME);
	}
	
	public static boolean isWindowsSpecialFolder(String path) {
		return StringUtils.startsWith(path, "::");
	}
	
}
