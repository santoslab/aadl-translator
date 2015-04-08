package edu.ksu.cis.projects.mdcf.aadltranslator.util;

import java.util.Locale;

import org.stringtemplate.v4.AttributeRenderer;

/**
 * Singleton (which delays initialization until necessary) holding our renderer
 * for links in Markdown.
 * 
 * @author Sam Procter
 *
 */
public class MarkdownLinkRenderer implements AttributeRenderer {

	// Disallow instantiation
	private MarkdownLinkRenderer() {
	}

	private static class MarkdownLinkRendererHolder {
		private static final MarkdownLinkRenderer INSTANCE = new MarkdownLinkRenderer();
	}

	public static MarkdownLinkRenderer getInstance() {
		return MarkdownLinkRendererHolder.INSTANCE;
	}

	@Override
	public String toString(Object o, String formatString, Locale locale) {
		// o will be a String
		String ret = (String) o;
		
		if(formatString == null || !formatString.equals("MarkdownLink"))
			return ret;
		
		ret = ret.toLowerCase();
		ret = ret.replace(' ', '-');
		ret = ret.replace(":", "");

		return ret;
	}
}