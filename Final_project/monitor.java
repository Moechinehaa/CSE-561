/*     
 *    
 *  Author     : ACIMS(Arizona Centre for Integrative Modeling & Simulation)
 *  Version    : DEVSJAVA 2.7 
 *  Date       : 08-15-02 
 */
package Component.Jessi;

import java.awt.Color;
import java.lang.*;
import java.util.ArrayList;
import java.util.List;

import GenCol.*;

import model.modeling.*;
import model.simulation.*;

import view.modeling.ViewableAtomic;
import view.simView.*;

public class monitor extends ViewableAtomic {

	protected double int_arr_time;
	protected int count;
	static int c = 0;
	protected int index=0;
	protected double totalFastWaitTime = 0,totalNorWaitTime = 0;
	protected int totalFastNum = 0,totalNorNum = 0;

	public monitor() {
		this("genr", 10);
	}

	public monitor(String name, double Int_arr_time) {
		super(name);
		addInport("waitingTime");
		addOutport("FastPass");
		addOutport("Normal");
		addInport("stop");
		addInport("start");
		int_arr_time = Int_arr_time;

		addTestInput("start", new entity(""));
		addTestInput("stop", new entity(""));
		
		setBackgroundColor(Color.orange);
	}

	public void initialize() {
		holdIn("passive", int_arr_time);

		//phase = "passive";
		// sigma = INFINITY;
		sigma = 0;
		count = 0;
		super.initialize();
	}

	public void deltext(double e, message x) {
		Continue(e);
		visitor val;
//		System.out.println("******************** elapsed tiem for generator is " + e + "************************");
		if (phaseIs("passive")) {
			for (int i = 0; i < x.getLength(); i++) {
				if (messageOnPort(x, "waitingTime", i)) {
					val = (visitor) x.getValOnPort("waitingTime",i);
					if(val.status == true) {
						totalFastWaitTime+=val.waitingTime;
						totalFastNum += 1;
						System.out.println("fasttime: "+totalFastWaitTime+" num: "+totalFastNum);
					}
					else if(val.status == false) {
						totalNorWaitTime+=val.waitingTime;
						totalNorNum += 1;
						System.out.println("normalTime: "+totalNorWaitTime+" num: "+totalNorNum);
					}
					//holdIn("active", int_arr_time);
				}
				if (messageOnPort(x, "stop", i)) {
					System.out.println("stop in monitor");
					holdIn("finishing",0);
				}
			}
		}
//		if (phaseIs("active"))
//			for (int i = 0; i < x.getLength(); i++)
//				if (messageOnPort(x, "stop", i))
//					phase = "finishing";
	}

	public void deltint() {
		/*
		 * System.out.println(name+" deltint count "+count);
		 * System.out.println(name+" deltint int_arr_time "+int_arr_time);
		 * System.out.println(name+" deltint tL "+tL);
		 * System.out.println(name+" deltint tN "+tN);
		 */

		// System.out.println("********generator**********" + c);
		if (phaseIs("active")) {
			count = count + 1;

			holdIn("active", 10);
		} else
			passivate();
	}

	public message out() {

		// System.out.println(name+" out count "+count);
		message m = new message();
		if(phaseIs("finishing")) {
			System.out.println("this is monitor output function");
			content con = makeContent("FastPass", new doubleEnt(totalFastWaitTime/totalFastNum));
			content con2 = makeContent("NormalPass", new doubleEnt(totalNorWaitTime/totalNorNum));
			m.add(con);
			m.add(con2);
		}
		return m;
	}

	public void showState() {
		super.showState();
//		System.out.println("int_arr_t: " + int_arr_time);
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + " int_arr_time: " + int_arr_time + "\n" + " count: " + count;
	}

}
