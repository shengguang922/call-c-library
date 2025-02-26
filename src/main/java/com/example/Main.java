package com.example;

import com.sun.jna.Native;

/**
 * 运行调用C库测试类
 *
 * @author sg-y
 * @version v1.0
 */
public class Main {
    public static void main(String[] args) {
        // 加载动态库（替换成你的文件绝对路径）
        System.load("/Users/yeshengguang/Desktop/ysg/demo/call-c-library/lib/libadd.dylib");

        // 从动态库中取出add库（这里不需要写文件的前缀lib和文件扩展名）
        AddLibrary addLibrary = Native.load("add", AddLibrary.class);

        // 调用C库中的add函数
        String result = addLibrary.add(5, 3);

        // 输出结果
        System.out.println("result: " + result);
    }
}
