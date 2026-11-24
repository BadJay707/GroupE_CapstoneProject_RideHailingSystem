package shared;

public class CashPayment extends Payment{
    public CashPayment(double amount) throws InvalidPaymentException{
        super(amount);
    }

    @Override
    public boolean processPayment() throws InvalidPaymentException {
        if(getAmount() <= 0){
            throw new InvalidPaymentException("Invalid card payment aamount.");
        }
        markPaid();
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Cash";
    }
}
