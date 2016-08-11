package com.remind.activity;

import java.util.HashMap;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;

import com.help.remind.R;
import com.remind.view.GridItem;

public class ChooseRoleActivity extends BaseActivity implements MultiChoiceModeListener, OnClickListener {
    private GridView mGridView;
    private GridAdapter mGridAdapter;

    @SuppressLint("UseSparseArrays")
    private Map<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();

    private int[] mImgIds = new int[] { R.drawable.role_1, R.drawable.role_2, R.drawable.role_3, R.drawable.role_4,
            R.drawable.role_5, R.drawable.role_6, R.drawable.role_7, R.drawable.role_8, R.drawable.role_9,
            R.drawable.role_10, R.drawable.role_11, R.drawable.role_12, R.drawable.role_13, R.drawable.role_14,
            R.drawable.role_15, R.drawable.role_16, R.drawable.role_17, R.drawable.role_18, R.drawable.role_19 };

    private String[] paths = new String[] { "role_1", "role_2", "role_3", "role_4", "role_5", "role_6", "role_7", "role_8",
            "role_9", "role_10", "role_11", "role_12", "role_13", "role_14", "role_15", "role_16", "role_17", "role_18",
            "role_19" };

    private String path = "role_1";
    /**
     * 确定
     */
    private Button okBtn;
    /**
     * 取消
     */
    private Button cancelBtn;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_role);

        okBtn = (Button) findViewById(R.id.title_ok);
        cancelBtn = (Button) findViewById(R.id.title_cancel);
        mGridView = (GridView) findViewById(R.id.gridview);
        mGridView.setChoiceMode(GridView.CHOICE_MODE_SINGLE);
        mGridAdapter = new GridAdapter(this);
        mGridView.setAdapter(mGridAdapter);
        mGridView.setMultiChoiceModeListener(this);

        okBtn.setOnClickListener(this);
        cancelBtn.setOnClickListener(this);
    }

    /** Override MultiChoiceModeListener start **/
    @Override
    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
        return true;
    }

    @Override
    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
        return true;
    }

    @Override
    public void onDestroyActionMode(ActionMode mode) {
    }

    @Override
    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
        mode.invalidate();
    }

    private class GridAdapter extends BaseAdapter {

        private Context mContext;

        public GridAdapter(Context ctx) {
            mContext = ctx;
        }

        @Override
        public int getCount() {
            return mImgIds.length;
        }

        @Override
        public Integer getItem(int position) {
            return Integer.valueOf(mImgIds[position]);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            GridItem item;
            if (convertView == null) {
                item = new GridItem(mContext);
                item.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
            } else {
                item = (GridItem) convertView;
            }
            item.setImgResId(getItem(position));
            item.setChecked(mSelectMap.get(position) == null ? false : mSelectMap.get(position));
            return item;
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.title_ok:
            // 确定
            SparseBooleanArray selectedposition_blue;
            selectedposition_blue = mGridView.getCheckedItemPositions();

            for (int i = 0; i < mImgIds.length; i++) {
                if (selectedposition_blue.get(i)) {
                    path = paths[i];
                    break;
                }
            }

            Intent intent = new Intent();
            intent.putExtra("PATH", path);
            setResult(RESULT_OK, intent);
            finish();
            break;
        case R.id.title_cancel:
            // 取消
            finish();
            break;

        default:
            break;
        }
    }
}
