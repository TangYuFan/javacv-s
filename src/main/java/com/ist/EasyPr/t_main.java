package com.ist.EasyPr;


import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.MatVector;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import javax.swing.*;

/**
 * @desc :
 * @auth : TYF
 * @date : 2019-05-23 - 14:29
 */
public class t_main {

    //显示mat
    public static void showMatImage(opencv_core.Mat mat, String tit){
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame(tit, 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }
    //显示mat数组
    public static void showMatImages(MatVector vector){
        for(int i=0;i<vector.get().length;i++){
            opencv_core.Mat te = vector.get()[i];
            showMatImage(te,(i+1)+"");
        }
    }


    public static void main(String args[]){

        //1.图片预处理
        MatVector mats = t_1.preProcess("D:\\my_easypr\\test_data\\1.jpg");

        //showMatImages(mats);

        //2.分类
        MatVector cars = t_2.getCarPic(mats);
        System.out.println("数量:"+cars.get().length);



    }


}
