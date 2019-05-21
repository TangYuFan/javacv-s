package com.ist.EasyPr;


import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import org.opencv.imgproc.Imgproc;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.opencv.core.Core.BORDER_DEFAULT;

/**
*   @desc : 车辆图片预处理
*   @auth : TYF
*   @date : 2019-05-21 - 10:29
*/
public class t_1 {

    //图片转mat
    public static Mat imageToMat(String path){
        Mat image = imread(path, IMREAD_COLOR);
        if (image==null||image.empty()) {
            return null;
        }
        return image;
    }

    //显示mat图片
    public static void showMatImage(Mat mat,String tit){
        ToMat converter = new ToMat();
        CanvasFrame canvas = new CanvasFrame(tit, 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }

    //保存图片到path
    public static boolean saveMatImage(Mat mat, String path){
        try {
            //将mat转为java的BufferedImage
            ToMat convert= new ToMat();
            Frame frame= convert.convert(mat);
            Java2DFrameConverter java2dFrameConverter = new Java2DFrameConverter();
            BufferedImage bufferedImage= java2dFrameConverter.convert(frame);
            ImageIO.write(bufferedImage, "PNG", new File(path));
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //修改mat尺寸
    public static Mat reSize(Mat in){
        //图片转mat
        Mat m = in;
        //修改尺寸
        Size size= new Size(60,60);
        Mat _m = new Mat(size,CV_32S);
        resize(m,_m,size);
        return _m;
    }

    //高斯模糊(5*5模版)
    public static Mat gaussianBlur(Mat source){
        Size size= new Size(5,5);
        opencv_imgproc.GaussianBlur(source,source,size,0);
        return source;
    }

    //灰度化
    public static Mat grayTrans(Mat source){
        opencv_imgproc.cvtColor(source, source,Imgproc.COLOR_BGR2GRAY);
        return source;
    }

    //边缘检测(Sobel)
    public static Mat sobelTrans(Mat source){
        opencv_imgproc.Sobel(source, source, CV_8U, 1, 0, 3, 1, 0, BORDER_DEFAULT);
        return source;
    }

    //二值化
    public static Mat twMat(Mat source){
        opencv_imgproc.threshold(source, source, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);
        return source;
    }

    //闭操作
    public static Mat morphologyExClose(Mat source){
        Mat ele = opencv_imgproc.getStructuringElement(MORPH_RECT,new Size(17,3));
        opencv_imgproc.morphologyEx(source, source, CV_MOP_CLOSE, ele);
        return source;
    }

    //提取轮廓
    public static MatVector getOutLineMat(Mat source){
        MatVector  mats = new MatVector();
        opencv_imgproc.findContours(source,mats,CV_RETR_EXTERNAL,CV_CHAIN_APPROX_NONE);
        return mats;
    }

    //轮廓筛选(矩形检测)
    public static MatVector outLineSelect(MatVector vector){
        MatVector res = new MatVector();
        Mat[] mats = vector.get();
        for(int i=0;i<mats.length;i++){
            //取外框
            double r = (double)opencv_imgproc.boundingRect(mats[i]).area();
            //轮廓面积
            double a = opencv_imgproc.contourArea(mats[i]);
            //面积近似则判断为矩形
            double b = r/a;
            if((b<2)?true:false){
                res.push_back(mats[i]);
            }
        }
        return res;
    }


    //轮廓描边
    public static Mat drawOutLine(Mat source,MatVector lines){
        opencv_imgproc.drawContours(source,lines,-1,new Scalar(0,255));
        return source;
    }

    //轮廓转mat
    public static MatVector reShape(MatVector vector){
        MatVector res = new MatVector();
        Mat[] mats = vector.get();
        for(int i=0;i<mats.length;i++){
            //去掉偏折过大的mat
            //统一尺寸
            //保存为mat

        }
        return res;
    }

    public static void main(String args[]){

        //1.原始图片
        Mat mat = imageToMat("D:\\my_easypr\\test_data\\car_pic2.jpg");
        Mat source = imageToMat("D:\\my_easypr\\test_data\\car_pic2.jpg");
        //showMatImage(mat,"原始");

        //2.高斯模糊
        Mat gb_mat = gaussianBlur(mat);
        //showMatImage(gb_mat,"高斯模糊");

        //3.灰度化
        Mat gr_mat = grayTrans(gb_mat);
        //showMatImage(gr_mat,"灰度化");

        //4.边缘检测(Sobel)
        Mat sb_mat = sobelTrans(gr_mat);
        //showMatImage(sb_mat,"边缘检测");

        //5.二值化
        Mat tw_mat = twMat(sb_mat);
        //showMatImage(tw_mat,"二值化");

        //6.闭操作
        Mat cl_mat = morphologyExClose(tw_mat);
        //showMatImage(cl_mat,"闭操作");

        //8.筛选矩形
        MatVector outLines = getOutLineMat(cl_mat);
        MatVector resLines = outLineSelect(outLines);

        //9.轮廓描边
        Mat li_mat = drawOutLine(source,resLines);
        //showMatImage(li_mat,"轮廓");

        //10.轮廓保存为mat得到正负样本
        reShape(resLines);


    }


}
