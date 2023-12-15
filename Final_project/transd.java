/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */

package Component.Jessi;


import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import GenCol.doubleEnt;
import GenCol.entity;
import GenCol.visitor;
import java.util.ArrayList;


import model.modeling.content;
import model.modeling.message;

import view.modeling.ViewableAtomic;



public class transd extends  ViewableAtomic{
 protected Map arrived, solved,others;
 protected double clock,total_ta,observation_time,waiting_time,duration_time;
 public Double count=0.00;
 public Double count2=0.00;
 protected visitor sol,oth;
 //protected List<visitor> solvList,otherList; 
 protected ArrayList<visitor> solvList = new ArrayList<visitor>();
 protected ArrayList<visitor> otherList = new ArrayList<visitor>();
 public Double dist = 0.0;

 public transd(String  name,double Observation_time,double duration){
  super(name);
   //addInport("in");
   addOutport("waitingTime");
   //addOutport("finish");
   addOutport("T");
   //addOutport("TA");
   //addOutport("Thru");
  addInport("ariv");
  addInport("solved");
  addInport("other");
  //addOutport("out");
  arrived = new HashMap();
  solved = new HashMap();
  observation_time = Observation_time;
  duration_time = duration;
  addTestInput("ariv",new visitor("val1"));
  addTestInput("solved",new visitor("job1"));
  addTestInput("other",new visitor("val3"));
  initialize();
  
  setBackgroundColor(Color.green);
 }

 public transd() {this("transd", 200,10);}

 public void initialize(){
  phase = "active";
  sigma = observation_time;
  clock = 0;
  total_ta = 0;
  solvList = new ArrayList<visitor> ();
  otherList = new ArrayList<visitor> ();

  super.initialize();
  
 }

 public void showState(){
  super.showState();
  System.out.println("arrived: " + arrived.size());
  System.out.println("solved: " + solved.size());
  System.out.println("TA: "+compute_TA());
  System.out.println("Thruput: "+compute_Thru());
 }

 public void  deltext(double e,message  x){
//	 System.out.println("--------Transduceer elapsed time ="+e);
//	 System.out.println("-------------------------------------");
  clock = clock + e;
  Continue(e);
  visitor val;
  for(int i=0; i< x.size();i++){
    if(messageOnPort(x,"ariv",i)){
    	//generator
       val = (visitor) x.getValOnPort("ariv",i);
       System.out.println("ariv: "+val.getName()+" clock: "+clock);
       //val.setDistance();
       arrived.put(val.getName(),new doubleEnt(clock));
       //System.out.println("ariv: "+val.getName());
       
    }
    if(messageOnPort(x,"solved",i)){
       val = (visitor) x.getValOnPort("solved",i);
       //System.out.println(val.getName()+" this is solved");
       if(arrived.containsKey(val.getName())){
         //entity  ent = (entity)arrived.assoc(val.getName());
    	  // System.out.println("it get name "+clock);
	   	 doubleEnt num = (doubleEnt)arrived.get(val.getName());
	     double arrival_time = num.getv();
	     double turn_around_time = clock - arrival_time;
	     double s = turn_around_time+duration_time+val.dist;
	     val.SetWaitingTime(turn_around_time+duration_time+val.dist);
	     holdIn("busy",0);
	     //solved.put(val.getName(), new doubleEnt(clock));
	     val.renewOrderlist();
	     solvList.add(val);
	     arrived.remove(val.getName());
	    arrived.put(val.getName(),new doubleEnt(clock));
	     //arrived.remove(val.getName());
	     System.out.println("solv: "+val.getName()+" waitingTime: "+s+" t: "+turn_around_time+" d: "+duration_time+" dis: "+val.dist);
		}
    }
    if(messageOnPort(x,"other",i)){
    	//for setting the arrive time of job
    	val = (visitor) x.getValOnPort("other",i);
    	val.setDistance();
    	arrived.remove(val.getName());
    	arrived.put(val.getName(),new doubleEnt(clock));
    	//holdIn("other",0);
    	//others.put(val.getName(),new doubleEnt(clock));
    	//otherList.add(val);
    	System.out.println("other: "+val.getName()+" clock: "+clock);
    }
  }
 }

 public void  deltint(){
//  clock = clock + sigma;
//  if(count2>3)
//  passivate();
//  show_state();
	 if(phaseIs("busy")) {
		 if(clock<observation_time) {
			 clock = clock + sigma;
			 sigma = observation_time - clock;
			 phase = "passive";
		 }
		 
	 }
 }

 public  message    out( ){
  message  m = new message();
  if(phase == "busy") {
	  for(int i=0;i<solvList.size();i++) {
		  System.out.println("phase is busy in output");
		  content  con1 = makeContent("waitingTime",solvList.get(i));
		  content  con2 = makeContent("T",solvList.get(i));
		  m.add(con1);
		  m.add(con2);
	  }
  }
//  if(phase == "other") {
//	  for(int i=0;i<otherList.size();i++) {
//		  System.out.println("phase is busy in other");
//		  content con4 = makeContent("finish",otherList.get(i));
//		  m.add(con4);
//	  }
//  }
  return m;
 }
 

 public double compute_TA(){
  double avg_ta_time = 0;
  if(!solved.isEmpty())
    avg_ta_time = ((double)total_ta)/solved.size();
  return avg_ta_time;
 }

 
 public double compute_Thru(){
  double thruput = 0;
  if(clock > 0)
    thruput = solved.size()/(double)clock;
  return thruput;
 }
}