package com.app.chenyang.sweather;

import android.app.AlarmManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.app.chenyang.sweather.adapter.CityManagerAdapter;
import com.app.chenyang.sweather.entity.PositionChangeEvent;
import com.app.chenyang.sweather.entity.RefreshEvent;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.helper.RecycleViewHelp.CityRecycleCallBack;
import com.app.chenyang.sweather.helper.RecycleViewHelp.DividerItemDecoration;
import com.app.chenyang.sweather.helper.RecycleViewHelp.OnRecycleItemTouchListener;
import com.app.chenyang.sweather.helper.RecycleViewHelp.OnStartDragListener;
import com.app.chenyang.sweather.presenter.CityManagerPresenter;
import com.app.chenyang.sweather.service.UpdateWeatherService;
import com.app.chenyang.sweather.ui.view.ICityManagerView;
import com.app.chenyang.sweather.ui.widget.SmoothCheckBox;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;
import com.app.chenyang.sweather.utils.PrefUtils;
import com.app.chenyang.sweather.utils.ServiceUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class CityManageActivity extends BaseActivity<ICityManagerView,CityManagerPresenter> implements ICityManagerView{
    @BindView(R.id.layout_normal) View normalHead;
    @BindView(R.id.layout_edit) View editHead;
    @BindView(R.id.recycle) RecyclerView recyclerView;
    @BindView(R.id.iv_back) ImageView ivBack;
    @BindView(R.id.tv_add) TextView tvAdd;
    @BindView(R.id.tv_more) TextView tvMore;
    @BindView(R.id.tv_delete) TextView tvDelete;
    @BindView(R.id.cb_all) SmoothCheckBox cbAll;
    @BindView(R.id.loading) View loadView;
    @BindView(R.id.tv_cball) TextView tvCbAll;

    public static final int NORMAL_MODE = 0;
    public static final int EDIT_MODE = 1;
    private int currentMode = 0;
    private CityManagerAdapter adapter;
    private ProgressDialog dialog;
    private boolean isFail = false;
    private boolean isPositionChange = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        ButterKnife.bind(this);
        init();
        mPresenter.loadAllCity();
    }

    @Override
    public CityManagerPresenter createPresenter() {
        return new CityManagerPresenter(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN,sticky = true)
    public void doRefreshRecycleView(RefreshEvent event){
        EventBus.getDefault().removeStickyEvent(RefreshEvent.class);
        LogUtils.d("recycle View prepare refresh");
        adapter.setAllCity((ArrayList<WeatherDataInfo>) DataSupport.findAll(WeatherDataInfo.class));
        adapter.notifyDataSetChanged();
    }

    private void init() {
        BaseUtils.clickEvent(ivBack, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });
        BaseUtils.clickEvent(tvDelete, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mPresenter.deleteCity(adapter);
            }
        });
        tvAdd.setVisibility(View.GONE);
        BaseUtils.clickEvent(tvMore, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                showPopupMenu();
            }
        });
        addCheckListener();
    }

    private void showPopupMenu(){
        final PopupMenu popupMenu = new PopupMenu(this,tvMore,Gravity.RIGHT | Gravity.TOP);
        popupMenu.getMenuInflater().inflate(R.menu.citymanager_menu,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case R.id.edit:
                        mPresenter.recycleLongPressEvent(currentMode,adapter);
                        break;
                    case R.id.setting:
                        startActivity(new Intent(BaseUtils.getContext(),SettingsActivity.class));
                        break;
                    case R.id.update:
                        if(PrefUtils.getLong(PrefUtils.IS_UPDATE,-1) == -1 || ServiceUtils.isUpdateOverTimeException()){
                            if(DataSupport.count(WeatherDataInfo.class) > 0){
                                UpdateWeatherService.startService(BaseUtils.getContext(),true);
                            }else{
                                BaseUtils.showToast(R.string.no_city);
                            }
                        }else{
                            BaseUtils.showToast(R.string.update_service_running);
                        }
                        break;
                    default:
                        break;
                }
                return true;
            }
        });
        popupMenu.show();
    }

    private void addCheckListener(){
        cbAll.setOnCheckedChangeListener(new SmoothCheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SmoothCheckBox checkBox, boolean isChecked) {
                mPresenter.checkAll(isChecked,adapter);
            }
        });
    }

    @Override
    public void onBackPressed() {
        mPresenter.clickBack(currentMode,adapter);
    }

    @Override
    public void finish() {
        EventBus.getDefault().postSticky(new PositionChangeEvent(isPositionChange));
        isPositionChange = false;
        super.finish();
    }

    @Override
    public void showLoading() {
        loadView.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showAllCity(ArrayList<WeatherDataInfo> allCity) {
        loadView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        configRecycleView(allCity);
    }

    @Override
    public void showNull() {
        loadView.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        BaseUtils.showToast(R.string.no_city);
    }

    @Override
    public void doFinish() {
        finish();
    }

    @Override
    public void switchMode(int mode) {
        currentMode = mode;
        if(mode == NORMAL_MODE){
            cbAll.removeOnCheckChangeListener();
            cbAll.setChecked(false);
            tvCbAll.setText(R.string.select_city);
            normalHead.setVisibility(View.VISIBLE);
            editHead.setVisibility(View.GONE);
            return;
        }
        adapter.clearCheckList();
        normalHead.setVisibility(View.GONE);
        editHead.setVisibility(View.VISIBLE);
        addCheckListener();
    }

    @Override
    public void showUpdataLoading() {
        isPositionChange = true;
        loadTrigger(true);
    }

    @Override
    public void hideUpdataLoading(int msg) {
        BaseUtils.showToast(msg);
        loadTrigger(false);
    }

    @Override
    public void updataFail() {
        loadTrigger(false);
        isFail = true;
        mPresenter.revoceryDB(adapter);
    }

    @Override
    public void checkChange() {
        if(adapter.getAllCity().size() == 0){
            LogUtils.d("delete all city, cancel alarm");
            ServiceUtils.cancelAlarm((AlarmManager) getSystemService(Context.ALARM_SERVICE));
            mPresenter.clickBack(currentMode,adapter);
            return;
        }
        int size = adapter.getCheckList().size();
        checkBoxTrigger(size != 0 && size == adapter.getAllCity().size());
        if(size == 0){
            tvCbAll.setText(R.string.select_city);
            return;
        }
        tvCbAll.setText(size+"");
    }

    private void checkBoxTrigger(boolean isCheck){
        cbAll.removeOnCheckChangeListener();
        cbAll.setChecked(isCheck,false);
        addCheckListener();
    }

    private void loadTrigger(boolean isShow){
        if (isShow){
            dialog = new ProgressDialog(this);
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            dialog.setCancelable(false);
            if (isFail){
                isFail = false;
                dialog.setMessage(getString(R.string.updata_db_fail));
            }else{
                dialog.setMessage(getString(R.string.updata_db));
            }
            dialog.show();
            return;
        }
        if(dialog != null){
            dialog.dismiss();
            dialog = null;
        }
    }

    private void configRecycleView(ArrayList<WeatherDataInfo> allCity) {
        recyclerView.setLayoutManager(new LinearLayoutManager(BaseUtils.getContext()));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(BaseUtils.getContext(),DividerItemDecoration.VERTICAL_LIST,getDrawable(R.drawable.recycle_divider)));
        recyclerView.addOnItemTouchListener(new OnRecycleItemTouchListener(recyclerView){
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                super.onItemClick(vh, position);
                mPresenter.recycleClickEvent(position,currentMode,adapter);
            }

            @Override
            public void onItemLongClick(RecyclerView.ViewHolder vh, int position) {
                super.onItemLongClick(vh, position);
                mPresenter.recycleLongPressEvent(currentMode,adapter);
            }
        });
        adapter = new CityManagerAdapter(allCity);
        final ItemTouchHelper itemTouchHelp = new ItemTouchHelper(new CityRecycleCallBack(adapter));
        itemTouchHelp.attachToRecyclerView(recyclerView);
        adapter.setOnDragStartListener(new OnStartDragListener() {
            @Override
            public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
                itemTouchHelp.startDrag(viewHolder);
            }
        });
        recyclerView.setAdapter(adapter);
    }
}