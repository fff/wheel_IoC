package com.thoughtworks.wheels;

import com.thoughtworks.wheels.beans.Customer;
import com.thoughtworks.wheels.beans.CustomerName;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;

public class ElfContainerTest {

    private ElfContainer elfContainer;

    @Before
    public void setUp() throws Exception {
        elfContainer = new ElfContainer("src/test/resources/beans.xml").start();
    }

    @Test
    public void get_bean_id_and_class_from_xml() throws ParserConfigurationException, SAXException, IOException {

        //when
        Element bean = elfContainer.getWrapperById("customer").element;

        //then
        Assert.assertThat(bean.getAttribute("id"), Matchers.is("customer"));
        Assert.assertThat(bean.getAttribute("class"), Matchers.is("com.thoughtworks.wheels.beans.Customer"));
    }

    @Test
    public void new_a_simple_object_with_provided_data() throws Exception {
        //given
        Object name = null;

        //when
        name = elfContainer.getBean("customerName");

        //then
        Assert.assertThat(((CustomerName) name).getFirst(), Matchers.is("Ming"));
        Assert.assertThat(((CustomerName) name).getLast(), Matchers.is("Zhao"));
        Assert.assertThat(((CustomerName) name).getNick(), Matchers.is("xiaoming"));
    }

    @Test
    public void initial_ref() throws Exception {
        //given
        //when
        final Customer customer = elfContainer.getBean("customer");

        //then
        Assert.assertThat(customer.getCustomerName().getFirst(), Matchers.is("Ming"));
        Assert.assertThat(customer.getCustomerName().getLast(), Matchers.is("Zhao"));
        Assert.assertThat(customer.getCustomerName().getNick(), Matchers.is("xiaoming"));

    }

    @Test
    public void ref_with_ref() throws Exception {
        //when
        final Customer customer = elfContainer.getBean("customer");

        //then
        Assert.assertThat(customer.getCustomerName().getNameFormat().getDelimiter(), Matchers.is("-"));
    }

    @Test
    public void new_bean_by_constructor() throws Exception {
        //when
        final Customer customer = elfContainer.getBean("zhaoMing");

        //then
        Assert.assertThat(customer.getCustomerId(), Matchers.is("0001"));
        Assert.assertThat(customer.getCustomerName().getFirst(), Matchers.is("Ming"));
    }

    @Test
    public void add_child_container() throws Exception {
        //given
        ElfContainer father = new ElfContainer("src/test/resources/beans.xml");
        ElfContainer child = new ElfContainer("src/test/resources/test.xml");

        //when
        father.addChild(child).start();
        Customer customer = child.getBean("customer");

        //then
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getCustomerName());
        Assert.assertThat(customer.getCustomerName().getFirst(), Matchers.is("Ming"));
    }
}
