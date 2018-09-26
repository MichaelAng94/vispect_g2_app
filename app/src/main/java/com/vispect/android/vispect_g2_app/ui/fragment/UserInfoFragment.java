package com.vispect.android.vispect_g2_app.ui.fragment;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.vispect.android.vispect_g2_app.R;
import com.vispect.android.vispect_g2_app.app.AppConfig;
import com.vispect.android.vispect_g2_app.app.AppContext;
import com.vispect.android.vispect_g2_app.app.AppHelper;
import com.vispect.android.vispect_g2_app.bean.ResultData;
import com.vispect.android.vispect_g2_app.bean.UserInfo;
import com.vispect.android.vispect_g2_app.controller.AppApi;
import com.vispect.android.vispect_g2_app.controller.UIHelper;
import com.vispect.android.vispect_g2_app.interf.DialogClickListener;
import com.vispect.android.vispect_g2_app.interf.ResultCallback;
import com.vispect.android.vispect_g2_app.ui.activity.LoginActivity;
import com.vispect.android.vispect_g2_app.ui.widget.DialogHelp;
import com.vispect.android.vispect_g2_app.utils.DialogUtils;
import com.vispect.android.vispect_g2_app.utils.PermissionUtils;
import com.vispect.android.vispect_g2_app.utils.SDcardTools;
import com.vispect.android.vispect_g2_app.utils.XuFileUtils;
import com.vispect.android.vispect_g2_app.utils.XuLog;
import com.vispect.android.vispect_g2_app.utils.XuString;
import com.vispect.android.vispect_g2_app.utils.XuToast;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Request;

import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;
import static android.provider.MediaStore.ACTION_IMAGE_CAPTURE;
import static com.nostra13.universalimageloader.core.ImageLoader.TAG;
import static com.vispect.android.vispect_g2_app.app.AppConfig.IMAGE_USER_AVATAR_NAME;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_ALBUM;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_CAMERA;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_CAMERA_PERMISSION;
import static com.vispect.android.vispect_g2_app.app.AppConfig.REQUEST_CODE_CROP_IMAGE;

/**
 * 用户信息
 */
public class UserInfoFragment extends BaseFragment {

    @Bind(R.id.img_head)
    ImageView headView;
    @Bind(R.id.user_name)
    TextView userName;
    @Bind(R.id.phone)
    TextView phone;
    @Bind(R.id.email)
    TextView email;
    @Bind(R.id.sex)
    TextView sex;
    @Bind(R.id.version)
    TextView version;
    @Bind(R.id.logout)
    TextView logout;
    private int _userId;
    private Handler _handler = new Handler();
    private Runnable _refreshView = new Runnable() {
        @Override
        public void run() {
            UserInfo user = AppContext.getInstance().getUser();
//            userName.setText(user.getName());
            phone.setText(user.getPhone());
            email.setText(user.getEmail());
            sex.setText(STR(user.getSex() == 1 ? R.string.female : R.string.male));
        }
    };

    @Override
    public int getContentResource() {
        return R.layout.fragment_user_info;
    }

    @Override
    protected void initView(View view) {
        version.setText(AppHelper.getAppVersion(getActivity()));
    }

    @Override
    protected void initData() {

        String uid = AppConfig.getInstance(getActivity()).getUserId();
        if (XuString.isEmpty(uid)) return;

        _userId = Integer.parseInt(uid);
        //获取用户信息
        AppApi.getUserInfo(_userId, new ResultCallback<ResultData<UserInfo>>() {
            @Override
            public void onFailure(Request request, Exception e) {
                XuLog.d(TAG, "获取用户信息发生错误：" + e.getMessage());
            }

            @Override
            public void onResponse(ResultData<UserInfo> response) {
                if (response != null && response.getResult() == 0) {
                    UserInfo user = response.getMsg();
                    AppContext.getInstance().setUser(user);
                    ImageLoader.getInstance().displayImage(user.getAvatar(), headView);
                    _handler.post(_refreshView);
                }
            }
        });
    }

    @OnClick({R.id.img_head, R.id.fl_phone, R.id.fl_email, R.id.fl_sex, R.id.logout})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_head:
                choosePhotoType();
                break;
            case R.id.fl_phone:
                modifyPhone();
                break;
            case R.id.fl_email:
                modifyEmail();
                break;
            case R.id.fl_sex:
                selectSex();
                break;
            case R.id.logout:
                logout();
                break;
        }
    }

    //退出登录
    private void logout() {
        DialogUtils.confirmDialog(getActivity(), STR(R.string.ask_out_login), new Runnable() {
            @Override
            public void run() {
                AppConfig.getInstance(getActivity()).setFirstStart(true);
                UIHelper.startActivity(getActivity(), LoginActivity.class);
                getActivity().finish();
            }
        }, null);
    }

    //选择性别
    private void selectSex() {
        final UserInfo user = AppContext.getInstance().getUser();
        if (user == null) return;
        DialogHelp.getInstance().sexDialog(getActivity(), user.getSex(), new DialogClickListener() {
            @Override
            public void clickYes(String text) {
                int sex = Integer.parseInt(text);
                DialogHelp.getInstance().hideDialog();
                if (sex != user.getSex()) saveUserInfo(sex, user.getPhone(), user.getEmail());
            }
        });
    }

    //修改邮箱
    private void modifyEmail() {
        final UserInfo user = AppContext.getInstance().getUser();
        if (user == null) return;
        DialogHelp.getInstance().editDialog(getActivity(), STR(R.string.email_address), new DialogClickListener() {
            @Override
            public void clickYes(String text) {
                if (XuString.isEmpty(text) || !isEmail(text)) {
                    XuToast.show(getActivity(), STR(R.string.edit_input_legal_value));
                    return;
                }
                DialogHelp.getInstance().hideDialog();
                saveUserInfo(user.getSex(), user.getPhone(), text);
            }
        });
    }

    //修改手机号
    private void modifyPhone() {
        final UserInfo user = AppContext.getInstance().getUser();
        if (user == null) return;
        DialogHelp.getInstance().editDialog(getActivity(), STR(R.string.phone_number), new DialogClickListener() {
            @Override
            public void clickYes(String text) {
                if (XuString.isEmpty(text) || !isPhoneNumber(text)) {
                    XuToast.show(getActivity(), STR(R.string.edit_input_legal_value));
                    return;
                }
                DialogHelp.getInstance().hideDialog();
                saveUserInfo(user.getSex(), text, user.getEmail());
            }
        });
    }

    //匹配11位手机号
    private boolean isPhoneNumber(String phone) {
        if (XuString.isEmpty(phone)) return false;
        Pattern pattern = Pattern.compile("^1[0-9]{10}$");
        Matcher matcher = pattern.matcher(phone);
        return matcher.matches();
    }

    //简单过滤 email长度在3到25之间
    private boolean isEmail(String email) {
        if (XuString.isEmpty(email)) return false;
        return email.length() > 3 && email.length() < 25;
    }

    private void choosePhotoType() {
        DialogUtils.selectAvatar(getActivity(), new Runnable() {
            @Override
            public void run() {
                //判断是否已获取到摄像头权限
                if (PermissionUtils.request(getActivity(), UserInfoFragment.this, REQUEST_CODE_CAMERA_PERMISSION, CAMERA)) {
                    Intent intentFromCapture = new Intent(ACTION_IMAGE_CAPTURE);
                    // 判断存储卡是否可以用，可用则进行存储
                    if (SDcardTools.hasSdcard()) {
                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_USER_AVATAR_NAME)));
                    }
                    startActivityForResult(intentFromCapture, REQUEST_CODE_CAMERA);
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                //从相册中选择
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, REQUEST_CODE_ALBUM);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_OK) {
            return;
        }
        switch (requestCode) {
            case REQUEST_CODE_CAMERA:
                if (SDcardTools.hasSdcard()) {
                    File tempFile = new File(Environment.getExternalStorageDirectory() + IMAGE_USER_AVATAR_NAME);
                    startCropPhoto(Uri.fromFile(tempFile));
                } else {
                    XuToast.show(AppContext.getInstance(), "can not use sd card");
                }
                break;
            case REQUEST_CODE_CROP_IMAGE:
                saveAvatar(data);
                break;
            case REQUEST_CODE_ALBUM:
                String realPath = AppHelper.getRealPath(getActivity(), data.getData());
                startCropPhoto(Uri.parse(realPath));
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_CAMERA_PERMISSION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Intent intentFromCapture = new Intent(ACTION_IMAGE_CAPTURE);
                    // 判断存储卡是否可以用，可用则进行存储
                    if (SDcardTools.hasSdcard()) {
                        intentFromCapture.putExtra(MediaStore.EXTRA_OUTPUT,
                                Uri.fromFile(new File(Environment.getExternalStorageDirectory(), IMAGE_USER_AVATAR_NAME)));
                    }
                    startActivityForResult(intentFromCapture, REQUEST_CODE_CAMERA);
                } else {
                    XuToast.show(getActivity(), STR(R.string.allow_camera_permission));
                }
                break;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 裁剪图片方法实现
     */
    public void startCropPhoto(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 设置裁剪
        intent.putExtra("crop", "true");
        /* //裁剪区域设置为圆形
            intent.putExtra("circleCrop",true);*/
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param data
     */
    private void saveAvatar(Intent data) {
        Bundle extras = data.getExtras();
        if (extras != null && extras.getParcelable("data") != null) {
            final Bitmap photo = extras.getParcelable("data");
            XuFileUtils.saveBitmap(photo, AppConfig.EXTERNAL_DIR + "/" + "/user_face.png", 100);
            AppApi.updateAvatar(AppContext.getInstance().getUser().getId(), AppConfig.EXTERNAL_DIR + "/" + "/user_face.png", new ResultCallback<ResultData<String>>() {
                @Override
                public void onFailure(Request request, Exception e) {
                    XuToast.show(getActivity(), STR(R.string.upload_fail));
                }

                @Override
                public void onResponse(ResultData<String> response) {
                    headView.setImageBitmap(photo);
                    XuToast.show(getActivity(), STR(R.string.upload_success));
                }
            });
        }
    }

    private void saveUserInfo(final int sex, final @NonNull String phone, final @NonNull String email) {
        if ((sex != 0 && sex != 1)) {
            XuToast.show(getActivity(), STR(R.string.edit_input_legal_value));
            return;
        }

        DialogHelp.getInstance().connectDialog(getActivity(), STR(R.string.updateing));

        AppApi.modifyUserInfo(_userId, sex, phone, email, new ResultCallback<ResultData<String>>() {
            @Override
            public void onFailure(Request request, Exception e) {
                XuToast.show(getActivity(), STR(R.string.save_fail));
                DialogHelp.getInstance().hideDialog();
            }

            @Override
            public void onResponse(ResultData<String> response) {
                XuToast.show(getActivity(), STR(response.getResult() == 0 ? R.string.save_success : R.string.save_fail));
                if (response.getResult() == 0) {
                    UserInfo user = new UserInfo();
                    user.setPhone(phone);
                    user.setEmail(email);
                    user.setSex(sex);
                    AppContext.getInstance().setUser(user);
                    _handler.post(_refreshView);
                }
                DialogHelp.getInstance().hideDialog();
            }
        });
    }

}
