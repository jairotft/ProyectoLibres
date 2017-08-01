/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.libres.aplicacioneslibres.aplicacioneslibres;

import com.libres.aplicacioneslibres.interfaces.SeleccionarTipoGastoNegocios;
import com.libres.aplicacioneslibres.interfaces.SeleccionarTipoGastoPersonal;
import com.libres.aplicacioneslibres.conexionbdd.Conexion;
import com.libres.aplicacioneslibres.interfaces.FacturaData;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
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
    try {
            //Se crea el documento a traves del archivo
            Document document = (Document) builder.build(xmlFile);
            Conexion cp = new Conexion();

            ArrayList elementos = new ArrayList();
            
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
            
            //El numero de factura es : estab-ptoEmi-secuencial
            String numFact = infoTributaria.get("estab") + "-" 
                    + infoTributaria.get("estab") + "-" 
                    + infoTributaria.get("secuencial");
            
            //Info Factura
            Element factura = (Element) lista_campos.get(1);
            
            infoFactura.replace("fechaEmision", factura.getChildTextTrim("fechaEmision"));  
            infoFactura.replace("razonSocialComprador", factura.getChildTextTrim("razonSocialComprador"));  
            infoFactura.replace("identificacionComprador", factura.getChildTextTrim("identificacionComprador"));  
            infoFactura.replace("totalSinImpuestos", tributaria.getChildTextTrim("totalSinImpuestos"));    
                
                //TotalConImpuestos tiene dos hijos, el segundo campo (.getChild(1) )tiene el valor $ IVA
                List totalConImp = factura.getChild("totalConImpuestos").getChildren();
                Element totalImp = (Element) totalConImp.get(1);
                infoFactura.replace("valor", totalImp.getChildTextTrim("valor"));    
            
            infoFactura.replace("importeTotal", tributaria.getChildTextTrim("importeTotal"));
            
            //Info Detalles
            Element detalles = (Element) lista_campos.get(2);   //Detalles...
            List detalle = detalles.getChildren();  //extraido los hijos(detalle) en una lista auxiliar
            
            for (int j = 0; j < detalle.size(); j++) {
                campo = (Element) detalle.get(j);
                // Detalle    
                //Descripcion= Nombre del producto
                infoDetalle.replace("descripcion", campo.getChildTextTrim("descripcion"));
                infoDetalle.replace("cantidad", campo.getChildTextTrim("cantidad"));
                infoDetalle.replace("precioUnitario", campo.getChildTextTrim("precioUnitario"));
                //PrecioTotalSinImpuesto = PRECIO_UNITARIO * UNIDADES
                infoDetalle.replace("precioTotalSinImpuesto", campo.getChildTextTrim("precioTotalSinImpuesto"));
                
                
                if (!infoDetalle.get("descripcion").equals("")) {
                    
                    
                    
                    datosProducto[j][0] = descripcion;        
                    datosProducto[j][1] = total;        
                    datosProducto[j][2] = "";        
                    
                }    
            }
            
            
            // Extraer Anio de fecha de Emision
            String fechaCompleta = infoFactura.get("fechaEmision");
            StringTokenizer tk = new StringTokenizer(fechaCompleta, "/");
            String verificaAnio = "";

            while (tk.hasMoreTokens()) {
                verificaAnio = tk.nextToken(); //el ultimo token es el Anio
            }
            
            //if (verificarFecha.equals(String.valueOf(anio))) {
            
//                if (!cp.verificar_usuario("SELECT * FROM ESTABLECIMIENTO WHERE id_establecimiento='" + ruc + "'")) {
//                    String establecimiento = "INSERT INTO ESTABLECIMIENTO (id_establecimiento,nombre_establecimiento,direccion_establecimiento)"
//                            + "VALUES ('" + ruc + "','" + nombreEst + "','" + dirMatriz + "')";
//                    cp.insertar(establecimiento);
//                }
            
            
            
            
                if(usuario.equals(cedulaCli)){
                    
                    if (!cp.verificar_usuario("SELECT *FROM FACTURA WHERE id_factura='" + numFact + "'")) {
                        
//                        String facturaQ = "INSERT INTO FACTURA (id_factura,id_cliente,id_establecimiento,tipo_factura,fecha_emision,estado_factura,ambiente_factura,total_sin_iva,iva,total_con_iva)"
//                                + "VALUES ('" + numFact + "','" + cedulaCli + "','" + ruc + "','" + tipo + "','" + fecha + "','" + estado + "','" + ambiente + "'," + totalSinImp + "," + Imps + "," + totalConImps + ")";
//                        cp.insertar(facturaQ);


                        if (datosProducto.length != 0) {
                            if (tipo.equals("Personal")) {
                                SeleccionarTipoGastoPersonal seleccionarP = new SeleccionarTipoGastoPersonal(cp, datosProducto, numFact, anio, cedulaCli, tipo);
                                seleccionarP.setVisible(true);
                            } else {
                                SeleccionarTipoGastoNegocios seleccionarH = new SeleccionarTipoGastoNegocios(cp, datosProducto, numFact, anio, cedulaCli, tipo);
                                seleccionarH.setVisible(true);
                            }
                        }
                    } else {
                        JOptionPane.showMessageDialog(null, "Esta factura ya fue ingresada");
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "Factura pertenece a otro usuario");
                }
            } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(CargaXml.class.getName()).log(Level.SEVERE, null, ex);
        }
{
                JOptionPane.showMessageDialog(null, "El año de la factura no corresponde con el año seleccionado");
            }
        } catch (IOException | JDOMException io) {
            System.out.println(io.getMessage());
        }
    }
}

