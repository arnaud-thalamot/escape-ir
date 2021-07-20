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

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import android.content.Context;
import android.content.res.AssetManager;
import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GesturePoint;
import android.gesture.GestureStroke;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.PointF;
import android.util.Log;
import android.util.Xml;
import fr.umlv.escapeirandroid.R;
import fr.umlv.escapeirandroid.behaviour.BehaviourType;
import fr.umlv.escapeirandroid.entity.BodyDescription;
import fr.umlv.escapeirandroid.entity.EnemyType;
import fr.umlv.escapeirandroid.game.EscapeIR;
import fr.umlv.escapeirandroid.game.GameBoard;

/**
 * The Class ConfigLoader offers to load XML configuration files.
 */
public class FileManager {

	private static final String TAG = FileManager.class.getSimpleName();
	private static final String LEVEL_DIRECTORY = "level";
	private static final String GESTURE_DIRECTORY = "gestures";
	private static final String ZIP_EXTENSION = ".zip";
	private static final String XML_EXTENSION = ".xml";
	private static final String PNG_EXTENSION = ".png";
	private static final String JPG_EXTENSION = ".jpg";
	private static final String XML_TAG_BEHAVIOUR = "behaviour";
	private static final String XML_TAG_ENEMYTYPE = "enemytype";
	private static final String XML_TAG_BODY = "body";
	private static final String XML_ATTRIBUTE_NAME = "name";
	private static final String XML_ATTRIBUTE_LEVEL_BACKGROUND = "background";
	private static final String XML_ATTRIBUTE_LEVEL_DURATION = "duration";
	private static final String XML_ATTRIBUTE_BEHAVIOUR_POINTS = "points";
	private static final String XML_ATTRIBUTE_ENEMYTYPE_PICTURE = "picture";
	private static final String XML_ATTRIBUTE_ENEMYTYPE_HP = "hp";
	private static final String XML_ATTRIBUTE_ENEMYTYPE_RYTHM = "rythm";
	private static final String XML_ATTRIBUTE_ENEMYTYPE_BEHAVIOUR = "behaviourname";
	private static final String XML_ATTRIBUTE_ENEMYTYPE_MISSILE = "Missile";
	private static final String XML_ATTRIBUTE_ENEMYTYPE_FIREBALL = "Fireball";
	private static final String XML_ATTRIBUTE_ENEMYTYPE_SHIBOLEET = "Shiboleet";
	private static final String XML_ATTRIBUTE_ENEMYTYPE_TRIFORCE = "Triforce";
	private static final String XML_ATTRIBUTE_BODY_X = "x";
	private static final String XML_ATTRIBUTE_BODY_Y = "y";
	private static final String XML_ATTRIBUTE_BODY_TIME = "time";
	private static final String DELIM1 = ";";
	private static final String DELIM2 = " ";
	private static final String SEPARATOR = File.separator;
	private static int SECONDS_TO_STEP_FACTOR = 60;

	private static FileManager confLoader;
	private final Context context;

	private final DocumentBuilderFactory dbFactory;

	/**
	 * The constructor is private to prevent it to be instantiate directly from outside the class
	 */
	private FileManager(Context context) {
		this.context = context;
		this.dbFactory = DocumentBuilderFactory.newInstance();
	}

	/**
	 * Gets the single instance of the ConfigLoader object. If the config loader hasn't been already instantiated then it instantiates it, unless it just returns the reference of the config loader. 
	 *
	 * @return the reference to the ConfigLoader object.
	 */
	public static FileManager getInstance() {
		if (confLoader == null) {
			confLoader = new FileManager(EscapeIR.CONTEXT);
		}
		return confLoader;
	}

	/** Copy level from asset if the have been accidentally deleted
	 * 
	 * @param context the context
	 * @param asset the asset
	 * @return true if ok
	 */
	public boolean copyLevelAssetToInternalDir(Context context,AssetManager asset){

		String[] levels = null;
		String f = null;
		File file = null;
		InputStream in = null;
		FileOutputStream out = null;

		try {
			levels = asset.list(LEVEL_DIRECTORY);
			int len = levels.length;
			for(int i=0;i<len;i++){
				f = levels[i];
				file = context.getFileStreamPath(f);
				if(!file.exists()){
					in = asset.open(LEVEL_DIRECTORY+SEPARATOR+f);
					out = context.openFileOutput(f,Context.MODE_PRIVATE);
					copyFile(in,out);
					in.close();
					out.flush();
					out.close();
				}
			}
			return true;
		} catch (IOException e) {
			Log.d(TAG, "Unable to copy asset to internal directory "+e);
			return false;
		}
	}

	/** List the available level from the app local storage
	 * 
	 * @return the list
	 */
	public ArrayList<String> loadAvailableLevel(){
		ArrayList<String> seq = new ArrayList<String>();

		File levelDir = new File(context.getFilesDir(),"");
		final File levels[] = levelDir.listFiles();

		if (levelDir.exists()) {
			for (int i = 0; i < levels.length; i++) {
				String name = levels[i].getName();
				name = name.replace(ZIP_EXTENSION,"");
				seq.add(name);
			}
		}
		return seq;
	}

	/** Deletes a level
	 * 
	 * @param name the name of the level
	 */
	public void deleteLevel(String name){
		File file = new File(context.getFilesDir()+SEPARATOR+name+ZIP_EXTENSION);
		file.delete();
	}

	/**
	 * Loads a gameboard XML file from it's name passed as a String. The methods will try to find the XML file in the default /config folder.
	 * If the XML file can't be read, it displays a corresponding error message on the default error ouput.
	 * If the XML is malformed, it displays a corresponding error message on the default error ouput.
	 * Those errors are considered critical so the application will exit if they occur.
	 *
	 * Then, if the gameboard object corresponding hasn't been already loaded it stores its reference in the cache. Unless it just returns the corresponding reference in the cache
	 *
	 * @param gameBoardName the name of the gameboard XML file, without the extension (example : "board1")
	 * @return the reference to GameBoard object
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public GameBoard loadGameBoard(String gameBoardName) throws FileNotFoundException, IOException, ParserConfigurationException, SAXException{
		GameBoard gameboard;
		InputStream gameBoardPath = null;
		unzipLevel(context.getFilesDir().getPath(),gameBoardName+ZIP_EXTENSION);
		gameBoardPath = new FileInputStream(context.getCacheDir()+SEPARATOR+gameBoardName+XML_EXTENSION);
		gameboard = loadLevelConfigFile(gameBoardPath);
		cleanCacheDir();
		return gameboard;
	}

	/** Saves the gameboard to a zip file
	 * 
	 * @param gb the gameboard
	 * @return true if ok
	 */
	public boolean saveGameBoard(GameBoard gb) {
		try {
			File file = new File(context.getFilesDir()+gb.getName()+ZIP_EXTENSION);
			if(file.exists()) file.delete();
			ZipOutputStream out = new ZipOutputStream(context.openFileOutput(gb.getName()+ZIP_EXTENSION,Context.MODE_PRIVATE));
			addXmlToZip(gb, out);
			addPicturesToZip(gb,out);
			out.close();
			cleanCacheDir();
			Log.d(TAG, "Zip file created");
			return true;
		} catch (IOException e) {
			Log.d(TAG, "Error creating zip file "+e);
			return false;
		}
	}

	/** Loads the gesture library from asset
	 * 
	 * @return the library
	 */
	public GestureLibrary loadGestureLibrary(){

		GestureLibrary gestureLibrary = GestureLibraries.fromRawResource(context,R.raw.gestures);
		gestureLibrary.load();

		ArrayList<GesturePoint> points;
		GestureStroke stroke;
		Gesture gesture;

		String[] gestures = null;
		String gestureName = null;
		InputStream in = null;

		try {
			gestures = context.getAssets().list(GESTURE_DIRECTORY);
			int len = gestures.length;

			for(int i=0;i<len;i++){

				gestureName = gestures[i];
				in = context.getAssets().open(GESTURE_DIRECTORY+SEPARATOR+gestureName);
				points = readGestureFromXml(in);
				stroke = new GestureStroke(points);
				gesture = new Gesture();
				gesture.addStroke(stroke);
				gestureLibrary.addGesture(gestureName.replace(XML_EXTENSION,""),gesture);
				in.close();
			}
		} catch (IOException e) {
			Log.d(TAG, "Unable to load gesture library "+e);
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "Unable to parse the gesture library file "+e);
		} catch (SAXException e) {
			Log.d(TAG, "Unable to parse the gesture library file "+e);
		}
		return gestureLibrary;
	}

	/** Loads a level overt Internet
	 * 
	 * @return the gameboard
	 */
	public GameBoard loadLevelOverHttp(URL url){

		GameBoard gameboard = null;
		try {

			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setRequestMethod("GET");
			urlConnection.setInstanceFollowRedirects(false); 
			urlConnection.setDoOutput(true);
			urlConnection.connect();

			String p = url.getFile();

			String[] tokens = p.split(SEPARATOR);
			int leng = tokens.length;

			File cacheDir = new File(context.getFilesDir(),"");
			File file = new File(cacheDir,tokens[leng-1]);
			if(file.exists()) file.delete();
			FileOutputStream fileOutput = new FileOutputStream(file);
			InputStream inputStream = urlConnection.getInputStream();

			byte[] buffer = new byte[1024];
			int bufferLength = 0; 

			while ( (bufferLength = inputStream.read(buffer)) > 0 ) {
				fileOutput.write(buffer, 0, bufferLength);
			}

			fileOutput.close();
			gameboard = loadGameBoard(file.getName().replace(ZIP_EXTENSION,""));
		} catch (ParserConfigurationException e) {
			Log.d(TAG, "Unable to parse the file "+e);
		} catch (SAXException e) {
			Log.d(TAG, "Unable to parse the file "+e);
		}catch (IOException e) {
			Log.d(TAG, "Unable to download the file "+e);
		}
		return gameboard;
	}

	/** Reads behavior from xml
	 * 
	 * @param file the xml file
	 * @return an arraylist of gesturepoint
	 * @throws ParserConfigurationException
	 * @throws SAXException
	 * @throws IOException
	 */
	private ArrayList<GesturePoint> readGestureFromXml(InputStream file) throws ParserConfigurationException, SAXException, IOException{

		ArrayList<GesturePoint> f = new ArrayList<GesturePoint>();

		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = null;

		doc = dBuilder.parse(file);
		doc.getDocumentElement().normalize();

		NodeList nList = doc.getElementsByTagName("position");
		Element eElement;

		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node nNode = nList.item(temp);
			if (nNode.getNodeType() == Node.ELEMENT_NODE) {
				eElement = (Element) nNode;
				f.add(new GesturePoint(Float.valueOf(getTagValue("x", eElement)),Float.valueOf(getTagValue("y", eElement)), (long)temp));
			}
		}
		return f;
	}

	/**
	 * This method parses a gameboard XML file as it should be formed and returns the object GameBoad according to the values in the file 
	 *
	 * @param filePath the path of the XML file
	 * @return the object Gameboard corresponding to the values in the file
	 * @throws ParserConfigurationException if a DocumentBuilder cannot be created which satisfies the configuration requested 
	 * @throws SAXException If any parse errors occur
	 * @throws IOException Signals If an I/O error has occurred.
	 */
	public GameBoard loadLevelConfigFile(InputStream filePath) throws ParserConfigurationException, SAXException, IOException{

		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document d = dBuilder.parse(filePath);
		d.getDocumentElement().normalize();
		Element e = d.getDocumentElement();
		NodeList n = null;

		String boardName = e.getAttribute(XML_ATTRIBUTE_NAME);
		Bitmap background = ImageManager.getInstance().loadBackgroundImage(e.getAttribute(XML_ATTRIBUTE_LEVEL_BACKGROUND));
		float levelDuration = Float.valueOf(e.getAttribute(XML_ATTRIBUTE_LEVEL_DURATION));
		return new GameBoard(boardName,background,levelDuration,readBehaviourFromXML(d,n,e),readEnemyTypeFromXML(d,n,e),readBodyDescriptionsFromXML(d,n,e));
	}

	private HashMap<String,BehaviourType> readBehaviourFromXML(Document doc, NodeList nList, Element e){
		HashMap<String,BehaviourType> behaviourType = new HashMap<String,BehaviourType>();
		ArrayList<PointF> points = new ArrayList<PointF>();
		nList = doc.getElementsByTagName(XML_TAG_BEHAVIOUR);
		int len = nList.getLength();

		for(int i=0;i<len;i++){
			e = (Element)nList.item(i);
			String name = e.getAttribute(XML_ATTRIBUTE_NAME);
			String p = getTagValue(XML_ATTRIBUTE_BEHAVIOUR_POINTS,e);
			String[] tokens = p.split(DELIM1);
			int leng = tokens.length;
			for(int j=0;j<leng;j++){
				String[] t = tokens[j].split(DELIM2);
				points.add(new PointF(Float.valueOf(t[0]),Float.valueOf(t[1])));
			}
			behaviourType.put(e.getAttribute(XML_ATTRIBUTE_NAME),new BehaviourType(name,points));
			points.clear();
		}
		return behaviourType;
	}

	private HashMap<String,EnemyType> readEnemyTypeFromXML(Document doc, NodeList nList, Element e){
		HashMap<String,EnemyType> enemyType = new HashMap<String,EnemyType>();
		nList = doc.getElementsByTagName(XML_TAG_ENEMYTYPE);
		int len = nList.getLength();

		for(int i=0;i<len;i++){
			e = (Element)nList.item(i);
			enemyType.put(e.getAttribute(XML_ATTRIBUTE_NAME),new EnemyType(e.getAttribute(XML_ATTRIBUTE_NAME),
					ImageManager.getInstance().loadImageFromInternalCacheStorage(e.getAttribute(XML_ATTRIBUTE_ENEMYTYPE_PICTURE)),
					Integer.valueOf(e.getAttribute(XML_ATTRIBUTE_ENEMYTYPE_HP)),
					Integer.valueOf(e.getAttribute(XML_ATTRIBUTE_ENEMYTYPE_RYTHM)),
					e.getAttribute(XML_ATTRIBUTE_ENEMYTYPE_BEHAVIOUR),
					Integer.valueOf(e.getAttribute(XML_ATTRIBUTE_ENEMYTYPE_MISSILE)),
					Integer.valueOf(e.getAttribute(XML_ATTRIBUTE_ENEMYTYPE_FIREBALL)),
					Integer.valueOf(e.getAttribute(XML_ATTRIBUTE_ENEMYTYPE_SHIBOLEET)),
					Integer.valueOf(e.getAttribute(XML_ATTRIBUTE_ENEMYTYPE_TRIFORCE))));
		}
		return enemyType;
	}

	private PriorityBlockingQueue<BodyDescription> readBodyDescriptionsFromXML(Document doc, NodeList nList, Element e){
		PriorityBlockingQueue<BodyDescription> bodyDescriptions = new PriorityBlockingQueue<BodyDescription>();
		nList = doc.getElementsByTagName(XML_TAG_BODY);
		int len = nList.getLength();

		for (int i=0;i<len;i++) {
			e = (Element) nList.item(i);
			bodyDescriptions.add(new BodyDescription(Float.valueOf(e.getAttribute(XML_ATTRIBUTE_BODY_X)),
					Float.valueOf(e.getAttribute(XML_ATTRIBUTE_BODY_Y)),
					Float.valueOf(e.getAttribute(XML_ATTRIBUTE_BODY_TIME)),
					e.getAttribute(XML_ATTRIBUTE_NAME)));

		}
		return bodyDescriptions;
	}

	private void writeBehaviourToXML(GameBoard gb,XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException{

		HashMap<String,BehaviourType> b = gb.getBehaviourType();
		ArrayList<String> keys = new ArrayList<String>(b.keySet());
		ArrayList<GesturePoint> points;
		BehaviourType behav;
		GesturePoint gp;
		int len = keys.size();

		for(int i=0;i<len;i++){
			behav = b.get(keys.get(i));
			points = behav.getPoints();
			serializer.startTag(null,XML_TAG_BEHAVIOUR);
			serializer.attribute(null,XML_ATTRIBUTE_NAME,behav.getName());
			serializer.startTag(null,XML_ATTRIBUTE_BEHAVIOUR_POINTS);

			int leng = points.size();
			for(int j=0;j<leng;j++){
				gp = points.get(j);
				serializer.text(gp.x+DELIM2+gp.y+DELIM1);
			}
			serializer.endTag(null,XML_ATTRIBUTE_BEHAVIOUR_POINTS);
			serializer.endTag(null,XML_TAG_BEHAVIOUR);
		}
		Log.d(TAG,"Behaviour type writed to XML");
	}

	private void writeEnemyTypeToXML(GameBoard gb,XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException{

		HashMap<String,EnemyType> b = gb.getEnemyType();
		ArrayList<String> keys = new ArrayList<String>(b.keySet());
		EnemyType enemy;
		int len = keys.size();

		for(int i=0;i<len;i++){
			enemy = b.get(keys.get(i));
			serializer.startTag(null,XML_TAG_ENEMYTYPE);
			serializer.attribute(null,XML_ATTRIBUTE_NAME,enemy.getName());
			serializer.attribute(null,XML_ATTRIBUTE_ENEMYTYPE_PICTURE,enemy.getName()+PNG_EXTENSION);
			serializer.attribute(null,XML_ATTRIBUTE_ENEMYTYPE_HP,String.valueOf(enemy.getHp()));
			serializer.attribute(null,XML_ATTRIBUTE_ENEMYTYPE_RYTHM,String.valueOf(enemy.getRythm()));
			serializer.attribute(null,XML_ATTRIBUTE_ENEMYTYPE_BEHAVIOUR,enemy.getBehaviour());
			serializer.attribute(null,XML_ATTRIBUTE_ENEMYTYPE_MISSILE,String.valueOf(enemy.getNbMissile()));
			serializer.attribute(null,XML_ATTRIBUTE_ENEMYTYPE_FIREBALL,String.valueOf(enemy.getNbFireball()));
			serializer.attribute(null,XML_ATTRIBUTE_ENEMYTYPE_SHIBOLEET,String.valueOf(enemy.getNbShiboleet()));
			serializer.attribute(null,XML_ATTRIBUTE_ENEMYTYPE_TRIFORCE,String.valueOf(enemy.getNbTriforce()));
			serializer.endTag(null,XML_TAG_ENEMYTYPE);
		}
		Log.d(TAG,"Enemy type writed to XML");
	}

	private void writeBodyDescriptionsToXML(GameBoard gb,XmlSerializer serializer) throws IllegalArgumentException, IllegalStateException, IOException{

		PriorityBlockingQueue<BodyDescription> bodies = gb.getEnemies();
		BodyDescription body = bodies.peek();

		while((body!=null)){
			serializer.startTag(null,XML_TAG_BODY);
			serializer.attribute(null,XML_ATTRIBUTE_BODY_X,String.valueOf(body.getBodydef().position.x));
			serializer.attribute(null,XML_ATTRIBUTE_BODY_Y,String.valueOf(body.getBodydef().position.y));
			serializer.attribute(null,XML_ATTRIBUTE_BODY_TIME,String.valueOf(body.getTime()/SECONDS_TO_STEP_FACTOR));
			serializer.attribute(null,XML_ATTRIBUTE_NAME,body.getEnemyTypeName());
			serializer.endTag(null,XML_TAG_BODY);
			bodies.remove();
			body = bodies.peek();
		}
		Log.d(TAG,"Body description writed to XML");
	}

	private void cleanCacheDir(){
		File dir = context.getCacheDir();
		File[] files = dir.listFiles();
		int len = files.length;

		for (int i=0;i<len;i++) {
			files[i].delete();
		}
	}

	private void unzipLevel(String path, String zipname) throws FileNotFoundException, IOException{       

		String filename;
		File unzipPath = context.getCacheDir();
		InputStream is = new FileInputStream(path+SEPARATOR+zipname);
		ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is));          
		ZipEntry ze;
		byte[] buffer = new byte[1024];
		int count;

		while((ze = zis.getNextEntry()) != null){
			filename = ze.getName();
			FileOutputStream fout = new FileOutputStream(unzipPath+SEPARATOR+filename);
			while((count = zis.read(buffer)) != -1){
				fout.write(buffer, 0, count);             
			}
			fout.close();               
			zis.closeEntry();
		}
		zis.close();
		Log.d(TAG,"Level extracted");
	}

	/**
	 * Take the name of an XML tag as a String and an Element object. It returns the value of the tag in the file for the given element. 
	 *
	 * @param sTag the name of the tag in the file
	 * @param eElement the element in the file containing the tag
	 * @return the value of the tag for the given element
	 */
	private String getTagValue(String sTag, Element eElement) {
		NodeList nlList = eElement.getElementsByTagName(sTag).item(0).getChildNodes();
		Node nValue = nlList.item(0);
		return nValue.getNodeValue();
	}

	private void copyFile(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int read;
		while((read = in.read(buffer)) != -1){
			out.write(buffer, 0, read);
		}
	}

	private void addPicturesToZip(GameBoard gb,ZipOutputStream out) throws IOException{

		String imageName;
		OutputStream image;
		Bitmap bitmap;
		HashMap<String,EnemyType> b = gb.getEnemyType();
		ArrayList<String> keys = new ArrayList<String>(b.keySet());
		EnemyType e;
		int len = keys.size();

		for(int i=0;i<len;i++){
			e = b.get(keys.get(i));
			imageName = context.getCacheDir()+SEPARATOR+e.getName()+PNG_EXTENSION;
			image = new FileOutputStream(imageName);
			bitmap = e.getPicture();
			bitmap.compress(CompressFormat.PNG,100,image);
			image.close();
			addToZip(imageName,e.getName()+PNG_EXTENSION,out);
		}

		imageName = context.getCacheDir()+SEPARATOR+gb.getName()+JPG_EXTENSION;
		image = new FileOutputStream(imageName);
		bitmap = gb.getBackground();
		bitmap = Bitmap.createScaledBitmap(bitmap,(EscapeIR.CURRENT_WIDTH),(EscapeIR.CURRENT_HEIGHT),true);
		bitmap.compress(CompressFormat.JPEG,100,image);
		image.close();
		addToZip(imageName,gb.getName()+JPG_EXTENSION,out);
		Log.d(TAG,"Added Pictures to Zip File");
	}

	private void addXmlToZip(GameBoard gb,ZipOutputStream out) throws IllegalArgumentException, IllegalStateException, IOException{
		String name = context.getCacheDir()+SEPARATOR+gb.getName()+XML_EXTENSION;
		FileOutputStream config = new FileOutputStream(name);
		XmlSerializer serializer = Xml.newSerializer();
		serializer.setOutput(config, "UTF-8");
		serializer.startDocument(null, Boolean.valueOf(true));

		serializer.startTag(null,LEVEL_DIRECTORY);
		serializer.attribute(null,XML_ATTRIBUTE_NAME,gb.getName());
		serializer.attribute(null,XML_ATTRIBUTE_LEVEL_BACKGROUND,gb.getName()+JPG_EXTENSION);
		serializer.attribute(null,XML_ATTRIBUTE_LEVEL_DURATION,String.valueOf(gb.getLevelDuration()/SECONDS_TO_STEP_FACTOR));

		writeBehaviourToXML(gb,serializer);
		writeEnemyTypeToXML(gb,serializer);
		writeBodyDescriptionsToXML(gb, serializer);

		serializer.endTag(null,LEVEL_DIRECTORY);
		serializer.endDocument();
		serializer.flush();
		config.close();
		addToZip(name,gb.getName()+XML_EXTENSION,out);
		Log.d(TAG,"Added XML to Zip File");
	}

	private void addToZip(String file,String name,ZipOutputStream out) throws IOException{
		FileInputStream in = new FileInputStream(file);
		ZipEntry entry = new ZipEntry(name);
		out.putNextEntry(entry);
		byte buf[] = new byte[1024];
		int n ;
		while((n=in.read(buf)) >= 0){
			out.write(buf, 0, n);
		}
		in.close();
	}
}
