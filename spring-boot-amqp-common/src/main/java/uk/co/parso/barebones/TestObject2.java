/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package uk.co.parso.barebones;

/**
 *
 * @author sam
 */
public class TestObject2 {
    private TestObject obj;
    private Long obj2;
    private int obj3;

    public TestObject2() {
    }

    public TestObject2(TestObject obj, Long obj2, int obj3) {
        this.obj = obj;
        this.obj2 = obj2;
        this.obj3 = obj3;
    }
    
    public TestObject getObj() {
        return obj;
    }

    public void setObj(TestObject obj) {
        this.obj = obj;
    }

    public Long getObj2() {
        return obj2;
    }

    public void setObj2(Long obj2) {
        this.obj2 = obj2;
    }

    public int getObj3() {
        return obj3;
    }

    public void setObj3(int obj3) {
        this.obj3 = obj3;
    }
}
