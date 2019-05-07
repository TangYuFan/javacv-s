package com.ist.demo;


import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;

import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

/**
 * @desc : frame(图像帧)/mat(矩阵)转换,mat矩阵图片显示
 * @auth : TYF
 * @date : 2019-05-07 - 9:02
 */
public class t_6 {

    //mat方式读取本地图片
    public static Mat readImage(String filePath){
        Mat  image = imread(filePath, IMREAD_COLOR);
        if (image==null||image.empty()) {
            return null;
        }
        return image;
    }


    //显示mat矩阵对应的图片
    public static void showImage(Mat mat){
        //opencv自带的显示模块，跨平台性欠佳，转为Java2D图像类型进行显示
        ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame("标题", 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }

    //保存mat矩阵对应的图片到指定路径
    public static boolean saveMatImage(Mat mat,String filePath){
        try {
            //将mat转为java的BufferedImage
            ToMat convert= new ToMat();
            Frame frame= convert.convert(mat);
            Java2DFrameConverter java2dFrameConverter = new Java2DFrameConverter();
            BufferedImage bufferedImage= java2dFrameConverter.convert(frame);
            ImageIO.write(bufferedImage, "PNG", new File(filePath));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }



    public static void main(String args[]){

        //读取图片转mat
        Mat mat = t_6.readImage("D:\\1.jpg");
        //显示mat图片
        t_6.showImage(mat);
        //保存mat图片
        t_6.saveMatImage(mat,"D:\\2.jpg");
    }

}
