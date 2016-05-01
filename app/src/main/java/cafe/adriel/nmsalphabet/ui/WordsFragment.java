package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.ramotion.foldingcell.FoldingCell;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;
import com.tuesda.walker.circlerefresh.CircleRefreshLayout;
import com.tumblr.bookends.Bookends;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.ui.adapter.HomeAdapter;
import cafe.adriel.nmsalphabet.ui.adapter.ProfileAdapter;
import cafe.adriel.nmsalphabet.ui.view.EndlessRecyclerOnScrollListener;
import cafe.adriel.nmsalphabet.ui.view.RefreshLayout;
import cafe.adriel.nmsalphabet.util.SocialUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;
import mehdi.sakout.dynamicbox.DynamicBox;

public class WordsFragment extends BaseFragment {
    private static final String STATE_LOADING           = "loading";
    private static final String STATE_EMPTY             = "empty";
    private static final String STATE_NO_INTERNET       = "noInternet";
    private static final String STATE_REQUIRE_SIGN_IN   = "requireSignIn";

    public enum Type {
        HOME,
        PROFILE
    }

    private Type type;
    private Bookends<HomeAdapter> homeAdapter;
    private Bookends<ProfileAdapter> profileAdapter;
    private DynamicBox stateBox;

    @BindView(R.id.refresh_layout)
    RefreshLayout refreshLayout;
    @BindView(R.id.words)
    RecyclerView wordsView;
    @BindView(R.id.header_home_layout)
    LinearLayout headerHomeLayout;
    @BindView(R.id.header_profile)
    RelativeLayout headerProfileLayout;
    @BindView(R.id.user_image)
    ImageView userImageView;
    @BindView(R.id.user_name)
    TextView userNameView;
    @BindView(R.id.settings)
    Button settingsView;
    @BindView(R.id.search_layout)
    RelativeLayout searchLayout;
    @BindView(R.id.search)
    EditText searchView;
    @BindView(R.id.search_icon)
    TextView searchIconView;
    @BindView(R.id.search_clear)
    TextView searchClearView;
    @BindView(R.id.races)
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
        unbinder = ButterKnife.bind(this, rootView);
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
            headerHomeLayout.setVisibility(View.GONE);
            headerProfileLayout.setVisibility(View.VISIBLE);
            headerProfileLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (headerProfileLayout != null) {
                        headerProfileLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    initState();
                    updateList();
                }
            });
            settingsView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), SettingsActivity.class));
                }
            });
            userNameView.setText(App.isSignedIn() ? App.getUser().getName() : getString(R.string.unknown_explorer));
            Glide.with(getContext())
                    .load(App.isSignedIn() ? SocialUtil.getUserImageUrl() : R.drawable.default_user_image)
                    .asBitmap()
                    .centerCrop()
                    .into(new BitmapImageViewTarget(userImageView) {
                @Override
                protected void setResource(Bitmap resource) {
                    RoundedBitmapDrawable circularBitmapDrawable = RoundedBitmapDrawableFactory.create(getContext().getResources(), resource);
                    circularBitmapDrawable.setCircular(true);
                    userImageView.setImageDrawable(circularBitmapDrawable);
                }
            });
        } else {
            headerProfileLayout.setVisibility(View.GONE);
            headerHomeLayout.setVisibility(View.VISIBLE);
            headerHomeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    if (headerHomeLayout != null) {
                        headerHomeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    }
                    initState();
                    updateList();
                }
            });

            racesView.setBackground(ThemeUtil.getHeaderControlDrawable(getContext()));
            racesView.setTextColor(Color.WHITE);
            racesView.setArrowColor(Color.WHITE);
            racesView.setDropdownColor(ThemeUtil.getPrimaryDarkColor(getContext()));
            racesView.setItems(getString(R.string.all_alien_races), "Korvax");
            racesView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
                @Override
                public void onItemSelected(MaterialSpinner view, int position, long id, String item) {

                }
            });

            searchLayout.setBackground(ThemeUtil.getHeaderControlDrawable(getContext()));
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

    private void initState(){
        View loadingState = LayoutInflater.from(getContext()).inflate(R.layout.state_loading, null, false);
        View emptyState = LayoutInflater.from(getContext()).inflate(R.layout.state_empty, null, false);
        View noInternetState = LayoutInflater.from(getContext()).inflate(R.layout.state_no_internet, null, false);
        View requireSignInState = LayoutInflater.from(getContext()).inflate(R.layout.state_require_sign_in, null, false);

        if(type == Type.PROFILE){
            loadingState.setPadding(0, headerProfileLayout.getHeight(), 0, 0);
            emptyState.setPadding(0, headerProfileLayout.getHeight(), 0, 0);
            noInternetState.setPadding(0, headerProfileLayout.getHeight(), 0, 0);
            requireSignInState.setPadding(0, headerProfileLayout.getHeight(), 0, 0);
        } else {
            loadingState.setPadding(0, headerHomeLayout.getHeight(), 0, 0);
            emptyState.setPadding(0, headerHomeLayout.getHeight(), 0, 0);
            noInternetState.setPadding(0, headerHomeLayout.getHeight(), 0, 0);
            requireSignInState.setPadding(0, headerHomeLayout.getHeight(), 0, 0);
        }

        stateBox = new DynamicBox(getContext(), wordsView);
        stateBox.addCustomView(loadingState, STATE_LOADING);
        stateBox.addCustomView(emptyState, STATE_EMPTY);
        stateBox.addCustomView(noInternetState, STATE_NO_INTERNET);
        stateBox.addCustomView(requireSignInState, STATE_REQUIRE_SIGN_IN);
//        stateBox.showCustomView(STATE_LOADING);
    }

    private void initList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        EndlessRecyclerOnScrollListener infiniteScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                setLoadingList(true);
                Util.asyncCall(3000, new Runnable() {
                    @Override
                    public void run() {
                        setLoadingList(false);
                    }
                });
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
        FoldingCell listHeaderHomeLayout = (FoldingCell) LayoutInflater.from(getContext()).inflate(R.layout.list_header_home, null);
        listHeaderHomeLayout.getChildAt(0).setMinimumHeight(headerHomeLayout.getHeight());
        View listHeaderProfileLayout = LayoutInflater.from(getContext()).inflate(R.layout.list_header_profile, null);
        listHeaderProfileLayout.setPadding(0, headerProfileLayout.getHeight(), 0, 0);

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
                homeAdapter = new Bookends<>(new HomeAdapter(getContext(), l));
                homeAdapter.addHeader(listHeaderHomeLayout);
                homeAdapter.addFooter(LayoutInflater.from(getContext()).inflate(R.layout.list_footer_words, null));
                homeAdapter.setFooterVisibility(false);
                wordsView.swapAdapter(homeAdapter, true);
                break;
            case PROFILE:
                profileAdapter = new Bookends<>(new ProfileAdapter(getContext(), l));
                profileAdapter.addHeader(listHeaderProfileLayout);
                profileAdapter.addFooter(LayoutInflater.from(getContext()).inflate(R.layout.list_footer_words, null));
                profileAdapter.setFooterVisibility(false);
                wordsView.swapAdapter(profileAdapter, true);
                break;
        }
    }

    private void searchWord(String word){

    }

    private void setLoadingList(boolean loading){
        if(type == Type.PROFILE){
            profileAdapter.setFooterVisibility(loading);
        } else {
            homeAdapter.setFooterVisibility(loading);
        }
    }
}