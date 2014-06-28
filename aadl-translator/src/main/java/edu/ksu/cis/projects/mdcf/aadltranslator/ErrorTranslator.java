package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.AnnexSubclause;
import org.osate.aadl2.NamedElement;
import org.osate.aadl2.modelsupport.modeltraversal.AadlProcessingSwitchWithProgress;
import org.osate.xtext.aadl2.errormodel.errorModel.ConnectionErrorSource;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorModelSubclause;
import org.osate.xtext.aadl2.errormodel.errorModel.ErrorType;
import org.osate.xtext.aadl2.errormodel.errorModel.util.ErrorModelSwitch;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ImpactModel;
import edu.ksu.cis.projects.mdcf.aadltranslator.model.SystemModel;

public final class ErrorTranslator extends AadlProcessingSwitchWithProgress {
	
	private ErrorTranslatorSwitch errorSwitch;
	private HashMap<String, ImpactModel> impacts;

	public class ErrorTranslatorSwitch extends ErrorModelSwitch<String> {
		
		@Override
		public String caseNamedElement(NamedElement obj){
			return DONE;
		}
		
		@Override
		public String caseErrorModelSubclause(ErrorModelSubclause obj){
			processEList(obj.getChildren());
			return DONE;
		}
		
		@Override
		public String caseAnnexSubclause(AnnexSubclause obj){
			processEList(obj.getChildren());
			return DONE;
		}
		
		@Override
		public String caseConnectionErrorSource(ConnectionErrorSource obj){
			return DONE;
		}
	}	

	protected ErrorTranslator(IProgressMonitor pm, SystemModel systemModel) {
		super(pm, PROCESS_PRE_ORDER_ALL);
	}

	@Override
	protected final void initSwitches() {
		errorSwitch = new ErrorTranslatorSwitch();
	}

	public void setErrorTypes(HashSet<ErrorType> errors) {
		impacts = new HashMap<>();
		for(ErrorType et : errors){
			impacts.put(et.getName(), new ImpactModel(et.getName()));
		}
	}
}
