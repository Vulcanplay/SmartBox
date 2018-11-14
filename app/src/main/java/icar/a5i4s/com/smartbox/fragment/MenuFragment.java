package icar.a5i4s.com.smartbox.fragment;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;

import icar.a5i4s.com.smartbox.R;
import icar.a5i4s.com.smartbox.helper.Act;
import icar.a5i4s.com.smartbox.module.InterAction;
import icar.a5i4s.com.smartbox.module.ResultData;


public class MenuFragment extends Fragment implements View.OnClickListener {

    private OnFragmentInteractionListener mListener;

    protected RelativeLayout menuGridGet, menuGridChange, menuGridPut, menuLend, menuGirdCheck;

    public MenuFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
        initView(view);
        // Inflate the layout for this fragment
        return view;
    }

    private void initView(View view) {
        menuGirdCheck = (RelativeLayout) view.findViewById(R.id.menu_grid_check);
        menuLend = (RelativeLayout) view.findViewById(R.id.menu_grid_lend);
        menuGridPut = (RelativeLayout) view.findViewById(R.id.menu_grid_put);
        menuGridChange = (RelativeLayout) view.findViewById(R.id.menu_grid_change);
        menuGridGet = (RelativeLayout) view.findViewById(R.id.menu_grid_get);

        menuGirdCheck.setOnClickListener(this);
        menuLend.setOnClickListener(this);
        menuGridPut.setOnClickListener(this);
        menuGridChange.setOnClickListener(this);
        menuGridGet.setOnClickListener(this);
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

    @Override
    public void onClick(View view) {
        String authority = getActivity().getSharedPreferences("userId", Activity.MODE_PRIVATE).getString("authorityId", "").toString();
        String uri = null;
        Fragment fragment = null;
        if (view.getId() == R.id.menu_grid_check){
            if (!authority.equals("0")){
                fragment = new CheckFragment();
                uri = Act.URI_CHECK;
            } else {
                Toast.makeText(getActivity(), "无此操作权限",Toast.LENGTH_LONG).show();
            }
        } else {
            if (!authority.equals("0")){
                Toast.makeText(getActivity(), "无此操作权限",Toast.LENGTH_LONG).show();
            } else {
                switch (view.getId()){
                    case R.id.menu_grid_change:
                        fragment = new ChangeFragment();
                        uri = Act.URI_CHANGE;
                        break;
                    case R.id.menu_grid_get:
                        fragment = new GetFragment();
                        uri = Act.URI_GET;
                        break;
                    case R.id.menu_grid_lend:
                        fragment = new LendFragment();
                        uri = Act.URI_LEND;
                        break;
                    case R.id.menu_grid_put:
                        fragment = new PutFragment();
                        uri = Act.URI_PUT;
                        break;
                }
            }
        }
        if (fragment != null && uri != null){
            getFragmentManager().beginTransaction().addToBackStack(null).replace(R.id.console_fragment, fragment).commit();
            mListener.onFragmentInteraction(new InterAction(uri, Act.TAG_MENU_TO_CONSOLE));
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(InterAction interAction);
    }
}
