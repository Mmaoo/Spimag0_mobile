package com.mmaoo.spimag.ui.areaList;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mmaoo.spimag.Navigable;
import com.mmaoo.spimag.R;
import com.mmaoo.spimag.model.Area;
import com.mmaoo.spimag.model.Item;
import com.mmaoo.spimag.ui.areaAdd.AreaAddFragment;
import com.mmaoo.spimag.ui.areaShow.AreaShowFragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

public class AreaListFragment extends Fragment {

    public static final int ACTION_ADD_ITEM = 1;
    public static final int ACTION_ADD_AREA = 2;
    public static final int ACTION_NAVIGATE_UP = 999;

    Navigable navigable;

    TextView titleTextView;
    TextView searchTextView;
    FloatingActionButton addFloatingActionButton;
    RecyclerView areaListRecyclerView;
    SwipeRefreshLayout areaListSwipeRefreshLayout;

    FBAreaRecyclerViewAdapter areaRecyclerViewAdapter;

    View root;

    int action = -1;

    ArrayList<Area> areas;

    ActionMode actionMode = null;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_area_list, container, false);

        return root;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle arguments = getArguments();

        if(arguments == null) {
            action = Integer.MAX_VALUE;
        }else {
            action = arguments.getInt("action", Integer.MAX_VALUE);
        }

        titleTextView = root.findViewById(R.id.titleTextView);
        searchTextView = root.findViewById(R.id.searchAreaEditText);
        addFloatingActionButton = root.findViewById(R.id.addAreaFloatingActionButton);
        areaListRecyclerView = root.findViewById(R.id.areaListRecyclerView);
        areaListSwipeRefreshLayout = root.findViewById(R.id.areaListSwipeRefreshLayout);

        if(action == ACTION_ADD_ITEM) {
            try {
                Item item = (Item) arguments.getSerializable("item");
                titleTextView.setText("Edycja: "+item.getName());
            } catch (ClassCastException e) {
                e.printStackTrace();
                navigable.navigateUp();
            }
        }

        addFloatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle args = new Bundle();
                args.putInt("action", AreaAddFragment.ACTION_ADD);
                navigable.navigate(R.id.action_navigate_to_area_add,args);
            }
        });

        FBAreaRecyclerViewAdapter areaRecyclerViewAdapter = new FBAreaRecyclerViewAdapter(areaListRecyclerView,5,10,5);
        areaRecyclerViewAdapter.isRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean isRefreshing) {
                if(isRefreshing) {
                    areaListSwipeRefreshLayout.setRefreshing(true);
                }else{
                    areaListSwipeRefreshLayout.setRefreshing(false);
                    areaRecyclerViewAdapter.isRefreshing().removeObserver(this);
                }
            }
        });
        areaRecyclerViewAdapter.refresh(true,searchTextView.getText().toString());

        areaRecyclerViewAdapter.getClickedItem().observe(getViewLifecycleOwner(),new Observer<Area>() {
            @Override
            public void onChanged(Area area) {
                if(area != null) {
                    Bundle args = new Bundle();
                    if(action == ACTION_ADD_ITEM) {
                        args.putSerializable("area", area);
                        try {
                            Item item = (Item) arguments.getSerializable("item");
                            args.putSerializable("item",item);
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                            navigable.navigateUp();
                        }
                        args.putInt("action", AreaShowFragment.ACTION_ADD_ITEM);
                    }else if(action == ACTION_ADD_AREA){
                        args.putSerializable("addArea", area);
                        try {
                            Area ar = (Area) arguments.getSerializable("area");
                            args.putSerializable("area",ar);
                            args.putInt("action",AreaShowFragment.ACTION_ADD_AREA);
                        } catch (ClassCastException e) {
                            e.printStackTrace();
                            navigable.navigateUp();
                        }
                    }
                    else{
                        args.putSerializable("area", area);
                        args.putInt("action", AreaShowFragment.ACTION_SHOW);
                    }
                    navigable.navigate(R.id.action_navigate_to_area_show, args);
                }
            }
        });

//        areaRecyclerViewAdapter.getLongClickedItem().observe(getViewLifecycleOwner(), new Observer<Area>() {
//            @Override
//            public void onChanged(Area area) {
//
//                root.startActionMode(new ActionMode.Callback() {
//                    @Override
//                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//                        return false;
//                    }
//
//                    @Override
//                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//                        return false;
//                    }
//
//                    @Override
//                    public void onDestroyActionMode(ActionMode mode) {
//
//                    }
//                })
//            }
//        });

        areaListRecyclerView.setAdapter(areaRecyclerViewAdapter);

        areaListSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String search = searchTextView.getText().toString();
                areaRecyclerViewAdapter.refresh(true,search);
                areaRecyclerViewAdapter.isRefreshing().observe(getViewLifecycleOwner(), new Observer<Boolean>() {
                    @Override
                    public void onChanged(Boolean isRefreshing) {
                        if(!isRefreshing){
                            areaListSwipeRefreshLayout.setRefreshing(false);
                            areaRecyclerViewAdapter.isRefreshing().removeObserver(this);
                        }
                    }
                });
            }
        });

        searchTextView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                areaRecyclerViewAdapter.refresh(false,s.toString());
            }
        });
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            navigable = (Navigable) context;
        }catch (ClassCastException e){
            Log.e(this.getClass().toString(),e.getMessage());
            Log.e(this.getClass().toString(), Arrays.toString(e.getStackTrace()));
        }
    }
}