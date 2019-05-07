package com.ist.javacv;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.swing.*;

import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgproc.rectangle;

/**
 * @desc : 视频人脸检测
 * @auth : TYF
 * @data : 2019/5/7 21:06
 */
public class t_8 {

    //mat转frame
    public static Frame mat2frame(opencv_core.Mat mat){
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        return converter.convert(mat);
    }


    //人脸检测
    public static opencv_core.Mat detectFace(opencv_core.Mat src)
    {
        //面部识别级联分类器
        opencv_objdetect.CascadeClassifier cascade = new opencv_objdetect.CascadeClassifier("E:\\work\\opencv\\opencv-master\\data\\lbpcascades\\lbpcascade_frontalface.xml");
        //矢量图初始化
        opencv_core.Mat grayscr=new opencv_core.Mat();
        //彩图灰度化
        cvtColor(src,grayscr,COLOR_BGRA2GRAY);
        //均衡化直方图
        equalizeHist(grayscr,grayscr);
        opencv_core.RectVector faces=new opencv_core.RectVector();
        cascade.detectMultiScale(grayscr, faces);
        //size就是检测到的人脸个数
        for(int i=0;i<faces.size();i++)
        {
            opencv_core.Rect face_i=faces.get(i);
            rectangle(src, face_i, new opencv_core.Scalar(0, 0, 255, 1));
        }
        //显示释放否则内存溢出
        grayscr.release();
        return src;
    }


    //读取视频
    public static void showMp4(String inputFile) throws Exception{
        //读取视频
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        try {
            grabber.start();
            //new窗口
            CanvasFrame canvas = new CanvasFrame("人脸");
            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvas.setAlwaysOnTop(true);
            Frame frame = grabber.grabFrame();
            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
            while (frame!=null && true) {

                if(!canvas.isEnabled())
                {//窗口是否关闭
                    grabber.stop();
                    System.exit(0);
                }
                //frame转mat
                opencv_core.Mat scr = converter.convertToMat(grabber.grabImage());
                //人脸检测
                opencv_core.Mat de = detectFace(scr);
                //mat转frame
                canvas.showImage(mat2frame(de));
                //canvas.showImage(grabber.grabImage());
                System.out.println("show");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }finally {
            if (grabber != null) {
                grabber.stop();
            }
        }
    }



    public static void main(String args[]) throws Exception{

        //本地视频
        String in = "E:\\work\\vlc\\test.mp4";
        showMp4(in);

    }

}

