package cafe.adriel.nmsalphabet.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.github.ybq.android.spinkit.SpinKitView;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.ramotion.foldingcell.FoldingCell;
import com.rohit.recycleritemclicksupport.RecyclerItemClickSupport;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import cafe.adriel.nmsalphabet.App;
import cafe.adriel.nmsalphabet.Constant;
import cafe.adriel.nmsalphabet.R;
import cafe.adriel.nmsalphabet.event.TranslationUpdatedEvent;
import cafe.adriel.nmsalphabet.event.UpdateStateEvent;
import cafe.adriel.nmsalphabet.model.AlienRace;
import cafe.adriel.nmsalphabet.model.AlienWord;
import cafe.adriel.nmsalphabet.ui.adapter.HomeAdapter;
import cafe.adriel.nmsalphabet.ui.adapter.ProfileAdapter;
import cafe.adriel.nmsalphabet.ui.view.EndlessRecyclerOnScrollListener;
import cafe.adriel.nmsalphabet.ui.view.SwipeRefreshLayoutToggleScrollListener;
import cafe.adriel.nmsalphabet.util.DbUtil;
import cafe.adriel.nmsalphabet.util.SocialUtil;
import cafe.adriel.nmsalphabet.util.ThemeUtil;
import cafe.adriel.nmsalphabet.util.Util;
import mehdi.sakout.dynamicbox.DynamicBox;

public class WordsFragment extends BaseFragment {

    public enum Type {
        HOME,
        PROFILE
    }

    private Type type;
    private List<AlienWord> words;
    private AlienRace selectedRace;
    private HomeAdapter homeAdapter;
    private ProfileAdapter profileAdapter;
    private EndlessRecyclerOnScrollListener infiniteScrollListener;
    private DynamicBox viewState;

    @BindView(R.id.refresh_layout)
    SwipeRefreshLayout refreshLayout;
    @BindView(R.id.words)
    RecyclerView wordsView;
    @BindView(R.id.header_home_layout)
    LinearLayout headerHomeLayout;
    @BindView(R.id.header_profile_layout)
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
    @BindView(R.id.loading)
    SpinKitView loadingView;

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
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(sticky = true)
    public void onEvent(TranslationUpdatedEvent event) {
        if (type != null && type == Type.PROFILE) {
            EventBus.getDefault().removeStickyEvent(TranslationUpdatedEvent.class);
            profileAdapter.updateWordAndTranslations(event.word, event.translations);
        }
    }

    @Subscribe(sticky = true)
    public void onEvent(UpdateStateEvent event) {
        if (viewState != null && type != null && type == Type.PROFILE) {
            EventBus.getDefault().removeStickyEvent(UpdateStateEvent.class);
            if(!App.isSignedIn()){
                viewState.showCustomView(Constant.STATE_REQUIRE_SIGN_IN);
            } else if(Util.isEmpty(words)){
                viewState.showCustomView(Constant.STATE_EMPTY);
            } else if(!Util.isConnected(getContext())){
                viewState.showCustomView(Constant.STATE_NO_INTERNET);
            } else {
                viewState.hideAll();
            }
        }
    }

    @Override
    protected void init(){
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshWords();
            }
        });
        switch (type){
            case HOME:
                initHomeControls();
                break;
            case PROFILE:
                initProfileControls();
                break;
        }
        initList();
    }

    private void initHomeControls(){
        List<String> races = DbUtil.getRacesName();
        races.add(0, getString(R.string.all_alien_races));

        headerProfileLayout.setVisibility(View.GONE);
        headerHomeLayout.setVisibility(View.VISIBLE);
        headerHomeLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (headerHomeLayout != null) {
                    headerHomeLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                updateRefreshLayoutMarginTop();
                initState();
                updateWords(0);
            }
        });

        racesView.setBackground(ThemeUtil.getHeaderControlDrawable(getContext()));
        racesView.setTextColor(Color.WHITE);
        racesView.setArrowColor(Color.WHITE);
        racesView.setBackgroundColor(ThemeUtil.getPrimaryDarkColor(getContext()));
        racesView.setItems(races);
        racesView.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener<String>() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, String item) {
                selectedRace = DbUtil.getRaceByName(item);
                updateWords(0);
            }
        });

        searchLayout.setBackground(ThemeUtil.getHeaderControlDrawable(getContext()));
        searchView.setFilters(new InputFilter[] { Util.getWordInputFilter() });
        searchView.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                    updateWords(0);
                    return true;
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
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

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
                updateWords(0);
            }
        });
        searchClearView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setText("");
                updateWords(0);
            }
        });
    }

    private void initProfileControls(){
        headerHomeLayout.setVisibility(View.GONE);
        headerProfileLayout.setVisibility(View.VISIBLE);
        headerProfileLayout.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if (headerProfileLayout != null) {
                    headerProfileLayout.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                }
                updateRefreshLayoutMarginTop();
                initState();
                updateWords(0);
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
    }

    private void initState(){
        View emptyState = LayoutInflater.from(getContext()).inflate(R.layout.state_words_empty, null, false);
        View noInternetState = LayoutInflater.from(getContext()).inflate(R.layout.state_words_no_internet, null, false);
        View requireSignInState = LayoutInflater.from(getContext()).inflate(R.layout.state_words_require_sign_in, null, false);

        View.OnClickListener refreshListener = new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 refreshWords();
             }
        };
        View.OnClickListener signInListener = new View.OnClickListener() {
             @Override
             public void onClick(View v) {
                 Util.getSettings(getContext()).edit()
                         .putBoolean(Constant.SETTINGS_HAS_SIGNED_IN, false)
                         .commit();
                 getActivity().finish();
                 startActivity(new Intent(getContext(), SplashActivity.class));
             }
        };

        emptyState.findViewById(R.id.refresh).setOnClickListener(refreshListener);
        noInternetState.findViewById(R.id.refresh).setOnClickListener(refreshListener);
        requireSignInState.findViewById(R.id.sign_in).setOnClickListener(signInListener);

        viewState = new DynamicBox(getContext(), wordsView);
        viewState.addCustomView(emptyState, Constant.STATE_EMPTY);
        viewState.addCustomView(noInternetState, Constant.STATE_NO_INTERNET);
        viewState.addCustomView(requireSignInState, Constant.STATE_REQUIRE_SIGN_IN);
        if(type == Type.PROFILE && !App.isSignedIn()){
            viewState.showCustomView(Constant.STATE_REQUIRE_SIGN_IN);
        }
    }

    private void initList(){
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        infiniteScrollListener = new EndlessRecyclerOnScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int currentPage) {
                updateWords(currentPage);
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
        wordsView.addOnScrollListener(new SwipeRefreshLayoutToggleScrollListener(refreshLayout));
    }

    private void initAdapter(){
        if(wordsView != null) {
            switch (type) {
                case HOME:
                    homeAdapter = new HomeAdapter(getContext(), words);
                    wordsView.setAdapter(homeAdapter);
                    break;
                case PROFILE:
                    profileAdapter = new ProfileAdapter(getContext(), words);
                    wordsView.setAdapter(profileAdapter);
                    break;
            }
            wordsView.setMinimumHeight(refreshLayout.getHeight());
        }
    }

    private void updateWords(final int page){
        if(type == Type.PROFILE && !App.isSignedIn()) {
            viewState.showCustomView(Constant.STATE_REQUIRE_SIGN_IN);
            return;
        }
        Util.hideSoftKeyboard(getActivity());
        setLoadingList(true);
        switch (type) {
            case HOME:
                String word = searchView.getText().toString();
                DbUtil.getWords(word, selectedRace, page, new FindCallback<AlienWord>() {
                    @Override
                    public void done(List<AlienWord> objects, ParseException e) {
                        afterUpdateWords(page, objects, e);
                    }
                });
                break;
            case PROFILE:
                DbUtil.getWordsByUser(App.getUser(), page, new FindCallback<AlienWord>() {
                    @Override
                    public void done(List<AlienWord> objects, ParseException e) {
                        afterUpdateWords(page, objects, e);
                    }
                });
                break;
        }
    }

    private void afterUpdateWords(int page, List<AlienWord> newWords, ParseException e){
        if(page == 0){
            if(Util.isEmpty(newWords)){
                viewState.showCustomView(Constant.STATE_EMPTY);
            } else {
                words = newWords;
                initAdapter();
                viewState.hideAll();
            }
        } else {
            words.addAll(newWords);
            switch (type){
                case HOME:
                    homeAdapter.notifyDataSetChanged();
                    break;
                case PROFILE:
                    profileAdapter.notifyDataSetChanged();
                    break;
            }
            viewState.hideAll();
        }
        if(e != null){
            e.printStackTrace();
        }
        setLoadingList(false);
        refreshLayout.setRefreshing(false);
    }

    private void refreshWords(){
        resetWords();
        updateWords(0);
    }

    private void resetWords(){
        if(words != null) {
            words.clear();
        }
        if(infiniteScrollListener != null) {
            infiniteScrollListener.reset();
        }
        switch (type){
            case HOME:
                if(homeAdapter != null) {
                    homeAdapter.notifyDataSetChanged();
                }
                break;
            case PROFILE:
                if(profileAdapter != null) {
                    profileAdapter.notifyDataSetChanged();
                }
                break;
        }
    }

    private void setLoadingList(boolean loading){
        if(loadingView != null) {
            if(loading) {
                loadingView.setVisibility(View.VISIBLE);
            } else {
                Util.asyncCall(500, new Runnable() {
                    @Override
                    public void run() {
                        loadingView.setVisibility(View.GONE);
                    }
                });
            }
        }
    }

    private void updateRefreshLayoutMarginTop(){
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) refreshLayout.getLayoutParams();
        switch (type){
            case HOME:
                params.topMargin = headerHomeLayout.getHeight();
                break;
            case PROFILE:
                params.topMargin = headerProfileLayout.getHeight();
                break;
        }
        refreshLayout.setLayoutParams(params);
    }
}