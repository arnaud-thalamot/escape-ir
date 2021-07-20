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
package fr.umlv.escapeirandroid.entity;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
/**
 * The Class SpriteAnimator animates a image containing an array of sprite
 */
public class SpriteAnimator {

	protected Bitmap bitmap;	
	protected Rect sourceRect;
	protected Rect destRect;
	protected int frameNr;
	protected int currentFrame;
	protected long frameTicker;
	protected int framePeriod;

	protected int spriteWidth;
	protected int spriteHeight;

	protected int x;
	protected int y;

	/** The consutructor
	 * 
	 * @param bitmap the bitmap
	 * @param x the initial x position
	 * @param y the initial y position
	 * @param width the width of each sprite in the array
	 * @param height height of each sprite in the array
	 * @param fps the number of fps
	 * @param frameCount the number of frames in the animation
	 */
	public SpriteAnimator(Bitmap bitmap, int x, int y, int width, int height, int fps, int frameCount) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.currentFrame = 0;
		this.frameNr = frameCount;
		this.spriteWidth = bitmap.getWidth() / frameCount;
		this.spriteHeight = bitmap.getHeight();
		this.sourceRect = new Rect(0, 0, spriteWidth, spriteHeight);
		this.destRect = new Rect();
		this.framePeriod = 1000 / fps;
		this.frameTicker = 0L;
	}

	/** Update the sprite animation
	 * 
	 * @param gameTime
	 */
	public void update(long gameTime) {
		if (gameTime > frameTicker + framePeriod) {
			frameTicker = gameTime;
			currentFrame++;
			if (currentFrame >= frameNr) {
				currentFrame = 0;
			}
		}
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
	}

	/** Draw the sprite
	 * 
	 * @param canvas the canvas
	 * @param paint the paint
	 */
	public void draw(Canvas canvas,Paint paint) {
		destRect.left = x;
		destRect.top = y;
		destRect.right = x+ spriteWidth;
		destRect.bottom = y+spriteHeight;
		canvas.drawBitmap(bitmap, sourceRect, destRect,paint);
	}
}

