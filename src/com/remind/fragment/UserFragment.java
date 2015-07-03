package com.remind.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.help.remind.R;

/**
 * @author ChenLong
 *
 * 设置界面
 */
public class UserFragment extends Fragment{

	private View inflate;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (null != inflate) {
			ViewGroup parent = (ViewGroup) inflate.getParent();
			if (null != parent) {
				parent.removeView(inflate);
			}
		} else {
			inflate = inflater.inflate(R.layout.layout_setting, null);
		}
		return inflate;
	}
}
