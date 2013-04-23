package com.thoughtworks.wheels;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;

public class XmlParserTest {

    @Test
    public void get_specific_element_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //given
        File beanFile = new File("src/test/resources/beans.xml");

        //when
        String nodeName = XmlParser.parseXml(beanFile).getDocumentElement().getNodeName();

        //then
        Assert.assertThat(nodeName, Matchers.is("beans"));
    }

    @Test
    public void get_bean_id_and_class_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //given
        File beanFile = new File("src/test/resources/beans.xml");
        NodeList nodeList = XmlParser.parseXml(beanFile).getDocumentElement().getElementsByTagName("bean");

        //when
        Element bean = (Element) (nodeList.item(0));

        //then
        Assert.assertThat(bean.getAttribute("id"), Matchers.is("customer"));
        Assert.assertThat(bean.getAttribute("class"), Matchers.is("com.thoughtworks.wheels.Customer"));
    }

    @Test
    public void get_bean_constructor_args_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //given
        File beanFile = new File("src/test/resources/beans.xml");
        NodeList nodeList = XmlParser.parseXml(beanFile).getDocumentElement().getElementsByTagName("bean");

        //when
        Element constructorArgs = (Element) (((Element) (nodeList.item(0))).getElementsByTagName("constructor-args").item(0));

        //then
        Assert.assertThat(constructorArgs.getAttribute("value"), Matchers.is("0001"));
    }
}
