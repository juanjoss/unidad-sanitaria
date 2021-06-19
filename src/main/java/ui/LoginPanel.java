package ui;

import javax.swing.*;
import dao.UsuarioDAO;
import model.Usuario;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class LoginPanel extends JPanel {

    private JButton loginInButton;
    private JLabel passLabel;
    private JPasswordField passwordField;
    private JLabel titulo;
    private JLabel usserLabel;
    private JTextField usserTextField;
    private MainFrame frame;

    public LoginPanel(MainFrame ventana) {
        initComponents();
        frame = ventana;
    }

    private void initComponents() {
        passLabel = new javax.swing.JLabel();
        usserLabel = new javax.swing.JLabel();
        usserTextField = new javax.swing.JTextField();
        loginInButton = new javax.swing.JButton();
        titulo = new javax.swing.JLabel();
        passwordField = new javax.swing.JPasswordField();

        setBackground(new java.awt.Color(255, 255, 204));
        setFont(new java.awt.Font("Tahoma", 0, 18)); // NOI18N

        passLabel.setText("Contraseña:");

        usserLabel.setText("Correo electrónico:");

        usserTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                usserTextFieldActionPerformed(evt);
            }
        });

        loginInButton.setText("Ingresar");
        loginInButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                loginInButtonActionPerformed(evt);
            }
        });

        titulo.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        titulo.setText("Unidad Sanitaria Colonia Seré");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(loginInButton)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addGroup(layout.createSequentialGroup()
                            .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(usserLabel)
                                .addComponent(passLabel))
                            .addGap(18, 18, 18)
                            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(usserTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(passwordField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGroup(layout.createSequentialGroup()
                            .addGap(60, 60, 60)
                            .addComponent(titulo))))
                .addContainerGap(62, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(titulo)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(usserLabel)
                    .addComponent(usserTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(loginInButton)
                .addGap(125, 125, 125))
        );
    }

    private void loginInButtonActionPerformed(java.awt.event.ActionEvent evt) {
        /*
         * Chequear usuario y contraseña ingresados
         * Retornar el Usuario al MainFrame en caso de inicio de sesion
         * 
         */

        String username = usserTextField.getText();
        String pass = new String(passwordField.getPassword());

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

            usserTextField.setText("");
            passwordField.setText("");
        }
    }

    private void usserTextFieldActionPerformed(java.awt.event.ActionEvent evt) {

    }
}