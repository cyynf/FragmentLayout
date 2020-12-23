package cpf.fragment.layout;

import androidx.fragment.app.FragmentManager;

/**
 * @author : cpf
 * @date : 12/23/20
 * email       : cpf4263@gmail.com
 * description :
 */
public class DelayTask implements Runnable {

    private FragmentManager fragmentManager;
    private FragmentLayout fragmentLayout;

    public DelayTask(FragmentLayout fragmentLayout, FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
        this.fragmentLayout = fragmentLayout;
    }

    @Override
    public void run() {
        fragmentLayout.loadFragmentImpl(fragmentManager);
        fragmentLayout = null;
        fragmentManager = null;
    }
}
