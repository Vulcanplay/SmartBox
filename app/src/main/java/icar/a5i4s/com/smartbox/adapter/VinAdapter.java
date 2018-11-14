package icar.a5i4s.com.smartbox.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import icar.a5i4s.com.smartbox.R;
import icar.a5i4s.com.smartbox.helper.Act;
import icar.a5i4s.com.smartbox.module.Vin;

/**
 * Created by light on 2016/11/9.
 */

public class VinAdapter extends BaseAdapter {
    protected LayoutInflater inflater;
    protected int action = 0;
    protected Context context;
    List<Vin> lv = new ArrayList<Vin>();

    public VinAdapter(List<Vin> lv, Context context, int action){
        this.lv = lv;
        this.action = action;
        this.context = context;
        inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return lv.size();
    }

    @Override
    public Object getItem(int i) {
        return lv.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // 获取每一项的布局
        if (view == null) {
            view = (ViewGroup) inflater
                    .inflate(R.layout.vin_item, null);
        }
        Vin v = lv.get(i);
        TextView vinTV = (TextView) view.findViewById(R.id.vin_value);
        vinTV.setText(v.getVin());
        vinTV.setTag(R.id.tag_id, v.getId());
        vinTV.setTag(R.id.tag_box_cell_code, v.getBoxCellCode() == null ? "" : v.getBoxCellCode());
        return view;
    }
}
