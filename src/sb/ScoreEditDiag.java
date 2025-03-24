// filename : ScoreEditDiag.java
package sb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;

public class ScoreEditDiag extends JDialog {
    private JTextField tfStuID, tfStuSc, tfStuScno, tfStuNt;
    private JButton btnSave, btnCancel;

    private String stuID;

    public ScoreEditDiag(JFrame parent, String title, boolean modal, String stuID) {
        super(parent, title, modal);

        this.stuID = stuID;

        // 创建UI组件
        JPanel panel = new JPanel(new GridLayout(5, 2));
        panel.add(new JLabel("学生ID："));
        tfStuID = new JTextField(stuID);
        tfStuID.setEditable(false);
        panel.add(tfStuID);

        panel.add(new JLabel("已修学分："));
        tfStuSc = new JTextField();
        panel.add(tfStuSc);

        panel.add(new JLabel("未通过学分："));
        tfStuScno = new JTextField();
        panel.add(tfStuScno);

        panel.add(new JLabel("备注："));
        tfStuNt = new JTextField();
        panel.add(tfStuNt);

        btnSave = new JButton("保存");
        btnCancel = new JButton("取消");

        panel.add(btnSave);
        panel.add(btnCancel);

        getContentPane().add(panel, BorderLayout.CENTER);

        btnSave.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                saveScore();
            }
        });

        btnCancel.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
        });

        setSize(300, 250);
        setLocationRelativeTo(parent);
    }

    private void saveScore() {
        try {
            double stuSc = Double.parseDouble(tfStuSc.getText().trim());
            double stuScno = Double.parseDouble(tfStuScno.getText().trim());
            String stuNt = tfStuNt.getText().trim();

            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/stuinfo?useUnicode=true&characterEncoding=utf8&nullCatalogMeansCurrent=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String user = "root";
            String passwd = "123456";

            Connection ct = DriverManager.getConnection(url, user, passwd);
            PreparedStatement ps = ct.prepareStatement("REPLACE INTO stuScore (stuID, stuSc, stuScno, stuNt) VALUES (?, ?, ?, ?)");
            ps.setString(1, stuID);
            ps.setDouble(2, stuSc);
            ps.setDouble(3, stuScno);
            ps.setString(4, stuNt);

            ps.executeUpdate();
            ps.close();
            ct.close();

            JOptionPane.showMessageDialog(this, "成绩已保存", "成功", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "请输入有效的数字", "输入错误", JOptionPane.ERROR_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "保存成绩时出错", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}
