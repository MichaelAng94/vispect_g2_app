package com.vispect.android.vispect_g2_app.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.MediaMetadataRetriever;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


import com.vispect.android.vispect_g2_app.R;

import java.util.List;

public class VideoAdapter extends BaseAdapter
{
    private LayoutInflater mInflater;
    private Context mContext;
    private List<String> mDatas;

    public VideoAdapter(Context context, List<String> mDatas)
    {
        mInflater = LayoutInflater.from(context);
        this.mContext = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount()
    {
        return mDatas.size();
    }

    @Override
    public Object getItem(int position)
    {
        return mDatas.get(position);
    }

    @Override
    public long getItemId(int position)
    {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder viewHolder = null;
        if (convertView == null)
        {
            convertView = mInflater.inflate(R.layout.video_listview, parent,
                    false);
            viewHolder = new ViewHolder();
            viewHolder.mTextView = (TextView) convertView
                    .findViewById(R.id.videoname);
            viewHolder.mImageView = (ImageView) convertView
                    .findViewById(R.id.video_pic);
            convertView.setTag(viewHolder);
        } else
        {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        viewHolder.mTextView.setText(mDatas.get(position).substring(mDatas.get(position).lastIndexOf('/')+1,mDatas.get(position).length()));
        viewHolder.mImageView.setImageBitmap(getVideoThumbnail(mDatas.get(position)));
        return convertView;
    }

    private final class ViewHolder
    {
        TextView mTextView;
        ImageView mImageView;
    }

    public Bitmap getVideoThumbnail(String filePath) {
        Bitmap b=null;
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        try {
            retriever.setDataSource(filePath);
            b=retriever.getFrameAtTime();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (RuntimeException e) {
            e.printStackTrace();

        } finally {
            try {
                retriever.release();
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
        return b;
    }

}