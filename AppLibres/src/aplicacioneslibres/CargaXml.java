/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package aplicacioneslibres;

import Interfaces.SeleccionarTipoGastoNegocios;
import Interfaces.SeleccionarTipoGastoPersonal;
import conexionBDD.Conexion;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
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

            int cont;
            elementos.add("estado");
            elementos.add("ambiente");
            elementos.add("razonSocial");
            elementos.add("dirMatriz");
            elementos.add("ruc");
            elementos.add("estab");
            elementos.add("ptoEmi");
            elementos.add("secuencial");
            elementos.add("fechaEmision");
            elementos.add("identificacionComprador");
            elementos.add("totalSinImpuestos");
            elementos.add("valor");
            elementos.add("descripcion");
            elementos.add("precioTotalSinImpuesto");

            //Se obtiene la raiz 'tables'
            Element rootNode = document.getRootElement();

            // Datos sin cabecera
            cont = elementos.indexOf("estado");
            String estado = "";
            if (cont != -1) {
                Element est = (Element) rootNode.getChild(elementos.get(cont).toString());
                if (est != null) {
                    estado = est.getTextTrim();
                }
            }

            cont = elementos.indexOf("ambiente");
            String ambiente = "";
            if (cont != -1) {
                Element amb = (Element) rootNode.getChild(elementos.get(cont).toString());
                if (amb != null) {
                    ambiente = amb.getTextTrim();
                }
            }

            Element tabla = rootNode.getChild("comprobante");

            if (tabla != null) {
                String ex = tabla.getText();

                InputStream stream = new ByteArrayInputStream(ex.getBytes("UTF-8"));
                Document parse = builder.build(stream);

                tabla = parse.getRootElement();
            } else {
                tabla = rootNode;
            }

            String fechaCompleta = tabla.getChildren().get(1).getChildTextTrim("fechaEmision");
            StringTokenizer tk = new StringTokenizer(fechaCompleta, "/");
            String verificarFecha = "";

            while (tk.hasMoreTokens()) {
                verificarFecha = tk.nextToken();
            }
            
            System.out.println( verificarFecha+" fechaCompleta: "+fechaCompleta);
            
            if (verificarFecha.equals(String.valueOf(anio))) {
                List lista_campos = tabla.getChildren();
                Element campo;

                Element tributaria = (Element) lista_campos.get(0);

                // Info Tributaria
                cont = elementos.indexOf("razonSocial");
                String nombreEst = "";
                if (cont != -1) {
                    nombreEst = tributaria.getChildTextTrim(elementos.get(cont).toString());
                }

                cont = elementos.indexOf("dirMatriz");
                String dirMatriz = "";
                if (cont != -1) {
                    dirMatriz = tributaria.getChildTextTrim(elementos.get(cont).toString());
                }

                cont = elementos.indexOf("ruc");
                String ruc = "";
                if (cont != -1) {
                    ruc = tributaria.getChildTextTrim(elementos.get(cont).toString());
                }

                cont = elementos.indexOf("estab");
                String estab = "";
                if (cont != -1) {
                    estab = tributaria.getChildTextTrim(elementos.get(cont).toString());
                }

                cont = elementos.indexOf("ptoEmi");
                String emision = "";
                if (cont != -1) {
                    emision = tributaria.getChildTextTrim(elementos.get(cont).toString());
                }

                cont = elementos.indexOf("secuencial");
                String secuencial = "";
                if (cont != -1) {
                    secuencial = tributaria.getChildTextTrim(elementos.get(cont).toString());
                }

                String numFact = estab + "-" + emision + "-" + secuencial;

                if (!cp.verificar_usuario("SELECT *FROM ESTABLECIMIENTO WHERE id_establecimiento='" + ruc + "'")) {
                    String establecimiento = "INSERT INTO ESTABLECIMIENTO (id_establecimiento,nombre_establecimiento,direccion_establecimiento)"
                            + "VALUES ('" + ruc + "','" + nombreEst + "','" + dirMatriz + "')";
                    cp.insertar(establecimiento);
                    
                }

                //Se obtiene la raiz de la factura
                Element factura = (Element) lista_campos.get(1);

                // Info Factura
                
                cont = elementos.indexOf("identificacionComprador");
                String usuario = "";
                if (cont != -1) {
                    usuario = factura.getChildTextTrim(elementos.get(cont).toString());
                }
                
                
                cont = elementos.indexOf("fechaEmision");
                String fecha = "";
                if (cont != -1) {
                    fecha = factura.getChildTextTrim(elementos.get(cont).toString());
                }

                cont = elementos.indexOf("totalSinImpuestos");
                Double totalSinImp = 0.0;
                if (cont != -1) {
                    totalSinImp = Double.parseDouble(factura.getChildTextTrim(elementos.get(cont).toString()));
                }

                List totalConImp = factura.getChild("totalConImpuestos").getChildren();
                Element totalImp = (Element) totalConImp.get(0);

                cont = elementos.indexOf("valor");
                Double Imps = 0.0;
                if (cont != -1) {
                    Imps = Double.parseDouble(totalImp.getChildTextTrim(elementos.get(cont).toString()));
                }

                Double totalConImps = totalSinImp + Imps;

                if(usuario.equals(cedulaCli)){
                    if (!cp.verificar_usuario("SELECT * FROM FACTURA WHERE id_factura='" + numFact + "'")) {
                        String facturaQ = "INSERT INTO FACTURA (id_factura,id_cliente,id_establecimiento,tipo_factura,fecha_emision,estado_factura,ambiente_factura,total_sin_iva,iva,total_con_iva)"
                                + "VALUES ('" + numFact + "','" + cedulaCli + "','" + ruc + "','" + tipo + "','" + fecha + "','" + estado + "','" + ambiente + "'," + totalSinImp + "," + Imps + "," + totalConImps + ")";
                        cp.insertar(facturaQ);

                        System.out.println(facturaQ);
                        Element detalles = (Element) lista_campos.get(2);
                        List detalle = detalles.getChildren();

                        Object datosProducto[][] = new Object[detalle.size()][3];

                        for (int j = 0; j < detalle.size(); j++) {

                            campo = (Element) detalle.get(j);

                            // Detalle
                            cont = elementos.indexOf("descripcion");
                            String descripcion = "";
                            if (cont != -1) {
                                descripcion = campo.getChildTextTrim(elementos.get(cont).toString());
                            }

                            cont = elementos.indexOf("precioTotalSinImpuesto");
                            Double total = 0.0;
                            if (cont != -1) {
                                total = Double.parseDouble(campo.getChildTextTrim(elementos.get(cont).toString()));
                            }

                            if (!descripcion.equals("")) {
                                datosProducto[j][0] = descripcion;
                                datosProducto[j][1] = total;
                                datosProducto[j][2] = "";
                            }
                        }

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
            } else {
                JOptionPane.showMessageDialog(null, "El año de la factura no corresponde con el año seleccionado");
            }
        } catch (IOException | JDOMException io) {
            System.out.println(io.getMessage());
        }
    }
}
