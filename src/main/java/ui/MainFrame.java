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
import javax.swing.WindowConstants;
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
import org.apache.commons.text.StringEscapeUtils;
import util.DateUtil;
import org.apache.commons.validator.routines.EmailValidator;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.AsyncResponse;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;

public class MainFrame extends javax.swing.JFrame {

    TableRowSorter<TableModel> rowSorter;
    TableRowSorter<TableModel> meRowSorter;

    private MainFrame() {
        initComponents();

        DefaultTableModel medModel = (DefaultTableModel) medTable.getModel();
        DefaultTableModel meModel = (DefaultTableModel) meEqTable.getModel();
        DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();

        MedicamentoDAO medDAO = new MedicamentoDAO();

        /**
         * Se carga la tabla de medicamentos desde la BD y se remueve la columna Id.
         * Se carga la tabla de equipo medico desde la BD y se remueve la columna Id.
         */
        resetMedTableModel();
        resetMeTableModel();
        medTable.removeColumn(medTable.getColumnModel().getColumn(0));
        meEqTable.removeColumn(meEqTable.getColumnModel().getColumn(0));
        
        /**
         * Evento para la tabla de equipo medico la actualizacion de filas en la BD.
         */
        meModel.addTableModelListener((TableModelEvent evt) -> {
            if(evt.getType() == TableModelEvent.INSERT) {
//                System.out.println(meModel.getValueAt(evt.getFirstRow(), 1));
            }
            if(evt.getType() == TableModelEvent.UPDATE && evt.getColumn() != TableModelEvent.ALL_COLUMNS) {
                int id = (int) meModel.getValueAt(evt.getFirstRow(), 0);
                String name = (String) meModel.getValueAt(evt.getFirstRow(), 1);
                int stock = (int) meModel.getValueAt(evt.getFirstRow(), 2);
                
                EquipoMedicoDAO meDAO = new EquipoMedicoDAO();
                EquipoMedico me = meDAO.getMedEq(id);
                boolean error = false;
                
                if(name.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Nombre está vacío!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    meModel.setValueAt(me.getNombre(), evt.getFirstRow(), 1);
                    error = true;
                }
                
                if(stock < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Stock no puede ser menor a cero!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    meModel.setValueAt(me.getStock(), evt.getFirstRow(), 2);
                    error = true;
                }
                
                if(!error) {
                    String nameAux = me.getNombre();
                    me.setNombre(name);
                    me.setStock(stock);
                    
                    if(meDAO.update(me)) {
                        checkAlerts();
                    }
                    else {
                        JOptionPane.showMessageDialog(
                                this,
                                "¡Ya existe un equipamiento con el mismo nombre!",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                        
                        meModel.setValueAt(nameAux, evt.getFirstRow(), 1);
                    }
                }
            }
        });
        
        /**
         * Evento para la tabla de medicamentos la actualizacion de filas en la BD.
         */
        medModel.addTableModelListener((TableModelEvent evt) -> {
            if(evt.getType() == TableModelEvent.INSERT) {
//                System.out.println(medModel.getValueAt(evt.getFirstRow(), 1));
            }
            if (evt.getType() == TableModelEvent.UPDATE && evt.getColumn() != TableModelEvent.ALL_COLUMNS) {

                int idMed = (int) medModel.getValueAt(evt.getFirstRow(), 0);
                String medName = (String) medModel.getValueAt(evt.getFirstRow(), 1);
                int medStock = (int) medModel.getValueAt(evt.getFirstRow(), 2);
                String medExpDate = (String) medModel.getValueAt(evt.getFirstRow(), 3);
                String medDosis = (String) medModel.getValueAt(evt.getFirstRow(), 4);
                String medPres = (String) medModel.getValueAt(evt.getFirstRow(), 5);
                String medLab = (String) medModel.getValueAt(evt.getFirstRow(), 6);

                /**
                 * Controles de los nuevos datos
                 */
                Medicamento med = medDAO.getMedicamento(idMed);
                boolean error = false;

                if (medName.equals("")) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Nombre está vacío!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    medModel.setValueAt(med.getNombre(), evt.getFirstRow(), 1);
                    error = true;
                }

                if (medStock < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Stock no puede ser menor que cero!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    medModel.setValueAt(med.getStock(), evt.getFirstRow(), 2);
                    error = true;
                }

                if (!DateUtil.isValidDate(medExpDate, "d/M/uuuu")) {
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

                    medModel.setValueAt(med.getFechaVencimiento(), evt.getFirstRow(), 3);
                    error = true;
                }

                if (medPres.equals("")) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Presentación está vacío!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    medModel.setValueAt(med.getPresentacion(), evt.getFirstRow(), 5);
                    error = true;
                }

                if (!error) {
                    med.setId(idMed);
                    med.setNombre(medName);
                    med.setStock(medStock);
                    med.setFechaVencimiento(
                            DateUtil.formatDate(
                                    medExpDate,
                                    "dd/mm/yyyy",
                                    "yyyy-mm-dd"
                            )
                    );
                    med.setDosis(medDosis);
                    med.setPresentacion(medPres);
                    med.setLaboratorio(medLab);

                    medDAO.update(med);
                    checkAlerts();
                }
            }
        });

        stModel.addTableModelListener((TableModelEvent evt) -> {
            if (evt.getType() == TableModelEvent.UPDATE && evt.getColumn() != TableModelEvent.ALL_COLUMNS) {
                float valueChanged = Float.parseFloat(
                        String.valueOf(stModel.getValueAt(evt.getFirstRow(), evt.getColumn()))
                );

                if (valueChanged < 0) {
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
        jPanel2 = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        expAlertLbl = new javax.swing.JLabel();
        medExpAlert = new javax.swing.JTextField();
        meStockAlert = new javax.swing.JTextField();
        stockAlertLbl = new javax.swing.JLabel();
        medStockAlert = new javax.swing.JTextField();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        searchBar = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        medTable = new javax.swing.JTable();
        searchBarLabel = new javax.swing.JLabel();
        cbLowStock = new javax.swing.JCheckBox();
        cbExpDate = new javax.swing.JCheckBox();
        addMedBtn = new javax.swing.JButton();
        borrarButton = new javax.swing.JButton();
        resetTableBtn = new javax.swing.JButton();
        filterComboBox = new javax.swing.JComboBox<>();
        jPanel5 = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        meEqTable = new javax.swing.JTable();
        jLabel3 = new javax.swing.JLabel();
        meSearchBar = new javax.swing.JTextField();
        meCbLowStock = new javax.swing.JCheckBox();
        resetMeTableBtn = new javax.swing.JButton();
        addMEBtn = new javax.swing.JButton();
        delMEBtn = new javax.swing.JButton();
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
        jTabbedPane1.setForeground(new java.awt.Color(0, 0, 0));
        jTabbedPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTabbedPane1.setFont(new java.awt.Font("Cascadia Code", 0, 18)); // NOI18N
        jTabbedPane1.setMaximumSize(new java.awt.Dimension(1366, 768));
        jTabbedPane1.setMinimumSize(new java.awt.Dimension(1200, 600));
        jTabbedPane1.setName(""); // NOI18N
        jTabbedPane1.setOpaque(true);
        jTabbedPane1.setPreferredSize(new java.awt.Dimension(1366, 768));

        mainPanel.setBackground(new java.awt.Color(255, 255, 204));

        jPanel2.setBackground(new java.awt.Color(255, 255, 204));

        jPanel3.setBackground(new java.awt.Color(255, 255, 204));

        expAlertLbl.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        expAlertLbl.setForeground(new java.awt.Color(0, 0, 0));
        expAlertLbl.setText("Estado de Vencimientos:");

        medExpAlert.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        medExpAlert.setForeground(new java.awt.Color(255, 51, 51));
        medExpAlert.setDisabledTextColor(new java.awt.Color(153, 255, 153));
        medExpAlert.setEnabled(false);

        meStockAlert.setBackground(new java.awt.Color(255, 255, 204));
        meStockAlert.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        meStockAlert.setEnabled(false);
        meStockAlert.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meStockAlertActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(meStockAlert)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(80, Short.MAX_VALUE)
                .addComponent(expAlertLbl)
                .addGap(69, 69, 69))
            .addComponent(medExpAlert)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(meStockAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 43, Short.MAX_VALUE)
                .addComponent(expAlertLbl)
                .addGap(18, 18, 18)
                .addComponent(medExpAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        expAlertLbl.setHorizontalAlignment(JLabel.CENTER);
        medExpAlert.setHorizontalAlignment(JTextField.CENTER);

        stockAlertLbl.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        stockAlertLbl.setForeground(new java.awt.Color(0, 0, 0));
        stockAlertLbl.setText("Estado del Stock:");

        medStockAlert.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        medStockAlert.setForeground(new java.awt.Color(255, 0, 0));
        medStockAlert.setDisabledTextColor(new java.awt.Color(153, 255, 153));
        medStockAlert.setEnabled(false);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(medStockAlert)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                                .addComponent(stockAlertLbl)
                                .addGap(90, 90, 90)))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(169, 169, 169)
                .addComponent(stockAlertLbl)
                .addGap(18, 18, 18)
                .addComponent(medStockAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(63, Short.MAX_VALUE))
        );

        stockAlertLbl.setHorizontalAlignment(JLabel.CENTER);
        medStockAlert.setHorizontalAlignment(JTextField.CENTER);

        jTabbedPane2.setForeground(new java.awt.Color(0, 0, 0));
        jTabbedPane2.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N

        jPanel4.setBackground(new java.awt.Color(255, 255, 204));

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
        searchBarLabel.setForeground(new java.awt.Color(0, 0, 0));
        searchBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchBarLabel.setText("Ingrese un medicamento para buscar:");

        cbLowStock.setBackground(new java.awt.Color(255, 255, 204));
        cbLowStock.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        cbLowStock.setForeground(new java.awt.Color(0, 0, 0));
        cbLowStock.setText("Solo medicamentos con poco stock");
        cbLowStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbLowStockActionPerformed(evt);
            }
        });

        cbExpDate.setBackground(new java.awt.Color(255, 255, 204));
        cbExpDate.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        cbExpDate.setForeground(new java.awt.Color(0, 0, 0));
        cbExpDate.setText("Solo medicamentos en rango de vencimiento");
        cbExpDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbExpDateActionPerformed(evt);
            }
        });

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

        resetTableBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        resetTableBtn.setText("Quitar Filtros");
        resetTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetTableBtnActionPerformed(evt);
            }
        });

        filterComboBox.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        filterComboBox.setForeground(new java.awt.Color(0, 0, 0));
        filterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Por Nombre", "Por Dosis", "Por Laboratorio", "Por Presentación" }));
        filterComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterComboBoxItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(38, 38, 38)
                .addComponent(searchBarLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 968, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel4Layout.createSequentialGroup()
                                .addComponent(cbExpDate)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cbLowStock)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(resetTableBtn)
                                .addGap(63, 63, 63)
                                .addComponent(addMedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 193, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(24, 24, 24))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addComponent(borrarButton)
                        .addGap(46, 46, 46))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchBarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(37, 37, 37)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(resetTableBtn)
                    .addComponent(cbLowStock)
                    .addComponent(cbExpDate)
                    .addComponent(addMedBtn))
                .addGap(18, 18, 18)
                .addComponent(borrarButton)
                .addGap(18, 18, 18)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(23, Short.MAX_VALUE))
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

        jTabbedPane2.addTab("Medicamentos", jPanel4);

        jPanel5.setBackground(new java.awt.Color(255, 255, 204));

        meEqTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        meEqTable.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        meEqTable.setForeground(new java.awt.Color(0, 0, 0));
        meEqTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Id", "Nombre", "Stock"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, true, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        meEqTable.setRowHeight(32);
        /** centrado de las columnas */
        DefaultTableCellRenderer meCenterRndr = new DefaultTableCellRenderer();
        meCenterRndr.setHorizontalAlignment(JLabel.CENTER);

        Enumeration<TableColumn> meColModel = meEqTable.getColumnModel().getColumns();
        while(meColModel.hasMoreElements()) {
            meColModel.nextElement().setCellRenderer(meCenterRndr);
        }

        /** tamaño de la fila del header */
        meEqTable.getTableHeader().setPreferredSize(new Dimension(50, 50));

        /** centrado del header */
        JLabel meHeader = (JLabel) meEqTable.getTableHeader().getDefaultRenderer();
        meHeader.setHorizontalAlignment(JLabel.CENTER);

        /** buscador para la barra de busqueda */
        meRowSorter = new TableRowSorter<>(meEqTable.getModel());
        meEqTable.setRowSorter(meRowSorter);
        jScrollPane4.setViewportView(meEqTable);
        if (meEqTable.getColumnModel().getColumnCount() > 0) {
            meEqTable.getColumnModel().getColumn(0).setResizable(false);
            meEqTable.getColumnModel().getColumn(1).setResizable(false);
            meEqTable.getColumnModel().getColumn(2).setResizable(false);
        }

        jLabel3.setFont(new java.awt.Font("Cascadia Code", 0, 16)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(0, 0, 0));
        jLabel3.setText("Ingrese Nombre para Buscar:");

        meSearchBar.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        meSearchBar.setForeground(new java.awt.Color(0, 0, 0));
        meSearchBar.setPreferredSize(new java.awt.Dimension(15, 30));

        meCbLowStock.setBackground(new java.awt.Color(255, 255, 204));
        meCbLowStock.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        meCbLowStock.setForeground(new java.awt.Color(0, 0, 0));
        meCbLowStock.setText("Solo Equipo Médico con poco Stock");
        meCbLowStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meCbLowStockActionPerformed(evt);
            }
        });

        resetMeTableBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        resetMeTableBtn.setForeground(new java.awt.Color(0, 0, 0));
        resetMeTableBtn.setText("Quitar Filtros");
        resetMeTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetMeTableBtnActionPerformed(evt);
            }
        });

        addMEBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        addMEBtn.setForeground(new java.awt.Color(0, 0, 0));
        addMEBtn.setText("Agregar Equipo Médico");
        addMEBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMEBtnActionPerformed(evt);
            }
        });

        delMEBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        delMEBtn.setForeground(new java.awt.Color(0, 0, 0));
        delMEBtn.setText("Borrar Equipo Médico");
        delMEBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delMEBtnActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addContainerGap(67, Short.MAX_VALUE)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(142, 142, 142)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(addMEBtn)
                    .addComponent(delMEBtn)
                    .addComponent(resetMeTableBtn)
                    .addComponent(meCbLowStock))
                .addGap(89, 89, 89))
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(137, 137, 137)
                .addComponent(jLabel3)
                .addGap(49, 49, 49)
                .addComponent(meSearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGap(66, 66, 66)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(meSearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 71, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(addMEBtn)
                        .addGap(33, 33, 33)
                        .addComponent(delMEBtn)
                        .addGap(63, 63, 63)
                        .addComponent(resetMeTableBtn)
                        .addGap(33, 33, 33)
                        .addComponent(meCbLowStock)
                        .addGap(221, 221, 221))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(41, 41, 41))))
        );

        meSearchBar.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                String text = meSearchBar.getText();

                if (text.trim().length() == 0) {
                    meRowSorter.setRowFilter(null);
                } else {
                    meRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                String text = meSearchBar.getText();

                if (text.trim().length() == 0) {
                    meRowSorter.setRowFilter(null);
                } else {
                    meRowSorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
                }
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                throw new UnsupportedOperationException("Not supported yet.");
            }
        });

        jTabbedPane2.addTab("Equipo Médico", jPanel5);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1019, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addGap(20, 20, 20)
                        .addComponent(jTabbedPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 669, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Inicio", mainPanel);

        jPanel1.setBackground(new java.awt.Color(255, 255, 204));

        DefaultListModel mlModel = new DefaultListModel();
        missingsList.setModel(mlModel);
        missingsList.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        missingsList.setForeground(new java.awt.Color(0, 0, 0));
        DefaultListCellRenderer mlCellRenderer = (DefaultListCellRenderer) missingsList.getCellRenderer();
        mlCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        jScrollPane1.setViewportView(missingsList);

        mlLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        mlLabel.setForeground(new java.awt.Color(0, 0, 0));
        mlLabel.setText("Medicamentos y Equipo Médico Faltante:");

        slLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        slLabel.setForeground(new java.awt.Color(0, 0, 0));
        slLabel.setText("Lista de Pedidos:");

        addToSLBtn.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        addToSLBtn.setForeground(new java.awt.Color(0, 0, 0));
        addToSLBtn.setText("Agregar");
        addToSLBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addToSLBtnActionPerformed(evt);
            }
        });

        removeFromSTBtn.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        removeFromSTBtn.setForeground(new java.awt.Color(0, 0, 0));
        removeFromSTBtn.setText("Quitar");
        removeFromSTBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeFromSTBtnActionPerformed(evt);
            }
        });

        toTF.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        toTF.setForeground(new java.awt.Color(0, 0, 0));
        toTF.setHorizontalAlignment(javax.swing.JTextField.CENTER);

        toTFLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        toTFLabel.setForeground(new java.awt.Color(0, 0, 0));
        toTFLabel.setText("Enviar a:");

        fromTFLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        fromTFLabel.setForeground(new java.awt.Color(0, 0, 0));
        fromTFLabel.setText("Enviar desde:");

        fromTF.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        fromTF.setForeground(new java.awt.Color(0, 0, 0));
        fromTF.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fromTF.setText("unisancolser@hotmail.com");

        sendSolBtn.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        sendSolBtn.setForeground(new java.awt.Color(0, 0, 0));
        sendSolBtn.setText("Enviar Solicitud");
        sendSolBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sendSolBtnActionPerformed(evt);
            }
        });

        solicitudeTable.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        solicitudeTable.setForeground(new java.awt.Color(0, 0, 0));
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
            solicitudeTable.getColumnModel().getColumn(0).setPreferredWidth(300);
            solicitudeTable.getColumnModel().getColumn(1).setResizable(false);
            solicitudeTable.getColumnModel().getColumn(2).setResizable(false);
            solicitudeTable.getColumnModel().getColumn(3).setResizable(false);
        }

        emailSubject.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        emailSubject.setForeground(new java.awt.Color(0, 0, 0));
        emailSubject.setText("Solicitud de Stock");

        jLabel1.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(0, 0, 0));
        jLabel1.setText("Asunto:");

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        emailComment.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        emailComment.setForeground(new java.awt.Color(0, 0, 0));
        jScrollPane2.setViewportView(emailComment);

        jLabel2.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(0, 0, 0));
        jLabel2.setText("Comentario:");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mlLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addToSLBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(slLabel)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(toTFLabel)
                            .addComponent(toTF)
                            .addComponent(fromTFLabel)
                            .addComponent(fromTF)
                            .addComponent(jLabel1)
                            .addComponent(emailSubject)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sendSolBtn))
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap(901, Short.MAX_VALUE)
                .addComponent(removeFromSTBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(359, 359, 359))
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
                            .addComponent(mlLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(slLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
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
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 74, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(sendSolBtn))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addToSLBtn)
                    .addComponent(removeFromSTBtn))
                .addGap(253, 253, 253))
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
        resetMedTableModel();
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
        AddMedFrame addFrame = new AddMedFrame(this);
        addFrame.setVisible(true);
        addFrame.pack();
        addFrame.setLocationRelativeTo(null);
        addFrame.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
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
            resetMedTableModel();
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
            resetMedTableModel();
        }
    }//GEN-LAST:event_cbLowStockActionPerformed

    private void addToSLBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToSLBtnActionPerformed
        // TODO add your handling code here:
        List<String> selValues = missingsList.getSelectedValuesList();

        if (selValues.size() > 0) {
            DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();
            DefaultListModel mlModel = (DefaultListModel) missingsList.getModel();

            selValues.forEach(e -> {
                if (!contains(solicitudeTable, e)) {
                    stModel.addRow(new Object[]{e, 0, 0, 0});
                    mlModel.removeElement(e);
                }
            });
        }
    }//GEN-LAST:event_addToSLBtnActionPerformed

    private void removeFromSTBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromSTBtnActionPerformed
        // TODO add your handling code here:
        int[] selValues = solicitudeTable.getSelectedRows();

        if (selValues.length > 0) {
            DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();
            DefaultListModel mlModel = (DefaultListModel) missingsList.getModel();

            for (int i = selValues.length - 1; i >= 0; i--) {
                if (!mlModel.contains(stModel.getValueAt(selValues[i], 0))) {
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

        if (ev.isValid(toEmail) && ev.isValid(fromEmail)) {
            try {
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(".\\table.html", false))) {
                    bw.write("<html>");
                    bw.write("<body style='max-width: 500px; margin: auto;'>");
                    bw.write("<p>" + StringEscapeUtils.escapeHtml4(emailCmt) + "</p>");

                    bw.write("<table style='border: 1px solid black;'>");

                    bw.write("<tr style='border: 1px solid black;'>");
                    for (int c = 0; c < model.getColumnCount(); ++c) {
                        bw.write("<th style='text-align: center; border: 1px solid black;'>");
                        bw.write(model.getColumnName(c));
                        bw.write("</th>");
                    }
                    bw.write("</tr>");

                    for (int r = 0; r < model.getRowCount(); ++r) {
                        bw.write("<tr style='border: 1px solid black;'>");

                        for (int c = 0; c < model.getColumnCount(); ++c) {
                            bw.write("<td style='text-align: center; border: 1px solid black;'>");
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
                        .from("Unidad Sanitaria Colonia Seré", fromEmail)
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
                        options[0]
                );

                if (op == 0) {
                    String password = new String(passField.getPassword());

                    if (!password.equals("")) {
                        if (!model.getDataVector().isEmpty()) {
                            Mailer mailer = MailerBuilder
                                    .withSMTPServer("smtp.office365.com", 587, fromEmail, password)
                                    .withTransportStrategy(TransportStrategy.SMTP_TLS)
                                    .withDebugLogging(true)
                                    .async()
                                    .buildMailer();

                            AsyncResponse res = mailer.sendMail(email, true);

                            if (res != null) {
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
                                    if (e.getCause().getClass().equals(AuthenticationFailedException.class)) {
                                        JOptionPane.showMessageDialog(
                                                this,
                                                "La contraseña para el email " + fromEmail + " es incorrecta. Por favor intente de nuevo.",
                                                "Error",
                                                JOptionPane.ERROR_MESSAGE
                                        );
                                    }
                                });
                            } else {
                                JOptionPane.showMessageDialog(this, "Ha ocurrido un error en el envío del email.");
                            }
                        } else {
                            JOptionPane.showMessageDialog(this, "La tabla para la solicitud esta vacia. Por favor cargue elementos a la tabla.");
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Debe ingrear una contraseña para enviar el email.");
                    }
                }
            } catch (Exception e) {
                System.out.println(e);
            }
        } else {
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

    private void delMEBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delMEBtnActionPerformed
        // TODO add your handling code here:
        if (meEqTable.getSelectedRow() != -1) {
             DefaultTableModel model = (DefaultTableModel) meEqTable.getModel();

             int confirm = JOptionPane.showConfirmDialog(
                     this,
                     "¿Está seguro que desea borrar este elemento?",
                     "Confirmación",
                     JOptionPane.YES_NO_OPTION
             );

             if (JOptionPane.YES_OPTION == confirm) {
                 int row = meEqTable.getSelectedRow();
                 int id = (int) model.getValueAt(row, 0);

                 EquipoMedicoDAO meDAO = new EquipoMedicoDAO();
                 meDAO.delete(id);
                 model.removeRow(meEqTable.getSelectedRow());

                 checkAlerts();
             }
         }
    }//GEN-LAST:event_delMEBtnActionPerformed

    private void addMEBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMEBtnActionPerformed
        // TODO add your handling code here:
        AddMEDialog dialog = new AddMEDialog(this, false);
        dialog.setVisible(true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }//GEN-LAST:event_addMEBtnActionPerformed

    private void meCbLowStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meCbLowStockActionPerformed
        // TODO add your handling code here:
        DefaultTableModel model = (DefaultTableModel) meEqTable.getModel();
        EquipoMedicoDAO meDAO = new EquipoMedicoDAO();

        if (meCbLowStock.isSelected()) {
            List<EquipoMedico> me = meDAO.medEqWithLowStock();

            model.setRowCount(0);

            if (me != null) {
                me.forEach(m -> {
                    model.addRow(
                            new Object[]{
                                m.getId(),
                                m.getNombre(),
                                m.getStock()
                            });
                });
            }
        } else {
            resetMeTableModel();
        }
    }//GEN-LAST:event_meCbLowStockActionPerformed

    private void resetMeTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetMeTableBtnActionPerformed
        // TODO add your handling code here:
        resetMeTableModel();
    }//GEN-LAST:event_resetMeTableBtnActionPerformed

    private void meStockAlertActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meStockAlertActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meStockAlertActionPerformed

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
            if (rows.get(i).get(0).equals(value)) {
                return true;
            }
        }

        return false;
    }

    private void filterTable() {
        String text = searchBar.getText();
        int indexSearch = 0;

        switch (filterComboBox.getSelectedItem().toString()) {
            case "Por Nombre":
                indexSearch = 1;
                break;
            case "Por Dosis":
                indexSearch = 4;
                break;
            case "Por Presentación":
                indexSearch = 5;
                break;
            case "Por Laboratorio":
                indexSearch = 6;
                break;
            default:
                indexSearch = 0;
                break;
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
        List<Medicamento> medsInExpRange = (List<Medicamento>) medDAO.medsInExpRange();

        DefaultListModel mlModel = (DefaultListModel) missingsList.getModel();
        DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();

        mlModel.removeAllElements();

        if (medsWithLowStock != null) {
            if (medsWithLowStock.size() > 0) {
                medStockAlert.setText("Hay medicamentos con bajo stock!");
                medStockAlert.setDisabledTextColor(Color.red);

                medsWithLowStock.forEach(m -> {
                    mlModel.addElement(m.getNombre() + " " + m.getPresentacion() + " (" + m.getDosis() + ")");
                });
            } else {
                medStockAlert.setText("No hay medicamentos con bajo stock");
                medStockAlert.setDisabledTextColor(Color.green);
            }
        }

        if (medsInExpRange != null) {
            if (medsInExpRange.size() > 0) {
                medExpAlert.setText("Hay medicamentos en rango de vencimiento!");
                medExpAlert.setDisabledTextColor(Color.red);

                medsInExpRange.forEach(m -> {
                    if (!medsWithLowStock.contains(m)) {
                        mlModel.addElement(m.getNombre() + " " + m.getPresentacion() + " (" + m.getDosis() + ")");
                    }
                });
            } else {
                medExpAlert.setText("No hay vencimientos cercanos");
                medExpAlert.setDisabledTextColor(Color.green);
            }
        }

        List<EquipoMedico> medEqWithLowStock = medEqDAO.medEqWithLowStock();

        if (medEqWithLowStock != null) {
            if (medEqWithLowStock.size() > 0) {
                meStockAlert.setText("Hay equipo médico con bajo stock!");
                meStockAlert.setDisabledTextColor(Color.red);
                
                medEqWithLowStock.forEach(me -> {
                    mlModel.addElement(me.getNombre());
                });
            }
            else {
                meStockAlert.setText("No hay equipo médico con bajo stock");
                meStockAlert.setDisabledTextColor(Color.green);
            }
        }

        Vector<Vector> rows = stModel.getDataVector();

        for (int i = 0; i < rows.size(); i++) {
            Object e = rows.get(i).get(0);

            if (!mlModel.contains(e)) {
                stModel.removeRow(i);
                i--;
            } else {
                mlModel.removeElement(e);
            }
        }
    }

    /**
     * Restaura la tabla de medicamentos.
     */
    public void resetMedTableModel() {
        DefaultTableModel medModel = (DefaultTableModel) medTable.getModel();
        MedicamentoDAO medDAO = new MedicamentoDAO();
        List<Medicamento> meds = medDAO.selectAll();

        if (meds != null) {
            medModel.setNumRows(0);
            
            meds.forEach(m -> {                
                medModel.addRow(
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
            
        /**
         * Tabla de medicamentos
        **/
        rowSorter.setRowFilter(null);
        filterComboBox.setSelectedIndex(0);
        searchBar.setText("");
        medTable.getRowSorter().setSortKeys(null);
        cbLowStock.setSelected(false);
        cbExpDate.setSelected(false);
    }
    
    public void resetMeTableModel() {
        DefaultTableModel meModel = (DefaultTableModel) meEqTable.getModel();
        EquipoMedicoDAO meDAO = new EquipoMedicoDAO();
        List<EquipoMedico> me = meDAO.selectAll();
        
        if(me != null) {
            meModel.setNumRows(0);
            
            me.forEach(elem -> {
                meModel.addRow(
                        new Object[] {
                            elem.getId(),
                            elem.getNombre(),
                            elem.getStock()
                        }
                );
            });
        }
        
        /**
         * Tabla de equipo medico
        **/
        meRowSorter.setRowFilter(null);
        meSearchBar.setText("");
        meEqTable.getRowSorter().setSortKeys(null);
        meCbLowStock.setSelected(false);
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
    javax.swing.JButton addMEBtn;
    javax.swing.JButton addMedBtn;
    javax.swing.JButton addToSLBtn;
    javax.swing.JButton borrarButton;
    javax.swing.JCheckBox cbExpDate;
    javax.swing.JCheckBox cbLowStock;
    javax.swing.JButton delMEBtn;
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
    javax.swing.JLabel jLabel3;
    javax.swing.JPanel jPanel1;
    javax.swing.JPanel jPanel2;
    javax.swing.JPanel jPanel3;
    javax.swing.JPanel jPanel4;
    javax.swing.JPanel jPanel5;
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JScrollPane jScrollPane3;
    javax.swing.JScrollPane jScrollPane4;
    javax.swing.JTabbedPane jTabbedPane1;
    javax.swing.JTabbedPane jTabbedPane2;
    javax.swing.JPanel mainPanel;
    javax.swing.JCheckBox meCbLowStock;
    javax.swing.JTable meEqTable;
    javax.swing.JTextField meSearchBar;
    javax.swing.JTextField meStockAlert;
    javax.swing.JTextField medExpAlert;
    javax.swing.JTextField medStockAlert;
    javax.swing.JTable medTable;
    javax.swing.JList<String> missingsList;
    javax.swing.JLabel mlLabel;
    javax.swing.JButton removeFromSTBtn;
    javax.swing.JButton resetMeTableBtn;
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
    // End of variables declaration//GEN-END:variables
}
