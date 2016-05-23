package cafe.adriel.nmsalphabet.ui;

import android.support.v4.app.Fragment;

import butterknife.Unbinder;

public abstract class BaseFragment extends Fragment {
    protected Unbinder unbinder;

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    protected abstract void init();
}