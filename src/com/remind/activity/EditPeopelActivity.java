package com.remind.activity;

import java.io.File;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.application.RemindApplication;
import com.remind.dao.MessageIndexDao;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.MessageIndexDaoImpl;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.MessageIndexEntity;
import com.remind.entity.PeopelEntity;
import com.remind.global.AppConstant;
import com.remind.http.HttpClient;
import com.remind.sp.MySharedPreferencesLoginType;
import com.remind.up.Upload;
import com.remind.up.listener.CompleteListener;
import com.remind.up.listener.ProgressListener;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;
import com.remind.util.NetWorkUtil;
import com.remind.util.Utils;
import com.remind.view.RoleDetailImageView;

/**
 * @author ChenLong
 * 
 *         联系人详细信息（编辑联系人信息）
 */
public class EditPeopelActivity extends BaseActivity implements OnClickListener {
    public final static int HTTP_OVER = 0;
    public final static int IMG_UPLOAD_OVER = 1;
    /**
     * 登陆用户信息编辑
     */
    public static final String TYPE_OWNER = "owner";
    /**
     * 好友信息编辑
     */
    public static final String TYPE_OTHER = "other";
    
    /* 用来标识请求照相功能的activity */
    private static final int CAMERA_WITH_DATA = 3023;
    /* 用来标识请求gallery的activity */
    private static final int PHOTO_PICKED_WITH_DATA = 3021;
    /* 用来标识请求裁剪图片后的activity */
    private static final int CAMERA_CROP_DATA = 3022;
    /**
     * 选择系统自带头像
     */
    private static final int ROLE_DATA = 3024;

    private String TAG = "EditPeopelActivity";
    /**
     * 是否是编辑状态
     */
    // private boolean isEdit = false;

    /**
     * 信息是否有改变
     */
    private boolean isChanged = false;

    /**
     * 联系人详细
     */
    private PeopelEntity peopelEntity;

    /**
     * 编辑昵称
     */
    private EditText nickNameEdit;
    private TextView nickEditTxt;
    /**
     * 头像
     */
    private RoleDetailImageView imgView;
    /**
     * 编辑头像按钮
     */
    private Button editImgBtn;

    /**
     * 进入编辑状态
     */
    private ImageButton editBtn;
    /**
     * 确定
     */
    private Button okBtn;
    /**
     * 取消
     */
    private Button cancelBtn;
    /**
     * 退出
     */
    private Button exitBtn;

    /**
     * 名字
     */
    private TextView name;
    /**
     * 号码
     */
    private TextView num;

    /**
     * 头像路径
     */
    private String imgPath = "";
    /**
     * 是否需要上传头像
     */
    private boolean isNeedUploadRole = false;
    /**
     * 照相机拍照得到的图片
     */
    private File mCurrentPhotoFile;

    private PeopelDao peopelDao;

    private MessageIndexDao messageIndexDao;
    /**
     * 发送消息
     */
    private Button sendMsg;

    /**
     * 是否通过查看用户信息进入；false为好友界面进入
     */
    private boolean isUserLogin = false;

    /**
     * 选择头像提示框
     */
    private AlertDialog alertDialog = null;
    
    public Handler handler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {

            case HTTP_OVER:
                Bundle bundle = msg.getData();
                String s = bundle.getString("code");
                httpOver(s);
                break;
            case IMG_UPLOAD_OVER:
                bundle = msg.getData();
                String code = bundle.getString("code");
                // 网络路径，需要存一个本地路径，当登录时如果本地路径文件不存在的话，则需要下载, 然后存储一个本地路径
                String path = bundle.getString("path");
                if ("200".equals(code)) {
                    // 成功, 上传用户信息
                    startUploadInfo(path);
                } else {
                    // 失败
                    httpOver(code);
                }
                break;

            }
        };

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_people_edit);

        peopelDao = new PeopelDaoImpl(this);
        messageIndexDao = new MessageIndexDaoImpl(this);

        Intent intent = getIntent();
        peopelEntity = (PeopelEntity) intent.getSerializableExtra("peopel");

        if (null == peopelEntity) {
            AppUtil.showToast(this, "查看联系人信息失败");
            finish();
            return;
        }

        isUserLogin = intent.getBooleanExtra("user", false);
        initView();
    }

    /**
     * @param isUser
     *            是否是查看登陆用户信息
     */
    private void initView() {
        name = (TextView) findViewById(R.id.peopel_name_edit);
        num = (TextView) findViewById(R.id.peopel_num_edit);
        nickNameEdit = (EditText) findViewById(R.id.peopel_nick_edit);
        nickEditTxt = (TextView) findViewById(R.id.peopel_nick_edit_txt);
        imgView = (RoleDetailImageView) findViewById(R.id.peopel_img_preview);
        editImgBtn = (Button) findViewById(R.id.peopel_img_search);
        editBtn = (ImageButton) findViewById(R.id.title_edit);
        okBtn = (Button) findViewById(R.id.title_ok);
        cancelBtn = (Button) findViewById(R.id.title_cancel);
        sendMsg = (Button) findViewById(R.id.send_msg_btn);
        exitBtn = (Button) findViewById(R.id.exit_btn);

        imgPath = peopelEntity.getImgPath();

        editImgBtn.setOnClickListener(this);
        editBtn.setOnClickListener(this);
        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
        sendMsg.setOnClickListener(this);
        exitBtn.setOnClickListener(this);

        setupView();
        setupImg(imgPath);
        changeToCheck();
    }

    /**
     * 为界面元素添加数据
     */
    private void setupView() {
        name.setText(peopelEntity.getName());
        num.setText(peopelEntity.getNum());
        nickNameEdit.setText(peopelEntity.getNickName());
        nickEditTxt.setText(peopelEntity.getNickName());
    }

    /**
     * 设置头像
     */
    private void setupImg(String path) {
        Utils.setupImg(this, imgView, path, peopelEntity);
        imgView.init();
        // int id = Utils.getResoureIdbyName(this, imgPath);
        // if (0 == id) {
        // // 如果头像是用户上传的图片
        //
        // // 显示图片
        // BitmapFactory.Options options = new BitmapFactory.Options();
        // options.inSampleSize = 2;
        // Bitmap bm = BitmapFactory
        // .decodeFile(peopelEntity.getImgPath(), options);
        //
        // if (bm == null) {
        // // from contacts
        // Uri uri = Uri.parse(peopelEntity.getImgPath());
        // InputStream input = ContactsContract.Contacts
        // .openContactPhotoInputStream(this.getContentResolver(), uri);
        // bm = BitmapFactory.decodeStream(input);
        // }
        //
        // imgView.setImageDrawable(AppUtil.bitmapToDrawable(bm));
        // imgView.init();
        // } else {
        // // 如果头像是软件自带的图片
        // imgView.setImageResource(id);
        // imgView.init();
        // }
    }

    /**
     * 进入编辑状态
     */
    private void changeToEdit() {
        nickNameEdit.setFilters(new InputFilter[] { new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // 设置可编辑状态
                return null;
            }
        } });

        if (isUserLogin) {
            editImgBtn.setVisibility(View.VISIBLE);
        }
        nickNameEdit.setVisibility(View.VISIBLE);
        okBtn.setVisibility(View.VISIBLE);
        cancelBtn.setVisibility(View.VISIBLE);
        editBtn.setVisibility(View.GONE);
        sendMsg.setVisibility(View.GONE);
        nickEditTxt.setVisibility(View.GONE);
        if (!isUserLogin) {
            sendMsg.setVisibility(View.GONE);
        } else {
            sendMsg.setVisibility(View.GONE);
            exitBtn.setVisibility(View.GONE);
        }
    }

    /**
     * 进入查看状态
     */
    private void changeToCheck() {
        nickNameEdit.setFilters(new InputFilter[] { new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
                // 设置不可编辑状态
                return source.length() < 1 ? dest.subSequence(dstart, dend) : "";
            }
        } });

        nickNameEdit.setVisibility(View.GONE);
        editImgBtn.setVisibility(View.GONE);
        okBtn.setVisibility(View.GONE);
        cancelBtn.setVisibility(View.GONE);
        editBtn.setVisibility(View.VISIBLE);
        nickEditTxt.setText(nickNameEdit.getEditableText().toString());
        nickEditTxt.setVisibility(View.VISIBLE);
        if (!isUserLogin) {
            sendMsg.setVisibility(View.VISIBLE);
        } else {
            sendMsg.setVisibility(View.GONE);
            exitBtn.setVisibility(View.VISIBLE);
        }
    }

    private void showChooseImgDlg() {
        if (null == alertDialog) {
            alertDialog = new AlertDialog.Builder(this).setTitle("请选择头像来源")
                    .setPositiveButton("选择头像", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(EditPeopelActivity.this, ChooseRoleActivity.class);
                            startActivityForResult(intent, ROLE_DATA);
                        }
                    }).setNegativeButton("上传头像", new DialogInterface.OnClickListener() {

                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                Intent intent = new Intent(Intent.ACTION_GET_CONTENT, null);
                                intent.setType("image/*");
                                startActivityForResult(intent, PHOTO_PICKED_WITH_DATA);
                            } catch (ActivityNotFoundException e) {
                                AppUtil.showToast(EditPeopelActivity.this, "没有找到照片");
                            }
                        }
                    }).create();
        }
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.peopel_img_search:
            // 浏览头像
            showChooseImgDlg();
            break;
        case R.id.title_edit:
            // 进入编辑模式
            changeToEdit();
            break;
        case R.id.title_ok:
            // 确定
            isChanged();

            if (isChanged) {
                if (isUserLogin) {
                    // 修改用户信息需要上传
                    if (isNeedUploadRole) {
                        // 需要上传头像
                        startUploadImg();
                    } else {
                        startUploadInfo(imgPath);
                    }
                } else {
                    // 修改好友信息不需要上传
                    updateDB();
                    setResult(RESULT_OK);
                    changeToCheck();
                }
            } else {
                setResult(RESULT_CANCELED);
                changeToCheck();
            }

            break;
        case R.id.title_cancel:
            // 取消
            isChanged();

            if (isChanged) {
                setupView();

                if (null != imgPath && !imgPath.equals(peopelEntity.getImgPath())) {
                    setupImg(peopelEntity.getImgPath());
                }
            }

            changeToCheck();
            break;
        case R.id.send_msg_btn:
            // 发送消息
            Intent intent = new Intent(EditPeopelActivity.this, ChatActivity.class);
            intent.putExtra("num", peopelEntity.getNum());
            startActivity(intent);

            finish();
            break;
        case R.id.exit_btn:
            // 退出登陆
            exit();
            break;

        default:
            break;
        }
    }

    /**
     * 退出登陆
     */
    private void exit() {
        new AlertDialog.Builder(this).setTitle("是否退出登陆？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                RemindApplication.IS_LOGIN = false;
                AppConstant.USER_NUM = "";
                AppConstant.FROM_ID = "";
                AppConstant.USER_NAME = "";

                MySharedPreferencesLoginType.setOnlineState(EditPeopelActivity.this, false);
                try {
                    RemindApplication.iBackService.release();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                finish();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).create().show();
    }

    /**
     * 是否有数据上的变化
     * 
     * @return
     */
    private void isChanged() {
        if (null != nickNameEdit.getText().toString()
                && !nickNameEdit.getText().toString().equals(peopelEntity.getNickName())) {
            isChanged = true;
        }
        if (null == nickNameEdit.getText().toString() && null != peopelEntity.getNickName()) {
            isChanged = true;
        }
        if (null != imgPath && !imgPath.equals(peopelEntity.getImgPath())) {
            isChanged = true;
        }
        if (null == imgPath && null != peopelEntity.getImgPath()) {
            isChanged = true;
        }
    }

    /**
     * 从相册得到的url转换为SD卡中图片路径
     */
    @SuppressWarnings("deprecation")
    public String getPath(Uri uri) {
        if (AppUtil.isEmpty(uri.getAuthority())) {
            return null;
        }
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(column_index);
        return path;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent mIntent) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
        case PHOTO_PICKED_WITH_DATA:
            // 选择图片进入编辑模式
            Uri uri = mIntent.getData();
            String currentFilePath = getPath(uri);
            if (!AppUtil.isEmpty(currentFilePath)) {
                Intent intent1 = new Intent(this, CropImageActivity.class);
                intent1.putExtra("PATH", currentFilePath);
                startActivityForResult(intent1, CAMERA_CROP_DATA);
            } else {
                AppUtil.showToast(this, "未在存储卡中找到这个文件");
            }
            break;
        case CAMERA_WITH_DATA:
            Log.d(TAG, "将要进行裁剪的图片的路径是 = " + mCurrentPhotoFile.getPath());
            String currentFilePath2 = mCurrentPhotoFile.getPath();
            Intent intent2 = new Intent(this, CropImageActivity.class);
            intent2.putExtra("PATH", currentFilePath2);
            startActivityForResult(intent2, CAMERA_CROP_DATA);
            break;
        case CAMERA_CROP_DATA:
            isNeedUploadRole = true;
            imgPath = mIntent.getStringExtra("PATH");
            Log.d(TAG, "裁剪后得到的图片的路径是 = " + imgPath);
            // mImagePathAdapter.addItem(mImagePathAdapter.getCount()-1,path);
            // camIndex++;
            // AbViewUtil.setAbsListViewHeight(mGridView,3,25);
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inSampleSize = 2;
            Bitmap bm = BitmapFactory.decodeFile(imgPath, options);
            imgView.setImageDrawable(AppUtil.bitmapToDrawable(bm));
            break;
        case ROLE_DATA:
            // 选择系统自带头像
            isNeedUploadRole = false;
            imgPath = mIntent.getStringExtra("PATH");
            if (!TextUtils.isEmpty(imgPath)) {
                setupImg(imgPath);
            }
            break;
        }
    }

    /**
     * 开始上传头像图片
     */
    private void startUploadImg() {
        /*
         * 设置进度条回掉函数
         * 
         * 注意：由于在计算发送的字节数中包含了图片以外的其他信息，最终上传的大小总是大于图片实际大小，
         * 为了解决这个问题，代码会判断如果实际传送的大小大于图片 ，就将实际传送的大小设置成'fileSize-1000'（最小为0）
         */
        ProgressListener progressListener = new ProgressListener() {
            @Override
            public void transferred(long transferedBytes, long totalBytes) {
            }
        };

        CompleteListener completeListener = new CompleteListener() {
            @Override
            public void result(boolean isComplete, String result, String error) {
                // {"mimetype":"image\/jpeg","last_modified":1467881859,"file_size":148775,"image_frames":1,"bucket_name":"sisi0","image_type":"JPEG","image_width":1600,"path":"\/sisi0\/picture\/2016-07-07\/1467881810170test.jpg","image_height":1200,"code":200,"signature":"a0c8a7a28e0edc2b2e98c56457d0c35e"}
                String code = "";
                String path = "";
                // 成功/失败修改数据库
                if (isComplete) {
                    // 发送消息
                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(result);
                        path = jsonObject.getString("path");
                        code = "200";
                    } catch (JSONException e) {
                        e.printStackTrace();
                        code = "401";
                    }
                } else {
                    // fail
                    code = "401";
                }
                Message msg = handler.obtainMessage();
                msg.what = IMG_UPLOAD_OVER;
                Bundle bundle = new Bundle();
                bundle.putString("code", code);
                bundle.putString("path", path);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        };
        Upload.uploadRole(this, AppUtil.getFileNameFromPath(imgPath, "/"), completeListener, progressListener, imgPath);
    }
    
    /**
     * 开始上传用户信息
     */
    private void startUploadInfo(String imgPath) {
        String mobile = MySharedPreferencesLoginType.getString(getApplicationContext(), MySharedPreferencesLoginType.USERNAME);
        String pwd = MySharedPreferencesLoginType.getString(this, MySharedPreferencesLoginType.PASSWORD);
        String params = "";
        params = HttpClient.getJsonForPost(HttpClient.getUserForReg(mobile, pwd, nickNameEdit.getText().toString(), imgPath));
        editUser(params);
    }
    /**
     * 修改用户信息
     * @param params
     */
    private void editUser(final String params) {
        if (NetWorkUtil.isAvailable(this)) {
            if (!isProgesShow()) {
                showProgess();
            }
            new Thread(new Runnable() {

                @Override
                public void run() {
                    String s = HttpClient.post(HttpClient.url + HttpClient.update_user_info, params);
                    String code = null;
                    try {
                        if (null == s || !s.contains("|")) {
                            s = null;
                        }

                        code = s.split("\\|")[0];
//                        s = s.split("\\|")[1];
//                        JSONObject jsonObject = new JSONObject(s);
//                        from_id = jsonObject.getString("id");

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Message msg = handler.obtainMessage();
                    msg.what = HTTP_OVER;
                    msg.obj = s;
                    Bundle bundle = new Bundle();
                    bundle.putString("code", code);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }
            }).start();
        } else {
            showToast(getResources().getString(R.string.net_null));
            if (isProgesShow()) {
                hideProgess();
            }
        }
    }
    
    public void httpOver(String s) {
        if (TextUtils.isEmpty(s)) {
            // 失败
            hideProgess();
            Toast.makeText(this, "修改失败，请重试.", Toast.LENGTH_SHORT).show();
        } else if ("200".equals(s)) {
            // 成功
            updateDB();

            setResult(RESULT_OK);
            changeToCheck();
            hideProgess();
        } else {
            // 失败
            hideProgess();
            Toast.makeText(this, "修改失败，请重试.", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * 修改数据库
     */
    private void updateDB() {
        if (isUserLogin) {
            peopelEntity.setName(nickNameEdit.getText().toString());
        }
        peopelEntity.setNickName(nickNameEdit.getText().toString());
        peopelEntity.setImgPath(imgPath);
        peopelDao.updatePeopel(peopelEntity);

        // 修改索引数据库
        Cursor cursor = messageIndexDao.queryByNum(peopelEntity.getNum());
        if (cursor.getCount() > 0) {
            MessageIndexEntity messageIndexEntitiy = DataBaseParser.getMessageIndex(cursor).get(0);
            messageIndexEntitiy.setName(peopelEntity.getNickName());
            messageIndexEntitiy.setImgPath(peopelEntity.getImgPath());
            messageIndexDao.update(messageIndexEntitiy);
        }
        cursor.close();
    }
}
