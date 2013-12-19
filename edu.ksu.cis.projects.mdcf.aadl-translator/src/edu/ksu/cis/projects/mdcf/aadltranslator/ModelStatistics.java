package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.common.util.EList;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.Data;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.DeviceImplementation;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PortSpecification;
import org.osate.aadl2.Process;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.Property;
import org.osate.aadl2.PropertyAssociation;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.Type;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.properties.InvalidModelException;
import org.osate.aadl2.properties.PropertyDoesNotApplyToHolderException;
import org.osate.aadl2.properties.PropertyIsListException;
import org.osate.aadl2.properties.PropertyIsModalException;
import org.osate.aadl2.properties.PropertyNotPresentException;
import org.osate.aadl2.util.Aadl2Switch;
import org.osate.contribution.sei.names.DataModel;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.TaskModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.VariableModel;

public final class ModelStatistics extends AadlProcessingSwitchWithProgress {
	private enum ElementType {
		SYSTEM, PROCESS, THREAD, NONE
	};

	public class MyAadl2Switch extends Aadl2Switch<String> {
		private ArrayList<ProcessModel> processModels = new ArrayList<>();
		private ElementType lastElemProcessed = ElementType.NONE;

		private String DONE = "Done";
		private String NOT_DONE = null;

		@Override
		public String caseSystemImplementation(SystemImplementation obj) {
			System.out.println("System: " + obj.getName());
			lastElemProcessed = ElementType.SYSTEM;
			return NOT_DONE;
		}

		@Override
		public String caseThreadSubcomponent(ThreadSubcomponent obj) {
			System.out.println("ThreadSubcomponent: " + obj.getName());
			processModels.get(processModels.size() - 1).addTask(obj.getName());
			return NOT_DONE;
		}

		@Override
		public String caseThreadType(ThreadType obj) {
			System.out.println("Thread: " + obj.getName());
			lastElemProcessed = ElementType.THREAD;
			return NOT_DONE;
		}

		@Override
		public String caseThreadImplementation(ThreadImplementation obj) {
			System.out.println("ThreadImplementation: " + obj.getName());
			return NOT_DONE;
		}

		public String casePackageSection(PackageSection obj) {
			processEList(obj.getOwnedClassifiers());
			return DONE;
		}

		@Override
		public String caseDeviceImplementation(DeviceImplementation obj) {
			System.out.println("Device: " + obj.getName());
			return NOT_DONE;
		}

		@Override
		public String caseProcessType(ProcessType obj) {
			System.out.println("Process: " + obj.getName());
			processModels.add(new ProcessModel());
			processModels.get(processModels.size() - 1).setObjectName(
					obj.getName());
			lastElemProcessed = ElementType.PROCESS;
			processEList(obj.getOwnedElements());
			return NOT_DONE;
		}

		@Override
		public String caseProcessImplementation(ProcessImplementation obj) {
			System.out.println("ProcessImplementation: " + obj.getName());
			return NOT_DONE;
		}
		
		@Override
		public String caseProperty(Property obj) {
			System.out.println("Property: " + obj.getName());
			return NOT_DONE;
		}

		@Override
		public String casePort(Port obj) {
			System.out.print("Port: ");
			System.out.println(obj.getContainingClassifier().getName() + "."
					+ obj.getName());
			handlePort(obj);
			return NOT_DONE;
		}

		private void handlePort(Port obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				if (obj.getCategory() == PortCategory.EVENT_DATA) {
					String typeName = null;
					try {
						Property prop = GetProperties.lookupPropertyDefinition(
								((EventDataPort) obj)
										.getDataFeatureClassifier(),
								DataModel._NAME, DataModel.Data_Representation);
						typeName = getJavaType(PropertyUtils.getEnumLiteral(
								obj, prop).getName());
						if (obj.getDirection() == DirectionType.IN) {
							processModels.get(processModels.size() - 1)
									.addReceivePort(obj.getName(), typeName);
						} else if (obj.getDirection() == DirectionType.OUT) {
							processModels.get(processModels.size() - 1)
									.addSendPort(obj.getName(), typeName);
						} else {
							throw new NotImplementedException("Port "
									+ obj.getName() + " is neither in nor out");
						}
					} catch (NotImplementedException e) {
						handleException("Port", obj.getName(), e);
					}
				}
			}
		}

		private void handleException(String elemType, String elemName,
				Exception e) {
			System.err.println("An error has occurred: in " + elemType + " "
					+ elemName + e.getLocalizedMessage());
			e.printStackTrace();
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
			if (name.equals("Integer") || name.equals("Double") || name.equals("Boolean")) {
				return name;
			} else {
				throw new NotImplementedException(
						"No java equivalent for type " + name);
			}
		}

		@Override
		public String caseType(Type obj) {
			System.out.println("Type: " + obj.getName());
			return NOT_DONE;
		}

		@Override
		public String caseDataSubcomponent(DataSubcomponent obj) {
			System.out.println("Data: " + obj.getName());
			if (lastElemProcessed == ElementType.PROCESS) {
				ProcessModel proc = processModels.get(processModels.size() - 1);
				Property prop = GetProperties.lookupPropertyDefinition(
						obj.getDataSubcomponentType(), DataModel._NAME,
						DataModel.Data_Representation);
				String typeName = null;
				try {
					typeName = getJavaType(PropertyUtils.getEnumLiteral(obj,
							prop).getName());
				} catch (NotImplementedException e) {
					handleException("DataSubcomponent", obj.getName(), e);
				}
				proc.addGlobal(obj.getName(), typeName);
			}
			return NOT_DONE;
		}

		@Override
		public String casePortSpecification(PortSpecification obj) {
			return NOT_DONE;
		}

		@Override
		public String caseDataAccess(DataAccess obj) {
			return NOT_DONE;
		}

		@Override
		public String caseAccessConnection(AccessConnection obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				handleProcessDataConnection(obj);
			}
			System.out.println("AccessConnection: " + obj.getName());
			return NOT_DONE;
		}

		public String caseAadlPackage(AadlPackage obj) {
			processEList(obj.getOwnedPublicSection().getChildren());
			return DONE;
		}

		@Override
		public String caseComponentImplementation(ComponentImplementation obj) {
			System.out.println("ComponentImplementation: " + obj.getName());
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

		private void handleProcessPortConnection(PortConnection obj) {
			ProcessModel proc = processModels.get(processModels.size() - 1);
			String taskName, localName, portName, portType;
			TaskModel task;
			if (obj.getAllSource().getOwner() instanceof ThreadType) {
				// From thread to process
				taskName = ((ThreadType) obj.getAllSource().getOwner())
						.getName();
				localName = obj.getAllSource().getName();
				portName = obj.getAllDestination().getName();
				portType = proc.getSendPorts().get(portName);
				task = proc.getTask(taskName);
				try {
					task.setTrigPortInfo(portName, portType, localName);
				} catch (NotImplementedException e) {
					handleException("PortConnection", obj.getName(), e);
				}
			} else {
				// From process to thread
				taskName = ((ThreadType) obj.getAllDestination().getOwner())
						.getName();
				localName = obj.getAllDestination().getName();
				portName = obj.getAllSource().getName();
				portType = proc.getReceivePorts().get(portName);
				task = proc.getTask(taskName);
				try {
					task.setTrigPortInfo(portName, portType, localName);
				} catch (NotImplementedException e) {
					handleException("PortConnection", obj.getName(), e);
				}
			}
		}

		private void handleProcessDataConnection(AccessConnection obj) {
			ProcessModel proc = processModels.get(processModels.size() - 1);
			String parentName;
			TaskModel task;
			VariableModel vm = new VariableModel();
			String srcName = obj.getAllSource().getName();
			String dstName = obj.getAllDestination().getName();
			if (obj.getAllSource().getOwner() instanceof ThreadType) {
				// From thread to process
				parentName = ((ThreadType) obj.getAllSource().getOwner()).getName();
				task = proc.getTask(parentName);
				vm.setOuterName(dstName);
				vm.setInnerName(srcName);
				vm.setType(proc.getGlobalType(dstName));
				task.addOutGlobal(vm);
			} else {
				// From process to thread
				parentName = ((ThreadType) obj.getAllDestination().getOwner()).getName();
				task = proc.getTask(parentName);
				vm.setOuterName(srcName);
				vm.setInnerName(dstName);
				vm.setType(proc.getGlobalType(srcName));
				task.addIncGlobal(vm);
			}
		}

		@Override
		public String casePortConnection(PortConnection obj) {
			System.out.print("Connection: ");
			if (obj.getAllSource().getContainingClassifier() == null)
				System.out.print(obj.getAllSource().getName());
			else
				System.out.print(obj.getAllSource().getContainingClassifier()
						.getName()
						+ "." + obj.getAllSource().getName());
			System.out.print(" -> ");
			if (obj.getAllDestination().getContainingClassifier() == null)
				System.out.println(obj.getAllDestination().getName());
			else
				System.out.println(obj.getAllDestination()
						.getContainingClassifier().getName()
						+ "." + obj.getAllDestination().getName());
			if (lastElemProcessed == ElementType.PROCESS) {
				handleProcessPortConnection(obj);
			}
			return NOT_DONE;
		}

		public ProcessModel getProcessModel() {
			return processModels.get(0);
		}
	}

	public ModelStatistics(final IProgressMonitor monitor) {
		super(monitor, PROCESS_PRE_ORDER_ALL);

	}

	public ModelStatistics(final IProgressMonitor monitor,
			AnalysisErrorReporterManager errmgr) {
		super(monitor, PROCESS_PRE_ORDER_ALL, errmgr);
	}

	@Override
	protected final void initSwitches() {
		aadl2Switch = new MyAadl2Switch();
	}

	public ProcessModel getProcessModel() {
		// TODO: This seems super jank.
		return ((MyAadl2Switch) aadl2Switch).getProcessModel();
	}
}
