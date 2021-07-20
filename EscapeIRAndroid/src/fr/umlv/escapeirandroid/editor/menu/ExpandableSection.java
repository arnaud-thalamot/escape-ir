/**ESIPE - IR 2012/2013 - EscapeIR project for Android**/

package fr.umlv.escapeirandroid.editor.menu;

import java.util.ArrayList;

/**
 * The class ExpandableSection represents a container of children in an ExpandableListView
 *
 * @author THALAMOT Arnaud
 * @author VASSEUR Simon
 * @version $Revision: 1.0 $
 */

public class ExpandableSection {
   
	/**
	 * Title displayed in the ExpandableListView
	 */
	
	private String title;
    
	/**
	 * ArrayList of the children contained in this section e.g displayed under the title
	 */
	
	private ArrayList<String> children;
 
	/**
	 * 
	 * Returns the title of this section
	 * 
	 * @return string representing the title
	 */
	
    public String getTitle() {
        return title;
    }
    
    /**
     * Set the title of this section to the given string
     * @param title new title of this section
     */
 
    public void setTitle(String title) {
        this.title = title;
    }
    
    /**
     * Gives the list of children
     * @return ArrayList<String> list of the name of the children
     */
 
    public ArrayList<String> getArrayChildren() {
        return children;
    }

    /**
     * Sets the given has the list of children for this section
     * @param children new list of children for this section
     */
    
    public void setArrayChildren(ArrayList<String> children) {
        this.children = children;
    }
}