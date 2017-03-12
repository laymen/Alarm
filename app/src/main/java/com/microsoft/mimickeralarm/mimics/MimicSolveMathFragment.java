package com.microsoft.mimickeralarm.mimics;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.microsoft.mimickeralarm.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * 解数学题的办法关闭闹钟
 * Created by mouse on 2017/3/7 0007.
 */
public class MimicSolveMathFragment extends Fragment implements IMimicImplementation2 {
    private static final int TIMEOUT_MILLISECONDS = 30000;//设置的过期时间
    MimicFactory.MimicResultListener mCallback;
    private MimicStateManagerNomal mStateManagerNormal;//进度情况
    //数字键盘布局
    private GridView gv_nums;
    //数字键盘数据存放
    private List<Map<String, Integer>> lists;
    private int nums[] = {R.drawable.num_1, R.drawable.num_2, R.drawable.num_3,
            R.drawable.num_4, R.drawable.num_5, R.drawable.num_6, R.drawable.num_7,
            R.drawable.num_8, R.drawable.num_9, R.drawable.num_del, R.drawable.num_0, R.drawable.num_ok};
    //图片对应的值
    private String[] tags = {"1", "2", "3", "4", "5", "6", "7", "8", "9", "del", "0", "ok"};
    private int times;//做题的总数
    String numberStr = "";
    private EditText et_result;
    private int result;//结果
    private TextView tip_text;//提示
    private Random random;//产生随机数

    private TextView tv_nums;//随机产生的计算式


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_solve_mathematics, container, false);
        et_result = (EditText) view.findViewById(R.id.et_result);
        tip_text = (TextView) view.findViewById(R.id.tip_text);
        times = 3;
        random = new Random();

        mStateManagerNormal = new MimicStateManagerNomal();//这个是对IMimicMediator接口进行具体的实现。
        mStateManagerNormal.registerCountDownTimerNormal((CountDownTimerView) view.findViewById(R.id.countdown_timer), TIMEOUT_MILLISECONDS);
        mStateManagerNormal.registerStateBannerNormal((MimicStateBanner) view.findViewById(R.id.mimic_state));
        mStateManagerNormal.registerMimicNormal(this);

        tip_text.setText("完成下面的数学题取消闹钟，还剩" + times + "道题！");
        tv_nums = (TextView) view.findViewById(R.id.tv_nums);
        showNextNumber();//开始产生随机数


        //初始化adapter的数据list
        initData();
        gv_nums = (GridView) view.findViewById(R.id.gv_nums);
        //设置adapter
        gv_nums.setAdapter(new SimpleAdapter(getActivity(), lists,
                R.layout.grid_item, new String[]{"num"}, new int[]{R.id.iv_item}));

        final GameResultNormal gameResultNormal = new GameResultNormal();
        //设置按钮的点击事件
        gv_nums.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (tags[position]) {
                    case "0":
                        numberStr += tags[position];//记录用户输入的数据
                        Log.i("MimicSolveMathFragment-",numberStr+"");
                        break;
                    case "1":
                    case "2":
                    case "3":
                    case "4":
                    case "5":
                    case "6":
                    case "7":
                    case "8":
                    case "9":
                        if ("0".endsWith(numberStr)) {
                            numberStr = "";
                        }
                        numberStr += tags[position];
                        break;
                    case "del":
                        if (!TextUtils.isEmpty(numberStr) && numberStr.length() > 0) {
                            numberStr = numberStr.substring(0, numberStr.length() - 1);//一位一位地删除数据
                        }
                        break;
                    case "ok":
                        if (!TextUtils.isEmpty(et_result.getText()) && Integer.parseInt(et_result.getText().toString()) == result) {
                            numberStr = "";
                            tip_text.setText("恭喜你做对了");
                            times--;
                        } else {
                            numberStr = "";
                            tip_text.setText("很抱歉你做错了");
                        }
                        //释放闹钟
                        if (mStateManagerNormal.isMimicRunning()) {//时间还有
                            if (times == 0) {//3道题都正确
                                gameResultNormal.success = true;
                                gameResultNormal.message = "闯关成功";
                                gameSuccess(gameResultNormal);//闯关成功
                            }
                        } else {//时间没有
                            if (times != 0) {
                                gameFailure(null, false);//时间完了
                            }
                        }
                        showNextNumber();
                        tip_text.setText("完成下面的数学题取消闹钟，还剩" + times + "道题！");
                        break;

                }
                et_result.setText(numberStr);
                et_result.setSelection(numberStr.length());

            }
        });
        return view;

    }

    /**
     * 随机产生加减乘除数
     */
    private void showNextNumber() {
        int a = random.nextInt(100);
        int b = random.nextInt(100);
        int c = random.nextInt(3);
        switch (c) {
            case 0:
                //加法
                tv_nums.setText(a + " + " + b + "=");
                result = a + b;
                break;
            case 1:
                //减法
                if (a > b) {
                    tv_nums.setText(a + " - " + b + "=");
                    result = a - b;
                } else {
                    tv_nums.setText(b + " - " + a + "=");
                    result = b - a;
                }
                break;
            case 2:
                //乘法
                b = random.nextInt(11);//不能设置得过大了
                tv_nums.setText(a + " * " + b + " = ");
                result = a * b;
                break;
        }
    }

    private void initData() {
        lists = new ArrayList<>();
        for (int i = 0; i < nums.length; i++) {
            Map<String, Integer> map = new HashMap<>();
            map.put("num", nums[i]);
            lists.add(map);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mCallback = (MimicFactory.MimicResultListener) context;
    }

    @Override
    public void onStart() {
        super.onStart();
        mStateManagerNormal.startNormal();//启动进度条和时间
    }


    @Override
    public void onStop() {
        super.onStop();
        mStateManagerNormal.stopNormal();
    }


    @Override
    public void initializeNormal() {

    }

    @Override
    public void onCountDownTimerExpiredNormal() {
        gameFailure(null, false);

    }

    @Override
    public void onSucceededNormal() {
        if (mCallback != null) {
            mCallback.onMimicSuccess(null);//来自MimicFactory中的接口声明,在此处进行回调，在AlarmRingActivity中进行具体实现
        }
    }

    @Override
    public void onFailedNormal() {
        if (mCallback != null) {
            mCallback.onMimicFailure();//来自MimicFactory中的接口声明,在此处进行回调，在AlarmRingActivity中进行具体实现
        }
    }

    protected void gameSuccess(GameResultNormal gameResult) {
        String successMessage = getString(R.string.mimic_success_message);
        if (gameResult.message != null) {
            successMessage = gameResult.message;
        }
        mStateManagerNormal.onMimicSuccessNormal(successMessage);
    }

    protected void gameFailure(GameResultNormal gameResultNormal, boolean allowRetry) {
        if (allowRetry) {//不是因为时间完了
            //进入到铃声中
            String failureMessage = getString(R.string.mimic_failure_message);
            if (gameResultNormal != null && gameResultNormal.message != null) {
                failureMessage = gameResultNormal.message;
            }
            mStateManagerNormal.onMimicFailureWithRetryNormal(failureMessage);//不是因为时间用完了，而是你的任务完成失败，故需要再来一场，
        } else {//时间完了
            String failureMessage = getString(R.string.mimic_time_up_message);
            mStateManagerNormal.onMimicFailureNormal(failureMessage);
        }
    }

    protected class GameResultNormal {
        boolean success = false;
        String message = null;
    }
}
