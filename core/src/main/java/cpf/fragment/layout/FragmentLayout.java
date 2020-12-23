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
public class FragmentLayout extends FrameLayout implements LifecycleEventObserver {

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
    private AttributeSet attrs;
    private Handler handler = new Handler(Looper.getMainLooper());
    private FragmentLoadListener listener;
    private DelayTask delayTask;

    public FragmentLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        this.attrs = attrs;
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
            FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            if (delayDuration > 0) {
                lazyLoadFragment((LifecycleOwner) context, fragmentManager);
            } else {
                handler.post(new DelayTask(this, fragmentManager));
            }
        }
    }

    @NonNull
    synchronized Fragment loadFragmentImpl(FragmentManager fragmentManager) {
        Fragment fragment = fragmentManager.findFragmentByTag(tag);
        if (fragment != null) {
            isLoad = true;
            return fragment;
        }
        fragment = fragmentManager.getFragmentFactory().instantiate(context.getClassLoader(), name);
        fragment.onInflate(context, attrs, null);
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .add(getId(), fragment, tag)
                .commitNowAllowingStateLoss();
        if (listener != null) {
            listener.onFragmentLoad(this, fragment);
        }
        isLoad = true;
        return fragment;
    }

    private void lazyLoadFragment(LifecycleOwner owner, FragmentManager fragmentManager) {
        delayTask = new DelayTask(this, fragmentManager);
        owner.getLifecycle().removeObserver(this);
        owner.getLifecycle().addObserver(this);
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @MainThread
    public <T extends Fragment> T loadFragment(FragmentManager fragmentManager) {
        Fragment mFragment = loadFragmentImpl(fragmentManager);
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
        this.delayDuration = delayDuration;
        lazyLoadFragment(activity, activity.getSupportFragmentManager());
    }

    /**
     * 加载fragment
     *
     * @param fragment      用于获取fragmentManager
     * @param delayDuration 延迟加载时间ms
     */
    @MainThread
    public void loadFragmentDelayed(Fragment fragment, int delayDuration) {
        this.delayDuration = delayDuration;
        lazyLoadFragment(fragment, fragment.getChildFragmentManager());
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        switch (event) {
            case ON_CREATE:
                handler.postDelayed(delayTask, delayDuration);
                break;
            case ON_DESTROY:
                handler.removeCallbacks(delayTask);
                delayTask = null;
                source.getLifecycle().removeObserver(this);
                break;
        }
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
