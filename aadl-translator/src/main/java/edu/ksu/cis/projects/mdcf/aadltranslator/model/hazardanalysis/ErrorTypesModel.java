package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 * This class models an EMv2 error type which can be a set of single types.
 * 
 * It implements comparable so that unordered sets can be sorted for testing
 * purposes
 * 
 * @author Sam
 *
 */
public class ErrorTypesModel implements Comparable<ErrorTypesModel>{
	private Set<ErrorTypeModel> types;

	public ErrorTypesModel() {
		types = new HashSet<>();
	}
	
	public ErrorTypesModel(Set<ErrorTypeModel> types) {
		this.types = types;
	}

	public void addType(ErrorTypeModel type) {
		types.add(type);
	}
	
	public Set<ErrorTypeModel> getTypes() {
		return types;
	}

	@Override
	public int compareTo(ErrorTypesModel o) {
		if(types.size() != o.getTypes().size()){
			if(types.size() < o.getTypes().size()){
				return -1;
			} else {
				return 1;
			}
		} else {
			TreeSet<ErrorTypeModel> myErrors = new TreeSet<>();
			TreeSet<ErrorTypeModel> yourErrors = new TreeSet<>();
			myErrors.addAll(types);
			yourErrors.addAll(o.getTypes());
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
}
