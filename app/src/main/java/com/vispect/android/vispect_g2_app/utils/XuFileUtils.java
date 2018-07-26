package com.vispect.android.vispect_g2_app.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;

/**
 * file和byet[]之间的转换工具
 * Created by xu on 2016/8/19.
 */
public class XuFileUtils {
    private final static String TAG = "XuFileUtils";


    /**
     * 获得指定文件的byte数组
     */
    public static byte[] getBytes(String filePath) {
        byte[] buffer = null;
        try {
            File file = new File(filePath);
            FileInputStream fis = new FileInputStream(file);
            ByteArrayOutputStream bos = new ByteArrayOutputStream(1000);
            byte[] b = new byte[1000];
            int n;
            while ((n = fis.read(b)) != -1) {
                bos.write(b, 0, n);
            }
            fis.close();
            bos.close();
            buffer = bos.toByteArray();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;
    }

    /**
     * 根据byte数组，生成文件
     */
    public static void getFile(byte[] bfile, String filePath, String fileName) {
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        File file = null;
        try {
            File dir = new File(filePath);
            if (!dir.exists() && dir.isDirectory()) {//判断文件目录是否存在
                dir.mkdirs();
            }
            file = new File(filePath + "\\" + fileName);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bfile);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }


    /**
     * 将String内容文件保存为文件
     */
    public static void writeFileSdcardFile(String fileName, String writeStr) throws IOException {
        //TODO 写文件到SD卡
        try {
            File f = new File(fileName);
            FileOutputStream fout = new FileOutputStream(f, true);
            byte[] bytes = writeStr.getBytes();
            fout.write(bytes);
            fout.close();

        } catch (Exception e) {
            e.printStackTrace();
            XuLog.e("保存错误信息文件错误：" + e.toString());
        }
    }


    /**
     * 讲一个文件流保存为指定文件
     *
     * @param ins   : 文件的输入流
     * @param file: 指定的文件
     */
    public static void inputstreamtofile(InputStream ins, File file) {
        try {
            OutputStream os = new FileOutputStream(file);
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = ins.read(buffer, 0, 8192)) != -1) {
                os.write(buffer, 0, bytesRead);
            }
            os.close();
            ins.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 打开一个文件
     *
     * @param context  : 上下文
     * @param fileName : 文件名
     */
    public static void openFileOnSdCard(Context context, String fileName) {
        // TODO Auto-generated method stub
        String path = Environment.getExternalStorageDirectory().getPath();
        File file = new File(path + "/" + fileName);
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file),
                "application/vnd.android.package-archive");
        context.startActivity(intent);
    }

    /**
     * 更新文件是否存在(单纯判断是否有文件)
     *
     * @param fileName : 文件名
     */
    public static boolean hasUpdateFile(String fileName) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        File f = new File(path + "/" + fileName);
        if (f.exists()) {
            return true;
        } else {
            return false;

        }
    }

    /**
     * 更新文件是否存在(顺便判断MD5值)
     *
     * @param fileName : 文件
     */
    public static boolean hasUpdateFileForMD5(String fileName, String md5) {
        final String path = Environment.getExternalStorageDirectory().getPath();
        File f = new File(path + "/" + fileName);
        if (f.exists() && getFileMD5(f).equals(md5)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 更新文件是否存在(顺便判断长度是否对应)
     *
     * @param fileName : 文件名
     */
    public static boolean hasUpdateFileForLength(String fileName, int length) {

        final String path = Environment.getExternalStorageDirectory().getPath();
        File f = new File(path + "/" + fileName);

//        XuLog.d(TAG,"fileName:"+fileName+"     length:"+length+"    f.exists():"+f.exists()+"    f.length():"+f.length());
        if (f.exists() && f.length() == length) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 获取文件的MD5值
     *
     * @param file : 文件
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 将bitmap保存成png文件
     *
     * @param bm                  : 图片
     * @param path:保存路径
     * @param quality:保存的质量：1~100
     */
    public static void saveBitmap(Bitmap bm, String path, int quality) {
        File f = new File(path);
        try {
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream out = new FileOutputStream(f, true);
            bm.compress(Bitmap.CompressFormat.PNG, quality, out);
            out.flush();
            out.close();
            XuLog.e(TAG, "图片已经保存");
            ;
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }


    /**
     * 删除一个文件
     *
     * @param filepath : 文件路径
     */
    public static void delFile(String filepath) {
        File file = new File(filepath);
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            }
        }
    }

    /**
     * 获取APK文件的信息
     *
     * @param context : 上下文
     * @param path    : apk文件路径
     */
    public static PackageInfo getAPKInfo(Context context, String path) {
        PackageManager pm = context.getPackageManager();
        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);
        return info;
//        if(info != null){
//            ApplicationInfo appInfo = info.applicationInfo;
//            String appName = pm.getApplicationLabel(appInfo).toString();   // APP名称
//            String packageName = appInfo.packageName;  //得到安装包名称
//            String versionName=info.versionName;       //得到版本名信息
//            int versionCode=info.versionCode;       //得到版本号
//        }
    }


}
