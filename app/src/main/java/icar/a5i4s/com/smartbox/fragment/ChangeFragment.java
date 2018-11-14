package icar.a5i4s.com.smartbox.fragment;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.uartdemo.SerialPort;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import icar.a5i4s.com.smartbox.R;
import icar.a5i4s.com.smartbox.adapter.DoubleVinAdapter;
import icar.a5i4s.com.smartbox.adapter.VinAdapter;
import icar.a5i4s.com.smartbox.helper.Act;
import icar.a5i4s.com.smartbox.helper.CountDownTimerTool;
import icar.a5i4s.com.smartbox.helper.MaterialDialogTool;
import icar.a5i4s.com.smartbox.helper.Tools;
import icar.a5i4s.com.smartbox.helper.WebServicesTool;
import icar.a5i4s.com.smartbox.module.InterAction;
import icar.a5i4s.com.smartbox.module.ResultData;
import icar.a5i4s.com.smartbox.module.Vin;
import okhttp3.Call;


public class ChangeFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {
    protected int action = 0;

    protected WebServicesTool webServicesTool;
    MaterialDialog boxDialog, dialog;
    protected View fragmentView;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    protected List<Vin> vl;

    protected EditText searchET;
    protected ListView lv;
    protected Button success, finish;
    protected TextView notVin;
    protected ImageView submit;

    protected DoubleVinAdapter adapter;

    /** serialport **/
    private SerialPort mSerialPort ;
    private InputStream is ;
    private OutputStream os ;
    private RecvThread recvThread ;
    private String currentHz;
    private OnFragmentInteractionListener mListener;
    public ChangeFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //加载视图
        initView(getView());
        new MaterialDialog.Builder(getActivity())
                .title(getString(R.string.please_choice_action))
                .items(R.array.change)
                .cancelable(false)
                .itemsCallbackSingleChoice(0, new MaterialDialog.ListCallbackSingleChoice() {

                    @Override
                    public boolean onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {
                        if (text.toString().equals(getString(R.string.change))){
                            action = Act.ACTION_ADAPTER_CHANGE;
                            success.setText(getString(R.string.change));
                            url = "selectTradeBox.do";
                            urlUpdate = "updateTradeBox.do";
                        } else if (text.toString().equals(getString(R.string.cancel_change))){
                            action = Act.ACTION_ADAPTER_CANCEL_CHANGE;
                            success.setText(getString(R.string.cancel_change));
                            url = "selectTradeCancelBox.do";
                            urlUpdate = "updateTradeCancelBox.do";
                        }
                        shopId = getActivity().getSharedPreferences("userId", Activity.MODE_PRIVATE).getString("shopId", "").toString();
                        //根据选择，加载数据
                        getDatum("");
                        return false;
                    }
                })
                .positiveText(getString(R.string.confirm))
                .show();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }
    //1.加载视图
    private void initView(View view) {
        fragmentView = view;
        webServicesTool = new WebServicesTool(getActivity());
        //初始化控件
        lv = (ListView) view.findViewById(R.id.list);
        success = (Button) view.findViewById(R.id.success);
        finish = (Button) view.findViewById(R.id.finish);

        searchET = (EditText) view.findViewById(R.id.search_edittext);

        notVin = (TextView) view.findViewById(R.id.not_find_vin);
        submit = (ImageView) view.findViewById(R.id.search_submit);
        lv.setOnItemClickListener(this);

        submit.setOnClickListener(this);
        success.setOnClickListener(this);
        finish.setOnClickListener(this);
    }

    String url, urlUpdate, shopId;

    private void getDatum(final String inputIn) {
        dialog = new MaterialDialog.Builder(getContext())
                .content(getString(R.string.please_wait_loading))
                .progress(true, 0)
                .cancelable(false)
                .autoDismiss(false)
                .show();
        webServicesTool.Connect(url)
                .addParams("vin", inputIn)
                .addParams("shopId", shopId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                MaterialDialogTool.getInterError(getActivity());
                dialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                data = (List<Map<String, Object>>) resultData.getDataList();
                //初始化 In 集合
                vl = new ArrayList<Vin>();
                if (resultData.getSuccess()){
                    Log.w("====GET LIST SUCCESS", data.toString());
                    for (Map<String, Object> map : data) {
                        vl.add(new Vin(
                                map.get("idIn").toString(),
                                map.get("idOut").toString(),
                                map.get("vinIn").toString(),
                                map.get("vinOut").toString(),
                                map.get("boxCode").toString(),
                                map.get("boxCellCode").toString(),
                                map.get("ssId").toString()));
                    }
                    adapter = new DoubleVinAdapter(vl, getActivity(), Act.ACTION_ADAPTER_CHANGE);
                    lv.setAdapter(adapter);
                } else if (data == null){
                    Log.w("====GET LIST ", "DATUM NULL.");
                    notVin.setVisibility(View.VISIBLE);
                    adapter = new DoubleVinAdapter(vl, getActivity(), action);
                    lv.setAdapter(adapter);
                } else {
                    MaterialDialogTool.getErrorMessage(getActivity(), resultData.getErrorMassge()).show();
                }
                dialog.dismiss();
            }
        });
    }

    String idIn, vinIn, idOut, vinOut, boxCellCodeOut, boxCode, updateUser, ssId;
    boolean inIsChecked = false;
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        boxCode = Act.getBoxCode();
        updateUser = getActivity().getSharedPreferences("userId", Activity.MODE_PRIVATE).getString("id", "").toString();
            //In
        idIn = ((TextView) ((LinearLayout) view).getChildAt(0)).getTag(R.id.tag_id).toString();
        ssId = ((TextView) ((LinearLayout) view).getChildAt(0)).getTag(R.id.ssId).toString();
        vinIn = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();
        boxCellCodeOut = ((TextView) ((LinearLayout) view).getChildAt(0)).getTag(R.id.tag_box_cell_code).toString();
        idOut = ((TextView) ((LinearLayout) view).getChildAt(2)).getTag(R.id.tag_id).toString();
        vinOut = ((TextView) ((LinearLayout) view).getChildAt(2)).getText().toString();

        success.setEnabled(true);
        for (int j = 0; j < adapterView.getChildCount(); j++){
            LinearLayout ll = (LinearLayout) adapterView.getChildAt(j);
            ll.setBackground(getResources().getDrawable(R.drawable.bottom_item_green));
        }
        view.setBackgroundColor(getResources().getColor(R.color.green));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            //notVinIn
            case R.id.search_submit:
                getDatum(searchET.getText().toString());
                break;
            case R.id.success:
                //提示确认操作
                sendMobileCode(getView(), getActivity().getSharedPreferences("userId", 0).getString("mobile", "").toString());
                break;
            case R.id.finish:
                changeFinish();
                break;
        }
    }

    //完成
    private void changeFinish() {
        dialog.setContent(R.string.please_wait_loading);
        dialog.show();
        Log.w("===idOut", idOut);
        Log.w("===vinOut", vinOut);
        Log.w("===idIn", idIn);
        Log.w("===vinIn", vinIn);
        Log.w("===boxCode", boxCode);
        Log.w("===boxCellCodeOut", boxCellCodeOut);
        Log.w("===updateUser", updateUser);
        Log.w("===ssId", ssId);

        webServicesTool.Connect(urlUpdate)
                .addParams("idIn", idIn)
                .addParams("idOut", idOut)
                .addParams("vinIn", vinIn)
                .addParams("vinOut", vinOut)
                .addParams("boxCode", boxCode)
                .addParams("boxCellCode", boxCellCodeOut)
                .addParams("updateUser", updateUser)
                .addParams("ssId", ssId)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                MaterialDialogTool.getInterError(getActivity());
                dialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                dialog.dismiss();
                if (resultData.getSuccess()) {
                    try {
                        os.write(Tools.HexString2Bytes(Act.pushIn));//推入
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    boxDialog.show();
                    boxDialog.setContent(getString(R.string.please_wait_close_put));
                } else {
                    MaterialDialogTool.getErrorMessage(getActivity(), resultData.getErrorMassge()).show();
                }
            }
        });
    }

    protected PopupWindow popupWindow;
    protected View popWidowView;
    protected EditText codeEditText;
    protected Button codeSubmit, getVerifyCode;
    protected TextView mobileForVerifyCode;

    private void initPopWindow(){
        popWidowView = getActivity().getLayoutInflater().inflate(R.layout.send_code, null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popWidowView, 600, 400, true);
        popupWindow.setAnimationStyle(R.style.AnimationFade);
    }

    protected String verifyCode = "";
    protected String mobile = "";
    private void sendVerifyCode(String mobile){
        webServicesTool.Connect("sendVerifyCode.do")
                .addParams("mobile", mobile)
                .build().execute(new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                MaterialDialogTool.getInterError(getActivity()).show();
            }
            @Override
            public void onResponse(String response, int id) {
                ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                if (resultData.getSuccess()){
                    data = (List<Map<String, Object>>) resultData.getDataList();
                    verifyCode = data.get(0).get("verifyCode").toString();
                    new CountDownTimerTool(getActivity(), getVerifyCode, 60000, 1000).start();
                    Log.w("=====VerifyCode", verifyCode);
                } else {
                    MaterialDialogTool.getErrorMessage(getActivity(), resultData.getErrorMassge());
                }
            }
        });
    }

    private void sendMobileCode(View v, String m) {
        initPopWindow();
        this.mobile = m;
        sendVerifyCode(mobile);
        popupWindow.showAtLocation(v, Gravity.CENTER, 0, 0);
        codeEditText = (EditText) popWidowView.findViewById(R.id.code);
        codeSubmit = (Button) popWidowView.findViewById(R.id.code_submit_button);
        getVerifyCode = (Button) popWidowView.findViewById(R.id.get_verifyCode);
        mobileForVerifyCode = (TextView) popWidowView.findViewById(R.id.mobile_for_verifyCode);
        mobileForVerifyCode.setText(String.format(getString(R.string.input_mobile_for_verifyCode), m.replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2")));
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
        new CountDownTimerTool(getActivity(), getVerifyCode, 60000, 1000).start();
        getVerifyCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendVerifyCode(mobile);
            }
        });
        //提交验证码单击事件
        codeSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputCode = codeEditText.getText().toString();
                if (inputCode.equals(verifyCode)){
                    //关闭窗体
                    popupWindow.dismiss();
                    //开箱进度条
                    boxDialog = new MaterialDialog.Builder(getContext())
                            .content(R.string.please_wait_loading)
                            .progress(true, 0)
                            .autoDismiss(false)
                            .cancelable(false)
                            .progressIndeterminateStyle(true)
                            .show();
                    try {
                        //转动到指定格子
                        os.write(Tools.HexString2Bytes(Act.turn + Tools.encodeHex(Integer.parseInt(boxCellCodeOut))));
                        boxDialog.setContent(String.format(getString(R.string.please_wait_open_grid), boxCellCodeOut));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    new MaterialDialog.Builder(getActivity())
                            .title(getString(R.string.alert))
                            .content(getString(R.string.input_code_error))
                            .positiveText(getString(R.string.payResultClose))
                            .show();
                }
            }
        });
    }

    private class RecvThread extends Thread{
        @Override
        public void run() {
            super.run();
            try {
                while(!isInterrupted()){
                    int size = 0;
                    byte[] buffer = new byte[1024];
                    if(is == null){
                        return;
                    }
                    size = is.available();
                    if(size > 0){
                        Thread.sleep(10);
                        size = is.available();
                        is.read(buffer);
                        onDataReceived(buffer, size);
                    }
                }
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
    }

    private void myOpen(){
        //open
        //mSerialPort = new SerialPort();
        try {
            mSerialPort = new SerialPort(12, 115200, 0);
        }catch (Exception e) {
            return;
        }
        is = mSerialPort.getInputStream();
        os = mSerialPort.getOutputStream();
        recvThread = new RecvThread();
        recvThread.start();
    }

    private void onDataReceived(final byte[] buffer, final int size){
        currentHz = Tools.Bytes2HexString(buffer, size);
        if(Act.openHz.equals(currentHz)){//开门完成
            try {
                os.write(Tools.HexString2Bytes(Act.pushOut));//弹出
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boxDialog.setContent(getString(R.string.please_wait_open_put));
                    }
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(Act.pushInHz.equals(currentHz)){ //推入完成
            try {
                os.write(Tools.HexString2Bytes(Act.closeDoor));//关门
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boxDialog.setContent(getString(R.string.please_wait_close_door));
                    }
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(Act.pushOutHz.equals(currentHz)){ //弹出完成
            //已弹出完成
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boxDialog.dismiss();
                    /*
                    * 禁用 LIST VIEW
                    * 禁用 取证按钮
                    * 释放 完成按钮
                    * 更改 开箱标示
                    * */
                    lv.setEnabled(false);
                    success.setEnabled(false);
                    finish.setEnabled(true);
                    Act.isPush = false;
                }
            });
        }else if(Act.turnHz.equals(currentHz)){//转动完成
            try {
                os.write(Tools.HexString2Bytes(Act.openDoor));//开门
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        boxDialog.setContent(getString(R.string.please_wait_open_door));
                    }
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(Act.closeHz.equals(currentHz)){//关门完成
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boxDialog.dismiss();
                    if (!Act.isPush) {
                        /**
                         * 关门完成 更新状态
                         * @Act.isPush 标示已关门
                         * 释放 ListView 可选状态
                         * 禁用 取证按钮、完成按钮
                         * @getDatum()  刷新数据
                         * */
                        Act.isPush = true;
                        lv.setEnabled(true);
                        success.setEnabled(false);
                        finish.setEnabled(false);
                        getDatum("");
                    }
                }
            });
        }
    }
    //close serialport
    private void close(){
        if(recvThread != null){
            recvThread.interrupt();
        }
        if(mSerialPort != null){
            try {
                is.close();
                os.close();
            } catch (IOException e) {
//				e.printStackTrace();
            }
            mSerialPort.close(12);
        }
    }
    @Override
    public void onDestroy() {
        close();
        super.onDestroy();
    }

    @Override
    public void onStop() {
        close();
        super.onStop();
    }

    @Override
    public void onStart() {
        myOpen();
        super.onStart();
    }

    @Override
    public void onResume() {
        myOpen();
        super.onResume();
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(InterAction interAction);
    }
}
