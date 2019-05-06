package com.ist.demo;


import org.bytedeco.javacpp.opencv_core.Mat;

import static org.bytedeco.javacpp.opencv_highgui.imshow;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

/**
 * @desc : 读取图片
 * @auth : TYF
 * @data : 2019-03-08 - 15:32
 */
public class t_4 {

    public static void main(String[] args) {
        //读取原始图片
        Mat image = imread("D:\\1.jpg");
        if (image.empty()) {
            System.err.println("no pic！");
            return;
        }
        //显示图片
        imshow("pic", image);

        //无限等待按键按下
        waitKey(0);
    }


}
