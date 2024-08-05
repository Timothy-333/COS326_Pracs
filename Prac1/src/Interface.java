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
    private JPanel Update;
    private JPanel Delete;
    private JFormattedTextField readTransactionID;
    private JFormattedTextField updateTransactionID;
    private JFormattedTextField updateAmount;
    private JFormattedTextField updateTransactionDate;
    private JFormattedTextField updateSenderAccNum;
    private JFormattedTextField updateReceiverAccNum;
    private JButton updateButton;
    private JComboBox<String> updateTransactionType;
    private JTable readResults;
    private JButton readButton;
    private JFormattedTextField deleteTransactionID;
    private JButton deleteButton;
    private JTextArea operationResults;

    public class DatabaseManager {
        private EntityManagerFactory emf;
        private EntityManager em;

        public DatabaseManager() {
            emf = Persistence.createEntityManagerFactory("objectdb:db/transactions.odb");
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
        saveDetailsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createTransaction();
            }
        });

        readButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                readTransaction();
            }
        });

        updateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateTransaction();
            }
        });

        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                deleteTransaction();
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
        updateAmount.setFormatterFactory(new DefaultFormatterFactory(currencyFormatter));

        createAmount.setValue(0.0); // Internal default value
        createAmount.setForeground(Color.GRAY);
        updateAmount.setValue(0.0); // Internal default value
        updateAmount.setForeground(Color.GRAY);

        // Initialize TransactionDate with a DateFormatter
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormatter dateFormatter = new DateFormatter(dateFormat);
        createTransactionDate.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        createTransactionDate.setValue(new Date()); // Set current date as default
        updateTransactionDate.setFormatterFactory(new DefaultFormatterFactory(dateFormatter));
        updateTransactionDate.setValue(new Date()); // Set current date as default
        // Initialize TransactionType with options
        String[] transactionTypes = {"Deposit", "Withdrawal", "Transfer"};
        createTransactionType.setModel(new DefaultComboBoxModel<>(transactionTypes));
        updateTransactionType.setModel(new DefaultComboBoxModel<>(transactionTypes));

        // Set placeholder text for other fields
        setPlaceholderText(createSenderAccountNumber, "Sender Account Number");
        setPlaceholderText(createReceiverAccountNumber, "Receiver Account Number");
        setPlaceholderText(readTransactionID, "Transaction ID");
        setPlaceholderText(updateTransactionID, "Transaction ID");
        setPlaceholderText(updateSenderAccNum, "Sender Account Number");
        setPlaceholderText(updateReceiverAccNum, "Receiver Account Number");
        setPlaceholderText(deleteTransactionID, "Transaction ID");

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
        updateAmount.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (((Number) updateAmount.getValue()).doubleValue() == 0.0) {
                    updateAmount.setValue(null); // Clear the field when focused
                    updateAmount.setForeground(Color.BLACK);
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if (updateAmount.getValue() == null || updateAmount.getText().isEmpty()) {
                    updateAmount.setValue(0.0); // Set the field back to default value
                    updateAmount.setForeground(Color.GRAY);
                }
            }
        });
    }

    private void createTransaction() {
        DatabaseManager dbManager = new DatabaseManager();
        EntityManager em = dbManager.getEntityManager();

        try {
            em.getTransaction().begin();
            Transaction transaction = new Transaction();
            transaction.setAmount(((Number) createAmount.getValue()).doubleValue());
            transaction.setTransactionDate((Date) createTransactionDate.getValue());
            transaction.setSenderAccountNumber(createSenderAccountNumber.getText());
            transaction.setReceiverAccountNumber(createReceiverAccountNumber.getText());
            transaction.setTransactionType((String) createTransactionType.getSelectedItem());
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
                String transactionIDStr = readTransactionID.getText();
                if (transactionIDStr == null || transactionIDStr.equals("Transaction ID") || transactionIDStr.trim().isEmpty()) {
                    // No transaction ID entered, return all transactions
                    List<Transaction> transactions = em.createQuery("SELECT t FROM Transaction t", Transaction.class).getResultList();
                    if (!transactions.isEmpty()) {
                        StringBuilder result = new StringBuilder("Transactions found:\n");
                        for (Transaction transaction : transactions) {
                            result.append(transaction.toString()).append("\n");
                        }
                        operationResults.setText(result.toString());
        
                        DefaultTableModel model = new DefaultTableModel(new String[]{"Transaction ID", "Amount", "Date", "Sender", "Receiver", "Type"}, 0);
                        for (Transaction transaction : transactions) {
                            Object[] rowData = {
                                transaction.getTransactionID(),
                                transaction.getAmount(),
                                transaction.getTransactionDate(),
                                transaction.getSenderAccountNumber(),
                                transaction.getReceiverAccountNumber(),
                                transaction.getTransactionType()
                            };
                            model.addRow(rowData);
                        }
                        readResults.setModel(model);
                    } else {
                        operationResults.setText("No transactions found.");
                    }
                } else {
                    // Transaction ID entered, find specific transaction
                    Long transactionID = Long.parseLong(transactionIDStr);
                    Transaction transaction = em.find(Transaction.class, transactionID);
                    if (transaction != null) {
                        operationResults.setText("Transaction found: " + transaction.toString());
        
                        // Fill the readResults table with the specific transaction
                        DefaultTableModel model = new DefaultTableModel(new String[]{"Transaction ID", "Amount", "Date", "Sender", "Receiver", "Type"}, 0);
                        Object[] rowData = {
                            transaction.getTransactionID(),
                            transaction.getAmount(),
                            transaction.getTransactionDate(),
                            transaction.getSenderAccountNumber(),
                            transaction.getReceiverAccountNumber(),
                            transaction.getTransactionType()
                        };
                        model.addRow(rowData);
                        readResults.setModel(model);
                    } else {
                        operationResults.setText("Transaction not found.");
                    }
                }
            } catch (Exception ex) {
                operationResults.setText("Error reading transaction: " + ex.getMessage());
            } finally {
                dbManager.close();
            }
        }

        private void updateTransaction() {
        DatabaseManager dbManager = new DatabaseManager();
        EntityManager em = dbManager.getEntityManager();

        try {
            Long transactionID = Long.parseLong(updateTransactionID.getText());
            Transaction transaction = em.find(Transaction.class, transactionID);
            if (transaction != null) {
                em.getTransaction().begin();
                transaction.setAmount(((Number) updateAmount.getValue()).doubleValue());
                transaction.setTransactionDate((Date) updateTransactionDate.getValue());
                transaction.setSenderAccountNumber(updateSenderAccNum.getText());
                transaction.setReceiverAccountNumber(updateReceiverAccNum.getText());
                transaction.setTransactionType((String) updateTransactionType.getSelectedItem());
                em.getTransaction().commit();
                operationResults.setText("Transaction updated successfully.");
            } else {
                operationResults.setText("Transaction not found.");
            }
        } catch (Exception ex) {
            em.getTransaction().rollback();
            operationResults.setText("Error updating transaction: " + ex.getMessage());
        } finally {
            dbManager.close();
        }
    }

    private void deleteTransaction() {
        DatabaseManager dbManager = new DatabaseManager();
        EntityManager em = dbManager.getEntityManager();

        try {
            Long transactionID = Long.parseLong(deleteTransactionID.getText());
            Transaction transaction = em.find(Transaction.class, transactionID);
            if (transaction != null) {
                em.getTransaction().begin();
                em.remove(transaction);
                em.getTransaction().commit();
                operationResults.setText("Transaction deleted successfully.");
            } else {
                operationResults.setText("Transaction not found.");
            }
        } catch (Exception ex) {
            em.getTransaction().rollback();
            operationResults.setText("Error deleting transaction: " + ex.getMessage());
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
