package com.th.anlib;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


/**
 * Created by york on 22/06/2017.
 */

public class FileLog {
    private SimpleDateFormat sdf;
    private LogFile mLogFile;

    private int curWriteNum = 0;

    private static String logFolder =  "/sdcard/common_log/";

    private Thread mWorkThread;

    private Handler mHandler;


    private static FileLog instance;

    private FileLog() {
        sdf = new SimpleDateFormat("MM-dd HH:mm:ss.SSS");
        checkThreadAlive();
    }

    public static FileLog instance() {
        if (instance == null) {
            synchronized (FileLog.class) {
                if (instance == null) {
                    instance = new FileLog();
                }
            }
        }
        return instance;
    }

    public void setLogFolder(String logFolder) {
        if (!TextUtils.isEmpty(logFolder)) {
            FileLog.logFolder = "/sdcard/" + logFolder + "/";
        }
    }

    private synchronized void checkThreadAlive() {
        if (mWorkThread == null || !mWorkThread.isAlive()) {
            mWorkThread = new Thread(mRunnable);
            mWorkThread.start();
        }
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            Looper.prepare();
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    String toWrite = (String) msg.obj;
                    writeSingleLine(toWrite);
                }
            };
            Looper.loop();
        }
    };

    public void write(int level, String log) {
        checkThreadAlive();
        String levelS = "UnKnow Level";
        switch (level) {
            case Log.INFO:
                levelS = "I:";
                break;
            case Log.DEBUG:
                levelS = "D:";
                break;
            case Log.WARN:
                levelS = "W:";
                break;
            case Log.ERROR:
                levelS = "E:";
                break;
        }
        String toWrite = sdf.format(new Date()) + " " + levelS + " " + log;

        if (mHandler != null) {
            Message obtain = Message.obtain();
            obtain.obj = toWrite;
            mHandler.sendMessage(obtain);
        }
    }

    private void writeSingleLine(String toWrite) {
        PrintWriter fis = getFis();
        if (fis != null) {
            fis.println(toWrite);
        }

        curWriteNum++;
        checkExpiredFile();
    }

    private void checkExpiredFile() {
        long curTimeMillis = System.currentTimeMillis();
        /*
         * 检查三天之前的文件
         */
        long threeDayInMillis = 3 * 24 * 60 * 60;

        /*
         * 1000次之后检查3天之前的文件夹
         */
        int CHECK_FILE_NUM = 1000;
        if (curWriteNum > CHECK_FILE_NUM) {
            File folder = new File(logFolder);
            File[] files = folder.listFiles();

            List<File> toDelete = new ArrayList<>();
            if (files != null) {
                for (File file : files) {
                    if (file != null) {
                        long lmf = file.lastModified();
                        if ((curTimeMillis - lmf) > threeDayInMillis) {
                            toDelete.add(file);
                        }
                    }
                }

                for (File file : toDelete) {
                    file.delete();
                }
                curWriteNum = 0;
            }
        }
    }

    /**
     * 得到今天对应的日志文件
     */
    private PrintWriter getFis() {
        if (mLogFile == null) mLogFile = new LogFile();

        if (!mLogFile.isInToday()) {
            PrintWriter pw = mLogFile.pw;
            if (pw != null) {
                pw.close();
            }
            mLogFile = null;

            mLogFile = new LogFile();
        }
        return mLogFile.pw;
    }

    private static class LogFile {

        PrintWriter pw;
        long time;
        private Calendar fileCalendar;

        LogFile() {
            this.time = System.currentTimeMillis();
            fileCalendar = Calendar.getInstance();
            fileCalendar.setTimeInMillis(time);

            try {
                File folder = new File(logFolder);
                if (!folder.exists()) {
                    folder.mkdirs();
                }
                pw = new PrintWriter(new FileOutputStream(new File(getFileName()), true), true);
            } catch (FileNotFoundException e) {
                // 不能再这里打印文件日志，否则可能会陷入死循环。
                e.printStackTrace();
                pw = null;
            }
        }

        private String getFileName() {
            SimpleDateFormat fileNameFormat = new SimpleDateFormat("yyyy-MM-dd");
            return String.format("%s%s.log",
                    logFolder,
                    fileNameFormat.format(new Date()));
        }

        boolean isInToday() {
            Calendar todayCalendar = Calendar.getInstance();

            fileCalendar.setTimeInMillis(time);

            return fileCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR)
                    && fileCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR);
        }

    }

}
