/*
 * Copyright (C) Jakub Neubauer, 2007
 *
 * This file is part of TaskBlocks
 *
 * TaskBlocks is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 3 of the License, or
 * (at your option) any later version.
 *
 * TaskBlocks is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package taskblocks.bugzilla;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;


import junit.framework.TestCase;

public class BugzillaSubmitterTest extends TestCase {
	
	public void notestSubmit() {
		Map<String, String> formData = new HashMap<String, String>();
		formData.put(BugzillaSubmitter.KEYWORDS, "Plan");
		formData.put(BugzillaSubmitter.PRODUCT, "TestProduct");
		formData.put(BugzillaSubmitter.VERSION, "unspecified");
		formData.put(BugzillaSubmitter.COMPONENT, "TestComponent");
		formData.put(BugzillaSubmitter.HARDWARE, "All");
		formData.put(BugzillaSubmitter.OS, "All");
		formData.put(BugzillaSubmitter.PRIORITY, "P2");
		formData.put(BugzillaSubmitter.SEVERITY, "enhancement");
		formData.put(BugzillaSubmitter.STATUS, "NEW");
		formData.put(BugzillaSubmitter.ASSIGNED_TO, "j.neubauer@cz.gmc.net");
		formData.put(BugzillaSubmitter.SUMMARY, "Test Bug");
		formData.put(BugzillaSubmitter.DESCRIPTION, "Description of Test Bug");
		formData.put(BugzillaSubmitter.ESTIMATED_TIME, "16");
		formData.put(BugzillaSubmitter.STATUS_WHITEBOARD, "230407-240407");
		formData.put(BugzillaSubmitter.BLOCKS, "1,2");

		try {
			String bugId = new BugzillaSubmitter().submit(
					"http://jakubpc/bugzilla-3.0", "j.neubauer@cz.gmc.net", "heslo",
					formData);
			System.out.println("Submitted bug #" + bugId);
		} catch (UnknownHostException e) {
			System.err.println("Unknown host: " + e.getMessage());
			System.exit(2);
		} catch (FileNotFoundException e) {
			System.err.println("Page not found: " + e.getMessage());
			System.exit(2);
		} catch (IOException e) {
			System.err.println(e.getClass().getSimpleName() + ": " + e.getMessage());
			System.exit(2);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.exit(2);
		}
	}
	
	public void testQuery() {
		try {
			Map<String, Map<String, String>> result = new BugzillaSubmitter().query("http://wi", "j.neubauer@gmc.net", "abba", new String[] {"31396", "31397"});
			
			System.out.println(result.toString());
		} catch (MalformedURLException e) {
			System.err.println(e.getMessage());
			System.exit(2);
		} catch (IOException e) {
			System.err.println(e.getMessage());
			System.exit(2);
		} catch (SAXException e) {
			System.err.println(e.getMessage());
			System.exit(2);
		} catch (ParserConfigurationException e) {
			System.err.println(e.getMessage());
			System.exit(2);
		}
	}
	

}
