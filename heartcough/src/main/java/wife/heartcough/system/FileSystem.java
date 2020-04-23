package wife.heartcough.system;

import java.io.File;

import javax.swing.filechooser.FileSystemView;

import org.apache.commons.lang3.StringUtils;

/**
 * FileSystemView와 같이 파일 시스템과 관련된 처리를 합니다.
 * 
 * @author jdk
 */
public class FileSystem {
	
	/**
	 * OS에 의존적인 FileSystemView 객체
	 * (Windows에서는 IShellFolder를 구현해 놓은 듯 하다)
	 */
	public static FileSystemView VIEW;
	
	/**
	 * Windows의 Desktop폴더
	 */
	public static File DEFAULT;
	
	/**
	 * 사용자 홈 디렉토리
	 */
	public static File HOME;
	
	/**
	 * Windows 스페셜 폴더인 [내 PC]의 디렉토리 값
	 */
	public static final String WINDOWS_MY_PC_NAME;
	
	static {
		initialize();
		WINDOWS_MY_PC_NAME = "::{20D04FE0-3AEA-1069-A2D8-08002B30309D}";
	}
	
	/**
	 * 사용할 FileSystem객체를 초기화한다.
	 */
	private static void initialize() {
		VIEW = FileSystemView.getFileSystemView();
		DEFAULT = VIEW.getRoots()[0];
		HOME = VIEW.getHomeDirectory();
	}
	
	/**
	 * 파일의 절대경로를 받아서 Desktop폴더 경로와 비교한다.
	 * 
	 * @param path 비교할 파일의 절대경로
	 * @return 비교하는 폴더가 Desktop폴더와 같은 true, 그렇지 않으면 false
	 */
	public static boolean isDesktopPath(String path) {
		return StringUtils.equals(DEFAULT.getAbsolutePath(), path);
	}
	
	/**
	 * Windows의 [내 PC]경로와 비교한다.
	 * 
	 * @param directoryName 비교할 디렉토리 경로
	 * @return [내 PC]경로와 일치하면 true, 그렇지 않으면 false
	 */
	public static boolean isWindowsMyPC(String directoryName) {
		return StringUtils.equals(directoryName, WINDOWS_MY_PC_NAME);
	}
	
	/**
	 * '::'으로 시작하는 Windows 스페셜 폴더인지 확인한다.
	 * @param path 확인할 디렉토리명
	 * @return Windows 스페셜 폴더이면 true, 그렇지 않으면 false
	 */
	public static boolean isWindowsSpecialFolder(String path) {
		return StringUtils.startsWith(path, "::");
	}
	
}
