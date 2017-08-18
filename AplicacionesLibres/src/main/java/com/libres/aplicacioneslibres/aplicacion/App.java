/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.libres.aplicacioneslibres.aplicacion;

import com.libres.aplicacioneslibres.interfaces.Bienvenida;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

/**
 *
 * @author vengatus
 */
public class App {
    
    static String m1(String  s){
        
        return new String()+s;
        
    }
      
    public static void main(String args[]) {

         try {
            String alum= "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel";
            String graph= "com.jtattoo.plaf.graphite.GraphiteLookAndFeel";
            String luna= "com.jtattoo.plaf.luna.LunaLookAndFeel";
            String acryl= "com.jtattoo.plaf.acryl.AcrylLookAndFeel";
            String hifi= "com.jtattoo.plaf.hifi.HiFiLookAndFeel";
            String fast= "com.jtattoo.plaf.fast.FastLookAndFeel";
            String mcwin= "com.jtattoo.plaf.mcwin.McWinLookAndFeel";
            String mint= "com.jtattoo.plaf.mint.MintLookAndFeel";
            String smart= "com.jtattoo.plaf.smart.SmartLookAndFeel";
            String texture= "com.jtattoo.plaf.texture.TextureLookAndFeel";
            String bernstein= "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel";
            
            UIManager.setLookAndFeel(alum);
            
            /* Create and display the form */
            java.awt.EventQueue.invokeLater(new Runnable() {
                public void run() {
                    new Bienvenida().setVisible(true);
                }
            });
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException ex) {
            Logger.getLogger(Bienvenida.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }
}
