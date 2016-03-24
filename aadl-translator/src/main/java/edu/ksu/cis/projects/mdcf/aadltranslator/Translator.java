package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.BasicEList;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.ComponentClassifier;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DeviceSubcomponent;
import org.osate.aadl2.DeviceType;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.Element;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.ListValue;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyExpression;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.RecordValue;
import org.osate.aadl2.StringLiteral;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.ThreadImplementation;
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
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorPropagation;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;
import org.osate.xtext.aadl2.errormodel.errorModel.TypeToken;
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
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ModelUtil.ManifestationType;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessConnectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemConnectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.TaskModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.ErrorTypeModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.PropagationModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.hazardanalysis.StpaPreliminaryModel;

public final class Translator extends AadlProcessingSwitchWithProgress {
	private enum ElementType {
		SYSTEM, PROCESS, THREAD, SUBPROGRAM, DEVICE, NONE
	};

	private enum TranslationTarget {
		SYSTEM, PROCESS, DEVICE
	};

	private enum PropertyType {
		ENUM, INT, RANGE_MIN, RANGE_MAX, STRING
	};

	private TranslationTarget target = null;
	private SystemModel systemModel = null;
	private ArrayList<String> propertySetNames = new ArrayList<>();
	private ParseErrorReporterManager errorManager;
	public SystemImplementation sysImpl;
	@SuppressWarnings("rawtypes")
	private Map<ComponentModel, ComponentClassifier> children = new HashMap<>();

	/**
	 * Error type name -> model
	 */
	private Map<String, ErrorTypeModel> errorTypeModels = new HashMap<>();

	public class TranslatorSwitch extends Aadl2Switch<String> {

		/**
		 * A reference to the "current" component model, stored for convenience
		 */
		@SuppressWarnings("rawtypes")
		private ComponentModel componentModel = null;

		/**
		 * A reference to the current component's parent
		 */
		@SuppressWarnings("rawtypes")
		private ComponentModel parentModel = null;

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
			return NOT_DONE;
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
				systemModel = new SystemModel();
				systemModel.setName("Process_Stub_System");
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
				componentModel.addPort(pm);
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
			if (!obj.getOwnedSubcomponents().isEmpty())
				processEList(obj.getOwnedSubcomponents());
			if (obj instanceof ThreadImplementation
					&& !((((ThreadImplementation) obj).getOwnedSubprogramCallSequences()).isEmpty()))
				processEList(((ThreadImplementation) obj).getOwnedSubprogramCallSequences());
			if (!obj.getOwnedConnections().isEmpty())
				processEList(obj.getOwnedConnections());
			if (!obj.getOwnedPropertyAssociations().isEmpty())
				processEList(obj.getOwnedPropertyAssociations());
			return DONE;
		}

		/*-@Override
		public String caseComponentClassifier(ComponentClassifier obj) {
			handlePropagations(obj);
			return NOT_DONE;
		}*/

		public String caseAnnexSubclause(AnnexSubclause obj) {

			handlePropagations((ComponentClassifier) obj.getContainingClassifier());
			return NOT_DONE;
		}

		private void handlePropagations(ComponentClassifier obj) {
			String portName;
			PortModel portModel;
			Set<ErrorTypeModel> errorTypes;
			ErrorTypeModel etm;

			// Why can't I just get all the error propagations at once?
			Set<ErrorPropagation> eProps = new HashSet<>();
			eProps.addAll(EMV2Util.getAllErrorPropagations(obj, DirectionType.IN));
			eProps.addAll(EMV2Util.getAllErrorPropagations(obj, DirectionType.OUT));
			if (eProps.isEmpty())
				return;
			for (ErrorPropagation eProp : eProps) {
				errorTypes = new LinkedHashSet<>();
				for (TypeToken tt : eProp.getTypeSet().getTypeTokens()) {
					// We assume that there are no pre-defined sets of error
					// types allowed ie, only allow ad-hoc, anonymous sets
					// created in propagations
					etm = systemModel.getErrorTypeModelByName(tt.getType().get(0).getName());
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
					// have been logged already.
					// We bail out to avoid an NPE -- the error log should
					// reflect the root exception
					return;
				}

				try {
					for (ErrorTypeModel errorType : errorTypes) {
						portModel.addPropagation(new PropagationModel(errorType));
					}
				} catch (DuplicateElementException e) {
					handleException(obj, e);
					return;
				}
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
				} catch (PropertyNotPresentException e) {
					return null;
				}
			}
			return ret;
		}

		private String handlePropertyValue(NamedElement obj, Property prop, PropertyType propType)
				throws PropertyOutOfRangeException {
			if (propType == PropertyType.ENUM)
				return PropertyUtils.getEnumLiteral(obj, prop).getName();
			else if (propType == PropertyType.INT) {
				// Should you ever need to get the unit of a property, this is
				// how you can do it. This example needs a better home, but it
				// took me so long to figure out that I can't just delete it.
				//
				// NumberValue nv =
				// (NumberValue)PropertyUtils.getSimplePropertyValue(obj, prop);
				// nv.getUnit()

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
			} else {
				System.err.println("HandlePropertyValue called with garbage propType: " + propType);
			}
			return null;
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

		private void handleExplanations(RecordValue rv, StpaPreliminaryModel spm) {
			ListValue lv = (ListValue) PropertyUtils.getRecordFieldValue(rv, "Explanations");

			if (lv == null) {
				return;
			}

			for (PropertyExpression pe : lv.getOwnedListElements()) {
				spm.addExplanation(((StringLiteral) pe).getValue());
			}
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

		private PortModel resolvePortModel(ComponentModel<?, ?> model, String portName, boolean isIn) {
			PortModel pm = model.getPortByName(portName);

			// Ideally we'll just have the portname as planned
			if (pm != null) {
				return pm;
			} else if (model instanceof DeviceModel) {
				// Since devices get turned into pseudodevices, with both in and
				// out
				// ports, we only want the propagation to map to the port that
				// interacts with our system
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

	@SuppressWarnings("rawtypes")
	public Map<ComponentModel, ComponentClassifier> getChildren() {
		return children;
	}

	public String getTarget() {
		return target.name();
	}

	public void setTarget(String target) {
		this.target = TranslationTarget.valueOf(target.toUpperCase());
	}

	public void setErrorTypes(HashSet<ErrorType> errorTypes) {
		for (ErrorType et : errorTypes) {
			handleErrorType(et);
		}
	}

	private void handleErrorType(ErrorType et) {
		errorTypeModels.put(et.getName(), new ErrorTypeModel(et.getName(), et.getSuperType()));
	}
}
