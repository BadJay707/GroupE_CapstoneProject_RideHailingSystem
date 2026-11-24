package shared;

public interface PaymentGateway {
    boolean processPayment() throws InvalidPaymentException;
    String getPaymentMethodName();
}
