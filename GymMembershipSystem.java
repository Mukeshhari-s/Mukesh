import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

public class GymMembershipSystem {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Connection conn = null;

        try {
            conn = DatabaseHelper.getConnection();
            boolean running = true;

            while (running) {
                System.out.println("Gym Membership System");
                System.out.println("1. Add Member");
                System.out.println("2. View Members");
                System.out.println("3. Exit");
                System.out.print("Choose an option: ");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        addMember(conn, scanner);
                        break;
                    case 2:
                        viewMembers(conn);
                        break;
                    case 3:
                        System.out.println("Exiting the system.");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid option.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseHelper.closeConnection(conn);
            scanner.close();
        }
    }

    public static void addMember(Connection conn, Scanner scanner) {
        System.out.print("Enter name: ");
        String name = scanner.next();
        System.out.print("Enter age: ");
        int age = scanner.nextInt();

        String membershipType;
        while (true) {
            System.out.print("Enter membership type (Premium, Gold, Basic): ");
            membershipType = scanner.next();
            if (membershipType.equalsIgnoreCase("Premium") || 
                membershipType.equalsIgnoreCase("Gold") || 
                membershipType.equalsIgnoreCase("Basic")) {
                break;
            } else {
                System.out.println("Invalid membership type. Please enter Premium, Gold, or Basic.");
            }
        }

        Member member = new Member(name, age, membershipType);

        String sql = "INSERT INTO members (name, age, membership_type) VALUES (?, ?, ?)";
        PreparedStatement stmt = null;
        try {
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, member.name);
            stmt.setInt(2, member.age);
            stmt.setString(3, member.getMembershipType());
            stmt.executeUpdate();
            System.out.println("Member added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseHelper.closePreparedStatement(stmt);
        }
    }

    public static void viewMembers(Connection conn) {
        String sql = "SELECT * FROM members";
        PreparedStatement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();

            System.out.println("Gym Members:");
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                int age = rs.getInt("age");
                String membershipType = rs.getString("membership_type");

                System.out.printf("ID: %d, Name: %s, Age: %d, Type: %s%n", id, name, age, membershipType);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DatabaseHelper.closeResultSet(rs);
            DatabaseHelper.closePreparedStatement(stmt);
        }
    }
}
