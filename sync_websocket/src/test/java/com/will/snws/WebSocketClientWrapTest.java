package com.will.snws;

import com.google.gson.Gson;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * Created by work on 11/07/2017.
 */
@RunWith(JUnit4.class)
public class WebSocketClientWrapTest {

    @Test
    public void test() {
        System.out.println(new Gson().toJson("hello world"));
    }

}