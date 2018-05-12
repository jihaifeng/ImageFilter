package com.jihf.camerasdk.adapter;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.jihf.camerasdk.R;
import com.jihf.camerasdk.model.Filter_Sticker_Info;
import com.muzhi.camerasdk.library.utils.ViewHolder;

/**
 * 贴纸库Adapter
 */
public class StickerAdapter extends CommonListAdapter<Filter_Sticker_Info>{

	public StickerAdapter(Context context) {
		super(context);
		this.mContext=mContext;
		this.mLayoutId= R.layout.camerasdk_list_item_sticker;
	}

	@Override
	public void getCommonView(ViewHolder helper, Filter_Sticker_Info item) {

		ImageView image = helper.getView(R.id.iv_sticker);

		Glide.with(mContext)
				.load(item.getImage())
				.placeholder(R.drawable.camerasdk_pic_loading)
				.error(R.drawable.camerasdk_pic_loading)
				.override(120, 120)
				.centerCrop()
				.into(image);
	}


}


