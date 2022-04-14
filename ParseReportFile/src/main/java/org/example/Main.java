package org.example;

import org.entity.LoadPersistent;
import org.entity.LoadTemp;

import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class Main {

    static ResourceBundle rb;

    static {
        rb = ResourceBundle.getBundle("application");
    }

    public static void main(String[] args) {

        new LoadTemp(new String(rb.getString("source_directory").getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8));
        new LoadPersistent();

    }

}
