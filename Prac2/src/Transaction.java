import javax.persistence.*;
import java.util.Date;

@Entity
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long transactionID;

    private Date transactionDate;
    private double amount;
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "sender_account_num")
    private BankAccount sender;

    @ManyToOne
    @JoinColumn(name = "receiver_account_num")
    private BankAccount receiver;

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public BankAccount getSender() {
        return sender;
    }

    public void setSender(BankAccount sender) {
        this.sender = sender;
    }

    public BankAccount getReceiver() {
        return receiver;
    }

    public void setReceiver(BankAccount receiver) {
        this.receiver = receiver;
    }

    public long getTransactionID() {
        return transactionID;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "transactionID=" + transactionID +
                ", transactionDate=" + transactionDate +
                ", amount=" + amount +
                ", transactionType='" + transactionType + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                '}';
    }
}