package cafe.adriel.nmsalphabet.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.ramotion.foldingcell.FoldingCell;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;
import com.tumblr.bookends.Bookends;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.Util;
import cafe.adriel.nmsalphabet.ui.adapter.HomeAdapter;
import cafe.adriel.nmsalphabet.ui.adapter.ProfileAdapter;
import cafe.adriel.nmsalphabet.ui.view.EndlessRecyclerOnScrollListener;
import cafe.adriel.nmsalphabet.ui.view.RefreshLayout;

public class WordsFragment extends BaseFragment {
    public enum Type {
        HOME,
        PROFILE
    }

    private Type type;

    @Bind(R.id.refresh_layout)
    RefreshLayout refreshLayout;
    @Bind(R.id.words)
    RecyclerView wordsView;
    @Bind(R.id.control_layout)
    LinearLayout controlLayout;
    @Bind(R.id.search)
    EditText searchView;
    @Bind(R.id.search_icon)
    TextView searchIconView;
    @Bind(R.id.search_clear)
    TextView searchClearView;
    @Bind(R.id.races)
    MaterialSpinner racesView;

    public static WordsFragment newInstance(Type type) {
        Bundle args = new Bundle();
        args.putSerializable(Constant.EXTRA_TYPE, type);
        WordsFragment frag = new WordsFragment();
        frag.setArguments(args);
        return frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = (Type) getArguments().getSerializable(Constant.EXTRA_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_words, container, false);
        ButterKnife.bind(this, rootView);
        init();
        return rootView;
    }

    @Override
    protected void init(){
        initControls();
        initList();

        switch (type){
            case HOME:
                wordsView.setAdapter(new HomeAdapter(getContext(), new ArrayList<String>()));
                break;
            case PROFILE:
                wordsView.setAdapter(new ProfileAdapter(getContext(), new ArrayList<String>()));
                break;
        }
    }

    private void initControls(){
        controlLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (controlLayout != null) {
                    controlLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                updateList();
            }
        });
        refreshLayout.setOnRefreshListener(new CircleRefreshLayout.OnCircleRefreshListener() {
            @Override
            public void refreshing() {
                Util.asyncCall(3000, new Runnable() {
                    @Override
                    public void run() {
                        refreshLayout.finishRefreshing();
                    }
                });
            }
            @Override
            public void completeRefresh() {

            }
        });
        if(type == Type.PROFILE){
            controlLayout.setVisibility(View.GONE);
            refreshLayout.setBackgroundColor(getResources().getColor(R.color.bg_gray));
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setAniForeColor(getResources().getColor(R.color.bg_gray));
                }
            });
        } else {
            controlLayout.setVisibility(View.VISIBLE);
            refreshLayout.setBackgroundColor(getResources().getColor(R.color.home_controls));
            refreshLayout.post(new Runnable() {
                @Override
                public void run() {
                    refreshLayout.setAniForeColor(getResources().getColor(R.color.home_controls));
                }
            });

            racesView.setBackgroundResource(R.drawable.home_control);
            racesView.setTextColor(Color.WHITE);
            racesView.setArrowColor(Color.WHITE);
            racesView.setDropdownColor(getResources().getColor(R.color.colorPrimaryDark));
            racesView.setItems(getString(R.string.all_alien_races), "Korvax");
            racesView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                }
            });

            searchView.getViewTreeObserver().addOnGlobalFocusChangeListener(new ViewTreeObserver.OnGlobalFocusChangeListener() {
                @Override
                public void onGlobalFocusChanged(View oldFocus, View newFocus) {
                    if (searchView != null) {
                        searchView.getViewTreeObserver().removeOnGlobalFocusChangeListener(this);
                        searchView.clearFocus();
                    }
                }
            });
            searchView.setOnKeyListener(new View.OnKeyListener() {
                public boolean onKey(View v, int keyCode, KeyEvent event) {
                    if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                        if (searchView.getText().length() > 0) {
                            searchWord(searchView.getText().toString());
                            return true;
                        }
                    }
                    return false;
                }
            });
            searchView.addTextChangedListener(new TextWatcher() {
                @Override
                public void afterTextChanged(Editable s) {
                    searchClearView.setVisibility(s.length() == 0 ? View.INVISIBLE : View.VISIBLE);
                }

                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }
            });
            searchView.post(new Runnable() {
                @Override
                public void run() {
                    racesView.setHeight(searchClearView.getHeight());
                }
            });
            searchIconView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (searchView.getText().length() > 0) {
                        searchWord(searchView.getText().toString());
                    }
                }
            });
            searchClearView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    searchView.setText("");
                }
            });
        }
    }

    private void initList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        EndlessRecyclerOnScrollListener infiniteScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {

            }
        };
        RecyclerItemClickSupport.addTo(wordsView).setOnItemClickListener(new RecyclerItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                ((FoldingCell) v).toggle(false);
            }
        });

        wordsView.setLayoutManager(layoutManager);
        wordsView.addOnScrollListener(infiniteScrollListener);
        refreshLayout.setLayoutManager(layoutManager);
    }

    private void updateList(){
        FoldingCell homeHeaderLayout = (FoldingCell) LayoutInflater.from(getContext()).inflate(R.layout.home_header, null);
        homeHeaderLayout.getChildAt(0).setMinimumHeight(controlLayout.getHeight());
        List<String> l = new ArrayList<>();
        l.add("Sadipscing");
        l.add("Consetetur");
        l.add("Nonumy");
        l.add("Aliquyam");
        l.add("Voluptua");
        l.add("Takimata");
        l.add("Gubergren");
        l.add("Rebum");
        l.add("Est");

        switch (type){
            case HOME:
                Bookends<HomeAdapter> homeAdapter = new Bookends<>(new HomeAdapter(getContext(), l));
                homeAdapter.addHeader(homeHeaderLayout);
                wordsView.swapAdapter(homeAdapter, true);
                break;
            case PROFILE:
                Bookends<ProfileAdapter> profileAdapter = new Bookends<>(new ProfileAdapter(getContext(), l));
                profileAdapter.addHeader(LayoutInflater.from(getContext()).inflate(R.layout.profile_header, null));
                wordsView.swapAdapter(profileAdapter, true);
                break;
        }
    }

    private void searchWord(String word){
        Log.e("SEARCH", word+"");
    }
}