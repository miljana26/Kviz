package chatroomclient;

import java.awt.ComponentOrientation;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ContestantWindow extends javax.swing.JFrame {

    public ArrayList<User> users = new ArrayList<>();
    public ArrayList<FriendHelp> friendHelps = new ArrayList<>();
    public ArrayList<FriendHelp> friendsInNeedOfHelp = new ArrayList<>();

    public ContestantWindow() {
        initComponents();
        setLocationRelativeTo(null);
        jMenuBar1.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        jTable1.getTableHeader().setReorderingAllowed(false);
        jTable2.getTableHeader().setReorderingAllowed(false);
        jTable3.getTableHeader().setReorderingAllowed(false);
        fetchAndDisplayUsers();
        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                if (jTabbedPane1.getSelectedIndex() == 0) {
                    fetchAndDisplayUsers();
                }
            }
        });
        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                if (jTabbedPane1.getSelectedIndex() == 1) {
                    fetchQuestion();
                    fetchAndDisplayUsers();
                }
            }
        });
        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                if (jTabbedPane1.getSelectedIndex() == 2) {
                    fetchGetHelpFromFriends();
                }
            }

        });
        jTabbedPane1.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent e) {

                if (jTabbedPane1.getSelectedIndex() == 3) {
                    fetchFriendsInNeedOfHelp();
                }
            }

        });

    }

    private void fetchFriendsInNeedOfHelp() {
        friendsInNeedOfHelp.clear();
        String request = "FriendsInNeedOfHelp\n" + ClientMain.currentUser;
        String response = ClientMain.SendRequestAndRecieveResponse(request);
        String[] lines = response.split("\n");
        for (String line : lines) {
            String[] parts = line.split("__");
            if (parts.length == 4) {
                String WhoAskedForHelp = parts[0];
                String AnswerGiver = parts[1];
                String Answer = parts[2];
                String Question = parts[3];
                FriendHelp friendHelp = new FriendHelp(WhoAskedForHelp, AnswerGiver, Answer, Question);
                friendsInNeedOfHelp.add(friendHelp);

            }
        }
        updatefriendsInNeedOfHelpTable();
    }

    private void updatefriendsInNeedOfHelpTable() {
        Object[][] data = new Object[friendsInNeedOfHelp.size()][2];
        for (int i = 0; i < friendsInNeedOfHelp.size(); i++) {
            FriendHelp friendHelp = friendsInNeedOfHelp.get(i);
            data[i][0] = friendHelp.getWhoAskedForHelp();
            data[i][1] = friendHelp.getQuestion();
        }
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
                data, new String[]{"Who asked", "Question"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        }
        );
    }

    private void fetchGetHelpFromFriends() {
        friendHelps.clear();
        String request = "GetHelpFromFriends\n" + ClientMain.currentUser;
        String response = ClientMain.SendRequestAndRecieveResponse(request);
        String[] lines = response.split("\n");
        for (String line : lines) {
            String[] parts = line.split("__");
            if (parts.length == 4) {
                String WhoAskedForHelp = parts[0];
                String AnswerGiver = parts[1];
                String Answer = parts[2];
                String Question = parts[3];
                FriendHelp friendHelp = new FriendHelp(WhoAskedForHelp, AnswerGiver, Answer, Question);
                friendHelps.add(friendHelp);

            }
        }
        updateGetHelpFromFriendsTable();
    }

    private void updateGetHelpFromFriendsTable() {
        Object[][] data = new Object[friendHelps.size()][4];
        for (int i = 0; i < friendHelps.size(); i++) {
            FriendHelp friendHelp = friendHelps.get(i);
            data[i][0] = friendHelp.getWhoAskedForHelp();
            data[i][1] = friendHelp.getAnswerGiver();
            data[i][2] = friendHelp.getAnswer();
            data[i][3] = friendHelp.getQuestion();

        }
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
                data, new String[]{"Who asked", "Answer giver", "Answer", "Question"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        }
        );

    }

    private void fetchQuestion() {
        String request = "GetCurrentQuestion\n" + ClientMain.currentUser;
        String response = ClientMain.SendRequestAndRecieveResponse(request);
        String[] parts = response.split("\n");

        jRadioButton1.setEnabled(true);
        jRadioButton2.setEnabled(true);
        jRadioButton3.setEnabled(true);
        jRadioButton4.setEnabled(true);
        jButton1.setEnabled(true);
        jButton2.setEnabled(true);
        jButton3.setEnabled(true);
        jButton4.setEnabled(true);

        if (response.equals("NO QUESTION")) {
            jLabel1.setText("NO QUESTION AVAILABLE RIGHT NOW");
            jRadioButton1.setText("EMPTY");
            jRadioButton2.setText("EMPTY");
            jRadioButton3.setText("EMPTY");
            jRadioButton4.setText("EMPTY");

            jRadioButton1.setEnabled(false);
            jRadioButton2.setEnabled(false);
            jRadioButton3.setEnabled(false);
            jRadioButton4.setEnabled(false);
            jButton1.setEnabled(false);
            jButton2.setEnabled(false);
            jButton3.setEnabled(false);
            jButton4.setEnabled(false);
            return;
        }
        String question = parts[0];
        List<String> answers = Arrays.asList(parts).subList(1, 5);
        Collections.shuffle(answers);
        jLabel1.setText(new String(question.getBytes(), StandardCharsets.UTF_8));
        jRadioButton1.setText(new String(answers.get(0).getBytes(), StandardCharsets.UTF_8));
        jRadioButton2.setText(new String(answers.get(1).getBytes(), StandardCharsets.UTF_8));
        jRadioButton3.setText(new String(answers.get(2).getBytes(), StandardCharsets.UTF_8));
        jRadioButton4.setText(new String(answers.get(3).getBytes(), StandardCharsets.UTF_8));

        ArrayList<User> usersCopy = new ArrayList<>(users);

        usersCopy.removeIf(user -> user.getUsername().equals(ClientMain.currentUser));
        jButton3.setEnabled(true);
        jComboBox1.setVisible(true);
        jComboBox1.removeAllItems();
        if (usersCopy.isEmpty()) {
            jButton3.setEnabled(false);
            jComboBox1.setVisible(false);
        } else {
            jButton3.setEnabled(true);
            jComboBox1.setVisible(true);
            jComboBox1.removeAllItems();
            for (User user : usersCopy) {
                jComboBox1.addItem(user.getUsername());
            }
        }

    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton2 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jRadioButton4 = new javax.swing.JRadioButton();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jButton4 = new javax.swing.JButton();
        jComboBox1 = new javax.swing.JComboBox<>();
        jPanel3 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable2 = new javax.swing.JTable();
        jPanel4 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        jButton5 = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable3 = new javax.swing.JTable();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("ContestantWindow");

        jTabbedPane1.setFont(new java.awt.Font("Malgun Gothic", 1, 12)); // NOI18N

        jTable1.setFont(new java.awt.Font("Malgun Gothic", 0, 12)); // NOI18N
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1147, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 373, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(24, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Rang List", jPanel1);

        jLabel1.setFont(new java.awt.Font("Arial", 1, 24)); // NOI18N
        jLabel1.setText("Question: ");

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jRadioButton1.setSelected(true);
        jRadioButton1.setText("jRadioButton1");
        jRadioButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jRadioButton1ActionPerformed(evt);
            }
        });

        buttonGroup1.add(jRadioButton2);
        jRadioButton2.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jRadioButton2.setText("jRadioButton2");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jRadioButton3.setText("jRadioButton3");

        buttonGroup1.add(jRadioButton4);
        jRadioButton4.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jRadioButton4.setText("jRadioButton4");

        jButton1.setText("Answer");
        jButton1.setToolTipText("");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jButton2.setText("Half/Half");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jButton3.setText("Ask friend");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jButton4.setText("Change question");
        jButton4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton4ActionPerformed(evt);
            }
        });

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(191, 191, 191)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                            .addComponent(jRadioButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 289, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 142, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jRadioButton4, javax.swing.GroupLayout.PREFERRED_SIZE, 119, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(200, 200, 200)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jButton4)
                            .addGroup(jPanel2Layout.createSequentialGroup()
                                .addGap(16, 16, 16)
                                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jButton2)
                                    .addGroup(jPanel2Layout.createSequentialGroup()
                                        .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                        .addGap(37, 37, 37)
                                        .addComponent(jComboBox1, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(387, 387, 387)
                        .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 81, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(47, 47, 47)
                        .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 985, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(127, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jRadioButton1, jRadioButton2, jRadioButton3, jRadioButton4});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton1, jButton2, jButton3});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {jButton4, jComboBox1});

        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(42, 42, 42)
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(27, 27, 27)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jRadioButton1)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton2)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton3)
                        .addGap(18, 18, 18)
                        .addComponent(jRadioButton4))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jButton4)
                        .addGap(28, 28, 28)
                        .addComponent(jButton2))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(129, 129, 129)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jButton3)
                            .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(82, Short.MAX_VALUE))
        );

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jRadioButton1, jRadioButton2, jRadioButton3, jRadioButton4});

        jPanel2Layout.linkSize(javax.swing.SwingConstants.VERTICAL, new java.awt.Component[] {jButton1, jButton2, jButton3, jButton4, jComboBox1});

        jTabbedPane1.addTab("Answer Questions", jPanel2);

        jTable2.setFont(new java.awt.Font("Malgun Gothic", 0, 12)); // NOI18N
        jTable2.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane2.setViewportView(jTable2);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 1147, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 393, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Help from other contestnats", jPanel3);

        jTextField1.setFont(new java.awt.Font("Malgun Gothic", 0, 12)); // NOI18N
        jTextField1.setToolTipText("HelpFriend");
        jTextField1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField1ActionPerformed(evt);
            }
        });

        jButton5.setFont(new java.awt.Font("Malgun Gothic", 0, 12)); // NOI18N
        jButton5.setText("SEND");
        jButton5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton5ActionPerformed(evt);
            }
        });

        jTable3.setFont(new java.awt.Font("Malgun Gothic", 0, 12)); // NOI18N
        jTable3.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane3.setViewportView(jTable3);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 662, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jButton5, javax.swing.GroupLayout.PREFERRED_SIZE, 106, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(311, Short.MAX_VALUE))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 339, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton5, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(57, 57, 57))
        );

        jTabbedPane1.addTab("Help other contestants", jPanel4);

        jMenuBar1.setFont(new java.awt.Font("Malgun Gothic", 0, 12)); // NOI18N

        jMenu1.setText("Logout");
        jMenu1.addMenuListener(new javax.swing.event.MenuListener() {
            public void menuCanceled(javax.swing.event.MenuEvent evt) {
            }
            public void menuDeselected(javax.swing.event.MenuEvent evt) {
            }
            public void menuSelected(javax.swing.event.MenuEvent evt) {
                jMenu1MenuSelected(evt);
            }
        });
        jMenuBar1.add(jMenu1);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 444, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenu1MenuSelected(javax.swing.event.MenuEvent evt) {//GEN-FIRST:event_jMenu1MenuSelected
        dispose();
        new LoginWindow().setVisible(true);
    }//GEN-LAST:event_jMenu1MenuSelected

    private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
    }//GEN-LAST:event_jTextField1ActionPerformed

    private void jButton5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton5ActionPerformed
        int selectedRowIndex = jTable3.getSelectedRow();

        if (selectedRowIndex != -1) {
            String WhoAsked = (String) jTable3.getValueAt(selectedRowIndex, 0);
            String Question = (String) jTable3.getValueAt(selectedRowIndex, 1);
            String Answer = jTextField1.getText().trim();
            if (Answer.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Not answered!", "Not answered!", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String request = "HelpFriend\n" + ClientMain.currentUser + "\n" + WhoAsked + "\n" + Question + "\n" + Answer;

            String response = ClientMain.SendRequestAndRecieveResponse(request);
            if (response.equals("SUCCESS")) {
                JOptionPane.showMessageDialog(this, "ANSWERED", "ANSWERED", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, response, response, JOptionPane.ERROR_MESSAGE);
            }
            jTextField1.setText("");
            fetchFriendsInNeedOfHelp();
        } else {
            JOptionPane.showMessageDialog(null, "No selected question!", "No selected question!", JOptionPane.ERROR_MESSAGE);
        }

    }//GEN-LAST:event_jButton5ActionPerformed

    private void jButton4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton4ActionPerformed
        String request = "SwitchQuestion\n" + ClientMain.currentUser + "\n";
        String response = ClientMain.SendRequestAndRecieveResponse(request);
        if (response.equals("CAN NOT USE THIS HELP AT THIS MOMENT!")) {
            JOptionPane.showMessageDialog(this, response, response, JOptionPane.ERROR_MESSAGE);
        } else {
            String[] parts = response.split("\n");

            jRadioButton1.setEnabled(true);
            jRadioButton2.setEnabled(true);
            jRadioButton3.setEnabled(true);
            jRadioButton4.setEnabled(true);
            jButton1.setEnabled(true);
            jButton2.setEnabled(true);
            jButton3.setEnabled(true);
            jButton4.setEnabled(true);

            if (response.equals("NO QUESTION")) {
                jLabel1.setText("NO QUESTION AVAILABLE RIGHT NOW");
                jRadioButton1.setText("EMPTY");
                jRadioButton2.setText("EMPTY");
                jRadioButton3.setText("EMPTY");
                jRadioButton4.setText("EMPTY");

                jRadioButton1.setEnabled(false);
                jRadioButton2.setEnabled(false);
                jRadioButton3.setEnabled(false);
                jRadioButton4.setEnabled(false);
                jButton1.setEnabled(false);
                jButton2.setEnabled(false);
                jButton3.setEnabled(false);
                jButton4.setEnabled(false);

                return;
            }
            String question = parts[0];
            List<String> answers = Arrays.asList(parts).subList(1, 5);
            Collections.shuffle(answers);
            jLabel1.setText(new String(question.getBytes(), StandardCharsets.UTF_8));
            jRadioButton1.setText(new String(answers.get(0).getBytes(), StandardCharsets.UTF_8));
            jRadioButton2.setText(new String(answers.get(1).getBytes(), StandardCharsets.UTF_8));
            jRadioButton3.setText(new String(answers.get(2).getBytes(), StandardCharsets.UTF_8));
            jRadioButton4.setText(new String(answers.get(3).getBytes(), StandardCharsets.UTF_8));

        }
    }//GEN-LAST:event_jButton4ActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        String request = "AskForHelpFromFriend\n" + ClientMain.currentUser + ":" + (String) jComboBox1.getSelectedItem();
        String response = ClientMain.SendRequestAndRecieveResponse(request);
        if (!response.equals("SUCCESS")) {
            JOptionPane.showMessageDialog(this, response, response, JOptionPane.ERROR_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this, "Asked a friend", "Asked a friend", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        String request = "HalfHalfQuestion\n" + ClientMain.currentUser + "\n";
        String response = ClientMain.SendRequestAndRecieveResponse(request);
        if (response.equals("CAN NOT USE THIS HELP AT THIS MOMENT!")) {
            JOptionPane.showMessageDialog(this, response, response, JOptionPane.ERROR_MESSAGE);
        } else {
            String[] wrongAnswers = response.split("\n");
            if (!jRadioButton1.getText().equals(wrongAnswers[0]) && !jRadioButton1.getText().equals(wrongAnswers[1])) {
                jRadioButton1.setSelected(true);
            } else if (!jRadioButton2.getText().equals(wrongAnswers[0]) && !jRadioButton2.getText().equals(wrongAnswers[1])) {
                jRadioButton2.setSelected(true);
            } else if (!jRadioButton3.getText().equals(wrongAnswers[0]) && !jRadioButton3.getText().equals(wrongAnswers[1])) {
                jRadioButton3.setSelected(true);
            } else if (!jRadioButton4.getText().equals(wrongAnswers[0]) && !jRadioButton4.getText().equals(wrongAnswers[1])) {
                jRadioButton4.setSelected(true);
            }

            if (jRadioButton1.getText().equals(wrongAnswers[0]) || jRadioButton1.getText().equals(wrongAnswers[1])) {
                jRadioButton1.setEnabled(false);
            }
            if (jRadioButton2.getText().equals(wrongAnswers[0]) || jRadioButton2.getText().equals(wrongAnswers[1])) {
                jRadioButton2.setEnabled(false);
            }
            if (jRadioButton3.getText().equals(wrongAnswers[0]) || jRadioButton3.getText().equals(wrongAnswers[1])) {
                jRadioButton3.setEnabled(false);
            }
            if (jRadioButton4.getText().equals(wrongAnswers[0]) || jRadioButton4.getText().equals(wrongAnswers[1])) {
                jRadioButton4.setEnabled(false);
            }
        }
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        String selectedAnswer = "";
        if (jRadioButton1.isSelected()) {
            selectedAnswer = jRadioButton1.getText();
        } else if (jRadioButton2.isSelected()) {
            selectedAnswer = jRadioButton2.getText();
        } else if (jRadioButton3.isSelected()) {
            selectedAnswer = jRadioButton3.getText();
        } else if (jRadioButton4.isSelected()) {
            selectedAnswer = jRadioButton4.getText();
        }

        String request = "AnswerCurretnQuestion\n" + ClientMain.currentUser + "\n" + jLabel1.getText() + "\n" + selectedAnswer + "\n";
        String response = ClientMain.SendRequestAndRecieveResponse(request);
        if (response.equals("CORRECT")) {
            JOptionPane.showMessageDialog(this, "Selected answer is correct!", "Correct answer!", JOptionPane.INFORMATION_MESSAGE);
        } else {

            JOptionPane.showMessageDialog(this, response, "Incorrect answer", JOptionPane.ERROR_MESSAGE);
        }
        fetchQuestion();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jRadioButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jRadioButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jRadioButton1ActionPerformed

    private void fetchAndDisplayUsers() {
        users.clear();
        String request = "GetTableResults\n" + ClientMain.currentUser + "\n";
        String response = ClientMain.SendRequestAndRecieveResponse(request);
        String[] lines = response.split("\n");
        for (String line : lines) {
            String[] parts = line.split(":");
            if (parts.length == 6) {
                String username = parts[0];
                String userType = parts[1];
                User user = new User(username, userType, Integer.parseInt(parts[2]), Integer.parseInt(parts[3]), Integer.parseInt(parts[4]), Integer.parseInt(parts[5]));
                users.add(user);
            }
        }

        updateTable();
    }

    private void updateTable() {
        Object[][] data = new Object[users.size()][5];
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            data[i][0] = user.getUsername();
            data[i][1] = user.getCorrectAnsweredQuestions();
            data[i][2] = user.getTotalAnsweredQuestions();
            data[i][3] = user.getCurrentQuestionInSet();
            data[i][4] = user.getCurrentSet();
        }
        jTable1.setModel(new javax.swing.table.DefaultTableModel(
                data, new String[]{"Name", "Correct Answers", "Total Anwsers", "Current Question In Set", "Current Set"}
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        }
        );
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JButton jButton4;
    private javax.swing.JButton jButton5;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton2;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JRadioButton jRadioButton4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable2;
    private javax.swing.JTable jTable3;
    private javax.swing.JTextField jTextField1;
    // End of variables declaration//GEN-END:variables

}
