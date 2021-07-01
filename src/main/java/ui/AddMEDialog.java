package ui;

import dao.EquipoMedicoDAO;
import javax.swing.JOptionPane;
import model.EquipoMedico;

public class AddMEDialog extends javax.swing.JDialog {
    
    private MainFrame mainFrame;

    /**
     * Creates new form AddMEDialog
     * @param parent
     * @param modal
     */
    public AddMEDialog(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        
        initComponents();
        
        this.mainFrame = (MainFrame) parent;
        
        this.setName("AddMEDialog");
        nameTF.setName("nameTF");
        stockTF.setName("stockTF");
        addBtn.setName("addBtn");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        nameTF = new javax.swing.JTextField();
        stockTF = new javax.swing.JTextField();
        addBtn = new javax.swing.JButton();
        cancelBtn = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        jLabel1.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Nombre: ");

        jLabel2.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Stock: ");

        nameTF.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        nameTF.setForeground(new java.awt.Color(0, 0, 0));

        stockTF.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        stockTF.setForeground(new java.awt.Color(0, 0, 0));

        addBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        addBtn.setForeground(new java.awt.Color(0, 0, 0));
        addBtn.setText("Agregar");
        addBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addBtnActionPerformed(evt);
            }
        });

        cancelBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        cancelBtn.setForeground(new java.awt.Color(0, 0, 0));
        cancelBtn.setText("Cancelar");
        cancelBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelBtnActionPerformed(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Cascadia Code", 0, 10)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("Los campos con (*) son obligatorios.");

        jLabel4.setForeground(new java.awt.Color(255, 0, 0));
        jLabel4.setText("*");

        jLabel5.setForeground(new java.awt.Color(255, 0, 0));
        jLabel5.setText("*");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(69, 80, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nameTF, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                    .addComponent(stockTF)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(addBtn)
                        .addGap(18, 18, 18)
                        .addComponent(cancelBtn)))
                .addContainerGap(33, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(84, 84, 84)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(49, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(nameTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5))
                .addGap(31, 31, 31)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(stockTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4))
                .addGap(36, 36, 36)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addBtn)
                    .addComponent(cancelBtn))
                .addGap(30, 30, 30)
                .addComponent(jLabel3)
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelBtnActionPerformed
        this.dispose();
    }//GEN-LAST:event_cancelBtnActionPerformed

    private void addBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addBtnActionPerformed
        String name = nameTF.getText();
        int stock = 0;
        boolean err = false;
        
        try {
            stock = Integer.parseInt(stockTF.getText());
            
            if (stock < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "El campo stock debe ser mayor a cero.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                stockTF.setText("0");
                err = true;
            }
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(
                    this,
                    "El campo stock es invalido.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            stockTF.setText("0");
        }
        
        if (name.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "El campo nombre está vacio.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
            );
            nameTF.setText("");
            err = true;
        }
        
        if(!err) {
            EquipoMedico newMe = new EquipoMedico();
            newMe.setNombre(name);
            newMe.setStock(stock);

            EquipoMedicoDAO meDAO = new EquipoMedicoDAO();

            if (!meDAO.insert(newMe)) {
                JOptionPane.showMessageDialog(
                        this,
                        "Ya existe un equipamiento con el nombre " + name,
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
                nameTF.setText("");
            }
            else {
                JOptionPane.showMessageDialog(
                        this,
                        "El equipamiento se agrego de forma exitosa.",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                );
                mainFrame.resetMeTableModel();
                this.dispose();
            }
        }
    }//GEN-LAST:event_addBtnActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addBtn;
    private javax.swing.JButton cancelBtn;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField nameTF;
    private javax.swing.JTextField stockTF;
    // End of variables declaration//GEN-END:variables
}
