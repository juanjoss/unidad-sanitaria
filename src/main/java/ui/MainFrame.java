package ui;

import dao.EquipoMedicoDAO;
import dao.MedicamentoDAO;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import javax.mail.AuthenticationFailedException;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.EquipoMedico;
import model.Medicamento;
import util.DateUtil;
import org.apache.commons.validator.routines.EmailValidator;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.AsyncResponse;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class MainFrame extends javax.swing.JFrame {

    public MainFrame() {
        initComponents();

        DefaultTableModel model = (DefaultTableModel) medTable.getModel();
        DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();
        
        MedicamentoDAO medDAO = new MedicamentoDAO();

        /**
         * Se carga la tabla desde la BD y se remueve la columna Id
         */
        resetTableModel();
        medTable.removeColumn(medTable.getColumnModel().getColumn(0));

        /**
         * Evento para la actualizacion de filas en la BD.
         */
        model.addTableModelListener((TableModelEvent evt) -> {
            if (evt.getType() == TableModelEvent.UPDATE && evt.getColumn() != TableModelEvent.ALL_COLUMNS) {
                Medicamento newMed = new Medicamento();

                int idMed = (int) model.getValueAt(evt.getFirstRow(), 0);
                String medName = (String) model.getValueAt(evt.getFirstRow(), 1);
                int medStock = (int) model.getValueAt(evt.getFirstRow(), 2);
                String medExpDate = (String) model.getValueAt(evt.getFirstRow(), 3);
                String medDosis = (String) model.getValueAt(evt.getFirstRow(), 4);
                String medPres = (String) model.getValueAt(evt.getFirstRow(), 5);

                /**
                 * Controles de los nuevos datos
                 */
                Medicamento prevMed = medDAO.getMedicamento(idMed);

                if (medName.equals("")) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Nombre está vacío!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    model.setValueAt(prevMed.getNombre(), evt.getFirstRow(), 1);
                } else if (medStock < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Stock no puede ser menor que cero!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    model.setValueAt(prevMed.getStock(), evt.getFirstRow(), 2);
                } else if (!DateUtil.isValidDate(medExpDate, "d/M/uuuu")) {
                    /**
                     * El formato "d/M/uuuu" tiene que ser asi por la
                     * implementacion del validador
                     */
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Fecha de Vencimiento no corresponde a una fecha valida!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    model.setValueAt(prevMed.getFechaVencimiento(), evt.getFirstRow(), 3);
                } else if (medDosis.equals("")) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Dosis está vacío!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    model.setValueAt(prevMed.getDosis(), evt.getFirstRow(), 4);
                } 
                else if (medPres.equals("")) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Presentación está vacío!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                    model.setValueAt(prevMed.getPresentacion(), evt.getFirstRow(), 5);
                }
                else {
                    newMed.setId(idMed);
                    newMed.setNombre(medName);
                    newMed.setStock(medStock);
                    newMed.setFechaVencimiento(
                            DateUtil.formatDate(
                                    medExpDate,
                                    "dd/mm/yyyy",
                                    "yyyy-mm-dd"
                            )
                    );
                    newMed.setDosis(medDosis);
                    newMed.setPresentacion(medPres);

                    medDAO.update(newMed);
                    checkAlerts();
                }
            }
        });
        
        stModel.addTableModelListener((TableModelEvent evt) -> {
            if (evt.getType() == TableModelEvent.UPDATE && evt.getColumn() != TableModelEvent.ALL_COLUMNS) {
                float valueChanged = Float.parseFloat(
                        String.valueOf(stModel.getValueAt(evt.getFirstRow(), evt.getColumn()))
                );
                
                if(valueChanged < 0) {
                    stModel.setValueAt(0, evt.getFirstRow(), evt.getColumn());
                }
            }
        });

        checkAlerts();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        searchBar = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        medTable = new javax.swing.JTable();
        searchBarLabel = new javax.swing.JLabel();
        cbLowStock = new javax.swing.JCheckBox();
        cbExpDate = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        addMedBtn = new javax.swing.JButton();
        borrarButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        medStockAlert = new javax.swing.JTextField();
        expAlertLbl = new javax.swing.JLabel();
        stockAlertLbl = new javax.swing.JLabel();
        medExpAlert = new javax.swing.JTextField();
        resetTableBtn = new javax.swing.JButton();
        filterComboBox = new javax.swing.JComboBox<>();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        missingsList = new javax.swing.JList<>();
        filler1 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        filler2 = new javax.swing.Box.Filler(new java.awt.Dimension(0, 0), new java.awt.Dimension(0, 0), new java.awt.Dimension(32767, 0));
        mlLabel = new javax.swing.JLabel();
        slLabel = new javax.swing.JLabel();
        addToSLBtn = new javax.swing.JButton();
        removeFromSTBtn = new javax.swing.JButton();
        toTF = new javax.swing.JTextField();
        toTFLabel = new javax.swing.JLabel();
        fromTFLabel = new javax.swing.JLabel();
        fromTF = new javax.swing.JTextField();
        sendSolBtn = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        solicitudeTable = new javax.swing.JTable();
        emailSubject = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        emailComment = new javax.swing.JTextPane();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setMinimumSize(new java.awt.Dimension(1200, 600));

        jTabbedPane1.setBackground(new java.awt.Color(255, 255, 221));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N
        jTabbedPane1.setMaximumSize(new java.awt.Dimension(1366, 768));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(1200, 600));
        jTabbedPane1.setName(""); // NOI18N
        jTabbedPane1.setOpaque(true);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1366, 768));

        mainPanel.setBackground(new java.awt.Color(255, 255, 204));

        searchBar.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N

        medTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        medTable.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        medTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre", "Stock", "Fecha de Vencimiento", "Dosis", "Presentacion", "Laboratorio"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        medTable.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_LAST_COLUMN);
        medTable.setGridColor(new java.awt.Color(178, 173, 173));
        medTable.setIntercellSpacing(new java.awt.Dimension(0, 0));
        medTable.setRowHeight(32);
        /** centrado de las columnas */
        DefaultTableCellRenderer centerRndr = new DefaultTableCellRenderer();
        centerRndr.setHorizontalAlignment(JLabel.CENTER);

        Enumeration<TableColumn> colModel = medTable.getColumnModel().getColumns();
        while(colModel.hasMoreElements()) {
            colModel.nextElement().setCellRenderer(centerRndr);
        }

        /** tamaño de la fila del header */
        medTable.getTableHeader().setPreferredSize(new Dimension(50, 50));

        /** centrado del header */
        JLabel header = (JLabel) medTable.getTableHeader().getDefaultRenderer();
        header.setHorizontalAlignment(JLabel.CENTER);

        /** buscador para la barra de busqueda */
        rowSorter = new TableRowSorter<>(medTable.getModel());
        medTable.setRowSorter(rowSorter);
        scrollPane.setViewportView(medTable);
        if (medTable.getColumnModel().getColumnCount() > 0) {
            medTable.getColumnModel().getColumn(0).setResizable(false);
        }

        searchBarLabel.setFont(new java.awt.Font("Cascadia Code", 0, 16)); // NOI18N
        searchBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchBarLabel.setText("Ingrese un medicamento para buscar:");

        cbLowStock.setBackground(new java.awt.Color(255, 255, 204));
        cbLowStock.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        cbLowStock.setText("Solo medicamentos con poco stock");
        cbLowStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbLowStockActionPerformed(evt);
            }
        });

        cbExpDate.setBackground(new java.awt.Color(255, 255, 204));
        cbExpDate.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        cbExpDate.setText("Solo medicamentos en rango de vencimiento");
        cbExpDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbExpDateActionPerformed(evt);
            }
        });

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));

        addMedBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        addMedBtn.setText("Agregar Medicamento");
        addMedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMedBtnActionPerformed(evt);
            }
        });

        borrarButton.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        borrarButton.setText("Borrar Medicamento");
        borrarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarButtonActionPerformed(evt);
            }
        });

        jPanel3.setBackground(new java.awt.Color(255, 255, 204));

        medStockAlert.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        medStockAlert.setForeground(new java.awt.Color(255, 0, 0));
        medStockAlert.setDisabledTextColor(new java.awt.Color(153, 255, 153));
        medStockAlert.setEnabled(false);

        expAlertLbl.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        expAlertLbl.setText("Estado de Vencimientos:");

        stockAlertLbl.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        stockAlertLbl.setText("Estado del Stock:");

        medExpAlert.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        medExpAlert.setForeground(new java.awt.Color(255, 51, 51));
        medExpAlert.setDisabledTextColor(new java.awt.Color(153, 255, 153));
        medExpAlert.setEnabled(false);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(medExpAlert)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(0, 42, Short.MAX_VALUE)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(stockAlertLbl, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(expAlertLbl, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                        .addGap(59, 59, 59))
                    .addComponent(medStockAlert))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(stockAlertLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(medStockAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(31, 31, 31)
                .addComponent(expAlertLbl)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(medExpAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(16, Short.MAX_VALUE))
        );

        medStockAlert.setHorizontalAlignment(JTextField.CENTER);
        expAlertLbl.setHorizontalAlignment(JLabel.CENTER);
        stockAlertLbl.setHorizontalAlignment(JLabel.CENTER);
        medExpAlert.setHorizontalAlignment(JTextField.CENTER);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(addMedBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(borrarButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(40, 40, 40))
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(149, 149, 149)
                .addComponent(addMedBtn)
                .addGap(32, 32, 32)
                .addComponent(borrarButton)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        resetTableBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        resetTableBtn.setText("Quitar Filtros");
        resetTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetTableBtnActionPerformed(evt);
            }
        });

        filterComboBox.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        filterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Seleccionar...", "Por Nombre", "Por Dosis", "Por Laboratorio", "Por Presentación" }));
        filterComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap(51, Short.MAX_VALUE)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(searchBarLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 923, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addComponent(cbLowStock)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cbExpDate)
                        .addGap(12, 12, 12)
                        .addComponent(resetTableBtn)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 51, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBarLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(50, 50, 50)
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cbLowStock)
                    .addComponent(cbExpDate)
                    .addComponent(resetTableBtn))
                .addGap(12, 12, 12)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 484, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(101, 101, 101))
        );

        searchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = searchBar.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = searchBar.getText();

                if (text.trim().length() == 0) {
                    rowSorter.setRowFilter(null);
                } else {
                    rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        jTabbedPane1.addTab("Inicio", mainPanel);

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        DefaultListModel mlModel = new DefaultListModel();
        missingsList.setModel(mlModel);
        missingsList.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N
        DefaultListCellRenderer mlCellRenderer = (DefaultListCellRenderer) missingsList.getCellRenderer();
        mlCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        jScrollPane1.setViewportView(missingsList);

        mlLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        mlLabel.setText("Medicamentos y Equipo Médico Faltante:");

        slLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        slLabel.setText("Lista de Pedidos:");

        addToSLBtn.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        addToSLBtn.setText("Agregar");
        addToSLBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToSLBtnActionPerformed(evt);
            }
        });

        removeFromSTBtn.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        removeFromSTBtn.setText("Quitar");
        removeFromSTBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromSTBtnActionPerformed(evt);
            }
        });

        toTF.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        toTF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        toTF.setText("fuatojfj@gmail.com");

        toTFLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        toTFLabel.setText("Enviar a:");

        fromTFLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        fromTFLabel.setText("Enviar desde:");

        fromTF.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        fromTF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fromTF.setText("fuato1@hotmail.com");

        sendSolBtn.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        sendSolBtn.setText("Enviar Solicitud");
        sendSolBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendSolBtnActionPerformed(evt);
            }
        });

        solicitudeTable.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        solicitudeTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Nombre", "mg", "Comprimidos", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Float.class, java.lang.Float.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        solicitudeTable.setRowHeight(32);
        solicitudeTable.getTableHeader().setReorderingAllowed(false);
        /** centrado de las columnas */
        Enumeration<TableColumn> solTableColModel = solicitudeTable.getColumnModel().getColumns();
        while(solTableColModel.hasMoreElements()) {
            solTableColModel.nextElement().setCellRenderer(centerRndr);
        }

        /** tamaño de la fila del header */
        solicitudeTable.getTableHeader().setPreferredSize(new Dimension(50, 50));

        /** centrado del header */
        JLabel header2 = (JLabel) solicitudeTable.getTableHeader().getDefaultRenderer();
        header2.setHorizontalAlignment(JLabel.CENTER);
        jScrollPane3.setViewportView(solicitudeTable);
        if (solicitudeTable.getColumnModel().getColumnCount() > 0) {
            solicitudeTable.getColumnModel().getColumn(0).setResizable(false);
            solicitudeTable.getColumnModel().getColumn(1).setResizable(false);
            solicitudeTable.getColumnModel().getColumn(2).setResizable(false);
            solicitudeTable.getColumnModel().getColumn(3).setResizable(false);
        }

        emailSubject.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel1.setText("Asunto:");

        emailComment.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jScrollPane2.setViewportView(emailComment);

        jLabel2.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel2.setText("Comentario:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 18, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 61, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(mlLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 248, Short.MAX_VALUE)
                        .addComponent(slLabel)
                        .addGap(135, 135, 135)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 187, Short.MAX_VALUE)))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane2)
                            .addComponent(fromTF, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(111, 111, 111)
                                .addComponent(fromTFLabel))
                            .addComponent(toTF, javax.swing.GroupLayout.DEFAULT_SIZE, 320, Short.MAX_VALUE)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(125, 125, 125)
                                .addComponent(toTFLabel))
                            .addComponent(emailSubject))
                        .addGap(25, 25, 25))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(sendSolBtn)
                        .addGap(110, 110, 110))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(152, 152, 152))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(139, 139, 139))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(191, 191, 191)
                .addComponent(addToSLBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(removeFromSTBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(402, 402, 402))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(210, 210, 210)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(slLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(mlLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(40, 40, 40)
                                .addComponent(toTFLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(toTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fromTFLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(fromTF, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(emailSubject, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)
                                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(sendSolBtn))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))))
                        .addGap(18, 18, 18)
                        .addComponent(addToSLBtn)
                        .addGap(253, 253, 253))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(removeFromSTBtn)
                        .addGap(250, 250, 250))))
        );

        jTabbedPane1.addTab("Solicitud de Medicamentos", jPanel1);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.PAGE_START);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Restaura la tabla de medicamentos.
     */
    private void resetTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetTableBtnActionPerformed
        // TODO add your handling code here:
        resetTableModel();
    }//GEN-LAST:event_resetTableBtnActionPerformed

    /**
     * Evento para el boton de eliminar medicamento.
     */
    private void borrarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarButtonActionPerformed
        // TODO add your handling code here:
        if (medTable.getSelectedRow() != -1) {
            DefaultTableModel model = (DefaultTableModel) medTable.getModel();

            int confirmacion = JOptionPane.showConfirmDialog(
                    mainPanel,
                    "¿Está seguro que desea borrar ese medicamento?",
                    "Confirmación",
                    JOptionPane.YES_NO_OPTION
            );

            if (JOptionPane.YES_OPTION == confirmacion) {
                int column = 0;
                int row = medTable.getSelectedRow();
                int id = (int) model.getValueAt(row, column);

                MedicamentoDAO medDAO = new MedicamentoDAO();
                medDAO.deleteXId(id);
                model.removeRow(medTable.getSelectedRow());
                
                checkAlerts();
            }
        }
    }//GEN-LAST:event_borrarButtonActionPerformed
    
    /**
     * Evento para el boton de agregar medicamento.
     */
    private void addMedBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMedBtnActionPerformed
        // TODO add your handling code here:
        AddMedFrame p = new AddMedFrame();
        p.setVisible(true);
        p.pack();
        p.setLocationRelativeTo(null);
        p.setDefaultCloseOperation(p.DO_NOTHING_ON_CLOSE);
        this.dispose();
    }//GEN-LAST:event_addMedBtnActionPerformed

    /**
     * Checkbox para filtrar en la tabla solo vencimientos en rango de 15 dias.
     */
    private void cbExpDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbExpDateActionPerformed
        // TODO add your handling code here:
        if (cbExpDate.isSelected()) {
            MedicamentoDAO medDAO = new MedicamentoDAO();
            List<Medicamento> meds = medDAO.medsInExpRange();

            DefaultTableModel model = (DefaultTableModel) medTable.getModel();
            model.setRowCount(0);

            if (meds != null) {
                meds.forEach(m -> {
                    model.addRow(
                            new Object[]{
                                m.getId(),
                                m.getNombre(),
                                m.getStock(),
                                DateUtil.formatDate(
                                        m.getFechaVencimiento(),
                                        "yyyy-mm-dd",
                                        "dd/mm/yyyy"
                                ),
                                m.getDosis(),
                                m.getPresentacion(),
                                m.getLaboratorio()
                            });
                });

                cbLowStock.setSelected(false);
            }
        } else {
            resetTableModel();
        }
    }//GEN-LAST:event_cbExpDateActionPerformed

    /**
     * Checkbox para filtrar en la tabla solo stock bajo (5 unidades o menos).
     */
    private void cbLowStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbLowStockActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) medTable.getModel();
        MedicamentoDAO medDAO = new MedicamentoDAO();

        if (cbLowStock.isSelected()) {
            List<Medicamento> meds = medDAO.medsWithLowStock();

            model.setRowCount(0);

            if (meds != null) {
                meds.forEach(m -> {
                    model.addRow(
                            new Object[]{
                                m.getId(),
                                m.getNombre(),
                                m.getStock(),
                                DateUtil.formatDate(
                                        m.getFechaVencimiento(),
                                        "yyyy-mm-dd",
                                        "dd/mm/yyyy"
                                ),
                                m.getDosis(),
                                m.getPresentacion(),
                                m.getLaboratorio()
                            });
                });

                cbExpDate.setSelected(false);
            }
        } else {
            resetTableModel();
        }
    }//GEN-LAST:event_cbLowStockActionPerformed

    private void addToSLBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToSLBtnActionPerformed
        // TODO add your handling code here:
        List<String> selValues = missingsList.getSelectedValuesList();
        
        if(selValues.size() > 0) {
            DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();
            DefaultListModel mlModel = (DefaultListModel) missingsList.getModel();
            
            selValues.forEach(e -> {
                if(!contains(solicitudeTable, e)) {
                    stModel.addRow(new Object[]{ e, 0, 0, 0 });
                    mlModel.removeElement(e);
                }
            });
        }
    }//GEN-LAST:event_addToSLBtnActionPerformed

    private void removeFromSTBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromSTBtnActionPerformed
        // TODO add your handling code here:
        int[] selValues = solicitudeTable.getSelectedRows();
        
        if(selValues.length > 0) {
            DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();
            DefaultListModel mlModel = (DefaultListModel) missingsList.getModel();
            
            for (int i = selValues.length - 1; i >= 0; i--) {
                if(!mlModel.contains(stModel.getValueAt(selValues[i], 0))) {
                    mlModel.addElement(stModel.getValueAt(selValues[i], 0));
                    stModel.removeRow(selValues[i]);
                }
            }
        }
    }//GEN-LAST:event_removeFromSTBtnActionPerformed

    private void sendSolBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendSolBtnActionPerformed
        // TODO add your handling code here:
        String toEmail = toTF.getText();
        String fromEmail = fromTF.getText();
        String emailSub = emailSubject.getText();
        String emailCmt = emailComment.getText();
        
        EmailValidator ev = EmailValidator.getInstance();
        DefaultTableModel model = (DefaultTableModel) solicitudeTable.getModel();
        
        if(ev.isValid(toEmail) && ev.isValid(fromEmail)) {
            try {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(".\\table.html", false))) {
                    bw.write("<html>");
                    bw.write("<body style='max-width: 500px; margin: auto;'>");
                    bw.write("<h6>" + emailCmt + "</h6>");
                    
                    bw.write("<table>");
                    
                    bw.write("<tr>");
                    for(int c = 0; c < model.getColumnCount(); ++c) {
                        bw.write("<th style='text-align: center;'>");
                        bw.write(model.getColumnName(c));
                        bw.write("</th>");
                    }
                    bw.write("</tr>");
                    
                    for(int r = 0; r < model.getRowCount(); ++r) {
                        bw.write("<tr>");
                        
                        for(int c = 0; c < model.getColumnCount(); ++c) {
                            bw.write("<td style='text-align: center;'>");
                            bw.write(model.getValueAt(r, c).toString());
                            bw.write("</td>");
                        }
                        
                        bw.write("</tr>");
                    }
                    bw.write("</table>");
                    bw.write("</body>");
                    bw.write("</html>");
                }
            } catch (IOException ex) {
                System.out.println(ex);
            }
            
            try {
                File html = new File(".\\table.html");
                
                Email email = EmailBuilder.startingBlank()
                    .from("Salita Colonia Seré", fromEmail)
                    .to("To", toEmail)
                    .withSubject(emailSub)
                    .withHTMLText(html)
                    .buildEmail();

                JPasswordField passField = new JPasswordField();
                String[] options = new String[]{"OK", "Cancelar"};
                int op = JOptionPane.showOptionDialog(
                        null, 
                        passField, 
                        "Ingrese contraseña: ", 
                        JOptionPane.NO_OPTION, 
                        JOptionPane.PLAIN_MESSAGE, 
                        null, 
                        options,
                        options[1]
                );

                if(op == 0) {
                    String password = new String(passField.getPassword());

                    if(!password.equals("")) {
                        Mailer mailer = MailerBuilder
                            .withSMTPServer("smtp.office365.com", 587, fromEmail, password)
                            .withTransportStrategy(TransportStrategy.SMTP_TLS)
                            .withDebugLogging(true)
                            .async()
                            .buildMailer();

                        AsyncResponse res = mailer.sendMail(email, true);

                        if(res != null) {
                            res.onSuccess(() -> {
                                solicitudeTable.removeAll();

                                JOptionPane.showMessageDialog(
                                            this,
                                            "El email con la solicitud se ha enviado exitosamente.",
                                            "Information",
                                            JOptionPane.INFORMATION_MESSAGE
                                    );
                            });

                            res.onException(e -> {
                                if(e.getCause().getClass().equals(AuthenticationFailedException.class)) {
                                    JOptionPane.showMessageDialog(
                                            this,
                                            "La contraseña para el email " + fromEmail + " es incorrecta. Por favor intente de nuevo.",
                                            "Error",
                                            JOptionPane.ERROR_MESSAGE
                                    );
                                }
                            });
                        }
                    }
                    else {
                        JOptionPane.showMessageDialog(this, "Debe ingrear una contraseña para enviar el email.");
                    }
                }
            }
            catch(Exception e) {
                System.out.println(e);
            }
        }
        else {
            JOptionPane.showMessageDialog(
                    this,
                    "Debe rellenar el email de envío y recepción para realizar una solicitud.",
                    "Warning",
                    JOptionPane.WARNING_MESSAGE
            );
        }
    }//GEN-LAST:event_sendSolBtnActionPerformed

    private void filterComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filterComboBoxItemStateChanged
        filterTable();
    }//GEN-LAST:event_filterComboBoxItemStateChanged
    
    /**
     * Transforma un @String de formato fecha fromFormat a toFormat.
     *
     * @param table A {@code JTable} la tabla en la que se buscara.
     * @param value A {@code String} el valor a buscar.
     * @return A {@code boolean} si se encontro o no el valor.
     */
    private boolean contains(JTable table, String value) {
        DefaultTableModel model = (DefaultTableModel) table.getModel();
        Vector<Vector> rows = model.getDataVector();
        
        for (int i = 0; i < rows.size(); i++) {
            if(rows.get(i).get(0).equals(value)) {
               return true;
            }
        }
        
        return false;
    }
    
    private void filterTable() {
        String text = searchBar.getText();
        int indexSearch = 0;
        
        switch (filterComboBox.getSelectedItem().toString()) {
            case "Por Nombre": indexSearch = 1; break;
            case "Por Dosis": indexSearch = 4; break;
            case "Por Presentación": indexSearch = 5; break;
            case "Por Laboratorio": indexSearch = 6; break;
            default: indexSearch = 0; break;
        }

        if (text.trim().length() == 0) {
            rowSorter.setRowFilter(null);
        } else {
            rowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text, indexSearch));
        }
    }

    /**
     * Checkeo de alertas por bajo stock y vencimientos.
     */
    private void checkAlerts() {
        MedicamentoDAO medDAO = new MedicamentoDAO();
        EquipoMedicoDAO medEqDAO = new EquipoMedicoDAO();

        List<Medicamento> medsWithLowStock = medDAO.medsWithLowStock();
        List<Medicamento> medsInExpRange = medDAO.medsInExpRange();
        
        DefaultListModel mlModel = (DefaultListModel) missingsList.getModel();
        DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();
        
        mlModel.removeAllElements();

        if (medsWithLowStock != null) {
            if (medsWithLowStock.size() > 0) {
                medStockAlert.setText("Hay medicamentos con poco stock!");
                medStockAlert.setDisabledTextColor(Color.red);
                
                medsWithLowStock.forEach(m -> {
                    mlModel.addElement(m.getNombre());
                });
            } else {
                medStockAlert.setText("No hay medicamentos con poco stock");
                medStockAlert.setDisabledTextColor(Color.green);
            }
        }

        if (medsInExpRange != null) {
            if (medsInExpRange.size() > 0) {
                medExpAlert.setText("Hay medicamentos en rango de vencimiento!");
                medExpAlert.setDisabledTextColor(Color.red);
                
                medsInExpRange.forEach(m -> {
                    mlModel.addElement(m.getNombre());
                });
            } else {
                medExpAlert.setText("No hay vencimientos cercanos");
                medExpAlert.setDisabledTextColor(Color.green);
            }
        }
        
        List<EquipoMedico> medEqWithLowStock = medEqDAO.medEqWithLowStock();
        
        if(medEqWithLowStock != null) {
            if(medEqWithLowStock.size() > 0) {
                medEqWithLowStock.forEach(me -> {
                    mlModel.addElement(me.getNombre());
                });
            }
        }
        
        Vector<Vector> rows = stModel.getDataVector();
        
        for (int i = 0; i < rows.size(); i++) {
            Object e = rows.get(i).get(0);
            
            if(!mlModel.contains(e)) {
               stModel.removeRow(i);
               i--;
            }
            else {
                mlModel.removeElement(e);
            }
        }
    }

    /**
     * Restaura la tabla de medicamentos.
     */
    private void resetTableModel() {
        DefaultTableModel model = (DefaultTableModel) medTable.getModel();
        MedicamentoDAO medDAO = new MedicamentoDAO();
        List<Medicamento> meds = medDAO.selectAll();

        if (meds != null) {
            model.setNumRows(0);

            meds.forEach(m -> {
                model.addRow(
                        new Object[]{
                            m.getId(),
                            m.getNombre(),
                            m.getStock(),
                            DateUtil.formatDate(
                                    m.getFechaVencimiento(),
                                    "yyyy-mm-dd",
                                    "dd/mm/yyyy"
                            ),
                            m.getDosis(),
                            m.getPresentacion(),
                            m.getLaboratorio()
                        });
            });
        }
        
        rowSorter.setRowFilter(null);
        filterComboBox.setSelectedIndex(0);
        searchBar.setText("");
        
        medTable.getRowSorter().setSortKeys(null);
        cbLowStock.setSelected(false);
        cbExpDate.setSelected(false);
    }

    public static void main(String args[]) {
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Windows".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    javax.swing.JButton addMedBtn;
    javax.swing.JButton addToSLBtn;
    javax.swing.JButton borrarButton;
    javax.swing.JCheckBox cbExpDate;
    javax.swing.JCheckBox cbLowStock;
    javax.swing.JTextPane emailComment;
    javax.swing.JTextField emailSubject;
    javax.swing.JLabel expAlertLbl;
    javax.swing.Box.Filler filler1;
    javax.swing.Box.Filler filler2;
    javax.swing.JComboBox<String> filterComboBox;
    javax.swing.JTextField fromTF;
    javax.swing.JLabel fromTFLabel;
    javax.swing.JLabel jLabel1;
    javax.swing.JLabel jLabel2;
    javax.swing.JPanel jPanel1;
    javax.swing.JPanel jPanel2;
    javax.swing.JPanel jPanel3;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JScrollPane jScrollPane3;
    javax.swing.JTabbedPane jTabbedPane1;
    javax.swing.JPanel mainPanel;
    javax.swing.JTextField medExpAlert;
    javax.swing.JTextField medStockAlert;
    javax.swing.JTable medTable;
    javax.swing.JList<String> missingsList;
    javax.swing.JLabel mlLabel;
    javax.swing.JButton removeFromSTBtn;
    javax.swing.JButton resetTableBtn;
    javax.swing.JScrollPane scrollPane;
    javax.swing.JTextField searchBar;
    javax.swing.JLabel searchBarLabel;
    javax.swing.JButton sendSolBtn;
    javax.swing.JLabel slLabel;
    javax.swing.JTable solicitudeTable;
    javax.swing.JLabel stockAlertLbl;
    javax.swing.JTextField toTF;
    javax.swing.JLabel toTFLabel;
    TableRowSorter<TableModel> rowSorter;
    // End of variables declaration//GEN-END:variables
}
