package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.HashMap;
import java.util.HashSet;

import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyConstant;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.impl.PortConnectionImpl;
import org.osate.aadl2.impl.RecordValueImpl;
import org.osate.xtext.aadl2.errormodel.errorModel.ConnectionErrorSource;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;
import org.osate.xtext.aadl2.errormodel.util.EMV2Util;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.MissingRequiredPropertyException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ConstraintModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.HazardModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ErrorTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.Keyword;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.OccurrenceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public final class ErrorTranslator {
	
	private HashMap<String, ErrorTypeModel> errorTypes;
	private SystemModel systemModel;
	
	public void setErrorTypes(HashSet<ErrorType> errors) {
		errorTypes = new HashMap<>();
		for(ErrorType et : errors){
			errorTypes.put(et.getName(), new ErrorTypeModel(et.getName()));
		}
	}
	
	public void parseOccurrences(SystemImplementation sysImp){
		RecordValueImpl rv;
		String connectionName, connErrorName;
		try {
			for(PropertyAssociation pa : EMV2Util.getOwnEMV2Subclause(sysImp).getProperties()){
				// Setup 
				rv = ((RecordValueImpl)pa.getOwnedValues().get(0).getOwnedValue());
				if(!(pa.getAppliesTos().iterator().next().getContainmentPathElements().iterator().next().getNamedElement() instanceof PortConnectionImpl))
					continue;
				connectionName = ((PortConnectionImpl)pa.getAppliesTos().iterator().next().getContainmentPathElements().iterator().next().getNamedElement()).getName();
				connErrorName = pa.getAppliesTos().iterator().next().getContainmentPathElements().iterator().next().getNamedElement().getName();
				OccurrenceModel om = null;
				ConstraintModel cm = null;
				String title = "$UNSET$", description = "$UNSET$", compensation = "$UNSET$";
				ErrorTypeModel errorType = null;
				Keyword keyword = null;
				RecordValueImpl cause;

				// Kind
				NamedValue nv = ((NamedValue) PropertyUtils.getRecordFieldValue(rv, "Kind"));
				EnumerationLiteral el =  (EnumerationLiteral)nv.getNamedValue();
				String keywordName = el.getName();
				keyword = Keyword.valueOf(keywordName.toUpperCase());
				
				// Constraint
				nv = ((NamedValue) PropertyUtils.getRecordFieldValue(rv, "ViolatedConstraint"));
				PropertyConstant pc = (PropertyConstant)nv.getNamedValue();
				pc = (PropertyConstant)nv.getNamedValue();
				cm = systemModel.getConstraintByName(pc.getName());

				// Title
				title = ((StringLiteral) PropertyUtils.getRecordFieldValue(rv, "Title")).getValue();
				title = title.substring(1, title.length() - 1);
				
				// Cause
				cause = ((RecordValueImpl) PropertyUtils.getRecordFieldValue(rv, "Cause"));
				
				// Description
				description = ((StringLiteral) PropertyUtils.getRecordFieldValue(cause, "Description")).getValue();
				description = description.substring(1, description.length() - 1);
				
				// ErrorType
				ReferenceValue reva = (ReferenceValue) PropertyUtils.getRecordFieldValue(cause, "ErrorType");
				ContainmentPathElement cpe = (ContainmentPathElement) reva.getContainmentPathElements().get(0);
				errorType = errorTypes.get(cpe.getNamedElement().getName());
				
				// Compensation
				compensation = ((StringLiteral) PropertyUtils.getRecordFieldValue(rv, "Compensation")).getValue();
				compensation = compensation.substring(1, compensation.length() - 1);
				
				// Anything missing?
				if(keyword == null){
					throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Kind\"");
				} else if(cm == null){
					throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"ViolatedConstraint\"");
				} else if(title.equals("$UNSET$")){
					throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Title\"");
				} else if(description.equals("$UNSET$")){
					throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Cause\"");
				} else if(compensation.equals("$UNSET$")){
					throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"Compensation\"");
				} else if(errorType == null){
					throw new MissingRequiredPropertyException("Got an occurrence property missing the required subproperty \"ErrorType\"");
				}
				
				// Create model
				om = new OccurrenceModel();
				om.setKeyword(keyword);
				om.setConstraint(cm);
				om.setTitle(title);
				om.setCause(description);
				om.setCompensation(compensation);
				om.setErrorType(errorType);
				om.setConnErrorName(connErrorName);
				systemModel.getChannelByName(connectionName).addOccurrence(om);
				
				//TODO: Put in event chain trace
			}
		} catch (MissingRequiredPropertyException e) {
			e.printStackTrace();
		}
	}

	public void setSystemModel(SystemModel systemModel) {
		this.systemModel = systemModel;
	}
}
