package edu.ksu.cis.projects.mdcf.aadltranslator;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.Data;
import org.osate.aadl2.DeviceImplementation;
import org.osate.aadl2.EventDataPort;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortCategory;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.PortSpecification;
import org.osate.aadl2.Process;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.Thread;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.Type;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.properties.PropertyAcc;
import org.osate.aadl2.util.Aadl2Switch;
import org.osate.contribution.sei.names.DataModel;
import org.osate.xtext.aadl2.properties.util.GetProperties;
import org.osate.xtext.aadl2.properties.util.PropertyUtils;

public final class ModelStatistics extends AadlProcessingSwitchWithProgress {
	public class MyAadl2Switch extends Aadl2Switch<String> {

		private ProcessModel currentModel = null;

		@Override
		public String caseSystemImplementation(SystemImplementation obj) {
			System.out.println("System: " + obj.getName());
			return DONE;
		}

		@Override
		public String caseThread(Thread obj){
			return DONE;
		}

		@Override
		public String caseThreadImplementation(ThreadImplementation obj){
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
			currentModel = new ProcessModel();
			currentModel.setObjectName(obj.getName());
			return DONE;
		}

		@Override
		public String caseProcessImplementation(ProcessImplementation obj) {
			System.out.println("ProcessImplementation: " + obj.getName());
			return DONE;
		}
		
		@Override
		public String caseProperty(Property obj){
			System.out.println("Property: " + obj.getName());
			return DONE;
		}
		
		@Override
		public String casePort(Port obj){
			Property prop;
			System.out.print("Port: ");
			System.out.println(obj.getContainingClassifier().getName() + "." + obj.getName());
			if(currentModel != null){
				if(obj.getCategory() == PortCategory.EVENT_DATA){
					//TODO: There should be a method for translating to java types here.
					prop = GetProperties.lookupPropertyDefinition(((EventDataPort) obj).getDataFeatureClassifier(), DataModel._NAME, DataModel.Data_Representation);
					currentModel.addPort(obj.getName(), obj.getDirection(), obj.getCategory(), PropertyUtils.getEnumLiteral(obj, prop).getName());
				}
			}
			return DONE;
		}
		
		@Override
		public String caseType(Type obj){
			System.out.println("Type: " + obj.getName());
			return DONE;
		}
		
		@Override
		public String caseData(Data obj){
			System.out.println("Data: " + obj.getName());
			return DONE;
		}
		
		@Override
		public String casePortSpecification(PortSpecification obj){
			return DONE;
		}

		@Override
		public String casePortConnection(PortConnection obj){
			System.out.print("Connection: ");
			if(obj.getAllSource().getContainingClassifier() == null)
				System.out.print(obj.getAllSource().getName());
			else
				System.out.print(obj.getAllSource().getContainingClassifier().getName() + "." + obj.getAllSource().getName());
			System.out.print(" -> ");
			if(obj.getAllDestination().getContainingClassifier() == null)
				System.out.println(obj.getAllDestination().getName());
			else
				System.out.println(obj.getAllDestination().getContainingClassifier().getName() + "." + obj.getAllDestination().getName());
			return DONE;
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
}
