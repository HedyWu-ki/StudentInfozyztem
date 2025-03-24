// filename : Test3.java 这个是主程序
package sb;

import javax.swing.*;
import java.awt.event.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class Main extends JFrame implements ActionListener {
    // 定义一些控件
    JPanel jp1, jp2;
    JLabel jl1;
    JButton jb1, jb2, jb3, jb4, jb5; // 添加“清除查询”按钮 jb5
    JTable jt;
    JScrollPane jsp;
    JTextField jtf;
    StuModel sm;
    // 定义连接数据库的变量
    Statement stat = null;
    PreparedStatement ps;
    Connection ct = null;
    ResultSet rs = null;

    public static void main(String[] args) {
        Main main = new Main();
    }

    // 构造函数
    public Main() {
        jp1 = new JPanel();
        jtf = new JTextField(10);
        jb1 = new JButton("查询");
        jb1.addActionListener(this);
        jl1 = new JLabel("请输入名字：");

        jp1.add(jl1);
        jp1.add(jtf);
        jp1.add(jb1);

        jb2 = new JButton("添加");
        jb2.addActionListener(this);
        jb3 = new JButton("修改");
        jb3.addActionListener(this);
        jb4 = new JButton("删除");
        jb4.addActionListener(this);

        jb5 = new JButton("清除查询"); // 创建“清除查询”按钮
        jb5.addActionListener(this);

        jp2 = new JPanel();
        jp2.add(jb2);
        jp2.add(jb3);
        jp2.add(jb4);
        jp1.add(jb5); // 添加清除查询按钮到面板jp1(上部面板)

        // 创建模型对象
        sm = new StuModel();

        // 初始化
        jt = new JTable(sm);

        // 添加鼠标点击事件监听器
        jt.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // 监听双击事件
                if (e.getClickCount() == 2) {
                    int row = jt.getSelectedRow();
                    if (row != -1) {
                        // 获取选中行的学号
                        String stuID = (String) sm.getValueAt(row, 0);
                        showStudentScores(stuID);
                    }
                }
            }
        });

        jsp = new JScrollPane(jt);

        // 将jsp放入到jframe中
        this.add(jsp);
        this.add(jp1, "North");
        this.add(jp2, "South");
        this.setSize(600, 400);
        // this.setLocation(300, 200);
        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setVisible(true);
    }

    // 用于显示学生成绩的方法
    private void showStudentScores(String stuID) {
        Connection ct = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/stuinfo?useUnicode=true&characterEncoding=utf8&nullCatalogMeansCurrent=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String user = "root";
            String passwd = "123456";
            ct = DriverManager.getConnection(url, user, passwd);

            String sql = "SELECT * FROM stuScore WHERE stuID = ?";
            ps = ct.prepareStatement(sql);
            ps.setString(1, stuID);
            rs = ps.executeQuery();

            if (rs.next()) {
                double stuSc = rs.getDouble("stuSc");
                double stuScno = rs.getDouble("stuScno");
                String stuNt = rs.getString("stuNt");

                String message = String.format("学号: %s\n已通过学分: %.1f\n未通过学分: %.1f\n备注: %s", stuID, stuSc, stuScno, stuNt);
                JOptionPane.showMessageDialog(this, message, "学生成绩信息", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "未找到该学生的成绩信息", "提示", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) rs.close();
                if (ps != null) ps.close();
                if (ct != null) ct.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent arg0) {
        // 判断是哪个按钮被点击
        if (arg0.getSource() == jb1) {
            System.out.println("用户希望查询");
            // 因为把对表的数据封装到StuModel中，可以比较简单的完成查询
            String name = this.jtf.getText().trim();
            if (name.isEmpty()) { // 如果输入框为空，提示用户
                JOptionPane.showMessageDialog(this, "请输入名字以进行查询！");
                return;
            }
            // 写一个sql语句
            String sql = "select * from stu where stuName = '" + name + "' ";
            // 构建一个数据模型类，并更新
            sm = new StuModel(sql);
            // 更新jtable
            jt.setModel(sm);
        } else if (arg0.getSource() == jb5) { // 清除查询操作
            System.out.println("用户希望清除查询并显示所有记录");
            // 清空文本框
            jtf.setText("");
            // 重新加载所有数据
            sm = new StuModel();
            jt.setModel(sm);
        } else if (arg0.getSource() == jb2) {
            // 添加前进行身份验证
            if (performAuthentication()) {
                System.out.println("添加...");
                StuAddDiag sa = new StuAddDiag(this, "添加学生", true);

                // 重新再获得新的数据模型,
                sm = new StuModel();
                jt.setModel(sm);
            }
        } else if (arg0.getSource() == jb4) {
            // 删除前进行身份验证
            if (performAuthentication()) {
                // 删除记录前进行确认
                int confirm = JOptionPane.showConfirmDialog(this, "此操作将永久删除该记录，是否继续？", "确认删除", JOptionPane.YES_NO_OPTION);
                if (confirm == JOptionPane.YES_OPTION) {
                    // 执行删除操作
                    int rowNum = this.jt.getSelectedRow();
                    if (rowNum == -1) {
                        JOptionPane.showMessageDialog(this, "请选中一行");
                        return;
                    }
                    // 获取学生ID
                    String stuId = (String) sm.getValueAt(rowNum, 0);
                    System.out.println("Id： " + stuId);

                    // 删除学生记录和相应的成绩信息
                    deleteStudentRecord(stuId);

                    // 更新数据模型
                    sm = new StuModel();
                    jt.setModel(sm);
                }
            }
        } else if (arg0.getSource() == jb3) {
            // 修改前进行身份验证
            if (performAuthentication()) {
                System.out.println("用户希望修改");
                int rowNum = this.jt.getSelectedRow();
                if (rowNum == -1) {
                    JOptionPane.showMessageDialog(this, "请选择一行");
                    return;
                }
                // 显示修改对话框
                System.out.println("用户已经选中待修改行");
                StuUpDiag su = new StuUpDiag(this, "学生信息修改", true, sm, rowNum);

                // 更新数据模型
                sm = new StuModel();
                jt.setModel(sm);
            }
        }
    }

    // 执行身份验证的方法
    private boolean performAuthentication() {
        // 创建身份验证对话框
        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        Object[] message = {
            "用户名:", usernameField,
            "密码:", passwordField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "身份验证", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String username = usernameField.getText();
            String password = new String(passwordField.getPassword());

            // 检查数据库中的用户名和密码
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                String url = "jdbc:mysql://localhost:3306/users?useUnicode=true&characterEncoding=utf8&nullCatalogMeansCurrent=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
                String user = "root";
                String passwd = "123456";

                Connection ct = DriverManager.getConnection(url, user, passwd);
                PreparedStatement ps = ct.prepareStatement("SELECT * FROM admin WHERE username = ? AND password = ?");
                ps.setString(1, username);
                ps.setString(2, password);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                    return true;
                } else {
                    JOptionPane.showMessageDialog(this, "用户名或密码错误", "身份验证失败", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } catch (Exception e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "数据库连接错误", "错误", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        }
        return false;
    }

    // 删除学生记录和成绩信息的方法
    private void deleteStudentRecord(String stuId) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://localhost:3306/stuinfo?useUnicode=true&characterEncoding=utf8&nullCatalogMeansCurrent=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String user = "root";
            String passwd = "123456";

            Connection ct = DriverManager.getConnection(url, user, passwd);

            // 删除stu表中的记录
            PreparedStatement ps1 = ct.prepareStatement("DELETE FROM stu WHERE stuId = ?");
            ps1.setString(1, stuId);
            ps1.executeUpdate();

            // 删除stuScore表中的记录
            PreparedStatement ps2 = ct.prepareStatement("DELETE FROM stuScore WHERE stuId = ?");
            ps2.setString(1, stuId);
            ps2.executeUpdate();

            ps1.close();
            ps2.close();
            ct.close();

            JOptionPane.showMessageDialog(this, "记录已删除", "成功", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "删除记录时出错", "错误", JOptionPane.ERROR_MESSAGE);
        }
    }
}