package shared;

public class EwalletPayment extends Payment{
    private String accountNumber;

    // TODO ADD RESTRICTIONS (i.e. AAAAAA is not allowed)
    public EwalletPayment(double amount, String accountNumber) throws InvalidPaymentException{
        super(amount);
        if(accountNumber == null || accountNumber.trim().isEmpty()){
            throw new InvalidPaymentException("Invalid account number.");
        }
        this.accountNumber = accountNumber.trim();
    }

    @Override
    public boolean processPayment() throws InvalidPaymentException {
        if(getAmount() <= 0){
            throw new InvalidPaymentException("Invalid E-Wallet payment aamount.");
        }
        markPaid();
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "E-Wallet (" + accountNumber + ")";
    }
}
