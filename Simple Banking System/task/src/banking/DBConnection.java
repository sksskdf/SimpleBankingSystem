package banking;

import org.sqlite.SQLiteDataSource;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DBConnection {
    SQLiteDataSource ds;

    public DBConnection(String args[]) {
        createConnection(args);
    }

    void createConnection(String[] args) {
        String url = "jdbc:sqlite:".concat(args[1]);
        ds = new SQLiteDataSource();
        ds.setUrl(url);

        try(Connection cn = ds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                st.executeUpdate("CREATE TABLE IF NOT EXISTS card (id INTEGER PRIMARY KEY, number TEXT NOT NULL, pin TEXT NOT NULL, balance INTEGER DEFAULT 0)");
            } catch (Exception e) {e.printStackTrace();}
        } catch (Exception e) {e.printStackTrace();}
    }

    void insertCardInfo(CardInfo cardInfo) {
        try(Connection cn = ds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                st.executeUpdate("INSERT INTO card (number, pin) VALUES ("+ "'" +cardInfo.getCard_number()+ "', '" +cardInfo.getPin()+ "')");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int authentication(long number, int pin) {
        String sql = "SELECT EXISTS (SELECT * FROM card WHERE number = ? AND pin = ?);";
        try(Connection cn = ds.getConnection()) {
            try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
                pstmt.setLong(1, number);
                pstmt.setInt(2, pin);
                return pstmt.executeQuery().getInt(1);
            } catch (Exception e) {
                e.printStackTrace();}
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int authentication(long number) {
        String sql = "SELECT EXISTS (SELECT * FROM card WHERE number = ?);";
        try(Connection cn = ds.getConnection()) {
            try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
                pstmt.setLong(1, number);
                
                return pstmt.executeQuery().getInt(1);
            } catch (Exception e) {
                e.printStackTrace();}
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public int getBalance(long number) {
        try (Connection cn = ds.getConnection()) {
            try (Statement st = cn.createStatement()) {
                return st.executeQuery("SELECT balance FROM card WHERE number = " + number).getInt("balance");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void deleteCardInfo(long card_number) {
        String sql = "DELETE FROM card WHERE number = ?";
        try (Connection cn = ds.getConnection()) {
            try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
                pstmt.setBigDecimal(1, BigDecimal.valueOf(card_number));
                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addIncome(int income, long card_number) {
        String sql = "UPDATE card SET balance = (balance + ?) where number = ?";
        try (Connection cn = ds.getConnection()) {
            try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
                pstmt.setInt(1, income);
                pstmt.setBigDecimal(2, BigDecimal.valueOf(card_number));
                pstmt.executeUpdate();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int check_balance(int money_to_transfer, long card_number) {
        String sql = "SELECT ? < balance FROM card WHERE number = ?";

        try (Connection cn = ds.getConnection()) {
            try (PreparedStatement pstmt = cn.prepareStatement(sql)) {
                pstmt.setInt(1, money_to_transfer);
                pstmt.setBigDecimal(2, BigDecimal.valueOf(card_number));

                return pstmt.executeQuery().getInt(1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    public void transfer_money(int money_to_transfer, long card_number, long target_card_number) {
        String minus_balance = "UPDATE card SET balance = balance - ? WHERE number = ?";
        String plus_balance = "UPDATE card SET balance = balance + ? WHERE number = ?";

        try (Connection cn = ds.getConnection()) {
            try (PreparedStatement minus = cn.prepareStatement(minus_balance);
                 PreparedStatement plus = cn.prepareStatement(plus_balance)) {
                cn.setAutoCommit(false);

                minus.setInt(1, money_to_transfer);
                minus.setLong(2, card_number);
                minus.executeUpdate();

                plus.setInt(1, money_to_transfer);
                plus.setLong(2, target_card_number);
                plus.executeUpdate();

                cn.commit();
            } catch (Exception e) {
                e.printStackTrace();
                cn.rollback();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
