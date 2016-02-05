package edu.ksu.cis.projects.mdcf.aadltranslator.view;

import static org.junit.Assert.assertEquals;

import org.junit.BeforeClass;
import org.junit.Test;
import org.stringtemplate.v4.STGroup;

import edu.ksu.cis.projects.mdcf.aadltranslator.util.MarkdownLinkRenderer;

public class STRendererTests {


	private static STGroup stg;

	@BeforeClass
	public static void initialize() {
		stg = new STGroup();
		stg.defineTemplate("interlink", "linkText", "<linkText; format=\"MarkdownInterLink\">");
		stg.defineTemplate("intralink", "linkText", "<linkText; format=\"MarkdownIntraLink\">");
		stg.registerRenderer(String.class, MarkdownLinkRenderer.getInstance());
	}

	@Test
	public void testInterlink() {
		assertEquals("this-is-my-link.html", stg.getInstanceOf("interlink").add("linkText", "This is my Link").render());
	}
	
	@Test
	public void testIntralink() {
		assertEquals("#this-is-my-link", stg.getInstanceOf("intralink").add("linkText", "This is my Link").render());
	}
}
