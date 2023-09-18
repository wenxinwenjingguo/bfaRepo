package com.lgsa.bfademo.adapter;


import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.lgsa.bfademo.R;
import com.lgsa.bfademo.database.SummaryDBHelper;
import com.lgsa.bfademo.entity.TextBean;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.ViewHolder> {
    private List<TextBean> lists;
    private static final String TAG = "mix";

    //标记展开的item
    private int opened = -1;

    public RecycleAdapter(List<TextBean> list) {
        if (list != null) {
            lists = list;
        } else {
            lists = new ArrayList<>();
        }
    }

    //设置列表数据
    public void setLists(List<TextBean> lists) {
        this.lists = lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycle, parent, false);
        return new ViewHolder(view,this); //传入 adapter
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.bindView(position,lists.get(position));
    }

    // 获取数据的数量
    @Override
    public int getItemCount() {
        return lists.size();
    }
    public void addItem(TextBean item) {
        int size = lists.size();
        // 判断是否已存在相同的 TextBean 对象
        for (int i = 0; i < size; i++) {
            if (Objects.equals(lists.get(i).getId(), item.getId())) {
                // 如果已经存在相同 id 的元素，则不进行插入操作，直接返回
                return;
            }
        }
        // 如果不存在相同 id 的元素，则进行插入操作
        for (int i = 0; i < size; i++) {
            if (lists.get(i).getTime().compareTo(item.getTime()) < 0) {// 插入排序，确保时间从大到小
                lists.add(i, item);
                notifyItemInserted(i);
                return;
            }
        }
        // 如果新元素的时间最晚，则添加到列表最后
        lists.add(item);
        notifyItemInserted(lists.size() - 1);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvTime;
        private ImageView downArrow;
        private TextView tvContent;
        private TextView tvLocation;
        private RelativeLayout msgRl;
        private LinearLayout msgLl;
        private Button btDelete;
        private Button btUp;
        private RecycleAdapter adapter;
        private Button btCheck;
        private SummaryDBHelper dbHelper;
        public ViewHolder(View itemView, RecycleAdapter recycleAdapter) {
            super(itemView);
            this.adapter = adapter; // 初始化 adapter
            tvTime = (TextView) itemView.findViewById(R.id.tvTime);
            tvContent = (TextView) itemView.findViewById(R.id.tvContent);
            downArrow = (ImageView) itemView.findViewById(R.id.downArrow);
            tvLocation = (TextView) itemView.findViewById(R.id.tvLocation);
            msgRl = (RelativeLayout) itemView.findViewById(R.id.msg_rl);
            msgLl = (LinearLayout) itemView.findViewById(R.id.msg_ll);
            btDelete=(Button) itemView.findViewById(R.id.btDelete);
            btUp=(Button) itemView.findViewById(R.id.btUp);
            btCheck=(Button) itemView.findViewById(R.id.btCheck);

            downArrow.setOnClickListener(this);
            Log.i(TAG,"执行了下拉按钮的监听");
            btDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        TextBean tx=lists.get(position);
                        String time=tx.getTime();
                        // 从适配器中移除对应的数据项
                        lists.remove(position);
                        notifyItemRemoved(position);
                        // 从数据库中删除对应的数据项
                        dbHelper = new SummaryDBHelper(itemView.getContext());
                        dbHelper.deleteItem(time);
                    }
                }
            });
            btUp.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //将数据上传到服务器
                    myToast();
                }
            });
            btCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showMyDialog();
                }
            });
        }

         //此方法实现列表数据的绑定和item的展开/关闭
         public void bindView(int pos, TextBean lists) {
            tvTime.setText(lists.time);
            tvContent.setText(lists.content);
            tvLocation.setText(lists.location);

            if (pos == opened){
                msgLl.setVisibility(View.VISIBLE);
            } else{
                msgLl.setVisibility(View.GONE);
            }

        }
        //downArrow的点击事件
        @Override
        public void onClick(View v) {
            if (opened == getAdapterPosition()) {
                //当点击的item已经被展开了, 就关闭.
                opened = -1;
                notifyItemChanged(getAdapterPosition());
            } else {
                int oldOpened = opened;
                opened = getAdapterPosition();
                notifyItemChanged(oldOpened);
                notifyItemChanged(opened);
            }
        }

        // 弹出对话框
        private void showMyDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(itemView.getContext());
            View dialogView = LayoutInflater.from(itemView.getContext()).inflate(R.layout.dialog_layout, null);
            builder.setView(dialogView);
            ImageView ivDialog=dialogView.findViewById(R.id.ivDialog);
            TextView barrier=dialogView.findViewById(R.id.barrier);
            TextView detail=dialogView.findViewById(R.id.detail);
            builder.setTitle("详情");
            int position = getAdapterPosition();
            TextBean tx = lists.get(position);
            String id = tx.getId();
            dbHelper=new SummaryDBHelper(itemView.getContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();
            Cursor cursor = db.query("summaryInfo", null, "_id=?", new String[]{id}, null, null, null);
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") String type = cursor.getString(cursor.getColumnIndex("type"));
                    @SuppressLint("Range") String des = cursor.getString(cursor.getColumnIndex("detail"));
                    barrier.setText(type);
                    detail.setText(des);

                    @SuppressLint("Range") byte[] photo = cursor.getBlob(cursor.getColumnIndex("photo"));
                    //将字节数组转化为位图显示
                    if (photo != null) {
                        Bitmap bmp = BitmapFactory.decodeByteArray(photo, 0, photo.length);
                        ivDialog.setImageBitmap(bmp);
                    }else {
                        ivDialog.setImageResource(R.drawable.nophoto); // 或者你希望设置的默认图片
                    }
                } while (cursor.moveToNext());
            }
            cursor.close();
            db.close();
            builder.setPositiveButton("关闭", null);
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        private void myToast(){
            Toast.makeText(itemView.getContext(), "暂未连接服务器", Toast.LENGTH_SHORT).show();
        }
    }
}
