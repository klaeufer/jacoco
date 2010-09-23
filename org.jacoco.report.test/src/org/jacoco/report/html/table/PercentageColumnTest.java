/*******************************************************************************
 * Copyright (c) 2009, 2010 Mountainminds GmbH & Co. KG and Contributors
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Marc R. Hoffmann - initial API and implementation
 *    
 * $Id: $
 *******************************************************************************/
package org.jacoco.report.html.table;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.jacoco.core.analysis.CounterImpl;
import org.jacoco.core.analysis.CoverageNodeImpl;
import org.jacoco.core.analysis.ICoverageNode;
import org.jacoco.core.analysis.ICoverageNode.CounterEntity;
import org.jacoco.core.analysis.ICoverageNode.ElementType;
import org.jacoco.report.MemoryMultiReportOutput;
import org.jacoco.report.ReportOutputFolder;
import org.jacoco.report.html.HTMLDocument;
import org.jacoco.report.html.HTMLElement;
import org.jacoco.report.html.HTMLSupport;
import org.jacoco.report.html.resources.Resources;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;

/**
 * Unit tests for {@link PercentageColumn}.
 * 
 * @author Marc R. Hoffmann
 * @version $Revision: $
 */
public class PercentageColumnTest {

	private MemoryMultiReportOutput output;

	private ReportOutputFolder root;

	private Resources resources;

	private HTMLDocument doc;

	private HTMLElement td;

	private HTMLSupport support;

	private IColumnRenderer column;

	@Before
	public void setup() throws Exception {
		output = new MemoryMultiReportOutput();
		root = new ReportOutputFolder(output);
		resources = new Resources(root);
		doc = new HTMLDocument(root.createFile("Test.html"), "UTF-8");
		doc.head().title();
		td = doc.body().table("somestyle").tr().td();
		support = new HTMLSupport();
		column = new PercentageColumn(CounterEntity.LINE);
	}

	@After
	public void teardown() {
		output.assertAllClosed();
	}

	@Test
	public void testInit() throws Exception {
		assertTrue(column.init(null, null));
		doc.close();
	}

	@Test
	public void testItem1() throws Exception {
		final ITableItem item = createItem(150, 50);
		column.item(td, item, resources, root);
		doc.close();
		final Document doc = support.parse(output.getFile("Test.html"));
		assertEquals("33%",
				support.findStr(doc, "/html/body/table/tr/td[1]/text()"));
	}

	@Test
	public void testItem2() throws Exception {
		final ITableItem item = createItem(0, 50);
		column.item(td, item, resources, root);
		doc.close();
		final Document doc = support.parse(output.getFile("Test.html"));
		assertEquals("n/a",
				support.findStr(doc, "/html/body/table/tr/td[1]/text()"));
	}

	@Test
	public void testFooter1() throws Exception {
		final ITableItem item = createItem(80, 60);
		column.footer(td, item.getNode(), resources, root);
		doc.close();
		final Document doc = support.parse(output.getFile("Test.html"));
		assertEquals("75%", support.findStr(doc, "/html/body/table/tr"));
	}

	@Test
	public void testFooter2() throws Exception {
		final ITableItem item = createItem(0, 60);
		column.footer(td, item.getNode(), resources, root);
		doc.close();
		final Document doc = support.parse(output.getFile("Test.html"));
		assertEquals("n/a", support.findStr(doc, "/html/body/table/tr"));
	}

	private ITableItem createItem(final int total, final int covered) {
		final ICoverageNode node = createNode(total, covered);
		return new ITableItem() {
			public String getLinkLabel() {
				return "Foo";
			}

			public String getLink(ReportOutputFolder base) {
				return null;
			}

			public String getLinkStyle() {
				return Resources.getElementStyle(node.getElementType());
			}

			public ICoverageNode getNode() {
				return node;
			}
		};
	}

	private CoverageNodeImpl createNode(final int total, final int covered) {
		return new CoverageNodeImpl(ElementType.GROUP, "Foo", false) {
			{
				this.lineCounter = CounterImpl.getInstance(total, covered);
			}
		};
	}
}