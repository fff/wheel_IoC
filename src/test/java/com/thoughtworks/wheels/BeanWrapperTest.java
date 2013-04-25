package com.thoughtworks.wheels;

import com.thoughtworks.wheels.beans.CustomerName;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;

import java.io.File;
import java.lang.reflect.Method;

/**
 * User: fff
 * Date: 4/25/13
 * Time: 8:23 PM
 */
public class BeanWrapperTest {
    private File beanFile;
    private Element element;

    @Before
    public void setUp() throws Exception {
        beanFile = new File("src/test/resources/test_bean_wrapper.xml");
        element = (Element) XmlParser.parseXml(beanFile).getElementsByTagName("bean").item(0);
    }

    @Test
    public void testGetSetter() throws Exception {
        //when
        final Method setter = new BeanWrapper(element).getSetter("first", String.class);

        //then
        Assert.assertThat(setter.getName(), Matchers.is("setFirst"));
        Assert.assertThat(setter.getParameterCount(), Matchers.is(1));
        Assert.assertThat(setter.getParameterTypes()[0].getCanonicalName(), Matchers.is(String.class.getCanonicalName()));
    }

    @Test
    public void testSetProperty() throws Exception {
        //given
        final BeanWrapper<CustomerName> beanWrapper = new BeanWrapper(element);
        //when
        beanWrapper.setProperty("first", String.class, "feng");

        //then
        Assert.assertThat(beanWrapper.instance.getFirst(), Matchers.is("feng"));


    }

    @Test
    public void testInitialRefs() throws Exception {

        //given
        final BeanWrapper<CustomerName> wrapper = new BeanWrapper<>(element);

        //then
        Assert.assertThat(wrapper.getRefs().get("format"), Matchers.is("someFormat"));

    }
}
