package icar.a5i4s.com.smartbox.helper;

import android.content.Context;
import android.os.CountDownTimer;
import android.widget.Button;

import icar.a5i4s.com.smartbox.R;

/**
 * Created by light on 2016/11/11.
 */

public class CountDownTimerTool extends CountDownTimer {
    private Button button;
    private Context context;

    public CountDownTimerTool(Context context, Button button, long millisInFuture, long countDownInterval) {
        super(millisInFuture, countDownInterval);
        this.button = button;
        this.context = context;
    }

    @Override
    public void onTick(long l) {
        button.setEnabled(false);
        button.setText(String.format(context.getString(R.string.reGet_verifyCode_time), String.valueOf(l / 1000)));
    }

    @Override
    public void onFinish() {
        button.setEnabled(true);
        button.setText(context.getString(R.string.reGet_verifyCode));
    }
}
