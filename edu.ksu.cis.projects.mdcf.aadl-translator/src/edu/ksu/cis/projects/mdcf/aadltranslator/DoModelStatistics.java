package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.Element;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.modeltraversal.TraverseWorkspace;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.osate.ui.actions.AaxlReadOnlyActionAsJob;
import org.osgi.framework.Bundle;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;

public final class DoModelStatistics extends AaxlReadOnlyActionAsJob {
	private final STGroup javaSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/java.stg");
	private final STGroup midas_compsigSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/midas-compsig.stg");
	private final STGroup midas_appspecSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/midas-appspec.stg");

	protected Bundle getBundle() {
		return AadlTranslatorPlugin.getDefault().getBundle();
	}

	protected String getMarkerType() {
		return "org.osate.analysis.architecture.ModelStatisticsObjectMarker";
	}

	protected String getActionName() {
		return "Model statistics";
	}

	public void doAaxlAction(IProgressMonitor monitor, Element obj) {
		
		OsateResourceUtil.refreshResourceSet();
		
		ResourceSet rs = OsateResourceUtil.createResourceSet();
		HashSet<IFile> files = TraverseWorkspace
				.getAadlandInstanceFilesInWorkspace();
		LinkedList<IFile> fileList = new LinkedList<>();
		
		monitor.beginTask("Translating AADL to Java / MIDAS", files.size() + 1);

		ModelStatistics stats = new ModelStatistics(monitor, getErrorManager());
		
		// TODO: This is pretty ugly. It works as a testing rig, but it should
		// probably get cleaned up considerably before any sort of release
		
		// The system _has_ to come first, so we make sure it's first
		for (IFile f : files) {
			Resource res = rs.getResource(
					OsateResourceUtil.getResourceURI((IResource) f), true);

			Element target = (Element) res.getContents().get(0);
			if ((target instanceof PropertySet)){
				stats.addPropertySetName(((PropertySet)target).getName());
				continue;
			}
			AadlPackage pack = (AadlPackage) target;
			PublicPackageSection sect = pack.getPublicSection();
			Classifier ownedClassifier = sect.getOwnedClassifiers().get(0);
			if ((ownedClassifier instanceof org.osate.aadl2.System)) {
				fileList.addFirst(f);
			} else {
				fileList.addLast(f);
			}
		}

		// Now we process all the files, with the system first.
		for (IFile f : fileList) {
			Resource res = rs.getResource(
					OsateResourceUtil.getResourceURI((IResource) f), true);
			Element target = (Element) res.getContents().get(0);
			stats.process(target);
			monitor.worked(1);
		}
		
		// Filename -> file contents
		HashMap<String, String> compsigs = new HashMap<>();
		
		// Filename -> file contents
		HashMap<String, String> javaClasses = new HashMap<>();
		
		String appName;
		String appSpecContents;
		
		midas_compsigSTG.delimiterStartChar = '$';
		midas_compsigSTG.delimiterStopChar = '$';
		for (ProcessModel pm : stats.getSystemModel().getLogicComponents()
				.values()) {
			javaClasses.put(pm.getName(), javaSTG.getInstanceOf("class").add("model", pm).render());
			compsigs.put(pm.getName(), midas_compsigSTG.getInstanceOf("compsig").add("model", pm).render());
		}
		midas_appspecSTG.delimiterStartChar = '#';
		midas_appspecSTG.delimiterStopChar = '#';

		appName = stats.getSystemModel().getName();
		appSpecContents = midas_appspecSTG.getInstanceOf("appspec").add("system", stats.getSystemModel()).render();

		WriteOutputFiles.writeFiles(compsigs, javaClasses, appName, appSpecContents);
		
		monitor.worked(1);
		
		monitor.done();
	}
}
