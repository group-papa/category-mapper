package uk.ac.cam.cl.retailcategorymapper.classifier.features;

import org.junit.Assert;
import org.junit.Test;

public class SanitizerTest {
    @Test
    public void testSanitize1() throws Exception {
        String input = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin et.";
        String output = "lorem ipsum dolor sit amet consectetur adipiscing elit proin et";
        Assert.assertEquals(output, Sanitizer.sanitize(input));
    }

    @Test
    public void testSanitize2() throws Exception {
        String input = " Lore m ipsu#~m    do_lor sit amet, consectetur    adipiscing elit. Pr^oin ; et.";
        String output = "lore m ipsu m do lor sit amet consectetur adipiscing elit pr oin et";
        Assert.assertEquals(output, Sanitizer.sanitize(input));
    }
}
