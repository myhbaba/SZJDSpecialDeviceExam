package com.example.frank.jinding.UI.PublicMethodActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.bigkoo.pickerview.TimePickerView;
import com.example.frank.jinding.Adapter.OrderAdapter;
import com.example.frank.jinding.Bean.OrderBean.CheckOrder;
import com.example.frank.jinding.R;
import com.example.frank.jinding.Service.ApiService;
import com.example.frank.jinding.UI.SalesmanActivity.AddOrder;
import com.tamic.novate.Throwable;
import com.tamic.novate.callback.RxStringCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OrderSearch extends AppCompatActivity {

    private ListView lv_task;
    private ImageButton back;
    private TextView title;
    private Calendar cal;
    private Button search;
    private TextView startdate, enddate;
    private EditText orderOrgEt;
    private int year, month, day;
    private List<CheckOrder> orderList;
    private OrderAdapter mAdapter;
    private int requestCode;
    public static int LOOK_REQUEST_CODE=0x1a;
    public static int UPDATE_REQUEST_CODE=0x1b;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_task);

        init();
        requestCode=getIntent().getIntExtra("requestCode",0);
        if (requestCode==0x01)
        title.setText("订单查看");
        else if (requestCode==0x02)
            title.setText("订单修改");
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        startdate.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {
                chooseDate(1);
            }

        });


        enddate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseDate(2);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search();
            }
        });
        orderList = new ArrayList<>();
        mAdapter = new OrderAdapter(this);//得到一个MyAdapter对象
        mAdapter.listItem = orderList;
        lv_task.setAdapter(mAdapter);//为ListView绑定Adapter
/*为ListView添加点击事件*/
        lv_task.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Intent intent = new Intent(OrderSearch.this, AddOrder.class);
                if (requestCode == 0x01){
                    intent.putExtra("update", false);
                intent.putExtra("requestCode", LOOK_REQUEST_CODE);
            } else if (requestCode==0x02){
                    intent.putExtra("update", true);
                    intent.putExtra("requestCode", UPDATE_REQUEST_CODE);
                }
                intent.putExtra("checkOrder",orderList.get(arg2));
                startActivity(intent);
            }
        });
        lv_task.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
              if (requestCode==0x02) {
                  AlertDialog dialog = new AlertDialog.Builder(OrderSearch.this).setPositiveButton("确认", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          deleteOrder(position);
                      }
                  }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                      @Override
                      public void onClick(DialogInterface dialog, int which) {
                          dialog.dismiss();
                      }
                  }).create();
                  dialog.setTitle("确认删除订单？");
                  dialog.show();
              }
                return true;
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        orderList=null;
    }

    private void deleteOrder(final int position){
        Map<String, Object> map = new HashMap<>();
            map.put("orderId",orderList.get(position).getOrderId());
        ApiService.GetString(this, "orderDelete", map, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                if (response != null &&response.equals("success")) {
                    orderList.remove(position);
                   // mAdapter.listItem.remove(position);
                    mAdapter.notifyDataSetChanged();
                    Toast.makeText(OrderSearch.this,"删除成功",Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onError(Object tag, Throwable e) {

            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });
    }
    protected void chooseDate(final int code) {

//控制时间范围(如果不设置范围，则使用默认时间1900-2100年，此段代码可注释)
        //因为系统Calendar的月份是从0-11的,所以如果是调用Calendar的set方法来设置时间,月份的范围也要是从0-11
        java.util.Calendar selectedDate = java.util.Calendar.getInstance();
        final java.util.Calendar startDate = java.util.Calendar.getInstance();
        startDate.set(2013, 0, 23);
        final java.util.Calendar endDate = java.util.Calendar.getInstance();
        endDate.set(2100, 11, 28);
        //时间选择器
        TimePickerView pvTime = new TimePickerView.Builder(this, new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {//选中事件回调
                // 这里回调过来的v,就是show()方法里面所添加的 View 参数，如果show的时候没有添加参数，v则为null
                /*btn_Time.setText(getTime(date));*/
                if (code==1)
                    startdate.setText(getTime(date));
                else
                    enddate.setText(getTime(date));

            }
        })
                //年月日时分秒 的显示与否，不设置则默认全部显示
                .setType(new boolean[]{true, true, true, false, false, false})
                .setLabel("", "", "", "", "", "")
                .isCenterLabel(false)
                .setDividerColor(Color.DKGRAY)
                .setContentSize(21)
                .setDate(selectedDate)
                .setRangDate(startDate, endDate)
//                .setBackgroundId(0x00FFFFFF) //设置外部遮罩颜色
                .setDecorView(null)
                .build();
        pvTime.show();
    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        return format.format(date);
    }

    private void search() {
        View processView=View.inflate(this,R.layout.simple_processbar,null);
        final AlertDialog processDialog=new AlertDialog.Builder(this).create();
        processDialog.setView(processView);
        processDialog.show();
        Map<String, Object> map = new HashMap<>();
        if (requestCode==0x02){
            //只查询待审核订单
            map.put("orderStatus","01");
        }
        if (startdate.getText() != null)
            map.put("startDate", startdate.getText().toString());
        if (enddate.getText() != null)
            map.put("endDate", enddate.getText().toString());
        if (orderOrgEt.getText()!=null&&TextUtils.isEmpty(orderOrgEt.getText().toString()))
            map.put("orderOrg",orderOrgEt.getText());
        ApiService.GetString(this, "orderSearch", map, new RxStringCallback() {
            @Override
            public void onNext(Object tag, String response) {
                processDialog.dismiss();
                if (response != null && !TextUtils.isEmpty(response)) {
                    orderList.clear();
                    List<CheckOrder> list= JSON.parseArray(response,CheckOrder.class);
                     if (list!=null){
                         for (int i=0;i<list.size();i++){
                             orderList.add(list.get(i));
                         }
                         list.clear();
                     }
                    Log.i("获得订单",orderList.size()+"");
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onError(Object tag, Throwable e) {
                processDialog.dismiss();
                 Toast.makeText(OrderSearch.this,""+e.getMessage(),Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancel(Object tag, Throwable e) {

            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void init() {
        lv_task = (ListView) this.findViewById(R.id.lv_task);
        back = (ImageButton) this.findViewById(R.id.titleback);
        title = (TextView) this.findViewById(R.id.titleplain);
        startdate = (TextView) this.findViewById(R.id.start_date);
        enddate = (TextView) this.findViewById(R.id.end_date);
        search = (Button) this.findViewById(R.id.search_history);
         orderOrgEt=(EditText)this.findViewById(R.id.order_search_org);


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
      search();
    }
    //其他任务信息加载


}