import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.DateFormatter;
import javax.swing.text.DefaultFormatterFactory;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.util.Date;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.List;

public class Interface {
    private JPanel MainScreen;
    private JFormattedTextField createTransactionDate;
    private JFormattedTextField createAmount;
    private JFormattedTextField createSenderAccountNumber;
    private JFormattedTextField createReceiverAccountNumber;
    private JComboBox<String> createTransactionType;
    private JButton saveDetailsButton;
    private JTabbedPane Main;
    private JPanel Create;
    private JPanel Read;
    private JPanel Delete;
    private JFormattedTextField readAccountNum;
    private JTable readResults;
    private JButton readButton;
    private JFormattedTextField deleteTransactionID;
    private JButton deleteButton;
    private JTextArea operationResults;
    private JFormattedTextField createUserName;
    private JButton saveUserDetails;
    private JFormattedTextField deleteAccountNum;

    public class DatabaseManager {
        private EntityManagerFactory emf;
        private EntityManager em;

        public DatabaseManager() {
            emf = Persistence.createEntityManagerFactory("objectdb:db/database.odb");
            em = emf.createEntityManager();
        }

        public EntityManager getEntityManager() {
            return em;
        }

        public void close() {
            em.close();
            emf.close();
        }
    }

    public Interface() {
        // Initialize components and set up event listeners
        saveUserDetails.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (createUserName.getText().isEmpty() || createUserName.getText().equals("User Name")) {
                    operationResults.setText("Please enter a user name.");
                    return;
                }
                createUser();
            }
        });
        saveDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (createAmount.getValue() == null || createAmount.getText().isEmpty() || createAmount.getValue().equals(0.0)) {
                    operationResults.setText("Please enter a valid amount.");
                    return;
                }
                if (createSenderAccountNumber.getText().isEmpty() || createSenderAccountNumber.getText().equals("Sender Account Number")) {
                    operationResults.setText("Please enter a sender account number.");
                    return;
                }
                if (createReceiverAccountNumber.getText().isEmpty() || createReceiverAccountNumber.getText().equals("Receiver Account Number")) {
                    operationResults.setText("Please enter a receiver account number.");
                    return;
                }
                if(createTransactionDate.getValue() == null) {
                    operationResults.setText("Please enter a valid date.");
                    return;
                }
                createTransaction();
            }
        });

        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (readAccountNum.getText().isEmpty() || readAccountNum.getText().equals("Account Number")) {
                    operationResults.setText("Please enter a valid Account Number.");
                    return;
                }
                readTransaction();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (deleteAccountNum.getText().isEmpty() || deleteAccountNum.getText().equals("Account Number")) {
                    operationResults.setText("Please enter a valid Account Number.");
                    return;
                }
                try {
                    deleteUser();
                } catch (NumberFormatException ex) {
                    operationResults.setText("Invalid Account Number format.");
                }
            }
        });
        
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance();
        NumberFormatter currencyFormatter = new NumberFormatter(currencyFormat);
        currencyFormatter.setValueClass(Double.class);
        currencyFormatter.setMinimum(0.0);
        currencyFormatter.setMaximum(10000000.0);
        currencyFormatter.setAllowsInvalid(true);
        currencyFormatter.setCommitsOnValidEdit(true);
        createAmount.setFormatterFactory(new DefaultFormatterFactory(currencyFormatter));

        createAmount.setValue(0.0); // Internal default value
        createAmount.setForeground(Color.GRAY);

        // Initialize TransactionDate with a DateFormatter
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        createTransactionDate.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        createTransactionDate.setValue(new Date()); // Set current date as default
        // Initialize TransactionType with options
        String[] transactionTypes = {"Deposit", "Withdrawal", "Transfer"};
        createTransactionType.setModel(new DefaultComboBoxModel<>(transactionTypes));

        // Set placeholder text for other fields
        setPlaceholderText(createUserName, "User Name");
        setPlaceholderText(createSenderAccountNumber, "Sender Account Number");
        setPlaceholderText(createReceiverAccountNumber, "Receiver Account Number");
        setPlaceholderText(readAccountNum, "Account Number");
        setPlaceholderText(deleteAccountNum, "Account Number");

        // Manage focus and input for Amount field
        createAmount.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (((Number) createAmount.getValue()).doubleValue() == 0.0) {
                    createAmount.setValue(null); // Clear the field when focused
                    createAmount.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (createAmount.getValue() == null || createAmount.getText().isEmpty()) {
                    createAmount.setValue(0.0); // Set the field back to default value
                    createAmount.setForeground(Color.GRAY);
                }
            }
        });
    }
    private void createUser() {
        DatabaseManager dbManager = new DatabaseManager();
        EntityManager em = dbManager.getEntityManager();

        try {
            em.getTransaction().begin();
            BankAccount user = new BankAccount();
            user.setOwnerName(createUserName.getText());
            em.persist(user);
            em.getTransaction().commit();
            operationResults.setText("User created successfully account number: " + user.getAccountNum());
        } catch (Exception ex) {
            em.getTransaction().rollback();
            operationResults.setText("Error creating user: " + ex.getMessage());
        } finally {
            dbManager.close();
        }
    }
    private BankAccount readUser(Long accountNum) {
        DatabaseManager dbManager = new DatabaseManager();
        EntityManager em = dbManager.getEntityManager();
        BankAccount user = em.find(BankAccount.class, accountNum);
        dbManager.close();
        return user;
    }
        private void deleteUser() {
        DatabaseManager dbManager = new DatabaseManager();
        EntityManager em = dbManager.getEntityManager();
    
        try {
            Long accountNum = Long.parseLong(deleteAccountNum.getText());
            BankAccount user = em.find(BankAccount.class, accountNum);
            if (user != null) {
                em.getTransaction().begin();
    
                // Retrieve and delete all transactions where the user is either the sender or receiver
                List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t WHERE t.sender.accountNum = :accountNum OR t.receiver.accountNum = :accountNum", Transaction.class)
                                                   .setParameter("accountNum", accountNum)
                                                   .getResultList();
                for (Transaction transaction : transactions) {
                    em.remove(transaction);
                }
    
                // Delete the user
                em.remove(user);
                em.getTransaction().commit();
                operationResults.setText("User and associated transactions deleted successfully.");
            } else {
                operationResults.setText("User not found.");
            }
        } catch (Exception ex) {
            em.getTransaction().rollback();
            operationResults.setText("Error deleting user: " + ex.getMessage());
        } finally {
            dbManager.close();
        }
    }
    private void createTransaction() {
        DatabaseManager dbManager = new DatabaseManager();
        EntityManager em = dbManager.getEntityManager();
    
        try {
            em.getTransaction().begin();
    
            // Retrieve bank account numbers from user input
            long fromAccountNumber = Long.parseLong(createSenderAccountNumber.getText());
            long toAccountNumber =  Long.parseLong(createReceiverAccountNumber.getText());
    
            // Check if the bank accounts exist
            BankAccount fromAccount = em.find(BankAccount.class, fromAccountNumber);
            BankAccount toAccount = em.find(BankAccount.class, toAccountNumber);
    
            if (fromAccount == null || toAccount == null) {
                operationResults.setText("Invalid bank account number(s).");
                em.getTransaction().rollback();
                return;
            }
    
            // Proceed with transaction creation
            Transaction transaction = new Transaction();
            transaction.setAmount(((Number) createAmount.getValue()).doubleValue());
            transaction.setTransactionDate((Date) createTransactionDate.getValue());
            transaction.setTransactionType((String) createTransactionType.getSelectedItem());
            transaction.setSender(fromAccount);
            transaction.setReceiver(toAccount);
    
            em.persist(transaction);
            em.getTransaction().commit();
            operationResults.setText("Transaction created successfully.");
        } catch (Exception ex) {
            em.getTransaction().rollback();
            operationResults.setText("Error creating transaction: " + ex.getMessage());
        } finally {
            dbManager.close();
        }
    }
    private void readTransaction() {
        DatabaseManager dbManager = new DatabaseManager();
        EntityManager em = dbManager.getEntityManager();
    
        try {
            String accountNumStr = readAccountNum.getText();
            DefaultTableModel model = new DefaultTableModel(new String[]{"Transaction ID", "Amount", "Date", "Sender Account", "Sender Name", "Receiver Account", "Receiver Name", "Type"}, 0);
    
            if (accountNumStr == null || accountNumStr.equals("Account Number") || accountNumStr.trim().isEmpty()) {
                operationResults.setText("Please enter a valid account number.");
                return;
            }
    
            Long accountNum = Long.parseLong(accountNumStr);
            BankAccount account = em.find(BankAccount.class, accountNum);
    
            if (account == null) {
                operationResults.setText("Bank account not found.");
                return;
            }
    
            List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t WHERE t.sender.accountNum = :accountNum OR t.receiver.accountNum = :accountNum", Transaction.class)
                                               .setParameter("accountNum", accountNum)
                                               .getResultList();
    
            if (!transactions.isEmpty()) {
                StringBuilder result = new StringBuilder("Account Number: " + account.getAccountNum() + "\n");
                result.append("Account Holder's Name: ").append(account.getOwnerName()).append("\n");
                result.append("Transactions:\n");
    
                for (Transaction transaction : transactions) {
                    result.append(transaction.toString()).append("\n");
                    Object[] rowData = {
                        transaction.getTransactionID(),
                        transaction.getAmount(),
                        transaction.getTransactionDate(),
                        transaction.getSender().getAccountNum(),
                        transaction.getSender().getOwnerName(),
                        transaction.getReceiver().getAccountNum(),
                        transaction.getReceiver().getOwnerName(),
                        transaction.getTransactionType()
                    };
                    model.addRow(rowData);
                }
    
                operationResults.setText(result.toString());
                readResults.setModel(model);
            } else {
                operationResults.setText("No transactions found for this account.");
            }
        } catch (Exception ex) {
            operationResults.setText("Error reading transactions: " + ex.getMessage());
        } finally {
            dbManager.close();
        }
    }

    private void setPlaceholderText(JFormattedTextField textField, String placeholder) {
        textField.setText(placeholder);
        textField.setForeground(Color.GRAY);
        textField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (textField.getText().equals(placeholder)) {
                    textField.setText("");
                    textField.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (textField.getText().isEmpty()) {
                    textField.setText(placeholder);
                    textField.setForeground(Color.GRAY);
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Interface");
        frame.setContentPane(new Interface().MainScreen);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
