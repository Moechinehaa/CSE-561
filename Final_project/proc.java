/*     
 *    
 *  Author     : Savitha and Anindita ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 15-April-2012
 */
package Component.Jessi;

import java.awt.Color;
import java.util.ArrayList;

import GenCol.entity;
import GenCol.visitor;
import GenCol.intEnt;
import java.util.List;

import model.modeling.message;
import view.modeling.ViewableAtomic;

public class proc extends ViewableAtomic {// ViewableAtomic is used instead
	// of atomic due to its
	// graphics capability
	protected intEnt job;
	protected visitor visitor;
	protected Integer attr;
	protected Integer capacity;
//	protected Integer duration;
	protected Integer firstElement;
	protected Integer fastCount, normalCount;
	
	private List<visitor> fastQueue;
    private List<visitor> normalQueue;
    private List<visitor> loadQueue;
    
//	protected Integer desired_speed;
//	protected Integer actual_speed;
//	protected double processing_time;
//	private int elapsedTime;

//	public proc() {
//		this("proc", 1, 10, 5);
//		
//	}

//	public proc(String name, double Processing_time, int Capacity, int Duration) {
	public proc(String name, int Capacity) {
		super(name);
		addInport("in");
		addOutport("out");
		if ("attraction1".equals(name)) {
	        attr = 1;
	    }
		if ("attraction2".equals(name)) {
	        attr = 2;
	    }
		if ("attraction3".equals(name)) {
	        attr = 3;
	    }

//		processing_time = Processing_time;
		capacity = Capacity;
//		duration = Duration;
		
		fastQueue = new ArrayList<visitor>();
		normalQueue = new ArrayList<visitor>();
		loadQueue = new ArrayList<visitor>();
		
		addTestInput("in", new visitor("visitor0"));
		addTestInput("in", new visitor("visitor1"), 20);
		addTestInput("none", new visitor("visitor"));
		
		setBackgroundColor(Color.cyan);
	}

	public void initialize() {
		phase = "passive";
		sigma = INFINITY;
//		elapsedTime = 0;
		super.initialize();
	}

	public void deltext(double e, message x) {
		Continue(e);

			
		if (phaseIs("passive") || phaseIs("run")) {
			
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "in", i)) {
					visitor = (visitor)x.getValOnPort("in", i);
					firstElement = visitor.getOrderList().get(0);
						System.out.println("attraction "+ attr);
						System.out.println("Visitor name, status and order list: "+visitor.getName()+", "+visitor.getStatus()+ ","+visitor.getOrderList());
						System.out.println("Visitor first element: "+firstElement);
					
					
					if(firstElement==attr) {
						//System.out.println("Here for ride this!! Added on the waitlines");
						if (visitor.getStatus()) {
				            // If status is true, add to the queue with status true
				            fastQueue.add(visitor);
				        } else {
				            // If status is false, add to the queue with status false
				            normalQueue.add(visitor);
				        }
						if (fastQueue.size()+normalQueue.size()>=capacity){
							//System.out.println("Start Loading!");
							holdIn("loading",0);
						}
					}
				}
			}
//			System.out.println("attraction "+ attr);
//			System.out.println("FastPass Line: "+fastQueue);
//			System.out.println("Normal Line: "+normalQueue);
		}
					
				
					
//					job = x.getValOnPort2("in", i);
//					
//					//System.out.println("external: "+job.getv());
//					desired_speed = job.getv();
//					if(desired_speed>actual_speed) holdIn("increase",0);
//					else if(desired_speed<actual_speed) holdIn("decrease",0);
//					else holdIn("equal",0);
				
	}

	public void deltint() {
		//System.out.println("internal");
		fastCount = 0;
		normalCount =0;
		if (phaseIs("loading")) {
			loadQueue.clear();
			//System.out.println("check capacity full:"+capacity);
			// Bring elements from fastQueue
			// Bring elements from fastQueue
	        for (visitor visitor : fastQueue) {
	            if (loadQueue.size() <= capacity) {
	            	loadQueue.add(visitor);
	            	fastCount++;
	            } else {
	                break;
	            }
	        }

	        // Bring elements from normalQueue
	        for (visitor visitor : normalQueue) {
	            if (loadQueue.size() <= capacity) {
	            	loadQueue.add(visitor);
	            	normalCount++;
	            } else {
	                break;
	            }
	        }
	        for (int i=0; i<fastCount; i++) {
	        	if(fastQueue != null)fastQueue.remove(0);
	        }
	        for (int i=0; i<normalCount; i++) {
	        	if(normalQueue != null)normalQueue.remove(0);
	        }
	       
//		    System.out.println("Loaded Fast people: "+ fastCount+"/ normal people: "+normalCount);
//		    System.out.println("Loaded people List: "+loadQueue);
		    holdIn("run", 0);
		}
	}

	public void deltcon(double e, message x) {
//		System.out.println("confluent");
		deltint();
		deltext(e, x);

	}

	public message out() {
		message m = new message();

//		if (phaseIs("run")) {
//			
//            if (elapsedTime >= duration) {
//            	System.out.println("output something");
//            	for (visitor visitor : loadQueue) {
//    	            m.add(makeContent("out", visitor));
//    	        }
//                elapsedTime = 0; // Reset elapsed time after generating output
//                passivate();
//            } else {
//                elapsedTime++; // Increment the elapsed time
//            }
//        }
		
		
		if (phaseIs("run")) {
	        for (visitor visitor : loadQueue) {
	            m.add(makeContent("out", visitor));
	        }
	        passivate();
	    }
		
		
		return m;
	}

	public void showState() {
		super.showState();
		// System.out.println("job: " + job.getName());
	}

//	public String getTooltipText() {
//		return super.getTooltipText() + "\n" + "job: " + job.getName();
//	}
}
