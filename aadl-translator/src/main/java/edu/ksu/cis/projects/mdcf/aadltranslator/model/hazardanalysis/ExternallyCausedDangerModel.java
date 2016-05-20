package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.Set;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.CoreException;

/**
 * An externally caused danger represents the path of a set of errors through a
 * component; it's essentially an incoming danger (manifestation), outgoing
 * danger (succDanger), and some metadata.
 * 
 * @author sam
 *
 */
public class ExternallyCausedDangerModel extends CausedDangerModel {

	PropagationModel danger;
	public ExternallyCausedDangerModel(PropagationModel inProp, PropagationModel outProp, String interp,
			Set<ManifestationTypeModel> cooccurringDangers) throws CoreException {
		super(inProp, interp, cooccurringDangers);
		if (!inProp.getName().equals(outProp.getName())) {
			throw new CoreException(
					"Tried to create an external danger out of mismatched successor danger and manifestation!");
		}
		super.setName(inProp.getName());
		this.danger = outProp;
	}

	public PropagationModel getDanger() {
		return danger;
	}

}