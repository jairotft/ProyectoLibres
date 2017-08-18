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
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;

/**
 *
 * @author Buhobit
 */
public class SeleccionarTipoGastoPersonal extends javax.swing.JFrame {

    final JComboBox cmbCeldaTipoGasto;
    JTable tablaProductos;
    String tipoEstado[];

    String evtTipo = "";
    int filaTipo = -1;
    //Almacena detalle y productos de la factura
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
    ArrayList<HashMap> infoDetalles;
    
    //Items para Gastos Personal
    String Vivienda = "Vivienda";
    String Salud = "Salud";
    String Educacion = "Educacion";
    String Alimentacion = "Alimentacion";
    String Vestimenta = "Vestimenta";
    String Otro = "Otro";

    //Aregllo con todos los itemsGasto Personal
    String [] itemsGasto = {"",Vivienda, Salud, Educacion,
            Alimentacion, Vestimenta, Otro};
    //Verificar si el establecimiento es nuevo o no
    Boolean nuevoEstablecimiento;
    Boolean activarCmbTipoGasto;//permite saber cuando activar el comboBox de TipoGasto
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
        this.nuevoEstablecimiento = true;//Suponer que el establecimiento es nuevo!!!
        this.activarCmbTipoGasto = false;
        
        ID_ESTABLECIMIENTO=infoTributaria.get("ruc");
        NOMBRE_ESTABLECIMIENTO=infoTributaria.get("razonSocial");
        jlabelESTABLECIMIENTO.setText(NOMBRE_ESTABLECIMIENTO);
        TIPO_GASTO_ESTABLECIMIENTO=conTipo.getTipoGastoEstablecimiento(ID_ESTABLECIMIENTO);
        //Consultar si el establecimiento es nuevo o no!!!
        if (!TIPO_GASTO_ESTABLECIMIENTO.equals("")) {
            //cmbTipoGasto.enable(false);//si ya existe desabilita cmbTipoGasto
            for (String item1 : itemsGasto) this.cmbTipoGasto.addItem(item1);
            nuevoEstablecimiento=false;
        }else{
            //Habilitar cmbTipoGasto para elegir un tipo de Gasto que identifique al establecimiento!!!
            cmbTipoGasto.setVisible(true);
            activarCmbTipoGasto = false;//Al acrrancar ventana, desactivar los eventos que surgen en cmbTipoGasto
            //Iniciar los itemsGasto al cmbTipoGasto
            for (String item1 : itemsGasto) this.cmbTipoGasto.addItem(item1);
        }
        
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
        this.infoDetalles = infoDetalles;//Iniciar la lista de detalle productos
        tipos = new Object[infoDetalles.size()][6];//inicializar tamanio tipos para guardar lista detalles en tabla
        
        cmbCeldaTipoGasto = new JComboBox();//jcomboBox para la celdas de seleccionar TipoGasto
        //Agregar todos los itemsGasto Personal al comboboxCelda de la tabla
        for (String item1 : itemsGasto) this.cmbCeldaTipoGasto.addItem(item1);
        
        iniciarAutocompletarTabla();
        
        
        //Evento que al actuar dentro de la tabla, suma o restar el total de cada familia
        tablaProductos.getModel().addTableModelListener((TableModelEvent tme) -> {
            //Si el usuario no ha seleccionado un item en cmbTipoGasto 
            //Entonces, impedir modificar la tabla!!!
            if(nuevoEstablecimiento==true){
                if(cmbTipoGasto.getSelectedItem().equals("")){
                JOptionPane.showMessageDialog(null, "El Establecimiento es nuevo, primero seleccione un tipo de gasto que lo identifique!");
                }else{
                autoRestarSumarTabla();
                }
            }else{
                autoRestarSumarTabla();
            }
        });
        
        DefaultTableCellRenderer alinearDerecha = new DefaultTableCellRenderer();
        alinearDerecha.setHorizontalAlignment(DefaultTableCellRenderer.RIGHT);
        tablaProductos.getColumnModel().getColumn(1).setCellRenderer(alinearDerecha);

        tablaProductos.getColumnModel().getColumn(2).setCellEditor(new DefaultCellEditor(cmbCeldaTipoGasto));

        tablaProductos.getColumnModel().getColumn(1).setMinWidth(100);
        tablaProductos.getColumnModel().getColumn(1).setMaxWidth(100);
        tablaProductos.getColumnModel().getColumn(2).setMinWidth(150);
        tablaProductos.getColumnModel().getColumn(2).setMaxWidth(150);

        setLocationRelativeTo(getParent());
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

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
        cmbTipoGasto = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jSeparator2 = new javax.swing.JSeparator();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

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

        cmbTipoGasto.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbTipoGastoItemStateChanged(evt);
            }
        });
        cmbTipoGasto.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cmbTipoGastoMouseClicked(evt);
            }
        });
        cmbTipoGasto.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cmbTipoGastoActionPerformed(evt);
            }
        });

        jSeparator1.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Total por Tipo Gasto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        jSeparator2.setBorder(javax.swing.BorderFactory.createTitledBorder(javax.swing.BorderFactory.createEmptyBorder(1, 1, 1, 1), "Seleccionar Tipo de Gasto", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Dialog", 1, 14))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1)
            .addComponent(jSeparator2)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addGap(37, 37, 37)
                            .addComponent(jlabelESTABLECIMIENTO, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(cmbTipoGasto, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(12, 12, 12)
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
                                .addComponent(lblVestimenta)
                                .addComponent(lblAlimentacion)
                                .addComponent(lblOtro))
                            .addGap(59, 59, 59)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(txtOtro, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtAlimentacion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(txtVestimenta, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(523, 523, 523)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(30, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jlabelESTABLECIMIENTO, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cmbTipoGasto, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 17, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
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
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(txtEducacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(lblOtro))
                            .addGroup(layout.createSequentialGroup()
                                .addGap(6, 6, 6)
                                .addComponent(lblEducacion))))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(txtOtro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 217, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton1)
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
        
            

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
           if(validarRegistroFactura()==true){   
            registrarFactura();//Ingresar Establecimiento,Factura,Producto y Detalle
            JOptionPane.showMessageDialog(null, "Factura Ingresada Exitosamente!!!");
            recargar(conTipo);
            this.dispose();//salir de ventana
           }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void cmbTipoGastoItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbTipoGastoItemStateChanged
        // TODO add your handling code here:
        if(this.activarCmbTipoGasto==true){
            if(evt.getStateChange()==1){
                this.TIPO_GASTO_ESTABLECIMIENTO=evt.getItem().toString();    
            }
        }
        
    }//GEN-LAST:event_cmbTipoGastoItemStateChanged

    private void cmbTipoGastoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_cmbTipoGastoMouseClicked
        //Activar el cambio de item solamente cuando se haya iniciado la ventana
        //y se de click sobre el combobox Tipo de Gasto 
        this.activarCmbTipoGasto=true;
    }//GEN-LAST:event_cmbTipoGastoMouseClicked

    private void cmbTipoGastoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cmbTipoGastoActionPerformed
        // TODO add your handling code here:
        //si al arrancar ventana y activarCmbTipoGasto =true
        //se produce un error con this.tipos ==null
        //por eso activarCmbTipoGasto inicia en false;
        if (this.activarCmbTipoGasto ==true){
            for (Object[] tipo : this.tipos) {
            tipo[2] = TIPO_GASTO_ESTABLECIMIENTO; //Familia o TIPO_Familia
            }
         this.autoRestarSumarTabla();
         jScrollPane1.setViewportView(tablaProductos);
        }
            
    }//GEN-LAST:event_cmbTipoGastoActionPerformed

    /*Me permite validar la factura antes de ingresarlo a la base de datos!!!*/
    private boolean validarRegistroFactura(){
        boolean validar = true;
        //Si el establecimiento es nuevo se debe seleccionar un TipoDeGasto que lo identique
        if( nuevoEstablecimiento==true){
            if(cmbTipoGasto.getSelectedItem().toString().equals("")){
                JOptionPane.showMessageDialog(null, "Establecimiento nuevo, por favor seleccione un TipoDeGasto que lo identifique!");
                validar =false;
            }
        }
        //Si la tabla de productos no ha sido llenada, pedir que se complete primero antes de registrar la factura!!
        int filasTotales = tablaProductos.getRowCount();
        for (int i = 0; i < filasTotales; i++) {
            //if (validar==false) break;//No tiene caso comparar, si el establecimiento no ha sido seleccionado!!! 
            if (tablaProductos.getValueAt(i, 2).equals("")) {
                validar = false;
                JOptionPane.showMessageDialog(null, "No se ha seleccionado el Tipo de Gasto para cada producto");
                break;
            }
        }
        
        //Verificar que el campo tipo Gasto sea uno de los items de tipoFactura(Personal)
        for(int i=0;i<filasTotales;i++){
            if (validar==false) break;//No tiene caso comparar, si el establecimiento no ha sido seleccionado, o no se ha completado todos los campos!!! 
            //Comparar que el campo Tipo Gasto pertenezca a un solo tipo Personal o Negocio y no a ambos!!!
            boolean existe=false;
            for (String itemGasto : this.itemsGasto) {
                if (itemGasto.equals(this.tablaProductos.getValueAt(i,2)) ==true) {
                    existe = true;
                    break;
                }
            }
            if (existe == false){
                JOptionPane.showMessageDialog(null, "Existe campos que no corresponden a tipo de Gasto "+TIPO_FACTURA);
                validar = false;
                break;
            }
        }
        return validar;
    }
    
    public void registrarFactura(){
        
        //Insertar Historial Gastos Personales
        String sqlHistorial;
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
                sqlHistorial = "UPDATE HISTORIAL_PAGOS_PERSONALES SET total_alimentacion=total_alimentacion+" + totales[3] + ","
                        + "total_salud=total_salud+" + totales[1] + ","
                        + "total_vivienda=total_vivienda+" + totales[0] + ","
                        + "total_educacion=total_educacion+" + totales[2] + ","
                        + "total_vestimenta=total_vestimenta+" + totales[4] + ","
                        + "total_otros=total_otros+" + totales[5] + " WHERE anio_historial_p=" + anio + " AND id_cliente='" + ID_CLIENTE + "'";
            } else {
                sqlHistorial = "INSERT INTO HISTORIAL_PAGOS_PERSONALES VALUES (" + anio + ",'" + ID_CLIENTE + "'," + totales[3] + "," + totales[1] + "," + totales[0] + "," + totales[2] + "," + totales[4] + "," + totales[5] + ")";
            }
            //Insertar HistorialTipoGastoPersonal!!!
        this.conTipo.insertar(sqlHistorial);

        //Insertar Establecimiento
        if (this.conTipo.consultarEstablecimientoPor(ID_ESTABLECIMIENTO).equals("")){
            String sqlInsert = "INSERT INTO ESTABLECIMIENTO VALUES ('"+ID_ESTABLECIMIENTO+
                    "', '"+NOMBRE_ESTABLECIMIENTO+"', '"+TIPO_GASTO_ESTABLECIMIENTO+
                    "', '"+DIRECCION_ESTABLECIMIENTO+"', '"+TELEFONO_ESTABLECIMIENTO+"')";
            this.conTipo.insertar(sqlInsert);
        }else{
            String sqlUpdate = "UPDATE ESTABLECIMIENTO SET TIPO_GASTO_ESTABLECIMIENTO='"+TIPO_GASTO_ESTABLECIMIENTO+"' WHERE ID_ESTABLECIMIENTO='"+ID_ESTABLECIMIENTO+"'";
            this.conTipo.insertar(sqlUpdate);
            
        }
        //Insertar Factura
        if (this.conTipo.verificar_factura(ID_FACTURA)==false){
            String sqlFactura = "INSERT INTO FACTURA VALUES('"+ID_FACTURA+"', '"+ID_CLIENTE+
                    "', '"+ID_ESTABLECIMIENTO+"', '"+TIPO_FACTURA+"', '"+FECHA_EMISION+
                    "', '"+ESTADO_FACTURA+"', '"+AMBIENTE_FACTURA+"', "+TOTAL_SIN_IVA+
                    ", "+IVA+", "+TOTAL_CON_IVA+")";
            this.conTipo.insertar(sqlFactura);
        }
        
        //Insertar Productos y Detalles
        int numProductos =tipos.length;
        //Extraer detalle de los productos y los guarda en la matriz Object [][] detalleProducto
        for (int i =0; i<numProductos;i++){
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
    private void autoRestarSumarTabla(){
    //Cargar autocalificacion con los combobox
        //extraigo la tabla del objeto donde  ocurrio el evento
        for (int row=0;row<tablaProductos.getRowCount();row++){
            
            for (int column=0;column<tablaProductos.getColumnCount();column++){
                
                TableModel model = tablaProductos.getModel();
                Object data = model.getValueAt(row, column);
                
                if (!data.equals("") && column == 2) {
                    //int opc = comboBox.getSelectedIndex();
                    //System.out.println(row);
                    
                    if (!tipoEstado[row].equals("")) {
                        if (tipoEstado[row].equals(Vivienda)) {
                            restarAgregado(txtVivienda, row);
                        }
                        if (tipoEstado[row].equals(Salud)) {
                            restarAgregado(txtSalud, row);
                        }
                        if (tipoEstado[row].equals(Educacion)) {
                            restarAgregado(txtEducacion, row);
                        }
                        if (tipoEstado[row].equals(Alimentacion)) {
                            restarAgregado(txtAlimentacion, row);
                        }
                        if (tipoEstado[row].equals(Vestimenta)) {
                            restarAgregado(txtVestimenta, row);
                        }
                        if (tipoEstado[row].equals(Otro)) {
                            restarAgregado(txtOtro, row);
                        }
                    }
                    if (data.equals(Vivienda)) {
                        sumarAgregado(txtVivienda, row, Vivienda);
                    }
                    if (data.equals(Salud)) {
                        sumarAgregado(txtSalud, row, Salud);
                    }
                    if (data.equals(Educacion)) {
                        sumarAgregado(txtEducacion, row, Educacion);
                    }
                    if (data.equals(Alimentacion)) {
                        sumarAgregado(txtAlimentacion, row, Alimentacion);
                    }
                    if (data.equals(Vestimenta)) {
                        sumarAgregado(txtVestimenta, row, Vestimenta);
                    }
                    if (data.equals(Otro)) {
                        sumarAgregado(txtOtro, row, Otro);
                    }
                }
            }
        }
    }
    
    //Algoritmo para autocompletar
    private void iniciarAutocompletarTabla(){
        //Extraer detalle de los productos y los guarda en la matriz Object [][] 
            for (int i =0; i<this.infoDetalles.size();i++){
                this.tipos[i][0]=this.infoDetalles.get(i).get("descripcion").toString();
                this.tipos[i][1]=Double.parseDouble( this.infoDetalles.get(i).get("precioTotalSinImpuesto").toString() );
                
                //Retorna el tipo de gasto o Familia que pertece un producto de acuerdo a su ID_PRODUCTO
                String tipoFamiliaProducto = this.conTipo.consultarProductoPor( infoDetalles.get(i).get("codigoPrincipal").toString());
                if(!tipoFamiliaProducto.equals("")){
                    tipos[i][2]=tipoFamiliaProducto;//Familia o TIPO_FACTURA de Gasto
                }else {
                    //Familia o Tipo Gasto Factura, devuelve "" si no lo encuenra!!
                    tipos[i][2]=this.TIPO_GASTO_ESTABLECIMIENTO;
                }
                //verificar que la familia del producto o tipo Gasto pertenesca a Peronal o Negocio pero no a ambos
                boolean existe = false;
                for (String itemGasto : this.itemsGasto) {
                    if (itemGasto.equals(tipos[i][2].toString()) ==true) {
                        existe = true;
                        break;
                    }
                }
                if (existe ==false) tipos[i][2]="";
                
                this.tipos[i][3]=this.infoDetalles.get(i).get("codigoPrincipal").toString();
                this.tipos[i][4]=(int)Double.parseDouble( this.infoDetalles.get(i).get("cantidad").toString() );
                this.tipos[i][5]=Double.parseDouble( this.infoDetalles.get(i).get("precioUnitario").toString() );
            }
            
            //suma o resta si logra encontrar el tipo de gasto para cada descripcion del producto
            //cabezeras para mostrar en la tabla 
        String nombreCabeceras[] = {"Descripcion", "Precio Total", "Tipo De Gasto"};
        
        this.tipoEstado = new String[this.tipos.length];
        for (int i = 0; i < tipos.length; i++) {
            this.tipoEstado[i] = "";
        }
        
        this.tablaProductos = new JTable(this.tipos, nombreCabeceras) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2;
            }
        };
        //Actualizar la tabla productos!!!
        this.jScrollPane1.setViewportView(tablaProductos);
        //Finalemente calcular Total de cada tipo gasto!!
        autoRestarSumarTabla();
        
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
        total = BigDecimal.valueOf(total).setScale(4, RoundingMode.HALF_UP).doubleValue();
        txtField.setText(String.valueOf(total));
    }

    private void sumarAgregado(JTextField txtField, int row, String tipo) {
        double total;
        total = Double.parseDouble(txtField.getText());
        total += (Double) tablaProductos.getValueAt(row, 1);
        total = BigDecimal.valueOf(total).setScale(4, RoundingMode.HALF_UP).doubleValue();
        txtField.setText(String.valueOf(total));
        tipoEstado[row] = tipo;
    }

    public double ingresarTipo(JTextField txtField, JLabel lblLabel) {
        double total;
        String query;

        total = Double.parseDouble(txtField.getText());
        total = BigDecimal.valueOf(total).setScale(4, RoundingMode.HALF_UP).doubleValue();

        query = "INSERT INTO TIPO_GASTO (id_factura,tipo,total)"
                + "VALUES('" + ID_FACTURA + "','" + lblLabel.getText() + "'," + total + ")";

        conTipo.insertar(query);

        return total;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        
        
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoPersonal.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        
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
    private javax.swing.JComboBox<String> cmbTipoGasto;
    private javax.swing.JButton jButton1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
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
