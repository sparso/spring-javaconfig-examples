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
import ch.qos.logback.core.html.CssBuilder;

public class GmailCssBuilder implements CssBuilder {

    public static final String CSS = 
            "<style type=\"text/css\">"
            + "table, th, td { "
            +     "border: 1px solid black;"
            +  "}"
            + "</style>";
    
    @Override
    public void addCss(StringBuilder sb) {
        sb.append(CSS);
    }

}
