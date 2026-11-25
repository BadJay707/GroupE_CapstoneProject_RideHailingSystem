package shared;

public class CardPayment extends Payment{
    private String cardNumber;

    // TODO ADD RESTRICTIONS (i.e. AAAAAA is not allowed)
    public CardPayment(double amount, String cardNumber) throws InvalidPaymentException{
        super(amount);
        if(cardNumber == null || cardNumber.trim().length() < 4){
            throw new InvalidPaymentException("Invalid card number.");
        }
        this.cardNumber = cardNumber.trim();
    }

    //----------------Methods----------------//
    @Override
    public boolean processPayment() throws InvalidPaymentException {
        if(getAmount() <= 0){
            throw new InvalidPaymentException("Invalid cash payment aamount.");
        }
        markPaid();
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Card";
    }
}
