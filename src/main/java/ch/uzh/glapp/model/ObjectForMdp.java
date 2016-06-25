package ch.uzh.glapp.model;


public class ObjectForMdp {

	private String policy;
	private String cellId;
	private String organId;
	private String appId;
	private String metric;
	private float healthyValue;

	public ObjectForMdp(String policy, String cellId, String organId, String appId, String metric, float healthyValue) {
		this.policy = policy;
		this.cellId = cellId;
		this.organId = organId;
		this.appId = appId;
		this.metric = metric;
		this.healthyValue = healthyValue;
	}

	public String getPolicy() {
		return policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
	}

	public String getCellId() {
		return cellId;
	}

	public void setCellId(String cellId) {
		this.cellId = cellId;
	}

	public String getOrganId() {
		return organId;
	}

	public void setOrganId(String organId) {
		this.organId = organId;
	}

	public String getAppId() {
		return appId;
	}

	public void setAppId(String appId) {
		this.appId = appId;
	}

	public String getMetric() {
		return metric;
	}

	public void setMetric(String metric) {
		this.metric = metric;
	}

	public float getHealthyValue() {
		return healthyValue;
	}

	public void setHealthyValue(float healthyValue) {
		this.healthyValue = healthyValue;
	}
}
