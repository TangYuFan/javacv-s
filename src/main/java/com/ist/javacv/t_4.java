package com.ist.javacv;


import org.bytedeco.javacpp.Loader;
import org.bytedeco.javacpp.avcodec;
import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.*;
import javax.swing.*;

/**
 * @desc : rtsp推流到rtmp服务器
 * @auth : TYF
 * @date : 2019-04-30 - 15:39
 */
public class t_4 {


    public static void recordPush(String inputFile,String outputFile,int v_rs) throws Exception{
        Loader.load(opencv_objdetect.class);
        long startTime=0;
        FrameGrabber grabber =FFmpegFrameGrabber.createDefault(inputFile);//rtsp流或者直接mp4视频也可以
        grabber.setOption("rtsp_transport", "tcp"); //默认udp丢包严重图像卡顿跳帧
        try {
            grabber.start();
        } catch (Exception e) {
            try {
                grabber.restart();
            } catch (Exception e1) {
                throw e;
            }
        }
        OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
        Frame grabframe =grabber.grab();
        opencv_core.IplImage grabbedImage =null;
        if(grabframe!=null){
            System.out.println("get 1 frame success !");
            grabbedImage = converter.convert(grabframe);
        }else{
            System.out.println("get 1 frame fail !");
        }
        //可以使用 opencv_imgcodecs.cvSaveImage("hello.jpg", grabbedImage);来保存图片
        FrameRecorder recorder;
        try {
            recorder = FrameRecorder.createDefault(outputFile, 1280, 720);
        } catch (FrameRecorder.Exception e) {
            throw e;
        }
        recorder.setVideoCodec(avcodec.AV_CODEC_ID_H264); // avcodec.AV_CODEC_ID_H264
        recorder.setFormat("flv");
        recorder.setFrameRate(v_rs);
        recorder.setGopSize(v_rs);
        System.out.println("rtsp push to rtmp ready !");
        try {
            recorder.start();
        } catch (FrameRecorder.Exception e) {
            try {
                System.out.println("recorder start fail ！");
                if(recorder!=null)
                {
                    System.out.println("recorder stop ！");
                    recorder.stop();
                    System.out.println("recorder start ！");
                    recorder.start();
                }
            } catch (FrameRecorder.Exception e1) {
                throw e;
            }
        }
        System.out.println("rtsp push to rtmp start !");
        CanvasFrame frame = new CanvasFrame("camera", CanvasFrame.getDefaultGamma() / grabber.getGamma());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setAlwaysOnTop(true);
        while (frame.isVisible() && (grabframe=grabber.grab()) != null) {
            System.out.println("push.. ");
            frame.showImage(grabframe);//本地弹窗
            grabbedImage = converter.convert(grabframe);
            Frame rotatedFrame = converter.convert(grabbedImage);
            if (startTime == 0) {
                startTime = System.currentTimeMillis();
            }
            recorder.setTimestamp(1000 * (System.currentTimeMillis() - startTime));//时间戳
            if(rotatedFrame!=null){
                recorder.record(rotatedFrame);
            }
            //Thread.sleep(40);
        }
        frame.dispose();
        recorder.stop();
        recorder.release();
        grabber.stop();
        System.exit(2);
    }

    public static void main(String[] args) throws Exception{
        String inputFile = "rtsp://192.168.1.125:556/0";//inputFile可以是本地视频文件、rtsp地址、rtmp地址
        String outputFile = "rtmp://192.168.1.201/live/pushFlow";//rtmp
        recordPush(inputFile, outputFile, 100);
    }

}
