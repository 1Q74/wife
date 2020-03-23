package wife.heartcough.system;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang3.StringUtils;




public class FileSystem {
	
	public static FileSystemView VIEW;
	public static File DEFAULT;
	public static File HOME;
	
	static {
		initialize();
	}
	
	private static void initialize() {
		VIEW = FileSystemView.getFileSystemView();
		DEFAULT = VIEW.getRoots()[0];
		HOME = VIEW.getHomeDirectory();
	}
	
	public static boolean isDesktopPath(String path) {
		return StringUtils.equals(DEFAULT.getAbsolutePath(), path);
	}
	
	public static boolean isWindowsSpecialFolder(String path) {
		return StringUtils.startsWith(path, "::");
	}
	
}
