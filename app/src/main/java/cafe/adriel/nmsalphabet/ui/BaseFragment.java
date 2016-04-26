package cafe.adriel.nmsalphabet.ui;

import android.support.v4.app.Fragment;

import butterknife.ButterKnife;

public abstract class BaseFragment extends Fragment {

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    protected abstract void init();
}