package com.ist.other;


import org.bytedeco.javacpp.opencv_core.*;
import org.bytedeco.javacpp.opencv_ml.DTrees;
import java.io.File;
import java.util.HashMap;
import static org.bytedeco.javacpp.opencv_core.CV_32FC1;
import static org.bytedeco.javacpp.opencv_core.FileStorage.READ;
import static org.bytedeco.javacpp.opencv_core.FileStorage.WRITE;
import static org.bytedeco.javacpp.opencv_imgcodecs.IMREAD_COLOR;
import static org.bytedeco.javacpp.opencv_imgcodecs.imread;
import static org.bytedeco.javacpp.opencv_ml.ROW_SAMPLE;

/**
 * @desc : 决策树
 * @auth : TYF
 * @date : 2019-05-13 - 14:14
 * @remark :
 * 回归结束的触发:
 * 树的深度达到了指定的最大值
 * 在该结点训练样本的数目少于指定阈值
 * 在该结点所有的样本属于同一类（如果是回归的话，变化已非常小）
 * 能选择到的最好的分裂跟随机选择相比已经基本没有什么有意义的改进了
 */

public class b {


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



    //mat方式读取本地图片
    public static Mat readImage(String filePath){
        Mat  image = imread(filePath, IMREAD_COLOR);
        if (image==null||image.empty()) {
            return null;
        }
        return image;
    }

    //将Mat数组转为一个Mat
    //Mat(15*20)转成Mat(1*300) 40个就是Mat(40*300)
    public static Mat GetAllsamples(MatVector images){
        //存放所有mat
        Mat allsamples = new Mat();
        Mat mat;
        //mat拼成一行
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
    public static String train(){

        //opencv用的CART算法
        DTrees tree = DTrees.create();
        //训练集矩阵
        Mat trainData = GetAllsamples(new MatVector());
        //结果集矩阵
        Mat trainLabel = new Mat(trainData.rows(),1,CV_32FC1);//结果集矩阵



        tree.setMaxCategories(4);//类别数
        tree.setMaxDepth(10);//树深度
        tree.setMinSampleCount(10);//训练样本数
        tree.setCVFolds(0);//修剪
        tree.setRegressionAccuracy(0);//回归终止标准
        tree.setPriors(new Mat());//先验概率数值
        tree.train(trainData,ROW_SAMPLE,trainLabel);//训练
        tree.save("./target/trainResult.xml");//保存训练结果


        return null;
    }


    public static void main(String args[]){

        //批量读取图片
        HashMap<Mat,String> map = loadImages("D:\\test\\face");
        Mat[] matArr = new Mat[map.size()];
        String[] namtes = new String[map.size()];//顺序保存每张图片的文件名
        int i =0;
        for(Mat m:map.keySet()){
            matArr[i]=m;
            namtes[i]=map.get(m).split("\\.")[0];
            i++;
        }
        MatVector images = new MatVector();
        images.put(matArr);
        //获取训练集数据
        Mat trainData = GetAllsamples(images);
        //获取结果集数据
        Mat trainLable = new Mat(trainData.rows(),1,CV_32FC1);
        SaveMat(trainLable,"./target/trainLable.xml");

        //训练
        DTrees tree = DTrees.create();
        tree.setMaxCategories(4);//类别数
        tree.setMaxDepth(10);//树深度
        tree.setMinSampleCount(10);//训练样本数
        tree.setCVFolds(0);//修剪
        tree.setRegressionAccuracy(0);//回归终止标准
        tree.setPriors(new Mat());//先验概率数值
        tree.train(trainData,ROW_SAMPLE,trainLable);//训练
        tree.save("./target/trainResult.xml");//保存训练结果


        //获取所有节点
        System.out.println("ss"+tree.getRoots().getString());

    }

}


