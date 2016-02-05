package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.PropertyConstant;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.impl.PortConnectionImpl;
import org.osate.aadl2.impl.RecordValueImpl;
import org.osate.aadl2.impl.SystemImplementationImpl;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorModelSubclause;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorPropagation;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;
import org.osate.xtext.aadl2.errormodel.errorModel.TypeToken;
import org.osate.xtext.aadl2.errormodel.util.EMV2Util;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.MissingRequiredPropertyException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.Keyword;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ConstraintModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ErrorTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.OccurrenceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;

public final class ErrorTranslator {

	private HashMap<String, ErrorTypeModel> errorTypes;
	private SystemModel systemModel;

	public void setErrorType(HashSet<ErrorType> errors) {
		errorTypes = new HashMap<>();
		
		errors.forEach((et) -> {
			errorTypes.put(et.getName(), new ErrorTypeModel(et.getName()));
		});
	}

	public void parseEMV2(ComponentModel<?, ?> model, ComponentClassifier cl) {

		// If our classifier doesn't have an EMV2 block, then it doesn't have
		// occurrences so we can just stop now
		if (!EMV2Util.hasEMV2Subclause(cl))
			return;
		
		ErrorModelSubclause emv2 = EMV2Util.getOwnEMV2Subclause(cl);
		
		// No EMv2 block means we can quit now
		if(emv2 == null){
			return;
		}
		
		try {
			parseOccurrences(emv2);
			parsePropagations(model, emv2);
		} catch (MissingRequiredPropertyException e) {
			e.printStackTrace();
		}
	}

	private void parsePropagations(ComponentModel<?, ?> model, ErrorModelSubclause emv2) {
		PropagationModel propModel;
		PortModel portModel;
		Set<ErrorTypeModel> errors;
		boolean isIn = true;
		String portName;
		for(ErrorPropagation eProp : emv2.getPropagations()){
			if(eProp.getDirection() == DirectionType.IN){
				isIn = true;
			} else if (eProp.getDirection() == DirectionType.OUT){
				isIn = false;
			} else {
				// Direction == INOUT
				System.err.println("Inout propagation discovered!");
			}
			
			errors = tokenSetToTypes(eProp.getTypeSet().getTypeTokens());
			
			portName = eProp.getFeatureorPPRef().getFeatureorPP().getName();
			portModel = resolvePortModel(model, portName, isIn);
						
			propModel = new PropagationModel(isIn, errors, portModel);
			
			try {
				model.addPropagation(propModel);
			} catch (DuplicateElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void parseOccurrences(ErrorModelSubclause emv2)
			throws MissingRequiredPropertyException {
		RecordValueImpl rv;
		String connectionName, connErrorName;
		for (PropertyAssociation pa : emv2.getProperties()) {
			// Setup
			if(!(pa.getOwnedValues().get(0).getOwnedValue() instanceof RecordValueImpl)){
				continue;
			}
			rv = ((RecordValueImpl) pa.getOwnedValues().get(0)
					.getOwnedValue());
			if (!(pa.getAppliesTos().iterator().next()
					.getContainmentPathElements().iterator().next()
					.getNamedElement() instanceof PortConnectionImpl))
				continue;
			connectionName = ((PortConnectionImpl) pa.getAppliesTos()
					.iterator().next().getContainmentPathElements()
					.iterator().next().getNamedElement()).getName();
			connErrorName = pa.getAppliesTos().iterator().next()
					.getContainmentPathElements().iterator().next()
					.getNamedElement().getName();
			OccurrenceModel om = null;
			ConstraintModel cm = null;
			String title = "$UNSET$", description = "$UNSET$", compensation = "$UNSET$";
			ErrorTypeModel errorType = null;
			Keyword keyword = null;
			RecordValueImpl cause;

			// Kind
			NamedValue nv = ((NamedValue) PropertyUtils
					.getRecordFieldValue(rv, "Kind"));
			EnumerationLiteral el = (EnumerationLiteral) nv.getNamedValue();
			String keywordName = el.getName();
			keyword = Keyword.valueOf(keywordName.toUpperCase());

			// Constraint
			nv = ((NamedValue) PropertyUtils.getRecordFieldValue(rv,
					"ViolatedConstraint"));
			if (nv != null) {
				// Constraints are optional, typically they aren't used for
				// non-hazardous / non-applicable occurrences
				PropertyConstant pc = (PropertyConstant) nv.getNamedValue();
				pc = (PropertyConstant) nv.getNamedValue();
				cm = systemModel.getConstraintByName(pc.getName());
			}
			// Title
			title = ((StringLiteral) PropertyUtils.getRecordFieldValue(rv,
					"Title")).getValue();
			
			// Cause
			cause = ((RecordValueImpl) PropertyUtils.getRecordFieldValue(
					rv, "Cause"));

			// Description
			description = ((StringLiteral) PropertyUtils
					.getRecordFieldValue(cause, "Description")).getValue();

			// ErrorType
			ReferenceValue reva = (ReferenceValue) PropertyUtils
					.getRecordFieldValue(cause, "ErrorType");
			if (reva != null) {
				// ErrorTypes are optional, typically they aren't used for
				// non-hazardous / non-applicable occurrences
				ContainmentPathElement cpe = (ContainmentPathElement) reva
						.getContainmentPathElements().get(0);
				errorType = errorTypes.get(cpe.getNamedElement().getName());
			}

			// Compensation
			compensation = ((StringLiteral) PropertyUtils
					.getRecordFieldValue(rv, "Compensation")).getValue();

			// Anything missing?
			if (keyword == null) {
				throw new MissingRequiredPropertyException(
						"Got an occurrence property missing the required subproperty \"Kind\"");
			} else if (title.equals("$UNSET$")) {
				throw new MissingRequiredPropertyException(
						"Got an occurrence property missing the required subproperty \"Title\"");
			} else if (description.equals("$UNSET$")) {
				throw new MissingRequiredPropertyException(
						"Got an occurrence property missing the required subproperty \"Cause\"");
			} else if (compensation.equals("$UNSET$")) {
				throw new MissingRequiredPropertyException(
						"Got an occurrence property missing the required subproperty \"Compensation\"");
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
			if (emv2.getContainingClassifier() instanceof SystemImplementationImpl) {
				systemModel.getChannelByName(connectionName).addOccurrence(om);
			}

			// TODO: Put in event chain trace
		}
	}
	
	private PortModel resolvePortModel(ComponentModel<?, ?> model, String portName, boolean isIn){
		PortModel pm = model.getPortByName(portName);
		
		// Ideally we'll just have the portname as planned
		if (pm != null){
			return pm;
		} else if (model instanceof DeviceModel) {
			// Since devices get turned into pseudodevices, with both in and out
			// ports, we only want the propagation to map to the port that
			// interacts with our system
			if(isIn){
				return model.getPortByName(portName + "In");
			} else {
				return model.getPortByName(portName + "Out");
			}
		}
			
		return null;
	}
	
	private Set<ErrorTypeModel> tokenSetToTypes(List<TypeToken> typeTokens) {
		HashSet<ErrorTypeModel> ret = new HashSet<>();
		for(TypeToken tok : typeTokens){
			// Guaranteed to only have one since we don't consider type sets
			ret.add(new ErrorTypeModel(tok.getType().iterator().next().getName()));
		}
		return ret;
	}

	public void setSystemModel(SystemModel systemModel) {
		this.systemModel = systemModel;
	}
}
