package com.yunqinglai.okdownload.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.yunqinglai.okdownload.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListActivity extends BaseSampleActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final RecyclerView recyclerView = new RecyclerView(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        setContentView(recyclerView);

        final Adapter adapter = new Adapter();
        setupAdapter(new ItemsHolder() {
            @Override
            public void addItem(@StringRes int nameRes, @StringRes int descRes, Class<?> activityClass) {
                adapter.itemModelList.add(new ItemModel(nameRes, descRes, activityClass));
            }
        });

        recyclerView.setAdapter(adapter);
    }

    protected abstract void setupAdapter(ItemsHolder holder);

    public interface ItemsHolder {
        void addItem(@StringRes int nameRes, @StringRes int descRes, Class<?> activityClass);
    }

    private static class ItemModel {
        @StringRes
        private int nameRes;
        @StringRes
        private int descRes;

        private Class<?> activityClass;

        ItemModel(@StringRes int nameRes, @StringRes int descRes, Class<?> activityClass) {
            this.nameRes = nameRes;
            this.descRes = descRes;
            this.activityClass = activityClass;
        }
    }

    private class Adapter extends RecyclerView.Adapter<Adapter.Holder> {
        List<ItemModel> itemModelList = new ArrayList<>();

        @Override
        public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new Holder(LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.item_base_list_activity, parent, false));
        }

        @Override
        public void onBindViewHolder(Holder holder, int position) {
            final ItemModel model = itemModelList.get(position);
            holder.nameTv.setText(model.nameRes);
            holder.descTv.setText(model.descRes);
        }

        @Override
        public int getItemCount() {
            return itemModelList.size();
        }

        class Holder extends RecyclerView.ViewHolder {
            TextView nameTv;
            TextView descTv;

            Holder(View itemView) {
                super(itemView);
                nameTv = itemView.findViewById(R.id.name_tv);
                descTv = itemView.findViewById(R.id.desc_tv);
                itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final ItemModel model = itemModelList.get(getAdapterPosition());
                        startActivity(new Intent(BaseListActivity.this, model.activityClass));
                    }
                });
            }
        }
    }
}
