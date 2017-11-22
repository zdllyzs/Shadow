package com.zdlly.shadow.ImageSelector;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zdlly.shadow.R;

class MyAdpter extends RecyclerView.Adapter<MyAdpter.MyViewHolder> {

    private ListActivity listActivity;
    private ListActivity.OnItemClickListener mOnItemClickListener;

    public MyAdpter(ListActivity listActivity) {
        this.listActivity = listActivity;
    }

    public void setOnItemClickLitener(ListActivity.OnItemClickListener mOnItemClickLitener) {
        this.mOnItemClickListener = mOnItemClickLitener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(listActivity).inflate(R.layout.image_selector_item, null);
        MyViewHolder viewholder = new MyViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Glide.with(listActivity).load(listActivity.list.get(position).getTopImagePath()).fitCenter()
                .centerCrop().into(holder.image);
        holder.title.setText(listActivity.list.get(position).getFolderName() + "(" + listActivity.list.get(position).getImageCounts() + ")");
        final int pos = position;
        holder.rv_selector_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOnItemClickListener.OnItemClick(view, pos);
            }
        });

    }

    @Override
    public int getItemCount() {
        return listActivity.list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public ImageView image;
        public RelativeLayout rv_selector_list;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv_selector_list_text);
            image = (ImageView) itemView.findViewById(R.id.iv_selector_list_image);
            rv_selector_list = (RelativeLayout) itemView.findViewById(R.id.rv_selector_list);
        }
    }
}
