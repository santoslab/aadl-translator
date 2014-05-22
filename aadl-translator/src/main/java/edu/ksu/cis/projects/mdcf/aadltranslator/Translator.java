package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.ArrayList;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.nodemodel.util.NodeModelUtils;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DataPort;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.DeviceSubcomponent;
import org.osate.aadl2.DeviceType;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.Element;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.SubprogramCallSequence;
import org.osate.aadl2.SubprogramType;
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
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.CoreException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.DuplicateElementException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.MissingRequiredPropertyException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.PropertyOutOfRangeException;
import edu.ksu.cis.projects.mdcf.aadltranslator.exception.UseBeforeDeclarationException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ComponentModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ConnectionModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.DeviceModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.PortModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.TaskModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.VariableModel;

public final class Translator extends AadlProcessingSwitchWithProgress {
	private enum ElementType {
		SYSTEM, PROCESS, THREAD, SUBPROGRAM, DEVICE, NONE
	};

	private SystemModel systemModel = null;
	private ArrayList<String> propertySetNames = new ArrayList<>();
	private ParseErrorReporterManager errorManager;

	public class TranslatorSwitch extends Aadl2Switch<String> {
		/**
		 * A reference to the "current" process model, stored for convenience
		 */
		private ComponentModel componentModel = null;
		private ElementType lastElemProcessed = ElementType.NONE;

		private String DONE = "Done";
		private String NOT_DONE = null;

		@Override
		public String caseSystem(org.osate.aadl2.System obj) {
			try {
				if (systemModel != null)
					throw new NotImplementedException("Got a system called "
							+ obj.getName() + " but I already have one called "
							+ systemModel.getName());
			} catch (NotImplementedException e) {
				handleException(obj, e);
				return DONE;
			}
			systemModel = new SystemModel();
			systemModel.setName(obj.getName());
			lastElemProcessed = ElementType.SYSTEM;
			return NOT_DONE;
		}

		@Override
		public String caseThreadSubcomponent(ThreadSubcomponent obj) {
			try {
				if (componentModel instanceof ProcessModel)
					((ProcessModel) componentModel).addTask(obj.getName());
				else
					throw new CoreException(
							"Trying to add thread to non-process component "
									+ componentModel.getName());
			} catch (DuplicateElementException | CoreException e) {
				handleException(obj, e);
				return DONE;
			}
			return NOT_DONE;
		}

		@Override
		public String caseThreadType(ThreadType obj) {
			lastElemProcessed = ElementType.THREAD;
			handleThreadProperties(obj);
			return NOT_DONE;
		}

		@Override
		public String casePropertySet(PropertySet obj) {
			propertySetNames.add(obj.getName());
			return NOT_DONE;
		}

		@Override
		public String casePackageSection(PackageSection obj) {
			processEList(obj.getOwnedClassifiers());
			return DONE;
		}

		@Override
		public String caseDeviceSubcomponent(DeviceSubcomponent obj) {
			DeviceModel dm = new DeviceModel();
			dm.setName(obj.getComponentType().getName());
			try {
				systemModel.addDevice(obj.getName(), dm);
			} catch (DuplicateElementException e) {
				handleException(obj, e);
				return DONE;
			}
			return NOT_DONE;
		}

		@Override
		public String caseProcessSubcomponent(ProcessSubcomponent obj) {
			ProcessModel pm = new ProcessModel();
			pm.setName(obj.getComponentType().getName());
			try {
				systemModel.addProcess(obj.getName(), pm);
			} catch (DuplicateElementException e) {
				handleException(obj, e);
				return DONE;
			}
			return NOT_DONE;
		}

		@Override
		public String caseDeviceType(DeviceType obj) {
			try {
				if (systemModel.hasDeviceType(obj.getName())) {
					componentModel = systemModel.getDeviceByType(obj.getName());
				} else {
					throw new UseBeforeDeclarationException(
							"Attempted to define a device that wasn't declared as a system component");
				}

			} catch (UseBeforeDeclarationException e) {
				handleException(obj, e);
				return DONE;
			}
			lastElemProcessed = ElementType.DEVICE;
			processEList(obj.getOwnedElements());
			return NOT_DONE;
		}

		@Override
		public String caseProcessType(ProcessType obj) {
			ProcessModel pm;
			try {
				if (systemModel.hasProcessType(obj.getName())) {
					componentModel = systemModel
							.getProcessByType(obj.getName());
					pm = systemModel.getProcessByType(obj.getName());
				} else {
					throw new UseBeforeDeclarationException(
							"Attempted to define a process that wasn't declared as a system component");
				}

				if (checkCustomProperty(obj, "Component_Type", "enum",
						"Process Declaration") != null
						&& checkCustomProperty(obj, "Component_Type", "enum",
								"Process Declaration")
								.equalsIgnoreCase("logic")) {
					pm.setDisplay(false);
				} else if (checkCustomProperty(obj, "Component_Type", "enum",
						"Process Declaration") != null
						&& checkCustomProperty(obj, "Component_Type", "enum",
								"Process Declaration").equalsIgnoreCase(
								"display")) {
					pm.setDisplay(true);
				} else {
					throw new PropertyOutOfRangeException(
							"Processes must declare their component type to be either display or logic");
				}
			} catch (UseBeforeDeclarationException
					| PropertyOutOfRangeException e) {
				handleException(obj, e);
				return DONE;
			}
			lastElemProcessed = ElementType.PROCESS;
			processEList(obj.getOwnedElements());
			return NOT_DONE;
		}

		@Override
		public String casePort(Port obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				handlePort(obj);
			} else if (lastElemProcessed == ElementType.DEVICE) {
				handlePort(obj);
				handleImplicitTask(obj);
			}
			return NOT_DONE;
		}

		private void handlePort(Port obj) {
			// if (lastElemProcessed == ElementType.PROCESS) {
			String typeName = null, minPeriod = null, maxPeriod = null;
			Property typeNameProp = null;
			try {

				if (obj.getCategory() == PortCategory.EVENT_DATA) {
					typeNameProp = GetProperties.lookupPropertyDefinition(
							((EventDataPort) obj).getDataFeatureClassifier(),
							DataModel._NAME, DataModel.Data_Representation);
				} else if (obj.getCategory() == PortCategory.DATA) {
					typeNameProp = GetProperties.lookupPropertyDefinition(
							((DataPort) obj).getDataFeatureClassifier(),
							DataModel._NAME, DataModel.Data_Representation);
				} else if (obj.getCategory() == PortCategory.EVENT) {
					// Do nothing, we have an event port
				}
				try {
					if (typeNameProp != null) {
						typeName = getJavaType(PropertyUtils.getEnumLiteral(
								obj, typeNameProp).getName());
					} else {
						typeName = getJavaType(null);
					}
				} catch (PropertyNotPresentException e) {
					throw new MissingRequiredPropertyException(
							"Missing the required data representation");
				}

				minPeriod = checkCustomProperty(obj, "Default_Output_Rate",
						"range_min", "Port");
				maxPeriod = checkCustomProperty(obj, "Default_Output_Rate",
						"range_max", "Port");

				if (minPeriod == null || maxPeriod == null)
					throw new MissingRequiredPropertyException(
							"Missing the required output rate specification.");

				PortModel pm = new PortModel();
				pm.setName(obj.getName());
				pm.setType(typeName);
				pm.setMinPeriod(Integer.valueOf(minPeriod));
				pm.setMaxPeriod(Integer.valueOf(maxPeriod));
				if (obj.getDirection() == DirectionType.IN) {
					pm.setSubscribe(true);
				} else if (obj.getDirection() == DirectionType.OUT) {
					pm.setSubscribe(false);
				} else {
					throw new NotImplementedException(
							"Ports must be either in nor out");
				}
				if (obj.getCategory() == PortCategory.EVENT_DATA) {
					pm.setEventData();
				} else if (obj.getCategory() == PortCategory.DATA) {
					pm.setData();
				} else if (obj.getCategory() == PortCategory.EVENT) {
					pm.setEvent();
				}
				componentModel.addPort(pm);
			} catch (NotImplementedException | MissingRequiredPropertyException
					| DuplicateElementException e) {
				handleException(obj, e);
				return;
			}
			// }
		}

		private void handleException(Element obj, Exception e) {
			INode node = NodeModelUtils.findActualNodeFor(obj);
			IResource file = OsateResourceUtil.convertToIResource(obj
					.eResource());
			ParseErrorReporter errReporter = errorManager.getReporter(file);
			if (errReporter instanceof MarkerParseErrorReporter)
				((MarkerParseErrorReporter) errReporter).setContextResource(obj
						.eResource());
			errReporter.error(obj.eResource().getURI().lastSegment(),
					node.getStartLine(), e.getMessage());
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
			} else if (name.equals("Integer") || name.equals("Double")
					|| name.equals("Boolean")) {
				return name;
			} else {
				throw new NotImplementedException(
						"No java equivalent for type " + name);
			}
		}

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

		@Override
		public String caseAccessConnection(AccessConnection obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				handleProcessDataConnection(obj);
			} else if (lastElemProcessed == ElementType.THREAD) {
				handleSubprogramDataConnection(obj);
			}
			return NOT_DONE;
		}

		@Override
		public String caseAadlPackage(AadlPackage obj) {
			processEList(obj.getOwnedPublicSection().getChildren());
			return DONE;
		}

		@Override
		public String caseComponentImplementation(ComponentImplementation obj) {
			if (!obj.getOwnedSubcomponents().isEmpty())
				processEList(obj.getOwnedSubcomponents());
			if (obj instanceof ThreadImplementation
					&& !((((ThreadImplementation) obj)
							.getOwnedSubprogramCallSequences()).isEmpty()))
				processEList(((ThreadImplementation) obj)
						.getOwnedSubprogramCallSequences());
			if (!obj.getOwnedConnections().isEmpty())
				processEList(obj.getOwnedConnections());
			if (!obj.getOwnedPropertyAssociations().isEmpty())
				processEList(obj.getOwnedPropertyAssociations());
			return DONE;
		}

		@Override
		public String caseSubprogramType(SubprogramType obj) {
			lastElemProcessed = ElementType.SUBPROGRAM;
			return NOT_DONE;
		}

		@Override
		public String caseSubprogramCallSequence(SubprogramCallSequence obj) {
			// handleCallSequence(
			// ((ThreadImplementation) obj.getOwner()).getTypeName(),
			// obj.getOwnedCallSpecifications());
			return NOT_DONE;
		}

		private void handleImplicitTask(Port obj) {
			TaskModel tm;
			String taskName = obj.getName() + "Task";
			// Default values; period is set to -1 since these tasks are all
			// sporadic
			int period = -1, deadline = 50, wcet = 5;
			try {
				componentModel.addTask(taskName);
				tm = componentModel.getTask(taskName);
				tm.setSporadic(true);
				tm.setPeriod(period);
				tm.setDeadline(deadline);
				tm.setWcet(wcet);
				tm.setTrigPortInfo(obj.getName(),
						componentModel.getPortByName(obj.getName()).getType(),
						obj.getName(), true);
			} catch (DuplicateElementException | NotImplementedException e) {
				handleException(obj, e);
				return;
			}
		}

		private void handleThreadProperties(ThreadType obj) {
			try {
				String trigType = handleThreadProperty(obj,
						"Default_Thread_Dispatch", "Thread_Properties",
						"Dispatch_Protocol", "enum");
				String period = handleThreadProperty(obj,
						"Default_Thread_Period", "Timing_Properties", "Period",
						"int");
				String deadline = handleThreadProperty(obj,
						"Default_Thread_Deadline", "Timing_Properties",
						"Deadline", "int");
				String wcet = handleThreadProperty(obj, "Default_Thread_WCET",
						"Timing_Properties", "Compute_Execution_Time", "int");
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
					if (componentModel.getTask(obj.getName()) == null) {
						throw new UseBeforeDeclarationException(
								"Threads must be declared as subcomponents before being defined");
					}
					if (trigType.equalsIgnoreCase("sporadic")) {
						componentModel.getTask(obj.getName()).setSporadic(true);
					} else if (trigType.equalsIgnoreCase("periodic")) {
						componentModel.getTask(obj.getName())
								.setSporadic(false);
					} else {
						throw new NotImplementedException(
								"Thread dispatch must be either sporadic or periodic, instead got "
										+ trigType);
					}
					componentModel.getTask(obj.getName()).setPeriod(
							Integer.valueOf(period));
					componentModel.getTask(obj.getName()).setDeadline(
							Integer.valueOf(deadline));
					componentModel.getTask(obj.getName()).setWcet(
							Integer.valueOf(wcet));
				}
			} catch (MissingRequiredPropertyException | NotImplementedException
					| UseBeforeDeclarationException e) {
				handleException(obj, e);
				return;
			}

		}

		private String handleThreadProperty(NamedElement obj,
				String defaultName, String overridePropertySet,
				String overrideName, String propType) {
			Property prop = GetProperties.lookupPropertyDefinition(obj,
					overridePropertySet, overrideName);
			String ret = null;

			// This try / catch (and the nested one in the for loop) are here
			// because I can't just check if a property exists -- instead, I
			// have to just try and check for a PropertyNotPresentException,
			// which makes for super clumsy code.
			try {
				ret = handlePropertyValue(obj, prop, propType);
			} catch (PropertyNotPresentException e) {
				ret = checkCustomProperty(obj, defaultName, propType, "Thread");
			} catch (PropertyOutOfRangeException e) {
				handleException(obj, e);
				return null;
			}
			return ret;
		}

		private String checkCustomProperty(NamedElement obj,
				String propertyName, String propType, String elementType) {
			String ret = null;
			Property prop;
			for (String propertySetName : propertySetNames) {
				try {
					prop = GetProperties.lookupPropertyDefinition(obj,
							propertySetName, propertyName);
					if (prop == null)
						continue;
					else
						ret = handlePropertyValue(obj, prop, propType);
				} catch (PropertyOutOfRangeException e) {
					handleException(obj, e);
					return null;
				}
			}
			return ret;
		}

		private String handlePropertyValue(NamedElement obj, Property prop,
				String propType) throws PropertyOutOfRangeException {
			if (propType.equals("enum"))
				return PropertyUtils.getEnumLiteral(obj, prop).getName();
			else if (propType.equals("int")) {
				// Should you ever need to get the unit of a property, this is
				// how you can do it. This example needs a better home, but it
				// took me so long to figure out that I can't just delete it.
				//
				// NumberValue nv =
				// (NumberValue)PropertyUtils.getSimplePropertyValue(obj, prop);
				// nv.getUnit()

				return getStringFromScaledNumber(
						PropertyUtils.getScaledNumberValue(obj, prop,
								GetProperties.findUnitLiteral(prop, "ms")),
						obj, prop);
			} else if (propType.equals("range_min")) {
				return getStringFromScaledNumber(
						PropertyUtils.getScaledRangeMinimum(obj, prop,
								GetProperties.findUnitLiteral(prop, "ms")),
						obj, prop);
			} else if (propType.equals("range_max")) {
				return getStringFromScaledNumber(
						PropertyUtils.getScaledRangeMaximum(obj, prop,
								GetProperties.findUnitLiteral(prop, "ms")),
						obj, prop);
			} else {
				System.err
						.println("HandlePropertyValue called with garbage propType: "
								+ propType);
			}
			return null;
		}

		private String getStringFromScaledNumber(double num, NamedElement obj,
				Property prop) throws PropertyOutOfRangeException {
			if (num == (int) Math.rint(num))
				return String.valueOf((int) Math.rint(num));
			else
				throw new PropertyOutOfRangeException("Property "
						+ prop.getName() + " on element " + obj.getName()
						+ " converts to " + num
						+ " ms, which cannot be converted to an integer");
		}

		// private void handleCallSequence(String taskName,
		// EList<CallSpecification> calls) {
		// TaskModel task = componentModel.getTask(taskName);
		// SubprogramCall call;
		// SubprogramImplementation subProgramImpl;
		// for (CallSpecification callSpec : calls) {
		// call = (SubprogramCall) callSpec;
		// subProgramImpl = (SubprogramImplementation) call
		// .getCalledSubprogram();
		// task.addCalledMethod(call.getName(),
		// subProgramImpl.getTypeName());
		// }
		// }

		private void handleProcessPortConnection(PortConnection obj) {
			String taskName, localName, portName, portType;
			TaskModel task;
			if (obj.getAllSource().getOwner() instanceof ThreadType) {
				// From thread to process
				taskName = ((ThreadType) obj.getAllSource().getOwner())
						.getName();
				localName = obj.getAllSource().getName();
				portName = obj.getAllDestination().getName();
				portType = componentModel.getPortByName(portName).getType();
				task = componentModel.getTask(taskName);
			} else {
				// From process to thread
				boolean eventTriggered;
				taskName = ((ThreadType) obj.getAllDestination().getOwner())
						.getName();
				localName = obj.getAllDestination().getName();
				portName = obj.getAllSource().getName();
				task = componentModel.getTask(taskName);
				eventTriggered = componentModel.getPortByName(portName)
						.isEvent();
				portType = componentModel.getPortByName(portName).getType();
				try {
					task.setTrigPortInfo(portName, portType, localName,
							eventTriggered);
				} catch (NotImplementedException e) {
					handleException(obj, e);
					return;
				}

			}
		}

		private void handleSystemPortConnection(PortConnection obj) {
			try {
				if (obj.isBidirectional())
					throw new NotImplementedException(
							"Bidirectional ports are not yet allowed.");
			} catch (NotImplementedException e) {
				handleException(obj, e);
				return;
			}
			String pubTypeName, pubPortName, subPortName, subTypeName, pubName, subName;
			ComponentModel pubModel = null, subModel = null;
			ConnectionModel connModel = new ConnectionModel();
			String channelDelay = null;
			try {
				if (obj.getAllSource().getOwner() instanceof DeviceType) {
					// From device to process
					pubTypeName = ((DeviceType) obj.getAllSource().getOwner())
							.getName();
					subTypeName = ((ProcessType) obj.getAllDestination()
							.getOwner()).getName();
					pubModel = systemModel.getDeviceByType(pubTypeName);
					subModel = systemModel.getProcessByType(subTypeName);
					connModel.setDevicePublished(true);
					connModel.setDeviceSubscribed(false);
				} else if (obj.getAllDestination().getOwner() instanceof DeviceType) {
					// From process to device
					pubTypeName = ((ProcessType) obj.getAllSource().getOwner())
							.getName();
					subTypeName = ((DeviceType) obj.getAllDestination()
							.getOwner()).getName();
					pubModel = systemModel.getProcessByType(pubTypeName);
					subModel = systemModel.getDeviceByType(subTypeName);
					connModel.setDevicePublished(false);
					connModel.setDeviceSubscribed(true);
				} else if ((obj.getAllSource().getOwner() instanceof ProcessType)
						&& (obj.getAllDestination().getOwner() instanceof ProcessType)) {
					// From process to process
					pubTypeName = ((ProcessType) obj.getAllSource().getOwner())
							.getName();
					subTypeName = ((ProcessType) obj.getAllDestination()
							.getOwner()).getName();
					pubModel = systemModel.getProcessByType(pubTypeName);
					subModel = systemModel.getProcessByType(subTypeName);
					connModel.setDevicePublished(false);
					connModel.setDeviceSubscribed(false);
				} else {
					throw new NotImplementedException(
							"Device to device connections are not yet allowed.");
				}
				channelDelay = checkCustomProperty(obj,
						"Default_Channel_Delay", "int",
						"System-level port connection");
				if (channelDelay == null)
					throw new MissingRequiredPropertyException(
							"Missing required property 'Default_Channel_Delay'");
			} catch (NotImplementedException | MissingRequiredPropertyException e) {
				handleException(obj, e);
				return;
			}

			pubName = obj.getAllSourceContext().getName();
			subName = obj.getAllDestinationContext().getName();
			pubPortName = obj.getAllSource().getName();
			subPortName = obj.getAllDestination().getName();
			connModel.setPublisher(pubModel);
			connModel.setSubscriber(subModel);
			connModel.setPubName(pubName);
			connModel.setSubName(subName);
			connModel.setPubPortName(pubPortName);
			connModel.setSubPortName(subPortName);
			connModel.setChannelDelay(Integer.valueOf(channelDelay));
			systemModel.addConnection(connModel);

		}

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
			if (obj.getAllSource().getOwner() instanceof ThreadType) {
				// A passed parameter: From thread to method
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
			} else {
				// A returned value: From method to thread
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
			if (obj.getAllSource().getOwner() instanceof ThreadType) {
				// From thread to process
				parentName = ((ThreadType) obj.getAllSource().getOwner())
						.getName();
				task = pm.getTask(parentName);
				vm.setOuterName(dstName);
				vm.setInnerName(srcName);
				vm.setType(pm.getGlobalType(dstName));
				task.addOutGlobal(vm);
			} else {
				// From process to thread
				parentName = ((ThreadType) obj.getAllDestination().getOwner())
						.getName();
				task = pm.getTask(parentName);
				vm.setOuterName(srcName);
				vm.setInnerName(dstName);
				vm.setType(pm.getGlobalType(srcName));
				task.addIncGlobal(vm);
			}
		}

		@Override
		public String casePortConnection(PortConnection obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				handleProcessPortConnection(obj);
			} else if (lastElemProcessed == ElementType.SYSTEM) {
				handleSystemPortConnection(obj);
			}
			return NOT_DONE;
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
}
