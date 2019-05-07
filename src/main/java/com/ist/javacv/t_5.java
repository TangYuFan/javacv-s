package com.ist.javacv;

import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.FrameGrabber.Exception;
import org.bytedeco.javacv.OpenCVFrameGrabber;

import javax.swing.*;

/**
 * @desc : 预览本机摄像头
 * @auth : TYF
 * @data : 2019-03-08 - 15:33
 */
public class t_5 {

    public static void main(String[] args) throws Exception, InterruptedException {
            OpenCVFrameGrabber grabber = new OpenCVFrameGrabber(0);
            grabber.start();   //开始获取摄像头数据
            CanvasFrame canvas = new CanvasFrame("摄像头");//新建一个窗口
            canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            canvas.setAlwaysOnTop(true);
                while(true)
                {
                    //窗口是否关闭
                    if(!canvas.isDisplayable())
                    {
                        //停止抓取
                        grabber.stop();
                        //退出
                        System.exit(2);
                    }
                    //frame是一帧视频图像
                    canvas.showImage(grabber.grab());
                    //50毫秒刷新一次图像
                    Thread.sleep(50);
                }
            }
}
