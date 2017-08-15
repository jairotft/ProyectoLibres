/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.libres.aplicacioneslibres.aplicacioneslibres;

import com.libres.aplicacioneslibres.conexionbdd.Conexion;
import com.libres.aplicacioneslibres.interfaces.Reportes;
import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;
import net.sf.jasperreports.engine.util.JRLoader;
import net.sf.jasperreports.export.SimpleExporterInput;
import net.sf.jasperreports.export.SimpleOutputStreamExporterOutput;
import net.sf.jasperreports.export.SimpleXlsxReportConfiguration;
import net.sf.jasperreports.view.JasperViewer;

/**
 *
 * @author mathcrap
 */
public class Reporte {
    private final Conexion conn;
    
    public Reporte(Conexion conn){
        this.conn = conn;
    }
    
    
    public void generar_reporte(String archivo, Map parametros) {
        try {
            //todos los establecimientos
            String path = "src/main/resources/Reportes/" + archivo + ".jasper" ;
            String pathDestinity = "";
            
            String file = JOptionPane.showInputDialog(null, "Ingrese el Nombre del archivo");
            if(file == null || file.equals("")){
                JOptionPane.showMessageDialog(null, "Nombre no ingresado");
                return;
            }
            
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            pathDestinity = f.getAbsolutePath();
            if(pathDestinity == null || pathDestinity.equals("")){
                JOptionPane.showMessageDialog(null, "Direccion no ingresada");
                return;
            }
            pathDestinity += "/" + file + "-reporte.pdf";
             
            JasperReport jr = null;

            jr = (JasperReport) JRLoader.loadObjectFromFile(path);
            
            System.out.println("-------");
            for (Object object : parametros.entrySet()) {
                System.out.println(object);
            }
            
            JasperPrint jp = JasperFillManager.fillReport(jr, parametros, conn.getConn());
            
            JasperExportManager.exportReportToPdfFile(jp, pathDestinity);
            if (Desktop.isDesktopSupported()) {
                try {
                    File myFile = new File(pathDestinity);
                    Desktop.getDesktop().open(myFile);
                } catch (IOException ex) {
                    // no application registered for PDFs
                }
            }
            
            
//            JasperViewer jv = new JasperViewer(jp, false);
//            jv.setVisible(true);
//            jv.setTitle(path);
//            jv.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        } catch (JRException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    
    public void generar_excel(String archivo, Map parametros) {
        try {
            //todos los establecimientos
            String path = "src/main/resources/Reportes/" + archivo + ".jasper" ;
            String pathDestinity = "";
            
            String file = JOptionPane.showInputDialog(null, "Ingrese el Nombre del archivo");
            if(file == null || file.equals("")){
                JOptionPane.showMessageDialog(null, "Nombre no ingresado");
                return;
            }
            
            JFileChooser chooser = new JFileChooser();
            chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
            chooser.showOpenDialog(null);
            File f = chooser.getSelectedFile();
            pathDestinity = f.getAbsolutePath();
            if(pathDestinity == null || pathDestinity.equals("")){
                JOptionPane.showMessageDialog(null, "Direccion no ingresada");
                return;
            }
            pathDestinity += "/" + file + "-reporte.xlsx";
             
            JasperReport jr = null;

            jr = (JasperReport) JRLoader.loadObjectFromFile(path);
            
            System.out.println("-------");
            for (Object object : parametros.entrySet()) {
                System.out.println(object);
            }
            
            JasperPrint jp = JasperFillManager.fillReport(jr, parametros, conn.getConn());
            
            JRXlsxExporter exporter = new JRXlsxExporter();
            exporter.setExporterInput(new SimpleExporterInput(jp));
            File outputFile = new File(pathDestinity);
            exporter.setExporterOutput(new SimpleOutputStreamExporterOutput(outputFile));
            SimpleXlsxReportConfiguration configuration = new SimpleXlsxReportConfiguration(); 
            configuration.setDetectCellType(true);//Set configuration as you like it!!
            configuration.setCollapseRowSpan(false);
            exporter.setConfiguration(configuration);

            exporter.exportReport();
            if (Desktop.isDesktopSupported()) {
                try {
                    File myFile = new File(pathDestinity);
                    Desktop.getDesktop().open(myFile);
                } catch (IOException ex) {
                    // no application registered for PDFs
                }
            }
            
            
//            JasperViewer jv = new JasperViewer(jp, false);
//            jv.setVisible(true);
//            jv.setTitle(path);
//            jv.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        } catch (JRException ex) {
            Logger.getLogger(Reportes.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
}
