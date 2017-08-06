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
    
    public void cargarXml(String name, String cedulaCli, int anio, String tipo) {
        //Se crea un SAXBuilder para poder parsear el archivo
        SAXBuilder builder = new SAXBuilder();

        File xmlFile = new File(name);
            //Se crea el documento a traves del archivo
            Document document;
        try {
            
            document = (Document) builder.build(xmlFile);
            Conexion cp = new Conexion();
            
        //Info Producto
            
            HashMap<String,String> infoEncabezado = new HashMap<>();
            infoEncabezado.put("estado", "");
            infoEncabezado.put("ambiente", "");
            
            HashMap<String,String> infoTributaria = new HashMap<>();
            infoTributaria.put("razonSocial", "");
            infoTributaria.put("ruc", "");
            infoTributaria.put("estab", "");
            infoTributaria.put("ptoEmi", "");
            infoTributaria.put("secuencial", "");
            infoTributaria.put("dirMatriz", "");
            
            HashMap<String,String> infoFactura = new HashMap<>();
            infoFactura.put("fechaEmision", "");
            infoFactura.put("razonSocialComprador", "");//nombre_Comprador
            infoFactura.put("identificacionComprador", "");//cedula_comprador
            infoFactura.put("fechaEmision", "");
            infoFactura.put("totalSinImpuestos", "");
            infoFactura.put("valor", "");//valor de impuestos
            infoFactura.put("importeTotal", "");//Valor final de esa factura,con propina o descuento 

            //informacion de un producto
            HashMap<String,String> infoDetalle = new HashMap<>();
            infoDetalle.put("codigoPrincipal", "");//Codigo
            infoDetalle.put("descripcion", "");//Nombre
            infoDetalle.put("cantidad", "");//cantidad
            infoDetalle.put("precioUnitario", "");//precio unitario
            infoDetalle.put("precioTotalSinImpuesto", "");//Precio total sin iva del producto

            //informacion de la lista de productos
            ArrayList<HashMap> infoDetalles = new ArrayList<>();
            
            
            //Se obtiene la raiz 'tables'
            Element rootNode = document.getRootElement(); //Autorizacion

            // Datos Cabecera =============================================
            infoEncabezado.replace("estado", rootNode.getChild("estado").getTextTrim());
            infoEncabezado.replace("ambiente", rootNode.getChild("ambiente").getTextTrim());
            
            // Datos Comprobante =======================================
            Element tabla = rootNode.getChild("comprobante");
            
            if (tabla != null) {
                String ex = tabla.getText();
                InputStream stream = new ByteArrayInputStream(ex.getBytes("UTF-8"));
                Document parse = builder.build(stream);
                tabla = parse.getRootElement();
            } else {
                tabla = rootNode;
            }
            
            //Comprobante tiene 3 hijos, Info Tributaria, Info Factura y Detalles
            List lista_campos = tabla.getChildren();//guardo en una lista los 3 hijos
            Element campo;

            Element tributaria = (Element) lista_campos.get(0);

            // Info Tributaria
            infoTributaria.replace("razonSocial", tributaria.getChildTextTrim("razonSocial"));
            infoTributaria.replace("dirMatriz", tributaria.getChildTextTrim("dirMatriz"));
            infoTributaria.replace("ruc", tributaria.getChildTextTrim("ruc"));
            infoTributaria.replace("estab", tributaria.getChildTextTrim("estab"));
            infoTributaria.replace("ptoEmi", tributaria.getChildTextTrim("ptoEmi"));
            infoTributaria.replace("secuencial", tributaria.getChildTextTrim("secuencial"));
            
            //Info Factura
            Element factura = (Element) lista_campos.get(1);
            
            infoFactura.replace("fechaEmision", factura.getChildTextTrim("fechaEmision"));  
            infoFactura.replace("razonSocialComprador", factura.getChildTextTrim("razonSocialComprador"));  
            infoFactura.replace("identificacionComprador", factura.getChildTextTrim("identificacionComprador"));  
            infoFactura.replace("totalSinImpuestos", factura.getChildTextTrim("totalSinImpuestos"));    
                
                //TotalConImpuestos tiene dos hijos, el segundo campo (.getChild(1) )tiene el valor $ IVA
                List totalConImp = factura.getChild("totalConImpuestos").getChildren();
                Element totalImp = (Element) totalConImp.get(1);
                infoFactura.replace("valor", totalImp.getChildTextTrim("valor"));    
            
            //Total a pagar 
            infoFactura.replace("importeTotal", tributaria.getChildTextTrim("importeTotal"));
            
            //Info Detalles
            Element detalles = (Element) lista_campos.get(2);   //Detalles...
            List detalle = detalles.getChildren();  //extraido los hijos(detalle) en una lista auxiliar
            
            for (int j = 0; j < detalle.size(); j++) {
                campo = (Element) detalle.get(j);
                // Detalle    
                //Descripcion= Nombre del producto
                infoDetalle.replace("codigoPrincipal", campo.getChildTextTrim("codigoPrincipal"));
                infoDetalle.replace("descripcion", campo.getChildTextTrim("descripcion"));
                infoDetalle.replace("cantidad", campo.getChildTextTrim("cantidad"));
                infoDetalle.replace("precioUnitario", campo.getChildTextTrim("precioUnitario"));
                //PrecioTotalSinImpuesto = PRECIO_UNITARIO * UNIDADES
                infoDetalle.replace("precioTotalSinImpuesto", campo.getChildTextTrim("precioTotalSinImpuesto"));
                
                //agregamos el detalle a la lista de detalles SSI el Nombre_producto != Empty
                if (!infoDetalle.get("descripcion").equals("")) {
                    infoDetalles.add(infoDetalle);
                }    
            }
            
            // Extraer Anio de fecha de Emision 02-05-2016 02/04/2016 2016/04/26 2017-05-05
            String fechaEmision = infoFactura.get("fechaEmision");            
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
            //GUARDAR FECHA EMISION EN EL FORMATO YY-MM-DD
            infoFactura.replace("fechaEmision", ANIO+"-"+MES+"-"+DIA);
            
            //sentencia para validar si la factura pertenece a otro usuario
            String sqlUser="select *from Cliente where id_cliente='"+infoFactura.get("identificacionComprador")+"'";
            
            //El numero de factura es : estab-ptoEmi-secuencial
            //numFact para validar si la factura ya ha sido ingresada
            String numFact = infoTributaria.get("estab") + "-" 
                    + infoTributaria.get("ptoEmi") + "-" 
                    + infoTributaria.get("secuencial");
            
            //VALIDACIONES
            String opcion="";
            
            if( !ANIO.equals(anio) ) { //si el Anio es deferente!! 
                opcion="problemaAnio";
                
            }else if( !cp.verificar_usuario(sqlUser) ){ //si el usuario es diferente!!
                opcion="facturaDeOtroUsuario";
                
            }else if( cp.verificar_factura(numFact) ){ //si la factura ya fue ingresada!!
                opcion="facturaYaIngresada";
                
            }else if( tipo.equals("Personal") ) { 
                opcion="interfazPersonal";
                
            }else if( tipo.equals("Negocio") ) {
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
                    SeleccionarTipoGastoPersonal sP = new SeleccionarTipoGastoPersonal( infoEncabezado, infoTributaria, infoDetalles);
                    sP.setVisible(true);
                
                    break;
                case "interfazNegocio":
                    SeleccionarTipoGastoNegocios sN = new SeleccionarTipoGastoNegocios(infoEncabezado, infoTributaria, infoDetalles);
                    sN.setVisible(true);
                    break;
            }
                
                
            } catch (JDOMException | IOException ex) {
                Logger.getLogger(CargaXml.class.getName()).log(Level.SEVERE, null, ex);
            }
    }   
}
