package edu.ksu.cis.projects.mdcf.aadltranslator;

import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.osate.aadl2.Element;
import org.osate.aadl2.instance.InstanceObject;
import org.osate.aadl2.instance.SystemInstance;

import edu.ksu.cis.projects.mdcf.aadltranslator.ArchitecturePlugin;
import edu.ksu.cis.projects.mdcf.aadltranslator.ModelStatistics;

import org.osate.ui.actions.AaxlReadOnlyActionAsJob;
import org.osgi.framework.Bundle;

public final class DoModelStatistics extends AaxlReadOnlyActionAsJob {
	private final STGroup javaSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/templ/java.stg");
	private final STGroup midasSTG = new STGroupFile(
			"bin/edu/ksu/cis/projects/mdcf/aadltranslator/templ/midas.stg");

	protected Bundle getBundle() {
		return ArchitecturePlugin.getDefault().getBundle();
	}

	protected String getMarkerType() {
		return "org.osate.analysis.architecture.ModelStatisticsObjectMarker";
	}

	protected String getActionName() {
		return "Model statistics";
	}

	public void doAaxlAction(IProgressMonitor monitor, Element obj) {
		/*
		 * Doesn't make sense to set the number of work units, because the whole
		 * point of this action is count the number of elements. To set the work
		 * units we would effectively have to count everything twice.
		 */
		monitor.beginTask("Gathering model statistics",
				IProgressMonitor.UNKNOWN);
		// Get the root object of the model
		Element root = obj.getElementRoot();

		// Get the system instance (if any)
		SystemInstance si;
		if (obj instanceof InstanceObject)
			si = ((InstanceObject) obj).getSystemInstance();
		else
			si = null;

		/**
		 * Examples of using the Index to look up a specific package,
		 * classifier, property, etc. In this case any scoping rules based on
		 * with clauses or project dependencies are ignored
		 */

		// Element e =
		// EMFIndexRetrieval.getPropertyDefinitionInWorkspace("Deadline");
		// Element p = EMFIndexRetrieval.getPackageInWorkspace("mydata::dd");
		// Element c =
		// EMFIndexRetrieval.getClassifierInWorkspace("mydata::dd::sys");

		/**
		 * Example of using the Index to get all classifiers In this case we
		 * then call on the resolver for the reference (causing the classifier
		 * to be loaded)
		 */

		// EList<IEObjectDescription> classifierlist =
		// EMFIndexRetrieval.getAllClassifiersInWorkspace();
		// for (IEObjectDescription cleod : classifierlist){
		// Classifier cl = (Classifier)
		// EcoreUtil.resolve(cleod.getEObjectOrProxy(),
		// OsateResourceUtil.getResourceSet());//obj.eResource().getResourceSet());
		// stats.process(cl);
		// }

		/**
		 * Example of counting without causing the classifier to load
		 */

		// EList<IEObjectDescription> classifierlist1 =
		// EMFIndexRetrieval.getAllClassifiersInWorkspace();
		// Resource res = obj.eResource();
		// for (IEObjectDescription cleod : classifierlist1){
		// stats.countClassifier(cleod.getEClass());
		// }

		/*
		 * Create a new model statistics analysis object and run it over the
		 * declarative model. If an instance model exists, run it over that too.
		 */
		ModelStatistics stats = new ModelStatistics(monitor, getErrorManager());
		/*
		 * Accumulate the results in a StringBuffer, but also report them using
		 * info markers attached to the root model object.
		 */
		// run statistics on all declarative models in the workspace
		stats.defaultTraversalAllDeclarativeModels();
		final StringBuffer msg = new StringBuffer();

		if (si != null) {
			stats.defaultTraversal(si);
		}
		System.out.println(javaSTG.getInstanceOf("class")
				.add("model", stats.getProcessModel()).render());
		monitor.done();
	}
}
