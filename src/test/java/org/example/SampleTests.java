package org.example;

import org.approvaltests.Approvals;
import org.approvaltests.JsonJacksonApprovals;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SampleTests
{
    @Test
    void testNormalJunit()
    {
        assertEquals(5, 3+2);
    }
    @Test
    void testWithApprovalTests()
    {
        Approvals.verify("Hello World");
    }
    /**
     *  note: this requires GSON which is currently added in the pom.xml file.
     *  This is only required if you want to use the VerifyAsJson.
     **/
    @Test
    @Disabled("Run manually if needed")
    void testJson()
    {
        Person hero = new Person("jayne", "cobb", true, 38);
        JsonJacksonApprovals.verifyAsJson(hero);
    }
}