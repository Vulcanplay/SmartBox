package icar.a5i4s.com.smartbox.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import icar.a5i4s.com.smartbox.R;
import icar.a5i4s.com.smartbox.helper.Act;
import icar.a5i4s.com.smartbox.helper.BoxUtils;

import com.afollestad.materialdialogs.MaterialDialog;
import com.example.uartdemo.SerialPort;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zhy.http.okhttp.callback.StringCallback;

import org.w3c.dom.Text;

import icar.a5i4s.com.smartbox.helper.MaterialDialogTool;
import icar.a5i4s.com.smartbox.helper.Tools;
import icar.a5i4s.com.smartbox.helper.WebServicesTool;
import icar.a5i4s.com.smartbox.module.Grid;
import icar.a5i4s.com.smartbox.module.InterAction;
import icar.a5i4s.com.smartbox.module.ResultData;
import icar.a5i4s.com.smartbox.module.checkData;
import okhttp3.Call;


public class CheckFragment extends Fragment {

    MaterialDialog dialog, submitDialog;

    protected RadioGroup radioCheckStatusGroup, radioKeyStatusGroup;
    //一致 未存 不一致
    protected RadioButton radioIdentical, radioNothingness, radioDisagree, radioKeyIdentical, radioKeyNothingness, radioKeyDisagree;
    protected EditText remarksValue, keyValue, heGeZheng;
    protected int certificateCheckResult = -1, keyCheckResult = -1;
    protected String checkId;

    protected WebServicesTool webServicesTool;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    protected View fragmentView;
    protected List<Grid> gridList = new ArrayList<Grid>();
    protected LinearLayout row0, row1, row2, row3, row4;
    protected RelativeLayout rowChild;
    protected ImageView childTop, childCenter;
    protected TextView childVIN,childCertificateCheckResult,childComment;
    protected Button gridSubmitButton;

    /** serialport **/
    private SerialPort mSerialPort ;
    private InputStream is ;
    private OutputStream os ;
    private RecvThread recvThread ;
    private String currentHz;

    private OnFragmentInteractionListener mListener;
    private View.OnClickListener gridSubmitButtonListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (certificateCheckResult != -1 && keyCheckResult != -1) {
                String json = new Gson().toJson(new checkData(certificateCheckResult,remarksValue.getText().toString(),getActivity().getSharedPreferences("userId", 0).getString("id", ""), checkId, heGeZhengET.getText().toString(), keyCountET.getText().toString(), keyCheckResult + ""));
                Log.d("===json", json);
                submitDialog = new MaterialDialog.Builder(getActivity())
                        .content(R.string.please_wait_submit)
                        .progress(true, 0)
                        .autoDismiss(false)
                        .cancelable(false)
                        .show();
                webServicesTool.Connect("updateWatchCheck.do")
                        .addParams("checkData", json)
                        .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("====核查提交失败", e.toString());
                        new MaterialDialog.Builder(getActivity())
                                .title(getString(R.string.alert))
                                .content("网络连接失败")
                                .positiveText(getString(R.string.payResultClose))
                                .show();
                        submitDialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.e("====待核查核查成功", response);
                        ResultData resultData = new Gson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                        if (resultData.getSuccess()){
                            try {
                                os.write(Tools.HexString2Bytes(Act.pushIn));//推入
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            submitDialog.dismiss();
                            dialog.show();
                            dialog.setContent(getString(R.string.please_wait_close_put));
                        } else {
                            new MaterialDialog.Builder(getActivity())
                                    .title(getString(R.string.alert))
                                    .content(resultData.getErrorMassge())
                                    .positiveText(getString(R.string.payResultClose))
                                    .show();
                        }
                    }
                });
            } else {
                new MaterialDialog.Builder(getActivity())
                        .title(getString(R.string.alert))
                        .content(getString(R.string.dont_check_radio))
                        .positiveText(getString(R.string.payResultClose))
                        .show();
            }
        }
    };

    public CheckFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        myOpen();
        super.onCreate(savedInstanceState);
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
                    Thread.sleep(10);
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
                        dialog.setContent(getString(R.string.please_wait_open_put));
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
                        dialog.setContent(getString(R.string.please_wait_close_door));
                    }
                });
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }else if(Act.pushOutHz.equals(currentHz)){ //弹出完成
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();
                    gridSubmitButton.setEnabled(true);
                }
            });
        }else if(Act.turnHz.equals(currentHz)){//转动完成
            try {
                os.write(Tools.HexString2Bytes(Act.openDoor));//开门
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dialog.setContent(getString(R.string.please_wait_open_door));
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
                    dialog.dismiss();
                    popupWindow.dismiss();
                    //重置全局变量
                    certificateCheckResult = -1;
                    //加载视图
                    initView(fragmentView);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_check, container, false);
        // Inflate the layout for this fragment
        myOpen();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initView(view);
        myOpen();
    }

    private void initView(View view) {
        dialog = new MaterialDialog.Builder(getContext())
                .content(R.string.please_wait_loading)
                .progress(true, 0)
                .autoDismiss(false)
                .show();
        fragmentView = view;
        webServicesTool = new WebServicesTool(getActivity());
        //初始化控件
        row0 = (LinearLayout) view.findViewById(R.id.row1);
        row1 = (LinearLayout) view.findViewById(R.id.row2);
        row2 = (LinearLayout) view.findViewById(R.id.row3);
        row3 = (LinearLayout) view.findViewById(R.id.row4);
        row4 = (LinearLayout) view.findViewById(R.id.row5);
        Log.d("====User ID", getContext().getSharedPreferences("userId", Activity.MODE_PRIVATE).getString("id", "").toString());
        webServicesTool.Connect("getWatchCheckDatas.do")
                .addParams("checkUserId", getContext().getSharedPreferences("userId", Activity.MODE_PRIVATE).getString("id", "").toString())
                .addParams("boxCode", Act.getBoxCode())
                .build().execute(new StringCallback(){

            @Override
            public void onError(Call call, Exception e, int id) {
                Log.e("====待核查加载失败", e.toString());
                MaterialDialogTool.getInterError(getActivity());
                dialog.dismiss();
            }

            @Override
            public void onResponse(String response, int id) {
                Log.e("====待核查加载成功", response);
                ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                if (resultData.getSuccess()){
                    data = (List<Map<String, Object>>) resultData.getDataList();
                    Log.d("====核查数据", data.toString());
                    if (data.size() != 0){
                        //遍历dataList
                        for (Map<String, Object> map : data) {
                            gridList.add(new Grid(
                                    map.get("checkId").toString(),
                                    map.get("boxId").toString(),
                                    map.get("certificateCheckResult") == null ? "-1" : map.get("certificateCheckResult").toString(),
                                    map.get("boxCellCode").toString(),
                                    map.get("vin").toString(),
                                    map.get("certificateId") == null ? "" : map.get("certificateId").toString(),
                                    map.get("certificateCheckResult") == null ? "" : map.get("certificateCheckResult").toString(),
                                    map.get("comment") == null ? "" : map.get("comment").toString(),
                                    map.get("keyQuantity") == null ? "" : map.get("keyQuantity").toString(),
                                    map.get("keyCheckResult") == null ? "-1" : map.get("keyCheckResult").toString()
                            ));
                        }
                        int row,col,cellCode,checkStatus,checkResultStatus,keyCheckStatus;
                        String vin;
                        LinearLayout nowRow = null;
                        for (Grid g : gridList){
                            cellCode = Integer.parseInt(g.getBoxCellCode()) - 1;
                            checkResultStatus = Integer.parseInt(g.getCheckStatus());
                            keyCheckStatus = Integer.parseInt(g.getKeyCheckResult());

                            //根据两个核查结果判断
                            if (checkResultStatus == -1 && keyCheckStatus == -1){
                                checkStatus = -1;
                            } else if (checkResultStatus ==0 && keyCheckStatus == 0){
                                checkStatus = 0;
                            } else {
                                checkStatus = 1;
                            }
                            vin = g.getVin();
                            row = cellCode / 8;
                            col = cellCode % 8;
                            switch (row){
                                case 0:
                                    nowRow = row0;
                                    break;
                                case 1:
                                    nowRow = row1;
                                    break;
                                case 2:
                                    nowRow = row2;
                                    break;
                                case 3:
                                    nowRow = row3;
                                    break;
                                case 4:
                                    nowRow = row4;
                                    break;
                            }
                            //获取块
                            rowChild = (RelativeLayout) nowRow.getChildAt(col);
                            //获取块的子视图
                            //顶部图标
                            childTop = (ImageView) rowChild.getChildAt(0);
                            //中间图标
                            childCenter = (ImageView) rowChild.getChildAt(2);
                            //VIN号码
                            childVIN = (TextView) rowChild.getChildAt(3);
                            //核查标示
                            childCertificateCheckResult = (TextView) rowChild.getChildAt(4);
                            childComment = (TextView) rowChild.getChildAt(5);
                            childVIN.setTag(g.getCheckId());
                            childCertificateCheckResult.setText(g.getCertificateCheckResult());
                            childComment.setText(g.getComment());
                            //保存 VIN 和 核查标示
                            childVIN.setText(vin);
                            childVIN.setTag(R.id.certificateId, g.getCertificateId());
                            childVIN.setTag(R.id.keyQuantity, g.getKeyQuantity());
                            childVIN.setTag(R.id.keyCheckResult, g.getKeyCheckResult());
                            //判断状态
                            switch (checkStatus){
                                //待查
                                case -1:
                                    childCenter.setImageResource(R.drawable.icon_center_yellow);
                                    childTop.setImageResource(R.drawable.icon_top_yellow);
                                    rowChild.setOnClickListener(waitCheckListener);
                                    break;
                                //已查
                                case 0:
                                    childCenter.setImageResource(R.drawable.icon_center_green);
                                    childTop.setImageResource(R.drawable.icon_top_green);
                                    rowChild.setOnClickListener(ErrorAndCheckedCheckListener);
                                    break;
                                //异常
                                default:
                                    childCenter.setImageResource(R.drawable.icon_center_red);
                                    childTop.setImageResource(R.drawable.icon_top_red);
                                    rowChild.setOnClickListener(ErrorAndCheckedCheckListener);
                                    break;
                            }
                        }
                    } else {
                        //无数据
                        MaterialDialogTool.getErrorMessage(getActivity(), resultData.getErrorMassge());
                    }
                }else{
                    MaterialDialogTool.getErrorMessage(getActivity(), resultData.getErrorMassge());
                }
                dialog.dismiss();
            }
        });
    }
    View.OnClickListener ErrorAndCheckedCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            RelativeLayout v = (RelativeLayout) view;
            TextView vin = (TextView) v.getChildAt(3);
            //钥匙核查结果
            String keyCheckResultValue = vin.getTag(R.id.keyCheckResult).toString();
            String keyCount = vin.getTag(R.id.keyQuantity).toString();
            String certificateId = vin.getTag(R.id.certificateId).toString();
            TextView cellCode = (TextView) v.getChildAt(1);
            TextView childCertificateCheckResult = (TextView) v.getChildAt(4);
            TextView comment = (TextView) v.getChildAt(5);
            String childCertificateCheckResultString = "";
            String keyCheckResult = "";
            switch (Integer.parseInt(childCertificateCheckResult.getText().toString())){
                case 0:
                    childCertificateCheckResultString = "一致";
                    break;
                case 1:
                    childCertificateCheckResultString = "不一致";
                    break;
                case 2:
                    childCertificateCheckResultString = "未存入";
                    break;
            }
            switch (Integer.parseInt(keyCheckResultValue)){
                case 0:
                    keyCheckResult = "押齐";
                    break;
                case 1:
                    keyCheckResult = "未押齐";
                    break;
                case 2:
                    keyCheckResult = "未存入";
                    break;
            }
            new MaterialDialog.Builder(getActivity())
                    .title(cellCode.getText().toString() + "号")
                    .content("VIN号: " + vin.getText().toString() + "\n\n"
                            +"合格证号: " + certificateId + "\n\n"
                            +"核查结果: " + childCertificateCheckResultString + "\n\n"
                            +"钥匙数量: " + keyCount + "\n\n"
                            +"钥匙数量核查结果: " + keyCheckResult + "\n\n"
                            +"备注: " + comment.getText().toString())
                    .positiveText(getString(R.string.payResultClose))
                    .cancelable(false)
                    .show();
        }
    };
    protected TextView vinValue;
    protected EditText heGeZhengET, keyCountET;
    View.OnClickListener waitCheckListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            dialog = new MaterialDialog.Builder(getContext())
                    .content(R.string.please_wait_loading)
                    .progress(true, 0)
                    .autoDismiss(false)
                    .cancelable(false)
                    .progressIndeterminateStyle(true)
                    .show();
            initGridPopWindow();
            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            RelativeLayout v = (RelativeLayout) view;
            TextView vin = (TextView) v.getChildAt(3);
            TextView cellCode = (TextView) v.getChildAt(1);
            int cellCodeInt = Integer.parseInt(cellCode.getText().toString());
            vinValue = (TextView) popWidowView.findViewById(R.id.vin_value);
            heGeZhengET = (EditText) popWidowView.findViewById(R.id.hegezheng_value);
            keyCountET = (EditText) popWidowView.findViewById(R.id.key_count_value);
            vinValue.setText(vin.getText().toString());
            heGeZheng.setText(vin.getTag(R.id.certificateId).toString());
            keyCountET.setText(vin.getTag(R.id.keyQuantity).toString());
            String keyQuantity = vin.getTag(R.id.keyQuantity).toString();

            checkId = vin.getTag().toString();
            //获取CheckID
            Log.d("=====", cellCode.getText().toString());
            try {
                os.write(Tools.HexString2Bytes(Act.turn + Tools.encodeHex(cellCodeInt)));//转动到指定格子
                dialog.setContent(String.format(getString(R.string.please_wait_open_grid), cellCode.getText().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("===OnClick Get VIN", vin.getText().toString());
        }
    };

    protected PopupWindow popupWindow;
    protected View popWidowView;
    private void initGridPopWindow() {
        popWidowView = getActivity().getLayoutInflater().inflate(R.layout.show_grid , null, false);
        // 创建PopupWindow实例,200,LayoutParams.MATCH_PARENT分别是宽度和高度
        popupWindow = new PopupWindow(popWidowView, 750, 500, true);
        popupWindow.setAnimationStyle(R.style.AnimationFade);

        //提交按钮
        gridSubmitButton = (Button) popWidowView.findViewById(R.id.grid_submit_button);
        gridSubmitButton.setOnClickListener(gridSubmitButtonListener);
        radioCheckStatusGroup = (RadioGroup) popWidowView.findViewById(R.id.check_result_radio_group);
        radioDisagree = (RadioButton) popWidowView.findViewById(R.id.radio_disagree);
        radioIdentical = (RadioButton) popWidowView.findViewById(R.id.radio_identical);
        radioNothingness = (RadioButton) popWidowView.findViewById(R.id.radio_nothingness);

        radioKeyStatusGroup = (RadioGroup) popWidowView.findViewById(R.id.check_key_radio_group);
        radioKeyDisagree = (RadioButton) popWidowView.findViewById(R.id.radio_key_disagree);
        radioKeyIdentical = (RadioButton) popWidowView.findViewById(R.id.radio_key_identical);
        radioKeyNothingness = (RadioButton) popWidowView.findViewById(R.id.radio_key_nothingness);

        remarksValue = (EditText) popWidowView.findViewById(R.id.remarks_value);
        keyValue = (EditText) popWidowView.findViewById(R.id.key_count_value);
        heGeZheng = (EditText) popWidowView.findViewById(R.id.hegezheng_value);
        radioCheckStatusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == radioIdentical.getId()){
                    certificateCheckResult = 0;
                } else if (i == radioDisagree.getId()){
                    certificateCheckResult = 1;
                } else if (i == radioNothingness.getId()){
                    certificateCheckResult = 2;
                }
            }
        });
        radioKeyStatusGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (i == radioKeyIdentical.getId()){
                    keyCheckResult = 0;
                } else if (i == radioKeyDisagree.getId()){
                    keyCheckResult = 1;
                } else if (i == radioKeyNothingness.getId()){
                    keyCheckResult = 2;
                }
            }
        });
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(InterAction interAction);
    }
}
