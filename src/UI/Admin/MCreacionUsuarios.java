package UI.Admin;

import Logica.AES;
import Logica.Memoria;
import Logica.Run;
import Objetos.Usuario;
import java.awt.Color;
import java.util.LinkedList;
import javax.swing.DefaultListModel;

public class MCreacionUsuarios extends javax.swing.JFrame {

    //Lista que contiene a todos los usuarios.
    private LinkedList<Usuario> lista_usuarios = new LinkedList<Usuario>();
    //Modelo para la jlist
    private DefaultListModel<Usuario> jlist_model = new DefaultListModel<>();
    //Si se selecciona un usuario para editar
    private Usuario usuario_seleccionado_para_editar = null;

    public MCreacionUsuarios() {
        initComponents();
        settings();
        cargar_usuarios();
    }

    private void settings() {
        //Establece el icono en la barra de estado y en el icono.
        setIconImage(Memoria.getIconImage());
        //Coloca el frame en el centro de la pantalla.
        this.setLocationRelativeTo(null);
        //Hace el frame visible.
        this.setVisible(true);
        //Establece texto en la barra de estado.
        this.setTitle(Memoria.app_name);
        //No dejar que el frame se pueda hacer de tamaño grande
        this.setResizable(false);
        //Que el radiobutton de habilitado siempre este seleccionado
        this.jRadioButton1_habilitado.setSelected(true);
        //Deshabilitamos el boton de guardar cambios porqué aun no se ha seleccionado
        //algun usuario para editar.
        this.jButton_actualizar_usuario_existente.setEnabled(false);
        //Setea el label de estado a vacio
        label_status_change("", "black");
    }

    //Trae los usuarios de la base de datos.
    private void cargar_usuarios() {
        this.lista_usuarios = Memoria.sql_lite_query.obtener_usuarios("SELECT * FROM USER");
        actualizar_jlist();
    }

    //Llenar lista de usuarios
    private void actualizar_jlist() {
        //Limpiamos el modelo en caso de que ya tenga algo establecido.
        this.jlist_model.clear();
        //Se rellena el jlist model con la lista de usuarios.
        for (Usuario u : this.lista_usuarios) {
            //Si no es cliente, se agrega a la lista.
            if (!u.getRol().equals("cliente")) {
                this.jlist_model.add(0, u);
            }
        }
        //Se la establecemos al jList.
        this.jList1.setModel(this.jlist_model);
    }

    ///////////////////////////////////////////////////////////////////////////////
    
    //Rellena los espacios con la informacion del usuario seleccionado de 
    //la lista.
    private void rellenar_espacios_click_en_jlist(Usuario usuario) {
        //Se usa la informacion del usuario dado para rellenar los espacios
        //y combo box del metodo.
        jTextField1_nombre_de_usuario.setText(usuario.getNombre_de_usuario());
        jTextField2_nombre.setText(usuario.getNombre());
        jTextField3_primer_apellido.setText(usuario.getAp_paterno());
        jTextField4_segundo_apellido.setText(usuario.getAp_materno());
        jPasswordField1_contrasenia.setText(AES.decrypt(usuario.getContrasenia(), Memoria.DBKeyPassword));
        jPasswordField2_repite_contrasenia.setText(AES.decrypt(usuario.getContrasenia(), Memoria.DBKeyPassword));
        switch (usuario.getRol()) {
            case "admin":
                jComboBox1_tipo_de_usuario.setSelectedIndex(1);
                break;
            case "chofer":
                jComboBox1_tipo_de_usuario.setSelectedIndex(0);
                break;
            case "masteradmin":
                jComboBox1_tipo_de_usuario.setSelectedIndex(1);
                break;
            default:
                throw new AssertionError();
        }
        if (usuario.isHabilitado()) {
            jRadioButton1_habilitado.setSelected(true);
            jRadioButton2_deshabilitado.setSelected(false);
        } else {
            jRadioButton1_habilitado.setSelected(false);
            jRadioButton2_deshabilitado.setSelected(true);
        }
    }
    
    //Actualiza el usuario seleccionado en la base de datos.
    private void actualizar_usuario() {
        //Carga la información del usuario seleccionado 
        String nombre_usuario_original = this.usuario_seleccionado_para_editar.getNombre_de_usuario();
        this.usuario_seleccionado_para_editar.setNombre_de_usuario(jTextField1_nombre_de_usuario.getText());
        this.usuario_seleccionado_para_editar.setNombre(jTextField2_nombre.getText());
        this.usuario_seleccionado_para_editar.setAp_paterno(jTextField3_primer_apellido.getText());
        this.usuario_seleccionado_para_editar.setAp_materno(jTextField4_segundo_apellido.getText());
        this.usuario_seleccionado_para_editar.setContrasenia(AES.encrypt(new String(jPasswordField1_contrasenia.getPassword()), Memoria.DBKeyPassword));
        switch (jComboBox1_tipo_de_usuario.getSelectedItem().toString()) {
            case "Chofer":
                this.usuario_seleccionado_para_editar.setRol("chofer");
                break;
            case "Administrador":
                this.usuario_seleccionado_para_editar.setRol("admin");
                break;
            default:
                throw new AssertionError();
        }
        if (jRadioButton1_habilitado.isSelected()) {
            this.usuario_seleccionado_para_editar.setHabilitado(true);
        } else {
            this.usuario_seleccionado_para_editar.setHabilitado(false);
        }
        
        //Consulta de SQL que actualiza la informacion del usuario
        Memoria.sql_lite_query.Query("UPDATE USER\n"
                + "SET Rol = '" + this.usuario_seleccionado_para_editar.getRol() + "' ,"
                + " Nombre_usuario = '" + this.usuario_seleccionado_para_editar.getNombre_de_usuario() + "' ,"
                + " Contrasenia = '" + this.usuario_seleccionado_para_editar.getContrasenia() + "',"
                + " Nombre = '" + this.usuario_seleccionado_para_editar.getNombre() + "',"
                + " Ap_paterno = '" + this.usuario_seleccionado_para_editar.getAp_paterno() + "',"
                + " Ap_materno = '" + this.usuario_seleccionado_para_editar.getAp_materno() + "',"
                + "Habilitado = '" + this.usuario_seleccionado_para_editar.isHabilitado() + "'\n"
                + "WHERE UserID = " + this.usuario_seleccionado_para_editar.getDB_ID() + ";", "Usuario actualizado");
        
        // Carga los usuarios con la informacion actualizada, muestra mensaje de exito, limpia los espacios
        //de texto, reinicia los botones.
        cargar_usuarios();
        Run.message("Usuario " + nombre_usuario_original + " ha sido actualizado.", "Actualizado", 1);
        limpiar_espacios();
        this.jButton2_guardar_usuario_o_cancelar_cambios.setText("Guardar usuario");
        this.jButton_actualizar_usuario_existente.setEnabled(false);
    }

    //Veficia que todos los espacios de texto tengan informacion escrita.
    private boolean sin_espacios_vacios() {
        if (!jTextField1_nombre_de_usuario.getText().equals("")
                && !jTextField2_nombre.getText().equals("")
                && !jTextField3_primer_apellido.getText().equals("")
                && !jTextField4_segundo_apellido.getText().equals("")
                && !new String(this.jPasswordField1_contrasenia.getPassword()).equals("")
                && !new String(this.jPasswordField2_repite_contrasenia.getPassword()).equals("")) {
            return true;
        } else {
            label_status_change("Debe rellenar todos los espacios", "red");
            return false;
        }
    }

    //Verifica que el nombre de usuario escrito no exista ya en la base de datos.
    private boolean sin_nombre_de_usuario_repetido() {
        for (Usuario u : this.lista_usuarios) {
            if (u.getNombre_de_usuario().toLowerCase().equals(this.jTextField1_nombre_de_usuario.getText().toLowerCase())) {
                label_status_change("El nombre de usuario escrito ya ha sido tomado..", "red");
                return false;
            }
        }
        return true;
    }
    
    //Verifica si las contraseña a establecer coincide dentro de los dos espacios
    //de texto.
    private boolean contrasenias_coinsiden() {
        String password_1 = new String(this.jPasswordField1_contrasenia.getPassword());
        String password_2 = new String(this.jPasswordField2_repite_contrasenia.getPassword());
        if (password_1.equals(password_2)) {
            return true;
        } else {
            label_status_change("Las contraseñas no coinciden..", "red");
            return false;
        }
    }

    //Verifica que la contraseña tenga la cantida de caracteres establecida
    //como minima.
    private boolean longitud_contrasenia(int longitud) {
        String password = new String(this.jPasswordField1_contrasenia.getPassword());
        if (password.length() >= longitud) {
            return true;
        } else {
            label_status_change("La contraseña debe ser de " + longitud + " caracteres o más..", "red");
            return false;
        }
    }

    //Verifica que el nombre de usuario escrito tenga la cantidad de caracteres
    //establecida como minima.
    private boolean longitud_nombre_de_usuario(int longitud) {
        String nombre_usuario = this.jTextField1_nombre_de_usuario.getText();
        if (nombre_usuario.length() >= longitud) {
            return true;
        } else {
            label_status_change("El nombre de usuario debe ser de al menos " + longitud + " caracteres", "red");
            return false;
        }
    }

   //Vacia todos los espacios de texto.
    private void limpiar_espacios() {
        jTextField1_nombre_de_usuario.setText("");
        jTextField2_nombre.setText("");
        jTextField3_primer_apellido.setText("");
        jTextField4_segundo_apellido.setText("");
        jPasswordField1_contrasenia.setText("");
        jPasswordField2_repite_contrasenia.setText("");
    }

    //Metodo para guardar un nuevo usuario en la base de datos.
    private void guardar_nuevo_usuario() {
        
        //Variables temporales que contienen la informacion a guardar.
        Usuario usuario = new Usuario();
        usuario.setNombre_de_usuario(jTextField1_nombre_de_usuario.getText());
        usuario.setContrasenia(AES.encrypt(new String(jPasswordField1_contrasenia.getPassword()), Memoria.DBKeyPassword));
        usuario.setNombre(jTextField2_nombre.getText());
        usuario.setAp_paterno(jTextField3_primer_apellido.getText());
        usuario.setAp_materno(jTextField4_segundo_apellido.getText());
        usuario.setHabilitado(true);
        if (jRadioButton1_habilitado.isSelected()) {
            usuario.setHabilitado(true);
        } else {
            usuario.setHabilitado(false);
        }
        if (jComboBox1_tipo_de_usuario.getSelectedItem().toString().equals("Chofer")) {
            usuario.setRol("chofer");
        } else {
            usuario.setRol("admin");
        }
        
        //Consula SQLite para guardar el usuario en la base de datos.
        Memoria.sql_lite_query.Query("INSERT INTO USER (Rol, "
                + "Nombre_usuario, "
                + "Contrasenia, "
                + "Nombre, "
                + "Ap_paterno, "
                + "Ap_materno, "
                + "Habilitado)\n"
                + "VALUES ('" + usuario.getRol() + "', "
                + "'" + usuario.getNombre_de_usuario() + "', "
                + "'" + usuario.getContrasenia() + "', "
                + "'" + usuario.getNombre() + "',"
                + "'" + usuario.getAp_paterno() + "',"
                + "'" + usuario.getAp_materno() + "',"
                + "'" + usuario.isHabilitado() + "');", "Usuario agregado");
        
        //Mensaje de exito, limpia los espacios de texto, recarga los usuarios.
        Run.message("Usuario " + usuario.getNombre_de_usuario() + " agregado!", "Agreagado", 1);
        limpiar_espacios();
        cargar_usuarios();
        label_status_change("", "red");
    }

    //cambia el color y el texto de el label de estado.
    private void label_status_change(String message, String color) {
        switch (color) {
            case "red":
                jLabel3_status.setForeground(Color.red);
                break;
            case "blue":
                jLabel3_status.setForeground(Color.blue);
                break;
            case "green":
                jLabel3_status.setForeground(Color.green);
                break;
            case "black":
                jLabel3_status.setForeground(Color.black);
                break;
            default:
                throw new AssertionError();
        }
        jLabel3_status.setText(message);
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList<>();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jTextField1_nombre_de_usuario = new javax.swing.JTextField();
        jComboBox1_tipo_de_usuario = new javax.swing.JComboBox<>();
        jPasswordField1_contrasenia = new javax.swing.JPasswordField();
        jLabel3 = new javax.swing.JLabel();
        jButton1_volver = new javax.swing.JButton();
        jPasswordField2_repite_contrasenia = new javax.swing.JPasswordField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton2_guardar_usuario_o_cancelar_cambios = new javax.swing.JButton();
        jTextField2_nombre = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        jTextField3_primer_apellido = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField4_segundo_apellido = new javax.swing.JTextField();
        jLabel10 = new javax.swing.JLabel();
        jRadioButton1_habilitado = new javax.swing.JRadioButton();
        jRadioButton2_deshabilitado = new javax.swing.JRadioButton();
        jButton_actualizar_usuario_existente = new javax.swing.JButton();
        jLabel3_status = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jList1.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jList1MouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(jList1);

        jLabel1.setText("Nombre de usuario");

        jLabel2.setText("Contraseña");

        jComboBox1_tipo_de_usuario.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Chofer", "Administrador" }));

        jLabel3.setFont(new java.awt.Font("Tahoma", 1, 18)); // NOI18N
        jLabel3.setText("Creación de usuarios");

        jButton1_volver.setText("Volver");
        jButton1_volver.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1_volverActionPerformed(evt);
            }
        });

        jLabel4.setText("Repite contraseña");

        jLabel5.setFont(new java.awt.Font("Tahoma", 1, 12)); // NOI18N
        jLabel5.setText("Usuarios actuales");

        jLabel6.setText("Tipo de usuario");

        jButton2_guardar_usuario_o_cancelar_cambios.setText("Guardar usuario");
        jButton2_guardar_usuario_o_cancelar_cambios.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2_guardar_usuario_o_cancelar_cambiosActionPerformed(evt);
            }
        });

        jLabel7.setText("Nombre");

        jLabel8.setText("Primer apellido");

        jLabel9.setText("Segundo apellido");

        jLabel10.setText("Estado del usuario");

        buttonGroup1.add(jRadioButton1_habilitado);
        jRadioButton1_habilitado.setText("Habilitado");

        buttonGroup1.add(jRadioButton2_deshabilitado);
        jRadioButton2_deshabilitado.setText("Deshabilitado");

        jButton_actualizar_usuario_existente.setText("Guardar cambios");
        jButton_actualizar_usuario_existente.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_actualizar_usuario_existenteActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel3)
                            .addComponent(jLabel2)
                            .addComponent(jLabel6)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel10)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton1_habilitado)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(jRadioButton2_deshabilitado))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel9)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField4_segundo_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel4)
                                .addGap(28, 28, 28)
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPasswordField1_contrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPasswordField2_repite_contrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jComboBox1_tipo_de_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField1_nombre_de_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel7)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField2_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel8)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(jTextField3_primer_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jButton2_guardar_usuario_o_cancelar_cambios, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel1Layout.createSequentialGroup()
                                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jButton_actualizar_usuario_existente, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 155, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(37, 37, 37))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                                .addComponent(jLabel5)
                                .addGap(128, 128, 128))))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jButton1_volver)
                        .addGap(61, 61, 61)
                        .addComponent(jLabel3_status, javax.swing.GroupLayout.PREFERRED_SIZE, 307, javax.swing.GroupLayout.PREFERRED_SIZE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jLabel5))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel1)
                            .addComponent(jTextField1_nombre_de_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel7)
                            .addComponent(jTextField2_nombre, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel8)
                            .addComponent(jTextField3_primer_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel9)
                            .addComponent(jTextField4_segundo_apellido, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jPasswordField1_contrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel4)
                            .addComponent(jPasswordField2_repite_contrasenia, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(jComboBox1_tipo_de_usuario, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(18, 18, 18)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel10)
                            .addComponent(jRadioButton1_habilitado)
                            .addComponent(jRadioButton2_deshabilitado)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 285, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2_guardar_usuario_o_cancelar_cambios)
                    .addComponent(jButton_actualizar_usuario_existente))
                .addGap(18, 18, 18)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jButton1_volver, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3_status, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 513, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton1_volverActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1_volverActionPerformed
        //Boton de volver. Abre de nuevo el frame anterior.
        MNavAdmin mNavAdmin = new MNavAdmin();
        this.dispose();
    }//GEN-LAST:event_jButton1_volverActionPerformed

    private void jList1MouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_jList1MouseClicked
        //Se ejecuta cuando se hace click en la lista
        if (!this.lista_usuarios.isEmpty()) {
            //Rellena los espacios del usuario si se selecciona uno
            this.usuario_seleccionado_para_editar = jList1.getSelectedValue();
            if (this.usuario_seleccionado_para_editar != null) {
                rellenar_espacios_click_en_jlist(this.usuario_seleccionado_para_editar);
                //el boton de guardar usuario cambia el texto a cancelar cambios por que se va a editar
                //un usuario
                jButton2_guardar_usuario_o_cancelar_cambios.setText("Cancelar cambios");
                //se habilita el boton de actualizar el usuario seleccionado 
                this.jButton_actualizar_usuario_existente.setEnabled(true);
            }
        }
    }//GEN-LAST:event_jList1MouseClicked

    private void jButton_actualizar_usuario_existenteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_actualizar_usuario_existenteActionPerformed
        if (sin_espacios_vacios()
                && contrasenias_coinsiden()
                && sin_nombre_de_usuario_repetido()
                && longitud_contrasenia(12)
                && longitud_nombre_de_usuario(5)) {
            actualizar_usuario();
        }
    }//GEN-LAST:event_jButton_actualizar_usuario_existenteActionPerformed


    private void jButton2_guardar_usuario_o_cancelar_cambiosActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2_guardar_usuario_o_cancelar_cambiosActionPerformed
        //Boton para guardar o cancelar cambios
        String opcion = this.jButton2_guardar_usuario_o_cancelar_cambios.getText();
        switch (opcion) {
            case "Guardar usuario":
                if (sin_espacios_vacios()
                        && sin_nombre_de_usuario_repetido()
                        && contrasenias_coinsiden()
                        && longitud_contrasenia(12)
                        && longitud_nombre_de_usuario(5)) {
                    guardar_nuevo_usuario();
                }
                break;
            case "Cancelar cambios":
                this.usuario_seleccionado_para_editar = null;
                limpiar_espacios();
                this.jButton2_guardar_usuario_o_cancelar_cambios.setText("Guardar usuario");
                this.jButton_actualizar_usuario_existente.setEnabled(false);
                break;
            default:
                throw new AssertionError();
        }
    }//GEN-LAST:event_jButton2_guardar_usuario_o_cancelar_cambiosActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1_volver;
    private javax.swing.JButton jButton2_guardar_usuario_o_cancelar_cambios;
    private javax.swing.JButton jButton_actualizar_usuario_existente;
    private javax.swing.JComboBox<String> jComboBox1_tipo_de_usuario;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel3_status;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JList<Usuario> jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPasswordField jPasswordField1_contrasenia;
    private javax.swing.JPasswordField jPasswordField2_repite_contrasenia;
    private javax.swing.JRadioButton jRadioButton1_habilitado;
    private javax.swing.JRadioButton jRadioButton2_deshabilitado;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextField jTextField1_nombre_de_usuario;
    private javax.swing.JTextField jTextField2_nombre;
    private javax.swing.JTextField jTextField3_primer_apellido;
    private javax.swing.JTextField jTextField4_segundo_apellido;
    // End of variables declaration//GEN-END:variables
}
