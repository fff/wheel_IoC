package com.thoughtworks.elf;

import com.thoughtworks.elf.beans.Customer;
import com.thoughtworks.elf.beans.CustomerName;
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
        elfContainer = new ElfContainer("src/test/resources/beans.xml");
    }

    @Test
    public void get_bean_id_and_class_from_xml() throws ParserConfigurationException, SAXException, IOException {
        //given

        //when
        Element bean = elfContainer.getWrapperById("customer").element;

        //then
        Assert.assertThat(bean.getAttribute("id"), Matchers.is("customer"));
        Assert.assertThat(bean.getAttribute("class"), Matchers.is("com.thoughtworks.elf.beans.Customer"));
    }

    @Test
    public void new_a_simple_object_with_provided_data() throws Exception {
        //given
        //when
        CustomerName name = elfContainer.getBean("customerName");

        //then
        Assert.assertThat(name.getFirst(), Matchers.is("Ming"));
        Assert.assertThat(name.getLast(), Matchers.is("Zhao"));
        Assert.assertThat(name.getNick(), Matchers.is("xiaoming"));
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
        //given
        //when
        final Customer customer = elfContainer.getBean("customer");

        //then
        Assert.assertThat(customer.getCustomerName().getNameFormat().getDelimiter(), Matchers.is("-"));
    }

    @Test
    public void new_bean_by_constructor() throws Exception {
        //given
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
        ElfContainer child = father.createChild("src/test/resources/test.xml");

        //when
        Customer customer = child.getBean("customer");

        //then
        Assert.assertNotNull(customer);
        Assert.assertNotNull(customer.getCustomerName());
        Assert.assertThat(customer.getCustomerName().getFirst(), Matchers.is("Ming"));
    }

    @Test(expected = CircleDependencyException.class)
    public void find_circle_dependency() throws Exception {
        //given
        //when
        ElfContainer friends = new ElfContainer("src/test/resources/test_circle.xml");
    }
}
