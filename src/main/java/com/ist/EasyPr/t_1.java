package com.ist.EasyPr;


import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.opencv.imgproc.Imgproc;
import javax.swing.*;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.opencv.core.Core.BORDER_DEFAULT;

/**
 * @desc : 图片与处理，得到车牌可能区域的mat集合
 * @auth : TYF
 * @data : 2019/5/21 23:28
 */
public class t_1 {

    //显示mat图片
    public static void showMatImage(Mat mat,String tit){
        ToMat converter = new ToMat();
        CanvasFrame canvas = new CanvasFrame(tit, 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }

    //1.预处理
    public static MatVector preProcess(String path){
        //读取图片
        Mat image = imread(path, IMREAD_COLOR);
        Mat source = imread(path, IMREAD_COLOR);
        Mat _source = imread(path, IMREAD_COLOR);
        //截取线圈内 todo
        //高斯模糊(5*5模版)
        Size size= new Size(5,5);
        opencv_imgproc.GaussianBlur(image,image,size,0);
        //灰度化
        opencv_imgproc.cvtColor(image, image,Imgproc.COLOR_BGR2GRAY);
        //sobel边缘检测
        opencv_imgproc.Sobel(image, image, CV_8U, 1, 0, 3, 1, 0, BORDER_DEFAULT);
        //二值化
        opencv_imgproc.threshold(image, image, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);
        //闭操作
        Mat ele = opencv_imgproc.getStructuringElement(MORPH_RECT,new Size(17,3));
        opencv_imgproc.morphologyEx(image, image, CV_MOP_CLOSE, ele);
        //筛选前轮廓
        MatVector  mats = new MatVector();
        opencv_imgproc.findContours(image,mats,CV_RETR_EXTERNAL,CV_CHAIN_APPROX_NONE);
        opencv_imgproc.drawContours(source,mats,-1,new Scalar(0,255));//原图轮廓描边
        showMatImage(source,"轮廓");
        //轮廓筛选并截取
        MatVector res = new MatVector();
        Mat[] _mats = mats.get();
        for(int i=0;i<_mats.length;i++){
            //外框宽高
            float w = (float)opencv_imgproc.boundingRect(_mats[i]).width();
            float h = (float)opencv_imgproc.boundingRect(_mats[i]).height();
            double s1 = opencv_imgproc.contourArea(_mats[i]);
            //外框宽高比
            if(((w/h)>2.5?true:false) &&((w/h)<5?true:false)){
                RotatedRect rect = opencv_imgproc.minAreaRect(_mats[i]);//最小外接矩形
                //最小外接矩形面积和轮廓面积比
                int s2 = rect.boundingRect().area();
                if(((s2/s1)>1.2?true:false) &&((s2/s1)<2?true:false)){
                    //摆正 todo
                    Size s= new Size(240,60);
                    Mat _m = new Mat(s,CV_32S);
                    resize(new Mat(_source,rect.boundingRect()),_m,s);//从原图截取为mat、修改尺寸、保存到_m
                    res.push_back(_m);//放入结果集
                }
            }
        }
        return res;
    }

    public static void main(String args[]){
        //返回可能包含有车牌的截图
        MatVector mats = preProcess("D:\\my_easypr\\test_data\\2.jpg");
        //循环显示
        for(int i=0;i<mats.get().length;i++){
            showMatImage(mats.get()[i],"车牌域");
        }
    }

}
