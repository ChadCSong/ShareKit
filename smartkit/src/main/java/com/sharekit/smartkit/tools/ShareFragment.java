//package com.sharekit.smartkit.tools;
//
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.Animation;
//import android.view.animation.AnimationSet;
//import android.view.animation.TranslateAnimation;
//
//import com.sharekit.smartkit.R;
//
///**
// * @author Chad
// * @title com.sharekit.smartkit.tools
// * @description
// * @modifier
// * @date
// * @since 15/1/21 下午2:16
// */
//public class ShareFragment extends Fragment{
//    View rootView;
//    @Override
//    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
//        return getPushInAnimation(enter);
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//    }
//
//    public void showPanel(FragmentManager fragmentManager){
//        fragmentManager.beginTransaction().add(new ShareFragment(),"SharePanel").commit();
//    }
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        rootView= inflater.from(getActivity()).inflate(R.layout.share_fragment,null);
//        return super.onCreateView(inflater, container, savedInstanceState);
//
//    }
//    private AnimationSet getPushInAnimation(boolean enter) {
//        if(enter) {
//            AnimationSet set = new AnimationSet(true);
//            Animation animation = new AlphaAnimation(0.0f, 1.0f);
//            animation.setDuration(500);
//            set.addAnimation(animation);
//
//            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.0f,
//                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                    -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//            animation.setDuration(500);
//            set.addAnimation(animation);
//            return set;
//        }else {
//            AnimationSet set = new AnimationSet(true);
//            Animation animation = new AlphaAnimation(1.0f, 0.0f);
//            animation.setDuration(500);
//            set.addAnimation(animation);
//
//            animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, -0.0f,
//                    Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                    0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
//            animation.setDuration(500);
//            set.addAnimation(animation);
//            return set;
//        }
//    }
//}
