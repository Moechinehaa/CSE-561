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

public class genr extends ViewableAtomic {

	protected double int_arr_time;
	protected int count;
	protected int time;
	static int c = 0;
	protected int index=0;

	public genr() {
		this("genr", 10);
	}

	public genr(String name, double Int_arr_time) {
		super(name);
		addOutport("out");
		addInport("stop");
		addInport("start");
		int_arr_time = Int_arr_time;

		addTestInput("start", new entity(""));
		addTestInput("stop", new entity(""));
		
		setBackgroundColor(Color.orange);
	}

	public void initialize() {
		holdIn("active", int_arr_time);

		// phase = "passive";
		// sigma = INFINITY;
		sigma = 0;
		count = 0;
		time = 0;
		super.initialize();
	}

	public void deltext(double e, message x) {
		Continue(e);
//		System.out.println("******************** elapsed tiem for generator is " + e + "************************");
		if (phaseIs("passive")) {
			for (int i = 0; i < x.getLength(); i++)
				if (messageOnPort(x, "start", i)) {

					holdIn("active", int_arr_time);
				}
		}
		if (phaseIs("active"))
			for (int i = 0; i < x.getLength(); i++)
				if (messageOnPort(x, "stop", i))
					phase = "finishing";
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
			//time = time + 1;

			holdIn("active", int_arr_time);
		} else
			passivate();
	}

	public message out() {

		int numVisitor = (int) (Math.random() * 3) + 5;
		message m = new message();
		for (int i=0; i<numVisitor; i++) {
			visitor entity = new visitor("visitor" + count);
			content con = makeContent("out", entity);
			List<Integer> orderList = entity.getOrderList();
			//remove all 0s in orderList
			orderList.removeIf(value -> value == 0);
	        boolean status = entity.getStatus();
//			System.out.println("new entity:"+con);
//			System.out.println("Order List: " + orderList);
//	        System.out.println("Status: " + status);
			count = count + 1;
			m.add(con);
		}
		
		return m;
	}

	public void showState() {
		super.showState();
//		System.out.println("int_arr_t: " + int_arr_time);
	}

	public String getTooltipText() {
		return super.getTooltipText() + "\n" + " int_arr_time: " + int_arr_time + "\n" + " time: " + time;
	}

}