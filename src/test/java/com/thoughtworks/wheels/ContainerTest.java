package com.thoughtworks.wheels;

import org.junit.Before;
import org.junit.Test;

public class ContainerTest {

    private ApplicationContext applicationContext;

    @Before
    public void setUp() {
        applicationContext = new ApplicationContext("src/test/resources/test.xml");
    }

    @Test
    public void testName() throws Exception {

    }
}
