/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.libres.aplicacioneslibres.aplicacion;

import com.libres.aplicacioneslibres.interfaces.Bienvenida;

/**
 *
 * @author vengatus
 */
public class App {
    
    static String m1(String  s){
        
        return new String()+s;
        
    }
      
    public static void main(String args[]) {

        
        new Bienvenida().setVisible(true);
        
       
    }
}
