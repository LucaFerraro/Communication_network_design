
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.jom.OptimizationProblem;
import com.net2plan.interfaces.networkDesign.Demand;
import com.net2plan.interfaces.networkDesign.IAlgorithm;
import com.net2plan.interfaces.networkDesign.Link;
import com.net2plan.interfaces.networkDesign.Net2PlanException;
import com.net2plan.interfaces.networkDesign.NetPlan;
import com.net2plan.interfaces.networkDesign.Route;
import com.net2plan.libraries.GraphUtils;
import com.net2plan.libraries.WDMUtils;
import com.net2plan.utils.Constants.RoutingType;

import cern.colt.matrix.tdouble.DoubleMatrix2D;

import com.net2plan.utils.Triple;

public class RouteFormulationWP3 implements IAlgorithm
{
	
	/** The method called by Net2Plan to run the algorithm (when the user presses the "Execute" button)
	 * @param netPlan The input network design. The developed algorithm should modify it: it is the way the new design is returned
	 * @param algorithmParameters Pair name-value for the current value of the input parameters
	 * @param net2planParameters Pair name-value for some general parameters of Net2Plan
	 * @return
	 */
	@Override
	public String executeAlgorithm(NetPlan netPlan, Map<String, String> algorithmParameters, Map<String, String> net2planParameters)
	{
		/* The link capacity input parameter is the capacity of the two links in the three node design */
		final int k = Integer.parseInt (algorithmParameters.get ("k")); // if you expect a Double, you have to make the conversion
		final boolean isNonBifurcated = Boolean.parseBoolean(algorithmParameters.get ("isNonBifurcated"));
		//final int W = 10;
		final int W = Integer.parseInt (algorithmParameters.get ("W"));
		
		final int D = netPlan.getNumberOfDemands();

		/* Fix the routing type as source routing */
		netPlan.setRoutingTypeAllDemands(RoutingType.SOURCE_ROUTING,netPlan.getNetworkLayerDefault());
		
		/* Remove any routes (carried traffic) in the input design  */
		netPlan.removeAllRoutes();
		
		/* For each demand, create up to k admissible routes, the k shortest in number of hops */
		for (Demand d : netPlan.getDemands ())
		{
			List<List<Link>> kShortestPaths = GraphUtils.getKLooplessShortestPaths(netPlan.getNodes () , netPlan.getLinks () , d.getIngressNode() , d.getEgressNode() , null , k , -1 , -1 , -1 , -1, -1, -1);
			if (kShortestPaths.isEmpty()) throw new Net2PlanException ("There are no admissible routes for a demand");
			for (List<Link> sp : kShortestPaths)
				netPlan.addRoute (d , 0 , 0 , sp , null);
		}
		
		/* Create the optimization problem */
		OptimizationProblem op = new OptimizationProblem ();
		
		/* add the vector of decision variables */
		/* i-th coordinate in decision variables array, corresponds to the route of index i in the netPlan object */
		op.addDecisionVariable("r_cnw" , isNonBifurcated , new int [] {D , netPlan.getNumberOfRoutes (),W } , 0 , Double.MAX_VALUE);  // fraction between 0 and 1
		op.addDecisionVariable("v_cw",false,new int[] {D,W},0,Double.MAX_VALUE);
		
		/* Set the objective function */
		op.setInputParameter("l_p" , netPlan.getVectorRouteNumberOfLinks() , "row");
		op.setObjectiveFunction("minimize" , "sum(l_p*permute(r_cnw,[2;1;3]))");
		
		/* Add the flow satisfaction constraints (all the traffic is carried) */
		for (Demand d : netPlan.getDemands ())
		{
			op.setInputParameter("n" , NetPlan.getIndexes(d.getRoutes()) , "row"); //this is the "n" index in the sum
			op.setInputParameter("V_c" , d.getOfferedTraffic());
			op.setInputParameter ("c" , d.getIndex ());
			for(int w=0;w<W;w++) {
				op.setInputParameter("w" , w);
				op.addConstraint ("sum(r_cnw(c,n,w),2)== v_cw(c,w)");
			}
			op.addConstraint ("sum(v_cw(c,all))== V_c");
		}

		/* Add the link capacity constraints (all links carry less or equal traffic than its capacity) */
		for (Link e : netPlan.getLinks ())
		{
			op.setInputParameter("R_lk" , NetPlan.getIndexes(e.getTraversingRoutes()) , "row");
			op.setInputParameter("linkcapacity" , e.getCapacity());
			
			for(int w=0;w<W;w++) {
				op.setInputParameter("w" , w);
				op.setInputParameter("W" , W);
				op.addConstraint ("W*sum(sum(r_cnw(all,R_lk,w),2),1) <= linkcapacity");
			}	
			
			
		}

		/* call the solver to solve the problem */
		op.solve ("glpk");
		
		/* An optimal solution was not found */
		if (!op.solutionIsOptimal()) 
			throw new Net2PlanException ("An optimal solution was not found");

		final double [][][] r_cnw = op.getPrimalSolution("r_cnw").to3DArray(); //turn into array with index: c,n,w
		
		// ADDED
		final DoubleMatrix2D v_cw = op.getPrimalSolution("v_cw").view2D();
		System.out.println(v_cw);
		
		// ADDED
		System.out.println(netPlan.getVectorRouteNumberOfLinks());
		
		
		for (Route r : netPlan.getRoutes())
		{
			int index=r.getIndex();
			double traffic=0;
			
			for(Demand d : netPlan.getDemands ())
				for(int w=0;w<W;w++) {
				traffic=traffic+r_cnw[d.getIndex()][index][w];
				// ADDED
				if (r_cnw[d.getIndex()][index][w] > 0) {
					System.out.println( "n: "+index+", c: " + (d.getIndex()) +", w: "+ w +", " + r_cnw[d.getIndex()][index][w]);
				}
			}
			r.setCarriedTraffic(traffic,traffic);	//insert traffic summing all demands in all wavelengths for that route
		}
		netPlan.removeAllRoutesUnused(0.001);
		
		
		return "Ok! Total number of wavelengths used in the links: " + netPlan.getVectorLinkCarriedTraffic().zSum(); 
	}

	/** Returns a description message that will be shown in the graphical user interface
	 */
	@Override
	public String getDescription()
	{
		return "Route formulation with wavelength continuity";
	}

	
	/** Returns the list of input parameters of the algorithm. For each parameter, you shoudl return a Triple with its name, default value and a description
	 * @return
	 */
	@Override
	public List<Triple<String, String, String>> getParameters()
	{
		final List<Triple<String, String, String>> param = new LinkedList<Triple<String, String, String>> ();
		param.add (Triple.of ("k" , "10" , "Maximum number of loopless admissible paths per demand"));
		param.add (Triple.of ("W" , "10" , "Number of wavelenghts per fiber"));		
		param.add (Triple.of ("isNonBifurcated" , "false" , "True if the traffic is constrained to be non-bifurcated"));
		return param;
	}
}
