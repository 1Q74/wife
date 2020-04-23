package wife.heartcough.command;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class CommandHandler {

	/**
	 * 작업중인 쓰레드를 관리하는 객체
	 */
	public static ExecutorService handler;

	/**
	 * 작업상태 창을 강제로 종료할 경우에 사용한다.
	 * false로 설정하지 않고 작업중인 쓰레드를 강제종료하면 Exception이 발생할 수 있다.
	 */
	private static boolean stopState = false;
	
	public static ExecutorService getHandler() {
		return
			Executors.newSingleThreadExecutor(new ThreadFactory() {
				@Override
				public Thread newThread(Runnable r) {
					return new Thread(r);
				}
			});
	}
	
	/**
	 * 강제종료하기 위해 플래그 값을 설정한다.
	 * 
	 * @param state 강제종료 시는 ture, 작업을 진행하려면 false
	 */
	public static void isStopped(boolean state) {
		stopState = state;
	}
	
	/**
	 * 강제종료하기 위해 플래그 값을 리턴한다.
	 * 
	 * @return 강제종료하기 위해 플래그 값(강제종료 시는 ture, 작업을 진행하려면 false)
	 */
	public static boolean isStopped() {
		return stopState;
	}
	
}
