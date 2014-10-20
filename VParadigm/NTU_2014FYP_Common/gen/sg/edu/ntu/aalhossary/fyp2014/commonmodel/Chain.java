package sg.edu.ntu.aalhossary.fyp2014.commonmodel;

import java.util.*;

import org.biojava.bio.structure.Group;
import org.biojava.bio.structure.HetatomImpl;

public class Chain implements Particle {

	protected String name;
	public ArrayList<Residue> residues;
	public ArrayList<Atom> atomSeq;
	
	public Chain() {
		residues = new ArrayList<Residue>();
		atomSeq = new ArrayList<Atom>();
	}
	
	public String getChainName() {
		return this.name;
	}

	/**
	 * 
	 * @param chainID
	 */
	public void setChainName(String chainID) {
		this.name = chainID;
	}
	
	public ArrayList<Residue> getResidues() {
		return this.residues;
	}

	public java.util.ArrayList<Atom> getAtoms() {
		return this.atomSeq;
	}

	/**
	 * 
	 * @param chain
	 */
	public void setChainSequence(org.biojava.bio.structure.Chain chain) {
		Residue res;
		for(Group g : chain.getAtomGroups()){
			if ( g instanceof org.biojava.bio.structure.AminoAcid ){
				res = new AminoAcid();
				res.setName(((org.biojava.bio.structure.AminoAcid)g).getPDBName());
				res.setResidueSeqNum(((org.biojava.bio.structure.AminoAcid)g).getResidueNumber().getSeqNum());
				((AminoAcid) res).setAminoChar(((org.biojava.bio.structure.AminoAcid)g).getAminoType());
				residues.add(res);
			}
			else if(g instanceof HetatomImpl){
				Atom atom = new Atom();
				atom.setName(g.getPDBName());
				atom.setChainSeqNum(g.getResidueNumber().getSeqNum());
				atomSeq.add(atom);
			}
		}
	}

}