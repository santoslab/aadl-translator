package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AbstractFeature;
import org.osate.aadl2.AbstractNamedValue;
import org.osate.aadl2.AbstractSubcomponent;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.ContainmentPathElement;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.DataSubcomponentType;
import org.osate.aadl2.DefaultAnnexSubclause;
import org.osate.aadl2.DeviceSubcomponent;
import org.osate.aadl2.DeviceType;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.Element;
import org.osate.aadl2.EnumerationLiteral;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.IntegerLiteral;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.NamedValue;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.RangeValue;
import org.osate.aadl2.RecordValue;
import org.osate.aadl2.ReferenceValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.modelsupport.errorreporting.MarkerParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.aadl2.properties.PropertyNotPresentException;
import org.osate.aadl2.util.Aadl2Switch;
import org.osate.contribution.sei.names.DataModel;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionElement;
import org.osate.xtext.aadl2.errormodel.errorModel.ConditionExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.EMV2PropertyAssociation;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorEvent;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorState;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorStateMachine;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorBehaviorTransition;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorEvent;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorFlow;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorPath;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorPropagation;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorSink;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorSource;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;
import org.osate.xtext.aadl2.errormodel.errorModel.OrExpression;
import org.osate.xtext.aadl2.errormodel.errorModel.TypeSet;
import org.osate.xtext.aadl2.errormodel.errorModel.TypeToken;
import org.osate.xtext.aadl2.errormodel.util.EMV2Properties;
import org.osate.xtext.aadl2.errormodel.util.EMV2Util;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import com.google.common.collect.Lists;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.CoreException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.MissingRequiredPropertyException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.PropertyOutOfRangeException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.UseBeforeDeclarationException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.DevOrProcModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.FeatureModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ManifestationType;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessConnectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemConnectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.TaskModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.AccidentLevelModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.AccidentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.CausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ConstraintModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.DesignTimeDetectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ErrorBehaviorModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ExternallyCausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.HazardModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.InternallyCausedDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ManifestationTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.NotDangerousDangerModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ProcessVariableModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.RuntimeDetectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.RuntimeHandlingModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.StpaPreliminaryModel;

public final class Translator extends AadlProcessingSwitchWithProgress {
	private enum DangerSource {
		BOTH, INTERNAL, EXTERNAL
	}

	private enum ElementType {
		SYSTEM, PROCESS, THREAD, SUBPROGRAM, DEVICE, NONE
	};

	private enum TranslationTarget {
		SYSTEM, PROCESS, DEVICE
	};

	private enum PropertyType {
		ENUM, INT, RANGE_MIN, RANGE_MAX, STRING, RECORD, LIST, BOOLEAN,
	};

	private static final String FAULT_CLASSES_NAME = "StandardFaultClasses";

	private TranslationTarget target = null;
	private SystemModel systemModel = null;
	private ArrayList<String> propertySetNames = new ArrayList<>();
	private ParseErrorReporterManager errorManager;
	public SystemImplementation sysImpl;
	private Map<ComponentModel<?, ?>, ComponentClassifier> children = new LinkedHashMap<>();

	/**
	 * Error type name -> model
	 */
	private Map<String, ManifestationTypeModel> errorTypeModels = new LinkedHashMap<>();

	/**
	 * Error type set name -> collection of type models in the set
	 */
	private Map<String, HashMap<String, ManifestationTypeModel>> errorTypeSets = new HashMap<>();

	/**
	 * Machine Name -> Machine model
	 */
	private Map<String, ErrorBehaviorModel> errBehaviorMachines = new HashMap<>();

	public class TranslatorSwitch extends Aadl2Switch<String> {

		private static final String FUNDAMENTALS_PROP_NAME = "MAP_Error_Properties::Fundamentals";

		/**
		 * A reference to the "current" component model, stored for convenience
		 */
		private ComponentModel<?, ?> componentModel = null;

		/**
		 * A reference to the current component's parent
		 */
		private ComponentModel<?, ?> parentModel = null;

		/**
		 * A reference to the type of the last element processed
		 */
		private ElementType lastElemProcessed = ElementType.NONE;

		@Override
		public String caseSystem(org.osate.aadl2.System obj) {
			try {
				if (systemModel != null)
					throw new NotImplementedException("Got a system called " + obj.getName()
							+ " but I already have one called " + systemModel.getName());
			} catch (NotImplementedException e) {
				handleException(obj, e);
				return DONE;
			}
			SimpleDateFormat sdf = new SimpleDateFormat("MMMM d, yyyy 'at' h:mm a");
			systemModel = new SystemModel();
			systemModel.setTimestamp(sdf.format(new Date()));
			systemModel.setErrorTypes(errorTypeModels);
			lastElemProcessed = ElementType.SYSTEM;
			return NOT_DONE;
		}

		@Override
		public String caseSystemImplementation(SystemImplementation obj) {
			sysImpl = obj;
			componentModel = systemModel;
			try {
				getComponentType(obj);
			} catch (MissingRequiredPropertyException e) {
				handleException(obj, e);
				return DONE;
			}
			return NOT_DONE;
		}

		@Override
		public String caseProcessImplementation(ProcessImplementation obj) {
			// This is a hack to get the ordering of children correct
			BasicEList<Element> elems = new BasicEList<>(obj.getOwnedElements());
			if (obj.getOwnedElements().get(0) instanceof DefaultAnnexSubclause) {
				elems.remove(0);
				elems.add(obj.getOwnedElements().get(0));
			}
			processEList(elems);
			return DONE;
			// return NOT_DONE;
		}

		@Override
		public String caseThreadSubcomponent(ThreadSubcomponent obj) {
			try {
				if (componentModel instanceof ProcessModel)
					((ProcessModel) componentModel).addChild(obj.getName(), new TaskModel(obj.getName()));
				else
					throw new CoreException(
							"Trying to add thread to non-process component " + componentModel.getName());
			} catch (DuplicateElementException | CoreException e) {
				handleException(obj, e);
				return DONE;
			}
			return NOT_DONE;
		}

		@Override
		public String caseThreadType(ThreadType obj) {
			lastElemProcessed = ElementType.THREAD;
			if (componentModel instanceof ProcessModel)
				parentModel = componentModel;
			componentModel = parentModel.getChild(obj.getName());
			children.put(componentModel, obj);
			handleThreadProperties(obj, (TaskModel) componentModel);
			processEList(obj.getOwnedElements());
			return NOT_DONE;
		}

		/*-
		@Override
		public String casePropertySet(PropertySet obj) {
			propertySetNames.add(obj.getName());
			return NOT_DONE;
		}
		 */

		/*-
		@Override
		public String casePackageSection(PackageSection obj) {
			processEList(obj.getOwnedClassifiers());
			return DONE;
		}
		 */

		@Override
		public String caseDeviceSubcomponent(DeviceSubcomponent obj) {
			if (handleNewDevice(obj) != null)
				return DONE;
			return NOT_DONE;
		}

		@Override
		public String caseProcessSubcomponent(ProcessSubcomponent obj) {
			if (handleNewProcess(obj) != null)
				return DONE;
			return NOT_DONE;
		}

		@Override
		public String caseAbstractFeature(AbstractFeature obj) {
			boolean in = obj.getDirection().incoming();
			String name = obj.getName();
			FeatureModel fm = new FeatureModel();
			fm.setName(name);
			fm.setSubscribe(in);
			try {
				componentModel.addFeature(fm);
			} catch (DuplicateElementException e) {
				handleException(obj, e);
				return DONE;
			}
			return NOT_DONE;
		}

		@Override
		public String caseDataSubcomponent(DataSubcomponent obj) {
			String isProcessModel = checkCustomProperty(obj, "ProcessVariable", PropertyType.BOOLEAN);
			if (isProcessModel != null && isProcessModel.equals("true")) {
				handleProcessModel(obj);
			}
			return NOT_DONE;
		}

		private void handleProcessModel(DataSubcomponent obj) {
			DataSubcomponentType dst = obj.getDataSubcomponentType();
			String name = obj.getName();
			String type = GetProperties.getDataRepresentation(dst).getName();
			String units = GetProperties.getMeasurementUnit(dst);
			ProcessVariableModel pvm = new ProcessVariableModel(type, units, name);
			if (type.equalsIgnoreCase("Float")) {
				RangeValue rv = GetProperties.getDataRealRange(dst);
				String minVal = String.valueOf(rv.getMinimumValue().getScaledValue());
				String maxVal = String.valueOf(rv.getMaximumValue().getScaledValue());
				pvm = new ProcessVariableModel(type, units, name, minVal, maxVal);
			} else if (type.equalsIgnoreCase("Integer")) {
				RangeValue rv = GetProperties.getDataIntegerRange(dst);
				String minVal = String.valueOf(rv.getMinimumValue().getScaledValue());
				String maxVal = String.valueOf(rv.getMaximumValue().getScaledValue());
				pvm = new ProcessVariableModel(type, units, name, minVal, maxVal);
			}
			componentModel.addProcessVariable(obj.getName(), pvm);
		}

		@Override
		public String caseDeviceType(DeviceType obj) {
			if (target == TranslationTarget.SYSTEM) {
				try {
					if (systemModel.hasDeviceType(obj.getName())) {
						componentModel = systemModel.getDeviceByType(obj.getName());
						getComponentType(obj);
					} else {
						throw new UseBeforeDeclarationException(
								"Attempted to define a device that wasn't declared as a system component");
					}
					children.put(componentModel, obj);
				} catch (UseBeforeDeclarationException | MissingRequiredPropertyException e) {
					handleException(obj, e);
					return DONE;
				}
			} else if (target == TranslationTarget.DEVICE) {
				// Translating just a device
				systemModel = new SystemModel();
				systemModel.setName("Device_Stub_System");
				if (handleNewDevice(obj) != null) {
					return DONE;
				}
				children.put(systemModel.getDeviceByType(obj.getName()), obj);
			} else {
				// TODO: This shouldn't be hit. Throw an error?
			}
			lastElemProcessed = ElementType.DEVICE;
			// For some reason getOwnedElements returns items in the reverse of
			// their declaration order, so we have to reverse the list to avoid
			// processing EMv2 first
			processEList(new BasicEList<Element>(Lists.reverse(obj.getOwnedElements())));
			return NOT_DONE;
		}

		private void getComponentType(ComponentClassifier obj) throws MissingRequiredPropertyException {
			String componentType = checkCustomProperty(obj, "Component_Type", PropertyType.ENUM);
			if (componentType == null)
				throw new MissingRequiredPropertyException(
						"Components must specify their component type (eg Actuator, Sensor, Controller, or Controlled Process)");
			componentModel.setComponentType(componentType);
		}

		// @Override
		// public String casePropertyConstant(PropertyConstant obj) {
		// try {
		// if (obj.getPropertyType() == null || obj.getPropertyType().getName()
		// == null) {
		// return NOT_DONE;
		// } else if (obj.getPropertyType().getName().equals("Accident_Level"))
		// {
		// RecordValue rv = (RecordValue) obj.getConstantValue();
		// StringLiteral sl = (StringLiteral)
		// PropertyUtils.getRecordFieldValue(rv, "Description");
		// IntegerLiteral il = ((IntegerLiteral)
		// PropertyUtils.getRecordFieldValue(rv, "Level"));
		// if (il.getValue() > Integer.MAX_VALUE) {
		// throw new PropertyOutOfRangeException("Accident levels must be less
		// than 2,147,483,647");
		// }
		// AccidentLevelModel alm = new AccidentLevelModel();
		// alm.setNumber((int) il.getValue());
		// handleExplanations(rv, alm);
		// alm.setName(obj.getName());
		// alm.setDescription(sl.getValue());
		// systemModel.addAccidentLevel(alm);
		// } else if (obj.getPropertyType().getName().equals("Context")) {
		// StringLiteral sl = (StringLiteral) obj.getConstantValue();
		// systemModel.setHazardReportContext(sl.getValue());
		// } else if (obj.getPropertyType().getName().equals("Assumption")) {
		// StringLiteral sl = (StringLiteral) obj.getConstantValue();
		// systemModel.addAssumption(sl.getValue());
		// } else if (obj.getPropertyType().getName().equals("Abbreviation")) {
		// RecordValue rv = (RecordValue) obj.getConstantValue();
		// StringLiteral fullSL = (StringLiteral)
		// PropertyUtils.getRecordFieldValue(rv, "Full");
		// StringLiteral defSL = (StringLiteral)
		// PropertyUtils.getRecordFieldValue(rv, "Definition");
		// AbbreviationModel am = new AbbreviationModel();
		// am.setName(obj.getName());
		// am.setFull(fullSL.getValue());
		// am.setDefinition(defSL.getValue());
		// systemModel.addAbbreviation(am);
		// } else if (obj.getPropertyType().getName().equals("Accident")) {
		// RecordValue rv = (RecordValue) obj.getConstantValue();
		// StringLiteral sl = (StringLiteral)
		// PropertyUtils.getRecordFieldValue(rv, "Description");
		// NamedValue nv = (NamedValue) PropertyUtils.getRecordFieldValue(rv,
		// "Level");
		// PropertyConstant pc = (PropertyConstant) nv.getNamedValue();
		// AccidentModel am = new AccidentModel();
		// handleExplanations(rv, am);
		// am.setName(obj.getName());
		// am.setDescription(sl.getValue());
		// am.setParent(systemModel.getAccidentLevelByName(pc.getName()));
		// systemModel.addAccident(am);
		// } else if (obj.getPropertyType().getName().equals("Hazard")) {
		// RecordValue rv = (RecordValue) obj.getConstantValue();
		// StringLiteral sl = (StringLiteral)
		// PropertyUtils.getRecordFieldValue(rv, "Description");
		// NamedValue nv = (NamedValue) PropertyUtils.getRecordFieldValue(rv,
		// "Accident");
		// PropertyConstant pc = (PropertyConstant) nv.getNamedValue();
		// HazardModel hm = new HazardModel();
		// handleExplanations(rv, hm);
		// hm.setName(obj.getName());
		// hm.setDescription(sl.getValue());
		// hm.setParent(systemModel.getAccidentByName(pc.getName()));
		// systemModel.addHazard(hm);
		// } else if (obj.getPropertyType().getName().equals("Constraint")) {
		// RecordValue rv = (RecordValue) obj.getConstantValue();
		// StringLiteral sl = (StringLiteral)
		// PropertyUtils.getRecordFieldValue(rv, "Description");
		// NamedValue nv = (NamedValue) PropertyUtils.getRecordFieldValue(rv,
		// "Hazard");
		// PropertyConstant pc = (PropertyConstant) nv.getNamedValue();
		// ConstraintModel cm = new ConstraintModel();
		// handleExplanations(rv, cm);
		// cm.setName(obj.getName());
		// cm.setDescription(sl.getValue());
		// cm.setParent(systemModel.getHazardByName(pc.getName()));
		// systemModel.addConstraint(cm);
		// }
		// } catch (PropertyOutOfRangeException | DuplicateElementException e) {
		// handleException(obj, e);
		// }
		// return NOT_DONE;
		// }

		@Override
		public String caseProcessType(ProcessType obj) {
			ProcessModel pm = null;
			if (target == TranslationTarget.SYSTEM) {
				try {
					if (systemModel.hasProcessType(obj.getName())) {
						componentModel = systemModel.getProcessByType(obj.getName());
						pm = systemModel.getProcessByType(obj.getName());
					} else {
						throw new UseBeforeDeclarationException(
								"Attempted to define a process that wasn't declared as a system component");
					}
				} catch (UseBeforeDeclarationException e) {
					handleException(obj, e);
					return DONE;
				}
				children.put(componentModel, obj);
			} else if (target == TranslationTarget.PROCESS) {
				// Translating just a process...

				// 1. Create a stub system
				systemModel = new SystemModel();
				systemModel.setName("Process_Stub_System");

				// 2. Process the type before the impl
				// processEList(new
				// BasicEList<Element>(Collections.singletonList((Element)obj.getContainingClassifier())));

				// 3. Process the impl
				if (handleNewProcess(obj) != null)
					return DONE;
				pm = systemModel.getProcessByType(obj.getName());
				children.put(pm, obj);
			} else {
				// TODO: This shouldn't be hit. Throw an error?
			}
			try {
				getComponentType(obj);
				String processType = checkCustomProperty(obj, "Process_Type", PropertyType.ENUM);
				if (processType != null && processType.equalsIgnoreCase("logic")) {
					pm.setDisplay(false);
				} else if (processType != null && processType.equalsIgnoreCase("display")) {
					pm.setDisplay(true);
				} else {
					throw new PropertyOutOfRangeException(
							"Processes must declare their component type to be either display or logic");
				}
			} catch (PropertyOutOfRangeException | MissingRequiredPropertyException e) {
				handleException(obj, e);
				return DONE;
			}
			lastElemProcessed = ElementType.PROCESS;
			processEList(new BasicEList<Element>(Lists.reverse(obj.getOwnedElements())));
			return NOT_DONE;
		}

		@Override
		public String casePort(Port obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				handlePort(obj);
			} else if (lastElemProcessed == ElementType.DEVICE) {
				handlePort(obj); // Explicit "out" port
				if (!cancelled()) {
					handlePseudoDeviceImplicitTask(obj); // Implicit task to
					// handle incoming data
				}
			} else if (lastElemProcessed == ElementType.THREAD) {
				handlePort(obj);
			}
			return NOT_DONE;
		}

		private void handlePort(Port obj) {
			String typeName = null, minPeriod = null, maxPeriod = null, exchangeName = null;
			Property typeNameProp = null;
			try {

				if (obj.getCategory() == PortCategory.EVENT_DATA) {
					typeNameProp = GetProperties.lookupPropertyDefinition(
							((EventDataPort) obj).getDataFeatureClassifier(), DataModel._NAME,
							DataModel.Data_Representation);
				} else if (obj.getCategory() == PortCategory.DATA) {
					typeNameProp = GetProperties.lookupPropertyDefinition(((DataPort) obj).getDataFeatureClassifier(),
							DataModel._NAME, DataModel.Data_Representation);
				} else if (obj.getCategory() == PortCategory.EVENT) {
					// Do nothing, we have an event port
				}
				try {
					if (typeNameProp != null) {
						typeName = getJavaType(PropertyUtils.getEnumLiteral(obj, typeNameProp).getName());
					} else {
						typeName = getJavaType(null);
					}
				} catch (PropertyNotPresentException e) {
					throw new MissingRequiredPropertyException("Missing the required data representation");
				}

				minPeriod = handleOverridableProperty(obj, "Default_Output_Rate", "MAP_Properties", "Output_Rate",
						PropertyType.RANGE_MIN);
				maxPeriod = handleOverridableProperty(obj, "Default_Output_Rate", "MAP_Properties", "Output_Rate",
						PropertyType.RANGE_MAX);

				if (minPeriod == null || maxPeriod == null) {
					throw new MissingRequiredPropertyException("Missing the required output rate specification.");
				}

				exchangeName = checkCustomProperty(obj, "Exchange_Name", PropertyType.STRING);

				PortModel pm = new PortModel();
				pm.setName(obj.getName());
				pm.setContainingComponentName(obj.getContainingClassifier().getName());
				pm.setType(typeName);
				pm.setMinPeriod(Integer.valueOf(minPeriod));
				pm.setMaxPeriod(Integer.valueOf(maxPeriod));
				pm.setExchangeName(exchangeName);
				if (obj.getDirection() == DirectionType.IN) {
					pm.setSubscribe(true);
				} else if (obj.getDirection() == DirectionType.OUT) {
					pm.setSubscribe(false);
				} else {
					throw new NotImplementedException("Ports must be either in nor out");
				}
				if (obj.getCategory() == PortCategory.EVENT_DATA) {
					pm.setEventData();
				} else if (obj.getCategory() == PortCategory.DATA) {
					pm.setData();
					handleDataPortImplicitTask(pm, obj);
				} else if (obj.getCategory() == PortCategory.EVENT) {
					pm.setEvent();
				}
				componentModel.addFeature(pm);
			} catch (NotImplementedException | MissingRequiredPropertyException | DuplicateElementException e) {
				handleException(obj, e);
				return;
			}
		}

		private void handleException(Element obj, Exception e) {
			INode node = NodeModelUtils.findActualNodeFor(obj);
			IResource file = OsateResourceUtil.convertToIResource(obj.eResource());
			ParseErrorReporter errReporter = errorManager.getReporter(file);
			if (errReporter instanceof MarkerParseErrorReporter)
				((MarkerParseErrorReporter) errReporter).setContextResource(obj.eResource());
			errReporter.error(obj.eResource().getURI().lastSegment(), node.getStartLine(), e.getMessage());
			cancelTraversal();
		}

		/**
		 * Gets the java representation of the type with the specified name
		 * 
		 * @param name
		 *            The name of the AADL data representation (eg "Integer" or
		 *            "Double")
		 * @return The equivalent java type
		 * @throws NotImplementedException
		 *             Thrown if there's no java equivalent of the supplied type
		 */
		private String getJavaType(String name) throws NotImplementedException {
			if (name == null) {
				return "Object";
			} else if (name.equals("Integer") || name.equals("Boolean")) {
				return name;
			} else if (name.equals("Float")) {
				return "Double";
			} else {
				throw new NotImplementedException("No java equivalent for type " + name);
			}
		}

		/*-
		@Override
		public String caseDataSubcomponent(DataSubcomponent obj) {
			ProcessModel pm = null;
			if (lastElemProcessed == ElementType.PROCESS) {
				Property prop = GetProperties.lookupPropertyDefinition(
						obj.getDataSubcomponentType(), DataModel._NAME,
						DataModel.Data_Representation);
				String typeName = null;
				try {
					if (componentModel instanceof ProcessModel)
						pm = (ProcessModel) componentModel;
					else
						throw new NotImplementedException(
								"Data subcomponents aren't supported for non-process subcomponent "
										+ componentModel.getName());
					typeName = getJavaType(PropertyUtils.getEnumLiteral(obj,
							prop).getName());
				} catch (NotImplementedException e) {
					handleException(obj, e);
					return DONE;
				}
				pm.addGlobal(obj.getName(), typeName);
			}
			return NOT_DONE;
		}
		 */

		/*-
		@Override
		public String caseAccessConnection(AccessConnection obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				handleProcessDataConnection(obj);
			} else if (lastElemProcessed == ElementType.THREAD) {
				handleSubprogramDataConnection(obj);
			}
			return NOT_DONE;
		}
		 */

		@Override
		public String caseAadlPackage(AadlPackage obj) {
			processEList(obj.getOwnedPublicSection().getChildren());
			return DONE;
		}

		@Override
		public String casePropertySet(PropertySet obj) {
			processEList(obj.getOwnedPropertyConstants());
			return DONE;
		}

		@Override
		public String caseComponentImplementation(ComponentImplementation obj) {
			processEList(obj.getOwnedElements());
			return DONE;
		}

		/*-@Override
		public String caseComponentClassifier(ComponentClassifier obj) {
			handlePropagations(obj);
			return NOT_DONE;
		}*/

		public String caseAnnexSubclause(AnnexSubclause obj) {
			if (!(obj.getName().equals("EMV2") && (obj.getContainingClassifier() instanceof ComponentClassifier))) {
				// Make sure we aren't reading non-emv2 annotations
				// And that we're dealing with a component
				return NOT_DONE;
			}
			ComponentClassifier component = (ComponentClassifier) obj.getContainingClassifier();
			if (!(component instanceof ComponentImplementation)) {
				// AADL lets you redefine propagations in a component
				// implementation. This seems, to me, to be counter to the
				// notion of an interface, so we skip propagation declarations
				// within implementations
				handlePropagations(component);
			}
			componentModel.setFaultClasses(errorTypeSets.get(FAULT_CLASSES_NAME));
			try {
				handleErrorFlows(component);
				handleErrorEvents(component);
				handleErrorStateTransitions(component);
				handleErrorStates(component);
			} catch (CoreException e) {
				// Exceptions are handled in the handlers for more specific
				// messages, but we need to stop translation
				return DONE;
			}
			return NOT_DONE;
		}

		@Override
		public String caseAbstractSubcomponent(AbstractSubcomponent obj) {
			try {
				handleFundamentalsProperties(obj);
			} catch (NotImplementedException | CoreException | DuplicateElementException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return NOT_DONE;
		}

		private void handleFundamentalsProperties(AbstractSubcomponent obj)
				throws NotImplementedException, CoreException, DuplicateElementException {

			// Initialize counters -- these get set and reset by the loops below
			int accidentLevelNumber = 0;
			int accidentNumber = 0;
			int hazardNumber = 0;
			int constraintNumber = 0;

			// Declare models uptop rather than within the loops
			AccidentLevelModel accidentLevelModel;
			AccidentModel accidentModel;
			HazardModel hazardModel;
			ConstraintModel constraintModel;

			// Declare and initialize the proptypes and path list.
			// These are paths through the full fundamentals structure, and are
			// read from low index (top of fundamentals tree) to high index
			// (leaves of fund. tree)
			List<PropertyType> propTypes = new ArrayList<PropertyType>();
			List<String> path = new ArrayList<String>();

			// Initialize values for parsing the accident model: outer structure
			// is a record
			propTypes.add(PropertyType.RECORD);
			// Then a list
			propTypes.add(PropertyType.LIST);
			// First key is the name "fundamentals"
			path.add("Fundamentals");
			// And we want the first one (index 0)
			path.add(String.valueOf(accidentLevelNumber));
			// Create the model
			accidentLevelModel = new AccidentLevelModel();
			// Parse the common values
			handleFundamentalsProperty(obj, accidentLevelModel, propTypes, path);

			while (accidentLevelModel.getName() != null) {
				// If we're here, we know we have an accident level, so we add
				// the AccidentLevel Unique feature (number)
				accidentLevelModel.setNumber(accidentLevelNumber + 1);
				// Associate the accident level with this system so the children
				// can query it later
				accidentLevelModel.setSystem(systemModel);
				// And add the whole thing to the model
				systemModel.addAccidentLevel(accidentLevelModel);

				// Now we parse the accidents associated with this accident
				// level. First, this involves extending the path and proptypes
				propTypes.add(PropertyType.RECORD);
				path.add("Accidents");
				propTypes.add(PropertyType.LIST);
				path.add(String.valueOf(accidentNumber));
				// Initializing our model
				accidentModel = new AccidentModel();
				// And filling in the known values
				handleFundamentalsProperty(obj, accidentModel, propTypes, path);
				while (accidentModel.getName() != null) {
					// If we're here, we know we have an accident
					accidentModel.setParent(accidentLevelModel);
					systemModel.addAccident(accidentModel);

					// Now we parse the hazards associated with this accident
					propTypes.add(PropertyType.RECORD);
					path.add("Hazards");
					propTypes.add(PropertyType.LIST);
					path.add(String.valueOf(hazardNumber));
					hazardModel = new HazardModel();
					handleFundamentalsProperty(obj, hazardModel, propTypes, path);
					while (hazardModel.getName() != null) {
						hazardModel.setParent(accidentModel);
						propTypes.add(PropertyType.RECORD);
						path.add("SystemElement");
						hazardModel.setSystemElement(
								checkCustomEMV2Property(obj, FUNDAMENTALS_PROP_NAME, propTypes, path));
						path.set(path.size() - 1, "EnvironmentElement");
						hazardModel.setEnvironmentElement(
								checkCustomEMV2Property(obj, FUNDAMENTALS_PROP_NAME, propTypes, path));
						path.remove(path.size() - 1);
						propTypes.remove(propTypes.size() - 1);
						systemModel.addHazard(hazardModel);

						// Now we parse the constraints associated with this
						// hazard
						propTypes.add(PropertyType.RECORD);
						path.add("Constraints");
						propTypes.add(PropertyType.LIST);
						path.add(String.valueOf(constraintNumber));
						constraintModel = new ConstraintModel();
						handleFundamentalsProperty(obj, constraintModel, propTypes, path);
						while (constraintModel.getName() != null) {
							constraintModel.setParent(hazardModel);
							propTypes.add(PropertyType.RECORD);
							path.add("ErrorType");
							constraintModel.setErrorTypeName(
									checkCustomEMV2Property(obj, FUNDAMENTALS_PROP_NAME, propTypes, path));
							path.remove(path.size() - 1);
							propTypes.remove(propTypes.size() - 1);
							systemModel.addConstraint(constraintModel);

							path.set(path.size() - 1, String.valueOf(++constraintNumber));
							constraintModel = new ConstraintModel();
							handleFundamentalsProperty(obj, constraintModel, propTypes, path);
						}
						constraintNumber = 0;
						path.remove(path.size() - 1);
						propTypes.remove(propTypes.size() - 1);
						path.remove(path.size() - 1);
						propTypes.remove(propTypes.size() - 1);

						path.set(path.size() - 1, String.valueOf(++hazardNumber));
						hazardModel = new HazardModel();
						handleFundamentalsProperty(obj, hazardModel, propTypes, path);
					}
					hazardNumber = 0;
					path.remove(path.size() - 1);
					propTypes.remove(propTypes.size() - 1);
					path.remove(path.size() - 1);
					propTypes.remove(propTypes.size() - 1);

					path.set(path.size() - 1, String.valueOf(++accidentNumber));
					accidentModel = new AccidentModel();
					handleFundamentalsProperty(obj, accidentModel, propTypes, path);
				}
				// By this point, we've parsed all the accidents (and their
				// children), so we reset the path and propTypes so we can
				// examine the next accident level model.
				accidentNumber = 0;
				path.remove(path.size() - 1);
				propTypes.remove(propTypes.size() - 1);
				path.remove(path.size() - 1);
				propTypes.remove(propTypes.size() - 1);

				path.set(path.size() - 1, String.valueOf(++accidentLevelNumber));
				accidentLevelModel = new AccidentLevelModel();
				handleFundamentalsProperty(obj, accidentLevelModel, propTypes, path);
			}
		}

		private void handleFundamentalsProperty(AbstractSubcomponent obj, StpaPreliminaryModel prelim,
				List<PropertyType> propTypes, List<String> path) throws NotImplementedException, CoreException {

			propTypes.add(PropertyType.RECORD);
			path.add("Name");
			String name = checkCustomEMV2Property(obj, FUNDAMENTALS_PROP_NAME, propTypes, path);

			path.set(path.size() - 1, "Description");
			String desc = checkCustomEMV2Property(obj, FUNDAMENTALS_PROP_NAME, propTypes, path);

			if (name == null || desc == null) {
				propTypes.remove(propTypes.size() - 1);
				path.remove(path.size() - 1);
				return;
			}

			path.set(path.size() - 1, "Explanations");
			path.add("0");
			propTypes.add(PropertyType.LIST);
			String accLevelExp = checkCustomEMV2Property(obj, FUNDAMENTALS_PROP_NAME, propTypes, path);
			int i = 1;
			while (accLevelExp != null) {
				prelim.addExplanation(accLevelExp);
				path.set(path.size() - 1, String.valueOf(i));
				accLevelExp = checkCustomEMV2Property(obj, FUNDAMENTALS_PROP_NAME, propTypes, path);
				i++;
			}
			path.remove(path.size() - 1);
			path.remove(path.size() - 1);
			propTypes.remove(propTypes.size() - 1);
			propTypes.remove(propTypes.size() - 1);
			prelim.setName(name);
			prelim.setDescription(desc);
		}

		private void handleErrorStates(ComponentClassifier obj) throws CoreException {
			ErrorBehaviorState init = null;
			for (ErrorBehaviorState state : EMV2Util.getAllErrorBehaviorStates(obj)) {
				if (!state.isIntial()) {
					continue; // We only care about properties of the initial
								// state
				}
				init = state;
			}
			String explanation;
			Set<String> elimFaults;
			for (EMV2PropertyAssociation pa : EMV2Properties.getProperty("MAP_Error_Properties::EliminatedFaults", obj,
					init, null)) {
				RecordValue rv = (RecordValue) EMV2Properties.getPropertyValue(pa);
				explanation = ((StringLiteral) PropertyUtils.getRecordFieldValue(rv, "Explanation")).getValue();
				elimFaults = new LinkedHashSet<>();
				for (PropertyExpression pe : ((ListValue) PropertyUtils.getRecordFieldValue(rv, "FaultTypes"))
						.getOwnedListElements()) {
					ContainmentPathElement cpe = (ContainmentPathElement) ((ReferenceValue) pe)
							.getContainmentPathElements().get(0);
					elimFaults.add(cpe.getNamedElement().getName());
				}
				componentModel.addEliminatedFaults(explanation, elimFaults);
			}
		}

		private void handleErrorStateTransitions(ComponentClassifier obj) throws CoreException {
			Set<ErrorBehaviorTransition> ebTrans = new LinkedHashSet<>();
			ebTrans.addAll(EMV2Util.getAllErrorBehaviorTransitions(obj));
			if (ebTrans.isEmpty()) {
				return; // Bail out if there aren't any transitions
			}
			try {
				for (ErrorBehaviorTransition ebt : ebTrans) {
					List<ErrorEvent> events = new LinkedList<>();
					ConditionExpression cond = ebt.getCondition();
					OrExpression orCond;

					while (cond instanceof OrExpression) {
						orCond = (OrExpression) cond;
						events.add((ErrorEvent) ((ConditionElement) (orCond).getOperands().get(1))
								.getQualifiedErrorPropagationReference().getEmv2Target().getNamedElement());
						cond = orCond.getOperands().get(0);
					}
					events.add((ErrorEvent) ((ConditionElement) cond).getQualifiedErrorPropagationReference()
							.getEmv2Target().getNamedElement());

					// Set dangerSource to internal or external depending on the
					// events.
					DangerSource dangerSource = events.stream()
							.reduce(typesAreInternalOrExternal(
									events.get(0).getTypeSet()),
							(accumDS, evt) -> typesAreInternalOrExternal(evt.getTypeSet()) == accumDS ? accumDS : null,
							(accDS1, accDS2) -> accDS1 == accDS2 ? accDS1 : null);

					String kind = null;
					if (dangerSource == DangerSource.INTERNAL) {
						kind = "Fault";
					} else if (dangerSource == DangerSource.EXTERNAL) {
						kind = "Error";
					} else {
						throw new NotImplementedException(
								"Error behavior transitions must not mix internal and external dangers.");
					}

					String errorApproachStr = checkCustomEMV2Property(ebt,
							"MAP_Error_Properties::Runtime" + kind + "Handling",
							Collections.singletonList(PropertyType.RECORD),
							Collections.singletonList("ErrorHandlingApproach"));
					if (errorApproachStr == null) {
						errorApproachStr = "NOTSPECIFIED";
					}
					String explanation = checkCustomEMV2Property(ebt,
							"MAP_Error_Properties::Runtime" + kind + "Handling",
							Collections.singletonList(PropertyType.RECORD), Collections.singletonList("Explanation"));
					if (explanation == null) {
						throw new MissingRequiredPropertyException(
								"All behavior transitions associated with externally caused problems must have an explanation specified via a Runtime"
										+ kind + "Handling property");
					}
					RuntimeHandlingModel rhm = new RuntimeHandlingModel(ebt.getName(), errorApproachStr, explanation);

					if (dangerSource == DangerSource.INTERNAL) {
						String faultApproachStr = checkCustomEMV2Property(ebt,
								"MAP_Error_Properties::RuntimeFaultHandling",
								Collections.singletonList(PropertyType.RECORD),
								Collections.singletonList("FaultHandlingApproach"));
						if (faultApproachStr == null) {
							faultApproachStr = "NOTSPECIFIED";
						}
						rhm.setFaultHandlingApproach(faultApproachStr);
					}

					for (ErrorEvent evt : events) {
						for (CausedDangerModel cdm : getCausedDangerModelsByPropagationName(evt)) {
							cdm.addRuntimeHandling(rhm);
						}
					}

				}
			} catch (NotImplementedException | MissingRequiredPropertyException e) {
				handleException(obj, e);
				throw new CoreException("An internal error occurred");
			} catch (ClassCastException e) {
				Exception e2 = new NotImplementedException(
						"Error behavior state machine transitions must be single depth or expressions");
				handleException(obj, e2);
				throw new CoreException("An internal error occurred");
			}
		}

		private void handleErrorEvents(ComponentClassifier obj) throws CoreException {
			Set<ErrorBehaviorEvent> eEvents = new LinkedHashSet<>();
			eEvents.addAll(EMV2Util.getAllErrorBehaviorEvents(obj));
			if (eEvents.isEmpty()) {
				return; // Bail out if there aren't any events
			}
			for (ErrorBehaviorEvent event : eEvents) {
				try {
					if (event instanceof ErrorEvent) {
						handleErrorEvent((ErrorEvent) event);
					} else {
						throw new NotImplementedException(
								"Error behavior events must not be recover or repair events!");
					}
				} catch (NotImplementedException | MissingRequiredPropertyException e) {
					handleException(event, e);
					throw new CoreException("An internal error occurred.");
				}
			}
		}

		private void handleErrorEvent(ErrorEvent evt)
				throws NotImplementedException, MissingRequiredPropertyException, CoreException {
			TypeSet ts = evt.getTypeSet();
			DangerSource dangerSource = typesAreInternalOrExternal(ts);
			if (dangerSource == DangerSource.BOTH) {
				throw new NotImplementedException(
						"Error events have to be associated with either internal or external problems, but not both.");
			} else if (dangerSource == DangerSource.EXTERNAL) {
				handleDetectableExternalEvent(evt);
			} else if (dangerSource == DangerSource.INTERNAL) {
				handleDetectableInternalEvent(evt);
			}
		}

		private DangerSource typesAreInternalOrExternal(TypeSet ts) {
			Set<ManifestationTypeModel> ecdms = new HashSet<>();
			Set<InternallyCausedDangerModel> icdms = new HashSet<>();
			for (TypeToken tt : ts.getTypeTokens()) {
				// get compensated dangers
				ecdms.addAll(componentModel.getSunkDangers().values().stream()
						// get the sets of errors that cause them
						.map(v -> v.getSuccessorDanger().getErrors())
						// flatten those sets into one collection
						.flatMap(w -> w.stream())
						// if the name matches, store it.
						.filter(v -> v.getName().equals(EMV2Util.getName(tt))).collect(Collectors.toSet()));
				icdms.addAll(componentModel.getInternallyCausedDangers().values().stream()
						.filter(v -> v.getFaultClasses().contains(EMV2Util.getName(tt))).collect(Collectors.toSet()));
			}
			DangerSource kind = null;
			if (ecdms.size() > 0 && icdms.size() > 0) {
				// Event is triggered by both fault and external event.
				kind = DangerSource.BOTH;
			} else if (ecdms.size() > 0) {
				// Detectable external error
				kind = DangerSource.EXTERNAL;
			} else if (icdms.size() > 0) {
				// Detectable internal fault
				kind = DangerSource.INTERNAL;
			}
			return kind;
		}

		private void handleDetectableInternalEvent(ErrorEvent evt) throws NotImplementedException, CoreException {
			String explanation = checkCustomEMV2Property(evt, "MAP_Error_Properties::DesignTimeFaultDetection",
					Collections.singletonList(PropertyType.RECORD), Collections.singletonList("Explanation"));
			if (explanation == null) {
				explanation = "(None Given)";
			}
			String approachStr = checkCustomEMV2Property(evt, "MAP_Error_Properties::DesignTimeFaultDetection",
					Collections.singletonList(PropertyType.RECORD),
					Collections.singletonList("FaultDetectionApproach"));
			DesignTimeDetectionModel ddm = new DesignTimeDetectionModel(explanation, evt.getName(), approachStr);

			// TODO: can this be optimized? Does it need to be?
			for (CausedDangerModel cdm : getCausedDangerModelsByPropagationName(evt)) {
				((InternallyCausedDangerModel) cdm).addDesignTimeDetection(ddm);
			}

		}

		private void handleDetectableExternalEvent(ErrorEvent evt)
				throws NotImplementedException, MissingRequiredPropertyException, CoreException {
			String explanation = checkCustomEMV2Property(evt, "MAP_Error_Properties::RuntimeErrorDetection",
					Collections.singletonList(PropertyType.RECORD), Collections.singletonList("Explanation"));
			if (explanation == null) {
				throw new MissingRequiredPropertyException(
						"Runtime error detections, with an explanation, must be specified.");
			}
			String approachStr = checkCustomEMV2Property(evt, "MAP_Error_Properties::RuntimeErrorDetection",
					Collections.singletonList(PropertyType.RECORD),
					Collections.singletonList("ErrorDetectionApproach"));
			if (approachStr == null) {
				throw new MissingRequiredPropertyException(
						"Runtime error detections, with a detection approach, must be specified.");
			}
			RuntimeDetectionModel rdm = new RuntimeDetectionModel(explanation, evt.getName(), approachStr);

			// TODO: can this be optimized? Does it need to be?
			for (CausedDangerModel cdm : getCausedDangerModelsByPropagationName(evt)) {
				cdm.addRuntimeDetection(rdm);
			}
		}

		private Set<CausedDangerModel> getCausedDangerModelsByPropagationName(ErrorEvent evt) {
			Set<CausedDangerModel> ret = new LinkedHashSet<>();
			for (CausedDangerModel cdm : componentModel.getCausedDangers().values()) {
				for (TypeToken tt : evt.getTypeSet().getTypeTokens()) {
					if (((cdm instanceof NotDangerousDangerModel)
							&& (cdm.getSuccessorDanger().getErrorByName(EMV2Util.getName(tt)) != null))
							|| ((cdm instanceof InternallyCausedDangerModel) && (((InternallyCausedDangerModel) cdm)
									.getFaultClasses().contains(EMV2Util.getName(tt))))) {
						ret.add(cdm);
					}
				}
			}
			return ret;
		}

		private void handleErrorFlows(ComponentClassifier obj) throws CoreException {
			Set<ErrorFlow> eFlows = new LinkedHashSet<>();
			eFlows.addAll(EMV2Util.getAllErrorFlows(obj));
			if (eFlows.isEmpty()) {
				return; // Bail out if there aren't any flows
			}
			for (ErrorFlow flow : eFlows) {
				try {
					if (flow instanceof ErrorPath) {
						handleErrorPath((ErrorPath) flow);
					} else if (flow instanceof ErrorSink) {
						handleErrorSink((ErrorSink) flow);
					} else if (flow instanceof ErrorSource) {
						handleErrorSource((ErrorSource) flow);
					} else {
						throw new NotImplementedException("Error flows must be paths, sources, or sinks!");
					}
				} catch (NotImplementedException | CoreException | MissingRequiredPropertyException e) {
					handleException(flow, e);
					throw new CoreException("An internal error occurred.");
				}
			}
		}

		private void handleErrorSource(ErrorSource source)
				throws CoreException, NotImplementedException, MissingRequiredPropertyException {

			// 1. Get the fault class, resulting error, and successor danger
			TypeSet faultClass = source.getFailureModeType();
			TypeSet resultingError = source.getTypeTokenConstraint();
			PropagationModel succDanger = getPropFromTokens(source, resultingError.getTypeTokens(), false);

			// 2. Parse the associated internally caused danger property
			String interp = checkCustomEMV2Property(source, "MAP_Error_Properties::InternallyCausedDanger",
					Collections.singletonList(PropertyType.RECORD), Collections.singletonList("Explanation"));
			if (interp == null) {
				// Throwing this exception here means that in data ports are
				// required to declare ICD's, which really makes the tooling
				// hard to use.
//				throw new MissingRequiredPropertyException("No matching InternallyCausedDanger found!");
				return;
			}

			// X. Create the model, and set its values
			InternallyCausedDangerModel icdm = new InternallyCausedDangerModel(succDanger, interp);
			setFaultClasses(faultClass, icdm);
			componentModel.addCausedDanger(icdm);
		}

		private void setFaultClasses(TypeSet faultClass, InternallyCausedDangerModel icdm) throws CoreException {
			if (faultClass.getTypeTokens().isEmpty()) {
				throw new CoreException("All internal faults must have a base fault class declared");
			}
			for (TypeToken errType : faultClass.getTypeTokens()) {
				// TODO: This name should come from a system-level fundamental
				// property
				icdm.addFaultClass(EMV2Util.getName(errType));
			}
		}

		private void handleErrorSink(ErrorSink sink) {
			TypeSet incomingErrors = sink.getTypeTokenConstraint();
			String interp = "Not dangerous";
			PropagationModel succDanger = getPropFromTokens(sink, incomingErrors.getTypeTokens(), true);
			for (ManifestationTypeModel mtm : succDanger.getErrors()) {
				mtm.setSunk();
			}
			NotDangerousDangerModel nddm = new NotDangerousDangerModel(succDanger, interp);
			componentModel.addCausedDanger(nddm);
		}

		private void handleErrorPath(ErrorPath path)
				throws CoreException, NotImplementedException, MissingRequiredPropertyException {
			TypeSet incomingErrors = path.getTypeTokenConstraint();
			TypeSet outgoingErrors = path.getTargetToken();
			PropagationModel manifestation = null, succDanger = null;
			manifestation = getPropFromTokens(path, incomingErrors.getTypeTokens(), true);
			succDanger = getPropFromTokens(path, outgoingErrors.getTypeTokens(), false);
			String interp = checkCustomEMV2Property(path, "MAP_Error_Properties::ExternallyCausedDanger",
					Collections.singletonList(PropertyType.RECORD), Collections.singletonList("Explanation"));
			if (interp == null) {
				throw new MissingRequiredPropertyException("No matching ExternallyCausedDanger found!");
			}
			ProcessVariableModel pvm = componentModel.getProcessModel()
					.get(checkCustomEMV2Property(path, "MAP_Error_Properties::ExternallyCausedDanger",
							Collections.singletonList(PropertyType.RECORD),
							Collections.singletonList("ProcessVariableValue")));
			String constraint = checkCustomEMV2Property(path, "MAP_Error_Properties::ExternallyCausedDanger",
					Collections.singletonList(PropertyType.RECORD),
					Collections.singletonList("ProcessVariableConstraint"));
			ExternallyCausedDangerModel ecdm = new ExternallyCausedDangerModel(succDanger, manifestation, interp, pvm,
					constraint);
			componentModel.addCausedDanger(ecdm);
		}

		/**
		 * Retrieves the value of the specified property as a String.
		 * 
		 * @param elem
		 *            The element that the property applies to
		 * @param propName
		 *            The fully qualified name of the property
		 * @param propType
		 *            The types of the properties that are associated with each
		 *            element of the path list
		 * @param path
		 *            The keys that should be used to navigate through the
		 *            record property, or null if the property isn't a record
		 * @return String representation of property value or null if the
		 *         property isn't found
		 * @throws NotImplementedException
		 *             Thrown if the property usage isn't supported by the
		 *             translator
		 * @throws CoreException
		 *             Thrown if the caller uses different-length lists for
		 *             propTypes and path
		 */
		private String checkCustomEMV2Property(NamedElement elem, String propName, List<PropertyType> propTypes,
				List<String> path) throws NotImplementedException, CoreException {

			if (path.size() != propTypes.size()) {
				throw new CoreException("Path and proptype list sizes don't match!");
			}

			List<EMV2PropertyAssociation> pas = EMV2Properties.getProperty(propName, elem.getContainingClassifier(),
					elem, null);
			if (pas.size() == 0) {
				return null;
			} else if (pas.size() > 1) {
				throw new NotImplementedException(
						"Mutliple properties are associated with this element, but only one was expected");
			}

			PropertyExpression prex = EMV2Properties.getPropertyValue(pas.get(0));

			try {
				for (int i = 0; i < path.size(); i++) {
					if (prex == null) {
						// A property that the caller thought was set isn't
						return null;
					}
					if (propTypes.get(i) == PropertyType.RECORD) {
						prex = (PropertyUtils.getRecordFieldValue((RecordValue) prex, path.get(i)));
					} else if (propTypes.get(i) == PropertyType.LIST) {
						prex = ((ListValue) prex).getOwnedListElements().get(Integer.valueOf(path.get(i)));
					}
				}
			} catch (IndexOutOfBoundsException e) {
				// This gets hit if the caller wants a value that isn't set, ie
				// an unset key for a record or an out of bounds list index
				return null;
			}

			if (prex instanceof StringLiteral) {
				return ((StringLiteral) prex).getValue();
			} else if (prex instanceof NamedValue) {
				// check for enum literal, and maybe constant?
				AbstractNamedValue anv = ((NamedValue) prex).getNamedValue();
				if (anv instanceof EnumerationLiteral) {
					return ((EnumerationLiteral) anv).getName();
				}
				return null;
			} else if (prex instanceof IntegerLiteral) {
				return String.valueOf(((IntegerLiteral) prex).getValue());
			} else if (prex instanceof ReferenceValue) {
				return ((ReferenceValue) prex).getContainmentPathElements().get(0).getNamedElement().getName();
			} else {
				return null;
			}
		}

		/**
		 * Get the propagation associated with a particular error type token on
		 * a particular propagation path
		 * 
		 * @param flow
		 *            The ErrorPath to examine
		 * @param token
		 *            The token that contains the error type we want
		 * @param isIn
		 *            True if we want the incoming error token, false if we want
		 *            the outgoing
		 * @return The propagation corresponding to the token, or null if it
		 *         can't be found
		 */
		private PropagationModel getPropFromTokens(ErrorFlow flow, EList<TypeToken> tokens, boolean isIn) {
			String portName = getPortNameFromErrorFlow(flow, isIn);
			FeatureModel portModel = resolvePortModel(componentModel, portName, isIn);
			Collection<ManifestationTypeModel> errors = getErrorModelsFromTokens(tokens, portModel).stream()
					.map(m -> (ManifestationTypeModel) m).collect(Collectors.toCollection(LinkedHashSet::new));
			PropagationModel propModel = new PropagationModel(flow.getName(), errors, portModel);
			portModel.addPropagation(propModel);
			return propModel;
		}

		/**
		 * Get a portname from an error flow and direction
		 * 
		 * @param flow
		 *            The error flow
		 * @param isIn
		 *            True if the port is incoming
		 * @return The name of the port
		 */
		private String getPortNameFromErrorFlow(ErrorFlow flow, boolean isIn) {
			ErrorPropagation eProp = null;
			if (isIn) {
				if (flow instanceof ErrorPath) {
					eProp = ((ErrorPath) flow).getIncoming();
				} else { // flow instanceof ErrorSink
					eProp = ((ErrorSink) flow).getIncoming();
				}
			} else {
				if (flow instanceof ErrorPath) {
					eProp = ((ErrorPath) flow).getOutgoing();
				} else { // flow instanceof ErrorSource
					eProp = ((ErrorSource) flow).getOutgoing();
				}
			}
			String portName = eProp.getFeatureorPPRef().getFeatureorPP().getName();
			return portName;
		}

		/**
		 * Gets the collection of error types referred to in a list of tokens
		 * 
		 * @param tokens
		 *            The tokens to resolve the error type models of
		 * @param portModel
		 *            The port these tokens are being propagated into or out of
		 * @return The error type models referred to by the tokens
		 */
		private Collection<ManifestationTypeModel> getErrorModelsFromTokens(EList<TypeToken> tokens,
				FeatureModel portModel) {
			Collection<ManifestationTypeModel> errors = new LinkedHashSet<>();
			for (TypeToken token : tokens) {
				errors.add(portModel.getPropagatableErrorByName(EMV2Util.getName(token)));
			}
			return errors;
		}

		private void handlePropagations(ComponentClassifier obj) {
			String portName;
			FeatureModel portModel;
			Set<ManifestationTypeModel> errorTypes;
			ManifestationTypeModel etm;

			// Odd that I can't just get all the error propagations at once
			Set<ErrorPropagation> eProps = new HashSet<>();
			eProps.addAll(EMV2Util.getAllErrorPropagations(obj, DirectionType.IN));
			eProps.addAll(EMV2Util.getAllErrorPropagations(obj, DirectionType.OUT));
			if (eProps.isEmpty()) {
				return; // Bail out if there aren't any propagations
			}
			for (ErrorPropagation eProp : eProps) {
				errorTypes = new LinkedHashSet<>();
				for (TypeToken tt : eProp.getTypeSet().getTypeTokens()) {
					// We assume that there are no pre-defined sets of error
					// types allowed ie, only allow ad-hoc, anonymous sets
					// created in propagations
					etm = systemModel.getErrorTypeModelByName(EMV2Util.getName(tt));
					if (etm != null) {
						etm.setManifestation(ManifestationType
								.valueOf(((ErrorType) tt.getType().get(0)).getSuperType().getName().toUpperCase()));
						errorTypes.add(etm);
					}
				}

				portName = eProp.getFeatureorPPRef().getFeatureorPP().getFullName();
				portModel = resolvePortModel(componentModel, portName, eProp.getDirection() == DirectionType.IN);

				if (portModel == null) {
					// If we're here, something is wrong and an error should
					// have been logged already. We bail out to avoid an NPE --
					// the error log should reflect the root exception
					return;
				}
				portModel.addPropagatableErrors(errorTypes);
			}
		}

		/*-
		@Override
		public String caseSubprogramType(SubprogramType obj) {
			lastElemProcessed = ElementType.SUBPROGRAM;
			return NOT_DONE;
		}
		
		
		@Override
		public String caseSubprogramCallSequence(SubprogramCallSequence obj) {
			handleCallSequence(
					((ThreadImplementation) obj.getOwner()).getTypeName(),
					obj.getOwnedCallSpecifications());
			return NOT_DONE;
		}
		 */

		private void handlePseudoDeviceImplicitTask(Port obj) {
			TaskModel tm;
			String taskName = obj.getName() + "Task";
			DeviceModel dm = (DeviceModel) componentModel;
			// Default values; period is set to -1 since these tasks are all
			// sporadic
			// TODO: Read these from plugin preferences?
			int period = -1, deadline = 50, wcet = 5;
			try {
				dm.addChild(taskName, new TaskModel(taskName));
				tm = dm.getChild(taskName);
				tm.setSporadic(true);
				tm.setPeriod(period);
				tm.setDeadline(deadline);
				tm.setWcet(wcet);
				String portName = obj.getName() + "Out";
				// tm.setTrigPortInfo(trigPortName,
				// dm.getPortByName(portName).getType(), portName, false);
				tm.setTrigPortLocalName(portName);
				tm.setTrigPort(dm.getPortByName(portName));
			} catch (DuplicateElementException | NotImplementedException e) {
				handleException(obj, e);
				return;
			}
		}

		private void handleDataPortImplicitTask(PortModel portModel, Port obj)
				throws NotImplementedException, DuplicateElementException {
			String taskName = portModel.getName() + "Task";
			TaskModel tm = new TaskModel(taskName);
			// Default values; period is set to -1 since these tasks are all
			// sporadic
			// TODO: Read these from plugin preferences?
			int period = -1, deadline = 50, wcet = 5;
			if (componentModel instanceof DeviceModel) {
				throw new NotImplementedException("Incoming device ports must be event or event data.");
			}
			ProcessModel pm = (ProcessModel) componentModel;
			pm.addChild(taskName, tm);
			tm = pm.getChild(taskName);
			tm.setSporadic(true);
			tm.setPeriod(period);
			tm.setDeadline(deadline);
			tm.setWcet(wcet);
			tm.setTrigPortLocalName(portModel.getName());
			tm.setTrigPort(portModel);
		}

		private void handleThreadProperties(ThreadType obj, TaskModel tm) {
			try {
				String trigType = handleOverridableProperty(obj, "Default_Thread_Dispatch", "Thread_Properties",
						"Dispatch_Protocol", PropertyType.ENUM);
				String period = handleOverridableProperty(obj, "Default_Thread_Period", "Timing_Properties", "Period",
						PropertyType.INT);
				String deadline = handleOverridableProperty(obj, "Default_Thread_Deadline", "Timing_Properties",
						"Deadline", PropertyType.INT);
				String wcet = handleOverridableProperty(obj, "Default_Thread_WCET", "MAP_Properties",
						"Worst_Case_Execution_Time", PropertyType.INT);
				if (trigType == null)
					throw new MissingRequiredPropertyException(
							"Thread dispatch type must either be set with Default_Thread_Dispatch (at package level) or with Thread_Properties::Dispatch_Protocol (on individual thread)");
				else if (period == null)
					throw new MissingRequiredPropertyException(
							"Thread period must either be set with Default_Thread_Period (at package level) or with Timing_Properties::Period (on individual thread)");
				else if (deadline == null)
					throw new MissingRequiredPropertyException(
							"Thread deadline must either be set with Default_Thread_Deadline (at package level) or with Timing_Properties::Deadline (on individual thread)");
				else if (wcet == null)
					throw new MissingRequiredPropertyException(
							"Thread WCET must either be set with Default_Thread_WCET (at package level) or with Timing_Properties::Compute_Execution_Time (on individual thread)");
				else {
					if (tm == null) {
						throw new UseBeforeDeclarationException(
								"Threads must be declared as subcomponents before being defined");
					}
					if (trigType.equalsIgnoreCase("sporadic")) {
						tm.setSporadic(true);
					} else if (trigType.equalsIgnoreCase("periodic")) {
						tm.setSporadic(false);
					} else {
						throw new NotImplementedException(
								"Thread dispatch must be either sporadic or periodic, instead got " + trigType);
					}
					tm.setPeriod(Integer.valueOf(period));
					tm.setDeadline(Integer.valueOf(deadline));
					tm.setWcet(Integer.valueOf(wcet));
				}
			} catch (MissingRequiredPropertyException | NotImplementedException | UseBeforeDeclarationException e) {
				handleException(obj, e);
				return;
			}

		}

		private String handleNewDevice(NamedElement obj) {
			DeviceModel dm = new DeviceModel();
			if (obj instanceof DeviceType) {
				dm.setName(obj.getName());
			} else if (obj instanceof DeviceSubcomponent) {
				dm.setName(((DeviceSubcomponent) obj).getComponentType().getName());
			} else {
				// TODO: This should never happen... handle it?
			}
			((DeviceModel) dm).setParentName(systemModel.getName());
			try {
				String componentType = checkCustomProperty(obj, "Component_Type", PropertyType.ENUM);
				if (componentType != null)
					dm.setComponentType(componentType);
				systemModel.addChild(obj.getName(), dm);

				componentModel = dm;
			} catch (DuplicateElementException e) {
				handleException(obj, e);
				return DONE;
			}
			return NOT_DONE;
		}

		private String handleNewProcess(NamedElement obj) {
			ProcessModel pm = new ProcessModel();
			if (obj instanceof ProcessType) {
				pm.setName(obj.getName());
			} else if (obj instanceof ProcessSubcomponent) {
				pm.setName(((ProcessSubcomponent) obj).getComponentType().getName());
			} else {
				// TODO: This should never happen... handle it?
			}
			pm.setParentName(systemModel.getName());
			try {
				systemModel.addChild(obj.getName(), pm);
			} catch (DuplicateElementException e) {
				handleException(obj, e);
				return DONE;
			}
			componentModel = pm;
			return NOT_DONE;
		}

		private String handleOverridableProperty(NamedElement obj, String defaultName, String overridePropertySet,
				String overrideName, PropertyType propType) {
			Property prop = GetProperties.lookupPropertyDefinition(obj, overridePropertySet, overrideName);
			String ret = null;

			// This try / catch (and the nested one in the for loop) are here
			// because I can't just check if a property exists -- instead, I
			// have to just try and check for a PropertyNotPresentException,
			// which makes for super clumsy code.
			try {
				ret = handlePropertyValue(obj, prop, propType);
			} catch (PropertyNotPresentException e) {
				ret = checkCustomProperty(obj, defaultName, propType);
			} catch (PropertyOutOfRangeException e) {
				handleException(obj, e);
				return null;
			} catch (CoreException e) {
				return null;
			}
			return ret;
		}

		/**
		 * Returns the value of a custom (ie, non-library) property, or null if
		 * no property is found
		 * 
		 * @param obj
		 *            The element that may contain the property
		 * @param propertyName
		 *            The name of the property
		 * @param propType
		 *            The type of the property
		 * @return The property value or null if the property isn't found
		 */
		private String checkCustomProperty(NamedElement obj, String propertyName, PropertyType propType) {
			String ret = null;
			Property prop;
			for (String propertySetName : propertySetNames) {
				try {
					prop = GetProperties.lookupPropertyDefinition(obj, propertySetName, propertyName);
					if (prop == null)
						continue;
					else
						ret = handlePropertyValue(obj, prop, propType);
				} catch (PropertyOutOfRangeException e) {
					handleException(obj, e);
					return null;
				} catch (PropertyNotPresentException | CoreException e) {
					return null;
				}
			}
			return ret;
		}

		private String handlePropertyValue(NamedElement obj, Property prop, PropertyType propType)
				throws PropertyOutOfRangeException, CoreException {
			if (propType == PropertyType.ENUM)
				return PropertyUtils.getEnumLiteral(obj, prop).getName();
			else if (propType == PropertyType.INT) {
				return getStringFromScaledNumber(
						PropertyUtils.getScaledNumberValue(obj, prop, GetProperties.findUnitLiteral(prop, "ms")), obj,
						prop);
			} else if (propType == PropertyType.RANGE_MIN) {
				return getStringFromScaledNumber(
						PropertyUtils.getScaledRangeMinimum(obj, prop, GetProperties.findUnitLiteral(prop, "ms")), obj,
						prop);
			} else if (propType == PropertyType.RANGE_MAX) {
				return getStringFromScaledNumber(
						PropertyUtils.getScaledRangeMaximum(obj, prop, GetProperties.findUnitLiteral(prop, "ms")), obj,
						prop);
			} else if (propType == PropertyType.STRING) {
				return PropertyUtils.getStringValue(obj, prop);
			} else if (propType == PropertyType.BOOLEAN) {
				return String.valueOf(PropertyUtils.getBooleanValue(obj, prop));
			} else {
				throw new CoreException("HandlePropertyValue called with garbage propType: " + propType);
			}
		}

		private String getStringFromScaledNumber(double num, NamedElement obj, Property prop)
				throws PropertyOutOfRangeException {
			if (num == (int) Math.rint(num))
				return String.valueOf((int) Math.rint(num));
			else
				throw new PropertyOutOfRangeException("Property " + prop.getName() + " on element " + obj.getName()
						+ " converts to " + num + " ms, which cannot be converted to an integer");
		}

		/*-
		private void handleCallSequence(String taskName,
				EList<CallSpecification> calls) {
			TaskModel task = componentModel.getTask(taskName);
			SubprogramCall call;
			SubprogramImplementation subProgramImpl;
			for (CallSpecification callSpec : calls) {
				call = (SubprogramCall) callSpec;
				subProgramImpl = (SubprogramImplementation) call
						.getCalledSubprogram();
				task.addCalledMethod(call.getName(),
						subProgramImpl.getTypeName());
			}
		}
		 */

		private void handleProcessPortConnection(PortConnection obj) {
			String taskName, localName, portName;
			TaskModel task;
			ProcessConnectionModel connModel = new ProcessConnectionModel();
			ProcessModel procModel = (ProcessModel) componentModel;
			try {
				if (obj.getAllSource().getOwner() instanceof ThreadType
						&& obj.getAllDestination().getOwner() instanceof ProcessType) {
					// From thread to process
					taskName = ((ThreadType) obj.getAllSource().getOwner()).getName();
					localName = obj.getAllSource().getName();
					portName = obj.getAllDestination().getName();
					task = procModel.getChild(taskName);

					connModel.setName(obj.getName());
					connModel.setProcessToThread(false);
					connModel.setPublisher(task);
					connModel.setPubName(taskName);
					connModel.setPubPortName(localName);
					connModel.setSubscriber(procModel);
					connModel.setSubName(procModel.getName());
					connModel.setSubPortName(portName);
					procModel.addConnection(connModel.getName(), connModel);
				} else if (obj.getAllSource().getOwner() instanceof ProcessType
						&& obj.getAllDestination().getOwner() instanceof ThreadType) {
					// From process to thread
					taskName = ((ThreadType) obj.getAllDestination().getOwner()).getName();
					localName = obj.getAllDestination().getName();
					portName = obj.getAllSource().getName();
					task = procModel.getChild(taskName);
					task.setTrigPort(procModel.getPortByName(portName));
					task.setTrigPortLocalName(localName);

					connModel.setName(obj.getName());
					connModel.setProcessToThread(true);
					connModel.setPublisher(procModel);
					connModel.setPubName(procModel.getName());
					connModel.setPubPortName(portName);
					connModel.setSubscriber(task);
					connModel.setSubName(taskName);
					connModel.setSubPortName(localName);
					procModel.addConnection(connModel.getName(), connModel);
				} else if (obj.getAllSource().getOwner() instanceof ThreadType
						&& obj.getAllDestination().getOwner() instanceof ThreadType) {
					// From thread to thread
					throw new NotImplementedException("Thread <--> thread communication is handled via shared state");
				} else if (obj.getAllSource().getOwner() instanceof ThreadType
						&& obj.getAllDestination().getOwner() instanceof ThreadType) {
					// From process port to other process port
					throw new NotImplementedException(
							"Direct pass-through is not supported, connect the ports directly at the system level");
				}
			} catch (NotImplementedException e) {
				handleException(obj, e);
				return;
			}
		}

		private void handleSystemPortConnection(PortConnection obj) {
			try {
				if (obj.isBidirectional())
					throw new NotImplementedException("Bidirectional ports are not yet allowed.");
			} catch (NotImplementedException e) {
				handleException(obj, e);
				return;
			}

			String pubTypeName, pubPortName, subPortName, subTypeName, pubName, subName;
			DevOrProcModel pubModel = null, subModel = null;
			SystemConnectionModel connModel = new SystemConnectionModel();

			String channelDelay = null;

			pubName = obj.getAllSourceContext().getName();
			subName = obj.getAllDestinationContext().getName();
			pubPortName = obj.getAllSource().getName();
			subPortName = obj.getAllDestination().getName();

			try {
				if ((obj.getAllSource().getOwner() instanceof DeviceType)
						&& (obj.getAllDestination().getOwner() instanceof ProcessType)) {
					// From device to process
					pubTypeName = ((DeviceType) obj.getAllSource().getOwner()).getName();
					subTypeName = ((ProcessType) obj.getAllDestination().getOwner()).getName();
					pubModel = systemModel.getDeviceByType(pubTypeName);
					subModel = systemModel.getProcessByType(subTypeName);
					connModel.setDevicePublished(true);
					connModel.setDeviceSubscribed(false);
				} else if ((obj.getAllSource().getOwner() instanceof ProcessType)
						&& (obj.getAllDestination().getOwner() instanceof DeviceType)) {
					// From process to device
					pubTypeName = ((ProcessType) obj.getAllSource().getOwner()).getName();
					subTypeName = ((DeviceType) obj.getAllDestination().getOwner()).getName();
					pubModel = systemModel.getProcessByType(pubTypeName);
					subModel = systemModel.getDeviceByType(subTypeName);
					connModel.setDevicePublished(false);
					connModel.setDeviceSubscribed(true);
				} else if ((obj.getAllSource().getOwner() instanceof ProcessType)
						&& (obj.getAllDestination().getOwner() instanceof ProcessType)) {
					// From process to process
					pubTypeName = ((ProcessType) obj.getAllSource().getOwner()).getName();
					subTypeName = ((ProcessType) obj.getAllDestination().getOwner()).getName();
					pubModel = systemModel.getProcessByType(pubTypeName);
					subModel = systemModel.getProcessByType(subTypeName);
					connModel.setDevicePublished(false);
					connModel.setDeviceSubscribed(false);
				} else {
					throw new NotImplementedException("Device to device connections are not yet allowed.");
				}
				channelDelay = handleOverridableProperty(obj, "Default_Channel_Delay", "MAP_Properties",
						"Channel_Delay", PropertyType.INT);
				if (channelDelay == null)
					throw new MissingRequiredPropertyException("Missing required property 'Default_Channel_Delay'");
			} catch (NotImplementedException | MissingRequiredPropertyException e) {
				handleException(obj, e);
				return;
			}

			connModel.setPublisher(pubModel);
			connModel.setSubscriber(subModel);
			connModel.setPubName(pubName);
			connModel.setSubName(subName);
			connModel.setPubPortName(pubPortName);
			connModel.setSubPortName(subPortName);
			connModel.setChannelDelay(Integer.valueOf(channelDelay));
			connModel.setName(obj.getName());
			systemModel.addConnection(obj.getName(), connModel);
		}

		/*-
		private void handleSubprogramDataConnection(AccessConnection obj) {
			// TODO: This method currently creates methods as necessary --
			// instead, they should be declared at the process level and
			// initialized ahead of time
			String parentName, internalName, formalParam, actualParam;
			TaskModel task;
			ProcessModel pm;
			try {
				if (componentModel instanceof ProcessModel)
					pm = (ProcessModel) componentModel;
				else
					throw new NotImplementedException(
							"Attempted to add subprogram data connection to unsupported component "
									+ componentModel.getName());
			} catch (NotImplementedException e) {
				handleException(obj, e);
				return;
			}
		
			// A passed parameter: From thread to method
			if (obj.getAllSource().getOwner() instanceof ThreadType) {
				formalParam = obj.getAllDestination().getName();
				actualParam = obj.getAllSource().getName();
				internalName = obj.getAllDestinationContext().getName();
				parentName = ((ThreadType) obj.getAllSource().getOwner())
						.getName();
				task = componentModel.getTask(parentName);
				String paramType = null;
				String methodName = task.getMethodProcessName(internalName);
				for (DataAccess data : ((SubprogramType) obj
						.getAllDestination().getOwner()).getOwnedDataAccesses()) {
					if (data.getName().equals(formalParam)) {
						try {
							if (componentModel instanceof ProcessModel)
								pm = (ProcessModel) componentModel;
							else
								throw new NotImplementedException(
										"Attempted to add subprogram data connection to unsupported component "
												+ componentModel.getName());
							Property prop = GetProperties
									.lookupPropertyDefinition(
											data.getDataFeatureClassifier(),
											DataModel._NAME,
											DataModel.Data_Representation);
							paramType = getJavaType(PropertyUtils
									.getEnumLiteral(data, prop).getName());
							pm.addParameterToMethod(methodName, formalParam,
									paramType);
						} catch (NotImplementedException
								| DuplicateElementException e) {
							handleException(obj, e);
							return;
						}
					}
				}
				task.addParameterToCalledMethod(internalName, formalParam,
						actualParam);
			} else { // A returned value: From method to thread
				parentName = ((ThreadType) obj.getAllDestination().getOwner())
						.getName();
				task = componentModel.getTask(parentName);
				internalName = obj.getAllSourceContext().getName();
				String methodName = task.getMethodProcessName(internalName);
				String returnType = null;
				formalParam = obj.getAllSource().getName();
				for (DataAccess data : ((SubprogramType) obj.getAllSource()
						.getOwner()).getOwnedDataAccesses()) {
					if (data.getName().equals(formalParam)) {
						try {
							Property prop = GetProperties
									.lookupPropertyDefinition(
											data.getDataFeatureClassifier(),
											DataModel._NAME,
											DataModel.Data_Representation);
							returnType = getJavaType(PropertyUtils
									.getEnumLiteral(data, prop).getName());
						} catch (NotImplementedException e) {
							handleException(obj, e);
							return;
						}
					}
				}
				pm.addReturnToMethod(methodName, returnType);
			}
		}
		 */

		/*-
		private void handleProcessDataConnection(AccessConnection obj) {
			String parentName;
			TaskModel task;
			VariableModel vm = new VariableModel();
			String srcName = obj.getAllSource().getName();
			String dstName = obj.getAllDestination().getName();
			ProcessModel pm;
			try {
				if (componentModel instanceof ProcessModel)
					pm = (ProcessModel) componentModel;
				else
					throw new NotImplementedException(
							"Attempted to add process data connection to unsupported component "
									+ componentModel.getName());
			} catch (NotImplementedException e) {
				handleException(obj, e);
				return;
			}
			// From thread to process
			if (obj.getAllSource().getOwner() instanceof ThreadType) { 
				parentName = ((ThreadType) obj.getAllSource().getOwner())
						.getName();
				task = pm.getTask(parentName);
				vm.setOuterName(dstName);
				vm.setInnerName(srcName);
				vm.setType(pm.getGlobalType(dstName));
				task.addOutGlobal(vm);
			} else { // From process to thread
				parentName = ((ThreadType) obj.getAllDestination().getOwner())
						.getName();
				task = pm.getTask(parentName);
				vm.setOuterName(srcName);
				vm.setInnerName(dstName);
				vm.setType(pm.getGlobalType(srcName));
				task.addIncGlobal(vm);
			}
		}
		 */

		@Override
		public String casePortConnection(PortConnection obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				handleProcessPortConnection(obj);
			} else if (lastElemProcessed == ElementType.SYSTEM) {
				handleSystemPortConnection(obj);
			}
			return NOT_DONE;
		}

		private FeatureModel resolvePortModel(ComponentModel<?, ?> model, String portName, boolean isIn) {
			FeatureModel pm = model.getFeatureByName(portName);

			// Ideally we'll just have the portname as planned
			if (pm != null) {
				return pm;
			} else if (model instanceof DeviceModel) {
				// Since devices get turned into pseudodevices, with both in and
				// out ports, we only want the propagation to map to the port
				// that interacts with our system
				if (isIn) {
					return model.getPortByName(portName + "In");
				} else {
					return model.getPortByName(portName + "Out");
				}
			}

			return null;
		}
	}

	public Translator(final IProgressMonitor monitor) {
		super(monitor, PROCESS_PRE_ORDER_ALL);
	}

	@Override
	protected final void initSwitches() {
		aadl2Switch = new TranslatorSwitch();
	}

	public SystemModel getSystemModel() {
		return systemModel;
	}

	public void setErrorManager(ParseErrorReporterManager parseErrManager) {
		errorManager = parseErrManager;
	}

	public void addPropertySetName(String propSetName) {
		propertySetNames.add(propSetName);
	}

	public SystemImplementation getSystemImplementation() {
		return sysImpl;
	}

	public Map<ComponentModel<?, ?>, ComponentClassifier> getChildren() {
		return children;
	}

	public String getTarget() {
		return target.name();
	}

	public void setTarget(String target) {
		this.target = TranslationTarget.valueOf(target.toUpperCase());
	}

	public void setErrorInfo(HashSet<NamedElement> errorTypes) {

		// Run these sequentially so that all error types get processed before
		// any sets
		for (NamedElement ets : errorTypes) {
			if (ets instanceof ErrorType) {
				handleErrorType((ErrorType) ets);
			}
		}
		for (NamedElement ets : errorTypes) {
			if (ets instanceof TypeSet) {
				handleErrorTypeSet((TypeSet) ets);
			}
			if (ets instanceof ErrorBehaviorStateMachine) {
				handleErrorBehaviorStateMachine((ErrorBehaviorStateMachine) ets);
			}
		}
	}

	private void handleErrorBehaviorStateMachine(ErrorBehaviorStateMachine ets) {
		ErrorBehaviorModel ebm = new ErrorBehaviorModel();
		for (ErrorBehaviorState state : ets.getStates()) {
			ebm.addState(state.getName());
		}
		errBehaviorMachines.put(ets.getName(), ebm);
	}

	private void handleErrorType(ErrorType et) {
		errorTypeModels.put(et.getName(), new ManifestationTypeModel(et.getName(), et.getSuperType()));
	}

	private void handleErrorTypeSet(TypeSet ts) {
		HashMap<String, ManifestationTypeModel> typeSet = new HashMap<>();
		for (TypeToken tt : ts.getTypeTokens()) {
			typeSet.put(EMV2Util.getName(tt), errorTypeModels.get(EMV2Util.getName(tt)));
		}
		errorTypeSets.put(ts.getName(), typeSet);
	}
}
