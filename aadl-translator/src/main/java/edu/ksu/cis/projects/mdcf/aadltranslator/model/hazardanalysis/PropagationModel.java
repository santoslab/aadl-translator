package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ManifestationType;

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
	private Set<ErrorTypeModel> errors;
	private ManifestationType manifestation;

	public PropagationModel(Set<ErrorTypeModel> errors, String manifestationStr) {
		this.errors = errors;
		if(manifestationStr != null) {
			this.manifestation = ManifestationType.valueOf(manifestationStr.toUpperCase());
		}
	}

	public Set<ErrorTypeModel> getErrors() {
		return errors;
	}

	public void setErrors(Set<ErrorTypeModel> errors) {
		this.errors = errors;
	}
	
	@Override
	public int compareTo(PropagationModel theirPropModel) {
		if(errors.size() != theirPropModel.getErrors().size()) {
			if(errors.size() < theirPropModel.getErrors().size()){
				return -1;
			} else {
				return 1;
			}
		} else {
			TreeSet<ErrorTypeModel> myErrors = new TreeSet<>();
			TreeSet<ErrorTypeModel> yourErrors = new TreeSet<>();
			myErrors.addAll(errors);
			yourErrors.addAll(theirPropModel.getErrors());
			Iterator<ErrorTypeModel> myIter = myErrors.iterator();
			Iterator<ErrorTypeModel> yourIter = yourErrors.iterator();
			ErrorTypeModel myModel;
			ErrorTypeModel yourModel;
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
	
	public String getManifestation() {
		return manifestation.toString();
	}
	
}
