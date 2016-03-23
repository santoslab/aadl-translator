package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

/**
 * This class models EMv2 propagations.
 * 
 * It implements comparable so that unordered sets can be sorted for testing
 * purposes
 * 
 * @author Sam
 *
 */
public class PropagationModel{// implements Comparable<PropagationModel>{	
	private ErrorTypeModel error;
	private boolean incoming;

	public PropagationModel(ErrorTypeModel error, boolean isIncoming) {
		this.error = error;
		incoming = isIncoming;
	}

	public ErrorTypeModel getError() {
		return error;
	}

	public void setError(ErrorTypeModel errors) {
		this.error = errors;
	}
	
	public boolean isIncoming(){
		return incoming;
	}
	
//	@Override
//	public int compareTo(PropagationModel theirPropModel) {
//		if(errors.size() != theirPropModel.getErrors().size()) {
//			if(errors.size() < theirPropModel.getErrors().size()){
//				return -1;
//			} else {
//				return 1;
//			}
//		} else {
//			TreeSet<ErrorTypeModel> myErrors = new TreeSet<>();
//			TreeSet<ErrorTypeModel> yourErrors = new TreeSet<>();
//			myErrors.addAll(errors);
//			yourErrors.addAll(theirPropModel.getErrors());
//			Iterator<ErrorTypeModel> myIter = myErrors.iterator();
//			Iterator<ErrorTypeModel> yourIter = yourErrors.iterator();
//			ErrorTypeModel myModel;
//			ErrorTypeModel yourModel;
//			int comparisonResult;
//			while(myIter.hasNext()){
//				myModel = myIter.next();
//				yourModel = yourIter.next();
//				comparisonResult = myModel.compareTo(yourModel);
//				if(comparisonResult != 0){
//					return myModel.compareTo(yourModel);
//				}
//			}
//		}
//		return 0;
//	}
	
}
