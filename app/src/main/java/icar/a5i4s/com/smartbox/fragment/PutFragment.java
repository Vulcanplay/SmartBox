package icar.a5i4s.com.smartbox.fragment;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.afollestad.materialdialogs.DialogAction;
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
import icar.a5i4s.com.smartbox.adapter.VinAdapter;
import icar.a5i4s.com.smartbox.helper.Act;
import icar.a5i4s.com.smartbox.helper.MaterialDialogTool;
import icar.a5i4s.com.smartbox.helper.Tools;
import icar.a5i4s.com.smartbox.helper.WebServicesTool;
import icar.a5i4s.com.smartbox.module.InterAction;
import icar.a5i4s.com.smartbox.module.ResultData;
import icar.a5i4s.com.smartbox.module.Vin;
import okhttp3.Call;


public class PutFragment extends Fragment implements AdapterView.OnItemClickListener, TextWatcher, View.OnClickListener {
    protected WebServicesTool webServicesTool;
    MaterialDialog boxDialog, dialog;
    protected View fragmentView;
    private List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
    protected List<Vin> vl;

    protected EditText searchET;
    protected ListView lv;
    protected Button success, returnMenu;
    protected TextView notVin;
    protected ImageView submit;

    protected VinAdapter adapter;

    /** serialport **/
    private SerialPort mSerialPort ;
    private InputStream is ;
    private OutputStream os ;
    private RecvThread recvThread ;
    private String currentHz;

    private OnFragmentInteractionListener mListener;
    public PutFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_put, container, false);

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        myOpen();
        initView(getView());
        super.onActivityCreated(savedInstanceState);
    }

    //1.加载视图
    private void initView(View view) {
        fragmentView = view;
        webServicesTool = new WebServicesTool(getActivity());
        //初始化控件
        lv = (ListView) view.findViewById(R.id.list);
        success = (Button) view.findViewById(R.id.success);
        returnMenu = (Button) view.findViewById(R.id.return_menu);
        searchET = (EditText) view.findViewById(R.id.search_edittext);
        notVin = (TextView) view.findViewById(R.id.not_find_vin);
        submit = (ImageView) view.findViewById(R.id.search_submit);
        lv.setOnItemClickListener(this);
        searchET.addTextChangedListener(this);
        submit.setOnClickListener(this);
        success.setOnClickListener(this);
        returnMenu.setOnClickListener(this);
        getDatum("");
    }
    /*
    * 获取数据
    * params input
    * */
    public void getDatum(String input) {
        dialog = new MaterialDialog.Builder(getContext())
                .content(R.string.please_wait_loading)
                .progress(true, 0)
                .autoDismiss(false)
                .show();
            webServicesTool.Connect("selectReceivingDetailBox.do")
                .addParams("vin", input)
                .addParams("shopId", getActivity().getSharedPreferences("userId", Activity.MODE_PRIVATE).getString("shopId", "").toString())
                .build().execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.w("====PUT DATUM ERROR==", e.toString());
                        MaterialDialogTool.getInterError(getActivity()).show();
                        dialog.dismiss();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        dialog.dismiss();
                        ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                        data = (List<Map<String, Object>>) resultData.getDataList();
                        vl = new ArrayList<Vin>();
                        if (resultData.getSuccess()){
                            Log.w("====PUT LIST SUCCESS", data.toString());
                            for (Map<String, Object> map : data) {
                                vl.add(new Vin(map.get("id").toString(), map.get("vin").toString()));
                            }
                            adapter = new VinAdapter(vl, getActivity(), Act.ACTION_ADAPTER_PUT);
                            lv.setAdapter(adapter);
                        } else if(data == null){
                            notVin.setVisibility(View.VISIBLE);
                            adapter = new VinAdapter(vl, getActivity(), Act.ACTION_ADAPTER_PUT);
                            lv.setAdapter(adapter);
                        } else {
                            MaterialDialogTool.getErrorMessage(getActivity(), resultData.getErrorMassge()).show();
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
        close();
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        id = ((TextView) ((LinearLayout) view).getChildAt(0)).getTag(R.id.tag_id).toString();
        vin = ((TextView) ((LinearLayout) view).getChildAt(0)).getText().toString();
        boxCode = Act.getBoxCode();
        updateUser = getActivity().getSharedPreferences("userId", Activity.MODE_PRIVATE).getString("id", "").toString();
        Log.w("====ID", id);
        Log.w("====VIN ", vin);
        Log.w("====boxCode", boxCode);
        Log.w("====操作人员", updateUser);
        if (Act.isPush){
            view.setBackgroundColor(getResources().getColor(R.color.pink));
            webServicesTool.Connect("getEmptyBoxCell.do")
                    .addParams("boxCode", Act.getBoxCode())
                    .build().execute(new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Log.w("====GET CELL ID ERROR==", e.toString());
                      MaterialDialogTool.getInterError(getActivity()).show();
                }

                @Override
                public void onResponse(String response, int id) {
                    ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                    if (resultData.getSuccess()) {
                        data = (List<Map<String, Object>>) resultData.getDataList();
                        boxDialog = new MaterialDialog.Builder(getContext())
                                .content(R.string.please_wait_loading)
                                .progress(true, 0)
                                .autoDismiss(false)
                                .cancelable(false)
                                .progressIndeterminateStyle(true)
                                .show();
                        boxCellCode = data.get(0).get("boxCellCode").toString();
                        Log.w("======开启的格子号", boxCellCode);
                        try {
                            os.write(Tools.HexString2Bytes(Act.turn + Tools.encodeHex(Integer.parseInt(boxCellCode))));//转动到指定格子
                            boxDialog.setContent(String.format(getString(R.string.please_wait_open_grid), boxCellCode));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        MaterialDialogTool.getErrorMessage(getActivity(), resultData.getErrorMassge()).show();
                    }
                }
            });
        } else {
            for (int j = 0; j < adapterView.getChildCount(); j++){
                LinearLayout ll = (LinearLayout) adapterView.getChildAt(j);
                ll.setBackground(getResources().getDrawable(R.drawable.bottom_item_pink));
            }
            view.setBackgroundColor(getResources().getColor(R.color.pink));
        }
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        if (charSequence.length() == 0){
//            getDatum("");
//        }
    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    protected int action = 0;
    protected String id = "";
    protected String vin = "";
    protected String boxCode = "";
    protected String boxCellCode = "";
    protected String updateUser = "";
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search_submit:
                getDatum(searchET.getText().toString());
                adapter.notifyDataSetChanged();
                break;
            case R.id.success:
                //提示确认操作
                new MaterialDialog.Builder(getActivity())
                        .title(R.string.alert)
                        .content(R.string.is_submit)
                        .positiveText(R.string.confirm)
                        .negativeText(R.string.payResultClose)
                        .onPositive(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull final MaterialDialog dialogs, @NonNull DialogAction which) {
                                webServicesTool.Connect("updateReceivingDetailBox.do")
                                        .addParams("id", id)
                                        .addParams("vin", vin)
                                        .addParams("boxCode", boxCode)
                                        .addParams("boxCellCode", boxCellCode)
                                        .addParams("updateUser", updateUser)
                                        .build().execute(new StringCallback() {
                                    @Override
                                    public void onError(Call call, Exception e, int id) {
                                        Log.w("====PUT SUBMIT ERROR==", e.toString());
                                        MaterialDialogTool.getInterError(getActivity()).show();
                                        dialog.dismiss();
                                    }

                                    @Override
                                    public void onResponse(String response, int id) {
                                        ResultData resultData = WebServicesTool.getOrderGson().fromJson(response, new TypeToken<ResultData>(){}.getType());
                                        if (resultData.getSuccess()){
                                            action = Act.ACTION_SUCCESS;
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
                        })
                        .onNegative(new MaterialDialog.SingleButtonCallback() {
                            @Override
                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {dialog.dismiss();
                            }
                        })
                        .show();
                break;
            case R.id.return_menu:
                //标示返回菜单操作
                action = Act.ACTION_RETURN_MENU;
                if (Act.isPush){
                    getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.console_fragment, new MenuFragment()).commit();
                    mListener.onFragmentInteraction(new InterAction(Act.URI_MENU, Act.TAG_MENU_TO_CONSOLE));
                } else {
                    try {
                        os.write(Tools.HexString2Bytes(Act.pushIn));//推入
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    boxDialog.show();
                    boxDialog.setContent(getString(R.string.please_wait_close_put));
                }
                break;
        }
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
            Act.isPush = false;
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    boxDialog.dismiss();
                    success.setEnabled(true);
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
                        Act.isPush = true;
                        switch (action){
                            case Act.ACTION_RETURN_MENU:
                                getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.console_fragment, new MenuFragment()).commit();
                                mListener.onFragmentInteraction(new InterAction(Act.URI_MENU, Act.TAG_MENU_TO_CONSOLE));
                                break;
                            case Act.ACTION_SUCCESS:
                                success.setEnabled(false);
                                getDatum("");
                                break;
                        }
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
    public void onPause() {
        close();
        super.onPause();
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
