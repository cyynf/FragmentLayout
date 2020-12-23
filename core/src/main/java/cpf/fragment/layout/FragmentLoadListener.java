package cpf.fragment.layout;

import androidx.fragment.app.Fragment;

/**
 * @author : cpf
 * @date : 12/22/20
 * email       : cpf4263@gmail.com
 * description : 延迟加载fragment时回调
 */
public interface FragmentLoadListener {
    void onFragmentLoad(FragmentLayout view, Fragment fragment);
}
