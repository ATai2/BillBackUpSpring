package com.tuojin.bill.utils;

import java.io.File;

/**
 * Created by Administrator on 2017/1/12.
 */
public class Context {
    public static final String BASEPATH;

    static {
        File dir = new File("");
        BASEPATH = dir.getAbsoluteFile().getAbsolutePath();
        System.out.println(BASEPATH);
    }
}
