import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
public class BankAccount
{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long accountNum;
    private String ownerName;

    public long getAccountNum() {
        return accountNum;
    }

    public void setAccountNum(long accountNum) {
        accountNum = accountNum;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }
    @Override
    public String toString() {
        return "BankAccount{" +
                "AccountNum=" + accountNum +
                ", ownerName='" + ownerName + '\'' +
                '}';
    }
}
