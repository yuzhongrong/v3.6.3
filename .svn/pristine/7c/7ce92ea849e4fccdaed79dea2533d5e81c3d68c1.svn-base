package com.jinr.core.security.lockpanttern.view;

import android.content.Context;
import android.os.Environment;
import android.os.FileObserver;

import com.jinr.core.utils.MyLog;

import org.apache.http.util.EncodingUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 图案解锁加密、解密工具类
 *
 * @author way
 */
public class LockPatternUtils {
    private static final String TAG = "LockPatternUtils";
    private static final String LOCK_PATTERN_FILE = "gesture.key";
    private static final String LOCK_PATTERN_OFF_ON_FILE = "LOCK_PATTERN_OFF_ON.key";
    public static final String PATH = "/data" + Environment.getDataDirectory().getAbsolutePath() + "/com.jinr.core/";

    /**
     * The minimum number of dots in a valid pattern.
     */
    public static final int MIN_LOCK_PATTERN_SIZE = 4;
    /**
     * The maximum number of incorrect attempts before the user is prevented
     * from trying again for {@link #FAILED_ATTEMPT_TIMEOUT_MS}.
     */
    public static final int FAILED_ATTEMPTS_BEFORE_TIMEOUT = 5;
    /**
     * The minimum number of dots the user must include in a wrong pattern
     * attempt for it to be counted against the counts that affect
     * {@link #FAILED_ATTEMPTS_BEFORE_TIMEOUT} and
     * {@link #FAILED_ATTEMPTS_BEFORE_RESET}
     */
    public static final int MIN_PATTERN_REGISTER_FAIL = MIN_LOCK_PATTERN_SIZE;
    /**
     * How long the user is prevented from trying again after entering the wrong
     * pattern too many times.
     */
    public static final long FAILED_ATTEMPT_TIMEOUT_MS = 30000L;

    private static File sLockPatternFilename;
    private static File sLockPattern_off_on;
    private static final AtomicBoolean sHaveNonZeroPatternFile = new AtomicBoolean(false);
    private static FileObserver sPasswordObserver;

    private static class LockPatternFileObserver extends FileObserver {
        public LockPatternFileObserver(String path, int mask) {
            super(path, mask);
        }

        @Override
        public void onEvent(int event, String path) {
            if (LOCK_PATTERN_FILE.equals(path)) {
                sHaveNonZeroPatternFile.set(sLockPatternFilename.length() > 0);
            }
        }
    }

    public LockPatternUtils(Context context) {
        String dataSystemDirectory = PATH;
        if (sLockPatternFilename == null) {
            sLockPatternFilename = new File(dataSystemDirectory, LOCK_PATTERN_FILE);
            sHaveNonZeroPatternFile.set(sLockPatternFilename.length() > 0);
            int fileObserverMask = FileObserver.CLOSE_WRITE | FileObserver.DELETE | FileObserver.MOVED_TO | FileObserver.CREATE;
            sPasswordObserver = new LockPatternFileObserver(dataSystemDirectory, fileObserverMask);
            sPasswordObserver.startWatching();
        }
        if (sLockPattern_off_on == null) {// 用于创建存放用户ID和是否打开图形解锁的信息
            sLockPattern_off_on = new File(dataSystemDirectory, LOCK_PATTERN_OFF_ON_FILE);
        }
    }

    /**
     * Check to see if the user has stored a lock pattern.
     *
     * @return Whether a saved pattern exists.
     */
    public boolean savedPatternExists() {
        // 做图形锁的开关
        if (sLockPatternFilename.exists()) {
            if (lock_off_on())
                return true;
            else
                return false;
        } else
            return false;

    }

    public boolean savedLockPatternExists() { // 解锁文件是否存在
        return sLockPatternFilename.exists();
    }

    public static String savedPatternPath() { // 返回图形解锁的路径，测试使用
        return "---" + sLockPatternFilename + "--";
    }

    public static void clearLock() {
        sLockPatternFilename.delete();
    }

    /**
     * Deserialize a pattern. 解密,用于保存状态
     *
     * @param string The pattern serialized with {@link #patternToString}
     * @return The pattern.
     */
    public static List<LockPatternView.Cell> stringToPattern(String string) {
        List<LockPatternView.Cell> result = new ArrayList<LockPatternView.Cell>();
        final byte[] bytes = string.getBytes();
        for (int i = 0; i < bytes.length; i++) {
            byte b = bytes[i];
            result.add(LockPatternView.Cell.of(b / 3, b % 3));
        }
        return result;
    }

    /**
     * Serialize a pattern. 加密
     *
     * @param pattern The pattern.
     * @return The pattern in string form.
     */
    public static String patternToString(List<LockPatternView.Cell> pattern) {
        if (pattern == null) {
            return "";
        }
        final int patternSize = pattern.size();
        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        return new String(res);
    }

    /**
     * Save a lock pattern.
     *
     * @param pattern    The new pattern to save.
     * @param isFallback Specifies if this is a fallback to biometric weak
     */
    public static void saveLockPattern(List<LockPatternView.Cell> pattern) {
        // Compute the hash
        final byte[] hash = LockPatternUtils.patternToHash(pattern);
        try {
            // Write the hash to file
            RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename, "rwd");
            // Truncate the file if pattern is null, to clear the lock
            if (pattern == null) {
                raf.setLength(0);
            } else {
                raf.write(hash, 0, hash.length);
            }
            raf.close();
        } catch (FileNotFoundException fnfe) {
            // Cant do much, unless we want to fail over to using the settings
            // provider
            MyLog.e(TAG, "Unable to save lock pattern to " + sLockPatternFilename);
        } catch (IOException ioe) {
            // Cant do much
            MyLog.e(TAG, "Unable to save lock pattern to " + sLockPatternFilename);
        }
    }

    /*
     * Generate an SHA-1 hash for the pattern. Not the most secure, but it is at
     * least a second level of protection. First level is that the file is in a
     * location only readable by the system process.
     *
     * @param pattern the gesture pattern.
     *
     * @return the hash of the pattern in a byte array.
     */
    private static byte[] patternToHash(List<LockPatternView.Cell> pattern) {
        if (pattern == null) {
            return null;
        }

        final int patternSize = pattern.size();
        byte[] res = new byte[patternSize];
        for (int i = 0; i < patternSize; i++) {
            LockPatternView.Cell cell = pattern.get(i);
            res[i] = (byte) (cell.getRow() * 3 + cell.getColumn());
        }
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
            byte[] hash = md.digest(res);
            return hash;
        } catch (NoSuchAlgorithmException nsa) {
            return res;
        }
    }

    /**
     * Check to see if a pattern matches the saved pattern. If no pattern
     * exists, always returns true.
     *
     * @param pattern The pattern to check.
     * @return Whether the pattern matches the stored one.
     */
    public static boolean checkPattern(List<LockPatternView.Cell> pattern) {
        try {
            // Read all the bytes from the file
            RandomAccessFile raf = new RandomAccessFile(sLockPatternFilename,
                    "r");
            final byte[] stored = new byte[(int) raf.length()];
            int got = raf.read(stored, 0, stored.length);
            raf.close();
            if (got <= 0) {
                return true;
            }
            // Compare the hash from the file with the entered pattern's hash
            return Arrays.equals(stored,
                    LockPatternUtils.patternToHash(pattern));
        } catch (FileNotFoundException fnfe) {
            return true;
        } catch (IOException ioe) {
            return true;
        }
    }

    /**
     * 开启图形解锁，将开启标识和用户ID写到文件中
     *
     * @param user_id 用户ID
     * @author fym
     */
    public void lockPattern_on(String user_id) {

        if (!sLockPattern_off_on.exists())
            try {
                sLockPattern_off_on.createNewFile();
            } catch (IOException e) {
                MyLog.i("an_off_on", "判断文件创建出错");
                e.printStackTrace();
            }
        if (sLockPattern_off_on.exists()) {
            String str = "1" + user_id;
            try {
                MyLog.i("lockPattern_on", str);
                writeSDFile(str, sLockPattern_off_on + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 关闭图形解锁，将关闭标识和用户ID写到文件中
     *
     * @param user_id 用户ID
     * @author fym
     */
    public void lockPattern_off(String user_id) {// 关闭图形解锁
        if (sLockPattern_off_on.exists()) {
            String str = "0" + user_id;
            try {
                MyLog.i("lockPattern_on", str);
                writeSDFile(str, sLockPattern_off_on + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 获得之前登录用户保存的ID
     *
     * @return 用户ID
     * @author fym
     */
    public String userID() { // 获得保存的用户ID
        String str = "x";
        try {
            str = readSDFile(sLockPattern_off_on + "");
            if (!str.equals(""))
                str = str.substring(1);
            MyLog.i("userID", str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str;
    }

    /**
     * 获得图形锁开关是否打开
     *
     * @return 返回开关状态
     * @author fym
     */
    public Boolean lock_off_on() { // 获得开关是否打开
        String str = "";
        try {
            str = readSDFile(sLockPattern_off_on + "");
            str = str.substring(0, 1);
            MyLog.i("lock_off_on", str);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return str.equals("1");
    }

    public void clear_lock_off_on() {// 清除用户图形信息
        if (sLockPattern_off_on.exists()) {
            String str = "0" + "xxx";
            try {
                MyLog.i("lockPattern_on", str);
                writeSDFile(str, sLockPattern_off_on + "");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * @param fileName 读取文件的文件名
     * @return 读取出的字符串
     * @throws IOException
     * @author fym
     */
    public String readSDFile(String fileName) throws IOException { // 读取文件方法

        File file = new File(fileName);
        if (!file.exists()) {
            file.createNewFile();
            return "";
        }

        FileInputStream fis = new FileInputStream(file);

        int length = fis.available();

        byte[] buffer = new byte[length];
        fis.read(buffer);

        String res = EncodingUtils.getString(buffer, "UTF-8");
        MyLog.i("readSDFile", res + " + " + fileName);
        fis.close();
        return res;
    }

    /**
     * 对指定文件进行写入指定字符串
     *
     * @param write_str 写入字符串
     * @param fileName  文件名
     * @throws IOException
     * @author fym
     */
    public void writeSDFile(String write_str, String fileName)
            throws IOException { // 写入文件方法
        File file = new File(fileName);
        MyLog.i("writeSDFile", write_str + " + " + fileName);
        if (!file.exists())
            file.createNewFile();
        FileOutputStream fos = new FileOutputStream(file);
        byte[] bytes = write_str.getBytes();
        fos.write(bytes);
        fos.close();

    }

}
