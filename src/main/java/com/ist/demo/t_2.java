package com.ist.demo;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;


/**
 * @desc : 获取rtsp流，帧图片弹窗显示
 * @auth : TYF
 * @date : 2019-05-06 - 16:39
 */
public class t_2 {


    public static void frameRecord(String inputFile) throws Exception{

        //rtsp视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.setOption("rtsp_transport","tcp");
        grabber.setFrameRate(30);
        grabber.setVideoBitrate(3000000);
        try {
            grabber.start();
            //新建一个窗口
            CanvasFrame canvas = new CanvasFrame("摄像头");
            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvas.setAlwaysOnTop(true);
            Frame frame = grabber.grabFrame();
            while (frame!= null) {
                //弹窗按帧显示
                canvas.showImage(grabber.grab());
            }
            grabber.stop();
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
        //rtsp://192.168.1.125:556/0
        String inputFile = "C:\\Users\\pc\\Desktop\\aaa.mp4";//inputFile可以是本地视频文件、rtsp地址、rtmp地址
        frameRecord(inputFile);
    }

}
