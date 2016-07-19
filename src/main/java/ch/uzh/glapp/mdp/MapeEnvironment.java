package ch.uzh.glapp.mdp;

import burlap.mdp.core.Domain;
import burlap.mdp.core.action.Action;
import burlap.mdp.core.oo.state.OOVariableKey;
import burlap.mdp.core.oo.state.generic.DeepOOState;
import burlap.mdp.singleagent.environment.Environment;
import burlap.mdp.singleagent.environment.EnvironmentOutcome;
import ch.uzh.glapp.MapeUtils;
import ch.uzh.glapp.SailsRetriever;
import ch.uzh.glapp.model.sails.cellinfo.Cell;
import ch.uzh.glapp.model.ObjectForMdp;

import java.util.HashMap;
import java.util.List;

import static ch.uzh.glapp.mdp.MapeWorld.*;


public class MapeEnvironment implements Environment {

	protected ObjectForMdp objectForMpd;
	protected final ThreadLocal<Domain> domain = new ThreadLocal<>();
	protected DeepOOState curState;
	protected DeepOOState nextState;

	public MapeEnvironment(Domain domain, ObjectForMdp objectForMpd) {
		this.domain.set(domain);
		this.objectForMpd = objectForMpd;
	}

	@Override
	public DeepOOState currentObservation() {
		curState = new DeepOOState();
		List<Cell> cells = new SailsRetriever().getCellInfo();
		System.out.println("getCurrentObservation - Size of cells list (all cells in app): "+cells.size());

		MdpUtils mdpUtils = new MdpUtils();
		HashMap numOfCellsList = mdpUtils.countCellsInAllOrgans(cells);

		System.out.println("##### " + objectForMpd.getMetric());

		for (Cell cell : cells) {

			String cellID = cell.getId();

			if(!cell.getIsProxy()) {

				curState.addObject(new MapeCell(cellID));
//				curState.set(new OOVariableKey(cellID, VAR_VIOLATED_POLICY), objectForMpd.getMetric());

				curState.set(new OOVariableKey(cellID, VAR_PROVIDER), cell.getHost().getLabels().getProvider());
				curState.set(new OOVariableKey(cellID, VAR_REGION), cell.getHost().getLabels().getRegion());
				curState.set(new OOVariableKey(cellID, VAR_TIER), cell.getHost().getLabels().getTier());
				curState.set(new OOVariableKey(cellID, VAR_CELLS), numOfCellsList.get(cell.getOrganId().getId()));

//				curState.set(VAR_PROXY_PROVIDER, cell.getHost().getLabels().adsf);
//				curState.set(VAR_PROXY_REGION, cell.getHost().getLabels().adsf);

				Object tProvider =  curState.get(new OOVariableKey(cell.getId(), VAR_PROVIDER));
				Object tRegion = curState.get(new OOVariableKey(cell.getId(), VAR_REGION));
				Object tTier = curState.get(new OOVariableKey(cell.getId(), VAR_TIER));
				Object tCells = curState.get(new OOVariableKey(cell.getId(), VAR_CELLS));
				System.out.println(tRegion + ", " + tTier + ", " + tCells + ", " + tProvider + ", " + cell.getOrganId().getName());
			}
		}
		return curState;
	}

	@Override
	public EnvironmentOutcome executeAction(Action action) {

		String cellId = objectForMpd.getCellId();
		String organId = objectForMpd.getOrganId();
//		String options = "{\"region\":\"us\",\"provider\":\"digitalocean\"}";// TODO: is still hardcoded. Have to get the values from action.
		String options;
		String provider;
		String region;
		String tier;


		SailsRetriever sailsRetriever = new SailsRetriever();

		System.out.println(action.actionName());
		System.out.println(action);

		if ("ch.uzh.glapp.mdp.MapeActionMove".equals(action.getClass().getName())) {
			provider = ((MapeActionMove) action).getProvider();
			region = ((MapeActionMove) action).getRegion();
			tier = ((MapeActionMove) action).getTier();
			options = "{\"provider\":\"" + provider + "\",\"region\":\"" + region + "\",\"tier\":\"" + tier +"\"}";
//			System.out.println(options);
			sailsRetriever.postMove(cellId, options);
		} else if ("ch.uzh.glapp.mdp.MapeActionCreate".equals(action.getClass().getName())) {
			provider = ((MapeActionCreate) action).getProvider();
			region = ((MapeActionCreate) action).getRegion();
			tier = ((MapeActionCreate) action).getTier();
			options = "{\"provider\":\"" + provider + "\",\"region\":\"" + region + "\",\"tier\":\"" + tier +"\"}";
//			System.out.println(options);
			sailsRetriever.postCreate(organId, options);
		} else if ("ch.uzh.glapp.mdp.MapeActionRemove".equals(action.getClass().getName())) {
			sailsRetriever.postRemove(organId, cellId);
		} else {
			System.err.println("action name is wrong!");
			System.exit(199);
		}

		//TODO: calculate the reward (difference in healthiness values)
		MapeUtils mapeUtils = new MapeUtils();
		double appHealthinessAfter = mapeUtils.healthiness(objectForMpd.getAppId(), 60, 3600);
		double deltaHealthiness = objectForMpd.getHealthyValue() - appHealthinessAfter;

		nextState = currentObservation();
		EnvironmentOutcome envOutCome = new EnvironmentOutcome(curState, action, nextState, deltaHealthiness, true);
		return envOutCome;
	}

	@Override
	public double lastReward() {
		return 0;
	}

	@Override
	public boolean isInTerminalState() {
		return false;
	}

	@Override
	public void resetEnvironment() {

	}

}
