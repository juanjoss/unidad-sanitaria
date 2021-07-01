package ui;

import dao.*;
import db.SQLiteDAO;
import java.awt.Color;
import java.awt.Dimension;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
import javax.swing.ListCellRenderer;
import javax.swing.RowFilter;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import model.*;
import model.Medicamento;
import model.EquipoMedico;
import model.Usuario;
import org.apache.commons.text.StringEscapeUtils;
import util.DateUtil;
import util.SesionUsuario;

import org.apache.commons.validator.routines.EmailValidator;
import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.AsyncResponse;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.email.EmailBuilder;
import org.simplejavamail.mailer.MailerBuilder;
import util.PintarLista;

public class MainFrame extends javax.swing.JFrame {

    public MainFrame() {
        SQLiteDAO.getConn();

        if (SesionUsuario.getInstance().isLogged()) {
            loggedIn(SesionUsuario.getInstance().getLoggedUser());
        } else {
            logP = new LoginPanel(this);
            this.add(logP);
        }
        
        setTitle("Control de stock de farmacia - Unidad Sanitaria Colonia Seré");
        setMinimumSize(new java.awt.Dimension(1200, 600));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
    }

    public void loggedIn(Usuario user) {
        
        remove(logP);
        
        SesionUsuario sesion = SesionUsuario.getInstance();
        sesion.setLoggedUser(user);

        initComponents();

        // Components names
        searchBar.setName("searchBar");
        mainTabbedPane.setName("mainTabbedPane");
        addMEBtn.setName("addMEBtn");        

        DefaultTableModel medModel = (DefaultTableModel) medTable.getModel();
        DefaultTableModel meModel = (DefaultTableModel) meEqTable.getModel();
        DefaultTableModel stModel = (DefaultTableModel) solicitudeTable.getModel();
        DefaultTableModel pModel = (DefaultTableModel) pedidoTable.getModel();

        MedicamentoDAO medDAO = new MedicamentoDAO();
        PedidoDAO pedDAO = new PedidoDAO();

        /**
         * Se carga la tabla medicamentos desde la BD y se remueve la columna Id
         */
        resetMedTableModel();
        resetMeTableModel();
        medTable.removeColumn(medTable.getColumnModel().getColumn(0));
        meEqTable.removeColumn(meEqTable.getColumnModel().getColumn(0));

        /**
         * Evento para la actualizacion de filas en la tabla de equipo médico en la BD.
         */
        meModel.addTableModelListener((TableModelEvent evt) -> {
            if (evt.getType() == TableModelEvent.UPDATE && evt.getColumn() != TableModelEvent.ALL_COLUMNS) {
                int id = (int) meModel.getValueAt(evt.getFirstRow(), 0);
                String name = (String) meModel.getValueAt(evt.getFirstRow(), 1);
                int stock = (int) meModel.getValueAt(evt.getFirstRow(), 2);
                
                EquipoMedicoDAO meDAO = new EquipoMedicoDAO();
                EquipoMedico me = meDAO.getMedEq(id);
                boolean error = false;
                
                if (name.isEmpty()) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Nombre está vacío!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    meModel.setValueAt(me.getNombre(), evt.getFirstRow(), 1);
                    error = true;
                }
                
                if (stock < 0) {
                    JOptionPane.showMessageDialog(
                            this,
                            "¡El campo Stock no puede ser menor a cero!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );

                    meModel.setValueAt(me.getStock(), evt.getFirstRow(), 2);
                    error = true;
                }
                
                if (!error) {
                    String nameAux = me.getNombre();
                    me.setNombre(name);
                    me.setStock(stock);
                    
                    if (meDAO.update(me)) {
                        checkAlerts();
                    } else {
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
         * Evento para la actualizacion de filas la tabla de medicamentos en la BD.
         */
        medModel.addTableModelListener((TableModelEvent evt) -> {  
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
        
        /**
         * Se carga la tabla pedidos desde la BD.
         */
        resetTablePedidos();
        
        /**
         * Evento para la actualizacion de filas de la tabla pedidos en la BD.
         */
        pModel.addTableModelListener((TableModelEvent evt) -> {
            if (evt.getType() == TableModelEvent.UPDATE && evt.getColumn() != TableModelEvent.ALL_COLUMNS) {
                
                int idPedido = (int) pModel.getValueAt(evt.getFirstRow(), 0);
                String estado = (String) pModel.getValueAt(evt.getFirstRow(), 3);
                
                Pedido pedido = pedDAO.getPedido(idPedido);
                
                pedido.setEstado(estado);
                pedDAO.update(pedido);
                
                checkAlerts();
            }
        });
        
        checkAlerts();
        PintarLista pintarLista = new PintarLista();
        missingsList.setCellRenderer(pintarLista); 
    }
    
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        jTabbedPane1 = new javax.swing.JTabbedPane();
        mainPanel = new javax.swing.JPanel();
        alertsPanel = new javax.swing.JPanel();
        meStockAlert = new javax.swing.JTextField();
        medExpAlert = new javax.swing.JTextField();
        stockAlertLbl = new javax.swing.JLabel();
        medStockAlert = new javax.swing.JTextField();
        expAlertLbl = new javax.swing.JLabel();
        mainTabbedPane = new javax.swing.JTabbedPane();
        mainMedsPanel = new javax.swing.JPanel();
        searchBarLabel = new javax.swing.JLabel();
        searchBar = new javax.swing.JTextField();
        scrollPane = new javax.swing.JScrollPane();
        medTable = new javax.swing.JTable();
        cbExpDate = new javax.swing.JCheckBox();
        resetTableBtn = new javax.swing.JButton();
        cbLowStock = new javax.swing.JCheckBox();
        filterComboBox = new javax.swing.JComboBox<>();
        addMedBtn = new javax.swing.JButton();
        borrarButton = new javax.swing.JButton();
        mainMeEqPanel = new javax.swing.JPanel();
        meEqTableScrollPane = new javax.swing.JScrollPane();
        meEqTable = new javax.swing.JTable();
        meEqSearchBarLabel = new javax.swing.JLabel();
        meSearchBar = new javax.swing.JTextField();
        addMEBtn = new javax.swing.JButton();
        delMEBtn = new javax.swing.JButton();
        resetMeTableBtn = new javax.swing.JButton();
        meCbLowStock = new javax.swing.JCheckBox();
        solicitudePanel = new javax.swing.JPanel();
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
        jLabel3 = new javax.swing.JLabel();
        pedidosPanel = new javax.swing.JPanel();
        listaPedidosLabel = new javax.swing.JLabel();
        detallesMedLabel = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        pedidoTable = new javax.swing.JTable();
        leyenda = new javax.swing.JLabel();
        mostrarDetalleButton = new javax.swing.JButton();
        detallesEMLabel = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        detallesMedTable = new javax.swing.JTable();
        jScrollPane8 = new javax.swing.JScrollPane();
        detallesEMTable = new javax.swing.JTable();
        changePassPanel = new javax.swing.JPanel();
        newPassField = new javax.swing.JPasswordField();
        repeatPassField = new javax.swing.JPasswordField();
        newPassLabel = new javax.swing.JLabel();
        repeatPassLabel = new javax.swing.JLabel();
        changePassButton = new javax.swing.JButton();

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

        alertsPanel.setBackground(new java.awt.Color(255, 255, 204));

        meStockAlert.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        meStockAlert.setEnabled(false);

        medExpAlert.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        medExpAlert.setDisabledTextColor(new java.awt.Color(153, 255, 153));
        medExpAlert.setEnabled(false);

        stockAlertLbl.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        stockAlertLbl.setText("Estado del Stock:");

        medStockAlert.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        medStockAlert.setDisabledTextColor(new java.awt.Color(153, 255, 153));
        medStockAlert.setEnabled(false);

        expAlertLbl.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        expAlertLbl.setText("Estado de Vencimientos:");

        javax.swing.GroupLayout alertsPanelLayout = new javax.swing.GroupLayout(alertsPanel);
        alertsPanel.setLayout(alertsPanelLayout);
        alertsPanelLayout.setHorizontalGroup(
            alertsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(alertsPanelLayout.createSequentialGroup()
                .addGap(82, 82, 82)
                .addComponent(stockAlertLbl)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(medStockAlert, javax.swing.GroupLayout.Alignment.TRAILING)
            .addComponent(meStockAlert)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alertsPanelLayout.createSequentialGroup()
                .addContainerGap(66, Short.MAX_VALUE)
                .addComponent(expAlertLbl)
                .addGap(55, 55, 55))
            .addComponent(medExpAlert)
        );
        alertsPanelLayout.setVerticalGroup(
            alertsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, alertsPanelLayout.createSequentialGroup()
                .addContainerGap(150, Short.MAX_VALUE)
                .addComponent(stockAlertLbl)
                .addGap(18, 18, 18)
                .addComponent(medStockAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(meStockAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(35, 35, 35)
                .addComponent(expAlertLbl)
                .addGap(18, 18, 18)
                .addComponent(medExpAlert, javax.swing.GroupLayout.PREFERRED_SIZE, 47, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(195, 195, 195))
        );

        meStockAlert.setHorizontalAlignment(JTextField.CENTER);
        medExpAlert.setHorizontalAlignment(JTextField.CENTER);
        stockAlertLbl.setHorizontalAlignment(JLabel.CENTER);
        medStockAlert.setHorizontalAlignment(JTextField.CENTER);
        expAlertLbl.setHorizontalAlignment(JLabel.CENTER);

        mainTabbedPane.setBackground(new java.awt.Color(255, 255, 204));
        mainTabbedPane.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N

        mainMedsPanel.setBackground(new java.awt.Color(255, 255, 204));

        searchBarLabel.setFont(new java.awt.Font("Cascadia Code", 0, 16)); // NOI18N
        searchBarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        searchBarLabel.setText("Ingrese un medicamento para buscar:");

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

        cbExpDate.setBackground(new java.awt.Color(255, 255, 204));
        cbExpDate.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        cbExpDate.setText("Solo medicamentos en rango de vencimiento");
        cbExpDate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbExpDateActionPerformed(evt);
            }
        });

        resetTableBtn.setBackground(new java.awt.Color(255, 255, 204));
        resetTableBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        resetTableBtn.setText("Quitar Filtros");
        resetTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetTableBtnActionPerformed(evt);
            }
        });

        cbLowStock.setBackground(new java.awt.Color(255, 255, 204));
        cbLowStock.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        cbLowStock.setText("Solo medicamentos con poco stock");
        cbLowStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbLowStockActionPerformed(evt);
            }
        });

        filterComboBox.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        filterComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Por Nombre", "Por Dosis", "Por Laboratorio", "Por Presentación" }));
        filterComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                filterComboBoxItemStateChanged(evt);
            }
        });

        addMedBtn.setBackground(new java.awt.Color(255, 255, 204));
        addMedBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        addMedBtn.setText("Agregar Medicamento");
        addMedBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMedBtnActionPerformed(evt);
            }
        });

        borrarButton.setBackground(new java.awt.Color(255, 255, 204));
        borrarButton.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        borrarButton.setText("Borrar Medicamento");
        borrarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                borrarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainMedsPanelLayout = new javax.swing.GroupLayout(mainMedsPanel);
        mainMedsPanel.setLayout(mainMedsPanelLayout);
        mainMedsPanelLayout.setHorizontalGroup(
            mainMedsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMedsPanelLayout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(mainMedsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addGroup(mainMedsPanelLayout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(cbLowStock)
                        .addGap(18, 18, 18)
                        .addComponent(cbExpDate)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(resetTableBtn))
                    .addGroup(mainMedsPanelLayout.createSequentialGroup()
                        .addComponent(searchBarLabel)
                        .addGap(18, 18, 18)
                        .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGroup(mainMedsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainMedsPanelLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(mainMedsPanelLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addGroup(mainMedsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(borrarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(addMedBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 196, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(0, 19, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainMedsPanelLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 900, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(57, 57, 57))
        );
        mainMedsPanelLayout.setVerticalGroup(
            mainMedsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMedsPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainMedsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(searchBarLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(searchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(filterComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(28, 28, 28)
                .addGroup(mainMedsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(mainMedsPanelLayout.createSequentialGroup()
                        .addGap(30, 30, 30)
                        .addGroup(mainMedsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(cbExpDate)
                            .addComponent(resetTableBtn)
                            .addComponent(cbLowStock)))
                    .addGroup(mainMedsPanelLayout.createSequentialGroup()
                        .addComponent(addMedBtn)
                        .addGap(18, 18, 18)
                        .addComponent(borrarButton)))
                .addGap(18, 18, 18)
                .addComponent(scrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 461, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(34, 34, 34))
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

        mainTabbedPane.addTab("Medicamentos", mainMedsPanel);

        mainMeEqPanel.setBackground(new java.awt.Color(255, 255, 204));

        meEqTable.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        meEqTable.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
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
        meEqTableScrollPane.setViewportView(meEqTable);

        meEqSearchBarLabel.setFont(new java.awt.Font("Cascadia Code", 0, 16)); // NOI18N
        meEqSearchBarLabel.setText("Ingrese Nombre para Buscar:");

        meSearchBar.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        meSearchBar.setPreferredSize(new java.awt.Dimension(15, 30));

        addMEBtn.setBackground(new java.awt.Color(255, 255, 204));
        addMEBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        addMEBtn.setText("Agregar Equipo Médico");
        addMEBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMEBtnActionPerformed(evt);
            }
        });

        delMEBtn.setBackground(new java.awt.Color(255, 255, 204));
        delMEBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        delMEBtn.setText("Borrar Equipo Médico");
        delMEBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                delMEBtnActionPerformed(evt);
            }
        });

        resetMeTableBtn.setBackground(new java.awt.Color(255, 255, 204));
        resetMeTableBtn.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        resetMeTableBtn.setText("Quitar Filtros");
        resetMeTableBtn.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetMeTableBtnActionPerformed(evt);
            }
        });

        meCbLowStock.setBackground(new java.awt.Color(255, 255, 204));
        meCbLowStock.setFont(new java.awt.Font("Cascadia Code", 0, 12)); // NOI18N
        meCbLowStock.setText("Solo Equipo Médico con poco Stock");
        meCbLowStock.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meCbLowStockActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout mainMeEqPanelLayout = new javax.swing.GroupLayout(mainMeEqPanel);
        mainMeEqPanel.setLayout(mainMeEqPanelLayout);
        mainMeEqPanelLayout.setHorizontalGroup(
            mainMeEqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainMeEqPanelLayout.createSequentialGroup()
                .addContainerGap(114, Short.MAX_VALUE)
                .addComponent(meEqTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 509, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(76, 76, 76)
                .addGroup(mainMeEqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(resetMeTableBtn)
                    .addComponent(meCbLowStock)
                    .addGroup(mainMeEqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                        .addComponent(delMEBtn, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(addMEBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(51, 51, 51))
            .addGroup(mainMeEqPanelLayout.createSequentialGroup()
                .addGap(138, 138, 138)
                .addComponent(meEqSearchBarLabel)
                .addGap(53, 53, 53)
                .addComponent(meSearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 374, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        mainMeEqPanelLayout.setVerticalGroup(
            mainMeEqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainMeEqPanelLayout.createSequentialGroup()
                .addGap(44, 44, 44)
                .addGroup(mainMeEqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(meEqSearchBarLabel)
                    .addComponent(meSearchBar, javax.swing.GroupLayout.PREFERRED_SIZE, 45, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(68, 68, 68)
                .addGroup(mainMeEqPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(meEqTableScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainMeEqPanelLayout.createSequentialGroup()
                        .addGap(61, 61, 61)
                        .addComponent(addMEBtn)
                        .addGap(18, 18, 18)
                        .addComponent(delMEBtn)
                        .addGap(53, 53, 53)
                        .addComponent(meCbLowStock)
                        .addGap(18, 18, 18)
                        .addComponent(resetMeTableBtn)))
                .addContainerGap(78, Short.MAX_VALUE))
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

        mainTabbedPane.addTab("Equipo Médico", mainMeEqPanel);

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(mainTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 1018, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 24, Short.MAX_VALUE)
                .addComponent(alertsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(58, Short.MAX_VALUE))
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(alertsPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(mainPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(mainTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 681, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(47, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Inicio", mainPanel);

        solicitudePanel.setBackground(new java.awt.Color(255, 255, 204));

        DefaultListModel mlModel = new DefaultListModel();
        missingsList.setModel(mlModel);
        missingsList.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        DefaultListCellRenderer mlCellRenderer = (DefaultListCellRenderer) missingsList.getCellRenderer();
        mlCellRenderer.setHorizontalAlignment(JLabel.CENTER);
        jScrollPane1.setViewportView(missingsList);

        mlLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        mlLabel.setText("Medicamentos y Equipo Médico Faltante:");

        slLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        slLabel.setText("Lista de medicamentos y equipos médicos a pedir:");

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
        toTF.setText("Farmacia_garre@hotmail.com");

        toTFLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        toTFLabel.setText("Enviar a:");

        fromTFLabel.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        fromTFLabel.setText("Enviar desde:");

        fromTF.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        fromTF.setHorizontalAlignment(javax.swing.JTextField.CENTER);

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
            solicitudeTable.getColumnModel().getColumn(0).setPreferredWidth(300);
            solicitudeTable.getColumnModel().getColumn(1).setResizable(false);
            solicitudeTable.getColumnModel().getColumn(2).setResizable(false);
            solicitudeTable.getColumnModel().getColumn(3).setResizable(false);
        }

        emailSubject.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        emailSubject.setText("Solicitud de stock");

        jLabel1.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel1.setText("Asunto:");

        jScrollPane2.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);

        emailComment.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jScrollPane2.setViewportView(emailComment);

        jLabel2.setFont(new java.awt.Font("Cascadia Code", 0, 14)); // NOI18N
        jLabel2.setText("Comentario:");

        jLabel3.setFont(new java.awt.Font("Tahoma", 2, 14)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 0, 0));
        jLabel3.setText("(*) Los medicamentos o equipos médicos resaltados ya fueron pedidos, pero aún no han sido recibidos.");

        javax.swing.GroupLayout solicitudePanelLayout = new javax.swing.GroupLayout(solicitudePanel);
        solicitudePanel.setLayout(solicitudePanelLayout);
        solicitudePanelLayout.setHorizontalGroup(
            solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(solicitudePanelLayout.createSequentialGroup()
                .addContainerGap(39, Short.MAX_VALUE)
                .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(mlLabel)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 292, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addToSLBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 121, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 35, Short.MAX_VALUE)
                .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(slLabel)
                    .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 619, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(solicitudePanelLayout.createSequentialGroup()
                        .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(47, 47, 47)
                        .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(toTFLabel)
                            .addComponent(toTF, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE)
                            .addComponent(fromTFLabel)
                            .addComponent(fromTF)
                            .addComponent(jLabel1)
                            .addComponent(emailSubject)
                            .addComponent(jLabel2)
                            .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sendSolBtn))
                        .addContainerGap(35, Short.MAX_VALUE))
                    .addGroup(solicitudePanelLayout.createSequentialGroup()
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, solicitudePanelLayout.createSequentialGroup()
                .addContainerGap(901, Short.MAX_VALUE)
                .addComponent(removeFromSTBtn, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(359, 359, 359))
            .addGroup(solicitudePanelLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jLabel3)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        solicitudePanelLayout.setVerticalGroup(
            solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(solicitudePanelLayout.createSequentialGroup()
                .addGap(210, 210, 210)
                .addComponent(filler2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(solicitudePanelLayout.createSequentialGroup()
                .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(solicitudePanelLayout.createSequentialGroup()
                        .addGap(63, 63, 63)
                        .addComponent(filler1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, solicitudePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(mlLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(slLabel, javax.swing.GroupLayout.Alignment.TRAILING))))
                .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(solicitudePanelLayout.createSequentialGroup()
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
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 65, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(sendSolBtn))
                    .addGroup(solicitudePanelLayout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 350, Short.MAX_VALUE))))
                .addGap(18, 18, 18)
                .addGroup(solicitudePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addToSLBtn)
                    .addComponent(removeFromSTBtn))
                .addGap(27, 27, 27)
                .addComponent(jLabel3)
                .addGap(212, 212, 212))
        );

        fromTF.setText(SesionUsuario.getInstance().getLoggedUser().getEmail());

        jTabbedPane1.addTab("Solicitud de Medicamentos", solicitudePanel);

        pedidosPanel.setBackground(new java.awt.Color(255, 255, 204));
        pedidosPanel.setLayout(new java.awt.GridBagLayout());

        listaPedidosLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        listaPedidosLabel.setText("LISTADO DE PEDIDOS");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        pedidosPanel.add(listaPedidosLabel, gridBagConstraints);

        detallesMedLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        detallesMedLabel.setText("MEDICAMENTOS DEL PEDIDO #");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pedidosPanel.add(detallesMedLabel, gridBagConstraints);

        jScrollPane4.setPreferredSize(new java.awt.Dimension(600, 500));

        pedidoTable.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        pedidoTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Número pedido", "Correo del proveedor", "Fecha y hora del pedido", "Estado del pedido"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.Integer.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };
            boolean[] canEdit = new boolean [] {
                false, false, false, true
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        pedidoTable.setPreferredSize(new java.awt.Dimension(400, 600));
        pedidoTable.setRowHeight(30);
        pedidoTable.setRowMargin(2);
        pedidoTable.setSelectionBackground(new java.awt.Color(102, 102, 102));
        pedidoTable.setShowGrid(true);
        jScrollPane4.setViewportView(pedidoTable);
        pedidoTable.getAccessibleContext().setAccessibleDescription("");
        ((DefaultTableCellRenderer) pedidoTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        Enumeration<TableColumn> colModel2 = pedidoTable.getColumnModel().getColumns();
        while(colModel2.hasMoreElements()) {
            colModel2.nextElement().setCellRenderer(centerRndr);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.gridheight = 4;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pedidosPanel.add(jScrollPane4, gridBagConstraints);

        leyenda.setFont(new java.awt.Font("Arial", 2, 14)); // NOI18N
        String userName = SesionUsuario.getInstance().getLoggedUser().getUserName();
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pedidosPanel.add(leyenda, gridBagConstraints);
        leyenda.setText("Los pedidos que se muestran a continuación se hicieron bajo el usuario: " + userName);

        mostrarDetalleButton.setFont(new java.awt.Font("Arial", 1, 12)); // NOI18N
        mostrarDetalleButton.setText("Mostrar detalles del pedido");
        mostrarDetalleButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mostrarDetalleButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 6;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        pedidosPanel.add(mostrarDetalleButton, gridBagConstraints);

        detallesEMLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        detallesEMLabel.setText("EQUIPO MÉDICO DEL PEDIDO #");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 4;
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        pedidosPanel.add(detallesEMLabel, gridBagConstraints);

        jScrollPane7.setPreferredSize(new java.awt.Dimension(500, 200));

        detallesMedTable.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        detallesMedTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Descripción", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        detallesMedTable.setPreferredSize(new java.awt.Dimension(150, 200));
        detallesMedTable.setRowHeight(30);
        jScrollPane7.setViewportView(detallesMedTable);
        ((DefaultTableCellRenderer) detallesMedTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        Enumeration<TableColumn> colModel3 = detallesMedTable.getColumnModel().getColumns();
        while(colModel3.hasMoreElements()) {
            colModel3.nextElement().setCellRenderer(centerRndr);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 3;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pedidosPanel.add(jScrollPane7, gridBagConstraints);

        jScrollPane8.setPreferredSize(new java.awt.Dimension(500, 200));

        detallesEMTable.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        detallesEMTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Descripción", "Cantidad"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.Integer.class
            };
            boolean[] canEdit = new boolean [] {
                false, false
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        detallesEMTable.setPreferredSize(new java.awt.Dimension(150, 200));
        detallesEMTable.setRowHeight(30);
        jScrollPane8.setViewportView(detallesEMTable);
        ((DefaultTableCellRenderer) detallesEMTable.getTableHeader().getDefaultRenderer()).setHorizontalAlignment(SwingConstants.CENTER);

        Enumeration<TableColumn> colModel4 = detallesEMTable.getColumnModel().getColumns();
        while(colModel4.hasMoreElements()) {
            colModel4.nextElement().setCellRenderer(centerRndr);
        }

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 5;
        gridBagConstraints.insets = new java.awt.Insets(10, 10, 10, 10);
        pedidosPanel.add(jScrollPane8, gridBagConstraints);

        jTabbedPane1.addTab("Historial de Pedidos", pedidosPanel);

        changePassPanel.setBackground(new java.awt.Color(255, 255, 204));
        changePassPanel.setLayout(new java.awt.GridBagLayout());

        newPassField.setPreferredSize(new java.awt.Dimension(250, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        changePassPanel.add(newPassField, gridBagConstraints);

        repeatPassField.setPreferredSize(new java.awt.Dimension(250, 30));
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        gridBagConstraints.insets = new java.awt.Insets(10, 20, 10, 20);
        changePassPanel.add(repeatPassField, gridBagConstraints);

        newPassLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        newPassLabel.setText("Ingrese la nueva contraseña:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        changePassPanel.add(newPassLabel, gridBagConstraints);

        repeatPassLabel.setFont(new java.awt.Font("Arial", 1, 18)); // NOI18N
        repeatPassLabel.setText("Confirme la nueva contraseña:");
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        changePassPanel.add(repeatPassLabel, gridBagConstraints);

        changePassButton.setFont(new java.awt.Font("Arial", 1, 14)); // NOI18N
        changePassButton.setText("Cambiar");
        changePassButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                changePassButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 10, 0);
        changePassPanel.add(changePassButton, gridBagConstraints);

        jTabbedPane1.addTab("Cambiar contraseña", changePassPanel);

        getContentPane().add(jTabbedPane1, java.awt.BorderLayout.PAGE_START);

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    /**
     * Restaura la tabla de medicamentos.
     */
    private void resetTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetTableBtnActionPerformed
        resetMedTableModel();
    }//GEN-LAST:event_resetTableBtnActionPerformed

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

    private void sendSolBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sendSolBtnActionPerformed
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
                                    crearPedido();
                                    
                                    PedidoDAO pDAO = new PedidoDAO();
                                    crearDetalles(pDAO.ultimoPedido()); //Para que devuelva el ID del último pedido creado.
                                    
                                    solicitudeTable.removeAll();
                                    model.setRowCount(0);
                                    checkAlerts();
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

    private void removeFromSTBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeFromSTBtnActionPerformed
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

    private void addToSLBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addToSLBtnActionPerformed
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

    /**
     * Evento para el boton de eliminar medicamento.
     */
    private void borrarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_borrarButtonActionPerformed
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
     * Checkbox para filtrar en la tabla solo vencimientos en rango de 15 dias.
     */
    private void cbExpDateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbExpDateActionPerformed
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
    
    private void cbLowStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbLowStockActionPerformed
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

    /**
     * Botón para mostrar los detalles de un pedido.
     */
    private void mostrarDetalleButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mostrarDetalleButtonActionPerformed
        if (pedidoTable.getSelectedRow() != -1) {
            DefaultTableModel model = (DefaultTableModel) pedidoTable.getModel();
            int columIdPedido = 0;
            int row = pedidoTable.getSelectedRow();
            int idPedido = (int)model.getValueAt(row, columIdPedido);
           
           resetTableDetallesEM(idPedido); 
           resetTableDetallesMed(idPedido);
        }
    }//GEN-LAST:event_mostrarDetalleButtonActionPerformed
    
    
    private void filterComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_filterComboBoxItemStateChanged
        filterTable();
    }//GEN-LAST:event_filterComboBoxItemStateChanged

    private void addMEBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMEBtnActionPerformed
        AddMEDialog dialog = new AddMEDialog(this, false);
        dialog.setVisible(true);
        dialog.pack();
        dialog.setLocationRelativeTo(null);
        dialog.setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
    }//GEN-LAST:event_addMEBtnActionPerformed

    private void delMEBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_delMEBtnActionPerformed
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

    private void resetMeTableBtnActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetMeTableBtnActionPerformed
        resetMeTableModel();
    }//GEN-LAST:event_resetMeTableBtnActionPerformed

    private void meCbLowStockActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meCbLowStockActionPerformed
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

    /*
    * Cambio de contraseña.
    */
    private void changePassButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_changePassButtonActionPerformed
        String newPass = new String(newPassField.getPassword());
        String repeatPass = new String(repeatPassField.getPassword());
        
        UsuarioDAO uDAO = new UsuarioDAO();
        Usuario user = SesionUsuario.getInstance().getLoggedUser();
        
        if (!newPass.equals("") && !repeatPass.equals("")) {
            if (newPass.equals(repeatPass)) {
                uDAO.changePass(user.getId(), newPass);
                user.setPass(newPass);
                
                JOptionPane.showMessageDialog(
                        this,
                        "¡La contraseña se cambió exitosamente!",
                        "Éxito",
                        JOptionPane.INFORMATION_MESSAGE
                    );
                
                newPassField.setText("");
                repeatPassField.setText("");
            } else {
                JOptionPane.showMessageDialog(
                        this,
                        "¡Las contraseñas no coinciden! Intente de nuevo.",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                
                newPassField.setText("");
                repeatPassField.setText("");
            }
        } else {
            JOptionPane.showMessageDialog(
                    this,
                    "¡Los campos están vacíos! Por favor, ingrese la nueva contraseña.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE
                );
        }
    }//GEN-LAST:event_changePassButtonActionPerformed

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
                medStockAlert.setText("Hay medicamentos con poco stock!");
                medStockAlert.setDisabledTextColor(Color.red);
                
                medsWithLowStock.forEach(m -> {
                    mlModel.addElement(m.getNombre() + " - " + m.getPresentacion() + " - " + m.getDosis());
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
                    if (!medsWithLowStock.contains(m)) {
                        mlModel.addElement(m.getNombre() + " - " + m.getPresentacion() + " - " + m.getDosis());
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
     * Al enviarse la solicitud se crea el pedido.
     */
    private void crearPedido() {
        Date date = new Date();
        DateFormat hourFormat = new SimpleDateFormat("HH:mm:ss");
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fecha_pedido = "" + dateFormat.format(date) + " - " + hourFormat.format(date);

        int idUsuario = SesionUsuario.getInstance().getLoggedUser().getId();

        Pedido pedido = new Pedido();
        pedido.setIdUsuario(idUsuario);
        pedido.setCorreoProveedor(toTF.getText());
        pedido.setFecha(fecha_pedido);
        pedido.setEstado("Enviado");

        PedidoDAO pDAO = new PedidoDAO();
        pDAO.insert(pedido);

        resetTablePedidos();
    }
    
    /**
     * Se crean los detalles de un pedido.
     * 
     * @param idPedido {@code int} id del pedido.
     */
    private void crearDetalles(int idPedido) {
        DefaultTableModel model = (DefaultTableModel) solicitudeTable.getModel();
        
        for (int r = 0; r < model.getRowCount(); ++r) {
            String descripcion = "";
            String[] partes = null;
            String mg = "";
            String comprimido = "";
            int cantidad = 0;
            
            for (int c = 0; c < model.getColumnCount(); ++c) {
                switch (c) {
                    case 0:
                        descripcion = model.getValueAt(r, c).toString();
                        partes = descripcion.split("-");
                        break;
                    case 1:
                        mg = model.getValueAt(r, c).toString();
                        break;
                    case 2:
                        comprimido = model.getValueAt(r, c).toString();
                        break;
                    case 3:
                        cantidad = Integer.parseInt(model.getValueAt(r, c).toString());
                        break;
                }
            }
            
            EquipoMedicoDAO equipoMedicoDAO = new EquipoMedicoDAO();
            MedicamentoDAO medicamentoDAO = new MedicamentoDAO();
            
            if (partes.length == 3) {  //Se controla si es un medicamento o un equipo médico
                Medicamento m = medicamentoDAO.buscarPorNombrePresentacion(partes[0].trim(), partes[1].trim(), partes[2].trim());
                
                DetallePedidoM dpm = new DetallePedidoM();
                dpm.setCantidad(cantidad);
                dpm.setDescripcion(descripcion + " - mg:" + mg + " - Comprimido:" + comprimido);
                dpm.setPedido_id(idPedido);
                dpm.setMedicamento_id(m.getId());
                
                DetallePedidoMDAO dpmDAO = new DetallePedidoMDAO();
                dpmDAO.insert(dpm);
            } else {
                EquipoMedico em = equipoMedicoDAO.buscarPorNombre(partes[0].trim());
                
                DetallePedidoEM dpem = new DetallePedidoEM();
                dpem.setCantidad(cantidad);
                dpem.setDescripcion(descripcion);
                dpem.setPedido_id(idPedido);
                dpem.setEquipoMedico_id(em.getId());
                
                DetallePedidoEMDAO dpemDAO = new DetallePedidoEMDAO();
                dpemDAO.insert(dpem);
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
        checkAlerts();
    }
    
    /**
     * Restaura la tabla de equipos médicos.
     */
    public void resetMeTableModel() {
        DefaultTableModel meModel = (DefaultTableModel) meEqTable.getModel();
        EquipoMedicoDAO meDAO = new EquipoMedicoDAO();
        List<EquipoMedico> me = meDAO.selectAll();
        
        if (me != null) {
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
        checkAlerts();
    }

    /**
     * Restaura la tabla de pedidos.
     */
    private void resetTablePedidos() {
        DefaultTableModel pmodel = (DefaultTableModel) pedidoTable.getModel();
        PedidoDAO pedDAO = new PedidoDAO();
        List<Pedido> peds = pedDAO.selectAllxId(SesionUsuario.getInstance().getLoggedUser().getId());

        if (peds != null) {
            pmodel.setNumRows(0);

            peds.forEach(p -> {
                pmodel.addRow(
                        new Object[]{
                            p.getId(),
                            p.getCorreoProveedor(),
                            p.getFecha(),
                            p.getEstado()
                        });
            });
        }
    }
    
    /**
     * Restaura la tabla de los medicamentos que tiene un pedido.
     * 
     * @param pedidoId {@code int} id del pedido.
     */
    private void resetTableDetallesMed(int pedidoId) {
        DefaultTableModel model = (DefaultTableModel) detallesMedTable.getModel();
        DetallePedidoMDAO dpmDAO = new DetallePedidoMDAO();
        List<DetallePedidoM> detallesMed = dpmDAO.selectXPedidoId(pedidoId);

        if (detallesMed != null) {
            model.setNumRows(0);

            detallesMed.forEach(m -> {
                model.addRow(
                        new Object[]{
                            m.getDescripcion(),
                            m.getCantidad(),
                        });
            });
        }
        
        detallesMedLabel.setText("MEDICAMENTOS DEL PEDIDO #" + pedidoId);
    }
    
    /**
     * Restaura la tabla de los equipos médicos que tiene un pedido.
     * 
     * @param pedidoId {@code int} id del pedido.
     */
    private void resetTableDetallesEM(int pedidoId) {
        DefaultTableModel model = (DefaultTableModel) detallesEMTable.getModel();
        DetallePedidoEMDAO dpemDAO = new DetallePedidoEMDAO();
        List<DetallePedidoEM> detallesEM = dpemDAO.selectXPedidoId(pedidoId);

        if (detallesEM != null) {
            model.setNumRows(0);

            detallesEM.forEach(em -> {
                model.addRow(
                        new Object[]{
                            em.getDescripcion(),
                            em.getCantidad(),
                        });
            });
        }
        
        detallesEMLabel.setText("EQUIPOS MÉDICOS DEL PEDIDO #" + pedidoId);
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
    javax.swing.JPanel alertsPanel;
    javax.swing.JButton borrarButton;
    javax.swing.JCheckBox cbExpDate;
    javax.swing.JCheckBox cbLowStock;
    javax.swing.JButton changePassButton;
    javax.swing.JPanel changePassPanel;
    javax.swing.JButton delMEBtn;
    javax.swing.JLabel detallesEMLabel;
    javax.swing.JTable detallesEMTable;
    javax.swing.JLabel detallesMedLabel;
    javax.swing.JTable detallesMedTable;
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
    javax.swing.JScrollPane jScrollPane1;
    javax.swing.JScrollPane jScrollPane2;
    javax.swing.JScrollPane jScrollPane3;
    javax.swing.JScrollPane jScrollPane4;
    javax.swing.JScrollPane jScrollPane7;
    javax.swing.JScrollPane jScrollPane8;
    javax.swing.JTabbedPane jTabbedPane1;
    javax.swing.JLabel leyenda;
    javax.swing.JLabel listaPedidosLabel;
    javax.swing.JPanel mainMeEqPanel;
    javax.swing.JPanel mainMedsPanel;
    javax.swing.JPanel mainPanel;
    javax.swing.JTabbedPane mainTabbedPane;
    javax.swing.JCheckBox meCbLowStock;
    javax.swing.JLabel meEqSearchBarLabel;
    javax.swing.JTable meEqTable;
    javax.swing.JScrollPane meEqTableScrollPane;
    javax.swing.JTextField meSearchBar;
    javax.swing.JTextField meStockAlert;
    javax.swing.JTextField medExpAlert;
    javax.swing.JTextField medStockAlert;
    javax.swing.JTable medTable;
    javax.swing.JList<String> missingsList;
    javax.swing.JLabel mlLabel;
    javax.swing.JButton mostrarDetalleButton;
    javax.swing.JPasswordField newPassField;
    javax.swing.JLabel newPassLabel;
    javax.swing.JTable pedidoTable;
    javax.swing.JPanel pedidosPanel;
    javax.swing.JButton removeFromSTBtn;
    javax.swing.JPasswordField repeatPassField;
    javax.swing.JLabel repeatPassLabel;
    javax.swing.JButton resetMeTableBtn;
    javax.swing.JButton resetTableBtn;
    javax.swing.JScrollPane scrollPane;
    javax.swing.JTextField searchBar;
    javax.swing.JLabel searchBarLabel;
    javax.swing.JButton sendSolBtn;
    javax.swing.JLabel slLabel;
    javax.swing.JPanel solicitudePanel;
    javax.swing.JTable solicitudeTable;
    javax.swing.JLabel stockAlertLbl;
    javax.swing.JTextField toTF;
    javax.swing.JLabel toTFLabel;
    // End of variables declaration//GEN-END:variables
    TableRowSorter<TableModel> rowSorter;
    TableRowSorter<TableModel> meRowSorter;
    LoginPanel logP;
}


