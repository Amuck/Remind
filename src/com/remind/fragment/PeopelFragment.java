package com.remind.fragment;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.help.remind.R;
import com.remind.activity.AddPeopleActivity;
import com.remind.activity.ContactsActivity;
import com.remind.activity.EditPeopelActivity;
import com.remind.adapter.PeopelAdapter;
import com.remind.dao.MessageIndexDao;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.MessageIndexDaoImpl;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.PeopelEntity;
import com.remind.http.HttpClient;
import com.remind.receiver.MessageReceiver;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 * 
 *         联系人界面
 */
public class PeopelFragment extends Fragment implements OnClickListener {
    /**
     * 刷新界面
     */
    private final static int REFRESH_UI = 3003;
    /**
     * 添加联系人
     */
    public final static int ADD_PEOPEL = 3001;
    /**
     * 编辑联系人
     */
    public final static int EDIT_PEOPEL = 3002;

    private View inflate;
    /**
     * 搜索框
     */
    private ImageButton searchBtn;
    /**
     * 添加联系人按钮
     */
    private ImageButton addPeopelBtn;
    /**
     * 无联系人提示
     */
    private TextView peopelEmpty;
    /**
     * 联系人列表
     */
    private ListView peopelList;

    /**
     * 联系人适配器
     */
    private PeopelAdapter peopelAdapter;

    /**
     * 联系人数据源
     */
    private List<PeopelEntity> datas = new ArrayList<PeopelEntity>();

    /**
     * 联系人数据库
     */
    private PeopelDao peopelDao;

    private MessageIndexDao messageIndexDao;
    /**
     * 删除添加信息的对话框
     */
    private AlertDialog alertDialog = null;
    /**
     * 现在选择的好友信息
     */
    private PeopelEntity currentPeople = null;

    // private LocalBroadcastManager mLocalBroadcastManager;
    private MessageBackReciver mReciver;
    private IntentFilter mIntentFilter;

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
            case 0:
                String s = (String) msg.obj;
                if (null == s || !s.contains("|")) {
                    Toast.makeText(getActivity(), "网络连接失败，请确认后重试.", Toast.LENGTH_SHORT).show();
                }

                String[] ss = s.split("\\|");
                if (!ss[0].equals("200")) {
                    // 失败
                    Toast.makeText(getActivity(), "拒绝好友失败，请重试.", Toast.LENGTH_SHORT).show();
                } else {
                    // 成功
                    peopelDao.realDeleteByNum(currentPeople.getNum());
                    deleteFromMessageIndexDB(currentPeople.getNum());
                    datas.remove(currentPeople);
                    peopelAdapter.notifyDataSetChanged();
                }

                break;

            case REFRESH_UI:
                Bundle bundle = msg.getData();
                String pn = (String) bundle.get("pn");
                String state = (String) bundle.get("state");
                getBackAndRefreshData(pn, state);
                break;
            default:
                break;
            }
        };
    };

    @SuppressLint("InflateParams")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (null != inflate) {
            ViewGroup parent = (ViewGroup) inflate.getParent();
            if (null != parent) {
                parent.removeView(inflate);
            }
        } else {
            inflate = inflater.inflate(R.layout.layout_peopel, null);
            setupView(inflate);
        }

        return inflate;
    }

    private void setupView(View view) {
        // mLocalBroadcastManager =
        // LocalBroadcastManager.getInstance(getActivity());
        mReciver = new MessageBackReciver();
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(MessageReceiver.PEOPLE_STATE_CHANGE_ACTION);
        mIntentFilter.addAction(MessageReceiver.PEOPLE_ADD_ACTION);

        searchBtn = (ImageButton) view.findViewById(R.id.title_search);
        addPeopelBtn = (ImageButton) view.findViewById(R.id.title_add);
        peopelEmpty = (TextView) view.findViewById(R.id.peopel_empty_text);
        peopelList = (ListView) view.findViewById(R.id.peopel_list);

        peopelDao = new PeopelDaoImpl(getActivity());
        messageIndexDao = new MessageIndexDaoImpl(getActivity());

        initAddBtn();
        initPeopelList();
        initSearch();
    }

    /**
     * 初始化搜索框
     */
    private void initSearch() {
        searchBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 打开搜索页面
                // startActivity(new Intent(getActivity(),
                // AddPeopleActivity.class));
                Intent intent = new Intent(getActivity(), AddPeopleActivity.class);
                startActivityForResult(intent, ADD_PEOPEL);
            }
        });
    }

    /**
     * 初始化添加联系人按钮
     */
    private void initAddBtn() {
        addPeopelBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 添加联系人
                Intent intent = new Intent(getActivity(), ContactsActivity.class);
                startActivityForResult(intent, ADD_PEOPEL);
                // doStartApplicationWithPackageName("com.bjjw.sysbuild");

            }
        });
    }

    /**
     * 初始化联系人列表
     */
    private void initPeopelList() {
        getData();
        peopelList.setTextFilterEnabled(true);// 设置lv可以被过虑
        peopelAdapter = new PeopelAdapter(getActivity(), datas);
        peopelList.setAdapter(peopelAdapter);
        peopelList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 打开联系人详细
                Intent intent = new Intent(getActivity(), EditPeopelActivity.class);
                intent.putExtra("peopel", datas.get(position));
                intent.putExtra("user", false);
                startActivityForResult(intent, EDIT_PEOPEL);
            }
        });

        peopelList.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                currentPeople = datas.get(position);
                showDleteDlg();
                return true;
            }
        });

        changeEmptyTextVisibale();
    }

    /**
     * 显示删除/拒绝的对话框
     */
    @SuppressLint("InflateParams")
    private void showDleteDlg() {
        if (null == alertDialog) {
            View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_del_people, null);
            Button delete = (Button) view.findViewById(R.id.delete);
            Button refuse = (Button) view.findViewById(R.id.refuse);
            Button cancel = (Button) view.findViewById(R.id.cancel);
            if (currentPeople.getStatus() == PeopelEntity.ACCEPT) {
                // 等待用户接受， 可以拒绝
                refuse.setVisibility(View.VISIBLE);
            } else {
                refuse.setVisibility(View.GONE);
            }

            delete.setOnClickListener(this);
            refuse.setOnClickListener(this);
            cancel.setOnClickListener(this);

            alertDialog = new AlertDialog.Builder(getActivity()).setView(view).create();
        }
        alertDialog.show();
    }

    /**
     * 改变联系人列表的显示状态
     */
    private void changeEmptyTextVisibale() {
        if (null == datas || datas.size() <= 0) {
            peopelEmpty.setVisibility(View.VISIBLE);
            peopelList.setVisibility(View.GONE);
        } else {
            peopelList.setVisibility(View.VISIBLE);
            peopelEmpty.setVisibility(View.GONE);
        }
    }

    /**
     * 获取数据库中的信息
     */
    private void getData() {
        Cursor cursor = peopelDao.queryPeopelExceptOwner();
        datas.clear();
        datas.addAll(DataBaseParser.getPeoPelDetail(cursor));
        cursor.close();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_OK) {
            return;
        }

        switch (requestCode) {
        case ADD_PEOPEL:
            // 添加联系人成功,刷新页面
            if (datas.size() == 0) {
                initPeopelList();
            } else {
                getData();
                peopelAdapter.notifyDataSetChanged();
            }

            break;

        case EDIT_PEOPEL:
            getData();
            peopelAdapter.notifyDataSetChanged();
            break;

        default:
            break;
        }
    }

    /**
     * 根据包名启动程序
     * 
     * @param packagename
     */
    public void doStartApplicationWithPackageName(String packagename) {

        // 通过包名获取此APP详细信息，包括Activities、services、versioncode、name等等
        PackageInfo packageinfo = null;
        try {
            packageinfo = getActivity().getPackageManager().getPackageInfo(packagename, 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        if (packageinfo == null) {
            return;
        }

        // 创建一个类别为CATEGORY_LAUNCHER的该包名的Intent
        Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
        // resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        resolveIntent.setPackage(packageinfo.packageName);

        // 通过getPackageManager()的queryIntentActivities方法遍历
        List<ResolveInfo> resolveinfoList = getActivity().getPackageManager().queryIntentActivities(resolveIntent, 0);

        ResolveInfo resolveinfo = resolveinfoList.iterator().next();
        if (resolveinfo != null) {
            // packagename = 参数packname
            String packageName = resolveinfo.activityInfo.packageName;
            // 这个就是我们要找的该APP的LAUNCHER的Activity[组织形式：packagename.mainActivityname]
            String className = resolveinfo.activityInfo.name;
            // LAUNCHER Intent
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);

            // 设置ComponentName参数1:packagename参数2:MainActivity路径
            ComponentName cn = new ComponentName(packageName, className);

            intent.setComponent(cn);
            startActivity(intent);
        }
    }

    private void refuseFriend(final String params, final PeopelEntity entity) {
        new Thread(new Runnable() {

            @Override
            public void run() {
                String s = HttpClient.post(HttpClient.url + HttpClient.agree_friend, params);
                Message msg = handler.obtainMessage();
                msg.what = 0;
                msg.obj = s;
                Bundle bundle = new Bundle();
                bundle.putSerializable("PeopelEntity", entity);
                msg.setData(bundle);
                handler.sendMessage(msg);
            }
        }).start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.delete:
            // 删除
            peopelDao.deleteFromDbByNum(currentPeople.getNum());
            deleteFromMessageIndexDB(currentPeople.getNum());
            datas.remove(currentPeople);
            peopelAdapter.notifyDataSetChanged();
            alertDialog.dismiss();
            break;
        case R.id.refuse:
            // 拒绝
            String param = HttpClient.getJsonForPost(HttpClient.agreeFriend(currentPeople.getNum(), 0,
                    currentPeople.getFriendId()));
            refuseFriend(param, currentPeople);
            alertDialog.dismiss();
            break;
        case R.id.cancel:
            // 取消
            alertDialog.dismiss();
            break;

        default:
            break;
        }
    }
    
    /**
     * 从索引表中删除
     * @param num
     */
    private void deleteFromMessageIndexDB(String num) {
        int count = messageIndexDao.getCountByNum(num);
        if (count > 0) {
            messageIndexDao.deleteByNum(num);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getActivity().registerReceiver(mReciver, mIntentFilter);
    }

    @Override
    public void onPause() {
        getActivity().unregisterReceiver(mReciver);
        super.onPause();
    }

    /**
     * @author ChenLong
     * 
     *         推送消息发送过来
     */
    class MessageBackReciver extends BroadcastReceiver {

        public MessageBackReciver() {
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(MessageReceiver.PEOPLE_STATE_CHANGE_ACTION)) {
                // 收到反馈后修改界面显示
                String pn = intent.getStringExtra("pn");
                String state = intent.getStringExtra("state");
                Message message = handler.obtainMessage();
                message.what = REFRESH_UI;
                Bundle data = new Bundle();
                data.putString("pn", pn);
                data.putString("state", state);
                message.setData(data);
                handler.sendMessage(message);
            } else if (action.equals(MessageReceiver.PEOPLE_ADD_ACTION)) {
                // 收到添加好友请求修改页面
                PeopelEntity entity = (PeopelEntity) intent.getSerializableExtra("PeopelEntity");
                datas.add(entity);
                peopelAdapter.notifyDataSetChanged();
            }
        };
    }

    /**
     * 获取了反馈之后，修改数据，刷新画面
     */
    private void getBackAndRefreshData(String pn, String state) {
        boolean isFind = false;
        int findIndex = 0;
        // 寻找改变状态的好友
        for (int i = datas.size() - 1; i >= 0; i--) {
            PeopelEntity temp = datas.get(i);
            if (temp.getNum().equals(pn)) {
                if ("1".equals(state)) {
                    // 同意
                    temp.setStatus(PeopelEntity.FRIEND);
                } else if ("0".equals(state)) {
                    // 不同意
                    temp.setStatus(PeopelEntity.REFUSE);
                }
                isFind = true;
                findIndex = i;
                break;
            }
        }
        // 如果所修改界面在可见范围内，则刷新画面
        if (isFind) {
            int firstPosition = peopelList.getFirstVisiblePosition();
            int lastPosition = peopelList.getLastVisiblePosition();
            if (findIndex >= firstPosition && findIndex <= lastPosition) {
                peopelAdapter.notifyDataSetChanged();
            }
        }
    }
}
