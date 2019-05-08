package com.ist.javacv;

import org.bytedeco.javacpp.opencv_core.*;

import static org.bytedeco.javacpp.helper.opencv_core.cvNorm;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.FileStorage.READ;
import static org.bytedeco.javacpp.opencv_core.FileStorage.WRITE;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

public class t_10 {

    //图片转mat
    public static Mat readImage(String filePath){
        Mat  image = imread(filePath, IMREAD_COLOR);
        if (image==null||image.empty()) {
            return null;
        }
        return image;
    }
    //mat数组转行向量
    public static Mat GetAllsamples(MatVector images){
        //所有mat转为一行
        Mat allsamples = new Mat();
        Mat mat;
        //mat转行向量
        Mat convertMat = new Mat();
        for (int i = 0; i < images.size(); ++i) {
            mat = images.get(i);
            mat.reshape(1, 1).row(0).convertTo(convertMat, CV_32FC1);
            allsamples.push_back(convertMat);
        }
        return allsamples;
    }
    //mat转xml
    public static void SaveMat(Mat mat,String path){
        FileStorage f = new FileStorage(path,WRITE);
        f.write("tag",mat);
        f.release();
    }
    //xml转mat
    public static Mat LoadMat(String path){
        FileStorage f = new FileStorage(path,READ);
        Mat mat  = f.get("tag").mat();
        System.out.println("mat:"+mat.cols()+","+mat.rows());
        return mat;
    }
    //训练
    public static Mat Train(String trainData) {
        //加载训练数据
        Mat allsamples = LoadMat(trainData);
        Mat placeholder = new Mat();
        //pca
        PCA pca = new PCA(allsamples, placeholder, CV_PCA_DATA_AS_ROW);
        //特征脸向量矩阵
        Mat result = pca.project(allsamples);
        return result;
    }

    //计算欧氏距离
    public static double EuclideanMetric(Mat mat1, Mat mat2) {
        CvMat cv1 = new CvMat(mat1);
        CvMat cv2 = new CvMat(mat2);
        return cvNorm(cv1, cv2);
    }
    //测试
    public static void Test(Mat testImage,String trainData,String resultData){
        //加载训练数据
        Mat allsamples = LoadMat(trainData);
        Mat placeholder = new Mat();
        //加载特征向量数据
        Mat result = LoadMat(resultData);
        //创建PCA
        PCA pca = new PCA(allsamples, placeholder, CV_PCA_DATA_AS_ROW);
        if(pca!=null){
            //mat转向量
            Mat convertMat = new Mat();
            testImage.reshape(1, 1).row(0).convertTo(convertMat, CV_32FC1);
            //记录所有欧氏距离
            double[] distance = new double[result.rows()];
            Mat testResult = pca.project(convertMat);
            System.out.println("test data:"+testResult.rows()+","+testResult.cols());
            double min_distance = 0;
            //最小的欧氏距离对应的训练数据id
            int min_i = -1;
            //对每个向量进行求欧式距离
            for (int i = 0; i < result.rows(); i++){
                distance[i] = EuclideanMetric(testResult, result.row(i));
                System.out.println("distance:" + distance[i]);
                if (i == 0) {
                    min_distance = distance[0];
                    min_i = i;
                } else if (min_distance > distance[i]){
                    min_distance = distance[i];
                    min_i = i;
                }
            }
            System.out.println("min_i:"+min_i);
        }
    }
    public static void main(String args[]){

        //mat数组
        MatVector images = new MatVector();
        Mat [] matArry = new Mat[10];
        matArry[0]=readImage("E:\\work\\test\\train\\0.jpg");
        matArry[1]=readImage("E:\\work\\test\\train\\1.jpg");
        matArry[2]=readImage("E:\\work\\test\\train\\2.jpg");
        matArry[3]=readImage("E:\\work\\test\\train\\3.jpg");
        matArry[4]=readImage("E:\\work\\test\\train\\4.jpg");//鹿晗
        matArry[5]=readImage("E:\\work\\test\\train\\5.jpg");
        matArry[6]=readImage("E:\\work\\test\\train\\6.jpg");
        matArry[7]=readImage("E:\\work\\test\\train\\7.jpg");//王源
        matArry[8]=readImage("E:\\work\\test\\train\\8.jpg");
        matArry[9]=readImage("E:\\work\\test\\train\\9.jpg");//电鳗
        images.put(matArry);
        //mat数组转行向量
        Mat mats = GetAllsamples(images);
        //训练数据保存到xml
        SaveMat(mats,"./target/Mats.xml");
        //训练得到特征向量矩阵
        Mat result = Train("./target/Mats.xml");
        //特征向量矩阵保存到xml
        SaveMat(result,"./target/Result.xml");

        //测试mat
        Mat mat = readImage("E:\\work\\test\\train\\wuyifan.jpg");//鹿晗
        //测试
        Test(mat,"./target/Mats.xml","./target/Result.xml");
    }
}