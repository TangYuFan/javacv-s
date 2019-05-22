package com.ist.EasyPr;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.TermCriteria;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_ml.TrainData;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import javax.swing.*;
import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_EPS;
import static org.bytedeco.javacpp.opencv_core.CV_TERMCRIT_ITER;
import static org.bytedeco.javacpp.opencv_core.FileStorage.WRITE;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_ml.ROW_SAMPLE;
import static org.bytedeco.javacpp.opencv_ml.SVM.C_SVC;
import static org.bytedeco.javacpp.opencv_ml.SVM.RBF;
import static org.bytedeco.javacv.JavaCV.FLT_EPSILON;

/**
 * @desc : SVM对正负样本分类，得到包含车牌的图片 https://blog.csdn.net/yc5300891/article/details/82261814
 * @auth : TYF
 * @date : 2019-05-22 - 13:56
 */
public class t_2 {


    public static void test(){
        //创建svm
        SVM svm = SVM.create();
        //svm类型:C_SVC/C类支撑向量分类机,NU_SVC/类支撑向量分类机,ONE_CLASS/单分类器,EPS_SVR/类支撑向量回归机,NU_SVR/类支撑向量回归机
        svm.setType(C_SVC);
        //核函数类型:LINEAR/线性,POLY/多项式,RBF/径向量,SIGMOID/二层神经收集
        svm.setKernel(RBF);
        //POLY内核函数的参数degree
        svm.setDegree(0);
        //POLY/RBF/SIGMOID内核函数
        svm.setGamma(1);
        //POLY/SIGMOID内核函数的参数coef0
        svm.setCoef0(0);
        //NU_SVC/ONE_CLASS/NU_SVR类型SVM的参数
        svm.setNu(0);
        //EPS_SVR类型SVM的参数
        svm.setP(0);
        //C_SVC/EPS_SVR/NU_SVR类型SVM的参数C
        svm.setC(1);
        //C_SVC类型SVM的可选权重
        svm.setClassWeights(new Mat(0));
        //终止条件(类型、迭代最大次数、阈值)
        TermCriteria ct = new TermCriteria(CV_TERMCRIT_ITER+CV_TERMCRIT_EPS,1000,FLT_EPSILON);
        svm.setTermCriteria(ct);


        //train数据
        Mat trainMat = new Mat();//样本
        Mat labelMat = new Mat();//标签
        TrainData tData = TrainData.create(trainMat,ROW_SAMPLE,labelMat);//ROW_SAMPLE每行是一个样本

        //训练
        svm.train(tData);
        //保存结果
        svm.save("svm.xml");

        //测试
        SVM _svm = SVM.load("svm.xml");
        Mat testMat = new Mat();
        float result = _svm.predict(testMat);
        System.out.println("result:"+result);
    }


    //mat转xml
    public static void SaveMat(Mat mat,String path){
        opencv_core.FileStorage f = new opencv_core.FileStorage(path,WRITE);
        f.write("tag",mat);
        f.release();
    }

    //显示mat图片
    public static void showMatImage(Mat mat,String tit){
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame(tit, 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }
    //训练
    public static void train(){

        //训练数据
        Mat trainData = new Mat();
        //标签
        Mat labelData = new Mat();

        //批量读取车牌图片
        for(int i=1;i<=4;i++){
            Mat temp = imread("D:\\my_easypr\\trainData\\"+i+".png",IMREAD_GRAYSCALE);//读取灰度图
            opencv_imgproc.threshold(temp, temp, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);//二值化
            Mat convertMat = new Mat();
            temp.reshape(1, 1).row(0).convertTo(convertMat, CV_32FC1);//转一行
            trainData.push_back(convertMat);//塞入样本
            //labelData.push_back(new Mat());//塞入标签
        }

        //保存下来
        SaveMat(trainData,"./target/svmTrainData.xml");
        //SaveMat(labelData,"./target/svmLabelData.xml");

    }


    public static void main(String args[]){
            train();
    }


}
