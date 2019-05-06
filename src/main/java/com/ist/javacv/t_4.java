package com.ist.javacv;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.imageio.ImageIO;
import javax.swing.JFrame;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_imgcodecs;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import static org.bytedeco.javacpp.opencv_core.FONT_HERSHEY_PLAIN;
import static org.bytedeco.javacpp.opencv_core.flip;
import static org.bytedeco.javacpp.opencv_highgui.waitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;

import org.bytedeco.javacpp.opencv_core.Point;
import org.bytedeco.javacpp.opencv_core.Scalar;
import org.bytedeco.javacpp.opencv_imgproc;


/**
 * @desc : 图像二值化
 * @auth : TYF
 * @data : 2019-03-12 - 16:59
 */
public class t_4 {

    //显示图像
    public static void imShow(Mat mat,String title) {
        //opencv自带的显示模块，跨平台性欠佳，转为Java2D图像类型进行显示
        ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame(title, 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));

    }


    //保存mat到指定路径
    public static boolean imWrite(Mat mat,String filePath){
        //不包含中文，直接使用opencv原生方法进行保存
        if(!containChinese(filePath)){
            return opencv_imgcodecs.imwrite(filePath, mat);
        }
        try {
            /**
             * 将mat转为java的BufferedImage
             */
            ToMat convert= new ToMat();
            Frame frame= convert.convert(mat);
            Java2DFrameConverter java2dFrameConverter = new Java2DFrameConverter();
            BufferedImage bufferedImage= java2dFrameConverter.convert(frame);
            ImageIO.write(bufferedImage, "PNG", new File(filePath));
            return true;
        } catch (Exception e) {
            System.out.println("保存文件出现异常:"+filePath);
            e.printStackTrace();
        }
        return false;
    }

    //判断是否包含中文
    private static boolean containChinese(String inputString){
        //四段范围，包含全面
        String regex ="[\\u4E00-\\u9FA5\\u2E80-\\uA4CF\\uF900-\\uFAFF\\uFE30-\\uFE4F]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(inputString);
        return matcher.find();
    }



//    public static void main(String args[]){
//
//            t_4 obj = new t_4();
//
//            //以彩色模式读取图像
//            Mat  image = imread("D:\\1.jpg", IMREAD_COLOR);
//            if (image==null||image.empty()) {
//                System.out.println("读取图像失败，图像为空");
//                return;
//            }
//
//            System.out.println("图像宽x高" + image.cols() + " x " + image.rows());
//
//            //显示图像
//            obj.imShow(image, "原始图像");
//
//            //创建空mat，保存处理图像
//            Mat result = new Mat();
//            int flipCode=1;
//
//            //水平翻转
//            flip(image, result, flipCode);
//            //显示处理过的图像
//            obj.imShow(result, "水平翻转");
//
//            //保存图像
//            obj.imWrite(result, "data/javacv/lakeResult.jpg");
//
//            //克隆图像
//            Mat imageCircle = image.clone();
//
//
//            //图像上画圆
//            circle(imageCircle, // 目标图像
//                    new Point(420, 150), // 圆心坐标
//                    65, // radius
//                    new Scalar(0,200,0,0), // 颜色，绿色
//                    2, // 线宽
//                    8, // 8-connected line
//                    0); // shift
//
//            opencv_imgproc.putText(imageCircle, //目标图像
//                    "Lake and Tower", // 文本内容(不可包含中文)
//                    new Point(460, 200), // 文本起始位置坐标
//                    FONT_HERSHEY_PLAIN, // 字体类型
//                    2.0, // 字号大小
//                    new Scalar(0,255,0,3), //文本颜色，绿色
//                    1, // 文本字体线宽
//                    8, // 线形.
//                    false); //控制文本走向
//            obj.imShow(imageCircle, "画圆mark");
//
//
//            //图像二值化
//            Mat gray=new Mat();
//            cvtColor(image,gray,COLOR_RGB2GRAY);		//彩色图像转为灰度图像
//            obj.imShow(gray, "灰度图像");
//            Mat bin=new Mat();
//            threshold(gray,bin,120,255,THRESH_TOZERO); 	//图像二值化
//            obj.imShow(bin, "二值图像");
//            waitKey(0);
//
//        }


    //人脸检测
    public Mat detectFace(Mat src)
    {
        //面部识别级联分类器
        opencv_objdetect.CascadeClassifier cascade = new opencv_objdetect.CascadeClassifier("E:\\work\\opencv\\opencv-master\\data\\lbpcascades\\lbpcascade_frontalface.xml");
        //矢量图初始化
        Mat grayscr=new Mat();
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
            rectangle(src, face_i, new Scalar(0, 0, 255, 1));
        }
        //显示释放否则内存溢出
        grayscr.release();
        return src;
    }


        //用一张图片测试人脸检测
        public void face(){

            //mat方式读取图片
            Mat  image = imread("D:\\1.jpg", IMREAD_COLOR);
            if (image==null||image.empty()) {
                System.out.println("读取图像失败，图像为空");
                return;
            }

            //显示图像
            this.imShow(image, "原始图像");

            //人脸检测
            this.detectFace(image);

            //显示图像
            this.imShow(image, "识别图像");


        }



        public static void main(String args[]){
            t_4 obj = new t_4();
            obj.face();
        }



}
