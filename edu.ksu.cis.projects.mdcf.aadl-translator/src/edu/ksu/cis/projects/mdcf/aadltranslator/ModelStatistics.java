package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.Data;
import org.osate.aadl2.DataAccess;
import org.osate.aadl2.DeviceImplementation;
import org.osate.aadl2.DirectionType;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PortSpecification;
import org.osate.aadl2.Process;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.Type;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.util.Aadl2Switch;
import org.osate.contribution.sei.names.DataModel;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

import edu.ksu.cis.projects.mdcf.aadltranslator.exception.NotImplementedException;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;

public final class ModelStatistics extends AadlProcessingSwitchWithProgress {
	private enum ElementType {PROCESS, THREAD, NONE}; 
	public class MyAadl2Switch extends Aadl2Switch<String> {
		private ArrayList<ProcessModel> processModels = new ArrayList<>();
		private ElementType lastElemProcessed = ElementType.NONE;

		@Override
		public String caseSystemImplementation(SystemImplementation obj) {
			System.out.println("System: " + obj.getName());
			return DONE;
		}

		@Override
		public String caseThreadType(ThreadType obj) {
			System.out.println("Thread: " + obj.getName());
			processModels.get(processModels.size() - 1)
					.addTask(obj.getName());
			lastElemProcessed = ElementType.THREAD;
			return DONE;
		}

		@Override
		public String caseThreadImplementation(ThreadImplementation obj) {
			return DONE;
		}

		@Override
		public String caseDeviceImplementation(DeviceImplementation obj) {
			System.out.println("Device: " + obj.getName());
			return DONE;
		}

		@Override
		public String caseProcess(Process obj) {
			System.out.println("Process: " + obj.getName());
			processModels.add(new ProcessModel());
			processModels.get(processModels.size() - 1).setObjectName(
					obj.getName());
			lastElemProcessed = ElementType.PROCESS;
			return DONE;
		}

		@Override
		public String caseProcessImplementation(ProcessImplementation obj) {
			System.out.println("ProcessImplementation: " + obj.getName());
			return DONE;
		}

		@Override
		public String caseProperty(Property obj) {
			System.out.println("Property: " + obj.getName());
			return DONE;
		}

		@Override
		public String casePort(Port obj) {
			System.out.print("Port: ");
			System.out.println(obj.getContainingClassifier().getName() + "."
					+ obj.getName());
			handlePort(obj);
			return DONE;
		}

		private void handlePort(Port obj) {
			if (lastElemProcessed == ElementType.PROCESS) {
				if (obj.getCategory() == PortCategory.EVENT_DATA) {
					Property prop = GetProperties.lookupPropertyDefinition(
							((EventDataPort) obj).getDataFeatureClassifier(),
							DataModel._NAME, DataModel.Data_Representation);
					try {
						String typeName = getJavaType(PropertyUtils
								.getEnumLiteral(obj, prop).getName());
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

		private void handleException(String elemType, String elemName, Exception e) {
			System.err.println("An error has occurred: in " + elemType + " " + elemName
					+ e.getLocalizedMessage());
			e.printStackTrace();
		}

		private String getJavaType(String name) throws NotImplementedException {
			if (name.equals("Integer") || name.equals("Double")) {
				return name;
			} else {
				throw new NotImplementedException(
						"No java equivalent for type " + name);
			}
		}

		@Override
		public String caseType(Type obj) {
			System.out.println("Type: " + obj.getName());
			return DONE;
		}

		@Override
		public String caseData(Data obj) {
			System.out.println("Data: " + obj.getName());
			return DONE;
		}

		@Override
		public String casePortSpecification(PortSpecification obj) {
			return DONE;
		}

		@Override
		public String caseDataAccess(DataAccess obj) {
			return DONE;
		}
		
		@Override
		public String caseAccessConnection(AccessConnection obj) {
			String parentName;
			if(obj.getAllSource().getOwner() instanceof ThreadType){
				// Write
				parentName = ((ThreadType)obj.getAllSource().getOwner()).getName();
			} else {
				// Read
				parentName = ((ThreadType)obj.getAllDestination().getOwner()).getName();
			}
			String srcName = obj.getAllSource().getName();
			String dstName = obj.getAllDestination().getName();
			//TODO: Next step: add these usages into the models (as either incoming globals, or values to be set)
			return DONE;
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
			return DONE;
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
