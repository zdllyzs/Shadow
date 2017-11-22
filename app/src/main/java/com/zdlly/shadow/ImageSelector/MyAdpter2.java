package com.zdlly.shadow.ImageSelector;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.zdlly.shadow.R;

/**
 * Created by zdlly on 2017/3/10.
 */
class MyAdpter2 extends RecyclerView.Adapter<MyAdpter2.MyViewHolder> {


    private DetailsActivity detailsActivity;
    private DetailsActivity.OnItemClickListener mOnItemClickListener;
    private DetailsActivity.OnItemSelectkListener mOnItemSelectkListener;

    public MyAdpter2(DetailsActivity detailsActivity) {
        this.detailsActivity = detailsActivity;
    }

    public void setOnItemClickLitener(DetailsActivity.OnItemClickListener mOnItemClickLitener) {
        this.mOnItemClickListener = mOnItemClickLitener;
    }

    public void setmOnItemSelectkListener(DetailsActivity.OnItemSelectkListener mOnItemSelectkListener) {
        this.mOnItemSelectkListener = mOnItemSelectkListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(detailsActivity).inflate(R.layout.image_rec_item, null);


        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(detailsActivity).load(detailsActivity.fileList.get(position)).fitCenter()
                .centerCrop().into(holder.iv);
        final int pos = position;
        holder.iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnItemClickListener != null)
                    mOnItemClickListener.OnItemClick(v, pos);
            }
        });


        }

    @Override
    public int getItemCount() {
        return detailsActivity.fileList.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {
        ImageView iv;

        public MyViewHolder(View itemView) {
            super(itemView);
            iv = (ImageView) itemView.findViewById(R.id.iv_rec_item);

            ViewGroup.LayoutParams params = iv.getLayoutParams();
            int width = (detailsActivity.getScreenWidth(detailsActivity) - 20) / 3;
            int height = (detailsActivity.getScreenHeight(detailsActivity)) / 5;

            RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(width, height);
            iv.setLayoutParams(lp);


        }
    }
}
