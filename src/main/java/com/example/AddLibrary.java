package com.example;

import com.sun.jna.Library;

/**
 * 映射C库的类
 *
 * @author sg-y
 * @version v1.0
 */
public interface AddLibrary extends Library {

    /**
     * 映射C库中的函数
     *
     * @param a p
     * @param b p
     * @return v
     */
    String add(int a, int b);

}
