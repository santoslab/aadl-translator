package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.HashMap;
import java.util.HashSet;

import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.DeviceType;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyConstant;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.impl.RecordValueImpl;
import org.osate.xtext.aadl2.errormodel.errorModel.ConnectionErrorSource;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;
import org.osate.xtext.aadl2.errormodel.util.EMV2Util;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.MissingRequiredPropertyException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ConstraintModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.HazardModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ImpactModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.Keyword;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.OccurrenceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public final class ErrorTranslator {
	
	private HashMap<String, ImpactModel> impacts;
	private SystemModel systemModel;
	
	public void setErrorTypes(HashSet<ErrorType> errors) {
		impacts = new HashMap<>();
		for(ErrorType et : errors){
			impacts.put(et.getName(), new ImpactModel(et.getName()));
		}
	}
	
	public void parseOccurrences(SystemImplementation sysImp){
		HashSet<ConnectionErrorSource> connErrors = new HashSet<>(EMV2Util.getAllConnectionErrorSources(sysImp));
		RecordValueImpl rv;
		String connectionName, connErrorName;
		for(ConnectionErrorSource connError : connErrors){
			try {
				for(PropertyAssociation pa : EMV2Util.getOwnEMV2Subclause(connError.getContainingClassifier()).getProperties()){
					// Setup 
					rv = ((RecordValueImpl)pa.getOwnedValues().get(0).getOwnedValue());
					connectionName = connError.getConnection().getName();
					connErrorName = connError.getName();
					OccurrenceModel om;
					HazardModel hm = null;
					ConstraintModel cm = null;
					String desc = "$UNSET$", cause = "$UNSET$", compensation = "$UNSET$";
					ImpactModel im = null;
					Keyword keyword = null;

					// Kind
					NamedValue nv = ((NamedValue) PropertyUtils.getRecordFieldValue(rv, "Kind"));
					EnumerationLiteral el =  (EnumerationLiteral)nv.getNamedValue();
					String keywordName = el.getName();
					keyword = Keyword.valueOf(keywordName.toUpperCase());
					
					// Hazard
					nv = ((NamedValue) PropertyUtils.getRecordFieldValue(rv, "Hazard"));
					PropertyConstant pc = (PropertyConstant)nv.getNamedValue();
					hm = systemModel.getHazardByName(pc.getName());
					
					// Constraint
					nv = ((NamedValue) PropertyUtils.getRecordFieldValue(rv, "ViolatedConstraint"));
					pc = (PropertyConstant)nv.getNamedValue();
					cm = systemModel.getConstraintByName(pc.getName());

					// Description
					desc = ((StringLiteral) PropertyUtils.getRecordFieldValue(rv, "Description")).getValue();
					desc = desc.substring(1, desc.length() - 1);
					
					// Cause
					cause = ((StringLiteral) PropertyUtils.getRecordFieldValue(rv, "Cause")).getValue();
					cause = cause.substring(1, cause.length() - 1);
					
					// Compensation
					compensation = ((StringLiteral) PropertyUtils.getRecordFieldValue(rv, "Compensation")).getValue();
					compensation = compensation.substring(1, compensation.length() - 1);
					
					// Impact
					ReferenceValue reva = (ReferenceValue) PropertyUtils.getRecordFieldValue(rv, "Impact");
					ContainmentPathElement cpe = (ContainmentPathElement) reva.getContainmentPathElements().get(0);
					im = impacts.get(cpe.getNamedElement().getName());
					
					// Anything missing?
					if(keyword == null){
						throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Kind\"");
					} else if(hm == null){
						throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Hazard\"");
					} else if(cm == null){
						throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"ViolatedConstraint\"");
					} else if(desc.equals("$UNSET$")){
						throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Description\"");
					} else if(cause.equals("$UNSET$")){
						throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Cause\"");
					} else if(compensation.equals("$UNSET$")){
						throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Compensation\"");
					} else if(im == null){
						throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Impact\"");
					}
					
					// Create model
					om = new OccurrenceModel();
					systemModel.getChannelByName(connectionName).addOccurrence(om);
					om.setKeyword(keyword);
					om.setHazard(hm);
					om.setConstraint(cm);
					om.setDescription(desc);
					om.setCause(cause);
					om.setCompensation(compensation);
					om.setImpact(im);
					om.setConnErrorName(connErrorName);
				}
			} catch (MissingRequiredPropertyException e) {
				e.printStackTrace();
			}
		}
	}

	public void setSystemModel(SystemModel systemModel) {
		this.systemModel = systemModel;
	}
}
