package edu.ksu.cis.projects.mdcf.aadltranslator.error;

import org.eclipse.core.resources.IResource;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporter;
import org.osate.aadl2.modelsupport.errorreporting.ParseErrorReporterFactory;

/*
 * Adapted from http://www.javaworld.com/article/2073352
 */

public class TestParseErrorReporterFactory implements ParseErrorReporterFactory {

	public final static TestParseErrorReporterFactory INSTANCE = new TestParseErrorReporterFactory();
	
	private final TestParseErrorReporter reporter = new TestParseErrorReporter();

	private TestParseErrorReporterFactory() {
		// Private to prevent instantiation...
		
	}

	@Override
	public ParseErrorReporter getReporterFor(IResource aadlRsrc) {
		return reporter;
	}

}
