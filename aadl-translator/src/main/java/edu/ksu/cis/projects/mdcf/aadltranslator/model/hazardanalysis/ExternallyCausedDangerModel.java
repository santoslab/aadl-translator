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

	public ExternallyCausedDangerModel(PropagationModel succDanger, PropagationModel manifestation, String interp,
			Set<ManifestationTypeModel> cooccurringDangers) throws CoreException {
		super(succDanger, interp, cooccurringDangers);
		if (!succDanger.getName().equals(manifestation.getName())) {
			throw new CoreException(
					"Tried to create an external danger out of mismatched successor danger and manifestation!");
		}
		this.name = succDanger.getName();
		this.danger = manifestation;
	}

	public PropagationModel getDanger() {
		return danger;
	}

}
