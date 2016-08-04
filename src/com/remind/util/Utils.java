package com.remind.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.remind.entity.PeopelEntity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.provider.ContactsContract;
import android.widget.ImageView;

public class Utils {
    /**
     * 判断空
     * 
     * @param rawData
     * @return
     */
    public static boolean isNull(String rawData) {
        if (null != rawData && !"".equals(rawData) && !"null".equals(rawData))
            return true;
        else
            return false;
    }

    /**
     * 格式化数据
     * 
     * @param s
     * @return
     */
    public static String stringTrim(String s) {
        return isNull(s) ? s.trim() : s;
    }

    public static String converIdToString(int id, Context mContext) {
        String result = null;
        result = mContext.getResources().getString(id);
        return result;
    }

    /**
     * 获取字符串中包含某字符个数
     * 
     * @param str
     * @param find
     * @return
     */
    public static int getCount(String str, String find) {
        int o = 0;
        int index = -1;
        while ((index = str.indexOf(find, index)) > -1) {
            ++index;
            ++o;
        }
        return o;
    }

    /**
     * 如果服务器不支持中文路径的情况下需要转换url的编码。
     * 
     * @param string
     * @return
     */
    public static String encodeGB(String string) {
        // 转换中文编码
        String split[] = string.split("/");
        for (int i = 1; i < split.length; i++) {
            try {
                split[i] = URLEncoder.encode(split[i], "UTF-8");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            split[0] = split[0] + "/" + split[i];
        }
        split[0] = split[0].replaceAll("\\+", "%20");// 处理空格
        split[0] = split[0].replaceAll("\\{", "%7B");
        split[0] = split[0].replaceAll("\\}", "%7D");
        split[0] = split[0].replaceAll("%3A", "\\:");
        return split[0];
    }

    /**
     * dip转像素
     * 
     * @param context
     * @param dipValue
     * @return
     */
    public static int dip2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * 像素转dip
     * 
     * @param context
     * @param pxValue
     * @return
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * 将px值转换为sp值，保证文字大小不变
     * 
     * @param pxValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    /**
     * 将sp值转换为px值，保证文字大小不变
     * 
     * @param spValue
     * @param fontScale
     *            （DisplayMetrics类中属性scaledDensity）
     * @return
     */
    public static int sp2px(Context context, float spValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (spValue * fontScale + 0.5f);
    }

    /**
     * 获取当前时间
     */
    public static String getNowTime() {
        String time = null;
        Date now = new Date();
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        time = temp.format(now);
        return time;
    }

    /**
     * 获取当前日期
     */
    public static String getNowDate() {
        String time = null;
        Date now = new Date();
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
        time = temp.format(now);
        return time;
    }

    /**
     * 获取3天前的日期
     */
    public static String get3DaysAgoDate() {
        Calendar cal = Calendar.getInstance();
        String time = null;
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
        cal.setTime(new Date());
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) - 3);
        time = temp.format(cal.getTime());
        return time;
    }

    /**
     * 获得目标时间
     * 
     * @param startDate
     *            开始的时间
     * @param passDayCounts
     *            经过的天数
     * @param passMinuteCounts
     *            经过的分钟
     * @param passHourCounts
     *            经过的小时
     * @param passMonthCounts
     *            经过的月
     * @param passYearCounts
     *            经过的年
     * @return
     */
    public static String getTargeDate(String startDate, int passMinuteCounts, int passHourCounts, int passDayCounts, int passMonthCounts,
            int passYearCounts) {
        Calendar cal = Calendar.getInstance();
        String time = null;
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        Date date;
        try {
            date = temp.parse(startDate);
            cal.setTime(date);

            cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE) + passMinuteCounts);
            cal.set(Calendar.HOUR, cal.get(Calendar.HOUR) + passHourCounts);
            cal.set(Calendar.DATE, cal.get(Calendar.DATE) + passDayCounts);
            cal.set(Calendar.MONTH, cal.get(Calendar.MONTH) + passMonthCounts);
            cal.set(Calendar.YEAR, cal.get(Calendar.YEAR) + passYearCounts);
            time = temp.format(cal.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }

    public static boolean isAvailable(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();

        if (info == null || !info.isConnected() || info.isRoaming())
            return false;

        return true;
    }

    @SuppressWarnings("resource")
    public static String[] get2File(String uri) {
        if (uri == null) {
            return null;
        }
        String[] strs = new String[2];
        File file = new File(uri);
        byte[] b = null;
        try {
            strs[0] = uri.substring(uri.lastIndexOf("/") + 1, uri.length());
            FileInputStream fis = new FileInputStream(file);
            b = new byte[fis.available()];
            fis.read(b);
            strs[1] = new String(b, "UTF-8");
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return strs;
    }

    /**
     * 获取明天的日期
     */
    public static String getTomorrowAgoDate() {
        Calendar cal = Calendar.getInstance();
        String time = null;
        SimpleDateFormat temp = new SimpleDateFormat("yyyy-MM-dd");
        cal.setTime(new Date());
        cal.set(Calendar.DATE, cal.get(Calendar.DATE) + 1);
        time = temp.format(cal.getTime());
        return time;
    }

    /**
     * 根据资源的名称获取资源的id
     * 
     * @param context
     * @param name
     * @return 0:没有此资源
     */
    public static int getResoureIdbyName(Context context, String name) {
        int resId1 = context.getResources().getIdentifier(name, "drawable", "com.help.remind");
        return resId1;
    }

    /**
     * 设置头像
     */
    public static void setupImg(Context context, ImageView imgView, String name, PeopelEntity peopelEntity) {
        int id = Utils.getResoureIdbyName(context, name);
        if (0 == id) {
            // 如果头像是用户上传的图片

            // 显示图片
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(peopelEntity.getImgPath(), options);

            if (bm == null) {
                // from contacts
                Uri uri = Uri.parse(peopelEntity.getImgPath());
                InputStream input = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(), uri);
                bm = BitmapFactory.decodeStream(input);
            }

            imgView.setImageDrawable(AppUtil.bitmapToDrawable(bm));
        } else {
            // 如果头像是软件自带的图片
            imgView.setImageResource(id);
        }
    }
}
