package com.example.demonickrace.fragment_example;

/**
 * Created by demonickrace on 15/6/5.
 */
import  android.app.Fragment;
import  android.os.Bundle;
import  android.view.LayoutInflater;
import  android.view.View;
import  android.view.ViewGroup;

public  class  ContentFragment  extends  Fragment
{

    @Override
    public  View onCreateView(LayoutInflater inflater, ViewGroup container,
                              Bundle savedInstanceState)
    {
        return  inflater.inflate(R.layout.content, container,  false );
    }

}