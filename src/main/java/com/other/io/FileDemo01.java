package com.other.io;

import java.io.File;

/**
 * @Auther:cdx
 * @Date:2020-05-07
 * @Description:com.other.io
 * @Version:1.0
 */
public class FileDemo01 {
    public static void main(String[] args) {
//    File(File parent, String child)
//    从父抽象路径名和子路径名字符串创建新的 File实例。

//    File(String pathname)
//    通过将给定的路径名字符串转换为抽象路径名来创建新的 File实例。
        File file = new File("D:\\IntelliJ IDEAworkplace2\\项目\\other\\src\\main\\resources\\img\\preview2.jpg");

//    File(String parent, String child)
//    从父路径名字符串和子路径名字符串创建新的 File实例。
        File file1 = new File("D:/IntelliJ IDEAworkplace2/项目/other/src","/main/resources/img/preview2.jpg");

//    File(URI uri)
//    通过将给定的 file: URI转换为抽象路径名来创建新的 File实例。
        File file2 = new File("网络路径");

    }
}
