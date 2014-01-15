package edu.ksu.cis.projects.mdcf.aadltranslator;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.osate.aadl2.AadlPackage;
import org.osate.aadl2.Classifier;
import org.osate.aadl2.Element;
import org.osate.aadl2.PropertySet;
import org.osate.aadl2.PublicPackageSection;
import org.osate.aadl2.modelsupport.modeltraversal.TraverseWorkspace;
import org.osate.aadl2.modelsupport.resources.OsateResourceUtil;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;

import edu.ksu.cis.projects.mdcf.aadltranslator.model.ProcessModel;

public final class DoTranslation implements IHandler, IRunnableWithProgress{
	private final STGroup java_superclassSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/java-superclass.stg");
	private final STGroup java_userimplSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/java-userimpl.stg");
	private final STGroup midas_compsigSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/midas-compsig.stg");
	private final STGroup midas_appspecSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/view/midas-appspec.stg");

	public void doAaxlAction(IProgressMonitor monitor) {
		
		OsateResourceUtil.refreshResourceSet();
		
		ResourceSet rs = OsateResourceUtil.createResourceSet();
		HashSet<IFile> files = TraverseWorkspace
				.getAadlandInstanceFilesInWorkspace();
		LinkedList<IFile> fileList = new LinkedList<>();
		
		monitor.beginTask("Translating AADL to Java / MIDAS", files.size() + 1);

		Translator stats = new Translator(monitor);
		
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
			javaClasses.put(pm.getName() + "SuperType", java_superclassSTG.getInstanceOf("class").add("model", pm).render());
			javaClasses.put(pm.getName(), java_userimplSTG.getInstanceOf("userimpl").add("model", pm).render());
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

	@Override
	public void addHandlerListener(IHandlerListener handlerListener) {
		// We don't track handlers, so we do nothing here
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IRunnableWithProgress runnable = new DoTranslation();
		try {
			new ProgressMonitorDialog(new Shell()).run(true, true, runnable);
		} catch (InvocationTargetException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean isEnabled() {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isHandled() {
		// If we're enabled, then we can handle input
		return true;
	}

	@Override
	public void removeHandlerListener(IHandlerListener handlerListener) {
		// We don't track handlers, so we do nothing here
	}

	@Override
	public void dispose() {
		// We have nothing to dispose of, so we do nothing here
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException,
			InterruptedException {
		doAaxlAction(monitor);
	}
}
