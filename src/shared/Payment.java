package shared;

public abstract class Payment implements PaymentGateway{
    private double amount;
    private String status;

    public Payment(double amount) throws InvalidPaymentException{
        setAmount(amount);
        status = "Pending";
    }

    //----------------Methods----------------//
    public void markPaid(){ status = "Paid"; }

    // Getters and Setters
    public String getStatus(){ return status; }
    public double getAmount() { return amount; }

    public void setAmount(double amount) throws InvalidPaymentException{
        if(amount <= 0){
            throw new InvalidPaymentException("Payment amount must be greater than 0.");
        }
        this.amount = amount;
    }
}
