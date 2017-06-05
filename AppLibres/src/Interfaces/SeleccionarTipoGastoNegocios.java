/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Interfaces;

import conexionBDD.Conexion;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
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
public class SeleccionarTipoGastoNegocios extends javax.swing.JFrame {

    final JComboBox comboBox;
    JTable tablaProductos;
    String tipoEstado[];

    String evtTipo = "";
    int filaTipo = -1;

    Conexion conTipo;
    String numFac;
    int anio;
    String cedula, tipo;

    /**
     * Creates new form SeleccionarTipoGasto
     *
     * @param conn
     * @param tipos
     * @param factura
     * @param anio
     * @param cedula
     * @param tipo
     */
    public SeleccionarTipoGastoNegocios(Conexion conn, Object[][] tipos, String factura, int anio, String cedula, String tipo) {
        initComponents();
        this.conTipo = conn;
        this.numFac = factura;
        this.anio = anio;
        this.cedula = cedula;
        this.tipo = tipo;

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
        comboBox.addItem("");

        comboBox.addItem("Mercaderia");
        comboBox.addItem("Arriendo");
        comboBox.addItem("Servicios Basicos");
        comboBox.addItem("Sueldos");
        comboBox.addItem("Movilizacion");
        comboBox.addItem("Viaticos");
        comboBox.addItem("Capacitacion");
        comboBox.addItem("Suministros");
        comboBox.addItem("Herramientas");

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
                        if (tipoEstado[row].equals("Mercaderia")) {
                            restarAgregado(txtMercaderia, row);
                        }
                        if (tipoEstado[row].equals("Arriendo")) {
                            restarAgregado(txtArriendo, row);
                        }
                        if (tipoEstado[row].equals("Servicios Basicos")) {
                            restarAgregado(txtServicios, row);
                        }
                        if (tipoEstado[row].equals("Sueldos")) {
                            restarAgregado(txtSueldos, row);
                        }
                        if (tipoEstado[row].equals("Movilizacion")) {
                            restarAgregado(txtMovilizacion, row);
                        }
                        if (tipoEstado[row].equals("Viaticos")) {
                            restarAgregado(txtViaticos, row);
                        }
                        if (tipoEstado[row].equals("Capacitacion")) {
                            restarAgregado(txtCapacitacion, row);
                        }
                        if (tipoEstado[row].equals("Suministros")) {
                            restarAgregado(txtSuministros, row);
                        }
                        if (tipoEstado[row].equals("Herramientas")) {
                            restarAgregado(txtHerramientas, row);
                        }
                    }

                    if (data.equals("Mercaderia")) {
                        sumarAgregado(txtMercaderia, row, "Mercaderia");
                    }
                    if (data.equals("Arriendo")) {
                        sumarAgregado(txtArriendo, row, "Arriendo");
                    }
                    if (data.equals("Servicios Basicos")) {
                        sumarAgregado(txtServicios, row, "Servicios Basicos");
                    }
                    if (data.equals("Sueldos")) {
                        sumarAgregado(txtSueldos, row, "Sueldos");
                    }
                    if (data.equals("Movilizacion")) {
                        sumarAgregado(txtMovilizacion, row, "Movilizacion");
                    }
                    if (data.equals("Viaticos")) {
                        sumarAgregado(txtViaticos, row, "Viaticos");
                    }
                    if (data.equals("Capacitacion")) {
                        sumarAgregado(txtCapacitacion, row, "Capacitacion");
                    }
                    if (data.equals("Suministros")) {
                        sumarAgregado(txtSuministros, row, "Suministros");
                    }
                    if (data.equals("Herramientas")) {
                        sumarAgregado(txtHerramientas, row, "Herramientas");
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
        lblMercaderia = new javax.swing.JLabel();
        txtMercaderia = new javax.swing.JTextField();
        lblArriendo = new javax.swing.JLabel();
        txtArriendo = new javax.swing.JTextField();
        lblServicios = new javax.swing.JLabel();
        txtServicios = new javax.swing.JTextField();
        lblSueldos = new javax.swing.JLabel();
        txtSueldos = new javax.swing.JTextField();
        lblMovilizacion = new javax.swing.JLabel();
        txtMovilizacion = new javax.swing.JTextField();
        lblViaticos = new javax.swing.JLabel();
        txtViaticos = new javax.swing.JTextField();
        lblCapacitacion = new javax.swing.JLabel();
        txtCapacitacion = new javax.swing.JTextField();
        lblSuministros = new javax.swing.JLabel();
        txtSuministros = new javax.swing.JTextField();
        lblHerramientas = new javax.swing.JLabel();
        txtHerramientas = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jLabel1.setText("SELECCIONAR TIPO DE GASTO");

        jButton1.setText("Aceptar");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        lblMercaderia.setText("Mercaderia");

        txtMercaderia.setEditable(false);
        txtMercaderia.setText("0.0");

        lblArriendo.setText("Arriendo");

        txtArriendo.setEditable(false);
        txtArriendo.setText("0.0");

        lblServicios.setText("Servicios Basicos");

        txtServicios.setEditable(false);
        txtServicios.setText("0.0");

        lblSueldos.setText("Sueldos");

        txtSueldos.setEditable(false);
        txtSueldos.setText("0.0");

        lblMovilizacion.setText("Movilizacion");

        txtMovilizacion.setEditable(false);
        txtMovilizacion.setText("0.0");

        lblViaticos.setText("Viaticos");

        txtViaticos.setEditable(false);
        txtViaticos.setText("0.0");

        lblCapacitacion.setText("Capacitacion");

        txtCapacitacion.setEditable(false);
        txtCapacitacion.setText("0.0");

        lblSuministros.setText("Sumistros");

        txtSuministros.setEditable(false);
        txtSuministros.setText("0.0");

        lblHerramientas.setText("Herramientas");

        txtHerramientas.setEditable(false);
        txtHerramientas.setText("0.0");

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
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 556, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(512, 512, 512)
                        .addComponent(jButton1))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(12, 12, 12)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMercaderia)
                            .addComponent(lblArriendo)
                            .addComponent(lblServicios)
                            .addComponent(lblSueldos)
                            .addComponent(lblMovilizacion))
                        .addGap(46, 46, 46)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(txtMovilizacion)
                                .addGap(304, 304, 304))
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtMercaderia, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(65, 65, 65)
                                        .addComponent(lblViaticos))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtArriendo, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(65, 65, 65)
                                        .addComponent(lblCapacitacion))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtServicios, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(65, 65, 65)
                                        .addComponent(lblSuministros))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(txtSueldos, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(65, 65, 65)
                                        .addComponent(lblHerramientas)))
                                .addGap(51, 51, 51)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(txtHerramientas, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtSuministros, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtCapacitacion, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(txtViaticos, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(12, 12, 12))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(jLabel1)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtMercaderia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtViaticos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMercaderia)
                            .addComponent(lblViaticos))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtArriendo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtCapacitacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblArriendo)
                            .addComponent(lblCapacitacion))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtServicios, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtSuministros, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblServicios)
                            .addComponent(lblSuministros))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtSueldos, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtHerramientas, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblSueldos)
                            .addComponent(lblHerramientas))))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(lblMovilizacion))
                    .addComponent(txtMovilizacion, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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

            double totales[] = {0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0};

            if (!txtMercaderia.getText().equals("0.0")) {
                totales[0] = ingresarTipo(txtMercaderia, lblMercaderia);
            }
            if (!txtArriendo.getText().equals("0.0")) {
                totales[1] = ingresarTipo(txtArriendo, lblArriendo);
            }
            if (!txtServicios.getText().equals("0.0")) {
                totales[2] = ingresarTipo(txtServicios, lblServicios);
            }
            if (!txtSueldos.getText().equals("0.0")) {
                totales[3] = ingresarTipo(txtSueldos, lblSueldos);
            }
            if (!txtMovilizacion.getText().equals("0.0")) {
                totales[4] = ingresarTipo(txtMovilizacion, lblMovilizacion);
            }
            if (!txtViaticos.getText().equals("0.0")) {
                totales[5] = ingresarTipo(txtViaticos, lblViaticos);
            }
            if (!txtCapacitacion.getText().equals("0.0")) {
                totales[6] = ingresarTipo(txtCapacitacion, lblCapacitacion);
            }
            if (!txtSuministros.getText().equals("0.0")) {
                totales[7] = ingresarTipo(txtSuministros, lblSuministros);
            }
            if (!txtHerramientas.getText().equals("0.0")) {
                totales[8] = ingresarTipo(txtHerramientas, lblHerramientas);
            }

            if (conTipo.verificar_usuario("SELECT * FROM HISTORIAL_PAGOS_NEGOCIOS WHERE anio_historial_n=" + anio + " AND id_cliente='" + cedula + "'")) {
                query = "UPDATE HISTORIAL_PAGOS_NEGOCIOS SET total_mercaderia=total_mercaderia+" + totales[0] + "::money,"
                        + "total_arriendo=total_arriendo+" + totales[1] + "::money,"
                        + "total_servicios=total_servicios+" + totales[2] + "::money,"
                        + "total_sueldos=total_sueldos+" + totales[3] + "::money,"
                        + "total_movilizacion=total_movilizacion+" + totales[4] + "::money,"
                        + "total_viaticos=total_viaticos+" + totales[5] + "::money,"
                        + "total_capacitacion=total_capacitacion+" + totales[6] + "::money,"
                        + "total_suministros=total_suministros+" + totales[7] + "::money,"
                        + "total_herramientas=total_herramientas+" + totales[8] + "::money WHERE anio_historial_n=" + anio + " AND id_cliente='" + cedula + "'";
            } else {
                query = "INSERT INTO HISTORIAL_PAGOS_NEGOCIOS VALUES (" + anio + ",'" + cedula + "'," + totales[0] + "," + totales[1] + "," + totales[2] + "," + totales[3] + "," + totales[4] + "," + totales[5] + "," + totales[6] + "," + totales[7] + ", " + totales[8] + ")";
            }

            conTipo.insertar(query);
            recargar(conTipo);
            JOptionPane.showMessageDialog(this, "Factura ingresada exitosamente");
            this.dispose();
        } else {
            JOptionPane.showMessageDialog(this, "No se ha seleccionado el tipo para cada producto");
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void recargar(Conexion conn) {
         ArrayList auxRec = new ArrayList();
        Interfaces.FacturaManualPersonal.combo_Establecimientos.removeAllItems();
        Interfaces.FacturaManualNegocio.combo_Establecimientos.removeAllItems();
        Interfaces.FacturaManualPersonal.combo_Establecimientos.addItem("");
        Interfaces.FacturaManualNegocio.combo_Establecimientos.addItem("");
        auxRec = conn.cargarEstablecimiento();
        for (Object est : auxRec) {            
            Interfaces.FacturaManualPersonal.combo_Establecimientos.addItem(est.toString());
            Interfaces.FacturaManualNegocio.combo_Establecimientos.addItem(est.toString());
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

    public double ingresarTipo(JTextField txtField, JLabel lblLabel) {
        double total;
        String query;

        total = Double.parseDouble(txtField.getText());
        total = BigDecimal.valueOf(total).setScale(3, RoundingMode.HALF_UP).doubleValue();

        query = "INSERT INTO TIPO_GASTO (id_factura,tipo,total)"
                + "VALUES('" + numFac + "','" + lblLabel.getText() + "'," + total + ")";

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
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoNegocios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoNegocios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoNegocios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(SeleccionarTipoGastoNegocios.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
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
    private javax.swing.JLabel lblArriendo;
    private javax.swing.JLabel lblCapacitacion;
    private javax.swing.JLabel lblHerramientas;
    private javax.swing.JLabel lblMercaderia;
    private javax.swing.JLabel lblMovilizacion;
    private javax.swing.JLabel lblServicios;
    private javax.swing.JLabel lblSueldos;
    private javax.swing.JLabel lblSuministros;
    private javax.swing.JLabel lblViaticos;
    private javax.swing.JTextField txtArriendo;
    private javax.swing.JTextField txtCapacitacion;
    private javax.swing.JTextField txtHerramientas;
    private javax.swing.JTextField txtMercaderia;
    private javax.swing.JTextField txtMovilizacion;
    private javax.swing.JTextField txtServicios;
    private javax.swing.JTextField txtSueldos;
    private javax.swing.JTextField txtSuministros;
    private javax.swing.JTextField txtViaticos;
    // End of variables declaration//GEN-END:variables
}
