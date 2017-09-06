package org.insightech.er.editor.model.settings;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

//import org.insightech.er.Activator;

public class EnvironmentSetting implements Serializable, Cloneable {

	private static final long serialVersionUID = 4937234635221817893L;

	private List<Environment> environments;

	private Environment currentEnvironment;

	public EnvironmentSetting() {
		this.environments = new ArrayList<Environment>();
		// this.environments.add(new Environment(Activator
		// .getResourceString("label.default")));
	}

	public List<Environment> getEnvironments() {
		return environments;
	}

	public void setEnvironments(List<Environment> environments) {
		this.environments = environments;
	}

	public Environment getCurrentEnvironment() {
		return currentEnvironment;
	}

	public void setCurrentEnvironment(Environment currentEnvironment) {
		this.currentEnvironment = currentEnvironment;
	}

	public Object clone(Map<Environment, Environment> environmentCloneMap) {
		try {
			EnvironmentSetting setting = (EnvironmentSetting) super.clone();

			setting.environments = new ArrayList<Environment>();

			for (Environment environment : this.environments) {
				setting.environments.add(environmentCloneMap.get(environment));
			}

			setting.currentEnvironment = environmentCloneMap
					.get(this.currentEnvironment);

			return setting;

		} catch (CloneNotSupportedException e) {
			return null;
		}
	}

}
