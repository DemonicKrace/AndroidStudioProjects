package tw.android;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class ImageAdapter extends BaseAdapter {

	private Context mContext;		// 儲存程式的執行環境。
	private Integer[] miImgArr;		// 儲存影像縮圖id陣列。

	public ImageAdapter(Context context, Integer[] imgArr) {
		mContext = context;
		miImgArr = imgArr;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return miImgArr.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView v;

		// 如果convertView是null，就建立一個新的ImageView物件。
		// 如果convertView不是null，就重複使用它。
		if (convertView == null) {
			v = new ImageView(mContext);
			v.setLayoutParams(new GridView.LayoutParams(90, 90));
			v.setScaleType(ImageView.ScaleType.CENTER_CROP);
			v.setPadding(10, 10, 10, 10);

		}
		else
			v = (ImageView) convertView;

		// 把要顯示的縮圖放到ImageView物件中。
        v.setImageResource(miImgArr[position]);
        
        return v;
	}

}
