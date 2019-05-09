package com.ist.javacv;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect;

import static org.bytedeco.javacpp.helper.opencv_core.cvNorm;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.FileStorage.READ;
import static org.bytedeco.javacpp.opencv_core.FileStorage.WRITE;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgproc.resize;

public class t_9 {

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
    public static int Test(Mat testImage,String trainData,String resultData){
        //加载训练集合数据
        Mat allsamples = LoadMat(trainData);
        Mat placeholder = new Mat();
        //加载特征脸集合数据
        Mat result = LoadMat(resultData);
        //创建PCA
        PCA pca = new PCA(allsamples, placeholder, CV_PCA_DATA_AS_ROW);
        if(pca!=null){
            //mat转向量
            Mat convertMat = new Mat();
            testImage.reshape(1, 1).row(0).convertTo(convertMat, CV_32FC1);
            //记录所有欧氏距离
            double[] d = new double[result.rows()];
            Mat testResult = pca.project(convertMat);
            System.out.println("test data:"+testResult.rows()+","+testResult.cols());
            double min_distance = 0;
            //最小的欧氏距离对应的训练数据id
            int min_i = -1;
            //对每个向量进行求欧式距离
            for (int i = 0; i < result.rows(); i++){
                d[i] = EuclideanMetric(testResult, result.row(i));
                System.out.println("d:" + d[i]);
                if (i == 0) {
                    min_distance = d[0];
                    min_i = i;
                } else if (min_distance > d[i]){
                    min_distance = d[i];
                    min_i = i;
                }
            }
            System.out.println("min_i:"+min_i);
            return min_i;//返回欧式距离最小的那个特征脸的ID
        }
        return -1;
    }


    //人脸检测-将截取的人脸调用test
    public static Mat DetectFace(Mat src,String trainData,String resultData)
    {
        //面部识别级联分类器
        opencv_objdetect.CascadeClassifier cascade = new opencv_objdetect.CascadeClassifier("D:\\my_opencv\\opencv\\data\\lbpcascades\\lbpcascade_frontalface.xml");
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
            rectangle(src, face_i, new opencv_core.Scalar(0, 0, 255, 1));
            //人脸截图取mat
            Mat face = new Mat(src,face_i);
            Size size= new Size(55,55);
            Mat _face = new Mat(size,CV_32S);
            resize(face,_face,size);
            //调用识别-找到最匹配的特征脸的ID
            int ID = Test(_face,trainData,resultData);
            //原图上标出 特征脸ID+欧氏距离
            //TODO  face_i

        }
        //显示释放否则内存溢出
        return src;
    }


    public static void main(String args[]){
        //Train
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
        //训练得到特征脸集合
        Mat result = Train("./target/Mats.xml");
        //特征脸集合保存到xml
        SaveMat(result,"./target/Result.xml");

        //Test
        //人脸检测+识别
        Mat mat = readImage("E:\\work\\test\\train\\wuyifan.jpg");//另找一张鹿晗
        DetectFace(mat,"./target/Mats.xml","./target/Result.xml");
    }
}