package cafe.adriel.nmsalphabet.util;

import android.content.Context;

import java.lang.reflect.Method;

import cafe.adriel.nmsalphabet.R;

public class ThemeUtil {
    enum Theme {
        Theme1,
        Theme2
    }

    public static Theme getCurrentTheme(Context context){
        try {
            Class<?> wrapper = Context.class;
            Method method = wrapper.getMethod("getThemeResId");
            method.setAccessible(true);
            int themeId = (Integer) method.invoke(context);
            switch (themeId){
                case R.style.AppTheme:
                    return Theme.Theme1;
//                case R.style.AppTheme2:
//                    return Theme.Theme2;
                default:
                    return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}