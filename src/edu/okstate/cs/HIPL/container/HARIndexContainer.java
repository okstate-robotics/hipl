/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.okstate.cs.hipl.container;

/**
 *
 * @author Sridhar
 */
public class HARIndexContainer {
    public int hash;
	public String index_output;
	public HARIndexContainer(int hash, String index_output) {
		this.hash = hash;
		this.index_output = index_output;
	}
}
