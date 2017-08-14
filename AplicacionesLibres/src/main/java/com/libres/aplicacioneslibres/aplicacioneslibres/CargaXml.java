/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.libres.aplicacioneslibres.aplicacioneslibres;

import com.libres.aplicacioneslibres.interfaces.SeleccionarTipoGastoNegocios;
import com.libres.aplicacioneslibres.interfaces.SeleccionarTipoGastoPersonal;
import com.libres.aplicacioneslibres.conexionbdd.Conexion;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import org.jdom2.*;
import org.jdom2.input.SAXBuilder;
/**
 *
 * @author root
 */

public class CargaXml {
    
    @SuppressWarnings("null")
    public void cargarXml(String name, String cedulaCli, int anio, String tipo) {
        //Se crea un SAXBuilder para poder parsear el archivo
        SAXBuilder builder = new SAXBuilder();

        File xmlFile = new File(name);
            //Se crea el documento a traves del archivo
            Document document;
        try {
            
            document = (Document) builder.build(xmlFile);
            Conexion cp = new Conexion();
            //Hashmap para extraer datos de documento XML
            HashMap<String,String> infoEncabezado = new HashMap<>();
            HashMap<String,String> infoTributaria = new HashMap<>();
            HashMap<String,String> infoFactura = new HashMap<>();
            //informacion de la lista de productos 
            //guardar cada detalle( o info de Producto) en la lista infoDetalles
            ArrayList<HashMap> infoDetalles = new ArrayList<>();
            
                              
            Element rootNode; //Se obtiene la raiz 'tables'
            Element rootAutorizacion;//nodo raiz autorizacion
            Element rootComprobante;//nodo raiz Comprobante
            //Comprobante tiene 3 hijos, infoTributaria, infoFactura y infoDetalles
            Element rootInfoTributaria;//nodo raiz InfoTributaria
            Element rootInfoFactura;//nodo raiz infoFactura
            Element rootInfoDetalles;//nodo raiz infoDetalles
            Element rootInfoDetalle;//nodo raiz infoDetalle
            
             rootNode= document.getRootElement(); //Nodo raiz del documento XML
            //Encontrar el nodo autorizacion
                rootAutorizacion = encontrarNodo(rootNode, "autorizacion");
                
            // Datos Cabecera =============================================
            infoEncabezado.put("estado", nodoGetValor(rootAutorizacion,"estado"));
            infoEncabezado.put("ambiente", nodoGetValor(rootAutorizacion,"ambiente"));
            
            // Datos Comprobante =======================================
            
                //Si comprobante es hermano de autorizacion, entonces tomar rootNodo
                rootComprobante = encontrarNodo(rootNode,"comprobante");
                //Si no existe, buscar comprobante como hijo de rootAutorizacion
                if(rootComprobante==null)
                    rootComprobante = encontrarNodo(rootAutorizacion,"comprobante");
        
                //Lectura CDATA
            if (rootComprobante != null) {
                String ex = rootComprobante.getText();
                InputStream stream = new ByteArrayInputStream(ex.getBytes("UTF-8"));
                Document parse = builder.build(stream);
                rootComprobante = parse.getRootElement();//Obtener 
            } else {
                //Hay facturas sin cabecera autorizacions
                rootComprobante = rootNode;
            }
            
            //Comprobante tiene 3 hijos, Info Tributaria, Info Factura y Detalles

            // Info Tributaria=======================================================================
            rootInfoTributaria = (Element) encontrarNodo(rootComprobante, "infoTributaria");
            //Agregamos la Clave y su Valor de cada dato al hashmap infoTributaria
            infoTributaria.put("razonSocial", nodoGetValor(rootInfoTributaria,"razonSocial"));//nombre Establecimiento
            infoTributaria.put("dirMatriz", nodoGetValor(rootInfoTributaria,"dirMatriz"));
            infoTributaria.put("ruc", nodoGetValor(rootInfoTributaria,"ruc"));
            infoTributaria.put("estab", nodoGetValor(rootInfoTributaria,"estab"));
            infoTributaria.put("ptoEmi", nodoGetValor(rootInfoTributaria,"ptoEmi"));
            infoTributaria.put("secuencial", nodoGetValor(rootInfoTributaria,"secuencial"));
            
            //Info Factura==========================================================================
            rootInfoFactura = (Element) encontrarNodo(rootComprobante, "infoFactura");
            
            //GUARDAR FECHA EMISION EN EL FORMATO YY-MM-DD
            
            infoFactura.put("fechaEmision", miFormatoYYMMDD( nodoGetValor(rootInfoFactura,"fechaEmision")) );  
            infoFactura.put("razonSocialComprador", nodoGetValor(rootInfoFactura,"razonSocialComprador"));  
            infoFactura.put("identificacionComprador", nodoGetValor(rootInfoFactura,"identificacionComprador"));  
            infoFactura.put("totalSinImpuestos", nodoGetValor(rootInfoFactura,"totalSinImpuestos")); 
            infoFactura.put("valor", nodoGetValor(rootInfoFactura.getChild("totalConImpuestos"),"valor"));//Impuesto
            //TOTAL A PAGAR CON IMPUESTOS
            infoFactura.put("importeTotal", nodoGetValor(rootInfoFactura,"importeTotal"));
            
            //Info Detalles=============================================================================
            rootInfoDetalles = (Element) encontrarNodo(rootComprobante, "detalles");   //Detalles...
            List detalle = rootInfoDetalles.getChildren();  //extraido los hijos(detalle) en una lista auxiliar
            
            for (int j = 0; j < detalle.size(); j++) {
                rootInfoDetalle = (Element) detalle.get(j);
                // Detalle o informacion de Producto
                HashMap<String,String> infoDetalle=new HashMap<>();
                //Descripcion= Nombre del producto
                infoDetalle.put("codigoPrincipal", nodoGetValor(rootInfoDetalle,"codigoPrincipal"));
                infoDetalle.put("descripcion", nodoGetValor(rootInfoDetalle,"descripcion"));
                infoDetalle.put("cantidad", nodoGetValor(rootInfoDetalle,"cantidad"));
                infoDetalle.put("precioUnitario", nodoGetValor(rootInfoDetalle,"precioUnitario"));
                //PrecioTotalSinImpuesto = PRECIO_UNITARIO * UNIDADES
                infoDetalle.put("precioTotalSinImpuesto", nodoGetValor(rootInfoDetalle,"precioTotalSinImpuesto"));
                
                //agregamos el detalle a la lista de detalles SSI el Nombre_producto != Empty
                if (!infoDetalle.get("descripcion").equals("")) {
                    infoDetalles.add(infoDetalle);
                }    
            }
            
            //El numero de factura es : estab-ptoEmi-secuencial
            //numFact para validar si la factura ya ha sido ingresada
            String numFact = infoTributaria.get("estab") + "-" 
                    + infoTributaria.get("ptoEmi") + "-" 
                    + infoTributaria.get("secuencial");
            
            //            JOptionPane.showMessageDialog(null, infoEncabezado.values());
            //            JOptionPane.showMessageDialog(null, infoTributaria.values());
            //            JOptionPane.showMessageDialog(null, infoFactura.values());
            //            JOptionPane.showMessageDialog(null, infoDetalles.toString());
            
            //VALIDACIONES
            String opcion="";
            String ANIO=infoFactura.get("fechaEmision");
            if( ANIO.compareTo(anio+"") < 0 ) { //-1 si el Anio es diferente!! 
                opcion="problemaAnio";
                
            }else if( cedulaCli.equals(infoFactura.get("identificacionComprador") )==false){
            //si la cedula del usuario no es igual a la de la factura que quiere ingresar
                opcion="facturaDeOtroUsuario";
                
            }else if( cp.verificar_factura(numFact) ){ //si la factura ya fue ingresada!!
                opcion="facturaYaIngresada";
                
            }else if( tipo.equals("Personal") ==true ) { 
                opcion="interfazPersonal";
                
            }else if( tipo.equals("Negocio") == true ) {
                opcion="interfazNegocio";
                
            }
            
            //Para cada caso!!
            
            switch(opcion){
            
                case "problemaAnio":
                    JOptionPane.showMessageDialog(null, "El año de la factura no corresponde con el año seleccionado");
                    break;
                    
                case "facturaDeOtroUsuario":
                    JOptionPane.showMessageDialog(null, "Esta factura pertenece a otro usuario");
                    break;
                    
                case "facturaYaIngresada":
                    JOptionPane.showMessageDialog(null, "Esta factura ya fue ingresada!");
                    break;
                   
                case "interfazPersonal":
                    SeleccionarTipoGastoPersonal sP = new SeleccionarTipoGastoPersonal(cp,infoEncabezado, infoTributaria,infoFactura, infoDetalles);
                    sP.setVisible(true);
                
                    break;
                case "interfazNegocio":
//                    SeleccionarTipoGastoNegocios sN = new SeleccionarTipoGastoNegocios(infoEncabezado, infoTributaria,infoFactura, infoDetalles);
//                    sN.setVisible(true);
                    break;
            }
                
            } catch (JDOMException | IOException ex) {
                Logger.getLogger(CargaXml.class.getName()).log(Level.SEVERE, null, ex);
            }
    }
    
    //Metodo recursivo para encontrar un nodo del Documento XML
    public Element encontrarNodo(Element padre,String nombreNodo){
        //caso base si lo encuentra return Padre
        if(padre ==null) return null;
        if(padre.getName().equals(nombreNodo)){
            //JOptionPane.showMessageDialog(null, "padre "+padre);
            return padre;
        }else{
            //Si no lo encuentra, se genera una lista de sus hijos
            List hijos = padre.getChildren();
            
            Element hijo = null;//El hijo debe inicializarse
            for (int i=0; i<hijos.size();i++){//recorre los hijos de la lista
                hijo = (Element)hijos.get(i);//extrae hijo en la posicion i
                if(hijo!=null){
                    if(hijo.getName().equals(nombreNodo)){
                    //JOptionPane.showMessageDialog(null, "hijo "+hijo);
                    break;
                    }
                }
            }
            return encontrarNodo(hijo, nombreNodo);
        }       
    }
    
    //Metodo que devuele el valor de un atributo XML
    //Si no existe el atributo retorna ""
    public String nodoGetValor(Element padre,String nombreNodo){
        Element element = encontrarNodo(padre, nombreNodo);
        //JOptionPane.showMessageDialog(null, element==null);
        if(element==null){
            return " ";
        }else{
            //JOptionPane.showMessageDialog(null,element.getTextTrim());
            return element.getTextTrim();
        }
    }
    
     // Convertir fecha de Emision 02-05-2016 02/04/2016 2016/04/26 => 2017-05-05
    public String miFormatoYYMMDD(String fechaEmision){
        
        //convierte la fecha DD/MM/YY a DD-MM-YY y la guarda en un arreglo
        String[] fecha = fechaEmision.replace('/', '-').split("-");
        String DIA=fecha[0];
        String MES=fecha[1];
        String ANIO=fecha[2];
        //Pero si la fechaEmision es de la forma 2017-08-05
        if(fecha[0].length()==4) {
            ANIO=fecha[0];
            DIA=fecha[2];
        }
        return ANIO +"-"+ MES +"-"+ DIA;
}
    
}
