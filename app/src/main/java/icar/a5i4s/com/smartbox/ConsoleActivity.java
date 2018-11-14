package icar.a5i4s.com.smartbox;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import icar.a5i4s.com.smartbox.fragment.ChangeFragment;
import icar.a5i4s.com.smartbox.fragment.CheckFragment;
import icar.a5i4s.com.smartbox.fragment.GetFragment;
import icar.a5i4s.com.smartbox.fragment.LendFragment;
import icar.a5i4s.com.smartbox.fragment.MenuFragment;
import icar.a5i4s.com.smartbox.fragment.PutFragment;
import icar.a5i4s.com.smartbox.helper.Act;
import icar.a5i4s.com.smartbox.module.InterAction;

public class ConsoleActivity extends FragmentActivity implements PutFragment.OnFragmentInteractionListener,ChangeFragment.OnFragmentInteractionListener, LendFragment.OnFragmentInteractionListener, MenuFragment.OnFragmentInteractionListener,GetFragment.OnFragmentInteractionListener,CheckFragment.OnFragmentInteractionListener {

    protected int iconNormal [] = {R.drawable.icon_logout,
            R.drawable.icon_menu,
            R.drawable.icon_put,
            R.drawable.icon_change,
            R.drawable.icon_lend,
            R.drawable.icon_get,
            R.drawable.icon_check};
    protected int iconCheck [] = {R.drawable.icon_logout_check,
            R.drawable.icon_menu_check,
            R.drawable.icon_put_check,
            R.drawable.icon_change_check,
            R.drawable.icon_lend_check,
            R.drawable.icon_get_check,
            R.drawable.icon_check_check};
    protected RelativeLayout bar;
    protected ImageView itemImage;

    //fragment
    private FragmentManager fm;
    private FragmentTransaction transaction;
    //菜单
    private MenuFragment menuFragment;
    //赎证
    private GetFragment getFragment;
    //借证
    private LendFragment lendFragment;
    //收证
    private PutFragment putFragment;
    //换证
    private ChangeFragment changeFragment;
    //核查
    private CheckFragment checkFragment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_console);
        settingBottomBar();
        setDefaultFragment();
        String authority = getSharedPreferences("userId", Activity.MODE_PRIVATE).getString("authorityId", "").toString();
        if (!authority.equals("0")){
            for (int i = 0; i < bar.getChildCount(); i++) {
                itemImage = (ImageView) bar.getChildAt(i);
                if (i == 0 ||i == 1 || i == 6){
                    itemImage.setVisibility(View.VISIBLE);
                }else {
                    itemImage.setVisibility(View.GONE);
                }
            }
        } else {
            bar.getChildAt(6).setVisibility(View.GONE);
        }
    }
    private void settingBottomBar() {
        bar = (RelativeLayout) findViewById(R.id.right_bar);
        for (int i = 0; i < bar.getChildCount(); i++) {
            itemImage = (ImageView) bar.getChildAt(i);
            itemImage.setOnClickListener(barOnClickListener);
        }
    }
    protected View.OnClickListener barOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
        if (Act.isPush){
            ImageView iv = (ImageView) v;
            fm = getSupportFragmentManager();
            transaction = fm.beginTransaction();
            bar = (RelativeLayout) findViewById(R.id.right_bar);
            for (int i = 0; i < bar.getChildCount(); i++) {
                itemImage = (ImageView) bar.getChildAt(i);
                itemImage.setImageResource(iconNormal[i]);
            }
            switch (v.getId()) {
                case R.id.menu:
                    iv.setImageResource(iconCheck[Act.ARR_MENU]);
                    if (menuFragment == null) {
                        menuFragment = new MenuFragment();
                    }
                    transaction.replace(R.id.console_fragment, menuFragment);
                    break;
                case R.id.get:
                    iv.setImageResource(iconCheck[Act.ARR_GET]);
                    if (getFragment == null) {
                        getFragment = new GetFragment();
                    }
                    transaction.replace(R.id.console_fragment, getFragment);
                    break;
                case R.id.put:
                    iv.setImageResource(iconCheck[Act.ARR_PUT]);
                    if (putFragment == null) {
                        putFragment = new PutFragment();
                    }
                    transaction.replace(R.id.console_fragment, putFragment);
                    break;
                case R.id.change:
                    iv.setImageResource(iconCheck[Act.ARR_CHANGE]);
                    if (changeFragment == null) {
                        changeFragment = new ChangeFragment();
                    }
                    transaction.replace(R.id.console_fragment, changeFragment);
                    break;
                case R.id.lend:
                    iv.setImageResource(iconCheck[Act.ARR_LEND]);
                    if (lendFragment == null) {
                        lendFragment = new LendFragment();
                    }
                    transaction.replace(R.id.console_fragment, lendFragment);
                    break;
                case R.id.check:
                    iv.setImageResource(iconCheck[Act.ARR_CHECK]);
                    if (checkFragment == null) {
                        checkFragment = new CheckFragment();
                    }
                    transaction.replace(R.id.console_fragment, checkFragment);
                    break;
                case R.id.logout:
                    getSharedPreferences("userId",0).edit().clear().commit();
                    startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                    finish();
                    break;
            }
            //transaction.disallowAddToBackStack();
            //事务提交
            transaction.commit();
        } else {
            Toast.makeText(getApplicationContext(), "箱门未关闭 无法操作", Toast.LENGTH_LONG).show();
        }
        }
    };

    private void setDefaultFragment() {
        if (menuFragment == null){
            fm = getSupportFragmentManager();
            transaction = fm.beginTransaction();
            menuFragment = new MenuFragment();
            transaction.replace(R.id.console_fragment, menuFragment);
            transaction.commit();
        }
    }

    @Override
    public void onFragmentInteraction(InterAction ia) {
        if (ia.getTag() == Act.TAG_MENU_TO_CONSOLE) {
            for (int i = 0; i < bar.getChildCount(); i++){
                itemImage = (ImageView) bar.getChildAt(i);
                itemImage.setImageResource(iconNormal[i]);
            }
            int uri = 0;
            switch (ia.getUri()){
                case Act.URI_CHECK:
                    uri = Act.ARR_CHECK;
                    break;
                case Act.URI_GET:
                    uri = Act.ARR_GET;
                    break;
                case Act.URI_PUT:
                    uri = Act.ARR_PUT;
                    break;
                case Act.URI_LEND:
                    uri = Act.ARR_LEND;
                    break;
                case Act.URI_CHANGE:
                    uri = Act.ARR_CHANGE;
                    break;
                case Act.URI_MENU:
                    uri = Act.ARR_MENU;
                    break;
            }
            itemImage = (ImageView) bar.getChildAt(uri);
            itemImage.setImageResource(iconCheck[uri]);
        }
    }
}