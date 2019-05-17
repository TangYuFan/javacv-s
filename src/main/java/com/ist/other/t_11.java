package com.ist.other;


import org.bytedeco.javacpp.opencv_ml.NormalBayesClassifier;

/**
 * @desc : 朴素贝叶斯算法
 * @auth : TYF
 * @date : 2019-05-14 - 11:35
 */
public class t_11 {


    public static void main(String args[]){

        //正太贝叶斯分类器(特征向量服从多变量正太高斯分布)
        //朴素贝叶斯分类器(特征向量之前相互独立)


        NormalBayesClassifier   classifier = NormalBayesClassifier.create();


    }


}
