package shared;

public class PaymentFactory{ // Abstract Factory Method
    public static Payment createPayment(String method, double amount, String accountDetails) throws InvalidPaymentException{
        if(method == null || method.trim().isEmpty()){
            throw new InvalidPaymentException("Please select a payment method.");
        }

        switch(method.trim().toLowerCase()){
            case "cash":
                return new CashPayment(amount);
            case "card":
                return new CardPayment(amount, accountDetails);
            case "e-wallet":
                return new EwalletPayment(amount, accountDetails);
            default:
                throw new InvalidPaymentException("Invalid payment method.");
        }
    }
}
