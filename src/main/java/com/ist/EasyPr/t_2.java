package com.ist.EasyPr;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.Size;
import org.bytedeco.javacpp.opencv_core.Mat;
import org.bytedeco.javacpp.opencv_core.TermCriteria;
import org.bytedeco.javacpp.opencv_imgproc;
import org.bytedeco.javacpp.opencv_ml.TrainData;
import org.bytedeco.javacpp.opencv_ml.SVM;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import org.opencv.imgproc.Imgproc;
import javax.swing.*;
import java.io.File;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.FileStorage.READ;
import static org.bytedeco.javacpp.opencv_core.FileStorage.WRITE;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_GRAYSCALE;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_BINARY;
import static org.bytedeco.javacpp.opencv_imgproc.CV_THRESH_OTSU;
import static org.bytedeco.javacpp.opencv_ml.ROW_SAMPLE;
import static org.bytedeco.javacpp.opencv_ml.SVM.C_SVC;
import static org.bytedeco.javacpp.opencv_ml.SVM.RBF;
import static org.bytedeco.javacv.JavaCV.FLT_EPSILON;
import static org.opencv.ml.SVM.LINEAR;

/**
 * @desc : SVM对正负样本分类，得到包含车牌的图片
 * @auth : TYF
 * @date : 2019-05-22 - 13:56
 */
public class t_2 {

    //显示mat
    public static void showMatImage(Mat mat,String tit){
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame(tit, 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }

    //读取训练数据(0为正例、1为负例、样本名称n.png)
    public static void loadTrainData(String path0,String path1,String trainXml,String labelXml){
        //训练数据
        Mat trainData = new Mat();
        //标签
        Mat labelData = new Mat();
        File file0 = new File(path0);
        File file1 = new File(path1);
        File[] pics0 = file0.listFiles();
        File[] pics1 = file1.listFiles();
        //负例
        for(int i=1;i<=pics0.length;i++){
            File f = pics0[i-1];
            Mat temp = imread(f.getPath(),IMREAD_GRAYSCALE);//灰度图
            opencv_imgproc.threshold(temp, temp, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);//二值图
            Mat convertMat = new Mat();
            temp.reshape(1, 1).row(0).convertTo(convertMat, CV_32F);//转一行
            trainData.push_back(convertMat);//塞入样本
            labelData.push_back(new Mat().put(Mat.zeros(new Size(1,1),CV_32SC1)));//塞入标签0(无车牌)
        }
        //正例
        for(int i=1;i<=pics1.length;i++){
            File f = pics1[i-1];
            Mat temp = imread(f.getPath(),IMREAD_GRAYSCALE);//灰度图
            opencv_imgproc.threshold(temp, temp, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);//二值图
            Mat convertMat = new Mat();
            temp.reshape(1, 1).row(0).convertTo(convertMat, CV_32F);//转一行
            trainData.push_back(convertMat);//塞入样本
            labelData.push_back(new Mat().put(Mat.ones(new Size(1,1),CV_32SC1)));//塞入标签1(无车牌)
        }
        //保存为xml(注意像素点数据类型svm.train对数据类型有要求)
        opencv_core.FileStorage ft = new opencv_core.FileStorage(trainXml,WRITE);
        ft.write("tag",trainData);
        opencv_core.FileStorage fl = new opencv_core.FileStorage(labelXml,WRITE);
        fl.write("tag",labelData);
        ft.release();
        fl.release();
    }


    //训练
    public static void trainSvm(String tXml,String lXml,String path){
        //创建svm
        SVM svm = SVM.create();
        //svm类型:C_SVC/C类支撑向量分类机,NU_SVC/类支撑向量分类机,ONE_CLASS/单分类器,EPS_SVR/类支撑向量回归机,NU_SVR/类支撑向量回归机
        svm.setType(C_SVC);
        //核函数类型:LINEAR/线性,POLY/多项式,RBF/径向量,SIGMOID/二层神经收集
        svm.setKernel(LINEAR);
        //POLY内核函数的参数degree
        //svm.setDegree(0);
        //POLY/RBF/SIGMOID内核函数
        //svm.setGamma(1);
        //POLY/SIGMOID内核函数的参数coef0
        //svm.setCoef0(0);
        //NU_SVC/ONE_CLASS/NU_SVR类型SVM的参数
        //svm.setNu(0);
        //EPS_SVR类型SVM的参数
        //svm.setP(0);
        //C_SVC/EPS_SVR/NU_SVR类型SVM的参数C
        //svm.setC(1);
        //C_SVC类型SVM的可选权重
        //svm.setClassWeights();
        //终止条件(类型、迭代最大次数、阈值)
        TermCriteria ct = new TermCriteria(CV_TERMCRIT_ITER,1000,FLT_EPSILON);
        svm.setTermCriteria(ct);

        //train数据
        FileStorage ft = new FileStorage(tXml,READ);
        FileStorage fl = new FileStorage(lXml,READ);
        Mat trainMat = ft.get("tag").mat();
        Mat labelMat = fl.get("tag").mat();

        TrainData tData = TrainData.create(trainMat,ROW_SAMPLE,labelMat);//ROW_SAMPLE 样本和标签为每行

        //训练
        svm.train(tData);
        //保存结果
        svm.save(path);

    }


    //预测
    public static float testSvm(String mXml,String image){
        SVM svm = SVM.load(mXml);
        Mat temp = imread(image,IMREAD_GRAYSCALE);//灰度图
        opencv_imgproc.threshold(temp, temp, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);//二值图
        Mat convertMat = new Mat();
        temp.reshape(1, 1).row(0).convertTo(convertMat, CV_32F);//转一行
        float res = svm.predict(convertMat);
        return res;
    }

    //筛选车牌图片
    public static MatVector getCarPic(MatVector in){

        SVM svm = SVM.load("./target/svmModulData.xml");

        MatVector out = new MatVector();

        for(int i=0;i<in.get().length;i++){
            Mat temp = in.get()[i];
            opencv_imgproc.cvtColor(temp, temp, Imgproc.COLOR_BGR2GRAY);//灰度图
            opencv_imgproc.threshold(temp, temp, 0, 255, CV_THRESH_OTSU+CV_THRESH_BINARY);//二值图
            showMatImage(temp,"车牌:"+i);
            Mat convertMat = new Mat();
            temp.reshape(1, 1).row(0).convertTo(convertMat, CV_32F);//转一行
            float res = svm.predict(convertMat);
            System.out.println("res:"+res);
            //是正例
            if(res==1.0){
                out.push_back(temp);
            }
        }
        return out;
    }


    public static void main(String args[]){

        //获取训练、标签数据mat
        //loadTrainData("D:\\my_easypr\\trainData\\0","D:\\my_easypr\\trainData\\1","./target/svmTrainData.xml","./target/svmLabelData.xml");

        //训练
        //trainSvm("./target/svmTrainData.xml","./target/svmLabelData.xml","./target/svmModulData.xml");

        //预测
        //float res = testSvm("./target/svmModulData.xml","D:\\my_easypr\\testData\\1\\1.jpg");
        //System.out.println("res:"+res);

        int count = 0 ;
        int error = 0 ;
        //正例测试
        for(int i=1;i<=50;i++){
            float x = testSvm("./target/svmModulData.xml","D:\\my_easypr\\testData\\1\\"+i+".jpg");
            //判断正确
            if(x==1.0){
                count++;
            }
            //判断错误
            else{
                error++;
            }
        }
        System.out.println("正例测试:"+count+"正确,"+error+"错误");
        count = 0 ;
        error = 0 ;
        //负例测试
        for(int i=0;i<=127;i++){
            float x = testSvm("./target/svmModulData.xml","D:\\my_easypr\\testData\\0\\"+i+".jpg");
            //判断正确
            if(x==0.0){
                count++;
            }
            //判断错误
            else{
                error++;
            }
        }
        System.out.println("负例测试:"+count+"正确,"+error+"错误");
    }


}
