/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.parso.barebones;

import java.io.Serializable;

/**
 *
 * @author sam
 */
public class TestObject implements Serializable {
    private String blah;
    
    public void setBlah(String blah) {
        this.blah = blah;
    }
    
    public String getBlah() {
        return blah;
    }
}
