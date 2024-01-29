package com.cloudpos.newisofingerprintdemo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.cloudpos.DeviceException;
import com.cloudpos.OperationResult;
import com.cloudpos.POSTerminal;
import com.cloudpos.TimeConstants;
import com.cloudpos.fingerprint.Fingerprint;
import com.cloudpos.fingerprint.FingerprintDevice;
import com.cloudpos.fingerprint.FingerprintOperationResult;
import com.cloudpos.newisofingerprintdemo.util.ByteConvertStringUtil;
import com.cloudpos.newisofingerprintdemo.util.LogHelper;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnEnroll;
    private Button btnVerifyAll;
    private Button btnDeleteALl;
    private TextView textView;
    private Context mContext = this;
    private int userID = 0;
    private int timeout = 10 * 1000;
    private FingerprintDevice fingerprintDevice = null;

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private static final String FINGERINDEX1 = "Finger1";
    private static final String FINGERINDEX2 = "Finger2";
    private static final String FINGERINDEX3 = "Finger3";
    private static final String FINGERINDEX4 = "Finger4";
    private static final String FINGERINDEX5 = "Finger5";

    private Handler handler;
    private static final int SHOW_NORMAL_MESSAGE = 0;
    private static final int SHOW_SUCCESS_MESSAGE = 1;
    private static final int SHOW_FAIL_MESSAGE = 2;
    private static final int SHOW_BTN = 3;
    private static final int HIDE_BTN = 4;

    @SuppressLint("HandlerLeak")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initParams();
        initSpf();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case SHOW_NORMAL_MESSAGE:
                        sendMsg((String) msg.obj);
                        scrollLogView();
                        break;
                    case SHOW_SUCCESS_MESSAGE:
                        sendSuccessMsg((String) msg.obj);
                        scrollLogView();
                        break;
                    case SHOW_FAIL_MESSAGE:
                        sendFailMsg((String) msg.obj);
                        scrollLogView();
                        break;

                    case SHOW_BTN:
                        btnEnroll.setEnabled(true);
                        btnVerifyAll.setEnabled(true);
                        btnDeleteALl.setEnabled(true);
                        break;
                    case HIDE_BTN:
                        btnEnroll.setEnabled(false);
                        btnVerifyAll.setEnabled(false);
                        btnDeleteALl.setEnabled(false);
                        break;
                }
            }
        };
        openDevice();
    }

    void initParams() {
        Button btnFp1 = (Button) findViewById(R.id.fp1);
        Button btnFp2 = (Button) findViewById(R.id.fp2);
        Button btnFp3 = (Button) findViewById(R.id.fp3);
        Button btnFp4 = (Button) findViewById(R.id.fp4);
        Button btnFp5 = (Button) findViewById(R.id.fp5);
        Button btnMatch = (Button) findViewById(R.id.match);
        btnEnroll = (Button) findViewById(R.id.enroll);
        btnVerifyAll = (Button) findViewById(R.id.verifyAll);
        btnDeleteALl = (Button) findViewById(R.id.deleteAll);
        Button btnClearLog = (Button) findViewById(R.id.clearLog);
        btnFp1.setOnClickListener(this);
        btnFp2.setOnClickListener(this);
        btnFp3.setOnClickListener(this);
        btnFp4.setOnClickListener(this);
        btnFp5.setOnClickListener(this);
        btnMatch.setOnClickListener(this);
        btnEnroll.setOnClickListener(this);
        btnVerifyAll.setOnClickListener(this);
        btnDeleteALl.setOnClickListener(this);
        btnClearLog.setOnClickListener(this);
        textView = (TextView) findViewById(R.id.textView);
        textView.setMovementMethod(ScrollingMovementMethod.getInstance());
    }

    void initSpf() {

        preferences = mContext.getSharedPreferences("userFinger", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString(FINGERINDEX1, "0");
        editor.putString(FINGERINDEX2, "0");
        editor.putString(FINGERINDEX3, "0");
        editor.putString(FINGERINDEX4, "0");
        editor.putString(FINGERINDEX5, "0");
        editor.apply();

    }

    void openDevice() {
        try {
            fingerprintDevice = (FingerprintDevice) POSTerminal.getInstance(mContext).getDevice("cloudpos.device.fingerprint");
            fingerprintDevice.open(1);
            //delAllFingers();
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }


    Thread th = null;

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.match) {
            handler.obtainMessage(HIDE_BTN).sendToTarget();
        }
        if (v.getId() != R.id.deleteAll) {
            sendMsg(getStr(R.string.MESSAGE));
        }
        if (v.getId() == R.id.deleteAll || v.getId() == R.id.clearLog) {
            switch (v.getId()) {
                case R.id.deleteAll:
                    delAllFingers();
                    break;
                case R.id.clearLog:
                    textView.setText("");
                    break;
            }
        } else {
            if (th == null || th.getState() == Thread.State.TERMINATED) {
                switch (v.getId()) {
                    case R.id.fp1:
                        th = new Thread() {
                            @Override
                            public void run() {
                                fingerPut(FINGERINDEX1);
                            }
                        };

                        break;
                    case R.id.fp2:
                        th = new Thread() {
                            @Override
                            public void run() {
                                fingerPut(FINGERINDEX2);
                            }
                        };
                        break;
                    case R.id.fp3:
                        th = new Thread() {
                            @Override
                            public void run() {
                                fingerPut(FINGERINDEX3);
                            }
                        };
                        break;
                    case R.id.fp4:
                        th = new Thread() {
                            @Override
                            public void run() {
                                fingerPut(FINGERINDEX4);
                            }
                        };
                        break;
                    case R.id.fp5:
                        th = new Thread() {
                            @Override
                            public void run() {
                                fingerPut(FINGERINDEX5);
                            }
                        };
                        break;
                    case R.id.match:
                        th = new Thread() {
                            @Override
                            public void run() {
                                preferences = mContext.getSharedPreferences("userFinger", Context.MODE_PRIVATE);
                                String finger1 = preferences.getString(FINGERINDEX1, null);
                                String finger2 = preferences.getString(FINGERINDEX2, null);
                                String finger3 = preferences.getString(FINGERINDEX3, null);
                                String finger4 = preferences.getString(FINGERINDEX4, null);
                                String finger5 = preferences.getString(FINGERINDEX5, null);

                                byte[] bytes1 = ByteConvertStringUtil.hexToBytes(finger1);
                                byte[] bytes2 = ByteConvertStringUtil.hexToBytes(finger2);
                                byte[] bytes3 = ByteConvertStringUtil.hexToBytes(finger3);
                                byte[] bytes4 = ByteConvertStringUtil.hexToBytes(finger4);
                                byte[] bytes5 = ByteConvertStringUtil.hexToBytes(finger5);

                                Fingerprint fingerprint1 = new Fingerprint();
                                fingerprint1.setFeature(bytes1);
                                Fingerprint fingerprint2 = new Fingerprint();
                                fingerprint2.setFeature(bytes2);
                                Fingerprint fingerprint3 = new Fingerprint();
                                fingerprint3.setFeature(bytes3);
                                Fingerprint fingerprint4 = new Fingerprint();
                                fingerprint4.setFeature(bytes4);
                                Fingerprint fingerprint5 = new Fingerprint();
                                fingerprint5.setFeature(bytes5);

                                match(fingerprint1, fingerprint2, fingerprint3, fingerprint4, fingerprint5);
                            }
                        };
                        break;
                    case R.id.enroll:
                        th = new Thread() {
                            @Override
                            public void run() {
                                enroll();
                            }
                        };
                        break;
                    case R.id.verifyAll:
                        th = new Thread() {
                            @Override
                            public void run() {
                                verifyAll();
                            }
                        };
                        break;
                }
                th.start();
            }
        }


    }
    public void saveImage(Bitmap bmp) {
        File appDir = new File(Environment.getExternalStorageDirectory(), "/FpImage");
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        String fileName = System.currentTimeMillis() + ".jpg";
        File file = new File(appDir, fileName);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Fingerprint getFingerprint() {
        Fingerprint fingerprint = null;
        try {
            FingerprintOperationResult operationResult = fingerprintDevice.waitForFingerprint(TimeConstants.FOREVER);
            if (operationResult.getResultCode() == OperationResult.SUCCESS) {
                fingerprint = operationResult.getFingerprint(0, 0);
                //handler.obtainMessage(SHOW_SUCCESS_MESSAGE, mContext.getString(R.string.SUCCESSINFO)).sendToTarget();
            } else {
                handler.obtainMessage(SHOW_FAIL_MESSAGE, mContext.getString(R.string.FAILEDINFO)).sendToTarget();
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            handler.obtainMessage(SHOW_FAIL_MESSAGE, mContext.getString(R.string.DEVICEFAILED)).sendToTarget();
        }
        return fingerprint;
    }

    private void fingerPut(String fingerIndex) {
        preferences = mContext.getSharedPreferences("userFinger", Context.MODE_PRIVATE);
        editor = preferences.edit();
        Fingerprint fingerprint = getFingerprint();
        if (fingerprint != null) {
            byte[] buffer1 = fingerprint.getFeature();
            byte[] img = fingerprint.getWsqImage();
            Log.d("FINGERPRINT_DEMO", buffer1.length + ",fpBmp img = " + img.length);

            try {
                SimpleDateFormat format0 = new SimpleDateFormat("HH-mm-ss");
                OutputStream out = new FileOutputStream(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Download/TUZHENG_img_" + format0.format(new Date()));
                InputStream is = new ByteArrayInputStream(img);
                byte[] buff = new byte[1024];
                int len = 0;
                while ((len = is.read(buff)) != -1) {
                    out.write(buff, 0, len);
                }
                is.close();
                out.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            editor.putString(fingerIndex, ByteConvertStringUtil.bytesToHexString(buffer1));
            editor.apply();
            handler.obtainMessage(SHOW_SUCCESS_MESSAGE, mContext.getString(R.string.entry) + fingerIndex).sendToTarget();
        }
    }

    private void match(Fingerprint fingerprint1, Fingerprint fingerprint2, Fingerprint fingerprint3, Fingerprint fingerprint4, Fingerprint fingerprint5) {
        try {
            Fingerprint fingerprint = getFingerprint();
            int index = 0;
            if (fingerprint != null) {
                int matchResult = 0;
                try {
                    if (fingerprint1.getFeature() != null && fingerprint1.getFeature().length > 0) {
                        matchResult = fingerprintDevice.match(fingerprint, fingerprint1);
                        index = 1;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (fingerprint2.getFeature() != null && fingerprint2.getFeature().length > 0) {
                        matchResult = fingerprintDevice.match(fingerprint, fingerprint2);
                        index = 2;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (fingerprint3.getFeature() != null && fingerprint3.getFeature().length > 0) {
                        matchResult = fingerprintDevice.match(fingerprint, fingerprint3);
                        index = 3;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (fingerprint4.getFeature() != null && fingerprint4.getFeature().length > 0) {
                        matchResult = fingerprintDevice.match(fingerprint, fingerprint4);
                        index = 4;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (fingerprint5.getFeature() != null && fingerprint5.getFeature().length > 0) {
                        matchResult = fingerprintDevice.match(fingerprint, fingerprint5);
                        index = 5;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (matchResult > 80) {
                    handler.obtainMessage(SHOW_SUCCESS_MESSAGE, mContext.getString(R.string.MatchSuccess) + matchResult + ",fingerPrint index = " + index).sendToTarget();
                } else {
                    handler.obtainMessage(SHOW_FAIL_MESSAGE, mContext.getString(R.string.MatchFailed)).sendToTarget();
                }
            } else {
                handler.obtainMessage(SHOW_FAIL_MESSAGE, mContext.getString(R.string.FAILEDINFO)).sendToTarget();
            }
        } catch (Exception e) {
            e.printStackTrace();
            handler.obtainMessage(SHOW_FAIL_MESSAGE, mContext.getString(R.string.DEVICEFAILED)).sendToTarget();
        } finally {
            handler.obtainMessage(SHOW_BTN).sendToTarget();
        }
    }


    public void enroll() {
        try {
            userID++;
            fingerprintDevice.enroll(userID, timeout);
            handler.obtainMessage(SHOW_SUCCESS_MESSAGE, getStr(R.string.SUCCESSINFO) + ",id=" + userID).sendToTarget();
        } catch (DeviceException e) {
            e.printStackTrace();
            handler.obtainMessage(SHOW_FAIL_MESSAGE, getStr(R.string.FAILEDINFO)).sendToTarget();
        }
    }


    public void verifyAll() {
        try {
            int result = fingerprintDevice.verifyAll(timeout);
            handler.obtainMessage(SHOW_SUCCESS_MESSAGE, getStr(R.string.MatchSuccess) + ",id=" + result).sendToTarget();
        } catch (DeviceException e) {
            e.printStackTrace();
            handler.obtainMessage(SHOW_FAIL_MESSAGE, getStr(R.string.MatchFailed)).sendToTarget();
        }
    }

    public void delAllFingers() {
        try {
            int result = fingerprintDevice.delAllFingers();
            if (result >= 0) {
                handler.obtainMessage(SHOW_SUCCESS_MESSAGE, getStr(R.string.Cleared)).sendToTarget();
            }
        } catch (DeviceException e) {
            e.printStackTrace();
            handler.obtainMessage(SHOW_FAIL_MESSAGE, getStr(R.string.MatchFailed)).sendToTarget();
        }
    }

    private void closeDevice() {
        try {
            fingerprintDevice.close();
        } catch (DeviceException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        closeDevice();
    }

    private void sendSuccessMsg(String msg) {
        LogHelper.infoAppendMsgForSuccess(msg + "\n", textView);
        scrollLogView();
    }

    private void sendFailMsg(String msg) {
        LogHelper.infoAppendMsgForFailed(msg + "\n", textView);
        scrollLogView();
    }

    private void sendMsg(String msg) {
        LogHelper.infoAppendMsg(msg + "\n", textView);
        scrollLogView();
    }

    public void scrollLogView() {
        int offset = textView.getLineCount() * textView.getLineHeight();
        if (offset > textView.getHeight()) {
            textView.scrollTo(0, offset - textView.getHeight());
        }
    }

    private String getStr(int strId) {
        return getResources().getString(strId);
    }


}
