package com.techelevator.tebucks.security.dao;

import com.techelevator.tebucks.exception.DaoException;
import com.techelevator.tebucks.security.model.RegisterUserDto;
import com.techelevator.tebucks.security.model.User;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.CannotGetJdbcConnectionException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Component
public class JdbcUserDao implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final AccountDao accountDao;

    public JdbcUserDao(JdbcTemplate jdbcTemplate, AccountDao accountDao) {
        this.jdbcTemplate = jdbcTemplate;
        this.accountDao = accountDao;
    }

    public User getUserById(int userId) {
        User user = null;
        String sql = "SELECT user_id, username, password_hash, first_name, last_name, email FROM users WHERE user_id = ?";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userId);
            if (results.next()) {
                user = mapRowToUser(results);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return user;
    }
    @Override
    public List<User> listUsers() {
        List<User> users = new ArrayList<>();
        String sql = "SELECT * FROM users;";
        try {
            SqlRowSet results = jdbcTemplate.queryForRowSet(sql);
            while (results.next()) {
                User user = mapRowToUser(results);
                users.add(user);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return users;
    }
    @Override
    public User getUserByUsername(String username) {
        if (username == null) throw new IllegalArgumentException("Username cannot be null");
        User user = null;
        String sql = "SELECT user_id, username, password_hash, first_name, last_name, email FROM users WHERE username = LOWER(TRIM(?));";
        try {
            SqlRowSet rowSet = jdbcTemplate.queryForRowSet(sql, username);
            if (rowSet.next()) {
                user = mapRowToUser(rowSet);
            }
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        }
        return user;
    }

    @Override
    public User createUser(RegisterUserDto user) {
        // create user
        String sql = "INSERT INTO users (username, password_hash, first_name, last_name, email) VALUES (TRIM(?), ?, ?, ?, ?) RETURNING user_id";
        String passwordHash = new BCryptPasswordEncoder().encode(user.getPassword());
        try {
            Integer newUserId = jdbcTemplate.queryForObject(sql, int.class,
                    user.getUsername(),
                    passwordHash,
                    user.getFirstName(),
                    user.getLastName(),
                    user.getEmail());

            if (newUserId == null) {
                throw new DaoException("Could not create user");
            }

            // TODO: Create account for the user with a starting balance of $1000
            accountDao.createAccount(newUserId);

            return getUserById(newUserId);
        } catch (CannotGetJdbcConnectionException e) {
            throw new DaoException("Unable to connect to server or database", e);
        } catch (DataIntegrityViolationException e) {
            throw new DaoException("Data integrity violation", e);
        }
    }

    private User mapRowToUser(SqlRowSet rs) {
        User user = new User();
        user.setId(rs.getInt("user_id"));
        user.setUsername(rs.getString("username"));
        user.setPassword(rs.getString("password_hash"));
        user.setFirstName(rs.getString("first_name"));
        user.setLastName(rs.getString("last_name"));
        user.setEmail(rs.getString("email"));
        user.setActivated(true);
        user.setAuthorities("USER");
        return user;
    }
}
