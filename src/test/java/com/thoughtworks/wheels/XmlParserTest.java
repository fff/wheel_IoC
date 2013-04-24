package com.thoughtworks.wheels;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlParserTest {

    private File file;

    @Before
    public void setUp() throws Exception {
        file = new File("src/test/resources/test.xml");

    }

    @Test
    public void get_specific_element_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //when
        String nodeName = XmlParser.parseXml(file).getDocumentElement().getNodeName();

        //then
        Assert.assertThat(nodeName, Matchers.is("beans"));
    }

    @Test
    public void get_bean_under_beans_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //given
        NodeList nodeList = XmlParser.parseXml(file).getDocumentElement().getElementsByTagName("bean");

        //when
        Element bean = (Element) (nodeList.item(0));

        //then
        Assert.assertThat(bean.getAttribute("id"), Matchers.is("customer"));
        Assert.assertThat(bean.getAttribute("class"), Matchers.is("com.thoughtworks.wheels.Customer"));
    }

    @Test
    public void get_attributes_of_bean_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //given
        NodeList nodeList = XmlParser.parseXml(file).getDocumentElement().getElementsByTagName("bean");

        //when
        Element constructorArgs = (Element) (((Element) (nodeList.item(0))).getElementsByTagName("constructor-args").item(0));

        //then
        Assert.assertThat(constructorArgs.getAttribute("var"), Matchers.is("0001"));
    }

}
