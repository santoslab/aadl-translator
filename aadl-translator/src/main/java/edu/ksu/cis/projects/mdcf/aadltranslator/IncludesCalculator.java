package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.HashSet;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.aadl2.util.Aadl2Switch;

public class IncludesCalculator extends AadlProcessingSwitchWithProgress {
	private HashSet<IFile> usedFiles;
	
	protected IncludesCalculator(IProgressMonitor pm) {
		super(pm);
		usedFiles = new HashSet<>();
	}

	public class ComputeIncludesSwitch extends Aadl2Switch<String> {
		@Override
		public String caseAadlPackage(AadlPackage obj) {
			IFile f = OsateResourceUtil.getOsateIFile(obj.eResource().getURI());
			if(usedFiles.contains(f))
				return DONE;
			usedFiles.add(f);
			processEList(obj.getOwnedPublicSection().getImportedUnits());
			return DONE;
		}
		
		@Override
		public String casePropertySet(PropertySet obj) {
			IFile f = OsateResourceUtil.getOsateIFile(obj.eResource().getURI());
			if(usedFiles.contains(f))
				return DONE;
			usedFiles.add(f);
			processEList(obj.getImportedUnits());
			return DONE;
		}
	}

	@Override
	protected void initSwitches() {
		aadl2Switch = new ComputeIncludesSwitch();
	}
	
	public HashSet<IFile> getUsedFiles(){
		return usedFiles;
	}
}
