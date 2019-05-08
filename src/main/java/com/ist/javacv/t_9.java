package com.ist.javacv;


import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacv.CanvasFrame;
import org.bytedeco.javacv.OpenCVFrameConverter;
import javax.swing.*;
import static org.bytedeco.javacpp.helper.opencv_core.cvNorm;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_core.FileStorage.READ;
import static org.bytedeco.javacpp.opencv_core.FileStorage.WRITE;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;

/**
 * @desc : 特征脸算法
 * @auth : TYF
 * @date : 2019-05-08 - 10:42
 */
public class t_9 {

    //pca降维:http://blog.codinglabs.org/articles/pca-tutorial.html
    private static double threshold = -1;	//阈值，不设置返回最相似的距离的标签
    private static int num_components = -1;
    private static PCA pca = null;
    private static Mat result;	//经过投影后的矩阵

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
        OpenCVFrameConverter.ToMat converter = new OpenCVFrameConverter.ToMat();
        CanvasFrame canvas = new CanvasFrame("人脸", 1);
        canvas.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        canvas.showImage(converter.convert(mat));
    }

    //训练
    public static Mat train(MatVector images) {

        //所有mat转为一行
        Mat allsamples = new Mat();
        Mat mat;
        //单个mat转为一行
        Mat convertMat = new Mat();
        Mat placeholder = new Mat();
        for (int i = 0; i < images.size(); ++i) {	//遍历每一个Mat
            mat = images.get(i);
            mat.reshape(1, 1).row(0).convertTo(convertMat, CV_32FC1);
            allsamples.push_back(convertMat);
        }
        if (num_components != -1) {	//说明最大维度为设置过
            pca = new PCA(allsamples, placeholder, CV_PCA_DATA_AS_ROW, num_components);	//pca
        } else {
            pca = new PCA(allsamples, placeholder, CV_PCA_DATA_AS_ROW);	//pca
        }
        result = pca.project(allsamples);
        System.out.println("result:"+result.size());
        return result;
    }

    //欧氏距离
    public static double norm(Mat mat1, Mat mat2) {
        CvMat cv1 = new CvMat(mat1);
        CvMat cv2 = new CvMat(mat2);
        return cvNorm(cv1, cv2);
    }


    //保存矩阵到xml
    public static void saveMat(Mat mat,String path){
        FileStorage f = new FileStorage(path,WRITE);
        f.write("tag",mat);
        f.release();
    }

    //从xml读取矩阵
    public static Mat loadMat(String path){
        FileStorage f = new FileStorage(path,READ);
        Mat mat  = f.get("tag").mat();
        System.out.println("mat:"+mat.cols()+","+mat.rows());
        return mat;
    }

    //测试
    public static void test(Mat testImage){

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
                distance[i] = norm(testResult, result.row(i));
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

        MatVector images = new MatVector();
        Mat m0 = readImage("D:\\test\\face\\0.jpg");
        Mat m1 = readImage("D:\\test\\face\\1.jpg");
        Mat m2 = readImage("D:\\test\\face\\2.jpg");
        Mat m3 = readImage("D:\\test\\face\\3.jpg");
        Mat [] mats = new Mat[4];
        mats[0]=m0;
        mats[1]=m1;
        mats[2]=m2;
        mats[3]=m3;
        images.put(mats);

        //训练4张图片得到基向量
        Mat rs = train(images);

        //保存基向量
        //saveMat(rs,"./target/mat.xml");
        //读取基向量
        //Mat j = loadMat("./target/mat.xml");

        result = rs;

        //测试
        test(m3);



    }



}
