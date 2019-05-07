package com.ist.javacv;


import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect.*;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import javax.swing.*;

import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgproc.equalizeHist;

/**
 * @desc : 人脸检测
 * @auth : TYF
 * @data : 2019-03-08 - 15:33
 */
public class t_8 {

    //内存溢出
    static {
        System.setProperty("org.bytedeco.javacpp.maxphysicalbytes","0");
        System.setProperty("org.bytedeco.javacpp.maxbytes","0");
    }

    //人脸检测
    public static Mat detectFace(Mat src)
    {
        //面部识别级联分类器
        CascadeClassifier cascade = new CascadeClassifier("E:\\work\\opencv\\opencv-master\\data\\lbpcascades\\lbpcascade_frontalface.xml");
        //矢量图初始化
        Mat grayscr=new Mat();
        //彩图灰度化
        cvtColor(src,grayscr,COLOR_BGRA2GRAY);
        //均衡化直方图
        equalizeHist(grayscr,grayscr);
        RectVector faces=new RectVector();
        cascade.detectMultiScale(grayscr, faces);
        //size就是检测到的人脸个数
        for(int i=0;i<faces.size();i++)
        {
            Rect face_i=faces.get(i);
            rectangle(src, face_i, new Scalar(0, 0, 255, 1));
        }
        //显示释放否则内存溢出
        grayscr.release();
        return src;
    }

    //读取视频
    public static void frameRecord(String inputFile) throws Exception{
        //读取视频
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        try {
            grabber.start();
            //new窗口
            CanvasFrame canvas = new CanvasFrame("视频");
            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvas.setAlwaysOnTop(true);
            Frame frame = grabber.grabImage();
            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
            while (true) {
                if(!canvas.isEnabled())
                {//窗口是否关闭
                    grabber.stop();
                    System.exit(0);
                }
                //frame转mat
                Mat scr=converter.convertToMat(frame);
                //人脸检测
                detectFace(scr);
                //mat转frame
                frame =converter.convert(scr);
                //按帧显示
                canvas.showImage(grabber.grabFrame());
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


    public static void main(String[] args) throws Exception{
        //本地视频文件、rtsp地址、rtmp地址
        String inputFile = "E:\\work\\vlc\\test.mp4";
        frameRecord(inputFile);
    }

}
