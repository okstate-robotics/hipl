package edu.okstate.cs.hipl.util;


import java.util.ArrayList;
import java.util.List;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Sridhar
 */
public class Random {
    static List x=new ArrayList<Integer>();
    
    public static int random(){
        int y;
        do{
            y=(int)(Math.random() * Integer.MAX_VALUE);
        }while(x.contains(y));
        x.add(y);
        return y;
    }
}
