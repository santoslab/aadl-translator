package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;

/**
 * This class models EMv2 propagations.
 * 
 * It implements comparable so that unordered sets can be sorted for testing
 * purposes
 * 
 * @author Sam
 *
 */
public class PropagationModel implements Comparable<PropagationModel>{
	private boolean in;
	private Set<ErrorTypesModel> errors;
	private PortModel port;
	
	public PropagationModel(boolean in, Set<ErrorTypesModel> errors, PortModel port) {
		this.in = in;
		this.errors = errors;
		this.port = port;
	}

	public boolean isIn() {
		return in;
	}

	public void setIn() {
		this.in = true;
	}

	public boolean isOut() {
		return !in;
	}

	public void setOut() {
		this.in = false;
	}

	public Set<ErrorTypesModel> getErrors() {
		return errors;
	}

	public void setErrors(Set<ErrorTypesModel> errors) {
		this.errors = errors;
	}

	public PortModel getPort() {
		return port;
	}

	public void setPort(PortModel port) {
		this.port = port;
	}

	@Override
	public int compareTo(PropagationModel theirPropModel) {
		if(in != theirPropModel.isIn()){
			if(in){
				return -1;
			} else {
				return 1;
			}
		} else if(!(port.getName().equals(theirPropModel.getPort().getName()))) {
			return port.getName().compareTo(theirPropModel.getPort().getName());
		} else if(errors.size() != theirPropModel.getErrors().size()) {
			if(errors.size() < theirPropModel.getErrors().size()){
				return -1;
			} else {
				return 1;
			}
		} else {
			TreeSet<ErrorTypesModel> myErrors = new TreeSet<>();
			TreeSet<ErrorTypesModel> yourErrors = new TreeSet<>();
			myErrors.addAll(errors);
			yourErrors.addAll(theirPropModel.getErrors());
			Iterator<ErrorTypesModel> myIter = myErrors.iterator();
			Iterator<ErrorTypesModel> yourIter = yourErrors.iterator();
			ErrorTypesModel myModel;
			ErrorTypesModel yourModel;
			int comparisonResult;
			while(myIter.hasNext()){
				myModel = myIter.next();
				yourModel = yourIter.next();
				comparisonResult = myModel.compareTo(yourModel);
				if(comparisonResult != 0){
					return myModel.compareTo(yourModel);
				}
			}
		}
		return 0;
	}
	
}
