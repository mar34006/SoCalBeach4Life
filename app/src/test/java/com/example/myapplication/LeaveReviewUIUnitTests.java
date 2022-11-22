package com.example.myapplication;

import static org.junit.Assert.assertEquals;

import android.view.View;
import android.widget.TextView;

import org.junit.Test;

public class LeaveReviewUIUnitTests {

    Integer rating = 1;
    Boolean anonymous = true;

    // Functions taken from LeaveReviewActivity
    // The only think changed is the commented parts

    private void onClickMinus(){
        if (rating != 1){
            rating -= 1;
            //TextView rating_view = findViewById(R.id.rating);
            //rating_view.setText(Integer.toString(rating));
        }
    }

    private void onClickPlus(){
        if (rating != 5){
            rating += 1;
            //TextView rating_view = findViewById(R.id.rating);
            //rating_view.setText(Integer.toString(rating));
        }
    }

    private void onClickAnon(){
        //TextView tv = (TextView) view;
        if(anonymous){
            anonymous = false;
            //tv.setText("No");
        }
        else{
            anonymous = true;
            //tv.setText("Yes");
        }
    }

    public int get_rating(){ return rating; }
    public Boolean get_anon(){ return anonymous; }

    @Test
    public void rating_is_correct_1() {
        onClickPlus();
        assertEquals(2, get_rating());
    }

    @Test
    public void rating_is_correct_2() {
        onClickPlus();
        onClickMinus();
        assertEquals(1, get_rating());
    }

    @Test
    public void rating_is_correct_3() {
        onClickPlus();
        onClickMinus();
        onClickMinus();
        assertEquals(1, get_rating());
    }

    @Test
    public void rating_is_correct_4() {
        onClickPlus();
        onClickPlus();
        onClickPlus();
        onClickPlus();
        onClickPlus();
        onClickPlus(); //6
        assertEquals(5, get_rating());
    }

    @Test
    public void rating_is_correct_5() {
        onClickMinus();
        onClickMinus();
        assertEquals(1, get_rating());
    }

    @Test
    public void anon_is_correct_1() {
        onClickAnon();
        assertEquals(false, get_anon());
    }

    @Test
    public void anon_is_correct_2() {
        onClickAnon();
        onClickAnon();
        assertEquals(true, get_anon());
    }

    @Test
    public void anon_is_correct_3() {
        onClickPlus();
        onClickAnon();
        assertEquals(2, get_rating());
        onClickAnon();
        assertEquals(true, get_anon());
    }

}
