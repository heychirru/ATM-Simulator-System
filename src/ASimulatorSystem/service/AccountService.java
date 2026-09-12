package ASimulatorSystem.service;

import ASimulatorSystem.dao.AccountDao;
import java.sql.SQLException;

/** Business operations related to account authentication. */
public class AccountService {
    private final AccountDao accountDao;

    public AccountService() {
        this.accountDao = new AccountDao();
    }

    public boolean accountExists(String cardNumber) throws SQLException {
        return accountDao.findByCardNumber(cardNumber) != null;
    }
}
