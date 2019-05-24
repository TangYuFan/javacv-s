package com.ist.EasyPr;


import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.MatVector;
import java.io.File;

import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;

/**
 * @desc : 常用的工具方法
 * @auth : TYF
 * @date : 2019-05-24 - 11:42
 */
public class util {

    //文件重命名
    public static void fileReName(String path,String path2){

        File files = new File(path);
        File[] pics = files.listFiles();
        for(int i=0;i<pics.length;i++){
            pics[i].renameTo(new File(path2+"\\"+i+".jpg"));
        }

    }

    public static void main(String args[]){

//        int k = 1;
//        for(int i=0;i<700;i++){
//            //预处理
//            MatVector mats = t_1.preProcess("D:\\my_easypr\\pics\\"+i+".jpg");
//            //循环保存
//            for(int j=0;j<mats.get().length;j++){
//                opencv_core.Mat te = mats.get()[j];
//                imwrite("D:\\my_easypr\\trainData\\"+k+".png",te);
//                k++;
//            }
//        }

        fileReName("D:\\my_easypr\\testData\\0","D:\\my_easypr\\testData\\0_0");

    }


}
