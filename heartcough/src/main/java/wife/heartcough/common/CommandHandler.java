package wife.heartcough.common;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class CommandHandler {
	
	public static ExecutorService handler;

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
	
	public static void isStopped(boolean state) {
		stopState = state;
	}
	
	public static boolean isStopped() {
		return stopState;
	}
	
}
