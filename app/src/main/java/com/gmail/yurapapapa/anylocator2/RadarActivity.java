package com.gmail.yurapapapa.anylocator2;

import android.support.v4.app.Fragment;

/**
 * Created by yurap_000 on 004 04 сен.
 */
public class RadarActivity extends SingleFragmentActivity{
    @Override
    protected Fragment createFragment() {
        return new RadarFragment();
    }
}
