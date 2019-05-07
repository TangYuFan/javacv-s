package com.ist.javacv;

import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;



/**
 * @desc : 获取rtsp流，帧图片保存
 * @auth : TYF
 * @date : 2019-05-06 - 16:39
 */
public class t_1 {


    public static void frameRecord(String inputFile) throws Exception{

        //rtsp视频源
        FFmpegFrameGrabber grabber = new FFmpegFrameGrabber(inputFile);
        grabber.setOption("rtsp_transport","tcp");
        grabber.setFrameRate(30);
        grabber.setVideoBitrate(3000000);
        try {
            grabber.start();
            Frame frame = grabber.grabFrame();
            while (frame!= null) {
                //图片保存
                Java2DFrameConverter converter = new Java2DFrameConverter();
                BufferedImage bi = converter.getBufferedImage(frame);
                File output = new File("./target/pic_"+System.currentTimeMillis()+".png");
                try {
                    ImageIO.write(bi, "png", output);
                } catch (IOException e) {
                    e.printStackTrace();
                }
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
        String inputFile = "rtsp://192.168.1.125:556/0";//inputFile可以是本地视频文件、rtsp地址、rtmp地址
        frameRecord(inputFile);
    }

}
