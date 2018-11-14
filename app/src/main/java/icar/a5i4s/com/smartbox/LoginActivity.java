package icar.a5i4s.com.smartbox;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import icar.a5i4s.com.smartbox.helper.Act;
import icar.a5i4s.com.smartbox.helper.CountDownTimerTool;
import icar.a5i4s.com.smartbox.helper.MaterialDialogTool;
import icar.a5i4s.com.smartbox.helper.WebServicesTool;
import icar.a5i4s.com.smartbox.module.ResultData;
import okhttp3.Call;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    //获取本地存储
    protected SharedPreferences sf;
    protected WebServicesTool webServicesTool;
    protected EditText userName;
    protected EditText password;
    protected Button signInButton;
    Intent intent;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_login);
        intent = new Intent();
        intent.setClass(LoginActivity.this, ConsoleActivity.class);
        sf = getSharedPreferences("userId", Activity.MODE_PRIVATE);
        if(!sf.getString("id", "").equals("")){
            startActivity(intent);
        }
        init();
    }
    protected void init(){
        webServicesTool = new WebServicesTool(this);
        userName = (EditText) findViewById(R.id.account);
        password = (EditText) findViewById(R.id.password);
        signInButton = (Button) findViewById(R.id.login_button);
        signInButton.setOnClickListener(this);
    }
    //表单验证
    protected boolean checkForm(String userName, String password){
        if (userName.equals("")){
            this.userName.requestFocus();
            return false;
        } else if(password.equals("")){
            this.password.requestFocus();
            return false;
        } else {
            return true;
        }
    }


    protected MaterialDialog dialog;
    String us = "";
    String pw = "";
    View popWindowNeedViewId;
    @Override
    public void onClick(View v) {
        popWindowNeedViewId = v;
        switch (v.getId()){
            case R.id.login_button:
                us = userName.getText().toString();
                pw = password.getText().toString();
                if (checkForm(userName.getText().toString(), password.getText().toString())){
                    dialog = new MaterialDialog.Builder(this)
                            .content(R.string.please_wait_login)
                            .progress(true, 0)
                            .cancelable(false)
                            .show();
                    webServicesTool.Connect("boxLogin.do")
                            .addParams("userName", us)
                            .addParams("userPwd", pw)
                            .addParams("boxCode", Act.getBoxCode())
                            .build().execute(new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Log.w("====登录失败", e.toString());
                            new MaterialDialog.Builder(LoginActivity.this)
                                    .title(getString(R.string.alert))
                                    .content("网络连接失败")
                                    .positiveText(getString(R.string.payResultClose))
                                    .show();
                            dialog.dismiss();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            Log.w("====登录成功", response);
                            ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                            if (resultData.getSuccess()){
                                data = (List<Map<String, Object>>) resultData.getDataList();
                                String userId = data.get(0).get("userId").toString();
                                String userAuthorityId = data.get(0).get("authorityId").toString();
                                String shopId = data.get(0).get("shopId").toString();
                                String boxId = data.get(0).get("boxId").toString();
                                String mobile = data.get(0).get("mobile").toString();
                                Log.d("=====", "用户ID：" + userId);
                                Log.d("=====", "权限ID：" + userAuthorityId);
                                Log.d("=====", "SHOP ID：" + shopId);
                                Log.d("=====", "box ID：" + boxId);
                                Log.d("=====", "手机号：" + mobile);
                                if (!userAuthorityId.equals("0")){
                                    sendMobileCode(popWindowNeedViewId,data.get(0).get("verifyCode").toString(), userId, userAuthorityId, shopId, boxId, mobile);
                                }else {
                                    SharedPreferences sp;
                                    SharedPreferences.Editor editor;
                                    //存储到 XML
                                    sp = getApplicationContext().getSharedPreferences("userId", 0);
                                    editor = sp.edit();
                                    editor.putString("id", userId);
                                    editor.putString("authorityId", userAuthorityId);
                                    editor.putString("shopId", shopId);
                                    editor.putString("boxId", boxId);
                                    editor.putString("mobile", mobile);
                                    editor.commit();
                                    startActivity(intent);
                                }
                            }else{
                                new MaterialDialog.Builder(LoginActivity.this)
                                        .title(getString(R.string.alert))
                                        .content(resultData.getErrorMassge())
                                        .positiveText(getString(R.string.payResultClose))
                                        .show();
                            }
                            dialog.dismiss();
                        }
                    });
                } else {
                    new MaterialDialog.Builder(LoginActivity.this)
                            .title(getString(R.string.alert))
                            .content(getString(R.string.sign_not_null))
                            .positiveText(getString(R.string.payResultClose))
                            .show();
                }
                break;
        }
    }

    protected PopupWindow popupWindow;
    protected View popWidowView;
    protected EditText codeEditText;
    protected Button codeSubmit, getVerifyCode;
    protected TextView mobileForVerifyCode;
    private void initPopWindow(){
        popWidowView = getLayoutInflater().inflate(R.layout.send_code, null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popWidowView, 600, 400, true);
        popupWindow.setAnimationStyle(R.style.AnimationFade);
    }
    protected String verifyCode = "";
    private void sendMobileCode(View v,final String code, final String userId, final String authorityId, final String shopId, final String boxId, final String mobile) {
        initPopWindow();
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        codeEditText = (EditText) popWidowView.findViewById(R.id.code);
        codeSubmit = (Button) popWidowView.findViewById(R.id.code_submit_button);
        getVerifyCode = (Button) popWidowView.findViewById(R.id.get_verifyCode);
        mobileForVerifyCode = (TextView) popWidowView.findViewById(R.id.mobile_for_verifyCode);
        mobileForVerifyCode.setText(String.format(getString(R.string.input_mobile_for_verifyCode), mobile.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2")));
        //验证码编辑框获取焦点
        codeEditText.requestFocus();
        codeEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (codeEditText.getText().toString().length() == 4){
                    codeSubmit.setEnabled(true);
                }else{
                    codeSubmit.setEnabled(false);
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
        //禁用获取验证码
        new CountDownTimerTool(this, getVerifyCode, 60000, 1000).start();
        //创建验证码
        verifyCode = code;
        getVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                webServicesTool.Connect("sendVerifyCode.do")
                        .addParams("mobile", mobile)
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        MaterialDialogTool.getInterError(getApplicationContext()).show();
                    }
                    @Override
                    public void onResponse(String response, int id) {
                        ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                        if (resultData.getSuccess()){
                            data = (List<Map<String, Object>>) resultData.getDataList();
                            verifyCode = data.get(0).get("verifyCode").toString();
                            new CountDownTimerTool(getApplicationContext(), getVerifyCode, 60000, 1000).start();
                            Log.w("=====VerifyCode", verifyCode);
                        } else {
                            MaterialDialogTool.getErrorMessage(getApplicationContext(), resultData.getErrorMassge());
                        }
                    }
                });
            }
        });
        //提交验证码单击事件
        codeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputCode = codeEditText.getText().toString();
                        if (inputCode.equals(verifyCode)){
                            SharedPreferences sp;
                            SharedPreferences.Editor editor;
                            //存储到 XML
                            sp = getApplicationContext().getSharedPreferences("userId", 0);
                            editor = sp.edit();
                            editor.putString("id", userId);
                            editor.putString("authorityId", authorityId);
                            editor.putString("shopId", shopId);
                            editor.putString("boxId", boxId);
                            editor.putString("mobile", mobile);
                            editor.commit();
                            startActivity(intent);
                        } else {
                            dialog.show();
                            new MaterialDialog.Builder(LoginActivity.this)
                                    .title(getString(R.string.alert))
                                    .content(getString(R.string.input_code_error))
                                    .positiveText(getString(R.string.payResultClose))
                                    .show();
                        }
            }
        });
    }

    @Override
    protected void onStop() {
        this.finish();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.finish();
        super.onDestroy();
    }
}
