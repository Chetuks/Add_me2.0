package neutrinos.addme.Interface;

import neutrinos.addme.activity.ScrollViewExt;

public interface ScrollViewListener {
    void onScrollChanged(ScrollViewExt scrollView,
                         int x, int y, int oldx, int oldy);
}
