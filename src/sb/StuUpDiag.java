//filename: StuUpDiag.java
package sb;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.sql.*;

public class StuUpDiag extends JDialog implements ActionListener {
    // 定义组件
    JLabel jl1, jl2, jl3, jl4, jl5, jl6;
    JTextField jf1, jf2, jf3, jf4, jf5, jf6;
    JButton jb1, jb2;
    JPanel jp1, jp2, jp3;

    // 添加新成员变量
    private StuModel stuModel; // 用于存储传入的 StuModel 实例
    private int selectedRow;   // 用于存储传入的选中行号

    // 构造函数定义，与 Main 类中的调用匹配
    public StuUpDiag(Main owner, String title, boolean modal, StuModel model, int row) {
        // 调用父类 JDialog 的构造函数，设置所有者、标题和模态选项
        super(owner, title, modal);
        
        // 初始化传入的参数
        this.stuModel = model;
        this.selectedRow = row;

        // 初始化面板和标签
        jp1 = new JPanel();
        jp2 = new JPanel();
        jp3 = new JPanel();

        jl1 = new JLabel("学号");
        jl2 = new JLabel("名字");
        jl3 = new JLabel("性别");
        jl4 = new JLabel("年龄");
        jl5 = new JLabel("籍贯");
        jl6 = new JLabel("院系");

        // 初始化文本输入框
        jf1 = new JTextField(10);
        jf2 = new JTextField(10);
        jf3 = new JTextField(10);
        jf4 = new JTextField(10);
        jf5 = new JTextField(10);
        jf6 = new JTextField(10);

        // 初始化按钮
        jb1 = new JButton("修改");
        jb2 = new JButton("取消");

        // 初始化文本字段
        initializeFields();

        // 设置布局管理器
        jp1.setLayout(new GridLayout(6, 1));
        jp2.setLayout(new GridLayout(6, 1));

        // 添加组件到面板
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

        // 添加按钮到第三个面板
        jp3.add(jb1);
        jp3.add(jb2);

        // 添加事件监听器
        jb1.addActionListener(this);
        jb2.addActionListener(this);

        // 设置主窗口布局
        this.add(jp1, BorderLayout.WEST);
        this.add(jp2, BorderLayout.CENTER);
        this.add(jp3, BorderLayout.SOUTH);

        // 设置窗口大小和可见性
        this.setSize(300, 200);
        this.setVisible(true);
    }

    // 初始化文本字段
    private void initializeFields() {
        // 从 StuModel 中获取数据，并填充到文本字段
        jf1.setText(stuModel.getValueAt(selectedRow, 0).toString()); // 学号
        jf2.setText(stuModel.getValueAt(selectedRow, 1).toString()); // 名字
        jf3.setText(stuModel.getValueAt(selectedRow, 2).toString()); // 性别
        jf4.setText(stuModel.getValueAt(selectedRow, 3).toString()); // 年龄
        jf5.setText(stuModel.getValueAt(selectedRow, 4).toString()); // 籍贯
        jf6.setText(stuModel.getValueAt(selectedRow, 5).toString()); // 院系
    }

    // 实现 ActionListener 接口的方法
    @Override
    public void actionPerformed(ActionEvent e) {
        // 如果点击了修改按钮
        if (e.getSource() == jb1) {
            Connection ct = null;
            PreparedStatement pstmt = null;

            // 获取用户输入的数据
            String stuID = jf1.getText().trim();
            String stuName = jf2.getText().trim();
            String stuSex = jf3.getText().trim();
            String stuAgeStr = jf4.getText().trim();
            String stuJG = jf5.getText().trim();
            String stuDept = jf6.getText().trim();

            // 输入验证
            if (stuID.isEmpty() || stuName.isEmpty() || stuSex.isEmpty() || stuAgeStr.isEmpty() || stuJG.isEmpty() || stuDept.isEmpty()) {
                JOptionPane.showMessageDialog(this, "所有字段均不能为空！");
                return;
            }

            // 性别验证
            if (!stuSex.equals("男") && !stuSex.equals("女")) {
                JOptionPane.showMessageDialog(this, "性别只能为'男'或'女'！");
                return;
            }

            // 年龄验证
            int stuAge = 0;
            try {
                stuAge = Integer.parseInt(stuAgeStr);
                if (stuAge < 0 || stuAge > 150) {
                    JOptionPane.showMessageDialog(this, "年龄必须在0到150之间！");
                    return;
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "年龄必须是有效的整数！");
                return;
            }

            try {
                // 加载驱动
                Class.forName("com.mysql.cj.jdbc.Driver");
                System.out.println("加载成功");

                // 连接数据库
                String url = "jdbc:mysql://localhost:3306/stuinfo?useUnicode=true&characterEncoding=utf8&nullCatalogMeansCurrent=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
                String user = "root";
                String passwd = "123456";
                ct = DriverManager.getConnection(url, user, passwd);

                // 创建 SQL 更新语句
                String strsql = "UPDATE stu SET stuName = ?, stuSex = ?, stuAge = ?, stuJG = ?, stuDept = ? WHERE stuID = ?";
                pstmt = ct.prepareStatement(strsql);

                // 设置参数
                pstmt.setString(1, stuName); // 设置名字
                pstmt.setString(2, stuSex); // 设置性别
                pstmt.setInt(3, stuAge); // 设置年龄
                pstmt.setString(4, stuJG); // 设置籍贯
                pstmt.setString(5, stuDept); // 设置院系
                pstmt.setString(6, stuID); // 设置学号

                // 执行更新
                int updateCount = pstmt.executeUpdate();

                if (updateCount > 0) {
                    JOptionPane.showMessageDialog(this, "更新成功！");
                } else {
                    JOptionPane.showMessageDialog(this, "更新失败：学号未找到！");
                }

                // 更新表格中的数据
                stuModel.setValueAt(stuName, selectedRow, 1);
                stuModel.setValueAt(stuSex, selectedRow, 2);
                stuModel.setValueAt(stuAge, selectedRow, 3);
                stuModel.setValueAt(stuJG, selectedRow, 4);
                stuModel.setValueAt(stuDept, selectedRow, 5);

                // 关闭学生对话框
                this.dispose();

            } catch (Exception arg1) {
                arg1.printStackTrace();
            } finally {
                try {
                    if (pstmt != null) {
                        pstmt.close();
                        pstmt = null;
                    }
                    if (ct != null) {
                        ct.close();
                        ct = null;
                    }
                } catch (Exception arg2) {
                    arg2.printStackTrace();
                }
            }
        } else if (e.getSource() == jb2) { 
            // 如果点击了取消按钮，关闭对话框
            this.dispose();
        }
    }
}
//package SchoolProject.src;

/*
 * 修改学生
 */
