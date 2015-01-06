package sg.edu.ntu.aalhossary.fyp2014.moleculeeditor;

import org.jmol.api.*;
import org.jmol.c.CBK;
import org.jmol.java.BS;

import sg.edu.ntu.aalhossary.fyp2014.common.AbstractParticle;

/**
 * @author Xiu Ting
 *
 */
public class MyJmolStatusListener implements JmolStatusListener {

	private boolean verbose;
	public JmolDisplay jmolPanel;
	
	public MyJmolStatusListener(JmolDisplay jmolPanel) {
		this.jmolPanel = jmolPanel;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

	public boolean notifyEnabled(CBK callbackType) {
		//System.out.println("Callback Type: " + callbackType);
		switch (callbackType) {
	    case ANIMFRAME: return true;
	    case ATOMMOVED: return true;
	    case ECHO: return false;
	    case LOADSTRUCT: return true;
	    case MEASURE: return false;
	    case MESSAGE: return false;
	    case PICK: return false;
	    case SCRIPT: return true;
	    case CLICK: return false;
	    case ERROR: return false;
	    case HOVER: return false;
	    case MINIMIZATION: return false;
	    case RESIZE: return false;
	    case SYNC: return false;
	      // applet only (but you could change this for your listener)
	    default:
	    	return false;
	    }
	}

	public void notifyCallback(org.jmol.c.CBK callbackType, java.lang.Object[] data) {
		//System.out.println(callbackType + " " + data[0] + " " + data[1] + " " + data[2]);
		
		switch (callbackType) {
		case ANIMFRAME:
    		//getCurrentModel((String) data[2]);			
			return;
		case ATOMMOVED:
			jmolPanel.getMediator().atomMoved((BS)data[1]);
			return;
    	case LOADSTRUCT:	
    		if(data[1]==null || data[1].toString().contains("file[]"))// no data send
    			return;
    		else if(data[1].toString().contains("string")){
    			return;
    		}
    		else{
    			notifyFileLoaded((String) data[1], (String) data[2], (String) data[3], (String) data[4]);
    		}
    		return;
    	case SCRIPT:
    		// will enter when using edited library of Jmol in "res/resources/editedJmol/Jmol-edited.jar"
    		if(data[2]!=null && data[2].toString().startsWith("own ")){
    			System.out.println("Entering own function");
    			jmolPanel.getMediator().evaluateUserAction((String)data[2]);
    		}
    		return;
    	default: return;
		}
	}

	private void notifyFileLoaded(String fullPathName, String fileName, String modelName, String errorMsg) {
		if (errorMsg != null)
			return;
		jmolPanel.getMediator().createUserModel(fullPathName);	//notifyNewFileOpen(fullPathName, modelName, fileName);
	}

	public void setCallbackFunction(String callbackType, String callbackFunction) {
		System.out.println(callbackType);
		System.out.println(callbackFunction);
	}

	public String eval(String aAStrEval) {
		// TODO - implement MyJmolStatusListener.eval
		throw new UnsupportedOperationException();
	}

	public float[][] functionXY(java.lang.String aAFunctionName, int aAX, int aAY) {
		// TODO - implement MyJmolStatusListener.functionXY
		throw new UnsupportedOperationException();
	}

	public float[][][] functionXYZ(java.lang.String aArg0, int aArg1, int aArg2, int aArg3) {
		// TODO - implement MyJmolStatusListener.functionXYZ
		throw new UnsupportedOperationException();
	}
	
	public java.lang.String createImage(java.lang.String aAFileName, java.lang.String aAType, java.lang.Object aAText_or_bytes, int aAQuality) {
		// TODO - implement MyJmolStatusListener.createImage
		throw new UnsupportedOperationException();
	}

	public java.util.Map getRegistryInfo() {
		// TODO - implement MyJmolStatusListener.getRegistryInfo
		throw new UnsupportedOperationException();
	}

	public void showUrl(java.lang.String aAUrl) {
		// TODO - implement MyJmolStatusListener.showUrl
		throw new UnsupportedOperationException();
	}

	public javajs.awt.Dimension resizeInnerPanel(java.lang.String aAData) {
		// TODO - implement MyJmolStatusListener.resizeInnerPanel
		throw new UnsupportedOperationException();
	}

	public java.util.Map getJSpecViewProperty(java.lang.String aAType) {
		// TODO - implement MyJmolStatusListener.getJSpecViewProperty
		throw new UnsupportedOperationException();
	}

}