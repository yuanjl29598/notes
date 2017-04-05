package com.zzj.notes.utils;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;

import com.zzj.notes.NoteApplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.channels.FileChannel;


public final class FileUtils {
    /**
     * 检测SD卡是否存在
     */
    public static boolean checkSDcard() {
        return Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState());
    }

    /**
     * 将文件保存到本地
     */
    public static void saveFileCache(byte[] fileData, String folderPath,
                                     String fileName) {
        File folder = new File(folderPath);
        folder.mkdirs();
        File file = new File(folderPath, fileName);
        ByteArrayInputStream is = new ByteArrayInputStream(fileData);
        OutputStream os = null;
        if (!file.exists()) {
            try {
                file.createNewFile();
                os = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int len = 0;
                while (-1 != (len = is.read(buffer))) {
                    os.write(buffer, 0, len);
                }
                os.flush();
            } catch (Exception e) {
                throw new RuntimeException(
                        FileUtils.class.getClass().getName(), e);
            } finally {
                closeIO(is, os);
            }
        }
    }

    /**
     * 从指定文件夹获取文件
     *
     * @return 如果文件不存在则创建, 如果如果无法创建文件或文件名为空则返回null
     */
    public static File getSaveFile(String folderPath, String fileNmae) {
        File file = new File(getSavePath(folderPath) + File.separator
                + fileNmae);
        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 获取SD卡下指定文件夹的绝对路径
     *
     * @return 返回SD卡下的指定文件夹的绝对路径
     */
    public static String getSavePath(String folderName) {
        return getSaveFolder(folderName).getAbsolutePath();
    }

    /**
     * 获取文件夹对象
     *
     * @return 返回SD卡下的指定文件夹对象，若文件夹不存在则创建
     */
    public static File getSaveFolder(String folderName) {
        File file = new File(getSDCardPath() + File.separator + folderName
                + File.separator);
        file.mkdirs();
        return file;
    }

    public static String getSDCardPath() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 输入流转byte[]<br>
     */
    public static final byte[] input2byte(InputStream inStream) {
        if (inStream == null) {
            return null;
        }
        byte[] in2b = null;
        BufferedInputStream in = new BufferedInputStream(inStream);
        ByteArrayOutputStream swapStream = new ByteArrayOutputStream();
        int rc = 0;
        try {
            while ((rc = in.read()) != -1) {
                swapStream.write(rc);
            }
            in2b = swapStream.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            closeIO(inStream, in, swapStream);
        }
        return in2b;
    }

    /**
     * 把uri转为File对象
     */
    public static File uri2File(Activity aty, Uri uri) {
        if (SystemUtils.getSDKVersion() < 11) {
            // 在API11以下可以使用：managedQuery
            String[] proj = {MediaStore.Images.Media.DATA};
            @SuppressWarnings("deprecation")
            Cursor actualimagecursor = aty.managedQuery(uri, proj, null, null,
                    null);
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            String img_path = actualimagecursor
                    .getString(actual_image_column_index);
            return new File(img_path);
        } else {
            // 在API11以上：要转为使用CursorLoader,并使用loadInBackground来返回
            String[] projection = {MediaStore.Images.Media.DATA};
            CursorLoader loader = new CursorLoader(aty, uri, projection, null,
                    null, null);
            Cursor cursor = loader.loadInBackground();
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return new File(cursor.getString(column_index));
        }
    }

    /**
     * 复制文件
     *
     * @param from
     * @param to
     */
    public static void copyFile(File from, File to) {
        if (null == from || !from.exists()) {
            return;
        }
        if (null == to) {
            return;
        }
        FileInputStream is = null;
        FileOutputStream os = null;
        try {
            is = new FileInputStream(from);
            if (!to.exists()) {
                to.createNewFile();
            }
            os = new FileOutputStream(to);
            copyFileFast(is, os);
        } catch (Exception e) {
            throw new RuntimeException(FileUtils.class.getClass().getName(), e);
        } finally {
            closeIO(is, os);
        }
    }

    /**
     * 快速复制文件（采用nio操作）
     *
     * @param is 数据来源
     * @param os 数据目标
     * @throws IOException
     */
    public static void copyFileFast(FileInputStream is, FileOutputStream os)
            throws IOException {
        FileChannel in = is.getChannel();
        FileChannel out = os.getChannel();
        in.transferTo(0, in.size(), out);
    }

    /**
     * 关闭流
     *
     * @param closeables
     */
    public static void closeIO(Closeable... closeables) {
        if (null == closeables || closeables.length <= 0) {
            return;
        }
        for (Closeable cb : closeables) {
            try {
                if (null == cb) {
                    continue;
                }
                cb.close();
            } catch (IOException e) {
                throw new RuntimeException(
                        FileUtils.class.getClass().getName(), e);
            }
        }
    }

    /**
     * 图片写入文件
     *
     * @param bitmap   图片
     * @param filePath 文件路径
     * @return 是否写入成功
     */
    public static boolean bitmapToFile(Bitmap bitmap, String filePath) {
        boolean isSuccess = false;
        if (bitmap == null) {
            return isSuccess;
        }
        File file = new File(filePath.substring(0,
                filePath.lastIndexOf(File.separator)));
        if (!file.exists()) {
            file.mkdirs();
        }

        OutputStream out = null;
        try {
            out = new BufferedOutputStream(new FileOutputStream(filePath),
                    8 * 1024);
            isSuccess = bitmap.compress(CompressFormat.PNG, 100, out);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            closeIO(out);
        }
        return isSuccess;
    }

    /**
     * 从文件中读取文本
     *
     * @param filePath
     * @return
     */
    public static String readFile(String filePath) {
        InputStream is = null;
        try {
            is = new FileInputStream(filePath);
        } catch (Exception e) {
            throw new RuntimeException(FileUtils.class.getName()
                    + "readFile---->" + filePath + " not found");
        }
        return inputStream2String(is);
    }

    /**
     * 从assets中读取文本
     *
     * @param name
     * @return
     */
    public static String readFileFromAssets(Context context, String name) {
        InputStream is = null;
        try {
            is = context.getResources().getAssets().open(name);
        } catch (Exception e) {
            throw new RuntimeException(FileUtils.class.getName()
                    + ".readFileFromAssets---->" + name + " not found");
        }
        return inputStream2String(is);
    }

    /**
     * 输入流转字符串
     *
     * @param is
     * @return 一个流中的字符串
     */
    public static String inputStream2String(InputStream is) {
        if (null == is) {
            return null;
        }
        StringBuilder resultSb = null;
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(is));
            resultSb = new StringBuilder();
            String len;
            while (null != (len = br.readLine())) {
                resultSb.append(len);
            }
        } catch (Exception ex) {
        } finally {
            closeIO(is);
        }
        return null == resultSb ? null : resultSb.toString();
    }

    public static String isFileExist(String strFile) {
        String strRet = null;
        boolean bRet = false;
        if (StringUtils.isNotEmpty(strFile)) {
            File file = new File(strFile);
            if (file != null && file.exists()) {
                bRet = true;
                strRet = strFile;
            }

            if (!bRet) {
                String strPath = strFile.substring(4);
                File f = new File(strPath);
                if (f != null && f.exists()) {
                    bRet = true;
                    strRet = strPath;
                }
            }
        }

        return strRet;
    }

    private static final int IO_BUFFER_SIZE = 8 * 1024;


    public static boolean saveBitmap2file(Bitmap bmp, String url) {

        if (url == null) {
            return false;
        }

        if (!url.contains("#W0#H0")) {
            url = "#W0#H0" + url;
        }

        //LogUtil.e("saveBitmap2file  url = " + url);
        String fullPath = getImageFileCacheDir() + url;//getCachePathEx("images", CipherUtils.md5(url));
        if (fullPath == null || isFileExist(fullPath) != null) {
            return false;
        }

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(fullPath);

            if (stream == null || bmp == null) {
                return false;
            }

            return bmp.compress(CompressFormat.PNG, 100, stream);


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                stream = null;
            }
        }


        return false;
    }


    public static String getCachePathEx(String dir, String fileName) {
        String strRet = null;
        if (fileName == null /*|| !checkSDcard()*/) {
            return strRet;
        }

        strRet = getSDFolderPath(dir) + fileName;//+".jpg";

        return strRet;
    }


    public static String getSDFolderPath(String filename) {
        String strFilePath = getSDRootPath() + filename
                + File.separator;

        File dir = new File(strFilePath);
        if (!dir.exists() || !dir.isDirectory()) {
            boolean bRet = dir.mkdirs();
        }

        return strFilePath;
    }


    public static String getSDRootPath() {
        String strRoot = null;
        File sdDir = null;
        boolean sdCardExist = Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
        if (sdCardExist) {
            sdDir = Environment.getExternalStorageDirectory().getAbsoluteFile();
            if (sdDir != null) {
                strRoot = sdDir.toString();
            }
        } else {

            strRoot = getDefaultAppSystemDataPath(NoteApplication.getNoteApplication());
        }

        return strRoot + File.separator + NoteApplication.getNoteApplication().getPackageName() + File.separator;
    }

    private static final String APP_DATA_PATH = "/data/data";


    /**
     * Get the data path like this:/data/data/com.snda.cloudary/
     *
     * @return
     */
    public static String getDefaultAppSystemDataPath(Context context) {

        String packageName = "";
        if (null == context) {
            packageName = "com.dada.app";
        } else {
            packageName = context.getPackageName();
        }

        return APP_DATA_PATH + File.separator + packageName + File.separator;
    }


    private static int IMAGE_MAX_WIDTH = 720;// 960;
    private static int IMAGE_MAX_HEIGHT = 960;// 1280;

    public static Bitmap loadBitmapByPath(String url, String dfullName, int nW,
                                          int nH) {

        Bitmap retBmp = null;
        if (url != null) {
            dfullName = FileUtils.isFileExist(dfullName);
        }

        if (dfullName == null) {
            return retBmp;
        }

        int nTry = 0;
        FileInputStream fis = null;
        FileDescriptor fd = null;
        try {
            fis = new FileInputStream(dfullName);
            fd = fis.getFD();
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            // BitmapFactory.decodeFile(imgFile, options);
            BitmapFactory.decodeFileDescriptor(fd, null, options);

            int w = options.outWidth;
            int h = options.outHeight;
            options.inSampleSize = 1;

            if (w > IMAGE_MAX_WIDTH || h > IMAGE_MAX_HEIGHT) {
                int rw = w / IMAGE_MAX_WIDTH;
                int rh = h / IMAGE_MAX_HEIGHT;
                options.inSampleSize = rw > rh ? rw : rh;
                if (url != null && url.indexOf("avatars") != -1) {
                    return null;
                }
            }

            do {

                if ((w > 0 && h > 0) && (nW > 0 || nH > 0)) {
                    options.inSampleSize = w > nW ? w / nW
                            : options.inSampleSize;
                }

                // Logger.e("", "w = " + w + ", h = " + h + ", nW = " + nW +
                // ", nH = " + nH + ", options.inSampleSize = " +
                // options.inSampleSize + ", nTry = " + nTry);
                try {
                    // 这里一定要将其设置回false，因为之前我们将其设置成了true
                    // 设置inJustDecodeBounds为true后，decodeFile并不分配空间，即，BitmapFactory解码出来的Bitmap为Null,但可计算出原始图片的长度和宽度
                    options.inJustDecodeBounds = false;

                    retBmp = BitmapFactory.decodeFile(dfullName, options);

                    if (retBmp != null) {
                        break;
                    }
                } catch (OutOfMemoryError err) {

                    nW = 240;
                    nH = 320;
                }
            } while (nTry++ < 2);

        } catch (Exception e) {
            return null;
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                fis = null;
            }
        }

        return retBmp;
    }


    public static String parseCameraResult(Context context, Intent intent,
                                           String tag) {
        String retString = "";
        Uri uri = intent.getData();
        if (uri != null) {
            retString = getAbsoluteImagePath(context, uri);

        } else {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Bitmap bitmap = bundle.getParcelable("data");
                if (bitmap != null) {
                    String fileName = tag + "_" + "_" + System.currentTimeMillis() + ".JPEG";

                    retString = getCachePathEx("cache", fileName);

                    FileUtils.saveBitmapToDisk(bitmap, fileName);
                }

            }

        }

        return retString;
    }


    public static boolean saveBitmapToDisk(Bitmap bitmap, String fileName) {
        boolean bRet = false;
        FileOutputStream fis = null;
        String dfullName = fileName;
        try {
            File file = new File(dfullName);
            //if(!file.getParentFile().exists()) {
            //file.getParentFile().mkdirs();
            // }

            if (file.exists()) {
                file.delete();
            }

            try {

                fis = new FileOutputStream(dfullName);

                bitmap.compress(CompressFormat.JPEG, 100, fis);// 把数据写入文件

                bRet = true;
            } catch (Exception e) {
                e.printStackTrace();
                Log.e("utils", "e1 = " + e);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.e("utils", "e2 = " + e);
        } finally {
            if (fis != null) {
                try {
                    fis.flush();
                    fis.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Log.e("utils", "e = " + e);
                }
            }
        }

        return bRet;
    }

    public static String saveBitmapFileWithFormat(Bitmap bmp, String fileName, String format) {
        if (fileName == null) {
            return null;
        }

        //LogUtil.e("saveBitmap2file  url = " + url);
        String fullPath = getImageFileCacheDir() + fileName + (StringUtils.isNotEmpty(format) ? format : "");//getCachePathEx("images", CipherUtils.md5(fileName)+(StringUtils.isNotEmpty(format)?format:""));
        if (fullPath == null || isFileExist(fullPath) != null) {
            return null;
        }

        OutputStream stream = null;
        try {
            stream = new FileOutputStream(fullPath);

            if (stream == null || bmp == null) {
                return null;
            }

            bmp.compress(CompressFormat.JPEG, 100, stream);


        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.e("", "e1  =  " + e);
        } finally {
            if (stream != null) {
                try {
                    stream.close();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                stream = null;
            }
        }


        return fullPath;
    }

    public static String getAbsoluteImagePath(Context mContext, Uri uri) {
        // can post image
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = ((Activity) mContext).managedQuery(uri, proj, null,
                    null, // WHERE clause selection arguments (none)
                    null); // Order-by clause (ascending by name)
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {


            try {
                //4.0以上的版本会自动关闭 (4.0--14;; 4.0.3--15)
                if (Integer.parseInt(Build.VERSION.SDK) < 14) {
                    cursor.close();
                }
            } catch (Exception e) {

            }
        }


        return null;
    }


    public static synchronized void writeCacheFile(String fileName, byte[] data) {
        String dfullName = fileName;

        BufferedOutputStream bos = null;
        try {
            File file = new File(dfullName);
            file.delete();
            if (!file.exists()) {
                file.createNewFile();
            }

            bos = new BufferedOutputStream(new FileOutputStream(file));
            bos.write(data);
            bos.flush();
            //Log.e("", "writeCacheFile()----dfullName = " + dfullName);
        } catch (FileNotFoundException e) {
            Log.e("", "1  " + e);
        } catch (IOException e) {
            Log.e("", "2  " + e);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    // Log.d(TAG, e);
                }
            }
        }
    }


    public static String createAppDirs(String dirArr) {
        StringBuilder sBuilder = new StringBuilder();
        sBuilder.append(getSDRootPath() + dirArr);

        File dir = new File(sBuilder.toString());
        if (!dir.exists() || !dir.isDirectory()) {
            dir.mkdirs();
        }

        return sBuilder.toString();

    }

    public static void writeJsonFile(String fileName, String jsonString) {
        if (jsonString == null || jsonString.length() == 0) {
            return;
        }

        if (fileName.lastIndexOf(".json") == -1) {
            fileName += ".json";
        }

        String jsonPath = createAppDirs("/cache/json/");
        String strFilePath = jsonPath + fileName;

        File file = new File(strFilePath);
        if (file != null && file.exists()) {
            file.delete();
            file = null;
        }

        FileWriter fw = null;
        try {
            fw = new FileWriter(strFilePath);
            PrintWriter out = new PrintWriter(fw);
            out.write(jsonString);
            out.println();
            out.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (fw != null) {
                    fw.close();
                }

            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }

    }

    public static String readJsonFile(String fileName) {
        if (fileName.lastIndexOf(".json") == -1) {
            fileName += ".json";
        }

        String strFilePath = getSDRootPath() + "/cache/json/"
                + File.separator + fileName;
        File file = new File(strFilePath);
        //LogUtil.e("", "readJsonFile " + strFilePath);
        if (!file.exists()) {
            return null;
        }

        String data = readFile(strFilePath);

        return data;
    }

    public static boolean deleteJsonFile(String fileName) {
        boolean bRet = false;
        String strFilePath = getSDRootPath() + "/cache/json/"
                + File.separator + fileName;
        //LogUtil.e("", "deleteJsonFile  strFilePath = " + strFilePath);
        bRet = deleteFile(strFilePath);

        return bRet;
    }

    public static boolean deleteCacheFile(String fileName) {
        boolean bRet = false;
        if (StringUtils.isEmpty(fileName)) {

            return bRet;
        }

        String fullPath = getSDRootPath() + "cache" + File.separator;
        bRet = deleteFile(fullPath);

        return bRet;
    }

    public static String readFileEx(String path) {
        File file = new File(path);
        BufferedReader reader = null;
        String laststr = "";
        try {

            reader = new BufferedReader(new FileReader(file));
            String tempString = null;
            // int line = 1;

            while ((tempString = reader.readLine()) != null) {

                laststr = laststr + tempString;
            }

            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return laststr;
    }

    public static boolean deleteFile(String fullFileName) {
        boolean bRet = false;
        if (isFileExist(fullFileName) != null) {
            File file = new File(fullFileName);
            if (file.exists()) {
                file.delete();
                bRet = true;
            }
        }

        return bRet;
    }

    public static boolean saveJsonToFile(String fileName, JSONObject jsonObj) {
        if (jsonObj == null) {
            return false;
        }

        if (fileName.lastIndexOf(".json") == -1) {
            fileName += ".json";
        }

        FileUtils.writeJsonFile(fileName, jsonObj.toString());

        return true;
    }

    public static JSONObject loadJsonFile(String fileName) {

        if (fileName.lastIndexOf(".json") == -1) {
            fileName += ".json";
        }

        String json = FileUtils.readJsonFile(fileName);
        if (StringUtils.isEmpty(json)) {
            return null;
        }

        try {
            return new JSONObject(json);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return null;
    }


    public static String getFileName(String pathandname) {
        if (pathandname == null || pathandname.length() == 0) {
            return null;
        }

        int start = pathandname.lastIndexOf("/");
        int end = pathandname.lastIndexOf(".");
        if (start != -1 && end != -1 && start < end
                && end < pathandname.length()) {
            return pathandname.substring(start + 1, end);
        } else {
            return null;
        }
    }

    public static void removeFile(String url) {
        if (url == null) {
            return;
        }
        String fullPath = getImageFileCacheDir() + url;
        if (!TextUtils.isEmpty(fullPath)) {
            File file = new File(fullPath);
            boolean bRet = file.delete();
            //LogUtil.e("bRet = " + bRet + ", url = " + url);
        }

    }


    public static void deleteDir(String filepath) {
        try {
            File f = new File(filepath);// 定义文件路径
            if (f.exists() && f.isDirectory()) {// 判断是文件还是目录
                if (f.listFiles().length == 0) {// 若目录下没有文件则直接删除
                    f.delete();
                } else {// 若有则把文件放进数组，并判断是否有下级目录
                    File delFile[] = f.listFiles();
                    int i = delFile.length;
                    for (int j = 0; j < i; j++) {
                        if (delFile[j].isDirectory()) {
                            deleteDir(delFile[j].getAbsolutePath());// 递归调用del方法并取得子目录路径
                        } else {
                            delFile[j].delete();// 删除文件
                        }
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static String getImageFileCacheDir() {
        return getSDFolderPath("images");
    }

    public static String getDefaultCacheDir() {
        return getSDFolderPath("cache");
    }
}
