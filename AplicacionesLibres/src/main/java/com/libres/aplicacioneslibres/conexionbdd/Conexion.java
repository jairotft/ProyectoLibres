/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.libres.aplicacioneslibres.conexionbdd;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
/**
 *
 * @author vengatus
 */
public class Conexion {

    Connection conexion;

    public Conexion() {
        String url="src/main/resources/Database/facturas.db";
        try {
            Class.forName("org.sqlite.JDBC");
            conexion = DriverManager.getConnection("jdbc:sqlite:"+url);

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e); 
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Conexion.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(null, ex);
        }

    }

    public ArrayList cargarEstablecimiento() {
        ArrayList n = new ArrayList();
        try {
            Statement comando = conexion.createStatement();
            ResultSet resultado = comando.executeQuery("SELECT nombre_establecimiento FROM establecimiento");
            while (resultado.next()) {
                n.add(resultado.getString("nombre_establecimiento"));
            }
            resultado.close();
            comando.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return n;
    }

    public ArrayList cargarAnios() {
        ArrayList n = new ArrayList();
        try {
            Statement comando = conexion.createStatement();
            ResultSet resultado = comando.executeQuery("SELECT * FROM gastosanualespersonales");
            while (resultado.next()) {
                n.add(resultado.getString("anio_gastos"));
            }
            resultado.close();
            comando.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return n;
    }

    public ArrayList cambiarDatosEstablecimiento(String est) {
        ArrayList n = new ArrayList();
        try {
            Statement comando = conexion.createStatement();
            ResultSet resultado = comando.executeQuery("SELECT id_establecimiento, direccion_establecimiento,telefono_establecimiento "
                    + "FROM establecimiento WHERE nombre_establecimiento='" + est + "'");
            while (resultado.next()) {
                n.add(resultado.getString("id_establecimiento"));
                n.add(resultado.getString("direccion_establecimiento"));
                if (resultado.getString("telefono_establecimiento") != null) {
                    n.add(resultado.getString("telefono_establecimiento"));
                }else{
                    n.add("");
                }
            }
            resultado.close();
            comando.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
        return n;
    }

//    public  String consultarMAXidDetalle() {
//            String num = "0";
//        try {
//            
//            Statement comando = conexion.createStatement();
//            String sql = "SELECT ID_DETALLE,MAX(ID_DETALLE) FROM DETALLE;";
//            
//            ResultSet resultado = comando.executeQuery(sql);
//                num = resultado.getString("ID_DETALLE");
//            resultado.close();
//            comando.close();
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(null, e.getMessage());
//        }
//        return num;
//    }
    
//    public  String consultar(String tabla) {
//            String n = "";
//        try {
//            Statement comando = conexion.createStatement();
//            String sql = "SELECT count(*) as count FROM " + tabla + ";";
//            ResultSet resultado = comando.executeQuery(sql);
//            while (resultado.next()) {
//                n = resultado.getString("count");
//            }
//            JOptionPane.showMessageDialog(null,n);
//            resultado.close();
//            comando.close();
//        } catch (SQLException e) {
//            JOptionPane.showMessageDialog(null,"Error en consultar()"+ e.getMessage());
//        }
//        return n;
//    }

    
    
    public String consultarEstablecimientoPor(String codigo){
         String n = "";
        try {
            Statement comando = conexion.createStatement();
            String sql = "SELECT ID_ESTABLECIMIENTO FROM ESTABLECIMIENTO WHERE "
                    + "ID_ESTABLECIMIENTO='" + codigo+"';";
            ResultSet resultado = comando.executeQuery(sql);
            while (resultado.next()) {
                n = resultado.getString("ID_ESTABLECIMIENTO");
            }
            JOptionPane.showMessageDialog(null,n);
            resultado.close();
            comando.close();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null,"Error en consultar()"+ e.getMessage());
        }
        return n;
    }
    
    //si encuentra el producto devuelve el tipo de Gasto o Familia del Producto!!
    public  String consultarProductoPor(String codigo) {
            String familia = "";
            
        try {
            
            Statement comando = conexion.createStatement();
            String sql = "select familia from producto where "
                    + "id_producto='"+codigo+"'";
            
            
            ResultSet resultado = comando.executeQuery(sql);
                familia = resultado.getString("familia");
                JOptionPane.showMessageDialog(null, resultado.getString("familia"));
            resultado.close();
            comando.close();
        } catch (SQLException e) {
            System.out.println(""+e.getMessage());
        }
        return familia;
    }
    
    public void insertar(String sql) {
        try {
            Statement comando = conexion.createStatement();
            comando.executeUpdate(sql);
            comando.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    
    public boolean verificar_factura(String id_factura) {
        boolean val = false;
        try {
            String sql="select *from factura where id_factura='"+id_factura+"'";
            Statement comando = conexion.createStatement();
            ResultSet resultado = comando.executeQuery(sql);
            val = resultado.next();
            resultado.close();
            comando.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return val;
    }
    
    
    public boolean verificar_usuario(String sql) {
        boolean val = false;
        try {
            Statement comando = conexion.createStatement();
            ResultSet resultado = comando.executeQuery(sql);
            val = resultado.next();
            resultado.close();
            comando.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return val;
    }

    public ArrayList ddl(String sql) {
        ArrayList salida = new ArrayList();
        
        try {
            Statement comando = conexion.createStatement();
            ResultSet resultado = comando.executeQuery(sql);
            ResultSetMetaData mt = resultado.getMetaData();
 
            
            while(resultado.next()) {
                for (int i = 1; i <= mt.getColumnCount(); i++) {
                    String elemento = resultado.getString(i);
                    if(elemento == null){
                        elemento = "";
                    }
                    //System.out.println(elemento);
                    salida.add(elemento);
                    
                }
                //System.out.println("Fila" + resultado.getRow());
                //System.out.println(mt.getColumnCount());
            }
            resultado.close();
            comando.close();
        } catch (SQLException e) {
            System.out.println("error; " +e.getMessage());
        }
        return salida;
    }

    public Connection getConn() {
        return conexion;
    }

}
