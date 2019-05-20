package com.ist.javacv;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;

import javax.swing.*;
import java.io.File;
import java.util.HashMap;
import static org.bytedeco.javacpp.helper.opencv_core.cvNorm;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.FileStorage.READ;
import static org.bytedeco.javacpp.opencv_core.FileStorage.WRITE;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_imgproc.*;
import static org.bytedeco.javacpp.opencv_imgproc.resize;


/**
 * @desc : PCA+KNN 人脸识别
 * @auth : TYF
 * @date : 2019-05-20 - 13:20
 */
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
        //存放所有mat
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
    //名字数组转xml
    public static void SaveName(String [] arr,String path){
        FileStorage f = new FileStorage(path,WRITE);
        for(int i= 0;i<arr.length;i++){
            f.write("tag_"+i,arr[i]);
        }
        f.release();
    }
    //匹配名字
    public static String GetName(int i,String path){
        FileStorage f = new FileStorage(path,READ);
        String name  = f.get("tag_"+i).asBytePointer().getString();
        return name;
    }
    //显示mat矩阵对应的图片
    public static void showImage(Mat mat){
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame("人脸", 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
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
    public static Mat DetectFace(Mat src,String trainData,String resultData,String nameData)
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
        System.out.println("开始检测:");
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
            //原图上标出 特征脸name
            int x  = face_i.x()-(face_i.width()/2);
            int y  = face_i.y();
            String name = GetName(ID,nameData);
            putText(src, name, new Point(x,y), CV_FONT_ITALIC, 1, new Scalar(0, 0, 255, 1), 2, 0, false);
            System.out.println("识别到:id="+ID+",name="+name);
        }
        //显示释放否则内存溢出
        return src;
    }

    //批量读取图片
    public static HashMap<Mat,String> loadImages(String path){
        HashMap <Mat,String> mats = new HashMap<>();
        File file = new File(path);
        if(file.isDirectory()){
            File[] pics = file.listFiles();
            for(int i=0;i<pics.length;i++){
                File f = pics[i];
                if(f.getName().contains(".jpg")||f.getName().contains(".jpeg")||f.getName().contains(".png")||f.getName().contains(".pgm")){
                    //图片
                    Mat mat = readImage(f.getPath());
                    //图片名称
                    String name = f.getName();
                    mats.put(mat,name);
                }
            }
        }
        return mats;
    }

    public static void main(String args[]){

        //批量读取图片
        HashMap<Mat,String> map = loadImages("E:\\work\\atest\\face2");
        Mat[] matArr = new Mat[map.size()];
        String[] namtes = new String[map.size()];//顺序保存每个特征脸矩阵的文件名
        int i =0;
        for(Mat m:map.keySet()){
            matArr[i]=m;
            namtes[i]=map.get(m).split("\\.")[0];
            i++;
        }
        MatVector images = new MatVector();
        images.put(matArr);

        //mat数组转行向量
        Mat mats = GetAllsamples(images);
        //训练数据保存到xml
        SaveMat(mats,"./target/Mats.xml");
        SaveName(namtes,"./target/Names.xml");
        //训练得到特征脸集合
        Mat result = Train("./target/Mats.xml");
        //特征脸集合保存到xml
        SaveMat(result,"./target/Result.xml");

        //Test
        //人脸检测+识别
        Mat mat = readImage("E:\\work\\atest\\face\\face.png");
        Mat re = DetectFace(mat,"./target/Mats.xml","./target/Result.xml","./target/Names.xml");
        showImage(re);
    }
}