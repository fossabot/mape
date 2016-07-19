package ch.uzh.glapp;

import ch.uzh.glapp.model.sails.cellinfo.Cell;
import ch.uzh.glapp.model.sails.ruleinfo.Organ;
import ch.uzh.glapp.model.sails.ruleinfo.Rule;
import ch.uzh.glapp.PrometheusRetriever.MetricNotFoundException;
import ch.uzh.glapp.mdp.BasicBehaviorMape;
import ch.uzh.glapp.model.ObjectForMdp;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class MainLoop {
	public static String prometheusServerIP;
	public static int prometheusServerPort;
	public static boolean ruleViolated;
	
    public static void main (String[] args) throws IOException {
    	
    	// Read the config file
    	FileInputStream configFile = new FileInputStream("config.txt");
    	Properties config = new Properties();
    	config.load(configFile);
    	configFile.close();
    	
    	prometheusServerIP = config.getProperty("prometheusServerIP");
    	prometheusServerPort = Integer.parseInt(config.getProperty("prometheusServerPort"));
    	ruleViolated = false;
    	
    	MapeUtils mapeUtils = new MapeUtils();
    	SailsRetriever sa = new SailsRetriever();
    	
//    	prometheusRetriever.findContainerID("bcf548d11ab3");

//	    List<Cell> cells = sa.getCellInfo();
//	    for (Cell c : cells) {
//		    System.out.println("Cell ID: " + c.getHost().getLabels().getRegion());
//	    }

        Map<String, String> appMap;
		List<String> appList = new ArrayList<>();
		String appId;

        appMap = sa.getAppIds();

        // Display app status
        System.out.println("Application status:");
		for (String appKey: appMap.keySet()){
			String value = appMap.get(appKey);
			System.out.println(appKey + " " + value);
			if ("deployed".equals(value)) {
				appList.add(appKey);
			}

		}
		System.out.println();

		// Start processing the applications one by one
        for (int i = 0; i < appList.size(); ++i) {
        	boolean ruleViolated = false;
        	
        	appId = appList.get(i);
        	
        	System.out.println("MAPE started for app ID: " + appList.get(i) + "\n");
        	System.out.println("Stage 1: Get data and compute healthiness value");
        	
        	// Stage 1: Get data and compute healthiness value
            // 1. Retrieve user defined policy (a set of rules)
        	
        	// get the application healthiness value
        	// for metric data, range=60 and duration=3600 means
        	// get a per-second average metric value from a 60-second range in the past hour (3600 seconds)
        	double appHealthiness = MapeUtils.healthiness(appId, 60, 3600);

//    		if (config.getProperty("ForceMDP").equals("true")) {
//    			ruleViolated = true; // For testing, force trigger MDP
//    		} else {
//    			ruleViolated = false;
//    		}
    		
    		// Stage 2: MDP
    		// if any rule is violated, perform MDP to find an adaptation action
    		if (ruleViolated) {
        		System.out.println("Stage 2: Perform MDP");
    			
    			// Simulate a violated rule
    			String violoatedMetric = config.getProperty("violoatedMetric");
    			String violatedCellId = config.getProperty("violatedCellId");
    			String violatedOrganId = config.getProperty("violatedOrganId");
    			String violatedAppId = config.getProperty("violatedAppId");
    			float healthinessValue = Float.parseFloat(config.getProperty("healthinessValue"));

    			ObjectForMdp o = new ObjectForMdp(violoatedMetric, violatedCellId, violatedOrganId, violatedAppId, healthinessValue);

    			BasicBehaviorMape basicBehaviorMape = new BasicBehaviorMape(o);
    			String outputPath = "output/" + appId + "/"; // directory to record results

    			// solve MDP (Q-Learning)
    			basicBehaviorMape.MyQLearningFunc(outputPath);
    		

	    		// TODO: (Riccardo) implement the call to Sails platform to execute the action from MDP (implement in MapeEnviroment class)
	    		// Stage 3: Send actions to the platform
	    		// GETConnection to sails API.
	    		// pass three pieces of information:
	    		// 1. action (move, delete, create cell)
	    		// 2. application, organ, cell information
	    		// 3. Infrastructure information.
	    		// store the (improved) healthiness value somewhere, after taking the action.
	    		// (improved) healthiness value = value of new node.
    		}
        }
    }
}
