package wife.heartcough.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CommandHandler {
	
	public static ExecutorService handler = Executors.newCachedThreadPool();

	private static boolean stopState = false;
	
	public static ExecutorService getHandler() {
		return handler;
	}
	
	public static void isStopped(boolean state) {
		stopState = state;
	}
	
	public static boolean isStopped() {
		return stopState;
	}
	
}
