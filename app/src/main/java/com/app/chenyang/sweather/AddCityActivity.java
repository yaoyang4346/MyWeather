package com.app.chenyang.sweather;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.app.chenyang.sweather.adapter.SearchCityListAdapter;
import com.app.chenyang.sweather.adapter.TopicCityAdapter;
import com.app.chenyang.sweather.db.SearchCityDao;
import com.app.chenyang.sweather.entity.CityPosition;
import com.app.chenyang.sweather.entity.SearchCityInfo;
import com.app.chenyang.sweather.entity.WeatherDataInfo;
import com.app.chenyang.sweather.global.MyConst;
import com.app.chenyang.sweather.presenter.AddCityPresenter;
import com.app.chenyang.sweather.ui.view.AddCityViewManager;
import com.app.chenyang.sweather.ui.view.IAddCityView;
import com.app.chenyang.sweather.utils.BaseUtils;
import com.app.chenyang.sweather.utils.LogUtils;

import org.greenrobot.eventbus.EventBus;
import org.litepal.crud.DataSupport;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.functions.Action1;

public class AddCityActivity extends BaseActivity<IAddCityView ,AddCityPresenter> implements AdapterView.OnItemClickListener ,IAddCityView{
    @BindView(R.id.iv_back) ImageView ivBack;
    @BindView(R.id.iv_clear_input) ImageView ivClearInput;
    @BindView(R.id.etSearch) EditText etSearch;
    @BindView(R.id.tv_edit_hint) TextView tvEditHint;
    @BindView(R.id.add_city_view_manager) AddCityViewManager viewManager;
    @BindView(R.id.show_map) TextView tvMap;

    private SearchCityListAdapter listViewAdapter ;
    private TopicCityAdapter gridViewAdapter ;
    private GridView gridView;
    private ListView listView;
    private ArrayList<SearchCityInfo> tempList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_city);
        ButterKnife.bind(this);
        viewManager.getMap().onCreate(savedInstanceState);
        initView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        viewManager.getMap().onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        viewManager.getMap().onSaveInstanceState(outState);
    }

    @Override
    public AddCityPresenter createPresenter() {
        return new AddCityPresenter(this);
    }

    private void initView(){
        BaseUtils.clickEvent(ivClearInput, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                mPresenter.unsubscribeload();
                etSearch.setEnabled(true);
                etSearch.setText("");
            }
        });
        BaseUtils.clickEvent(ivBack, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                finish();
            }
        });
        BaseUtils.clickEvent(tvMap, new Action1<Void>() {
            @Override
            public void call(Void aVoid) {
                if(viewManager.getCurrentState() == AddCityViewManager.AddCityState.STATE_LOADING){
                    mPresenter.unsubscribeload();
                    etSearch.setText("");
                }
                if(viewManager.getCurrentMode() == AddCityViewManager.TEXT_MODE){
                    viewManager.switchMode(AddCityViewManager.MAP_MODE);
                    tvMap.setText(R.string.topic);
                    mPresenter.loadCityAtMap(viewManager.getMap());
                    return;
                }
                viewManager.switchMode(AddCityViewManager.TEXT_MODE);
                tvMap.setText(R.string.add_city_head_map);
            }
        });
        mPresenter.searchCity(etSearch);
    }

    @Override
    public void showIdle() {
        switchState(true);
        viewManager.setState(AddCityViewManager.AddCityState.STATE_IDLE);
        loadGridlayout();
    }

    @Override
    public void showLoading() {
        switchState(false);
        viewManager.setState(AddCityViewManager.AddCityState.STATE_LOADING);
    }

    @Override
    public void showNullSearch() {
        switchState(false);
        viewManager.setState(AddCityViewManager.AddCityState.STATE_NULL);
    }

    @Override
    public void showSearchSuccess(ArrayList<SearchCityInfo> cityList, String key) {
        switchState(false);
        tempList = cityList;
        viewManager.setState(AddCityViewManager.AddCityState.STATE_SEARCH_SUCCESS);
        loadListView(cityList,key);
    }

    @Override
    public void showNetError() {
        switchState(false);
        viewManager.setState(AddCityViewManager.AddCityState.STATE_NET_ERROR);
    }

    @Override
    public void showServeError() {
        switchState(false);
        viewManager.setState(AddCityViewManager.AddCityState.STATE_SERVE_ERROR);
    }

    @Override
    public void loadSuccess() {
        EventBus.getDefault().postSticky(new CityPosition(DataSupport.count(WeatherDataInfo.class)-1));
        finish();
    }

    @Override
    public void jumpTargetPage(int position) {
        EventBus.getDefault().postSticky(new CityPosition(position));
        finish();
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        switch (adapterView.getId()){
            case R.id.gl_topic_city:
                etSearch.setEnabled(false);
                SearchCityDao searchCityDao = new SearchCityDao();
                etSearch.setText(MyConst.CITY_LIST[i]);
                mPresenter.loadWeather(searchCityDao.getIDByName(MyConst.CITY_LIST[i]),AddCityViewManager.TEXT_MODE);
                searchCityDao.closeDB();
                break;
            case R.id.lv_search_city:
                mPresenter.loadWeather(tempList.get(i).getID(),viewManager.getCurrentMode());
                break;
            default:
                break;
        }
    }

    private void loadGridlayout(){
        if(gridView == null){
            gridView = (GridView) viewManager.findViewById(R.id.gl_topic_city);
            gridView.setOnItemClickListener(this);
        }
        if(gridViewAdapter == null){
            gridViewAdapter = new TopicCityAdapter();
            gridView.setAdapter(gridViewAdapter);
            return;
        }
        gridViewAdapter.notifyDataSetChanged();
    }

    private void loadListView(ArrayList<SearchCityInfo> list, String key){
        if(listView == null){
            listView = (ListView) viewManager.findViewById(R.id.lv_search_city);
            listView.setOnItemClickListener(this);
        }
        if(listViewAdapter == null){
            listViewAdapter = new SearchCityListAdapter(list,key);
            listView.setAdapter(listViewAdapter);
            return;
        }
        listViewAdapter.setKey(key)
                .setList(list);
        listViewAdapter.notifyDataSetChanged();
    }

    private void switchState(boolean isIdle){
        if(isIdle){
            if(etSearch.getText().length() != 0){
                etSearch.setText("");
            }
            if(!etSearch.isEnabled()){
                etSearch.setEnabled(true);
            }
            ivClearInput.setVisibility(View.GONE);
            tvEditHint.setVisibility(View.VISIBLE);
            return;
        }
        ivClearInput.setVisibility(View.VISIBLE);
        tvEditHint.setVisibility(View.GONE);
    }
}
