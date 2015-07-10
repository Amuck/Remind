package com.remind.fragment;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.help.remind.R;
import com.remind.activity.ContactsActivity;
import com.remind.activity.EditPeopelActivity;
import com.remind.adapter.PeopelAdapter;
import com.remind.dao.PeopelDao;
import com.remind.dao.impl.PeopelDaoImpl;
import com.remind.entity.PeopelEntity;
import com.remind.util.DataBaseParser;

/**
 * @author ChenLong
 *
 *	联系人界面
 */
public class PeopelFragment extends Fragment{
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

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
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
		searchBtn = (ImageButton) view.findViewById(R.id.title_search);
		addPeopelBtn = (ImageButton) view.findViewById(R.id.title_add);
		peopelEmpty = (TextView) view.findViewById(R.id.peopel_empty_text);
		peopelList = (ListView) view.findViewById(R.id.peopel_list);

		peopelDao = new PeopelDaoImpl(getActivity());

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
//				doStartApplicationWithPackageName("com.bjjw.sysbuild");

			}
		});
	}

	/**
	 * 初始化联系人列表
	 */
	private void initPeopelList() {
		getData();
		peopelList.setTextFilterEnabled(true);//设置lv可以被过虑
		peopelAdapter = new PeopelAdapter(getActivity(), datas);
		peopelList.setAdapter(peopelAdapter);
		peopelList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// 打开联系人详细
				Intent intent = new Intent(getActivity(), EditPeopelActivity.class);
				intent.putExtra("peopel", datas.get(position));
				startActivityForResult(intent, EDIT_PEOPEL);
			}
		});

		changeEmptyTextVisibale();
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
		Cursor cursor = peopelDao.queryPeopel();
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
	private void doStartApplicationWithPackageName(String packagename) {

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
		//resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		resolveIntent.setPackage(packageinfo.packageName);

		// 通过getPackageManager()的queryIntentActivities方法遍历
		List<ResolveInfo> resolveinfoList = getActivity().getPackageManager()
				.queryIntentActivities(resolveIntent, 0);

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

}
