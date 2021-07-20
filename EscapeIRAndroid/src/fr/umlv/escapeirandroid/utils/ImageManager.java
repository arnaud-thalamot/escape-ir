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
package fr.umlv.escapeirandroid.utils;

import java.io.File;
import java.util.HashMap;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.SparseArray;
import fr.umlv.escapeirandroid.game.EscapeIR;

/**
 * The Class ImageLoader offers to load pictures and to store them as Singleton, meaning that if a picture has already been loaded, the picture won't loaded again and it's reference stored in a cache will given instead.
 */
public class ImageManager {

	private static ImageManager imgageManager = null;
	private static final String SEPARATOR = File.separator;

	private static final float DEFAULT_MAX_IMAGE_ENEMY_WIDTH = 384f;
	private static final float DEFAULT_MAX_IMAGE_ENEMY_HEIGHT = 384f;

	/** Options for the BitmapFactory */
	private final BitmapFactory.Options options;
	private final Resources resources;
	private final Context context;
	private final float ratioHeight;
	private final float ratioWidth;
	private SparseArray<Bitmap> bitmapCache;
	private HashMap<String,Bitmap> bitmapCacheInternal;

	/**
	 * The constructor is private to prevent it to be instantiate directly from outside the class
	 */
	private ImageManager(Context context,Resources res,float rh,float rw) {
		this.context = context;
		this.resources = res;
		this.ratioHeight = rh;
		this.ratioWidth = rw;
		this.options = new BitmapFactory.Options();
		this.options.inPreferredConfig=Bitmap.Config.RGB_565;
		this.options.inScaled = true;
		this.bitmapCache = new SparseArray<Bitmap>();
		this.bitmapCacheInternal = new HashMap<String, Bitmap>();
	}

	/**
	 * Gets the single instance of the imageLoader. If the image loader hasn't been already instantiated then it instantiates it, unless it just returns the reference of the image loader. 
	 *
	 * @return the reference to the image loader object.
	 */
	public static ImageManager getInstance() {
		if (imgageManager == null) {
			imgageManager = new ImageManager(EscapeIR.CONTEXT,EscapeIR.RESOURCES,EscapeIR.RATIO_HEIGHT,EscapeIR.RATIO_WIDTH);
		}
		return imgageManager;
	}

	/**
	 * Loads an image from it's name and extension passed as a String. The methods will try to find the image in the default /images folder.
	 * If the image can't be found, it displays a corresponding error message on the default error ouput.
	 * If the image can't be read, it displays a corresponding error message on the default error ouput.
	 * 
	 * Then, if the image hasn't been already loaded it stores its reference in the cache. Unless it just returns the corresponding reference in the cache 
	 *
	 * @param imageFilename the name and extension of the image (example: "foo.png")
	 * @return the reference to the image loaded, null if the image doesn't exists or can't be read
	 */
	public Bitmap loadImage(int imageFilename){
		Bitmap image = this.bitmapCache.get(imageFilename);
		if (image == null) {
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeResource(resources,imageFilename,options);
			options.inSampleSize = calculateInSampleSize(options,(int)(options.outWidth*ratioWidth),(int)(options.outHeight*ratioHeight));
			options.inJustDecodeBounds = false;
			image = BitmapFactory.decodeResource(resources,imageFilename,options);
			this.bitmapCache.put(imageFilename, image);
		}
		return image;
	}

	/** Loads an image from a path
	 * 
	 * @param pathImage the path
	 * @return a scaled image
	 */
	public Bitmap loadImageFromPath(String pathImage){

		options.inJustDecodeBounds = true;
		System.out.println(pathImage);
		BitmapFactory.decodeFile(pathImage, options);
		options.inSampleSize = calculateInSampleSize(options, EscapeIR.CURRENT_HEIGHT, EscapeIR.CURRENT_WIDTH);
		options.inJustDecodeBounds = false;
		return BitmapFactory.decodeFile(pathImage, options);
	}

	/** Loads a scaled background
	 * 
	 * @param imageFilename the path
	 * @return a scaled image
	 */
	public Bitmap loadBackgroundImage(String imageFilename){
		Bitmap image = BitmapFactory.decodeFile(context.getCacheDir()+SEPARATOR+imageFilename,options);
		return Bitmap.createScaledBitmap(image,(EscapeIR.CURRENT_WIDTH),(EscapeIR.CURRENT_HEIGHT),true);
	}

	/** Loads an image from the internalCache
	 * 
	 * @param pathImage the path
	 * @return a scaled image
	 */
	public Bitmap loadImageFromInternalCacheStorage(String imageFilename){
		Bitmap image = this.bitmapCacheInternal.get(imageFilename);
		if (image == null) {
			options.inJustDecodeBounds = true;
			BitmapFactory.decodeFile(context.getCacheDir()+SEPARATOR+imageFilename,options);
			options.inSampleSize = calculateInSampleSize(options,(int)(options.outWidth*ratioWidth),(int)(options.outHeight*ratioHeight));
			options.inJustDecodeBounds = false;
			image = BitmapFactory.decodeFile(context.getCacheDir()+SEPARATOR+imageFilename,options);
			this.bitmapCacheInternal.put(imageFilename, image);
		}
		return image;
	}

	/** Scales Image for the level editor
	 * 
	 * @param pathImage the path
	 * @return a scaled image
	 */
	public Bitmap loadImageLevelEditor(String imageFilename){
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(imageFilename,options);

		float ratio = 1.0f;

		if((options.outWidth >= options.outHeight) && (options.outWidth > DEFAULT_MAX_IMAGE_ENEMY_WIDTH)){
			ratio  = DEFAULT_MAX_IMAGE_ENEMY_WIDTH / options.outWidth;
		}
		else if((options.outHeight > options.outWidth) && (options.outHeight > DEFAULT_MAX_IMAGE_ENEMY_HEIGHT)){
			ratio  = DEFAULT_MAX_IMAGE_ENEMY_HEIGHT / options.outHeight; 
		}

		options.inJustDecodeBounds = false;
		Bitmap image = BitmapFactory.decodeFile(imageFilename,options);
		return Bitmap.createScaledBitmap(image,(int)(options.outWidth*ratio),(int)(options.outHeight*ratio),true);
	}

	private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {

		int height = options.outHeight;
		int width = options.outWidth;
		int inSampleSize = 1;

		if (height > reqHeight || width > reqWidth) {

			int heightRatio = Math.round((float) height / (float) reqHeight);
			int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}
}
