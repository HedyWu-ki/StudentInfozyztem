package sb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;

public class StuAddDiag extends JDialog implements ActionListener {
    // 定义需要的 Swing 组件
    JLabel jl1, jl2, jl3, jl4, jl5, jl6;
    JTextField jf1, jf2, jf3, jf4, jf5, jf6;
    JPanel jp1, jp2, jp3;
    JButton jb1, jb2;

    // 构造函数，参数 owner 代表父窗口，title 是窗口的名字，modal 指定是模态窗口还是非模态窗口
    public StuAddDiag(Frame owner, String title, boolean modal) {
        // 调用父类构造函数
        super(owner, title, modal);

        // 初始化标签
        jl1 = new JLabel("学号"); // jl = Java Label
        jl2 = new JLabel("名字");
        jl3 = new JLabel("性别");
        jl4 = new JLabel("年龄");
        jl5 = new JLabel("专业");
        jl6 = new JLabel("院系");

        // 初始化文本框
        jf1 = new JTextField(10);
        jf2 = new JTextField(10);
        jf3 = new JTextField(10);
        jf4 = new JTextField(10);
        jf5 = new JTextField(10);
        jf6 = new JTextField(10);

        // 初始化按钮
        jb1 = new JButton("添加"); // jb = Java Button
        jb1.addActionListener(this); // 为 "添加" 按钮注册监听器
        jb2 = new JButton("取消");
        jb2.addActionListener(this); // 为 "取消" 按钮注册监听器

        // 初始化面板
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();

        // 设置面板布局
        jp1.setLayout(new GridLayout(6, 1)); // 6 行 1 列的网格布局
        jp2.setLayout(new GridLayout(6, 1));

        // 将组件添加到面板
        jp1.add(jl1);
        jp1.add(jl2);
        jp1.add(jl3);
        jp1.add(jl4);
        jp1.add(jl5);
        jp1.add(jl6);

        jp2.add(jf1);
        jp2.add(jf2);
        jp2.add(jf3);
        jp2.add(jf4);
        jp2.add(jf5);
        jp2.add(jf6);

        // 将按钮添加到第三个面板
        jp3.add(jb1);
        jp3.add(jb2);

        // 将面板添加到对话框
        this.add(jp1, BorderLayout.WEST);
        this.add(jp2, BorderLayout.CENTER);
        this.add(jp3, BorderLayout.SOUTH);

        // 设置对话框大小和可见性
        this.setSize(300, 200);
        this.setLocationRelativeTo(owner); // 居中显示
        this.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // 判断哪个按钮触发了事件
        if (e.getSource() == jb1) { // 如果是“添加”按钮
            // 执行数据验证
            if (!validateInput()) {
                return; // 如果验证失败，停止执行
            }

            // 开始数据库操作
            Connection ct = null;
            PreparedStatement pstmt = null;

            try {
                // 加载驱动
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("驱动加载成功");

                // 连接数据库
                String url = "jdbc:mysql://localhost:3306/stuinfo?useUnicode=true&characterEncoding=utf8&nullCatalogMeansCurrent=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
                String user = "root";
                String passwd = "123456";
                ct = DriverManager.getConnection(url, user, passwd);

                // 插入语句
                String strsql = "INSERT INTO stu (stuID, stuName, stuSex, stuAge, stuJG, stuDept) VALUES (?, ?, ?, ?, ?, ?)";
                pstmt = ct.prepareStatement(strsql);

                // 设置参数
                pstmt.setString(1, jf1.getText().trim());
                pstmt.setString(2, jf2.getText().trim());
                pstmt.setString(3, jf3.getText().trim());
                pstmt.setInt(4, Integer.parseInt(jf4.getText().trim())); // 年龄转为整数
                pstmt.setString(5, jf5.getText().trim());
                pstmt.setString(6, jf6.getText().trim());

                // 执行更新
                int result = pstmt.executeUpdate();

                if (result > 0) {
                    JOptionPane.showMessageDialog(this, "添加成功！");
                } else {
                    JOptionPane.showMessageDialog(this, "添加失败，请重试！");
                }

                this.dispose(); // 关闭对话框

            } catch (SQLIntegrityConstraintViolationException ex) {
                // 捕获并自定义处理 SQLIntegrityConstraintViolationException 异常
                JOptionPane.showMessageDialog(this, "错误: 学号已存在，请使用不同的学号", "数据库错误", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException ex) {
                // 捕获其他 SQL 异常
                JOptionPane.showMessageDialog(this, "数据库连接错误: " + ex.getMessage(), "数据库错误", JOptionPane.ERROR_MESSAGE);
            } catch (Exception ex) {
                // 捕获所有其他异常
                JOptionPane.showMessageDialog(this, "发生未知错误: " + ex.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
            } finally {
                // 关闭资源
                try {
                    if (pstmt != null) {
                        pstmt.close();
                    }
                    if (ct != null) {
                        ct.close();
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        } else if (e.getSource() == jb2) { // 如果是“取消”按钮
            this.dispose(); // 关闭对话框
        }
    }

    // 输入验证方法
    private boolean validateInput() {
        // 检查是否有任何输入为空
        if (jf1.getText().trim().isEmpty() || jf2.getText().trim().isEmpty() || jf3.getText().trim().isEmpty() ||
            jf4.getText().trim().isEmpty() || jf5.getText().trim().isEmpty() || jf6.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "所有字段均不能为空！");
            return false;
        }

        // 检查学号是否为13位的阿拉伯数字
        String stuID = jf1.getText().trim();
        if (!stuID.matches("\\d{13}")) {
            JOptionPane.showMessageDialog(this, "学号必须是13位的数字！");
            return false;
        }

        // 检查性别是否合法
        String stuSex = jf3.getText().trim();
        if (!stuSex.equals("男") && !stuSex.equals("女")) {
            JOptionPane.showMessageDialog(this, "性别只能为'男'或'女'！");
            return false;
        }

        // 检查年龄是否合法
        try {
            int stuAge = Integer.parseInt(jf4.getText().trim());
            if (stuAge < 0 || stuAge > 150) {
                JOptionPane.showMessageDialog(this, "年龄必须在0到150之间！");
                return false;
            }
        } catch (NumberFormatException nfe) {
            JOptionPane.showMessageDialog(this, "年龄必须是有效的整数！");
            return false;
        }

        return true; // 输入合法
    }
}