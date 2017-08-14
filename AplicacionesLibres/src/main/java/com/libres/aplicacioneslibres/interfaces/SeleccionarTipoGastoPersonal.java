/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.libres.aplicacioneslibres.interfaces;


import com.libres.aplicacioneslibres.conexionbdd.Conexion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.DefaultCellEditor;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author andreu
 */
public class SeleccionarTipoGastoPersonal extends javax.swing.JFrame {

    final JComboBox comboBox;
    JTable tablaProductos;
    String tipoEstado[];

    String evtTipo = "";
    int filaTipo = -1;
    
    Object[][] tipos;
    
    Conexion conTipo;
    
    //Tabla Establecimiento----
    String ID_ESTABLECIMIENTO;
    String NOMBRE_ESTABLECIMIENTO;
    String TIPO_GASTO_ESTABLECIMIENTO;
    String DIRECCION_ESTABLECIMIENTO;
    String TELEFONO_ESTABLECIMIENTO;
    
    //tabla Factura------------- 
    String ID_FACTURA;
    String ID_CLIENTE;
    //String ID_ESTABLECIMIENTO;
    String TIPO_FACTURA;
    String FECHA_EMISION;
    String ESTADO_FACTURA;
    String AMBIENTE_FACTURA;
    Double TOTAL_SIN_IVA;
    Double IVA;
    Double TOTAL_CON_IVA;
    
    //Tabla DETALLE---------------
    //String ID_FACTURA
    //String ID_PRODUCTO
    int CANTIDAD;
    Double TOTAL;
    
    //Tabla  PRODUCTO
    String ID_PRODUCTO;
    String NAME_PRODUCTO;
    String FAMILIA;
    Double PRECIO_UNITARIO;
    
    int anio;
    /**
     * Creates new form SeleccionarTipoGasto
     *
     * @param conn
     * @param infoEncabezado
     * @param infoTributaria
     * @param infoFactura
     * @param infoDetalles
     */
    public SeleccionarTipoGastoPersonal(Conexion conn,HashMap<String,String> infoEncabezado,HashMap<String,String> infoTributaria,HashMap<String,String> infoFactura,ArrayList<HashMap> infoDetalles) {
        initComponents();
        
        this.conTipo = conn;
        
        ID_ESTABLECIMIENTO=infoTributaria.get("ruc");
        NOMBRE_ESTABLECIMIENTO=infoTributaria.get("razonSocial");
        jlabelESTABLECIMIENTO.setText(NOMBRE_ESTABLECIMIENTO);
        TIPO_GASTO_ESTABLECIMIENTO="";//conTipo.getTipoGastoEstablecimiento(ID_ESTABLECIMIENTO);
        //cmbTIPO_GASTO_ESTABLECIMIENTO.setSelectedItem(TIPO_GASTO_ESTABLECIMIENTO);
        
        DIRECCION_ESTABLECIMIENTO=infoTributaria.get("dirMatriz");
        TELEFONO_ESTABLECIMIENTO="";
    
        //Tabla Factura
        ID_FACTURA = infoTributaria.get("estab")+"-"+infoTributaria.get("ptoEmi")+"-"+infoTributaria.get("secuencial");
        ID_CLIENTE = infoFactura.get("identificacionComprador");
        //String ID_ESTABLECIMIENTO;
        TIPO_FACTURA = "Personal";
        FECHA_EMISION = infoFactura.get("fechaEmision");
        ESTADO_FACTURA = infoEncabezado.get("estado");
        AMBIENTE_FACTURA = infoEncabezado.get("ambiente");
        TOTAL_SIN_IVA =Double.parseDouble( infoFactura.get("totalSinImpuestos") );
        IVA = Double.parseDouble(infoFactura.get("valor") );
        TOTAL_CON_IVA = Double.parseDouble(infoFactura.get("importeTotal") );
        
        this.anio = Integer.parseInt(infoFactura.get("fechaEmision").substring(0, 4));

        
        int numeroProductos =infoDetalles.size();
        tipos=new Object[numeroProductos][6];
        
        //Extraer detalle de los productos y los guarda en la matriz Object [][] detalleProducto
        for (int i =0; i<numeroProductos;i++){
            tipos[i][0]=infoDetalles.get(i).get("descripcion").toString();
            tipos[i][1]=Double.parseDouble( infoDetalles.get(i).get("precioTotalSinImpuesto").toString() );
            
            //Retorna el tipo de gasto o Familia que pertece un producto de acuerdo a su ID_PRODUCTO
            String tipoFamiliaProducto = conTipo.consultarProductoPor( infoDetalles.get(i).get("codigoPrincipal").toString());
            if(!tipoFamiliaProducto.equals("")){
                tipos[i][2]=tipoFamiliaProducto;//Familia o TIPO_FACTURA de Gasto
            }else {
                tipos[i][2]=this.conTipo.getTipoGastoEstablecimiento(ID_ESTABLECIMIENTO);//Familia o TIPO_FACTURA de Gasto
            }
            
            tipos[i][3]=infoDetalles.get(i).get("codigoPrincipal").toString();
            tipos[i][4]=(int)Double.parseDouble( infoDetalles.get(i).get("cantidad").toString() );
            tipos[i][5]=Double.parseDouble( infoDetalles.get(i).get("precioUnitario").toString() );
        }
                
        String nombreCabeceras[] = {"Descripcion", "Precio Total", "Tipo de Gasto"};

        tipoEstado = new String[tipos.length];
        for (int i = 0; i < tipos.length; i++) {
            tipoEstado[i] = "";
        }

        tablaProductos = new JTable(tipos, nombreCabeceras) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        jScrollPane1.setViewportView(tablaProductos);

        comboBox = new JComboBox();
        comboBox.addItem("Vivienda");
        comboBox.addItem("Salud");
        comboBox.addItem("Educacion");
        comboBox.addItem("Alimentacion");
        comboBox.addItem("Vestimenta");
        comboBox.addItem("Otro");
        
        //Cargar autocalificacion con los combobox
        //extraigo la tabla del objeto donde  ocurrio el evento
        for (int row=0;row<tablaProductos.getRowCount();row++){
            for (int column=0;column<tablaProductos.getColumnCount();column++){
                
                TableModel model = tablaProductos.getModel();
                Object data = model.getValueAt(row, column);
                if (!data.equals("") && column == 2) {
                    //int opc = comboBox.getSelectedIndex();
                    //System.out.println(row);

                    if (data.equals("Vivienda")) {
                        sumarAgregado(txtVivienda, row, "Vivienda");
                    }
                    if (data.equals("Salud")) {
                        sumarAgregado(txtSalud, row, "Salud");
                    }
                    if (data.equals("Educacion")) {
                        sumarAgregado(txtEducacion, row, "Educacion");
                    }
                    if (data.equals("Alimentacion")) {
                        sumarAgregado(txtAlimentacion, row, "Alimentacion");
                    }
                    if (data.equals("Vestimenta")) {
                        sumarAgregado(txtVestimenta, row, "Vestimenta");
                    }
                    if (data.equals("Otro")) {
                        sumarAgregado(txtOtro, row, "Otro");
                    }
                }
            
            }
        
        
        tablaProductos.getModel().addTableModelListener(new TableModelListener() {
            @Override
            public void tableChanged(TableModelEvent tme) {
                int row = tme.getFirstRow();
                int column = tme.getColumn();

                TableModel model = (TableModel) tme.getSource();
                Object data = model.getValueAt(row, column);

                if (!data.equals("") && column == 2) {
                    //int opc = comboBox.getSelectedIndex();
                    //System.out.println(row);

                    if (!tipoEstado[row].equals("")) {
                        if (tipoEstado[row].equals("Vivienda")) {
                            restarAgregado(txtVivienda, row);
                        }
                        if (tipoEstado[row].equals("Salud")) {
                            restarAgregado(txtSalud, row);
                        }
                        if (tipoEstado[row].equals("Educacion")) {
                            restarAgregado(txtEducacion, row);
                        }
                        if (tipoEstado[row].equals("Alimentacion")) {
                            restarAgregado(txtAlimentacion, row);
                        }
                        if (tipoEstado[row].equals("Vestimenta")) {
                            restarAgregado(txtVestimenta, row);
                        }
                        if (tipoEstado[row].equals("Otro")) {
                            restarAgregado(txtOtro, row);
                        }
                    }

                    if (data.equals("Vivienda")) {
                        sumarAgregado(txtVivienda, row, "Vivienda");
                    }
                    if (data.equals("Salud")) {
                        sumarAgregado(txtSalud, row, "Salud");
                    }
                    if (data.equals("Educacion")) {
                        sumarAgregado(txtEducacion, row, "Educacion");
                    }
                    if (data.equals("Alimentacion")) {
                        sumarAgregado(txtAlimentacion, row, "Alimentacion");
                    }
                    if (data.equals("Vestimenta")) {
                        sumarAgregado(txtVestimenta, row, "Vestimenta");
                    }
                    if (data.equals("Otro")) {
                        sumarAgregado(txtOtro, row, "Otro");
                    }
                }

            }
        });

        DefaultTableCellRenderer alinearDerecha = new DefaultTableCellRenderer();
        alinearDerecha.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        tablaProductos.getColumnModel().getColumn(1).setCellRenderer(alinearDerecha);

        tablaProductos.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(comboBox));

        tablaProductos.getColumnModel().getColumn(1).setMinWidth(100);
        tablaProductos.getColumnModel().getColumn(1).setMaxWidth(100);
        tablaProductos.getColumnModel().getColumn(2).setMinWidth(150);
        tablaProductos.getColumnModel().getColumn(2).setMaxWidth(150);

        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jButton1 = new javax.swing.JButton();
        lblVivienda = new javax.swing.JLabel();
        lblSalud = new javax.swing.JLabel();
        lblEducacion = new javax.swing.JLabel();
        lblAlimentacion = new javax.swing.JLabel();
        txtVivienda = new javax.swing.JTextField();
        txtSalud = new javax.swing.JTextField();
        txtEducacion = new javax.swing.JTextField();
        txtAlimentacion = new javax.swing.JTextField();
        txtVestimenta = new javax.swing.JTextField();
        txtOtro = new javax.swing.JTextField();
        lblVestimenta = new javax.swing.JLabel();
        lblOtro = new javax.swing.JLabel();
        jlabelESTABLECIMIENTO = new javax.swing.JLabel();
        cmbTIPO_GASTO_ESTABLECIMIENTO = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("SELECCIONAR TIPO DE GASTO");

        jButton1.setText("Aceptar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblVivienda.setText("Vivienda");

        lblSalud.setText("Salud");

        lblEducacion.setText("Educacion");

        lblAlimentacion.setText("Alimentacion");

        txtVivienda.setEditable(false);
        txtVivienda.setText("0.0");

        txtSalud.setEditable(false);
        txtSalud.setText("0.0");

        txtEducacion.setEditable(false);
        txtEducacion.setText("0.0");

        txtAlimentacion.setEditable(false);
        txtAlimentacion.setText("0.0");

        txtVestimenta.setEditable(false);
        txtVestimenta.setText("0.0");

        txtOtro.setEditable(false);
        txtOtro.setText("0.0");

        lblVestimenta.setText("Vestimenta");

        lblOtro.setText("Otro");

        jlabelESTABLECIMIENTO.setFont(new java.awt.Font("Tahoma", 1, 14)); // NOI18N
        jlabelESTABLECIMIENTO.setText("ESTABLECIMIENTO");

        cmbTIPO_GASTO_ESTABLECIMIENTO.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "...", "Vivienda", "Salud", "Educacion", "Alimentacion", "Vestimenta", "Otro" }));
        cmbTIPO_GASTO_ESTABLECIMIENTO.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTIPO_GASTO_ESTABLECIMIENTOActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(512, 512, 512)
                        .addComponent(jButton1))
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addContainerGap()
                            .addComponent(jLabel1)
                            .addGap(33, 33, 33)
                            .addComponent(jlabelESTABLECIMIENTO, javax.swing.GroupLayout.PREFERRED_SIZE, 207, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbTIPO_GASTO_ESTABLECIMIENTO, javax.swing.GroupLayout.PREFERRED_SIZE, 125, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                            .addGap(12, 12, 12)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(layout.createSequentialGroup()
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lblSalud)
                                                .addComponent(lblVivienda))
                                            .addGap(97, 97, 97)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtSalud, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtVivienda, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblEducacion)
                                            .addGap(85, 85, 85)
                                            .addComponent(txtEducacion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                    .addGap(65, 65, 65)
                                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addGroup(layout.createSequentialGroup()
                                            .addComponent(lblOtro)
                                            .addGap(100, 100, 100)
                                            .addComponent(txtOtro, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                                        .addGroup(layout.createSequentialGroup()
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(lblVestimenta)
                                                .addComponent(lblAlimentacion))
                                            .addGap(59, 59, 59)
                                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(txtAlimentacion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(txtVestimenta, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabelESTABLECIMIENTO, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(cmbTIPO_GASTO_ESTABLECIMIENTO, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblVivienda)
                    .addComponent(txtVivienda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(lblAlimentacion)
                        .addComponent(txtAlimentacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSalud, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtVestimenta, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSalud)
                            .addComponent(lblVestimenta))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(txtEducacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblOtro))
                    .addComponent(txtOtro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblEducacion)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 183, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        int filasTotales = tablaProductos.getRowCount();
        boolean validado = true;

        for (int i = 0; i < filasTotales; i++) {
            if (tablaProductos.getValueAt(i, 2).equals("")) {
                validado = false;
                break;
            }
        }

        if (validado == true) {
            String query;

            double totales[] = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

            if (!txtVivienda.getText().equals("0.0")) {
                totales[0] = ingresarTipo(txtVivienda, lblVivienda);
            }
            if (!txtSalud.getText().equals("0.0")) {
                totales[1] = ingresarTipo(txtSalud, lblSalud);
            }
            if (!txtEducacion.getText().equals("0.0")) {
                totales[2] = ingresarTipo(txtEducacion, lblEducacion);
            }
            if (!txtAlimentacion.getText().equals("0.0")) {
                totales[3] = ingresarTipo(txtAlimentacion, lblAlimentacion);
            }
            if (!txtVestimenta.getText().equals("0.0")) {
                totales[4] = ingresarTipo(txtVestimenta, lblVestimenta);
            }
            if (!txtOtro.getText().equals("0.0")) {
                totales[5] = ingresarTipo(txtOtro, lblOtro);
            }

            if (conTipo.verificar_usuario("SELECT * FROM HISTORIAL_PAGOS_PERSONALES WHERE anio_historial_p=" + anio + " AND id_cliente='" + ID_CLIENTE + "'")) {
                query = "UPDATE HISTORIAL_PAGOS_PERSONALES SET total_alimentacion=total_alimentacion+" + totales[3] + ","
                        + "total_salud=total_salud+" + totales[1] + ","
                        + "total_vivienda=total_vivienda+" + totales[0] + ","
                        + "total_educacion=total_educacion+" + totales[2] + ","
                        + "total_vestimenta=total_vestimenta+" + totales[4] + ","
                        + "total_otros=total_otros+" + totales[5] + " WHERE anio_historial_p=" + anio + " AND id_cliente='" + ID_CLIENTE + "'";
            } else {
                query = "INSERT INTO HISTORIAL_PAGOS_PERSONALES VALUES (" + anio + ",'" + ID_CLIENTE + "'," + totales[3] + "," + totales[1] + "," + totales[0] + "," + totales[2] + "," + totales[4] + "," + totales[5] + ")";
            }

            TIPO_GASTO_ESTABLECIMIENTO=cmbTIPO_GASTO_ESTABLECIMIENTO.getSelectedItem().toString();
            if (!TIPO_GASTO_ESTABLECIMIENTO.equals("...")){
                conTipo.insertar(query);
                registrarFactura();//ingresar Establecimiento,Factura,Producto y Detalle
                JOptionPane.showMessageDialog(this, "Factura ingresada exitosamente");
                recargar(conTipo);
                this.dispose();
             }else{
                JOptionPane.showMessageDialog(null, "Por favor, seleccione un tipo de gasto que identifique al Establecimiento");
            }
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado el tipo para cada producto");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cmbTIPO_GASTO_ESTABLECIMIENTOActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTIPO_GASTO_ESTABLECIMIENTOActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbTIPO_GASTO_ESTABLECIMIENTOActionPerformed

    public void registrarFactura(){
        
        //Insertar Establecimiento
        if (this.conTipo.consultarEstablecimientoPor(ID_ESTABLECIMIENTO).equals("")){
            String sql = "INSERT INTO ESTABLECIMIENTO VALUES ('"+ID_ESTABLECIMIENTO+
                    "', '"+NOMBRE_ESTABLECIMIENTO+"', '"+TIPO_GASTO_ESTABLECIMIENTO+
                    "', '"+DIRECCION_ESTABLECIMIENTO+"', '"+TELEFONO_ESTABLECIMIENTO+"')";
            this.conTipo.insertar(sql);
        }else{
            String sql = "UPDATE ESTABLECIMIENTO SET TIPO_GASTO_ESTABLECIMIENTO='"+TIPO_GASTO_ESTABLECIMIENTO+"' WHERE ID_ESTABLECIMIENTO='"+ID_ESTABLECIMIENTO+"'";
            this.conTipo.insertar(sql);
            
        }
        //Insertar Factura
        if (this.conTipo.verificar_factura(ID_FACTURA)==false){
            String sql = "INSERT INTO FACTURA VALUES('"+ID_FACTURA+"', '"+ID_CLIENTE+
                    "', '"+ID_ESTABLECIMIENTO+"', '"+TIPO_FACTURA+"', '"+FECHA_EMISION+
                    "', '"+ESTADO_FACTURA+"', '"+AMBIENTE_FACTURA+"', "+TOTAL_SIN_IVA+
                    ", "+IVA+", "+TOTAL_CON_IVA+")";
            this.conTipo.insertar(sql);
        }
        
        //Insertar Productos y Detalles
        int numeroProductos =tipos.length;
        
        //Extraer detalle de los productos y los guarda en la matriz Object [][] detalleProducto
        for (int i =0; i<numeroProductos;i++){
//            tipos[i][0]=infoDetalles.get(i).get("descripcion").toString();
//            tipos[i][1]=Double.parseDouble( infoDetalles.get(i).get("precioTotalSinImpuesto").toString() );
//            tipos[i][2]=conTipo.consultarProductoPor( infoDetalles.get(i).get("codigoPrincipal").toString() );//Familia o TIPO_FACTURA de Gasto
//            tipos[i][3]=infoDetalles.get(i).get("codigoPrincipal").toString();
//            tipos[i][4]=(int)Double.parseDouble( infoDetalles.get(i).get("cantidad").toString() );
//            tipos[i][5]=Double.parseDouble( infoDetalles.get(i).get("precioUnitario").toString() );
            //Tabla DETALLE---------------
            //ID_FACTURA
            ID_PRODUCTO =  tipos[i][3].toString();
            CANTIDAD = (int)tipos[i][4];
            TOTAL =(double)tipos[i][1];
            String sqlDetalle = "INSERT INTO DETALLE VALUES('"+ID_FACTURA+"', '"+ID_PRODUCTO+"', "+CANTIDAD+", "+TOTAL+")";
            
            //Tabla  PRODUCTO
            //ID_PRODUCTO
            NAME_PRODUCTO = tipos[i][0].toString();
            FAMILIA = tipos[i][2].toString();
            PRECIO_UNITARIO = (double) tipos[i][5];
            
            String sqlProducto = "INSERT INTO PRODUCTO VALUES('"+ID_PRODUCTO+"', '"+NAME_PRODUCTO+"', '"+FAMILIA+"', "+PRECIO_UNITARIO+")";
            String sqlProductoUpdate = "UPDATE PRODUCTO SET NAME_PRODUCTO='"+
                    NAME_PRODUCTO+"', FAMILIA='"+FAMILIA+"', PRECIO_UNITARIO="+PRECIO_UNITARIO+" WHERE ID_PRODUCTO='"+ID_PRODUCTO+"'";
            
            if(this.conTipo.consultarProductoPor(ID_PRODUCTO).equals("")){
                this.conTipo.insertar(sqlProducto);
            }else{
                this.conTipo.insertar(sqlProductoUpdate);//el insert ejecuta cualquier operacion INSERT UPDATE DELETE!!
            }
            this.conTipo.insertar(sqlDetalle);
        }
        
    }
            
            
            
    private void recargar(Conexion conn) {
         ArrayList auxRec = new ArrayList();
        com.libres.aplicacioneslibres.interfaces.FacturaManualPersonal.combo_Establecimientos.removeAllItems();
        com.libres.aplicacioneslibres.interfaces.FacturaManualNegocio.combo_Establecimientos.removeAllItems();
        com.libres.aplicacioneslibres.interfaces.FacturaManualPersonal.combo_Establecimientos.addItem("");
        com.libres.aplicacioneslibres.interfaces.FacturaManualNegocio.combo_Establecimientos.addItem("");
        auxRec = conn.cargarEstablecimiento();
        for (Object est : auxRec) {            
            com.libres.aplicacioneslibres.interfaces.FacturaManualPersonal.combo_Establecimientos.addItem(est.toString());
            com.libres.aplicacioneslibres.interfaces.FacturaManualNegocio.combo_Establecimientos.addItem(est.toString());
        }
    }
    
    public void restarAgregado(JTextField txtField, int row) {
        double total;
        total = Double.parseDouble(txtField.getText());
        total -= (Double) tablaProductos.getValueAt(row, 1);
        total = BigDecimal.valueOf(total).setScale(3, RoundingMode.HALF_UP).doubleValue();
        txtField.setText(String.valueOf(total));
    }

    private void sumarAgregado(JTextField txtField, int row, String tipo) {
        double total;
        total = Double.parseDouble(txtField.getText());
        total += (Double) tablaProductos.getValueAt(row, 1);
        total = BigDecimal.valueOf(total).setScale(3, RoundingMode.HALF_UP).doubleValue();
        txtField.setText(String.valueOf(total));
        tipoEstado[row] = tipo;
    }

    public double ingresarTipo(JTextField txtField, JLabel lblLabel) {
        double total;
        String query;

        total = Double.parseDouble(txtField.getText());
        total = BigDecimal.valueOf(total).setScale(3, RoundingMode.HALF_UP).doubleValue();

        query = "INSERT INTO TIPO_GASTO (id_factura,tipo,total)"
                + "VALUES('" + ID_FACTURA + "','" + lblLabel.getText() + "'," + total + ")";

        conTipo.insertar(query);

        return total;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                // new SeleccionarTipoGasto().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> cmbTIPO_GASTO_ESTABLECIMIENTO;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel jlabelESTABLECIMIENTO;
    private javax.swing.JLabel lblAlimentacion;
    private javax.swing.JLabel lblEducacion;
    private javax.swing.JLabel lblOtro;
    private javax.swing.JLabel lblSalud;
    private javax.swing.JLabel lblVestimenta;
    private javax.swing.JLabel lblVivienda;
    private javax.swing.JTextField txtAlimentacion;
    private javax.swing.JTextField txtEducacion;
    private javax.swing.JTextField txtOtro;
    private javax.swing.JTextField txtSalud;
    private javax.swing.JTextField txtVestimenta;
    private javax.swing.JTextField txtVivienda;
    // End of variables declaration//GEN-END:variables
}
