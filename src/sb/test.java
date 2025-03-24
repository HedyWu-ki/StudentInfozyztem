package sb;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class test {
    // MySQL 连接信息
    private static final String URL = "jdbc:mysql://localhost:3306/stuinfo?useUnicode=true&characterEncoding=utf8&useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "123456";

    public static void main(String[] args) {
        // 创建 Connection 连接对象
        try (Connection connection = DriverManager.getConnection(URL, USER, PASSWORD)) {
            // 加载 MySQL JDBC 驱动程序
            Class.forName("com.mysql.cj.jdbc.Driver");

            System.out.println("数据库连接成功!");

            // 创建 Statement 对象
            try (Statement statement = connection.createStatement()) {
                // 执行 SQL 查询
                String query = "SELECT * FROM stu"; // 查询 stu 表中的所有记录
                try (ResultSet resultSet = statement.executeQuery(query)) {
                    // 检查是否有数据
                    if (!resultSet.isBeforeFirst()) {
                        System.out.println("stu 表中没有数据.");
                    } else {
                        // 读取并输出结果
                        System.out.printf("%-10s%-10s%-10s%-10s%-30s%-20s%n", "stuId", "stuName", "stuSex", "stuAge", "stuMajor", "stuDept");
                        System.out.println("------------------------------------------------------------------------------------");

                        while (resultSet.next()) {
                            // 从 ResultSet 中读取数据
                            String stuId = resultSet.getString("stuId");
                            String stuName = resultSet.getString("stuName");
                            String stuSex = resultSet.getString("stuSex");
                            int stuAge = resultSet.getInt("stuAge");
                            String stuMajor = resultSet.getString("stuJG");
                            String stuDept = resultSet.getString("stuDept");

                            // 打印数据
                            System.out.printf("%-10s%-10s%-10s%-10d%-30s%-20s%n", 
                                              stuId, stuName, stuSex, stuAge, stuMajor, stuDept);
                        }
                    }
                }
            }

            System.out.println("数据库连接关闭成功.");

        } catch (ClassNotFoundException e) {
            System.out.println("JDBC 驱动程序加载失败: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("数据库连接或查询失败: " + e.getMessage());
        }
    }
}