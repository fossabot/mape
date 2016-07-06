package ch.uzh.glapp;

import ch.uzh.glapp.model.sails.appinfo.AppDataObject;
import ch.uzh.glapp.model.sails.cellinfo.Cell;
import ch.uzh.glapp.model.sails.cellinfo.CellDataObject;
import ch.uzh.glapp.model.sails.hostinfo.Host;
import ch.uzh.glapp.model.sails.hostinfo.HostDataObject;
import ch.uzh.glapp.model.sails.ruleinfo.RuleDataObject;
import ch.uzh.glapp.model.sails.ruleinfo.Rule;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SailsRetriever {

	private String urlSails = "http://localhost:1337";


	private String callSails (String paramSails) {
		HttpRequest con = new HttpRequest();
		String str = "";
		try {
			str = con.connect(urlSails, paramSails);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return str;
	}


	public void postMove (String cellId, String options) {
		String paramSails = "/cell/move?cell_id=" + cellId + "&options=" + options;
			System.out.println(urlSails + "" + paramSails);
		String str = callSails(paramSails);

		System.out.println(str);
	}

	public void postCreate (String organId, String options) {
		String paramSails = "/organ/scaleUp?organ_id=" + organId + "&options=" + options;
			System.out.println(urlSails + "" + paramSails);
		String str = callSails(paramSails);

		System.out.println(str);
	}

	public void postRemove (String organId, String cellId) {
		String paramSails = "/organ/scaleDown?organ_id=" + organId + "&cell_id=" + cellId;
			System.out.println(urlSails + "" + paramSails);
		String str = callSails(paramSails);

		System.out.println(str);
	}


	public List<Host> getHostInfo () {

		String paramSails = "/host/infoMape";
		List<Host> hostList = new ArrayList<>();
		String str = callSails(paramSails);

		String jsonString;
		HostDataObject jobj;

		jsonString = str;
		//System.out.println(jsonString);
		jobj = new Gson().fromJson(jsonString, HostDataObject.class);

		int sizeHosts = jobj.getHosts().size();
		for (int i = 0; i<sizeHosts; i++) {
			hostList.add(jobj.getHosts().get(i));
//			System.out.println("Get Host: "+ hostList.get(i));
		}

		return hostList;
	}


	public Map<String, String> getAppIds () {

		String paramSails = "/application/getAppInfo";
		Map appIdMap = new HashMap<>();
		String str = callSails(paramSails);

		String jsonString;
		AppDataObject jobj;

		jsonString = str;
//		System.out.println(jsonString);
		jobj = new Gson().fromJson(jsonString, AppDataObject.class);

		int sizeApplications = jobj.getApps().size();
//		System.out.println();
		for (int i = 0; i<sizeApplications; i++) {
			appIdMap.put(jobj.getApps().get(i).getId(), jobj.getApps().get(i).getStatus());
//			System.out.println("Get App: "+ appIdMap.get(i));
		}

		return appIdMap;
	}


    public List<Rule> getRules (String appId) {

		String paramSails = "/policy?app_id=" + appId;
		List<Rule> myList = new ArrayList<>();
	    String str = callSails(paramSails);

        String jsonString;
        RuleDataObject jobj;

        jsonString = str;
//        System.out.println(jsonString);
        jobj = new Gson().fromJson(jsonString, RuleDataObject.class);

		int sizeRules = jobj.getRules().size();
		for (int i = 0; i<sizeRules; i++) {
			myList.add(jobj.getRules().get(i));
//			System.out.println("Get Rule: "+ myList.get(i));
		}

		return myList;

    }


	public List<Cell> getCellInfo () {

		String paramSails = "/application/getCellInfo";
		List<Cell> cellList = new ArrayList<>();
		String str = callSails(paramSails);

		String jsonString;
		CellDataObject jobj;

		jsonString = str;
//		System.out.println(jsonString);
		jobj = new Gson().fromJson(jsonString, CellDataObject.class);

		int sizeCells = jobj.getCells().size();
//		System.out.println(sizeCells);
		for (int i = 0; i<sizeCells; i++) {
			cellList = jobj.getCells();

//			MapeCellObject cellObject = new MapeCellObject(provider, region, tier, cells);
		}

		return cellList;
	}

}