package com.remind.fragment;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ViewFlipper;

import com.help.remind.R;
import com.remind.activity.ChatActivity;
import com.remind.entity.FaceHistoryData;

public class FaceFragment extends Fragment {
	int[] faceId = { R.drawable.f_static_000, R.drawable.f_static_001,
			R.drawable.f_static_002, R.drawable.f_static_003,
			R.drawable.f_static_004, R.drawable.f_static_005,
			R.drawable.f_static_006, R.drawable.f_static_009,
			R.drawable.f_static_010, R.drawable.f_static_011,
			R.drawable.f_static_012, R.drawable.f_static_013,
			R.drawable.f_static_014, R.drawable.f_static_015,
			R.drawable.f_static_017, R.drawable.f_static_018 };
	String[] faceName = { "\\呲牙", "\\淘气", "\\流汗", "\\偷笑", "\\再见", "\\敲打",
			"\\擦汗", "\\流泪", "\\掉泪", "\\小声", "\\炫酷", "\\发狂", "\\委屈", "\\便便",
			"\\菜刀", "\\微笑", "\\色色", "\\害羞" };

	protected ViewFlipper viewFlipper = null;
	protected LinearLayout pagePoint = null;
	ArrayList<ArrayList<HashMap<String, Object>>> listGrid = null;
	ArrayList<ImageView> pointList = null;
	public static MyFaceHandler faceHandler = null;

	public static final int ActivityId = 1;
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
			inflate = inflater.inflate(R.layout.myface_layout, null);
			setupView(inflate);
		}
		return inflate;
		


		

	}
	
	private void setupView(View view) {
		faceHandler = new MyFaceHandler(Looper.myLooper());

		viewFlipper = (ViewFlipper) view.findViewById(R.id.faceFlipper);
		pagePoint = (LinearLayout) view.findViewById(R.id.pagePoint);
		listGrid = new ArrayList<ArrayList<HashMap<String, Object>>>();
		pointList = new ArrayList<ImageView>();

		addFaceData();
		addGridView();
		setPointEffect(0);
	}
	
	public class MyFaceHandler extends Handler{

		public MyFaceHandler(Looper looper){
			super(looper);
		}
		
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch(msg.what){
			
			case ChatActivity.ActivityID:
				if(msg.obj.toString().equals("collapse")){   //关闭表情
					
					if(ChatActivity.currentTabTag.equals("face"))
						viewFlipper.setDisplayedChild(0);
					setPointEffect(0);
					
				}
				
				
			}
			
			
		}
		
	}
	
	private void addFaceData(){
		if(listGrid!=null)
			listGrid.clear();                //首先将先前的数据清空
		ArrayList<HashMap<String,Object>> list=null;
		for(int i=0; i<faceId.length; i++){
			if(i%14==0){
				list=new ArrayList<HashMap<String,Object>>();
				listGrid.add(list);
			}  
			HashMap<String,Object> map=new HashMap<String,Object>();
			map.put("image", faceId[i]);
			map.put("faceName", faceName[i]);
			
			/**
			 * 这里把表情对应的名字也添加进数据对象中，便于在点击时获得表情对应的名称
			 */
			listGrid.get(i/14).add(map);		
		}
		System.out.println("listGrid size is "+listGrid.size());
	}
	
	private void addGridView(){
		if(viewFlipper!=null){
			viewFlipper.removeAllViews();
			pagePoint.removeAllViews();
			pointList.clear();               //更新前首先清除原有的所有数据
		}
		for(int i=0; i< listGrid.size();i++){
			View view=LayoutInflater.from(getActivity()).inflate(R.layout.view_item, null);
			GridView gv=(GridView)view.findViewById(R.id.myGridView);
			gv.setNumColumns(5);
			gv.setSelector(new ColorDrawable(Color.TRANSPARENT));
			MyGridAdapter adapter=new MyGridAdapter(getActivity(), listGrid.get(i), R.layout.chat_grid_item, new String[]{"image"}, new int[]{R.id.gridImage});
			gv.setAdapter(adapter);
			gv.setOnTouchListener(new MyTouchListener(viewFlipper));
			viewFlipper.addView(view);
		//	ImageView image=new ImageView(this);
		//	ImageView image=(ImageView)LayoutInflater.from(this).inflate(R.layout.image_point_layout, null);
            /**
             * 这里不喜欢用Java代码设置Image的边框大小等，所以单独配置了一个Imageview的布局文件
             */
			View pointView=LayoutInflater.from(getActivity()).inflate(R.layout.point_image_layout, null);
			ImageView image=(ImageView)pointView.findViewById(R.id.pointImageView);
			image.setBackgroundResource(R.drawable.qian_point);
			pagePoint.addView(pointView);   
			/**
			 * 这里验证了LinearLayout属于ViewGroup类型，可以采用addView 动态添加view
			 */
			
			pointList.add(image);
			/**
			 * 将image放入pointList，便于修改点的颜色
			 */
		}
	
	}
	
	
	/**
	 * 设置游标（小点）的显示效果
	 * @param darkPointNum    
	 */
	private void setPointEffect(int darkPointNum){
		for(int i=0; i<pointList.size(); i++){
			pointList.get(i).setBackgroundResource(R.drawable.qian_point);
		}
		if(pointList.size()>0)
			pointList.get(darkPointNum).setBackgroundResource(R.drawable.shen_point);
	}
	private boolean moveable=true;
	private float startX=0;
	
	/**
	 * 用到的方法 viewFlipper.getDisplayedChild()  获得当前显示的ChildView的索引
	 * @author Administrator
	 *
	 */
	class MyTouchListener implements OnTouchListener{

		ViewFlipper viewFlipper=null;
		
		
		public MyTouchListener(ViewFlipper viewFlipper) {
			super();
			this.viewFlipper = viewFlipper;
		}

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			// TODO Auto-generated method stub
			switch(event.getAction()){
			case MotionEvent.ACTION_DOWN:startX=event.getX(); moveable=true; break;
			case MotionEvent.ACTION_MOVE:
				if(moveable){
					if(event.getX()-startX>40){
						moveable=false;
						int childIndex=viewFlipper.getDisplayedChild();
						/**
						 * 这里的这个if检测是防止表情列表循环滑动
						 */
						if(childIndex>0){
						    viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_in));
						    viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_out));						
							viewFlipper.showPrevious();
							setPointEffect(childIndex-1);
						}
					}
					else if(event.getX()-startX<-40){
						moveable=false;
						int childIndex=viewFlipper.getDisplayedChild();
						/**
						 * 这里的这个if检测是防止表情列表循环滑动
						 */
						if(childIndex<listGrid.size()-1){
							viewFlipper.setInAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.right_in));
							viewFlipper.setOutAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.left_out));
							viewFlipper.showNext();
							setPointEffect(childIndex+1);
						}
					}
				}
			    break;
			case MotionEvent.ACTION_UP:moveable=true;break;
			default:break;
			}
			
			return false;
		}
		
	}
	
	
	/**
	 * GridViewAdapter
	 * @param textView
	 * @param text
	 */
	
	class MyGridAdapter extends BaseAdapter{

		Context context;
		ArrayList<HashMap<String,Object>> list;
		int layout;
		String[] from;
		int[] to;
		
		
		public MyGridAdapter(Context context,
				ArrayList<HashMap<String, Object>> list, int layout,
				String[] from, int[] to) {
			super();
			this.context = context;
			this.list = list;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return list.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return null;
		}  

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		class ViewHolder{
			ImageView image=null;
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ViewHolder holder=null;
			if(convertView==null){
				convertView=LayoutInflater.from(context).inflate(layout, null);
				holder=new ViewHolder();
				holder.image=(ImageView)convertView.findViewById(to[0]);
				convertView.setTag(holder);
			}
			else{
				holder=(ViewHolder)convertView.getTag();
			}
			holder.image.setImageResource((Integer)list.get(position).get(from[0]));
			class MyGridImageClickListener implements OnClickListener{

				int position;
				
				public MyGridImageClickListener(int position) {
					super();
					this.position = position;
				}


				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					//editText.append((String)list.get(position).get("faceName"));
					Message msg=new Message();
					msg.what=FaceFragment.ActivityId;
					msg.arg1=0;
					msg.obj=(String)list.get(position).get("faceName");
					ChatActivity.chatHandler.sendMessage(msg);
					//一个消息不能被多个Handler使用！！！
					
					if(FaceHistoryData.faceHistoryList==null){
						FaceHistoryData.faceHistoryList=new ArrayList<HashMap<String,Object>>();
					}
					
					int index=0;
					int curFaceId=(Integer)list.get(position).get("image");
					for(index=0; index<FaceHistoryData.faceHistoryList.size(); index++){
						if((Integer)FaceHistoryData.faceHistoryList.get(index).get("faceId")==curFaceId){
							break;
						}
					}
					if(index>=FaceHistoryData.faceHistoryList.size()){
						HashMap<String,Object> map=new HashMap<String,Object>();
						map.put("faceId", (Integer)list.get(position).get("image"));
						map.put("faceName", (String)list.get(position).get("faceName"));
						FaceHistoryData.faceHistoryList.add(0, map);     //前插入
						int curSize=FaceHistoryData.faceHistoryList.size();  //控制长度
						if(curSize>36){
							FaceHistoryData.faceHistoryList.remove(curSize-1);
						}
					}
						
					
				}
				
			}
			//这里创建了一个方法内部类
			holder.image.setOnClickListener(new MyGridImageClickListener(position));
					
			return convertView;
		}
		
	}

	

	@Override
	public void onResume() {
		addFaceData();
		addGridView();
		setPointEffect(0);
		super.onResume();
	}
}
