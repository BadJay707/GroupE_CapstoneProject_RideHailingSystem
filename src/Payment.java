public class Payment {
    private double amount;

    public Payment(double amount){
        this.amount = amount;
    }

    /* Uncomment after implementing
    public String status(){

    }

    public double refund(){

    }
    */

    public double getAmount() { return amount; }
    public void setAmount(double amount){ this.amount = amount; }
}
