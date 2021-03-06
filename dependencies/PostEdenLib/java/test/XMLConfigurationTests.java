/*
 *
 * Copyright 2007 Florin T.PATRASCU
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

import com.anthonyeden.lib.config.Configuration;
import com.anthonyeden.lib.config.ConfigurationException;
import com.anthonyeden.lib.config.MutableConfiguration;
import com.anthonyeden.lib.config.XMLConfiguration;
import junit.framework.TestCase;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.ByteArrayInputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

/**
 * unit tests for the {@link com.anthonyeden.lib.config.XMLConfiguration} class
 *
 * @author <a href="mailto:florin.patrascu@gmail.com">Florin T.PATRASCU</a>
 * @since $Revision$ (created: Jul 4, 2007 7:01:27 PM)
 */
public class XMLConfigurationTests extends TestCase {
    private static final Log log = LogFactory.getLog(XMLConfigurationTests.class);
    String hitsTagString = "<hits>Καλημέρα κόσμε</hits>";
    byte[] hitsTagByteArray = {'<', 'h', 'i', 't', 's', '>', 'a', 't', 'e', 'n',
            (byte) 0xE5, (byte) 0xBF, (byte) 0xAE, '<', '/', 'h', 'i', 't', 's', '>', '\n'};

    public void testParseString() throws ConfigurationException, UnsupportedEncodingException {
        log.info("parsing a String containing an xml structure....");

        Configuration c = new XMLConfiguration("hitsTagString", hitsTagString);
        assertEquals("Καλημέρα κόσμε", c.getValue());

        String s = new String(hitsTagByteArray, 0, hitsTagByteArray.length);
        c = new XMLConfiguration("s", s);
        assertEquals("aten忮", c.getValue());
    }

    public void testParseByteArray() throws ConfigurationException, UnsupportedEncodingException {
        log.info("parsing a byte[] containing an xml structure....");

        Configuration c = new XMLConfiguration("hitsTagByteArray",
                new ByteArrayInputStream(hitsTagByteArray));
        assertTrue("aten忮".equals(c.getValue()));
    }

    public void testSaveMethodAfterAddChild() throws ConfigurationException {
        //this was the case where when added new empty node the text was null and save failed
        log.info("trying to save the xml document after adding a child node to an xml document");
        XMLConfiguration c = new XMLConfiguration("testXML", hitsTagString);
        c.addChild("boo");
        String s = null;
        StringWriter out = new StringWriter();
        try {
            c.save(out);
            s = out.toString();
        } catch (Exception ignore) {
        }
        assertNotNull(s);
    }

    public void testAddRemoveChild() throws ConfigurationException {
        //try to add and remove a child node
        log.info("trying to save the xml document after adding a child node to an xml document");
        XMLConfiguration c = new XMLConfiguration("testXML", hitsTagString);
        c.addChild("boo");
        Configuration theSameChild = c.getChild("boo");
        assertNotNull(theSameChild);
        c.removeChild(theSameChild);
        assertNull(c.getChild("boo"));
    }

    public void testToXMLString() throws ConfigurationException {
        log.info("trying to compare outputs of two different toXMLString methods");
        XMLConfiguration c = new XMLConfiguration("testXML1", hitsTagString);
        XMLConfiguration d = new XMLConfiguration("testXML2", hitsTagString);
        assertEquals(c.toXMLString(), d.toXMLString());
    }

    public void testCopy() throws ConfigurationException {
        log.info("trying to compare outputs of two different toXMLString methods");
        XMLConfiguration c = new XMLConfiguration("testXML1", hitsTagString);
        c.addChild("boo", "foo");
        Configuration d = c.copy();
        assertEquals(c.getChildValue("boo"), d.getChildValue("boo"));
        ((MutableConfiguration) c.getChild("boo")).addChild("zchor", "smor");
        assertEquals(1, c.getChild("boo").getChildren().size());
        assertEquals(0, d.getChild("boo").getChildren().size());
        ((MutableConfiguration) d.getChild("boo")).addChild("aaa", "1a");
        ((MutableConfiguration) d.getChild("boo")).addChild("bbb", "2b");
        assertEquals(2, d.getChild("boo").getChildren().size());
        assertEquals(1, c.getChild("boo").getChildren().size());
    }

    public void testAddMutableChild() throws ConfigurationException {
        log.info("trying to ensure element is never added with a null value to mutable configuration");
        XMLConfiguration c = new XMLConfiguration("testXML", hitsTagString);
        MutableConfiguration boo = c.addChild("boo");
        boo.addChild("foo");
        assertNotNull(boo.getChildValue("foo"));
        assertNotNull(c.toXMLString());
    }

    public void testAddChildWithDefaultValue() throws ConfigurationException {
        log.info("trying to ensure that if child is added with null value that it indeed uses the provided default");
        XMLConfiguration c = new XMLConfiguration("testXML", hitsTagString);
        MutableConfiguration boo = c.addChild("boo", null, "Test");
        String s = null;
        try {
            s = c.toXMLString();
        } catch (Exception ignore) {
        }
        assertNotNull(s);
        assertEquals("Test", c.getChildValue("boo"));
    }
}
