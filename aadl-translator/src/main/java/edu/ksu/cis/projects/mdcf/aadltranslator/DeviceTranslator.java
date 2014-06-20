package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.AccessConnection;
import org.osate.aadl2.ComponentImplementation;
import org.osate.aadl2.DataSubcomponent;
import org.osate.aadl2.DeviceSubcomponent;
import org.osate.aadl2.DeviceType;
import org.osate.aadl2.Element;
import org.osate.aadl2.FeatureGroup;
import org.osate.aadl2.PackageSection;
import org.osate.aadl2.Port;
import org.osate.aadl2.PortConnection;
import org.osate.aadl2.ProcessSubcomponent;
import org.osate.aadl2.ProcessType;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.SubprogramCallSequence;
import org.osate.aadl2.SubprogramType;
import org.osate.aadl2.ThreadImplementation;
import org.osate.aadl2.ThreadSubcomponent;
import org.osate.aadl2.ThreadType;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterManager;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.util.Aadl2Switch;

import edu.ksu.cis.projects.mdcf.aadltranslator.model_for_device.DeviceComponentModel;

public final class DeviceTranslator extends AadlProcessingSwitchWithProgress {
	private ArrayList<String> propertySetNames = new ArrayList<>();
	private ParseErrorReporterManager errorManager;
	private DeviceComponentModel deviceComponentModel = null;
	
	public class TranslatorSwitch extends Aadl2Switch<String> {


		private String DONE = "Done";
		private String NOT_DONE = null;

		@Override
		public String caseSystem(org.osate.aadl2.System obj) {
			System.err.println("caseSystem:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String caseThreadSubcomponent(ThreadSubcomponent obj) {
			System.err.println("caseThreadSubcomponent:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String caseThreadType(ThreadType obj) {
			System.err.println("caseThreadType:" + obj.getFullName());
			return NOT_DONE;
		}
		
		@Override
		public String caseFeatureGroup(FeatureGroup obj){
			System.err.println("caseFeatureGroup:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String casePropertySet(PropertySet obj) {
			System.err.println("casePropertySet:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String casePackageSection(PackageSection obj) {
			System.err.println("casePackageSection:" + obj.getFullName());
			return DONE;
		}

		@Override
		public String caseDeviceSubcomponent(DeviceSubcomponent obj) {
			System.err.println("caseDeviceSubcomponent:" 
						+ obj.getComponentType().getName() + "(" + obj.getFullName() + ")");
			return NOT_DONE;
		}

		@Override
		public String caseProcessSubcomponent(ProcessSubcomponent obj) {
			System.err.println("caseProcessSubcomponent:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String caseDeviceType(DeviceType obj) {
			System.err.println("caseDeviceType:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String caseProcessType(ProcessType obj) {
			System.err.println("caseProcessType:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String casePort(Port obj) {
			System.err.println("casePort:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String caseDataSubcomponent(DataSubcomponent obj) {
			System.err.println("caseDataSubcomponent:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String caseAccessConnection(AccessConnection obj) {
			System.err.println("caseAccessConnection:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String caseAadlPackage(AadlPackage obj) {
			System.err.println("caseAadlPackage:" + obj.getFullName());
			for(Element e : obj.getOwnedPublicSection().allOwnedElements()) {
				System.err.println("   caseAadlPackage:" + e.toString());
				process(e);
			}
			//processEList(obj.getOwnedPublicSection().getChildren());

			return DONE;
		}

		@Override
		public String caseComponentImplementation(ComponentImplementation obj) {
			System.err.println("caseComponentImplementation:" + obj.getFullName());
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
			System.err.println("caseSubprogramType:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String caseSubprogramCallSequence(SubprogramCallSequence obj) {
			System.err.println("caseSubprogramCallSequence:" + obj.getFullName());
			return NOT_DONE;
		}

		@Override
		public String casePortConnection(PortConnection obj) {
			System.err.println("casePortConnection:" + obj.getFullName());
			return NOT_DONE;
		}
	}

	public DeviceTranslator(final IProgressMonitor monitor) {
		super(monitor, PROCESS_PRE_ORDER_ALL);

	}

	@Override
	protected final void initSwitches() {
		aadl2Switch = new TranslatorSwitch();
	}
	
	public void addPropertySetName(String propSetName) {
		propertySetNames.add(propSetName);
	}
	
	public void setErrorManager(ParseErrorReporterManager parseErrManager) {
		errorManager = parseErrManager;
	}
	
	public DeviceComponentModel getDeviceComponentModel(){
		return this.deviceComponentModel;
	}
}

