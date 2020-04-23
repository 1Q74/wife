package wife.heartcough.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 진행상태 창의 ProgressBar쓰레드를 강제종료하기 위한 클래스
 * 
 *  @author jdk
 */
public class ProgressHandler {
	
	/**
	 * ProgressBar쓰레드를 관리하기 위한 객체
	 */
	public static ExecutorService handler = Executors.newFixedThreadPool(10);

	/**
	 * 진행상태 바의 동작을 중지시킬지의 여부
	 */
	private static boolean stopState = false;
	
	/**
	 * 진행상태 쓰레드가 등록된 관리 객체를 리턴한다.
	 * 
	 * @return 쓰레드 관리 객체
	 */
	public static ExecutorService getHandler() {
		return handler;
	}
	
	/**
	 * 진행상태 쓰레드의 중지 여부를 설정한다.
	 * 
	 * @param state 쓰레드를 중지시키려면 true, 그렇지 않으면 false
	 */
	public static void isStopped(boolean state) {
		stopState = state;
	}
	
	/**
	 * 진행상태 쓰레드의 중지 여부를 리턴한다.
	 * 
	 * @return 진행상태 쓰레드의 중지 여부
	 */
	public static boolean isStopped() {
		return stopState;
	}
	
}
