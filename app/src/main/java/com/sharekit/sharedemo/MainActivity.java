package com.sharekit.sharedemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.sharekit.smartkit.tools.ShareFragment;
import com.sharekit.smartkit.tools.ShareTool;

public class MainActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ShareTool shareTool
        Button button = (Button)findViewById(R.id.share_button);
        if(ShareTool.getSinaWeiboShareApi(MainActivity.this, getAppkey()).registAppToSinaWeibo()) {
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
//                    SinaWeiboParams sinaWeiboParams = new SinaWeiboParams();
//                    sinaWeiboParams.setContent("fenxiang");
//                    sinaWeiboParams.setActionUrl("http://chadsong.tk");
//                    ShareTool.getSinaWeiboShareApi(MainActivity.this, getAppkey()).shareSinaWeibo(sinaWeiboParams, SinaWeibo.TYPE_TEXT);
                    ShareFragment shareFragment = new ShareFragment();

                }
            });
        }

    }

    private String getAppkey(){
//        return "967724435";
        return "2045436852";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
