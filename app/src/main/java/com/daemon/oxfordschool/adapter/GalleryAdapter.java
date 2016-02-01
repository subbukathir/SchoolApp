package com.daemon.oxfordschool.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.daemon.oxfordschool.R;
import com.daemon.oxfordschool.classes.CustomGallery;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.utils.DiskCacheUtils;
import com.nostra13.universalimageloader.utils.MemoryCacheUtils;


public class GalleryAdapter extends BaseAdapter
{
	private Context mContext;
	private LayoutInflater infalter;
	private ArrayList<CustomGallery> data = new ArrayList<CustomGallery>();
	ImageLoader imageLoader;
    private DisplayImageOptions options;

	private boolean isActionMultiplePick;

	public GalleryAdapter(Context c, ImageLoader imageLoader)
    {
		infalter = (LayoutInflater) c.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		mContext = c;
		this.imageLoader = imageLoader;
		// clearCache();
	}

	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public CustomGallery getItem(int position) {
		return data.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setMultiplePick(boolean isMultiplePick) {
		this.isActionMultiplePick = isMultiplePick;
	}

	public void selectAll(boolean selection) {
		for (int i = 0; i < data.size(); i++) {
			data.get(i).isSeleted = selection;
		}
		notifyDataSetChanged();
	}

	public boolean isAllSelected() {
		boolean isAllSelected = true;

		for (int i = 0; i < data.size(); i++) {
			if (!data.get(i).isSeleted) {
				isAllSelected = false;
				break;
			}
		}

		return isAllSelected;
	}

	public boolean isAnySelected() {
		boolean isAnySelected = false;

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				isAnySelected = true;
				break;
			}
		}

		return isAnySelected;
	}

	public ArrayList<CustomGallery> getSelected() {
		ArrayList<CustomGallery> dataT = new ArrayList<CustomGallery>();

		for (int i = 0; i < data.size(); i++) {
			if (data.get(i).isSeleted) {
				dataT.add(data.get(i));
			}
		}

		return dataT;
	}

	public void addAll(ArrayList<CustomGallery> files) {

		try {
			this.data.clear();
			this.data.addAll(files);

		} catch (Exception e) {
			e.printStackTrace();
		}

		notifyDataSetChanged();
	}

	public void changeSelection(View v, int position) {

		if (data.get(position).isSeleted) {
			data.get(position).isSeleted = false;
		} else {
			data.get(position).isSeleted = true;
		}
		((ViewHolder) v.getTag()).imgQueueMultiSelected.setSelected(data
				.get(position).isSeleted);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)
    {
        String Str_Path = "";
        final ViewHolder holder;
		if (convertView == null)
        {

			convertView = infalter.inflate(R.layout.gallery_item, null);
			holder = new ViewHolder();
			holder.imgQueue = (ImageView) convertView.findViewById(R.id.imgQueue);
			holder.imgQueueMultiSelected = (ImageView) convertView.findViewById(R.id.imgQueueMultiSelected);
			if (isActionMultiplePick)
            {
				holder.imgQueueMultiSelected.setVisibility(View.VISIBLE);
			}
            else
            {
				holder.imgQueueMultiSelected.setVisibility(View.GONE);
			}

			convertView.setTag(holder);

		}
        else
        {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.imgQueue.setTag(position);

		try
        {
            Str_Path = "file://" + data.get(position).sdcardPath;
            clearCache(Str_Path);
            imageLoader.displayImage(Str_Path, holder.imgQueue,new ImageLoadingListener()
            {
                @Override
                public void onLoadingStarted(String imageUri, View view)
                {
                    holder.imgQueue.setImageResource(R.drawable.ic_profile);
                }
                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason)
                {
                    holder.imgQueue.setImageResource(R.drawable.ic_profile);
                }
                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage)
                {
                }
                @Override
                public void onLoadingCancelled(String imageUri, View view)
                {
                }
            });
    		if (isActionMultiplePick)
            {
				holder.imgQueueMultiSelected.setSelected(data.get(position).isSeleted);
			}
		}
        catch (Exception e)
        {
			e.printStackTrace();
		}

		return convertView;
	}

	public class ViewHolder
    {
		ImageView imgQueue;
		ImageView imgQueueMultiSelected;
	}

	public void clearCache(String Str_ImagePath)
    {
        MemoryCacheUtils.removeFromCache(Str_ImagePath, ImageLoader.getInstance().getMemoryCache());
        DiskCacheUtils.removeFromCache(Str_ImagePath, ImageLoader.getInstance().getDiskCache());
	}

	public void clear()
    {
		data.clear();
		notifyDataSetChanged();
	}

}
