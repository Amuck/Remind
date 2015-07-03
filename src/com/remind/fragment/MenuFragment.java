package com.remind.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.help.remind.R;
import com.remind.activity.MainActivity;

/**
 * @author ChenLong
 *
 * 左侧导航菜单
 */
public class MenuFragment extends Fragment implements OnClickListener {

	private View inflate;

	private MainActivity mainActivity;

	/**
	 * 提醒
	 */
	private RelativeLayout remindLayout;
	/**
	 * 联系人
	 */
	private RelativeLayout peopleLayout;
	/**
	 * 设置
	 */
	private RelativeLayout settingLayout;
	
	/**
	 * 提醒界面
	 */
	private ContentFragment contentFragment;
	
	/**
	 * 联系人界面
	 */
	private PeopelFragment peopelFragment = new PeopelFragment();
	
	/**
	 * 设置界面
	 */
	private SettingFragment settingFragment = new SettingFragment();

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		inflate = inflater.inflate(R.layout.layout_menu, null);
		return inflate;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		setupViews();
	}

	private void setupViews() {
		mainActivity = (MainActivity) getActivity();
		if (mainActivity.getCurrentFragment() instanceof ContentFragment) {
			contentFragment = (ContentFragment) mainActivity.getCurrentFragment();
		}

		remindLayout = (RelativeLayout) inflate.findViewById(R.id.relative1);
		peopleLayout = (RelativeLayout) inflate.findViewById(R.id.relative2);
		settingLayout = (RelativeLayout) inflate.findViewById(R.id.relative3);
		
		remindLayout.setOnClickListener(this);
		peopleLayout.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Fragment newContent = null;
		switch (v.getId()) {
		case R.id.relative1:
			// 跳转到提醒界面
			newContent = contentFragment;
			break;
		case R.id.relative2:
			// 跳转到联系人界面
			newContent = peopelFragment;
			break;
		case R.id.relative3:
			// 跳转到设置界面
			newContent = settingFragment;
			break;
		default:
			break;
		}
		
		if (newContent != null)
			switchFragment(newContent);
	}

	/**
	 * 切换fragment
	 * 
	 * @param fragment
	 */
	private void switchFragment(Fragment fragment) {
		if (getActivity() == null)
			return;

		MainActivity activity = (MainActivity) getActivity();
		activity.switchContent(fragment);
		activity.setCurrentFragment(fragment);
	}
}
