# Java调用C/C++动态库技术可行性调研

## 前言

在Java中调用C++库是完全可行的，主要通过JNI来实现。JNI是一种编程框架，允许Java代码调用和接受从本地应用程序（如C或C++编写的应用程序）的调用。

注：本文案例使用的是JNA技术，JNA 简化了 JNI 的使用

## 调研环境

- 机器环境：Mac系统
- 开发工具：Idea、Jdk8、Maven
- 设计技术：JNA（用于调用C/C++动态库）
- 编译工具：用于编译C/C++代码为动态库
    - gcc（编译C代码）
    - g++（编译C++代码）

## 项目结构

项目结构简单，主要是C代码和编译后的动态库，以及Java调用动态库代码的示例。

```text
.
├──c-code  // 存放c代码目录
├──lib     // 存放C代码编译后的动态库文件
└──src
    └──main
        └──java
            └──com
                └──example // Java代码

```

## 调用过程代码及结果

### **1. C代码**

创建一个简单的代码，包含一个C函数，用于计算两个整数的和，并返回字符串结果。

**C代码：**​**​`add.c`​**

```c
#include <stdlib.h>
#include <stdio.h>

char* add(int a, int b) {
    int result = a + b;
    // 动态分配内存来存储描述字符串
    char* description = (char*)malloc(100 * sizeof(char));
    if (description == NULL) {
        return NULL;
    }
    // 格式化字符串并存储到分配的内存中
    snprintf(description, 100, "The result of %d + %d is %d", a, b, result);
    return description;
}
```

---

### **2. 编译代码为动态库**

#### **在Linux上**

```bash
# C代码
gcc -shared -fPIC -o libadd.so add.c
# C++代码
g++ -shared -fPIC -o libadd.so add.cpp
```

#### **在Windows上**

```bash
# C代码
gcc -shared -o add.dll add.c
# C++代码
g++ -shared -o add.dll add.cpp
```

#### **在macOS上**

```bash
# C代码
gcc -shared -fPIC -o libadd.dylib add.c
# C++代码
g++ -shared -fPIC -o libadd.dylib add.cpp
```

---

### **3. Java代码**

#### **添加JNA依赖**

在`pom.xml`中添加以下依赖：

```xml

<dependency>
    <groupId>net.java.dev.jna</groupId>
    <artifactId>jna</artifactId>
    <version>5.13.0</version>
</dependency>
```

#### **定义Java接口**

在Java中，定义一个接口来映射动态库中的函数。

**Java代码：**​**​`AddLibrary.java`​**

```java
import com.sun.jna.Library;

/**
 * 映射库的类
 *
 * @author sg-y
 * @version v1.0
 */
public interface AddLibrary extends Library {

    /**
     * 映射库中的函数
     *
     * @param a p
     * @param b p
     * @return v
     */
    String add(int a, int b);

}
```

#### **调用动态库**

编写Java代码调用动态库中的函数。

**Java代码：**​**​`Main.java`​**

```java
import com.sun.jna.Native;

/**
 * 运行调用库测试类
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

        // 调用库中的add函数
        String result = addLibrary.add(5, 3);

        // 输出结果
        System.out.println("result: " + result);
    }
}
```

---

### **4. 运行程序**

```text
result: The result of 5 + 3 is 8
```

---

## FAQ

### 返回结构体执行异常

如果动态库中返回C面向对象结构体，Java调用后会报错，由于没有返回对象成功例子，所有上面例子都是返回数据类型。

```text
#
# A fatal error has been detected by the Java Runtime Environment:
#
#  SIGSEGV (0xb) at pc=0x00000001084f6f3e, pid=75392, tid=0x0000000000001603
#
# JRE version: Java(TM) SE Runtime Environment (8.0_301-b09) (build 1.8.0_301-b09)
# Java VM: Java HotSpot(TM) 64-Bit Server VM (25.301-b09 mixed mode bsd-amd64 compressed oops)
# Problematic frame:
# C  [libadd.dylib+0xf3e]  add+0x1e
#
# Failed to write core dump. Core dumps have been disabled. To enable core dumping, try "ulimit -c unlimited" before starting Java again
#
# An error report file with more information is saved as:
# /Users/yeshengguang/Desktop/ysg/demo/call-c-library/hs_err_pid75392.log
#
# If you would like to submit a bug report, please visit:
#   http://bugreport.java.com/bugreport/crash.jsp
# The crash happened outside the Java Virtual Machine in native code.
# See problematic frame for where to report the bug.
#
```

提示开启ulimit -c unlimited核心转储，在同一个进程开启后，使用java命令执行代码依然报错，暂时没有合适的解决方案，如果有结构体建议使用JSON字符串返回。
