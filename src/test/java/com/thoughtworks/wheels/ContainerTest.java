package com.thoughtworks.wheels;

import org.junit.Before;
import org.junit.Test;

public class ContainerTest {

    private ElfContainer elfContainer;

    @Before
    public void setUp() {
        elfContainer = new ElfContainer("src/test/resources/test.xml");
    }

    @Test
    public void testName() throws Exception {

    }
}
