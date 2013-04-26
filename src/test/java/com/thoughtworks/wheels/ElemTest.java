package com.thoughtworks.wheels;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import java.io.File;

/**
 * User: fff
 * Date: 4/25/13
 * Time: 11:09 PM
 */
public class ElemTest {

    private Element element;
    private File beanFile;

    @Before
    public void setUp() throws Exception {
        beanFile = new File("src/test/resources/test.xml");
        element = (Element) XmlParser.parseXml(beanFile).getElementsByTagName("bean").item(0);
    }

    @Test
    public void get_element_id() throws Exception {
        final Elem elem = new Elem(element);
        Assert.assertThat(elem.getId(), Matchers.is("customer"));
    }


}
