package com.thoughtworks.wheels;

import com.thoughtworks.wheels.beans.CustomerName;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

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
        Element bean = applicationContext.getWrapperById("customer").element;

        //then
        Assert.assertThat(bean.getAttribute("id"), Matchers.is("customer"));
        Assert.assertThat(bean.getAttribute("class"), Matchers.is("com.thoughtworks.wheels.beans.Customer"));
    }

    @Test
    @Ignore
    public void get_bean_constructor_args_list_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //when
        ArrayList<Element> constructorArgs = applicationContext.getConstructorArgsList("customer");

        //then
        Assert.assertThat(constructorArgs.get(0).getAttribute("var"), Matchers.is("0001"));
        Assert.assertThat(constructorArgs.get(1).getAttribute("ref"), Matchers.is("customerName"));
    }

    @Test
    public void get_bean_properties_list_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //when
        ArrayList<Element> constructorArgs = applicationContext.getPropertiesList("customerName");

        //then
        Assert.assertThat(constructorArgs.get(0).getAttribute("name"), Matchers.is("first"));
        Assert.assertThat(constructorArgs.get(0).getAttribute("var"), Matchers.is("Ming"));
        Assert.assertThat(constructorArgs.get(1).getAttribute("name"), Matchers.is("last"));
        Assert.assertThat(constructorArgs.get(1).getAttribute("var"), Matchers.is("Zhao"));
        Assert.assertThat(constructorArgs.get(2).getAttribute("name"), Matchers.is("nick"));
        Assert.assertThat(constructorArgs.get(2).getAttribute("var"), Matchers.is("xiaoming"));
    }

    @Test
    public void new_a_simple_object_with_provided_data() throws Exception {
        //given
        Object name = null;

        //when
        name = applicationContext.getBean("customerName");

        //then
        Assert.assertThat(((CustomerName) name).getFirst(), Matchers.is("Ming"));
        Assert.assertThat(((CustomerName) name).getLast(), Matchers.is("Zhao"));
        Assert.assertThat(((CustomerName) name).getNick(), Matchers.is("xiaoming"));
    }


}
