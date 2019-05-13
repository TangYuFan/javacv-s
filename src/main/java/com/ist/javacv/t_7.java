package com.ist.javacv;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.bytedeco.javacv.OpenCVFrameConverter.ToMat;
import javax.swing.*;

import static org.bytedeco.javacpp.opencv_core.CV_32S;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgcodecs.imwrite;
import static org.bytedeco.javacpp.opencv_imgproc.*;

/**
 * @desc : 单个图片人脸检测，同尺寸保存人脸截图
 * @auth : TYF
 * @date : 2019-05-07 - 9:15
 */
public class t_7 {

    //mat方式读取本地图片
    public static Mat readImage(String filePath){
        Mat  image = imread(filePath, IMREAD_COLOR);
        if (image==null||image.empty()) {
            return null;
        }
        return image;
    }

    //显示mat矩阵对应的图片
    public static void showImage(Mat mat){
        ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame("人脸", 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }

    //人脸检测
    public static Mat detectFace(Mat src)
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
            //人脸画框
            rectangle(src, face_i, new Scalar(0, 0, 255, 1));
            //人脸截图保存到本地(保持相同像素)
            Mat face = new Mat(src,face_i);
            Size size= new Size(55,55);
            Mat _face = new Mat(size,CV_32S);
            resize(face,_face,size);
            imwrite("E:\\work\\atest\\face2\\"+i+".jpg",_face);
            //原图上加文本
            //int x  = face_i.x()-(face_i.width()/2);
            //int y  = face_i.y();
            //putText(src, "Tang", new Point(x,y), CV_FONT_ITALIC, 1, new Scalar(0, 0, 255, 1), 2, 0, false);
        }
        //显示释放否则内存溢出
        return src;
    }

    public static void main(String args[]){
        //读取图片转mat
        Mat mat = readImage("E:\\work\\atest\\KH{}_40S[`)}L~ARV05DTBJ.png");
        //显示mat图片
        showImage(mat);
        //人脸检测
        mat = detectFace(mat);
        //显示mat图片
        showImage(mat);
    }

}
