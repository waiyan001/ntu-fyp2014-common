package sg.edu.ntu.aalhossary.fyp2014.physics_engine.core;

import java.util.ArrayList;

import sg.edu.ntu.aalhossary.fyp2014.common.AbstractParticle;

public class OctTree {
	private final int MAX_PARTICLES = 5;	// for each node
	private final int MAX_DEPTH = 3;		// NECESSARY, if the points are too close, 
											// too many recursive splitting of nodes will cause stack overflow error
	private OctTree [] nodes;
	private ArrayList <AbstractParticle> particles;
	private Vector3D centre;
	private int depth;
	private double half_size;
	
	// define an oct-tree 
	public OctTree (){
	   nodes = new OctTree[8];
	   particles = new ArrayList<>();
       this.centre = new Vector3D(0,0,0);
       this.half_size = 100e-10;
       depth = 0;
	}
	
	// define an oct-tree 
	public OctTree (int level, Vector3D centre, double half_size){
	   nodes = new OctTree[8];
	   particles = new ArrayList<>();
       this.centre = centre;
       this.half_size = half_size;
       depth = level;
	}
   
	public ArrayList <AbstractParticle> getAllParticles(){
		return particles;
	}
	public void clear() {
      particles.clear();
    
      for (int i = 0; i < nodes.length; i++) {
        if (nodes[i] != null) {
          nodes[i].clear();
          nodes[i] = null;
        }
      }
    }
    
    /*
     * Splits the node into 8 subnodes
     */
	private void split() {
		double x = centre.x;
		double y = centre.y;
		double z = centre.z;
       
		int new_depth = depth+1;
		double new_half_size = half_size*0.5;		 
      
		Vector3D c1 = new Vector3D(x-new_half_size, y-new_half_size, z-new_half_size);
		Vector3D c2 = new Vector3D(x-new_half_size, y-new_half_size, z+new_half_size);
		Vector3D c3 = new Vector3D(x-new_half_size, y+new_half_size, z-new_half_size);
		Vector3D c4 = new Vector3D(x-new_half_size, y+new_half_size, z+new_half_size);
		Vector3D c5 = new Vector3D(x+new_half_size, y-new_half_size, z-new_half_size);
		Vector3D c6 = new Vector3D(x+new_half_size, y-new_half_size, z+new_half_size);
		Vector3D c7 = new Vector3D(x+new_half_size, y+new_half_size, z-new_half_size);
		Vector3D c8 = new Vector3D(x+new_half_size, y+new_half_size, z+new_half_size);
       
		nodes[0] = new OctTree (new_depth, c1, new_half_size);
		nodes[1] = new OctTree (new_depth, c2, new_half_size);
		nodes[2] = new OctTree (new_depth, c3, new_half_size);
		nodes[3] = new OctTree (new_depth, c4, new_half_size);
		nodes[4] = new OctTree (new_depth, c5, new_half_size);
		nodes[5] = new OctTree (new_depth, c6, new_half_size);
       	nodes[6] = new OctTree (new_depth, c7, new_half_size);
       	nodes[7] = new OctTree (new_depth, c8, new_half_size);
	}
     
    private int getIndex (Vector3D centre){
    	int index = 0;
    	if (centre.x > this.centre.x)	index+=1;
    	if (centre.y > this.centre.y)	index+=2;
    	if (centre.z > this.centre.z)	index+=4;
    	 
    	if(centre.x == this.centre.x || centre.y == this.centre.y || centre.z == this.centre.z)
    		return -1;
    	 
    	return index;
     }
     
     public void insert(AbstractParticle particle){
    	 
		// if current node is not leaf, recursively find the leaf node to place particle 
		if (nodes[0] != null) {
		    int index = getIndex(particle.getPosition());
		    
		    // if not on axis
		    if(index != -1) {
		    	nodes[index].insert(particle);
		    	return;
		    }
		}
		particles.add(particle);
		//System.out.println("Tree depth:" + depth + " added Particle " + ((Atom)(particle)).getAtomicSymbol());
		
		// if max. particle count is exceeded
		if(particles.size()>MAX_PARTICLES && depth < MAX_DEPTH) {
			if(nodes[0]==null) 
				split();
			
			int i =0;
			while(i<particles.size()) {
				int index = getIndex(particles.get(i).getPosition());
			    // if not on axis
			    if(index != -1) {
			    //	System.out.println("Tree depth:" + depth + " removed Particle " + ((Atom)(particles.get(i))).getAtomicSymbol());
			    	nodes[index].insert(particles.remove(i));
			    	 
			    }
			    else {
			    //	System.out.println("Tree depth:" + depth + " ignored Particle " + ((Atom)(particles.get(i))).getAtomicSymbol());
			   	 	i++;
			    }
			}
		}
     }
     
     /*
      * Return all objects that could collide with the given object
      */
     public ArrayList <AbstractParticle> retrieve( ArrayList <AbstractParticle> potentialContacts, AbstractParticle particle) {
    	 int index = getIndex(particle.getPosition());
    	 if (index != -1 && nodes[0] != null) {
    		 nodes[index].retrieve(particles, particle);
    	 }
    	 // can collide with all objects in this node
    	 potentialContacts.addAll(particles);
    	 potentialContacts.remove(particle);
         return potentialContacts;
     }
      
     public void remove(AbstractParticle particle, Vector3D old_position){
    	 int index = getIndex(old_position);
    	 if(index != -1 && nodes[0] != null)
    		 nodes[index].remove(particle, old_position);
    	 
    	 for(AbstractParticle p: particles){
    		 if(p.getPosition() == old_position){
    			 particles.remove(p);
    			 return;
    		 }
    	 } 
     }
     
     public void remove(AbstractParticle particle){
    	 int index = getIndex(particle.getPosition());
    	 if (index != -1 && nodes[0] != null) {
    		 nodes[index].remove(particle);
   	  	 }
    	 for (AbstractParticle p : particles){
    		 if(p.getPosition() == particle.getPosition()) {
    			 particles.remove(p);
    			 return;
    		 }
    	 }
     }
      
     public void update(AbstractParticle particle, Vector3D old_position){
    	 remove(particle, old_position);
    	 insert(particle);
     }
     
     public void updateAllActiveParticles(){
    	 ArrayList <AbstractParticle> particles = World.activeParticles;
    	 ArrayList <Vector3D> positions = World.oldPositions;
    	 for(int i=0; i< particles.size(); i++)
    		 update(particles.get(i), positions.get(i));
     }
     
     public ArrayList <AbstractParticle> getAllParticles(ArrayList<AbstractParticle> particles){
    	 if (nodes[0] != null) {
    		 for(int i=0; i<8; i++)
    			 nodes[i].getAllParticles(particles);
    	 }
    	 particles.addAll(this.particles);
    	 return particles;
     }
     
     public void printTree(OctTree o){
    	  
    	  if(o.nodes[0] == null && o.particles.isEmpty())
    		  return;
    	    
		  System.out.println("=================\nDepth: " + o.depth);
		  System.out.println("centre: (" + o.centre.x + ", " + o.centre.y + ", " + o.centre.z + ")");
		  for(AbstractParticle p: o.particles)
			  System.out.println("Particle: " + ((Atom)p).getAtomicSymbol());
    	  
		  if(o.nodes[0]!=null) {
			  for (int i=0; i<8; i++) {
    			  printTree(o.nodes[i]);
    		  }
    	  }
      }
     
     
	
}
