package cpf.fragment.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.widget.FrameLayout;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * @author : cpf
 * @date : 12/22/20
 * email       : cpf4263@gmail.com
 * description : fragment容器
 */
public class FragmentLayout extends FrameLayout implements Runnable, LifecycleEventObserver {

    private boolean isLoad;
    // 自动加载，默认不启用自动加载，建议只在activity使用
    private boolean isAutoLoad;
    // 自动加载延迟，单位毫秒，默认不自动加载
    private int delayDuration;
    // fragment类名
    private String name;
    // fragment tag
    private String tag;
    private Context context;
    private Handler handler = new Handler(Looper.getMainLooper());
    private FragmentManager fragmentManager;
    private FragmentLoadListener listener;

    public FragmentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.FragmentLayout);
        isAutoLoad = typedArray.getBoolean(R.styleable.FragmentLayout_autoLoad, false);
        delayDuration = typedArray.getInt(R.styleable.FragmentLayout_delayDuration, 0);
        name = typedArray.getString(R.styleable.FragmentLayout_android_name);
        tag = typedArray.getString(R.styleable.FragmentLayout_android_tag);
        typedArray.recycle();
        init();
    }

    private void init() {
        isLoad = false;
        // 当未设置tag时默认tag为id，请确保id唯一，否则请设置唯一tag
        if (tag == null) {
            tag = String.valueOf(getId());
        }
        if (isAutoLoad && context instanceof FragmentActivity) {
            fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            if (delayDuration > 0) {
                lazyLoadFragment((LifecycleOwner) context);
            } else {
                handler.post(this);
            }
        }
    }

    @NonNull
    private synchronized Fragment loadFragment() {
        handler.removeCallbacks(this);
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            isLoad = true;
            return fragment;
        }
        fragment = fragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), name);
        fragmentManager.beginTransaction()
                .add(getId(), fragment, tag)
                .commitNowAllowingStateLoss();
        fragmentManager = null;
        if (listener != null) {
            listener.onFragmentLoad(this, fragment);
        }
        isLoad = true;
        return fragment;
    }

    private void lazyLoadFragment(LifecycleOwner owner) {
        owner.getLifecycle().removeObserver(this);
        owner.getLifecycle().addObserver(this);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @MainThread
    public <T extends Fragment> T loadFragment(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        Fragment mFragment = loadFragment();
        return (T) mFragment;
    }

    /**
     * 加载fragment
     *
     * @param activity      用于获取fragmentManager
     * @param delayDuration 延迟加载时间ms
     */
    @MainThread
    public void loadFragmentDelayed(FragmentActivity activity, int delayDuration) {
        this.fragmentManager = activity.getSupportFragmentManager();
        this.delayDuration = delayDuration;
        lazyLoadFragment(activity);
    }

    /**
     * 加载fragment
     *
     * @param fragment      用于获取fragmentManager
     * @param delayDuration 延迟加载时间ms
     */
    @MainThread
    public void loadFragmentDelayed(Fragment fragment, int delayDuration) {
        this.fragmentManager = fragment.getChildFragmentManager();
        this.delayDuration = delayDuration;
        lazyLoadFragment(fragment);
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_CREATE:
                handler.postDelayed(this, delayDuration);
                break;
            case ON_DESTROY:
                handler.removeCallbacks(this);
                fragmentManager = null;
                source.getLifecycle().removeObserver(this);
                break;
        }
    }

    @Override
    public void run() {
        loadFragment();
    }

    public FragmentLoadListener getListener() {
        return listener;
    }

    public void setListener(FragmentLoadListener listener) {
        this.listener = listener;
    }

    public boolean isLoad() {
        return isLoad;
    }
}
