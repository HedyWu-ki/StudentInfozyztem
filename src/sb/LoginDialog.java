package sb;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class LoginDialog extends JDialog implements ActionListener {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton cancelButton;
    private boolean authenticated = false; // 记录用户是否通过验证

    public LoginDialog(Frame owner, String title, boolean modal) {
        super(owner, title, modal);

        // 创建组件
        JLabel usernameLabel = new JLabel("用户名:");
        JLabel passwordLabel = new JLabel("密码:");

        usernameField = new JTextField(15);
        passwordField = new JPasswordField(15);

        loginButton = new JButton("登录");
        cancelButton = new JButton("取消");

        loginButton.addActionListener(this);
        cancelButton.addActionListener(this);

        // 设置布局
        setLayout(new GridLayout(3, 2));
        add(usernameLabel);
        add(usernameField);
        add(passwordLabel);
        add(passwordField);
        add(loginButton);
        add(cancelButton);

        // 设置对话框
        setSize(300, 150);
        setLocationRelativeTo(owner); // 窗口居中显示
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginButton) {
            // 执行身份验证
            String username = usernameField.getText().trim();
            String password = new String(passwordField.getPassword()).trim();

            if (validateUser(username, password)) {
                authenticated = true;
                JOptionPane.showMessageDialog(this, "登录成功！");
                dispose(); // 关闭对话框
            } else {
                JOptionPane.showMessageDialog(this, "用户名或密码错误，请重试！", "登录失败", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == cancelButton) {
            dispose(); // 关闭对话框
        }
    }

    private boolean validateUser(String username, String password) {
        // 在数据库中检查用户名和密码是否匹配
        Connection ct = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;
        boolean isValid = false;

        try {
            // 加载驱动
            Class.forName("com.mysql.cj.jdbc.Driver");

            // 连接数据库 (注意这里是 users 数据库)
            String url = "jdbc:mysql://localhost:3306/users?useUnicode=true&characterEncoding=utf8&nullCatalogMeansCurrent=true&useSSL=false&useLegacyDatetimeCode=false&serverTimezone=UTC";
            String user = "root";
            String passwd = "123456";
            ct = DriverManager.getConnection(url, user, passwd);

            // 查询语句
            String query = "SELECT * FROM admin WHERE username = ? AND password = ?";
            pstmt = ct.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            // 执行查询
            rs = pstmt.executeQuery();
            if (rs.next()) {
                isValid = true; // 用户名和密码匹配
            }

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "数据库连接错误：" + e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
        } finally {
            // 关闭资源
            try {
                if (rs != null) rs.close();
                if (pstmt != null) pstmt.close();
                if (ct != null) ct.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return isValid;
    }

    public boolean isAuthenticated() {
        return authenticated;
    }
}
