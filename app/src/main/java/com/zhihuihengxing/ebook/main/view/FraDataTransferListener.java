package com.zhihuihengxing.ebook.main.view;

import android.os.Bundle;

/**
 * 用于Fragment之间传递数据
 * Created by Administrator on 2017/3/2 0002.
 */
public interface FraDataTransferListener {
    void setBundle(Bundle data);
    Bundle getBundle();
}
