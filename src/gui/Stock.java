/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package gui;

import com.formdev.flatlaf.FlatDarkLaf;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import model.MySQL;

/**
 *
 * @author 94701
 */
public class Stock extends javax.swing.JFrame {
    
    
       
    private GRN grn;
    
    public void setGrn(GRN grn){
    
        this.grn = grn;
    
    }
    
    private Invoice invoice;
    
    public void setInvoice(Invoice invoice){
    this.invoice = invoice;
    }
    

    private HashMap<String, String> brandMap = new HashMap<>();
 

    /**
     * Creates new form Stock
     */
    public Stock() {
        initComponents();
        loadBrand();
        loadProduct();
        loadStock();
    }

    private void loadBrand() {
        try {

            ResultSet brandRs = MySQL.execute("SELECT * FROM `brand`");
            Vector<String> vector = new Vector<>();
            vector.add("Select Brand");
            while (brandRs.next()) {
                vector.add(brandRs.getString("brand_name"));
                brandMap.put(brandRs.getString("brand_name"), brandRs.getString("brand_id"));

            }

            jComboBox1.setModel(new DefaultComboBoxModel<>(vector));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadProduct() {

        try {
            ResultSet productRs = MySQL.execute("SELECT * FROM `product` INNER JOIN `brand` ON `product`.`brand_brand_id`=`brand`.`brand_id`");
            DefaultTableModel model = (DefaultTableModel) jTable2.getModel();
            model.setRowCount(0);
            while (productRs.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(productRs.getString("product.id"));
                vector.add(productRs.getString("brand_brand_id"));
                vector.add(productRs.getString("brand.brand_name"));
                vector.add(productRs.getString("product.name"));
                model.addRow(vector);
                jTable2.setModel(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void loadStock() {

        try {

            int row = jTable2.getSelectedRow();

            String queary = "SELECT * FROM `stock` INNER JOIN `product` ON `stock`.`product_id`=`product`.`id` "
                    + "INNER JOIN `brand`ON `product`.`brand_brand_id`=`brand`.`brand_id` ";

            if (row != -1) {
                String pid = String.valueOf(jTable2.getValueAt(row, 0));
                queary += "WHERE `stock`.`product_id`='" + pid + "' ";
            }

            if (queary.contains("WHERE")) {
                queary += "AND ";
            } else {
                queary += "WHERE ";
            }

            double min_price = 0;
            double max_price = 0;

            if (!jFormattedTextField1.getText().isEmpty()) {
                min_price = Double.parseDouble(jFormattedTextField1.getText());
            }

            if (!jFormattedTextField2.getText().isEmpty()) {
                max_price = Double.parseDouble(jFormattedTextField2.getText());
                
            }

            if (min_price > 0 && max_price == 0) {
                queary += "`stock`.`selling_price` > '" + min_price + "'";
            } else if (min_price == 0 && max_price > 0) {
                queary += "`stock`.`selling_price` < '" + max_price + "'";
            } else if (min_price > 0 && max_price > min_price) {
                queary += "`stock`.`selling_price` > '" + min_price + "' AND `stock`.`selling_price`<'" + max_price + "'";
            }

            //exp
            Date start = null;
            Date end = null;

            SimpleDateFormat format = new SimpleDateFormat("yyy-MM-dd");

            if (jDateChooser1.getDate() != null) {
                start = jDateChooser1.getDate();
                queary += "`stock`.`exp` > '" + format.format(start) + "' AND ";
            }
            if (jDateChooser2.getDate() != null) {
                end = jDateChooser2.getDate();
                   queary += "`stock`.`exp` < '" + format.format(end) + "' ";
            }
//            if (start.before(end)) {
//
//            }

            //Stock ID ASC, Stock ID DESC, Brand ASC, Brand DESC, Name ASC, Name DESC,Selling Price ASC,Selling Price DESC,Qty ASC,Qty DESC
            String sort = String.valueOf(jComboBox2.getSelectedItem());

            queary += "ORDER BY ";

            queary = queary.replace("WHERE ORDER BY ", "ORDER BY ");
            queary = queary.replace("AND ORDER BY ", "ORDER BY ");

            if (sort.equals("Stock ID ASC")) {
                queary += "`stock`.`stock_id` ASC";
            } else if (sort.equals("Stock ID DESC")) {
                queary += "`stock`.`stock_id` DESC";
            } else if (sort.equals("Brand ASC")) {
                queary += "`brand`.`brand_name` ASC";
            } else if (sort.equals("Brand DESC")) {
                queary += "`brand`.`brand_name` DESC";
            } else if (sort.equals("Name ASC")) {
                queary += "`product`.`name` ASC";
            } else if (sort.equals("Name DESC")) {
                queary += "`product`.`name` DESC";
            } else if (sort.equals("Selling Price ASC")) {
                queary += "`stock`.`selling_price` ASC";
            } else if (sort.equals("Selling Price DESC")) {
                queary += "`stock`.`selling_price` DESC";
            } else if (sort.equals("Qty ASC")) {
                queary += "`stock`.`qty` ASC";
            } else if (sort.equals("Qty DESC")) {
                queary += "`stock`.`qty` DESC";
            }

            ResultSet productRs = MySQL.execute(queary);
            DefaultTableModel model = (DefaultTableModel) jTable1.getModel();
            model.setRowCount(0);
            while (productRs.next()) {
                Vector<String> vector = new Vector<>();
                vector.add(productRs.getString("stock.stock_id"));
                vector.add(productRs.getString("product_id"));
                vector.add(productRs.getString("brand.brand_name"));
                vector.add(productRs.getString("product.name"));
                vector.add(productRs.getString("selling_price"));
                vector.add(productRs.getString("qty"));
                vector.add(productRs.getString("mfd"));
                vector.add(productRs.getString("exp"));
                model.addRow(vector);
                jTable1.setModel(model);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void resetProductUi() {
        loadStock();
        jTable2.clearSelection();
        jTextField5.setEditable(true);
        jTextField5.setText("");
        jComboBox1.setSelectedIndex(0);
        jTextField6.setText("");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        jTextField5 = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jTextField6 = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jTextField7 = new javax.swing.JTextField();
        jButton3 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton4 = new javax.swing.JButton();
        jDateChooser1 = new com.toedter.calendar.JDateChooser();
        jLabel1 = new javax.swing.JLabel();
        jDateChooser2 = new com.toedter.calendar.JDateChooser();
        jLabel2 = new javax.swing.JLabel();
        jButton5 = new javax.swing.JButton();
        jFormattedTextField1 = new javax.swing.JFormattedTextField();
        jFormattedTextField2 = new javax.swing.JFormattedTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Product Id", "Brand", "Name", "Selling Price", "Quantity", "MFG", "EXP"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jTable1);

        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });
        jTextField5.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField5KeyReleased(evt);
            }
        });

        jLabel6.setText("Product Id");

        jLabel7.setText("Brand");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });
        jTextField6.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField6KeyReleased(evt);
            }
        });

        jLabel8.setText("Product Name");

        jButton1.setText("Add Product");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Update Product");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jTextField7.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField7ActionPerformed(evt);
            }
        });
        jTextField7.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextField7KeyReleased(evt);
            }
        });

        jButton3.setText("jButton3");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Product Id", "Brand Id", "Brand", "Name"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jTable2.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTable2MouseClicked(evt);
            }
        });
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField7)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(jLabel8)
                .addGap(18, 18, 18)
                .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(18, 18, 18)
                .addComponent(jButton2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jScrollPane2)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jButton1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField7, javax.swing.GroupLayout.DEFAULT_SIZE, 30, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton3)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 209, Short.MAX_VALUE))
        );

        jComboBox2.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Stock ID ASC", "Stock ID DESC", "Brand ASC", "Brand DESC", "Name ASC", "Name DESC", "Selling Price ASC", "Selling Price DESC", "Qty ASC", "Qty DESC" }));
        jComboBox2.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboBox2ItemStateChanged(evt);
            }
        });

        jLabel9.setText("Sort By");

        jLabel10.setText("Selling Price");

        jLabel11.setText("To");

        jButton4.setText("Find");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jDateChooser1.setDateFormatString("yyy-MM-dd");

        jLabel1.setText("MFG");

        jDateChooser2.setDateFormatString("yyy-MM-dd");

        jLabel2.setText("EXP");

        jButton5.setText("Find");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jFormattedTextField1.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField1.setText("0");

        jFormattedTextField2.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0.00"))));
        jFormattedTextField2.setText("0");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 211, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addComponent(jFormattedTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel11)
                .addGap(18, 18, 18)
                .addComponent(jFormattedTextField2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton4)
                .addGap(18, 18, 18)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDateChooser1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jDateChooser2, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jDateChooser2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jDateChooser1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel1))
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jButton5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jFormattedTextField1)
                    .addComponent(jFormattedTextField2))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollPane1))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 258, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void jTextField5KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField5KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5KeyReleased

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void jTextField6KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField6KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6KeyReleased

    private void jTextField7ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField7ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7ActionPerformed

    private void jTextField7KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextField7KeyReleased
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField7KeyReleased

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:

        try {
            String brand = jTextField7.getText();

            if (brand.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Pleace enter brand name", "Warning", JOptionPane.WARNING_MESSAGE);
            } else {

                ResultSet resultset = MySQL.execute("SELECT * FROM `brand` WHERE `brand_name`='" + brand + "'");

                if (resultset.next()) {
                    JOptionPane.showMessageDialog(this, "Brand alredy added", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {

                    if (jComboBox1.getSelectedIndex() == 0) {
                        MySQL.execute("INSERT INTO `brand`(`brand_name`)VALUES('" + brand + "')");
                        loadBrand();
                        JOptionPane.showMessageDialog(this, "New Brand Added", "Sucess", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        int response = JOptionPane.showConfirmDialog(this, "Do you want to update this brand?", "Update", JOptionPane.YES_NO_OPTION, JOptionPane.YES_NO_OPTION);
                        if (response == JOptionPane.YES_OPTION) {
                            MySQL.execute("UPDATE `brand` SET `brand_name`='" + brand + "' WHERE `brand_name`='" + String.valueOf(jComboBox1.getSelectedItem()) + "'");
                        }
                    }

                    loadBrand();

                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        String productId = jTextField5.getText();
        String brandName = String.valueOf(jComboBox1.getSelectedItem());
        String productName = jTextField6.getText();

        if (productId.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pleace enter product Id", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (brandName.equals("Select Brand")) {
            JOptionPane.showMessageDialog(this, "Pleace Select brand name", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pleace enter product name", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {

            try {

                ResultSet resultset = MySQL.execute("SELECT * FROM `product` WHERE `id`='" + productId + "'OR (`name`='" + productName + "' AND `brand_brand_id`='" + brandMap.get(brandName) + "') ");

                if (resultset.next()) {
                    JOptionPane.showMessageDialog(this, "Product alredy added", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {

                    MySQL.execute("INSERT INTO `product`(`id`,`brand_brand_id`,`name`)VALUES('" + productId + "','" + brandMap.get(brandName) + "','" + productName + "')");

                    JOptionPane.showMessageDialog(this, "new product add", "Warning", JOptionPane.WARNING_MESSAGE);

                    loadProduct();
                    loadStock();

                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jTable2MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable2MouseClicked
        // TODO add your handling code here:

        int selectedRow = jTable2.getSelectedRow();
        
        if(evt.getClickCount() == 2){
        
            if(grn != null){
            
                grn.getjTextField4().setText(String.valueOf(jTable2.getValueAt(selectedRow, 0)));
                grn.getjTextField6().setText(String.valueOf(jTable2.getValueAt(selectedRow, 2)));
                grn.getjTextField10().setText(String.valueOf(jTable2.getValueAt(selectedRow, 3)));
                grn.getjTextField3().grabFocus();
                this.dispose();
            
            }
        
        }
        
        jTextField5.setText(String.valueOf(jTable2.getValueAt(selectedRow, 0)));
        jComboBox1.setSelectedItem(String.valueOf(jTable2.getValueAt(selectedRow, 2)));
        jTextField6.setText(String.valueOf(jTable2.getValueAt(selectedRow, 3)));
        jTextField5.setEditable(false);
        loadStock();
    }//GEN-LAST:event_jTable2MouseClicked

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:

        String productId = jTextField5.getText();
        String brandName = String.valueOf(jComboBox1.getSelectedItem());
        String productName = jTextField6.getText();

        if (brandName.equals("Select Brand")) {
            JOptionPane.showMessageDialog(this, "Pleace Select brand name", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (productName.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Pleace enter Product name", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {

            try {

                ResultSet resultset = MySQL.execute("SELECT * FROM `product` WHERE `name`='" + productName + "' AND `brand_brand_id`='" + brandMap.get(brandName) + "' AND `id`!='" + productId + "' ");

                if (resultset.next()) {
                    JOptionPane.showMessageDialog(this, "Product alredy added", "Warning", JOptionPane.WARNING_MESSAGE);
                } else {

                    MySQL.execute("UPDATE `product` SET `brand_brand_id`='" + brandMap.get(brandName) + "',`name`='" + productName + "' WHERE `id`='" + productId + "' ");

                    JOptionPane.showMessageDialog(this, "Product Updated", "Sucess", JOptionPane.INFORMATION_MESSAGE);

                    loadProduct();

                    resetProductUi();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jComboBox2ItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboBox2ItemStateChanged
        // TODO add your handling code here:
        loadStock();

    }//GEN-LAST:event_jComboBox2ItemStateChanged

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        // TODO add your handling code here:
        String price_min = jFormattedTextField1.getText();
        String price_max = jFormattedTextField2.getText();

        if (price_min.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter min price", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (price_max.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter max price", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (Double.parseDouble(price_min) > Double.parseDouble(price_max)) {
            JOptionPane.showMessageDialog(this, "Please enter max price", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            loadStock();
        }

    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        // TODO add your handling code here:

        Date date1 = jDateChooser1.getDate();
        Date date2 = jDateChooser2.getDate();

        if (date1 == null) {
            JOptionPane.showMessageDialog(this, "Please enter start date", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (date2 == null) {
            JOptionPane.showMessageDialog(this, "Please enter ending date", "Warning", JOptionPane.WARNING_MESSAGE);
        } else if (date1.after(date2)) {
            JOptionPane.showMessageDialog(this, "Start date greater than end date", "Warning", JOptionPane.WARNING_MESSAGE);
        } else {
            loadStock();
        }
    }//GEN-LAST:event_jButton5ActionPerformed

    private void jTable1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jTable1MouseClicked
        // TODO add your handling code here:
        
        if(evt.getClickCount() == 2){
        
            if(invoice != null){
            
                int selectRow = jTable1.getSelectedRow();
                invoice.getjTextField4().setText(String.valueOf(jTable1.getValueAt(selectRow, 0)));
                invoice.getjTextField6().setText(String.valueOf(jTable1.getValueAt(selectRow, 2)));
                invoice.getjTextField10().setText(String.valueOf(jTable1.getValueAt(selectRow, 3)));
                invoice.getjTextField11().setText(String.valueOf(jTable1.getValueAt(selectRow, 6)));
                  invoice.getjTextField12().setText(String.valueOf(jTable1.getValueAt(selectRow, 7)));
                invoice.getjTextField3().setText(String.valueOf(jTable1.getValueAt(selectRow, 5)));
                 invoice.getjTextField9().setText(String.valueOf(jTable1.getValueAt(selectRow, 4)));
                 this.dispose();
               
            
            }
        
        }
    }//GEN-LAST:event_jTable1MouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        FlatDarkLaf.setup();
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Stock().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private com.toedter.calendar.JDateChooser jDateChooser1;
    private com.toedter.calendar.JDateChooser jDateChooser2;
    private javax.swing.JFormattedTextField jFormattedTextField1;
    private javax.swing.JFormattedTextField jFormattedTextField2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JTextField jTextField7;
    // End of variables declaration//GEN-END:variables
}
