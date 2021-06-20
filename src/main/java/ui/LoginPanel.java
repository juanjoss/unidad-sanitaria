package ui;

import javax.swing.*;
import java.awt.*;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import dao.UsuarioDAO;
import model.Usuario;

public class LoginPanel extends javax.swing.JPanel {
    
    private MainFrame frame;

    public LoginPanel(MainFrame ventana) {
        initComponents();
        frame = ventana;
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        mensaje = new javax.swing.JLabel();
        usserLabel = new javax.swing.JLabel();
        passLabel = new javax.swing.JLabel();
        usserTextField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        loginInButton = new javax.swing.JButton();
        changePassButton = new javax.swing.JButton();

        setBackground(new java.awt.Color(255, 255, 204));
        setBorder(javax.swing.BorderFactory.createTitledBorder(null, "UNIDAD SANITARIA COLONIA SERÉ", javax.swing.border.TitledBorder.CENTER, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 28))); // NOI18N
        setMinimumSize(new java.awt.Dimension(1200, 600));
        setPreferredSize(new java.awt.Dimension(1200, 600));
        setLayout(new java.awt.GridBagLayout());

        mensaje.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        mensaje.setText("Para ingresar, complete los siguientes campos:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        add(mensaje, gridBagConstraints);

        usserLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        usserLabel.setText("Correo electrónico:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        add(usserLabel, gridBagConstraints);

        passLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        passLabel.setText("Contraseña:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.LINE_START;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        add(passLabel, gridBagConstraints);

        usserTextField.setFont(new java.awt.Font("Arial", 2, 18)); // NOI18N
        usserTextField.setToolTipText("");
        usserTextField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        usserTextField.setFocusable(false);
        usserTextField.setHighlighter(null);
        usserTextField.setInheritsPopupMenu(true);
        usserTextField.setMargin(new java.awt.Insets(4, 4, 4, 4));
        usserTextField.setMinimumSize(new java.awt.Dimension(20, 40));
        usserTextField.setPreferredSize(new java.awt.Dimension(300, 40));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 10);
        add(usserTextField, gridBagConstraints);
        usserTextField.setText("unisancolser@hotmail.com.ar");

        passwordField.setFont(new java.awt.Font("Arial", 0, 18)); // NOI18N
        passwordField.setBorder(new javax.swing.border.LineBorder(new java.awt.Color(0, 0, 0), 1, true));
        passwordField.setMargin(new java.awt.Insets(4, 4, 4, 4));
        passwordField.setPreferredSize(new java.awt.Dimension(300, 40));
        passwordField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                passwordFieldActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 20, 10);
        add(passwordField, gridBagConstraints);

        loginInButton.setBackground(new java.awt.Color(255, 204, 102));
        loginInButton.setFont(new java.awt.Font("Arial", 1, 16)); // NOI18N
        loginInButton.setText("Ingresar");
        loginInButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        loginInButton.setMargin(new java.awt.Insets(4, 14, 4, 14));
        loginInButton.setPreferredSize(new java.awt.Dimension(100, 35));
        loginInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginInButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.insets = new java.awt.Insets(0, 10, 0, 10);
        add(loginInButton, gridBagConstraints);

        changePassButton.setBackground(new java.awt.Color(255, 204, 102));
        changePassButton.setFont(new java.awt.Font("Arial", 3, 12)); // NOI18N
        changePassButton.setForeground(new java.awt.Color(255, 0, 0));
        changePassButton.setText("¡No recuerdo la contraseña!");
        changePassButton.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));
        changePassButton.setMargin(new java.awt.Insets(4, 14, 4, 14));
        changePassButton.setPreferredSize(new java.awt.Dimension(180, 30));
        changePassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePassButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        add(changePassButton, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void passwordFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_passwordFieldActionPerformed
        
    }//GEN-LAST:event_passwordFieldActionPerformed

    private void changePassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePassButtonActionPerformed
        
    }//GEN-LAST:event_changePassButtonActionPerformed

    /*
     * Chequear usuario y contraseña ingresados
     * Retornar el Usuario al MainFrame en caso de inicio de sesion
     * 
     */
    private void loginInButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_loginInButtonActionPerformed
        String username = usserTextField.getText();
        String pass = new String(passwordField.getPassword());
        System.out.println(username);
        System.out.println(pass);
        
        UsuarioDAO uDAO = new UsuarioDAO();
        Usuario user = uDAO.getUsuario(username, pass);

        if (user != null) {
            Date date = new Date();
            DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            String lastSession = "" + dateFormat.format(date) + " - " + hourFormat.format(date);

            uDAO.updateLastLogin(user.getId(), lastSession);

            frame.loggedIn(user);
        }
        else {
            JOptionPane.showMessageDialog(
                                this,
                                "¡El correo o la contraseña son incorrectos!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );

            //usserTextField.setText("");
            passwordField.setText("");
        }
    }//GEN-LAST:event_loginInButtonActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton changePassButton;
    private javax.swing.JButton loginInButton;
    private javax.swing.JLabel mensaje;
    private javax.swing.JLabel passLabel;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel usserLabel;
    private javax.swing.JTextField usserTextField;
    // End of variables declaration//GEN-END:variables
}
