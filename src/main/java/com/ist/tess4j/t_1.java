package com.ist.tess4j;


import java.io.File;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;

/**
 * @desc : 文字识别
 * @auth : TYF
 * @data : 2019/5/7 22:25
 */
public class t_1 {

    //文字识别
    public static void _tess4j(String in) throws Exception{
        //加载
        ITesseract instance = new Tesseract();
        instance.setDatapath("E:\\work\\tess4j\\Tess4J\\tessdata");
        //英文之外语种需显示指定,且添加语言包到上面datapath中
        instance.setLanguage("chi_sim");
        //图片
        File imgDir = new File(in);
        String ocrResult = instance.doOCR(imgDir);
        // 输出识别结果
        System.out.println("OCR Result: \n" + ocrResult + "\n");
    }


    public static void main(String[] args) throws Exception {

        //"E:\work\test\2.png"
        _tess4j("E:\\work\\test\\3.png");

    }


}


