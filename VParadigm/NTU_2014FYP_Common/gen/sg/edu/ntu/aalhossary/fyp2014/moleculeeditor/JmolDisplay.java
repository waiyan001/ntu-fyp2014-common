package sg.edu.ntu.aalhossary.fyp2014.moleculeeditor;

import org.biojava.bio.structure.Structure;
import org.biojava.bio.structure.align.gui.*;
import org.jmol.adapter.smarter.SmarterJmolAdapter;
import org.jmol.api.JmolViewer;
import org.jmol.java.BS;
import org.jmol.util.Logger;

import sg.edu.ntu.aalhossary.fyp2014.common.Atom;
import sg.edu.ntu.aalhossary.fyp2014.common.Model;

import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class JmolDisplay extends JPrintPanel implements ActionListener {

	private static final long serialVersionUID = -4721103453203185678L;
	private org.jmol.api.JmolViewer viewer;
	private org.jmol.api.JmolAdapter adapter;
	private org.jmol.api.JmolStatusListener statusListener;
	final java.awt.Dimension currentSize = new Dimension();
	final java.awt.Rectangle rectClip = new Rectangle();
	public static org.biojava.bio.structure.Structure structure;
	private boolean verbose;
	private JMolSelectionListener jMolSelectionListener;
	public static Model model;
	public static ArrayList<Atom> selectedAtoms;
	static ServerSocket serverSocket;
	static Socket clientSocket;
	
	public JmolDisplay() {
		super();
		statusListener = new MyJmolStatusListener(this);
		adapter = new SmarterJmolAdapter();
		Logger.setLogLevel( verbose?Logger.LEVEL_INFO:Logger.LEVEL_ERROR);
		viewer = JmolViewer.allocateViewer(this, adapter);
		viewer.setJmolCallbackListener(statusListener);
		jMolSelectionListener = new JMolSelectionListener();
		viewer.addSelectionListener(jMolSelectionListener);
		viewer.evalString("load menu \"res/jmol.mnu\"");
	}
	
	/**
	 * 
	 * @param g
	 */
	public void paint(java.awt.Graphics g) {
		getSize(currentSize);
		g.getClipBounds(rectClip);
		viewer.renderScreenImage(g, currentSize.width, currentSize.height);
	}
	
	public void refreshDisplay(){
		viewer.refresh(3, "");
	}
	
	/**
	 * 
	 * @param rasmolScript
	 */
	public void evaluateString(java.lang.String rasmolScript) {
		viewer.evalString(rasmolScript);
	}
	
	/**
	 * 
	 * @param pdbFile
	 */
	public void openStringInline(java.lang.String pdbFile) {
		viewer.openStringInline(pdbFile);
	}
	
	public JmolViewer getViewer() {
		return this.viewer;
	}
	
	/**
	 * 
	 * @param rasmolScript
	 */
	public void executeCmd(java.lang.String rasmolScript) {
		viewer.evalString(rasmolScript);
		if(rasmolScript.contains("zap")){
			model = null;
			structure = null;
		}
	}
	
	public Structure getStructure(){
		return structure;
	}

	public void setStructure(Structure structure) {
		this.structure = structure;
		String pdb = structure.toPDB();
		viewer.openStringInline(pdb);		
		evaluateString("save STATE state_1");
	}

	public void clearDisplay() {
		executeCmd("zap;");
		structure = null;
		
		viewer = null;
		adapter = null;
	}

	/**
	 * 
	 * @param fullPathName
	 * @param modelName
	 * @param fileName
	 */
	public void notifyNewFileOpen(java.lang.String fullPathName, java.lang.String modelName, java.lang.String fileName) {
		//System.out.println("notifyFileOpen: " + fullPathName + "\n" + fileName);
		structure = DataManager.readFile(fileName);
		if (model==null)
			model = new Model();
		model.setMolecule(structure);
		ToolPanel.makeModelList();
		// check objects
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter("test.txt"));
			for(int i=0;i<model.getMolecules().size();i++){
				writer.write("Molecule: " + model.getMolecules().get(i).getName());
				writer.newLine();
				for(int j=0;j<model.getMolecules().get(i).getChains().size();j++){
					writer.write("Chain Name: " + model.getMolecules().get(i).getChains().get(j).getName());
					writer.newLine();
					for(int k=0;k<model.getMolecules().get(i).getChains().get(j).getResidues().size();k++){
						writer.write("Residues: " + model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getName() + ", " + model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getResidueSeqNum());
						writer.write("\tAtoms: ");
						for(int l=0;l<model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getAtomList().size();l++){
							writer.write(model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getAtomList().get(l).name + "("+ model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getAtomList().get(l).getAtomSeqNum()+ "), ");
						}
						writer.newLine();
					}
					for(int k=0;k<model.getMolecules().get(i).getChains().get(j).getAtoms().size();k++){
						writer.write("Atom: " + model.getMolecules().get(i).getChains().get(j).getAtoms().get(k).getName() + ", " + model.getMolecules().get(i).getChains().get(j).getAtoms().get(k).getChainSeqNum());
						writer.newLine();
					}
				}
			}
			writer.close();
			writer = new BufferedWriter(new FileWriter("bonds.txt"));
			for(int i=0;i<model.getMolecules().size();i++){
				for(int j=0;j<model.getMolecules().get(i).getChains().size();j++){
					for(int k=0;k<model.getMolecules().get(i).getChains().get(j).getResidues().size();k++){
						writer.write("Residues: " + model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getName() + ", " + model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getResidueSeqNum());
						writer.newLine();
						for(int l=0;l<model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getAtomList().size();l++){
							writer.write(model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getAtomList().get(l).name + "("+ model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getAtomList().get(l).getAtomSeqNum()+ ") ");
							for(int m=0;m<model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getAtomList().get(l).getBond().size();m++){
								writer.write(model.getMolecules().get(i).getChains().get(j).getResidues().get(k).getAtomList().get(l).getBond().get(m).getBondType() + ", ");
							}
							writer.newLine();
						}
						writer.newLine();
					}
					for(int k=0;k<model.getMolecules().get(i).getChains().get(j).getAtoms().size();k++){
						writer.write("Atom: " + model.getMolecules().get(i).getChains().get(j).getAtoms().get(k).getName() + ", " + model.getMolecules().get(i).getChains().get(j).getAtoms().get(k).getChainSeqNum());
						writer.newLine();
					}
				}
			}
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void getSelected(BS values) {
		System.out.println("Selected: " + values);
		// clear the list to update latest selected atoms
		if(selectedAtoms==null)
			selectedAtoms = new ArrayList<Atom>();
		else
			selectedAtoms.clear();
		
		String[] valueGet = values.toString().split("[{ }]");
		System.out.print("NUM: ");
		for(int i=0;i<valueGet.length;i++){
			if(valueGet[i].compareTo("")==1 || valueGet[i].compareTo(" ")==1){
				// if its list of continuous atoms
				if(valueGet[i].contains(":")){
					int start = Integer.parseInt(valueGet[i].split(":")[0]);
					int end = Integer.parseInt(valueGet[i].split(":")[1]);
					for(int j=start;j<=end;j++){
						selectedAtoms.add(model.getMolecules().get(0).getAtom(j));
					}
				}
				else{
					System.out.println("[" + valueGet[i] + "]");
					//selectedAtoms.add(model.getMolecules().get(0).getAtom(Integer.parseInt(valueGet[i])));
				}
			}
		}
		System.out.println();
	}

	public void setConnection() {
		try {
			int clientNumber = 0;
			serverSocket = new ServerSocket(8765);
			System.out.println("Done server setup");
			while(true) {
				new ClientConnect(serverSocket.accept(), clientNumber++).start();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	 private static class ClientConnect extends Thread {
		 private Socket socket;
	     private int clientNumber;
		 public ClientConnect(Socket socket, int clientNumber) {
	            this.socket = socket;
	            this.clientNumber = clientNumber;
	            System.out.println("New connection with client " + clientNumber + " at " + socket);
	     }
		 
		 public void run() {
			 try {
	                //BufferedReader fromClient = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	                PrintWriter toClient = new PrintWriter(socket.getOutputStream(), true);

	                if(structure == null)
	                	toClient.println("No model to send");
	                else
	                	toClient.println(structure.toPDB());
			 }
			 catch (IOException e) {
	                e.printStackTrace();
	            } finally {
	                try {
	                    socket.close();
	                } catch (IOException e) {
	                    e.printStackTrace();
	                }
	                System.out.println("Connection with client " + clientNumber + " closed");
	            }
		 }
	 }
}