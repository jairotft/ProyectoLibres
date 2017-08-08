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
    Object[][] datosProducto;// Descripcion, precioSinIva, Familia
    String evtTipo = "";
    int filaTipo = -1;

    Conexion conTipo;//Declarar una nueva conexión
    //ATRIBUTOS QUE VAN HA SER INGRESADOS EN LA BASE DA DATOS!!!
    String numFac;
    int anio;
    String cedula;
    //Información de la factura XML
    HashMap<String,String> infoEncabezado;
    HashMap<String,String> infoTributaria;
    HashMap<String,String> infoFactura;
    //informacion de la lista de productos 
    //guardar cada detalle( o info de Producto) en la lista infoDetalles
    ArrayList<HashMap> infoDetalles;

    /**
     * Creates new form SeleccionarTipoGasto
     *
     * @param infoEncabezado     
     * @param infoTributaria     
     * @param infoFactura     
     * @param infoDetalles
     */
    public SeleccionarTipoGastoPersonal(HashMap<String,String> infoEncabezado, HashMap<String,String> infoTributaria,HashMap<String,String> infoFactura,ArrayList<HashMap> infoDetalles) {
        initComponents();
        
        this.conTipo = new Conexion();//Iniciar conexion con base de datos!!
        this.infoEncabezado=infoEncabezado;
        this.infoTributaria=infoTributaria;
        this.infoFactura=infoFactura;
        this.infoDetalles=infoDetalles;
        //Conexion conn, Object[][] tipos, String numFactura, int anio, String cedula, String tipo
        //el arreglo tipos tiene 3 columnas Descripcion o NombreProducto, precioSiniva, Familia o tipo de Gasto
        
        int numeroProductos =this.infoDetalles.size();
        this.datosProducto =new Object[numeroProductos][6];
        
        //Extraer detalle de los productos y los guarda en la matriz Object [][] detalleProducto
        for (int i =0; i<numeroProductos;i++){
                this.datosProducto[i][0]=this.infoDetalles.get(i).get("descripcion");
                this.datosProducto[i][1]=Double.parseDouble((String) this.infoDetalles.get(i).get("precioTotalSinImpuesto"));
                this.datosProducto[i][2]=this.conTipo.consultarProductoPor(this.infoDetalles.get(i).get("codigoPrincipal").toString());//Familia o tipo de Gasto
                this.datosProducto[i][3]=this.infoDetalles.get(i).get("codigoPrincipal");
                this.datosProducto[i][4]=this.infoDetalles.get(i).get("cantidad");
                this.datosProducto[i][5]=this.infoDetalles.get(i).get("precioUnitario");
        }
     
        //this.conTipo = conn;
        this.numFac = this.infoTributaria.get("estab")+"-"+this.infoTributaria.get("ptoEmi")+"-"+this.infoTributaria.get("secuencial");
        this.anio = Integer.parseInt( infoFactura.get("fechaEmision").substring(0, 4) ); //los 4 caracteres son el numero
        this.cedula = infoFactura.get("identificacionComprador");
        
        String nombreCabeceras[] = {"Descripcion", "Precio Total", "Tipo de Gasto"};

        
        tipoEstado = new String[this.datosProducto.length];
        for (int i = 0; i < this.datosProducto.length; i++) {
            tipoEstado[i] = "";
        }
        
        //ingresa cada detalle en la tablaProductos
        tablaProductos = new JTable(this.datosProducto, nombreCabeceras) {
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
//
//        //Cargar autocalificacion con los combobox
//        //extraigo la tabla del objeto donde  ocurrio el evento
//        for (int row=0;row<tablaProductos.getRowCount();row++){
//            for (int column=0;column<tablaProductos.getColumnCount();column++){
//                
//                TableModel model = tablaProductos.getModel();
//                Object data = model.getValueAt(row, column);
//                if (!data.equals("") && column == 2) {
//                    //int opc = comboBox.getSelectedIndex();
//                    //System.out.println(row);
//
//                    if (data.equals("Vivienda")) {
//                        sumarAgregado(txtVivienda, row, "Vivienda");
//                    }
//                    if (data.equals("Salud")) {
//                        sumarAgregado(txtSalud, row, "Salud");
//                    }
//                    if (data.equals("Educacion")) {
//                        sumarAgregado(txtEducacion, row, "Educacion");
//                    }
//                    if (data.equals("Alimentacion")) {
//                        sumarAgregado(txtAlimentacion, row, "Alimentacion");
//                    }
//                    if (data.equals("Vestimenta")) {
//                        sumarAgregado(txtVestimenta, row, "Vestimenta");
//                    }
//                    if (data.equals("Otro")) {
//                        sumarAgregado(txtOtro, row, "Otro");
//                    }
//                }
//            
//            }
//        }
//        
        //Evento que suma o resta automáticamente seleccionar la Familia(Vivienda,alimentacion ...)
        tablaProductos.getModel().addTableModelListener(new TableModelListener() {
            @Override
            //Cuando se cambia un campo de la tabla
            public void tableChanged(TableModelEvent tme) {
                int row = tme.getFirstRow();//fila a la que pertenece el campo
                int column = tme.getColumn();//columna del campo

                //extraigo la tabla del objeto donde  ocurrio el evento
                TableModel model = (TableModel) tme.getSource();
                //creo un objeto para extraerla fila y columna del campo
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
                        datosProducto[row][column]="Vivienda";
                    }
                    if (data.equals("Salud")) {
                        sumarAgregado(txtSalud, row, "Salud");
                        datosProducto[row][column]="Salud";
                    }
                    if (data.equals("Educacion")) {
                        sumarAgregado(txtEducacion, row, "Educacion");
                        datosProducto[row][column]="Educacion";
                    }
                    if (data.equals("Alimentacion")) {
                        sumarAgregado(txtAlimentacion, row, "Alimentacion");
                        datosProducto[row][column]="Alimentacion";
                    }
                    if (data.equals("Vestimenta")) {
                        sumarAgregado(txtVestimenta, row, "Vestimenta");
                        datosProducto[row][column]="Vestimenta";
                    }
                    if (data.equals("Otro")) {
                        sumarAgregado(txtOtro, row, "Otro");
                        datosProducto[row][column]="Otro";
                    }
                }

            }
        });

        //Alinear la tabla nada mas!!! :D
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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(jLabel1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblVivienda)
                        .addGap(95, 95, 95)
                        .addComponent(txtVivienda, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(lblAlimentacion)
                        .addGap(56, 56, 56)
                        .addComponent(txtAlimentacion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblSalud)
                        .addGap(111, 111, 111)
                        .addComponent(txtSalud, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(lblVestimenta)
                        .addGap(66, 66, 66)
                        .addComponent(txtVestimenta, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addComponent(lblEducacion)
                        .addGap(85, 85, 85)
                        .addComponent(txtEducacion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(65, 65, 65)
                        .addComponent(lblOtro)
                        .addGap(104, 104, 104)
                        .addComponent(txtOtro, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(512, 512, 512)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtVivienda, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtAlimentacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblVivienda)
                            .addComponent(lblAlimentacion))))
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
                    .addComponent(txtEducacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtOtro, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblEducacion)
                            .addComponent(lblOtro))))
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
        
        //Registrar el Historial de Tipo de Gastos en nuestro DBB
        registrarHistorialTipoGastos();
            //Ingresar los datosProducto
           
        
    }//GEN-LAST:event_jButton1ActionPerformed

    private boolean registrarHistorialTipoGastos(){
        
        int filasTotales = this.tablaProductos.getRowCount();
        boolean validado = true;
        //Verifica que todas las filas y productos tengan su respectiva familia o TipoDeGasto
        for (int i = 0; i < filasTotales; i++) {
            if (this.tablaProductos.getValueAt(i, 2).equals("")) {
                validado = false;
                break;
            }
        }

        if (validado == true) {
            String query;

            double totales[] = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

            if (!txtAlimentacion.getText().equals("0.0")) {
                totales[0] = ingresarTipoGasto(txtAlimentacion, lblAlimentacion);
            } 
            if (!txtSalud.getText().equals("0.0")) {
                totales[1] = ingresarTipoGasto(txtSalud, lblSalud);
            }
            
            if (!txtVivienda.getText().equals("0.0")) {
                totales[2] = ingresarTipoGasto(txtVivienda, lblVivienda);
            }
           
            if (!txtEducacion.getText().equals("0.0")) {
                totales[3] = ingresarTipoGasto(txtEducacion, lblEducacion);
            }
           
            if (!txtVestimenta.getText().equals("0.0")) {
                totales[4] = ingresarTipoGasto(txtVestimenta, lblVestimenta);
            }
            if (!txtOtro.getText().equals("0.0")) {
                totales[5] = ingresarTipoGasto(txtOtro, lblOtro);
            }

            if (conTipo.verificar_usuario("SELECT * FROM HISTORIAL_PAGOS_PERSONALES WHERE anio_historial_p=" + this.anio + " AND id_cliente='" + this.cedula + "'")) {
                query = "UPDATE HISTORIAL_PAGOS_PERSONALES SET total_alimentacion=total_alimentacion+" + totales[0] + ","
                        + "total_salud=total_salud+" + totales[1] + ","
                        + "total_vivienda=total_vivienda+" + totales[2] + ","
                        + "total_educacion=total_educacion+" + totales[3] + ","
                        + "total_vestimenta=total_vestimenta+" + totales[4] + ","
                        + "total_otros=total_otros+" + totales[5] + " WHERE anio_historial_p=" + this.anio + " AND id_cliente='" + this.cedula + "'";
            } else {
                query = "INSERT INTO HISTORIAL_PAGOS_PERSONALES VALUES (" + this.anio + ",'" + this.cedula + "'," + totales[0] + "," + totales[1] + "," + totales[2] + "," + totales[3] + "," + totales[4] + "," + totales[5] + ")";
            }
            
            //JOptionPane.showMessageDialog(null, query);

            conTipo.insertar(query);
            //Registrar el Historial de Tipo de Gastos en nuestro DBB
            //Ingresar los datosFactura
                registrarFactura();
             
            JOptionPane.showMessageDialog(this, "Factura ingresada exitosamente");
            recargar(conTipo);
            this.dispose();//Oculta el Frame o la ventana SeleccionarTipoGasto
            
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado el tipo de gasto para cada producto");
            validado=false;
        }
        return validado;
    }
    //Registrar Nuevo Producto
    @SuppressWarnings("UnusedAssignment")
    private void registrarFactura(){
        //Tabla ESTABLECIMIENTO ===============================
        String ID_ESTABLECIMIENTO="";
        String NOMBRE_ESTABLECIMIENTO, TIPO_GASTO_ESTABLECIMIENTO, DIRECCION_ESTABLECIMIENTO, TELEFONO_ESTABLECIMIENTO;
        ID_ESTABLECIMIENTO= infoTributaria.get("ruc");
        NOMBRE_ESTABLECIMIENTO = infoTributaria.get("razonSocial");
        TIPO_GASTO_ESTABLECIMIENTO="";
        DIRECCION_ESTABLECIMIENTO=infoTributaria.get("dirMatriz");//Los ultimos 10 digitos son el telefono en algunas facturas
        TELEFONO_ESTABLECIMIENTO=infoTributaria.get("dirMatriz").substring(infoTributaria.get("dirMatriz").length()-10);
        
        String sqlEstablecimiento="INSERT INTO ESTABLECIMIENTO VALUES('"+ID_ESTABLECIMIENTO+
                "', '"+NOMBRE_ESTABLECIMIENTO+"', '"+TIPO_GASTO_ESTABLECIMIENTO+
                "', '"+DIRECCION_ESTABLECIMIENTO+"', '"+TELEFONO_ESTABLECIMIENTO+"')";
        
        String sqlEstablecimientoUpdate="UPDATE ESTABLECIMIENTO SET "
                + "TIPO_GASTO_ESTABLECIMIENTO='"+TIPO_GASTO_ESTABLECIMIENTO+"' WHERE ID_ESTABLECIMIENTO='"+ID_ESTABLECIMIENTO+"'";

        /// Insertar datos en Tabla Establecimiento------------
        if( conTipo.consultarEstablecimientoPor(ID_ESTABLECIMIENTO).equals("") ){
            //JOptionPane.showMessageDialog(null, sqlEstablecimiento);
            conTipo.insertar(sqlEstablecimiento);
        }else{//si devuelve un numero ya existe, solo actualizamos su tipo_gasto_establecimiento
            //JOptionPane.showMessageDialog(null, sqlEstablecimientoUpdate);
            conTipo.insertar(sqlEstablecimientoUpdate);
        }
        
        
        //Tabla Factura===============================
        String ID_FACTURA="", ID_CLIENTE="";
        //String ID_ESTABLECIMIENTO ya definido para tabla Establecimiento
        String TIPO_FACTURA="";
        String FECHA_EMISION="", ESTADO_FACTURA="", AMBIENTE_FACTURA="";
        double TOTAL_SIN_IVA=0.0, IVA=0.0, TOTAL_CON_IVA=0.0;
        
        ID_FACTURA=this.numFac;
        ID_CLIENTE=this.cedula;
        ID_ESTABLECIMIENTO=this.infoTributaria.get("ruc");
        TIPO_FACTURA="Personal";
        FECHA_EMISION=this.infoFactura.get("fechaEmision");
        ESTADO_FACTURA=this.infoEncabezado.get("estado");
        AMBIENTE_FACTURA=this.infoEncabezado.get("ambiente");
        TOTAL_SIN_IVA=Double.parseDouble( this.infoFactura.get("totalSinImpuestos") );
        IVA=Double.parseDouble( this.infoFactura.get("valor") );//totalConImpuestos
        TOTAL_CON_IVA=Double.parseDouble( this.infoFactura.get("importeTotal") );
        
        String sqlFactura="INSERT INTO FACTURA VALUES('"+ID_FACTURA+
                "', '"+ID_CLIENTE+"', '"+ID_ESTABLECIMIENTO+"', '"+TIPO_FACTURA+
                "', '"+FECHA_EMISION+"', '"+ESTADO_FACTURA+"', '"+AMBIENTE_FACTURA+
                "', "+TOTAL_SIN_IVA+", "+IVA+", "+TOTAL_CON_IVA+")";
        
        /// Insertar datos en Tabla Factura------------
        //JOptionPane.showMessageDialog(null, sqlFactura);
        conTipo.insertar(sqlFactura);
        
        //Tabla Producto y Detalle=========================================
        String ID_PRODUCTO="", NAME_PRODUCTO="", FAMILIA="";
        double PRECIO_UNITARIO=0.0;
       
        //Tabla Detalle
        int ID_DETALLE=0;
        //ID_FACTURA
        //ID_PRODUCTO
        int CANTIDAD=0;
        double TOTAL=0.0;
        
        
        for (Object[] producto : this.datosProducto) {
            
            NAME_PRODUCTO = producto[0].toString(); //this.infoDetalles.get(i).get("descripcion");
            TOTAL = Double.parseDouble(producto[1].toString()); //Double.parseDouble((String) this.infoDetalles.get(i).get("precioTotalSinImpuesto"));
            FAMILIA = producto[2].toString(); //Familia o tipo de Gasto
            ID_PRODUCTO = producto[3].toString(); //=this.infoDetalles.get(i).get("codigoPrincipal");
            CANTIDAD = Integer.parseInt(producto[4].toString()); //this.infoDetalles.get(i).get("cantidad");
            PRECIO_UNITARIO = Double.parseDouble(producto[5].toString()); //this.infoDetalles.get(i).get("precioUnitario");
            
             String sqlProducto="INSERT INTO PRODUCTO VALUES('"+ID_PRODUCTO+"', '"+NAME_PRODUCTO+"', '"+FAMILIA+"', "+PRECIO_UNITARIO+")";
            String sqlProductoUpdate="update Producto set familia='"+FAMILIA+"' where id_producto='"+ID_PRODUCTO+"'";
        
            
            //Si ya existe el producto solamene actualizar Familia---------
            if (conTipo.consultarProductoPor( ID_PRODUCTO).equals(FAMILIA)){
                //JOptionPane.showMessageDialog(null, sqlProductoUpdate);
                conTipo.insertar(sqlProductoUpdate);
            }else{//Si no existe familia o producto
                //JOptionPane.showMessageDialog(null, sqlProducto);
                conTipo.insertar(sqlProducto);
            }
            //Insertar en tabla Detalle 
            
            String sqlDetalle="INSERT INTO DETALLE VALUES('"+ID_FACTURA+"', '"+ID_PRODUCTO+"', "+CANTIDAD+", "+TOTAL+")";
            
            JOptionPane.showMessageDialog(null, sqlDetalle);
            conTipo.insertar(sqlDetalle);
        }
                               
        
    }
    
    private void recargar(Conexion conn) {
         ArrayList auxRec = new ArrayList();
        FacturaManualPersonal.combo_Establecimientos.removeAllItems();
        FacturaManualNegocio.combo_Establecimientos.removeAllItems();
        FacturaManualPersonal.combo_Establecimientos.addItem("");
        FacturaManualNegocio.combo_Establecimientos.addItem("");
        auxRec = conn.cargarEstablecimiento();
        for (Object est : auxRec) {            
            FacturaManualPersonal.combo_Establecimientos.addItem(est.toString());
            FacturaManualNegocio.combo_Establecimientos.addItem(est.toString());
        }
    }
    
    public void restarAgregado(JTextField txtField, int row) {
        double total;
        total = Double.parseDouble(txtField.getText());
        total -= (Double) tablaProductos.getValueAt(row, 1);
        total = BigDecimal.valueOf(total).setScale(3, RoundingMode.HALF_UP).doubleValue();
        txtField.setText(String.valueOf(total));
    }

    public void sumarAgregado(JTextField txtField, int row, String tipo) {
        double total;
        total = Double.parseDouble(txtField.getText());
        total += (Double) tablaProductos.getValueAt(row, 1);
        total = BigDecimal.valueOf(total).setScale(3, RoundingMode.HALF_UP).doubleValue();
        txtField.setText(String.valueOf(total));
        tipoEstado[row] = tipo;
    }

    public double ingresarTipoGasto(JTextField txtField, JLabel lblLabel) {
        double total;
        String query;

        total = Double.parseDouble(txtField.getText());
        total = BigDecimal.valueOf(total).setScale(3, RoundingMode.HALF_UP).doubleValue();

        query = "INSERT INTO TIPO_GASTO (id_factura,tipo,total)"
                + "VALUES('" + this.numFac + "','" + lblLabel.getText() + "'," + total + ")";

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
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JScrollPane jScrollPane1;
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
