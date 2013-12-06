package org.osate.analysis.architecture;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.DeviceImplementation;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessImplementation;
import org.osate.aadl2.Property;
import org.osate.aadl2.SystemImplementation;
import org.osate.aadl2.Thread;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.modelsupport.errorreporting.AnalysisErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.util.Aadl2Switch;

public final class ModelStatistics extends AadlProcessingSwitchWithProgress {
	public class MyAadl2Switch extends Aadl2Switch<String> {

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
		public String  caseProcessImplementation(ProcessImplementation obj) {
			System.out.println("Process: " + obj.getName());
			return DONE;
		}
		
		@Override
		public String caseProperty(Property obj){
			System.out.println("Property: " + obj.getName());
			return DONE;
		}
		
		@Override
		public String casePort(Port obj){
			System.out.print("Port: ");
			System.out.println(obj.getContainingClassifier().getName() + "." + obj.getName());
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
