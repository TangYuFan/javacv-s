package com.ist.EasyPr;


import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.opencv.imgproc.Imgproc;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.opencv.core.Core.BORDER_DEFAULT;

/**
 * @desc : 车牌识别
 * @auth : TYF
 * @data : 2019/5/21 23:28
 */
public class t_1_1 {

    //显示mat图片
    public static void showMatImage(Mat mat,String tit){
        ToMat converter = new ToMat();
        CanvasFrame canvas = new CanvasFrame(tit, 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }

    //1.预处理
    public static Mat preProcess(String path){
        //读取图片
        Mat image = imread(path, IMREAD_COLOR);
        Mat source = imread(path, IMREAD_COLOR);
        //高斯模糊(5*5模版)
        Size size= new Size(5,5);
        opencv_imgproc.GaussianBlur(image,image,size,0);
        //灰度化
        opencv_imgproc.cvtColor(image, image,Imgproc.COLOR_BGR2GRAY);
        //边缘检测
        opencv_imgproc.Sobel(image, image, CV_8U, 1, 0, 3, 1, 0, BORDER_DEFAULT);
        //二值化
        opencv_imgproc.threshold(image, image, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);
        //闭操作
        Mat ele = opencv_imgproc.getStructuringElement(MORPH_RECT,new Size(17,3));
        opencv_imgproc.morphologyEx(image, image, CV_MOP_CLOSE, ele);
        //提取轮廓
        MatVector  mats = new MatVector();//筛选前轮廓
        opencv_imgproc.findContours(image,mats,CV_RETR_EXTERNAL,CV_CHAIN_APPROX_NONE);
        //轮廓筛选
        MatVector res = new MatVector();//筛选后轮廓(得到的是二通道)
        Mat[] _mats = mats.get();
        for(int i=0;i<_mats.length;i++){
            Rect tem = opencv_imgproc.boundingRect(_mats[i]);//外框面积
            float w = (float)tem.width();//外框宽
            float h = (float)tem.height();//外框高
            double r = (double)tem.area();//外框面积
            double a = opencv_imgproc.contourArea(_mats[i]);//轮廓面积
            //外框宽高比大于2.5小于5
            if(((w/h)>2.5?true:false) &&((w/h)<5?true:false)){
                res.push_back(_mats[i]);
            }
        }
        //轮廓描边
        opencv_imgproc.drawContours(source,res,-1,new Scalar(0,255));
        //显示
        //showMatImage(source,"轮廓");

        //将筛选后轮廓保存下来
        Mat[] lines = res.get();
        for(int i=0;i<lines.length;i++){
            //轮廓最小矩形
            RotatedRect rect = opencv_imgproc.minAreaRect(lines[i]);
            //得到四个顶点
            Point2f point2f = new Point2f(4);
            rect.points(point2f);

        }


        return null;
    }


    public static void main(String args[]){

        preProcess("E:\\work\\easypr\\test_data\\car_pic.png");

    }

}
