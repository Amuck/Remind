package com.remind.activity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.ab.view.wheel.AbWheelUtil;
import com.ab.view.wheel.AbWheelView;
import com.help.remind.R;
import com.remind.adapter.SelectPeopelAdapter;
import com.remind.adapter.SelectPeopelAdapter.PeopelSelected;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.PeopelEntity;
import com.remind.entity.RemindEntity;
import com.remind.global.AppConstant;
import com.remind.util.AppUtil;
import com.remind.util.DataBaseParser;
import com.remind.view.SwitchButton;

/**
 * @author ChenLong
 * 
 *         查看提醒详细信息界面
 */
public class RemindDetailActivity extends BaseActivity implements OnClickListener {

    /**
     * 选择联系人按钮
     */
    private TextView selectPeopelBtn;
    /**
     * 选择日期按钮
     */
    // private TextView selectDateBtn;
    /**
     * 选择时间按钮
     */
    // private TextView selectTimeBtn;
    /**
     * 确定按钮
     */
    // private Button okBtn;
    /**
     * 取消按钮
     */
    // private Button cancelBtn;
    /**
     * 选择联系人姓名
     */
    private TextView selectPeopelTxt;
    /**
     * 选择日期
     */
    private TextView selectDateTxt;
    /**
     * 选择时间
     */
    private TextView selectTimeTxt;
    /**
     * 提醒内容
     */
    // private EditText contentEidt;
    /**
     * 提醒标题
     */
    // private EditText titleEidt;

    /**
     * 选择联系人对话框
     */
    private AlertDialog alertDialog;
    private PeopelDao peopelDao;
    // private RemindDao remindDao;
    /**
     * 当前选择联系人信息
     */
    private PeopelEntity currentPeopel;

    /**
     * 日期选择
     */
    // private View mDateView = null;
    /**
     * 时间选择
     */
    // private View mTimeView = null;
    private LinearLayout prevPanel;
    /**
     * 选择是否预览按钮
     */
    private SwitchButton switchButton;
    /**
     * 是否是给自己的
     */
    // private boolean isForSelf = false;

    /**
     * 选中 的 提醒
     */
    private RemindEntity entity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setAbContentView(R.layout.activity_add_remind);
        peopelDao = new PeopelDaoImpl(this);
        // remindDao = new RemindDaoImpl(this);

        setUpView();
    }

    private void setUpView() {
        // okBtn = (Button) findViewById(R.id.title_ok);
        // cancelBtn = (Button) findViewById(R.id.title_cancel);
        selectPeopelBtn = (TextView) findViewById(R.id.select_peopel_btn);
        // selectDateBtn = (TextView) findViewById(R.id.select_date_btn);
        // selectTimeBtn = (TextView) findViewById(R.id.select_time_btn);

        selectPeopelTxt = (TextView) findViewById(R.id.select_peopel_text);
        selectDateTxt = (TextView) findViewById(R.id.select_date_text);
        selectTimeTxt = (TextView) findViewById(R.id.select_time_text);

        // contentEidt = (EditText) findViewById(R.id.set_content_edit);
        // titleEidt = (EditText) findViewById(R.id.set_title_edit);

        // mDateView = mInflater.inflate(R.layout.choose_three, null);
        // mTimeView = mInflater.inflate(R.layout.choose_two, null);
        prevPanel = (LinearLayout) findViewById(R.id.select_preview_panel);
        prevPanel.setVisibility(View.GONE);
        switchButton = (SwitchButton) findViewById(R.id.select_preview_btn);
        switchButton.setImageResource(R.drawable.preview_bord, R.drawable.preview_btn);
        // switchButton.setOnSwitchStateListener(new OnSwitchListener() {
        //
        // @Override
        // public void onSwitched(boolean state) {
        // // isPreview = state ? 1 : 0;
        // }
        // });
        // selectDateTxt.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // showDialog(AbConstant.DIALOGBOTTOM, mDateView, 40);
        // }
        //
        // });

        // selectTimeTxt.setOnClickListener(new OnClickListener() {
        //
        // @Override
        // public void onClick(View v) {
        // showDialog(AbConstant.DIALOGBOTTOM, mTimeView, 40);
        // }
        //
        // });

        // initWheelDate(mDateView, selectDateTxt);
        // initWheelTime(mTimeView, selectTimeTxt);

        // okBtn.setOnClickListener(this);
        // cancelBtn.setOnClickListener(this);
        // selectPeopelBtn.setOnClickListener(this);
        // selectDateBtn.setOnClickListener(this);
        // selectTimeBtn.setOnClickListener(this);

        selectPeopelBtn.setVisibility(View.INVISIBLE);
        // cancelBtn.setVisibility(View.INVISIBLE);
        // contentEidt.setEnabled(false);
        // contentEidt.setFocusable(false);
        // titleEidt.setEnabled(false);
        // titleEidt.setFocusable(false);

        Intent intent = getIntent();
        Object obj = intent.getSerializableExtra("remind");

        if (null != obj && obj instanceof RemindEntity) {
            entity = (RemindEntity) obj;
            selectPeopelTxt.setText(AppUtil.getName(entity));
            String date = entity.getRemindTime();
            if (date.contains(" ")) {
                selectDateTxt.setText(date.split(" ")[0]);
                selectTimeTxt.setText(date.split(" ")[1]);
            }

            // contentEidt.setText(entity.getContent());
            // titleEidt.setText(entity.getTitle());
        } else {
            AppUtil.showToast(this, "提醒信息有误，请重新选择");
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.select_peopel_btn:
            // 选择联系人
            // openPeopelDig();
            break;
        case R.id.select_date_btn:
            // 选择日期
            break;
        case R.id.select_time_btn:
            // 选择时间
            break;
        case R.id.title_ok:
            // 确认
            finish();
            break;
        case R.id.title_cancel:
            // 取消
            setResult(RESULT_CANCELED);
            finish();
            break;
        default:
            break;
        }
    }

    /**
     * 打开选择联系人对话框
     */
    public void openPeopelDig() {
        // 设置对话框中的view
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView textView = new TextView(this);
        textView.setHeight(AppUtil.dip2px(this, 80));
        textView.setText("自己");
        textView.setTextSize(AppUtil.dip2px(this, 24));
        textView.setGravity(Gravity.CENTER_VERTICAL);
        textView.setTextColor(Color.BLACK);
        textView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // isForSelf = true;
                currentPeopel = new PeopelEntity("自己", "自己", getPhoneNumber(), "", "", "", PeopelEntity.NORMAL,
                        PeopelEntity.FRIEND, AppConstant.FROM_ID, AppConstant.USER_NUM);
                selectPeopelTxt.setText(getName(currentPeopel));
                alertDialog.dismiss();
            }
        });

        ListView listView = new ListView(this);
        List<PeopelEntity> datas = getPeopel();

        layout.addView(textView);
        layout.addView(listView);

        if (null == datas || datas.size() == 0) {
            AppUtil.showToast(this, "您还没有联系人，快去添加一个吧！");
            return;
        }

        SelectPeopelAdapter adapter = new SelectPeopelAdapter(this, datas);
        listView.setAdapter(adapter);

        alertDialog = new AlertDialog.Builder(this).setTitle("请选择联系人").setView(layout)
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        alertDialog.dismiss();
                    }
                }).create();

        adapter.setPeopelSelected(new PeopelSelected() {

            @Override
            public void onPeopelSelected(int position, PeopelEntity entity) {
                // 设置选中联系人操作
                // isForSelf = false;

                currentPeopel = entity;
                selectPeopelTxt.setText(getName(entity));
                alertDialog.dismiss();
            }
        });

        alertDialog.show();
    }

    /**
     * 将数据库中联系人取出
     * 
     * @return
     */
    private List<PeopelEntity> getPeopel() {
        List<PeopelEntity> results = new ArrayList<PeopelEntity>();
        Cursor cursor = peopelDao.queryPeopelExceptOwner();
        results = DataBaseParser.getPeoPelDetail(cursor);
        cursor.close();
        return results;
    }

    /**
     * 获取联系人显示名称
     * 
     * @param entity
     * @return
     */
    private String getName(PeopelEntity entity) {
        String name = entity.getNickName();
        if (null == name || name.trim().length() <= 0) {
            // 无备注名称则显示联系人名称
            name = entity.getName();
        }
        if (null == name || name.trim().length() <= 0) {
            name = "佚名";
        }
        return name;
    }

    /**
     * 获取本机号码
     * 
     * @return
     */
    private String getPhoneNumber() {
        TelephonyManager mTelephonyMgr;
        mTelephonyMgr = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        String temp = mTelephonyMgr.getLine1Number();

        if (null != temp && temp.length() > 11) {
            // 获取11位手机号
            temp = temp.substring(temp.length() - 11, temp.length() - 1);
        }

        return temp;
    }

    /**
     * 初始化时间选择
     * 
     * @param mTimeView
     * @param mText
     */
    public void initWheelTime(View mTimeView, TextView mText) {
        final AbWheelView mWheelViewHH = (AbWheelView) mTimeView.findViewById(R.id.wheelView1);
        final AbWheelView mWheelViewMM = (AbWheelView) mTimeView.findViewById(R.id.wheelView2);
        Button okBtn = (Button) mTimeView.findViewById(R.id.okBtn);
        Button cancelBtn = (Button) mTimeView.findViewById(R.id.cancelBtn);
        mWheelViewMM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewHH.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        AbWheelUtil.initWheelTimePicker2(this, mText, mWheelViewHH, mWheelViewMM, okBtn, cancelBtn, 10, 0, true);
    }

    /**
     * 初始化日期选择
     * 
     * @param mDateView
     * @param mText
     */
    public void initWheelDate(View mDateView, TextView mText) {
        // 年月日时间选择器
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH) + 1;
        int day = calendar.get(Calendar.DATE);
        final AbWheelView mWheelViewY = (AbWheelView) mDateView.findViewById(R.id.wheelView1);
        final AbWheelView mWheelViewM = (AbWheelView) mDateView.findViewById(R.id.wheelView2);
        final AbWheelView mWheelViewD = (AbWheelView) mDateView.findViewById(R.id.wheelView3);
        Button okBtn = (Button) mDateView.findViewById(R.id.okBtn);
        Button cancelBtn = (Button) mDateView.findViewById(R.id.cancelBtn);
        mWheelViewY.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewM.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        mWheelViewD.setCenterSelectDrawable(this.getResources().getDrawable(R.drawable.wheel_select));
        AbWheelUtil.initWheelDatePicker(this, mText, mWheelViewY, mWheelViewM, mWheelViewD, okBtn, cancelBtn, year, month,
                day, year, 120, false);
    }
}
