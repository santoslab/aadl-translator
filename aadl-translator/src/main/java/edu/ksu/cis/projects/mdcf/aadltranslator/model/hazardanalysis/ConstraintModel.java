package edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis;

import java.util.List;
import java.util.stream.Collectors;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public class ConstraintModel extends StpaPreliminaryModel {
	private ManifestationTypeModel errorType = null;
	private boolean resolved = false;
	private String typeName;

	public void setErrorTypeName(String errorTypeName) {
		this.typeName = errorTypeName;
	}

	/**
	 * @return The manifestation associated with this constraint's violation.
	 */
	public ManifestationTypeModel getErrorType() {
		if (!resolved) {
			resolved = true;
			resolveErrorType();
		}
		return errorType;
	}

	/**
	 * We have to resolve error types after creation of the constraint models
	 * because constraints are declared at the system level, which is parsed
	 * before the individual elements. This method takes time linear to the
	 * number of errors propagated out of all successor dangers
	 */
	private void resolveErrorType() {
		HazardModel parent = (HazardModel) this.getParent();
		String elemName = parent.getSystemElement();
		SystemModel system = ((AccidentLevelModel) parent.getParent().getParent()).getSystem();
		List<ManifestationTypeModel> errorTypeSet = system.getChild(elemName).getExternallyCausedDangers().values()
				.stream().map(v -> v.getSuccessorDanger().getErrors()).flatMap(w -> w.stream())
				.filter(x -> x.getName().equals(typeName)).collect(Collectors.toList());
		// TODO: Can this list have more than one element?
		this.errorType = errorTypeSet.get(0);
	}
}
