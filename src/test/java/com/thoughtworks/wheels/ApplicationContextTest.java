package com.thoughtworks.wheels;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

public class ApplicationContextTest {

    private ApplicationContext applicationContext;
    private File beanFile;

    @Before
    public void setUp() throws Exception {
        beanFile = new File("src/test/resources/beans.xml");
        applicationContext = new ApplicationContext("src/test/resources/beans.xml");
    }

    @Test
    public void get_bean_id_and_class_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //when
        Element bean = applicationContext.getBeanElementByName("customer");

        //then
        Assert.assertThat(bean.getAttribute("id"), Matchers.is("customer"));
        Assert.assertThat(bean.getAttribute("class"), Matchers.is("com.thoughtworks.wheels.Customer"));
    }

    @Test
    public void put_beans_from_xml_into_map() throws ParserConfigurationException, SAXException, IOException {
        //given
        HashMap expectedMap = new HashMap<>();
        expectedMap.put("customer", 0);
        expectedMap.put("name", 1);

        //when
        applicationContext.putBeanIntoBeanMap();

        //then
        Assert.assertEquals(expectedMap, applicationContext.BEAN_MAP);
    }

    @Test
    public void get_bean_constructor_args_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //when
        Element constructorArgs = applicationContext.getConstructorArgsElement("customer");

        //then
        Assert.assertThat(constructorArgs.getAttribute("value"), Matchers.is("0001"));
    }

    @Test
    public void generate_a_bean_according_to_xml() throws Exception {

        //when
        Name name = (Name) applicationContext.getBean("name");

        //then
        Assert.assertThat(name.getFirstName(), Matchers.is("Ming"));
        Assert.assertThat(name.getLastName(), Matchers.is("Zhao"));
        Assert.assertThat(name.getNickName(), Matchers.is("xiaoming"));
    }
}
