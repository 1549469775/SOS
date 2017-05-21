package com.xieyaxin.space.sos;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.lv_data)
    ListView lvData;
    private List<Data> dataList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        OkHttpUtils.get()
                .url("http://192.168.137.1/diet")
                .build()
                .connTimeOut(2000)
                .readTimeOut(2000)
                .writeTimeOut(2000)
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        if (e != null)
                            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (JsonUtil.judgeError(response).equals("")) {
                            dataList = JsonUtil.stringToList(JsonUtil.getEntity(response), Data.class);
                            baseAdapter.notifyDataSetChanged();
                        }
                    }
                });
        lvData.setAdapter(baseAdapter);
        lvData.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EventBus.getDefault().postSticky(dataList.get(position));
                startActivity(new Intent(MainActivity.this,WebActivity.class));
            }
        });
    }

    BaseAdapter baseAdapter=new BaseAdapter() {
        @Override
        public int getCount() {
            return dataList.size();
        }

        @Override
        public Object getItem(int position) {
            return dataList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder=null;
           View view=null;
            if (viewHolder==null){
                view = LayoutInflater.from(MainActivity.this).inflate(R.layout.item,null);
                viewHolder=new ViewHolder();
                viewHolder.textView= (TextView) view.findViewById(R.id.textView);
                viewHolder.imageView= (ImageView) view.findViewById(R.id.imageView);
                view.setTag(viewHolder);
            }else {
                view=convertView;
                viewHolder= (ViewHolder) view.getTag();
            }
            viewHolder.textView.setText(dataList.get(position).getTitle());
            Glide.with(MainActivity.this).load(dataList.get(position).getImg()).asBitmap().into(viewHolder.imageView);
            return view;
        }

        class ViewHolder{
            private TextView textView;
            private ImageView imageView;
        }
    };
}
