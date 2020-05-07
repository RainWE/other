package com.other.io;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * @Auther:cdx
 * @Date:2020-05-07
 * @Description:com.other.io
 * @Version:1.0
 * IO操作步骤
 * //1.创建源
* //2.选择流
* //3.操作
* //4.释放资源
 */
public class IOTest01 {

    public static void main(String[] args) {
        //1.创建源
        File file = new File("D:\\IntelliJ IDEAworkplace2\\项目\\other\\src\\main\\a.txt");
        try{
            System.out.println(file.getPath());
            System.out.println(file.getAbsolutePath());
            //2.选择流
            InputStream inputStream = new FileInputStream(file);
            //3.操作
            int date1 = inputStream.read();
            System.out.println((char)date1);
            //4.释放资源
            inputStream.close();
        }catch (Exception e){

        }
    }
}
