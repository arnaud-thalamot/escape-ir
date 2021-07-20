/******************************************************
 * 
 * ESIPE - IR 2012/2013 - EscapeIR project for Android
 * 
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * 
 * All rights reserved
 * 
 ******************************************************/
package fr.umlv.escapeirandroid.game;

/**
 * The Class GameThread is the gameloop
 */
import android.graphics.Canvas;
import android.util.Log;
import android.view.SurfaceHolder;

public class GameThread {

	private static final String TAG = GameThread.class.getSimpleName();
	private final Thread gameLoop;	

	/**
	 * Instantiates the game thread
	 *
	 * @param surfaceHolder to draw things
	 * @param gameView the view to display on it
	 */
	public GameThread(SurfaceHolder surfaceHolder,GameView gameView) {
		this.gameLoop = new Thread(new GameCode(surfaceHolder,gameView));
	}

	/**
	 * Start the thread
	 */
	public void start() {
		gameLoop.start();	
	}

	/**
	 * Stop the thread
	 */
	public void stop() {
		gameLoop.interrupt();
		Log.d(TAG, "Thread was shut down cleanly");
	}

	/**
	 * The code for the thread with the accumulator to dispatch between update and render
	 */
	public static class GameCode implements Runnable {

		private final SurfaceHolder surfaceHolder;
		private final GameView gameView;

		public GameCode(SurfaceHolder holder,GameView gameView){
			this.surfaceHolder = holder;
			this.gameView = gameView;
		}

		@Override
		public void run() {

			Canvas canvas = null;
			Log.d(TAG, "Starting game loop");

			while(!Thread.interrupted()){

				final long newTime = System.currentTimeMillis();					
				long frameTime = (newTime - gameView.currentTime);
				gameView.currentTime = newTime;
				gameView.accumulator = gameView.accumulator + frameTime;

				while(gameView.accumulator >= gameView.timeDifferential){
					if(gameView.gameState == State.gameState.IN_PROGRESS){
						gameView.update();
						gameView.accumulator = gameView.accumulator - gameView.timeDifferential;
					}
					try {
						canvas = surfaceHolder.lockCanvas(null);
						if (canvas != null) {
							gameView.render(canvas,System.currentTimeMillis());
						}

					} finally {
						if (canvas != null) {
							surfaceHolder.unlockCanvasAndPost(canvas);
						}
					}
				}
			}
		}
	}
}
